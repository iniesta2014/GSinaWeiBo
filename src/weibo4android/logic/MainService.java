
package weibo4android.logic;

import java.util.ArrayList;
import java.util.List;

import weibo4android.Paging;
import weibo4android.Status;
import weibo4android.User;
import weibo4android.Weibo;
import weibo4android.WeiboException;
import weibo4android.logic.R;
import weibo4android.logic.http.OAuthConstant;
import weibo4android.logic.ui.HomeActivity;
import weibo4android.logic.ui.Login;
import weibo4android.logic.ui.SearchUser;
import weibo4android.util.WeiboUtil;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

public class MainService extends Service implements Runnable {
    public static Weibo mWeibo;
    public static User mNowuser;// 当前的用户
    // 将当前的activity加到Service中方便管理和调用
    public static ArrayList<Activity> mAllActivity = new ArrayList<Activity>();
    // 将所有任务放到任务集合中
    public static ArrayList<Task> mAllTask = new ArrayList<Task>();

    // 遍历所有activity 根据名称在 allActivity 中找到需要的activity
    public static Activity getActivityByName(String name) {
        for (Activity ac : mAllActivity) {
            if (ac.getClass().getName().indexOf(name) >= 0) {
                Log.i("status", ACTIVITY_SERVICE.getClass().getName()
                        .toString());
                return ac;
            }
        }
        return null;
    }

    // 将当前的任务加到任务集合中
    public static void newTask(Task task)
    {
        mAllTask.add(task);
    }

    public boolean isrun = true;// 线程开关

    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case Task.TASK_USER_LOGIN:// 通知Login页面 登陆成功
                    IWeiboActivity login = (IWeiboActivity) MainService// 获得请求任务的Activity
                            .getActivityByName("Login");// 调用Login
                                                        // Activity刷新页面的方法
                    login.refresh(new Integer(Login.REFRESH_LOGIN), msg.obj);
                    break;
                case Task.TASK_GET_USER_HOMETIMEINLINE:
                    IWeiboActivity ia = (IWeiboActivity) MainService
                            .getActivityByName("HomeActivity");
                    ia.refresh(HomeActivity.REFRESH_WEIBO, msg.obj);
                    break;
                case Task.TASK_SEARCH_WEIBO:
                    IWeiboActivity ia2 = (IWeiboActivity) MainService
                            .getActivityByName("SearchUser");
                    ia2.refresh(SearchUser.SEARCH_WEIBO, msg.obj);
                    break;

            }

        }
    };

    private void doTask(Task task) {
        Message mess = handler.obtainMessage();
        mess.what = task.getTaskID();// 将当前任务的ID 放到Message中
        switch (task.getTaskID()) {
            case Task.TASK_USER_LOGIN:// 得到登陆任务
                // 接到登录任务 执行登录
                System.setProperty("weibo4j.oauth.consumerKey",
                        Weibo.CONSUMER_KEY);
                System.setProperty("weibo4j.oauth.consumerSecret",
                        Weibo.CONSUMER_SECRET);
                String toke = ((String) task.getTaskParam().get("token"));
                String secret = ((String) task.getTaskParam().get("secret"));
                Log.i("yanzheng", toke + "token <----->" + "两个密钥" + secret);
                mWeibo = OAuthConstant.getInstance().getWeibo();
                mWeibo.setToken(toke, secret);
                User u = null;
                try {
                    // 验证当前用户身份是否合法 验证成功 返回一个user对象
                    u = mWeibo.verifyCredentials();
                } catch (WeiboException e) {
                    e.printStackTrace();
                }
                MainService.mNowuser = u;
                mess.obj = u;
                break;
            case Task.TASK_GET_USER_HOMETIMEINLINE:// 得到刷新主页面信息的任务
                try {
                    // HomeActivity传递来的分页参数
                    Paging paging = new Paging(
                            (Integer) task.getTaskParam().get("nowPage"),
                            (Integer) task.getTaskParam().get("pageSize"));
                    // 获取当前登录用户及其所关注用户的最新20条微博消息其中paging是请求的分页
                    List<Status> allweibo = mWeibo.getFriendsTimeline(paging);
                    mess.obj = allweibo;// 将获取信息放入到Message中发送
                } catch (WeiboException e) {
                    e.printStackTrace();
                }
                break;
            case Task.TASK_SEARCH_WEIBO:
                Paging paging = new Paging(
                        (Integer) task.getTaskParam().get("nowPage"),
                        (Integer) task.getTaskParam().get("pageSize"));
                String content = (String) task.getTaskParam().get("content");
                List<Status> searchweibo = WeiboUtil.getThrendweibo(MainService.this, content,
                        paging);
                ;
                mess.obj = searchweibo;
                break;

        }
        handler.sendMessage(mess);// 发送当前消息
        mAllTask.remove(task);// 当前任务执行完毕 把任务从任务集合中remove 不然会重复执行
    }

    @Override
    public void onCreate() {
        super.onCreate();
        isrun = true;// 启动线程
        Thread t = new Thread(this);
        t.start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.stopSelf();// 停止服务
        isrun = false;// 关闭线程
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void run() {
        while (isrun) {
            Task lastTask = null;
            synchronized (mAllTask) {
                if (mAllTask.size() > 0) {
                    lastTask = mAllTask.get(0);
                    Log.i("yanzheng", "任务ID" + lastTask.getTaskID());
                    doTask(lastTask);
                }
            }
            // 每隔一秒钟检查是否有任务
            try {
                Thread.sleep(1000);
            } catch (Exception e) {
            }
        }
    }

    /**
     * 退出应用程序
     * 
     * @param context
     */
    public static void exitAPP(Context context) {
        Intent it = new Intent("weibo4android.logic.util.MainService");
        context.stopService(it);// 停止服务
        // 杀死进程 我感觉这种方式最直接了当
        android.os.Process.killProcess(android.os.Process.myPid());
        for (Activity activity : mAllActivity) {// 遍历所有activity 一个一个删除
            activity.finish();
        }
    }

    /**
     * 网络连接异常
     * 
     * @param context
     */
    public static void AlertNetError(final Context context) {
        AlertDialog.Builder alerError = new AlertDialog.Builder(context);
        alerError.setTitle(R.string.main_fetch_fail);
        alerError.setMessage(R.string.NoSignalException);
        alerError.setNegativeButton(R.string.apn_is_wrong1_exit,
                new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        exitAPP(context);
                    }
                });
        alerError.setPositiveButton(R.string.apn_is_wrong1_setnet,
                new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        context.startActivity(new Intent(
                                android.provider.Settings.ACTION_WIRELESS_SETTINGS));
                    }
                });
        alerError.create().show();
    }
}

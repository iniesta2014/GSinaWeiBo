
package weibo4android.logic.ui;

import java.util.HashMap;

import weibo4android.Weibo;
import weibo4android.WeiboException;
import weibo4android.logic.IWeiboActivity;
import weibo4android.logic.MainService;
import weibo4android.logic.R;
import weibo4android.logic.Task;
import weibo4android.logic.http.OAuthConstant;
import weibo4android.logic.http.RequestToken;
import weibo4android.util.Exit;
import weibo4android.util.SaveLoginParam;
import weibo4android.util.WeiboUtil;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class Login extends Activity implements IWeiboActivity {

    HashMap<String, String> mParam;
    public static Weibo mWeibo;
    public static String mToken = null;
    public static String mSecret = null;
    public Dialog mDialog;
    public ProgressDialog mProgressDialog;
    private CheckBox mAutologin;// 自动登录选择框
    boolean mIsAutologin;
    public static final int REFRESH_LOGIN = 1;// 登陆

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        MainService.mAllActivity.add(this);// 将当前的activity添加到Servicre的activity集合中
        Button btlogin = (Button) this.findViewById(R.id.login_Button);
        init();// 初始化部分信息
        btlogin.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mToken != null) {
                    goHome();// 执行跳转到主页面
                } else {// 如果当前的首选项没有用户信息则到Oauth认证页面
                    goOAuth(Login.this);
                    mDialog.dismiss();// 要先关闭dialog否则会窗体泄露
                    Login.this.finish();// 关闭当前activity
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 通过隐式意图启动Service
        Intent it = new Intent("weibo4android.logic.MainService");
        this.startService(it);
    }

    // 第一次登陆是提示去Author认证的Dialog
    public void dialogshow() {
        View dialogview = LayoutInflater.from(Login.this).inflate(R.layout.dialogshow, null);
        mDialog = new Dialog(Login.this, R.style.oauthdialog);
        mDialog.setContentView(dialogview);
        Button btstart = (Button) dialogview.findViewById(R.id.btn_start);
        try {
            mDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
        btstart.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                goOAuth(Login.this);
                mDialog.dismiss();
                Login.this.finish();// 关闭当前activity
            }
        });
    }

    // 跳转到主页面
    @SuppressWarnings("unchecked")
    public void goHome() {

        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(Login.this);
        }
        mProgressDialog.setMessage("正在登录中....");
        mProgressDialog.show();
        @SuppressWarnings("rawtypes")
        HashMap param = new HashMap();
        param.put("token", mToken);// 登陆请求参数token
        param.put("secret", mSecret);// 登陆请求参数secret
        // 将map放到Task参数中 传递到Service中
        Task loginTask = new Task(Task.TASK_USER_LOGIN, param);
        MainService.newTask(loginTask);// 将当前任务发送到Service的任务队列中

    }

    // 拼装当前的URL
    public static void goOAuth(Context context) {
        System.setProperty("weibo4j.oauth.consumerKey", Weibo.CONSUMER_KEY);
        System.setProperty("weibo4j.oauth.consumerSecret", Weibo.CONSUMER_SECRET);
        Weibo weibo = new Weibo();
        RequestToken requestToken;
        try {
            requestToken = weibo.getOAuthRequestToken("weibo4android://OAuthActivity");
            OAuthConstant.getInstance().setRequestToken(requestToken);
            Uri uri = Uri.parse(requestToken.getAuthenticationURL() + "&display=mobile");
            context.startActivity(new Intent(Intent.ACTION_VIEW, uri));
        } catch (WeiboException e) {
            e.printStackTrace();
        }
    }

    // 初始话化数据 判断首选项是否保存了当前用户的信息
    @Override
    public void init() {
        InitViewInfo();// 初始化一些基本信息
        if (WeiboUtil.checkNet(Login.this)) {
            // 判断自动登录
            if (mIsAutologin) {
                mAutologin.setChecked(true);
                goHome();
            }
        } else {
            MainService.AlertNetError(this);
        }

    }

    private void InitViewInfo() {
        // 判断是否是自动登录
        mAutologin = (CheckBox) this.findViewById(R.id.auto_login);
        mAutologin.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SaveLoginParam.savaautoLogin(Login.this, isChecked);
            }
        });
        mIsAutologin = SaveLoginParam.getauto(Login.this);
        EditText editText = (EditText) this.findViewById(R.id.user);
        mParam = SaveLoginParam.getnowuserparam(this);
        if (mParam.get("token") != null) {// 有的话将用户昵称显示在EditText中
            editText.setText(mParam.get("userName"));
            mToken = mParam.get("token");
            mSecret = mParam.get("secret");
        } else {// 如果没有
            dialogshow();// 弹出认证Dialog
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Exit.btexit(Login.this);// 当我们按下返回键的时候要执行的动作
            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    @Override
    public void refresh(Object... param) {
        int flag = ((Integer) param[0]).intValue();// 获取第一个参数
        switch (flag) {
            case REFRESH_LOGIN:
                Toast.makeText(Login.this, "登录成功", 3000).show();
                Log.i("yanzheng", ((Integer) param[0]).intValue() + "loginrafush");
                if (mProgressDialog != null) {
                    mProgressDialog.dismiss();
                }
                Intent it = new Intent(this, MainActivity.class);
                this.startActivity(it);
                MainService.mAllActivity.remove(this);
                finish();
                break;
        }
    }

}

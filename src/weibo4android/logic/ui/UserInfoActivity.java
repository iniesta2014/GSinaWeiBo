
package weibo4android.logic.ui;

import java.net.URL;

import weibo4android.User;
import weibo4android.logic.MainService;
import weibo4android.logic.R;
import weibo4android.logic.ui.imaCache.Anseylodar;
import weibo4android.util.Exit;
import weibo4android.util.WeiboUtil;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class UserInfoActivity extends Activity implements Runnable {
    private TextView mShowtitle;// 标题
    private ImageView mUserprotrait;// 头像
    private TextView mUsernickname;// 昵称
    private Anseylodar mAnseylodar;
    private TextView mUseradress, mUserloginname;// 地址.登录名称
    // 用户 关注数,微博数,粉丝数,话题数
    private TextView mUserattnum, mUserweibonum, mUserfansnum, mUsertopnum;
    private TextView mUsercollectnum, mUserblacknum;// 用户收藏,黑名单
    private User mNowuserinfo; // 当前的用户
    private ImageButton mUserFav, mUserweibo;
    private ImageView mBack;
    private Button mCreatefriendship;// 关注
    private LinearLayout mUserfavLayout;// 收藏和黑名单布局
    public static final int GET_OK = 0;
    public static final int GET_FAIL = 1;
    public boolean mIsOther;
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case GET_OK:
                    try {
                        // 这里把人头像的图片转换成了180*180尺寸的大图了
                        URL url = WeiboUtil.getString(mNowuserinfo.getProfileImageURL());
                        mAnseylodar.showimgAnsy(mUserprotrait, url.toString());
                        mUsernickname.setText(mNowuserinfo.getScreenName());
                        mUseradress.setText(mNowuserinfo.getLocation());
                        mUserloginname.setText(mNowuserinfo.getUserDomain());
                        mUserattnum.setText(mNowuserinfo.getFriendsCount() + "");
                        mUserweibonum.setText(mNowuserinfo.getStatusesCount() + "");
                        mUserfansnum.setText(mNowuserinfo.getFollowersCount() + "");
                        mUsertopnum.setText("0");
                        mUsercollectnum.setText(mNowuserinfo.getFavouritesCount() + "");
                        mUserblacknum.setText("0");
                        if (mIsOther) {// 如果是另外一个页面启动到这个页面
                            mBack.setImageResource(R.drawable.title_back);
                            if (mNowuserinfo.getId() != MainService.mNowuser.getId()) {
                                mUserfavLayout.setVisibility(View.GONE);
                                mCreatefriendship.setText(R.string.destoryfirendship);
                            }
                        }
                    } catch (Exception e) {
                        Toast.makeText(UserInfoActivity.this, "ERROR", 3000).show();
                        e.printStackTrace();
                    }
                    break;
                case GET_FAIL:
                    Toast.makeText(UserInfoActivity.this, R.string.getfail, 3000).show();
                    break;
            }
        }

    };

    @SuppressWarnings("static-access")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(getWindow().FEATURE_NO_TITLE);
        setContentView(R.layout.userinfo);
        Thread users = new Thread(this);
        users.start();
        mAnseylodar = new Anseylodar();
        initview();
    }

    public void initview() {
        View title = this.findViewById(R.id.freelook_title_userinfo);
        mBack = (ImageView) title.findViewById(R.id.title_bt_left);
        mShowtitle = (TextView) title.findViewById(R.id.tvinfo);
        mShowtitle.setText(R.string.userinfo);
        mUserprotrait = (ImageView) this.findViewById(R.id.user_portrait);
        mUsernickname = (TextView) this.findViewById(R.id.user_nickname);
        mUseradress = (TextView) this.findViewById(R.id.user_adress);
        mUserloginname = (TextView) this.findViewById(R.id.user_loginname);
        mUserattnum = (TextView) this.findViewById(R.id.user_attention_num);
        mUserweibonum = (TextView) this.findViewById(R.id.user_weibo_num);
        mUserfansnum = (TextView) this.findViewById(R.id.user_fans_num);
        mUsertopnum = (TextView) this.findViewById(R.id.user_topic_num);
        mUsercollectnum = (TextView) this.findViewById(R.id.user_collect_num);
        mUserblacknum = (TextView) this.findViewById(R.id.user_blacklist_num);
        mUserFav = (ImageButton) this.findViewById(R.id.user_fav_bt);
        mUserweibo = (ImageButton) this.findViewById(R.id.user_weibo);
        mUserfavLayout = (LinearLayout) title.findViewById(R.id.Userfavlin);
        mCreatefriendship = (Button) this.findViewById(R.id.button1);
        mCreatefriendship.setOnClickListener(new OnClickListener() {
            // 取消对某用户的关注
            @Override
            public void onClick(View v) {

            }
        });
        mBack.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                UserInfoActivity.this.finish();
            }
        });
        mUserFav.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // Intent intent=new
                // Intent(UserInfoActivity.this,FavLlist.class);
                // intent.putExtra("userid",
                // String.valueOf(MainService.nowuser.getId()));
                // UserInfoActivity.this.startActivity(intent);
            }
        });
        mUserweibo.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // Intent intent=new
                // Intent(UserInfoActivity.this,UserWeibolist.class);
                // intent.putExtra("userid",
                // String.valueOf(MainService.nowuser.getId()));
                // UserInfoActivity.this.startActivity(intent);
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (!mIsOther) {
                Exit.btexit(UserInfoActivity.this);// 当我们按下返回键的时候要执行的动作
                return true;
            } else {
                return false;
            }
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    @Override
    public void run() {
        Intent intent = getIntent();
        String userid = intent.getStringExtra("userid");
        if (userid == null) {
            // 直接到我的资料页面
            mNowuserinfo = MainService.mNowuser;
            mHandler.sendEmptyMessage(0);
            mIsOther = false;
        } else {
            mNowuserinfo = WeiboUtil.getuserbyid(UserInfoActivity.this, userid);
            if (mNowuserinfo != null) {
                mIsOther = true;
                mHandler.sendEmptyMessage(0);
            } else {
                mHandler.sendEmptyMessage(1);
            }

        }
    }
}

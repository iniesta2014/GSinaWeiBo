
package weibo4android.logic.weibo.ui;

import java.net.URL;
import java.util.List;

import weibo4android.Status;
import weibo4android.logic.R;
import weibo4android.logic.http.PostParameter;
import weibo4android.logic.ui.UserInfoActivity;
import weibo4android.logic.ui.imaCache.Anseylodar;
import weibo4android.util.WeiboUtil;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class WeiboInfo extends Activity implements Runnable {

    ImageView mTweet_profile_preview;// 发微博人的头像
    TextView mTweet_profile_name;// 发微博的人
    TextView mTweet_message;// 微博内容
    ImageView mTweet_upload_pic;
    TextView mTweet_oriTxt;// 转发内容
    ImageView mTweet_upload_pic2;// 转发内容图片
    public Status mStatus;// 返回的微博内容
    public Status mTweetstatus;// 转发内容
    LinearLayout mTweetstatusview;// 转发微博内容页面
    ImageView mBack;// 返回
    View mProgress;// 圆形进度条
    Button mComment, mRedirect;// 评论和转发按钮
    TextView mComment_num, mRedirect_num;// 条数
    TextView mTitle;
    Anseylodar mAnseylodar;
    RelativeLayout mTweet_profile;//
    List<PostParameter> mParams;
    // 下面的5个按钮 刷新 评论 转发 收藏 更多
    TextView mReload, mTvComment, mTvForward, mTvFav, mTvMore;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            refreshview();// 刷新页面内容
            // 如果使用又转发 则显示又转发页面
            if (mTweetstatus != null) {
                mTweetstatusview.setVisibility(View.VISIBLE);
                refeshtweestatus();
            }
        }
    };

    @SuppressWarnings("static-access")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(getWindow().FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.detailweibo);
        initview();// 初始化 页面
        // 又转发内容的布局
        mTweetstatusview = (LinearLayout) this.findViewById(R.id.src_text_block);
        // System.out.println("传递页信息"+status);
        mAnseylodar = new Anseylodar();
        Thread statusT = new Thread(this);
        statusT.start();
    }

    public void initview() {
        try {
            mProgress = this.findViewById(R.id.detaweibo_progress);
            View title = this.findViewById(R.id.detailweibo_title);
            mTweet_profile_preview = (ImageView) this.findViewById(R.id.tweet_profile_preview);
            mTweet_profile_name = (TextView) this.findViewById(R.id.tweet_profile_name);
            mTweet_message = (TextView) this.findViewById(R.id.tweet_message);
            mTweet_upload_pic = (ImageView) this.findViewById(R.id.tweet_upload_pic);
            mTweet_oriTxt = (TextView) this.findViewById(R.id.tweet_oriTxt);
            mTweet_upload_pic2 = (ImageView) this.findViewById(R.id.tweet_upload_pic2);
            mTitle = (TextView) title.findViewById(R.id.tvinfo);
            mTitle.setText(R.string.weiboinfo);
            mBack = (ImageView) title.findViewById(R.id.title_bt_left);
            mBack.setImageResource(R.drawable.title_back);
            Button home = (Button) title.findViewById(R.id.title_bt_right);
            home.setVisibility(View.GONE);
            mReload = (TextView) this.findViewById(R.id.tvReload);
            mTvComment = (TextView) this.findViewById(R.id.tvComment);
            mTvForward = (TextView) this.findViewById(R.id.tvForward);
            mTvFav = (TextView) this.findViewById(R.id.tvFav);
            mTvMore = (TextView) this.findViewById(R.id.tvMore);
            mTweet_profile = (RelativeLayout) this.findViewById(R.id.tweet_profile);
            // 底部菜单点击事件
            mReload.setOnClickListener(new textclick());
            mTvComment.setOnClickListener(new textclick());
            mTvForward.setOnClickListener(new textclick());
            mTvFav.setOnClickListener(new textclick());
            mTvMore.setOnClickListener(new textclick());

            mComment = (Button) this.findViewById(R.id.detail_comment);
            mRedirect = (Button) this.findViewById(R.id.detail_redirect);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 返回
        mBack.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                WeiboInfo.this.finish();
            }
        });
        // 底部评论按钮
        mComment.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                goComment();
            }
        });
        // 底部转发按钮
        mRedirect.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                goRediret();
            }
        });
        // 用户布局那快 点击跳转到用户信息页面
        mTweet_profile.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String userid = String.valueOf(mStatus.getUser().getId());
                Intent intent = new Intent(WeiboInfo.this, UserInfoActivity.class);
                intent.putExtra("userid", userid);
                WeiboInfo.this.startActivity(intent);
            }
        });
    }

    // 跳转到评论页面(单条评论)
    private void goComment() {
        long statusid = mStatus.getId();
        Intent intent = new Intent(WeiboInfo.this, AddComment.class);
        intent.putExtra("statusid", statusid);
        WeiboInfo.this.startActivity(intent);
    }

    // 跳转到转发页面
    private void goRediret() {
        Intent intent = new Intent(WeiboInfo.this, Respostweibo.class);
        // 当前微博ID
        intent.putExtra("sid", String.valueOf(mStatus.getId()));
        intent.putExtra("status", "@" + mStatus.getUser().getScreenName() + " "
                + mStatus.getText().toString());
        intent.putExtra("user", mStatus.getUser().getName().toString());
        WeiboInfo.this.startActivity(intent);
    }

    // 底部5个按钮的点击事件
    public class textclick implements OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.tvReload:// 刷新
                    break;
                case R.id.tvComment:// 评论
                    goComment();
                    break;
                case R.id.tvForward:// 转发
                    goRediret();
                    break;
                case R.id.tvFav:// 收藏
                    WeiboUtil.addFav(WeiboInfo.this, mStatus.getId());
                    Toast.makeText(WeiboInfo.this, "收藏成功", 3000).show();
                    break;
                case R.id.tvMore:// 更多
                    break;
            }
        }
    }

    public void refreshview() {
        try {
            // 发表微博人的名字
            mTweet_profile_name.setText(mStatus.getUser().getScreenName());
            // 微博内容
            mTweet_message.setText(mStatus.getText().toString());
            mProgress.setVisibility(View.GONE);
            // 这里把人头像的图片转换成了180*180尺寸的大图了
            URL url = WeiboUtil.getString(mStatus.getUser().getProfileImageURL());
            // 异步加载头像图片
            mAnseylodar.showimgAnsy(mTweet_profile_preview, url.toString());
            if (mStatus.getOriginal_pic() != null) {
                // 一步加载内容图片
                mAnseylodar.showimgAnsy(mTweet_upload_pic, mStatus.getThumbnail_pic());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 如果是又转发 则加载又转发内容
    public void refeshtweestatus() {
        // 获取转发内容和图片
        try {
            // 又转发内容
            mTweet_oriTxt.setText(mTweetstatus.getText().toString());
            // 异步加载图片
            mAnseylodar.showimgAnsy(mTweet_upload_pic2, mTweetstatus.getThumbnail_pic());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        // 获取HomeActivity发来的数据
        Intent data = getIntent();
        mStatus = (Status) data.getExtras().getSerializable("status");
        // 获取又转发内容
        mTweetstatus = mStatus.getRetweeted_status();
        mHandler.sendEmptyMessage(0);
    }
}


package weibo4android.logic.weibo.ui;

import weibo4android.logic.R;
import weibo4android.util.WeiboUtil;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class AddComment extends Activity {

    CheckBox mBoxwbsub;
    Button mSubmit;
    Button mBack, mHome;
    EditText mCmtReason;
    boolean mSendweibo = false;
    long mStatusID;
    String mContent = null;

    @SuppressWarnings("static-access")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(getWindow().FEATURE_NO_TITLE);
        setContentView(R.layout.comment);
        View titleview = this.findViewById(R.id.title);
        mBack = (Button) titleview.findViewById(R.id.title_bt_left);
        mHome = (Button) titleview.findViewById(R.id.title_bt_right);
        TextView tvtitleTextView = (TextView) titleview
                .findViewById(R.id.tvHomeUserName);
        tvtitleTextView.setText(R.string.comment);
        mBoxwbsub = (CheckBox) this.findViewById(R.id.rb_forward);
        mSubmit = (Button) this.findViewById(R.id.bt_submit);
        mCmtReason = (EditText) this.findViewById(R.id.etCmtReason);
        // 获取WeiboInfo页面传递过来的信息内容
        Intent intent = getIntent();
        try {// 评论微博的ID
            mStatusID = intent.getLongExtra("statusid", 123);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 返回
        mBack.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                AddComment.this.finish();
            }
        });
        // 主页面
        mHome.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                AddComment.this.finish();
            }
        });
        // 判断是否 同时发表一篇微博
        mBoxwbsub.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                    boolean isChecked) {
                // 判断是否同时发布一条微博
                mSendweibo = isChecked;
            }
        });
        // 提交
        mSubmit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String staus = mCmtReason.getText().toString();
                if (null != staus) {
                    // 发送微博评论调用WeiboUtil的方法
                    boolean isok = WeiboUtil.sendComment(AddComment.this,
                            staus, String.valueOf(mStatusID));
                    if (isok) {// 评论成功
                        Toast.makeText(AddComment.this,
                                R.string.new_comment_succeed, 3000).show();
                    } else {// 评论失败
                        Toast.makeText(AddComment.this,
                                R.string.new_comment_fail, 3000).show();
                    }
                    if (mSendweibo) {
                        // 发送微博
                        boolean isok2 = WeiboUtil.updateweibo(AddComment.this, staus);
                        if (isok2) {// 提示发送结果
                            Toast.makeText(AddComment.this, R.string.new_weibo_succeed, 3000)
                                    .show();
                        } else {
                            Toast.makeText(AddComment.this, R.string.new_weibo_fail, 3000).show();
                        }
                    }
                } else {// 评论内容不许为空
                    Toast.makeText(AddComment.this, R.string.notnull, 3000).show();
                }
                AddComment.this.finish();// 发送完毕返回上一页
            }
        });

    }

}

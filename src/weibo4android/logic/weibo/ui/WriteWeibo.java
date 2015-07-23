
package weibo4android.logic.weibo.ui;

import java.util.HashMap;

import weibo4android.Status;
import weibo4android.Weibo;
import weibo4android.WeiboException;
import weibo4android.logic.R;
import weibo4android.util.SaveLoginParam;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class WriteWeibo extends Activity {

    ImageView mBack;// 返回键
    Button mSend;// 发送键
    EditText mEtblogEditText;// 微博信息
    LinearLayout mUpdatelay;// 状态布局
    TextView mNowtite; // 提示信息
    public static weibo4android.Weibo mWeibo;

    @SuppressWarnings("static-access")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(getWindow().FEATURE_NO_TITLE);
        setContentView(R.layout.newblog);
        View titleview = this.findViewById(R.id.newblog_title);
        mBack = (ImageView) titleview.findViewById(R.id.title_bt_left);
        mBack.setImageResource(R.drawable.title_back);
        mSend = (Button) titleview.findViewById(R.id.title_bt_right);
        mNowtite = (TextView) titleview.findViewById(R.id.tvinfo);
        mEtblogEditText = (EditText) this.findViewById(R.id.etBlog);
        mBack.setOnClickListener(new ontitlebtclick());
        mSend.setOnClickListener(new ontitlebtclick());
        mUpdatelay = (LinearLayout) this.findViewById(R.id.linear_progress);
        mNowtite.setText(R.string.newweibo);
    }

    public class ontitlebtclick implements OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.title_bt_left:
                    // 返回 结束当前Activity
                    WriteWeibo.this.finish();
                    break;
                case R.id.title_bt_right:
                    // 发送 进度布局
                    mUpdatelay.setVisibility(View.VISIBLE);
                    // 获取EditText中用户写的微博内容
                    String bloginfo = mEtblogEditText.getText().toString();
                    boolean isok = updateStatus(bloginfo, WriteWeibo.this);
                    if (isok) {// 如果发送成功
                        Toast.makeText(WriteWeibo.this, "发送成功", 3000).show();
                        WriteWeibo.this.finish();
                    } else {
                        mUpdatelay.setVisibility(View.GONE);
                        Toast.makeText(WriteWeibo.this, "发送错误.", 3000).show();
                    }
                    break;
            }
        }
    }

    /**
     * 发送一条微博
     * 
     * @param status 微博内容
     * @param context 上下文
     * @return 是否发送成功
     */
    @SuppressWarnings("deprecation")
    public boolean updateStatus(String status, Context context) {
        mWeibo = new Weibo();
        // 因为发表微博需要重新重新认证,这里我们需要在次setToken
        HashMap<String, String> param =
                SaveLoginParam.getnowuserparam(context);
        mWeibo.setToken(param.get("token"), param.get("secret"));
        try {
            // 这是微博类的一个方法
            Status status2 = mWeibo.update(status);
            if (status2 != null) {
                return true;
            } else {
                return false;
            }
        } catch (WeiboException e) {
            e.printStackTrace();
            return false;
        }

    }
}


package weibo4android.logic.ui;

import weibo4android.User;
import weibo4android.Weibo;
import weibo4android.WeiboException;
import weibo4android.logic.R;
import weibo4android.logic.http.AccessToken;
import weibo4android.logic.http.OAuthConstant;
import weibo4android.logic.http.RequestToken;
import weibo4android.util.SaveLoginParam;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class OAuthActivity extends Activity {
    public static Weibo mWeibo;
    String mToke = null;
    String mSecret = null;
    User mUser;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.timeline);
        Uri uri = this.getIntent().getData();
        try {
            RequestToken requestToken = OAuthConstant.getInstance().getRequestToken();
            AccessToken accessToken = requestToken.getAccessToken(uri
                    .getQueryParameter("oauth_verifier"));
            OAuthConstant.getInstance().setAccessToken(accessToken);
            TextView textView = (TextView) findViewById(R.id.TextView01);
            if (accessToken.getToken() == null) {
                textView.setText("由于你的网络环境的问题,你需要返回重新授权,或者检查你的网络重新授权!!!");
            } else {
                textView.setText("得到AccessToken的key和Secret,可以使用这两个参数进行授权登录了" +
                        ".\n Access token:\n" + accessToken.getToken() +
                        "\n Access token secret:\n" + accessToken.getTokenSecret());
                mToke = accessToken.getToken();
                mSecret = accessToken.getTokenSecret();
                System.out.println(mToke + "密钥" + mSecret);
            }
        } catch (WeiboException e) {
            e.printStackTrace();
        }

        Button btgologin = (Button) this.findViewById(R.id.btgologin);
        btgologin.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                try {
                    Log.i("vista", "111111");
                    mWeibo = OAuthConstant.getInstance().getWeibo();
                    mWeibo.setToken(mToke, mSecret);// 根据我们url返回的密匙去认证登陆
                } catch (Exception e) {
                    Log.i("vista", "222222");
                    e.printStackTrace();
                    Toast.makeText(OAuthActivity.this, "网络失败", 3000).show();
                    return;
                }
                try {
                    Log.i("vista", "333333");
                    mUser = mWeibo.verifyCredentials();// 如果认证成功返回给我们一个User对象
                } catch (WeiboException e) {
                    Log.i("vista", "444444");
                    Toast.makeText(OAuthActivity.this, "登录失败", 3000).show();
                    e.printStackTrace();
                }
                Log.i("vista", "55555");
                // 将认证的密匙以及当前用户的信息保存在首选项
                SaveLoginParam.savanowuserparam(
                        OAuthActivity.this,
                        String.valueOf(mUser.getId()),
                        mSecret,
                        mUser.getScreenName(),
                        mToke);
                Toast.makeText(OAuthActivity.this, "认证信息已保存", 3000).show();
                // 跳转到登陆页面 进行登陆
                Intent intent = new Intent(OAuthActivity.this, Login.class);
                startActivity(intent);
            }
        });

    }
}

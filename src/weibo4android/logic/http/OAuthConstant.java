
package weibo4android.logic.http;

import weibo4android.Weibo;
import weibo4android.logic.http.AccessToken;
import weibo4android.logic.http.RequestToken;

public class OAuthConstant {
    private static Weibo mWeibo = null;
    private static OAuthConstant mInstance = null;
    private RequestToken mRequestToken;
    private AccessToken mAccessToken;
    private String mToken;
    private String mTokenSecret;

    private OAuthConstant() {
    };

    public static synchronized OAuthConstant getInstance() {
        if (mInstance == null)
            mInstance = new OAuthConstant();
        return mInstance;
    }

    public Weibo getWeibo() {
        if (mWeibo == null)
            mWeibo = new Weibo();
        return mWeibo;
    }

    public AccessToken getAccessToken() {
        return mAccessToken;
    }

    public void setAccessToken(AccessToken accessToken) {
        this.mAccessToken = accessToken;
        this.mToken = accessToken.getToken();
        this.mTokenSecret = accessToken.getTokenSecret();
    }

    public RequestToken getRequestToken() {
        return mRequestToken;
    }

    public void setRequestToken(RequestToken requestToken) {
        this.mRequestToken = requestToken;
    }

    public String getToken() {
        return mToken;
    }

    public void setToken(String token) {
        this.mToken = token;
    }

    public String getTokenSecret() {
        return mTokenSecret;
    }

    public void setTokenSecret(String tokenSecret) {
        this.mTokenSecret = tokenSecret;
    }

}

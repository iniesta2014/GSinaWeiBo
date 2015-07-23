/*
Copyright (c) 2007-2009, Yusuke Yamamoto
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright
      notice, this list of conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above copyright
      notice, this list of conditions and the following disclaimer in the
      documentation and/or other materials provided with the distribution.
 * Neither the name of the Yusuke Yamamoto nor the
      names of its contributors may be used to endorse or promote products
      derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY Yusuke Yamamoto ``AS IS'' AND ANY
EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL Yusuke Yamamoto BE LIABLE FOR ANY
DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package weibo4android.logic.http;

import weibo4android.WeiboException;

public class RequestToken extends OAuthToken {
    private HttpClient mHttpClient;
    private static final long serialVersionUID = -8214365845469757952L;

    RequestToken(Response res, HttpClient httpClient) throws WeiboException {
        super(res);
        this.mHttpClient = httpClient;
    }

    RequestToken(String token, String tokenSecret) {
        super(token, tokenSecret);
    }

    public String getAuthorizationURL() {
        return mHttpClient.getAuthorizationURL() + "?oauth_token=" + getToken();
    }

    /**
     * since Weibo4J 2.0.10
     */
    public String getAuthenticationURL() {
        return mHttpClient.getAuthenticationRL() + "?oauth_token=" + getToken();
    }

    public AccessToken getAccessToken(String pin) throws WeiboException {
        return mHttpClient.getOAuthAccessToken(this, pin);
    }

    public HttpClient getHttpClient() {
        return mHttpClient;
    }

    public void setHttpClient(HttpClient httpClient) {
        this.mHttpClient = httpClient;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        if (!super.equals(o))
            return false;

        RequestToken that = (RequestToken) o;

        if (mHttpClient != null ? !mHttpClient.equals(that.mHttpClient) : that.mHttpClient != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (mHttpClient != null ? mHttpClient.hashCode() : 0);
        return result;
    }
}

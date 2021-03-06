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

import javax.crypto.spec.SecretKeySpec;

import weibo4android.WeiboException;

import java.io.Serializable;

abstract class OAuthToken implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 2385887178385032767L;

	private String mToken;
    private String mTokenSecret;

    private transient SecretKeySpec mSecretKeySpec;
    String[] mResponseStr = null;

    public OAuthToken(String token, String tokenSecret) {
        this.mToken = token;
        this.mTokenSecret = tokenSecret;
    }

    OAuthToken(Response response) throws WeiboException {
        this(response.asString());
    }
    OAuthToken(String string) {
        mResponseStr = string.split("&");
        mTokenSecret = getParameter("oauth_token_secret");
        mToken = getParameter("oauth_token");
    }

    public String getToken() {
        return mToken;
    }

    public String getTokenSecret() {
        return mTokenSecret;
    }

    /*package*/ void setSecretKeySpec(SecretKeySpec secretKeySpec) {
        this.mSecretKeySpec = secretKeySpec;
    }

    /*package*/ SecretKeySpec getSecretKeySpec() {
        return mSecretKeySpec;
    }

    public String getParameter(String parameter) {
    	String value = null;
        for (String str : mResponseStr) {
        	if (str.startsWith(parameter+'=')) {
        		value = str.split("=")[1].trim();
            	break;
            }
        }
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OAuthToken)) return false;

        OAuthToken that = (OAuthToken) o;

        if (mSecretKeySpec != null ? !mSecretKeySpec.equals(that.mSecretKeySpec) : that.mSecretKeySpec != null)
            return false;
        if (!mToken.equals(that.mToken)) return false;
        if (!mTokenSecret.equals(that.mTokenSecret)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = mToken.hashCode();
        result = 31 * result + mTokenSecret.hashCode();
        result = 31 * result + (mSecretKeySpec != null ? mSecretKeySpec.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "OAuthToken{" +
                "token='" + mToken + '\'' +
                ", tokenSecret='" + mTokenSecret + '\'' +
                ", secretKeySpec=" + mSecretKeySpec +
                '}';
    }
}

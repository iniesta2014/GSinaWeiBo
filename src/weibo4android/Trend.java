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

package weibo4android;

import weibo4android.org.json.JSONException;
import weibo4android.org.json.JSONObject;

public class Trend implements java.io.Serializable {
    private String mName;
    private String mQuery = null;
    private static final long serialVersionUID = 1925956704460743946L;

    public Trend(JSONObject json) throws JSONException {
        this.mName = json.getString("name");
        if (!json.isNull("query")) {
            this.mQuery = json.getString("query");
        }
    }

    public String getName() {
        return mName;
    }

    public String getQuery() {
        return mQuery;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Trend))
            return false;

        Trend trend = (Trend) o;

        if (!mName.equals(trend.mName))
            return false;
        if (mQuery != null ? !mQuery.equals(trend.mQuery) : trend.mQuery != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = mName.hashCode();
        result = 31 * result + (mQuery != null ? mQuery.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Trend{" +
                "name='" + mName + '\'' +
                ", query='" + mQuery + '\'' +
                '}';
    }
}

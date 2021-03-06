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

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Collections;
import java.util.Arrays;

import weibo4android.logic.http.Response;
import weibo4android.org.json.JSONArray;
import weibo4android.org.json.JSONException;
import weibo4android.org.json.JSONObject;

public class Trends extends WeiboResponse implements Comparable<Trends> {
    private Date mAsOf;
    private Date mTrendAt;
    private Trend[] mTrends;
    private static final long serialVersionUID = -7151479143843312309L;

    public int compareTo(Trends that) {
        return this.mTrendAt.compareTo(that.mTrendAt);
    }

    /*package*/ Trends(Response res, Date asOf, Date trendAt, Trend[] trends)
            throws WeiboException {
        super(res);
        this.mAsOf = asOf;
        this.mTrendAt = trendAt;
        this.mTrends = trends;
    }

    /*package*/
    static List<Trends> constructTrendsList(Response res) throws
            WeiboException {
        JSONObject json = res.asJSONObject();
        List<Trends> trends;
        try {
            Date asOf = parseDate(json.getString("as_of"));
            JSONObject trendsJson = json.getJSONObject("trends");
            trends = new ArrayList<Trends>(trendsJson.length());
            Iterator ite = trendsJson.keys();
            while (ite.hasNext()) {
                String key = (String) ite.next();
                JSONArray array = trendsJson.getJSONArray(key);
                Trend[] trendsArray = jsonArrayToTrendArray(array);
                if (key.length() == 19) {
                    // current trends
                    trends.add(new Trends(res, asOf, parseDate(key
                            , "yyyy-MM-dd HH:mm:ss"), trendsArray));
                } else if (key.length() == 16) {
                    // daily trends
                    trends.add(new Trends(res, asOf, parseDate(key
                            , "yyyy-MM-dd HH:mm"), trendsArray));
                } else if (key.length() == 10) {
                    // weekly trends
                    trends.add(new Trends(res, asOf, parseDate(key
                            , "yyyy-MM-dd"), trendsArray));
                }
            }
            Collections.sort(trends);
            return trends;
        } catch (JSONException jsone) {
            throw new WeiboException(jsone.getMessage() + ":" + res.asString(), jsone);
        }
    }

    /*package*/
    static Trends constructTrends(Response res) throws WeiboException {
        JSONObject json = res.asJSONObject();
        try {
            Date asOf = parseDate(json.getString("as_of"));
            JSONArray array = json.getJSONArray("trends");
            Trend[] trendsArray = jsonArrayToTrendArray(array);
            return new Trends(res, asOf, asOf, trendsArray);
        } catch (JSONException jsone) {
            throw new WeiboException(jsone.getMessage() + ":" + res.asString(), jsone);
        }
    }

    private static Date parseDate(String asOfStr) throws WeiboException {
        Date parsed;
        if (asOfStr.length() == 10) {
            parsed = new Date(Long.parseLong(asOfStr) * 1000);
        } else {
            parsed = WeiboResponse.parseDate(asOfStr, "EEE, d MMM yyyy HH:mm:ss z");
        }
        return parsed;
    }

    private static Trend[] jsonArrayToTrendArray(JSONArray array) throws JSONException {
        Trend[] trends = new Trend[array.length()];
        for (int i = 0; i < array.length(); i++) {
            JSONObject trend = array.getJSONObject(i);
            trends[i] = new Trend(trend);
        }
        return trends;
    }

    public Trend[] getTrends() {
        return this.mTrends;
    }

    public Date getAsOf() {
        return mAsOf;
    }

    public Date getTrendAt() {
        return mTrendAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Trends)) return false;

        Trends trends1 = (Trends) o;

        if (mAsOf != null ? !mAsOf.equals(trends1.mAsOf) : trends1.mAsOf != null)
            return false;
        if (mTrendAt != null ? !mTrendAt.equals(trends1.mTrendAt) : trends1.mTrendAt != null)
            return false;
        if (!Arrays.equals(mTrends, trends1.mTrends)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = mAsOf != null ? mAsOf.hashCode() : 0;
        result = 31 * result + (mTrendAt != null ? mTrendAt.hashCode() : 0);
        result = 31 * result + (mTrends != null ? Arrays.hashCode(mTrends) : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Trends{" +
                "asOf=" + mAsOf +
                ", trendAt=" + mTrendAt +
                ", trends=" + (mTrends == null ? null : Arrays.asList(mTrends)) +
                '}';
    }
}

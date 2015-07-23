
package weibo4android;

import java.util.ArrayList;
import java.util.List;

import weibo4android.logic.http.Response;
import weibo4android.org.json.JSONArray;
import weibo4android.org.json.JSONException;
import weibo4android.org.json.JSONObject;

public class UserTrend extends WeiboResponse {
    private String mNum;
    private String mHotword = null;
    private String mThread = null;
    private static final long serialVersionUID = 1925956704460743946L;

    public UserTrend() {
        super();
    }

    public UserTrend(Response res) throws WeiboException {
        super(res);
        JSONObject json = res.asJSONObject();
        try {
            mNum = json.getString("num");
            mHotword = json.getString("hotword");
            mThread = json.getString("trend_id");
            if (json.getString("topicid") != null)
                mThread = json.getString("topicid");
        } catch (JSONException je) {
            throw new WeiboException(je.getMessage() + ":" + json.toString(),
                    je);
        }
    }

    public UserTrend(JSONObject json) throws WeiboException {
        try {
            mNum = json.getString("num");
            mHotword = json.getString("hotword");
            mThread = json.getString("trend_id");
        } catch (JSONException je) {
            throw new WeiboException(je.getMessage() + ":" + json.toString(),
                    je);
        }
    }

    static List<UserTrend> constructTrendList(Response res) throws WeiboException {
        try {
            JSONArray list = res.asJSONArray();
            int size = list.length();
            List<UserTrend> trends = new ArrayList<UserTrend>(size);
            for (int i = 0; i < size; i++) {
                trends.add(new UserTrend(list.getJSONObject(i)));
            }
            return trends;
        } catch (JSONException jsone) {
            throw new WeiboException(jsone);
        } catch (WeiboException te) {
            throw te;
        }

    }

    public String getNum() {
        return mNum;
    }

    public void setNum(String num) {
        this.mNum = num;
    }

    public String getHotword() {
        return mHotword;
    }

    public void setHotword(String hotword) {
        this.mHotword = hotword;
    }

    public String getTrend_id() {
        return mThread;
    }

    public void setTrend_id(String trend_id) {
        this.mThread = trend_id;
    }

    @Override
    public String toString() {
        return "Trend [num=" + mNum + ", hotword=" + mHotword + ", trend_id="
                + mThread + "]";
    }

}

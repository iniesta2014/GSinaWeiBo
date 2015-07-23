
package weibo4android;

import java.util.ArrayList;
import java.util.List;

import weibo4android.logic.http.Response;
import weibo4android.org.json.JSONArray;
import weibo4android.org.json.JSONException;
import weibo4android.org.json.JSONObject;

public class Emotion extends WeiboResponse {
    private static final long serialVersionUID = -4096813631691846494L;

    private String mPhrase;

    private String mType;

    private String mUrl;

    private boolean mIsHot;

    private boolean mIsCommon;

    private int mOrderName;

    private String mCategory;

    public Emotion(Response res) throws WeiboException {
        super(res);
        JSONObject json = res.asJSONObject();
        try {
            mPhrase = json.getString("phrase");
            mType = json.getString("type");
            mUrl = json.getString("url");
            mIsHot = json.getBoolean("is_hot");
            mOrderName = json.getInt("order_number");
            mCategory = json.getString("category");
            mIsCommon = json.getBoolean("is_common");
        } catch (JSONException je) {
            throw new WeiboException(je.getMessage() + ":" + json.toString(),
                    je);
        }
    }

    public Emotion(JSONObject json) throws WeiboException {
        try {
            mPhrase = json.getString("phrase");
            mType = json.getString("type");
            mUrl = json.getString("url");
            mIsHot = json.getBoolean("is_hot");
            mOrderName = json.getInt("order_number");
            mCategory = json.getString("category");
            mIsCommon = json.getBoolean("is_common");
        } catch (JSONException je) {
            throw new WeiboException(je.getMessage() + ":" + json.toString(),
                    je);
        }
    }

    static List<Emotion> constructEmotions(Response res) throws WeiboException {
        try {
            JSONArray list = res.asJSONArray();
            int size = list.length();
            List<Emotion> emotions = new ArrayList<Emotion>(size);
            for (int i = 0; i < size; i++) {
                emotions.add(new Emotion(list.getJSONObject(i)));
            }
            return emotions;
        } catch (JSONException jsone) {
            throw new WeiboException(jsone);
        } catch (WeiboException te) {
            throw te;
        }

    }

    public Emotion() {
        super();
    }

    public String getPhrase() {
        return mPhrase;
    }

    public void setPhrase(String phrase) {
        this.mPhrase = phrase;
    }

    public String getType() {
        return mType;
    }

    public void setType(String type) {
        this.mType = type;
    }

    public String getUrl() {
        return mUrl;
    }

    public void setUrl(String url) {
        this.mUrl = url;
    }

    public boolean isIs_hot() {
        return mIsHot;
    }

    public void setIs_hot(boolean isHot) {
        mIsHot = isHot;
    }

    public boolean isIs_common() {
        return mIsCommon;
    }

    public void setIs_common(boolean isCommon) {
        mIsCommon = isCommon;
    }

    public int getOrder_number() {
        return mOrderName;
    }

    public void setOrder_number(int orderNumber) {
        mOrderName = orderNumber;
    }

    public String getCategory() {
        return mCategory;
    }

    public void setCategory(String category) {
        this.mCategory = category;
    }

    @Override
    public String toString() {
        return "Emotion [phrase=" + mPhrase + ", type=" + mType + ", url="
                + mUrl + ", is_hot=" + mIsHot + ", is_common=" + mIsCommon
                + ", order_number=" + mOrderName + ", category="
                + mCategory + "]";
    }

}

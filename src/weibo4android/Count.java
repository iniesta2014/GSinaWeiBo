/**
 * 
 */

package weibo4android;

import java.util.ArrayList;
import java.util.List;

import weibo4android.logic.http.Response;
import weibo4android.org.json.JSONArray;
import weibo4android.org.json.JSONException;
import weibo4android.org.json.JSONObject;

public class Count implements java.io.Serializable {

    private static final long serialVersionUID = 9076424494907778181L;

    private long mId;

    private long mCcomments;

    private long mRt;

    private long mDm;

    private long mMentions;

    private long mFollowers;

    public Count(JSONObject json) throws WeiboException, JSONException {
        mId = json.getLong("id");
        mCcomments = json.getLong("comments");
        mRt = json.getLong("rt");
        mDm = json.getLong("dm");
        mMentions = json.getLong("mentions");
        mFollowers = json.getLong("followers");
    }

    Count(Response res) throws WeiboException {
        JSONObject json = res.asJSONObject();
        try {
            mId = json.getLong("id");
            mCcomments = json.getLong("comments");
            mRt = json.getLong("rt");
            mDm = json.getLong("dm");
            mMentions = json.getLong("mentions");
            mFollowers = json.getLong("followers");

        } catch (JSONException je) {
            throw new WeiboException(je.getMessage() + ":" + json.toString(),
                    je);
        }

    }

    static List<Count> constructCounts(Response res) throws WeiboException {
        try {
            System.out.println(res.asString());
            JSONArray list = res.asJSONArray();
            int size = list.length();
            List<Count> counts = new ArrayList<Count>(size);
            for (int i = 0; i < size; i++) {
                counts.add(new Count(list.getJSONObject(i)));
            }
            return counts;
        } catch (JSONException jsone) {
            throw new WeiboException(jsone);
        } catch (WeiboException te) {
            throw te;
        }
    }

    public long getId() {
        return mId;
    }

    public long getComments() {
        return mCcomments;
    }

    public long getRt() {
        return mRt;
    }

    public long getDm() {
        return mDm;
    }

    public long getMentions() {
        return mMentions;
    }

    public long getFollowers() {
        return mFollowers;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (mId ^ (mId >>> 32));
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Count other = (Count) obj;
        if (mId != other.mId)
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "Count{ id=" + mId +
                ", comments=" + mCcomments +
                ", rt=" + mRt +
                ", dm=" + mDm +
                ", mentions=" + mMentions +
                ", followers=" + mFollowers +
                '}';
    }
}


package weibo4android;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import weibo4android.logic.http.Response;
import weibo4android.org.json.JSONArray;
import weibo4android.org.json.JSONException;
import weibo4android.org.json.JSONObject;

public class SearchResult extends WeiboResponse implements java.io.Serializable {

    private static final long serialVersionUID = 8227371192527300467L;

    private Date mCreatedAt;
    private long mUserID;
    private String mToUser;
    private String mText;
    private String mSource;
    private long mId;
    private long mFromUserID;
    private String mFromUser;
    private String mIsLanagerCode;
    private String mProfileImageUrl;

    public SearchResult(JSONObject json) throws WeiboException, JSONException {
        mCreatedAt = parseDate(json.getString("created_at"), "EEE MMM dd HH:mm:ss z yyyy");
        mUserID = json.getLong("to_user_id");
        mToUser = json.getString("to_user");
        mText = json.getString("text");
        mSource = json.getString("source");
        mId = json.getLong("id");
        mFromUserID = json.getLong("from_user_id");
        mFromUser = json.getString("from_user");
        mIsLanagerCode = json.getString("iso_language_code");
        mProfileImageUrl = json.getString("profile_image_url");
    }

    public static List<SearchResult> constructResults(Response res) throws WeiboException {

        JSONObject json = res.asJSONObject();
        try {
            JSONArray list = json.getJSONArray("results");
            int size = list.length();
            List<SearchResult> rt = new ArrayList<SearchResult>(size);
            for (int i = 0; i < size; i++) {
                rt.add(new SearchResult(list.getJSONObject(i)));
            }
            return rt;

        } catch (JSONException je) {
            throw new WeiboException(je);
        }

    }

    public Date getCreatedAt() {
        return this.mCreatedAt;
    }

    public long getToUserId() {
        return this.mUserID;
    }

    public long getId() {
        return this.mId;
    }

    public long getFromUserId() {
        return this.mFromUserID;
    }

    public String getText() {
        return this.mText;
    }

    public String getSource() {
        return this.mSource;
    }

    public String getFromUser() {
        return this.mFromUser;
    }

    public String getToUser() {
        return this.mToUser;
    }

    public String getName() {
        return this.mIsLanagerCode;
    }

    public URL getProfileImageURL() {
        try {
            return new URL(mProfileImageUrl);
        } catch (MalformedURLException ex) {
            return null;
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((mFromUser == null) ? 0 : mFromUser.hashCode());
        result = prime * result + (int) (mFromUserID ^ (mFromUserID >>> 32));
        result = prime * result + (int) (mId ^ (mId >>> 32));
        result = prime * result + ((mToUser == null) ? 0 : mToUser.hashCode());
        result = prime * result + (int) (mUserID ^ (mUserID >>> 32));
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
        SearchResult other = (SearchResult) obj;
        if (mFromUser == null) {
            if (other.mFromUser != null)
                return false;
        } else if (!mFromUser.equals(other.mFromUser))
            return false;
        if (mFromUserID != other.mFromUserID)
            return false;
        if (mId != other.mId)
            return false;
        if (mToUser == null) {
            if (other.mToUser != null)
                return false;
        } else if (!mToUser.equals(other.mToUser))
            return false;
        if (mUserID != other.mUserID)
            return false;
        return true;
    }

    public String toString() {

        return "Result{ " + mUserID +
                "," + mToUser +
                "," + mText +
                "," + mId +
                "," + mFromUserID +
                "," + mFromUser +
                "," + mIsLanagerCode +
                "," + mSource +
                "," + mProfileImageUrl +
                "," + mCreatedAt +
                '}';

    }
}

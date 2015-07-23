
package weibo4android;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import weibo4android.logic.http.Response;
import weibo4android.org.json.JSONArray;
import weibo4android.org.json.JSONException;
import weibo4android.org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Comment extends WeiboResponse implements java.io.Serializable {

    private Date mCreatedAt; // 评论时间
    private long mId; // 评论id
    private String mText; // 评论内容
    private String mSource; // 内容来源
    private boolean mIsTruncated;
    private long mInReplyToStatusId;
    private long mInReplyToUserId;
    private boolean mIsFavorited; // 保留字段，未弃用
    private String mInReplyToScreenName;
    private double mLatitude = -1; // 纬度
    private double mLongitude = -1; // 经度
    private Comment mReplycomment = null; // 回复的评论内容
    private static final long serialVersionUID = 1608000492860584608L;

    /* package */Comment(Response res, Weibo weibo) throws WeiboException {
        super(res);
        Element elem = res.asDocument().getDocumentElement();
        init(res, elem, weibo);
    }

    /* modify by sycheng add json define */
    /* package */Comment(Response res) throws WeiboException {
        super(res);
        JSONObject json = res.asJSONObject();
        try {
            mId = json.getLong("id");
            mText = json.getString("text");
            mSource = json.getString("source");
            mCreatedAt = parseDate(json.getString("created_at"), "EEE MMM dd HH:mm:ss z yyyy");
            if (!json.isNull("user"))
                user = new User(json.getJSONObject("user"));
            if (!json.isNull("status"))
                status = new Status(json.getJSONObject("status"));
            if (!json.isNull("reply_comment"))
                mReplycomment = (new Comment(json.getJSONObject("reply_comment")));
        } catch (JSONException je) {
            throw new WeiboException(je.getMessage() + ":" + json.toString(), je);
        }
    }

    /* modify by hezhou add some field */
    public Comment(JSONObject json) throws WeiboException, JSONException {
        mId = json.getLong("id");
        mText = json.getString("text");
        mSource = json.getString("source");
        mCreatedAt = parseDate(json.getString("created_at"), "EEE MMM dd HH:mm:ss z yyyy");
        if (!json.isNull("user"))
            user = new User(json.getJSONObject("user"));
        if (!json.isNull("status"))
            status = new Status(json.getJSONObject("status"));
    }

    /* package */Comment(Response res, Element elem, Weibo weibo) throws
            WeiboException {
        super(res);
        init(res, elem, weibo);
    }

    public Comment(String str) throws WeiboException, JSONException {
        // StatusStream uses this constructor
        super();
        JSONObject json = new JSONObject(str);
        mId = json.getLong("id");
        mText = json.getString("text");
        mSource = json.getString("source");
        mCreatedAt = parseDate(json.getString("created_at"), "EEE MMM dd HH:mm:ss z yyyy");
        status = new Status(json.getJSONObject("status"));
        user = new User(json.getJSONObject("user"));

    }

    private void init(Response res, Element elem, Weibo weibo) throws
            WeiboException {
        ensureRootNodeNameIs("comment", elem);
        user = new User(res, (Element) elem.getElementsByTagName("user").item(0)
                , weibo);
        status = new Status(res, (Element) elem.getElementsByTagName("status").item(0)
                , weibo);
        mId = getChildLong("id", elem);
        mText = getChildText("text", elem);
        mSource = getChildText("source", elem);
        mCreatedAt = getChildDate("created_at", elem);
    }

    /**
     * Return the created_at
     * 
     * @return created_at
     * @since Weibo4J 1.2.1
     */

    public Date getCreatedAt() {
        return this.mCreatedAt;
    }

    /**
     * Returns the id of the status
     * 
     * @return the id
     */
    public long getId() {
        return this.mId;
    }

    /**
     * Returns the text of the status
     * 
     * @return the text
     */
    public String getText() {
        return this.mText;
    }

    /**
     * Returns the source
     * 
     * @return the source
     * @since Weibo4J 1.2.1
     */
    public String getSource() {
        return this.mSource;
    }

    /**
     * Test if the status is truncated
     * 
     * @return true if truncated
     * @since Weibo4J 1.2.1
     */
    public boolean isTruncated() {
        return mIsTruncated;
    }

    /**
     * Returns the in_reply_tostatus_id
     * 
     * @return the in_reply_tostatus_id
     * @since Weibo4J 1.2.1
     */
    public long getInReplyToStatusId() {
        return mInReplyToStatusId;
    }

    /**
     * Returns the in_reply_user_id
     * 
     * @return the in_reply_tostatus_id
     * @since Weibo4J 1.2.1
     */
    public long getInReplyToUserId() {
        return mInReplyToUserId;
    }

    /**
     * Returns the in_reply_to_screen_name
     * 
     * @return the in_in_reply_to_screen_name
     * @since Weibo4J 1.2.1
     */
    public String getInReplyToScreenName() {
        return mInReplyToScreenName;
    }

    /**
     * returns The location's latitude that this tweet refers to.
     * 
     * @since Weibo4J 1.2.1
     */
    public double getLatitude() {
        return mLatitude;
    }

    /**
     * returns The location's longitude that this tweet refers to.
     * 
     * @since Weibo4J 1.2.1
     */
    public double getLongitude() {
        return mLongitude;
    }

    /**
     * Test if the status is favorited
     * 
     * @return true if favorited
     * @since Weibo4J 1.2.1
     */
    public boolean isFavorited() {
        return mIsFavorited;
    }

    private User user = null;

    /**
     * Return the user
     * 
     * @return the user
     */
    public User getUser() {
        return user;
    }

    private Status status = null;

    public Status getStatus() {
        return status;
    }

    public Comment getReplyComment() {
        return mReplycomment;
    }

    /* package */
    static List<Comment> constructComments(Response res, Weibo weibo) throws WeiboException {
        Document doc = res.asDocument();
        if (isRootNodeNilClasses(doc)) {
            return new ArrayList<Comment>(0);
        } else {
            try {
                ensureRootNodeNameIs("comments", doc);
                NodeList list = doc.getDocumentElement().getElementsByTagName(
                        "comment");
                int size = list.getLength();
                List<Comment> statuses = new ArrayList<Comment>(size);
                for (int i = 0; i < size; i++) {
                    Element status = (Element) list.item(i);
                    statuses.add(new Comment(res, status, weibo));
                }
                return statuses;
            } catch (WeiboException te) {
                ensureRootNodeNameIs("nil-classes", doc);
                return new ArrayList<Comment>(0);
            }
        }
    }

    static List<Comment> constructComments(Response res) throws WeiboException {
        try {
            JSONArray list = res.asJSONArray();
            int size = list.length();
            List<Comment> comments = new ArrayList<Comment>(size);
            for (int i = 0; i < size; i++) {
                comments.add(new Comment(list.getJSONObject(i)));
            }
            return comments;
        } catch (JSONException jsone) {
            throw new WeiboException(jsone);
        } catch (WeiboException te) {
            throw te;
        }

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
        Comment other = (Comment) obj;
        if (mId != other.mId)
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "Comment{" +
                "createdAt=" + mCreatedAt +
                ", id=" + mId +
                ", text='" + mText + '\'' +
                ", source='" + mSource + '\'' +
                ", isTruncated=" + mIsTruncated +
                ", inReplyToStatusId=" + mInReplyToStatusId +
                ", inReplyToUserId=" + mInReplyToUserId +
                ", isFavorited=" + mIsFavorited +
                ", inReplyToScreenName='" + mInReplyToScreenName + '\'' +
                ", latitude=" + mLatitude +
                ", longitude=" + mLongitude +
                ", user=" + user +
                ", status=" + status +
                '}';
    }
}

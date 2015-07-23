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
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import weibo4android.logic.http.Response;
import weibo4android.org.json.JSONArray;
import weibo4android.org.json.JSONException;
import weibo4android.org.json.JSONObject;

public class Status extends WeiboResponse implements java.io.Serializable {

    private static final long serialVersionUID = -8795691786466526420L;

    private User mUser = null;

    private Date mCreatedAt; // status创建时间
    private long mId; // status id
    private String mText; // 微博内容
    private String mSource; // 微博来源
    private boolean mIsTruncated; // 保留字段
    private long mInReplyToStatusId;
    private long mInReplyToUserId;
    private boolean mIsFavorited; // 保留字段，未弃用
    private String mInReplyToScreenName;
    private double mLatitude = -1; // 纬度
    private double mLongitude = -1; // 经度
    private String mThumbnail_pic; // 微博内容中的图片的缩略地址
    private String mBmiddle_pic; // 中型图片
    private String mOriginal_pic; // 原始图片
    private Status retweeted_status; // 转发的微博内容
    private String mid; // mid
    private int mReposts_count;// 转发数
    private int mComments_count;// 评论数

    /* package */Status(Response res, Weibo weibo) throws WeiboException {
        super(res);
        Element elem = res.asDocument().getDocumentElement();
        init(res, elem, weibo);
    }

    /* package */Status(Response res, Element elem, Weibo weibo) throws
            WeiboException {
        super(res);
        init(res, elem, weibo);
    }

    Status(Response res) throws WeiboException {
        super(res);
        JSONObject json = res.asJSONObject();
        constructJson(json);
    }

    /* modify by Reilost add some field */
    private void constructJson(JSONObject json) throws WeiboException {
        try {
            mId = json.getLong("id");
            mText = json.getString("text");
            mSource = json.getString("source");
            mCreatedAt = parseDate(json.getString("created_at"), "EEE MMM dd HH:mm:ss z yyyy");
            mInReplyToStatusId = getLong("in_reply_to_status_id", json);
            mInReplyToUserId = getLong("in_reply_to_user_id", json);
            mIsFavorited = getBoolean("favorited", json);
            mThumbnail_pic = json.getString("thumbnail_pic");
            mBmiddle_pic = json.getString("bmiddle_pic");
            mOriginal_pic = json.getString("original_pic");
            mComments_count = json.getInt("comments_count");
            mReposts_count = json.getInt("reposts_count");
            if (!json.isNull("user"))
                mUser = new User(json.getJSONObject("user"));
            mInReplyToScreenName = json.getString("inReplyToScreenName");
            if (!json.isNull("retweeted_status")) {
                retweeted_status = new Status(json.getJSONObject("retweeted_status"));
            }

            mid = json.getString("mid");
            String geo = json.getString("geo");
            if (geo != null && !"".equals(geo) && !"null".equals(geo)) {
                getGeoInfo(geo);
            }
        } catch (JSONException je) {
            throw new WeiboException(je.getMessage() + ":" + json.toString(), je);
        }
    }

    private void getGeoInfo(String geo) {
        StringBuffer value = new StringBuffer();
        for (char c : geo.toCharArray()) {
            if (c > 45 && c < 58) {
                value.append(c);
            }
            if (c == 44) {
                if (value.length() > 0) {
                    mLatitude = Double.parseDouble(value.toString());
                    value.delete(0, value.length());
                }
            }
        }
        mLongitude = Double.parseDouble(value.toString());
    }

    public Status(JSONObject json) throws WeiboException, JSONException {
        constructJson(json);
    }

    public Status(String str) throws WeiboException, JSONException {
        // StatusStream uses this constructor
        super();
        JSONObject json = new JSONObject(str);
        constructJson(json);
    }

    private void init(Response res, Element elem, Weibo weibo) throws
            WeiboException {
        ensureRootNodeNameIs("status", elem);
        mUser = new User(res, (Element) elem.getElementsByTagName("user").item(0)
                , weibo);
        mId = getChildLong("id", elem);
        mText = getChildText("text", elem);
        mSource = getChildText("source", elem);
        mCreatedAt = getChildDate("created_at", elem);
        mIsTruncated = getChildBoolean("truncated", elem);
        mInReplyToStatusId = getChildLong("in_reply_to_status_id", elem);
        mInReplyToUserId = getChildLong("in_reply_to_user_id", elem);
        mIsFavorited = getChildBoolean("favorited", elem);
        mReposts_count = getChildInt("reposts_count", elem);
        mComments_count = getChildInt("comments_count", elem);
        mInReplyToScreenName = getChildText("in_reply_to_screen_name", elem);
        NodeList georssPoint = elem.getElementsByTagName("georss:point");
        if (1 == georssPoint.getLength()) {
            String[] point = georssPoint.item(0).getFirstChild().getNodeValue().split(" ");
            if (!"null".equals(point[0]))
                mLatitude = Double.parseDouble(point[0]);
            if (!"null".equals(point[1]))
                mLongitude = Double.parseDouble(point[1]);
        }
        NodeList retweetDetailsNode = elem.getElementsByTagName("retweet_details");
        if (1 == retweetDetailsNode.getLength()) {
            retweeted_status = new Status(res, (Element) retweetDetailsNode.item(0), weibo);
        }
    }

    /**
     * Return the created_at
     * 
     * @return created_at
     * @since Weibo4J 1.1.0
     */

    public Date getCreatedAt() {
        return this.mCreatedAt;
    }

    public int getReposts_count() {
        return mReposts_count;
    }

    public int getComments_count() {
        return mComments_count;
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

    public String getThumbnail_pic() {
        return mThumbnail_pic;
    }

    public String getBmiddle_pic() {
        return mBmiddle_pic;
    }

    public String getOriginal_pic() {
        return mOriginal_pic;
    }

    /**
     * Return the user
     * 
     * @return the user
     */
    public User getUser() {
        return mUser;
    }

    /**
     * @since Weibo4J 1.2.1
     */
    public boolean isRetweet() {
        return null != retweeted_status;
    }

    public Status getRetweeted_status() {
        return retweeted_status;
    }

    public String getMid() {
        return mid;
    }

    /* package */
    static List<Status> constructStatuses(Response res,
            Weibo weibo) throws WeiboException {

        Document doc = res.asDocument();
        if (isRootNodeNilClasses(doc)) {
            return new ArrayList<Status>(0);
        } else {
            try {
                ensureRootNodeNameIs("statuses", doc);
                NodeList list = doc.getDocumentElement().getElementsByTagName(
                        "status");
                int size = list.getLength();
                List<Status> statuses = new ArrayList<Status>(size);
                for (int i = 0; i < size; i++) {
                    Element status = (Element) list.item(i);
                    statuses.add(new Status(res, status, weibo));
                }
                return statuses;
            } catch (WeiboException te) {
                ensureRootNodeNameIs("nil-classes", doc);
                return new ArrayList<Status>(0);
            }
        }

    }

    /* modify by sycheng add json call method */
    /* package */
    static List<Status> constructStatuses(Response res) throws WeiboException {
        try {
            JSONArray list = res.asJSONArray();
            int size = list.length();
            List<Status> statuses = new ArrayList<Status>(size);

            for (int i = 0; i < size; i++) {
                statuses.add(new Status(list.getJSONObject(i)));
            }
            return statuses;
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
        Status other = (Status) obj;
        if (mId != other.mId)
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "Status [createdAt=" + mCreatedAt + ", id=" + mId + ", text="
                + mText + ", source=" + mSource + ", isTruncated=" + mIsTruncated
                + ", inReplyToStatusId=" + mInReplyToStatusId
                + ", inReplyToUserId=" + mInReplyToUserId + ", isFavorited="
                + mIsFavorited + ", inReplyToScreenName=" + mInReplyToScreenName
                + ", latitude=" + mLatitude + ", longitude=" + mLongitude
                + ", thumbnail_pic=" + mThumbnail_pic + ", bmiddle_pic="
                + mBmiddle_pic + ", original_pic=" + mOriginal_pic
                + ",  mid=" + mid + ", user=" + mUser
                + ", retweeted_status="
                + (retweeted_status == null ? "null" : retweeted_status.toString()) +
                "]";
    }

}

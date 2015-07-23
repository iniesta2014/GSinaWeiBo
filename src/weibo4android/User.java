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

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import weibo4android.logic.http.Response;
import weibo4android.org.json.JSONArray;
import weibo4android.org.json.JSONException;
import weibo4android.org.json.JSONObject;

/**
 * A data class representing Basic user information element
 */
public class User extends WeiboResponse implements java.io.Serializable {

    private static final long serialVersionUID = 3473349966713163765L;
    static final String[] POSSIBLE_ROOT_NAMES = new String[] {
            "user", "sender", "recipient", "retweeting_user"
    };
    private Weibo mWeibo;
    private long mId; // 用户id
    private String mScreenName; // 微博昵称
    private String mName; // 友好显示名称，如Bill Gates(此特性暂不支持)
    private int mProvince; // 省份编码（参考省份编码表）
    private int mCity; // 城市编码（参考城市编码表）
    private String mLocation; // 地址
    private String mDescription; // 个人描述
    private String mUrl; // 用户博客地址
    private String mProfileImageUrl; // 自定义图像
    private String mUserDomain; // 用户个性化URL
    private String mGender; // 性别,m--男，f--女,n--未知
    private int mFollowersCount; // 粉丝数
    private int mFriendsCount; // 关注数
    private int mStatusesCount; // 微博数
    private int mFavouritesCount; // 收藏数
    private Date mCreatedAt; // 创建时间
    private boolean mFollowing; // 保留字段,是否已关注(此特性暂不支持)
    private boolean mVerified; // 加V标示，是否微博认证用户
    private boolean mGeoEnabled; // 地理
    private boolean mAllowAllActMsg; // 保留字段（暂时不支持）

    private Status status = null;

    /* package */User(Response res, Weibo weibo) throws WeiboException {
        super(res);
        Element elem = res.asDocument().getDocumentElement();
        init(res, elem, weibo);
    }

    /* package */User(Response res, Element elem, Weibo weibo) throws WeiboException {
        super(res);
        init(res, elem, weibo);
    }

    /* package */User(JSONObject json) throws WeiboException {
        super();
        init(json);
    }

    private void init(JSONObject json) throws WeiboException {
        if (json != null) {
            try {
                mId = json.getLong("id");
                mName = json.getString("name");
                mScreenName = json.getString("screen_name");
                mLocation = json.getString("location");
                mDescription = json.getString("description");
                mProfileImageUrl = json.getString("profile_image_url");
                mUrl = json.getString("url");
                mAllowAllActMsg = json.getBoolean("allow_all_act_msg");
                mFollowersCount = json.getInt("followers_count");
                mFriendsCount = json.getInt("friends_count");
                mCreatedAt = parseDate(json.getString("created_at"), "EEE MMM dd HH:mm:ss z yyyy");
                mFavouritesCount = json.getInt("favourites_count");
                mFollowing = getBoolean("following", json);
                mVerified = getBoolean("verified", json);
                mStatusesCount = json.getInt("statuses_count");
                mUserDomain = json.getString("domain");
                mGender = json.getString("gender");
                mProvince = json.getInt("province");
                mCity = json.getInt("city");
                if (!json.isNull("status")) {
                    setStatus(new Status(json.getJSONObject("status")));
                }
            } catch (JSONException jsone) {
                throw new WeiboException(jsone.getMessage() + ":" + json.toString(), jsone);
            }
        }
    }

    private void init(Response res, Element elem, Weibo weibo) throws WeiboException {
        this.mWeibo = weibo;
        ensureRootNodeNameIs(POSSIBLE_ROOT_NAMES, elem);
        mId = getChildLong("id", elem);
        mName = getChildText("name", elem);
        mScreenName = getChildText("screen_name", elem);
        mLocation = getChildText("location", elem);
        mDescription = getChildText("description", elem);
        mProfileImageUrl = getChildText("profile_image_url", elem);
        mUrl = getChildText("url", elem);
        mAllowAllActMsg = getChildBoolean("allow_all_act_msg", elem);
        mFollowersCount = getChildInt("followers_count", elem);
        mFriendsCount = getChildInt("friends_count", elem);
        mCreatedAt = getChildDate("created_at", elem);
        mFavouritesCount = getChildInt("favourites_count", elem);
        mFollowing = getChildBoolean("following", elem);
        mStatusesCount = getChildInt("statuses_count", elem);
        mGeoEnabled = getChildBoolean("geo_enabled", elem);
        mVerified = getChildBoolean("verified", elem);
        mUserDomain = getChildText("domain", elem);
        mGender = getChildText("gender", elem);
        mProvince = getChildInt("province", elem);
        mCity = getChildInt("city", elem);
        status = new Status(res, (Element) elem.getElementsByTagName("status").item(0)
                , weibo);
    }

    /**
     * Returns the id of the user
     * 
     * @return the id of the user
     */
    public long getId() {
        return mId;
    }

    /**
     * Returns the name of the user
     * 
     * @return the name of the user
     */
    public String getName() {
        return mName;
    }

    /**
     * Returns the screen name of the user
     * 
     * @return the screen name of the user
     */
    public String getScreenName() {
        return mScreenName;
    }

    /**
     * Returns the location of the user
     * 
     * @return the location of the user
     */
    public String getLocation() {
        return mLocation;
    }

    /**
     * Returns the description of the user
     * 
     * @return the description of the user
     */
    public String getDescription() {
        return mDescription;
    }

    /**
     * Returns the profile image url of the user
     * 
     * @return the profile image url of the user
     */
    public URL getProfileImageURL() {
        try {
            return new URL(mProfileImageUrl);
        } catch (MalformedURLException ex) {
            return null;
        }
    }

    /**
     * Returns the url of the user
     * 
     * @return the url of the user
     */
    public URL getURL() {
        try {
            return new URL(mUrl);
        } catch (MalformedURLException ex) {
            return null;
        }
    }

    /**
     * Test if the user status is protected
     * 
     * @return true if the user status is protected
     */
    public boolean isAllowAllActMsg() {
        return mAllowAllActMsg;
    }

    public String getUserDomain() {
        return mUserDomain;
    }

    /**
     * Returns the number of followers
     * 
     * @return the number of followers
     * @since Weibo4J 1.2.1
     */
    public int getFollowersCount() {
        return mFollowersCount;
    }

    /**
     * Returns the code of province
     * 
     * @return the code of province
     * @since Weibo4J 1.2.1
     */
    public int getProvince() {
        return mProvince;
    }

    /**
     * Returns the code of city
     * 
     * @return the code of city
     * @since Weibo4J 1.2.1
     */
    public int getCity() {
        return mCity;
    }

    public static List<User> constructUser(Response res) throws WeiboException {

        JSONObject json = res.asJSONObject();
        try {
            // int next_cursor = json.getInt("next_cursor");
            // int previous_cursor = json.getInt("previous_cursor");

            JSONArray list = json.getJSONArray("users");
            int size = list.length();
            List<User> users = new ArrayList<User>(size);
            for (int i = 0; i < size; i++) {
                users.add(new User(list.getJSONObject(i)));
            }
            return users;

        } catch (JSONException je) {
            throw new WeiboException(je);
        }

    }

    public static List<User> constructUsers(Response res, Weibo weibo) throws WeiboException {
        Document doc = res.asDocument();
        if (isRootNodeNilClasses(doc)) {
            return new ArrayList<User>(0);
        } else {
            try {
                ensureRootNodeNameIs("users", doc);
                // NodeList list =
                // doc.getDocumentElement().getElementsByTagName(
                // "user");
                // int size = list.getLength();
                // List<User> users = new ArrayList<User>(size);
                // for (int i = 0; i < size; i++) {
                // users.add(new User(res, (Element) list.item(i), weibo));
                // }

                // 去除掉嵌套的bug
                NodeList list = doc.getDocumentElement().getChildNodes();
                List<User> users = new ArrayList<User>(list.getLength());
                Node node;
                for (int i = 0; i < list.getLength(); i++) {
                    node = list.item(i);
                    if (node.getNodeName().equals("user")) {
                        users.add(new User(res, (Element) node, weibo));
                    }
                }

                return users;
            } catch (WeiboException te) {
                if (isRootNodeNilClasses(doc)) {
                    return new ArrayList<User>(0);
                } else {
                    throw te;
                }
            }
        }
    }

    public static UserWapper constructWapperUsers(Response res, Weibo weibo) throws WeiboException {
        Document doc = res.asDocument();
        if (isRootNodeNilClasses(doc)) {
            return new UserWapper(new ArrayList<User>(0), 0, 0);
        } else {
            try {
                ensureRootNodeNameIs("users_list", doc);
                Element root = doc.getDocumentElement();
                NodeList user = root.getElementsByTagName("users");
                int length = user.getLength();
                if (length == 0) {
                    return new UserWapper(new ArrayList<User>(0), 0, 0);
                }
                //
                Element listsRoot = (Element) user.item(0);
                NodeList list = listsRoot.getChildNodes();
                List<User> users = new ArrayList<User>(list.getLength());
                Node node;
                for (int i = 0; i < list.getLength(); i++) {
                    node = list.item(i);
                    if (node.getNodeName().equals("user")) {
                        users.add(new User(res, (Element) node, weibo));
                    }
                }
                //
                long previousCursor = getChildLong("previous_curosr", root);
                long nextCursor = getChildLong("next_curosr", root);
                if (nextCursor == -1) { // 兼容不同标签名称
                    nextCursor = getChildLong("nextCurosr", root);
                }
                return new UserWapper(users, previousCursor, nextCursor);
            } catch (WeiboException te) {
                if (isRootNodeNilClasses(doc)) {
                    return new UserWapper(new ArrayList<User>(0), 0, 0);
                } else {
                    throw te;
                }
            }
        }
    }

    public static List<User> constructUsers(Response res) throws WeiboException {
        try {
            JSONArray list = res.asJSONArray();
            int size = list.length();
            List<User> users = new ArrayList<User>(size);
            for (int i = 0; i < size; i++) {
                users.add(new User(list.getJSONObject(i)));
            }
            return users;
        } catch (JSONException jsone) {
            throw new WeiboException(jsone);
        } catch (WeiboException te) {
            throw te;
        }
    }

    /**
     * @param res
     * @return
     * @throws WeiboException
     */
    public static UserWapper constructWapperUsers(Response res) throws WeiboException {
        JSONObject jsonUsers = res.asJSONObject(); // asJSONArray();
        try {
            JSONArray user = jsonUsers.getJSONArray("users");
            int size = user.length();
            List<User> users = new ArrayList<User>(size);
            for (int i = 0; i < size; i++) {
                users.add(new User(user.getJSONObject(i)));
            }
            long previousCursor = jsonUsers.getLong("previous_curosr");
            long nextCursor = jsonUsers.getLong("next_cursor");
            if (nextCursor == -1) { // 兼容不同标签名称
                nextCursor = jsonUsers.getLong("nextCursor");
            }
            return new UserWapper(users, previousCursor, nextCursor);
        } catch (JSONException jsone) {
            throw new WeiboException(jsone);
        }
    }

    /**
     * @param res
     * @return
     * @throws WeiboException
     */
    static List<User> constructResult(Response res) throws WeiboException {
        JSONArray list = res.asJSONArray();
        try {
            int size = list.length();
            List<User> users = new ArrayList<User>(size);
            for (int i = 0; i < size; i++) {
                users.add(new User(list.getJSONObject(i)));
            }
            return users;
        } catch (JSONException e) {
        }
        return null;
    }

    public int getFriendsCount() {
        return mFriendsCount;
    }

    public Date getCreatedAt() {
        return mCreatedAt;
    }

    public int getFavouritesCount() {
        return mFavouritesCount;
    }

    public String getGender() {
        return mGender;
    }

    /**
     * @deprecated
     */
    public boolean isFollowing() {
        return mFollowing;
    }

    public int getStatusesCount() {
        return mStatusesCount;
    }

    /**
     * @return the user is enabling geo location
     * @since Weibo4J 1.2.1
     */
    public boolean isGeoEnabled() {
        return mGeoEnabled;
    }

    /**
     * @return returns true if the user is a verified celebrity
     * @since Weibo4J 1.2.1
     */
    public boolean isVerified() {
        return mVerified;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Status getStatus() {
        return status;
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
        User other = (User) obj;
        if (mId != other.mId)
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "User{" +
                "weibo=" + mWeibo +
                ", id=" + mId +
                ", name='" + mName + '\'' +
                ", screenName='" + mScreenName + '\'' +
                ", location='" + mLocation + '\'' +
                ", description='" + mDescription + '\'' +
                ", profileImageUrl='" + mProfileImageUrl + '\'' +
                ", province='" + mProvince + '\'' +
                ", city='" + mCity + '\'' +
                ", domain ='" + mUserDomain + '\'' +
                ", gender ='" + mGender + '\'' +
                ", url='" + mUrl + '\'' +
                ", allowAllActMsg=" + mAllowAllActMsg +
                ", followersCount=" + mFollowersCount +
                ", friendsCount=" + mFriendsCount +
                ", createdAt=" + mCreatedAt +
                ", favouritesCount=" + mFavouritesCount +
                ", following=" + mFollowing +
                ", statusesCount=" + mStatusesCount +
                ", geoEnabled=" + mGeoEnabled +
                ", verified=" + mVerified +
                ", status=" + status +
                '}';
    }

}

/*
 * UserObjectWapper.java created on 2010-7-28 上午08:48:35 by bwl (Liu Daoru)
 */

package weibo4android;

import java.io.Serializable;
import java.util.List;

public class UserWapper implements Serializable {

    private static final long serialVersionUID = -3119107701303920284L;

    /**
     * 用户对象列表
     */
    private List<User> mUsers;

    /**
     * 向前翻页的cursor
     */
    private long mPreviousCursor;

    /**
     * 向后翻页的cursor
     */
    private long mNextCursor;

    public UserWapper(List<User> users, long previousCursor, long nextCursor) {
        this.mUsers = users;
        this.mPreviousCursor = previousCursor;
        this.mNextCursor = nextCursor;
    }

    public List<User> getUsers() {
        return mUsers;
    }

    public void setUsers(List<User> users) {
        this.mUsers = users;
    }

    public long getPreviousCursor() {
        return mPreviousCursor;
    }

    public void setPreviousCursor(long previousCursor) {
        this.mPreviousCursor = previousCursor;
    }

    public long getNextCursor() {
        return mNextCursor;
    }

    public void setNextCursor(long nextCursor) {
        this.mNextCursor = nextCursor;
    }

}

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

import weibo4android.logic.http.Response;
import weibo4android.org.json.JSONArray;
import weibo4android.org.json.JSONException;
import weibo4android.org.json.JSONObject;

public class SavedSearch extends WeiboResponse {
    private Date mCreatedAt;
    private String mQuery;
    private int mPosition;
    private String mName;
    private int mId;
    private static final long serialVersionUID = 3083819860391598212L;

    /* package */SavedSearch(Response res) throws WeiboException {
        super(res);
        init(res.asJSONObject());
    }

    /* package */SavedSearch(Response res, JSONObject json) throws WeiboException {
        super(res);
        init(json);
    }

    /* package */SavedSearch(JSONObject savedSearch) throws WeiboException {
        init(savedSearch);
    }

    /* package */static List<SavedSearch> constructSavedSearches(Response res)
            throws WeiboException {
        JSONArray json = res.asJSONArray();
        List<SavedSearch> savedSearches;
        try {
            savedSearches = new ArrayList<SavedSearch>(json.length());
            for (int i = 0; i < json.length(); i++) {
                savedSearches.add(new SavedSearch(res, json.getJSONObject(i)));
            }
            return savedSearches;
        } catch (JSONException jsone) {
            throw new WeiboException(jsone.getMessage() + ":" + res.asString(), jsone);
        }
    }

    private void init(JSONObject savedSearch) throws WeiboException {
        try {
            mCreatedAt = parseDate(savedSearch.getString("created_at"), "EEE MMM dd HH:mm:ss z yyyy");
            mQuery = getString("query", savedSearch, true);
            mPosition = getInt("position", savedSearch);
            mName = getString("name", savedSearch, true);
            mId = getInt("id", savedSearch);
        } catch (JSONException jsone) {
            throw new WeiboException(jsone.getMessage() + ":" + savedSearch.toString(), jsone);
        }
    }

    public Date getCreatedAt() {
        return mCreatedAt;
    }

    public String getQuery() {
        return mQuery;
    }

    public int getPosition() {
        return mPosition;
    }

    public String getName() {
        return mName;
    }

    public int getId() {
        return mId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof SavedSearch))
            return false;

        SavedSearch that = (SavedSearch) o;

        if (mId != that.mId)
            return false;
        if (mPosition != that.mPosition)
            return false;
        if (!mCreatedAt.equals(that.mCreatedAt))
            return false;
        if (!mName.equals(that.mName))
            return false;
        if (!mQuery.equals(that.mQuery))
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = mCreatedAt.hashCode();
        result = 31 * result + mQuery.hashCode();
        result = 31 * result + mPosition;
        result = 31 * result + mName.hashCode();
        result = 31 * result + mId;
        return result;
    }

    @Override
    public String toString() {
        return "SavedSearch{" +
                "createdAt=" + mCreatedAt +
                ", query='" + mQuery + '\'' +
                ", position=" + mPosition +
                ", name='" + mName + '\'' +
                ", id=" + mId +
                '}';
    }
}

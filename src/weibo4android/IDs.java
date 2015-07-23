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

import java.util.Arrays;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import weibo4android.logic.http.Response;
import weibo4android.org.json.JSONArray;
import weibo4android.org.json.JSONException;
import weibo4android.org.json.JSONObject;

public class IDs extends WeiboResponse {
    private long[] mIds;
    private long mPreviousCursor;
    private long mNextCursor;
    private static final long serialVersionUID = -6585026560164704953L;
    private static String[] ROOT_NODE_NAMES = {
            "id_list", "ids"
    };

    /* package */IDs(Response res) throws WeiboException {
        super(res);
        Element elem = res.asDocument().getDocumentElement();
        ensureRootNodeNameIs(ROOT_NODE_NAMES, elem);
        NodeList idlist = elem.getElementsByTagName("id");
        mIds = new long[idlist.getLength()];
        for (int i = 0; i < idlist.getLength(); i++) {
            try {
                mIds[i] = Long.parseLong(idlist.item(i).getFirstChild().getNodeValue());
            } catch (NumberFormatException nfe) {
                throw new WeiboException("Weibo API returned malformed response: " + elem, nfe);
            }
        }
        mPreviousCursor = getChildLong("previous_cursor", elem);
        mNextCursor = getChildLong("next_cursor", elem);
    }

    /* package */IDs(Response res, Weibo w) throws WeiboException {
        super(res);
        if ("[]\n".equals(res.asString())) {
            mPreviousCursor = 0;
            mNextCursor = 0;
            mIds = new long[0];
            return;
        }
        JSONObject json = res.asJSONObject();
        try {
            mPreviousCursor = json.getLong("previous_cursor");
            mNextCursor = json.getLong("next_cursor");

            if (!json.isNull("ids")) {
                JSONArray jsona = json.getJSONArray("ids");
                int size = jsona.length();
                mIds = new long[size];
                for (int i = 0; i < size; i++) {
                    mIds[i] = jsona.getLong(i);
                }
            }

        } catch (JSONException jsone) {
            throw new WeiboException(jsone);
        }

    }

    public long[] getIDs() {
        return mIds;
    }

    /**
     * @since Weibo4J 1.2.0
     */
    public boolean hasPrevious() {
        return 0 != mPreviousCursor;
    }

    /**
     * @since Weibo4J 1.2.0
     */
    public long getPreviousCursor() {
        return mPreviousCursor;
    }

    /**
     * @since Weibo4J 1.2.0
     */
    public boolean hasNext() {
        return 0 != mNextCursor;
    }

    /**
     * @since Weibo4J 1.2.0
     */
    public long getNextCursor() {
        return mNextCursor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof IDs))
            return false;

        IDs iDs = (IDs) o;

        if (!Arrays.equals(mIds, iDs.mIds))
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        return mIds != null ? Arrays.hashCode(mIds) : 0;
    }

    @Override
    public String toString() {
        return "IDs{" +
                "ids=" + mIds +
                ", previousCursor=" + mPreviousCursor +
                ", nextCursor=" + mNextCursor +
                '}';
    }
}

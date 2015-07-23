/*
Copyright (c) 2007-2009
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

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import weibo4android.logic.http.PostParameter;

public class Query {
    private String mKey; // 搜索的关键字。
    private Boolean mSnick = null;// 搜索范围是否包含昵称
    private int mRpp = 20;
    private Boolean mSdomain = null;// 搜索范围是否包含个性域名
    private Boolean mSintro = null;// 搜索范围是否包含简介
    private Integer mProvince = null;// 省份ID，参考省份城市编码表
    private Integer mCity = null;// 城市ID，参考省份城市编码表
    private Gender mGender = null;// 性别
    private String mComorsch = null;
    private int mSort = 1;// 排序方式，1为按更新时间，2为按粉丝数。
    private Integer mPage = null;
    private Integer mCount = null;// 默认每页10条
    private boolean mBase_app = true;// 是否不基于当前应用来获取数据
    private int mFilter_ori = 0;// 过滤器，是否为原创，0为全部，5为原创，4为转发。默认为0。
    private int mFilter_pic;// 过滤器。是否包含图片。0为全部，1为包含，2为不包含。
    private long mFuid;// 微博作者的用户ID。
    private Date mStarttime;// 开始时间，Unix时间戳
    private Date mEndtime;// 结束时间，Unix时间戳
    private boolean mNeedcount = false;// 返回结果中是否包含返回记录数。true则返回搜索结果记录数。
    private String mGeocode = null;// 返回指定经纬度附近的信息。经纬度参数格式是“纬度，经度，半径”，半径支持km（公里），m（米），mi（英里）。格式需要URL
                                   // Encode编码

    public void setQ(String q) {
        this.mKey = q;
        // if(!URLEncodeUtils.isURLEncoded(q))
        // q=URLEncodeUtils.encodeURL(q);
    }

    public String getQ() {
        return mKey;
    }

    public Boolean getSnick() {
        return mSnick;
    }

    public void setSnick(Boolean snick) {
        this.mSnick = snick;
    }

    public int getRpp() {
        return mRpp;
    }

    public void setRpp(int rpp) {
        this.mRpp = rpp;
    }

    public Boolean getSdomain() {
        return mSdomain;
    }

    public void setSdomain(Boolean sdomain) {
        this.mSdomain = sdomain;
    }

    public Boolean getSintro() {
        return mSintro;
    }

    public void setSintro(Boolean sintro) {
        this.mSintro = sintro;
    }

    public Integer getProvince() {
        return mProvince;
    }

    public void setProvince(Integer province) {
        this.mProvince = province;
    }

    public Integer getCity() {
        return mCity;
    }

    public void setCity(Integer city) {
        this.mCity = city;
    }

    public Gender getGender() {
        return mGender;
    }

    public void setGender(Gender gender) {
        this.mGender = gender;
    }

    public String getComorsch() {
        return mComorsch;
    }

    public void setComorsch(String comorsch) {
        this.mComorsch = comorsch;
    }

    public int getSort() {
        return mSort;
    }

    public void setSort(int sort) {
        this.mSort = sort;
    }

    public Integer getPage() {
        return mPage;
    }

    public void setPage(Integer page) {
        this.mPage = page;
    }

    public Integer getCount() {
        return mCount;
    }

    public void setCount(Integer count) {
        this.mCount = count;
    }

    public boolean getBase_app() {
        return mBase_app;
    }

    public void setBase_app(boolean baseApp) {
        mBase_app = baseApp;
    }

    public int getFilter_ori() {
        return mFilter_ori;
    }

    public void setFilter_ori(int filterOri) {
        mFilter_ori = filterOri;
    }

    public int getFilter_pic() {
        return mFilter_pic;
    }

    public void setFilter_pic(int filterPic) {
        mFilter_pic = filterPic;
    }

    public long getFuid() {
        return mFuid;
    }

    public void setFuid(Integer fuid) {
        this.mFuid = fuid;
    }

    public Date getStarttime() {
        return mStarttime;
    }

    public void setStarttime(Date starttime) {
        this.mStarttime = starttime;
    }

    public Date getEndtime() {
        return mEndtime;
    }

    public void setEndtime(Date endtime) {
        this.mEndtime = endtime;
    }

    public boolean getNeedcount() {
        return mNeedcount;
    }

    public void setNeedcount(boolean needcount) {
        this.mNeedcount = needcount;
    }

    public String getGeocode() {
        return mGeocode;
    }

    public void setGeocode(String geocode) {
        this.mGeocode = geocode;
    }

    public PostParameter[] getParameters() throws WeiboException {
        List<PostParameter> list = new ArrayList<PostParameter>();
        Class<Query> clz = Query.class;
        Field[] fields = clz.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            String fieldName = field.getName();
            String firstLetter = fieldName.substring(0, 1).toUpperCase();
            String getMethodName = "get" + firstLetter + fieldName.substring(1);
            Method getMethod;
            try {
                getMethod = clz.getMethod(getMethodName, new Class[] {});
                Object value = getMethod.invoke(this, new Object[] {});
                if (value != null) {
                    list.add(getParameterValue(fieldName, value));
                }
            } catch (Exception e) {
                throw new WeiboException(e);
            }
        }
        return list.toArray(new PostParameter[list.size()]);

    }

    private PostParameter getParameterValue(String name, Object value) {
        if (value instanceof Boolean) {
            return new PostParameter(name, (Boolean) value ? "0" : "1");
        } else if (value instanceof String) {
            return new PostParameter(name, value.toString());
        } else if (value instanceof Integer) {
            return new PostParameter(name, Integer.toString((Integer) value));
        } else if (value instanceof Gender) {
            return new PostParameter(name, Gender.valueOf((Gender) value));
        } else if (value instanceof Long) {
            return new PostParameter(name, Long.toString((Long) value));
        }
        return null;
    }

}

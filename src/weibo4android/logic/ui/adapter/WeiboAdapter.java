
package weibo4android.logic.ui.adapter;

import java.util.List;

import weibo4android.Status;
import weibo4android.logic.R;
import weibo4android.logic.ui.imaCache.Anseylodar;
import weibo4android.logic.ui.util.ViewHolder;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class WeiboAdapter extends BaseAdapter {
    public List<Status> mStatus;// 微博信息
    Context mContext;// 上下文
    Anseylodar mAnsylodar;

    public WeiboAdapter(Context context, List<Status> allstatus) {
        mStatus = allstatus;
        mContext = context;
        mAnsylodar = new Anseylodar();
    }

    @Override
    public int getCount() {
        // 由于我们最后一项时更多 所以这里getcount+1
        return mStatus.size();
    }

    @Override
    public Object getItem(int arg0) {
        return mStatus.get(arg0);
    }

    // 请求更多的数据
    public void addmoreDate(List<Status> addmore) {
        if (mStatus != null) {
            this.mStatus.addAll(addmore);// 吧新传得数据加到现在的list中
            this.notifyDataSetChanged();// 将数据追加到ListView中显示
        }
    }

    @Override
    public long getItemId(int index) {
        return mStatus.get(index).getId();
    }

    @SuppressWarnings("static-access")
    @Override
    public View getView(int position, View conterView, ViewGroup arg2) {

        View statusView = null;
        if ((conterView != null) && (conterView.findViewById(R.id.contentPic) != null)) {
            statusView = conterView;
        } else {// 如果缓存中没有就重新创建
                // 加载一个新的View当 Root为null时可以拿到所有XML的资源文件
            statusView = LayoutInflater.from(mContext).inflate(R.layout.itemview, null);
        }
        ViewHolder vHolder = null;
        vHolder = new ViewHolder();
        vHolder.mItemName = (TextView) statusView.findViewById(R.id.tvItemName);
        vHolder.mContent = (TextView) statusView.findViewById(R.id.tvItemContent);
        vHolder.mIvitemPorprait = (ImageView) statusView.findViewById(R.id.ivItemPortrait);
        vHolder.mItemTimeData = (TextView) statusView.findViewById(R.id.tvItemDate);
        vHolder.mSubLayoutView = (LinearLayout) statusView.findViewById(R.id.subLayout);
        vHolder.mItemSubcontent = (TextView) statusView.findViewById(R.id.tvItemSubContent);
        vHolder.mContentPic = (ImageView) statusView.findViewById(R.id.contentPic);
        vHolder.mSubContenPic = (ImageView) statusView.findViewById(R.id.subContentPic);
        Status mstatus = mStatus.get(position);
        if (mstatus.getUser().isVerified()) {
            vHolder.mItemV = (ImageView) statusView.findViewById(R.id.ivItemV);
            vHolder.mItemV.setVisibility(View.VISIBLE);
        }
        // 设定发表微博的用户的昵称
        vHolder.mItemName.setText(mstatus.getUser().getName());
        // 设定内容
        vHolder.mContent.setText(mstatus.getText());
        // 设定表发微博的时间
        vHolder.mItemTimeData.setText(mstatus.getCreatedAt().toGMTString().substring(11, 20));
        // 加载用户头像
        String usericon = mstatus.getUser().getProfileImageURL().toString();
        mAnsylodar.showimgAnsy(vHolder.mIvitemPorprait, usericon);
        // 判断是否又转发
        if (mstatus.getRetweeted_status() != null) {
            vHolder.mContentPic.setVisibility(View.GONE);
            vHolder.mSubLayoutView.setVisibility(View.VISIBLE);// 设置有转发布局可见
            Status comentsStatus = mStatus.get(position).getRetweeted_status();// 获取又转发内容
            vHolder.mItemSubcontent.setText(comentsStatus.getText());
            String subconpic = comentsStatus.getThumbnail_pic().toString();
            if (null != subconpic) {
                vHolder.mSubContenPic.setVisibility(View.VISIBLE);// 设置又转发图片可见
                mAnsylodar.showimgAnsy(vHolder.mSubContenPic, subconpic);
            }
        } else {// 没有又转发 我们设置当前微博内容图片可见
            String pic_path = mstatus.getThumbnail_pic();
            if (null != pic_path) {
                vHolder.mContentPic.setVisibility(View.VISIBLE);
                mAnsylodar.showimgAnsy(vHolder.mContentPic, pic_path);
            }
            vHolder.mContentPic.setVisibility(View.GONE);
            vHolder.mSubLayoutView.setVisibility(View.GONE);// 设置又转发布局不可见
        }

        return statusView;
    }

}

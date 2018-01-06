package com.fengda.app.activity;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.fengda.app.R;
import com.fengda.app.bean.BannerBean;
import com.fengda.app.constant.MyConstant;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by lijuan on 2016/9/12.
 */
public class GridViewAdapter extends BaseAdapter {
    private List<BannerBean> mDatas;
    private LayoutInflater inflater;
    private Context mContext;
    /**
     * 页数下标,从0开始(当前是第几页)
     */
    private int curIndex;
    /**
     * 每一页显示的个数
     */
    private int pageSize;
    private String fronturl;
    public GridViewAdapter(Context context, List<BannerBean> mDatas, int curIndex, int pageSize, String url) {
        inflater = LayoutInflater.from(context);
        this.mContext = context;
        this.mDatas = mDatas;
        this.curIndex = curIndex;
        this.pageSize = pageSize;
        this.fronturl = url;
    }

    /**
     * 先判断数据集的大小是否足够显示满本页？mDatas.size() > (curIndex+1)*pageSize,
     * 如果够，则直接返回每一页显示的最大条目个数pageSize,
     * 如果不够，则有几项返回几,(mDatas.size() - curIndex * pageSize);(也就是最后一页的时候就显示剩余item)
     */
    @Override
    public int getCount() {
        return mDatas.size() > (curIndex + 1) * pageSize ? pageSize : (mDatas.size() - curIndex * pageSize);

    }

    @Override
    public Object getItem(int position) {
        return mDatas.get(position + curIndex * pageSize);
    }

    @Override
    public long getItemId(int position) {
        return position + curIndex * pageSize;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
//        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_gridview, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.tv = (TextView) convertView.findViewById(R.id.textView);
            viewHolder.iv = (ImageView) convertView.findViewById(R.id.imageView);
            convertView.setTag(viewHolder);
//        } else {
//            viewHolder = (ViewHolder) convertView.getTag();
//        }
        /**
         * 在给View绑定显示的数据时，计算正确的position = position + curIndex * pageSize，
         */
        int pos = position + curIndex * pageSize;
        viewHolder.tv.setText(mDatas.get(pos).getName());
        String bannerImg3 = (fronturl + mDatas.get(pos).getLogo() + MyConstant.PIC_DPI2).trim();
        Picasso.with(mContext).load(bannerImg3)
                //    .centerInside()
                .config(Bitmap.Config.RGB_565)
                .error(R.drawable.pic_nomal_loading_style)
                .into(viewHolder.iv);
        return convertView;
    }


    class ViewHolder {
        private TextView tv;
        private ImageView iv;
    }
}
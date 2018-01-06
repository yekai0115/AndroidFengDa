package com.fengda.app.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.fengda.app.R;
import com.fengda.app.activity.GoodsDetalActivity;
import com.fengda.app.activity.MyWebViewActivity;
import com.fengda.app.bean.BannerBean;
import com.fengda.app.constant.MyConstant;
import com.fengda.app.model.Message;
import com.fengda.app.utils.DimenUtils;
import com.fengda.app.utils.StringUtils;
import com.squareup.picasso.Picasso;

import java.util.LinkedList;
import java.util.List;


/**
 * 适配器
 */
public class NotifyAdapter extends PagerAdapter {
    private List<Message> list;
    private HolderView holderView;
    private LayoutInflater mInflater;
    private LinkedList<View> mViewCache = null;
    public NotifyAdapter(Context context, List<Message> subjectsInfos) {
        this.list = subjectsInfos;
        this.mInflater = LayoutInflater.from(context);
        this.mViewCache = new LinkedList<>();
    }

    public void update(List<Message> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return list == null ? 0 : list.size();
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {

        View convertView = null;
        if (mViewCache.size() == 0) {
            holderView = new HolderView();
            convertView = mInflater.inflate(R.layout.notify_item_view, null, false);
            holderView.tv_title = (TextView) convertView.findViewById(R.id.tv_title);
            holderView.tv_content = (TextView) convertView.findViewById(R.id.tv_content);
            convertView.setTag(holderView);
        } else {
            convertView = mViewCache.removeFirst();
            holderView = (HolderView) convertView.getTag();
        }
        final Message bean = list.get(position);
        holderView.tv_title.setText(bean.getTitle());
        holderView.tv_content.setText(bean.getContent());
        // container.addView(convertView);
        container.addView(convertView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        return convertView;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        View contentView = (View) object;
        container.removeView(contentView);
        this.mViewCache.add(contentView);

    }

    static class HolderView {

        private TextView tv_title;
        private TextView tv_content;
    }
}
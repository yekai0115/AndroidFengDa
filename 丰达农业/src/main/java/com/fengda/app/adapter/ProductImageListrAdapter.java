package com.fengda.app.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.fengda.app.R;
import com.fengda.app.activity.ImagePagerActivity;
import com.fengda.app.constant.MyConstant;
import com.fengda.app.utils.DimenUtils;

import java.util.ArrayList;
import java.util.LinkedList;


/**
 * 首页banner适配器
 */
public class ProductImageListrAdapter extends PagerAdapter {
    private ArrayList<String> list;
    private Context context;
    private HolderView holderView;
    private LayoutInflater mInflater;
    private LinkedList<View> mViewCache = null;
    private int width;

    public ProductImageListrAdapter(Context context, ArrayList<String> subjectsInfos) {
        this.context = context;
        this.list = subjectsInfos;
        this.mInflater = LayoutInflater.from(context);
        this.mViewCache = new LinkedList<>();
        width = DimenUtils.getWidth(context);
    }

    public void update(ArrayList<String> list) {
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
            convertView = mInflater.inflate(R.layout.banner_item_view, null, false);
            holderView.iv_banner = (ImageView) convertView.findViewById(R.id.iv_banner);
            convertView.setTag(holderView);
        } else {
            convertView = mViewCache.removeFirst();
            holderView = (HolderView) convertView.getTag();
        }
        final String url = list.get(position);
        Glide.with(context).load(MyConstant.ALI_PUBLIC_URL + url)
                .fitCenter()
                .placeholder(R.drawable.pic_nomal_loading_style)
                .error(R.drawable.pic_nomal_loading_style)
                .into(holderView.iv_banner);

        holderView.iv_banner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageBrower(position, list);
            }
        });
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

        private ImageView iv_banner;
    }


    private void imageBrower(int position, ArrayList<String> urls) {
        Intent intent = new Intent(context, ImagePagerActivity.class);
        intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_URLS, urls);
        intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_INDEX, position);// 角标
        context.startActivity(intent);
    }

}
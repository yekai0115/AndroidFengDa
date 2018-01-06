package com.fengda.app.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.fengda.app.R;
import com.fengda.app.constant.MyConstant;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;


/**
 * 缩放展示图片的适配器
 *
 * @author Administrator
 *
 */
public class ScaleImgAdapter extends BaseAdapter {
    private List<String> urls;

    private LayoutInflater mLayoutInflater;
    private Context context;

    public ScaleImgAdapter(List<String> urls, Context context) {
        this.urls = urls;
        this.context = context;
        mLayoutInflater = LayoutInflater.from(context);
    }

    public void updataListView(ArrayList<String> urls) {
        this.urls = urls;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return urls == null ? 0 : urls.size();
    }

    @Override
    public String getItem(int position) {
        return urls.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MyGridViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new MyGridViewHolder();
            convertView = mLayoutInflater.inflate(R.layout.scale_img_item,parent, false);
            x.view().inject(viewHolder, convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (MyGridViewHolder) convertView.getTag();
        }
        String url = getItem(position);
        Picasso.with(context).load(MyConstant.ALI_PUBLIC_URL + url)
                //    .centerInside()
                .config(Bitmap.Config.RGB_565)
                .placeholder(R.drawable.pic_nomal_loading_style)
                .error(R.drawable.pic_nomal_loading_style)
                .into(viewHolder.imageView);

        return convertView;
    }

    static class MyGridViewHolder {
        @ViewInject(R.id.album_image)
        private ImageView imageView;
    }



}
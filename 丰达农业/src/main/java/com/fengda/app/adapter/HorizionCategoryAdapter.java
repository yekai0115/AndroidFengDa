package com.fengda.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.fengda.app.R;
import com.fengda.app.bean.Catergory;

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
public class HorizionCategoryAdapter extends BaseAdapter {
    private List<Catergory> urls;

    private LayoutInflater mLayoutInflater;
    private Context context;
    /** 记录被点击的item位置索引 */
    public int mSelectIndex;
    public HorizionCategoryAdapter(List<Catergory> urls, Context context) {
        this.urls = urls;
        this.context = context;
        mLayoutInflater = LayoutInflater.from(context);
    }

    public void updataListView(ArrayList<Catergory> urls) {
        this.urls = urls;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return urls == null ? 0 : urls.size();
    }

    @Override
    public Object getItem(int position) {
        return urls.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MyGridViewHolder viewHolder;
        Catergory firstCatergory = (Catergory) getItem(position);
        if (convertView == null) {
            viewHolder = new MyGridViewHolder();
            convertView = mLayoutInflater.inflate(R.layout.adapter_category_goodslist_category_item,parent, false);
            x.view().inject(viewHolder, convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (MyGridViewHolder) convertView.getTag();
        }
        viewHolder.tv_category.setText(firstCatergory.getName());
        // 点击改变背景色
        if (mSelectIndex == position) {
            viewHolder.tv_category.setTextColor(context.getResources().getColor(R.color.bg_main_bottom));
        } else {
            viewHolder.tv_category.setTextColor(context.getResources().getColor(R.color.tv_color2));
        }
        return convertView;
    }

    static class MyGridViewHolder {
        @ViewInject(R.id.tv_category)
        private TextView tv_category;
        @ViewInject(R.id.img_indicator)
        private ImageView img_indicator;


    }



}
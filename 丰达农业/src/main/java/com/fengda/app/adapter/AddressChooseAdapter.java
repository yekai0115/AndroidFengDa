package com.fengda.app.adapter;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.fengda.app.R;
import com.fengda.app.bean.Address;
import com.fengda.app.utils.StringUtils;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.List;


public class AddressChooseAdapter extends BaseAdapter {

    private Context context;
    private List<Address> list;
    private HolderView holderView;
    private LayoutInflater mInflater;
    private int lastPosition = -1;


    public AddressChooseAdapter(Context context, List<Address> list) {
        super();
        this.context = context;
        this.list = list;
        this.mInflater = LayoutInflater.from(context);


    }

    /**
     * 当ListView数据发生变化时,调用此方法来更新ListView
     */
    public void updateListView(List<Address> List) {
        this.list = List;
        notifyDataSetChanged();

    }

    @Override
    public int getCount() {
        return list == null ? 0 : list.size();
    }

    @Override
    public Object getItem(int position) {

        return list.get(position);
    }

    @Override
    public long getItemId(int position) {

        return position;
    }

    @Override
    public View getView(final int position, View convertView,
                        final ViewGroup parent) {
        Address bean = list.get(position);
        if (convertView == null) {
            holderView = new HolderView();
            convertView = mInflater.inflate(R.layout.adapter_address_chose_list_item, null);
            x.view().inject(holderView, convertView);
            convertView.setTag(holderView);
        } else {
            holderView = (HolderView) convertView.getTag();
        }
        holderView.tv_name.setText(bean.getAddr_name());
        holderView.tv_phone.setText(bean.getAddr_mobile());
        String address;
        if (!StringUtils.isBlank(bean.getAddr_county())) {
            address = bean.getAddr_province_name() + bean.getAddr_city_name() + bean.getAddr_county_name() + "  " + bean.getAddr_detail();
        } else {
            address = bean.getAddr_province_name() + bean.getAddr_city_name() + "  " + bean.getAddr_detail();
        }
        if (position == 0) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("[默认地址]").append(address);
            SpannableStringBuilder builder = new SpannableStringBuilder(stringBuilder.toString());
            ForegroundColorSpan redSpan = new ForegroundColorSpan(context.getResources().getColor(R.color.bg_main_bottom));
            builder.setSpan(redSpan, 0, 6, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);//设置此部分为特殊颜色
            holderView.tv_place.setText(builder);
        } else {
            holderView.tv_place.setText(address);
        }
        return convertView;
    }


    static class HolderView {
        // 姓名
        @ViewInject(R.id.tv_name)
        private TextView tv_name;
        // 手机号
        @ViewInject(R.id.tv_phone)
        private TextView tv_phone;
        // 地址
        @ViewInject(R.id.tv_place)
        private TextView tv_place;
    }

}



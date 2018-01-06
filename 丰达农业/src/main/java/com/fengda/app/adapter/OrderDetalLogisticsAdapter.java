package com.fengda.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.fengda.app.R;
import com.fengda.app.bean.LogisticsBase;
import com.fengda.app.utils.DateUtil;
import com.fengda.app.utils.StringUtils;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.List;

/**
 * 订单详情页面物流信息
 */
public class OrderDetalLogisticsAdapter extends BaseAdapter {

    private List<LogisticsBase> list;
    private HolderView holderView;
    private LayoutInflater mInflater;


    public OrderDetalLogisticsAdapter(Context context, List<LogisticsBase> list) {
        super();
        this.list = list;
        this.mInflater = LayoutInflater.from(context);
    }

    /**
     * 当ListView数据发生变化时,调用此方法来更新ListView
     */
    public void updateListView(List<LogisticsBase> List) {
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
        LogisticsBase logisticsBase = list.get(position);
        if (convertView == null) {
            holderView = new HolderView();
            convertView = mInflater.inflate(R.layout.adapter_order_detal_logistics_item ,null);
            x.view().inject(holderView, convertView);
            convertView.setTag(holderView);
        } else {
            holderView = (HolderView) convertView.getTag();
        }
        if(list.size()==1){
            holderView.tv_package.setVisibility(View.GONE);
            holderView.view_line.setVisibility(View.GONE);
        }else{
            holderView.tv_package.setVisibility(View.VISIBLE);
            holderView.tv_package.setText("包裹-" + (position + 1));
            holderView.view_line.setVisibility(View.VISIBLE);
        }

        String deliverystatus =logisticsBase.getDeliverystatus();
        if(StringUtils.isBlank(deliverystatus)){
            holderView.tv_wl_state.setText("处理中");
            holderView.tv_wl_content.setText("处理中");
            holderView.tv_wl_time.setText(DateUtil.getStandardTime(System.currentTimeMillis()));
        }else{
            int status=Integer.valueOf(deliverystatus);
            if (status == 1) {//在途中
                holderView.tv_wl_state.setText("在途中");
            } else if (status == 2) {//派件中
                holderView.tv_wl_state.setText("派件中");
            } else if (status == 3) {//已签收
                holderView.tv_wl_state.setText("已签收");
            } else if (status == 4) {//派送失败
                holderView.tv_wl_state.setText("派送失败");
            }
            holderView.tv_wl_content.setText(logisticsBase.getStatus());
            holderView.tv_wl_time.setText(logisticsBase.getTime());
        }

        return convertView;
    }

    static class HolderView {

        @ViewInject(R.id.tv_package)
        private TextView tv_package;
        //最新物流状态
        @ViewInject(R.id.tv_wl_state)
        private TextView tv_wl_state;
        //最新物流状态
        @ViewInject(R.id.tv_wl_content)
        private TextView tv_wl_content;

        @ViewInject(R.id.tv_wl_time)
        private TextView tv_wl_time;

        @ViewInject(R.id.view_line)
        private View view_line;


    }

}



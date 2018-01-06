package com.fengda.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.fengda.app.R;
import com.fengda.app.bean.OrderGoodsInfo;
import com.fengda.app.bean.OrderMailInfo;
import com.fengda.app.constant.MyConstant;
import com.fengda.app.interfaces.ExpandListItemClickHelp;
import com.fengda.app.utils.CompuUtils;
import com.fengda.app.utils.StringUtils;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.List;


public class MailOrderAdapter extends BaseExpandableListAdapter {

    private Context context;
    private List<OrderMailInfo> list;
    private HolderView holderView;
    private LayoutInflater mInflater;
    private ExpandListItemClickHelp callback;


    public MailOrderAdapter(Context context, List<OrderMailInfo> list, ExpandListItemClickHelp callback) {
        super();
        this.context = context;
        this.list = list;
        this.callback = callback;
        this.mInflater = LayoutInflater.from(context);
    }

    /**
     * 当ListView数据发生变化时,调用此方法来更新ListView
     */
    public void updateListView(List<OrderMailInfo> List) {
        this.list = List;
        notifyDataSetChanged();

    }
    @Override
    public int getGroupCount() {
        return list == null ? 0 : list.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    //获取指定组位置处的组数据
    @Override
    public OrderMailInfo getGroup(int groupPosition) {

        return list.get(groupPosition);
    }


    //获取指定组位置、指定子列表项处的子列表项数据
    @Override
    public OrderGoodsInfo getChild(int groupPosition, int childPosition) {
        OrderMailInfo orderMailInfo=list.get(groupPosition);
        List<OrderGoodsInfo> goods_info_list=orderMailInfo.getGoods_info_list();
        OrderGoodsInfo orderGoodsInfo=goods_info_list.get(childPosition);
   //     return list.get(groupPosition).getGoods_info_list().get(childPosition);
        return  orderGoodsInfo;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        if(null==list||list.isEmpty()){
            return  0;
        }else{
            OrderMailInfo orderMailInfo=list.get(groupPosition);
            List<OrderGoodsInfo> goods_info_list=orderMailInfo.getGoods_info_list();
//        return list.get(groupPosition).getGoods_info_list() == null ? 0 : list.get(groupPosition).getGoods_info_list().size();
            return goods_info_list == null ? 0 : goods_info_list.size();
        }

    }

    //该方法决定每个子选项的外观
    @Override
    public View getChildView(final int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, final ViewGroup parent) {
        List<OrderGoodsInfo> goods_info_list=list.get(groupPosition).getGoods_info_list();
        OrderGoodsInfo goodsInfo = goods_info_list.get(childPosition);
        if (convertView == null) {
            holderView = new HolderView();
            convertView = mInflater.inflate(R.layout.adapter_mail_order_list_item, null);
            x.view().inject(holderView, convertView);
            convertView.setTag(holderView);
        } else {
            holderView = (HolderView) convertView.getTag();
        }
        int status = Integer.valueOf(list.get(groupPosition).getDelivery_status());
        if (childPosition == goods_info_list.size() - 1) {
            if (status == 0) {//待支付
                holderView.ll_state.setVisibility(View.VISIBLE);
                holderView.tv_wuliu.setText("取消订单");
                holderView.tv_shouhuo.setText("付款");
                holderView.tv_shouhuo.setVisibility(View.VISIBLE);
            } else if (status == 1) {//待发货
                holderView.ll_state.setVisibility(View.GONE);
            } else if (status == 2) {//待收货
                holderView.ll_state.setVisibility(View.VISIBLE);
                holderView.tv_wuliu.setVisibility(View.VISIBLE);
                holderView.tv_wuliu.setText("查看物流");
                holderView.tv_shouhuo.setText("确认收货");
                holderView.tv_shouhuo.setVisibility(View.VISIBLE);
            }else if (status == 3) {//已完成
                holderView.ll_state.setVisibility(View.GONE);
            }else if (status == 4) {//取消订单
                holderView.ll_state.setVisibility(View.GONE);
            }
            holderView.view_child_itemLine.setVisibility(View.VISIBLE);
            holderView.ll_bottom.setVisibility(View.VISIBLE);
        } else {
            holderView.view_child_itemLine.setVisibility(View.GONE);
            holderView.ll_state.setVisibility(View.GONE);
            holderView.ll_bottom.setVisibility(View.GONE);
        }
        if (Float.valueOf(goodsInfo.getAttr_point()) > 0){
            StringUtils.titleTipUtils(context,holderView.tv_goods_name,"消费果抵扣",goodsInfo.getGname(),10,15,10,15);
        }else if (goodsInfo.getReturn_point()!=null && Float.parseFloat(goodsInfo.getReturn_point())>0){
            StringUtils.titleTipUtils(context,holderView.tv_goods_name,"返分",goodsInfo.getGname(),12,15,10,16);
        }else {
            holderView.tv_goods_name.setText(goodsInfo.getGname());
        }
        String goods_logo = goodsInfo.getPic();
        String[] arr = goods_logo.split(",");
        goods_logo = arr == null || arr.length == 0 ? "" : arr[0];
        Glide.with(context).load(MyConstant.ALI_PUBLIC_URL + goods_logo).fitCenter()
                //  .override(width,DimenUtils.dip2px(context,130))
                .placeholder(R.drawable.default_user).error(R.drawable.default_user).into(holderView.img_goods_pic);
//        holderView.tv_goods_name.setText(goodsInfo.getName());
        holderView.tv_guige.setText(goodsInfo.getAttr_value());
        holderView.tv_num.setText("X" + goodsInfo.getNumber());
        holderView.tv_price.setText(goodsInfo.getAttr_price());
        String nocan_points = list.get(groupPosition).getNocan_points();
        String postage = list.get(groupPosition).getPostage();
        String totalprice = list.get(groupPosition).getTotalprice();
        holderView.tv_point.setText(nocan_points);
        holderView.tv_total_yunfei.setText(postage);
        int  totalNumber=0;
        for(OrderGoodsInfo orderGoodsInfo:goods_info_list){
            int number=orderGoodsInfo.getNumber();
            totalNumber= CompuUtils.add(totalNumber,number);
        }
        holderView.tv_total_num.setText("" + totalNumber);
        holderView.tv_total_money.setText(totalprice);


        final View view = convertView;
        final int p = childPosition;
        final int one = holderView.tv_wuliu.getId();
        final int two = holderView.tv_shouhuo.getId();
        final int three = holderView.ll_detal.getId();

        //查看物流
        holderView.tv_wuliu.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                callback.onCheckClick(view, parent, groupPosition, childPosition, one);
            }
        });

        //立即收货
        holderView.tv_shouhuo.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                callback.onCheckClick(view, parent, groupPosition, childPosition, two);
            }
        });

        //查看详情
        holderView.ll_detal.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                callback.onCheckClick(view, parent, groupPosition, childPosition, three);
            }
        });
        return convertView;


    }





    //该方法决定每个组选项的外观
    private GroupHolderView groupHolderView;

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View groupConvertView, ViewGroup parent) {

        if (groupConvertView == null) {
            groupHolderView = new GroupHolderView();
            groupConvertView = mInflater.inflate(R.layout.adapter_group_list, null);
            x.view().inject(groupHolderView, groupConvertView);
            groupConvertView.setTag(groupHolderView);
        } else {
            groupHolderView = (GroupHolderView) groupConvertView.getTag();
        }

//        groupHolderView.tv_trade_id.setText(list.get(groupPosition).getOrder_sn());
        int status = Integer.valueOf(list.get(groupPosition).getDelivery_status());
        if (status == 0) {//待支付
            groupHolderView.tv_order_state.setText("待支付");
        } else if (status == 1) {//待发货
            groupHolderView.tv_order_state.setText("待发货");
        } else if (status == 2) {//待收货
            groupHolderView.tv_order_state.setText("待收货");
        }else if (status == 3) {//已完成
            groupHolderView.tv_order_state.setText("已签收");
        }else {
            groupHolderView.tv_order_state.setText("已取消");
        }
        return groupConvertView;

    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    static class GroupHolderView {
        @ViewInject(R.id.tv_trade_id)
        private TextView tv_trade_id;
        //
        @ViewInject(R.id.tv_order_state)
        private TextView tv_order_state;
    }

    static class HolderView {

        @ViewInject(R.id.img_goods_pic)
        private ImageView img_goods_pic;

        @ViewInject(R.id.tv_goods_name)
        private TextView tv_goods_name;

        @ViewInject(R.id.tv_guige)
        private TextView tv_guige;

        @ViewInject(R.id.tv_price)
        private TextView tv_price;

        @ViewInject(R.id.tv_point)
        private TextView tv_point;

        @ViewInject(R.id.tv_num)
        private TextView tv_num;


        @ViewInject(R.id.ll_state)
        private LinearLayout ll_state;

        @ViewInject(R.id.tv_wuliu)
        private TextView tv_wuliu;
        @ViewInject(R.id.tv_shouhuo)
        private TextView tv_shouhuo;

        @ViewInject(R.id.ll_detal)
        private LinearLayout ll_detal;

        /**
         * 运费
         */
        @ViewInject(R.id.tv_total_yunfei)
        private TextView tv_total_yunfei;
        /**
         * 合计
         */
        @ViewInject(R.id.tv_total_money)
        private TextView tv_total_money;
        /**
         * 合计
         */
        @ViewInject(R.id.tv_total_num)
        private TextView tv_total_num;

        @ViewInject(R.id.view_child_itemLine)
        private View view_child_itemLine;
        /**
         * 订单底部
         */
        @ViewInject(R.id.ll_bottom)
        private LinearLayout ll_bottom;


    }

}



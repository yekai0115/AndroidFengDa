package com.fengda.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.fengda.app.R;
import com.fengda.app.bean.OrderGoodsInfo;
import com.fengda.app.constant.MyConstant;
import com.fengda.app.utils.StringUtils;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.List;


/**
 * 订单详情订单适配器
 *
 * @author Administrator
 */
public class OrderDetalsAdapter extends BaseAdapter {
    private Context context;
    private List<OrderGoodsInfo> list;
    private LayoutInflater mInflater;


    public OrderDetalsAdapter(Context context, List<OrderGoodsInfo> list) {
        this.context = context;
        this.list = list;

        this.mInflater = LayoutInflater.from(context);
    }

    public OrderDetalsAdapter(Context context) {
        this.context = context;

    }

    public void updateListview(List<OrderGoodsInfo> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int arg0) {
        return list.get(arg0);
    }

    @Override
    public long getItemId(int arg0) {
        return 0;
    }
    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        HolderView holderView = null;
        final OrderGoodsInfo goods = list.get(position);
        if (convertView == null) {
            holderView = new HolderView();
            convertView = mInflater.inflate(R.layout.adapter_order_goods_list_item, null);
            x.view().inject(holderView, convertView);
            convertView.setTag(holderView);
        } else {
            holderView = (HolderView) convertView.getTag();
        }
        if (list.size() != 0) {
            if (Float.valueOf(goods.getAttr_point()) > 0){
                StringUtils.titleTipUtils(context,holderView.tv_goodsTitle,"消费果抵扣",goods.getGname(),10,15,8,15);
            }else if (goods.getReturn_point()!=null && Float.parseFloat(goods.getReturn_point())>0){
                StringUtils.titleTipUtils(context,holderView.tv_goodsTitle,"返分",goods.getGname(),12,15,8,16);
            }else {
                holderView.tv_goodsTitle.setText(goods.getGname());
            }
            holderView.tv_guige.setText(goods.getAttr_value());
            holderView.tv_num.setText("X" + goods.getNumber() + "");
            holderView.tv_goods_danjia.setText(goods.getAttr_price());
//            holderView.tv_member_price.setText(goods.getAttr_value());
            String point = goods.getAttr_point();
            if (StringUtils.isBlank(point)) {
                point = "0";
            }
            holderView.tv_point.setText(point);
//            String postprice = goods.getPostprice();//邮费
//            int compare = CompuUtils.compareTo(postprice, "0");
//            if (compare > 0) {
//                holderView.tv_yunfei.setText("快递  " + postprice);
//            } else {
//                holderView.tv_yunfei.setText("快递  包邮");
//            }
            String goods_logo = goods.getPic();
            String[] arr = goods_logo.split(",");
            Glide.with(context).load(MyConstant.ALI_PUBLIC_URL + arr[0])
                    //     .override(DimenUtils.px2dip(context, 100), DimenUtils.px2dip(context, 100))
                    // .fitCenter()
                    .centerCrop()
                    .placeholder(R.drawable.bg_loading_style)
                    .error(R.drawable.bg_loading_style)
                    .into(holderView.iv_goods_pic);

        }
        return convertView;
    }

    static class HolderView {
        @ViewInject(R.id.iv_goods_pic)
        private ImageView iv_goods_pic;
        /**
         * 商品
         */
        @ViewInject(R.id.tv_goodsTitle)
        private TextView tv_goodsTitle;

        /**
         * 原价
         */
        @ViewInject(R.id.tv_goods_danjia)
        private TextView tv_goods_danjia;
        /**
         * 规格
         */
        @ViewInject(R.id.tv_guige)
        private TextView tv_guige;

        /**
         * 运费
         */
        @ViewInject(R.id.tv_yunfei)
        private TextView tv_yunfei;
        /**
         * 会员价
         */
        @ViewInject(R.id.tv_member_price)
        private TextView tv_member_price;
        @ViewInject(R.id.tv_point)
        private TextView tv_point;
        /**
         * 数量
         */
        @ViewInject(R.id.tv_num)
        private TextView tv_num;


        @ViewInject(R.id.tv_use_point)
        private TextView tv_use_point;


    }

}

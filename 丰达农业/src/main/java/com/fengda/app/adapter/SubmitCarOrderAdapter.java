package com.fengda.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.fengda.app.R;
import com.fengda.app.bean.GoodsInfo;
import com.fengda.app.bean.Shipping;
import com.fengda.app.constant.MyConstant;
import com.fengda.app.interfaces.ListItemClickHelp;
import com.fengda.app.utils.CompuUtils;
import com.fengda.app.utils.StringUtils;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.List;


/**
 * 购物车确认订单适配器
 *
 * @author Administrator
 */
public class SubmitCarOrderAdapter extends BaseAdapter {
    private Context context;
    private List<GoodsInfo> list;
    private LayoutInflater mInflater;
    private ListItemClickHelp callback;
    private String shipping_id;

    public SubmitCarOrderAdapter(Context context, List<GoodsInfo> list, ListItemClickHelp callback) {
        this.context = context;
        this.list = list;
        this.callback = callback;

        this.mInflater = LayoutInflater.from(context);
    }

    public SubmitCarOrderAdapter(Context context) {
        this.context = context;

    }

    public void updateListview(List<GoodsInfo> list) {
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
        final GoodsInfo goods = list.get(position);
        if (convertView == null) {
            holderView = new HolderView();
            convertView = mInflater.inflate(R.layout.order_queren_item, null);
            x.view().inject(holderView, convertView);
            convertView.setTag(holderView);
        } else {
            holderView = (HolderView) convertView.getTag();
        }
        if (list.size() != 0) {
            if (Float.parseFloat(list.get(position).getAttr_point())>0){
                StringUtils.titleTipUtils(context,holderView.tv_goodsTitle,"消费果抵扣",goods.getGname(),12,15,4,16);
            }else if (Float.parseFloat(list.get(position).getReturn_point())>0) {
                StringUtils.titleTipUtils(context, holderView.tv_goodsTitle, "返分", goods.getGname(), 12, 15, 4, 16);
            }else {
                holderView.tv_goodsTitle.setText(goods.getGname());

            }
            holderView.tv_guige.setText(goods.getAttr_value());
            holderView.tv_buy_num.setText(goods.getNumber() + "");
            holderView.tv_goods_danjia.setText(goods.getAttr_price());
            holderView.rl_kuaidi.setVisibility(View.VISIBLE);
            String postprice = goods.getPostprice();//邮费
            int compare = CompuUtils.compareTo(postprice, "0");
            if (compare > 0) {            //   goodsTotalPoint = CompuUtils.multiply(goods_point, "" + buyNum).toString();//商品需要的总积分（抵扣金额：商品原价-会员价）

                holderView.tv_yunfei.setText("快递  " + postprice);
            } else {
                holderView.tv_yunfei.setText("快递  包邮");
            }

            String goods_logo = goods.getGoods_logo();
            String[] arr = goods_logo.split(",");
            Glide.with(context).load(MyConstant.ALI_PUBLIC_URL + arr[0])
                    //     .override(DimenUtils.px2dip(context, 100), DimenUtils.px2dip(context, 100))
                    // .fitCenter()
                    .centerCrop()
                    .placeholder(R.drawable.bg_loading_style)
                    .error(R.drawable.bg_loading_style)
                    .into(holderView.iv_goods_pic);


            final View view = convertView;
            final int p = position;
            final int one = holderView.tv_num_jian.getId();
            final int two = holderView.tv_num_jia.getId();
            //减
            holderView.tv_num_jian.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    callback.onClick(view, parent, p, one);
                }
            });

            //加
            holderView.tv_num_jia.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    callback.onClick(view, parent, p, two);
                }
            });

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
         * 数量
         */
        @ViewInject(R.id.tv_buy_num)
        private TextView tv_buy_num;

        @ViewInject(R.id.tv_num_jian)
        private TextView tv_num_jian;

        @ViewInject(R.id.tv_num_jia)
        private TextView tv_num_jia;




        @ViewInject(R.id.rl_kuaidi)
        private RelativeLayout rl_kuaidi;

    }

}

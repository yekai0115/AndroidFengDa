package com.fengda.app.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.fengda.app.R;
import com.fengda.app.activity.GoodsDetalActivity;
import com.fengda.app.bean.CartGoodsBase;
import com.fengda.app.constant.MyConstant;
import com.fengda.app.interfaces.ListItemClickHelp;
import com.fengda.app.utils.StringUtils;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.List;


public class ShoppingCarAdapter extends BaseAdapter {

	private Context context;
	private List<CartGoodsBase> list;
	private HolderView holderView;
	private LayoutInflater mInflater;
	private int lastPosition = -1;

	private ListItemClickHelp callback;


	public ShoppingCarAdapter(Context context, List<CartGoodsBase> list, ListItemClickHelp callback) {
		super();
		this.context = context;
		this.list = list;
		this.mInflater=LayoutInflater.from(context);  
		this.callback=callback;

	}

	/**
	 * 当ListView数据发生变化时,调用此方法来更新ListView
	 */
	public void updateListView(List<CartGoodsBase> List) {
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
		CartGoodsBase cartGoods=list.get(position);
		if (convertView == null) {
			holderView = new HolderView();
			convertView = mInflater.inflate(R.layout.adapter_car_list_item, null);
			x.view().inject(holderView,convertView);
			convertView.setTag(holderView);
		} else {
			holderView = (HolderView) convertView.getTag();
		}
		holderView.cb_check.setChecked(cartGoods.isChecked());
		holderView.tv_goods_name.setText(cartGoods.getGname());
		holderView.tv_price.setText(cartGoods.getPrice());
		holderView.tv_member_price.setText(cartGoods.getAttr_price());
		String point = cartGoods.getAttr_point();
		if (StringUtils.isBlank(point)) {
			point="0";
		}
		String number=cartGoods.getNumber();
		holderView.tv_buy_num.setText(number);
		holderView.tv_point.setText(point);
		holderView.tv_guige.setText(cartGoods.getAttr_value());
		String goods_logo = cartGoods.getGoods_logo();
		if (!StringUtils.isBlank(goods_logo)){
			String[] arr = goods_logo.split(",");
			Glide.with(context).load(MyConstant.ALI_PUBLIC_URL + arr[0])
					//     .override(DimenUtils.px2dip(context, 100), DimenUtils.px2dip(context, 100))
					// .fitCenter()
					.centerCrop()
					.placeholder(R.drawable.bg_loading_style)
					.error(R.drawable.bg_loading_style)
					.into(holderView.img_goods_pic);
		}
		final View view = convertView;
		final int p = position;
		final int one = holderView.tv_num_jian.getId();
		final int two = holderView.tv_num_jia.getId();
		final int three = holderView.cb_check.getId();
		final int four = holderView.ll_shop_info.getId();

		//减
		holderView.tv_num_jian.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				callback.onClick(view, parent, p, one);
			}
		});

		//加
		holderView.tv_num_jia.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				callback.onClick(view, parent, p, two);
			}
		});


		// 是否选中按钮的事件
		holderView.cb_check.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				callback.onClick(view, parent, p, three);
			}
		});
		return convertView;
	}

	static class HolderView {
		// 商品图片
		@ViewInject(R.id.img_goods_pic)
		private ImageView img_goods_pic;
		// 手机号
		@ViewInject(R.id.tv_goods_name)
		private TextView tv_goods_name;
		// 地址
		@ViewInject(R.id.tv_guige)
		private TextView tv_guige;
		// 单选框：默认地址
		@ViewInject(R.id.tv_price)
		private TextView tv_price;
		// 编辑
		@ViewInject(R.id.tv_member_price)
		private TextView tv_member_price;
		@ViewInject(R.id.tv_point)
		private TextView tv_point;

		@ViewInject(R.id.tv_num_jian)
		private TextView tv_num_jian;
		@ViewInject(R.id.tv_buy_num)
		private TextView tv_buy_num;
		@ViewInject(R.id.tv_num_jia)
		private TextView tv_num_jia;
		@ViewInject(R.id.cb_check)
		private CheckBox cb_check;
		@ViewInject(R.id.ll_shop_info)
		private LinearLayout ll_shop_info;

	}

}



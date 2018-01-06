package com.fengda.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.fengda.app.R;
import com.fengda.app.bean.CartGoodsBase;
import com.fengda.app.bean.FavoriteShop;
import com.fengda.app.constant.MyConstant;
import com.fengda.app.interfaces.ListItemClickHelp;
import com.fengda.app.utils.StringUtils;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.List;


public class CollectAdapter extends BaseAdapter {

	private Context context;
	private List<FavoriteShop> list;
	private HolderView holderView;
	private LayoutInflater mInflater;
	private int lastPosition = -1;

	private ListItemClickHelp callback;


	public CollectAdapter(Context context, List<FavoriteShop> list, ListItemClickHelp callback) {
		super();
		this.context = context;
		this.list = list;
		this.mInflater=LayoutInflater.from(context);  
		this.callback=callback;

	}

	/**
	 * 当ListView数据发生变化时,调用此方法来更新ListView
	 */
	public void updateListView(List<FavoriteShop> List) {
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
		FavoriteShop favoriteGoods=list.get(position);
		if (convertView == null) {
			holderView = new HolderView();
			convertView = mInflater.inflate(R.layout.adapter_collect_list_item, null);
			x.view().inject(holderView,convertView);
			convertView.setTag(holderView);
		} else {
			holderView = (HolderView) convertView.getTag();
		}
		holderView.cb_check.setChecked(favoriteGoods.isChecked());
		Boolean visitivity=favoriteGoods.getVisibility();
		if(visitivity){
			//holderView.cb_chose.startAnimation(inAnimation);
			holderView.cb_check.setVisibility(View.VISIBLE);
		}else{
			// holderView.cb_chose.startAnimation(outAnimation);
			holderView.cb_check.setVisibility(View.GONE);
		}
		if (list.get(position).getPoint()!=null){
			if (Float.parseFloat(list.get(position).getPoint())>0){
				StringUtils.titleTipUtils(context,holderView.tv_goods_name,"消费果抵扣",favoriteGoods.getGname(),12,15,10,16);
			}else if (Float.parseFloat(list.get(position).getReturn_point())>0){
				StringUtils.titleTipUtils(context,holderView.tv_goods_name,"返分",favoriteGoods.getGname(),12,15,10,16);
			}else {
				holderView.tv_goods_name.setText(favoriteGoods.getGname());
			}
		}else {
			holderView.tv_goods_name.setText(favoriteGoods.getGname());
		}
//		holderView.tv_goods_name.setText(cartGoods.getGname());
		holderView.tv_price.setText(favoriteGoods.getPrice());
		String goods_logo = favoriteGoods.getGoods_logo();
		String[] arr = goods_logo.split(",");
		Glide.with(context).load(MyConstant.ALI_PUBLIC_URL + arr[0])
				//     .override(DimenUtils.px2dip(context, 100), DimenUtils.px2dip(context, 100))
				// .fitCenter()
				.centerCrop()
				.placeholder(R.drawable.bg_loading_style)
				.error(R.drawable.bg_loading_style)
				.into(holderView.img_goods_pic);

		final View view = convertView;
		final int p = position;
		final int one = holderView.cb_check.getId();





		// 是否选中按钮的事件
		holderView.cb_check.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				callback.onClick(view, parent, p, one);
			}
		});
		return convertView;
	}

	static class HolderView {
		// 商品图片
		@ViewInject(R.id.img_goods_pic)
		private ImageView img_goods_pic;
		//
		@ViewInject(R.id.tv_goods_name)
		private TextView tv_goods_name;
		//
		@ViewInject(R.id.tv_guige)
		private TextView tv_guige;
		// 单选框：默认地址
		@ViewInject(R.id.tv_price)
		private TextView tv_price;
		@ViewInject(R.id.cb_check)
		private CheckBox cb_check;
	}

}



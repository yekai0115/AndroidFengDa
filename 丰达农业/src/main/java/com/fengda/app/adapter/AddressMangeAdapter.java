package com.fengda.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.fengda.app.R;
import com.fengda.app.bean.Address;
import com.fengda.app.interfaces.ListItemClickHelp;
import com.fengda.app.utils.StringUtils;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.List;


public class AddressMangeAdapter extends BaseAdapter {

	private Context context;
	private List<Address> list;
	private HolderView holderView;
	private LayoutInflater mInflater;  
	private int lastPosition = -1;
	
	private ListItemClickHelp callback;

	
	public AddressMangeAdapter(Context context, List<Address> list, ListItemClickHelp callback) {
		super();
		this.context = context;
		this.list = list;
		this.mInflater=LayoutInflater.from(context);  
		this.callback=callback;

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
		Address bean=list.get(position);
		if (convertView == null) {
			holderView = new HolderView();
			convertView = mInflater.inflate(R.layout.destination_manage_list_item, null);
			x.view().inject(holderView,convertView);
			convertView.setTag(holderView);
		} else {
			holderView = (HolderView) convertView.getTag();
		}
		holderView.tv_name.setText(bean.getAddr_name());
		holderView.tv_phone.setText(bean.getAddr_mobile());
		if(!StringUtils.isBlank(bean.getAddr_county())){
			holderView.tv_place.setText(bean.getAddr_province_name()+bean.getAddr_city_name()+bean.getAddr_county_name()+"  "+bean.getAddr_detail());
		}else{
			holderView.tv_place.setText(bean.getAddr_province_name()+bean.getAddr_city_name()+"  "+bean.getAddr_detail());
		}
		int  addr_primary=bean.getAddr_primary();
		if(addr_primary==1){
			holderView.cb_chose.setChecked(true);
		}else{
			holderView.cb_chose.setChecked(false);
		}
		final View view = convertView;
		final int p = position;
		final int one = holderView.cb_chose.getId();
		final int two = holderView.tv_bianji.getId();
		final int three = holderView.tv_delete.getId();

		//编辑
		holderView.tv_bianji.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				callback.onClick(view, parent, p, two);
			}
		});

		//编辑
		holderView.tv_delete.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				callback.onClick(view, parent, p, three);
			}
		});



		// 是否选中按钮的事件
		holderView.cb_chose.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				callback.onClick(view, parent, p, one);
			}
		});
		
		

//		if (position > lastPosition) {//这里就是动画的应用
//			Animation animation = AnimationUtils.loadAnimation(mContext, (position > lastPosition) ? R.anim.up_from_bottom :                                 R.anim.down_from_top);
//			convertView.startAnimation(animation);
//			lastPosition = position;
//			}
		return convertView;
	}

	protected void changeView(int position) {

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
		// 单选框：默认地址
		@ViewInject(R.id.cb_chose)
		private CheckBox cb_chose;
		// 编辑
		@ViewInject(R.id.tv_bianji)
		private TextView tv_bianji;
		@ViewInject(R.id.tv_delete)
		private TextView tv_delete;
	}

}



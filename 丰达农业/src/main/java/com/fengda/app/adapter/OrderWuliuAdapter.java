package com.fengda.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.fengda.app.R;
import com.fengda.app.bean.Logistics;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.List;


/**
 * 物流适配器
 * 
 * @author Administrator
 * 
 */
public class OrderWuliuAdapter extends BaseAdapter {
	private List<Logistics> groupList;
	private LayoutInflater inflater;
	private HolderView holderView;
	private Context context;

	public OrderWuliuAdapter(Context ctx, List<Logistics> wlList) {
		this.groupList = wlList;
		this.context = ctx;
		inflater = LayoutInflater.from(ctx);
	}

	public void updatelistview(List<Logistics> wlList) {
		this.groupList = wlList;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {

		return groupList.size();
	}

	@Override
	public Object getItem(int position) {

		return groupList.get(position);
	}

	@Override
	public long getItemId(int position) {

		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Logistics bean = (Logistics) getItem(position);
		if (convertView == null) {
			holderView = new HolderView();
			convertView = inflater.inflate(R.layout.adapter_order_logistics, null);
			x.view().inject(holderView,convertView);  
			convertView.setTag(holderView);
		} else {
			holderView = (HolderView) convertView.getTag();
		}

		if (position == 0) {
			holderView.line.setVisibility(View.INVISIBLE);
		//	holderView.view_bottom.setVisibility(View.VISIBLE);
			holderView.tv_content.setTextColor(context.getResources().getColor(R.color.bg_main_bottom));
			holderView.img_point.setVisibility(View.VISIBLE);
			holderView.img_point_gray.setVisibility(View.GONE);

		} else if (position == (groupList.size() - 1)) {
			holderView.line.setVisibility(View.VISIBLE);
	//		holderView.view_bottom.setVisibility(View.GONE);
			holderView.img_point.setVisibility(View.GONE);
			holderView.img_point_gray.setVisibility(View.VISIBLE);
			holderView.tv_content.setTextColor(context.getResources().getColor(R.color.tv_color6));
		} else {
		//	holderView.view_bottom.setVisibility(View.VISIBLE);
			holderView.line.setVisibility(View.VISIBLE);
			holderView.img_point.setVisibility(View.GONE);
			holderView.img_point_gray.setVisibility(View.VISIBLE);
			holderView.tv_content.setTextColor(context.getResources().getColor(R.color.tv_color6));

		}

//		if (position == (groupList.size() - 1)) {
//			holderView.view_line.setVisibility(View.INVISIBLE);
//		} else {
//			holderView.view_line.setVisibility(View.VISIBLE);
//		}

		holderView.tv_content.setText(bean.getStatus());
		holderView.tv_time.setText(bean.getTime());

		return convertView;
	}

	static class HolderView {
		@ViewInject(R.id.img_point)  
		private ImageView img_point;
		@ViewInject(R.id.img_point_gray)  
		private ImageView img_point_gray;
		@ViewInject(R.id.tv_content)  
		private TextView tv_content;
		@ViewInject(R.id.tv_time)  
		private TextView tv_time;
		@ViewInject(R.id.line)  
		private View line;

//		@ViewInject(R.id.view_bottom)
//		private View view_bottom;
//		@ViewInject(R.id.view_line)
//		private View view_line;
	}
}

package com.fengda.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fengda.app.R;
import com.fengda.app.model.GoodsBean;
import com.fengda.app.view.flowlayout.TagFlowLayout;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.List;

/**
 * 活动
 */
public class ActiviticesAdapter extends BaseAdapter {

	private HolderView holderView;
	private Context context;
	private List<GoodsBean> arrayList;
	private LayoutInflater mInflater;

	public ActiviticesAdapter(Context context,
                              List<GoodsBean> arrayList) {
		this.context = context;
		this.arrayList = arrayList;
		 this.mInflater=LayoutInflater.from(context);  
	}

	/**
	 * 当ListView数据发生变化时,调用此方法来更新ListView

	 */
	public void updateListView(List<GoodsBean> arrayList) {

		this.arrayList = arrayList;
		notifyDataSetChanged();

	}

	@Override
	public int getCount() {
		return arrayList.size();
	}

	@Override
	public Object getItem(int position) {
		return arrayList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final GoodsBean parentCatergory=arrayList.get(position);
		if (convertView == null) {
			holderView = new HolderView();
			convertView = mInflater.inflate(R.layout.catergory_list_item, null);
			 x.view().inject(holderView,convertView);
			convertView.setTag(holderView);
		} else {
			holderView = (HolderView) convertView.getTag();
		}
		return convertView;
	}


	static class HolderView {
		/** 一级分类标题 */
		@ViewInject(R.id.tv_firstCategory)
		private TextView tv_firstCategory;
		/** 一级分类图标 */
		@ViewInject(R.id.iv_adapter_list_pic)
		private ImageView iv_adapter_list_pic;
		@ViewInject(R.id.linearLayout)
		private LinearLayout linearLayout;
		@ViewInject(R.id.categoty_flowlayout)
		private TagFlowLayout categoty_flowlayout;
	}
}

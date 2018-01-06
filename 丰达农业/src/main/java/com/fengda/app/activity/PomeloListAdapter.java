package com.fengda.app.activity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.fengda.app.R;
import com.fengda.app.bean.PomeloBuyBean;
import com.fengda.app.view.HandyTextView;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.List;

/**
 * 适配器
 * @author Administrator
 * 
 */
public class PomeloListAdapter extends BaseAdapter {

	private HolderView holderView;

	private Context context;
	private LayoutInflater mInflater;
	private List<PomeloBuyBean> list;

	public PomeloListAdapter(Context context) {
		this.mInflater = LayoutInflater.from(context);

		this.context = context;
	}
	public PomeloListAdapter(Context context, List<PomeloBuyBean> list) {
		this.list = list;
		this.context = context;
		this.mInflater = LayoutInflater.from(context);

	}

	public void updateListview(List<PomeloBuyBean> list) {
		this.list = list;
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
	public View getView(final int position, View convertView, ViewGroup parent) {
		final PomeloBuyBean pomeloBuyBean = (PomeloBuyBean) getItem(position);
		if (convertView == null) {
			holderView = new HolderView();
			convertView = mInflater.inflate(R.layout.adapter_exchange_list_item, null);
			x.view().inject(holderView, convertView);
			convertView.setTag(holderView);
		} else {
			holderView = (HolderView) convertView.getTag();
		}
		holderView.iar_plan_name.setVisibility(View.VISIBLE);
		holderView.iar_plan_name.setText("金柚A");
		holderView.tv_bank.setText(pomeloBuyBean.getBankname());
		holderView.tv_time.setText(pomeloBuyBean.getAdd_time());
		holderView.tv_money.setText("-"+pomeloBuyBean.getMoney());
		holderView.ex_states.setVisibility(View.VISIBLE);
		holderView.ex_states.setText(pomeloBuyBean.getRemark());
		return convertView;
	}

	static class HolderView {
		@ViewInject(R.id.tv_bank)
		private HandyTextView tv_bank;
		@ViewInject(R.id.tv_time)
		private HandyTextView tv_time;
		@ViewInject(R.id.tv_money)
		private HandyTextView tv_money;

		@ViewInject(R.id.ex_states)
		private HandyTextView ex_states;

		@ViewInject(R.id.iar_status)
		private HandyTextView iar_status;

		@ViewInject(R.id.iar_plan_name)
		private HandyTextView iar_plan_name;

	}

}

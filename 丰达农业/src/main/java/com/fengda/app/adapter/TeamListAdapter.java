package com.fengda.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.fengda.app.R;
import com.fengda.app.bean.ExchangeBean;
import com.fengda.app.view.HandyTextView;
import com.fengda.app.widget.GlideCircleTransform;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.List;

/**
 * 适配器
 * @author Administrator
 * 
 */
public class TeamListAdapter extends BaseAdapter {

	private HolderView holderView;

	private Context context;
	private LayoutInflater mInflater;
	private List<ExchangeBean> list;


	public TeamListAdapter(Context context) {
		this.mInflater = LayoutInflater.from(context);
		this.context = context;
	}
	public TeamListAdapter(Context context, List<ExchangeBean> list) {
		this.list = list;
		this.context = context;
		this.mInflater = LayoutInflater.from(context);

	}

	public void updateListview(List<ExchangeBean> list) {
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
		final ExchangeBean exchangeBean = (ExchangeBean) getItem(position);
		if (convertView == null) {
			holderView = new HolderView();
			convertView = mInflater.inflate(R.layout.adapter_team_list_item, null);
			x.view().inject(holderView, convertView);
			convertView.setTag(holderView);
			// 可以对item设置不同的动画
			// convertView.setAnimation(AnimationUtils.loadAnimation(context,
			// R.anim.push_right_in));
		} else {
			holderView = (HolderView) convertView.getTag();
		}
		Glide.with(context).load(R.drawable.one)
				.fitCenter()
				.override(80, 80).transform(new GlideCircleTransform(context))
				.placeholder(R.drawable.img_default_head)
				.error(R.drawable.img_default_head)
				.into(holderView.tv_img_logo);
		holderView.tv_team_performance.setText("兑换到"+exchangeBean.getBankname());
		holderView.tv_team_name.setText(exchangeBean.getAdd_time());
		holderView.tv_team_leavel.setText("-"+exchangeBean.getMoney());
		holderView.tv_team_num.setText(exchangeBean.getStatus());
		return convertView;
	}

	static class HolderView {
		@ViewInject(R.id.tv_team_performance)
		private HandyTextView tv_team_performance;

		@ViewInject(R.id.tv_team_name)
		private HandyTextView tv_team_name;

		@ViewInject(R.id.tv_team_leavel)
		private HandyTextView tv_team_leavel;
		@ViewInject(R.id.tv_team_num)
		private HandyTextView tv_team_num;

		@ViewInject(R.id.tv_img_logo)
		private ImageView tv_img_logo;


	}

}

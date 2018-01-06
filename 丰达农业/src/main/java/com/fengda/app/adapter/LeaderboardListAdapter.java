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
import com.fengda.app.utils.DimenUtils;
import com.fengda.app.view.HandyTextView;
import com.fengda.app.widget.GlideCircleTransform;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.List;

import static android.R.attr.banner;
import static android.R.attr.width;
import static com.fengda.app.R.id.img_my_ranking;

/**
 * 适配器
 * @author Administrator
 * 
 */
public class LeaderboardListAdapter extends BaseAdapter {

	private HolderView holderView;

	private Context context;
	private LayoutInflater mInflater;
	private List<ExchangeBean> list;


	public LeaderboardListAdapter(Context context) {
		this.mInflater = LayoutInflater.from(context);

		this.context = context;
	}
	public LeaderboardListAdapter(Context context, List<ExchangeBean> list) {
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
		} else {
			holderView = (HolderView) convertView.getTag();
		}
		Glide.with(context).load(R.drawable.img_default_head)
				.fitCenter()
				.transform(new GlideCircleTransform(context))
				.placeholder(R.drawable.img_default_head)
				.error(R.drawable.img_default_head)
				.into(holderView.img_logo);
		switch (position){
			case 0:
				holderView.tv_ranking.setVisibility(View.GONE);
				holderView.img_ranking.setVisibility(View.VISIBLE);
				holderView.img_ranking.setImageResource(R.drawable.one);
				break;
			case 1:
				holderView.tv_ranking.setVisibility(View.GONE);
				holderView.img_ranking.setVisibility(View.VISIBLE);
				holderView.img_ranking.setImageResource(R.drawable.two);
				break;
			case 2:
				holderView.tv_ranking.setVisibility(View.GONE);
				holderView.img_ranking.setVisibility(View.VISIBLE);
				holderView.img_ranking.setImageResource(R.drawable.three);
				break;
		}
		holderView.tv_team_performance.setText("兑换到"+exchangeBean.getBankname());
		holderView.tv_team_name.setText(exchangeBean.getAdd_time());
		holderView.tv_team_leavel.setText("-"+exchangeBean.getMoney());
		return convertView;
	}

	static class HolderView {

		@ViewInject(R.id.img_ranking)
		private ImageView img_ranking;

		@ViewInject(R.id.img_logo)
		private ImageView img_logo;

		@ViewInject(R.id.tv_ranking)
		private HandyTextView tv_ranking;

		@ViewInject(R.id.tv_team_name)
		private HandyTextView tv_team_name;

		@ViewInject(R.id.tv_team_leavel)
		private HandyTextView tv_team_leavel;

		@ViewInject(R.id.tv_team_performance)
		private HandyTextView tv_team_performance;
	}

}

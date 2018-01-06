package com.fengda.app.adapter;

import android.content.Context;
import android.text.TextPaint;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.fengda.app.R;
import com.fengda.app.bean.ExchangeBean;
import com.fengda.app.constant.MyConstant;
import com.fengda.app.utils.CommonUtils;
import com.fengda.app.utils.CompuUtils;
import com.fengda.app.utils.StringUtils;
import com.fengda.app.view.HandyTextView;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.List;

/**
 * 适配器
 * @author Administrator
 * 
 */
public class ExchangeListAdapter extends BaseAdapter {

	private HolderView holderView;

	private Context context;
	private LayoutInflater mInflater;
	private List<ExchangeBean> list;

	private int type;
	public ExchangeListAdapter(Context context) {
		this.mInflater = LayoutInflater.from(context);

		this.context = context;
	}
	public ExchangeListAdapter(Context context, List<ExchangeBean> list) {
		this.list = list;
		this.context = context;
		this.mInflater = LayoutInflater.from(context);

	}

	public void updateListview(List<ExchangeBean> list,int type) {
		this.list = list;
		this.type = type;
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
			convertView = mInflater.inflate(R.layout.adapter_pomelo_list_item, null);
			x.view().inject(holderView, convertView);
			convertView.setTag(holderView);
			// 可以对item设置不同的动画
			// convertView.setAnimation(AnimationUtils.loadAnimation(context,
			// R.anim.push_right_in));
		} else {
			holderView = (HolderView) convertView.getTag();
		}
		if (type == MyConstant.EXCHANGE_CASH || type == MyConstant.EXCHANGE_JIYOUMEI){
			holderView.tv_bank.setText("兑换到"+exchangeBean.getBankname());
			holderView.tv_money.setText("-"+exchangeBean.getMoney());
			holderView.ex_states.setText(exchangeBean.getRemark());
		}else if (type == MyConstant.EXCHANGE_TRANSFEN){
			String mobile = exchangeBean.getSendee_mobile().replaceAll("(\\d{3})\\d{4}(\\d{4})","$1****$2");
			holderView.tv_bank.setText(mobile);
			holderView.tv_money.setVisibility(View.INVISIBLE);
			holderView.ex_states.setVisibility(View.GONE);
			holderView.tv_ftuits.setVisibility(View.VISIBLE);
			holderView.tv_ftuits.setText("-"+exchangeBean.getMoney());
		}else if (type == MyConstant.EXCHANGE_VOTING){
			holderView.tv_bank.setText("-"+exchangeBean.getMoney()+"存果");
			TextPaint tp = holderView.tv_bank.getPaint();
			tp.setFakeBoldText(true);
			String fruits = CompuUtils.multiply(CompuUtils.subtract(exchangeBean.getMoney(),exchangeBean.getFees()).toString(),exchangeBean.getNum()).toString();
			holderView.ex_states.setVisibility(View.GONE);
			holderView.tv_money.setVisibility(View.INVISIBLE);
			holderView.ex_states.setVisibility(View.GONE);
			holderView.tv_ftuits.setVisibility(View.VISIBLE);
			holderView.tv_ftuits.setText("+"+fruits+"待产果");
		}else {
			holderView.tv_bank.setText("-"+exchangeBean.getMoney()+"存果");
		}
		holderView.tv_time.setText(exchangeBean.getAdd_time());
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

		@ViewInject(R.id.tv_ftuits)
		private HandyTextView tv_ftuits;

	}

}

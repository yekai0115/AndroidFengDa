package com.fengda.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.fengda.app.R;
import com.fengda.app.bean.BindCard;

import java.util.List;


public class BankAdapter extends BaseAdapter {

	private int mPosition;
	private List<BindCard> mActionItems;
	private Context context;
	private ViewHolder holder;
	private LayoutInflater mInflater;



	public BankAdapter(Context context, List<BindCard> mActionItems){
		this.context=context;
		this.mActionItems=mActionItems;
		this.mInflater= LayoutInflater.from(context);
	}


	public int getCount() {

		return mActionItems == null ? 0 : mActionItems.size();
	}

	public Object getItem(int position) {
		return mActionItems.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	private static class ViewHolder {
		private TextView text;
		private ImageView img_checked;
	}

	public View getView(final int arg0, final View arg1, ViewGroup arg2) {
		View view=arg1;
		holder=null;
		if(view==null){
			view= View.inflate(context, R.layout.title_pop_list_item, null);
			holder = new ViewHolder();
			holder.text=(TextView)view.findViewById(R.id.tv_text);
			holder.img_checked=(ImageView)view.findViewById(R.id.img_checked);
			view.setTag(holder);
		}else {
			holder = (ViewHolder) view.getTag();
		}
		holder.text.setText(mActionItems.get(arg0).getBankname());
		boolean check=mActionItems.get(arg0).isChecked();
		if (check){
			holder.img_checked.setVisibility(View.VISIBLE);
		}else{
			holder.img_checked.setVisibility(View.GONE);
		}

		return view;
	}
}

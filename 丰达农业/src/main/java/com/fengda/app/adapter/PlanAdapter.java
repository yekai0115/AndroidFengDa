package com.fengda.app.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.fengda.app.R;
import com.fengda.app.bean.AssemblingBase;
import com.fengda.app.bean.AssemblingPlan;
import com.fengda.app.bean.BindCard;
import com.fengda.app.constant.MyConstant;
import com.squareup.picasso.Picasso;

import java.util.List;


public class PlanAdapter extends BaseAdapter {

	private int mPosition;
	private List<AssemblingBase> mActionItems;
	private Context context;
	private ViewHolder holder;
	private LayoutInflater mInflater;



	public PlanAdapter(Context context, List<AssemblingBase> mActionItems){
		this.context=context;
		this.mActionItems=mActionItems;
		this.mInflater= LayoutInflater.from(context);
	}


	public int getCount() {

		return mActionItems == null ? 0 : mActionItems.get(0).getActivities().size();
	}

	public Object getItem(int position) {
		return mActionItems.get(0).getActivities().get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	private class ViewHolder {
		private TextView name;
		private ImageView logo;
	}

	public View getView(final int arg0, final View arg1, ViewGroup arg2) {
		View view=arg1;
		holder = null;
		if(view==null){
			view= View.inflate(context, R.layout.item_plan_list, null);
			holder = new ViewHolder();
			holder.name=(TextView)view.findViewById(R.id.tv_plan_name);
			holder.logo=(ImageView)view.findViewById(R.id.iv_plan_logo);
			view.setTag(holder);
		}else {
			holder = (ViewHolder) view.getTag();
		}
		List<AssemblingPlan> plans = mActionItems.get(0).getActivities();
		String plan = plans.get(arg0).getName();
		holder.name.setText(plan.toString());
		Picasso.with(context).load(mActionItems.get(0).getFronturl()+mActionItems.get(0).getActivities().get(arg0).getLogo()+ MyConstant.PIC_DPI2)
				//    .centerInside()
				.config(Bitmap.Config.RGB_565)
				.error(R.drawable.pic_nomal_loading_style)
				.into(holder.logo);
		return view;
	}
}

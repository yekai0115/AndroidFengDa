package com.fengda.app.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.fengda.app.R;
import com.fengda.app.adapter.OrderWuliuAdapter;
import com.fengda.app.api.APIService;
import com.fengda.app.api.RetrofitWrapper;
import com.fengda.app.bean.Logistics;
import com.fengda.app.bean.LogisticsBase;
import com.fengda.app.bean.LogisticsCompany;
import com.fengda.app.constant.MyConstant;
import com.fengda.app.fragment.MailOrderFrament;
import com.fengda.app.fragment.OrderLogisticsFrament;
import com.fengda.app.utils.DimenUtils;
import com.fengda.app.utils.FileUtils;
import com.fengda.app.utils.GsonUtil;
import com.fengda.app.utils.StringUtils;
import com.fengda.app.utils.ToastUtil;
import com.fengda.app.view.statelayout.StateLayout;
import com.fengda.app.widget.PagerSlidingTabStrip;
import com.fengda.app.widget.TopNvgBar5;
import com.squareup.picasso.Picasso;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.fengda.app.R.id.stateLayout;


/**
 * 描述 ：订单物流页面
 * 
 */
public class OrderWuliuActivity extends BaseActivity {




	/** 上下文 **/
	private Context mContext;

	@ViewInject(R.id.orderTabs)
	private PagerSlidingTabStrip tabs;

	@ViewInject(R.id.main_viewpager)
	private ViewPager pager;
	private int position;//当前滑动的导航栏状态

	private Intent intent;
	/**物流单号*/
	private String delivery_sn;

	/**订单号*/
	private String order_sn;
	/**
	 * 获取当前屏幕的密度
	 */
	private DisplayMetrics dm;

	private List<String> titles=new ArrayList<>();
	private List<String> delivery_sns=new ArrayList<>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.order_wuliu);
		mContext = this;
		x.view().inject(this);
		initViews();
		intent = getIntent();
		position = getIntent().getIntExtra("position",0);
		delivery_sn = intent.getStringExtra("delivery_sn");
		if(StringUtils.isBlank(delivery_sn)){

		}else{
			String[] arr = delivery_sn.split(",");
			for (int i=0;i<arr.length;i++){
				delivery_sns.add(arr[i]);
				titles.add("包裹"+(i+1));
			}
			order_sn = intent.getStringExtra("order_sn");
			dm = getResources().getDisplayMetrics();
			pager.setAdapter(new OrderWuliuActivity.MyPagerAdapter(getSupportFragmentManager()));
			if(titles.size()==1){
				tabs.setVisibility(View.GONE);
			}else{
				tabs.setViewPager(pager);
				setTabsValue();
			}
			pager.setCurrentItem(position);
			pager.setOffscreenPageLimit(2);
		}
	}


	@Override
	protected void initEvents() {

	}

	@Override
	protected void initViews() {

		TopNvgBar5 topNvgBar = (TopNvgBar5) findViewById(R.id.llytTitle);
		topNvgBar.setMyOnClickListener(new TopNvgBar5.MyOnClickListener() {
			@Override
			public void onLeftClick() {
					finish();
			}

			@Override
			public void onRightClick() {

			}
		});
	}



	@Override
	protected void onResume() {
		super.onResume();

	}

	@Override
	protected void onPause() {
		super.onPause();

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			finish();	
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

	}

	/**
	 * 对PagerSlidingTabStrip的各项属性进行赋值。
	 */
	private void setTabsValue() {
		// 设置Tab是自动填充满屏幕的
		tabs.setShouldExpand(true);
		// 设置Tab的分割线是透明的
		tabs.setDividerColor(Color.TRANSPARENT);
		// 设置Tab底部线的高度
		tabs.setUnderlineHeight((int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP, 0, dm));
		// 设置Tab Indicator的高度
		tabs.setIndicatorHeight((int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP, 3, dm));
		// 设置Tab标题文字的大小
		tabs.setTextSize((int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_SP, 14, dm));
		// 设置Tab Indicator的颜色
		tabs.setIndicatorColor(mContext.getResources().getColor(
				R.color.bg_main_bottom));
		// 设置选中Tab文字的颜色 (这是我自定义的一个方法)
		tabs.setSelectedTextColor(mContext.getResources().getColor(
				R.color.bg_main_bottom));
		// 取消点击Tab时的背景色
		tabs.setTabBackground(0);
	}
	public class MyPagerAdapter extends FragmentPagerAdapter {

		public MyPagerAdapter(FragmentManager fm) {
			super(fm);
		}


		@Override
		public CharSequence getPageTitle(int position) {
			return titles.get(position);
		}

		@Override
		public int getCount() {
			return titles.size();
		}

		@Override
		public Fragment getItem(int position) {
			String delivery_sn= delivery_sns.get(position);
			return OrderLogisticsFrament.newInstance(position,delivery_sn,order_sn);
		}

	}
}

package com.fengda.app.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fengda.app.R;
import com.fengda.app.common.KeyboardEnum;
import com.fengda.app.utils.StringUtils;

import java.util.ArrayList;

public class PayPasswordView {

	private RelativeLayout del;
	private TextView zero;
	private TextView one;
	private TextView two;
	private TextView three;
	private TextView four;
	private TextView five;
	private TextView sex;
	private TextView seven;
	private TextView eight;
	private TextView nine;

	private TextView box1;
	private TextView box2;
	private TextView box3;
	private TextView box4;
	private TextView box5;
	private TextView box6;

	private TextView title;
	private TextView content,exChange;
	private ImageView imageView;

	private ArrayList<String> mList = new ArrayList<String>();
	private View rootView;
	private OnPayListener listener;
	private Context mContext;

	private RelativeLayout rl_money;


	public PayPasswordView(String monney, String name, Context mContext, OnPayListener listener) {
		getDecorView(monney,name, mContext, listener);
	}

	public static PayPasswordView getInstance(String monney, String name, Context mContext, OnPayListener listener){
		return new PayPasswordView(monney,name, mContext, listener);
	}

	public void getDecorView(String monney, String name, Context mContext, final OnPayListener listener){
		this.listener = listener;
		this.mContext = mContext;
		rootView = LayoutInflater.from(mContext).inflate(R.layout.item_paypassword, null);
		exChange = (TextView) rootView.findViewById(R.id.exchange);
		del = (RelativeLayout) rootView.findViewById(R.id.pay_keyboard_del);
		zero = (TextView) rootView.findViewById(R.id.pay_keyboard_zero);
		one = (TextView) rootView.findViewById(R.id.pay_keyboard_one);
		two = (TextView) rootView.findViewById(R.id.pay_keyboard_two);
		three = (TextView) rootView.findViewById(R.id.pay_keyboard_three);
		four = (TextView) rootView.findViewById(R.id.pay_keyboard_four);
		five = (TextView) rootView.findViewById(R.id.pay_keyboard_five);
		sex = (TextView) rootView.findViewById(R.id.pay_keyboard_sex);
		seven = (TextView) rootView.findViewById(R.id.pay_keyboard_seven);
		eight = (TextView) rootView.findViewById(R.id.pay_keyboard_eight);
		nine = (TextView) rootView.findViewById(R.id.pay_keyboard_nine);
		box1 = (TextView) rootView.findViewById(R.id.pay_box1);

		box2 = (TextView) rootView.findViewById(R.id.pay_box2);
		box3 = (TextView) rootView.findViewById(R.id.pay_box3);
		box4 = (TextView) rootView.findViewById(R.id.pay_box4);
		box5 = (TextView) rootView.findViewById(R.id.pay_box5);
		box6 = (TextView) rootView.findViewById(R.id.pay_box6);
		title = (TextView) rootView.findViewById(R.id.pay_title);
		content = (TextView) rootView.findViewById(R.id.pay_amount);
		imageView = (ImageView) rootView.findViewById(R.id.pay_close);
		rl_money= (RelativeLayout) rootView.findViewById(R.id.rl_money);
		if(StringUtils.isBlank(monney)){
			rl_money.setVisibility(View.INVISIBLE);
		}
		if(StringUtils.isBlank(name)){
			exChange.setVisibility(View.GONE);
		}
		content.setText(monney);
		exChange.setText(name);
		View.OnClickListener onClickListener = new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				switch (v.getId()) {
					case R.id.pay_keyboard_del:
						parseActionType(KeyboardEnum.del);
						break;
					case R.id.pay_keyboard_zero:
						parseActionType(KeyboardEnum.zero);
						break;
					case R.id.pay_keyboard_one:
						parseActionType(KeyboardEnum.one);
						break;
					case R.id.pay_keyboard_two:
						parseActionType(KeyboardEnum.two);
						break;
					case R.id.pay_keyboard_three:
						parseActionType(KeyboardEnum.three);
						break;
					case R.id.pay_keyboard_four:
						parseActionType(KeyboardEnum.four);
						break;
					case R.id.pay_keyboard_five:
						parseActionType(KeyboardEnum.five);
						break;
					case R.id.pay_keyboard_sex:
						parseActionType(KeyboardEnum.sex);
						break;
					case R.id.pay_keyboard_seven:
						parseActionType(KeyboardEnum.seven);
						break;
					case R.id.pay_keyboard_eight:
						parseActionType(KeyboardEnum.eight);
						break;
					case R.id.pay_keyboard_nine:
						parseActionType(KeyboardEnum.nine);
						break;
					case R.id.pay_close:
						listener.onCancelPay();
						break;
				}

			}
		};
		imageView.setOnClickListener(onClickListener);
		del.setOnClickListener(onClickListener);
		zero.setOnClickListener(onClickListener);
		one.setOnClickListener(onClickListener);
		two.setOnClickListener(onClickListener);
		three.setOnClickListener(onClickListener);
		four.setOnClickListener(onClickListener);
		five.setOnClickListener(onClickListener);
		sex.setOnClickListener(onClickListener);
		seven.setOnClickListener(onClickListener);
		eight.setOnClickListener(onClickListener);
		nine.setOnClickListener(onClickListener);

		del.setOnLongClickListener(new View.OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				parseActionType(KeyboardEnum.longdel);
				return false;
			}
		});
	}

	private void parseActionType(KeyboardEnum type) {

		if(type.getType()== KeyboardEnum.ActionEnum.add){
			if(mList.size()<6){
				mList.add(type.getValue());
				updateUi();
				if(mList.size() == 6) {
					String pwd = "";
					for (int i = 0; i < mList.size(); i++) {
						pwd += mList.get(i);
					}
					listener.onSurePay(pwd);
				}
			}
		}else if(type.getType()== KeyboardEnum.ActionEnum.delete){
			if(mList.size()>0){
				mList.remove(mList.get(mList.size()-1));
				updateUi();
			}
		}
//		else if(type.getType()== KeyboardEnum.ActionEnum.cancel){
//			listener.onCancelPay();
//		}
//		else if(type.getType()== KeyboardEnum.ActionEnum.sure){
//			if(mList.size()<6){
//				Toast.makeText(mContext, "Input Less Than 6 char", Toast.LENGTH_SHORT).show();
//			}else{
//				String payValue="";
//				for (int i = 0; i < mList.size(); i++) {
//					payValue+=mList.get(i);
//				}
//				listener.onSurePay(payValue);
//			}
//		}
		else if(type.getType()== KeyboardEnum.ActionEnum.longClick){
			mList.clear();
			updateUi();
		}
	}

	private void updateUi() {
		if(mList.size()==0){
			box1.setText("");
			box2.setText("");
			box3.setText("");
			box4.setText("");
			box5.setText("");
			box6.setText("");
		}else if(mList.size()==1){
			box1.setText(mList.get(0));
			box2.setText("");
			box3.setText("");
			box4.setText("");
			box5.setText("");
			box6.setText("");
		}else if(mList.size()==2){
			box1.setText(mList.get(0));
			box2.setText(mList.get(1));
			box3.setText("");
			box4.setText("");
			box5.setText("");
			box6.setText("");
		}else if(mList.size()==3){
			box1.setText(mList.get(0));
			box2.setText(mList.get(1));
			box3.setText(mList.get(2));
			box4.setText("");
			box5.setText("");
			box6.setText("");
		}else if(mList.size()==4){
			box1.setText(mList.get(0));
			box2.setText(mList.get(1));
			box3.setText(mList.get(2));
			box4.setText(mList.get(3));
			box5.setText("");
			box6.setText("");
		}else if(mList.size()==5){
			box1.setText(mList.get(0));
			box2.setText(mList.get(1));
			box3.setText(mList.get(2));
			box4.setText(mList.get(3));
			box5.setText(mList.get(4));
			box6.setText("");
		}else if(mList.size()==6){
			box1.setText(mList.get(0));
			box2.setText(mList.get(1));
			box3.setText(mList.get(2));
			box4.setText(mList.get(3));
			box5.setText(mList.get(4));
			box6.setText(mList.get(5));
		}
	}

	public interface OnPayListener{
		void onCancelPay();
		void onSurePay(String password);
	}

	public View getView(){
		return rootView;
	}
}

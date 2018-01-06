package com.fengda.app.view.popwindow;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.text.Editable;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.fengda.app.R;
import com.fengda.app.bean.Specifications;
import com.fengda.app.constant.MyConstant;
import com.fengda.app.eventbus.MsgEvent4;
import com.fengda.app.model.GoodsBean;
import com.fengda.app.utils.StringUtils;
import com.fengda.app.utils.ToastUtil;
import com.fengda.app.view.flowlayout.FlowLayout;
import com.fengda.app.view.flowlayout.TagAdapter;
import com.fengda.app.view.flowlayout.TagFlowLayout;

import org.greenrobot.eventbus.EventBus;

import java.util.List;
import java.util.Set;

import static com.fengda.app.R.id.tv_price;


/**
 * @autho Administrator
 */
public class BuyPopup extends PopupWindow implements OnClickListener {

    // 关闭按钮
    private ImageView imgClose;
    private ImageView imageView;
    private View buy_close_view;
    private TextView btn_jian, btn_jia;
    private Button btn_sure;
    private TextView tv_guige, tv_kucun;
    private TextView buy_num;
    private TagFlowLayout id_flowlayout;
    /**
     * 购买数量
     */
    private int num;
    /**
     * 单价
     */
    private TextView tv_danjia;
    private TextView tv_point;
    private TextView tv_member_price;
    private LinearLayout ll_point;




    private int goods_type;
    /**
     * 购买数量
     */
    private String number;
    private List<Specifications> list;
    private int cuPosition;
    private String select_attr_id;
    /**
     * 1选规格；
     * 2立即购买
     * 3加入购物车
     */
    private int type;
    private Specifications specifications;
    /**
     * 商品库存
     */
    private int stock_quantity;
    private Context context;

    public BuyPopup(Context context) {
        super(context);
    }

    /**
     * @param context
     * @param contentView
     */
    public BuyPopup(Context context, View contentView, final GoodsBean goodsBean, int position, String attr_id, int type) {
        super(contentView);
        this.context = context;
        this.cuPosition = position;
        this.type = type;
        this.buy_close_view = (View) contentView.findViewById(R.id.buy_close_view);
        this.imgClose = (ImageView) contentView.findViewById(R.id.imgClose);
        imageView = (ImageView) contentView.findViewById(R.id.imageView);
        this.btn_jian = (TextView) contentView.findViewById(R.id.tv_num_jia);
        this.btn_jia = (TextView) contentView.findViewById(R.id.tv_num_jian);
        this.btn_sure = (Button) contentView.findViewById(R.id.btn_sure);
        this.tv_guige = (TextView) contentView.findViewById(R.id.tv_guige);
        this.tv_kucun = (TextView) contentView.findViewById(R.id.tv_kucun);
        this.buy_num = (TextView) contentView.findViewById(R.id.tv_buy_num);
        this.tv_danjia = (TextView) contentView.findViewById(R.id.tv_danjia);
        tv_point = (TextView) contentView.findViewById(R.id.tv_point);
        ll_point= (LinearLayout) contentView.findViewById(R.id.ll_point);
        tv_member_price = (TextView) contentView.findViewById(R.id.tv_member_price);
        this.id_flowlayout = (TagFlowLayout) contentView.findViewById(R.id.id_flowlayout);
        list = goodsBean.getAttr_detail();
        goods_type = goodsBean.getGoods_type();
        if (null != list && !list.isEmpty()) {
            final LayoutInflater mInflater = LayoutInflater.from(context);
            id_flowlayout.setMaxSelectCount(1);//设置为单选
            if (!StringUtils.isBlank(attr_id)) {
                specifications = list.get(position);
                id_flowlayout.setSelectCurrent(cuPosition);
                stock_quantity = specifications.getAttr_number();
                if (goods_type == 1) {
//                    tv_danjia.setText(specifications.getPrice_cost());
//                    String point = specifications.getAttr_point();//不显示积分
//                    if (StringUtils.isBlank(point)) {
//                        point="0";
//                    }
                    tv_danjia.setText(specifications.getAttr_price());
//                    tv_point.setText( point);
                } else {//智品商品
//                    ll_point.setVisibility(View.GONE);
//                    tv_danjia.setText(specifications.getAttr_price());
                }
                tv_guige.setText(specifications.getAttr_value());
                tv_kucun.setText(stock_quantity + "");
                select_attr_id = specifications.getGoods_attr_id();
            } else {
                tv_kucun.setText(goodsBean.getGoods_number());
                stock_quantity = Integer.valueOf(goodsBean.getGoods_number());
                if (goods_type == 1) {
                    tv_danjia.setText(goodsBean.getPrice());
                } else {
//                    ll_point.setVisibility(View.GONE);
//                    tv_danjia.setText(goodsBean.getPrice());
                }

            }
            id_flowlayout.setAdapter(new TagAdapter(list) {
                @Override
                public View getView(FlowLayout parent, int position, Object o) {
                    TextView tv = (TextView) mInflater.inflate(R.layout.tag_view2, id_flowlayout, false);
                    Specifications specifications = list.get(position);
                    tv.setText(specifications.getAttr_value());
                    return tv;
                }
            });
            id_flowlayout.setOnTagClickListener(new TagFlowLayout.OnTagClickListener() {
                @Override
                public boolean onTagClick(View view, int position, FlowLayout parent) {

                    return false;
                }
            });

            id_flowlayout.setOnSelectListener(new TagFlowLayout.OnSelectListener() {
                @Override
                public void onSelected(Set<Integer> selectPosSet) {
                    if (selectPosSet.isEmpty()) {
                        if (goods_type == 1) {
                            tv_danjia.setText(goodsBean.getPrice());
//                            tv_danjia.setText(goodsBean.getPrice_cost());
//                            tv_member_price.setText(goodsBean.getPrice());
                        } else {
//                            ll_point.setVisibility(View.GONE);
//                            tv_danjia.setText(goodsBean.getPrice());
                        }
//                        String point = goodsBean.getPoint();//不显示积分
//                        if (StringUtils.isBlank(point)) {
//                            point="0";
//                        }
//                        tv_point.setText( point);
                        tv_guige.setText("请选择规格");
                        tv_kucun.setText(goodsBean.getGoods_number());
                        stock_quantity = Integer.valueOf(goodsBean.getGoods_number());
                        select_attr_id = "";
                        specifications = null;
                    } else {
                        for (Integer index : selectPosSet) {
                            cuPosition = index;
                            specifications = list.get(index);
                            stock_quantity = specifications.getAttr_number();
                            if (goods_type == 1) {
                                tv_danjia.setText(specifications.getAttr_price());
//                                String attrPoint = specifications.getAttr_point();//不显示积分
//                                if (!StringUtils.isBlank(attrPoint)) {
//                                    tv_danjia.setText(specifications.getAttr_price()+"+"+attrPoint+"积分");
//                                }
//                                tv_member_price.setText(specifications.getAttr_price());
                            } else {
//                                ll_point.setVisibility(View.GONE);
//                                tv_danjia.setText(specifications.getAttr_price());
                            }
                            tv_guige.setText(specifications.getAttr_value());
                            tv_kucun.setText(stock_quantity + "");
                            select_attr_id = specifications.getGoods_attr_id();
                            String point = specifications.getAttr_point();//不显示积分
                            if (StringUtils.isBlank(point)) {
                                point="0";
                            }
//                            tv_point.setText( point);
                        }
                    }

                }

            });

        }
        String goods_logo = goodsBean.getGoods_logo();
        String[] arr = goods_logo.split(",");
        Glide.with(context).load(MyConstant.ALI_PUBLIC_URL + arr[0]).fitCenter()
                //  .override(width,DimenUtils.dip2px(context,130))
                .placeholder(R.drawable.pic_nomal_loading_style)
                .error(R.drawable.pic_nomal_loading_style)
                .into(imageView);
        CharSequence text = buy_num.getText();// 光标显示在内容右边
        if (text instanceof Spannable) {
            Spannable spanText = (Spannable) text;
            Selection.setSelection(spanText, text.length());
        }
        this.btn_jian.setOnClickListener(this);
        this.btn_jia.setOnClickListener(this);
        this.btn_sure.setOnClickListener(this);
        this.imgClose.setOnClickListener(this);
        this.buy_close_view.setOnClickListener(this);
        // 设置属性
        setProperty();
        buy_num.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {

                CharSequence text = buy_num.getText();// 光标显示在内容右边
                if (text instanceof Spannable) {
                    Spannable spanText = (Spannable) text;
                    Selection.setSelection(spanText, text.length());
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                number = s.toString();
                if (StringUtils.isBlank(number)) {
                    num = 1;
                } else {
                    num = Integer.parseInt(number);
                }
            }
        });
    }

    public interface OnConfirmClickedListener {
        void onConfirmClick(Specifications s, String num, int position);
    }

    /**
     * 设置属性
     */
    private void setProperty() {

        setWidth(LayoutParams.MATCH_PARENT);
        setHeight(LayoutParams.MATCH_PARENT);
        setFocusable(true);
        setOutsideTouchable(true);
        setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);// 为edittext设置的
        ColorDrawable dw = new ColorDrawable(context.getResources().getColor(R.color.status_view));
        setAnimationStyle(R.style.AnimBottom);
        setBackgroundDrawable(dw);
    }

    @Override
    public void onClick(View v) {
        try {
            switch (v.getId()) {
                case R.id.tv_num_jian:
                    number = buy_num.getText().toString();
                    if (StringUtils.isBlank(number)) {
                        num = 1;
                    } else {
                        num = Integer.parseInt(number);
                    }
                    if (num == 1) {
                        num = 1;
                        ToastUtil.showToast(context, "不能再减了");
                    } else {
                        num--;
                    }
                    buy_num.setText(num + "");
                    break;
                case R.id.tv_num_jia:
                    number = buy_num.getText().toString();
                    if (StringUtils.isBlank(number)) {
                        num = 1;
                    } else {
                        num = Integer.parseInt(number);
                    }
                    if (num == stock_quantity) {
                        ToastUtil.showToast(context, "不能超过库存");
                    } else {
                        num++;
                    }
                    buy_num.setText(num + "");
                    break;
                case R.id.btn_sure://确定
                    number = buy_num.getText().toString();
                    if (StringUtils.isBlank(number) || number.equals("0")) {
                        ToastUtil.showToast(context, "请选择商品数量");
                    } else {
                        EventBus.getDefault().post(new MsgEvent4(select_attr_id, specifications, number, cuPosition, type));
                    }
                    break;
                case R.id.imgClose:
                    number = buy_num.getText().toString();
                    EventBus.getDefault().post(new MsgEvent4(select_attr_id, specifications, number, cuPosition, 1));
                    dismiss();
                    break;
                case R.id.buy_close_view:
                    EventBus.getDefault().post(new MsgEvent4(select_attr_id, specifications, number, cuPosition, 1));
                    dismiss();
                    break;
                default:
                    break;
            }
        } catch (Exception e) {

        }
    }
}

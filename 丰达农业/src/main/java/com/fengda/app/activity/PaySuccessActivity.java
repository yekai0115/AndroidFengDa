package com.fengda.app.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import com.fengda.app.R;
import com.fengda.app.application.MyApplication;
import com.fengda.app.widget.TopNvgBar5;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

/**
 * 购买成功
 */

public class PaySuccessActivity extends BaseActivity {


    @ViewInject(R.id.tv_order_money)
    private TextView tv_order_money;



    private String pay_money;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_success);
        x.view().inject(this);
        pay_money = getIntent().getStringExtra("pay_money");
        initViews();
        initEvents();
    }

    @Override
    protected void initViews() {
        TopNvgBar5 topNvgBar = (TopNvgBar5) findViewById(R.id.top_nvg_bar);
        topNvgBar.setMyOnClickListener(new TopNvgBar5.MyOnClickListener() {
            @Override
            public void onLeftClick() {
                finish();
            }

            @Override
            public void onRightClick() {

            }
        });
        tv_order_money.setText(pay_money);

    }

    @Override
    protected void initEvents() {
        TopNvgBar5 topNvgBar = (TopNvgBar5) findViewById(R.id.top_nvg_bar);
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

    @Event({R.id.tv_shouye})
    private void click(View view) {
        switch (view.getId()) {
            case R.id.tv_shouye://返回首页
                MyApplication.finishSingleActivityByClass(SearchActivity.class);
                MyApplication.finishSingleActivityByClass(CategoryGoodsListActivity.class);
                MyApplication.finishSingleActivityByClass(GoodsListActivity.class);
                MyApplication.finishSingleActivityByClass(ShoppingCarActivity.class);
                finish();
                break;
            default:
                break;
        }
    }
}

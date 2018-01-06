package com.fengda.app.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;

import com.fengda.app.R;
import com.fengda.app.api.APIService;
import com.fengda.app.api.RetrofitWrapper;
import com.fengda.app.bean.BaseResponse;
import com.fengda.app.constant.MyConstant;
import com.fengda.app.eventbus.LoginMsgEvent;
import com.fengda.app.http.HttpCallBack;
import com.fengda.app.model.Message;
import com.fengda.app.utils.EncodeUtils;
import com.fengda.app.utils.SPUtils;
import com.fengda.app.utils.StringUtils;
import com.fengda.app.utils.ToastUtil;
import com.fengda.app.view.HandyTextView;
import com.fengda.app.view.pullableview.PullableListView;
import com.fengda.app.view.pulltorefresh.PullLayout;
import com.fengda.app.view.pulltorefresh.PullToRefreshLayout;
import com.fengda.app.view.statelayout.StateLayout;
import com.fengda.app.widget.TopNvgBar5;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by 24448 on 2017/10/20.
 */

public class MyMessageDetailActivity extends BaseActivity{

    private List<Message> messageList = new ArrayList<>();
    @ViewInject(R.id.message_content)
    private HandyTextView message_content;

    @ViewInject(R.id.message_send_name)
    private HandyTextView message_send_name;

    @ViewInject(R.id.message_send_time)
    private HandyTextView message_send_time;
    /**
     * 上下文
     **/
    private Context mContext;
    private String token;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acticity_message_detail);
        mContext = this;
        x.view().inject(this);
        initViews();
        initDialog();
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
        Message message = (Message)getIntent().getSerializableExtra("message");
        message_content.setText(message.getContent());
        message_send_name.setText(message.getTitle());
        message_send_time.setText(message.getAdd_time());
    }

    @Override
    protected void initEvents() {
        messageList.clear();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}

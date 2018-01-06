package com.fengda.app.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.fengda.app.R;
import com.fengda.app.constant.MyConstant;
import com.fengda.app.db.SearchHistoryTable;
import com.fengda.app.db.XDBUtil;
import com.fengda.app.utils.DateUtil;
import com.fengda.app.utils.DimenUtils;
import com.fengda.app.utils.KeyBoardUtils;
import com.fengda.app.utils.SPUtils;
import com.fengda.app.utils.StringUtils;
import com.fengda.app.view.flowlayout.FlowLayout;
import com.fengda.app.view.flowlayout.TagAdapter;
import com.fengda.app.view.flowlayout.TagFlowLayout;
import com.fengda.app.view.slide.Slidr;
import com.fengda.app.view.slide.SlidrConfig;
import com.fengda.app.view.slide.SlidrPosition;

import org.xutils.DbManager;
import org.xutils.DbManager.DaoConfig;
import org.xutils.ex.DbException;
import org.xutils.view.annotation.Event;
import org.xutils.x;

import java.util.Date;
import java.util.List;

/**
 * 搜索页面
 */
public class SearchActivity extends BaseActivity {

    private EditText ed_search;
    private TextView tv_cancle;

    private TagFlowLayout id_flowlayout;

    private DaoConfig daoConfig;
    private DbManager dbManager;
    private Context mContext;
    private String phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_search);
        mContext = this;
        x.view().inject(this);
        initViews();
        if (MyConstant.HASLOGIN) {
            phone = (String) SPUtils.get(mContext, "phone", "");
        } else {
            phone = MyConstant.VISTER;
        }
        daoConfig = XDBUtil.getDaoConfig();
        dbManager = x.getDb(daoConfig);
        // 查询搜索历史
        querySearchHistory();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }


    @Override
    protected void initViews() {
        ed_search = (EditText) findViewById(R.id.ed_search);
        tv_cancle = (TextView) findViewById(R.id.tv_cancle);

        id_flowlayout = (TagFlowLayout) findViewById(R.id.id_flowlayout);
        ed_search.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
        ed_search.setInputType(EditorInfo.TYPE_CLASS_TEXT);
        ed_search.setSingleLine(true);
        /**
         * 键盘搜索键直接点击搜索
         */
        ed_search.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if (actionId == EditorInfo.IME_ACTION_SEARCH || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_SEARCH))

                {
                    String inputWord = ed_search.getText().toString();
                    KeyBoardUtils.closeKeybord(ed_search, mContext);
                    if (!StringUtils.isBlank(inputWord)) {
                        clickInsert(1, inputWord);
                    }
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    protected void initEvents() {

    }


    private void setSlidr() {
        int primary = getResources().getColor(R.color.toming);
        int secondary = getResources().getColor(R.color.accent);
        SlidrConfig config = new SlidrConfig.Builder().primaryColor(primary)
                .secondaryColor(secondary).position(SlidrPosition.LEFT)
                .touchSize(DimenUtils.dip2px(mContext, 60)).build();
        // Attach the Slidr Mechanism to this activity
        Slidr.attach(this, config);

    }


    @Event({R.id.ed_search, R.id.tv_cancle, R.id.img_clear})
    private void onClick(View v) {
        switch (v.getId()) {
            case R.id.ed_search:

                break;
            case R.id.tv_cancle:
                finish();
                break;
            case R.id.img_clear://清除历史记录
                clearSearchHistory();
                break;

        }
    }

    /**
     * 查询搜索历史
     */
    List<SearchHistoryTable> oftenCategories;

    private void querySearchHistory() {
        try {
            //	List<OftenCategoryTable> oftenCategories = dbManager.selector(OftenCategoryTable.class).orderBy("searchtime", true).where("user", "=", user_id).limit(8).findAll();
            oftenCategories = dbManager.selector(SearchHistoryTable.class).orderBy("searchtime", true).limit(50).findAll();//不区分用户
            if (null != oftenCategories) {
                final LayoutInflater mInflater = LayoutInflater.from(mContext);
                id_flowlayout.setAdapter(new TagAdapter(oftenCategories) {
                    @Override
                    public View getView(FlowLayout parent, int position, Object o) {
                        TextView tv = (TextView) mInflater.inflate(R.layout.tag_view, id_flowlayout, false);
                        SearchHistoryTable historyTable = oftenCategories.get(position);
                        tv.setText(historyTable.getTitle());
                        return tv;
                    }
                });
            }

            id_flowlayout.setOnTagClickListener(new TagFlowLayout.OnTagClickListener() {
                @Override
                public boolean onTagClick(View view, int position, FlowLayout parent) {
                    SearchHistoryTable historyTable = oftenCategories.get(position);
                    Intent intent = new Intent(mContext, GoodsListActivity.class);
                    intent.putExtra("title", historyTable.getTitle());
                    startActivity(intent);

                    return false;
                }
            });

        } catch (DbException e) {
            e.printStackTrace();
        }

    }

    /**
     * 将点击的类目数据库加入搜索历史表
     */
    private void clickInsert(int type, String title) {
        try {
            String time = DateUtil.formatYmdsfm(new Date());
            SearchHistoryTable oftenCategory = new SearchHistoryTable();
            oftenCategory.setTitle(title);
            oftenCategory.setType(type);
            oftenCategory.setSearchtime(time);
            oftenCategory.setUser(phone);
            List<SearchHistoryTable> oftenCategories = dbManager.selector(SearchHistoryTable.class).where("user", "=", phone).and("type", "=", type).and("title", "=", title).findAll();
            if (null == oftenCategories || oftenCategories.isEmpty()) {//查询是否已存在
                dbManager.saveOrUpdate(oftenCategory);
                querySearchHistory();
            }
            Intent intent = new Intent(mContext, GoodsListActivity.class);
            intent.putExtra("title", title);
            startActivity(intent);
        } catch (DbException e) {
            e.printStackTrace();
        }

    }

    /**
     * 清空历史记录
     */
    private void clearSearchHistory() {

        try {
            dbManager.delete(SearchHistoryTable.class);
            id_flowlayout.removeAllViews();
        } catch (DbException e) {

            e.printStackTrace();
        }

    }

    private void myStartActivity(Class classObj) {
        startActivity(new Intent(mContext, classObj));
    }

}

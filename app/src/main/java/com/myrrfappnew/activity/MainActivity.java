package com.myrrfappnew.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.myrrfappnew.BuildConfig;
import com.myrrfappnew.R;
import com.myrrfappnew.bean.WorkInfo;
import com.myrrfappnew.fragment.MyFragmentManger;
import com.myrrfappnew.seriver.SyncService;
import com.myrrfappnew.utils.AppUtils;
import com.myrrfappnew.utils.DbHelper;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, RadioGroup.OnCheckedChangeListener {

    private LinearLayout activityMain, layoutSn, layoutBack, layoutSearch, layoutCrn, layoutAddress;
    private RadioGroup radioGroup;
    private RadioButton radioWhiteHead;
    private RadioButton radioNotGo;
    private RadioButton radioNotComplete;
    private RadioButton radioComplete;
    private RadioButton radioLog;
    private FragmentManager fManager;
    private ImageView ivArrow, ivAdd;
    private EditText editSearch;

    private TextView titleTv, tvExplain, logBtn, tvSN, tvCrn, tvAddress, tvRefresh, tvMatch;
    private ImageView ivLogo, ivSearch;
    private int lastOutBtnState;//進入log頁面前的outBtnState;
    private int outBtnState;//outBtn類別，1.log頁面 2.work頁面,3.未完成頁面 4.添加白头单
    private Fragment lastFragment;//進入log頁面前的一個fragment頁面
    private int lastRadioClickId = -1;//進入work頁面前的RadioButton選中的ID;
    private int logLastRadioClickId = -1;//進入log頁面前radiobutton選中的ID;

    private boolean isLog = false;//是否在log頁面
    private boolean isInit = true;//是否初始化
    private String workId;//id
    //记录当前Fragment, -1是白头单 0是未到场 1是未完工 2是完工 3是工作日志 4是签到  -->index还与WorkInfo的state有一定关联
    public static int index = -1; //用来标记在哪个fragment页面
    private WorkInfo info;
    public static TextView countView;
    private Intent intent;  //开启后台上传日志的意图

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init(); //初始化一些视图
//        CheckVersion.getInstance(this).checkVersion(); //检查版本是否可以更新
        //开启后台上传 --->没有上传的数据进行上传
        intent = new Intent(this, SyncService.class);
        startService(intent);
        DbHelper.getInstance().findAll();
//        getSupportFragmentManager().beginTransaction().add(R.id.content_layout, WorkFragment.getInstance("53860030")).commit();
        MyFragmentManger.showRootFragment(this, -1);
    }

    private void init() {
        countView = (TextView) findViewById(R.id.tv_count);
        countView.setVisibility(View.GONE);
        ivLogo = (ImageView) findViewById(R.id.iv_logo);
        titleTv = (TextView) findViewById(R.id.title_tv);
        if (BuildConfig.API_ENV_PRODUCTION.equals("951")) {
            titleTv.setText(getString(R.string.title_951, AppUtils.getVersion(this)));
            ivLogo.setImageResource(R.drawable.ic_logo951);
        } else if (BuildConfig.API_ENV_PRODUCTION.equals("931")) {
            titleTv.setText(getString(R.string.title, AppUtils.getVersion(this)));
            ivLogo.setImageResource(R.drawable.ic_logo931);
        } else {
            titleTv.setText("dev-测试");
            ivLogo.setImageResource(R.drawable.ic_logo931);
        }
        tvExplain = (TextView) findViewById(R.id.tv_explain);
        tvExplain.setOnClickListener(this);
        tvRefresh = (TextView) findViewById(R.id.tv_refresh);
        tvRefresh.setOnClickListener(this);
        activityMain = (LinearLayout) findViewById(R.id.activity_main);
        radioGroup = (RadioGroup) findViewById(R.id.radio_group);
        radioWhiteHead = (RadioButton) findViewById(R.id.radio_white_head);
        radioNotGo = (RadioButton) findViewById(R.id.radio_not_go);
        radioNotComplete = (RadioButton) findViewById(R.id.radio_not_complete);
        radioComplete = (RadioButton) findViewById(R.id.radio_complete);
        radioLog = (RadioButton) findViewById(R.id.radio_log);
        radioGroup.setOnCheckedChangeListener(this);
        radioWhiteHead.setChecked(true);
        ivArrow = (ImageView) findViewById(R.id.iv_arrow);
        layoutSn = (LinearLayout) findViewById(R.id.layout_sn);
        tvSN = (TextView) findViewById(R.id.tv_sn);
        tvSN.setText(AppUtils.getDeviceID(this));
        layoutSearch = (LinearLayout) findViewById(R.id.layout_search);
        ivSearch = (ImageView) findViewById(R.id.iv_search);
        ivSearch.setOnClickListener(this);
        editSearch = (EditText) findViewById(R.id.edit_search);
        layoutCrn = (LinearLayout) findViewById(R.id.layout_crn);
        tvCrn = (TextView) findViewById(R.id.tv_crn);
        layoutAddress = (LinearLayout) findViewById(R.id.layout_address);
        tvAddress = (TextView) findViewById(R.id.tv_address);

        ivAdd = (ImageView) findViewById(R.id.iv_add);
        ivAdd.setOnClickListener(this);
        tvMatch = (TextView) findViewById(R.id.tv_match);
        editSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_SEARCH) {
                    // 当按了搜索之后关闭软键盘
                    AppUtils.hideKeyboard(MainActivity.this, editSearch);
                    search();
                    return true;
                }
                return false;
            }
        });
        radioWhiteHead.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                ivArrow.setTranslationX(radioWhiteHead.getX() + radioWhiteHead.getWidth() / 2 - ivArrow.getWidth() / 2);
                radioWhiteHead.getViewTreeObserver()
                        .removeGlobalOnLayoutListener(this);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        AppUtils.hideKeyboard(MainActivity.this, editSearch);
    }

    private void search() {
        MyFragmentManger.search(editSearch.getText().toString());
    }

    @Override
    public void onClick(View v) { // 点击事件
        switch (v.getId()) {
            case R.id.iv_search:    //点击搜索
                search();
                AppUtils.hideKeyboard(this, editSearch);
                break;
            case R.id.tv_explain:   //点击说明 -->跳转到说明页面
                startActivity(new Intent(this, ExplainActivity.class));
                break;
            case R.id.tv_refresh:     // 点击刷新页面
                MyFragmentManger.fragmentRefresh();
                break;
            case R.id.iv_add:  //点击添加白头单
                MyFragmentManger.showFragment(this, 7, null);
                break;
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        editSearch.setText("");
        countView.setVisibility(View.VISIBLE); //显示查询的数据条目
        tvMatch.setVisibility(View.GONE); //配对白头单
        ivAdd.setVisibility(View.GONE); //添加白头单
        if (checkedId == radioWhiteHead.getId()) {
            countView.setVisibility(View.GONE);
            ivArrow.setTranslationX(radioWhiteHead.getX() + radioWhiteHead.getWidth() / 2 - ivArrow.getWidth() / 2);
            index = -1;
            tvMatch.setVisibility(View.VISIBLE);
            ivAdd.setVisibility(View.VISIBLE);
        } else if (checkedId == radioNotGo.getId()) {
            isInit = false;
            ivArrow.setTranslationX(radioNotGo.getX() + radioNotGo.getWidth() / 2 - ivArrow.getWidth() / 2);
            index = 0;
        } else if (checkedId == radioNotComplete.getId()) {
            isInit = false;
            ivArrow.setTranslationX(radioNotComplete.getX() + radioNotComplete.getWidth() / 2 - ivArrow.getWidth() / 2);
            index = 1;
        } else if (checkedId == radioComplete.getId()) {
            isInit = false;
            ivArrow.setTranslationX(radioComplete.getX() + radioComplete.getWidth() / 2 - ivArrow.getWidth() / 2);
            index = 2;
        } else {
            countView.setVisibility(View.GONE);
            ivArrow.setTranslationX(radioLog.getX() + radioLog.getWidth() / 2 - ivArrow.getWidth() / 2);
            index = 3;
        }
        MyFragmentManger.showRootFragment(this, index); //显示一个Fragment
        layoutSearch.setVisibility(View.VISIBLE);
        layoutCrn.setVisibility(View.GONE);
        layoutAddress.setVisibility(View.GONE);
        tvMatch.setVisibility(View.GONE);
    }

    @Override
    public void onBackPressed() {
        if (index > 3 || index < -1) { //不在根页面
            if (index == 4) { //在签到页面
                MyFragmentManger.showFragment(this, MyFragmentManger.rootInterface, MyFragmentManger.id);
            } else if (index == 5) {
                MyFragmentManger.showFragment(this, 4, MyFragmentManger.id);
            }
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (intent != null) {
            stopService(intent);
        }
        System.exit(0);
    }
}

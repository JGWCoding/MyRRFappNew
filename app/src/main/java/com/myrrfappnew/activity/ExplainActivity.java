package com.myrrfappnew.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.myrrfappnew.R;


/**
 * Created by Administrator on 2017/2/23.
 */
public class ExplainActivity extends Activity implements OnClickListener {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explain);
        WebView web = (WebView) findViewById(R.id.web);
        web.setVerticalScrollbarOverlay(true); //指定的垂直滚动条有叠加样式
        WebSettings webSettings = web.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setUseWideViewPort(true);//设定支持viewport
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setSupportZoom(true); //设置支持手势缩放
        webSettings.setBuiltInZoomControls(true);
        webSettings.setDisplayZoomControls(false);//设定缩放控件隐藏
        try {
            Intent intent = getIntent();
            String jpg = intent.getStringExtra("jpg");
            if (jpg!=null){ web.loadUrl("file:///"+ Environment.getExternalStorageDirectory().getAbsolutePath() + "/Sdf/"+jpg);}
            else {
                web.loadUrl("file:///android_asset/explain.jpg");
            }
        }catch (Exception e) {
            Log.e("===", "onCreate: "+"----");
        }
//        iv = (LargeImageView) findViewById(R.id.iv);
//        try
//        {
//            InputStream inputStream = getAssets().open("explain.jpg");
//            iv.setInputStream(inputStream);
//            Log.e("====", "onCreate: "+"cuole" );
//
//        } catch (IOException e)
//        {
//            e.printStackTrace();
//        }
    }

    @Override
    public void onClick(View view) {
        finish();
    }
}

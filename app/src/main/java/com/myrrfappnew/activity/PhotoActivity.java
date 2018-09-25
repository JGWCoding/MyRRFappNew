package com.myrrfappnew.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver;

import com.myrrfappnew.R;
import com.myrrfappnew.adapter.ImageViewPagerAdapter;
import com.myrrfappnew.view.MyViewPager;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/2/23.
 */
public class PhotoActivity extends Activity implements OnClickListener {


    private ViewPager viewPager;
    int index;
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);

        viewPager = (MyViewPager) findViewById(R.id.viewpager);
        Intent intent = getIntent();
        if (intent!=null)  {
            String path = intent.getStringExtra("path");
            ArrayList<String> imgs = intent.getStringArrayListExtra("paths");
            //TODO 设置ViewPager加载图片
            viewPager.setAdapter(new ImageViewPagerAdapter(this,imgs));
            for (int i = 0; i < imgs.size() ; i++) {
                if(imgs.get(i).equals(path)) {
                    index = i;
                }
            }
            viewPager.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    viewPager.setCurrentItem(index);
                    viewPager.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }
            });
        }
    }

    @Override
    public void onClick(View view) {
        finish();
    }
}

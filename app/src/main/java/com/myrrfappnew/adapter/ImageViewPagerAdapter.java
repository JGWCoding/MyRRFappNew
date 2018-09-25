package com.myrrfappnew.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.myrrfappnew.activity.PhotoActivity;

import java.util.ArrayList;
import java.util.List;

import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * 这是图片相册的Adapter
 */

public class ImageViewPagerAdapter extends PagerAdapter {
    private ArrayList<View> viewList;
    Context context;
    public ImageViewPagerAdapter(Context context, List<String> list) {
        this.context = context;
        viewList = new ArrayList<View>();
        for (int i = 0; i < list.size() ; i++) {
        viewList.add(createPagerImageView(list.get(i)));
        }
    }

    @Override
    public int getCount() {
        if (viewList==null) return 0;
        return viewList.size();
    }
    private PhotoViewAttacher photoViewAttacher;

    private View createPagerImageView(String path){
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        LinearLayout layout = new LinearLayout(context);
        layout.setLayoutParams(params);
        layout.setOrientation(LinearLayout.HORIZONTAL);
        ImageView imageView = new ImageView(context);
        imageView.setLayoutParams(params);
        imageView.setScaleType(ImageView.ScaleType.CENTER);
        Bitmap bitmap = BitmapFactory.decodeFile(path);
        imageView.setImageBitmap(bitmap);
        photoViewAttacher = new PhotoViewAttacher(imageView);//用来多点触控的
        photoViewAttacher.update();
        photoViewAttacher.setOnDoubleTapListener(new GestureDetector.OnDoubleTapListener() {
            @Override
            public boolean onSingleTapConfirmed(MotionEvent motionEvent) {
                ((PhotoActivity)(context)).finish();
                return false;
            }

            @Override
            public boolean onDoubleTap(MotionEvent motionEvent) {
                return false;
            }

            @Override
            public boolean onDoubleTapEvent(MotionEvent motionEvent) {
                return false;
            }
        });
        layout.addView(imageView);
        return layout;
    }
    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view==object;
    }
    @Override     //初始化ItemView
    public Object instantiateItem(ViewGroup container, int position) {
        ((ViewPager) container).addView(viewList.get(position),0);
        return viewList.get(position);
    }

    @Override     //销毁ItemView
    public void destroyItem(View container, int position, Object object) {
        ((ViewPager) container).removeView(viewList.get(position));
    }
}

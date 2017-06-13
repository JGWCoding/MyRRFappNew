package com.myrrfappnew.utils;

import android.content.Context;
import android.support.annotation.StringRes;
import android.util.Log;
import android.widget.Toast;

/**Toast工具类
 * Created by Administrator on 2016/11/29.
 */

public class ToastUtils {
    static Toast ts;
    static Context mContext;
    static boolean mIsShowToast;

    public static void init(Context context,boolean isShowToast){
        mContext = context;
        mIsShowToast = isShowToast;
    }

    public static void showToast(final String msg){
        if (mContext == null){
            Log.e("showToastError","Please first initialization in the Application");
            return;
        }
        if (!mIsShowToast) return;
        if(Thread.currentThread()==mContext.getMainLooper().getThread()) {
            if (ts == null) {
                ts = Toast.makeText(mContext, msg, Toast.LENGTH_SHORT);
            } else {
                ts.setText(msg);
                ts.setDuration(Toast.LENGTH_SHORT);
            }
            ts.show();
        }else {
            ThreadUtils.mainThread(new Runnable() {
                @Override
                public void run() {
                    if (ts == null) {
                        ts = Toast.makeText(mContext, msg, Toast.LENGTH_SHORT);
                    } else {
                        ts.setText(msg);
                        ts.setDuration(Toast.LENGTH_SHORT);
                    }
                    ts.show();
                }
            });
        }
    }

    public static void showToast(@StringRes final int msg){ //
        if (mContext == null){
            Log.e("showToastError","Please first initialization in the Application");
            return;
        }
        if (!mIsShowToast) return;
       showToast(mContext.getResources().getString(msg));
    }
    public static void closeToast(){
        if (ts != null) {
            ts.cancel();
        }
    }
}

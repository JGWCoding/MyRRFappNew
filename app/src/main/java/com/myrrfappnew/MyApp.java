package com.myrrfappnew;

import android.app.Application;
import android.os.Handler;

import com.dou361.dialogui.DialogUIUtils;
import com.myrrfappnew.utils.DbHelper;
import com.myrrfappnew.utils.FileUtils;
import com.myrrfappnew.utils.LogUtil;
import com.myrrfappnew.utils.ToastUtils;

import org.xutils.x;

/**
 * Created by Administrator on 2017/6/12.
 */

public class MyApp extends Application {
    public static Handler handler;
    @Override
    public void onCreate() {
        super.onCreate();

        x.Ext.init(this);
        x.Ext.setDebug(true);
        DialogUIUtils.init(this);  //dialog样式
        FileUtils.context = this;
        ToastUtils.init(this,true);
        DbHelper.getInstance();
        LogUtil.init(true);
        handler = new Handler();

    }
    public static void executeMain(Runnable task) {
        handler.post(task);
    }
}

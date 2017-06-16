package com.myrrfappnew.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.myrrfappnew.seriver.SyncService;
import com.myrrfappnew.utils.AppUtils;

public class NetWorkReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if(AppUtils.isNetworkAvailable(context)){
            //有网络，开启日志同步服务
            context.startService(new Intent(context, SyncService.class));
        }else{
            //无网络，关闭日志同步服务
            context.stopService(new Intent(context, SyncService.class));
        }
    }
}
package com.myrrfappnew.seriver;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.myrrfappnew.bean.WorkInfo;
import com.myrrfappnew.bean.WorkLogBean;
import com.myrrfappnew.utils.AppUtils;
import com.myrrfappnew.utils.DbHelper;
import com.myrrfappnew.utils.HttpManager;
import com.myrrfappnew.utils.ThreadUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Administrator on 23/3/2017.
 */

public class SyncService extends Service { //用于在后台运行

    private List<WorkLogBean> logList = new ArrayList<>();
    private List<WorkInfo> infoList = new ArrayList<>();
    private Timer timer;
    private TimerTask timerTask;
    private long delay = 1000 * 60 * 60;
    private static boolean isRun;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (!isRun) {
            ThreadUtils.pools.submit(new Runnable() {
                @Override
                public void run() { //子线程中更新数据 --> 网络更新
                    syncDate();
                }
            });
        }
        return super.onStartCommand(intent, flags, startId);

    }


    @Override
    public void onCreate() {
        super.onCreate();
        timer = new Timer();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                syncDate();
            }
        };
        timer.schedule(timerTask, 1000, delay);
    }

    public void syncDate() {  //开始上传数据 --> 没有上传过的
        isRun = true;
        if (AppUtils.isNetworkAvailable(this)) {
            logList = DbHelper.getInstance().getLog();
            if (logList != null) {
                for (WorkLogBean workLogBean : logList) {
                    if (workLogBean.getUpload() == 0) {
                        HttpManager.getInstance(this).uploadLog(workLogBean);
                    } else {
                        if (workLogBean.getWorkState() == 2) {
                            int expiredDay = AppUtils.getExpiredDayWithLog(workLogBean.getCreatDate());
                            if (expiredDay >= 10) {
                                if (!AppUtils.isEmpty(workLogBean.getImgUrls())) {
                                    File file = new File(workLogBean.getImgUrls());
                                    if (file.exists()) file.delete();
                                }
                            }
                        }
                    }
                }
            }
            infoList = DbHelper.getInstance().getAllUnUploadWhiteHead();
            if (infoList != null) {
                for (WorkInfo workInfo : infoList) {
                    HttpManager.getInstance(this).addWhiteHead(workInfo);
                }
            }
        }
        isRun = false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (timer != null) timer.cancel();
        if (timerTask != null) timerTask.cancel();
    }
}

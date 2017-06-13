package com.myrrfappnew.utils;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import com.myrrfappnew.R;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;


/**
 * author by john
 * 检查更新工具
 */

public class CheckVersion {

    private static CheckVersion instance;
    private Context context;
    private Dialog dialog;
    private String apkLink = "";
    private int versionCode = 0;
    private DownLoadAPKUtil downLoadAPKUtil;
    private ProgressDialog progressDialog;
    private long downloadId = -1;
    private Callback.Cancelable cancelable;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    if (!((AppCompatActivity) context).isFinishing())
                        showUpdateDialog();
                    break;
                case 1:
                    if (progressDialog == null) {
                        progressDialog = new ProgressDialog(context);
                        progressDialog.setTitle(context.getResources().getString(R.string.downloading));
                        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                        progressDialog.setMax(100);
                        progressDialog.setCancelable(false);
                        if (!((AppCompatActivity) context).isFinishing())
                            progressDialog.show();
                    }
                    int pro = downLoadAPKUtil.getProgress(downloadId);
                    progressDialog.setProgress(pro);
                    if (pro == 100) {
                        progressDialog.setTitle(context.getResources().getString(R.string.download_success));
                    } else if (pro == -1) {
                        progressDialog.setTitle(context.getResources().getString(R.string.download_failed));
                        progressDialog.dismiss();
                    }
                    if (pro >= 0 && pro < 100)
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                handler.sendEmptyMessage(1);
                            }
                        }, 1000);
                    LogUtil.i("--------progress = " + pro);
                    break;
            }
        }
    };

    public CheckVersion(Context context) {
        this.context = context;
    }

    public static CheckVersion getInstance(Context context) {
        if (instance == null)
            instance = new CheckVersion(context);
        return instance;
    }

    public void checkVersion() {
        RequestParams params = new RequestParams(UrlManager.CHECK_VERSION);
        cancelable = x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                LogUtil.i("-----onSuccess = " + result);
                try {
                    JSONObject json = new JSONObject(result);
                    int versionCode = json.getInt("version");
                    apkLink = json.getString("url");
                    LogUtil.e(versionCode+"==========="+AppUtils.getVersionCode(context));
                    if (versionCode > AppUtils.getVersionCode(context) && !AppUtils.isEmpty(apkLink)) {
                        handler.sendEmptyMessage(0);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {

            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });

    }

    private void showUpdateDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(context.getResources().getString(R.string.tip));
        builder.setMessage(context.getResources().getString(R.string.has_new_version));
        builder.setCancelable(false);
        builder.setNegativeButton(context.getResources().getString(R.string.update), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (downLoadAPKUtil == null)
                    downLoadAPKUtil = new DownLoadAPKUtil(context, "RRF_" + versionCode, apkLink);
                if (downloadId == -1)
                    downloadId = downLoadAPKUtil.start();
                handler.sendEmptyMessage(1);
            }
        });
        dialog = builder.create();
        dialog.show();
    }

    public void cancel() {
        if (cancelable != null)
            if (cancelable.isCancelled())
                cancelable.cancel();
    }

}

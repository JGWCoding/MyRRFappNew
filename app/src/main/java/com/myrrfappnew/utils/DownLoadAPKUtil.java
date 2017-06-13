package com.myrrfappnew.utils;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.widget.Toast;

import com.myrrfappnew.R;


/**
 * UpdateUtil downloadUtil = new UpdateUtil(activity, downloadUrl);
 * //下载显示名字，不能是中文
 * downloadUtil.setDownloadFileName("apkName" + System.currentTimeMillis() + ".apk");
 * downloadUtil.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
 * downloadUtil.start();
 * author by john
 */
public class DownLoadAPKUtil {
    private Context context;
    private String downloadFileName;
    private static long myReference;
    private static DownloadManager downloadManager;
    private DownloadManager.Request downloadRequest;
    private String updateUrl = "http://p.gdown.baidu.com/64ee1c469981e47516bbb5edee6956ff3aef0a04b473d0bc07cd5a0bd4a3f354430c9dae6242428cac92e170c2515eccc3c56a05a4335ee5490781b677badb18230216d6b61278cbb7cad5869ed84c023e2f834ddb3e1fc246115400bd5277f2024161f37957665ea987236b0919fbc8bfbde4841a6b62318d9610da5bb35f651538926fee90cd00a09c9c99d83f7b56083e23aa942db5c29ea0e90bda7854ceff8c40216d297ee2d98547fd4dccf04ccce91510b2c82f198d4fa2cf1c381186211c69ac85fa078946fdf08ebb5c6d59";

    public DownLoadAPKUtil(Context context, String filenName, String downloadUrl) {
        this.context = context;
        downloadFileName = filenName + ".apk";
        initDownload(downloadUrl);
//        reristerReceiver();
    }

    private void initDownload(String downloadUrl) {

        downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        Uri uri = Uri.parse(downloadUrl);
        downloadRequest = new DownloadManager.Request(uri);
        downloadRequest.setDestinationInExternalFilesDir(context, Environment.DIRECTORY_DOWNLOADS, downloadFileName);
        downloadRequest.allowScanningByMediaScanner();
        downloadRequest.setVisibleInDownloadsUi(false);
        downloadRequest.setMimeType("application/vnd.android.package-archive");
        downloadRequest.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        downloadRequest.setTitle(context.getResources().getString(R.string.app_name) + "_" + downloadFileName);
        downloadRequest.setDescription("");
    }

    public long start() {
        myReference = downloadManager.enqueue(downloadRequest);
        return myReference;
    }

    private void reristerReceiver() {
        IntentFilter intentFilter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        DownloadManagerReceiver receiver = new DownloadManagerReceiver();
        context.registerReceiver(receiver, intentFilter);

    }

    /**
     * 须static，不然在manife注册时报错：java.lang.InstantiationException has no zero argument constructor
     * 或者must be registered and unregistered inside the Parent class
     */
    public static class DownloadManagerReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            //Notification点击
            if (intent.getAction().equals(DownloadManager.ACTION_NOTIFICATION_CLICKED)) {
                String extraID = DownloadManager.EXTRA_NOTIFICATION_CLICK_DOWNLOAD_IDS;
                long[] references = intent.getLongArrayExtra(extraID);
                for (long reference : references)
                    if (reference == myReference) {
                    }
            }
            //下载完成
            if (intent.getAction().equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE)) {
                long completeDownloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                if (completeDownloadId == myReference) {
                    Cursor cursor = downloadManager.query(new DownloadManager.Query()
                            .setFilterById(completeDownloadId));
                    cursor.moveToFirst();
                    String filePath = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
                    LogUtil.i("------filepath = " + filePath);
                    cursor.close();
                    if (filePath != null) {
                        if (filePath.contains(context.getPackageName())) {
                            AppUtils.installAPK(context, filePath.trim().substring(7));
                        }
                    } else {
                        Toast.makeText(context, "下載失敗", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    }

    public void setDownloadFileName(String downloadFileName) {
        // 设置目标存储在外部目录，一般位置可以用
        downloadRequest.setDestinationInExternalFilesDir(context, Environment.DIRECTORY_DOWNLOADS, downloadFileName);
    }

    public void setNotificationTitle(CharSequence title) {
        //设置notification的标题
        downloadRequest.setTitle(title);

    }


    public void setNotificationDescription(CharSequence description) {
        //设置notification的描述
        downloadRequest.setDescription(description);
    }

    public void setNotificationVisibility(int visibility) {

        downloadRequest.setNotificationVisibility(visibility);
    }

    public DownloadManager.Request getDownloadRequest() {
        return downloadRequest;
    }

    public int getProgress(long id) {
        DownloadManager.Query query = new DownloadManager.Query();
        query.setFilterById(id);
        Cursor c = downloadManager.query(query);
        int progress = 0;
        if (c != null && c.moveToFirst()) {
            int status = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS));
            int fileSizeIdx =
                    c.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES);
            int bytesDLIdx =
                    c.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR);
            int fileSize = c.getInt(fileSizeIdx) / 1000;
            int bytesDL = c.getInt(bytesDLIdx) / 1000;
            switch (status) {
                case DownloadManager.STATUS_PAUSED:
                case DownloadManager.STATUS_PENDING:
                case DownloadManager.STATUS_RUNNING:
                case DownloadManager.STATUS_SUCCESSFUL:
                    if (fileSize > 0) {
                        progress = bytesDL * 100 / fileSize;
                    }
                    break;
                case DownloadManager.STATUS_FAILED:
                    progress = -1;
                    break;
            }
            c.close();
        } else {
            progress = -1;
        }
        return progress;
    }
}

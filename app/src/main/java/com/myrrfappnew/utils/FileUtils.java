package com.myrrfappnew.utils;

import android.content.Context;
import android.os.Environment;
import android.util.Base64;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

/**
 * Created by Administrator on 2017/2/21.
 */

public class FileUtils {

    private static final String ROOT_FILE_NAME = "RRFApp";
    private static final String IMG_FILE_NAME = "imgs";
    public static final String PDF_FILE_NAME = "pdf";
    private static final String DB_FILE_NAME = "db";

    public static Context context;
    private static FileUtils instant;

    public static FileUtils getInstant() {
        if (instant == null)
            instant = new FileUtils();
        return instant;
    }

    private FileUtils() {

    }


    public String getimgFile() {
        File file = new File(getSDPath() + File.separator + ROOT_FILE_NAME + File.separator + IMG_FILE_NAME);
        if (!file.exists()) file.mkdirs();
        return file.getPath();
    }

    public String getPdfFile() {
        File file = new File(getSDPath() + File.separator + ROOT_FILE_NAME + File.separator + PDF_FILE_NAME);
        if (!file.exists()) file.mkdirs();
        return file.getPath();
    }

    public String getSDPath() {
        File sdDir = null;
        boolean sdCardExist = Environment.getExternalStorageState()
                .equals(Environment.MEDIA_MOUNTED);//判断sd卡是否存在
        if (sdCardExist) {
            sdDir = Environment.getExternalStorageDirectory();//获取跟目录
        } else {
            sdDir = context.getFilesDir();
        }
        return sdDir.getPath();
    }

    public File getDBFile() {
        File file = new File(getSDPath() + File.separator + ROOT_FILE_NAME + File.separator + DB_FILE_NAME);
        if (!file.exists()) file.mkdirs();
        return file;
    }

    /**
     * 从assets目录中复制pdf至sd卡
     */
    public static void copyPDFToSDCard(Context context) {
        try {
            File file = new File(FileUtils.getInstant().getPdfFile() + "/" + PDF_FILE_NAME + ".pdf");
            if (file.exists()) {
                return;
            }
            LogUtil.i("-------copyPDFToSDCard");
            InputStream is = context.getAssets().open(FileUtils.PDF_FILE_NAME + ".pdf");
            FileOutputStream fos = new FileOutputStream(file);
            byte[] buffer = new byte[1024];
            int byteCount = 0;
            while ((byteCount = is.read(buffer)) != -1) {
                fos.write(buffer, 0, byteCount);
            }
            fos.flush();//刷新缓冲区
            is.close();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
            //如果捕捉到错误则通知UI线程
        }
    }

    /**
     * 文件转base64
     * @param path
     * @return
     * @throws Exception
     */
    public static String encodeBase64File(String path) throws Exception {
        File file = new File(path);
        FileInputStream inputFile = new FileInputStream(file);
        byte[] buffer = new byte[(int) file.length()];
        inputFile.read(buffer);
        inputFile.close();
        return Base64.encodeToString(buffer, Base64.DEFAULT);
    }

}

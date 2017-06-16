package com.myrrfappnew.utils;

import android.content.Context;
import android.util.Base64;
import android.util.Log;

import com.myrrfappnew.bean.WorkInfo;
import com.myrrfappnew.bean.WorkLogBean;

import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import static android.content.ContentValues.TAG;

/**
 * author by john
 */

public class HttpManager {

    private static HttpManager sInstance;
    private Context context;

    private HttpManager(Context context) {
        this.context = context;
    }

    public synchronized static HttpManager getInstance(Context context) {
        if (null == sInstance) {
            synchronized (HttpManager.class) {
                if (null == sInstance) sInstance = new HttpManager(context);
            }
        }
        return sInstance;
    }

    /**
     * 上传log
     *
     * @param bean
     */
    public void uploadLog(final WorkLogBean bean) {
        if (bean.getWorkId().contains("-")) {
            syncWhiteHead(bean);
            return;
        }
        final SoapObject request = new SoapObject(UrlManager.NAME_SPACE, UrlManager.METHOD_UPLOAD_LOG);
        request.addProperty("sid", AppUtils.getDeviceID(context));
        request.addProperty("postTime", bean.getCreatDate() + " " + bean.getTime());
        request.addProperty("crnNo", bean.getWorkId());
        if (!AppUtils.isEmpty(bean.getImgUrls())) {
            try {
                File file = new File(bean.getImgUrls());
                if (!file.exists()) return;
                Log.e(TAG, "uploadLog: "+file.getAbsolutePath() +"====="+bean.getImgUrls());
                FileInputStream stream = new FileInputStream(file);
                ByteArrayOutputStream out = new ByteArrayOutputStream(1000);
                byte[] b = new byte[1000];
                int n;
                while ((n = stream.read(b)) != -1)
                    out.write(b, 0, n);
                stream.close();
                out.close();
                String base64 = Base64.encodeToString(out.toByteArray(), Base64.NO_WRAP);
                request.addProperty("buffer", base64);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                request.addProperty("buffer", "");
            } catch (IOException e) {
                e.printStackTrace();
                request.addProperty("buffer", "");
            }
        }
        if (bean.getDesc().contains("&"))
            request.addProperty("attachmentData", bean.getWorkState() + "&" + bean.getDesc());
        else
            request.addProperty("attachmentData", bean.getWorkState() + "&" + bean.getDesc() + "&");
        request.addProperty("syncTime", AppUtils.getCurrentDate() + " " + AppUtils.getCurrentTime());
        LogUtil.i("--------request uploadLog = " + request.toString());
        ThreadUtils.pools.execute(new Runnable() {
            @Override
            public void run() {
                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapSerializationEnvelope.VER11);
                envelope.bodyOut = request;//由于是发送请求，所以是设置bodyOut
                envelope.dotNet = true;//由于是.net开发的webservice，所以这里要设置为true
                MyHttpTransport MyHttpTransport = new MyHttpTransport(UrlManager.WSDL_URI);
                try {
                    MyHttpTransport.call(UrlManager.ACTION_UPLOAD_LOG, envelope);
                    String result = envelope.getResponse().toString();
                    LogUtil.i("-------uploadLog result = " + result);
                    String tagStart = "<Status>";
                    String tagEnd = "</Status>";
                    String status = result.substring(result.indexOf(tagStart) + 8, result.indexOf(tagEnd));
                    if (status.equals("0")) {
                        DbHelper.getInstance().actionUpdataStatus(bean.getId());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 上传白头单
     *
     * @param info
     */
    public void addWhiteHead(final WorkInfo info) {
        final SoapObject request = new SoapObject(UrlManager.NAME_SPACE, UrlManager.METHOD_CREATE_WHITE_HEAD);
        request.addProperty("sid", AppUtils.getDeviceID(context));
        request.addProperty("caseId", info.getWorkId());
        request.addProperty("chineseAddr", info.getAddress());
        request.addProperty("caseType", info.getType().split(",")[1]);
        request.addProperty("createDate", info.getDate());
        request.addProperty("uploadTime", AppUtils.getCurrentDate() + " " + AppUtils.getCurrentTime());
        request.addProperty("sync", info.getUpload());
        request.addProperty("paramers", "");
        LogUtil.i("--------request addWhiteHead = " + request.toString());
        ThreadUtils.pools.execute(new Runnable() {
            @Override
            public void run() {
                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapSerializationEnvelope.VER11);
                envelope.bodyOut = request;
                envelope.dotNet = true;
                MyHttpTransport MyHttpTransport = new MyHttpTransport(UrlManager.WSDL_URI);
                try {
                    MyHttpTransport.call(UrlManager.ACTION_CREATE_WHITE_HEAD, envelope);
                    String result = envelope.getResponse().toString();
                    LogUtil.i("-------addWhiteHead result = " + result);
                    String tagStart = "<Status>";
                    String tagEnd = "</Status>";
                    String status = result.substring(result.indexOf(tagStart) + 8, result.indexOf(tagEnd));
                    if (status.equals("0")) {
                        info.setUpload(1);
                        DbHelper.getInstance().updataWork(info);
//                        EventBus.getDefault().post(new AddWhiteHeadEvent());
                        //TODO 更新页面的信息 ---> 刷新白头单的页面 ---> 防止有*号难看业务逻辑不好
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

    /**
     * @param caseId
     * @param crnNo
     */
    public void matchWhiteHead(String caseId, String crnNo) {
        final SoapObject request = new SoapObject(UrlManager.NAME_SPACE, UrlManager.METHOD_MATCH);
        request.addProperty("sid", AppUtils.getDeviceID(context));
        request.addProperty("caseId", caseId);
        request.addProperty("crnNo", crnNo);
        request.addProperty("matchTime", AppUtils.getCurrentDate() + " " + AppUtils.getCurrentTime());
        request.addProperty("paramers", "");
        LogUtil.i("--------request matchWhiteHead = " + request.toString());
        ThreadUtils.pools.execute(new Runnable() {
            @Override
            public void run() {
                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapSerializationEnvelope.VER11);
                envelope.bodyOut = request;
                envelope.dotNet = true;
                MyHttpTransport MyHttpTransport = new MyHttpTransport(UrlManager.WSDL_URI);
                try {
                    MyHttpTransport.call(UrlManager.ACTION_MATCH, envelope);
                    String result = envelope.getResponse().toString();
                    LogUtil.i("-------matchWhiteHead result = " + result);
                    String tagStart = "<Status>";
                    String tagEnd = "</Status>";
                    String status = result.substring(result.indexOf(tagStart) + 8, result.indexOf(tagEnd));
                    if (status.equals("0")) {

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

    /**
     * 上传白頭單log
     *
     * @param bean
     */
    public void syncWhiteHead(final WorkLogBean bean) {
        final SoapObject request = new SoapObject(UrlManager.NAME_SPACE, UrlManager.METHOD_UPLOAD_WHITE_HEAD_LOG);
        request.addProperty("sid", AppUtils.getDeviceID(context));
        request.addProperty("postTime", bean.getCreatDate() + " " + bean.getTime());
        request.addProperty("caseId", bean.getWorkId());
        if (!AppUtils.isEmpty(bean.getImgUrls())) {
            try {
                File file = new File(bean.getImgUrls());
                if (!file.exists()) return;
                FileInputStream stream = new FileInputStream(file);
                ByteArrayOutputStream out = new ByteArrayOutputStream(1000);
                byte[] b = new byte[1000];
                int n;
                while ((n = stream.read(b)) != -1)
                    out.write(b, 0, n);
                stream.close();
                out.close();
                String base64 = Base64.encodeToString(out.toByteArray(), Base64.NO_WRAP);
                request.addProperty("buffer", base64);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                request.addProperty("buffer", "");
            } catch (IOException e) {
                e.printStackTrace();
                request.addProperty("buffer", "");
            }
        }
        if (bean.getDesc().contains("&"))
            request.addProperty("attachmentData", bean.getWorkState() + "&" + bean.getDesc());
        else
            request.addProperty("attachmentData", bean.getWorkState() + "&" + bean.getDesc() + "&");
        request.addProperty("syncTime", AppUtils.getCurrentDate() + " " + AppUtils.getCurrentTime());
        LogUtil.i("--------request syncWhiteHead = " + request.toString());
        ThreadUtils.pools.execute(new Runnable() {
            @Override
            public void run() {
                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapSerializationEnvelope.VER11);
                envelope.bodyOut = request;//由于是发送请求，所以是设置bodyOut
                envelope.dotNet = true;//由于是.net开发的webservice，所以这里要设置为true
                MyHttpTransport MyHttpTransport = new MyHttpTransport(UrlManager.WSDL_URI);
                try {
                    MyHttpTransport.call(UrlManager.ACTION_UPLOAD_WHITE_HEAD_LOG, envelope);
                    String result = envelope.getResponse().toString();
                    LogUtil.i("-------uploadLog WhiteHead result = " + result);
                    String tagStart = "<Status>";
                    String tagEnd = "</Status>";
                    String status = result.substring(result.indexOf(tagStart) + 8, result.indexOf(tagEnd));
                    if (status.equals("0")) {
                        DbHelper.getInstance().actionUpdataStatus(bean.getId());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

    }


}

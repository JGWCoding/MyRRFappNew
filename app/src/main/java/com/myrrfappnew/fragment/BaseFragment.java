package com.myrrfappnew.fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.util.Xml;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dou361.dialogui.DialogUIUtils;
import com.myrrfappnew.activity.MainActivity;
import com.myrrfappnew.bean.WorkInfo;
import com.myrrfappnew.utils.AppUtils;
import com.myrrfappnew.utils.Constant;
import com.myrrfappnew.utils.DbHelper;
import com.myrrfappnew.utils.LogUtil;
import com.myrrfappnew.utils.MyHttpTransport;
import com.myrrfappnew.utils.PresentComparator;
import com.myrrfappnew.utils.ThreadUtils;
import com.myrrfappnew.utils.ToastUtils;
import com.myrrfappnew.utils.UrlManager;

import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.xmlpull.v1.XmlPullParser;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static android.content.ContentValues.TAG;
import static com.myrrfappnew.MyApp.handler;

/**
 * Created by Administrator on 2017/6/14.
 */

public abstract class BaseFragment extends Fragment{

    private Dialog dialog;
    private Runnable task;
    public String imei; //手机唯一号

    // TODO  --> 管理我的Fragment 使用hide show展示Fragment --->添加进栈按返回键可回退 ---> 当有网上传了数据更新页面刷新
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        imei = AppUtils.getDeviceID(getActivity());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return getMyView();
    }

    @Override
    public void onResume() {
        super.onResume();
        triggerRefresh(); //触发刷新按钮 --->先有一个加载中的视图 ----> 再加载数据
    }

    protected abstract View getMyView();   // TODO 給予一个视图  ---> 交给子类必须实现
    //触发刷新页面
    protected void triggerRefresh() {
        showDialog(); //显示加载中页面
        if (task==null) {
            task = new Runnable() {
                @Override
                public void run() {
                    try {
                        threadGetData();
                    } catch (Exception e) {
                        LogUtil.e(e.getMessage());
                        ToastUtils.showToast("加載數據時發生未知錯誤");
                    }
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            mainRefresh();
                            hideDialog();//隐藏加载中页面
                            Constant.isNetWorkGetData = false; //防止一直去网络加载数据
                        }
                    });
                }
            };
        }
        ThreadUtils.pools.submit(task);
    }
    //点击搜索按钮之后显示的数据
    protected void search(final String key) {
        task = new Runnable() {
            @Override
            public void run() {
                try {
                    showDialog();
                    searchData(key);
                } catch (Exception e) {
                    LogUtil.e(e.getMessage());
                    ToastUtils.showToast("加載數據時發生未知錯誤,或者沒有你要找的數據");
                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        mainRefresh();
                       hideDialog();
                    }
                });
            }
        };
        ThreadUtils.pools.execute(task);
    }

    protected abstract void searchData(String key);//TODO 显示搜索出来的数据

    protected abstract void threadGetData();//TODO 子线程中拿数据给予刷新数据及视图

    protected abstract void mainRefresh(); // TODO 用来转换主线程刷新View


    @Override
    public void onPause() { //当失去焦点时不再加载数据了
        super.onPause();
        ThreadUtils.pools.remove(task);
    }

    //下面在写点子类使用到的公共方法
    public void showDialog() { //当失去焦点时不再加载数据了
        if(AppUtils.isMainThread(getActivity())) {
            if (dialog != null) {
                dialog.show();
            } else {
                dialog = DialogUIUtils.showLoadingVertical(getActivity(), "請稍後,加載中...", false, false, true).show();
            }
        }else {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    if (dialog != null) {
                        dialog.show();
                    } else {
                        dialog = DialogUIUtils.showLoadingVertical(getActivity(), "請稍後,加載中...", false, false, true).show();
                    }
                }
            });
        }

    }

    public void hideDialog() { //当失去焦点时不再加载数据了
        if(AppUtils.isMainThread(getActivity())) {
            if (dialog!=null) {
                dialog.dismiss();
            }
        }else {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    if (dialog!=null) {
                        dialog.dismiss();
                    }
                }
            });
        }

    }
    //网络查询数据
    protected void queryRRFAll(final int sort) { //查询某个状态的数据
        List<WorkInfo> list = null;
        SoapObject request = new SoapObject(UrlManager.NAME_SPACE, UrlManager.METHOD_QUERY_RRFALL);
        request.addProperty("sid", imei);
        request.addProperty("querytime", AppUtils.getCurrentDate() + " " + AppUtils.getTime());
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapSerializationEnvelope.VER11);
        envelope.bodyOut = request;//由于是发送请求，所以是设置bodyOut
        envelope.dotNet = true;//由于是.net开发的webservice，所以这里要设置为true
        MyHttpTransport httpTransportSE = new MyHttpTransport(UrlManager.WSDL_URI);
        Log.e(TAG, "queryRRFAll: " + UrlManager.WSDL_URI);
        try {
            httpTransportSE.call(UrlManager.ACTION_QUERY_RRFALL, envelope);
            String result = envelope.getResponse().toString();
            LogUtil.i("------queryRRFAll result = " + result);
            XmlPullParser xmlPullParser = Xml.newPullParser();
            InputStream in_result = new ByteArrayInputStream(result.getBytes("UTF-8"));
            xmlPullParser.setInput(in_result, "UTF-8");
            List<WorkInfo> workNetList = null;
            WorkInfo workInfo = null;
            int eventType = xmlPullParser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT:
                        if (workNetList == null) workNetList = new ArrayList<>();
                        break;
                    case XmlPullParser.START_TAG:
                        if (workInfo == null)
                            workInfo = new WorkInfo();
                        String name = xmlPullParser.getName();
                        if (name.equals("urgent")) { //"urgent"
                            workInfo.setUrgent(xmlPullParser.nextText());
                        }
                        if (name.equals("crnNo")) {//"workId"
                            workInfo.setWorkId(xmlPullParser.nextText());
                        }
                        if (name.equals("pdfLink")) {//"pdfLink"
                            workInfo.setPdfLink(xmlPullParser.nextText());
                        }
                        if (name.equals("AppointDate")) { //"date" 时间
                            String s = xmlPullParser.nextText();
                            Log.e(TAG, "AppointDate: " + s);
                            workInfo.setDate(s);
                        }
                        if (name.equals("rrfTime")) {  //"time" 时间
                            String s = xmlPullParser.nextText();
                            workInfo.setTime(s);
                            Log.e(TAG, "rrfTime: " + s);
                        }
                        if (name.equals("address")) { //"address"  地址
                            workInfo.setAddress(xmlPullParser.nextText());
                        }
                        if (name.equals("jpgLink")) { //"address"  地址
                            workInfo.setJpgLink(xmlPullParser.nextText());
                            Log.e(TAG, "queryRRFAll: " + name);
                        }
                        if (name.equals("worker")) { // "worker" 工作者
                            workInfo.setWorker(xmlPullParser.nextText());
                        }
                        if (name.equals("IEME")) { // "ieme" 手机唯一码
                            workInfo.setIeme(xmlPullParser.nextText());
                        }
                        if (name.equals("appstatusdate")) {  // "completeDate" 到场时间
                            String completeDate = xmlPullParser.nextText();
                            if (!AppUtils.isEmpty(completeDate))
                                workInfo.setCompleteDate(AppUtils.getCompleteDate(completeDate));
                            Log.e(TAG, "appstatusdate: " + completeDate);
                        }
                        if (name.equals("appstatus")) {  // "state" 工作状态
                            String state = xmlPullParser.nextText();
                            if (AppUtils.isEmpty(state))
                                workInfo.setState(0);
                            else
                                workInfo.setState(Integer.valueOf(state));
                        }
                        workInfo.setWhiteHead(0);
                        break;
                    case XmlPullParser.END_TAG:
                        if ("RRF".equals(xmlPullParser.getName())) {
//                            DbHelper.getInstance().insertNetWorkData(workInfo); //网络查询的每一条插入进去
                            DbHelper.getInstance().insertNetWorkDataJGP(workInfo); //只是插入一個字段的数据,因为以前的数据没有这个字段
                            workNetList.add(workInfo);
                            if (workInfo.getState() == sort)
                                LogUtil.i("-----workInfo = " + sort + "=" + workInfo);
                            workInfo = null;
                        }
                        break;
                }
                eventType = xmlPullParser.next();
            }
            //TODO ---> 查询出每个状态的本地数据 ----> 对比网络数据库 ---> 干掉网络状态中的没有的(没有上传的不用干掉,白头单不用干掉),删除以前的图片等数据 ---> 刷新页面

            if (workNetList != null) {  //插入网络上的数据库 --- 删除本地的以前已上传而网络没有的workID
                if (list != null)
                    list.clear();
                list = DbHelper.getInstance().getNotWhiteHead(); //本地数据库的数据 --->不是白头单的数据 其他上传是否的都有
                //对比workID  workID相同删除网络的数据库 不同的删除本地数据库的
                if (list != null) {
                    ArrayList<String> workLocalIDs = new ArrayList<>(); //存本地的workID
                    for (int i = 0; i < list.size(); i++) {
                        workLocalIDs.add(list.get(i).getWorkId());
                    }
                    ArrayList<String> workNetIDs = new ArrayList<>(); //存网络的workID
                    for (int i = 0; i < workNetList.size(); i++) {
                        workNetIDs.add(workNetList.get(i).getWorkId());
                    }
                    ArrayList<String> sameIDs = new ArrayList<>(); //存网络和本地相同的workID
                    for (int i = 0; i < workLocalIDs.size(); i++) {
                        if (workNetIDs.contains(workLocalIDs.get(i))) {
                            sameIDs.add(workLocalIDs.get(i));
                        }
                    }
                    for (int i = 0; i < workNetList.size(); i++) { //网络上面 存储的数据--->存本地没有workID的数据
                        WorkInfo info = workNetList.get(i);
                        if (sameIDs.contains(info.getWorkId())) {
//                            workNetList.remove(info);
                        } else {
                            if (info.getWorkId() != null)
                                DbHelper.getInstance().insertNetWorkData(info);
                        }
                    }
                    for (int i = 0; i < list.size(); i++) {  //本地要存储的数据  ---> 存网络和本地相同的workID数据  以及 没有上传的数据
                        WorkInfo info = list.get(i);
                        if (sameIDs.contains(info.getWorkId())) { //网络和本地都有的  --> 删除网络的
                            //以为数据已经存起来了,不用再操作
                        } else {  //
                            if (info.getUpload() == 0) { //没有上传
                            } else { //已经上传了的 --->而且网络上没有这个workID--->删掉这条数据
                                DbHelper.getInstance().deleteWorkInfo(info);
                            }
                        }
                    }
                    sameIDs = null;
                    workNetIDs = null;
                    workLocalIDs = null;
                } else { //本地没数据
                    for (WorkInfo workInfo1 : workNetList)
                        DbHelper.getInstance().insertNetWorkData(workInfo1);
                }
                workNetList = null;
                list = DbHelper.getInstance().getWorkList(sort); //再获取本地数据库的数据
                Log.e(TAG, "queryRRFAll: " + list.size());
            } else {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtils.showToast("網絡開小差了");
                    }
                });
                return;
            }
            if (list != null) {
                if (list.size() > 1)
                    Collections.sort(list, new PresentComparator(imei));
            }
        } catch (Exception e) {
            Log.d("取數據出錯", "------Exception" + e.toString());
            e.printStackTrace();
            list = DbHelper.getInstance().getWorkList(sort);
            if (list != null) {
                if (list.size() > 1)
                    Collections.sort(list, new PresentComparator(imei));
            }
        }
        handler.post(new Runnable() { //显示 HUEN 的查询的数
            @Override
            public void run() {
                if (MainActivity.countView != null)
                    MainActivity.countView.setText(DbHelper.getInstance().calToatal(sort));
            }
        });

    }
    //网络下载 pdf jpg 等,返回绝对路径
    protected String downLoadFile(String link) {  //注意这个要在子线程中执行
        showDialog();
        if (TextUtils.isEmpty(link)) {
           ToastUtils.showToast("這個文件沒有生成哦");
            return null;
        }
       File file =  new File(Constant.path+File.separator+link.replace("\\","-"));
        if(file.exists()){
            return file.getAbsolutePath();
        }else {
            SoapObject request = new SoapObject(UrlManager.NAME_SPACE, UrlManager.METHOD_DOWNLOAD_PDF);
            request.addProperty("fileName", link);
            request.addProperty("querytime", AppUtils.getCurrentDate());
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapSerializationEnvelope.VER11);
            envelope.bodyOut = request;//由于是发送请求，所以是设置bodyOut
            envelope.dotNet = true;//由于是.net开发的webservice，所以这里要设置为true
            MyHttpTransport httpTransportSE = new MyHttpTransport(UrlManager.WSDL_URI);
            try {
                httpTransportSE.call(UrlManager.ACTION_DOWNLOAD_PDF, envelope);
                String result = envelope.getResponse().toString();
                if (result.equals("anyType{}")) {
                    ToastUtils.showToast("網絡文件錯誤");
                    return null;
                }
                File rootPath = new File(Constant.path);
                if (!rootPath.exists()) {
                    rootPath.mkdirs();
                }
                byte b[] = Base64.decode(result, Base64.DEFAULT);
                file.createNewFile();
                FileOutputStream out_pdf = new FileOutputStream(file);
                out_pdf.write(b);
                out_pdf.flush();
                out_pdf.close();
                return file.getAbsolutePath();
            } catch (Exception e) {
                e.printStackTrace();
                ToastUtils.showToast("網絡下載出錯了");
            }
        }
        hideDialog();
        return null;
    }
}

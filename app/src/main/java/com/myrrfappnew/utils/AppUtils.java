package com.myrrfappnew.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Looper;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;


/**
 * 获取手机唯一串号
 */
public class AppUtils {
    /**是否在主线程中*/
    public static boolean isMainThread(Context context) {
       if(Thread.currentThread() == Looper.getMainLooper().getThread()) {
           return true;
       }
        return false;
    }
    /**获取设备串号*/
    public static String getDeviceID(Context context) {
        // 已经有摄像头权限了，可以使用该权限完成app的相应的操作了
        TelephonyManager telephonyManager = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        String deviceId = telephonyManager.getDeviceId();   // imei
        if (deviceId == null) {
            deviceId = telephonyManager.getSubscriberId();  // imsi
        }
        if (deviceId == null) {
            deviceId = telephonyManager.getSimSerialNumber();   //得到sim卡号
        }
        if (deviceId == null) {
            deviceId = "noimei" + telephonyManager.getNetworkOperatorName(); //获取网络进网许可证号
        }
//        return "861695033281275";
//        return "861695032114865";
//        return "354485061431925";
//        return  "867106022928243";
//        return  "869161021936471";
        return deviceId;   //TODO 这里返回手机唯一码
    }


    /**
     * 获取手机唯一标志 返回MD5加密后的值
     */
    public static String getOnlySign(Context context) {

        //获取手机唯一串号
        String szImei = null;
        TelephonyManager TelephonyMgr = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        szImei = TelephonyMgr.getDeviceId();
        String m_szDevIDShort = "35" + //we make this look like a valid IMEI
                Build.BOARD.length() % 10 +
                Build.BRAND.length() % 10 +
                Build.CPU_ABI.length() % 10 +
                Build.DEVICE.length() % 10 +
                Build.DISPLAY.length() % 10 +
                Build.HOST.length() % 10 +
                Build.ID.length() % 10 +
                Build.MANUFACTURER.length() % 10 +
                Build.MODEL.length() % 10 +
                Build.PRODUCT.length() % 10 +
                Build.TAGS.length() % 10 +
                Build.TYPE.length() % 10 +
                Build.USER.length() % 10; //13 digits

        String m_szAndroidID = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);


        WifiManager wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        String m_szWLANMAC = wm.getConnectionInfo().getMacAddress();


        String m_szLongID = szImei + m_szDevIDShort
                + m_szAndroidID + m_szWLANMAC;
        // compute md5
        MessageDigest m = null;
        try {
            m = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        m.update(m_szLongID.getBytes(), 0, m_szLongID.length());
        // get md5 bytes
        byte p_md5Data[] = m.digest();
        // create a hex string
        String m_szUniqueID = new String();
        for (int i = 0; i < p_md5Data.length; i++) {
            int b = (0xFF & p_md5Data[i]);
            // if it is a single digit, make sure it have 0 in front (proper padding)
            if (b <= 0xF)
                m_szUniqueID += "0";
            // add number to string
            m_szUniqueID += Integer.toHexString(b);
        }   // hex string to uppercase
        m_szUniqueID = m_szUniqueID.toUpperCase();
        Log.e("手机唯一标志deviceId", m_szUniqueID);
        return m_szUniqueID;
    }


    /**
     * 获取版本号
     *
     * @return 当前应用的版本号
     */
    public static String getVersion(Context context) {
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            String version = info.versionName;
            return version;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * 两个数组合并成一个
     *
     * @param a
     * @param b
     * @return
     */
    public static String[] concat(String[] a, String[] b) {
        String[] c = new String[a.length + b.length];
        System.arraycopy(a, 0, c, 0, a.length);
        System.arraycopy(b, 0, c, a.length, b.length);
        return c;
    }

    /**
     * 獲取日期  年-月-日
     *
     * @return
     */
    public static String getDate() {
        DateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        return df.format(new Date());
    }

    /**
     * 獲取日期  年-月-日
     *
     * @return
     */
    public static String completeDate() {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        return df.format(new Date());
    }

    public static String getCurrentDate() {
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy ");
        return df.format(new Date());
    }


    public static String getCurrentTime() {
        DateFormat df = new SimpleDateFormat("HH:mm:ss ");
        return df.format(new Date());
    }

    /**
     * 獲取白头单日期
     *
     * @return
     */
    public static String getDateWithWhiteHead() {
        DateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
        return df.format(new Date());
    }

    /**
     * 獲取時間  時:分:秒
     *
     * @return
     */
    public static String getTime() {
        DateFormat df = new SimpleDateFormat("HH:mm:ss");
        return df.format(new Date());
    }

    /**
     * 使用第三方應用打開文件
     *
     * @param param
     * @return
     */
    public static Intent openPDF(String param) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.fromFile(new File(param));
        intent.setDataAndType(uri, "application/*");
        return intent;
    }


    /**
     * 获取App版本号
     *
     * @param context
     * @return
     */
    public static int getVersionCode(Context context) {
        PackageManager pm = context.getPackageManager();
        try {
            PackageInfo info = pm.getPackageInfo(context.getPackageName(), 0);
            return info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            return 0;
        }
    }

    /**
     * 安装apk
     *
     * @param context 上下文
     * @param path    文件路劲
     */
    public static void installAPK(Context context, String path) {
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(new File(path)),
                "application/vnd.android.package-archive");
        context.startActivity(intent);
    }

    /**
     * 判断过期多少天
     * 得到离现在过了多少天
     * @param fromDate
     */
    @SuppressLint("SimpleDateFormat")
    public static int getExpiredDay(String fromDate) {
        if (isEmpty(fromDate))
            return 0;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            sdf.setTimeZone(TimeZone.getTimeZone("GTM+8"));
            Date nowDate = new Date();
            Date d2 = sdf.parse(fromDate);
            return (int) ((nowDate.getTime() - d2.getTime()) / (24 * 3600 * 1000));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static String getCompleteDate(String completeDate){
        try {
            if(isEmpty(completeDate))
                return "";
            return new SimpleDateFormat("yyyy-MM-dd").format(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(completeDate));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return completeDate;
    }

    /**
     * 判断过期多少天
     *
     * @param fromDate
     */
    @SuppressLint("SimpleDateFormat")
    public static int getExpiredDayWithLog(String fromDate) {
        if (isEmpty(fromDate))
            return 0;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            sdf.setTimeZone(TimeZone.getTimeZone("GTM+8"));
            Date nowDate = new Date();
            Date d2 = sdf.parse(fromDate);
            return (int) ((nowDate.getTime() - d2.getTime()) / (24 * 3600 * 1000));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 比较两个日期大小
     *
     * @param date1
     * @param date2
     * @return
     */
    @SuppressLint("SimpleDateFormat")
    public static int dateEquals(String date1, String date2) {
        if (isEmpty(date1) || isEmpty(date2))
            return 0;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            sdf.setTimeZone(TimeZone.getTimeZone("GTM+8"));
            Date d1 = sdf.parse(date1);
            Date d2 = sdf.parse(date2);
            return (int) ((d1.getTime() - d2.getTime()) / (24 * 3600 * 1000));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 判断字符串是否为空
     *
     * @param charSequence
     * @return
     */
    public static boolean isEmpty(CharSequence charSequence) {
        if (TextUtils.isEmpty(charSequence) || charSequence.equals("null")) {
            return true;
        }
        return false;
    }
    /**
     * 判断是否是平板
     * @param context
     * @return  平板返回 True，手机返回 False
     */
    public static boolean isPad(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }
    /**
     * 判断当前网络是否可用
     *
     * @param context
     * @return
     */
    public static boolean isNetworkAvailable(Context context) {
        return getNetworkType(context) != 0;
    }

    /**
     * 获取当前连接的网络类型：
     * <ul>
     * <li>0：无网络</li>
     * <li>1：WIFI</li>
     * <li>2：CMWAP</li>
     * <li>3：CMNET</li>
     * </ul>
     *
     * @param context
     * @return
     */
    public static int getNetworkType(Context context) {
        ConnectivityManager connManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connManager == null) {
            return 0;
        } else {
            NetworkInfo[] info = connManager.getAllNetworkInfo();
            if (info != null) {
                for (NetworkInfo anInfo : info) {
                    if (anInfo.getState() == NetworkInfo.State.CONNECTED) {
                        NetworkInfo netWorkInfo = anInfo;
                        if (netWorkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                            return 1;
                        } else if (netWorkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                            String extraInfo = netWorkInfo.getExtraInfo();
                            if ("cmwap".equalsIgnoreCase(extraInfo)
                                    || "cmwap:gsm".equalsIgnoreCase(extraInfo)) {
                                return 2;
                            }
                            return 3;
                        }
                    }
                }
            }
        }
        return 0;
    }


    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     *
     * @param context
     * @param dpValue dp值
     * @return
     */
    public static int dp2px(Context context, float dpValue) {
        return (int) (dpValue * getDeviceDensity(context) + 0.5f);
    }

    /**
     * 获得设备的密度
     *
     * @param mContext
     * @return
     */
    public static float getDeviceDensity(Context mContext) {
        return mContext.getResources().getDisplayMetrics().density;
    }

    /**
     * 隐藏软键盘
     */
    public static void hideKeyboard(Context context, EditText view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    /**
     * 截取白头单id,
     * @param workId
     * @return
     */
    public static String getWhiteHeadId(String workId) {
        String[] id = workId.split("-");
        return id[1];
    }


}

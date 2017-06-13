package com.myrrfappnew.utils;


import com.myrrfappnew.BuildConfig;

/**
 * author by john
 */

public class UrlManager {
    public static final String WSDL_URI; //地址
    public static final String CHECK_VERSION; //检验的版本地址
    public static String METHOD_QUERY_RRFALL; //匹配的查询命
    public static String ACTION_QUERY_RRFALL; //查询的活动名
    static {
        if (BuildConfig.API_ENV_PRODUCTION.equals("931")) {
            WSDL_URI = "http://202.130.80.179:9031/Interface/AppService.asmx?wsdl";
            CHECK_VERSION = "http://makeitmobile.com.hk/AWOS/TCE931.php";
            METHOD_QUERY_RRFALL = "QqueryRRF";
            ACTION_QUERY_RRFALL = "http://tempuri.org/QqueryRRF";
        } else if (BuildConfig.API_ENV_PRODUCTION.equals("951")) {
            WSDL_URI = "http://202.130.80.179:9051/Interface/AppService.asmx?wsdl";
            CHECK_VERSION = "http://makeitmobile.com.hk/AWOS/TCE951.php";
            METHOD_QUERY_RRFALL = "QqueryRRF";
            ACTION_QUERY_RRFALL = "http://tempuri.org/QqueryRRF";
        } else {
//            WSDL_URI = "http://192.168.2.115:8890/Interface/AppService.asmx?WSDL";
            WSDL_URI = "http://203.132.203.57:8066/Interface/AppService.asmx?WSDL";
//            WSDL_URI = "http://202.130.80.179:9031/Interface/AppService.asmx?WSDL";
            CHECK_VERSION = "http://makeitmobile.com.hk/AWOS/AWOS.php";
            METHOD_QUERY_RRFALL = "QqueryRRF";
            ACTION_QUERY_RRFALL = "http://tempuri.org/QqueryRRF";
//            WSDL_URI = "http://202.130.80.179:9031/Interface/AppService.asmx?wsdl";
//            CHECK_VERSION = "http://makeitmobile.com.hk/AWOS/TCE931.php";
//            METHOD_QUERY_RRFALL = "QqueryRRF";
//            ACTION_QUERY_RRFALL = "http://tempuri.org/QqueryRRF";
        }
    }

    public static String NAME_SPACE = "http://tempuri.org/";

    /**
     * 下载pdf
     */
    public static String METHOD_DOWNLOAD_PDF = "downPdf";
    public static String ACTION_DOWNLOAD_PDF = "http://tempuri.org/downPdf";


    /**
     * 上传log
     */
    public static String METHOD_UPLOAD_LOG = "syncPicture";
    public static String ACTION_UPLOAD_LOG = "http://tempuri.org/syncPicture";

    /**
     * 上传白名單log
     */
    public static String METHOD_UPLOAD_WHITE_HEAD_LOG = "syncHead";
    public static String ACTION_UPLOAD_WHITE_HEAD_LOG = "http://tempuri.org/syncHead";

    /**
     * 添加白头单
     */
    public static String METHOD_CREATE_WHITE_HEAD = "CreateHead";
    public static String ACTION_CREATE_WHITE_HEAD = "http://tempuri.org/CreateHead";

    /** 配對白頭單*/
    public static String METHOD_MATCH = "MathchHead";
    public static String ACTION_MATCH = "http://tempuri.org/MathchHead";

}

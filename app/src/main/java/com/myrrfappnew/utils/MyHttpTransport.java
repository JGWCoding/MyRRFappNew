package com.myrrfappnew.utils;

import org.ksoap2.transport.HttpTransportSE;

/**
 * author by john
 */

public class MyHttpTransport extends HttpTransportSE {
    public MyHttpTransport(String url) { //这个用来设置http请求代理的,设置url和设置timeout
        super(url, 1000);
    }
}

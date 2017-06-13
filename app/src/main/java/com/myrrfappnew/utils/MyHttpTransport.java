package com.myrrfappnew.utils;

import org.ksoap2.transport.HttpTransportSE;

/**
 * author by john
 */

public class MyHttpTransport extends HttpTransportSE {
    public MyHttpTransport(String url) {
        super(url, 10000);
    }
}

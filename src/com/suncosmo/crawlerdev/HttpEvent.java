package com.suncosmo.crawlerdev;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

/**
 * Created by SunCosmo on 2017/3/4.
 */
public class HttpEvent {

    public class HttpConn {

        public HttpConn() {
        }

        public HttpConn(String method, String url, Map<String, String> header, String content) {
            this.method = method;
            this.url = url;
            this.header = header;
            this.content = content;
        }

        private String method = "GET";

        private String url;

        Map<String, String> header;

        private String content;

        private HttpURLConnection httpURLConnection;

        private int timeOut = 60000;

        boolean isConnected = false;

        public String getMethod() {
            return method;
        }

        public void setMethod(String method) {
            this.method = method;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public Map<String, String> getHeader() {
            return header;
        }

        public void setHeader(Map<String, String> header) {
            this.header = header;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public HttpURLConnection getHttpURLConnection() {
            return httpURLConnection;
        }

        public void setHttpURLConnection(HttpURLConnection httpURLConnection) {
            this.httpURLConnection = httpURLConnection;
        }
    }

    public static InputStream getHttpIS(HttpConn httpConn) {
        try {
            httpConn.httpURLConnection =
                    (HttpURLConnection)(new URL(httpConn.url).openConnection());
            httpConn.httpURLConnection.setRequestMethod(httpConn.method);
            httpConn.httpURLConnection.setConnectTimeout(httpConn.timeOut);
            //Set connection time out, needed here because of the http connection may be blocked overtime
            httpConn.httpURLConnection.setReadTimeout(httpConn.timeOut);
            if(httpConn.header != null) {
                httpConn.header.forEach(httpConn.httpURLConnection::setRequestProperty);
            }
            httpConn.httpURLConnection.connect();
            httpConn.isConnected = true;
            return httpConn.httpURLConnection.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void closeAllHttpConn(HttpConn httpConn) {
        if(httpConn.httpURLConnection != null) {
            httpConn.httpURLConnection.disconnect();
        }
    }

    public static void setTimeout(HttpConn httpConn, int timeout) {
        if(httpConn != null) {
            httpConn.httpURLConnection.setConnectTimeout(timeout);
        }
    }
}

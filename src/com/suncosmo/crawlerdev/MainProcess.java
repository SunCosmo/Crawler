package com.suncosmo.crawlerdev;

/**
 * Created by SunCosmo on 2017/3/4.
 */
public class MainProcess {
    public static void main(String args[])  {
        Thread htmlParser_t = new Thread( new MyHTMLParser());
        Thread imgDownload_t = new Thread( new MyImgCrawler());
        htmlParser_t.start();
        imgDownload_t.start();
    }
}

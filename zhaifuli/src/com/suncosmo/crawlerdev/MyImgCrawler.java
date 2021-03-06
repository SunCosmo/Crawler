package com.suncosmo.crawlerdev;


import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.suncosmo.crawlerdev.Env.IMG_DOWNLOAD_PATH;
import static com.suncosmo.crawlerdev.Env.imgUrls;

/**
 * Created by SunCosmo on 2017/3/4.
 */

class MyImgCrawler implements Runnable {
    private static String method = "GET";

    private static Map<String, String> header;

    static {
        header = new HashMap<>();

        header.put("User-Agent","Mozilla/5.0 (Windows; U; Windows NT 6.1; en-US; rv:1.9.1.6) Gecko/20091201 Firefox/3.5.6");

        header.put("Referer", "http://zhaofuli.biz/luyilu/2016/1023/1112.html");
    }
    
    public void downloadImg(String imgUrl) {
        HttpEvent.HttpConn http = new HttpEvent().new HttpConn(method, imgUrl, header, "");
        try {
            byte inf[] = new byte[65535];
            InputStream is = HttpEvent.getHttpIS(http);
            if(is != null) {
                String format = imgUrl.substring(imgUrl.lastIndexOf("."));
                String name = imgUrl.substring(imgUrl.lastIndexOf("/") + 1, imgUrl.lastIndexOf(".")) + "-" + (new Date()).getTime() + format;
                File newFile = new File(IMG_DOWNLOAD_PATH + name);
                if (!newFile.getParentFile().exists() && !newFile.getParentFile().mkdir()) {
                    throw new Exception("Failed to create local parent folder");
                }
                if (newFile.createNewFile()) {
                    FileOutputStream fo = new FileOutputStream(newFile);
                    int len;
                    while ((len = is.read(inf)) > 0) {
                        fo.write(inf, 0, len);
                    }
                    System.out.println("download img: " + name);
                    fo.close();
                }
                is.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        for(;;) {
            if(imgUrls.isEmpty()) {
                System.out.println("no img url left...sleeping...");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                String imgUrl = imgUrls.pop();
                System.out.println("download img: " + imgUrl);
                downloadImg(imgUrl);
            }
        }
    }
}

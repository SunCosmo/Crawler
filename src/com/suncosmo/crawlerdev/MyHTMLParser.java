package com.suncosmo.crawlerdev;

import java.io.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static com.suncosmo.crawlerdev.Env.*;

/**
 * Created by SunCosmo on 2017/3/4.
 */
public class MyHTMLParser implements Runnable{

    public MyHTMLParser() {

        this.srcKeyWords.add("zhaofuli");

        this.srcNoWords.add("youfanhao");

        this.srcNoWords.add("xiachedan");

        this.srcNoWords.add("php");
    }

    public void getAndParseHTML(String srcUrl) {
        String method = "GET";
        Map<String, String> header = new HashMap<>();
        header.put("User-Agent","Mozilla/5.0 (Windows; U; Windows NT 6.1; en-US; rv:1.9.1.6) Gecko/20091201 Firefox/3.5.6");
        header.put("Referer", "http://zhaofuli.biz/luyilu/2016/1023/1112.html");
        HttpEvent.HttpConn httpConn = new HttpEvent(). new HttpConn(method, srcUrl, header, "");
        httpConn.setUrl(srcUrl);
        try {
            InputStream is = HttpEvent.getHttpIS(httpConn);
            if(is != null) {
                BufferedReader bufin = new BufferedReader(
                        new InputStreamReader(is));
                String srcAtt = "href", imgTag = "<img", imgAtt = "src";
                for (String tempLine; (tempLine = bufin.readLine()) != null; ) {
                    int li;
                    do {
                        if ((li = tempLine.indexOf(srcAtt)) > 0) {
                            tempLine = tempLine.substring(li);
                            li = 6;
                            while (li < tempLine.length() && tempLine.charAt(li) != '\"'
                                    && tempLine.charAt(li) != '\'') {
                                ++li;
                            }
                            String newSrcUrl = tempLine.substring(6, li);
                            tempLine = tempLine.substring(li);
                            if (newSrcUrl.charAt(0) == '/') {
                                newSrcUrl = MAIN_DOMAIN + newSrcUrl;
                            } else if (newSrcUrl.length() < 13 && newSrcUrl.indexOf("htm") > 0) {
                                newSrcUrl = srcUrl.substring(0, srcUrl.lastIndexOf("/") + 1) + newSrcUrl;
                            }
                            boolean isNeeded = true;
                            for (String s : srcKeyWords) {
                                if (newSrcUrl.indexOf(s) > 0) {
                                    isNeeded = true;
                                    break;
                                }
                            }
                            for (String s : srcNoWords) {
                                if (newSrcUrl.indexOf(s) > 0) {
                                    isNeeded = false;
                                    break;
                                }
                            }
                            if (oldSrcUrls.contains(newSrcUrl)
                                    || newSrcUrl.length() == 0
                                    || !newSrcUrl.contains("http")
                                    || !newSrcUrl.contains("zhaofuli")) {
                                isNeeded = false;
                            }
                            if (isNeeded) {
                                System.out.println("src url: " + newSrcUrl);
                                srcUrls.push(newSrcUrl);
                                oldSrcUrls.add(newSrcUrl);
                            }
                        }
                        if ((li = tempLine.indexOf(imgTag)) > 0) {
                            tempLine = tempLine.substring(li);
                            tempLine = tempLine.substring(tempLine.indexOf(imgAtt));
                            li = 5;
                            while (li < tempLine.length() && tempLine.charAt(li) != '\"' && tempLine.charAt(li) != '\'') {
                                ++li;
                            }
                            String newImgUrl = tempLine.substring(5, li);
                            tempLine = tempLine.substring(li);
                            boolean isNeeded = true;
                            String fileName = newImgUrl.substring(newImgUrl.lastIndexOf("/"));
                            if (oldImgUrls.contains(newImgUrl)
                                    || newImgUrl.contains("-lp")
                                    || newImgUrl.contains("ajax")
                                    || newImgUrl.charAt(0) == '/'
                                    || fileName.charAt(2) == '_'
                                    || fileName.charAt(2) == '-') {
                                isNeeded = false;
                            }
                            if (isNeeded) {
                                System.out.println("img url: " + newImgUrl);
                                imgUrls.push(newImgUrl);
                                oldImgUrls.add(newImgUrl);
                            }
                        }
                    } while (li != 0 && (tempLine.contains(srcAtt) || tempLine.contains(imgTag)));
                    bufin.close();
                }
            }
            is.close();
        } catch (IOException | NullPointerException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        for( ; ;) {
            if(srcUrls.isEmpty()) {
                System.out.println("no src urls.");
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                String currSrcUrl = srcUrls.pop();
                System.out.println("Parsing src url: " + currSrcUrl);
                getAndParseHTML(currSrcUrl);
            }
        }

    }

    private Set<String> srcKeyWords = new HashSet<>();

    private Set<String> srcNoWords = new HashSet<>();

}

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

    }

    private static Set<String> srcKeyWords;

    private static Set<String> srcNoWords;

    private static String srcAtt = "href", imgTag = "<img", imgAtt = "src";

    private static String method = "GET";

    private static Map<String, String> header;

    static {

        srcKeyWords = new HashSet<>();

        srcKeyWords.add("zhaofuli");

        srcNoWords = new HashSet<>();

        srcNoWords.add("youfanhao");

        srcNoWords.add("xiachedan");

        srcNoWords.add("php");

        header = new HashMap<>();

        header.put("User-Agent","Mozilla/5.0 (Windows; U; Windows NT 6.1; en-US; rv:1.9.1.6) Gecko/20091201 Firefox/3.5.6");

        header.put("Referer", "http://zhaofuli.biz/luyilu/2016/1023/1112.html");

    }

    public void getAndParseHTML(String srcUrl) {
        HttpEvent.HttpConn httpConn = new HttpEvent(). new HttpConn(method, srcUrl, header, "");
        httpConn.setUrl(srcUrl);
        try {
            InputStream is = HttpEvent.getHttpIS(httpConn);
            if(is != null) {
                BufferedReader bufin = new BufferedReader(
                        new InputStreamReader(is));
                for (String tempLine; (tempLine = bufin.readLine()) != null; ) {
                    int srcI = 0, imgI = 0;
                    do {
                        srcI = tempLine.indexOf(srcAtt);
                        imgI = tempLine.indexOf(imgTag);
                        if(srcI > 0 && imgI > 0) {
                            tempLine = (srcI < imgI) ? parseImg(parseSrc(tempLine, srcUrl)) : parseSrc(parseImg(tempLine), srcUrl);
                        } else if(srcI > 0) {
                            tempLine = parseSrc(tempLine, srcUrl);
                        } else if(imgI > 0) {
                            tempLine = parseImg(tempLine);
                        }
                    } while(srcI > 0 || imgI > 0);
                }
                bufin.close();
            }
            is.close();
        } catch (IOException | NullPointerException e) {
            e.printStackTrace();
        }
    }

    private String parseSrc(String line, String srcUrl) {
        int start = line.indexOf(srcAtt), end = start + 6;
        if (start > 0) {
            while (end < line.length()
                    && line.charAt(end) != '\"'
                    && line.charAt(end) != '\'') {
                ++end;
            }
            String newSrcUrl = line.substring(start+5, end);
            line = line.substring(end);
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
        return line;
    }

    private String parseImg(String line) {
        int start = line.indexOf(imgAtt), end = start + 5;
        if (start > 0) {
            while (end < line.length() && line.charAt(end) != '\"' && line.charAt(end) != '\'') {
                ++end;
            }
            String newImgUrl = line.substring(start + 5, end);
            line = line.substring(end);
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
        return line;
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


}

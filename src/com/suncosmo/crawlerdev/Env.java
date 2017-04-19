package com.suncosmo.crawlerdev;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

/**
 * Created by SunCosmo on 2017/3/4.
 */
public final class Env {

    private Env() {}

    public static Stack<String> srcUrls = new Stack<>();

    public static Stack<String> imgUrls = new Stack<>();

    public static Set<String> oldSrcUrls = new HashSet<>();

    public static Set<String> oldImgUrls = new HashSet<>();

    public final static String MAIN_DOMAIN = "http://zhaofuli.biz";

    public final static String IMG_DOWNLOAD_PATH = "E:\\imgDownloadTest19\\";

    static {
        srcUrls.push("http://zhaofuli.biz/index.html");
    }
}

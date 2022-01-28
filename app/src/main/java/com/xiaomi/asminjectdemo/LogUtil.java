package com.xiaomi.asminjectdemo;

import android.util.Log;


/**
 * Log工具类
 */
public class LogUtil {
    private static final String TAG = "LogUtil";
    private static final String tag_pre = "ASMDemo_";
    //控制项目是否显示log
    private static boolean isShowLog = true;

    public static void v(String tag, String msg) {
        if (isShowLog) {
            Log.v(tag_pre + tag, msg);
        }
    }

    public static void v(Object tag, String msg) {
        if (isShowLog) {
            Log.v(tag_pre + tag.getClass().getSimpleName(), msg);
        }
    }

    public static void v(String tag, String msg, Exception e) {
        if (isShowLog) {
            Log.v(tag_pre + tag, msg, e);
        }
    }

    public static void i(String tag, String msg) {
        if (isShowLog) {
            Log.i(tag_pre + tag, msg);
        }
    }

    public static void i(Object tag, String msg) {
        if (isShowLog) {
            Log.i(tag_pre + tag.getClass().getSimpleName(), msg);
        }
    }

    public static void i(String tag, String msg, Exception e) {
        if (isShowLog) {
            Log.i(tag_pre + tag, msg, e);
        }
    }

    public static void d(String tag, String msg) {
        if (isShowLog) {
            Log.d(tag_pre + tag, msg);
        }
    }

    public static void d(Object tag, String msg) {
        if (isShowLog) {
            Log.d(tag_pre + tag.getClass().getSimpleName(), msg);
        }
    }

    public static void d(String tag, String msg, Exception e) {
        if (isShowLog) {
            Log.d(tag_pre + tag, msg, e);
        }
    }

    // warn 和 error 级别log比较少，不过滤
    public static void w(String tag, String msg) {
        Log.w(tag_pre + tag, msg);
    }

    public static void w(Object tag, String msg) {
        Log.w(tag_pre + tag.getClass().getSimpleName(), msg);
    }

    public static void w(String tag, String msg, Exception e) {
        Log.w(tag_pre + tag, msg, e);
    }

    public static void e(String tag, String msg) {
        Log.e(tag_pre + tag, msg);
    }

    public static void e(String tag, String msg, Exception e) {
        Log.e(tag_pre + tag, msg, e);
    }

    public static void e(Object tag, String msg) {
        Log.e(tag_pre + tag.getClass().getSimpleName(), msg);
    }

    public static void getStackTraceString(Throwable str) {
        Log.getStackTraceString(str);
    }
}

package com.dragonsoftbravo.businesscircle.utils;

import android.support.graphics.drawable.animated.BuildConfig;
import android.util.Log;

public class Logger {
    static final String DEBUG_TAG = "Logger";

    public static void i(String msg) {
        if (BuildConfig.DEBUG)
            Log.i(DEBUG_TAG, msg);
    }

    public static void d(String msg) {
        if (BuildConfig.DEBUG)
            Log.d(DEBUG_TAG, msg);
    }

    public static void e(String msg) {
        if (BuildConfig.DEBUG)
            Log.e(DEBUG_TAG, msg);
    }

    public static void w(String msg) {
        if (BuildConfig.DEBUG)
            Log.w(DEBUG_TAG, msg);
    }

    public static void v(String msg) {
        if (BuildConfig.DEBUG)
            Log.v(DEBUG_TAG, msg);
    }


    public static void i(String tag, String msg) {
        if (BuildConfig.DEBUG)
            i(tag + ":" + msg);
    }

    public static void d(String tag, String msg) {
        if (BuildConfig.DEBUG) {
            d(tag + ":" + msg);
        }
    }

    public static void e(String tag, String msg) {
        if (BuildConfig.DEBUG)
            e(tag + ":" + msg);
    }

    public static void w(String tag, String msg) {
        if (BuildConfig.DEBUG)
            w(tag + ":" + msg);
    }

    public static void v(String tag, String msg) {
        if (BuildConfig.DEBUG)
            v(tag + ":" + msg);
    }

    public static void i(Object o, String msg) {
        if (BuildConfig.DEBUG)
            i(o.getClass().getName() + ":" + msg);
    }

    public static void d(Object o, String msg) {
        if (BuildConfig.DEBUG)
            d(o.getClass().getName() + ":" + msg);
    }

    public static void e(Object o, String msg) {
        if (BuildConfig.DEBUG)
            e(o.getClass().getName() + ":" + msg);
    }

    public static void w(Object o, String msg) {
        if (BuildConfig.DEBUG)
            w(o.getClass().getName() + ":" + msg);
    }

    public static void v(Object o, String msg) {
        if (BuildConfig.DEBUG)
            v(o.getClass().getName() + ":" + msg);
    }

    public static void i(String tag, String info, Throwable e) {
        if (BuildConfig.DEBUG)
            i(tag + ":" + info + ":" + Log.getStackTraceString(e));
    }

    public static void d(String tag, String info, Throwable e) {
        if (BuildConfig.DEBUG)
            d(tag + ":" + info + ":" + Log.getStackTraceString(e));
    }

    public static void e(String tag, String info, Throwable e) {
        if (BuildConfig.DEBUG)
            e(tag + ":" + info + ":" + Log.getStackTraceString(e));
    }

    public static void v(String tag, String info, Throwable e) {
        if (BuildConfig.DEBUG)
            v(tag + ":" + info + ":" + Log.getStackTraceString(e));
    }

    public static void w(String tag, String info, Throwable e) {
        if (BuildConfig.DEBUG)
            w(tag + ":" + info + ":" + Log.getStackTraceString(e));
    }

    public static void i(String tag, Throwable e) {
        if (BuildConfig.DEBUG)
            i(tag + ":" + Log.getStackTraceString(e));
    }

    public static void d(String tag, Throwable e) {
        if (BuildConfig.DEBUG)
            d(tag + ":" + Log.getStackTraceString(e));
    }

    public static void e(String tag, Throwable e) {
        if (BuildConfig.DEBUG)
            e(tag + ":" + Log.getStackTraceString(e));
    }

    public static void v(String tag, Throwable e) {
        if (BuildConfig.DEBUG)
            v(tag + ":" + Log.getStackTraceString(e));
    }

    public static void w(String tag, Throwable e) {
        if (BuildConfig.DEBUG)
            w(tag + ":" + Log.getStackTraceString(e));
    }

    public static void vLarg(String sb) {
        if (sb.length() > 4000) {
            v("log.length = " + sb.length());
            int chunkCount = sb.length() / 4000;     // integer division
            for (int i = 0; i <= chunkCount; i++) {
                int max = 4000 * (i + 1);
                if (max >= sb.length()) {
                    v("chunk " + i + " of " + chunkCount + ":" + sb.substring(4000 * i));
                } else {
                    v("chunk " + i + " of " + chunkCount + ":" + sb.substring(4000 * i, max));
                }
            }
        } else {
            v(sb.toString());
        }
    }
}


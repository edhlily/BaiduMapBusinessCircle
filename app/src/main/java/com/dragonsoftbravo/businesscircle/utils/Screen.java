package com.dragonsoftbravo.businesscircle.utils;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;


public class Screen {
    public static float SCREEN_WIDTH_DP;
    public static float SCREEN_HEIGHT_DP;
    public static int SCREEN_WIDTH_PX;
    public static int SCREEN_HEIGHT_PX;

    public static void init(Context context) {
        DisplayMetrics dm = getScreen(context);
        SCREEN_WIDTH_PX = dm.widthPixels;
        SCREEN_HEIGHT_PX = dm.heightPixels;
        SCREEN_WIDTH_DP = px2dip(context, SCREEN_WIDTH_PX);
        SCREEN_HEIGHT_DP = px2dip(context, SCREEN_HEIGHT_PX);
        Logger.d(SCREEN_WIDTH_PX + "px-" + SCREEN_HEIGHT_PX + "px|" + SCREEN_WIDTH_DP + "dp-" + SCREEN_HEIGHT_DP + "dp");
    }

    /**
     * 获取屏幕的大小0：宽度 1：高度
     */
    public static DisplayMetrics getScreen(Context context) {
        WindowManager windowManager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);
        int width = outMetrics.widthPixels;
        int height = outMetrics.heightPixels;
        return outMetrics;
    }

    /**
     * 计算百分比
     */
    public static String getPercent(int n, float total) {
        float rs = (n / total) * 100;
        // 判断是否是正整数
        if (String.valueOf(rs).contains(".0")) {
            return String.valueOf((int) rs);
        } else {
            return String.format("%.1f", rs);
        }
    }

    /**
     * 格式化毫秒->00:00
     */
    public static String formatSecondTime(int millisecond) {
        if (millisecond == 0) {
            return "00:00";
        }
        millisecond = millisecond / 1000;
        int m = millisecond / 60 % 60;
        int s = millisecond % 60;
        return (m > 9 ? m : "0" + m) + ":" + (s > 9 ? s : "0" + s);
    }

    /**
     * 格式化文件大小 Byte->MB
     */
    public static String formatByteToMB(int size) {
        float mb = size / 1024f / 1024f;
        return String.format("%.2f", mb);
    }

    /**
     * 格式化文件大小 Byte->KB
     */
    public static String formatByteToKB(int size) {
        float kb = size / 1024f;
        return String.format("%.2f", kb);
    }

    public static int getDimen(Context context, int resId) {
        return context.getResources()
                .getDimensionPixelSize(resId);
    }

    public static int getDimenOffset(Context context, int resId) {
        return context.getResources().getDimensionPixelOffset(resId);
    }

    public static String arrToStr(String[] strs) {
        StringBuilder sb = new StringBuilder();
        if (strs.length < 1) {
            return "";
        }
        for (String str : strs) {
            sb.append(str).append(",");
        }
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }

    public static int convertDpToPixel(Context context, float dp) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return (int) px;
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }
}

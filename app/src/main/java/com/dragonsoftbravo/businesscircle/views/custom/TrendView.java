package com.dragonsoftbravo.businesscircle.views.custom;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.View;

import com.dragonsoftbravo.businesscircle.utils.Screen;

import java.util.ArrayList;
import java.util.List;

public class TrendView extends View {
    private String title;
    private List<ColItem> colItems = new ArrayList<>();
    private Paint titlePaint;
    private Paint colPaint;
    private Paint bgPaint;
    private int mViewHeight, mViewWidth;
    private boolean withPoint = true;
    private int itemtMargin;
    private int viewPadding;
    private int contentHeight;
    private int pointHeight;
    private int colWidth;
    private int colTextSize;
    private int titleSize;
    private int bgStorkeWidth;
    private int titleMargin;
    private int colTextMargin;
    private int triangleWidth;//三角形边长

    public TrendView(Context context) {
        super(context);
        init();
    }

    public TrendView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        itemtMargin = Screen.dip2px(getContext(), 5);
        viewPadding = Screen.dip2px(getContext(), 15);
        contentHeight = Screen.dip2px(getContext(), 40);
        pointHeight = Screen.dip2px(getContext(), 10);
        colWidth = Screen.dip2px(getContext(), 16);
        titleSize = Screen.dip2px(getContext(), 10);
        colTextSize = Screen.dip2px(getContext(), 10);
        bgStorkeWidth = Screen.dip2px(getContext(), 1);
        titleMargin = Screen.dip2px(getContext(), 3);
        colTextMargin = Screen.dip2px(getContext(), 3);
        triangleWidth = Screen.dip2px(getContext(), 8);
        titlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        titlePaint.setStyle(Paint.Style.FILL);
        titlePaint.setTextAlign(Paint.Align.CENTER);
        titlePaint.setTextSize(titleSize);
        colPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        colPaint.setStyle(Paint.Style.FILL);
        bgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        bgPaint.setStyle(Paint.Style.FILL);
        colPaint.setTextSize(colTextSize);
    }

    public void setWithPoint(boolean withPoint) {
        this.withPoint = withPoint;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void addItem(ColItem colItem) {
        if (!colItems.contains(colItem))
            colItems.add(colItem);
    }

    public static final int TREND_FLAT = 0;//平
    public static final int TREND_UP = 1;//上升
    public static final int TREND_DOWN = 2;//下降
    private int trend = TREND_FLAT;

    public void setItems(List<ColItem> colItems, long rate) {
        this.colItems.clear();
        this.colItems.addAll(colItems);
        if (colItems.size() > 1) {
            if (Math.abs(rate) <= 5) {
                trend = TREND_FLAT;
            } else if (rate < 0) {
                trend = TREND_DOWN;
            } else {
                trend = TREND_UP;
            }
        }
        requestLayout();
    }

    public int getItemsSize() {
        return this.colItems.size();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mViewWidth = w;
        mViewHeight = h;
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        drawBg(canvas);
        drawLine(canvas);
        drawTitle(canvas);
        int startX = viewPadding;
        for (int i = 0; i < colItems.size(); i++) {
            draw(colItems.get(i), i, startX, canvas);
            startX += getWidth(colItems.get(i));
        }
        drawTrend(canvas);
    }

    private void drawLine(Canvas canvas) {
        bgPaint.setColor(Color.BLACK);
        canvas.drawLine(viewPadding / 2, getBottomY() - viewPadding / 2, viewPadding * 3 / 2 + contentWidth, getBottomY() - viewPadding / 2, bgPaint);
    }

    private void drawTrend(Canvas canvas) {
        Path path = new Path();
        path.reset();
        int color = Color.parseColor("#0000FF");
        Point sp = new Point(0, 0);
        Point ep = new Point(0, 0);
        switch (trend) {
            case TREND_FLAT:
                sp = new Point(viewPadding, getContentBottomY() - contentHeight / 2);
                ep = new Point(viewPadding + contentWidth - triangleWidth / 2, getContentBottomY() - contentHeight / 2);
                path.moveTo(sp.x, sp.y);
                path.lineTo(ep.x, ep.y);
                break;
            case TREND_UP:
                sp = new Point(viewPadding + contentWidth * 3 / 4, getContentBottomY() - contentHeight / 2);
                ep = new Point(viewPadding + contentWidth - triangleWidth / 2, getContentBottomY() - contentHeight + triangleWidth / 2);
                color = Color.parseColor("#FF0000");
                path.moveTo(viewPadding, getContentBottomY());
                path.lineTo(viewPadding + contentWidth / 4, getContentBottomY() - contentHeight / 2);
                path.lineTo(viewPadding + contentWidth * 3 / 4, getContentBottomY() - contentHeight / 2);
                path.lineTo(ep.x, ep.y);
                break;
            case TREND_DOWN:
                sp = new Point(viewPadding + contentWidth * 3 / 4, getContentBottomY() - contentHeight / 2);
                ep = new Point(viewPadding + contentWidth - triangleWidth / 2, getContentBottomY() - triangleWidth / 2);
                color = Color.parseColor("#00FF00");
                path.moveTo(viewPadding, getContentBottomY() - contentHeight);
                path.lineTo(viewPadding + contentWidth / 4, getContentBottomY() - contentHeight / 2);
                path.lineTo(viewPadding + contentWidth * 3 / 4, getContentBottomY() - contentHeight / 2);
                path.lineTo(ep.x, ep.y);
                break;
        }
        bgPaint.setStyle(Paint.Style.STROKE);
        bgPaint.setColor(color);
        bgPaint.setStrokeWidth(bgStorkeWidth * 2);
        canvas.drawPath(path, bgPaint);
        drawAL(canvas, bgPaint, sp, ep);
    }

    private void drawBg(Canvas canvas) {
        bgPaint.setStyle(Paint.Style.FILL);
        bgPaint.setColor(Color.parseColor("#AAFFFFFF"));
        Path path = new Path();
        path.reset();
        path.moveTo(0, 0);
        path.lineTo(mViewWidth, 0);
        if (withPoint) {
            path.lineTo(mViewWidth, mViewHeight - pointHeight);
            path.lineTo(mViewWidth / 2 + pointHeight, mViewHeight - pointHeight);
            path.lineTo(mViewWidth / 2, mViewHeight);
            path.lineTo(mViewWidth / 2 - pointHeight, mViewHeight - pointHeight);
            path.lineTo(0, mViewHeight - pointHeight);
        } else {
            path.lineTo(mViewWidth, mViewHeight);
            path.lineTo(0, mViewHeight);
        }
        path.close();
        canvas.drawPath(path, bgPaint);
        Path path2 = new Path();
        path2.reset();
        path2.moveTo(0, 0);
        path2.lineTo(mViewWidth, 0);
        if (withPoint) {
            path2.lineTo(mViewWidth, mViewHeight - pointHeight);
            path2.lineTo(mViewWidth / 2 + pointHeight, mViewHeight - pointHeight);
            path2.lineTo(mViewWidth / 2, mViewHeight);
            path2.lineTo(mViewWidth / 2 - pointHeight, mViewHeight - pointHeight);
            path2.lineTo(0, mViewHeight - pointHeight);
        } else {
            path2.lineTo(mViewWidth, mViewHeight);
            path2.lineTo(0, mViewHeight);
        }
        path2.close();
        bgPaint.setStyle(Paint.Style.STROKE);
        bgPaint.setColor(Color.parseColor("#CCCCCCCC"));
        bgPaint.setStrokeWidth(bgStorkeWidth);
        canvas.drawPath(path2, bgPaint);
    }

    private void drawTitle(Canvas canvas) {
        titlePaint.setColor(Color.parseColor("#000000"));
        int titleTextHeight = getTitleTextHeight();
        if (titleTextHeight == 0) return;
        canvas.drawText(title, 0, title.length(), mViewWidth / 2, getBottomY() - viewPadding - getTitleHeight() - titleMargin - contentHeight / 2, titlePaint);
    }

    public static class ColItem {
        private String text;
        private int textColor;
        private int blockColor;

        public ColItem(int blockColor, String text, int textColor) {
            this.blockColor = blockColor;
            this.text = text;
            this.textColor = textColor;
        }

        public int getBlockColor() {
            return blockColor;
        }

        public void setBlockColor(int blockColor) {
            this.blockColor = blockColor;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public int getTextColor() {
            return textColor;
        }

        public void setTextColor(int textColor) {
            this.textColor = textColor;
        }
    }

    public void draw(ColItem colItem, int index, int startX, Canvas canvas) {
        colPaint.setColor(colItem.textColor);
        if (index == 0) {
            colPaint.setTextAlign(Paint.Align.LEFT);
            canvas.drawText(colItem.text, 0, colItem.text.length(), startX - viewPadding * 2 / 3, getColTextY(colItem, index) - colTextMargin, colPaint);
        } else if (index == colItems.size() - 1) {
            colPaint.setTextAlign(Paint.Align.RIGHT);
            canvas.drawText(colItem.text, 0, colItem.text.length(), startX + viewPadding * 2 / 3 + getWidth(colItem), getColTextY(colItem, index) - colTextMargin, colPaint);
        }
        colPaint.setColor(colItem.blockColor);
    }

    public float getColTextY(ColItem colItem, int index) {
        if ((trend == TREND_UP && index == 0) || (trend == TREND_DOWN && index == colItems.size() - 1)) {
            return getBottomY() - viewPadding;
        } else if ((trend == TREND_UP && index == colItems.size() - 1) || (trend == TREND_DOWN && index == 0)) {
            return getBottomY() - viewPadding - getTitleHeight() - contentHeight;
        } else if (trend == TREND_FLAT && index == 0) {
            return getBottomY() - viewPadding - getTitleHeight() - contentHeight / 2 + 2 * getColTextHeight();
        }
        return getBottomY() - viewPadding - getTitleHeight() - contentHeight / 2 - getColTextHeight();
    }

    public float getWidth(ColItem colItem) {
        return contentHeight;
    }

    public float getTextWidth(ColItem colItem) {
        return colPaint.measureText(colItem.text);
    }

    private int getTitleWidth() {
        return (int) titlePaint.measureText(title);
    }

    private int getTitleTextHeight() {
        if (title != null && title.length() > 1) {
            return (int) (Math.ceil(titlePaint.getFontMetrics().descent - titlePaint.getFontMetrics().ascent) + 2);
        } else {
            return 0;
        }
    }

    private int getColTextHeight() {
        return (int) (Math.ceil(colPaint.getFontMetrics().descent - colPaint.getFontMetrics().ascent) + 2) + colTextMargin;
    }

    private int getTitleHeight() {
        return getTitleTextHeight() == 0 ? 0 : getTitleTextHeight() + titleMargin;
    }

    private int contentWidth;

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        mViewWidth = 2 * viewPadding;
        contentWidth = 0;
        for (ColItem colItem : colItems) {
            contentWidth += getWidth(colItem);
        }
        mViewWidth += contentWidth;
        mViewHeight = contentHeight + 2 * viewPadding + getTitleHeight() + getColTextHeight();
        mViewHeight = withPoint ? mViewHeight + pointHeight : mViewHeight;
        setMeasuredDimension(mViewWidth, mViewHeight);
    }

    private int getBottomY() {
        return withPoint ? mViewHeight - pointHeight : mViewHeight;
    }

    private int getContentBottomY() {
        return getBottomY() - viewPadding - getTitleHeight();
    }

    public int getmViewHeight() {
        return mViewHeight;
    }

    public void drawAL(Canvas canvas, Paint paint, Point sp, Point ep) {
        //直线方程使得箭头向延长线移动一半箭头高度个单位，避免直线挡住箭头尖部
        double k = ((double) (ep.y - sp.y) / (double) (ep.x - sp.x));
        double b = sp.y - (k * sp.x);
        ep.x = ep.x + triangleWidth / 2;
        ep.y = (int) (Math.round(ep.x * k) + b);
        paint.setStyle(Paint.Style.FILL);
        int sx = sp.x;
        int sy = sp.y;
        int ex = ep.x;
        int ey = ep.y;
        double H = triangleWidth; // 箭头高度
        double L = triangleWidth / 2; // 底边的一半
        int x3;
        int y3;
        int x4;
        int y4;
        double awrad = Math.atan(L / H); // 箭头角度
        double arraow_len = Math.sqrt(L * L + H * H); // 箭头的长度
        double[] arrXY_1 = rotateVec(ex - sx, ey - sy, awrad, true, arraow_len);
        double[] arrXY_2 = rotateVec(ex - sx, ey - sy, -awrad, true, arraow_len);
        double x_3 = ex - arrXY_1[0]; // (x3,y3)是第一端点
        double y_3 = ey - arrXY_1[1];
        double x_4 = ex - arrXY_2[0]; // (x4,y4)是第二端点
        double y_4 = ey - arrXY_2[1];
        Double X3 = new Double(x_3);
        x3 = X3.intValue();
        Double Y3 = new Double(y_3);
        y3 = Y3.intValue();
        Double X4 = new Double(x_4);
        x4 = X4.intValue();
        Double Y4 = new Double(y_4);
        y4 = Y4.intValue();
        Path triangle = new Path();
        triangle.moveTo(ex, ey);
        triangle.lineTo(x3, y3);
        triangle.lineTo(x4, y4);
        triangle.close();
        canvas.drawPath(triangle, paint);

    }

    // 计算
    public double[] rotateVec(int px, int py, double ang, boolean isChLen, double newLen) {
        double mathstr[] = new double[2];
        // 矢量旋转函数，参数含义分别是x分量、y分量、旋转角、是否改变长度、新长度
        double vx = px * Math.cos(ang) - py * Math.sin(ang);
        double vy = px * Math.sin(ang) + py * Math.cos(ang);
        if (isChLen) {
            double d = Math.sqrt(vx * vx + vy * vy);
            vx = vx / d * newLen;
            vy = vy / d * newLen;
            mathstr[0] = vx;
            mathstr[1] = vy;
        }
        return mathstr;
    }
}

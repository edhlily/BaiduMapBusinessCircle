package com.dragonsoftbravo.businesscircle.views.custom;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.View;

import com.dragonsoftbravo.businesscircle.utils.Logger;
import com.dragonsoftbravo.businesscircle.utils.Screen;

import java.util.ArrayList;
import java.util.List;

public class ColTextView extends View {
    private String title;
    private List<ColItem> colItems = new ArrayList<>();
    private Paint titlePaint;
    private Paint colPaint;
    private Paint bgPaint;
    private int mViewHeight, mViewWidth;
    private boolean withPoint = true;

    private int itemtMargin = 20;
    private int viewPadding = 20;
    private int contentHeight = 300;
    private int pointHeight = 30;
    private int colWidth = 20;
    private int titleSize = 24;
    private int colTextSize = 20;
    private int bgStorkeWidth = 3;
    private int titleMargin = 10;
    private int colTextMargin = 10;

    public ColTextView(Context context) {
        super(context);
        init();
    }

    public ColTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        itemtMargin = Screen.dip2px(getContext(), 5);
        viewPadding = Screen.dip2px(getContext(), 5);
        contentHeight = Screen.dip2px(getContext(), 120);
        pointHeight = Screen.dip2px(getContext(), 10);
        colWidth = Screen.dip2px(getContext(), 8);
        titleSize = Screen.dip2px(getContext(), 12);
        colTextSize = Screen.dip2px(getContext(), 10);
        bgStorkeWidth = Screen.dip2px(getContext(), 1);
        titleMargin = Screen.dip2px(getContext(), 3);
        colTextMargin = Screen.dip2px(getContext(), 3);
        titlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        titlePaint.setStyle(Paint.Style.FILL);
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

    public void setItems(List<ColItem> colItems) {
        this.colItems.clear();
        this.colItems.addAll(colItems);
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
        //drawBg(canvas);
        drawBottomLine(canvas);
        drawTitle(canvas);
        int startX = viewPadding;
        for (ColItem colItem : colItems) {
            draw(colItem, startX, canvas);
            startX += getWidth(colItem);
        }
    }

    private void drawBottomLine(Canvas canvas) {
        bgPaint.setColor(Color.BLACK);
        canvas.drawLine(0, getBottomY(), mViewWidth, getBottomY(), bgPaint);
    }

    private void drawBg(Canvas canvas) {
        bgPaint.setStyle(Paint.Style.FILL);
        bgPaint.setColor(Color.parseColor("#55FFFFFF"));
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
        titlePaint.setTextSize(titleSize);
        int titleTextHeight = getTitleTextHeight();
        if (titleTextHeight == 0) return;
//        canvas.drawText(title, 0, title.length(), viewPadding + (mViewWidth - 2 * viewPadding - getTitleWidth()) / 2, viewPadding + titleTextHeight, titlePaint);
        canvas.drawText(title, 0, title.length(), viewPadding + (mViewWidth - 2 * viewPadding - getTitleWidth()) / 2, getBottomY() - viewPadding, titlePaint);
    }

    public static class ColItem {
        private String text;
        private int textColor;
        private int blockColor;
        private int percent;

        public ColItem(int blockColor, int percent, String text, int textColor) {
            this.blockColor = blockColor;
            this.percent = percent;
            this.text = text;
            this.textColor = textColor;
        }

        public int getBlockColor() {
            return blockColor;
        }

        public void setBlockColor(int blockColor) {
            this.blockColor = blockColor;
        }

        public int getPercent() {
            return percent;
        }

        public void setPercent(int percent) {
            this.percent = percent;
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

    public void draw(ColItem colItem, int startX, Canvas canvas) {
        colPaint.setTextSize(colTextSize);
        colPaint.setColor(colItem.textColor);
        canvas.drawText(colItem.text, 0, colItem.text.length(), startX + (getWidth(colItem) - getTextWidth(colItem)) / 2, getTop(colItem) - colTextMargin, colPaint);

//        colPaint.setColor(Color.parseColor("#55000000"));
//        canvas.drawRect(startX + getWidth(colItem) / 2 - colWidth, getTitleHeight() + viewPadding + titleMargin, startX + getWidth(colItem) / 2 + colWidth, getBottomY() - viewPadding - colTextSize - colTextMargin, colPaint);
        colPaint.setColor(colItem.blockColor);
//        canvas.drawRect(startX + getWidth(colItem) / 2 - colWidth, getTop(colItem), startX + getWidth(colItem) / 2 + colWidth, getBottomY() - viewPadding - colTextSize - colTextMargin, colPaint);
        canvas.drawRect(startX + getWidth(colItem) / 2 - colWidth, getTop(colItem), startX + getWidth(colItem) / 2 + colWidth, getBottomY() - viewPadding - getTitleTextHeight() - colTextMargin, colPaint);
    }

    public float getTop(ColItem colItem) {
        float h = (getBottomY() - viewPadding - getTitleHeight() - titleMargin - colTextSize - colTextMargin - viewPadding);
        float percentH = h * colItem.percent / 100;
        percentH = percentH > h ? h : percentH;
        return (getBottomY() - viewPadding - getTitleHeight() - titleMargin) - percentH;
    }

    public float getWidth(ColItem colItem) {
        float textWidth = getTextWidth(colItem);
        float textWidthDp = Screen.px2dip(getContext(), textWidth);
        return textWidthDp > 25 ? textWidth : textWidthDp < 20 ? Screen.dip2px(getContext(), 25) : textWidth + 5;
    }

    public float getTextWidth(ColItem colItem) {
        return colPaint.measureText(colItem.text);
    }

    private int getTitleWidth() {
        return (int) titlePaint.measureText(title);
    }

    private int getTitleTextHeight() {
        if (title != null && title.length() > 1) {
            return (int) titlePaint.measureText(title.substring(0, 1));
        } else {
            return 0;
        }
    }

    private float getTitleHeight() {
        if (title != null && title.length() > 1) {
            return titlePaint.measureText(title, 0, 1);
        } else {
            return 0;
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        mViewWidth = 2 * viewPadding;
        for (ColItem colItem : colItems) {
            mViewWidth += getWidth(colItem);
        }
        mViewHeight = withPoint ? contentHeight + pointHeight : contentHeight;
        Logger.d("ColTextView height : " + mViewHeight + ":" + Screen.px2dip(getContext(), mViewHeight));
        setMeasuredDimension(mViewWidth, mViewHeight);
    }

    Point center;

    private Point getCenter() {
        if (center == null) {
            if (withPoint) {
                center = new Point(mViewWidth / 2, (mViewHeight - pointHeight) / 2);
            } else {
                center = new Point(mViewWidth / 2, mViewHeight / 2);
            }
        }
        return center;
    }

    private int getBottomY() {
        if (withPoint) {
            return mViewHeight - pointHeight;
        } else {
            return mViewHeight;
        }
    }

    public int getmViewHeight() {
        return mViewHeight;
    }
}

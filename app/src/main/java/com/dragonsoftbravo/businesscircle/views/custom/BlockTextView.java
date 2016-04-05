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

public class BlockTextView extends View {
    private String title;
    private List<ColItem> colItems = new ArrayList<>();
    private Paint titlePaint;
    private Paint colPaint;
    private Paint bgPaint;
    private int mViewHeight, mViewWidth;
    private boolean withPoint = true;

    private boolean isCircle = false;

    public BlockTextView(Context context) {
        super(context);
        init();
    }

    public BlockTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private int itemtMargin = 20;
    private int viewPadding = 20;
    private int pointHeight = 30;
    private int titleTextSize = 40;
    private int blockTextSize = 40;
    private int bgStrokeWidth = 3;
    private int blockWidth = 40;

    private void init() {
        itemtMargin = Screen.dip2px(getContext(), 5);
        viewPadding = Screen.dip2px(getContext(), 5);
        pointHeight = Screen.dip2px(getContext(), 10);
        titleTextSize = Screen.dip2px(getContext(), 14);
        blockTextSize = Screen.dip2px(getContext(), 14);
        bgStrokeWidth = Screen.dip2px(getContext(), 1);
        blockWidth = Screen.dip2px(getContext(), 15);
        titlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        titlePaint.setStyle(Paint.Style.FILL);
        colPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        colPaint.setStyle(Paint.Style.FILL);
        bgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        bgPaint.setStyle(Paint.Style.FILL);
        titlePaint.setColor(Color.parseColor("#000000"));
        titlePaint.setTextSize(titleTextSize);
        colPaint.setTextSize(blockTextSize);
    }

    public void setIsCircle(boolean isCircle) {
        this.isCircle = isCircle;
    }

    public void setWithPoint(boolean withPoint) {
        this.withPoint = withPoint;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setItems(List<ColItem> colItems) {
        this.colItems.clear();
        this.colItems.addAll(colItems);
        requestLayout();
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
        if (!"".equals(title) && title != null) {
            drawTitle(canvas);
        }
        int startX = viewPadding + getTitleWidth();
        for (int i = 0; i < colItems.size(); i++) {
            if (i != 0) startX += itemtMargin;
            if (isCircle) {
                drawCircle(colItems.get(i), startX, canvas);
            } else {
                draw(colItems.get(i), startX, canvas);
            }

            startX += getColItemWidth(colItems.get(i));
        }
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
        bgPaint.setStrokeWidth(bgStrokeWidth);
        canvas.drawPath(path2, bgPaint);
    }

    private void drawTitle(Canvas canvas) {
        canvas.drawText(title, 0, title.length(), viewPadding, getTextY(title, titlePaint), titlePaint);
    }

    private int getTextY(String text, Paint paint) {
        return getCenter().y + (int) paint.measureText("A") / 2;
    }

    public static class ColItem {
        public String text;
        public int textColor;
        public int blockColor;

        public ColItem(int blockColor, String text, int textColor) {
            this.blockColor = blockColor;
            this.text = text + "";
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

    public void draw(ColItem colItem, int startX, Canvas canvas) {
        colPaint.setColor(colItem.blockColor);
        canvas.drawRect(startX, getCenter().y - blockWidth / 2, startX + blockWidth, getCenter().y + blockWidth / 2, colPaint);
        colPaint.setTextSize(blockTextSize);
        colPaint.setColor(colItem.textColor);
        canvas.drawText(colItem.text, 0, colItem.text.length(), startX + blockWidth + blockWidth / 8, getTextY(colItem.text, colPaint), colPaint);
    }

    public void drawCircle(ColItem colItem, int startX, Canvas canvas) {
        colPaint.setColor(colItem.blockColor);
        canvas.drawCircle(startX + blockWidth / 2, getCenter().y, blockWidth / 2, colPaint);
        colPaint.setTextSize(blockTextSize);
        colPaint.setColor(colItem.textColor);
        canvas.drawText(colItem.text, 0, colItem.text.length(), startX + blockWidth + blockWidth / 8, getTextY(colItem.text, colPaint), colPaint);
    }

    public float getColItemWidth(ColItem colItem) {
        return blockWidth + colPaint.measureText(colItem.getText() + "") + blockWidth / 8;
    }

    private int getTitleWidth() {
        if (title == null || "".equals(title)) {
            return 0;
        } else {
            return (int) titlePaint.measureText(title);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        mViewWidth = 2 * viewPadding;
        int titleWidth = getTitleWidth();
        mViewWidth += titleWidth;
        for (ColItem colItem : colItems) {
            mViewWidth += getColItemWidth(colItem);
        }
        mViewWidth += ((colItems.size() - 1) * itemtMargin);
        mViewHeight = withPoint ? blockWidth + 2 * viewPadding + pointHeight : blockWidth + 2 * viewPadding;
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

    public int getmViewHeight() {
        return mViewHeight;
    }
}

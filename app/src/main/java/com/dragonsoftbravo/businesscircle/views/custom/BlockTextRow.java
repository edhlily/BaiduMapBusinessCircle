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

public class BlockTextRow extends View {
    private List<BlockTextView.ColItem> colItems = new ArrayList<>();
    private Paint titlePaint;
    private Paint colPaint;
    private Paint bgPaint;
    private int mViewHeight, mViewWidth;

    public BlockTextRow(Context context) {
        super(context);
        init();
    }

    public BlockTextRow(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private int pointHeight = 30;
    private int titleTextSize = 40;
    private int blockTextSize = 40;
    private int bgStrokeWidth = 3;
    private int blockWidth = 40;

    private void init() {
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

    public void setItems(List<BlockTextView.ColItem> colItems) {
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
        int startX;
        for (int i = 0; i < rowItems.size(); i++) {
            List<BlockTextView.ColItem> row = rowItems.get(i);
            startX = blockWidth / 2;
            for (BlockTextView.ColItem item : row) {
                draw(item, i + 1, startX, canvas);
                startX += getColItemWidth(item);
            }
        }
//        for (int i = 0; i < colItems.size(); i++) {
//            if (i % 3 == 0) {
//                startX = blockWidth / 2;
//            }
//            draw(colItems.get(i), i / 3 + 1, startX, canvas);
//            startX += getColItemWidth(colItems.get(i));
//        }
    }

    private void drawBg(Canvas canvas) {
        bgPaint.setStyle(Paint.Style.FILL);
        bgPaint.setColor(Color.parseColor("#CCFFFFFF"));
        Path path = new Path();
        path.reset();
        path.moveTo(0, 0);
        path.lineTo(mViewWidth, 0);
        path.lineTo(mViewWidth, mViewHeight);
        path.lineTo(0, mViewHeight);
        path.close();
        canvas.drawPath(path, bgPaint);
        Path path2 = new Path();
        path2.reset();
        path2.moveTo(0, 0);
        path2.lineTo(mViewWidth, 0);
        path2.lineTo(mViewWidth, mViewHeight);
        path2.lineTo(0, mViewHeight);
        path2.close();
        bgPaint.setStyle(Paint.Style.STROKE);
        bgPaint.setColor(Color.parseColor("#CCCCCCCC"));
        bgPaint.setStrokeWidth(bgStrokeWidth);
        canvas.drawPath(path2, bgPaint);
    }

    private int getTextY(String text, int startY, Paint paint) {
        return startY + blockWidth / 2 + (int) paint.measureText("A") / 2;
    }

    public void draw(BlockTextView.ColItem colItem, int row, int startX, Canvas canvas) {
        colPaint.setColor(colItem.blockColor);
        canvas.drawRect(startX, blockWidth / 2 + (row - 1) * (blockWidth + blockWidth / 2), startX + blockWidth, blockWidth / 2 + (row - 1) * (blockWidth + blockWidth / 2) + blockWidth, colPaint);
        colPaint.setTextSize(blockTextSize);
        colPaint.setColor(colItem.textColor);
        canvas.drawText(colItem.text, 0, colItem.text.length(), startX + blockWidth + blockWidth / 8, getTextY(colItem.text, blockWidth / 2 + (row - 1) * (blockWidth + blockWidth / 2), colPaint), colPaint);
    }

    public float getColItemWidth(BlockTextView.ColItem colItem) {
        return blockWidth + blockWidth / 8 + colPaint.measureText(colItem.getText() + "") + blockWidth / 8;
    }

    List<List<BlockTextView.ColItem>> rowItems = new ArrayList<>();

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int totalWidth = blockWidth;
        int row = 0;
        rowItems.clear();
        mViewWidth = 0;
        for (BlockTextView.ColItem item : colItems) {
            float itemWidth = getColItemWidth(item);
            if (totalWidth + itemWidth < Screen.SCREEN_WIDTH_PX * 9 / 10) {
                totalWidth += itemWidth;
                List<BlockTextView.ColItem> items;
                if (rowItems.size() > row) {
                    items = rowItems.get(row);
                } else {
                    items = new ArrayList<>();
                    rowItems.add(row, items);
                }
                items.add(item);
            } else {
                row++;
                totalWidth = blockWidth;
                totalWidth += itemWidth;
                List<BlockTextView.ColItem> items = new ArrayList<>();
                items.add(item);
                rowItems.add(row, items);
            }
            mViewWidth = Math.max(totalWidth, mViewWidth);
        }
        /*if (row == 1) {
            mViewWidth = blockWidth;
            for (BlockTextView.ColItem colItem : colItems) {
                mViewWidth += getColItemWidth(colItem);
            }
            mViewWidth += colItems.size() * blockWidth / 8;
        } else {
            for (int i = 0; i < row; i++) {
                int tempWidth = blockWidth;
                for (int j = i * 3; j < (i + 1) * 3; j++) {
                    if (j >= colItems.size()) break;
                    tempWidth += getColItemWidth(colItems.get(j));
                }
                mViewWidth = Math.max(mViewWidth, tempWidth);
            }
        }*/
        mViewHeight = blockWidth / 2 + (row + 1) * (blockWidth + blockWidth / 2);
        setMeasuredDimension(mViewWidth, mViewHeight);
    }

    Point center;

    public int getmViewHeight() {
        return mViewHeight;
    }
}

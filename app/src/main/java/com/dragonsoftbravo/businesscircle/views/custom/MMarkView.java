package com.dragonsoftbravo.businesscircle.views.custom;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.dragonsoftbravo.businesscircle.utils.Screen;


public class MMarkView extends View {
    private Paint paint, textPaint, textBgPaint;
    private int mViewHeight, mViewWidth;
    private int markHeight, markWidth;
    private int markerColor = Color.RED;
    private int markerTextColor = Color.WHITE;
    private int markerTextBgColor = Color.parseColor("#88000000");
    private String text = "测试测试测试测试商圈";

    public MMarkView(Context context) {
        super(context);
        init();
    }

    public MMarkView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public int getMarkerColor() {
        return markerColor;
    }

    public void setMarkerColor(int markerColor) {
        this.markerColor = markerColor;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    private int outCircleRadius;
    private int innerCircleRadius;
    private int dp05;
    private int textSize;
    private int textMargin;

    private void init() {
        markWidth = Screen.dip2px(getContext(), 12);
        markHeight = Screen.dip2px(getContext(), 17.5F);
        mViewWidth = Screen.dip2px(getContext(), 14);
        mViewHeight = Screen.dip2px(getContext(), 17.5F);
        outCircleRadius = Screen.dip2px(getContext(), 6);
        innerCircleRadius = Screen.dip2px(getContext(), 3);
        dp05 = Screen.dip2px(getContext(), 0.5f);
        textSize = Screen.dip2px(getContext(), 12);
        textMargin = Screen.dip2px(getContext(), 5);
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.FILL);
        textBgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textBgPaint.setStyle(Paint.Style.FILL);
        textBgPaint.setColor(Color.RED);
        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setTextSize(textSize);
        textPaint.setTextAlign(Paint.Align.CENTER);
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mViewWidth = w;
        mViewHeight = h;
        outOval = new RectF((mViewWidth - markWidth) / 2, 0, mViewWidth / 2 + markWidth / 2, markWidth);
        innerOval = new RectF(mViewWidth / 2 - innerCircleRadius, markWidth / 2 - innerCircleRadius, mViewWidth / 2 + innerCircleRadius, markWidth / 2 + innerCircleRadius);
    }

    RectF outOval, innerOval;
    Path path = new Path();
    Path path2 = new Path();

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        drawMark(canvas);
        drawTextBg(canvas);
        drawText(canvas);
    }

    private void drawMark(Canvas canvas) {
        paint.setColor(markerColor);
        canvas.drawOval(outOval, paint);
        path.moveTo(mViewWidth / 2 - ((int) (outCircleRadius / Math.sqrt(2))), markWidth / 2 + ((int) (outCircleRadius / Math.sqrt(2))));
        path.quadTo(mViewWidth / 2 - 4 * dp05, markWidth / 2 + outCircleRadius + 4 * dp05, mViewWidth / 2 - 1, markHeight - 1);
        path.lineTo(mViewWidth / 2, markHeight - 1);
        path.lineTo(mViewWidth / 2, markWidth / 2);
        path.close();
        canvas.drawPath(path, paint);
        path2.moveTo(mViewWidth / 2 + ((int) (outCircleRadius / Math.sqrt(2))), markWidth / 2 + ((int) (outCircleRadius / Math.sqrt(2))));
        path2.quadTo(mViewWidth / 2 + 4 * dp05, markWidth / 2 + outCircleRadius + 4 * dp05, mViewWidth / 2 + 1, markHeight - 1);
        path2.lineTo(mViewWidth / 2, markHeight - 1);
        path2.lineTo(mViewWidth / 2, markWidth / 2);
        path2.close();
        canvas.drawPath(path2, paint);
        paint.setColor(Color.WHITE);
        canvas.drawOval(innerOval, paint);
    }

    public void setTextBgColor(int textBgColor) {
        this.markerTextBgColor = textBgColor;
    }

    public void setLargerIcon() {
        markWidth = Screen.dip2px(getContext(), 24);
        markHeight = Screen.dip2px(getContext(), 35);
        mViewWidth = Screen.dip2px(getContext(), 28);
        mViewHeight = Screen.dip2px(getContext(), 35);
        outCircleRadius = Screen.dip2px(getContext(), 12);
    }

    private void drawTextBg(Canvas canvas) {
        textBgPaint.setColor(markerTextBgColor);
        RectF rectF = new RectF(0, markHeight, mViewWidth, mViewHeight);
        canvas.drawRoundRect(rectF, 6 * dp05, 6 * dp05, textBgPaint);
    }

    private void drawText(Canvas canvas) {
        textPaint.setColor(markerTextColor);
        if (text != null && text.length() > 0) {
            float baseline = (mViewHeight + markHeight - textPaint.getFontMetrics().bottom - textPaint.getFontMetrics().top) / 2;
            canvas.drawText(text, 0, text.length(), mViewWidth / 2, baseline, textPaint);
        }
    }

    private float getTextWidth() {
        if (text == null || text.length() < 1) {
            return 0;
        } else {
            return textPaint.measureText(text);
        }
    }

    private float getTextHeight() {
        if (text == null || text.length() < 1) {
            return 0;
        } else {
            return textPaint.measureText(text, 0, 1);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int textWidth = 0;
        if (text != null && text.length() > 0) {
            textWidth = (int) (getTextWidth() + 2 * textMargin);
        }
        mViewWidth = Math.max(markWidth, textWidth);
        mViewHeight = (int) (markHeight + getTextHeight() + 2 * textMargin);
        setMeasuredDimension(mViewWidth, mViewHeight);
    }

    public int getmViewHeight() {
        return mViewHeight;
    }

    public int getMarkHeight() {
        return markHeight;
    }
}

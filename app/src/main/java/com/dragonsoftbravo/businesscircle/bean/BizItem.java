package com.dragonsoftbravo.businesscircle.bean;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.View;

import com.dragonsoftbravo.businesscircle.MApplication;
import com.dragonsoftbravo.businesscircle.views.custom.MMarkView;

public class BizItem extends Item {
    public static final int TYPE_DEFAULT = 0;
    public static final int TYPE_MARK1 = 1;

    @Override
    public Bitmap getBitmap() {
        Bitmap bitmap;
        switch (markType) {
            default:
                MMarkView mMarkView3 = new MMarkView(MApplication.get());
                mMarkView3.setMarkerColor(Color.RED);
                mMarkView3.setText(getName());
                mMarkView3.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
                mMarkView3.layout(0, 0, mMarkView3.getMeasuredWidth(), mMarkView3.getMeasuredHeight());
                if (mMarkView3.getDrawingCache() == null)
                    mMarkView3.buildDrawingCache();
                bitmap = mMarkView3.getDrawingCache();
                f1 = 0.5f;
                f2 = (float) mMarkView3.getMarkHeight() / (float) mMarkView3.getmViewHeight();
                break;
        }
        return bitmap;
    }
}

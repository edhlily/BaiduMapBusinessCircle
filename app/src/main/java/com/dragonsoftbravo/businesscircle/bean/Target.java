package com.dragonsoftbravo.businesscircle.bean;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.model.LatLng;
import com.dragonsoftbravo.businesscircle.utils.MarkerCache;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;

public abstract class Target implements Serializable {
    protected float f1, f2;
    protected int markType;
    protected double lat;
    protected double lng;

    public int getMarkType() {
        return markType;
    }

    public void setMarkType(int markType) {
        if (this.markType != markType) {
            MarkerCache.get().remove(this);
        }
        this.markType = markType;
    }

    public abstract Bitmap getBitmap();

    public LatLng getPosition() {
        if (lat < 3 || lat > 54) return null;
        if (lng < 73 || lng > 174) return null;
        return new LatLng(lat, lng);
    }

    public void setPosition(LatLng position) {
        this.lat = position.latitude;
        this.lng = position.longitude;
    }

    public MarkerOptions getMarkerOptions() {
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(getPosition());
        BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(getBitmap());
        markerOptions.anchor(f1, f2);
        markerOptions.icon(bitmapDescriptor);
        return markerOptions;
    }

    protected Bitmap compress(Bitmap bitmap) {
        if (true) return bitmap;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, baos);
        bitmap.recycle();
        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        bitmap = BitmapFactory.decodeStream(bais, null, null);
        try {
            bais.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            baos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    @Override
    public String toString() {
        return "Item{" +
                "f1=" + f1 +
                ", f2=" + f2 +
                ", markType=" + markType +
                ", lat=" + lat +
                ", lng=" + lng +
                '}';
    }
}

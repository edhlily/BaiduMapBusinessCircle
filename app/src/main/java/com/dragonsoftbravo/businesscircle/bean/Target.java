package com.dragonsoftbravo.businesscircle.bean;

import android.graphics.Bitmap;

import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.model.LatLng;
import com.dragonsoftbravo.businesscircle.views.MarkerCache;

import java.io.Serializable;

public abstract class Target implements Serializable {
    //Item中心位置相对图标的位置（0.5和0.5表示中心点正好在图标中心点）
    //f1表示水平中心位置，f2表示垂直中心位置
    protected float f1, f2;
    //Mark的类型
    protected int markType;
    //中心经纬度
    protected double lat, lng;
    //是否显示
    protected boolean visiable;
    //编号和名称，code可以改成Int型ID
    protected String code, name;
    //是否是显著的点，true的话图标会更大更显眼
    protected boolean target;

    int color;

    public int getMarkType() {
        return markType;
    }

    public void setMarkType(int markType) {
        if (this.markType != markType) {
            MarkerCache.get().remove(this);
        }
        this.markType = markType;
    }

    /**
     * 通过类型获得Mark图标
     *
     * @return
     */
    public abstract Bitmap getBitmap();

    public LatLng getPosition() {
        return new LatLng(lat, lng);
    }

    public void setPosition(LatLng position) {
        this.lat = position.latitude;
        this.lng = position.longitude;
    }

    public boolean isVisiable() {
        return visiable;
    }

    public void setVisiable(boolean visiable) {
        this.visiable = visiable;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isTarget() {
        return target;
    }

    public void setTarget(boolean target) {
        if (this.target ^ target) {
            MarkerCache.get().remove(this);
        }
        this.target = target;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public MarkerOptions getMarkerOptions() {
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(getPosition());
        BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(getBitmap());
        markerOptions.anchor(f1, f2);
        markerOptions.icon(bitmapDescriptor);
        return markerOptions;
    }


}

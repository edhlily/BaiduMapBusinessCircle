package com.dragonsoftbravo.businesscircle.bean;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;

import java.util.Collection;

//表示圈
public abstract class Circle extends Target {
    //半径
    double radius = 1000;
    //圈的颜色
    int bgColor = 0x99FF0000;
    //圈的范围，东北角经纬度，西南角经纬度
    private double nelatitude, nelongtitude, swlatitude, swlongtitude;

    public void setBounds(LatLngBounds bounds) {
        this.nelatitude = bounds.northeast.latitude;
        this.nelongtitude = bounds.northeast.longitude;
        this.swlatitude = bounds.southwest.latitude;
        this.swlongtitude = bounds.southwest.longitude;
        this.lat = bounds.getCenter().latitude;
        this.lng = bounds.getCenter().longitude;
    }

    public LatLngBounds getBounds() {
        return new LatLngBounds.Builder()
                .include(new LatLng(nelatitude, nelongtitude))
                .include(new LatLng(swlatitude, swlongtitude)).build();
    }

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    public int getBgColor() {
        return bgColor;
    }

    public void setBgColor(int bgColor) {
        this.bgColor = bgColor;
    }

    public abstract Collection<? extends Item> getItems();

    @Override
    public boolean equals(Object o) {
        if (o instanceof Circle) {
            if (getName() == null || ((Circle) o).getName() == null)
                return super.equals(o);
            return getName().equals(((Circle) o).getName());
        }
        return false;
    }
}

package com.dragonsoftbravo.businesscircle.bean;

import com.baidu.mapapi.model.LatLngBounds;

import java.util.Collection;

public abstract class Circle<T extends Item> extends Target {
    protected boolean visiable;

    public abstract void setBounds(LatLngBounds bounds);

    public abstract LatLngBounds getBounds();

    public abstract void setRadius(double radius);

    public abstract double getRadius();

    public abstract Collection<T> getItems();

    public abstract int getSize();

    public abstract String getClusterName();

    public boolean isVisiable() {
        return visiable;
    }

    public void setVisiable(boolean visiable) {
        this.visiable = visiable;
    }

    @Override
    public int hashCode() {
        return getClusterName() == null ? super.hashCode() : getClusterName().hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Circle) {
            if (getClusterName() == null || ((Circle) o).getClusterName() == null)
                return super.equals(o);
            return getClusterName().equals(((Circle) o).getClusterName());
        }
        return false;
    }
}

package com.dragonsoftbravo.businesscircle.views;

import android.support.v4.util.LruCache;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.baidu.mapapi.utils.DistanceUtil;
import com.dragonsoftbravo.businesscircle.bean.BizCircle;
import com.dragonsoftbravo.businesscircle.bean.BizItem;
import com.dragonsoftbravo.businesscircle.utils.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CircleCache {
    //最多换成10个城市的商圈
    public static final LruCache<String, List<BizCircle>> mCache = new LruCache<>(10);
    private BaiduMap mMap;

    public CircleCache(BaiduMap mMap) {
        this.mMap = mMap;
    }

    public static void clearCache() {
        mCache.evictAll();
    }

    /**
     * 拿到某个城市的商圈数据，缓存没有则从数据源（网络/数据库）获取
     *
     * @param city
     * @return
     */
    public List<BizCircle> getCircles(String city) {
        List<BizCircle> results;
        results = mCache.get(city);
        if (results == null) {
            results = loadCircles(city);
            if (results != null && results.size() > 0) {
                mCache.put(city, results);
            }
        }
        return results;
    }

    /**
     * 从数据源获取商圈数据
     *
     * @param cityCode
     * @return
     */
    public List<BizCircle> loadCircles(String cityCode) {
        CircleData circleData = new CircleData();
        List<BizCircle> tmpList = circleData.fetch(cityCode);
        if (tmpList == null) {
            return null;
        }
        List<BizCircle> circles = new ArrayList<>();
        Map<String, BizCircle> circleMap = new HashMap<>();
        LatLngBounds bounds;
        for (BizCircle bizCircle : tmpList) {
            bounds = getBonds(bizCircle);
            if (bounds != null) {
                bizCircle.setBounds(getBonds(bizCircle));
                circles.add(bizCircle);
            } else {
                Logger.e("error bizCircle :" + bizCircle.toString());
            }
        }
        Collections.sort(circles, new Comparator<BizCircle>() {
            @Override
            public int compare(BizCircle lhs, BizCircle rhs) {
                return (int) (rhs.getRadius() - lhs.getRadius());
            }
        });
        return circles;
    }

    private static LatLng lne, lsw;
    private static double minRadius = 0.002;
    private static List<LatLng> boundsPos = new ArrayList<>();

    private LatLngBounds getBonds(BizCircle circle) {
        boundsPos.clear();
        LatLngBounds.Builder llbb = new LatLngBounds.Builder();
        if (circle.getSize() == 1) {
            LatLng llCenter = null;
            for (BizItem t1 : circle.getItems()) {
                llCenter = t1.getPosition();
            }
            if (llCenter != null) {
                lne = new LatLng(llCenter.latitude + minRadius, llCenter.longitude + minRadius);
                lsw = new LatLng(llCenter.latitude - minRadius, llCenter.longitude - minRadius);
                boundsPos.add(lne);
                boundsPos.add(lsw);
            }
        } else {
            for (BizItem t1 : circle.getItems()) {
                if (t1.getPosition() != null) {
                    boundsPos.add(t1.getPosition());
                }
            }
        }

        if (boundsPos.size() == 0) return null;
        if (boundsPos.size() == 1) {
            boundsPos.clear();
            lne = new LatLng(boundsPos.get(0).latitude + minRadius, boundsPos.get(0).longitude + minRadius);
            lsw = new LatLng(boundsPos.get(0).latitude - minRadius, boundsPos.get(0).longitude - minRadius);
            boundsPos.add(lne);
            boundsPos.add(lsw);
        }

        for (LatLng latLng : boundsPos) {
            llbb.include(latLng);
        }
        LatLngBounds llb = llbb.build();
        circle.setRadius(DistanceUtil.getDistance(llb.getCenter(), llb.northeast));
        LatLng lne = llb.northeast;
        LatLng lsw = llb.southwest;
        LatLng lCenter = llb.getCenter();
        android.graphics.Point p1 = mMap.getProjection().toScreenLocation(lne);
        android.graphics.Point p2 = mMap.getProjection().toScreenLocation(lsw);
        android.graphics.Point pCenter = mMap.getProjection().toScreenLocation(lCenter);
        double maxR = Math.sqrt((p1.x - p2.x) * (p1.x - p2.x) + (p2.y - p1.y) * (p2.y - p1.y)) / 2;
        p1.x = (int) (pCenter.x + maxR);
        p1.y = (int) (pCenter.y - maxR);
        p2.x = (int) (pCenter.x - maxR);
        p2.y = (int) (pCenter.y + maxR);
        lne = mMap.getProjection().fromScreenLocation(p1);
        lsw = mMap.getProjection().fromScreenLocation(p2);
        return new LatLngBounds.Builder().include(lne).include(lsw).build();
    }

    private LatLng center(LatLng a, LatLng b) {
        return new LatLng(Math.abs(a.latitude + b.latitude) / 2, Math.abs(a.longitude + b.longitude) / 2);
    }

}

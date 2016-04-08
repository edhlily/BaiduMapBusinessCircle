package com.dragonsoftbravo.businesscircle.views;

import android.app.Activity;
import android.widget.Toast;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.CircleOptions;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.dragonsoftbravo.businesscircle.bean.BizCircle;
import com.dragonsoftbravo.businesscircle.bean.BizItem;
import com.dragonsoftbravo.businesscircle.utils.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingDeque;

public class CircleRenderer implements BaiduMap.OnMapClickListener, BaiduMap.OnMapLongClickListener, BaiduMap.OnMarkerClickListener {
    private final BaiduMap mMap;
    private Map<Overlay, BizCircle> mOverlayToBizCircle = new HashMap<>();
    private Map<BizCircle, Overlay> mBizCircleToOverlay = new HashMap<>();

    private Map<Overlay, BizCircle> mCenterMarkerToBizCircle = new HashMap<>();
    private Map<BizCircle, Overlay> mBizCircleToCenterMarker = new HashMap<>();

    private Map<Overlay, BizItem> mMarkerToBizItem = new HashMap<>();
    private Map<BizItem, Overlay> mBizItemToMarker = new HashMap<>();
    RenderBizCircleThread renderBizCircleThread;
    RenderBizItemThread renderBizItemThread;
    RemoveOverlayThread removeOverlayThread;
    private int circleType = BizCircle.TYPE_DEFAULT;

    public void setBizCircleType(int circleType) {
        this.circleType = circleType;
    }

    public int getBizCircleType() {
        return circleType;
    }

    private int circleBizItemType = BizItem.TYPE_DEFAULT;

    public void setBizCircleBizItemType(int circleBizItemType) {
        this.circleBizItemType = circleBizItemType;
        for (BizCircle circle : allBizCircle) {
            for (BizItem item : circle.getItems()) {
                if (item.getMarkType() != circleBizItemType) {
                    removeOverlayThread.removeBizItem(item);
                    item.setMarkType(circleBizItemType);
                }
            }
        }
    }

    public int getBizCircleBizItemType() {
        return circleBizItemType;
    }

    Activity activity;

    public CircleRenderer(Activity activity, BaiduMap map) {
        this.activity = activity;
        this.mMap = map;
        mMap.setOnMapClickListener(this);
        mMap.setOnMapLongClickListener(this);
        mMap.setOnMarkerClickListener(this);
        removeOverlayThread = new RemoveOverlayThread();
        removeOverlayThread.start();
        renderBizItemThread = new RenderBizItemThread();
        renderBizItemThread.start();
        renderBizCircleThread = new RenderBizCircleThread();
        renderBizCircleThread.start();
    }

    public void clear() {
        mMap.clear();
        circleMap.clear();
        mBizItemToMarker.clear();
        mMarkerToBizItem.clear();
        mBizCircleToOverlay.clear();
        mOverlayToBizCircle.clear();
        mCenterMarkerToBizCircle.clear();
        mBizCircleToCenterMarker.clear();
        MarkerCache.get().clear();
        System.gc();
    }

    public void release() {
        removeOverlayThread.stopRun();
        renderBizItemThread.stopRun();
        renderBizCircleThread.stopRun();
        clear();
    }

    @Override
    public void onMapLongClick(LatLng latLng) {

    }

    @Override
    public void onMapClick(LatLng latLng) {
        onClick(latLng);
    }

    @Override
    public boolean onMapPoiClick(MapPoi mapPoi) {
        onClick(mapPoi.getPosition());
        return false;
    }

    private boolean onClick(LatLng latLng) {
        List<BizCircle> circles = new ArrayList<>();
        for (BizCircle circle : mBizCircleToOverlay.keySet()) {
            if (circle.getBounds().contains(latLng)) {
                circles.add(circle);
            }
        }
        if (circles.size() > 0) {
            Collections.sort(circles, new Comparator<BizCircle>() {
                @Override
                public int compare(BizCircle lhs, BizCircle rhs) {
                    return (int) (rhs.getRadius() - lhs.getRadius());
                }
            });
            showBizCircle(circles.get(circles.size() - 1));
            Toast.makeText(activity, circles.get(circles.size() - 1).getName(), Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }

    BizCircle selectedBizCircle;

    public void clearTarget() {
        if (selectedBizCircle != null) {
            selectedBizCircle.setTarget(false);
            removeBizCircle(selectedBizCircle);
        }
        selectedBizCircle = null;
        if (getCurBizCircle() != null) {
            onBizCirclesChanged(getCurBizCircle());
        }
    }

    private void showBizCircle(BizCircle circle) {
        if (selectedBizCircle != null) {
            removeBizCircle(selectedBizCircle);
            selectedBizCircle.setTarget(false);
            selectedBizCircle.setMarkType(circle.getMarkType());
        }
        selectedBizCircle = circle;
        double circleWidth = 2 * circle.getRadius();
        double bilichi = circleWidth / CircleManager.screenMWidth;

        int zoomPosition = CircleManager.ms.length;
        double minMs = Double.MAX_VALUE;
        for (int i = 0; i < CircleManager.ms.length; i++) {
            if (Math.abs(bilichi - CircleManager.ms[i]) < minMs) {
                zoomPosition = i;
                minMs = Math.abs(bilichi - CircleManager.ms[i]);
            }
        }
        float newZoom = CircleManager.zs[zoomPosition] - 0.5f;
        MapStatusUpdate u = MapStatusUpdateFactory.newMapStatus(new MapStatus.Builder().target(circle.getPosition()).build());
        mMap.animateMapStatus(u, 1000);
        removeBizCircle(circle);
        circle.setTarget(true);
        onBizCirclesChanged(getCurBizCircle());
    }

    public void showBizCircleInBizCirclename(String circlename) {
        for (BizCircle circle : allBizCircle) {
            if (circle.getName().equals(circlename)) {
                showBizCircle(circle);
                break;
            }
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        if (mMarkerToBizItem.containsKey(marker)) {
            Toast.makeText(activity, mMarkerToBizItem.get(marker).getName(), Toast.LENGTH_SHORT).show();
            return true;
        } else if (mCenterMarkerToBizCircle.containsKey(marker)) {
            onClick(mCenterMarkerToBizCircle.get(marker).getPosition());
        }
        return true;
    }

    Map<String, BizCircle> circleMap = new HashMap<>();
    List<BizCircle> allBizCircle;

    boolean isShowBizItem = true;

    public List<BizCircle> getCurBizCircle() {
        return allBizCircle;
    }

    public void onBizCirclesChanged(final List<BizCircle> circles) {
        Logger.e("onBizCirclesChanged : " + circles.size() + ",mBizCircle:" + mBizCircleToOverlay.size() + ",mBizItem:" + mBizItemToMarker.size());
        allBizCircle = circles;
        for (BizCircle circle : circles) {
            if (circle.getMarkType() != circleType) {
                removeOverlayThread.removeBizCircle(circle);
                circle.setMarkType(circleType);
            }
            circle.setVisiable(getScreenRadius(circle) > 50 || circle.isTarget());
            renderBizCircleThread.render(circle);
            if (isShowBizItem) {
                for (BizItem t : circle.getItems()) {
                    if (circle.isTarget()) {
                        t.setVisiable(true);
                    } else {
                        t.setVisiable(isShowBizItem);
                    }
                    renderBizItemThread.render(t);
                }
            }
        }
    }


    private double getScreenRadius(BizCircle c) {
        android.graphics.Point pCenter = mMap.getProjection().toScreenLocation(c.getPosition());
        android.graphics.Point pRightTop = mMap.getProjection().toScreenLocation(c.getBounds().northeast);
        return Math.sqrt(distanceSquared(pCenter, pRightTop));
    }


    private boolean circleInScreen(BizCircle circle) {
        for (BizItem t : circle.getItems()) {
            LatLngBounds latLngBounds = mMap.getMapStatus().bound;
            LatLng latLng = t.getPosition();
            if (latLngBounds.contains(latLng)) {
                return true;
            }
        }
        return false;
    }

    private boolean itemInScreen(BizItem t) {
        return mMap.getMapStatus().bound.contains(t.getPosition());
    }

    private double distanceSquared(android.graphics.Point a, android.graphics.Point b) {
        return (a.x - b.x) * (a.x - b.x) + (a.y - b.y) * (a.y - b.y);
    }

    public class RemoveOverlayThread extends Thread {
        final LinkedBlockingDeque<Overlay> deque = new LinkedBlockingDeque<>();
        boolean isRemoveOverlay = true;

        public void stopRun() {
            isRemoveOverlay = false;
            synchronized (deque) {
                deque.notify();
            }
        }

        public void remove(Overlay overlay) {
            if (overlay != null) {
                deque.addLast(overlay);
            }
        }

        private void removeBizItem(BizItem t) {
            Overlay o = mBizItemToMarker.get(t);
            remove(o);
            mBizItemToMarker.remove(t);
            mMarkerToBizItem.remove(o);
        }

        private void removeBizCircle(BizCircle c) {
            circleMap.remove(c.getName());
            Overlay m = mBizCircleToCenterMarker.get(c);
            if (m != null) {
                remove(m);
                mCenterMarkerToBizCircle.remove(m);
                mBizCircleToCenterMarker.remove(c);
            }
            m = mBizCircleToOverlay.get(c);
            if (m != null) {
                remove(m);
                mOverlayToBizCircle.remove(m);
                mBizCircleToOverlay.remove(c);
            }
        }

        @Override
        public void run() {
            try {
                while (isRemoveOverlay) {
                    perform(deque.takeFirst());
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        public void perform(Overlay o) {
            o.remove();
        }
    }

    public void removeBizItem(BizItem t) {
        removeOverlayThread.removeBizItem(t);
    }

    public void removeBizCircle(BizCircle circle) {
        removeOverlayThread.removeBizCircle(circle);
    }

    public class RenderBizItemThread extends Thread {
        final LinkedBlockingDeque<BizItem> deque = new LinkedBlockingDeque<>();
        boolean isRenderBizItem = true;

        public void stopRun() {
            isRenderBizItem = false;
            synchronized (deque) {
                deque.notify();
            }
        }

        public void render(BizItem sp) {
            if (deque.contains(sp)) {
                deque.remove(sp);
            }
            deque.addLast(sp);
        }

        @Override
        public void run() {
            try {
                while (isRenderBizItem) {
                    perform(deque.takeLast());
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }


        public void perform(BizItem t) {
            BitmapDescriptor bd;
            if (mBizItemToMarker.containsKey(t)) {
                if (!itemInScreen(t) || !t.isVisiable()) {
                    removeOverlayThread.removeBizItem(t);
                }
                return;
            }
            if (!t.isVisiable()) return;
            if (!itemInScreen(t)) return;
            if (MarkerCache.get().get(t) == null) return;
            Overlay marker = mMap.addOverlay(MarkerCache.get().get(t));
            mBizItemToMarker.put(t, marker);
            mMarkerToBizItem.put(marker, t);
        }
    }

    private boolean showBizCircle = true;

    public void showBizCircle(boolean showBizCircle) {
        this.showBizCircle = showBizCircle;
    }

    public class RenderBizCircleThread extends Thread {
        boolean isRenderBizCircle = true;
        final LinkedBlockingDeque<BizCircle> deque = new LinkedBlockingDeque<>();

        public void stopRun() {
            isRenderBizCircle = false;
            synchronized (deque) {
                deque.notify();
            }
        }

        public void render(BizCircle circle) {
            if (deque.contains(circle)) {
                deque.remove(circle);
            }
            deque.addLast(circle);
        }

        @Override
        public void run() {
            try {
                BizCircle cp;
                while (isRenderBizCircle) {
                    performBizCircle(deque.takeLast());
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }


        private void buildBizCircle(final BizCircle circle) {
            if (!showBizCircle && mBizCircleToOverlay.containsKey(circle)) {
                Overlay o = mBizCircleToOverlay.get(circle);
                mBizCircleToOverlay.remove(circle);
                mOverlayToBizCircle.remove(o);
                o.remove();
            } else if (showBizCircle && !mBizCircleToOverlay.containsKey(circle)) {
                OverlayOptions ooBizCircle = new CircleOptions().fillColor(circle.getBgColor())
                        .center(circle.getPosition()).radius((int) circle.getRadius());

                Overlay marker0 = mMap.addOverlay(ooBizCircle);
                mOverlayToBizCircle.put(marker0, circle);
                mBizCircleToOverlay.put(circle, marker0);
            }
        }

        private void performBizCircle(final BizCircle circle) {
            boolean circleInScreen = circleInScreen(circle);
            if (circleMap.containsKey(circle.getName())) {
                if (!circle.isVisiable() || !circleInScreen) {
                    //移除circle
                    circleMap.remove(circle.getName());
                    removeOverlayThread.removeBizCircle(circle);
                } else {
                    buildBizCircle(circle);
                }
                return;
            }
            if (!circle.isVisiable()) return;
            if (!circleInScreen) return;
            if (MarkerCache.get().get(circle) == null) return;
            circleMap.put(circle.getName(), circle);
            Overlay marker = mMap.addOverlay(MarkerCache.get().get(circle));
            mCenterMarkerToBizCircle.put(marker, circle);
            mBizCircleToCenterMarker.put(circle, marker);
            buildBizCircle(circle);
        }
    }
}

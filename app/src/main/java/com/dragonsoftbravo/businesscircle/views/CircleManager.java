package com.dragonsoftbravo.businesscircle.views;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Build;
import android.widget.Toast;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.baidu.mapapi.utils.DistanceUtil;
import com.dragonsoftbravo.businesscircle.bean.BizCircle;
import com.dragonsoftbravo.businesscircle.bean.BizItem;
import com.dragonsoftbravo.businesscircle.utils.Logger;

import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class CircleManager implements BaiduMap.OnMapStatusChangeListener, BaiduMap.OnMapLoadedCallback {
    public static final String CURCITY = "com.dragonsoftbravo.pss.CURCITY";
    public static final int[] ms = new int[]{10, 20, 50, 100, 200, 500, 1000, 2000, 5000, 10000, 20000, 25000, 50000, 100000, 200000};
    public static final int[] zs = new int[]{20, 19, 18, 17, 16, 15, 14, 13, 12, 11, 10, 9, 8, 7, 6};
    public static double screenMWidth;
    private CircleCache circleCache;
    private final ReadWriteLock circleCacheLock = new ReentrantReadWriteLock();
    private final ReadWriteLock mClusterTaskLock = new ReentrantReadWriteLock();
    private CircleRenderer mRenderer;
    public static String curCity = "上海市";
    //district为null时，是商圈界面的manager；不为null时，是门店界面的manager。
    private BaiduMap mMap;
    private ClusterTask mClusterTask;

    private Activity activity;
    ProgressDialog progressDialog;

    public CircleManager(Activity activity, BaiduMap map) {
        this.activity = activity;
        mMap = map;
        mMap.setOnMapStatusChangeListener(this);
        mMap.setOnMapLoadedCallback(this);
        circleCache = new CircleCache(mMap);
        mClusterTask = new ClusterTask();
        mRenderer = new CircleRenderer(activity, map);
    }


    public void clearTarget() {
        mRenderer.clearTarget();
    }

    public void cluster() {
        cluster(curCity, null, false);
    }

    /**
     * @param city   城市code
     * @param moveto 是否定位到改城市
     */
    public void cluster(String city, LatLng moveto, boolean showProgress) {
        Logger.d("mClusterTask：" + isClustering());
        if (showProgress) {
            dismiss();
            progressDialog = ProgressDialog.show(activity, null, "Loading...");
        }
        mClusterTaskLock.writeLock().lock();
        try {
            mClusterTask.cancel(true);
            mClusterTask = new ClusterTask(moveto, 1000);
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
                mClusterTask.execute(city);
            } else {
                mClusterTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, city);
            }
        } finally {
            mClusterTaskLock.writeLock().unlock();
        }
    }

    public void renderClear() {
        mRenderer.clear();
    }

    boolean clustering;

    public boolean isClustering() {
        return clustering;
    }

    private class ClusterTask extends AsyncTask<String, List<BizCircle>, List<BizCircle>> {
        LatLng moveToPos;
        int moveTime;

        public ClusterTask() {

        }

        public ClusterTask(LatLng moveToPos, int moveTime) {
            this.moveToPos = moveToPos;
            this.moveTime = moveTime;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            clustering = true;
            Logger.d("clustering");
        }

        @Override
        protected List<BizCircle> doInBackground(String... citys) {
            clustering = true;
            circleCacheLock.readLock().lock();
            List<BizCircle> districts;
            try {
                districts = circleCache.getCircles(citys[0]);
                publishProgress(districts);
                return districts;
            } finally {
                circleCacheLock.readLock().unlock();
            }
        }

        @Override
        protected void onProgressUpdate(List<BizCircle>... values) {
            if (isCancelled()) return;
            if (moveToPos == null) return;
            clustering = false;
            //定位到当前城市
            List<BizCircle> districts = values[0];
            if (districts == null || districts.size() < 1)
                return;
            if (moveToPos.latitude == 0) {
                //清除重置上一次的mark
                for (BizCircle district : districts) {
                    district.setMarkType(BizCircle.TYPE_DEFAULT);
                    for (BizItem bizItem : district.getItems()) {
                        bizItem.setMarkType(BizItem.TYPE_DEFAULT);
                    }
                }
                //移动到商圈中心
                LatLng circleCenter;
                LatLngBounds.Builder llbb = new LatLngBounds.Builder();
                int avaliableCircleSize = 0;
                for (BizCircle d : districts) {
                    d.setTarget(false);
                    if (d.getPosition() == null) continue;
                    avaliableCircleSize++;
                    llbb.include(d.getPosition());
                }
                if (avaliableCircleSize > 1) {
                    circleCenter = llbb.build().getCenter();
                } else {
                    circleCenter = districts.get(0).getPosition();
                }
                MapStatusUpdate u = MapStatusUpdateFactory.newMapStatus(new MapStatus.Builder().target(circleCenter).zoom(12).build());
                mMap.animateMapStatus(u);
            } else {
                MapStatus.Builder msb = new MapStatus.Builder().target(moveToPos);
                if (mMap.getMapStatus().zoom < 14) {
                    msb.zoom(14f);
                }
                mMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(msb.build()), 1000);
            }
        }

        @Override
        protected void onPostExecute(List<BizCircle> clusters) {
            if (isCancelled()) return;
            if (clusters == null) {
                return;
            }
            if (clusters.size() == 0) {
                Toast.makeText(activity, "没有商圈数据", Toast.LENGTH_SHORT).show();
            }
            mClusterTask.cancel(true);
            mRenderer.onBizCirclesChanged(clusters);
            dismiss();
        }
    }

    public void dismiss() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    @Override
    public void onMapLoaded() {
        LatLng right = new LatLng(mMap.getMapStatus().bound.southwest.latitude, mMap.getMapStatus().bound.northeast.longitude);
        double screenMapWidth = DistanceUtil.getDistance(mMap.getMapStatus().bound.southwest, right);
        screenMWidth = screenMapWidth / ms[20 - (int) mMap.getMapStatus().zoom];
        cluster(curCity, new LatLng(0, 0), true);
    }

    //释放资源
    public void release() {
        if (mRenderer != null) mRenderer.release();
    }

    //获取当前正在展示的商圈信息
    public List<BizCircle> getCurBizCircle() {
        return mRenderer.getCurBizCircle();
    }

    //移除店铺
    public void removeBizItem(BizItem t) {
        mRenderer.removeBizItem(t);
    }

    //移除商圈
    public void removeCluster(BizCircle district) {
        mRenderer.removeBizCircle(district);
    }

    @Override
    public void onMapStatusChangeStart(MapStatus mapStatus) {
    }

    @Override
    public void onMapStatusChange(MapStatus mapStatus) {
    }

    @Override
    public void onMapStatusChangeFinish(MapStatus mapStatus) {
        cluster();
    }
}

package com.dragonsoftbravo.businesscircle.views;

import android.util.LruCache;

import com.baidu.mapapi.map.MarkerOptions;
import com.dragonsoftbravo.businesscircle.bean.Target;

/**
 * Mark缓存
 */
public class MarkerCache {
    private static MarkerCache markerCache = new MarkerCache();

    public static MarkerCache get() {
        return markerCache;
    }

    LruCache<Target, MarkerOptions> markBitmapsCache = new LruCache<Target, MarkerOptions>(30) {
        @Override
        protected MarkerOptions create(Target key) {
            return key.getMarkerOptions();
        }

        @Override
        protected int sizeOf(Target key, MarkerOptions value) {
            return 1;
        }

        @Override
        protected void entryRemoved(boolean evicted, Target key, MarkerOptions oldValue, MarkerOptions newValue) {
            oldValue.getIcon().recycle();
        }
    };

    public MarkerOptions get(Target key) {
        return markBitmapsCache.get(key);
    }

    public void remove(Target key) {
        markBitmapsCache.remove(key);
    }

    public void clear() {
        markBitmapsCache.evictAll();
    }

    public int size() {
        return markBitmapsCache.size();
    }
}

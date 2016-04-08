package com.dragonsoftbravo.businesscircle.views;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapView;
import com.dragonsoftbravo.businesscircle.R;
import com.dragonsoftbravo.businesscircle.utils.Screen;

public class MainActivity extends AppCompatActivity {
    MapView mapView;
    BaiduMap baiduMap;
    CircleManager manager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //这两行代码最好放到Application的onCreate里面。
        SDKInitializer.initialize(getApplicationContext());
        Screen.init(getApplicationContext());

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mapView = (MapView) findViewById(R.id.mapView);
        baiduMap = mapView.getMap();
        manager = new CircleManager(this,baiduMap);
    }
}

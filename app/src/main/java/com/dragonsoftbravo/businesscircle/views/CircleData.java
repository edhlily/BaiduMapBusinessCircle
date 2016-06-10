package com.dragonsoftbravo.businesscircle.views;

import android.graphics.Color;

import com.baidu.mapapi.model.LatLng;
import com.dragonsoftbravo.businesscircle.bean.BizCircle;
import com.dragonsoftbravo.businesscircle.bean.BizItem;

import java.util.ArrayList;
import java.util.List;

public class CircleData {

    public List<BizCircle> fetch(String city) {
        //{lng: 121.409451, lat: 31.268828}
        double baseLng = 121.409451;
        double baseLat = 31.268828;
        List<BizCircle> circles = new ArrayList<>();
        BizCircle circle;
        BizItem item;
        for (int i = 0; i < 4; i++) {
            double circleLeftTopLng = baseLng + (i * 0.08);
            for (int j = 0; j < 4; j++) {
                double circleLeftTopLat = baseLat - (j * 0.08);
                circle = new BizCircle();
                circle.setCode("bizCircleCode" + i * 4 + j);
                circle.setName("商圈" + i * 4 + j);
                circle.setColor(Color.GREEN);
                circle.setBgColor(getCircleColoe());
                for (int k = 0; k < 2; k++) {
                    double itemLng = circleLeftTopLng + (0.01 * k);
                    for (int m = 0; m < 2; m++) {
                        double itemLat = circleLeftTopLat - (0.01 * m);
                        item = new BizItem();
                        item.setCode("bizItemCode" + k * 4 + m);
                        item.setName("店铺" + i + ":" + k * 4 + m);
                        item.setColor(Color.RED);
                        item.setPosition(new LatLng(itemLat, itemLng));
                        circle.getItems().add(item);
                    }
                }
                circles.add(circle);
            }
        }
        return circles;
    }

    int getCircleColoe() {
        int random = (int) (Math.random() * 5);
        switch (random) {
            case 0:
                return 0x55FF0000;
            case 1:
                return 0x5500FF00;
            case 2:
                return 0x550000FF;
            case 3:
                return 0x55FFFF00;
            default:
                return 0x55FF00FF;
        }
    }
}

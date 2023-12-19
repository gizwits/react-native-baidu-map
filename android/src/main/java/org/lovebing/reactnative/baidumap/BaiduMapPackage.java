/*
 * Copyright (c) 2016-present, lovebing.net.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package org.lovebing.reactnative.baidumap;

import android.os.Build;
import android.os.Looper;

import androidx.annotation.MainThread;
import androidx.annotation.RequiresApi;

import com.baidu.mapapi.SDKInitializer;
import com.facebook.react.ReactPackage;
import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.uimanager.ViewManager;

import org.lovebing.reactnative.baidumap.module.BaiduMapManager;
import org.lovebing.reactnative.baidumap.module.GeolocationModule;
import org.lovebing.reactnative.baidumap.module.GetDistanceModule;
import org.lovebing.reactnative.baidumap.module.MapAppModule;
import org.lovebing.reactnative.baidumap.module.PoiSearchModule;
import org.lovebing.reactnative.baidumap.module.PoiSugSearchModule;
import org.lovebing.reactnative.baidumap.module.RoutePlanSearchModule;
import org.lovebing.reactnative.baidumap.uimanager.MapViewManager;
import org.lovebing.reactnative.baidumap.uimanager.OverlayArcManager;
import org.lovebing.reactnative.baidumap.uimanager.OverlayCircleManager;
import org.lovebing.reactnative.baidumap.uimanager.OverlayClusterManager;
import org.lovebing.reactnative.baidumap.uimanager.OverlayHeatMapManager;
import org.lovebing.reactnative.baidumap.uimanager.OverlayMarkerIconManager;
import org.lovebing.reactnative.baidumap.uimanager.OverlayMarkerManager;
import org.lovebing.reactnative.baidumap.uimanager.OverlayOverlayInfoWindowManager;
import org.lovebing.reactnative.baidumap.uimanager.OverlayPolygonManager;
import org.lovebing.reactnative.baidumap.uimanager.OverlayPolylineManager;
import org.lovebing.reactnative.baidumap.uimanager.OverlayTextManager;

import java.util.Arrays;
import java.util.List;


/**
 * Created by lovebing on 4/17/16.
 */
public class BaiduMapPackage implements ReactPackage {

    @Override
    public List<NativeModule> createNativeModules(ReactApplicationContext reactContext) {
        return Arrays.asList(
                new BaiduMapManager(reactContext),
                new GeolocationModule(reactContext),
                new GetDistanceModule(reactContext),
                new MapAppModule(reactContext),
                new PoiSugSearchModule(reactContext),
                new PoiSearchModule(reactContext),
                new RoutePlanSearchModule(reactContext)
        );
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public List<ViewManager> createViewManagers(
            ReactApplicationContext reactContext) {
        init(reactContext);
        return Arrays.asList(
                new MapViewManager(),
                new OverlayClusterManager(),
                new OverlayMarkerManager(),
                new OverlayMarkerIconManager(),
                new OverlayOverlayInfoWindowManager(),
                new OverlayArcManager(),
                new OverlayCircleManager(),
                new OverlayPolygonManager(),
                new OverlayPolylineManager(),
                new OverlayTextManager(),
                new OverlayHeatMapManager()
        );
    }

    @MainThread
    protected void init(ReactApplicationContext reactContext) {
        if (Looper.myLooper() == null) {
            Looper.prepare();
        }
        SDKInitializer.setAgreePrivacy(reactContext.getApplicationContext(), true);
        SDKInitializer.initialize(reactContext.getApplicationContext());
    }
}

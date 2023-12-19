/*
 * Copyright (c) 2016-present, lovebing.net.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package org.lovebing.reactnative.baidumap.module;

import android.Manifest;
import android.util.Log;

import androidx.annotation.NonNull;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.common.BaiduMapSDKException;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactMethod;

import org.lovebing.reactnative.baidumap.support.AppUtils;

/**
 * @author lovebing
 * @date 2019/10/30
 */
public class BaiduMapManager extends BaseModule {


    public BaiduMapManager(ReactApplicationContext reactContext) {
        super(reactContext);
        SDKInitializer.setAgreePrivacy(context.getApplicationContext(), false);
    }

    @NonNull
    @Override
    public String getName() {
        return getClass().getSimpleName();
    }

    @ReactMethod
    public void initSDK(String key) {
        try {
            // 在使用 SDK 各组间之前初始化 context 信息，传入 ApplicationContext
            SDKInitializer.setAgreePrivacy(context.getApplicationContext(), true);
            SDKInitializer.initialize(context.getApplicationContext());
            SDKInitializer.setApiKey(key);
        } catch (BaiduMapSDKException e) {
        
        }
    }

    @ReactMethod
    public void hasLocationPermission(Promise promise) {
        promise.resolve(AppUtils.hasPermission(context.getCurrentActivity(), Manifest.permission.ACCESS_FINE_LOCATION));
    }
}

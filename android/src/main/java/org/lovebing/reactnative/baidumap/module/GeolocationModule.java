/*
 * Copyright (c) 2016-present, lovebing.net.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package org.lovebing.reactnative.baidumap.module;

import android.Manifest;
import android.util.Log;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.baidu.mapapi.CoordType;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeOption;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.utils.CoordinateConverter;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;

import org.lovebing.reactnative.baidumap.support.AppUtils;
import org.lovebing.reactnative.baidumap.util.LatLngUtil;

import java.util.List;

/**
 * Created by lovebing on 2016/10/28.
 */
public class GeolocationModule extends BaseModule
        implements OnGetGeoCoderResultListener {

    private LocationClient locationClient;
    private static GeoCoder geoCoder;
    private volatile boolean locating = false;
    private volatile boolean locateOnce = false;

    public GeolocationModule(ReactApplicationContext reactContext) {
        super(reactContext);
        context = reactContext;
    }

    public String getName() {
        return "BaiduGeolocationModule";
    }

    private void initLocationClient(String coorType) {
        if (context.getCurrentActivity() != null) {
            AppUtils.checkPermission(context.getCurrentActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION);
        }
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationMode.Hight_Accuracy);
        option.setCoorType(coorType);
        option.setIsNeedAddress(true);
        option.setIsNeedAltitude(true);
        option.setScanSpan(5000); //243添加
        option.setIsNeedLocationDescribe(true);
        option.setOpenGps(true);
        try {
            LocationClient.setAgreePrivacy(true);
            locationClient = new LocationClient(context.getApplicationContext());
            locationClient.setLocOption(option);
            locationClient.registerLocationListener(new BDAbstractLocationListener() {
                @Override
                public void onReceiveLocation(BDLocation bdLocation) {
                    WritableMap params = Arguments.createMap();
                    params.putDouble("latitude", bdLocation.getLatitude());
                    params.putDouble("longitude", bdLocation.getLongitude());
                    params.putDouble("speed", bdLocation.getSpeed());
                    params.putDouble("direction", bdLocation.getDirection());
                    params.putDouble("altitude", bdLocation.getAltitude());
                    params.putDouble("radius", bdLocation.getRadius());
                    params.putString("address", bdLocation.getAddrStr());
                    params.putString("countryCode", bdLocation.getCountryCode());
                    params.putString("country", bdLocation.getCountry());
                    params.putString("province", bdLocation.getProvince());
                    params.putString("cityCode", bdLocation.getCityCode());
                    params.putString("city", bdLocation.getCity());
                    params.putString("district", bdLocation.getDistrict());
                    params.putString("street", bdLocation.getStreet());
                    params.putString("streetNumber", bdLocation.getStreetNumber());
                    params.putString("buildingId", bdLocation.getBuildingID());
                    params.putString("buildingName", bdLocation.getBuildingName());
                    params.putInt("locType", bdLocation.getLocType()); //243添加
                    params.putString("locationDescribe", bdLocation.getLocationDescribe()); //243添加
                    params.putString("town", bdLocation.getTown()); //243添加
                    params.putString("floor", bdLocation.getFloor()); //243添加
                    // 此定位点作弊概率，3代表高概率，2代表中概率，1代表低概率，0代表概率为0
                    params.putInt("mockGpsProbability", bdLocation.getMockGpsProbability()); //243添加
                    // 防作弊策略识别码，用于辅助分析排查问题
                    params.putInt("mockGpsStrategy", bdLocation.getMockGpsStrategy()); //243添加
                    BDLocation realLoc = bdLocation.getReallLocation();
                    if (bdLocation.getMockGpsStrategy() > 0 && null != realLoc) {
                        double dis = bdLocation.getDisToRealLocation(); // 虚假位置和真实位置之间的距离
                        params.putDouble("disToRealLocation", dis); //243添加
                        int realLocType = realLoc.getLocType(); // 真实定位结果类型
                        params.putInt("realLocType", realLocType); //243添加
                        String realLocTime = realLoc.getTime(); // 真实位置定位时间

                        double realLat = realLoc.getLatitude(); // 真实纬度
                        params.putDouble("realLatitude", realLat); //243添加
                        double realLng = realLoc.getLongitude();  // 真实经度
                        params.putDouble("realLongitude", realLng); //243添加
                        String realLocCoorType = realLoc.getCoorType(); // 真实位置坐标系

                    }
                    if (locateOnce) {
                        locating = false;
                        sendEvent("onGetCurrentLocationPosition", params);
                        locationClient.stop();
                        locationClient = null;
                    } else {
                        sendEvent("onLocationUpdate", params);
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @return
     */
    protected GeoCoder getGeoCoder() {
        if (geoCoder != null) {
            geoCoder.destroy();
        }
        geoCoder = GeoCoder.newInstance();
        geoCoder.setOnGetGeoCodeResultListener(this);
        return geoCoder;
    }

    /**
     * @param sourceLatLng
     * @return
     */
    protected LatLng getBaiduCoorFromGPSCoor(LatLng sourceLatLng) {
        CoordinateConverter converter = new CoordinateConverter();
        converter.from(CoordinateConverter.CoordType.GPS);
        converter.coord(sourceLatLng);
        LatLng desLatLng = converter.convert();
        return desLatLng;

    }

    @ReactMethod
    public void convertGPSCoor(double lat, double lng, Promise promise) {
        Log.i("convertGPSCoor", "convertGPSCoor");
        LatLng latLng = getBaiduCoorFromGPSCoor(new LatLng(lat, lng));
        WritableMap map = Arguments.createMap();
        map.putDouble("latitude", latLng.latitude);
        map.putDouble("longitude", latLng.longitude);
        promise.resolve(map);
    }

    @ReactMethod
    public void getCurrentPosition(String coorType) {
        if (locating) {
            return;
        }
        locateOnce = true;
        locating = true;
        if (locationClient == null) {
            initLocationClient(coorType);
        }
        Log.i("getCurrentPosition", "getCurrentPosition");
        locationClient.start();
    }

    @ReactMethod
    public void startLocating(String coorType) {
        if (locating) {
            return;
        }
        locateOnce = false;
        locating = true;
        initLocationClient(coorType);
        locationClient.start();
    }

    @ReactMethod
    public void stopLocating() {
        locating = false;
        if (locationClient != null) {
            locationClient.stop();
            locationClient = null;
        }
    }

    @ReactMethod
    public void geocode(String city, String addr) {
        getGeoCoder().geocode(new GeoCodeOption()
                .city(city).address(addr));
    }

    @ReactMethod
    public void reverseGeoCode(double lat, double lng) {
        getGeoCoder().reverseGeoCode(new ReverseGeoCodeOption()
                .location(new LatLng(lat, lng)));
    }

    @ReactMethod
    public void reverseGeoCodeGPS(double lat, double lng) {
        getGeoCoder().reverseGeoCode(new ReverseGeoCodeOption()
                .location(getBaiduCoorFromGPSCoor(new LatLng(lat, lng))));
    }

//    @Override
//    public void onReceiveLocation(BDLocation bdLocation) {
//        WritableMap params = Arguments.createMap();
//        params.putDouble("latitude", bdLocation.getLatitude());
//        params.putDouble("longitude", bdLocation.getLongitude());
//        params.putDouble("speed", bdLocation.getSpeed());
//        params.putDouble("direction", bdLocation.getDirection());
//        params.putDouble("altitude", bdLocation.getAltitude());
//        params.putDouble("radius", bdLocation.getRadius());
//        params.putString("address", bdLocation.getAddrStr());
//        params.putString("countryCode", bdLocation.getCountryCode());
//        params.putString("country", bdLocation.getCountry());
//        params.putString("province", bdLocation.getProvince());
//        params.putString("cityCode", bdLocation.getCityCode());
//        params.putString("city", bdLocation.getCity());
//        params.putString("district", bdLocation.getDistrict());
//        params.putString("street", bdLocation.getStreet());
//        params.putString("streetNumber", bdLocation.getStreetNumber());
//        params.putString("buildingId", bdLocation.getBuildingID());
//        params.putString("buildingName", bdLocation.getBuildingName());
//        params.putInt("locType", bdLocation.getLocType()); //243添加
//        params.putString("locationDescribe", bdLocation.getLocationDescribe()); //243添加
//        params.putString("town", bdLocation.getTown()); //243添加
//        params.putString("floor", bdLocation.getFloor()); //243添加
//        Log.i("onReceiveLocation", "onGetCurrentLocationPosition");
//
//        if (locateOnce) {
//            locating = false;
//            sendEvent("onGetCurrentLocationPosition", params);
//            locationClient.stop();
//            locationClient = null;
//        } else {
//            sendEvent("onLocationUpdate", params);
//        }
//    }

    @Override
    public void onGetGeoCodeResult(GeoCodeResult result) {
        WritableMap params = Arguments.createMap();
        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
            params.putInt("errcode", -1);
            params.putString("errmsg", result.error.name());
        } else {
            params.putDouble("latitude", result.getLocation().latitude);
            params.putDouble("longitude", result.getLocation().longitude);
        }
        sendEvent("onGetGeoCodeResult", params);
    }

    @Override
    public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
        WritableMap params = Arguments.createMap();
        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
            params.putInt("errcode", -1);
        } else {
            ReverseGeoCodeResult.AddressComponent addressComponent = result.getAddressDetail();
            params.putString("address", result.getAddress());
            params.putString("province", addressComponent.province);
            params.putString("city", addressComponent.city);
            params.putString("district", addressComponent.district);
            params.putString("street", addressComponent.street);
            params.putString("streetNumber", addressComponent.streetNumber);

            WritableArray list = Arguments.createArray();
            // List<PoiInfo> poiList = result.getPoiList();
            // for (PoiInfo info : poiList) {
            //     WritableMap attr = Arguments.createMap();
            //     attr.putString("name", info.name);
            //     attr.putString("address", info.address);
            //     attr.putString("city", info.city);
            //     attr.putDouble("latitude", info.location.latitude);
            //     attr.putDouble("longitude", info.location.longitude);
            //     list.pushMap(attr);
            // }
            // params.putArray("poiList", list);
        }
        sendEvent("onGetReverseGeoCodeResult", params);
    }


    @ReactMethod
    public void setCoordType(String coordType) {
        //自4.3.0起，百度地图SDK所有接口均支持百度坐标和国测局坐标，用此方法设置您使用的坐标类型.
        //包括BD09LL和GCJ02两种坐标，默认是BD09LL坐标。
        SDKInitializer.setCoordType(CoordType.valueOf(coordType.toUpperCase()));
    }

    @ReactMethod
    public void getCoordType(Promise promise) {
        promise.resolve(SDKInitializer.getCoordType().name());//BD09LL或者GCJ02坐标
    }

    @ReactMethod
    public void convertCoordinate(String from, ReadableMap sourceLatLng, Promise promise) {
        //初始化左边转换工具类，指定源坐标类型和坐标数据
        //sourceLatLng 待转换坐标
        CoordinateConverter converter = new CoordinateConverter()
                .from(CoordinateConverter.CoordType.valueOf(from.toUpperCase()))
                .coord(LatLngUtil.fromReadableMap(sourceLatLng));
        //转换坐标
        LatLng desLatLng = converter.convert();
        WritableMap writableMap = Arguments.createMap();
        writableMap.putDouble("latitude", desLatLng.latitude);
        writableMap.putDouble("longitude", desLatLng.longitude);
        promise.resolve(writableMap);
    }
}

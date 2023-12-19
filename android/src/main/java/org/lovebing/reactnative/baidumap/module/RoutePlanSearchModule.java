package org.lovebing.reactnative.baidumap.module;

import android.widget.Toast;

import androidx.annotation.NonNull;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiDetailSearchResult;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiNearbySearchOption;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.baidu.mapapi.search.route.BikingRouteResult;
import com.baidu.mapapi.search.route.DrivingRouteLine;
import com.baidu.mapapi.search.route.DrivingRoutePlanOption;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.IndoorRouteResult;
import com.baidu.mapapi.search.route.MassTransitRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRouteResult;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;

import org.lovebing.reactnative.baidumap.util.LatLngUtil;

import java.util.List;

/**
 * 提示POI搜索
 */
public class RoutePlanSearchModule extends BaseModule implements OnGetRoutePlanResultListener {
    private RoutePlanSearch mSearch = null;

    public RoutePlanSearchModule(ReactApplicationContext reactContext) {
        super(reactContext);
        context = reactContext;

    }

    public void initRoutePlanSearch() {
        // 初始化建议搜索模块，注册建议搜索事件监听
        mSearch = RoutePlanSearch.newInstance();
        mSearch.setOnGetRoutePlanResultListener(this);
    }

    @NonNull
    @Override
    public String getName() {
        return "RoutePlanSearchModule";
    }

    @ReactMethod
    public void drivingSearch(ReadableMap from, ReadableMap to) {
        if (mSearch == null) {
            initRoutePlanSearch();
        }
        PlanNode fromNode = PlanNode.withLocation(LatLngUtil.fromReadableMap(from));
        PlanNode toNode = PlanNode.withLocation(LatLngUtil.fromReadableMap(to));
        mSearch.drivingSearch(new DrivingRoutePlanOption()
                .from(fromNode)
                .to(toNode));
    }


    @Override
    public void onCatalystInstanceDestroy() {
        super.onCatalystInstanceDestroy();
        if (mSearch != null) {
            mSearch.destroy();
            mSearch = null;
        }
    }

//    @Override
//    public void onGetPoiResult(PoiResult poiResult) {
//        if (poiResult == null || poiResult.error == SearchResult.ERRORNO.RESULT_NOT_FOUND) {
//            Toast.makeText(context, "未找到结果", Toast.LENGTH_LONG).show();
//            return;
//        }
//        WritableMap params = Arguments.createMap();
//        params.putString("error", String.valueOf(poiResult.error));
//        params.putInt("status", poiResult.status);
//
//        if (poiResult.error == SearchResult.ERRORNO.NO_ERROR) {
//            WritableArray writableArray = Arguments.createArray();
//            List<PoiInfo> allPoi = poiResult.getAllPoi();
//            if (allPoi != null)
//                for (PoiInfo poiInfo : allPoi) {
//                    WritableMap item = Arguments.createMap();
//                    item.putString("name",poiInfo.name);
//                    item.putString("uid",poiInfo.uid);
//                    item.putString("address",poiInfo.address);
//                    item.putString("province",poiInfo.province);
//                    item.putString("city",poiInfo.city);
//                    item.putString("area",poiInfo.area);
//                    item.putString("street_id",poiInfo.street_id);
//                    item.putString("phoneNum",poiInfo.phoneNum);
//                    item.putString("postCode",poiInfo.postCode);
//                    if (poiInfo.location != null) {
//                        item.putDouble("latitude", poiInfo.location.latitude);
//                        item.putDouble("longitude", poiInfo.location.longitude);
//                    }
//                    item.putString("tag",poiInfo.tag);
//                    writableArray.pushMap(item);
//                }
//            params.putArray("allPoi", writableArray);
//        }
//        sendEvent("onGetPoiResult", params);
//    }

    @Override
    public void onGetWalkingRouteResult(WalkingRouteResult walkingRouteResult) {

    }

    @Override
    public void onGetTransitRouteResult(TransitRouteResult transitRouteResult) {

    }

    @Override
    public void onGetMassTransitRouteResult(MassTransitRouteResult massTransitRouteResult) {

    }

    @Override
    public void onGetDrivingRouteResult(DrivingRouteResult drivingRouteResult) {
        if (drivingRouteResult == null) {
            Toast.makeText(context, "未找到结果", Toast.LENGTH_LONG).show();
            return;
        }
        WritableMap params = Arguments.createMap();
        params.putString("error", String.valueOf(drivingRouteResult.error));
        params.putInt("status", drivingRouteResult.status);
        if (drivingRouteResult.error == SearchResult.ERRORNO.NO_ERROR) {
            WritableArray writableArray = Arguments.createArray();
            List<DrivingRouteLine> routeLines = drivingRouteResult.getRouteLines();
            if (routeLines != null)
                for (DrivingRouteLine routeLine : routeLines) {
                    WritableMap item = Arguments.createMap();
                    item.putInt("congestionDistance",routeLine.getCongestionDistance());
                    item.putInt("distance",routeLine.getDistance());
                    item.putInt("lightNum",routeLine.getLightNum());
                    item.putInt("toll",routeLine.getToll());
                    item.putInt("duration",routeLine.getDuration());
                    writableArray.pushMap(item);
                }
            params.putArray("routeLines", writableArray);
        }
        sendEvent("onGetDrivingRouteResult", params);

    }

    @Override
    public void onGetIndoorRouteResult(IndoorRouteResult indoorRouteResult) {

    }

    @Override
    public void onGetBikingRouteResult(BikingRouteResult bikingRouteResult) {

    }
}

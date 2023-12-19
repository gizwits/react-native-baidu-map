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
public class PoiSearchModule extends BaseModule implements OnGetPoiSearchResultListener {
    private PoiSearch mPoiSearch = null;

    public PoiSearchModule(ReactApplicationContext reactContext) {
        super(reactContext);
        context = reactContext;

    }

    public void initPoiSearch() {
        // 初始化建议搜索模块，注册建议搜索事件监听
        mPoiSearch = PoiSearch.newInstance();
        mPoiSearch.setOnGetPoiSearchResultListener(this);
    }

    @NonNull
    @Override
    public String getName() {
        return "PoiSearchModule";
    }

    @ReactMethod
    public void searchInCity(String cityStr, String keyWordStr, int pageIndex,int pageSize, boolean cityLimit, int scope) {
        if (mPoiSearch == null) {
            initPoiSearch();
        }
        // 发起请求
        mPoiSearch.searchInCity((new PoiCitySearchOption())
                .city(cityStr)
                .keyword(keyWordStr)
                .pageNum(pageIndex) // 分页编号
                .pageCapacity(pageSize)
                .cityLimit(cityLimit)
                .scope(scope));
    }

    @ReactMethod
    public void searchNearby(String keyWordStr, ReadableMap location, int radius, int pageIndex,int pageSize, boolean cityLimit, int scope) {
        if (mPoiSearch == null) {
            initPoiSearch();
        }
        LatLng latLng = LatLngUtil.fromReadableMap(location);
        // 配置请求参数
        PoiNearbySearchOption nearbySearchOption = new PoiNearbySearchOption()
                .keyword(keyWordStr) // 检索关键字
                .location(latLng) // 经纬度
                .radius(radius) // 检索半径 单位： m
                .pageNum(pageIndex) // 分页编号
                .pageCapacity(pageSize)
                .radiusLimit(cityLimit)
                .scope(scope);
        // 发起检索
        mPoiSearch.searchNearby(nearbySearchOption);
    }


    @Override
    public void onCatalystInstanceDestroy() {
        super.onCatalystInstanceDestroy();
        if (mPoiSearch != null) {
            mPoiSearch.destroy();
            mPoiSearch = null;
        }
    }

    @Override
    public void onGetPoiResult(PoiResult poiResult) {
        if (poiResult == null || poiResult.error == SearchResult.ERRORNO.RESULT_NOT_FOUND) {
            Toast.makeText(context, "未找到结果", Toast.LENGTH_LONG).show();
            return;
        }
        WritableMap params = Arguments.createMap();
        params.putString("error", String.valueOf(poiResult.error));
        params.putInt("status", poiResult.status);

        if (poiResult.error == SearchResult.ERRORNO.NO_ERROR) {
            WritableArray writableArray = Arguments.createArray();
            List<PoiInfo> allPoi = poiResult.getAllPoi();
            if (allPoi != null)
                for (PoiInfo poiInfo : allPoi) {
                    WritableMap item = Arguments.createMap();
                    item.putString("name",poiInfo.name);
                    item.putString("uid",poiInfo.uid);
                    item.putString("address",poiInfo.address);
                    item.putString("province",poiInfo.province);
                    item.putString("city",poiInfo.city);
                    item.putString("area",poiInfo.area);
                    item.putString("street_id",poiInfo.street_id);
                    item.putString("phoneNum",poiInfo.phoneNum);
                    item.putString("postCode",poiInfo.postCode);
                    if (poiInfo.location != null) {
                        item.putDouble("latitude", poiInfo.location.latitude);
                        item.putDouble("longitude", poiInfo.location.longitude);
                    }
                    item.putString("tag",poiInfo.tag);
                    writableArray.pushMap(item);
                }
            params.putArray("allPoi", writableArray);
        }
        sendEvent("onGetPoiResult", params);
    }

    @Override
    public void onGetPoiDetailResult(PoiDetailResult poiDetailResult) {
    }

    @Override
    public void onGetPoiDetailResult(PoiDetailSearchResult poiDetailSearchResult) {

    }

    @Override
    public void onGetPoiIndoorResult(PoiIndoorResult poiIndoorResult) {

    }
}

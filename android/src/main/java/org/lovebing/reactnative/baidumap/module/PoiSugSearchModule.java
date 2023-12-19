package org.lovebing.reactnative.baidumap.module;

import androidx.annotation.NonNull;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiChildrenInfo;
import com.baidu.mapapi.search.sug.OnGetSuggestionResultListener;
import com.baidu.mapapi.search.sug.SuggestionResult;
import com.baidu.mapapi.search.sug.SuggestionSearch;
import com.baidu.mapapi.search.sug.SuggestionSearchOption;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.WritableNativeArray;

import java.util.List;

/**
 * 提示POI搜索
 */
public class PoiSugSearchModule extends BaseModule implements OnGetSuggestionResultListener {
    private SuggestionSearch mSuggestionSearch = null;

    public PoiSugSearchModule(ReactApplicationContext reactContext) {
        super(reactContext);
        context = reactContext;

    }

    public void initSuggestionSearch() {
        // 初始化建议搜索模块，注册建议搜索事件监听
        mSuggestionSearch = SuggestionSearch.newInstance();
        mSuggestionSearch.setOnGetSuggestionResultListener(this);
    }

    @NonNull
    @Override
    public String getName() {
        return "PoiSugSearchModule";
    }

    @ReactMethod
    public void requestSuggestion(String keyword, String city, boolean cityLimit) {
        if (mSuggestionSearch == null) {
            initSuggestionSearch();
        }
        // 使用建议搜索服务获取建议列表，结果在onSuggestionResult()中更新
        mSuggestionSearch.requestSuggestion((new SuggestionSearchOption())
                .keyword(keyword) // 关键字
                .city(city) //城市
                .citylimit(cityLimit)); //是否限制Sug检索区域在city内
    }

    @Override
    public void onGetSuggestionResult(SuggestionResult suggestionResult) {
        if (suggestionResult == null || suggestionResult.getAllSuggestions() == null) {
            return;
        }
        List<SuggestionResult.SuggestionInfo> suggestionResults = suggestionResult.getAllSuggestions();
        WritableMap params = Arguments.createMap();
        params.putString("error", String.valueOf(suggestionResult.error));
        params.putInt("status", suggestionResult.status);
        WritableArray writableArray = Arguments.createArray();
        for (SuggestionResult.SuggestionInfo result : suggestionResults) {
            WritableMap item = Arguments.createMap();
            item.putInt("adCode", result.getAdCode());
            item.putString("address", result.address);
            item.putString("city", result.city);
            item.putString("district", result.district);
            item.putString("key", result.key);
            if (result.pt != null) {
                item.putDouble("latitude", result.pt.latitude);
                item.putDouble("longitude", result.pt.longitude);
            }
            item.putString("tag", result.tag);
            item.putString("uid", result.uid);
            WritableArray itemArray = Arguments.createArray();
            List<PoiChildrenInfo> poiChildrenInfoList = result.poiChildrenInfoList;
            if (poiChildrenInfoList != null)
                for (PoiChildrenInfo poiChildrenInfo : poiChildrenInfoList) {
                    WritableMap childrenMap = Arguments.createMap();
                    childrenMap.putString("uid", poiChildrenInfo.getUid());
                    childrenMap.putString("address", poiChildrenInfo.getAddress());
                    childrenMap.putString("name", poiChildrenInfo.getName());
                    childrenMap.putString("showName", poiChildrenInfo.getShowName());
                    childrenMap.putString("tag", poiChildrenInfo.getTag());
                    if (poiChildrenInfo.getLocation() != null) {
                        childrenMap.putDouble("latitude", poiChildrenInfo.getLocation().latitude);
                        childrenMap.putDouble("longitude", poiChildrenInfo.getLocation().longitude);
                    }
                    itemArray.pushMap(childrenMap);
                }
            item.putArray("poiChildrenInfoList", itemArray);
            writableArray.pushMap(item);
        }
        params.putArray("data", writableArray);
        sendEvent("onGetSuggestionResult", params);

    }

    @Override
    public void onCatalystInstanceDestroy() {
        super.onCatalystInstanceDestroy();
        if (mSuggestionSearch != null) {
            mSuggestionSearch.destroy();
            mSuggestionSearch = null;
        }
    }
}

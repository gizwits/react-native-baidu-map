//
//  PoiSearchModule.m
//  RCTBaiduMap
//
//  Created by reylen on 2022/8/1.
//  Copyright © 2022 lovebing.org. All rights reserved.
//

#import "PoiSearchModule.h"
#import <BaiduMapAPI_Base/BMKBaseComponent.h>
#import <BaiduMapAPI_Search/BMKSearchComponent.h>
#import <BMKLocationkit/BMKLocationComponent.h>

@interface PoiSearchModule ()<BMKPoiSearchDelegate> {
    BOOL _searching_city;
    BOOL _searching_nearby_city;
}

@property(nonatomic) BMKPoiSearch* poiSearch;

@end

@implementation PoiSearchModule

- (instancetype)init {
    self = [super init];
    [BMKMapManager setAgreePrivacy:YES];
    [[BMKLocationAuth sharedInstance] setAgreePrivacy:YES];
    _searching_city = NO;
    _searching_nearby_city = NO;

    return self;
}

- (BMKPoiSearch *)poiSearch {
    if (!_poiSearch) {
        _poiSearch = [[BMKPoiSearch alloc] init];
        _poiSearch.delegate = self;
    }
    return _poiSearch;
}
/**
 delegate
 */
- (void)onGetPoiResult:(BMKPoiSearch*)searcher result:(BMKPOISearchResult*)poiResult errorCode:(BMKSearchErrorCode)errorCode {

    _searching_city = NO;
    _searching_nearby_city = NO;

    if (errorCode == BMK_SEARCH_NO_ERROR) {
            //在此处理正常结果
        NSMutableDictionary* back_config = [@{
            @"status": @(errorCode),
            @"error": @"NO_ERROR"
        } mutableCopy];
        // @"status": poiResult.status

        NSMutableArray* results = [NSMutableArray array];
        [poiResult.poiInfoList enumerateObjectsUsingBlock:^(BMKPoiInfo * _Nonnull obj, NSUInteger idx, BOOL * _Nonnull stop) {

            NSMutableDictionary* poiInfo = [@{
                @"name": obj.name,
                @"uid": obj.UID,
            } mutableCopy];

            if (obj.phone && obj.phone > 0) {
                poiInfo[@"phoneNum"] = obj.phone;
            }
            if (obj.zipCode) {
                poiInfo[@"postCode"] = obj.zipCode;
            }
            if (obj.tag) {
                poiInfo[@"tag"] = obj.tag;
            }
            if (obj.streetID) {
                poiInfo[@"street_id"] = obj.streetID;
            }
            if (obj.area) {
                poiInfo[@"area"] = obj.area;
            }
            if (obj.city) {
                poiInfo[@"city"] = obj.city;
            }
            if (obj.province) {
                poiInfo[@"province"] = obj.province;
            }
            if (obj.address) {
                poiInfo[@"address"] = obj.address;
            }
            if (obj.pt.latitude > 0 && obj.pt.latitude > 0) {
                poiInfo[@"latitude"] = @(obj.pt.latitude);
                poiInfo[@"longitude"] = @(obj.pt.longitude);
            }
            [results addObject: poiInfo];
        }];
        back_config[@"allPoi"] = results;
        [self sendEvent:@"onGetPoiResult" body:back_config];
    }
    else if (errorCode == BMK_SEARCH_AMBIGUOUS_KEYWORD){
        //当在设置城市未找到结果，但在其他城市找到结果时，回调建议检索城市列表
        // result.cityList;
        NSLog(@"起始点有歧义");
        NSMutableDictionary* back_config = [@{
            @"status": @(errorCode),
            @"error": @"起始点有歧义"
        } mutableCopy];
        [self sendEvent:@"onGetPoiResult" body:back_config];
    } else {
        NSLog(@"抱歉，未找到结果, %@", @(errorCode));
        NSMutableDictionary* back_config = [@{
            @"status": @(errorCode),
            @"error": @"未找到结果"
        } mutableCopy];
        [self sendEvent:@"onGetPoiResult" body:back_config];
    }

}

- (void)onGetPoiDetailResult:(BMKPoiSearch*)searcher result:(BMKPOIDetailSearchResult*)poiDetailResult errorCode:(BMKSearchErrorCode)errorCode {

}
- (void)onGetPoiIndoorResult:(BMKPoiSearch*)searcher result:(BMKPOIIndoorSearchResult*)poiIndoorResult errorCode:(BMKSearchErrorCode)errorCode {

}
RCT_EXPORT_MODULE(PoiSearchModule);

RCT_EXPORT_METHOD(searchInCity:(NSString *)city keyword:(NSString *) keyWordStr pageIndex:(NSInteger) pageIndex pageSize:(NSInteger) pageSize cityLimit:(BOOL) cityLimit scope:(NSInteger) scope) {

    if (_searching_city) {
        return;
    }

    _searching_city = YES;

    dispatch_async(dispatch_get_main_queue(), ^{

        //初始化请求参数类BMKCitySearchOption的实例
        BMKPOICitySearchOption *cityOption = [[BMKPOICitySearchOption alloc] init];
        //检索关键字，必选。举例：小吃
        cityOption.keyword = keyWordStr;
        //区域名称(市或区的名字，如北京市，海淀区)，最长不超过25个字符，必选
        cityOption.city = city;
        //检索分类，可选，与keyword字段组合进行检索，多个分类以","分隔。举例：美食,烧烤,酒店
//        cityOption.tags = @[@"美食",@"烧烤"];
        //区域数据返回限制，可选，为YES时，仅返回city对应区域内数据
        cityOption.isCityLimit = cityLimit;
        //POI检索结果详细程度
        //cityOption.scope = BMK_POI_SCOPE_BASIC_INFORMATION;
        //检索过滤条件，scope字段为BMK_POI_SCOPE_DETAIL_INFORMATION时，filter字段才有效
        //cityOption.filter = filter;
        //分页页码，默认为0，0代表第一页，1代表第二页，以此类推
        cityOption.pageIndex = pageIndex;
        //单次召回POI数量，默认为10条记录，最大返回20条
        cityOption.pageSize = pageSize;
        cityOption.scope = scope;

        BOOL flag = [self.poiSearch poiSearchInCity:cityOption];
        if(flag) {
            NSLog(@"POI城市内检索成功");
        } else {
            self->_searching_city = NO;
            NSLog(@"POI城市内检索失败");
        }
    });
}
RCT_EXPORT_METHOD(searchNearby:(NSString *) keyWordStr location:(NSDictionary*) location radius:(NSInteger) radius pageIndex:(NSInteger) pageIndex pageSize:(NSInteger) pageSize cityLimit:(BOOL) cityLimit scope:(NSInteger) scope) {

    if (_searching_nearby_city) {
        return;
    }

    _searching_nearby_city = YES;

    dispatch_async(dispatch_get_main_queue(), ^{
        BMKPOINearbySearchOption* option = [[BMKPOINearbySearchOption alloc] init];
        option.keywords = @[keyWordStr];
        if (location && location[@"longitude"] && location[@"latitude"]) {
            double loc = [location[@"latitude"] doubleValue];
            double lng = [location[@"longitude"] doubleValue];
            CLLocationCoordinate2D coords = CLLocationCoordinate2DMake(loc,lng);//纬度，经度
            option.location = coords;
        }
        option.radius = radius;
        option.isRadiusLimit = cityLimit;
        option.pageIndex = pageIndex;
        option.pageSize = pageSize;
        option.scope = scope;
        BOOL result = [self.poiSearch poiSearchNearBy: option];
        if (!result) {
            self->_searching_city = NO;
        }
        NSLog(@"search poi result %@", @(result));
    });
}

@end

//
//  PoiSugSearchModule.m
//  RCTBaiduMap
//
//  Created by reylen on 2022/8/1.
//  Copyright © 2022 lovebing.org. All rights reserved.
//

#import "PoiSugSearchModule.h"
#import <BaiduMapAPI_Search/BMKSuggestionSearch.h>
#import <BMKLocationkit/BMKLocationComponent.h>
@interface PoiSugSearchModule ()<BMKSuggestionSearchDelegate> {
    BOOL _searching_city;
}

@property(nonatomic) BMKSuggestionSearch* sugSearch;

@end
@implementation PoiSugSearchModule

- (instancetype)init {
    self = [super init];
    [[BMKLocationAuth sharedInstance] setAgreePrivacy:YES];
    _sugSearch = [[BMKSuggestionSearch alloc] init];
    _sugSearch.delegate = self;
    _searching_city = NO;
    return self;
}

/**
 delegate
 */

- (void)onGetSuggestionResult:(BMKSuggestionSearch *)searcher result:(BMKSuggestionSearchResult *)result errorCode:(BMKSearchErrorCode)error {
    _searching_city = NO;
    NSMutableDictionary* body = [@{
        @"status": @(error),
        @"error": @"NO_ERROR"
    } mutableCopy];
    NSArray* list = result.suggestionList;
    NSMutableArray* results = [NSMutableArray array];
    for (int i = 0; i < list.count; i ++) {
        BMKSuggestionInfo* info = list[i];
        NSMutableDictionary* obj = [@{} mutableCopy];
        
        if(info.address) obj[@"address"] = info.address;
        if(info.city) obj[@"city"] = info.city;
        if(info.district) obj[@"district"] = info.district;
        if(info.key) obj[@"key"] = info.key;
        if(info.tag) obj[@"tag"] = info.tag;
        if(info.uid) obj[@"uid"] = info.uid;
        
        if (info.location.latitude > 0 && info.location.longitude > 0) {
            obj[@"latitude"] = @(info.location.latitude);
            obj[@"longitude"] = @(info.location.longitude);
        }
        
        NSArray* children = info.children;
        NSMutableArray* childSugs = [NSMutableArray array];
        for (BMKSuggestionChildrenInfo* child in children) {
            NSDictionary* childInfo = @{
                @"uid": child.uid,
                @"name": child.name,
                @"showName": child.showName
            };
            [childSugs addObject:childInfo];
        }
        
        obj[@"poiChildrenInfoList"] = childSugs;
        [results addObject:obj];
    }
    body[@"data"] = results;
    [self sendEvent:@"onGetSuggestionResult" body:body];
}

RCT_EXPORT_MODULE(PoiSugSearchModule);

RCT_EXPORT_METHOD(requestSuggestion:(NSString *)keyword city:(NSString *) city  cityLimit:(BOOL) cityLimit) {
    
    if (_searching_city) {
        return;
    }
    
    _searching_city = YES;
    
    dispatch_async(dispatch_get_main_queue(), ^{
        BMKSuggestionSearchOption* option = [BMKSuggestionSearchOption new];
        option.cityname = city;
        option.keyword = keyword;
        option.cityLimit = cityLimit;
        [self->_sugSearch suggestionSearch: option];
    });
}
@end

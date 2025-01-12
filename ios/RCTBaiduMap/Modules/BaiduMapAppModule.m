//
//  MapAppModule.m
//  RCTBaiduMap
//
//  Created by lovebing on 2019/10/30.
//  Copyright © 2019 lovebing.org. All rights reserved.
//

#import "BaiduMapAppModule.h"
#import <BaiduMapAPI_Utils/BMKUtilsComponent.h>

@implementation BaiduMapAppModule

RCT_EXPORT_MODULE(BaiduMapAppModule)

RCT_EXPORT_METHOD(openDrivingRoute:(NSDictionary *)startPoint endPoint:(NSDictionary *)endPoint) {
    BMKOpenDrivingRouteOption *option = [[BMKOpenDrivingRouteOption alloc] init];
    [self updateRouteOption:option startPoint:startPoint endPoint:endPoint];
    dispatch_async(dispatch_get_main_queue(), ^{
        NSInteger flag = [BMKOpenRoute openBaiduMapDrivingRoute:option];
        NSLog(@"调起百度地图客户端公交路线：%d", (int) flag);
    });
}

RCT_EXPORT_METHOD(openTransitRoute:(NSDictionary *)startPoint endPoint:(NSDictionary *)endPoint) {
    BMKOpenTransitRouteOption *option = [[BMKOpenTransitRouteOption alloc] init];
    option.openTransitPolicy = BMK_OPEN_TRANSIT_RECOMMAND;
    [self updateRouteOption:option startPoint:startPoint endPoint:endPoint];
    dispatch_async(dispatch_get_main_queue(), ^{
        NSInteger flag = [BMKOpenRoute openBaiduMapTransitRoute:option];
        NSLog(@"调起百度地图客户端公交路线：%d", (int) flag);
    });
}

RCT_EXPORT_METHOD(openWalkNavi:(NSDictionary *)startPoint endPoint:(NSDictionary *)endPoint) {
    BMKOpenWalkingRouteOption *option = [[BMKOpenWalkingRouteOption alloc] init];
    [self updateRouteOption:option startPoint:startPoint endPoint:endPoint];
    NSInteger flag = [BMKOpenRoute openBaiduMapWalkingRoute:option];
    dispatch_async(dispatch_get_main_queue(), ^{
        NSLog(@"调起百度地图客户端步行路线：%d", (int) flag);
    });
}

RCT_EXPORT_METHOD(openPoiNearbySearch:(NSDictionary *)options) {
    @try {
        BMKOpenPoiNearbyOption* option = [BMKOpenPoiNearbyOption new];
        option.keyword = [options objectForKey:@"key"];
        option.radius = [[options objectForKey:@"radius"] intValue];
        option.location = [OverlayUtils getCoorFromOption:options];
        [BMKOpenPoi openBaiduMapPoiNearbySearch:option];
    } @catch (NSException *exception) {
        NSLog(@"openPoiNearbySearch error: %@", exception.description);
    } @finally {
        
    }
}

RCT_EXPORT_METHOD(openPoiDetialsPage:(NSString *)uid) {
    @try {
        BMKOpenPoiDetailOption* option = [BMKOpenPoiDetailOption new];
        option.poiUid = uid;
        [BMKOpenPoi openBaiduMapPoiDetailPage:option];
    } @catch (NSException *exception) {
        NSLog(@"openPoiDetialsPage error: %@", exception.description);
    } @finally {
        
    }
}

RCT_EXPORT_METHOD(openPanoShow:(NSString *)uid) {
    NSLog(@"百度map不存在方法 openBaiduMapPanoShow");
}

-(void)updateRouteOption:(BMKOpenRouteOption *)option startPoint:(NSDictionary *)startPoint endPoint:(NSDictionary *)endPoint {
    option.appScheme = @"baidumapsdk://mapsdk.baidu.com";
    option.isSupportWeb = YES;
    BMKPlanNode *start = [[BMKPlanNode alloc]init];
    start.name = startPoint[@"name"];
    start.pt = [OverlayUtils getCoorFromOption:startPoint];
    option.startPoint = start;
    BMKPlanNode *end = [[BMKPlanNode alloc]init];
    end.pt = [OverlayUtils getCoorFromOption:endPoint];
    end.name = endPoint[@"name"];
    option.endPoint = end;
}

@end

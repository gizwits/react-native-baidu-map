//
//  RoutePlanSearchModule.m
//  react-native-gizwits-baidu-map
//
//  Created by reylen on 2022/8/25.
//

#import "RoutePlanSearchModule.h"
#import <BaiduMapAPI_Search/BMKRouteSearch.h>
#import <BMKLocationkit/BMKLocationComponent.h>
#import "OverlayUtils.h"

@interface RoutePlanSearchModule ()<BMKRouteSearchDelegate> {
    BOOL _searching_route;
}


@property(nonatomic) BMKRouteSearch* routeSearch;

@end

@implementation RoutePlanSearchModule

- (instancetype)init {
    self = [super init];
    [[BMKLocationAuth sharedInstance] setAgreePrivacy:YES];
    _routeSearch = [[BMKRouteSearch alloc] init];
    _routeSearch.delegate = self;
    _searching_route = NO;
    return self;
}

/**
 *
 * BMKRouteSearchDelegate
 *
 */
- (void)onGetDrivingRouteResult:(BMKRouteSearch *)searcher result:(BMKDrivingRouteResult *)result errorCode:(BMKSearchErrorCode)error {
    _searching_route = NO;
    if (!result) {
        
        [self sendEvent:@"onGetDrivingRouteResult" body:@{
            @"error": @"未搜索到结果",
            @"status": @(error)
        }];
        
        return;
    }
    NSMutableDictionary* params = @{
        @"status": @(error),
        @"error": @"NO_ERROR"
    }.mutableCopy;
    if (error == BMK_SEARCH_NO_ERROR) {
        NSArray* routes = result.routes;
        NSMutableArray* routeLines = [NSMutableArray arrayWithCapacity:routes.count];
        for (int i = 0; i < routes.count; i ++) {
            BMKDrivingRouteLine* routeLine = [routes objectAtIndex:i];
            [routeLines addObject:@{
                @"congestionDistance": @(routeLine.congestionMetres),
                @"distance": @(routeLine.distance),
                @"lightNum": @(routeLine.lightNum),
                @"toll": @(routeLine.toll),
                @"duration": @{
                    @"dates": @(routeLine.duration.dates),
                    @"hours": @(routeLine.duration.hours),
                    @"minutes": @(routeLine.duration.minutes),
                    @"seconds":@(routeLine.duration.seconds)
                }
            }];
        }
        [params setObject:routeLines forKey:@"routeLines"];
    }
    [self sendEvent:@"onGetDrivingRouteResult" body:params];
}

RCT_EXPORT_MODULE(RoutePlanSearchModule);

RCT_EXPORT_METHOD(drivingSearch:(NSDictionary *)from to:(NSDictionary *) to) {
    
    if (_searching_route) {
        return;
    }
    
    _searching_route = YES;
    
    dispatch_async(dispatch_get_main_queue(), ^{
        CLLocationCoordinate2D fromCoordinate = [OverlayUtils getCoorFromOption:from];
        CLLocationCoordinate2D toCoordinate = [OverlayUtils getCoorFromOption:to];
        BMKDrivingRoutePlanOption* option = [BMKDrivingRoutePlanOption new];
        
        BMKPlanNode* fromNode = [BMKPlanNode new];
        fromNode.pt = fromCoordinate;
        
        BMKPlanNode* toNode = [BMKPlanNode new];
        toNode.pt = toCoordinate;
        
        option.from = fromNode;
        option.to = toNode;
        [self->_routeSearch drivingSearch:option];
        
    });
}

@end

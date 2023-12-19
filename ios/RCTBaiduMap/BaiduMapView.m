//
//  RCTBaiduMap.m
//  RCTBaiduMap
//
//  Created by lovebing on 4/17/2016.
//  Copyright © 2016 lovebing.org. All rights reserved.
//

#import "BaiduMapView.h"

@implementation BaiduMapView {
//    BMKMapView* _mapView;
    BMKPointAnnotation* _annotation;
    NSMutableArray* _annotations;
    NSMutableSet* _markers;
    NSMutableSet* _circles;

}

- (void)setZoom:(float)zoom {
    self.zoomLevel = zoom;
}

- (void)setZoomGesturesEnabled:(BOOL)zoomGesturesEnabled{
    NSLog(@"setZoomGesturesEnabled: %d", zoomGesturesEnabled);
    self.gesturesEnabled = zoomGesturesEnabled;
}

- (void)setScrollGesturesEnabled:(BOOL)scrollGesturesEnabled{
    NSLog(@"setScrollGesturesEnabled: %d", scrollGesturesEnabled);
    self.scrollEnabled = scrollGesturesEnabled;
}

- (void)setCenterLatLng:(NSDictionary *)LatLngObj {
    double lat = [RCTConvert double:LatLngObj[@"lat"]];
    double lng = [RCTConvert double:LatLngObj[@"lng"]];
    CLLocationCoordinate2D point = CLLocationCoordinate2DMake(lat, lng);
    self.centerCoordinate = point;
}

- (void)setLocationData:(NSDictionary *)locationData {
    NSLog(@"setLocationData");
    if (_userLocation == nil) {
        _userLocation = [[BMKUserLocation alloc] init];
    }
    CLLocationCoordinate2D coord = [OverlayUtils getCoorFromOption:locationData];
    CLLocation *location = [[CLLocation alloc] initWithLatitude:coord.latitude longitude:coord.longitude];
    _userLocation.location = location;
    [self updateLocationData:_userLocation];
}

- (void)insertReactSubview:(UIView *)subview atIndex:(NSInteger)atIndex {
    NSLog(@"childrenCount:%d", _childrenCount);
    if (_markers == nil) {
        _markers = [[NSMutableSet alloc] init];
    }
    if (_circles == nil) {
        _circles = [[NSMutableSet alloc] init];
    }
    
    if ([subview isKindOfClass:[OverlayView class]]) {
        OverlayView *overlayView = (OverlayView *) subview;
        if ([subview isKindOfClass:[OverlayMarker class]]) {
            [_markers addObject:subview];
        }
        else if ([subview isKindOfClass:[OverlayCircle class]]) {
            [_circles addObject:subview];
        }
        [overlayView addToMap:self];
    }
    [super insertReactSubview:subview atIndex:atIndex];
}

- (void)removeReactSubview:(UIView *)subview {
    NSLog(@"removeReactSubview");
    if ([subview isKindOfClass:[OverlayView class]]) {
        OverlayView *overlayView = (OverlayView *) subview;
        [overlayView removeFromMap:self];
        if ([subview isKindOfClass:[OverlayMarker class]]) {
            [_markers removeObject:subview];
        } else if([subview isKindOfClass:[OverlayCircle class]]){
            [_circles removeObject:subview];
        }
    }
    [super removeReactSubview:subview];
}

- (void)didSetProps:(NSArray<NSString *> *) props {
    NSLog(@"mapView didSetProps: %@", props);
    [super didSetProps:props];
}

- (void)didUpdateReactSubviews {
    for (int i = 0; i < [self.reactSubviews count]; i++) {
        UIView * view = [self.reactSubviews objectAtIndex:i];
        if ([view isKindOfClass:[OverlayView class]]) {
            OverlayView *overlayView = (OverlayView *) view;
            [overlayView update];
        }
    }
    [super didUpdateReactSubviews];
    NSLog(@"didUpdateReactSubviews:%d", [self.reactSubviews count]);
}

/*
 * 方法废弃，采用下面方面，实现和marker一样
 **/
- (OverlayView *)_findOverlayView:(id<BMKOverlay>)overlay {
    NSArray* reactSuviews = self.reactSubviews;
    for (int i = 0; i < [reactSuviews count]; i++) {
        UIView * view = [reactSuviews objectAtIndex:i];
        if ([view isKindOfClass:[OverlayView class]]) {
            OverlayView *overlayView = (OverlayView *) view;
            if ([overlayView ownOverlay:overlay]) {
                return overlayView;
            }
        }
    }
    return nil;
}
- (OverlayView *)findOverlayView:(id<BMKOverlay>)overlay {
    NSEnumerator *enumerator = [_circles objectEnumerator];
    OverlayCircle *circle  = [enumerator nextObject];
    while (circle != nil) {
        if ([circle ownOverlay:overlay]) {
            return circle;
        }
        circle = [enumerator nextObject];
    }
    return nil;
}
- (OverlayMarker *)findOverlayMaker:(id<BMKAnnotation>)annotation {
    NSEnumerator *enumerator = [_markers objectEnumerator];
    OverlayMarker *marker  = [enumerator nextObject];
    while (marker != nil) {
        if ([marker.annotation isEqual:annotation]) {
            return marker;
        }
        marker = [enumerator nextObject];
    }
    return nil;
}

- (void)dealloc {
    NSLog(@"BaiduMapView dealloc");
}

@end

/**
 * Copyright (c) 2016-present, lovebing.org.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

import {
  NativeModules
} from 'react-native';

import _MapView from './js/MapView';
import _MapTypes from './js/MapTypes';
import _Geolocation from './js/Geolocation';
import _GetDistance from './js/GetDistance';
import _Overlay from './js/Overlay/index';
import _PoiSugSearch from './js/PoiSugSearch';
import _PoiSearch from './js/PoiSearch';
import _RoutePlanSearch from './js/RoutePlanSearch';

export const MapView = _MapView;
export const MapTypes = _MapTypes;
export const Geolocation = _Geolocation;
export const GetDistance = _GetDistance;
export const Overlay = _Overlay;
export const PoiSugSearch = _PoiSugSearch;
export const PoiSearch = _PoiSearch;
export const RoutePlanSearch = _RoutePlanSearch;
export const MapApp = NativeModules.BaiduMapAppModule;

export const BaiduMapManager = NativeModules.BaiduMapManager;

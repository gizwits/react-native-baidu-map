/**
 * Copyright (c) 2016-present, lovebing.org.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

import {
  NativeModules,
  DeviceEventEmitter,
} from "react-native";

import React, {
  Component,
  PropTypes,
} from "react";
import { CoorType, getCoordType } from "react-native-gizwits-baidu-map";


const _module = NativeModules.BaiduGeolocationModule;

const _locatingUpdateListener = {
  listener: null,
  handler: null,
  onLocationUpdate: (resp) => {
    this.listener && this.listener(resp);
  },
  setListener: (listener) => {
    this.listener = listener;
  },
};

export default {
  geocode(city, addr) {
    return new Promise((resolve, reject) => {
      try {
        _module.geocode(city, addr);
      } catch (e) {
        reject(e);
        return;
      }
      const sub_onGetGeoCodeResult = DeviceEventEmitter.addListener("onGetGeoCodeResult", resp => {
        resolve(resp);
        sub_onGetGeoCodeResult.remove();
      });
    });
  },
  convertGPSCoor(lat, lng) {
    return _module.convertGPSCoor(lat, lng);
  },
  reverseGeoCode(lat, lng) {
    return new Promise((resolve, reject) => {
      try {
        _module.reverseGeoCode(lat, lng);
      } catch (e) {
        reject(e);
        return;
      }
      const sub_onGetReverseGeoCodeResult = DeviceEventEmitter.addListener("onGetReverseGeoCodeResult", resp => {
        resolve(resp);
        sub_onGetReverseGeoCodeResult.remove();
      });
    });
  },
  reverseGeoCodeGPS(lat, lng) {
    return new Promise((resolve, reject) => {
      try {
        _module.reverseGeoCodeGPS(lat, lng);
      } catch (e) {
        reject(e);
        return;
      }
      const sub_onGetReverseGeoCodeResult = DeviceEventEmitter.addListener("onGetReverseGeoCodeResult", resp => {
        resp.latitude = parseFloat(resp.latitude);
        resp.longitude = parseFloat(resp.longitude);
        resolve(resp);
        sub_onGetReverseGeoCodeResult.remove();
      });
    });
  },
  getCurrentPosition(coorType) {
    if (!coorType) {
      coorType = "bd09ll";
    } else {
      coorType = coorType.toLowerCase();
    }

    return new Promise((resolve, reject) => {
      try {
        _module.getCurrentPosition(coorType);
      } catch (e) {
        reject(e);
        return;
      }
      const sub_onGetCurrentLocationPosition = DeviceEventEmitter.addListener("onGetCurrentLocationPosition", resp => {
        if (resp.errcode) {
          reject(resp);
          return;
        }
        if (!resp.address) {
          resp.address = `${resp.province} ${resp.city} ${resp.district} ${resp.streetName}`;
        }
        resolve(resp);
        sub_onGetCurrentLocationPosition.remove();
      });
    });
  },
  startLocating(listener, coorType) {
    if (!coorType) {
      coorType = "bd09ll";
    } else {
      coorType = coorType.toLowerCase();
    }
    _module.startLocating(coorType);
    if (_locatingUpdateListener.handler == null) {
      _locatingUpdateListener.handler = DeviceEventEmitter.addListener("onLocationUpdate", resp => {
        if (!resp.address) {
          resp.address = `${resp.province} ${resp.city} ${resp.district} ${resp.streetName}`;
        }
        _locatingUpdateListener.onLocationUpdate(resp);
      });
    }
    _locatingUpdateListener.setListener(listener);
  },
  stopLocating() {
    _module.stopLocating();
    if (_locatingUpdateListener.handler != null) {
      _locatingUpdateListener.handler.remove();
      _locatingUpdateListener.handler = null;
    }
  },
  setCoordType(coordType) {
    if (!coordType) {
      coordType = "bd09ll";
    } else {
      coordType = coordType.toLowerCase();
    }
    _module.setCoordType(coordType);
  },
  getCoordType() {
    return _module.getCoordType();
  },
  convertCoordinate(coordType, sourceLatLng) {
    return _module.convertCoordinate(coordType, sourceLatLng);
  },
};

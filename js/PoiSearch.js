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
import type { Double } from "react-native/Libraries/Types/CodegenTypes";
import { Location } from "react-native-gizwits-baidu-map";


const _module = NativeModules.PoiSearchModule;

export default {
  searchInCity(city: string, keyword: string, pageIndex: number = 0, pageSize: number = 10, cityLimit: boolean = false, scope: number = 1) {
    return new Promise((resolve, reject) => {
      try {
        _module.searchInCity(city, keyword, pageIndex, pageSize, cityLimit, scope);
      } catch (e) {
        reject(e);
        return;
      }
      const sub_searchInCity = DeviceEventEmitter.addListener("onGetPoiResult", resp => {
        if (resp.errcode) {
          reject(resp);
          return;
        }
        resolve(resp);
        sub_searchInCity.remove();
      });
    });
  },
  searchNearby(keyword: string, latLng: Location, radius: number, pageIndex: number = 0, pageSize: number = 10, cityLimit: boolean = false, scope: number = 1) {
    return new Promise((resolve, reject) => {
      try {
        _module.searchNearby(keyword, latLng, radius, pageIndex, pageSize, cityLimit, scope);
      } catch (e) {
        reject(e);
        return;
      }
      const sub_searchNearby = DeviceEventEmitter.addListener("onGetPoiResult", resp => {
        if (resp.errcode) {
          reject(resp);
          return;
        }
        resolve(resp);
        sub_searchNearby.remove();
      });
    });
  },
};

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


const _module = NativeModules.RoutePlanSearchModule;

export default {
  drivingSearch(from: Location, to: Location) {
    return new Promise((resolve, reject) => {
      try {
        _module.drivingSearch(from, to);
      } catch (e) {
        reject(e);
        return;
      }
      const sub_drivingSearch = DeviceEventEmitter.addListener("onGetDrivingRouteResult", resp => {
        if (resp.errcode) {
          reject(resp);
          return;
        }
        resolve(resp);
        sub_drivingSearch.remove();
      });
    });
  },
};

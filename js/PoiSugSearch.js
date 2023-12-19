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


const _module = NativeModules.PoiSugSearchModule;

export default {
  requestSuggestion(keyword: string, city: string, cityLimit: boolean = false) {

    return new Promise((resolve, reject) => {
      try {
        _module.requestSuggestion(keyword, city, cityLimit);
      } catch (e) {
        reject(e);
        return;
      }
      const sub_onGetSuggestionResult = DeviceEventEmitter.addListener("onGetSuggestionResult", resp => {
        if (resp.errcode) {
          reject(resp);
          return;
        }
        resolve(resp);
        sub_onGetSuggestionResult.remove();
      });
    });
  },
};

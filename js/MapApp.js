/**
 * Copyright (c) 2016-present, lovebing.org.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

 import {
  NativeModules
} from 'react-native';

import React, {
  Component,
  PropTypes
} from 'react';

const module = NativeModules.BaiduMapAppModule;

export default {
  openDrivingRoute(start, end) {
    module.openDrivingRoute(start, end);
  }
  openTransitRoute(start, end) {
    module.openTransitRoute(start, end);
  }
  openWalkNavi(start, end) {
    module.openWalkNavi(start, end);
  }
};

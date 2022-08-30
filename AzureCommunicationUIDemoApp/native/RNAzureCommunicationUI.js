//
//  Copyright (c) Microsoft Corporation. All rights reserved.
//  Licensed under the MIT License.
//

import {
  NativeModules,
  NativeEventEmitter,
} from 'react-native';

const { RNAzureCommunicationUICalling } = NativeModules;
const RNAzureCommunicationUICallingEvents = new NativeEventEmitter(RNAzureCommunicationUICalling)

RNAzureCommunicationUICallingEvents.addListener(
  "getToken",
  async (res) => {
    let response = await fetch(res.url);

    if (response.ok) {
      let json = await response.json();
      RNAzureCommunicationUICalling.tokenRefresher(json.token);
    } else {
      console.log(`HTTP-Error: ${response.status}`);
    }
  }
);

export default RNAzureCommunicationUICalling;

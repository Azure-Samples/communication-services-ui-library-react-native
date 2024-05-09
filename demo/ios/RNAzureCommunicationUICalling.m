//
//  Copyright (c) Microsoft Corporation. All rights reserved.
//  Licensed under the MIT License.
//

#import <React/RCTBridgeModule.h>
#import <React/RCTEventEmitter.h>

@interface RCT_EXTERN_MODULE(RNAzureCommunicationUICalling, RCTEventEmitter)

RCT_EXTERN_METHOD(tokenRefresher:(nullable NSString *)token)

RCT_EXTERN_METHOD(startCallComposite:(NSDictionary *)localOptions
                  localAvatar:(nullable NSDictionary *)localAvatar
                  remoteOptions:(NSDictionary *)remoteOptions
                  remoteAvatar:(nullable NSDictionary *)remoteAvatar
                  localizationOptions:(NSDictionary *)localizationOptions
                  resolver:(RCTPromiseResolveBlock)resolve
                  rejecter:(RCTPromiseRejectBlock)reject)

RCT_EXTERN_METHOD(getSupportedLocales:(RCTPromiseResolveBlock)resolve
                  rejecter:(RCTPromiseRejectBlock)reject)

RCT_EXTERN_METHOD(getDebugInfo:(RCTPromiseResolveBlock)resolve
                  rejecter:(RCTPromiseRejectBlock)reject)
@end

//
//  Copyright (c) Microsoft Corporation. All rights reserved.
//  Licensed under the MIT License.
//

#import <React/RCTBridgeModule.h>
#import <React/RCTEventEmitter.h>

@interface RCT_EXTERN_MODULE(RNAzureCommunicationUICalling, RCTEventEmitter)

RCT_EXTERN_METHOD(tokenRefresher:(nullable NSString *)token)

RCT_EXTERN_METHOD(startCallComposite:(NSString *)displayName
                  tokenInput:(nonnull NSString *)tokenInput
                  meetingInput:(nonnull NSString *)meetingInput
                  localAvatar:(nullable NSDictionary *)localAvatar
                  title:(nonnull NSString *)languageCode
                  subtitle:(nonnull NSString *)languageCode
                  languageCode:(nonnull NSString *)languageCode
                  isRightToLeft:(nonnull BOOL) isRightToLeft
                  remoteAvatar:(nullable NSDictionary *)remoteAvatar
                  resolver:(RCTPromiseResolveBlock)resolve
                  rejecter:(RCTPromiseRejectBlock)reject)

RCT_EXTERN_METHOD(getSupportedLocales:(RCTPromiseResolveBlock)resolve
                  rejecter:(RCTPromiseRejectBlock)reject)

@end

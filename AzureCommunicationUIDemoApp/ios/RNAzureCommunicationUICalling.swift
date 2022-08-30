//
//  Copyright (c) Microsoft Corporation. All rights reserved.
//  Licensed under the MIT License.
//

import AzureCommunicationUICalling
import AzureCommunicationCalling
import UIKit
import SwiftUI

@objc(RNAzureCommunicationUICalling)
class RNAzureCommunicationUICalling: RCTEventEmitter {
    enum RNEvents: String, CaseIterable {
        case getToken
    }

    private var tokenRefresherHandler: TokenRefreshHandler?

    override static func requiresMainQueueSetup() -> Bool {
        return false
    }

    override func supportedEvents() -> [String]! {
        return RNEvents.allCases.map({ $0.rawValue })
    }

    @objc func tokenRefresher(_ token: String) {
        self.tokenRefresherHandler?(token, nil)
    }
    
    @objc func getSupportedLocales(_ resolve: @escaping RCTPromiseResolveBlock,
                                   rejecter reject: @escaping RCTPromiseRejectBlock) {
        let localeStrings = SupportedLocale.values.map { $0.identifier }
        resolve(localeStrings)
    }

    @objc func startCallComposite(_ displayName: String,
                                  tokenInput: String,
                                  meetingInput: String,
                                  localAvatar: AnyObject?,
                                  languageCode: String,
                                  isRightToLeft: Bool,
                                  remoteAvatar: AnyObject?,
                                  resolver resolve: @escaping RCTPromiseResolveBlock,
                                  rejecter reject: @escaping RCTPromiseRejectBlock) {
        DispatchQueue.main.async {
            self._startCallComposite(displayName: displayName,
                                     tokenInput: tokenInput,
                                     meetingInput: meetingInput,
                                     localAvatar: localAvatar,
                                     languageCode: languageCode,
                                     isRightToLeft: isRightToLeft,
                                     remoteAvatar: remoteAvatar,
                                     resolver: resolve,
                                     rejecter: reject)
        }
    }

    private func _startCallComposite(displayName: String,
                                     tokenInput: String,
                                     meetingInput: String,
                                     localAvatar: AnyObject?,
                                     languageCode: String,
                                     isRightToLeft: Bool = false,
                                     remoteAvatar: AnyObject?,
                                     resolver resolve: @escaping RCTPromiseResolveBlock,
                                     rejecter reject: @escaping RCTPromiseRejectBlock) {
        
        var localizationConfig: LocalizationOptions?
        let layoutDirection: LayoutDirection = isRightToLeft ? .rightToLeft : .leftToRight
        let locale = Locale(identifier: languageCode.isEmpty ? "en" : languageCode)
        localizationConfig = LocalizationOptions(locale: locale,
                                                 layoutDirection: layoutDirection)
        
        let callCompositeOptions: CallCompositeOptions
        callCompositeOptions = CallCompositeOptions(localization: localizationConfig)

        let callComposite = CallComposite(withOptions: callCompositeOptions)
        callComposite.events.onError = onError(reject)
        
        let onRemoteParticipantJoinedHandler: ([CommunicationIdentifier]) -> Void = { [weak callComposite] communicationIds in
            guard let callComposite = callComposite else {
                return
            }
            self.onRemoteParticipantJoined(resolve, reject)(callComposite, communicationIds, remoteAvatar)
        }
        callComposite.events.onRemoteParticipantJoined = onRemoteParticipantJoinedHandler

        var localOptions: LocalOptions? = nil
        if let localAvatar = localAvatar {
            let avatar = RCTConvert.uiImage(localAvatar)
            let participantViewData = ParticipantViewData(avatar: avatar, displayName: displayName)
            localOptions = LocalOptions(participantViewData: participantViewData)
        }

        if let communicationTokenCredential = try? getTokenCredential(tokenInput: tokenInput) {
            if let url = URL(string: meetingInput),
               UIApplication.shared.canOpenURL(url as URL) {
                let remoteOptions = RemoteOptions(for: .teamsMeeting(teamsLink: meetingInput),
                                                  credential: communicationTokenCredential,
                                                  displayName: displayName)
                callComposite.launch(remoteOptions: remoteOptions, localOptions: localOptions)
            } else {
                let remoteOptions = RemoteOptions(for: .groupCall(groupId: UUID(uuidString: meetingInput) ?? UUID()),
                                                  credential: communicationTokenCredential,
                                                  displayName: displayName)
                callComposite.launch(remoteOptions: remoteOptions, localOptions: localOptions)
            }
            resolve(nil)
        } else {
            reject(DemoError.invalidToken.getErrorCode(),
                   "Token is invalid",
                   DemoError.invalidToken)
        }
    }

    private func getTokenCredential(tokenInput: String) throws -> CommunicationTokenCredential {
        if let url = URL(string: tokenInput),
           UIApplication.shared.canOpenURL(url as URL) {
            let communicationTokenRefreshOptions = CommunicationTokenRefreshOptions(initialToken: nil,
                                                                                    refreshProactively: true,
                                                                                    tokenRefresher: getCommunicationToken(tokenUrl: url))
            if let communicationTokenCredential = try? CommunicationTokenCredential(withOptions: communicationTokenRefreshOptions) {
                return communicationTokenCredential
            } else {
                throw DemoError.invalidToken
            }
        } else {
            if let communicationTokenCredential = try? CommunicationTokenCredential(token: tokenInput) {
                return communicationTokenCredential
            } else {
                throw DemoError.invalidToken
            }
        }
    }

    private func getCommunicationToken(tokenUrl: URL) -> TokenRefresher {
        return { [weak self] completionHandler in
            self?.tokenRefresherHandler = completionHandler
            self?.sendEvent(withName: RNEvents.getToken.rawValue,
                            body: ["url": tokenUrl.absoluteString])
        }
    }
    
    func onError(_ reject: @escaping RCTPromiseRejectBlock) -> ((CallCompositeError) -> Void) {
        return { (error: CallCompositeError) -> Void in
            print("ReactNativeDemoView::getEventsHandler::onError \(error)")
            print("ReactNativeDemoView error.code \(error.code)")
            reject(error.code,
                   error.error?.localizedDescription ?? "Unknown error",
                   error.error)
        }
    }
    
    func onRemoteParticipantJoined(_ resolve: @escaping RCTPromiseResolveBlock,
                                   _ reject: @escaping RCTPromiseRejectBlock) -> ((CallComposite, [CommunicationIdentifier], AnyObject?) -> Void) {
        return { (callComposite: CallComposite, identifiers: [CommunicationIdentifier], remoteAvatar: AnyObject?) -> Void in
            print("ReactNativeDemoView::getEventsHandler::onRemoteParticipantJoined \(identifiers)")
            guard let remoteAvatar = remoteAvatar, let remoteAvatarImage = RCTConvert.uiImage(remoteAvatar) else {
                return
            }
            RemoteParticipantAvatarHelper.onRemoteParticipantJoined(to: callComposite,
                                                                    identifiers: identifiers,
                                                                    remoteAvatar: remoteAvatarImage)
        }
    }
}

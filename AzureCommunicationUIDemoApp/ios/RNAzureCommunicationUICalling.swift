//
//  Copyright (c) Microsoft Corporation. All rights reserved.
//  Licensed under the MIT License.
//

import AzureCommunicationUICalling
import AzureCommunicationCalling
import AzureCommunicationCommon
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

    @objc func startCallComposite(_ localOptions: NSDictionary,
                                    localAvatar: AnyObject?,
                                    remoteOptions: NSDictionary,
                                    remoteAvatar: AnyObject?,
                                    localizationOptions: NSDictionary,
                                    resolver resolve: @escaping RCTPromiseResolveBlock,
                                    rejecter reject: @escaping RCTPromiseRejectBlock) {

        guard let localOptionsDict = localOptions as? [String: Any], 
              let remoteOptionsDict = remoteOptions as? [String: Any], 
              let localizationOptionsDict = localizationOptions as? [String: Any] else {
            return
        }

        // required remote options
        guard let tokenInput = remoteOptionsDict["token"] as? String, 
              let meetingInput = remoteOptionsDict["meetingURL"] as? String else {
            return
        }

        // required localization options
        guard let languageCode = localizationOptionsDict["locale"] as? String else {
            return
        }           

        DispatchQueue.main.async {
            self._startCallComposite(displayName: localOptionsDict["displayName"] as? String
                                     tokenInput: tokenInput,
                                     meetingInput: meetingInput,
                                     localAvatar: localAvatar,
                                     title: localOptionsDict["title"] as? String,
                                     subtitle: localOptionsDict["subtitle"] as? String,
                                     languageCode: languageCode,
                                     isRightToLeft: localizationOptionsDict["locale"] as? Bool ?? false,
                                     remoteAvatar: remoteAvatar,
                                     resolver: resolve,
                                     rejecter: reject)
        }
    }

    private func _startCallComposite(displayName: String?,
                                     tokenInput: String,
                                     meetingInput: String,
                                     localAvatar: AnyObject?,
                                     title: String?,
                                     subtitle: String?,
                                     languageCode: String,
                                     isRightToLeft: Bool,
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
        var participantViewData: ParticipantViewData? = nil
        var setupViewData: SetupScreenViewData? = nil

        if let localAvatar = localAvatar  {
            let avatar = RCTConvert.uiImage(localAvatar)
            participantViewData = ParticipantViewData(avatar: avatar, displayName: displayName)
        }

        if let title = title {
            setupViewData = SetupScreenViewData(title: title, subtitle: subtitle)
        }

        localOptions = LocalOptions(participantViewData: participantViewData, setupScreenViewData: setupViewData)

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
            reject(RNCallCompositeConnectionError.invalidToken.getErrorCode(),
                   "Token is invalid",
                   RNCallCompositeConnectionError.invalidToken)
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
                throw RNCallCompositeConnectionError.invalidToken
            }
        } else {
            if let communicationTokenCredential = try? CommunicationTokenCredential(token: tokenInput) {
                return communicationTokenCredential
            } else {
                throw RNCallCompositeConnectionError.invalidToken
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
            RNCallCompositeRemoteParticipantAvatarHelper.onRemoteParticipantJoined(to: callComposite,
                                                                    identifiers: identifiers,
                                                                    remoteAvatar: remoteAvatarImage)
        }
    }
}

enum RNCallCompositeConnectionError: Error {
    case invalidToken

    func getErrorCode() -> String {
        switch self {
        case .invalidToken:
            return CallCompositeErrorCode.tokenExpired
        }
    }
}

struct RNCallCompositeRemoteParticipantAvatarHelper {
    private static func getRemoteParticipantId(_ identifier: CommunicationIdentifier) -> String? {
        switch identifier {
        case is CommunicationUserIdentifier:
            return (identifier as? CommunicationUserIdentifier)?.identifier
        case is UnknownIdentifier:
            return (identifier as? UnknownIdentifier)?.identifier
        case is PhoneNumberIdentifier:
            return (identifier as? PhoneNumberIdentifier)?.phoneNumber
        case is MicrosoftTeamsUserIdentifier:
            return (identifier as? MicrosoftTeamsUserIdentifier)?.userId
        default:
            return nil
        }
    }

    static func onRemoteParticipantJoined(to callComposite: CallComposite,
                                          identifiers: [CommunicationIdentifier],
                                          remoteAvatar: UIImage? = nil) {
        let avatars = ["cat", "fox", "koala", "monkey", "mouse", "octopus"]
        for identifier in identifiers {
            let id = getRemoteParticipantId(identifier)
            let nameIdValue = id != nil ? "\(id?.suffix(4) ?? "")" : ""
            var avatarImage: UIImage?
            var selectedAvatarName = ""
            if let lastSymbol = id?.last {
                let index = Int((lastSymbol.asciiValue ?? 0 ) % 6)
                selectedAvatarName = avatars[index]
                avatarImage = UIImage(named: selectedAvatarName)
            }
            let displayName = selectedAvatarName.isEmpty ? nameIdValue : "\(selectedAvatarName) \(nameIdValue)"
            let participantViewData = ParticipantViewData(avatar: remoteAvatar ?? avatarImage,
                                                          displayName: displayName)
            callComposite.set(remoteParticipantViewData: participantViewData,
                              for: identifier) { result in
                switch result {
                case .success:
                    print("::::RNCallCompositeRemoteParticipantAvatarHelper::onRemoteParticipantJoined::success")
                    break
                case .failure(let error):
                    print("::::RNCallCompositeRemoteParticipantAvatarHelper::onRemoteParticipantJoined::failure \(error)")
                }
            }
        }
    }
}


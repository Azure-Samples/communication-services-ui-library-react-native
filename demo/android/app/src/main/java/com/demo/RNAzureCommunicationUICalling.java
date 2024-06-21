package com.demo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.util.LayoutDirection;
import android.util.Log;
import android.webkit.URLUtil;

import com.azure.android.communication.common.CommunicationIdentifier;
import com.azure.android.communication.common.CommunicationTokenCredential;
import com.azure.android.communication.common.CommunicationTokenRefreshOptions;
import com.azure.android.communication.ui.calling.CallComposite;
import com.azure.android.communication.ui.calling.CallCompositeBuilder;
import com.azure.android.communication.ui.calling.models.CallCompositeAudioVideoMode;
import com.azure.android.communication.ui.calling.models.CallCompositeCallHistoryRecord;
import com.azure.android.communication.ui.calling.models.CallCompositeDebugInfo;
import com.azure.android.communication.ui.calling.models.CallCompositeGroupCallLocator;
import com.azure.android.communication.ui.calling.models.CallCompositeJoinLocator;
import com.azure.android.communication.ui.calling.models.CallCompositeLocalOptions;
import com.azure.android.communication.ui.calling.models.CallCompositeLocalizationOptions;
import com.azure.android.communication.ui.calling.models.CallCompositeMultitaskingOptions;
import com.azure.android.communication.ui.calling.models.CallCompositeParticipantViewData;
import com.azure.android.communication.ui.calling.models.CallCompositeSetupScreenViewData;
import com.azure.android.communication.ui.calling.models.CallCompositeCallScreenControlBarOptions;
import com.azure.android.communication.ui.calling.models.CallCompositeCallScreenOptions;
import com.azure.android.communication.ui.calling.models.CallCompositeSupportedLocale;
import com.azure.android.communication.ui.calling.models.CallCompositeLeaveCallConfirmationMode;
import com.azure.android.communication.ui.calling.models.CallCompositeSupportedScreenOrientation;
import com.azure.android.communication.ui.calling.models.CallCompositeTeamsMeetingLinkLocator;
import com.azure.android.communication.ui.calling.models.CallCompositeUserReportedIssueEvent;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;

import org.json.JSONObject;
import org.threeten.bp.format.DateTimeFormatter;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class RNAzureCommunicationUICalling extends ReactContextBaseJavaModule {

    private static final String TAG = "RNAzureCallingUI";
    String mToken = "";
    CallComposite callComposite = null;

    RNAzureCommunicationUICalling(ReactApplicationContext context) {
        super(context);
    }

    @Override
    public String getName() {
        return "RNAzureCommunicationUICalling";
    }

    @ReactMethod
    public void dismiss() {
        if (callComposite != null) {
            callComposite.dismiss();
        }
    }

    @ReactMethod
    public void startCallComposite(ReadableMap localOptions, 
                                   ReadableMap localAvatarImageResource, 
                                   ReadableMap remoteOptions, 
                                   ReadableMap remoteAvatarImageResource, 
                                   ReadableMap localizationOptions,
                                   ReadableMap orientationOptions,
                                   Promise promise) {
        // local options
        String displayName = localOptions.getString("displayName");
        String title = localOptions.getString("title");
        String subtitle = localOptions.getString("subtitle");
        boolean disableLeaveCallConfirmation = localOptions.getBoolean("disableLeaveCallConfirmation");
        
        // remote options
        String tokenInput = remoteOptions.getString("token");
        String meetingInput = remoteOptions.getString("meeting");

        // localization options
        String selectedLanguage = localizationOptions.getString("locale");
        boolean isRightToLeft = localizationOptions.getBoolean("layout");
        
        if (URLUtil.isValidUrl(tokenInput.trim())) {
            getCommunicationToken(tokenInput, displayName, meetingInput, localAvatarImageResource, title, subtitle, selectedLanguage, isRightToLeft, disableLeaveCallConfirmation, remoteAvatarImageResource, orientationOptions, promise);
        } else {
            mToken = tokenInput;
            launchComposite(displayName, meetingInput, localAvatarImageResource, title, subtitle, selectedLanguage, isRightToLeft, disableLeaveCallConfirmation, remoteAvatarImageResource, orientationOptions, promise);
        }
    }

    @ReactMethod
    public void getSupportedLocales(Promise promise) {
        WritableArray wArr = Arguments.createArray();
        for (Locale locale : CallCompositeSupportedLocale.getSupportedLocales()) {
            wArr.pushString(locale.getLanguage());
        }
        promise.resolve(wArr);
    }

    @ReactMethod
    public void getSupportedOrientations(Promise promise) {
        WritableArray wArr = Arguments.createArray();
        for (CallCompositeSupportedScreenOrientation orientation: CallCompositeSupportedScreenOrientation.values()) {
            wArr.pushString((orientation.toString()));
        }
        promise.resolve(wArr);
    }

    @ReactMethod
    public void addListener(String eventName) {}

    @ReactMethod()
    public void getDebugInfo(Promise promise) {
        WritableArray wArr = Arguments.createArray();
        if (callComposite == null) {
            callComposite = new CallCompositeBuilder().build();
        }
        CallCompositeDebugInfo debugInfo = callComposite.getDebugInfo(getCurrentActivity());
        for (CallCompositeCallHistoryRecord record : debugInfo.getCallHistoryRecords()) {
            WritableMap recordMap = Arguments.createMap();
            recordMap.putString("callStartedOn", record.getCallStartedOn().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
            WritableArray callIdsArr = Arguments.createArray();
            for (String callId: record.getCallIds()) {
                callIdsArr.pushString(callId);
            }
            recordMap.putArray("callIds", callIdsArr);
            wArr.pushMap(recordMap);
        }

        WritableMap map = Arguments.createMap();
        map.putArray("callHistoryRecords", wArr);
        promise.resolve(map);
    }
    
    @ReactMethod
    public void removeListeners(Integer count) {}

    public void launchComposite(String displayName, String meetingInput, ReadableMap localAvatarImageResource,
                                String title, String subtitle, String selectedLanguage, boolean isRightToLeft, boolean disableLeaveCallConfirmation, ReadableMap remoteAvatarImageResource,
                                ReadableMap orientationOptions,
                                Promise promise) {
        Context context = getCurrentActivity();

        // Orientation options

        String setupOrientation = orientationOptions.getString("setupOrientation");
        String callOrientation = orientationOptions.getString("callOrientation");

        int layoutDirection = isRightToLeft ? LayoutDirection.RTL : LayoutDirection.LTR;

        CallCompositeCallScreenControlBarOptions callScreenControlBarOptions = new CallCompositeCallScreenControlBarOptions();
        if (disableLeaveCallConfirmation) {
            callScreenControlBarOptions.setLeaveCallConfirmation(CallCompositeLeaveCallConfirmationMode.ALWAYS_DISABLED);
        } else {
            callScreenControlBarOptions.setLeaveCallConfirmation(CallCompositeLeaveCallConfirmationMode.ALWAYS_ENABLED);
        }
        
        CallCompositeCallScreenOptions callScreenOptions = new CallCompositeCallScreenOptions();
        callScreenOptions.setControlBarOptions(callScreenControlBarOptions);
                        
        try {
            CommunicationTokenRefreshOptions communicationTokenRefreshOptions =
                    new CommunicationTokenRefreshOptions(this::fetchToken, true);

            CommunicationTokenCredential communicationTokenCredential = new CommunicationTokenCredential(communicationTokenRefreshOptions);

            CallComposite callComposite = new CallCompositeBuilder()
                    .localization(new CallCompositeLocalizationOptions(Locale.forLanguageTag(selectedLanguage), layoutDirection))
                    .setupScreenOrientation(getCompositeDefinedOrientation(setupOrientation))
                    .callScreenOrientation(getCompositeDefinedOrientation(callOrientation))
                    .multitasking(new CallCompositeMultitaskingOptions(true))
                    .callScreenOptions(callScreenOptions)
                    .applicationContext(context.getApplicationContext())
                    .credential(communicationTokenCredential)
                    .displayName(displayName)
                    .build();
            callComposite.addOnErrorEventHandler(eventHandler -> {
                Log.d(TAG, "================= application is logging exception =================");
                Log.d(TAG, eventHandler.getCause().toString());
                Log.d(TAG, eventHandler.getErrorCode().toString());
                Log.d(TAG, "====================================================================");

                promise.reject(eventHandler.getErrorCode() + " " + eventHandler.getCause().getMessage());
            });

            callComposite.addOnPictureInPictureChangedEventHandler(eventArgs -> {
                Log.d(TAG, "addOnMultitaskingStateChangedEventHandler it.isInPictureInPicture: " + eventArgs.isInPictureInPicture());
            });

            callComposite.addOnUserReportedEventHandler(eventArgs -> {
                CallCompositeUserReportedIssueEvent event = eventArgs;
                CallCompositeDebugInfo info = event.getDebugInfo();
                if(info != null) {
                    Log.d(TAG, info.toString());
                }
            });

            if (remoteAvatarImageResource != null) {
                String uri = remoteAvatarImageResource.getString("uri");
                URL url = new URL(uri);
                Bitmap remoteAvatarImageBitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());

                callComposite.addOnRemoteParticipantJoinedEventHandler((remoteParticipantJoinedEvent) -> {
                    for (CommunicationIdentifier identifier : remoteParticipantJoinedEvent.getIdentifiers()) {
                        callComposite.setRemoteParticipantViewData(identifier,
                                new CallCompositeParticipantViewData()
                                        .setDisplayName(uri.substring(uri.lastIndexOf("images/") + 7, uri.lastIndexOf('?')))
                                        .setAvatarBitmap(remoteAvatarImageBitmap)
                        );
                    }
                });
            }

            callComposite.addOnCallStateChangedEventHandler((callStateChangedEvent) -> {
                Log.d(TAG, "================= application is logging call state change =================");
                Log.d(TAG, callStateChangedEvent.getCode().toString());
                Log.d(TAG, callComposite.getCallState().toString());
            });

            callComposite.addOnDismissedEventHandler((dismissedEvent) -> {
                Log.d(TAG, "================= application is logging call composite dismissed =================");
                callComposite.dismiss();
            });   

            if (URLUtil.isValidUrl(meetingInput.trim())) {
                CallCompositeJoinLocator locator = new CallCompositeTeamsMeetingLinkLocator(meetingInput);

                CallCompositeLocalOptions localOptions = new CallCompositeLocalOptions();

                if (localAvatarImageResource != null) {
                    URL url = new URL(localAvatarImageResource.getString("uri"));
                    Bitmap avatarImageBitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());

                    CallCompositeParticipantViewData participantViewData = new CallCompositeParticipantViewData()
                            .setDisplayName(displayName)
                            .setAvatarBitmap(avatarImageBitmap);
                    localOptions.setParticipantViewData(participantViewData);
                }
                
                if (title != null) {
                    CallCompositeSetupScreenViewData setupViewData = new CallCompositeSetupScreenViewData()
                        .setTitle(title)
                        .setSubtitle(subtitle);
                    localOptions.setSetupScreenViewData(setupViewData);
                }
                localOptions.setAudioVideoMode(CallCompositeAudioVideoMode.AUDIO_ONLY);
                
                callComposite.launch(context, locator, localOptions);

            } else {
                boolean isUUID = false;

                try {
                    UUID uuid = UUID.fromString(meetingInput);
                    isUUID = true;
                } catch(Exception ignored) {
                }

                CallCompositeLocalOptions localOptions = new CallCompositeLocalOptions();

                if (localAvatarImageResource != null) {
                    URL url = new URL(localAvatarImageResource.getString("uri"));
                    Bitmap avatarImageBitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());

                    CallCompositeParticipantViewData participantViewData = new CallCompositeParticipantViewData()
                            .setDisplayName(displayName)
                            .setAvatarBitmap(avatarImageBitmap);

                    localOptions.setParticipantViewData(participantViewData);

                }
                if (title != null) {
                    CallCompositeSetupScreenViewData setupViewData = new CallCompositeSetupScreenViewData()
                            .setTitle(title)
                            .setSubtitle(subtitle);
                    localOptions.setSetupScreenViewData(setupViewData);
                }
                if (isUUID) {
                    CallCompositeJoinLocator locator = new CallCompositeGroupCallLocator(UUID.fromString(meetingInput));
                    callComposite.launch(context, locator, localOptions);
                } else {
                    List<CommunicationIdentifier> identifiers = new ArrayList<>();
                    String[] rawIdArray = meetingInput.split(",");
                    for (String rawId : rawIdArray) {
                        rawId = rawId.trim();
                        if (!rawId.isEmpty()) {
                            identifiers.add(CommunicationIdentifier.fromRawId(rawId));
                        }
                    }
                    callComposite.launch(context, identifiers, localOptions);
                }
            }

            promise.resolve(null);
        } catch (Exception e) {
            promise.reject("Token is invalid", e);
        }
    }

    private String fetchToken() {
        return mToken;
    }

    private void getCommunicationToken(String tokenInput, String displayName, String meetingInput, ReadableMap localAvatarImageResource, String title, String subtitle, String selectedLanguage,
                                       boolean isRightToLeft, boolean disableLeaveCallConfirmation, ReadableMap remoteAvatarImageResource,
                                       ReadableMap orientationOptions,
                                       Promise promise) {
        Thread thread = new Thread(() -> {
            try {
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url(tokenInput.trim())
                        .build();

                Response response = client.newCall(request).execute();
                String responseBody = response.body().string();
                JSONObject json = new JSONObject(responseBody);
                mToken = json.getString("token");

                new Handler(Looper.getMainLooper()).post(() -> {
                    launchComposite(displayName, meetingInput, localAvatarImageResource, title, subtitle, selectedLanguage, isRightToLeft, disableLeaveCallConfirmation, remoteAvatarImageResource, orientationOptions, promise);
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        thread.start();
    }

    private CallCompositeSupportedScreenOrientation getCompositeDefinedOrientation(String orientation) {
        if (orientation.equals(CallCompositeSupportedScreenOrientation.PORTRAIT.toString())) {
            return CallCompositeSupportedScreenOrientation.PORTRAIT;
        } else if (orientation.equals(CallCompositeSupportedScreenOrientation.LANDSCAPE.toString())) {
            return CallCompositeSupportedScreenOrientation.LANDSCAPE;
        } else if (orientation.equals(CallCompositeSupportedScreenOrientation.REVERSE_LANDSCAPE.toString())) {
            return CallCompositeSupportedScreenOrientation.REVERSE_LANDSCAPE;
        } else if (orientation.equals(CallCompositeSupportedScreenOrientation.FULL_SENSOR.toString())) {
            return CallCompositeSupportedScreenOrientation.FULL_SENSOR;
        } else if (orientation.equals(CallCompositeSupportedScreenOrientation.USER.toString())) {
            return CallCompositeSupportedScreenOrientation.USER;
        } else if (orientation.equals(CallCompositeSupportedScreenOrientation.USER_LANDSCAPE.toString())) {
            return CallCompositeSupportedScreenOrientation.USER_LANDSCAPE;
        } else {
            return null;
        }
    }
}
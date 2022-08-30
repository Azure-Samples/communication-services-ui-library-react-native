package com.azurecommunicationuidemoapp;

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
import com.azure.android.communication.ui.calling.models.CallCompositeGroupCallLocator;
import com.azure.android.communication.ui.calling.models.CallCompositeJoinLocator;
import com.azure.android.communication.ui.calling.models.CallCompositeLocalOptions;
import com.azure.android.communication.ui.calling.models.CallCompositeLocalizationOptions;
import com.azure.android.communication.ui.calling.models.CallCompositeParticipantViewData;
import com.azure.android.communication.ui.calling.models.CallCompositeRemoteOptions;
import com.azure.android.communication.ui.calling.models.CallCompositeSupportedLocale;
import com.azure.android.communication.ui.calling.models.CallCompositeTeamsMeetingLinkLocator;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableArray;

import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.UUID;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class RNAzureCommunicationUICalling extends ReactContextBaseJavaModule {

    private static final String TAG = "RNAzureCommunicationUICalling";
    String mToken = "";

    RNAzureCommunicationUICalling(ReactApplicationContext context) {
        super(context);
    }

    @Override
    public String getName() {
        return "RNAzureCommunicationUICalling";
    }

    @ReactMethod
    public void startCallComposite(String displayName, String tokenInput, String meetingInput, ReadableMap localAvatarImageResource, String selectedLanguage, boolean isRightToLeft, ReadableMap remoteAvatarImageResource, Promise promise) {
        if (URLUtil.isValidUrl(tokenInput.trim())) {
            getCommunicationToken(tokenInput, displayName, meetingInput, localAvatarImageResource, selectedLanguage, isRightToLeft, remoteAvatarImageResource, promise);
        } else {
            mToken = tokenInput;
            launchComposite(displayName, meetingInput, localAvatarImageResource, selectedLanguage, isRightToLeft, remoteAvatarImageResource, promise);
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

    public void launchComposite(String displayName, String meetingInput, ReadableMap localAvatarImageResource, String selectedLanguage, boolean isRightToLeft, ReadableMap remoteAvatarImageResource, Promise promise) {
        Context context = getCurrentActivity();


        int layoutDirection = isRightToLeft ? LayoutDirection.RTL : LayoutDirection.LTR;

        CallComposite callComposite = new CallCompositeBuilder()
                .localization(new CallCompositeLocalizationOptions(Locale.forLanguageTag(selectedLanguage), layoutDirection)).build();


        try {
            CommunicationTokenRefreshOptions communicationTokenRefreshOptions =
                    new CommunicationTokenRefreshOptions(this::fetchToken, true);

            CommunicationTokenCredential communicationTokenCredential = new CommunicationTokenCredential(communicationTokenRefreshOptions);

            callComposite.addOnErrorEventHandler(eventHandler -> {
                Log.d(TAG, "================= application is logging exception =================");
                Log.d(TAG, eventHandler.getCause().toString());
                Log.d(TAG, eventHandler.getErrorCode().toString());
                Log.d(TAG, "====================================================================");

                promise.reject(eventHandler.getErrorCode() + " " + eventHandler.getCause().getMessage());
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


            if (URLUtil.isValidUrl(meetingInput.trim())) {
                CallCompositeJoinLocator locator = new CallCompositeTeamsMeetingLinkLocator(meetingInput);

                CallCompositeRemoteOptions remoteOptions = new CallCompositeRemoteOptions(
                        locator,
                        communicationTokenCredential,
                        displayName);

                if (localAvatarImageResource != null) {
                    URL url = new URL(localAvatarImageResource.getString("uri"));
                    Bitmap avatarImageBitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());

                    CallCompositeParticipantViewData participantViewData = new CallCompositeParticipantViewData()
                            .setDisplayName(displayName)
                            .setAvatarBitmap(avatarImageBitmap);

                    CallCompositeLocalOptions dataOptions = new CallCompositeLocalOptions(participantViewData);
                    callComposite.launch(context, remoteOptions, dataOptions);

                } else {
                    callComposite.launch(context, remoteOptions);
                }

            } else {
                CallCompositeJoinLocator locator = new CallCompositeGroupCallLocator(UUID.fromString(meetingInput));

                CallCompositeRemoteOptions remoteOptions = new CallCompositeRemoteOptions(
                        locator,
                        communicationTokenCredential,
                        displayName);

                if (localAvatarImageResource != null) {
                    URL url = new URL(localAvatarImageResource.getString("uri"));
                    Bitmap avatarImageBitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());

                    CallCompositeParticipantViewData participantViewData = new CallCompositeParticipantViewData()
                            .setDisplayName(displayName)
                            .setAvatarBitmap(avatarImageBitmap);

                    CallCompositeLocalOptions dataOptions = new CallCompositeLocalOptions(participantViewData);

                    callComposite.launch(context, remoteOptions, dataOptions);

                } else {
                    callComposite.launch(context, remoteOptions);
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

    private void getCommunicationToken(String tokenInput, String displayName, String meetingInput, ReadableMap localAvatarImageResource, String selectedLanguage, boolean isRightToLeft, ReadableMap remoteAvatarImageResource, Promise promise) {
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
                    launchComposite(displayName, meetingInput, localAvatarImageResource, selectedLanguage, isRightToLeft, remoteAvatarImageResource, promise);
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        thread.start();
    }
}
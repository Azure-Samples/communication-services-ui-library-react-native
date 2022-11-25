package com.azurecommunicationuichatdemoapp;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;

import com.azure.android.communication.common.CommunicationTokenCredential;
import com.azure.android.communication.common.CommunicationTokenRefreshOptions;
import com.azure.android.communication.ui.chat.ChatAdapter;
import com.azure.android.communication.ui.chat.ChatAdapterBuilder;
import com.azure.android.communication.ui.chat.presentation.ChatCompositeView;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;

import java.util.concurrent.Callable;

public class RNAzureCommunicationUIChat extends ReactContextBaseJavaModule {

    public static ChatAdapter chatAdapter;
    private ReactApplicationContext rContext;

    RNAzureCommunicationUIChat(ReactApplicationContext context) {
        super(context);
        rContext = context;
    }

    @NonNull
    @Override
    public String getName() {
        return "RNAzureCommunicationUIChat";
    }

    @ReactMethod
    public void startChatComposite(String acsToken, String identity, String chatThreadId, String endpointUrl, String displayName) {
        Log.d("RNAzureCommunication", "" + acsToken + " " + identity + " " + chatThreadId + " " + endpointUrl + " " + displayName);

        CommunicationTokenRefreshOptions communicationTokenRefreshOptions = new CommunicationTokenRefreshOptions(
                new CachedTokenFetcher(acsToken), true);
        CommunicationTokenCredential communicationTokenCredential = new CommunicationTokenCredential(communicationTokenRefreshOptions);

        ChatAdapter mChatAdapter = new ChatAdapterBuilder()
                .endpointUrl(endpointUrl)
                .communicationTokenCredential(communicationTokenCredential)
                .identity(identity)
                .displayName(displayName)
                .build();

        mChatAdapter.connect(rContext, chatThreadId);

        this.chatAdapter = mChatAdapter;

        PojoChatAdapter.INSTANCE.setChatAdapter(chatAdapter);

        final Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //Do something after 100ms
            }
        }, 300);

        Intent intent = new Intent(rContext, ChatActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        rContext.startActivity(intent);
    }



    @ReactMethod
    public void stopChatComposite() {
        chatAdapter.disconnect();
        chatAdapter = null;
    }
}


class CachedTokenFetcher implements Callable<String> {
    private String token;
    CachedTokenFetcher(String token) {
        this.token = token;
    }

    @Override
    public String call() {
        String tokenRefresher;
        if(this.token != null && this.token.length() > 0) {
            tokenRefresher = token;
        } else {
            throw new IllegalStateException("Invalid token function URL or ACS Token.");
        }

        return tokenRefresher;
    }
}


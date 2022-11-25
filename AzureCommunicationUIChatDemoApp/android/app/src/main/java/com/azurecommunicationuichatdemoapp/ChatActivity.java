package com.azurecommunicationuichatdemoapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import com.azure.android.communication.ui.chat.presentation.ChatCompositeView;

public class ChatActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Log.d("RNAzureCommunication", "We are here trying to inflate.");
        FrameLayout container = (FrameLayout) findViewById(R.id.flContainer);
        container.addView(new ChatCompositeView(this, PojoChatAdapter.INSTANCE.getChatAdapter()));
        /*addContentView(
                PojoChatAdapter.INSTANCE.getChatView(),
                new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                )
        );*/
    }
}
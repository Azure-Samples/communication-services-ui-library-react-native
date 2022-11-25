package com.azurecommunicationuichatdemoapp;

import com.azure.android.communication.ui.chat.ChatAdapter;
import com.azure.android.communication.ui.chat.presentation.ChatCompositeView;

public enum PojoChatAdapter {
    INSTANCE;

    private ChatAdapter chatAdapter;

    public ChatAdapter getChatAdapter() {
        return chatAdapter;
    }

    public void setChatAdapter(ChatAdapter chatAdapter) {
        this.chatAdapter = chatAdapter;
    }
}

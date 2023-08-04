package com.sni.secure_chat.services;

import com.sni.secure_chat.model.dto.requests.MessageRequest;
import com.sni.secure_chat.model.dto.requests.SegmentedMessageRequest;

public interface MessageService {
    void sendMessage(MessageRequest messageRequest);
    void sendSegmentedMessage(SegmentedMessageRequest messageRequest);
}

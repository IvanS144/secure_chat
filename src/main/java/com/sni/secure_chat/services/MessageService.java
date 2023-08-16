package com.sni.secure_chat.services;

import com.sni.secure_chat.model.dto.MessageDTO;
import com.sni.secure_chat.model.dto.requests.MessageRequest;
import com.sni.secure_chat.model.dto.requests.SegmentedMessageRequest;

import java.util.List;

public interface MessageService {
    void sendMessage(MessageRequest messageRequest);
    void sendSegmentedMessage(SegmentedMessageRequest messageRequest);
    List<MessageDTO> findMessagesByRecipientId(int recipientId);
}

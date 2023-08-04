package com.sni.secure_chat.model.dto;

import lombok.Data;

@Data
public class MessageDTO {
    private String messageId;
    private String content;
    private Integer senderId;
    private Integer recipientId;
}

package com.sni.secure_chat.model.dto;

import lombok.Data;

@Data
public class MessageSegmentDTO {
    private String messageId;
    private Integer segmentNo;
    private Integer totalSegments;
    private String content;
    private Integer senderId;
    private Integer recipientId;
}

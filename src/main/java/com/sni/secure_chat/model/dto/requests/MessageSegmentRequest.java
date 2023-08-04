package com.sni.secure_chat.model.dto.requests;

import lombok.Data;

@Data
public class MessageSegmentRequest {
    private Integer senderId;
    private Integer recipientId;
    private Integer segmentNo;
    private Integer totalSegments;
    private String content;
}

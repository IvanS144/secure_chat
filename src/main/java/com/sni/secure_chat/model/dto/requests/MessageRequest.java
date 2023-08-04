package com.sni.secure_chat.model.dto.requests;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class MessageRequest {
    @NotBlank
    private String content;
    @NotNull
    @Min(1)
    private Integer senderId;
    @NotNull
    @Min(1)
    private Integer recipientId;
}

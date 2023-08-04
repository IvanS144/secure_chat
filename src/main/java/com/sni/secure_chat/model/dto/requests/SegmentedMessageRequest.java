package com.sni.secure_chat.model.dto.requests;

import com.sni.secure_chat.model.dto.MessageSegmentDTO;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;
@Data
public class SegmentedMessageRequest {
    @NotNull
    @Min(1)
    private Integer senderId;
    @NotNull
    @Min(1)
    private Integer recipientId;
    @NotEmpty
    private List<String> segments;
}

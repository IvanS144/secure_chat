package com.sni.secure_chat.controllers;

import com.sni.secure_chat.model.dto.requests.MessageRequest;
import com.sni.secure_chat.model.dto.requests.SegmentedMessageRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/messages")
public class MessageController {
    @PostMapping
    public void sendMessage(@RequestBody @Valid MessageRequest messageRequest){

    }

    @PostMapping("/segmented")
    public void sendSegmentedMessage(@RequestBody @Valid SegmentedMessageRequest segmentedMessageRequest){

    }
}

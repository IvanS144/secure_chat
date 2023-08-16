package com.sni.secure_chat.controllers;

import com.sni.secure_chat.model.dto.MessageDTO;
import com.sni.secure_chat.model.dto.requests.MessageRequest;
import com.sni.secure_chat.model.dto.requests.SegmentedMessageRequest;
import com.sni.secure_chat.services.MessageService;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/messages")
public class MessageController {
    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @GetMapping("/{id}")
    public List<MessageDTO> getMessagesByRecipientId(@PathVariable Integer id){
        return messageService.findMessagesByRecipientId(id);
    }

    @PostMapping
    public void sendMessage(@RequestBody @Valid MessageRequest messageRequest){
        messageService.sendMessage(messageRequest);
    }

    @PostMapping("/segmented")
    public void sendSegmentedMessage(@RequestBody @Valid SegmentedMessageRequest segmentedMessageRequest){

    }
}

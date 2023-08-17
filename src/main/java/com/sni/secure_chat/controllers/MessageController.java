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
@CrossOrigin(origins = "http://localhost:4200/", allowCredentials ="true" )
public class MessageController {
    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @GetMapping("/{id}")
    public List<MessageDTO> getMessagesByRecipientId(@PathVariable Integer id, @RequestParam(name="senderId", required = false) Integer senderId){
        if(senderId == null) {
            return messageService.findMessagesByRecipientId(id);
        }
        else {
            return messageService.findByRecipientIdAndSenderId(id, senderId);
        }
    }

    @PostMapping
    public void sendMessage(@RequestBody @Valid MessageRequest messageRequest){
        messageService.sendMessage(messageRequest);
    }

    @PostMapping("/segmented")
    public void sendSegmentedMessage(@RequestBody @Valid SegmentedMessageRequest segmentedMessageRequest){

    }
}

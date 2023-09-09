package com.sni.secure_chat.controllers;

import com.sni.secure_chat.exceptions.ForbiddenException;
import com.sni.secure_chat.model.dto.ChatUserDetails;
import com.sni.secure_chat.model.dto.MessageDTO;
import com.sni.secure_chat.model.dto.requests.MessageRequest;
import com.sni.secure_chat.model.dto.requests.SegmentedMessageRequest;
import com.sni.secure_chat.services.MessageService;
import io.swagger.annotations.Authorization;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/messages")
@CrossOrigin(origins = "http://localhost:4200/", allowCredentials ="true" )
public class MessageController {
    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @GetMapping("/{id}")
    public List<MessageDTO> getMessagesByRecipientId(@PathVariable Integer id, @RequestParam(name="senderId", required = false) Integer senderId, @AuthenticationPrincipal ChatUserDetails principal){
        if(!Objects.equals(principal.getUserId(), id))
            throw new ForbiddenException(null);
        if(senderId == null) {
            return messageService.findMessagesByRecipientId(id);
        }
        else {
            return messageService.findByRecipientIdAndSenderId(id, senderId);
        }
    }

    @GetMapping("/{recipientId}/sender/{senderId}/unread")
    public List<MessageDTO> getUnreadMessagesByRecipientIdAndSenderId(@PathVariable Integer recipientId, @PathVariable Integer senderId, @AuthenticationPrincipal ChatUserDetails principal){
        if(!Objects.equals(principal.getUserId(), recipientId))
            throw new ForbiddenException(null);
        return messageService.findUnreadByRecipientIdAndSenderId(recipientId, senderId);
    }

    @PostMapping
    public void sendMessage(@RequestBody @Valid MessageRequest messageRequest){
        messageService.sendMessage(messageRequest);
    }
}

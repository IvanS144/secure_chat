package com.sni.secure_chat.services.impl;

import com.sni.secure_chat.model.dto.MessageDTO;
import com.sni.secure_chat.model.dto.MessageSegmentDTO;
import com.sni.secure_chat.model.dto.requests.MessageRequest;
import com.sni.secure_chat.model.dto.requests.SegmentedMessageRequest;
import com.sni.secure_chat.services.MessageService;
import org.modelmapper.ModelMapper;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class MessageServiceImpl implements MessageService {
    private final RabbitTemplate rabbitTemplate;
    private final SecureRandom secureRandom;
    private final ModelMapper modelMapper;

    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("ddMMyyyyhhmmss");

    public MessageServiceImpl(RabbitTemplate rabbitTemplate, SecureRandom secureRandom, ModelMapper modelMapper) {
        this.rabbitTemplate = rabbitTemplate;
        this.secureRandom = secureRandom;
        this.modelMapper = modelMapper;
    }

    @Override
    public void sendMessage(MessageRequest messageRequest) {
        String id = dateTimeFormatter.format(LocalDateTime.now()) + secureRandom.nextInt(9000) + messageRequest.getSenderId();
        String content = messageRequest.getContent();
        int numberOfSegments = secureRandom.nextInt(content.length());
        int segmentLength = content.length()/numberOfSegments;
        String[] contentSegments = content.split("(?<=\\G.{" + segmentLength + "})");
        for(int i = 0; i<contentSegments.length; ++i){
            MessageSegmentDTO dto = new MessageSegmentDTO();
            dto.setMessageId(id);
            dto.setSenderId(messageRequest.getSenderId());
            dto.setRecipientId(messageRequest.getRecipientId());
            dto.setTotalSegments(contentSegments.length);
            dto.setSegmentNo(i);
            dto.setContent(contentSegments[i]);
            rabbitTemplate.convertAndSend("exchange", String.valueOf((i%4)+1), dto);

        }
    }

    @Override
    public void sendSegmentedMessage(SegmentedMessageRequest messageRequest) {
        String id = dateTimeFormatter.format(LocalDateTime.now()) + secureRandom.nextInt(9000) + messageRequest.getSenderId();
        for(int i = 0; i<messageRequest.getSegments().size(); ++i){
            int r = i%4;
            List<String> segments = messageRequest.getSegments();
            MessageSegmentDTO dto = new MessageSegmentDTO();
            dto.setMessageId(id);
            dto.setSenderId(messageRequest.getSenderId());
            dto.setRecipientId(messageRequest.getRecipientId());
            dto.setTotalSegments(segments.size());
            dto.setSegmentNo(i);
            dto.setContent(segments.get(i));
            rabbitTemplate.convertAndSend("exchange", String.valueOf(r+1), dto);
        }

    }
}

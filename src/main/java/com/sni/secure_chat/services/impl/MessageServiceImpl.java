package com.sni.secure_chat.services.impl;

import com.sni.secure_chat.exceptions.BadRequestException;
import com.sni.secure_chat.exceptions.NotFoundException;
import com.sni.secure_chat.model.dto.MessageDTO;
import com.sni.secure_chat.model.dto.MessageSegmentDTO;
import com.sni.secure_chat.model.dto.requests.MessageRequest;
import com.sni.secure_chat.model.dto.requests.SegmentedMessageRequest;
import com.sni.secure_chat.model.entities.Segment;
import com.sni.secure_chat.repositories.SegmentRepository;
import com.sni.secure_chat.repositories.UserRepository;
import com.sni.secure_chat.services.MessageService;
import com.sni.secure_chat.util.CryptoUtil;
import org.modelmapper.ModelMapper;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static com.fasterxml.jackson.databind.type.LogicalType.Map;

@Service
public class MessageServiceImpl implements MessageService {
    private final UserRepository userRepository;
    private final SegmentRepository segmentRepository;
    private final RabbitTemplate rabbitTemplate;
    private final SecureRandom secureRandom;
    private final ModelMapper modelMapper;

    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("ddMMyyyyhhmmss");

    public MessageServiceImpl(UserRepository userRepository, SegmentRepository segmentRepository, RabbitTemplate rabbitTemplate, SecureRandom secureRandom, ModelMapper modelMapper) {
        this.userRepository = userRepository;
        this.segmentRepository = segmentRepository;
        this.rabbitTemplate = rabbitTemplate;
        this.secureRandom = secureRandom;
        this.modelMapper = modelMapper;
    }

    @Override
    public void sendMessage(MessageRequest messageRequest) {
        if(Objects.equals(messageRequest.getSenderId(), messageRequest.getRecipientId()))
            throw new BadRequestException("Message can't be sent to yourself");
        try {
            if(!userRepository.existsById(messageRequest.getRecipientId()))
                throw new NotFoundException("Nonexistent recipient");
            String id = dateTimeFormatter.format(LocalDateTime.now()) + secureRandom.nextInt(9000) + messageRequest.getSenderId();
            String content = messageRequest.getContent();
            //key and iv
            SecretKey symmetricKey = CryptoUtil.generateKey(192);
            //System.out.println(symmetricKey.getEncoded().length);
            IvParameterSpec iv = CryptoUtil.generateIv();
            //System.out.println(iv.getIV().length);

            //envelope, base64 encoded
            String encryptedAesKey = CryptoUtil.encryptBytesWithRSAPublicKey(symmetricKey.getEncoded(), userRepository.getPublicKeyByUserId(messageRequest.getRecipientId()));

            //encrypted content, base64 encoded
            String encryptedContent = CryptoUtil.symmetricEncrypt("AES/CBC/PKCS5Padding", content, symmetricKey, iv);

            //content and envelope
            String messageToSend = CryptoUtil.base64Encode(iv.getIV())+"#"+encryptedAesKey+"#"+encryptedContent;


            //sending
            //System.out.println(messageToSend.length());
            int numberOfSegments = secureRandom.nextInt(4, messageToSend.length());
            //System.out.println("Broj segmenata: "+numberOfSegments);
            int segmentLength = messageToSend.length() / numberOfSegments;
            //System.out.println(segmentLength);
            String[] contentSegments = CryptoUtil.splitStringToChunks(messageToSend, segmentLength).toArray(new String[0]);
            for (int i = 0; i < contentSegments.length; ++i) {
                MessageSegmentDTO dto = new MessageSegmentDTO();
                dto.setMessageId(id);
                dto.setSenderId(messageRequest.getSenderId());
                dto.setRecipientId(messageRequest.getRecipientId());
                dto.setTotalSegments(contentSegments.length);
                dto.setSegmentNo(i);
                dto.setContent(contentSegments[i]);
                rabbitTemplate.convertAndSend("exchange", String.valueOf((i % 4) + 1), dto);

            }
        }
        catch(Exception e){
            throw new RuntimeException(e);
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

    @Override
    public List<MessageDTO> findMessagesByRecipientId(int recipientId) {
        try {
            if (!userRepository.existsById(recipientId))
                throw new NotFoundException("User not found");
            List<MessageDTO> listOfMessages = new ArrayList<>();
            Map<String, List<Segment>> segmentsMap = segmentRepository.findAllByRecipientId(recipientId).stream()
                    .collect(Collectors.groupingBy(Segment::getMessageId));
            for (List<Segment> segmentsList : segmentsMap.values()) {
                if (!segmentsList.isEmpty()) {
                    Segment oneSegment = segmentsList.get(0);
                    if (segmentsList.size() == oneSegment.getTotalSegments()) {
                        Collections.sort(segmentsList, Comparator.comparing(Segment::getSegmentNo));
                        StringBuilder sb = new StringBuilder();
                        for (int i = 0; i < segmentsList.size(); ++i) {
                            sb.append(segmentsList.get(i).getContent());
                        }
                        String segmentsCombined = sb.toString();
                        String[] parts = segmentsCombined.split("#");
                        String base64ivString = parts[0];
                        String base64SecretKeyString = parts[1];
                        String actualContent = parts[2];
                        byte[] secretKeyBytes = CryptoUtil.decryptWithRSAPrivateKey(CryptoUtil.base64DecodeToBytes(base64SecretKeyString), userRepository.getPrivateKeyByUserId(recipientId));
                        IvParameterSpec iv = new IvParameterSpec(CryptoUtil.base64DecodeToBytes(base64ivString));
                        SecretKey secretKey = new SecretKeySpec(secretKeyBytes, "AES");
                        String actualContentDecrypted = CryptoUtil.symmetricDecrypt("AES/CBC/PKCS5Padding", actualContent, secretKey, iv);
                        MessageDTO messageDTO = new MessageDTO();
                        messageDTO.setMessageId(oneSegment.getMessageId());
                        messageDTO.setRecipientId(oneSegment.getRecipientId());
                        messageDTO.setSenderId(oneSegment.getSenderId());
                        messageDTO.setContent(actualContentDecrypted);
                        listOfMessages.add(messageDTO);
                    }
                }
            }
            return listOfMessages;
        }
        catch(Exception e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<MessageDTO> findByRecipientIdAndSenderId(Integer recipientId, Integer senderId) {
        try {
            if (!userRepository.existsById(recipientId))
                throw new NotFoundException("User not found");
            List<MessageDTO> listOfMessages = new ArrayList<>();
            Map<String, List<Segment>> segmentsMap = segmentRepository.findAllByRecipientIdAndSenderId(recipientId, senderId).stream()
                    .collect(Collectors.groupingBy(Segment::getMessageId));
            for (List<Segment> segmentsList : segmentsMap.values()) {
                if (!segmentsList.isEmpty()) {
                    Segment oneSegment = segmentsList.get(0);
                    if (segmentsList.size() == oneSegment.getTotalSegments()) {
                        Collections.sort(segmentsList, Comparator.comparing(Segment::getSegmentNo));
                        StringBuilder sb = new StringBuilder();
                        for (int i = 0; i < segmentsList.size(); ++i) {
                            sb.append(segmentsList.get(i).getContent());
                        }
                        String segmentsCombined = sb.toString();
                        String[] parts = segmentsCombined.split("#");
                        String base64ivString = parts[0];
                        String base64SecretKeyString = parts[1];
                        String actualContent = parts[2];
                        byte[] secretKeyBytes = CryptoUtil.decryptWithRSAPrivateKey(CryptoUtil.base64DecodeToBytes(base64SecretKeyString), userRepository.getPrivateKeyByUserId(recipientId));
                        IvParameterSpec iv = new IvParameterSpec(CryptoUtil.base64DecodeToBytes(base64ivString));
                        SecretKey secretKey = new SecretKeySpec(secretKeyBytes, "AES");
                        String actualContentDecrypted = CryptoUtil.symmetricDecrypt("AES/CBC/PKCS5Padding", actualContent, secretKey, iv);
                        MessageDTO messageDTO = new MessageDTO();
                        messageDTO.setMessageId(oneSegment.getMessageId());
                        messageDTO.setRecipientId(oneSegment.getRecipientId());
                        messageDTO.setSenderId(oneSegment.getSenderId());
                        messageDTO.setContent(actualContentDecrypted);
                        listOfMessages.add(messageDTO);
                    }
                }
            }
            return listOfMessages;
        }
        catch(Exception e){
            throw new RuntimeException(e);
        }
    }
}

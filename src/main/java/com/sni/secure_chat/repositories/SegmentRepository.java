package com.sni.secure_chat.repositories;

import com.sni.secure_chat.model.entities.Segment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SegmentRepository extends JpaRepository<Segment, Integer> {
    List<Segment> findAllByRecipientId(Integer recipientId);
    List<Segment> findAllByRecipientIdAndSenderId(Integer recipientId, Integer senderId);
}

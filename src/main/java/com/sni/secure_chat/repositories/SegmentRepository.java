package com.sni.secure_chat.repositories;

import com.sni.secure_chat.model.entities.Segment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SegmentRepository extends JpaRepository<Segment, Integer> {
    List<Segment> findAllByRecipientId(Integer recipientId);
    List<Segment> findAllByRecipientIdAndSenderId(Integer recipientId, Integer senderId);
    List<Segment> findAllByRecipientIdAndSenderIdAndReadFalse(Integer recipientId, Integer senderId);
//    @Modifying
//    @Query("UPDATE Segment SET read = TRUE WHERE segmentId IN :list")
//    void setRead(List<Integer> list);
    @Modifying
    @Query("UPDATE Segment SET read = TRUE WHERE segmentId = :id")
    void setRead(Integer id);
}

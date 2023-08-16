package com.sni.secure_chat.model.entities;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name="segment")
public class Segment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="segment_id")
    private Integer segmentId;
    @Basic
    @Column(name="message_id")
    private String messageId;
    @Basic
    @Column(name="segment_no")
    private Integer segmentNo;
    @Basic
    @Column(name="total_segments")
    private Integer totalSegments;
    @Basic
    @Column(name="sender_id")
    private Integer senderId;
    @Basic
    @Column(name="recipient_id")
    private Integer recipientId;
    @Basic
    @Column(name="content")
    private String content;
}

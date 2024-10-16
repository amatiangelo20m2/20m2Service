//package com.ventimetriquadriconsulting.comminucation.opt.messagingcampain.entity;
//
//import com.ventimetriquadriconsulting.comminucation.opt.messagingcampain.entity.MessaginCampainType;
//import jakarta.persistence.*;
//import lombok.AllArgsConstructor;
//import lombok.Builder;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//
//import javax.sound.midi.Receiver;
//import java.util.Set;
//
//@Entity(name = "MessagingCampain")
//@Table(name = "MESSAGING_CAMPAIN", uniqueConstraints=@UniqueConstraint(columnNames={"branch_code"}))
//@NoArgsConstructor
//@Data
//@AllArgsConstructor
//@Builder
//public class MessagingCampain {
//
//    @Id
//    @SequenceGenerator(
//            name = "id",
//            sequenceName = "id",
//            allocationSize = 1
//    )
//    @GeneratedValue(
//            strategy = GenerationType.SEQUENCE,
//            generator = "id"
//    )
//    @Column(
//            name = "id",
//            updatable = false
//    )
//    private long id;
//
//    private String name;
//    private String nameFrom;
//
//    @Column(name = "payload", columnDefinition = "TEXT")
//    private String payload;
//
//    @ElementCollection
//    @CollectionTable(
//            name = "messaging_campain_type",
//            joinColumns = @JoinColumn(name = "messaging_campain_type_id")
//    )
//    @OrderColumn(name = "position")
//    private Set<MessaginCampainType> messaginCampainType;
//
////    @ElementCollection
////    @CollectionTable(
////            name = "receivers_type",
////            joinColumns = @JoinColumn(name = "receivers_type_id")
////    )
////    @OrderColumn(name = "position")
////    private Set<Receiver> receiverListIds;
//}

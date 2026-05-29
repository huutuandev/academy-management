package com.tuan.center.entity;

import com.tuan.center.Enum.FeeStatus;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "fee_records")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class FeeRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne
    @JoinColumn(name = "class_id", nullable = false)
    private Class clazz;

    private Double totalFee;
    private Double paidAmount = 0.0;
    private Double dueAmount;

    private LocalDate dueDate;

    @Enumerated(EnumType.STRING)
    private FeeStatus status = FeeStatus.UNPAID;

    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}


package com.tuan.center.entity;

import com.tuan.center.Enum.ClassStudentStatus;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "class_student")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ClassStudent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "class_id", nullable = false)
    private Class clazz;

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    private LocalDateTime joinedDate;

    @Enumerated(EnumType.STRING)
    private ClassStudentStatus status = ClassStudentStatus.ACTIVE;

    private String note;

    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (joinedDate == null) joinedDate = LocalDateTime.now();
    }
}


package com.tuan.center.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "teacher_profiles")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class TeacherProfile {

    @Id
    private Long id;                       // = user.id

    @OneToOne
    @MapsId
    @JoinColumn(name = "user_id")
    private User user;

    private String avatar;

    @Column(columnDefinition = "TEXT")
    private String bio;

    @Column(columnDefinition = "TEXT")
    private String qualifications;

    @Column(columnDefinition = "TEXT")
    private String experience;

    private String specialties;

    private Integer totalClasses = 0;
    private Double rating = 0.0;
    private Integer totalStudents = 0;

    private LocalDateTime updatedAt;

    @PrePersist
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}

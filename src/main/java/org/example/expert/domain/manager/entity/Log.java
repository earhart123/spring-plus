package org.example.expert.domain.manager.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor
@Table(name = "log")
@EntityListeners(AuditingEntityListener.class)
public class Log {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String action;
    private String message;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    public Log(String action, String message) {
        this.action = action;
        this.message = message;
    }
}

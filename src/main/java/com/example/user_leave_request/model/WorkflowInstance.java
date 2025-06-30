package com.example.user_leave_request.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class WorkflowInstance {
    @Id
    @GeneratedValue
    private Long id;
    @OneToOne
    private FormInstance formInstance;
    private String currentState;
    private LocalDateTime createdAt = LocalDateTime.now();
}


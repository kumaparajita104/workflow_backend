package com.example.user_leave_request.model;

import jakarta.persistence.*;
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
public class FormInstance {
    @Id
    @GeneratedValue
    private Long id;
    private String formType;
    private String userId;
    @Lob
    private String formDataJson;
    private LocalDateTime createdAt = LocalDateTime.now();
    @ManyToOne
    @JoinColumn(name = "workflow_template_id")
    private WorkflowTemplate template;
}

package com.example.user_leave_request.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Transition {
    @Id
    @GeneratedValue
    private Long id;

    private String fromState;
    private String toState;

    @ElementCollection
    private List<String> allowedRoles;

    @ManyToOne
    @JoinColumn(name = "workflow_template_id")
    private WorkflowTemplate workflowTemplate;
}


package com.example.user_leave_request.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class WorkflowInstanceDto {
    private Long id;
    private String currentState;
    private String employeeName;

    public WorkflowInstanceDto(Long id, String currentState, String employeeName) {
        this.id = id;
        this.currentState = currentState;
        this.employeeName = employeeName;
    }

    // Getters and setters (or use Lombok)
}


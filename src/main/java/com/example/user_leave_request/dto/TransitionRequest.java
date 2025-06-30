package com.example.user_leave_request.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TransitionRequest {
    private Long workflowInstanceId;
    private String nextState;

    // getters and setters
}


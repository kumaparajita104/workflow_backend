package com.example.user_leave_request.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LeaveRequestDto {
    private String employeeName;
    private String leaveType;
    private String startDate;
    private String endDate;
    private String reason;
    private String currentState;
}


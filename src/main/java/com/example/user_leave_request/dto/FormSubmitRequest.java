package com.example.user_leave_request.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class FormSubmitRequest {
    private String formType;
    private Map<String, Object> data;

}

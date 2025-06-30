package com.example.user_leave_request.service;

import com.example.user_leave_request.model.FormInstance;
import com.example.user_leave_request.repository.FormInstanceRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class FormService {
    @Autowired
    private FormInstanceRepository formRepo;

    public FormInstance saveForm(String formType, Map<String, Object> data, String userId) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(data);
            FormInstance form = new FormInstance();
            form.setFormType(formType);
            form.setUserId(userId);
            form.setFormDataJson(json);
            return formRepo.save(form);
        } catch (Exception e) {
            throw new RuntimeException("Invalid form data", e);
        }
    }
}

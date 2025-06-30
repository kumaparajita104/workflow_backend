package com.example.user_leave_request.controller;

import com.example.user_leave_request.dto.FormSubmitRequest;
import com.example.user_leave_request.model.FormInstance;
import com.example.user_leave_request.model.WorkflowTemplate;
import com.example.user_leave_request.repository.FormInstanceRepository;
import com.example.user_leave_request.repository.FormTemplateRepository;
import com.example.user_leave_request.repository.WorkflowTemplateRepository;
import com.example.user_leave_request.service.FormService;
import com.example.user_leave_request.service.WorkflowService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/forms")
public class FormController {
    @Autowired
    private FormService formService;
    @Autowired private WorkflowService workflowService;
    @Autowired
    private FormTemplateRepository formTemplateRepository;
    @Autowired
    private WorkflowTemplateRepository workflowTemplateRepository;
    @Autowired
    private FormInstanceRepository formInstanceRepository;
    @PostMapping("/submit")
    public ResponseEntity<String> submitForm(
            @RequestBody Map<String, Object> request,
            @AuthenticationPrincipal Jwt jwt
    ) {
        try {
            String formType = (String) request.get("formType");
            Object formData = request.get("data");
            String formDataJson = new ObjectMapper().writeValueAsString(formData);


            // ✅ Extract userId from JWT
            String userId = jwt.getSubject(); // Or another claim if appropriate

            // ✅ Fetch workflow template
            WorkflowTemplate template = workflowTemplateRepository
                    .findByFormType(formType)
                    .orElseThrow(() -> new RuntimeException("WorkflowTemplate not found for formType: " + formType));

            // ✅ Save form instance
            FormInstance instance = new FormInstance();
            instance.setFormType(formType);
            instance.setUserId(userId);
            instance.setFormDataJson(formDataJson);
            instance.setTemplate(template);

            formInstanceRepository.save(instance);
            workflowService.createWorkflowInstance(instance);

            return ResponseEntity.ok("Form submitted successfully");

        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/{formType}")
    public ResponseEntity<?> getFormTemplate(@PathVariable String formType) {
        return formTemplateRepository.findByFormType(formType)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}

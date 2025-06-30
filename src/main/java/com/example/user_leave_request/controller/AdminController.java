package com.example.user_leave_request.controller;

import com.example.user_leave_request.dto.TransitionRequest;
import com.example.user_leave_request.model.FormTemplate;
import com.example.user_leave_request.model.WorkflowTemplate;
import com.example.user_leave_request.repository.FormTemplateRepository;
import com.example.user_leave_request.repository.WorkflowTemplateRepository;
import com.example.user_leave_request.service.WorkflowService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    private FormTemplateRepository formRepo;
    @Autowired private WorkflowTemplateRepository workflowRepo;
    @Autowired private WorkflowService workflowService;
    private final ObjectMapper mapper = new ObjectMapper();



    @PostMapping("/forms")
    public ResponseEntity<?> createFormTemplate(@RequestBody Map<String, Object> payload) throws JsonProcessingException {
        FormTemplate form = new FormTemplate();
        form.setFormType((String) payload.get("formType"));
        form.setTitle((String) payload.get("title"));
        ObjectMapper mapper = new ObjectMapper();
        form.setSchemaJson(mapper.writeValueAsString(payload.get("fields")));
        formRepo.save(form);
        return ResponseEntity.ok("Form template saved");
    }

    @PostMapping("/workflows")
    public ResponseEntity<?> createWorkflow(@RequestBody Map<String, Object> payload) throws JsonProcessingException {
        WorkflowTemplate workflow = new WorkflowTemplate();
        workflow.setFormType((String) payload.get("formType"));
        ObjectMapper mapper = new ObjectMapper();
        workflow.setStatesJson(mapper.writeValueAsString(payload.get("states")));
        workflow.setTransitionsJson(mapper.writeValueAsString(payload.get("transitions")));
        workflowRepo.save(workflow);
        return ResponseEntity.ok("Workflow template saved");


    }
    @PostMapping("/form-generate")
    public ResponseEntity<?> generateForm(@RequestBody Map<String, String> payload) throws JsonProcessingException {
        String formType = payload.get("formType");
        FormTemplate form = formRepo.findByFormType(formType)
                .orElseThrow(() -> new RuntimeException("FormTemplate not found for type: " + formType));

        List<Map<String, Object>> fields = mapper.readValue(form.getSchemaJson(), List.class);

        return ResponseEntity.ok(Map.of(
                "formType", form.getFormType(),
                "title", form.getTitle(),
                "fields", fields
        ));
    }






}

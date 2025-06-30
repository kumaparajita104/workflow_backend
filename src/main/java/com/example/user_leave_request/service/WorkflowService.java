package com.example.user_leave_request.service;

import com.example.user_leave_request.dto.LeaveRequestDto;
import com.example.user_leave_request.model.FormInstance;
import com.example.user_leave_request.model.WorkflowInstance;
import com.example.user_leave_request.model.WorkflowTemplate;
import com.example.user_leave_request.model.Transition;
import com.example.user_leave_request.repository.TransitionRepository;
import com.example.user_leave_request.repository.WorkflowInstanceRepository;
import com.example.user_leave_request.repository.WorkflowTemplateRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class WorkflowService {

    @Autowired
    private WorkflowTemplateRepository templateRepo;

    @Autowired
    private WorkflowInstanceRepository instanceRepo;

    @Autowired
    private TransitionRepository transitionRepo;

    private final ObjectMapper objectMapper = new ObjectMapper();


    // ✅ Used when user submits a form
    public void createWorkflowInstance(FormInstance form) {
        WorkflowTemplate template = templateRepo.findByFormType(form.getFormType())
                .orElseThrow(() -> new RuntimeException("Workflow not found"));
        String initialState = template.getParsedStates().get(0);
        WorkflowInstance instance = new WorkflowInstance();
        instance.setFormInstance(form);
        instance.setCurrentState(initialState);
        instanceRepo.save(instance);
    }

    // ✅ Used by ADMIN/MANAGER/HR to move workflow forward
    public String transitionState(Long workflowInstanceId, String nextState, List<String> userRoles) {

        // Fetch workflow instance from DB
        WorkflowInstance instance = instanceRepo.findById(workflowInstanceId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid workflow instance ID"));

        // Fetch workflow template for this instance (e.g. by formType)
        WorkflowTemplate template = templateRepo.findByFormType(instance.getFormInstance().getFormType())
                .orElseThrow(() -> new IllegalStateException("Workflow template not found for form type: "
                        + instance.getFormInstance().getFormType()));

        if (template == null) {
            throw new IllegalStateException("Workflow template not found for form type: " + instance.getFormInstance().getFormType());
        }

        // Deserialize transitions JSON into List<Transition>
        List<Transition> transitions;
        try {
            transitions = objectMapper.readValue(template.getTransitionsJson(),
                    new TypeReference<List<Transition>>() {});
        } catch (Exception e) {
            throw new IllegalStateException("Failed to parse transitions JSON", e);
        }

        String currentState = instance.getCurrentState();

        // Find the transition from current state to requested nextState
        for (Transition t : transitions) {
            if (t.getFromState().equals(currentState) && t.getToState().equals(nextState)) {

                // Check if any user role matches allowedRoles
                boolean authorized = userRoles.stream()
                        .anyMatch(role -> t.getAllowedRoles().contains(role));

                if (!authorized) {
                    throw new AccessDeniedException("You are not authorized to perform this transition");
                }

                // Authorized: update state and save
                instance.setCurrentState(nextState);
                instanceRepo.save(instance);

                return "Transitioned from " + currentState + " to " + nextState;
            }
        }

        throw new IllegalStateException("Invalid state transition from " + currentState + " to " + nextState);
    }
    public LeaveRequestDto getLeaveRequestDetails(Long workflowInstanceId) {
        WorkflowInstance instance = instanceRepo.findById(workflowInstanceId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid workflow instance ID"));

        FormInstance formInstance = instance.getFormInstance();

        if (formInstance == null) {
            throw new IllegalStateException("Form instance not found for workflow");
        }

        try {
            Map<String, Object> formData = objectMapper.readValue(
                    formInstance.getFormDataJson(),
                    new TypeReference<Map<String, Object>>() {}
            );

            LeaveRequestDto dto = new LeaveRequestDto();
            dto.setEmployeeName((String) formData.get("employeeName")); // Optional: only if you actually have this field
            dto.setLeaveType((String) formData.get("leaveType"));       // Optional
            dto.setStartDate((String) formData.get("fromDate"));        // FIXED KEY
            dto.setEndDate((String) formData.get("toDate"));            // FIXED KEY
            dto.setReason((String) formData.get("reason"));
            dto.setCurrentState(instance.getCurrentState());

            return dto;

        } catch (Exception e) {
            throw new IllegalStateException("Failed to parse form data JSON", e);
        }
    }

    public boolean approveWorkflowTransition(Long workflowInstanceId, String nextState, List<String> userRoles) {

        WorkflowInstance instance = instanceRepo.findById(workflowInstanceId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid workflow instance ID"));

        WorkflowTemplate template = templateRepo.findByFormType(instance.getFormInstance().getFormType())
                .orElseThrow(() -> new IllegalStateException("Workflow template not found"));

        List<Transition> transitions;
        try {
            transitions = objectMapper.readValue(template.getTransitionsJson(),
                    new TypeReference<List<Transition>>() {});
        } catch (Exception e) {
            throw new IllegalStateException("Failed to parse transitions JSON", e);
        }

        String currentState = instance.getCurrentState();

        for (Transition t : transitions) {
            if (t.getFromState().equals(currentState) && t.getToState().equals(nextState)) {

                boolean authorized = userRoles.stream()
                        .anyMatch(role -> t.getAllowedRoles().contains(role));

                if (!authorized) {
                    throw new AccessDeniedException("You are not authorized to perform this transition");
                }

                // Authorized: update state and save
                instance.setCurrentState(nextState);
                instanceRepo.save(instance);

                return true;
            }
        }
        return false;
    }
    public List<WorkflowInstance> findInstancesByCurrentState(String currentState) {
        return instanceRepo.findByCurrentState(currentState);
    }

}

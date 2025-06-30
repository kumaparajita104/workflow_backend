package com.example.user_leave_request.controller;

import com.example.user_leave_request.dto.LeaveRequestDto;
import com.example.user_leave_request.dto.TransitionRequest;
import com.example.user_leave_request.model.WorkflowInstance;
import com.example.user_leave_request.model.WorkflowTemplate;
import com.example.user_leave_request.repository.WorkflowTemplateRepository;
import com.example.user_leave_request.service.WorkflowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;
@CrossOrigin(origins="*")
@RestController
@RequestMapping("/workflow")
public class WorkFlowController {

    @Autowired
    private WorkflowService workflowService;

    @Autowired
    private WorkflowTemplateRepository workflowRepo;

    @PostMapping("/transition")
    public ResponseEntity<?> transitionWorkflow(
            @RequestBody TransitionRequest request,
            @AuthenticationPrincipal Jwt jwt
    ) {
        // Safely extract roles from "realm_access"
        List<String> roles = extractRolesFromJwt(jwt);

        if (roles.isEmpty()) {
            throw new AccessDeniedException("No roles found in token. Access denied.");
        }

        String message = workflowService.transitionState(
                request.getWorkflowInstanceId(),
                request.getNextState(),
                roles
        );

        return ResponseEntity.ok(message);
    }

    private List<String> extractRolesFromJwt(Jwt jwt) {
        List<String> roles = new ArrayList<>();

        // Extract realm roles
        Map<String, Object> realmAccess = jwt.getClaim("realm_access");
        if (realmAccess != null && realmAccess.get("roles") instanceof List<?>) {
            roles.addAll(((List<?>) realmAccess.get("roles")).stream()
                    .filter(role -> role instanceof String)
                    .map(Object::toString)
                    .collect(Collectors.toList()));
        }

        // Extract client roles if needed (replace 'user_leave_api' with your client ID)
        Map<String, Object> resourceAccess = jwt.getClaim("resource_access");
        if (resourceAccess != null && resourceAccess.containsKey("user_leave_api")) {
            Map<String, Object> clientRoles = (Map<String, Object>) resourceAccess.get("user_leave_api");
            if (clientRoles.get("roles") instanceof List<?>) {
                roles.addAll(((List<?>) clientRoles.get("roles")).stream()
                        .filter(role -> role instanceof String)
                        .map(Object::toString)
                        .collect(Collectors.toList()));
            }
        }

        return roles;
    }
    @GetMapping("/transition/{formType}")
    public ResponseEntity<?> getTransitions(@PathVariable String formType) {
        WorkflowTemplate workflow = workflowRepo.findByFormType(formType)
                .orElseThrow(() -> new RuntimeException("Workflow not found for formType: " + formType));
        System.out.println(formType);
        System.out.println(workflow.getTransitionsJson());

        return ResponseEntity.ok(workflow.getTransitionsJson());
    }
    @GetMapping("/details/{workflowId}")
    public ResponseEntity<LeaveRequestDto> getLeaveDetails(@PathVariable Long workflowId) {
        LeaveRequestDto leaveRequest = workflowService.getLeaveRequestDetails(workflowId);
        if (leaveRequest == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(leaveRequest);
    }
    @PostMapping("/transition/MANAGER_APPROVED")
    public ResponseEntity<String> approveAsManager(
            @RequestBody Map<String, Object> payload,
            @AuthenticationPrincipal Jwt jwt
    ) {
        Long workflowId = Long.valueOf(payload.get("workflowId").toString());
        List<String> roles = extractRolesFromJwt(jwt);

        boolean success = workflowService.approveWorkflowTransition(workflowId, "MANAGER_APPROVED", roles);
        if (success) {
            return ResponseEntity.ok("Manager approval successful");
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Manager approval failed");
    }

    @PostMapping("/transition/HR_APPROVED")
    public ResponseEntity<String> approveAsHr(
            @RequestBody Map<String, Object> payload,
            @AuthenticationPrincipal Jwt jwt
    ) {
        Long workflowId = Long.valueOf(payload.get("workflowId").toString());
        List<String> roles = extractRolesFromJwt(jwt);

        boolean success = workflowService.approveWorkflowTransition(workflowId, "HR_APPROVED", roles);
        if (success) {
            return ResponseEntity.ok("HR approval successful");
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("HR approval failed");
    }
    @GetMapping("/pending/manager")
    public ResponseEntity<List<WorkflowInstance>> getManagerPendingWorkflows() {
        List<WorkflowInstance> managerPending = workflowService.findInstancesByCurrentState("PENDING");
        return ResponseEntity.ok(managerPending);
    }

    @GetMapping("/pending/hr")
    public ResponseEntity<List<WorkflowInstance>> getHrPendingWorkflows() {
        List<WorkflowInstance> hrPending = workflowService.findInstancesByCurrentState("MANAGER_APPROVED");
        return ResponseEntity.ok(hrPending);
    }
    @PostMapping("/transition/REJECTED")
    public ResponseEntity<String> rejectWorkflow(
            @RequestBody Map<String, Object> request,
            @AuthenticationPrincipal Jwt principal) {

        Long workflowId = Long.valueOf(request.get("workflowId").toString());

        // ✅ Extract realm roles
        List<String> realmRoles = Optional.ofNullable(principal.getClaim("realm_access"))
                .map(claim -> (Map<String, Object>) claim)
                .map(map -> (List<String>) map.get("roles"))
                .orElse(List.of());

        // ✅ Extract client roles (e.g., from your client_id 'user_leave_api')
        List<String> clientRoles = Optional.ofNullable(principal.getClaim("resource_access"))
                .map(claim -> (Map<String, Object>) claim)
                .map(map -> (Map<String, Object>) map.get("user_leave_api"))
                .map(client -> (List<String>) client.get("roles"))
                .orElse(List.of());
        System.out.println(principal.getId());

        // ✅ Combine both
        List<String> roles = new ArrayList<>();
        roles.addAll(realmRoles);
        roles.addAll(clientRoles);

        // ✅ Call transition logic
        workflowService.transitionState(workflowId, "REJECTED", roles);

        return ResponseEntity.ok("Application rejected successfully");
    }





}

package com.example.user_leave_request.repository;

import com.example.user_leave_request.model.WorkflowTemplate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WorkflowTemplateRepository extends JpaRepository<WorkflowTemplate, Long> {

    Optional<WorkflowTemplate> findByFormType(String formType);
}

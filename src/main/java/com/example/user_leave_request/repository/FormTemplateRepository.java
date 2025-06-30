package com.example.user_leave_request.repository;

import com.example.user_leave_request.model.FormTemplate;
import com.example.user_leave_request.model.WorkflowTemplate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FormTemplateRepository extends JpaRepository<FormTemplate, Long> {
    Optional<FormTemplate> findByFormType(String formType);
}

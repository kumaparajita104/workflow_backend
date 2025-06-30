package com.example.user_leave_request.repository;

import com.example.user_leave_request.model.Transition;
import com.example.user_leave_request.model.WorkflowTemplate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransitionRepository extends JpaRepository<Transition,Long> {
    List<Transition> findByWorkflowTemplateAndFromState(WorkflowTemplate template, String fromState);
}

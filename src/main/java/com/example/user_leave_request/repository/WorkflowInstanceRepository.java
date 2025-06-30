package com.example.user_leave_request.repository;

import com.example.user_leave_request.model.WorkflowInstance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WorkflowInstanceRepository extends JpaRepository<WorkflowInstance, Long> {
    List<WorkflowInstance> findByCurrentState(String currentState);
}

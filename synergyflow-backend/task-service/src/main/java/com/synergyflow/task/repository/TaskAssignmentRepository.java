package com.synergyflow.task.repository;

import com.synergyflow.task.entity.TaskAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskAssignmentRepository extends JpaRepository<TaskAssignment, String> {
    
    List<TaskAssignment> findByTask_TaskId(String taskId);
    
    void deleteByTask_TaskIdAndUserId(String taskId, String userId);
}

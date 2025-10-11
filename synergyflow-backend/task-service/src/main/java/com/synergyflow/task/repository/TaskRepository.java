package com.synergyflow.task.repository;

import com.synergyflow.task.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, String> {
    
    List<Task> findByColumn_ColumnIdOrderByOrderAsc(String columnId);
    
    @Query("SELECT t FROM Task t WHERE t.dueDate BETWEEN :start AND :end")
    List<Task> findTasksInDateRange(LocalDateTime start, LocalDateTime end);
    
    @Query("SELECT t FROM Task t JOIN t.assignments a WHERE a.userId = :userId")
    List<Task> findByAssignedUserId(String userId);
}

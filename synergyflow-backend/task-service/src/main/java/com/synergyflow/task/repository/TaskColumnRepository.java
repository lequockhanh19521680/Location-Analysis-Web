package com.synergyflow.task.repository;

import com.synergyflow.task.entity.TaskColumn;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskColumnRepository extends JpaRepository<TaskColumn, String> {
    
    List<TaskColumn> findByBoard_BoardIdOrderByOrderAsc(String boardId);
}

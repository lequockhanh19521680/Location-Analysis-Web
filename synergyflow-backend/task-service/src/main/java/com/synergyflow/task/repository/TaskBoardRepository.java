package com.synergyflow.task.repository;

import com.synergyflow.task.entity.TaskBoard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TaskBoardRepository extends JpaRepository<TaskBoard, String> {
    
    Optional<TaskBoard> findByChannelId(String channelId);
    
    boolean existsByChannelId(String channelId);
}

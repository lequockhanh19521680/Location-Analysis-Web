package com.synergyflow.workspace.repository;

import com.synergyflow.workspace.entity.Workspace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WorkspaceRepository extends JpaRepository<Workspace, String> {
    
    List<Workspace> findByOwnerId(String ownerId);
    
    @Query("SELECT w FROM Workspace w JOIN w.members m WHERE m.userId = :userId")
    List<Workspace> findByMemberUserId(String userId);
}

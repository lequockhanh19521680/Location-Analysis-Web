package com.synergyflow.workspace.repository;

import com.synergyflow.workspace.entity.WorkspaceMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WorkspaceMemberRepository extends JpaRepository<WorkspaceMember, String> {
    
    List<WorkspaceMember> findByWorkspace_WorkspaceId(String workspaceId);
    
    Optional<WorkspaceMember> findByWorkspace_WorkspaceIdAndUserId(String workspaceId, String userId);
    
    boolean existsByWorkspace_WorkspaceIdAndUserId(String workspaceId, String userId);
}

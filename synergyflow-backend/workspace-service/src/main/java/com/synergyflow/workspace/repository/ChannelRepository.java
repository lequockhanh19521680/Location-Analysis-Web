package com.synergyflow.workspace.repository;

import com.synergyflow.workspace.entity.Channel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChannelRepository extends JpaRepository<Channel, String> {
    
    List<Channel> findByWorkspace_WorkspaceId(String workspaceId);
}

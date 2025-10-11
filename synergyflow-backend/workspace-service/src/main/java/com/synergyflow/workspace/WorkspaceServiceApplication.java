package com.synergyflow.workspace;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication(scanBasePackages = {"com.synergyflow.workspace", "com.synergyflow.common"})
@EnableDiscoveryClient
public class WorkspaceServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(WorkspaceServiceApplication.class, args);
    }
}

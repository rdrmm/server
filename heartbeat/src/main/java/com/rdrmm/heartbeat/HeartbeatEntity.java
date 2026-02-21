package com.rdrmm.heartbeat;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "heartbeats")
public class HeartbeatEntity {

    @Id
    private String agentUuid;

    private String hostname;

    @Column(columnDefinition = "SMALLINT")
    private Integer cpu;

    @Column(columnDefinition = "SMALLINT")
    private Integer mem;

    @Column(length = 255)
    private String diskJson;

    private LocalDateTime timestamp;

    public String getAgentUuid() {
        return agentUuid;
    }

    public void setAgentUuid(String agentUuid) {
        this.agentUuid = agentUuid;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public Integer getCpu() {
        return cpu;
    }

    public void setCpu(Integer cpu) {
        this.cpu = cpu;
    }

    public Integer getMem() {
        return mem;
    }

    public void setMem(Integer mem) {
        this.mem = mem;
    }

    public String getDiskJson() {
        return diskJson;
    }

    public void setDiskJson(String diskJson) {
        this.diskJson = diskJson;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}

package com.rdrmm.hbw;

import jakarta.persistence.*;
import org.hibernate.annotations.Type;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import java.time.LocalDateTime;
import java.util.Map;

@Entity
@Table(name = "heartbeats")
public class Heartbeat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String agentUuid;

    private String hostname;

    private String cpu;

    private String mem;

    @Type(JsonBinaryType.class)
    @Column(columnDefinition = "jsonb")
    private String diskJson;

    private LocalDateTime timestamp;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public String getCpu() {
        return cpu;
    }

    public void setCpu(String cpu) {
        this.cpu = cpu;
    }

    public String getMem() {
        return mem;
    }

    public void setMem(String mem) {
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
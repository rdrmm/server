package com.rdrmm.hbs;

import java.util.Map;

public class HeartbeatPayload {

    private String agentUuid;

    private String hostname;

    private String cpu;

    private String mem;

    private Map<String, String> disk;

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

    public Map<String, String> getDisk() {
        return disk;
    }

    public void setDisk(Map<String, String> disk) {
        this.disk = disk;
    }
}
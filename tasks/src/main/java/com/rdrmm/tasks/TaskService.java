package com.rdrmm.tasks;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class TaskService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    private static final String AGENT_TASK_PREFIX = "agent:task:";
    private static final String TASK_AGENTS_PREFIX = "task:agents:";
    private static final String TASK_URLS_SET = "taskurls:all";

    /**
     * Submit a task assignment: agentUuid -> taskUrl
     */
    public void submitTask(String agentUuid, String taskUrl) {
        if (redisTemplate.hasKey(AGENT_TASK_PREFIX + agentUuid)) {
            String oldTaskUrl = redisTemplate.opsForValue().get(AGENT_TASK_PREFIX + agentUuid);
            // Remove agent from old task's agent list
            if (oldTaskUrl != null) {
                redisTemplate.opsForSet().remove(TASK_AGENTS_PREFIX + oldTaskUrl, agentUuid);
            }
        }
        
        // Store agent -> task mapping
        redisTemplate.opsForValue().set(AGENT_TASK_PREFIX + agentUuid, taskUrl);
        
        // Add to reverse mapping: task -> agents
        redisTemplate.opsForSet().add(TASK_AGENTS_PREFIX + taskUrl, agentUuid);
        
        // Add to global task URLs set
        redisTemplate.opsForSet().add(TASK_URLS_SET, taskUrl);
    }

    /**
     * Get taskUrl for a given agentUuid
     */
    public String getTaskForAgent(String agentUuid) {
        return redisTemplate.opsForValue().get(AGENT_TASK_PREFIX + agentUuid);
    }

    /**
     * Get all agents associated with a given taskUrl
     */
    public Set<String> getAgentsForTask(String taskUrl) {
        Set<String> agents = redisTemplate.opsForSet().members(TASK_AGENTS_PREFIX + taskUrl);
        return agents != null ? agents : new HashSet<>();
    }

    /**
     * List all task URLs in existence
     */
    public Set<String> getAllTaskUrls() {
        Set<String> taskUrls = redisTemplate.opsForSet().members(TASK_URLS_SET);
        return taskUrls != null ? taskUrls : new HashSet<>();
    }

    /**
     * Delete a task assignment by agentUuid
     */
    public boolean deleteTask(String agentUuid) {
        String taskUrl = redisTemplate.opsForValue().get(AGENT_TASK_PREFIX + agentUuid);
        
        if (taskUrl == null) {
            return false;
        }
        
        // Remove agent entry
        redisTemplate.delete(AGENT_TASK_PREFIX + agentUuid);
        
        // Remove agent from task's agent list
        redisTemplate.opsForSet().remove(TASK_AGENTS_PREFIX + taskUrl, agentUuid);
        
        // If no more agents for this task, remove the task from global set
        Set<String> remainingAgents = redisTemplate.opsForSet().members(TASK_AGENTS_PREFIX + taskUrl);
        if (remainingAgents == null || remainingAgents.isEmpty()) {
            redisTemplate.opsForSet().remove(TASK_URLS_SET, taskUrl);
            redisTemplate.delete(TASK_AGENTS_PREFIX + taskUrl);
        }
        
        return true;
    }
}

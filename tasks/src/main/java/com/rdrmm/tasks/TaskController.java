package com.rdrmm.tasks;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    @Autowired
    private TaskService taskService;

    /**
     * Submit a task assignment
     * POST /api/tasks
     */
    @PostMapping
    public ResponseEntity<Map<String, String>> submitTask(@RequestBody TaskSubmission submission) {
        if (submission.getAgentUuid() == null || submission.getAgentUuid().isEmpty() ||
            submission.getTaskUrl() == null || submission.getTaskUrl().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "agentUuid and taskUrl are required"));
        }

        try {
            taskService.submitTask(submission.getAgentUuid(), submission.getTaskUrl());
            return ResponseEntity.ok(Map.of("message", "Task submitted successfully", 
                                           "agentUuid", submission.getAgentUuid(), 
                                           "taskUrl", submission.getTaskUrl()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error submitting task: " + e.getMessage()));
        }
    }

    /**
     * Get taskUrl for a given agentUuid
     * GET /api/tasks/{agentUuid}
     */
    @GetMapping("/{agentUuid}")
    public ResponseEntity<?> getTaskForAgent(@PathVariable String agentUuid) {
        try {
            String taskUrl = taskService.getTaskForAgent(agentUuid);
            if (taskUrl == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "No task found for agentUuid: " + agentUuid));
            }
            return ResponseEntity.ok(Map.of("agentUuid", agentUuid, "taskUrl", taskUrl));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error retrieving task: " + e.getMessage()));
        }
    }

    /**
     * Get all agents associated with a task URL
     * GET /api/tasks/url-agents?taskUrl=...
     */
    @GetMapping("/url-agents")
    public ResponseEntity<?> getAgentsForTask(@RequestParam String taskUrl) {
        try {
            Set<String> agents = taskService.getAgentsForTask(taskUrl);
            return ResponseEntity.ok(Map.of("taskUrl", taskUrl, "agents", agents));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error retrieving agents: " + e.getMessage()));
        }
    }

    /**
     * List all task URLs
     * GET /api/tasks/urls/all
     */
    @GetMapping("/urls/all")
    public ResponseEntity<?> getAllTaskUrls() {
        try {
            Set<String> taskUrls = taskService.getAllTaskUrls();
            return ResponseEntity.ok(Map.of("taskUrls", taskUrls, "count", taskUrls.size()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error retrieving task URLs: " + e.getMessage()));
        }
    }

    /**
     * Delete a task assignment
     * DELETE /api/tasks/{agentUuid}
     */
    @DeleteMapping("/{agentUuid}")
    public ResponseEntity<Map<String, String>> deleteTask(@PathVariable String agentUuid) {
        try {
            boolean deleted = taskService.deleteTask(agentUuid);
            if (!deleted) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "No task found for agentUuid: " + agentUuid));
            }
            return ResponseEntity.ok(Map.of("message", "Task deleted successfully", "agentUuid", agentUuid));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error deleting task: " + e.getMessage()));
        }
    }
}

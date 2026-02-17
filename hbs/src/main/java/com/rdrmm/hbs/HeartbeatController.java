package com.rdrmm.hbs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
@RequestMapping("/api")
public class HeartbeatController {

    @Autowired
    private StringRedisTemplate redisTemplate;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @PostMapping("/heartbeat")
    public ResponseEntity<String> receiveHeartbeat(@RequestHeader("Authorization") String auth, @RequestBody HeartbeatPayload payload) {
        // Simple token check
        if (auth == null || !auth.startsWith("Bearer ") || !"valid-token".equals(auth.substring(7))) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
        }

        try {
            String json = objectMapper.writeValueAsString(payload);
            redisTemplate.opsForValue().set("heartbeat:" + payload.getAgentUuid(), json);
            System.out.println("Heartbeat received for " + payload.getAgentUuid());
            return ResponseEntity.ok("Heartbeat received");
        } catch (Exception e) {
            System.err.println("Error processing heartbeat: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing heartbeat");
        }
    }
}
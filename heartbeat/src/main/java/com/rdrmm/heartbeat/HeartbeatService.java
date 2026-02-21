package com.rdrmm.heartbeat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.util.Set;

@Service
public class HeartbeatService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private HeartbeatRepository heartbeatRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Scheduled(fixedRate = 60000)
    public void writeHeartbeats() {
        Set<String> keys = redisTemplate.keys("heartbeat:*");
        if (keys == null) {
            return;
        }
        for (String key : keys) {
            String value = redisTemplate.opsForValue().get(key);
            try {
                HeartbeatPayload payload = objectMapper.readValue(value, HeartbeatPayload.class);
                HeartbeatEntity heartbeat = new HeartbeatEntity();
                heartbeat.setAgentUuid(payload.getAgentUuid());
                heartbeat.setHostname(payload.getHostname());
                heartbeat.setCpu(Integer.parseInt(payload.getCpu().replace("%", "")));
                heartbeat.setMem(Integer.parseInt(payload.getMem().replace("%", "")));
                heartbeat.setDiskJson(objectMapper.writeValueAsString(payload.getDisk()));
                heartbeat.setTimestamp(LocalDateTime.now());
                heartbeatRepository.save(heartbeat);
                redisTemplate.delete(key);
            } catch (Exception e) {
                // Log error
                System.err.println("Error processing heartbeat: " + e.getMessage());
            }
        }
    }
}

package com.rdrmm.heartbeat;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HeartbeatRepository extends JpaRepository<HeartbeatEntity, String> {
}

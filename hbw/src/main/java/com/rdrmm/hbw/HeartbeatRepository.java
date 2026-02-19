package com.rdrmm.hbw;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface HeartbeatRepository extends JpaRepository<Heartbeat, String> {
}
package com.lowquality.serverwebm.repository;

import com.lowquality.serverwebm.models.entity.Status;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StatusRepository extends JpaRepository<Status, Integer> {
}

package com.hrsystem.demo;

import com.hrsystem.demo.entity.Status;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StatusH2TestRepository extends JpaRepository<Status,Integer> {
}

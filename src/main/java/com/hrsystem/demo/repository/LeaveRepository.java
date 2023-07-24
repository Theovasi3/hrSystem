package com.hrsystem.demo.repository;

import com.hrsystem.demo.entity.LeaveRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LeaveRepository extends JpaRepository<LeaveRequest, Integer> {


}
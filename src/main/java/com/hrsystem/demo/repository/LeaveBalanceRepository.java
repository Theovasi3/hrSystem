package com.hrsystem.demo.repository;

import com.hrsystem.demo.entity.LeaveBalance;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LeaveBalanceRepository extends JpaRepository<LeaveBalance, Integer> {
}

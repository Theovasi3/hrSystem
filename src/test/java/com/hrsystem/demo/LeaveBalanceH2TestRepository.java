package com.hrsystem.demo;

import com.hrsystem.demo.entity.LeaveBalance;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LeaveBalanceH2TestRepository extends JpaRepository<LeaveBalance,Integer> {
}

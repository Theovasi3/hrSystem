package com.hrsystem.demo;

import com.hrsystem.demo.entity.LeaveRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LeaveH2TestRepository extends JpaRepository<LeaveRequest,Integer> {


}

package com.hrsystem.demo;

import com.hrsystem.demo.entity.LeaveCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LeaveCategoryH2TestRepository extends JpaRepository<LeaveCategory,Integer> {
}

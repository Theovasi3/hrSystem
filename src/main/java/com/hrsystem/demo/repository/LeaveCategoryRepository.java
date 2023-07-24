package com.hrsystem.demo.repository;

import com.hrsystem.demo.entity.LeaveCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LeaveCategoryRepository extends JpaRepository<LeaveCategory, Integer> {

}
package com.hrsystem.demo;

import com.hrsystem.demo.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeH2TestRepository extends JpaRepository<Employee,String> {
}

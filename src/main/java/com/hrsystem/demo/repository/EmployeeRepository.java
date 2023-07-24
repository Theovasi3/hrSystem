package com.hrsystem.demo.repository;

import com.hrsystem.demo.entity.Employee;

import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeRepository extends JpaRepository<Employee, String> {

}
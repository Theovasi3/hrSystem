package com.hrsystem.demo.service;

import com.hrsystem.demo.dto.EmployeeProfileDTO;
import com.hrsystem.demo.entity.Employee;
import com.hrsystem.demo.entity.LeaveBalance;

import java.util.List;

public interface EmployeeService {

    EmployeeProfileDTO findByEmployeeId(String  userName, String theId);

    EmployeeProfileDTO findEmployeeDetailsById(String  userName);

    EmployeeProfileDTO updateEmployeeDetails(EmployeeProfileDTO employeeProfileDTO,String username);

    void deleteById(String username);

    void addSupervisor( String username, String supervisor);

    List<LeaveBalance> leaveBalanceInitialize(Employee employee);
}

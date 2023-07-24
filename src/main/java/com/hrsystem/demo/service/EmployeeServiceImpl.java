package com.hrsystem.demo.service;

import com.hrsystem.demo.dto.EmployeeProfileDTO;
import com.hrsystem.demo.entity.LeaveBalance;
import com.hrsystem.demo.entity.LeaveCategory;
import com.hrsystem.demo.entity.User;
import com.hrsystem.demo.exception.NotFoundException;
import com.hrsystem.demo.exception.UnauthorizedException;
import com.hrsystem.demo.repository.EmployeeRepository;
import com.hrsystem.demo.entity.Employee;
import com.hrsystem.demo.repository.LeaveBalanceRepository;
import com.hrsystem.demo.repository.LeaveCategoryRepository;
import com.hrsystem.demo.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final UserRepository userRepository;

    private final LeaveBalanceRepository leaveBalanceRepository;

    private final LeaveCategoryRepository leaveCategoryRepository;

    @Autowired
    public EmployeeServiceImpl(EmployeeRepository theEmployeeRepository, UserRepository userRepository, LeaveBalanceRepository leaveBalanceRepository, LeaveCategoryRepository leaveCategoryRepository) {
        employeeRepository = theEmployeeRepository;
        this.userRepository = userRepository;
        this.leaveBalanceRepository = leaveBalanceRepository;
        this.leaveCategoryRepository = leaveCategoryRepository;
    }

    @Override
    public EmployeeProfileDTO findByEmployeeId(String searcherUsername, String searchedUsername) {
        Optional<Employee> employee = employeeRepository.findById(searchedUsername); //searched employee
        EmployeeProfileDTO employeeProfileDTO = new EmployeeProfileDTO(); //initializing employee dto

        if (employee.isPresent()) {
            if ( employee.get().isSupervisedBy(searcherUsername) || employee.get().hasUsername(searcherUsername)) {
                BeanUtils.copyProperties(employee.get(), employeeProfileDTO); //create dto
            } else {
                throw new UnauthorizedException("No permission to access this information: " + searchedUsername + " user profile.");
            }
        } else {
            // we didn't find the employee
            throw new NotFoundException("Could not find the employee " + searchedUsername);
        }
        return employeeProfileDTO;
    }

    @Override
    public EmployeeProfileDTO findEmployeeDetailsById(String userName) {
        Optional<Employee> employee = employeeRepository.findById(userName); //searched employee
        EmployeeProfileDTO employeeProfileDTO = new EmployeeProfileDTO(); //initializing employee dto

        if (employee.isPresent()) {
                BeanUtils.copyProperties(employee.get(), employeeProfileDTO); //create dto
        } else {
            // we didn't find the employee
            throw new NotFoundException("Could not find the employee " + userName);
        }
        return employeeProfileDTO;
    }

    @Override
    public EmployeeProfileDTO updateEmployeeDetails(EmployeeProfileDTO employeeProfileDTO,String username) {
        Optional<Employee> theEmployee = employeeRepository.findById(username);

        if (theEmployee.isPresent()) {
                BeanUtils.copyProperties(employeeProfileDTO,theEmployee.get());
                employeeRepository.save(theEmployee.get());
        } else {
            // we didn't find the employee
            throw new NotFoundException("Could not find the employee " + username);
        }
        return employeeProfileDTO;
    }

    @Override
    @Transactional
    public void deleteById(String username) {
        employeeRepository.deleteById(username);
    }

    @Override
    public void addSupervisor( String username, String supervisor) {
        Optional<Employee> employee = employeeRepository.findById(username);
        Optional<User> supervisor1 = userRepository.findById(supervisor);
        if (employee.isPresent() && supervisor1.isPresent() ) {
            employee.get().addSupervisor(supervisor1.get());
            supervisor1.get().getSupervisedEmployees().add(employee.get());
            employeeRepository.save(employee.get());
            userRepository.save(supervisor1.get());

        }

    }

    @Override
    public List<LeaveBalance> leaveBalanceInitialize(Employee employee){
        List<LeaveBalance> leaveBalances= new ArrayList<>();
        Optional<LeaveCategory> lc1 = leaveCategoryRepository.findById(1);
        if (lc1.isPresent()) {
            LeaveBalance leaveBalance1 = LeaveBalance.builder().employee(employee)
                    .leaveCategory(lc1.get())
                    .days(20)
                    .daysTaken(0)
                    .build();
            leaveBalances.add(leaveBalance1);
        }
        Optional<LeaveCategory> lc2 = leaveCategoryRepository.findById(2);
        if (lc1.isPresent()) {
            LeaveBalance leaveBalance2 = LeaveBalance.builder().employee(employee)
                    .leaveCategory(lc2.get())
                    .days(20)
                    .daysTaken(0)
                    .build();
            leaveBalances.add(leaveBalance2);
        }
        Optional<LeaveCategory> lc3 = leaveCategoryRepository.findById(3);
        if (lc1.isPresent()) {
            LeaveBalance leaveBalance3 = LeaveBalance.builder().employee(employee)
                    .leaveCategory(lc3.get())
                    .days(20)
                    .daysTaken(0)
                    .build();
            leaveBalances.add(leaveBalance3);
        }


        return leaveBalances;
    }

}

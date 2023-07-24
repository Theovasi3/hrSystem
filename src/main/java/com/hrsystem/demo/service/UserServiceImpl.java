package com.hrsystem.demo.service;

import com.hrsystem.demo.dto.UserDTO;
import com.hrsystem.demo.entity.Employee;
import com.hrsystem.demo.entity.Role;
import com.hrsystem.demo.entity.User;
import com.hrsystem.demo.exception.NotFoundException;
import com.hrsystem.demo.exception.UnauthorizedException;
import com.hrsystem.demo.repository.EmployeeRepository;
import com.hrsystem.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService{


    private UserRepository userRepository;
    private EmployeeRepository employeeRepository;

    @Autowired
    private EmployeeService employeeService;


    public UserServiceImpl(UserRepository userRepository, EmployeeRepository employeeRepository){
        this.userRepository = userRepository;
        this.employeeRepository=employeeRepository;
    }


    @Transactional
    @Override
    public UserDTO save(UserDTO userDTO) {
        User user = new User(userDTO);
        user.getRoles().add(new Role("ROLE_EMPLOYEE"));
        userRepository.save(user);
        Employee employee = new Employee(user.getUsername());
        employee.setLeaveBalances(employeeService.leaveBalanceInitialize(employee));
        employeeRepository.save(employee);
        return userDTO;
    }

    @Override
    public void delete(String username, String searcherUsername) {
        Optional<Employee> employee= employeeRepository.findById(username);
        User user = userRepository.getReferenceById(username);
        if (employee.isPresent()) {
                userRepository.delete(user);
        }
        else {
            // we didn't find the employee
            throw new NotFoundException("Could not find the employee " + username);
        }

    }
}

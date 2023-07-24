package com.hrsystem.demo.service;

import com.hrsystem.demo.dto.EmployeeProfileDTO;
import com.hrsystem.demo.entity.*;
import com.hrsystem.demo.exception.NotFoundException;
import com.hrsystem.demo.exception.UnauthorizedException;
import com.hrsystem.demo.repository.EmployeeRepository;
import com.hrsystem.demo.repository.LeaveCategoryRepository;
import com.hrsystem.demo.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceImplTest {
    @Mock
    EmployeeRepository employeeRepository;
    @Mock
    UserRepository userRepository;
    @Mock
    LeaveCategoryRepository leaveCategoryRepository;

    @InjectMocks
    EmployeeServiceImpl employeeService;

    @Autowired
    Employee employee;

    @BeforeEach
   public void init()
    {
        employee = Employee.builder()
            .username("Testo")
            .firstName("test")
            .lastName("testiou")
            .email("testopoulos@gmail.com")
            .mobileNumber("6985345634")
            .address("testisias")
            .addressNumber(23)
            .build();
    }
    @Test
    public void employeeServiceFindEmployeeByIdTest()
    {

        List<User> userList = new ArrayList<>();

        User user = User.builder()
                .username("asd")
                .password("123")
                .enabled(1)
                .build();

         userList.add(user);


        employee.setSupervisors(userList);
        when(employeeRepository.findById(employee.getUsername())).thenReturn(Optional.of(employee));
        EmployeeProfileDTO employeeToReturn = employeeService.findByEmployeeId(user.getUsername(),employee.getUsername());
        Assertions.assertNotNull(employeeToReturn);

        String nonexistentUsername = "nonexistentUsername";
        when(employeeRepository.findById(nonexistentUsername)).thenReturn(Optional.empty());
        Assertions.assertThrows(UnauthorizedException.class, ()-> employeeService.findByEmployeeId("nonSupervisor",employee.getUsername()));
        Assertions.assertThrows(NotFoundException.class, ()-> employeeService.findByEmployeeId(user.getUsername(),nonexistentUsername));
    }

    @Test
    public void employeeServiceDeleteById()
    {

        when(employeeRepository.findById(employee.getUsername())).thenReturn(Optional.of(employee));
       EmployeeProfileDTO employeeProfileDTO = employeeService.findEmployeeDetailsById(employee.getUsername());
       assertAll(()-> employeeService.deleteById(employee.getUsername()));

    }
    @Test
    public void employeeServiceFindEmployeeDetailsByIdTest ()
    {


        when(employeeRepository.findById(employee.getUsername())).thenReturn(Optional.of(employee));
        EmployeeProfileDTO employeeToReturn = employeeService.findEmployeeDetailsById(employee.getUsername());
        Assertions.assertNotNull(employeeToReturn);

    }

    @Test
    public void employeeServiceUpdateEmployeeDetailsTest()
    {

        EmployeeProfileDTO employeeProfileDTO = EmployeeProfileDTO.builder()
                .firstName("pablo")
                .lastName("amvrosiadis")
                .email("pabloamvrosiadis@ots.gr")
                .mobileNumber("6692345324")
                .address("monstiriou")
                .addressNumber(20).build();

        when(employeeRepository.findById("Testo")).thenReturn(Optional.of(employee));

        when(employeeRepository.save(Mockito.any(Employee.class))).thenReturn(employee);

        EmployeeProfileDTO savedemployee = employeeService.updateEmployeeDetails(employeeProfileDTO,employee.getUsername());

        assertEquals(savedemployee.getFirstName(),employee.getFirstName());
        assertEquals(savedemployee.getLastName(),employee.getLastName());
        assertEquals(savedemployee.getEmail(),employee.getEmail());
        assertEquals(savedemployee.getMobileNumber(),employee.getMobileNumber());
        assertEquals(savedemployee.getAddress(),employee.getAddress());
        assertEquals(savedemployee.getAddressNumber(),employee.getAddressNumber());
    }

    @Test
    public void addSupervisorTest ()
    {
        User supervisor = User.builder()
                .username("boss")
                .build();

        when(employeeRepository.findById(employee.getUsername())).thenReturn(Optional.of(employee));
        when(userRepository.findById(supervisor.getUsername())).thenReturn(Optional.of(supervisor));
        employeeService.addSupervisor(employee.getUsername(), supervisor.getUsername());
        Assertions.assertTrue(employee.getSupervisors().contains(supervisor));
    }

    @Test
    public void leaveBalanceInitializeTest(){
        LeaveCategory testLeaveCategory = LeaveCategory.builder().categoryName("Normal").build();
        when(leaveCategoryRepository.findById(1)).thenReturn(Optional.of(testLeaveCategory));
        when(leaveCategoryRepository.findById(2)).thenReturn(Optional.of(testLeaveCategory));
        when(leaveCategoryRepository.findById(3)).thenReturn(Optional.of(testLeaveCategory));
        Assertions.assertEquals(3, employeeService.leaveBalanceInitialize(employee).size());
    }

}
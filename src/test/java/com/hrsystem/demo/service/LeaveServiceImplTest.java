package com.hrsystem.demo.service;

import com.hrsystem.demo.dto.LeaveBalanceDTO;
import com.hrsystem.demo.dto.LeaveInfoDTO;
import com.hrsystem.demo.dto.LeaveRequestDTO;
import com.hrsystem.demo.dto.LeaveRequestUpdateDTO;
import com.hrsystem.demo.entity.*;
import com.hrsystem.demo.exception.NotFoundException;
import com.hrsystem.demo.exception.UnauthorizedException;
import com.hrsystem.demo.repository.*;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LeaveServiceImplTest {
    @Mock
    LeaveRepository leaveRepository;
    @Mock
    EmployeeRepository employeeRepository;
    @Mock
    UserRepository userRepository;
    @Mock
    StatusServiceImpl statusService;
    @InjectMocks
    LeaveServiceImpl leaveService;
    private Employee employee;
    private LeaveRequestDTO normalLeaveRequestDTO;

    @BeforeEach
    void init() throws ParseException {
        //Set up employee
        employee = Employee.builder()
                .username("testUser")
                .firstName("Testos")
                .lastName("Testiou")
                .email("test_paokara@ots.gr")
                .mobileNumber("123123123")
                .address("Testopoulou")
                .addressNumber(1)
                .build();

        //Set up DTO (2-day normal type leave request)
        normalLeaveRequestDTO = LeaveRequestDTO.builder()
                .leaveCategory(LeaveCategory.builder()
                        .id(1)
                        .categoryName("Normal")
                        .build())
                .startDate(new SimpleDateFormat("yyyy-MM-dd").parse("2023-05-23"))
                .endDate(new SimpleDateFormat("yyyy-MM-dd").parse("2023-05-26"))
                .build();
    }

    @Test
    void requestNewUnjustifiedLeaveTest() {
        /* The employee has no LeaveBalance's defined.
        /* We expect the result of his normal leave request to be rejected
        */

        //Mocking
        when(employeeRepository.findById(employee.getUsername())).thenReturn(Optional.of(employee));
        when(statusService.getPendingStateStatus()).thenReturn(new Status(1, "PENDING"));
        when(statusService.getRejectedStateStatus()).thenReturn(new Status(2, "REJECTED"));

        //Assertions
        LeaveInfoDTO resultingLeaveInfoDTO = leaveService.save(normalLeaveRequestDTO, "testUser");
        assertEquals(resultingLeaveInfoDTO.getStatus().getState(), "REJECTED");
    }

    @Test
    void requestNewJustifiedLeaveTest() {
        /* We expect the result of his normal leave request to be a successful pending request
        /* And his leave balance to be updated (+3 days on "taken" field)
         */

        //Adding 18 available normal leaves to our employee
        List<LeaveBalance> leaveBalances = new ArrayList<>();
        leaveBalances.add(LeaveBalance.builder().
                employee(employee)
                .leaveCategory(new LeaveCategory(1, "Normal"))
                .days(18).daysTaken(0)
                .build());
        employee.setLeaveBalances(leaveBalances);

        //Mocking
        when(employeeRepository.findById(employee.getUsername())).thenReturn(Optional.of(employee));
        when(statusService.getPendingStateStatus()).thenReturn(new Status(1, "PENDING"));

        //Assertions
        LeaveInfoDTO resultingLeaveInfoDTO = leaveService.save(normalLeaveRequestDTO, "testUser");
        assertEquals(resultingLeaveInfoDTO.getStatus().getState(), "PENDING");
        assertEquals(employee.getLeaveBalances().get(0).getDaysTaken(), resultingLeaveInfoDTO.getDuration());
    }


    @Test
    void getRequestsForInvalidUsernameTest() {

        //Mocking
        when(employeeRepository.findById("nonExistentUser")).thenReturn(Optional.empty());

        //Requesting pending leave requests for invalid username
        //we expect a NotFoundException

        assertThrows(NotFoundException.class,
                () -> leaveService.getPendingRequestsByUsername(
                        "randomSupervisor",
                        "nonExistentUser")
        );

        assertThrows(NotFoundException.class,
                () -> leaveService.getAllLeaveRequestForSupervisorByEmployeeId(
                        "randomSupervisor",
                        "nonExistentUser")
        );

        assertThrows(NotFoundException.class,
                () -> leaveService.getAllLeaveRequestByUsername(
                        "nonExistentUser")
        );

    }

    @Test
    void getRequestsByUsernameFromNonSupervisorTest() {

        //Mocking
        when(employeeRepository.findById(employee.getUsername())).thenReturn(Optional.of(employee));

        //Requesting pending leave requests from a non supervisor
        //we expect a UnauthorizedException

        assertThrows(UnauthorizedException.class,
                () -> leaveService.getPendingRequestsByUsername(
                        "nonSupervisor",
                        "testUser")
        );

        assertThrows(UnauthorizedException.class,
                () -> leaveService.getAllLeaveRequestForSupervisorByEmployeeId(
                        "nonSupervisor",
                        "testUser")
        );

        assertThrows(NotFoundException.class,
                () -> leaveService.getPendingByEmployeeId(
                        "nonExistentUser")
        );

    }


    @Test
    void getRequestsGeneralCorrect() {

        //Mocking
        when(employeeRepository.findById(employee.getUsername())).thenReturn(Optional.of(employee));

        //adding a leave request to the user's registry
        List<LeaveRequest> leaveRequests = new ArrayList<>();
        leaveRequests.add(LeaveRequest.builder()
                .status(new Status("PENDING"))
                .employee(employee)
                .leaveCategory(new LeaveCategory("Normal"))
                .build());
        employee.setLeaveRequests(leaveRequests);

        //adding a supervisor to employee
        List<User> supervisors = new ArrayList<>();
        supervisors.add(User.builder().username("supervisor").build());
        employee.setSupervisors(supervisors);


        //supervisor looks for user
        assertNotNull(leaveService.getPendingRequestsByUsername(
                "supervisor",
                "testUser")
        );
        assertEquals(1, leaveService.getPendingRequestsByUsername(
                "supervisor",
                "testUser").size()
        );

        //user looks for himself
        assertNotNull(leaveService.getPendingRequestsByUsername(
                "testUser",
                "testUser")
        );
        assertEquals(1, leaveService.getPendingRequestsByUsername(
                "testUser",
                "testUser").size()
        );

        //self check pending
        assertNotNull(leaveService.getPendingByEmployeeId(
                "testUser")
        );
        assertEquals(1, leaveService.getPendingByEmployeeId(
                "testUser").size()
        );

        //all requests by supervisor
        assertNotNull(leaveService.getAllLeaveRequestForSupervisorByEmployeeId(
                "supervisor",
                "testUser")
        );
        assertEquals(1, leaveService.getAllLeaveRequestForSupervisorByEmployeeId(
                "supervisor",
                "testUser").size()
        );

        //self check all
        assertNotNull(leaveService.getAllLeaveRequestByUsername(
                "testUser")
        );
        assertEquals(1, leaveService.getAllLeaveRequestByUsername(
                "testUser").size()
        );

    }

    @Test
    void getPendingRequestsFromSubordinatesTest() {
        User supervisor = User.builder().username(employee.getUsername()).build();

        //Mocking
        when(userRepository.findById(employee.getUsername())).thenReturn(Optional.of(supervisor));

        //adding a leave request to the user's registry
        List<LeaveRequest> leaveRequests = new ArrayList<>();
        leaveRequests.add(LeaveRequest.builder()
                .status(new Status("PENDING"))
                .employee(employee)
                .leaveCategory(new LeaveCategory("Normal"))
                .build());
        employee.setLeaveRequests(leaveRequests);

        //adding a supervisor to employee
        List<Employee> subordinates = new ArrayList<>();
        subordinates.add(employee);
        supervisor.setSupervisedEmployees(subordinates);

        //assertions
        assertNotNull(leaveService.getPendingRequestsFromSubordinates(
                "testUser")
        );
        assertEquals(1, leaveService.getPendingRequestsFromSubordinates(
                "testUser").size()
        );
    }

    @Test
    void getLeaveBalanceTest() {

        //Adding 18 available normal leaves to our employee
        List<LeaveBalance> leaveBalances = new ArrayList<>();
        leaveBalances.add(LeaveBalance.builder().
                employee(employee)
                .leaveCategory(new LeaveCategory(1, "Normal"))
                .days(18).daysTaken(0)
                .build());
        employee.setLeaveBalances(leaveBalances);

        when(employeeRepository.findById(employee.getUsername())).thenReturn(Optional.of(employee));

        List<LeaveBalanceDTO> resultingLeaveInfoDTOs = leaveService.getLeaveBalance("testUser");
        assertEquals(1, resultingLeaveInfoDTOs.size());

        when(employeeRepository.findById("nonExistentUser")).thenReturn(Optional.empty());

        //expect exception for nonexistent user
        assertThrows(NotFoundException.class,
                () -> leaveService.getLeaveBalance(
                        "nonExistentUser")
        );
    }

    @Test
    void getLeaveBalanceByEmployeeIdTest() {

        //Adding 18 available normal leaves to our employee
        List<LeaveBalance> leaveBalances = new ArrayList<>();
        leaveBalances.add(LeaveBalance.builder().
                employee(employee)
                .leaveCategory(new LeaveCategory(1, "Normal"))
                .days(18).daysTaken(0)
                .build());
        employee.setLeaveBalances(leaveBalances);

        //adding a supervisor to employee
        List<User> supervisors = new ArrayList<>();
        supervisors.add(User.builder().username("supervisor").build());
        employee.setSupervisors(supervisors);


        when(employeeRepository.findById(employee.getUsername())).thenReturn(Optional.of(employee));

        List<LeaveBalanceDTO> resultingLeaveInfoDTOs = leaveService.getLeaveBalanceByEmployeeId("testUser", "testUser");
        assertEquals(1, resultingLeaveInfoDTOs.size());

        when(employeeRepository.findById("nonExistentUser")).thenReturn(Optional.empty());

        //expect not found exception for nonexistent user
        assertThrows(NotFoundException.class,
                () -> leaveService.getLeaveBalanceByEmployeeId(
                        "testUser",
                        "nonExistentUser")
        );

        //expect unauthorized exception for nonexistent supervisor
        assertThrows(UnauthorizedException.class,
                () -> leaveService.getLeaveBalanceByEmployeeId(
                        "randomNonSupervisor",
                        "testUser")
        );
    }

    @Test
    void deleteLeaveByIdTest(){
        LeaveCategory normal = new LeaveCategory(1, "Normal");
        LeaveRequest leave = LeaveRequest.builder().leaveCategory(normal).build();
        int searchedLeaveId = 1;

        //Adding 18 available normal leaves to our employee
        List<LeaveBalance> leaveBalances = new ArrayList<>();
        leaveBalances.add(LeaveBalance.builder().
                employee(employee)
                .leaveCategory(normal)
                .days(18).daysTaken(0)
                .build());
        employee.setLeaveBalances(leaveBalances);

        when(employeeRepository.findById(employee.getUsername())).thenReturn(Optional.of(employee));
        when(leaveRepository.findById(searchedLeaveId)).thenReturn(Optional.of(leave));

        assertDoesNotThrow(()->leaveService.deleteById(searchedLeaveId, "testUser"));
        assertThrows(NotFoundException.class, ()->leaveService.deleteById(searchedLeaveId, "nonexistentUser"));
        assertThrows(NotFoundException.class, ()->leaveService.deleteById(searchedLeaveId+1, "testUser"));

    }
    @Test
    public void UpdateLeaveOfEmployeeFromPendingToAcceptedTest()
    {
        List<User> userList = new ArrayList<>();

        User user = User.builder()
                .username("asd")
                .password("123")
                .enabled(1)
                .build();
        userList.add(user);
        employee.setSupervisors(userList);

        LeaveCategory normal = new LeaveCategory(1, "Normal");
        LeaveRequest leaveRequest = LeaveRequest.builder()
                .employee(employee)
                .leaveCategory(normal)
                .status(Status.builder()
                        .id(1)
                        .state("PENDING")
                        .build())
                .build();


        LeaveRequestUpdateDTO leaveRequestUpdateDTO = LeaveRequestUpdateDTO.builder()
                .status(Status.builder()
                        .id(1)
                        .state("ACCEPTED")
                        .build())
                .build();
        List<LeaveBalance> leaveBalances = new ArrayList<>();
        leaveBalances.add(LeaveBalance.builder().
                employee(employee)
                .leaveCategory(normal)
                .days(18).daysTaken(0)
                .build());
        employee.setLeaveBalances(leaveBalances);
        when(leaveRepository.findById(1)).thenReturn(Optional.of(leaveRequest));
        when(employeeRepository.findById(leaveRequest.getEmployee().getUsername())).thenReturn(Optional.of(employee));

        LeaveInfoDTO result = leaveService.updateLeave(leaveRequestUpdateDTO,1,user.getUsername());

       assertNotNull(result);
        Assert.assertEquals("ACCEPTED",result.getStatus().getState());
    }

    @Test
    public void UpdateLeaveOfEmployeeFromPendingToRejectedTest() throws ParseException
    {
        List<User> userList = new ArrayList<>();

        User user = User.builder()
                .username("asd")
                .password("123")
                .enabled(1)
                .build();
        userList.add(user);
        employee.setSupervisors(userList);

        LeaveCategory normal = new LeaveCategory(1, "Normal");
        LeaveRequest leaveRequest = LeaveRequest.builder()
                .employee(employee)
                .leaveCategory(normal)
                .status(Status.builder()
                        .id(1)
                        .state("PENDING")
                        .build())
                .startDate(new SimpleDateFormat("yyyy-MM-dd").parse("2023-05-23"))
                .endDate(new SimpleDateFormat("yyyy-MM-dd").parse("2023-05-26"))
                .duration(3)
                .build();
        LeaveRequestUpdateDTO leaveRequestUpdateDTO = LeaveRequestUpdateDTO.builder()
                .status(Status.builder()
                        .id(1)
                        .state("REJECTED")
                        .build())
                .build();
        List<LeaveBalance> leaveBalances = new ArrayList<>();
        leaveBalances.add(LeaveBalance.builder().
                employee(employee)
                .leaveCategory(normal)
                .days(18).daysTaken(5)
                .build());
        employee.setLeaveBalances(leaveBalances);

        leaveRequest.setCorrespondingLeaveBalance(employee.getLeaveBalances().get(0));

        when(leaveRepository.findById(1)).thenReturn(Optional.of(leaveRequest));
        when(employeeRepository.findById(leaveRequest.getEmployee().getUsername())).thenReturn(Optional.of(employee));

        LeaveInfoDTO result = leaveService.updateLeave(leaveRequestUpdateDTO,1,user.getUsername());
        assertNotNull(result);
        Assert.assertEquals("REJECTED",result.getStatus().getState());
        leaveRequest.restoreLeaveBalance();
        Assert.assertEquals(21,employee.getLeaveBalances().get(0).getDays());
        Assert.assertEquals(2,employee.getLeaveBalances().get(0).getDaysTaken());
        Assert.assertEquals(19,employee.getLeaveBalances().get(0).getAvailableDays());

    }
}


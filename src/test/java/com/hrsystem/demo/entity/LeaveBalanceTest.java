package com.hrsystem.demo.entity;

import com.hrsystem.demo.service.LeaveServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.*;


class LeaveBalanceTest {


    Employee employee = Employee.builder()
            .username("tvasi")
            .firstName("Teo")
            .lastName("vasi")
            .email("tvasi@ots.gr")
            .mobileNumber("123123123")
            .address("Monastiriou")
            .addressNumber(1)
            .build();

    LeaveCategory leaveCategory = LeaveCategory.builder()
            .categoryName("Normal")
            .build();


    LeaveBalance leaveBalance = LeaveBalance.builder()
            .employee(employee)
            .leaveCategory(leaveCategory)
            .days(10)
            .daysTaken(2)
            .build();

    @Test
    void getAvailableDays() {
        assertEquals(8,leaveBalance.getAvailableDays());
    }

    @Test
    void addDaysTaken() {
        leaveBalance.addDaysTaken(3);
        assertEquals(5,leaveBalance.getDaysTaken());
    }
}
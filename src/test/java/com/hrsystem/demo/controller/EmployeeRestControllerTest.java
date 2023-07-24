package com.hrsystem.demo.controller;

import com.hrsystem.demo.dto.EmployeeProfileDTO;
import com.hrsystem.demo.dto.UserDTO;
import com.hrsystem.demo.service.EmployeeService;
import com.hrsystem.demo.service.UserService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@RunWith(MockitoJUnitRunner.class)
public class EmployeeRestControllerTest {

    @InjectMocks
    private EmployeeRestController employeeRestController;

    @Mock
    private EmployeeService employeeService;

    @Mock
    private UserService userService;

    @Mock
    private Authentication authentication;

    @Mock
    private EmployeeProfileDTO employeeProfileDTO;





    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Test
    public void testGetEmployeeDetails() {
        String username = "testUser";
        Mockito.when(authentication.getName()).thenReturn(username);
        Mockito.when(employeeService.findEmployeeDetailsById(username)).thenReturn(employeeProfileDTO);

        EmployeeProfileDTO result = employeeRestController.getEmployeeDetails();

        Mockito.verify(employeeService).findEmployeeDetailsById(username);
        Assert.assertEquals(employeeProfileDTO, result);
    }

    @Test
    public void testUpdateEmployee() {
        String username = "testUser";
        Mockito.when(authentication.getName()).thenReturn(username);
        Mockito.when(employeeService.updateEmployeeDetails(Mockito.any(EmployeeProfileDTO.class), Mockito.eq(username)))
                .thenReturn(employeeProfileDTO);

        EmployeeProfileDTO requestBody = new EmployeeProfileDTO();
        EmployeeProfileDTO result = employeeRestController.updateEmployee(requestBody);

        Mockito.verify(employeeService).updateEmployeeDetails(requestBody, username);
        Assert.assertEquals(employeeProfileDTO, result);
    }

    @Test
    public void testFindEmployee() {
        String username = "testUser";
        String targetUsername = "targetUser";
        Mockito.when(authentication.getName()).thenReturn(username);
        Mockito.when(employeeService.findByEmployeeId(username, targetUsername)).thenReturn(employeeProfileDTO);

        EmployeeProfileDTO result = employeeRestController.findEmployee(targetUsername);

        Mockito.verify(employeeService).findByEmployeeId(username, targetUsername);
        Assert.assertEquals(employeeProfileDTO, result);
    }

    @Test
    public void testDeleteEmployee() {
        String username = "testUser";
        String targetUsername = "targetUser";
        Mockito.when(authentication.getName()).thenReturn(username);


        String response = employeeRestController.deleteUser(targetUsername);

        Mockito.verify(userService).delete(targetUsername, username);
        Assert.assertEquals("User : " + targetUsername + " deleted ", response);
    }

}
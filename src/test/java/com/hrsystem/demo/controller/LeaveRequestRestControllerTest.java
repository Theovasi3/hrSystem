package com.hrsystem.demo.controller;

import com.hrsystem.demo.dto.LeaveBalanceDTO;
import com.hrsystem.demo.dto.LeaveInfoDTO;
import com.hrsystem.demo.dto.LeaveRequestDTO;
import com.hrsystem.demo.service.LeaveService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.ArrayList;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class LeaveRequestRestControllerTest {

    @InjectMocks
    private LeaveRequestRestController leaveRequestRestController;

    @Mock
    private LeaveService leaveService;

    @Mock
    private Authentication authentication;

    @Mock
    private LeaveRequestDTO leaveRequestDTO;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Test
    public void testAddLeaveRequest() {
        String username = "testUser";
        Mockito.when(authentication.getName()).thenReturn(username);
        Mockito.when(leaveService.save(Mockito.any(LeaveRequestDTO.class), Mockito.eq(username)))
                .thenReturn(new LeaveInfoDTO());

        LeaveInfoDTO result = leaveRequestRestController.addLeaveRequest(leaveRequestDTO);

        Mockito.verify(leaveService).save(leaveRequestDTO, username);
        Assert.assertNotNull(result);
    }

    @Test
    public void testGetLeaveBalanceByUsername() {
        String username = "testUser";
        Mockito.when(authentication.getName()).thenReturn(username);
        Mockito.when(leaveService.getLeaveBalance(username)).thenReturn(new ArrayList<>());

        List<LeaveBalanceDTO> result = leaveRequestRestController.getLeaveBalanceByUsername();

        Mockito.verify(leaveService).getLeaveBalance(username);
        Assert.assertNotNull(result);
    }

    @Test
    public void testGetPendingByEmployee() {
        String username = "testUser";
        Mockito.when(authentication.getName()).thenReturn(username);
        Mockito.when(leaveService.getPendingByEmployeeId(username)).thenReturn(new ArrayList<>());

        List<LeaveInfoDTO> result = leaveRequestRestController.getPendingByEmployee();

        Mockito.verify(leaveService).getPendingByEmployeeId(username);
        Assert.assertNotNull(result);
    }

    @Test
    public void testGetAllEmployeeLeaves() {
        String username = "testUser";
        Mockito.when(authentication.getName()).thenReturn(username);
        Mockito.when(leaveService.getAllLeaveRequestByUsername(username)).thenReturn(new ArrayList<>());

        List<LeaveInfoDTO> result = leaveRequestRestController.getAllEmployeeLeaves();

        Mockito.verify(leaveService).getAllLeaveRequestByUsername(username);
        Assert.assertNotNull(result);
    }

    @Test
    public void testGetPendingRequestsFromSubordinates() {
        String username = "testUser";
        Mockito.when(authentication.getName()).thenReturn(username);
        Mockito.when(leaveService.getPendingRequestsFromSubordinates(username)).thenReturn(new ArrayList<>());

        List<LeaveInfoDTO> result = leaveRequestRestController.getPendingRequestsFromSubordinates();

        Mockito.verify(leaveService).getPendingRequestsFromSubordinates(username);
        Assert.assertNotNull(result);
    }

    @Test
    public void testGetPendingLeaveRequestsOf() {
        String username = "testUser";
        String targetUsername = "targetUser";
        Mockito.when(authentication.getName()).thenReturn(username);
        Mockito.when(leaveService.getPendingRequestsByUsername(username, targetUsername)).thenReturn(new ArrayList<>());

        List<LeaveInfoDTO> result = leaveRequestRestController.getPendingLeaveRequestsOf(targetUsername);

        Mockito.verify(leaveService).getPendingRequestsByUsername(username, targetUsername);
        Assert.assertNotNull(result);
    }

    @Test
    public void testGetAllLeaveRequestsOf() {
        String username = "testUser";
        String targetUsername = "targetUser";
        Mockito.when(authentication.getName()).thenReturn(username);
        Mockito.when(leaveService.getAllLeaveRequestForSupervisorByEmployeeId(username, targetUsername))
                .thenReturn(new ArrayList<>());

        List<LeaveInfoDTO> result = leaveRequestRestController.getAllLeaveRequestsOf(targetUsername);

        Mockito.verify(leaveService).getAllLeaveRequestForSupervisorByEmployeeId(username, targetUsername);
        Assert.assertNotNull(result);
    }

    @Test
    public void testGetLeaveBalanceByEmployeeId() {
        String username = "testUser";
        String targetUsername = "targetUser";
        Mockito.when(authentication.getName()).thenReturn(username);
        Mockito.when(leaveService.getLeaveBalanceByEmployeeId(username, targetUsername)).thenReturn(new ArrayList<>());

        List<LeaveBalanceDTO> result = leaveRequestRestController.getLeaveBalanceByEmployeeId(targetUsername);

        Mockito.verify(leaveService).getLeaveBalanceByEmployeeId(username, targetUsername);
        Assert.assertNotNull(result);
    }

    @Test
    public void testDeleteALeaveRequestById()
    {
        String username = "testUser";
        Mockito.when(authentication.getName()).thenReturn(username);
        String result = leaveRequestRestController.deleteALeaveRequestById(1);
        Assert.assertEquals("Leave Request Deleted",result);

    }
}

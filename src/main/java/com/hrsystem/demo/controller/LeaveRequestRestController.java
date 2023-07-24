package com.hrsystem.demo.controller;


import com.hrsystem.demo.dto.LeaveBalanceDTO;
import com.hrsystem.demo.dto.LeaveInfoDTO;
import com.hrsystem.demo.dto.LeaveRequestDTO;
import com.hrsystem.demo.dto.LeaveRequestUpdateDTO;
import com.hrsystem.demo.entity.LeaveRequest;
import com.hrsystem.demo.service.LeaveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/leaves")
public class LeaveRequestRestController {

    @Autowired
    private LeaveService leaveService;


    /*    BACKLOG #'s   */

    /* 3 */
    @PostMapping("/add")
    public LeaveInfoDTO addLeaveRequest (@RequestBody LeaveRequestDTO theLeave)
    {
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();

        return leaveService.save(theLeave,userName);
    }


    /* 5 */
    @GetMapping("/balance")
    public List<LeaveBalanceDTO> getLeaveBalanceByUsername(){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return  leaveService.getLeaveBalance(username);
    }


    /* 6 */
    @GetMapping("/pending")
    public List<LeaveInfoDTO> getPendingByEmployee(){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return leaveService.getPendingByEmployeeId(username);
    }


    /* 7 */
    @GetMapping("/all")
    public List<LeaveInfoDTO> getAllEmployeeLeaves(){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return leaveService.getAllLeaveRequestByUsername(username);
    }


    /* 9 */
    @GetMapping("/subordinates/pending")
    public List<LeaveInfoDTO> getPendingRequestsFromSubordinates(){
        String searcherUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        return leaveService.getPendingRequestsFromSubordinates(searcherUsername);
    }


    /* 11 */
    @GetMapping("/{username}/pending")
    public List<LeaveInfoDTO> getPendingLeaveRequestsOf(@PathVariable String username){
        String searcherUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        return leaveService.getPendingRequestsByUsername(searcherUsername, username);
    }

    /* 12 */
    @GetMapping("/{username}/all")
    public List<LeaveInfoDTO> getAllLeaveRequestsOf(@PathVariable String username){
        String searcherUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        return leaveService.getAllLeaveRequestForSupervisorByEmployeeId(searcherUsername, username);
    }


    /* 13 */
    @GetMapping("/{username}/balance")
    public List<LeaveBalanceDTO> getLeaveBalanceByEmployeeId(@PathVariable String username){
        String searcherUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        return  leaveService.getLeaveBalanceByEmployeeId(searcherUsername,username);
    }

    @DeleteMapping("remove/{id}")
    public String deleteALeaveRequestById (@PathVariable int id)
    {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        leaveService.deleteById(id,username);
        return "Leave Request Deleted";
    }

    @PutMapping("/update/{id}")
    public LeaveInfoDTO updateLeaveOfEmployee(@PathVariable int id,@RequestBody LeaveRequestUpdateDTO leaveRequestUpdateDTO){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        LeaveInfoDTO dto = leaveService.updateLeave(leaveRequestUpdateDTO,id,username);
        return dto;
    }

}
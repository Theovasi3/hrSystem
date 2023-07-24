package com.hrsystem.demo.service;

import com.hrsystem.demo.dto.LeaveBalanceDTO;
import com.hrsystem.demo.dto.LeaveInfoDTO;
import com.hrsystem.demo.dto.LeaveRequestDTO;
import com.hrsystem.demo.dto.LeaveRequestUpdateDTO;
import com.hrsystem.demo.entity.LeaveCategory;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface LeaveService {

    LeaveInfoDTO save(LeaveRequestDTO theLeave, String userName);

    void deleteById(int leaveIdToDelete,String searchedUsername);

    public List<LeaveCategory> getLeaveCategories();

    List<LeaveInfoDTO> getPendingRequestsFromSubordinates(String searcherUsername);

    List<LeaveInfoDTO> getPendingRequestsByUsername(String searcherUsername, String searchedUsername);

    List<LeaveInfoDTO> getPendingByEmployeeId(String username);

    List<LeaveInfoDTO> getAllLeaveRequestForSupervisorByEmployeeId(String username, String searchedUsername);

    List<LeaveInfoDTO> getAllLeaveRequestByUsername(String username);

    List<LeaveBalanceDTO> getLeaveBalance(String username);

    List<LeaveBalanceDTO> getLeaveBalanceByEmployeeId(String username,String searchedUsername);

    @Transactional
    LeaveInfoDTO updateLeave(LeaveRequestUpdateDTO theLeave, int id, String username);

}

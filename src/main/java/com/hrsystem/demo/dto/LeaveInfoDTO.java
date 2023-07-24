package com.hrsystem.demo.dto;

import com.hrsystem.demo.entity.LeaveRequest;
import com.hrsystem.demo.entity.Status;
import lombok.Data;
import java.util.Date;

@Data
public class LeaveInfoDTO {

    private String employeeUsername;
    private String leaveCategory;
    private Date sumbitDate;
    private Date startDate;
    private Date endDate;
    private int duration;
    private Status status;


    public LeaveInfoDTO(){}

    public void mapFrom(LeaveRequest leaveRequest){
            employeeUsername = leaveRequest.getEmployee().getUsername();
            leaveCategory = leaveRequest.getLeaveCategory().getCategoryName();
            sumbitDate = leaveRequest.getSubmitDate();
            startDate = leaveRequest.getStartDate();
            endDate = leaveRequest.getEndDate();
            duration = leaveRequest.getDuration();
            status = leaveRequest.getStatus();
    }



}

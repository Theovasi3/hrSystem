package com.hrsystem.demo.dto;

import com.hrsystem.demo.entity.LeaveBalance;
import lombok.Data;

@Data
public class LeaveBalanceDTO {

    private String leaveCategory;
    private int days;
    private int daysTaken;


    public LeaveBalanceDTO(){}

    public void mapFrom(LeaveBalance leaveBalance){
        this.leaveCategory = leaveBalance.getLeaveCategory().getCategoryName();
        this.days = leaveBalance.getDays();
        this.daysTaken = leaveBalance.getDaysTaken();
    }

}
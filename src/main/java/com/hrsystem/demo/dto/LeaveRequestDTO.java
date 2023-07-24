package com.hrsystem.demo.dto;

import com.hrsystem.demo.entity.LeaveCategory;
import lombok.Builder;
import lombok.Data;

import java.util.Date;
@Data
@Builder
public class LeaveRequestDTO {

    private LeaveCategory leaveCategory;
    private Date startDate;
    private Date endDate;

    public LeaveRequestDTO(){}

    public LeaveRequestDTO(LeaveCategory leaveCategory, Date startDate, Date endDate) {
        this.leaveCategory = leaveCategory;
        this.startDate = startDate;
        this.endDate = endDate;
    }
}

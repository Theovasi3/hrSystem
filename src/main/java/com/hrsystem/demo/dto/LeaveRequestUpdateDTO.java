package com.hrsystem.demo.dto;

import com.hrsystem.demo.entity.LeaveRequest;
import com.hrsystem.demo.entity.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Builder
@Data
@NoArgsConstructor
public class LeaveRequestUpdateDTO {
    private Status status;

    public void mapFrom(LeaveRequest leaveRequest){
        status = leaveRequest.getStatus();
    }
}

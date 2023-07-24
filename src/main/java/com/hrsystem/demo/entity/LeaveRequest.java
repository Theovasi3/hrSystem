package com.hrsystem.demo.entity;

import com.hrsystem.demo.dto.LeaveRequestDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.junit.After;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;

@Entity
@Table(name = "leave_request")
@Data
@Builder
@AllArgsConstructor
public class LeaveRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @ManyToOne
    @JoinColumn(name = "employee_username")
    private Employee employee;

    @ManyToOne
    @JoinColumn(name = "leave_category_id")
    private LeaveCategory leaveCategory;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name = "submit_date")
    private Date submitDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name = "start_date")
    private Date startDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name = "end_date")
    private Date endDate;

    @Column(name = "duration")
    private int duration;

    @ManyToOne
    @JoinColumn(name = "status_id")
    private Status status;

    @Transient
    private LeaveBalance correspondingLeaveBalance;

    public LeaveRequest() {}

    public LeaveRequest(Employee employee, LeaveCategory leaveCategory, Date submitDate, Date startDate, Date endDate, int duration, Status status) {
        this.employee = employee;
        this.leaveCategory = leaveCategory;
        this.submitDate = submitDate;
        this.startDate = startDate;
        this.endDate = endDate;
        this.duration = duration;
        this.status = status;
    }
    public void updateLeaveBalance(){
        correspondingLeaveBalance.addDaysTaken(duration);
        correspondingLeaveBalance.setDays(correspondingLeaveBalance.getDays() - duration);
    }

    public void restoreLeaveBalance()
    {
        correspondingLeaveBalance.removeDaysTaken(duration);
        correspondingLeaveBalance.setDays(correspondingLeaveBalance.getDays() + duration);
    }
}

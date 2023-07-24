package com.hrsystem.demo.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;


@Entity
@Table(name = "leave_balance")
@Data
@Builder
@AllArgsConstructor
public class LeaveBalance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "employee_username")
    private Employee employee;

    @ManyToOne
    @JoinColumn(name = "leave_category_id")
    private LeaveCategory leaveCategory;

    @Column(name = "days")
    private int days;

    @Column(name = "days_taken")
    private int daysTaken;


    public LeaveBalance() {}

    public LeaveBalance(Employee employeeId, LeaveCategory leaveCategory, int days, int daysTaken) {
        this.employee = employeeId;
        this.leaveCategory = leaveCategory;
        this.days = days;
        this.daysTaken = daysTaken;
    }

    public int getAvailableDays(){
        return days - daysTaken;
    }

    public void addDaysTaken(int amount){
        this.daysTaken += amount;
    }
    public void removeDaysTaken(int amount)
    {
        this.daysTaken -= amount;
    }

}

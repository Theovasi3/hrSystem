package com.hrsystem.demo.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


@Entity
@Table(name = "employee")
@Getter
@Setter
@Builder
public class Employee {
    @Id
    @Column(name = "username")
    private String username;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "email")
    private String email;

    @Column(name = "mobile_num")
    private String mobileNumber;

    @Column(name = "address")
    private String address;

    @Column(name = "address_num")
    private Integer addressNumber;

    // Define relationships


    @ManyToMany(cascade = CascadeType.PERSIST, fetch = FetchType.EAGER)
    @JoinTable(
            name = "employee_supervisor",
            joinColumns = @JoinColumn(name = "employee_username"),
            inverseJoinColumns = @JoinColumn(name = "supervisor_username")
    )
    private List<User> supervisors;

    @OneToMany(mappedBy = "employee", orphanRemoval = true)
    private List<LeaveRequest> leaveRequests;

    @OneToMany(mappedBy = "employee", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<LeaveBalance> leaveBalances;


    public Employee(){}

    public Employee(String username, String firstName, String lastName, String email, String mobileNumber, String address, Integer addressNumber) {
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.mobileNumber = mobileNumber;
        this.address = address;
        this.addressNumber = addressNumber;
    }

    public Employee(String username, String firstName, String lastName, String email, String mobileNumber, String address, Integer addressNumber
            ,List<User> supervisors,List<LeaveRequest> leaveRequests,List<LeaveBalance> leaveBalances) {
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.mobileNumber = mobileNumber;
        this.address = address;
        this.addressNumber = addressNumber;
        this.supervisors = new ArrayList<>();
        this.leaveRequests = new ArrayList<>();
        this.leaveBalances = new ArrayList<>();
    }

    public Employee(String username) {
        this.username = username;
    }


    public void addSupervisor(User supervisor){
        supervisors.add(supervisor);
    }

    public List<String> getSupervisorUsernames(){
        List<String> supervisorUsernames = new ArrayList<>();
        for (User supervisor : supervisors){
            supervisorUsernames.add(supervisor.getUsername());
        }
        return supervisorUsernames;
    }

    public Boolean isSupervisedBy(String supervisorUsername){
        return getSupervisorUsernames().contains(supervisorUsername);
    }

    public Boolean hasUsername(String username){
        return username.equals(getUsername());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Employee employee)) return false;
        return Objects.equals(getUsername(), employee.getUsername()) && Objects.equals(getFirstName(), employee.getFirstName()) && Objects.equals(getLastName(), employee.getLastName()) && Objects.equals(getEmail(), employee.getEmail()) && Objects.equals(getMobileNumber(), employee.getMobileNumber()) && Objects.equals(getAddress(), employee.getAddress()) && Objects.equals(getAddressNumber(), employee.getAddressNumber());
    }
    @Override
    public int hashCode() {
        return Objects.hash(getUsername(), getFirstName(), getLastName(), getEmail(), getMobileNumber(), getAddress(), getAddressNumber());
    }

    public void setLeaveBalances(List<LeaveBalance> leaveBalances) {
        this.leaveBalances = leaveBalances;
    }
}


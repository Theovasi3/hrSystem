package com.hrsystem.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
public class EmployeeProfileDTO {
    private String firstName;
    private String lastName;
    private String email;
    private String mobileNumber;
    private String address;
    private int addressNumber;


    public EmployeeProfileDTO(){}


    public EmployeeProfileDTO(String firstName, String lastName, String email, String mobileNumber, String address, int addressNumber) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.mobileNumber = mobileNumber;
        this.address = address;
        this.addressNumber = addressNumber;
    }

}
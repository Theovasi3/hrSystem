package com.hrsystem.demo.entity;

import com.hrsystem.demo.dto.UserDTO;
import com.hrsystem.demo.service.EmployeeService;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "users")
@Data
@Builder
public class User {

    @Id
    @Column(name = "username")
    private String username;

    @Column(name = "password")
    private String password;

    @Column(name = "enabled")
    private int enabled;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "authorities",
            joinColumns = @JoinColumn(name = "username"),
            inverseJoinColumns = @JoinColumn(name = "authority")
    )
    private List<Role> roles = new ArrayList<>();

    @ManyToMany(mappedBy = "supervisors",cascade = {CascadeType.REMOVE,CascadeType.PERSIST})
    private List<Employee> supervisedEmployees;

    public User(){}

    public User(String username, String password, int enabled, List<Role> roles, List<Employee> supervisedEmployees) {
        this.username = username;
        this.password = password;
        this.enabled = enabled;
        this.roles = new ArrayList<>();
        this.supervisedEmployees = new ArrayList<>();
    }

    public User(String username, String password, int enabled) {
        this.username = username;
        this.password = password;
        this.enabled = enabled;
    }

    public User(UserDTO userDTO) {
        this.username = userDTO.getUsername();
        this.password = "{noop}" + userDTO.getPassword();
        this.enabled = 1;
    }
    public void addSupervised(Employee employee){
        supervisedEmployees.add(employee);
    }
}

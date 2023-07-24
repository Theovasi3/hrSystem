package com.hrsystem.demo.controller;

import com.hrsystem.demo.dto.EmployeeProfileDTO;
import com.hrsystem.demo.dto.UserDTO;
import com.hrsystem.demo.entity.Employee;
import com.hrsystem.demo.entity.User;
import com.hrsystem.demo.service.EmployeeService;
import com.hrsystem.demo.service.EmployeeServiceImpl;
import com.hrsystem.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/employees")
public class EmployeeRestController {

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private UserService userService;

    /*    BACKLOG #'s   */

    /* 1 */
    @GetMapping("/profile")
    public EmployeeProfileDTO getEmployeeDetails(){
        String username= SecurityContextHolder.getContext().getAuthentication().getName();
        EmployeeProfileDTO employeeProfileDTO = employeeService.findEmployeeDetailsById(username);
        return employeeProfileDTO;
    }

    /* 8 */
    @GetMapping("/profile/{username}")
    public EmployeeProfileDTO findEmployee(@PathVariable String username){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        EmployeeProfileDTO employeeProfileDTO = employeeService.findByEmployeeId(authentication.getName(), username);
        return employeeProfileDTO;
    }

    /* 14 */
    @PostMapping("/add")
    public UserDTO addNewUser(@RequestBody UserDTO userDTO){
        return userService.save(userDTO);
    }


    /* 15 */
    @DeleteMapping("/delete/{username}")
    public String deleteUser(@PathVariable String username){

        String searcherUsername = SecurityContextHolder.getContext().getAuthentication().getName();

        userService.delete(username,searcherUsername);
        /*employeeService.deleteById(username);*/

        return "User : " + username + " deleted ";
    }

    /* 16 */
    @PostMapping("/{username}/addSupervisor/{supervisor}")
    public String assignSupervisor(@PathVariable String username, @PathVariable String supervisor){
        employeeService.addSupervisor(username,supervisor);
        return "Supervisor added successfully to user: " + username ;
    }

    @PutMapping("/profile")
    public EmployeeProfileDTO updateEmployee(@RequestBody EmployeeProfileDTO theEmployee) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return employeeService.updateEmployeeDetails(theEmployee, authentication.getName());
    }

}

package com.hrsystem.demo.entity;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class EmployeeTest {

    User supervisor1 = new User("user1", "test", 1);
    User supervisor2 = new User("user2", "test", 1);
    User supervisor3 = new User("user3", "test", 1);
    User randomUser1 = new User("user4", "test", 1);
    List<User> supervisors = new ArrayList<>();

    @Before
    public void addSupervisors(){
        supervisors.add(supervisor1);
        supervisors.add(supervisor2);
        supervisors.add(supervisor3);
    }

    @Test
    public void testGetSupervisorUsernames() {

        // Initialize
        Employee employee = new Employee();
        employee.setSupervisors(supervisors);
        List<String> supervisorUsernames = employee.getSupervisorUsernames();

        // Assert
        Assert.assertEquals(3, supervisorUsernames.size());
        Assert.assertTrue(supervisorUsernames.contains(supervisor1.getUsername()));
        Assert.assertTrue(supervisorUsernames.contains(supervisor2.getUsername()));
        Assert.assertTrue(supervisorUsernames.contains(supervisor3.getUsername()));
        Assert.assertFalse(supervisorUsernames.contains(randomUser1.getUsername()));
    }
    @Test
    public void testIsSupervisedBy() {
        // Initialize
        Employee employee = new Employee();
        employee.setSupervisors(supervisors);

        Assert.assertTrue(employee.isSupervisedBy(supervisor1.getUsername()));
        Assert.assertTrue(employee.isSupervisedBy(supervisor2.getUsername()));
        Assert.assertTrue(employee.isSupervisedBy(supervisor3.getUsername()));
        Assert.assertFalse(employee.isSupervisedBy(randomUser1.getUsername()));
    }

    @Test
    public void hasUsername(){
        // Initialize
        Employee employee = new Employee();
        employee.setUsername("emp1");

        Assert.assertTrue(employee.hasUsername("emp1"));
    }

}




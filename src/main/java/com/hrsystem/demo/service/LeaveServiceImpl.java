package com.hrsystem.demo.service;

import com.hrsystem.demo.dto.LeaveBalanceDTO;
import com.hrsystem.demo.dto.LeaveInfoDTO;
import com.hrsystem.demo.dto.LeaveRequestDTO;
import com.hrsystem.demo.dto.LeaveRequestUpdateDTO;
import com.hrsystem.demo.entity.*;
import com.hrsystem.demo.exception.NotFoundException;
import com.hrsystem.demo.exception.UnauthorizedException;
import com.hrsystem.demo.repository.EmployeeRepository;
import com.hrsystem.demo.repository.LeaveCategoryRepository;
import com.hrsystem.demo.repository.LeaveRepository;
import com.hrsystem.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.stream.Stream;
import java.util.*;

@Service
public class LeaveServiceImpl implements LeaveService {

    private final LeaveRepository leaveRepository;
    private final UserRepository userRepository;
    private final EmployeeRepository employeeRepository;
    private final LeaveCategoryRepository leaveCategoryRepository;
    private final StatusServiceImpl statusService;

    @Autowired
    public LeaveServiceImpl(LeaveRepository leaveRepository, LeaveCategoryRepository leaveCategoryRepository, UserRepository userRepository, EmployeeRepository employeeRepository, StatusServiceImpl statusService) {
        this.leaveRepository = leaveRepository;
        this.leaveCategoryRepository = leaveCategoryRepository;
        this.userRepository = userRepository;
        this.employeeRepository = employeeRepository;
        this.statusService = statusService;
    }


    @Transactional
    @Override
    public LeaveInfoDTO save(LeaveRequestDTO theLeave, String userName) {

        //Initialize employee, pending status, request and returned leave info and
        Employee employee = employeeRepository.findById(userName).get();
        LeaveRequest dbLeaveRequest = new LeaveRequest();
        initializeLeaveRequestFromDTO(dbLeaveRequest, theLeave, employee);
        LeaveInfoDTO leaveInfoDTO = new LeaveInfoDTO();
        leaveInfoDTO.mapFrom(dbLeaveRequest);


        if (isValid(dbLeaveRequest)) {
            leaveRepository.save(dbLeaveRequest);
            dbLeaveRequest.updateLeaveBalance();

        } else {
            leaveInfoDTO.setStatus(statusService.getRejectedStateStatus());
        }

        return leaveInfoDTO;
    }

    public boolean isValid(LeaveRequest lr) {
        //find the leaveBalance for the category of this request
        for (LeaveBalance leaveBalance : lr.getEmployee().getLeaveBalances()) {
            if (leaveBalance.getLeaveCategory().equals(lr.getLeaveCategory())) {
                lr.setCorrespondingLeaveBalance(leaveBalance);
            }
        }

        //the request is valid if it doesn't exceed the remaining days of the leave balance
        if (lr.getCorrespondingLeaveBalance() != null) {
            return (lr.getCorrespondingLeaveBalance().getAvailableDays() >= lr.getDuration());
        } else return false; //employee is not entitled to this type of leave
    }

    public void initializeLeaveRequestFromDTO(LeaveRequest lr, LeaveRequestDTO lrDTO, Employee employee) {
        lr.setEmployee(employee);
        lr.setStartDate(lrDTO.getStartDate());
        lr.setEndDate(lrDTO.getEndDate());
        lr.setLeaveCategory(lrDTO.getLeaveCategory());
        lr.setSubmitDate(Date.from(Instant.now()));
        lr.setStatus(statusService.getPendingStateStatus());
        //Duration calculation
        LocalDate startDate = lr.getStartDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate endDate = lr.getEndDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        long totalDays = ChronoUnit.DAYS.between(startDate, endDate) + 1;
        long weekends = Stream.iterate(startDate, date -> date.plusDays(1))
                .limit(totalDays)
                .filter(date -> date.getDayOfWeek() == DayOfWeek.SATURDAY || date.getDayOfWeek() == DayOfWeek.SUNDAY)
                .count();
        lr.setDuration((int) (totalDays - weekends));
        System.out.println(lr.getDuration());
    }


    @Override
    public List<LeaveInfoDTO> getPendingRequestsByUsername(String searcherUsername, String searchedUsername) {
        List<LeaveInfoDTO> leaves = new ArrayList<>();
        Optional<Employee> employee = employeeRepository.findById(searchedUsername);

        if (employee.isPresent()) {
            if (employee.get().isSupervisedBy(searcherUsername) || employee.get().hasUsername(searcherUsername)) {
                for (LeaveRequest leaveRequest : employee.get().getLeaveRequests()) {
                    if (Objects.equals(leaveRequest.getStatus().getState(), "PENDING")) {
                        LeaveInfoDTO leaveInfoDTO = new LeaveInfoDTO();
                        leaveInfoDTO.mapFrom(leaveRequest);
                        leaves.add(leaveInfoDTO);
                    }
                }
            } else {
                throw new UnauthorizedException("No permission to access this information: " + searchedUsername + " user leave requests.");
            }
        } else {
            // we didn't find the employee
            throw new NotFoundException("Could not find the employee " + searchedUsername);
        }
        return leaves;
    }

    @Override
    public List<LeaveInfoDTO> getPendingByEmployeeId(String username) {
        List<LeaveInfoDTO> leaves = new ArrayList<>();
        Optional<Employee> employee = employeeRepository.findById(username);

        if (employee.isPresent()) {

            for (LeaveRequest leaveRequest : employee.get().getLeaveRequests()) {
                if (Objects.equals(leaveRequest.getStatus().getState(), "PENDING")) {
                    LeaveInfoDTO leaveInfoDTO = new LeaveInfoDTO();
                    leaveInfoDTO.mapFrom(leaveRequest);
                    leaves.add(leaveInfoDTO);
                }
            }
        } else {
            // we didn't find the employee
            throw new NotFoundException("Could not find the employee " + username);
        }
        return leaves;
    }

    @Override
    public List<LeaveInfoDTO> getAllLeaveRequestForSupervisorByEmployeeId(String searcherUsername, String username) {
        List<LeaveInfoDTO> leaves = new ArrayList<>();
        Optional<Employee> employee = employeeRepository.findById(username);

        if (employee.isPresent()) {
            if (employee.get().isSupervisedBy(searcherUsername)) {
                for (LeaveRequest leaveRequest : employee.get().getLeaveRequests()) {
                    LeaveInfoDTO leaveInfoDTO = new LeaveInfoDTO();
                    leaveInfoDTO.mapFrom(leaveRequest);
                    leaves.add(leaveInfoDTO);
                }
            } else {
                throw new UnauthorizedException("No permission to access this information: " + username + " user leave requests.");
            }
        } else {
            // we didn't find the employee
            throw new NotFoundException("Could not find the employee " + username);
        }
        return leaves;
    }

    @Override
    public List<LeaveInfoDTO> getAllLeaveRequestByUsername(String username) {
        List<LeaveInfoDTO> leaves = new ArrayList<>();
        Optional<Employee> employee = employeeRepository.findById(username);

        if (employee.isPresent()) {
            if (employee.get().hasUsername(username)) {
                for (LeaveRequest leaveRequest : employee.get().getLeaveRequests()) {
                    LeaveInfoDTO leaveInfoDTO = new LeaveInfoDTO();
                    leaveInfoDTO.mapFrom(leaveRequest);
                    leaves.add(leaveInfoDTO);
                }
            }
        } else {
            throw new NotFoundException("Could not find the employee " + username);
        }
        return leaves;
    }

    @Override
    public List<LeaveInfoDTO> getPendingRequestsFromSubordinates(String searcherUsername) {
        List<LeaveInfoDTO> leaves = new ArrayList<>();
        List<Employee> subordinates = userRepository.findById(searcherUsername).get().getSupervisedEmployees();
        for (Employee employee : subordinates) {
            for (LeaveRequest leaveRequest : employee.getLeaveRequests()) {
                if (Objects.equals(leaveRequest.getStatus().getState(), "PENDING")) {
                    LeaveInfoDTO leaveInfoDTO = new LeaveInfoDTO();
                    leaveInfoDTO.mapFrom(leaveRequest);
                    leaves.add(leaveInfoDTO);
                }
            }
        }
        return leaves;
    }


    @Override
    public List<LeaveBalanceDTO> getLeaveBalance(String username) {
        List<LeaveBalanceDTO> leaveBalanceDTO = new ArrayList<>();
        Optional<Employee> employee = employeeRepository.findById(username);
        if (employee.isPresent()) {
            if (employee.get().hasUsername(username)) {
                for (LeaveBalance leaveBalance : employee.get().getLeaveBalances()) {
                    LeaveBalanceDTO leaveBalanceDTO1 = new LeaveBalanceDTO();
                    leaveBalanceDTO1.mapFrom(leaveBalance);
                    leaveBalanceDTO.add(leaveBalanceDTO1);
                }
            }
        } else {
            throw new NotFoundException("Could not find the employee " + username);
        }

        return leaveBalanceDTO;
    }

    @Override
    public List<LeaveBalanceDTO> getLeaveBalanceByEmployeeId(String username, String searchedUsername) {
        List<LeaveBalanceDTO> leaveBalanceDTO = new ArrayList<>();
        Optional<Employee> employee = employeeRepository.findById(searchedUsername);
        if (employee.isPresent()) {
            if (employee.get().isSupervisedBy(username) || employee.get().hasUsername(username)) {
                for (LeaveBalance leaveBalance : employee.get().getLeaveBalances()) {
                    LeaveBalanceDTO leaveBalanceDTO1 = new LeaveBalanceDTO();
                    leaveBalanceDTO1.mapFrom(leaveBalance);
                    leaveBalanceDTO.add(leaveBalanceDTO1);
                }
            } else {
                throw new UnauthorizedException("No permission to access this information: " + searchedUsername + " user leave requests.");
            }
        } else {
            // we didn't find the employee
            throw new NotFoundException("Could not find the employee " + searchedUsername);

        }
        return leaveBalanceDTO;
    }

    @Transactional
    @Override
    public void deleteById(int leaveIdToDelete, String searchedUserName) {

        Optional<Employee> employee = employeeRepository.findById(searchedUserName);
        Optional<LeaveRequest> leaveRequestOptional = leaveRepository.findById(leaveIdToDelete);

        if (employee.isPresent()) {
            if (leaveRequestOptional.isPresent()) {
                employee.get().getLeaveBalances().forEach(leaveBalance -> {

                    if (leaveRequestOptional.get().getLeaveCategory() == leaveBalance.getLeaveCategory()) {
                        leaveRequestOptional.get().setCorrespondingLeaveBalance(leaveBalance);
                        leaveRequestOptional.get().restoreLeaveBalance();
                        leaveRepository.deleteById(leaveIdToDelete);
                    }
                });
            } else throw new NotFoundException("Could not find the leaveRequest ");
        } else throw new NotFoundException("Could not find the employee ");
    }

    public List<LeaveCategory> getLeaveCategories() {
        return leaveCategoryRepository.findAll();
    }

    @Transactional
    @Override
    public LeaveInfoDTO updateLeave(LeaveRequestUpdateDTO theLeave, int id, String username) {

        Optional<LeaveRequest> leaveRequestOptional = leaveRepository.findById(id);
        if (leaveRequestOptional.isEmpty())
            throw new NotFoundException("Could not find the leaveRequest ");

        Optional<Employee> employee = employeeRepository.findById(leaveRequestOptional.get().getEmployee().getUsername());
        if (employee.get().isSupervisedBy(username) && employee.isPresent()) {
            if (leaveRequestOptional.get().getStatus().getState().equals("PENDING")) {
                leaveRequestOptional.get().setStatus(theLeave.getStatus());
                theLeave.mapFrom(leaveRequestOptional.get());
                LeaveInfoDTO leaveInfoDTO = new LeaveInfoDTO();
                leaveInfoDTO.mapFrom(leaveRequestOptional.get());
                leaveRepository.save(leaveRequestOptional.get());
                employee.get().getLeaveBalances().forEach(leaveBalance -> {
                    if (leaveRequestOptional.get().getLeaveCategory() == leaveBalance.getLeaveCategory() && leaveRequestOptional.get().getStatus().getState().equals("DECLINED")) {
                        leaveRequestOptional.get().setCorrespondingLeaveBalance(leaveBalance);
                        leaveRequestOptional.get().restoreLeaveBalance();
                    }
                });
                return leaveInfoDTO;
            } else throw new NotFoundException("This is not a Pending Request");
        } else throw new NotFoundException("Could not find the employee ");
    }

}
package com.hrsystem.demo;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hrsystem.demo.dto.*;
import com.hrsystem.demo.entity.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureMockMvc
class HrSystemApplicationTests {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private LeaveBalanceH2TestRepository leaveBalanceH2TestRepository;
    @Autowired
    private LeaveH2TestRepository leaveH2TestRepository;
    @Autowired
    private EmployeeH2TestRepository employeeH2TestRepository;
    @Autowired
    private RoleH2TestRepository roleH2TestRepository;
    @Autowired
    private StatusH2TestRepository statusH2TestRepository;
    @Autowired
    private UserH2TestRepository userH2TestRepository;
    @Autowired
    private LeaveCategoryH2TestRepository leaveCategoryH2TestRepository;
    private Status pendingStatus, acceptedStatus;
    private LeaveCategory normalLeaveCategory, sickLeaveCategory, parentalLeaveCategory;
    private Role adminRole;
    private User user, user1;
    private Employee employee, employee1;
    private LeaveBalance leaveBalance, leaveBalance1;

    /*
    // Creating a "PENDING"" status
    // an "ACCEPTED" status
    // a "NORMAL" type leave category
    // an "ADMIN" role
    // 2 users, "Intern" and "Proistamenos"
    // a leaveBalance for each - of category "NORMAL"
    // and the "Proistamenos" is being assigned as supervisor of "Intern"
    */
    @BeforeEach
    public void init() {
        userH2TestRepository.deleteAll();

        pendingStatus = Status.builder()
                .id(1)
                .state("PENDING")
                .build();
        statusH2TestRepository.save(pendingStatus);

        acceptedStatus = Status.builder()
                .id(2)
                .state("ACCEPTED")
                .build();
        statusH2TestRepository.save(acceptedStatus);

        normalLeaveCategory = LeaveCategory
                .builder()
                .id(1)
                .categoryName("NORMAL")
                .build();
        sickLeaveCategory = LeaveCategory
                .builder()
                .id(2)
                .categoryName("SICK")
                .build();
        parentalLeaveCategory = LeaveCategory
                .builder()
                .id(3)
                .categoryName("PARENTAL")
                .build();
        leaveCategoryH2TestRepository.save(normalLeaveCategory);
        leaveCategoryH2TestRepository.save(sickLeaveCategory);
        leaveCategoryH2TestRepository.save(parentalLeaveCategory);


        adminRole = Role.builder()
                .authority("ROLE_ADMIN")
                .build();
        roleH2TestRepository.save(adminRole);

        user = User.builder()
                .username("Proistamenos")
                .password("1234")
                .build();

        user1 = User.builder()
                .username("Intern")
                .password("1234")
                .build();

        userH2TestRepository.save(user);
        userH2TestRepository.save(user1);

        employee = Employee.builder()
                .username("Proistamenos")
                .firstName("Afentikos")
                .lastName("CEO")
                .email("boss@gmail.com")
                .mobileNumber("6985345634")
                .address("Monasthriou")
                .addressNumber(120)
                .build();

        employee1 = Employee.builder()
                .username("Intern")
                .firstName("Kahmenos")
                .lastName("Praktikopoulos")
                .email("noobas@ots.gr")
                .mobileNumber("6949168298")
                .address("Monasthriou")
                .addressNumber(60)
                .build();


        leaveH2TestRepository.deleteAll();
        leaveBalanceH2TestRepository.deleteAll();

        leaveBalance = LeaveBalance.builder().
                employee(employee)
                .leaveCategory(normalLeaveCategory)
                .days(18).daysTaken(5)
                .build();

        leaveBalance1 = LeaveBalance.builder().
                employee(employee1)
                .leaveCategory(normalLeaveCategory)
                .days(18).daysTaken(0)
                .build();


        employee.getLeaveBalances().add(leaveBalance);
        employee1.getLeaveBalances().add(leaveBalance1);
        employeeH2TestRepository.save(employee);
        employeeH2TestRepository.save(employee1);

        user.getSupervisedEmployees().add(employee1);
        userH2TestRepository.save(user);
        employee1.getSupervisors().add(user);
        employeeH2TestRepository.save(employee1);
    }


    @DisplayName("#1: Get user profile ")
    @WithMockUser(username = "Intern", roles = "EMPLOYEE", password = "1234")
    @Test
    void checkMyProfile() throws Exception {

        //get user profile
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/api/employees/profile"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "application/json"))
                .andReturn();

        String jsonResponse = result.getResponse().getContentAsString();
        String email = new ObjectMapper().readTree(jsonResponse).get("email").asText();

        //check that the email field matches the response
        assertEquals(employee1.getEmail(), email);
    }


    @DisplayName("#2: Edit user profile ")
    @WithMockUser(username = "Intern", roles = "EMPLOYEE", password = "1234")
    @Test
    void editMyProfile() throws Exception {
        //the change we want to make
        String modifiedEmail = "newEmail@ots.gr";

        //get our current profile info
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/api/employees/profile"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "application/json"))
                .andReturn();

        //change the email field
        String jsonResponse = result.getResponse().getContentAsString();
        ObjectMapper objectMapper = new ObjectMapper();
        String modifiedUserDtoJSON = objectMapper.writeValueAsString(((ObjectNode) new ObjectMapper().readTree(jsonResponse)).put("email", modifiedEmail));

        //put the modified user in the database (and have it retrieved)
        MvcResult modifiedUserResult = mockMvc.perform(MockMvcRequestBuilders.put("/api/employees/profile")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(modifiedUserDtoJSON))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "application/json"))
                .andReturn();

        //check if the email changed
        jsonResponse = modifiedUserResult.getResponse().getContentAsString();
        String email = new ObjectMapper().readTree(jsonResponse).get("email").asText();

        assertEquals(modifiedEmail, email);
    }


    @DisplayName("#3: Request new leave ")
    @WithMockUser(username = "Intern", roles = "EMPLOYEE", password = "1234")
    @Test
    public void addLeaveRequest() throws Exception {

        //create new leave request dto
        LeaveRequestDTO leaveRequestDTO = LeaveRequestDTO.builder()
                .leaveCategory(normalLeaveCategory)
                .startDate(new SimpleDateFormat("yyyy-MM-dd").parse("2023-06-05"))
                .endDate(new SimpleDateFormat("yyyy-MM-dd").parse("2023-06-12"))
                .build();

        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(leaveRequestDTO);

        //post it
        mockMvc.perform(MockMvcRequestBuilders.post("/api/leaves/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)).andExpect(status().isOk()).andReturn();

        //check that it has been saved
        assertEquals(1, leaveH2TestRepository.count());
    }


    @DisplayName("#4: Cancel a LeaveRequest")
    @WithMockUser(username = "Intern", roles = "EMPLOYEE", password = "1234")
    @Test
    public void cancelALeaveRequestTest() throws  Exception {

        LeaveRequestDTO leaveRequestDTO = LeaveRequestDTO.builder()
                .leaveCategory(normalLeaveCategory)
                .startDate(new SimpleDateFormat("yyyy-MM-dd").parse("2023-05-26"))
                .endDate(new SimpleDateFormat("yyyy-MM-dd").parse("2023-05-27"))
                .build();

        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(leaveRequestDTO);

        // save a leaveRequest
        mockMvc.perform(MockMvcRequestBuilders.post("/api/leaves/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)).andExpect(status().isOk());

        List<LeaveRequest> leaveRequests = leaveH2TestRepository.findAll();

        //cancel it
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/leaves/remove/{id}", leaveRequests.get(0).getId()));

        // assert that nothing has changed
        assertEquals(0, leaveH2TestRepository.count());

    }


    @DisplayName("#5: Get user leave balances ")
    @WithMockUser(username = "Intern", roles = "EMPLOYEE", password = "1234")
    @Test
    void checkMyLeaveBalance() throws Exception {

        //get user leaveBalances
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/api/leaves/balance"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "application/json"))
                .andReturn();

        String jsonResponse = result.getResponse().getContentAsString();
        List<LeaveBalanceDTO> returnedLeaveBalanceDTOlist = new ObjectMapper().readValue(jsonResponse, new TypeReference<List<LeaveBalanceDTO>>() {
        });

        //assertion
        List<LeaveBalanceDTO> employeeLeaveBalanceDTOlist = new ArrayList<>();
        for (LeaveBalance leaveBalance : employee1.getLeaveBalances()) {
            LeaveBalanceDTO leaveBalanceDTO = new LeaveBalanceDTO();
            leaveBalanceDTO.mapFrom(leaveBalance);
            employeeLeaveBalanceDTOlist.add(leaveBalanceDTO);
        }
        assertEquals(returnedLeaveBalanceDTOlist, employeeLeaveBalanceDTOlist);
    }


    @DisplayName("#6: Get pending requests")
    @WithMockUser(username = "Intern", roles = "EMPLOYEE", password = "1234")
    @Test
    public void seePendingRequestsTest() throws  Exception {

        LeaveRequestDTO leaveRequestDTO = LeaveRequestDTO.builder()
                .leaveCategory(normalLeaveCategory)
                .startDate(new SimpleDateFormat("yyyy-MM-dd").parse("2023-05-26"))
                .endDate(new SimpleDateFormat("yyyy-MM-dd").parse("2023-05-30"))
                .build();

        LeaveRequest leaveRequestAccepted = LeaveRequest.builder()
                .employee(employee1)
                .leaveCategory(normalLeaveCategory)
                .submitDate(new Date())
                .startDate(new SimpleDateFormat("yyyy-MM-dd").parse("2023-06-05"))
                .endDate(new SimpleDateFormat("yyyy-MM-dd").parse("2023-06-12"))
                .duration(6)
                .status(acceptedStatus)
                .build();
        leaveH2TestRepository.save(leaveRequestAccepted);


        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(leaveRequestDTO);

        // save a leaveRequest
        mockMvc.perform(MockMvcRequestBuilders.post("/api/leaves/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)).andExpect(status().isOk());

        // get all my pendingRequests
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/api/leaves/pending"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "application/json"))
                .andReturn();

        json = result.getResponse().getContentAsString();

        assertFalse(json.isEmpty());

        byte[] responseContent = result.getResponse().getContentAsByteArray();
        List<LeaveInfoDTO> resultObjects = new ObjectMapper().readValue(responseContent, new TypeReference<List<LeaveInfoDTO>>() {
        });

        //Assert that every leave request is "Pending"
        for (LeaveInfoDTO leaveInfoDTO : resultObjects) {
            assertEquals(pendingStatus, leaveInfoDTO.getStatus());
        }
    }


    @DisplayName("#7: Get history of requests")
    @WithMockUser(username = "Intern", roles = "EMPLOYEE", password = "1234")
    @Test
    public void seeHistoryOfRequestsTest() throws Exception {
        LeaveRequestDTO leaveRequestDTO = LeaveRequestDTO.builder()
                .leaveCategory(normalLeaveCategory)
                .startDate(new SimpleDateFormat("yyyy-MM-dd").parse("2023-05-26"))
                .endDate(new SimpleDateFormat("yyyy-MM-dd").parse("2023-05-30"))
                .build();

        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(leaveRequestDTO);

        // add a request
        mockMvc.perform(MockMvcRequestBuilders.post("/api/leaves/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)).andExpect(status().isOk());

        // get all my leaveRequests
        MvcResult modifiedUserResult = mockMvc.perform(MockMvcRequestBuilders.get("/api/leaves/all"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "application/json"))
                .andReturn();

        String jsonResponse = modifiedUserResult.getResponse().getContentAsString();

        assertFalse(jsonResponse.isEmpty());

    }


    @DisplayName("#8: Get subordinate profile")
    @WithMockUser(username = "Proistamenos", roles = {"MANAGER"}, password = "1234")
    @Test
    void checkSubordinateProfile() throws Exception {

        //get user profile
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/api/employees/profile/{username}", employee1.getUsername()))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "application/json"))
                .andReturn();

        String jsonResponse = result.getResponse().getContentAsString();
        String email = new ObjectMapper().readTree(jsonResponse).get("email").asText();

        //check that the email field matches the response
        assertEquals(employee1.getEmail(), email);
    }
    @DisplayName("#9: Get pending requests of all subordinates")
    @WithMockUser(username = "Proistamenos", roles = {"MANAGER","ADMIN"}, password = "1234")
    @Test
    public void seePendingRequestsOfAllSubordinatesTest() throws  Exception {

        UserDTO newUser1 = UserDTO.builder().username("employeeToTest").password("123").build();

        ObjectMapper objectMapper = new ObjectMapper();
        String nUser1 = objectMapper.writeValueAsString(newUser1).toString();

        //adding new user
        mockMvc.perform(MockMvcRequestBuilders.post("/api/employees/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(nUser1)).andReturn();

        EmployeeProfileDTO employeeProfileDTO = new EmployeeProfileDTO();
        employeeProfileDTO = EmployeeProfileDTO.builder()
                .firstName("New")
                .lastName("User")
                .email("newUser@ots.gr")
                .mobileNumber("123566897")
                .address("Mon")
                .addressNumber(1)
                .build();
        String employeeDTO = objectMapper.writeValueAsString(employeeProfileDTO).toString();

        mockMvc.perform(MockMvcRequestBuilders.post("/api/employees/{username}/addSupervisor/{supervisor}", "employeeToTest", "Proistamenos")).andReturn();
        //updating employee profile
        mockMvc.perform(MockMvcRequestBuilders.put("/api/employees/profile").with(user("employeeToTest").roles("EMPLOYEE"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(employeeDTO))
                .andExpect(status().isOk())
                .andReturn();



        //Pending leave request from subordinate
        LeaveRequest leaveRequestPending = LeaveRequest.builder()
                .employee(employee1)
                .leaveCategory(normalLeaveCategory)
                .submitDate(new Date())
                .startDate(new SimpleDateFormat("yyyy-MM-dd").parse("2023-06-05"))
                .endDate(new SimpleDateFormat("yyyy-MM-dd").parse("2023-06-12"))
                .duration(6)
                .status(pendingStatus)
                .build();
        leaveH2TestRepository.save(leaveRequestPending);

        LeaveRequest leaveRequestPending1 = LeaveRequest.builder()
                .employee(employeeH2TestRepository.findById("employeeToTest").get())
                .leaveCategory(normalLeaveCategory)
                .submitDate(new Date())
                .startDate(new SimpleDateFormat("yyyy-MM-dd").parse("2023-06-05"))
                .endDate(new SimpleDateFormat("yyyy-MM-dd").parse("2023-06-12"))
                .duration(6)
                .status(pendingStatus)
                .build();
        leaveH2TestRepository.save(leaveRequestPending);

        //Accepted leave request from subordinate
        LeaveRequest leaveRequestAccepted = LeaveRequest.builder()
                .employee(employee1)
                .leaveCategory(normalLeaveCategory)
                .submitDate(new Date())
                .startDate(new SimpleDateFormat("yyyy-MM-dd").parse("2023-06-05"))
                .endDate(new SimpleDateFormat("yyyy-MM-dd").parse("2023-06-12"))
                .duration(6)
                .status(acceptedStatus)
                .build();
        leaveH2TestRepository.save(leaveRequestAccepted);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/api/leaves/{username}/pending", employee1.getUsername()))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "application/json"))
                .andReturn();

        //Map json to LeaveInfoDTO's
        byte[] responseContent = result.getResponse().getContentAsByteArray();
        List<LeaveInfoDTO> resultObjects = new ObjectMapper().readValue(responseContent, new TypeReference<List<LeaveInfoDTO>>() {
        });

        //Assert that every leave request is "Pending"
        for (LeaveInfoDTO leaveInfoDTO : resultObjects) {
            assertEquals(pendingStatus, leaveInfoDTO.getStatus());
        }

    }

    @DisplayName("#10: Decline/approve subordinate request")
    @WithMockUser(username = "Proistamenos", roles = "ADMIN", password = "1234")
    @Test
    void updateLeaveRequestTest() throws Exception {

        //Build a Leave request from subordinate
        LeaveRequest leaveRequest = LeaveRequest.builder()
                .employee(employee1)
                .leaveCategory(normalLeaveCategory)
                .submitDate(new Date())
                .startDate(new SimpleDateFormat("yyyy-MM-dd").parse("2023-06-05"))
                .endDate(new SimpleDateFormat("yyyy-MM-dd").parse("2023-06-12"))
                .duration(6)
                .status(pendingStatus)
                .build();
        leaveH2TestRepository.save(leaveRequest);

        int leaveId = leaveRequest.getId();

        //Build Accept DTO
        LeaveRequestUpdateDTO leaveRequestUpdateDTO = LeaveRequestUpdateDTO.builder().status(acceptedStatus).build();
        String updateDto = new ObjectMapper().writeValueAsString(leaveRequestUpdateDTO);

        //Accept the leave request
        mockMvc.perform(MockMvcRequestBuilders.put("/api/leaves/update/{id}", leaveId)
                .contentType(MediaType.APPLICATION_JSON).content(updateDto)).andExpect(status().isOk());

        //Assert that the leave now has a state of "ACCEPTED"
        assertEquals(acceptedStatus, leaveH2TestRepository.findById(leaveId).get().getStatus());
    }

    @DisplayName("#11: Get pending requests of subordinate")
    @WithMockUser(username = "Proistamenos", roles = "MANAGER", password = "1234")
    @Test
    public void seePendingRequestsOfASubordinateTest() throws  Exception {

        //Pending leave request from subordinate
        LeaveRequest leaveRequestPending = LeaveRequest.builder()
                .employee(employee1)
                .leaveCategory(normalLeaveCategory)
                .submitDate(new Date())
                .startDate(new SimpleDateFormat("yyyy-MM-dd").parse("2023-06-05"))
                .endDate(new SimpleDateFormat("yyyy-MM-dd").parse("2023-06-12"))
                .duration(6)
                .status(pendingStatus)
                .build();
        leaveH2TestRepository.save(leaveRequestPending);

        //Accepted leave request from subordinate
        LeaveRequest leaveRequestAccepted = LeaveRequest.builder()
                .employee(employee1)
                .leaveCategory(normalLeaveCategory)
                .submitDate(new Date())
                .startDate(new SimpleDateFormat("yyyy-MM-dd").parse("2023-06-05"))
                .endDate(new SimpleDateFormat("yyyy-MM-dd").parse("2023-06-12"))
                .duration(6)
                .status(acceptedStatus)
                .build();
        leaveH2TestRepository.save(leaveRequestAccepted);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/api/leaves/{username}/pending", employee1.getUsername()))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "application/json"))
                .andReturn();

        //Map json to LeaveInfoDTO's
        byte[] responseContent = result.getResponse().getContentAsByteArray();
        List<LeaveInfoDTO> resultObjects = new ObjectMapper().readValue(responseContent, new TypeReference<List<LeaveInfoDTO>>() {
        });

        //Assert that every leave request is "Pending"
        for (LeaveInfoDTO leaveInfoDTO : resultObjects) {
            assertEquals(pendingStatus, leaveInfoDTO.getStatus());
        }
    }




    @DisplayName("#12: Get request history of a subordinate")
    @WithMockUser(username = "Proistamenos", roles = "MANAGER", password = "1234")
    @Test
    void seeHistoryOfLeaveRequestsOfAnEmployeeTest() throws  Exception {
        // creating 2 leaveRequests to save them
        LeaveRequestDTO leaveRequestDTO = LeaveRequestDTO.builder()
                .leaveCategory(normalLeaveCategory)
                .startDate(new SimpleDateFormat("yyyy-MM-dd").parse("2023-05-26"))
                .endDate(new SimpleDateFormat("yyyy-MM-dd").parse("2023-05-30"))
                .build();

        LeaveRequestDTO leaveRequest2DTO = LeaveRequestDTO.builder()
                .leaveCategory(normalLeaveCategory)
                .startDate(new SimpleDateFormat("yyyy-MM-dd").parse("2023-05-26"))
                .endDate(new SimpleDateFormat("yyyy-MM-dd").parse("2023-05-30"))
                .build();


        ObjectMapper objectMapper = new ObjectMapper();


        String json = objectMapper.writeValueAsString(leaveRequestDTO);
        // save the leaveRequests
        mockMvc.perform(MockMvcRequestBuilders.post("/api/leaves/add").with(user("Intern").roles("EMPLOYEE"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)).andExpect(status().isOk());

        String json1 = objectMapper.writeValueAsString(leaveRequest2DTO);
        mockMvc.perform(MockMvcRequestBuilders.post("/api/leaves/add").with(user("Intern").roles("EMPLOYEE"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(json1)).andExpect(status().isOk());



        // get  leaveRequests of a subordinate
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/api/leaves/subordinates/pending"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "application/json"))
                .andReturn();

        String response = result.getResponse().getContentAsString();


        assertFalse(response.isEmpty());
    }


    @DisplayName("#13: Get LeaveBalances of a subordinate")
    @WithMockUser(username = "Proistamenos", roles = "MANAGER", password = "1234")
    @Test
    void seeLeaveBalancesOfAnEmployee() throws Exception {

        //get leave balances of an employee
        MvcResult results = mockMvc.perform(MockMvcRequestBuilders.get("/api/leaves/{username}/balance", employee1.getUsername()))
                .andReturn();
        ObjectMapper objectMapper = new ObjectMapper();

        String response = results.getResponse().getContentAsString();

        byte[] returnLeaveBalances = results.getResponse().getContentAsByteArray();

        List<LeaveBalanceDTO> leaveBalanceDTOS = objectMapper.readValue(returnLeaveBalances, new TypeReference<List<LeaveBalanceDTO>>() {
        });

        // assert that employee has 1 leaveBalance (from the init method)
        assertEquals(1, leaveBalanceDTOS.size());

    }


    @DisplayName("#14/16: Add new user and set a supervisor for him")
    @WithMockUser(username = "Proistamenos", roles = "ADMIN", password = "1234")
    @Test
    public void addNewUserAndSetHimSupervisor() throws Exception {

        UserDTO newUser2 = UserDTO.builder().username("NewUser2").password("123").build();

        ObjectMapper objectMapper = new ObjectMapper();
        String nUser = objectMapper.writeValueAsString(newUser2).toString();

        //adding new user
        mockMvc.perform(MockMvcRequestBuilders.post("/api/employees/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(nUser)).andReturn();

        //assigning a supervisor to NewUser
        mockMvc.perform(MockMvcRequestBuilders.post("/api/employees/{username}/addSupervisor/{supervisor}", "NewUser2", "Proistamenos")).andReturn();

        EmployeeProfileDTO employeeProfileDTO = new EmployeeProfileDTO();
        employeeProfileDTO = EmployeeProfileDTO.builder()
                .firstName("New")
                .lastName("User")
                .email("newUser@ots.gr")
                .mobileNumber("123566897")
                .address("Mon")
                .addressNumber(1)
                .build();

        String employeeDTO = objectMapper.writeValueAsString(employeeProfileDTO).toString();

        //updating employee profile
        mockMvc.perform(MockMvcRequestBuilders.put("/api/employees/profile").with(user("NewUser2").roles("EMPLOYEE"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(employeeDTO))
                .andExpect(status().isOk())
                .andReturn();


        //getting employee updated profile
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/api/employees/profile/{username}", newUser2.getUsername()).with(user("Proistamenos").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "application/json"))
                .andReturn();

        String jsonResponse = result.getResponse().getContentAsString();
        String firstname = new ObjectMapper().readTree(jsonResponse).get("firstName").asText();


        //asserting existence of Proistamenos(supervisor) in NewUser's supervisors list
        assertTrue(employeeH2TestRepository.findById("NewUser2").get().getSupervisors().get(0).getUsername().equals("Proistamenos"));
        assertEquals("New", firstname);

    }
    @DisplayName("#15 : delete User ")
    @WithMockUser(username = "Proistamenos", roles = {"ADMIN","MANAGER"}, password = "1234")
    @Test
    public void deleteAUser () throws Exception
    {
        UserDTO  newUser = UserDTO.builder().username("userToTest").password("123").build();


        ObjectMapper objectMapper = new ObjectMapper();
        String nUser = objectMapper.writeValueAsString(newUser).toString();

        //adding new user
        mockMvc.perform(MockMvcRequestBuilders.post("/api/employees/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(nUser)).andReturn();
        // deleting the user so the repo must
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/employees/delete/{username}",newUser.getUsername()))
                .andExpect(status().isOk()).andReturn();

        assertEquals(2,userH2TestRepository.count());

    }

}
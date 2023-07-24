package com.hrsystem.demo.service;

import com.hrsystem.demo.dto.UserDTO;


public interface UserService {

    UserDTO save(UserDTO userDTO);

    void delete(String username, String searcherUsername);



}

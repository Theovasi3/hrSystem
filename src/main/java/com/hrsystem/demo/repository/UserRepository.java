package com.hrsystem.demo.repository;

import com.hrsystem.demo.entity.Status;
import com.hrsystem.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, String> {

}

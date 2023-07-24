package com.hrsystem.demo;

import com.hrsystem.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserH2TestRepository extends JpaRepository<User,String> {
}

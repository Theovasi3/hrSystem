package com.hrsystem.demo;

import com.hrsystem.demo.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleH2TestRepository extends JpaRepository<Role,String> {
}

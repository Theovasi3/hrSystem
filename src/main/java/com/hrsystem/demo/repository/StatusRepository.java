package com.hrsystem.demo.repository;

import com.hrsystem.demo.entity.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface StatusRepository extends JpaRepository<Status, Integer> {

    @Query("FROM Status st WHERE st.state = 'PENDING'")
    public Status getPendingStateStatus();

    @Query("FROM Status st WHERE st.state = 'REJECTED'")
    public Status getRejectedStateStatus();
}
package com.hrsystem.demo.service;

import com.hrsystem.demo.repository.StatusRepository;
import com.hrsystem.demo.entity.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StatusServiceImpl {

    private StatusRepository statusRepository;

    @Autowired
    public StatusServiceImpl(StatusRepository statusRepository) {
        this.statusRepository = statusRepository;
    }

    public Status getPendingStateStatus(){
        return statusRepository.getPendingStateStatus();
    }

    public Status getRejectedStateStatus(){
        return statusRepository.getRejectedStateStatus();
    }

}

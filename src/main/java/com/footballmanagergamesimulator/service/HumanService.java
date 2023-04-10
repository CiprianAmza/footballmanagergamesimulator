package com.footballmanagergamesimulator.service;

import com.footballmanagergamesimulator.repository.HumanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class HumanService {

    @Autowired
    HumanRepository humanRepository;
}

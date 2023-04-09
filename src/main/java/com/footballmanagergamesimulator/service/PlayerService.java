package com.footballmanagergamesimulator.service;

import com.footballmanagergamesimulator.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PlayerService {

    @Autowired
    PlayerRepository playerRepository;
}

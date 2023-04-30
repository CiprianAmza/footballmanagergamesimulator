package com.footballmanagergamesimulator.service;

import com.footballmanagergamesimulator.model.Human;
import com.footballmanagergamesimulator.model.Round;
import com.footballmanagergamesimulator.model.Team;
import com.footballmanagergamesimulator.nameGenerator.NameGenerator;
import com.footballmanagergamesimulator.repository.HumanRepository;
import com.footballmanagergamesimulator.repository.TeamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
public class HumanService {

    @Autowired
    HumanRepository humanRepository;
    @Autowired
    TeamRepository _teamRepository;
    @Autowired
    Round _round;


    public void retirePlayers() {

      humanRepository
        .findAll()
        .stream()
        .filter(human -> human.getAge() > 35)
        .forEach(human -> humanRepository.delete(human));
    }

    public void addOneYearToAge() {

      humanRepository
        .findAll()
        .forEach(human -> human.setAge(human.getAge() + 1));
    }

    public void addRegens() {

      List<Team> allTeams = _teamRepository
        .findAll()
        .stream()
        .toList();

      Random random = new Random();
      for (Team team: allTeams) {
        for (int i = 0; i < random.nextInt(3, 6); i++) {
          humanRepository.save(generateHuman(team.getId()));
        }
      }
    }

    private Human generateHuman(long teamId) {

      Random random = new Random();

      Human human = new Human();
      human.setName(NameGenerator.generateName());
      human.setAge(random.nextInt(15, 19));
      human.setRating(random.nextInt(100, 200));
      human.setTeamId(teamId);
      human.setTypeId(1);
      human.setSeasonCreated(_round.getSeason());

      return human;
    }
}

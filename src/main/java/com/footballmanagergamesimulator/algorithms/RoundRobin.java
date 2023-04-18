package com.footballmanagergamesimulator.algorithms;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
public class RoundRobin {

  public List<List<List<Long>>> getSchedule(List<Long> teams) {

    List<List<List<Long>>> scheduleFirstLeg = new ArrayList<>();
    List<List<List<Long>>> scheduleSecondLeg = new ArrayList<>();

    for (int round = 1; round < teams.size(); round++) {
      List<List<Long>> curRound = new ArrayList<>();
      for (int i = 0; i < teams.size(); i += 2)
        curRound.add(List.of(teams.get(i), teams.get(i+1)));

      scheduleFirstLeg.add(curRound);

      Collections.reverse(curRound);
      scheduleSecondLeg.add(curRound);

      swapList(teams);
    }

    scheduleFirstLeg.addAll(scheduleSecondLeg);

    return scheduleFirstLeg;
  }

  private void swapList(List<Long> teams) {

    Long indexTwo = teams.get(1);
    for (int i = 2; i < teams.size(); i++)
      teams.set(i-1, teams.get(i));

    teams.set(teams.size() - 1, indexTwo);
  }
}

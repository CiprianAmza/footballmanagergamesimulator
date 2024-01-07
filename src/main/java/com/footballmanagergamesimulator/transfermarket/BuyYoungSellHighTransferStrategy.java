package com.footballmanagergamesimulator.transfermarket;

import com.footballmanagergamesimulator.model.Human;
import com.footballmanagergamesimulator.model.Team;
import com.footballmanagergamesimulator.repository.HumanRepository;
import com.footballmanagergamesimulator.util.TypeNames;

import java.util.*;
import java.util.stream.Collectors;

public class BuyYoungSellHighTransferStrategy implements TransferStrategy {

  @Override
  public List<PlayerTransferView> playersToSell(Team team, HumanRepository humanRepository, HashMap<String, Integer> minimumPositionNeeded) {

    HashMap<String, Integer> currentPositionAllocated = new HashMap<>();

    List<Human> players = humanRepository
      .findAllByTeamIdAndTypeId(team.getId(), TypeNames.HUMAN_TYPE)
      .stream()
      .sorted(Comparator.comparing(Human::getTransferValue).reversed())
      .toList();

    for (Human player : players)
      currentPositionAllocated.put(player.getPosition(), currentPositionAllocated.getOrDefault(player.getPosition(), 0) + 1);

    List<Human> validThatCouldBeSold = new ArrayList<>();
    for (Human player : players) {
      if (minimumPositionNeeded.getOrDefault(player.getPosition(), 0) < currentPositionAllocated.getOrDefault(player.getPosition(), 0)) {
        validThatCouldBeSold.add(player);
        currentPositionAllocated.put(player.getPosition(), currentPositionAllocated.get(player.getPosition()) - 1);
      }
    }

    List<Human> playersForSale =
      validThatCouldBeSold.subList(Math.max(validThatCouldBeSold.size() - new Random().nextInt(3, 6), 0), validThatCouldBeSold.size());

    return fromHumanToPlayerTransferView(team, playersForSale);
  }

  private List<PlayerTransferView> fromHumanToPlayerTransferView(Team team, List<Human> players) {

    return players.stream()
      .map(player -> new PlayerTransferView(player.getId(), team.getId(), player.getRating(), player.getPosition(), player.getAge()))
      .collect(Collectors.toList());
  }
}

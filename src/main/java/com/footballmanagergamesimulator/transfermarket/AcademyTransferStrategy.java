package com.footballmanagergamesimulator.transfermarket;

import com.footballmanagergamesimulator.model.Human;
import com.footballmanagergamesimulator.model.Team;
import com.footballmanagergamesimulator.repository.HumanRepository;

import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class AcademyTransferStrategy extends AbstractTransferStrategy {

    @Override
    public List<PlayerTransferView> playersToSell(Team team, HumanRepository humanRepository) {

      List<Human> players = humanRepository
        .findAllByTeamIdAndTypeId(team.getId(), 1L)
        .stream()
        .sorted(Comparator.comparing(Human::getRating).reversed())
        .collect(Collectors.toList());

      List<Human> playersForSale =
        players.subList(players.size() - new Random().nextInt(3, 6), players.size());

      return fromHumanToPlayerTransferView(team, playersForSale);
    }

    public List<PlayerTransferView> fromHumanToPlayerTransferView(Team team, List<Human> players) {

      return players.stream()
        .map(player -> new PlayerTransferView(player.getId(), team.getId(), player.getRating()))
        .collect(Collectors.toList());
    }
}

package com.footballmanagergamesimulator.transfermarket;

import com.footballmanagergamesimulator.model.Team;
import com.footballmanagergamesimulator.repository.HumanRepository;

import java.util.ArrayList;
import java.util.List;

public class BuyYoungSellHighTransferStrategy implements TransferStrategy {

  @Override
  public List<PlayerTransferView> playersToSell(Team team, HumanRepository humanRepository) {

    return new ArrayList<>();
  }
}

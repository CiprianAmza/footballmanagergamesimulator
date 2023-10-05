package com.footballmanagergamesimulator.transfermarket;

import com.footballmanagergamesimulator.model.Team;
import com.footballmanagergamesimulator.repository.HumanRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CompositeTransferStrategy implements TransferStrategy {

  private final Map<Long, TransferStrategy> _transferStrategies = new HashMap<>();

  @PostConstruct
  public void init() {

    _transferStrategies.put(TransferStrategyUtil.TRANSFER_STRATEGY_ACADEMY, new AcademyTransferStrategy());
    _transferStrategies.put(TransferStrategyUtil.TRANSFER_STRATEGY_BUY_YOUNG_SELL_HIGH, new BuyYoungSellHighTransferStrategy());
  }

  @Override
  public List<PlayerTransferView> playersToSell(Team team, HumanRepository humanRepository) {

    TransferStrategy transferStrategy = _transferStrategies.get(team.getStrategy());

    if (transferStrategy == null) // if there is no available strategy
      return new ArrayList<>(); // then no players will be sold

    return transferStrategy.playersToSell(team, humanRepository);
  }
}

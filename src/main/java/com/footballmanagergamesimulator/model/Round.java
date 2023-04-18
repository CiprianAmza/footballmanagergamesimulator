package com.footballmanagergamesimulator.model;

import org.springframework.stereotype.Component;

@Component
public class Round {
  public long round = 1L;

  public long getRound() {
    return round;
  }

  public void setRound(long round) {
    this.round = round;
  }
}

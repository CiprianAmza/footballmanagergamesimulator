package com.footballmanagergamesimulator.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class TeamCompetitionDetail {

  @Id
  @GeneratedValue(strategy= GenerationType.IDENTITY)
  private long id;

  /**
   * Relation ids
   */
  private long teamId;
  private long competitionId;

  /**
   * TeamStats in competition
   */

  private int games;
  private int wins;
  private int draws;
  private int loses;
  private int goalsFor;
  private int goalsAgainst;
  private int goalDifference;
  private int points;
  private String form;
  private String last10Positions;
}

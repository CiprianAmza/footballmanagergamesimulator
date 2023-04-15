package com.footballmanagergamesimulator.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class MatchSquad {

  @Id
  @GeneratedValue(strategy= GenerationType.IDENTITY)
  private long id;

  /**
   * Relation ids
   */
  private long teamId;

  /**
   * 18 positions (11 first team positions + 7 substitues)
   */
  private long position1;
  private long position2;
  private long position3;
  private long position4;
  private long position5;
  private long position6;
  private long position7;
  private long position8;
  private long position9;
  private long position10;
  private long position11;
  private long position12;
  private long position13;
  private long position14;
  private long position15;
  private long position16;
  private long position17;
  private long position18;



}

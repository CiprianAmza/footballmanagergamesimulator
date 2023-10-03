package com.footballmanagergamesimulator.transfermarket;

public class PlayerTransferView {

  private long playerId;
  private long teamId;
  private double rating;

  public PlayerTransferView(long playerId, long teamId, double rating) {
    this.playerId = playerId;
    this.teamId = teamId;
    this.rating = rating;
  }

  public long getPlayerId() {
    return playerId;
  }

  public void setPlayerId(long playerId) {
    this.playerId = playerId;
  }

  public long getTeamId() {
    return teamId;
  }

  public void setTeamId(long teamId) {
    this.teamId = teamId;
  }

  public double getRating() {
    return rating;
  }

  public void setRating(double rating) {
    this.rating = rating;
  }
}

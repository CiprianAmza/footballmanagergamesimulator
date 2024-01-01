package com.footballmanagergamesimulator.transfermarket;

public class PlayerTransferView {

  private long playerId;
  private long teamId;
  private double rating;
  private String position;

  public PlayerTransferView(long playerId, long teamId, double rating, String position) {
    this.playerId = playerId;
    this.teamId = teamId;
    this.rating = rating;
    this.position = position;
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

  public String getPosition() {
    return position;
  }

  public void setPosition(String position) {
    this.position = position;
  }
}

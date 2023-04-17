package com.footballmanagergamesimulator.controller;

import com.footballmanagergamesimulator.frontend.TeamCompetitionView;
import com.footballmanagergamesimulator.model.*;
import com.footballmanagergamesimulator.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/competition")
@CrossOrigin(origins = "http://localhost:4200")
public class CompetitionController {

  @Autowired
  private TeamRepository teamRepository;
  @Autowired
  private TeamCompetitionRelationRepository teamCompetitionRelationRepository;
  @Autowired
  private TeamCompetitionDetailRepository teamCompetitionDetailRepository;
  @Autowired
  private CompetitionTeamInfoRepository competitionTeamInfoRepository;

  @Autowired
  private CompetitionTeamInfoDetailRepository competitionTeamInfoDetailRepository;


  @GetMapping("/getTeams/{competitionId}")
  public List<TeamCompetitionView> getTeamDetails(@PathVariable(name = "competitionId") long competitionId) {

    List<Long> teamParticipantIds = teamCompetitionRelationRepository
                                    .findAll()
                                    .stream()
                                    .filter(teamCompetitionRelation -> teamCompetitionRelation.getCompetitionId() == competitionId)
                                    .mapToLong(TeamCompetitionRelation::getTeamId)
                                    .boxed()
                                    .collect(Collectors.toList());

    List<TeamCompetitionView> teamCompetitionViews = new ArrayList<>();

    for (Long teamId: teamParticipantIds) {
      TeamCompetitionDetail teamCompetitionDetail = teamCompetitionDetailRepository.findByTeamIdAndCompetitionId(teamId, competitionId);
      Team team = teamRepository.findById(teamId).orElseGet(null);

      if (team == null || teamCompetitionDetail == null)
        throw new RuntimeException("No team found for competitionId " + competitionId + " and teamId " + teamId);

      TeamCompetitionView teamCompetitionView = adaptTeam(team, teamCompetitionDetail);
      teamCompetitionViews.add(teamCompetitionView);
    }

    return teamCompetitionViews;
  }

  private TeamCompetitionView adaptTeam(Team team, TeamCompetitionDetail teamCompetitionDetail) {

    TeamCompetitionView teamCompetitionView = new TeamCompetitionView();

    // Team information
    teamCompetitionView.setName(team.getName());
    teamCompetitionView.setColor1(team.getColor1());
    teamCompetitionView.setColor2(team.getColor2());
    teamCompetitionView.setBorder(team.getBorder());

    // TeamCompetitionDetail
    teamCompetitionView.setGames(String.valueOf(teamCompetitionDetail.getGames()));
    teamCompetitionView.setWins(String.valueOf(teamCompetitionDetail.getWins()));
    teamCompetitionView.setDraws(String.valueOf(teamCompetitionDetail.getDraws()));
    teamCompetitionView.setLoses(String.valueOf(teamCompetitionDetail.getLoses()));
    teamCompetitionView.setGoalsFor(String.valueOf(teamCompetitionDetail.getGoalsFor()));
    teamCompetitionView.setGoalsAgainst(String.valueOf(teamCompetitionDetail.getGoalsAgainst()));
    teamCompetitionView.setGoalDifference(String.valueOf(teamCompetitionDetail.getGoalDifference()));
    teamCompetitionView.setPoints(String.valueOf(teamCompetitionDetail.getPoints()));
    teamCompetitionView.setForm(teamCompetitionDetail.getForm());
    teamCompetitionView.setPositions(teamCompetitionDetail.getLast10Positions());

    return teamCompetitionView;
  }

  @GetMapping("playRound/{competitionId}/{roundId}")
  public void playRound(@RequestParam(name = "competitionId") String competitionId, @RequestParam(name = "roundId") String roundId) {

    long _competitionId = Long.parseLong(competitionId);
    long _roundId = Long.parseLong(roundId);
    long nextRound = getNextRound(_roundId);
    if (nextRound == -1)
      throw new RuntimeException("No more rounds");


    Random random = new Random();

    List<Long> getParticipants = competitionTeamInfoRepository
      .findAllByRound(_roundId)
      .stream()
      .mapToLong(CompetitionTeamInfo::getTeamId)
      .boxed()
      .collect(Collectors.toList());

    for (int i = 0; i < getParticipants.size(); i += 2) {
      long teamId1 = getParticipants.get(i);
      long teamId2 = getParticipants.get(i+1);

      long teamScore1 = random.nextLong(5);
      long teamScore2 = random.nextLong(5);
      while (teamScore2 == teamScore1)
        teamScore2 = random.nextLong(5);

      CompetitionTeamInfo competitionTeamInfo = new CompetitionTeamInfo();
      competitionTeamInfo.setCompetitionId(_competitionId);
      competitionTeamInfo.setRound(nextRound);

      competitionTeamInfo.setTeamId(teamScore1 > teamScore2 ? teamId1 : teamId2);
      competitionTeamInfoRepository.save(competitionTeamInfo);

      CompetitionTeamInfoDetail competitionTeamInfoDetail = new CompetitionTeamInfoDetail();
      competitionTeamInfoDetail.setCompetitionId(_competitionId);
      competitionTeamInfoDetail.setRoundId(_roundId);
      competitionTeamInfoDetail.setTeam1Id(teamId1);
      competitionTeamInfoDetail.setTeam2Id(teamId2);
      competitionTeamInfoDetail.setScore(teamScore1 + " - " + teamScore2);
      competitionTeamInfoDetailRepository.save(competitionTeamInfoDetail);
    }

  }

  private long getNextRound(long previousRound) {

    List<Long> rounds = List.of(1L, 2L, 3L, 4L);
    for (int i = 0; i < rounds.size(); i++)
      if (rounds.get(i) == previousRound)
        return i == rounds.size() - 1 ? -1 : rounds.get(i+1);

    return -1;
  }


  @GetMapping("getResults/{competitionId}/{roundId}")
  public List<CompetitionTeamInfoDetail> getResults(@RequestParam(name = "competitionId") String competitionId, @RequestParam(name = "roundId") String roundId) {

    long _competitionId = Long.parseLong(competitionId);
    long _roundId = Long.parseLong(roundId);

    List<CompetitionTeamInfoDetail> competitionTeamInfoDetails =
      competitionTeamInfoDetailRepository
        .findAll()
        .stream()
        .filter(competitionTeamInfoDetail -> competitionTeamInfoDetail.getRoundId() == _roundId)
        .filter(competitionTeamInfoDetail -> competitionTeamInfoDetail.getCompetitionId() == _competitionId)
        .toList();

    return competitionTeamInfoDetails;

  }
}

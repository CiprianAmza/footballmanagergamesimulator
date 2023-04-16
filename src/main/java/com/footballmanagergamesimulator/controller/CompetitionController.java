package com.footballmanagergamesimulator.controller;

import com.footballmanagergamesimulator.frontend.TeamCompetitionView;
import com.footballmanagergamesimulator.model.Team;
import com.footballmanagergamesimulator.model.TeamCompetitionDetail;
import com.footballmanagergamesimulator.model.TeamCompetitionRelation;
import com.footballmanagergamesimulator.repository.HumanRepository;
import com.footballmanagergamesimulator.repository.TeamCompetitionDetailRepository;
import com.footballmanagergamesimulator.repository.TeamCompetitionRelationRepository;
import com.footballmanagergamesimulator.repository.TeamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/competition")
@CrossOrigin(origins = "http://localhost:4200")
public class CompetitionController {

  @Autowired
  private HumanRepository humanRepository;
  @Autowired
  private TeamRepository teamRepository;
  @Autowired
  private TeamCompetitionRelationRepository teamCompetitionRelationRepository;
  @Autowired
  private TeamCompetitionDetailRepository teamCompetitionDetailRepository;


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

}

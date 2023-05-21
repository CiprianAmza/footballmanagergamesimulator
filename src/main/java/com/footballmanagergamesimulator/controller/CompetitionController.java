package com.footballmanagergamesimulator.controller;

import com.footballmanagergamesimulator.algorithms.RoundRobin;
import com.footballmanagergamesimulator.frontend.PlayerView;
import com.footballmanagergamesimulator.frontend.TeamCompetitionView;
import com.footballmanagergamesimulator.frontend.TeamMatchView;
import com.footballmanagergamesimulator.model.*;
import com.footballmanagergamesimulator.nameGenerator.NameGenerator;
import com.footballmanagergamesimulator.repository.*;
import com.footballmanagergamesimulator.service.HumanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.scheduling.annotation.Scheduled;
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
  private HumanRepository humanRepository;
  @Autowired
  private TeamCompetitionRelationRepository teamCompetitionRelationRepository;
  @Autowired
  private TeamCompetitionDetailRepository teamCompetitionDetailRepository;
  @Autowired
  private CompetitionTeamInfoRepository competitionTeamInfoRepository;
  @Autowired
  private CompetitionTeamInfoDetailRepository competitionTeamInfoDetailRepository;
  @Autowired
  private CompetitionTeamInfoMatchRepository competitionTeamInfoMatchRepository;
  @Autowired
  Round round;
  @Autowired
  RoundRobin roundRobin;
  @Autowired
  CompetitionHistoryRepository competitionHistoryRepository;
  @Autowired
  CompetitionRepository competitionRepository;
  @Autowired
  HumanService _humanService;
  @Autowired
  TeamFacilitiesRepository _teamFacilitiesRepository;

  @GetMapping("/getBestEleven/{teamId}")
  private List<PlayerView> getBestEleven(@PathVariable(name = "teamId") String teamId){

    long _teamId = Long.valueOf(teamId);

    Team team = teamRepository.findById(_teamId).orElse(null);
    if (team == null)
      throw new RuntimeException("Team not found.");

    List<Human> getBest11 = getBestElevenPlayers(team);

    List<PlayerView> bestEleven =  getBest11
      .stream()
      .map(player -> adaptPlayer(player, team))
      .toList();

    return bestEleven;
  }

  private List<Human> getBestElevenPlayers(Team team) {

    return humanRepository
      .findAllByTeamIdAndTypeId(team.getId(), 1L)
      .stream()
      .sorted((x, y) -> Double.compare(y.getRating(), x.getRating()))
      .limit(11)
      .toList();
  }

  private PlayerView adaptPlayer(Human human, Team team) {

    PlayerView playerView = new PlayerView();
    playerView.setAge(human.getAge());
    playerView.setPosition(human.getPosition());
    playerView.setRating(human.getRating());
    playerView.setName(human.getName());
    playerView.setTeamName(team.getName());

    return playerView;
  }

  @GetMapping("/getTeamTotalSkills/{competitionId}")
  private List<Pair<String, Double>> getTeamTotalSkills(@PathVariable(name = "competitionId") String competitionId) {

    long _competitionId = Long.valueOf(competitionId);

    List<Pair<String, Double>> teamTotalSkills = new ArrayList<>();
    for (Team team: teamRepository.findAllByCompetitionId(_competitionId)) {
      double totalSkill = getTotalTeamSkill(team.getId());
      Pair<String, Double> pair = Pair.of(team.getName(), totalSkill);
      teamTotalSkills.add(pair);
    }

    return teamTotalSkills
      .stream()
      .sorted((x, y) -> Double.compare(y.getValue(), x.getValue()))
      .toList();
  }

  @GetMapping("/getCurrentSeason")
  public String getCurrentSeason() {

    return String.valueOf(round.getSeason());
  }

  @GetMapping("/getCurrentRound")
  public String getCurrentRound() {

    return String.valueOf(round.getRound() - 1);
  }

  @GetMapping("/play")
  @Scheduled(fixedDelay = 3000)
  public void play() {

    List<Long> teamIds = List.of(1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 10L, 11L, 12L);

    if (round.getRound() > 50) {
      // season reset

      // transfer when the season begins
      // there are 5 team strategies: Academy, Buy Young/Sell High, Buy Free Only, Buy Best Players(Young Only), Buy Best Players(Any Age)






      // save historical values
      Set<Long> competitions = competitionRepository.findAll()
        .stream()
          .mapToLong(Competition::getId)
            .boxed().collect(Collectors.toSet());

      for (Long competitionId: competitions)
        this.saveHistoricalValues(competitionId, round.getSeason());

      // reset values
      this.resetCompetitionData();
      for (Long competitionId: competitions)
        this.removeCompetitionData(competitionId, round.getSeason());
      round.setRound(1);
      round.setSeason(round.getSeason() + 1);

      // add 1 year for each player
      _humanService.addOneYearToAge();
      _humanService.retirePlayers();

      for (Long teamId: teamIds) {
        TeamFacilities teamFacilities = _teamFacilitiesRepository.findByTeamId(teamId);
        _humanService.addRegens(teamFacilities, teamId);
      }

      // reinitialize CompetitionTeamInfo

      for (Long teamId: teamIds) {
        // championship
        CompetitionTeamInfo competitionTeamInfo = new CompetitionTeamInfo();
        competitionTeamInfo.setCompetitionId(1L);
        competitionTeamInfo.setRound(1);
        competitionTeamInfo.setTeamId(teamId);
        competitionTeamInfoRepository.save(competitionTeamInfo);

        // cup
        CompetitionTeamInfo cup = new CompetitionTeamInfo();
        cup.setCompetitionId(2L);
        cup.setRound(teamId < 5 ? 2: 1);
        cup.setTeamId(teamId);
        competitionTeamInfoRepository.save(cup);

      }

      for (long teamId: teamIds) {
        List<Human> players = humanRepository.findAllByTeamIdAndTypeId(teamId, 1L);
        for (Human human: players) {
          long seasonCreated = human.getSeasonCreated();
          if (round.getSeason() - seasonCreated <= 2 && seasonCreated != 1L)
            human.setCurrentStatus("Junior");
          else if (round.getSeason() - seasonCreated <= 6 && seasonCreated != 1L)
            human.setCurrentStatus("Intermediate");
          else
            human.setCurrentStatus("Senior");
          humanRepository.save(human);
        }
      }


    }

    if (round.getRound() == 1) {

      this.getFixturesForRound("1", "1");

      if (round.getSeason() == 1) {
        List<Team> teams = teamRepository.findAll();
        Random random = new Random();
        for (Team team: teams) {
          TeamFacilities teamFacilities = _teamFacilitiesRepository.findByTeamId(team.getId());
          int nrPlayers = random.nextInt(18, 23);
          for (int i = 0; i < nrPlayers; i++) {
            String name = NameGenerator.generateName();
            Human player = new Human();
            player.setTeamId(team.getId());
            player.setName(name);
            player.setTypeId(1L);
            player.setAge(random.nextInt(23, 30));
            player.setSeasonCreated(1L);
            player.setCurrentStatus("Senior");

            int reputation = (int) teamFacilities.getSeniorTrainingLevel() * 10;
            player.setRating(random.nextInt(reputation - 20, reputation + 20));
            humanRepository.save(player);
          }
        }
      }

    }

    if (round.getRound() % 3 == 0) {
      for (long teamId: teamIds) {
        TeamFacilities teamFacilities = _teamFacilitiesRepository.findByTeamId(teamId);
        List<Human> players = humanRepository.findAllByTeamIdAndTypeId(teamId, 1L);
        for (Human player: players) {
          player = _humanService.trainPlayer(player, teamFacilities);
          humanRepository.save(player);
        }

      }
    }

    this.simulateRound("1", round.getRound() - 1 + "");

    if (round.getRound() == 5) {
      this.getFixturesForRound("2", "1");
      this.simulateRound("2", "1");
    }
    if (round.getRound() == 10) {
      this.getFixturesForRound("2", "2");
      this.simulateRound("2", "2");
    }
    if (round.getRound() == 20) {
      this.getFixturesForRound("2", "3");
      this.simulateRound("2", "3");
    }
    if (round.getRound() == 35) {
      this.getFixturesForRound("2", "4");
      this.simulateRound("2", "4");
    }
    round.setRound(round.getRound() + 1);
  }

  public void removeCompetitionData(Long competitionId, Long seasonNumber) {

    List<TeamCompetitionDetail> teamCompetitionDetails = teamCompetitionDetailRepository.findAll();

    for (TeamCompetitionDetail team: teamCompetitionDetails) {

      TeamCompetitionDetail newTeam = new TeamCompetitionDetail();
      newTeam.setTeamId(team.getTeamId());
      newTeam.setCompetitionId(team.getCompetitionId());
      teamCompetitionDetailRepository.delete(team);
      teamCompetitionDetailRepository.save(newTeam);
    }
  }

  public void resetCompetitionData() {

    competitionTeamInfoDetailRepository.deleteAll();
    competitionTeamInfoMatchRepository.deleteAll();
    competitionTeamInfoRepository.deleteAll();

  }

  public void saveHistoricalValues(Long competitionId, Long seasonNumber) {

    List<TeamCompetitionDetail> teams = teamCompetitionDetailRepository.findAll()
      .stream()
      .filter(teamCompetitionDetail -> teamCompetitionDetail.getCompetitionId() == competitionId)
      .collect(Collectors.toList());

    Collections.sort(teams, (a, b) -> {
      int pointsA = a.getPoints();
      int pointsB = b.getPoints();
      if (pointsA != pointsB)
        return pointsA > pointsB ? -1 : 1;

      int gdA = a.getGoalDifference();
      int gdB = b.getGoalDifference();
      if (gdA != gdB)
        return gdA > gdB ? -1 : 1;

      return a.getGoalsFor() > b.getGoalsFor() ? -1 : 1;
    });

    for (int i = 0; i < teams.size(); i++) {
      TeamCompetitionDetail team = teams.get(i);
      if (team.getCompetitionId() != competitionId)
        continue;
      competitionHistoryRepository.save(this.adaptCompetitionHistory(team, seasonNumber, 1 + i));
    }
  }

  private CompetitionHistory adaptCompetitionHistory(TeamCompetitionDetail team, Long seasonNumber, long position) {

    CompetitionHistory competitionHistory = new CompetitionHistory();

    competitionHistory.setSeasonNumber(seasonNumber);
    competitionHistory.setLastPosition(position);
    competitionHistory.setCompetitionId(team.getCompetitionId());
    competitionHistory.setTeamId(team.getTeamId());
    competitionHistory.setGames(team.getGames());
    competitionHistory.setWins(team.getWins());
    competitionHistory.setDraws(team.getDraws());
    competitionHistory.setLoses(team.getLoses());
    competitionHistory.setGoalsFor(team.getGoalsFor());
    competitionHistory.setGoalsAgainst(team.getGoalsAgainst());
    competitionHistory.setGoalDifference(team.getGoalDifference());
    competitionHistory.setPoints(team.getPoints());
    competitionHistory.setForm(team.getForm());

    return competitionHistory;
  }

  @GetMapping("/historical/getTeams/{seasonNumber}/{competitionId}")
  public List<TeamCompetitionView> getHistoricalTeamDetails(@PathVariable(name = "competitionId") long competitionId, @PathVariable(name = "seasonNumber") long seasonNumber) {

    List<CompetitionHistory> teamParticipants = competitionHistoryRepository
      .findAll()
      .stream()
      .filter(competitionHistory -> competitionHistory.getCompetitionId() == competitionId && competitionHistory.getSeasonNumber() == seasonNumber)
      .collect(Collectors.toList());

    List<TeamCompetitionView> teamCompetitionViews = new ArrayList<>();

    for (CompetitionHistory competitionHistory: teamParticipants)
      teamCompetitionViews.add(adaptTeam(teamRepository.findById(competitionHistory.getTeamId()).orElse(new Team()), competitionHistory));

    return teamCompetitionViews;
  }

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
      TeamCompetitionDetail teamCompetitionDetail = teamCompetitionDetailRepository.findTeamCompetitionDetailByTeamIdAndCompetitionId(teamId, competitionId);
      Team team = teamRepository.findById(teamId).orElseGet(null);

      if (team == null || teamCompetitionDetail == null)
        throw new RuntimeException("No team found for competitionId " + competitionId + " and teamId " + teamId);

      TeamCompetitionView teamCompetitionView = adaptTeam(team, teamCompetitionDetail);
      teamCompetitionViews.add(teamCompetitionView);
    }

    return teamCompetitionViews;
  }

  private TeamCompetitionView adaptTeam(Team team, CompetitionHistory teamCompetitionHistory) {

    TeamCompetitionView teamCompetitionView = new TeamCompetitionView();

    // Team information
    teamCompetitionView.setName(team.getName());
    teamCompetitionView.setColor1(team.getColor1());
    teamCompetitionView.setColor2(team.getColor2());
    teamCompetitionView.setBorder(team.getBorder());

    // TeamCompetitionDetail
    teamCompetitionView.setGames(String.valueOf(teamCompetitionHistory.getGames()));
    teamCompetitionView.setWins(String.valueOf(teamCompetitionHistory.getWins()));
    teamCompetitionView.setDraws(String.valueOf(teamCompetitionHistory.getDraws()));
    teamCompetitionView.setLoses(String.valueOf(teamCompetitionHistory.getLoses()));
    teamCompetitionView.setGoalsFor(String.valueOf(teamCompetitionHistory.getGoalsFor()));
    teamCompetitionView.setGoalsAgainst(String.valueOf(teamCompetitionHistory.getGoalsAgainst()));
    teamCompetitionView.setGoalDifference(String.valueOf(teamCompetitionHistory.getGoalDifference()));
    teamCompetitionView.setPoints(String.valueOf(teamCompetitionHistory.getPoints()));
    teamCompetitionView.setForm(teamCompetitionHistory.getForm());

    return teamCompetitionView;
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

  private long getNextRound(long previousRound) {

    List<Long> rounds = List.of(1L, 2L, 3L, 4L);
    for (int i = 0; i < rounds.size(); i++)
      if (rounds.get(i) == previousRound)
        return i == rounds.size() - 1 ? -1 : rounds.get(i+1);

    return -1;
  }

  @GetMapping("getResults/{competitionId}/{roundId}")
  public List<CompetitionTeamInfoDetail> getResults(@PathVariable(name = "competitionId") String competitionId, @PathVariable(name = "roundId") String roundId) {

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

  @GetMapping("getParticipants/{competitionId}/{roundId}")
  public List<Long> getParticipants(@PathVariable(name = "competitionId") String competitionId, @PathVariable(name = "roundId") String roundId) {

    long _competitionId = Long.parseLong(competitionId);
    long _roundId = Long.parseLong(roundId);

    List<CompetitionTeamInfo> competitionTeamInfos = competitionTeamInfoRepository
      .findAllByRoundAndCompetitionId(_roundId, _competitionId);

    List<Long> participants = competitionTeamInfos
      .stream()
      .mapToLong(CompetitionTeamInfo::getTeamId)
      .boxed()
      .collect(Collectors.toList());

    return participants;
  }

  @GetMapping("getFuturesMatches/{competitionId}/{roundId}")
  public List<TeamMatchView> getNotPlayedMatches(@PathVariable(name = "competitionId") String competitionId, @PathVariable(name = "roundId") String roundId) {

    long _competitionId = Long.parseLong(competitionId);
    long _roundId = Long.parseLong(roundId);

    List<CompetitionTeamInfoMatch> futureMatches =
      competitionTeamInfoMatchRepository
        .findAll()
        .stream()
        .filter(competitionTeamInfoMatch -> competitionTeamInfoMatch.getCompetitionId() == _competitionId && competitionTeamInfoMatch.getRound() == _roundId)
        .toList();

    List<TeamMatchView> matchViews = new ArrayList<>();

    for (CompetitionTeamInfoMatch match: futureMatches)
      matchViews.add(adaptCompetitionTeamInfoMatch(match, _competitionId, _roundId));

    return matchViews;
  }

  private TeamMatchView adaptCompetitionTeamInfoMatch(CompetitionTeamInfoMatch match, long competitionId, long roundId) {

    TeamMatchView teamMatchView = new TeamMatchView();
    teamMatchView.setTeamName1(teamRepository.findById(match.getTeam1Id()).get().getName());
    teamMatchView.setTeamName2(teamRepository.findById(match.getTeam2Id()).get().getName());

    CompetitionTeamInfoDetail matchDetail = competitionTeamInfoDetailRepository.findCompetitionTeamInfoDetailByCompetitionIdAndRoundIdAndTeam1IdAndTeam2Id(competitionId, roundId, match.getTeam1Id(), match.getTeam2Id());
    if (matchDetail != null)
      teamMatchView.setScore(matchDetail.getScore());
    else
      teamMatchView.setScore("-");

    return teamMatchView;
  }

  @GetMapping("getFixtures/{competitionId}/{roundId}")
  public void getFixturesForRound(@PathVariable(name = "competitionId") String competitionId, @PathVariable(name = "roundId") String roundId) {

    long _competitionId = Long.parseLong(competitionId);
    long _roundId = Long.parseLong(roundId);

    List<Long> participants = this.getParticipants(competitionId, roundId);

    // we need to get matches and to add them into CompetitionTeamInfoMatch
    if (_competitionId == 1L) {
      List<List<List<Long>>> schedule = roundRobin.getSchedule(participants);
      int currentRound = 1;

      for (int i = 0; i < 2; i++) {

        for (List<List<Long>> round: schedule) {

          for (List<Long> match : round) {
            long teamHomeId = match.get(0);
            long teamAwayId = match.get(1);

            CompetitionTeamInfoMatch competitionTeamInfoMatch = new CompetitionTeamInfoMatch();
            competitionTeamInfoMatch.setCompetitionId(_competitionId);
            competitionTeamInfoMatch.setRound(currentRound);
            competitionTeamInfoMatch.setTeam1Id(teamHomeId);
            competitionTeamInfoMatch.setTeam2Id(teamAwayId);
            competitionTeamInfoMatchRepository.save(competitionTeamInfoMatch);
          }
          currentRound++;
        }
      }


    } else {

      Collections.shuffle(participants);
      for (int i = 0; i < participants.size(); i+=2) {
        long teamHomeId = participants.get(i);
        long teamAwayId = participants.get(i+1);

        CompetitionTeamInfoMatch competitionTeamInfoMatch = new CompetitionTeamInfoMatch();
        competitionTeamInfoMatch.setCompetitionId(_competitionId);
        competitionTeamInfoMatch.setRound(_roundId);
        competitionTeamInfoMatch.setTeam1Id(teamHomeId);
        competitionTeamInfoMatch.setTeam2Id(teamAwayId);
        competitionTeamInfoMatchRepository.save(competitionTeamInfoMatch);
      }
    }
  }

  @GetMapping("simulateRound/{competitionId}/{roundId}")
  public void simulateRound(@PathVariable(name = "competitionId") String competitionId, @PathVariable(name = "roundId") String roundId) {

    long _competitionId = Long.parseLong(competitionId);
    long _roundId = Long.parseLong(roundId);
    long nextRound = getNextRound(_roundId);


    Random random = new Random();
    List<CompetitionTeamInfoMatch> matches = competitionTeamInfoMatchRepository
      .findAll();

    matches = matches.stream().filter(x -> x.getRound() == _roundId && x.getCompetitionId() == _competitionId).toList();

    for (CompetitionTeamInfoMatch match : matches) {
      long teamId1 = match.getTeam1Id();
      long teamId2 = match.getTeam2Id();

      long teamReputation1 = teamRepository.findById(teamId1).get().getReputation();
      long teamReputation2 = teamRepository.findById(teamId2).get().getReputation();

      int teamScore1, teamScore2;

      double teamPower1 = getTotalTeamSkill(teamId1);
      double teamPower2 = getTotalTeamSkill(teamId2);

      List<Integer> limits = calculateLimits(teamPower1, teamPower2);
      int limitA = limits.get(0);
      int limitB = limits.get(1);


      teamScore1 = random.nextInt(limitA);
      teamScore2 = random.nextInt(limitB);


      if (_competitionId == 2L) {
        while (teamScore2 == teamScore1)
          teamScore2 = random.nextInt(5);
      }

      updateTeam(teamId1, _competitionId, teamScore1, teamScore2);
      updateTeam(teamId2, _competitionId, teamScore2, teamScore1);


      if (nextRound != -1 && _competitionId == 2L) {
        CompetitionTeamInfo competitionTeamInfo = new CompetitionTeamInfo();
        competitionTeamInfo.setCompetitionId(_competitionId);
        competitionTeamInfo.setRound(nextRound);

        competitionTeamInfo.setTeamId(teamScore1 > teamScore2 ? teamId1 : teamId2);
        competitionTeamInfoRepository.save(competitionTeamInfo);
      }


      CompetitionTeamInfoDetail competitionTeamInfoDetail = new CompetitionTeamInfoDetail();
      competitionTeamInfoDetail.setCompetitionId(_competitionId);
      competitionTeamInfoDetail.setRoundId(_roundId);
      competitionTeamInfoDetail.setTeam1Id(teamId1);
      competitionTeamInfoDetail.setTeam2Id(teamId2);
      competitionTeamInfoDetail.setTeamName1(teamRepository.findById(teamId1).get().getName());
      competitionTeamInfoDetail.setTeamName2(teamRepository.findById(teamId2).get().getName());
      competitionTeamInfoDetail.setScore(teamScore1 + " - " + teamScore2);
      competitionTeamInfoDetailRepository.save(competitionTeamInfoDetail);
    }

  }

  private void updateTeam(long teamId, long competitionId, int scoreHome, int scoreAway) {

    TeamCompetitionDetail team = teamCompetitionDetailRepository.findTeamCompetitionDetailByTeamIdAndCompetitionId(teamId, competitionId);
    if (team == null) {
      team = new TeamCompetitionDetail();
      team.setId(teamId);
    }

    team.setCompetitionId(competitionId);
    team.setGoalsFor(team.getGoalsFor() + scoreHome);
    team.setGoalsAgainst(team.getGoalsAgainst() + scoreAway);
    team.setGoalDifference(team.getGoalsFor() - team.getGoalsAgainst());
    if (scoreHome > scoreAway) {
      team.setForm(team.getForm() + "W");
      team.setWins(team.getWins() + 1);
      team.setPoints(team.getPoints() + 3);
    }
    else if (scoreHome == scoreAway){
      team.setForm(team.getForm() + "D");
      team.setDraws(team.getDraws() + 1);
      team.setPoints(team.getPoints() + 1);
    }
    else {
      team.setForm(team.getForm() + "L");
      team.setLoses(team.getLoses() + 1);
    }
    team.setGames(team.getGames() + 1);

    teamCompetitionDetailRepository.save(team);
  }

  private double getTotalTeamSkill(long teamId) {

    return humanRepository
      .findAll()
      .stream()
      .filter(human -> human.getTeamId() == teamId)
      .map(Human::getRating)
      .sorted((a, b) -> Double.compare(b, a))
      .limit(11)
      .reduce(Double::sum).orElse(0D);

  }

  private List<Integer> calculateLimits(double power1, double power2) {

    int limitA = (int) (2 + (Math.abs(power1 - power2)) / 100);
    int limitB = 2;

    return power1 >= power2 ? List.of(limitA, limitB) : List.of(limitB, limitA);

  }
}

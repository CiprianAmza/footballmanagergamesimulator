package com.footballmanagergamesimulator.controller;

import com.footballmanagergamesimulator.algorithms.RoundRobin;
import com.footballmanagergamesimulator.frontend.PlayerView;
import com.footballmanagergamesimulator.frontend.TeamCompetitionView;
import com.footballmanagergamesimulator.frontend.TeamMatchView;
import com.footballmanagergamesimulator.model.*;
import com.footballmanagergamesimulator.nameGenerator.NameGenerator;
import com.footballmanagergamesimulator.repository.*;
import com.footballmanagergamesimulator.service.HumanService;
import com.footballmanagergamesimulator.transfermarket.CompositeTransferStrategy;
import com.footballmanagergamesimulator.transfermarket.PlayerTransferView;
import com.footballmanagergamesimulator.util.TypeNames;
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

  @Autowired
  CompositeTransferStrategy _compositeTransferStrategy;


  @GetMapping("/getPlayers/{teamId}")
  private List<PlayerView> getPlayers(@PathVariable(name = "teamId") String teamId){

    long _teamId = Long.parseLong(teamId);

    Team team = teamRepository.findById(_teamId).orElse(null);
    if (team == null)
      throw new RuntimeException("Team not found.");

    List<Human> getAllPlayers = humanRepository.findAllByTeamIdAndTypeId(Long.parseLong(teamId), TypeNames.HUMAN_TYPE);

    List<PlayerView> allPlayers =  getAllPlayers
      .stream()
      .map(player -> adaptPlayer(player, team))
            .collect(Collectors.toList());

    return allPlayers;
  }
  @GetMapping("/getBestEleven/{teamId}")
  private List<PlayerView> getBestEleven(@PathVariable(name = "teamId") String teamId){

    long _teamId = Long.parseLong(teamId);

    Team team = teamRepository.findById(_teamId).orElse(null);
    if (team == null)
      throw new RuntimeException("Team not found.");

    List<Human> getBest11 = getBestElevenPlayers(team);

    List<PlayerView> bestEleven =  getBest11
      .stream()
      .map(player -> adaptPlayer(player, team))
      .collect(Collectors.toList());

    return bestEleven;
  }

  private List<Human> getBestElevenPlayers(Team team) {
    
    return humanRepository
      .findAllByTeamIdAndTypeId(team.getId(), 1L)
      .stream()
      .sorted((x, y) -> Double.compare(y.getRating(), x.getRating()))
      .limit(11)
      .collect(Collectors.toList());
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

    long _competitionId = Long.parseLong(competitionId);

    List<Pair<String, Double>> teamTotalSkills = new ArrayList<>();
    for (Team team: teamRepository.findAllByCompetitionId(_competitionId)) {
      double totalSkill = getTotalTeamSkill(team.getId());
      Pair<String, Double> pair = Pair.of(team.getName(), totalSkill);
      teamTotalSkills.add(pair);
    }

    return teamTotalSkills
      .stream()
      .sorted((x, y) -> Double.compare(y.getValue(), x.getValue()))
      .collect(Collectors.toList());
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
  @Scheduled(fixedDelay = 300)
  public void play() {

    List<Long> teamIds = getAllTeams();


    if (round.getRound() > 50) {

      // GF
      List<TeamCompetitionDetail> teamCompetitionDetails = teamCompetitionDetailRepository.findAll();
      for (int id = 1; id <= 3; id += 2) {
        int finalId = id;
        List<TeamCompetitionDetail> teamCompetitionDetailList = teamCompetitionDetails.stream()
          .filter(detail -> detail.getCompetitionId() == finalId)
          .sorted((o1, o2) -> {
            if (o1.getPoints() != o2.getPoints())
              return o1.getPoints() < o2.getPoints() ? 1 : -1;
            if (o1.getGoalDifference() != o2.getGoalDifference())
              return o1.getGoalDifference() < o2.getGoalDifference() ? 1 : -1;
            if (o1.getGoalsFor() != o2.getGoalsFor())
              return o1.getGoalsFor() < o2.getGoalsFor() ? 1 : -1;
            return 0;
          }).collect(Collectors.toList());

        int index = 1;

        for (TeamCompetitionDetail teamCompetitionDetail: teamCompetitionDetailList) {

          CompetitionTeamInfo competitionTeamInfo = new CompetitionTeamInfo();
          competitionTeamInfo.setCompetitionId(id);
          competitionTeamInfo.setSeasonNumber(Long.parseLong(getCurrentSeason()) + 1);
          competitionTeamInfo.setRound(1L);
          competitionTeamInfo.setTeamId(teamCompetitionDetail.getTeamId());

          competitionTeamInfoRepository.save(competitionTeamInfo);

          CompetitionTeamInfo competitionTeamInfoCup = new CompetitionTeamInfo();
          competitionTeamInfoCup.setCompetitionId(id+1);
          competitionTeamInfoCup.setSeasonNumber(Long.parseLong(getCurrentSeason()) + 1);
          competitionTeamInfoCup.setRound(index <= 4 ? 2L : 1L);
          competitionTeamInfoCup.setTeamId(teamCompetitionDetail.getTeamId());

          competitionTeamInfoRepository.save(competitionTeamInfoCup);

          index++;
        }
      }

      List<PlayerTransferView> playersForTransferMarket = new ArrayList<>();
      for (Long teamId: teamIds) {

        Team team = teamRepository.findById(teamId).orElse(new Team());
        playersForTransferMarket.addAll(_compositeTransferStrategy.playersToSell(team, humanRepository, getMinimumPositionNeeded()));
      }

      HashMap<String, List<PlayerTransferView>> transferMarket = new HashMap<>();
      for (PlayerTransferView playerTransferView : playersForTransferMarket) {
        if (transferMarket.containsKey(playerTransferView.getPosition()))
          transferMarket.get(playerTransferView.getPosition()).add(playerTransferView);
        else
          transferMarket.put(playerTransferView.getPosition(), new ArrayList<>(List.of(playerTransferView)));
      }

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

      for (long teamId: teamIds) {
        List<Human> players = humanRepository.findAllByTeamIdAndTypeId(teamId, TypeNames.HUMAN_TYPE);
        for (Human human: players) {
          long seasonCreated = human.getSeasonCreated();
          if (round.getSeason() - seasonCreated <= 2 && seasonCreated != 1L)
            human.setCurrentStatus("Junior");
          else if (round.getSeason() - seasonCreated <= 6 && seasonCreated != 1L)
            human.setCurrentStatus("Intermediate");
          else
            human.setCurrentStatus("Senior");
          human.setTransferValue(calculateTransferValue(human.getAge(), human.getPosition(), human.getRating()));
          humanRepository.save(human);
        }
      }
    }

    if (round.getRound() == 1) {

      for (String competitionId : List.of("1", "3"))
        this.getFixturesForRound(competitionId, "1");

      if (round.getSeason() == 1) {
        List<Team> teams = teamRepository.findAll();
        Random random = new Random();
        for (Team team : teams) {
          TeamFacilities teamFacilities = _teamFacilitiesRepository.findByTeamId(team.getId());
          int nrPlayers = 22;
          for (int i = 0; i < nrPlayers; i++) {
            String name = NameGenerator.generateName();
            Human player = new Human();
            player.setTeamId(team.getId());
            player.setName(name);
            player.setTypeId(1L);
            if (i < 2)
              player.setPosition("GK");
            else if (i < 4)
              player.setPosition("DL");
            else if (i < 6)
              player.setPosition("DR");
            else if (i < 10)
              player.setPosition("DC");
            else if (i < 12)
              player.setPosition("ML");
            else if (i < 14)
              player.setPosition("MR");
            else if (i < 18)
              player.setPosition("MC");
            else if (i < 22)
              player.setPosition("ST");
            player.setAge(random.nextInt(23, 30));
            player.setSeasonCreated(1L);
            player.setCurrentStatus("Senior");

            int reputation = (int) teamFacilities.getSeniorTrainingLevel() * 10;
            player.setRating(random.nextInt(reputation - 20, reputation + 20));
            player.setTransferValue(calculateTransferValue(player.getAge(), player.getPosition(), player.getRating()));

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

    for (String competitionId : List.of("1", "3"))
      this.simulateRound(competitionId, round.getRound() - 1 + "");

    for (String competitionId : List.of("2", "4")) {

      if (round.getRound() == 5) {
        this.getFixturesForRound(competitionId, "1");
        this.simulateRound(competitionId, "1");
      }
      if (round.getRound() == 10) {
        this.getFixturesForRound(competitionId, "2");
        this.simulateRound(competitionId, "2");
      }
      if (round.getRound() == 20) {
        this.getFixturesForRound(competitionId, "3");
        this.simulateRound(competitionId, "3");
      }
      if (round.getRound() == 35) {
        this.getFixturesForRound(competitionId, "4");
        this.simulateRound(competitionId, "4");
      }

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

      if (team == null || teamCompetitionDetail == null) {
        teamCompetitionDetail = new TeamCompetitionDetail();
        teamCompetitionDetail.setTeamId(teamId);
        teamCompetitionDetail.setCompetitionId(competitionId);
      }

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
        .collect(Collectors.toList());

    return competitionTeamInfoDetails;

  }

  @GetMapping("getParticipants/{competitionId}/{roundId}")
  public List<Long> getParticipants(@PathVariable(name = "competitionId") String competitionId, @PathVariable(name = "roundId") String roundId) {

    long _competitionId = Long.parseLong(competitionId);
    long _roundId = Long.parseLong(roundId);

    List<CompetitionTeamInfo> competitionTeamInfos = competitionTeamInfoRepository
      .findAllByRoundAndCompetitionIdAndSeasonNumber(_roundId, _competitionId, Long.parseLong(getCurrentSeason()));

    List<Long> participants = new ArrayList<>(competitionTeamInfos
      .stream()
      .mapToLong(CompetitionTeamInfo::getTeamId)
      .boxed()
      .collect(Collectors.toSet()));

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
        .filter(competitionTeamInfoMatch -> competitionTeamInfoMatch.getCompetitionId() == _competitionId && competitionTeamInfoMatch.getRound() == _roundId
        && competitionTeamInfoMatch.getSeasonNumber().equals(getCurrentSeason()))
        .collect(Collectors.toList());

    List<TeamMatchView> matchViews = new ArrayList<>();

    for (CompetitionTeamInfoMatch match: futureMatches)
      matchViews.add(adaptCompetitionTeamInfoMatch(match, _competitionId, _roundId));

    return matchViews;
  }

  private TeamMatchView adaptCompetitionTeamInfoMatch(CompetitionTeamInfoMatch match, long competitionId, long roundId) {

    TeamMatchView teamMatchView = new TeamMatchView();
    teamMatchView.setTeamName1(teamRepository.findById(match.getTeam1Id()).get().getName());
    teamMatchView.setTeamName2(teamRepository.findById(match.getTeam2Id()).get().getName());

    CompetitionTeamInfoDetail matchDetail = competitionTeamInfoDetailRepository.findCompetitionTeamInfoDetailByCompetitionIdAndRoundIdAndTeam1IdAndTeam2IdAndSeasonNumber(competitionId, roundId, match.getTeam1Id(), match.getTeam2Id(), Long.parseLong(getCurrentSeason()));
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
    if (_competitionId == 1L || _competitionId == 3L) {
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
            competitionTeamInfoMatch.setSeasonNumber(getCurrentSeason());
            competitionTeamInfoMatchRepository.save(competitionTeamInfoMatch);
          }
          currentRound++;
        }
      }


    } else {

      Collections.shuffle(participants);
      for (int i = 0; i < participants.size(); i += 2) {
        long teamHomeId = participants.get(i);
        long teamAwayId = participants.get(i+1);

        CompetitionTeamInfoMatch competitionTeamInfoMatch = new CompetitionTeamInfoMatch();
        competitionTeamInfoMatch.setCompetitionId(_competitionId);
        competitionTeamInfoMatch.setRound(_roundId);
        competitionTeamInfoMatch.setTeam1Id(teamHomeId);
        competitionTeamInfoMatch.setTeam2Id(teamAwayId);
        competitionTeamInfoMatch.setSeasonNumber(getCurrentSeason());
        competitionTeamInfoMatchRepository.save(competitionTeamInfoMatch);
      }
    }
  }

  @GetMapping("simulateRound/{competitionId}/{roundId}")
  public void simulateRound(@PathVariable(name = "competitionId") String competitionId, @PathVariable(name = "roundId") String roundId) {

    long _competitionId = Long.parseLong(competitionId);
    long _roundId = Long.parseLong(roundId);
    long nextRound = getNextRound(_roundId);

    java.util.Random random = new Random();
    List<CompetitionTeamInfoMatch> matches = competitionTeamInfoMatchRepository
      .findAll();

    matches = matches.stream().filter(x -> x.getRound() == _roundId && x.getCompetitionId() == _competitionId
    && x.getSeasonNumber().equals(getCurrentSeason())).collect(Collectors.toList());

    for (CompetitionTeamInfoMatch match : matches) {
      long teamId1 = match.getTeam1Id();
      long teamId2 = match.getTeam2Id();

      long teamReputation1 = teamRepository.findById(teamId1).get().getReputation();
      long teamReputation2 = teamRepository.findById(teamId2).get().getReputation();

      int teamScore1, teamScore2;

      List<Human> firstTeam = getBestEleven(teamId1);
      List<Human> secondTeam = getBestEleven(teamId2);

      double teamPower1 = getBestElevenRating(firstTeam);
      double teamPower2 = getBestElevenRating(secondTeam);

      List<Integer> limits = calculateLimits(teamPower1, teamPower2);
      int limitA = limits.get(0);
      int limitB = limits.get(1);

      teamScore1 = random.nextInt(limitA);
      teamScore2 = random.nextInt(limitB);

      if (_competitionId == 2L || _competitionId == 4L) {
        while (teamScore2 == teamScore1)
          teamScore2 = random.nextInt(5);
      }

      updateTeam(teamId1, _competitionId, teamScore1, teamScore2);
      updateTeam(teamId2, _competitionId, teamScore2, teamScore1);


      if (nextRound != -1 && (_competitionId == 2L || _competitionId == 4L)) {
        CompetitionTeamInfo competitionTeamInfo = new CompetitionTeamInfo();
        competitionTeamInfo.setCompetitionId(_competitionId);
        competitionTeamInfo.setRound(nextRound);

        competitionTeamInfo.setTeamId(teamScore1 > teamScore2 ? teamId1 : teamId2);
        competitionTeamInfo.setSeasonNumber(Long.parseLong(getCurrentSeason()));
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
      competitionTeamInfoDetail.setSeasonNumber(Long.parseLong(getCurrentSeason()));
      competitionTeamInfoDetailRepository.save(competitionTeamInfoDetail);
    }

  }

  private void updateTeam(long teamId, long competitionId, int scoreHome, int scoreAway) {

    TeamCompetitionDetail team = teamCompetitionDetailRepository.findTeamCompetitionDetailByTeamIdAndCompetitionId(teamId, competitionId);
    if (team == null) {
      team = new TeamCompetitionDetail();
      team.setTeamId(teamId);
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

    if (team.getForm().length() > 5)
      team.setForm(team.getForm().substring(team.getForm().length() - 5));

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

  private List<Long> getAllTeams() {

    return teamRepository.findAll()
      .stream()
      .map(Team::getId)
      .collect(Collectors.toList());

  }

  public HashMap<String, Integer> getMinimumPositionNeeded() {

    HashMap<String, Integer> minimumPositionNeeded = new HashMap<>();
    minimumPositionNeeded.put("GK", 2);
    minimumPositionNeeded.put("DL", 1);
    minimumPositionNeeded.put("DC", 3);
    minimumPositionNeeded.put("DR", 1);
    minimumPositionNeeded.put("MC", 3);
    minimumPositionNeeded.put("ML", 1);
    minimumPositionNeeded.put("MR", 1);
    minimumPositionNeeded.put("ST", 2);

    return minimumPositionNeeded;
  }
  
  public long calculateTransferValue(long age, String position, double rating) {

    double value = rating * 10000;

    return (long) value;
  }

  private double getBestElevenRating(List<Human> players) {

    double bestElevenRating = 0;

    for (Human player : players)
      bestElevenRating += player.getRating();

    return bestElevenRating;
  }

  private List<Human> getBestEleven(long teamId) {

    List<Human> players = humanRepository
      .findAllByTeamIdAndTypeId(teamId, TypeNames.HUMAN_TYPE)
      .stream()
      .sorted(Comparator.comparing(Human::getRating))
      .collect(Collectors.toList());

    List<String> positions = getPositionsForBestEleven(teamId);
    List<Human> bestEleven = new ArrayList<>();

    for (String position : positions) {
      for (Human player : players) {
        if (player.getPosition().equals(position)) {
          bestEleven.add(player);
          break;
        }
      }
    }

    return bestEleven;
  }

  private List<String> getPositionsForBestEleven(long teamId) {
        List<String> fourFourTwo = List.of("GK", "DL", "DC", "DC", "DR", "ML", "MC", "MC", "MR", "ST", "ST");
        List<String> fourTwoThreeOne = List.of("GK", "DL", "DC", "DC", "DR", "MC", "MC", "MC", "ML", "MR", "ST");
        List<String> fourOneFourOne = List.of("GK", "DL", "DC", "DC", "DR", "MC", "MC", "MC", "MC", "ST");
    return fourFourTwo;
  }
  
}

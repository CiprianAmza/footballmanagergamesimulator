-- human_type values

DROP TABLE IF EXISTS human_type;
DROP TABLE IF EXISTS humantype;
CREATE TABLE humantype (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255)
);

INSERT INTO humantype (name) VALUES ("Player");
INSERT INTO humantype (name) VALUES ("Manager");
INSERT INTO humantype (name) VALUES ("Chairman");
INSERT INTO humantype (name) VALUES ("Referee");
INSERT INTO humantype (name) VALUES ("Staff");
INSERT INTO humantype (name) VALUES ("Scout");
INSERT INTO humantype (name) VALUES ("Agent");





-- competition values

DROP TABLE IF EXISTS competition;
CREATE TABLE competition(
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  nationId BIGINT,
  prizesId BIGINT,
  name VARCHAR(255)
);

INSERT INTO competition (nationId, prizesId, name) VALUES (1, 1, "Gallactick Football First League");
INSERT INTO competition (nationId, prizesId, name) VALUES (1, 2, "Gallactick Football Cup");





-- team values

DROP TABLE IF EXISTS team;
CREATE TABLE team (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  competitionId BIGINT,
  stadiumId BIGINT,
  historyId BIGINT,
  name VARCHAR(255),
  totalFinances BIGINT,
  transferBudget BIGINT,
  salaryBudget BIGINT,
  reputation INT,
  color1 VARCHAR(255),
  color2 VARCHAR(255),
  border VARCHAR(255)
);

INSERT INTO team (competitionId, stadiumId, historyId, name, totalFinances, transferBudget, salaryBudget, reputation, color1, color2, border)
VALUES (1, 1, 1, "Shadows", 10000000, 10000000, 500000, 10000, "black", "grey", "25");
INSERT INTO team (competitionId, stadiumId, historyId, name, totalFinances, transferBudget, salaryBudget, reputation, color1, color2, border)
VALUES (1, 1, 1, "Lightnings", 10000000, 10000000, 500000, 9000, "blue", "darkblue", "55");
INSERT INTO team (competitionId, stadiumId, historyId, name, totalFinances, transferBudget, salaryBudget, reputation, color1, color2, border)
VALUES (1, 1, 1, "Xenon", 10000000, 10000000, 500000, 9000, "green", "darkgreen", "35");
INSERT INTO team (competitionId, stadiumId, historyId, name, totalFinances, transferBudget, salaryBudget, reputation, color1, color2, border)
VALUES (1, 1, 1, "Snow Kids", 10000000, 10000000, 500000, 8600, "white", "blue", "65");
INSERT INTO team (competitionId, stadiumId, historyId, name, totalFinances, transferBudget, salaryBudget, reputation, color1, color2, border)
VALUES (1, 1, 1, "Wambas", 10000000, 10000000, 500000, 8000, "yellow", "green", "5");
INSERT INTO team (competitionId, stadiumId, historyId, name, totalFinances, transferBudget, salaryBudget, reputation, color1, color2, border)
VALUES (1, 1, 1, "Technoid", 10000000, 10000000, 500000, 7900, "grey", "green", "70");
INSERT INTO team (competitionId, stadiumId, historyId, name, totalFinances, transferBudget, salaryBudget, reputation, color1, color2, border)
VALUES (1, 1, 1, "Cyclops", 10000000, 10000000, 500000, 7000, "orange", "black", "45");
INSERT INTO team (competitionId, stadiumId, historyId, name, totalFinances, transferBudget, salaryBudget, reputation, color1, color2, border)
VALUES (1, 1, 1, "Red Tigers", 10000000, 10000000, 500000, 6900, "red", "grey", "25");
INSERT INTO team (competitionId, stadiumId, historyId, name, totalFinances, transferBudget, salaryBudget, reputation, color1, color2, border)
VALUES (1, 1, 1, "Akillian", 10000000, 10000000, 500000, 6000, "white", "grey", "35");
INSERT INTO team (competitionId, stadiumId, historyId, name, totalFinances, transferBudget, salaryBudget, reputation, color1, color2, border)
VALUES (1, 1, 1, "Rykers", 10000000, 10000000, 500000, 7000, "orange", "yellow", "60");
INSERT INTO team (competitionId, stadiumId, historyId, name, totalFinances, transferBudget, salaryBudget, reputation, color1, color2, border)
VALUES (1, 1, 1, "Pirates", 10000000, 10000000, 500000, 6700, "blue", "black", "95");
INSERT INTO team (competitionId, stadiumId, historyId, name, totalFinances, transferBudget, salaryBudget, reputation, color1, color2, border)
VALUES (1, 1, 1, "Elektras", 10000000, 10000000, 500000, 6500, "pink", "lila", "9");


-- team_competition_relation values
DROP TABLE IF EXISTS team_competition_relation;
DROP TABLE IF EXISTS teamcompetitionrelation;
CREATE TABLE teamcompetitionrelation (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  teamId BIGINT,
  competitionId BIGINT
);

INSERT INTO teamcompetitionrelation (teamId, competitionId) VALUES(1, 1);
INSERT INTO teamcompetitionrelation (teamId, competitionId) VALUES(2, 1);
INSERT INTO teamcompetitionrelation (teamId, competitionId) VALUES(3, 1);
INSERT INTO teamcompetitionrelation (teamId, competitionId) VALUES(4, 1);
INSERT INTO teamcompetitionrelation (teamId, competitionId) VALUES(5, 1);
INSERT INTO teamcompetitionrelation (teamId, competitionId) VALUES(6, 1);
INSERT INTO teamcompetitionrelation (teamId, competitionId) VALUES(7, 1);
INSERT INTO teamcompetitionrelation (teamId, competitionId) VALUES(8, 1);
INSERT INTO teamcompetitionrelation (teamId, competitionId) VALUES(9, 1);
INSERT INTO teamcompetitionrelation (teamId, competitionId) VALUES(10, 1);
INSERT INTO teamcompetitionrelation (teamId, competitionId) VALUES(11, 1);
INSERT INTO teamcompetitionrelation (teamId, competitionId) VALUES(12, 1);

INSERT INTO teamcompetitionrelation (teamId, competitionId) VALUES(1, 2);
INSERT INTO teamcompetitionrelation (teamId, competitionId) VALUES(2, 2);
INSERT INTO teamcompetitionrelation (teamId, competitionId) VALUES(3, 2);
INSERT INTO teamcompetitionrelation (teamId, competitionId) VALUES(4, 2);
INSERT INTO teamcompetitionrelation (teamId, competitionId) VALUES(5, 2);
INSERT INTO teamcompetitionrelation (teamId, competitionId) VALUES(6, 2);
INSERT INTO teamcompetitionrelation (teamId, competitionId) VALUES(7, 2);
INSERT INTO teamcompetitionrelation (teamId, competitionId) VALUES(8, 2);
INSERT INTO teamcompetitionrelation (teamId, competitionId) VALUES(9, 2);
INSERT INTO teamcompetitionrelation (teamId, competitionId) VALUES(10, 2);
INSERT INTO teamcompetitionrelation (teamId, competitionId) VALUES(11, 2);
INSERT INTO teamcompetitionrelation (teamId, competitionId) VALUES(12, 2);


-- teamcompetitiondetail values
DROP TABLE IF EXISTS teamcompetitiondetail;
CREATE TABLE teamcompetitiondetail (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  games INT,
  wins INT,
  draws INT,
  loses INT,
  goalsFor INT,
  goalsAgainst INT,
  goalDifference INT,
  points INT,
  competitionId BIGINT,
  teamId BIGINT,
  form VARCHAR(255),
  last10positions VARCHAR(255)
);

INSERT INTO teamcompetitiondetail (games, wins, draws, loses, goalsFor, goalsAgainst, goalDifference, points, competitionId, teamId, form, last10positions)
VALUES (0, 0, 0, 0, 0, 0, 0, 0, 1, 1, "", "1, 1, 1, 1, 1, 1, 1, 1, 1, 1");
INSERT INTO teamcompetitiondetail (games, wins, draws, loses, goalsFor, goalsAgainst, goalDifference, points, competitionId, teamId, form, last10positions)
VALUES (0, 0, 0, 0, 0, 0, 0, 0, 1, 2, "", "5, 3, 5, 6, 7, 4, 4, 3, 2, 2");
INSERT INTO teamcompetitiondetail (games, wins, draws, loses, goalsFor, goalsAgainst, goalDifference, points, competitionId, teamId, form, last10positions)
VALUES (0, 0, 0, 0, 0, 0, 0, 0, 1, 3, "", "2, 2, 2, 2, 2, 2, 2, 2, 3, 3");
INSERT INTO teamcompetitiondetail (games, wins, draws, loses, goalsFor, goalsAgainst, goalDifference, points, competitionId, teamId, form, last10positions)
VALUES (0, 0, 0, 0, 0, 0, 0, 0, 1, 4, "", "3, 4, 4, 4, 4, 4, 3, 3, 4, 4");
INSERT INTO teamcompetitiondetail (games, wins, draws, loses, goalsFor, goalsAgainst, goalDifference, points, competitionId, teamId, form, last10positions)
VALUES (0, 0, 0, 0, 0, 0, 0, 0, 1, 5, "", "6, 5, 7, 5, 5, 6, 5, 5, 6, 5");
INSERT INTO teamcompetitiondetail (games, wins, draws, loses, goalsFor, goalsAgainst, goalDifference, points, competitionId, teamId, form, last10positions)
VALUES (0, 0, 0, 0, 0, 0, 0, 0, 1, 6, "", "8, 7, 5, 6, 4, 5, 6, 7, 7, 6");
INSERT INTO teamcompetitiondetail (games, wins, draws, loses, goalsFor, goalsAgainst, goalDifference, points, competitionId, teamId, form, last10positions)
VALUES (0, 0, 0, 0, 0, 0, 0, 0, 1, 7, "", "7, 10, 10, 11, 11, 10, 9, 8, 10, 7");
INSERT INTO teamcompetitiondetail (games, wins, draws, loses, goalsFor, goalsAgainst, goalDifference, points, competitionId, teamId, form, last10positions)
VALUES (0, 0, 0, 0, 0, 0, 0, 0, 1, 8, "", "11, 10, 10, 11, 11, 10, 9, 8, 10, 8");
INSERT INTO teamcompetitiondetail (games, wins, draws, loses, goalsFor, goalsAgainst, goalDifference, points, competitionId, teamId, form, last10positions)
VALUES (0, 0, 0, 0, 0, 0, 0, 0, 1, 9, "", "6, 10, 5, 8, 12, 9, 9, 12, 10, 9");
INSERT INTO teamcompetitiondetail (games, wins, draws, loses, goalsFor, goalsAgainst, goalDifference, points, competitionId, teamId, form, last10positions)
VALUES (0, 0, 0, 0, 0, 0, 0, 0, 1, 10, "", "8, 10, 10, 11, 11, 10, 9, 8, 10, 10");
INSERT INTO teamcompetitiondetail (games, wins, draws, loses, goalsFor, goalsAgainst, goalDifference, points, competitionId, teamId, form, last10positions)
VALUES (0, 0, 0, 0, 0, 0, 0, 0, 1, 11, "", "7, 10, 10, 11, 11, 10, 9, 8, 10, 11");
INSERT INTO teamcompetitiondetail (games, wins, draws, loses, goalsFor, goalsAgainst, goalDifference, points, competitionId, teamId, form, last10positions)
VALUES (0, 0, 0, 0, 0, 0, 0, 0, 1, 12, "", "12, 12, 12, 12, 12, 12, 12, 12, 12, 12");

INSERT INTO teamcompetitiondetail (games, wins, draws, loses, goalsFor, goalsAgainst, goalDifference, points, competitionId, teamId, form, last10positions)
VALUES (0, 0, 0, 0, 0, 0, 0, 0, 2, 1, "", "1, 1, 1, 1, 1, 1, 1, 1, 1, 1");
INSERT INTO teamcompetitiondetail (games, wins, draws, loses, goalsFor, goalsAgainst, goalDifference, points, competitionId, teamId, form, last10positions)
VALUES (0, 0, 0, 0, 0, 0, 0, 0, 2, 2, "", "5, 3, 5, 6, 7, 4, 4, 3, 2, 2");
INSERT INTO teamcompetitiondetail (games, wins, draws, loses, goalsFor, goalsAgainst, goalDifference, points, competitionId, teamId, form, last10positions)
VALUES (0, 0, 0, 0, 0, 0, 0, 0, 2, 3, "", "2, 2, 2, 2, 2, 2, 2, 2, 3, 3");
INSERT INTO teamcompetitiondetail (games, wins, draws, loses, goalsFor, goalsAgainst, goalDifference, points, competitionId, teamId, form, last10positions)
VALUES (0, 0, 0, 0, 0, 0, 0, 0, 2, 4, "", "3, 4, 4, 4, 4, 4, 3, 3, 4, 4");
INSERT INTO teamcompetitiondetail (games, wins, draws, loses, goalsFor, goalsAgainst, goalDifference, points, competitionId, teamId, form, last10positions)
VALUES (0, 0, 0, 0, 0, 0, 0, 0, 2, 5, "", "6, 5, 7, 5, 5, 6, 5, 5, 6, 5");
INSERT INTO teamcompetitiondetail (games, wins, draws, loses, goalsFor, goalsAgainst, goalDifference, points, competitionId, teamId, form, last10positions)
VALUES (0, 0, 0, 0, 0, 0, 0, 0, 2, 6, "", "8, 7, 5, 6, 4, 5, 6, 7, 7, 6");
INSERT INTO teamcompetitiondetail (games, wins, draws, loses, goalsFor, goalsAgainst, goalDifference, points, competitionId, teamId, form, last10positions)
VALUES (0, 0, 0, 0, 0, 0, 0, 0, 2, 7, "", "7, 10, 10, 11, 11, 10, 9, 8, 10, 7");
INSERT INTO teamcompetitiondetail (games, wins, draws, loses, goalsFor, goalsAgainst, goalDifference, points, competitionId, teamId, form, last10positions)
VALUES (0, 0, 0, 0, 0, 0, 0, 0, 2, 8, "", "11, 10, 10, 11, 11, 10, 9, 8, 10, 8");
INSERT INTO teamcompetitiondetail (games, wins, draws, loses, goalsFor, goalsAgainst, goalDifference, points, competitionId, teamId, form, last10positions)
VALUES (0, 0, 0, 0, 0, 0, 0, 0, 2, 9, "", "6, 10, 5, 8, 12, 9, 9, 12, 10, 9");
INSERT INTO teamcompetitiondetail (games, wins, draws, loses, goalsFor, goalsAgainst, goalDifference, points, competitionId, teamId, form, last10positions)
VALUES (0, 0, 0, 0, 0, 0, 0, 0, 2, 10, "", "8, 10, 10, 11, 11, 10, 9, 8, 10, 10");
INSERT INTO teamcompetitiondetail (games, wins, draws, loses, goalsFor, goalsAgainst, goalDifference, points, competitionId, teamId, form, last10positions)
VALUES (0, 0, 0, 0, 0, 0, 0, 0, 2, 11, "", "7, 10, 10, 11, 11, 10, 9, 8, 10, 11");
INSERT INTO teamcompetitiondetail (games, wins, draws, loses, goalsFor, goalsAgainst, goalDifference, points, competitionId, teamId, form, last10positions)
VALUES (0, 0, 0, 0, 0, 0, 0, 0, 2, 12, "", "12, 12, 12, 12, 12, 12, 12, 12, 12, 12");


-- competitionteaminfo values
DROP TABLE IF EXISTS competitionteaminfo;
CREATE TABLE competitionteaminfo (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  teamId BIGINT,
  competitionId BIGINT,
  round INT
);
INSERT INTO competitionteaminfo (teamId, competitionId, round) VALUES (1, 1, 1);
INSERT INTO competitionteaminfo (teamId, competitionId, round) VALUES (2, 1, 1);
INSERT INTO competitionteaminfo (teamId, competitionId, round) VALUES (3, 1, 1);
INSERT INTO competitionteaminfo (teamId, competitionId, round) VALUES (4, 1, 1);
INSERT INTO competitionteaminfo (teamId, competitionId, round) VALUES (5, 1, 1);
INSERT INTO competitionteaminfo (teamId, competitionId, round) VALUES (6, 1, 1);
INSERT INTO competitionteaminfo (teamId, competitionId, round) VALUES (7, 1, 1);
INSERT INTO competitionteaminfo (teamId, competitionId, round) VALUES (8, 1, 1);
INSERT INTO competitionteaminfo (teamId, competitionId, round) VALUES (9, 1, 1);
INSERT INTO competitionteaminfo (teamId, competitionId, round) VALUES (10, 1, 1);
INSERT INTO competitionteaminfo (teamId, competitionId, round) VALUES (11, 1, 1);
INSERT INTO competitionteaminfo (teamId, competitionId, round) VALUES (12, 1, 1);

INSERT INTO competitionteaminfo (teamId, competitionId, round) VALUES (1, 2, 2);
INSERT INTO competitionteaminfo (teamId, competitionId, round) VALUES (2, 2, 2);
INSERT INTO competitionteaminfo (teamId, competitionId, round) VALUES (3, 2, 2);
INSERT INTO competitionteaminfo (teamId, competitionId, round) VALUES (4, 2, 2);
INSERT INTO competitionteaminfo (teamId, competitionId, round) VALUES (5, 2, 1);
INSERT INTO competitionteaminfo (teamId, competitionId, round) VALUES (6, 2, 1);
INSERT INTO competitionteaminfo (teamId, competitionId, round) VALUES (7, 2, 1);
INSERT INTO competitionteaminfo (teamId, competitionId, round) VALUES (8, 2, 1);
INSERT INTO competitionteaminfo (teamId, competitionId, round) VALUES (9, 2, 1);
INSERT INTO competitionteaminfo (teamId, competitionId, round) VALUES (10, 2, 1);
INSERT INTO competitionteaminfo (teamId, competitionId, round) VALUES (11, 2, 1);
INSERT INTO competitionteaminfo (teamId, competitionId, round) VALUES (12, 2, 1);

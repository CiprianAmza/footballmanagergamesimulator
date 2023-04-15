-- human_type values

DROP TABLE IF EXISTS human_type;
CREATE TABLE human_type (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255)
);

INSERT INTO human_type (name) VALUES ("Player");
INSERT INTO human_type (name) VALUES ("Manager");
INSERT INTO human_type (name) VALUES ("Chairman");
INSERT INTO human_type (name) VALUES ("Referee");
INSERT INTO human_type (name) VALUES ("Staff");
INSERT INTO human_type (name) VALUES ("Scout");
INSERT INTO human_type (name) VALUES ("Agent");






-- competition values

DROP TABLE IF EXISTS competition;
CREATE TABLE competition(
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  nationId BIGINT,
  prizesId BIGINT,
  name VARCHAR(255)
);

INSERT INTO competition (nationId, prizesId, name) VALUES (1, 1, "Gallactick Football First League");





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
  reputation INT
);

INSERT INTO team (competitionId, stadiumId, historyId, name, totalFinances, transferBudget, salaryBudget, reputation)
VALUES (1, 1, 1, "Shadows", 10000000, 10000000, 500000, 10000);
INSERT INTO team (competitionId, stadiumId, historyId, name, totalFinances, transferBudget, salaryBudget, reputation)
VALUES (1, 1, 1, "Lightnings", 10000000, 10000000, 500000, 9000);
INSERT INTO team (competitionId, stadiumId, historyId, name, totalFinances, transferBudget, salaryBudget, reputation)
VALUES (1, 1, 1, "Xenon", 10000000, 10000000, 500000, 9000);
INSERT INTO team (competitionId, stadiumId, historyId, name, totalFinances, transferBudget, salaryBudget, reputation)
VALUES (1, 1, 1, "Snow Kids", 10000000, 10000000, 500000, 8600);
INSERT INTO team (competitionId, stadiumId, historyId, name, totalFinances, transferBudget, salaryBudget, reputation)
VALUES (1, 1, 1, "Wambas", 10000000, 10000000, 500000, 8000);
INSERT INTO team (competitionId, stadiumId, historyId, name, totalFinances, transferBudget, salaryBudget, reputation)
VALUES (1, 1, 1, "Technoid", 10000000, 10000000, 500000, 7900);
INSERT INTO team (competitionId, stadiumId, historyId, name, totalFinances, transferBudget, salaryBudget, reputation)
VALUES (1, 1, 1, "Cyclops", 10000000, 10000000, 500000, 7000);
INSERT INTO team (competitionId, stadiumId, historyId, name, totalFinances, transferBudget, salaryBudget, reputation)
VALUES (1, 1, 1, "Red Tigers", 10000000, 10000000, 500000, 6900);
INSERT INTO team (competitionId, stadiumId, historyId, name, totalFinances, transferBudget, salaryBudget, reputation)
VALUES (1, 1, 1, "Akillian", 10000000, 10000000, 500000, 6000);
INSERT INTO team (competitionId, stadiumId, historyId, name, totalFinances, transferBudget, salaryBudget, reputation)
VALUES (1, 1, 1, "Rykers", 10000000, 10000000, 500000, 7000);
INSERT INTO team (competitionId, stadiumId, historyId, name, totalFinances, transferBudget, salaryBudget, reputation)
VALUES (1, 1, 1, "Pirates", 10000000, 10000000, 500000, 6700);
INSERT INTO team (competitionId, stadiumId, historyId, name, totalFinances, transferBudget, salaryBudget, reputation)
VALUES (1, 1, 1, "Elektras", 10000000, 10000000, 500000, 6500);

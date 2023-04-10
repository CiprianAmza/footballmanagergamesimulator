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
CREATE TABLE IF NOT EXISTS Schools (
    schoolId   INTEGER     PRIMARY KEY AUTO_INCREMENT,
    schoolName VARCHAR(50) NOT NULL
);

CREATE TABLE IF NOT EXISTS Competitions (
    competitionId   INTEGER     PRIMARY KEY AUTO_INCREMENT,
    competitionName   VARCHAR(45) NOT NULL,
    competitionDate DATE        NOT NULL,
    schoolId        INTEGER     NOT NULL
);

CREATE TABLE IF NOT EXISTS Results (
    resultId      INTEGER       PRIMARY KEY AUTO_INCREMENT,
    resultFile    VARCHAR(1024) NOT NULL,
    competitionId INTEGER       NOT NULL,
    agentId       INTEGER       NOT NULL
);

CREATE TABLE IF NOT EXISTS Agents (
    agentId       INTEGER     PRIMARY KEY AUTO_INCREMENT,
    agentName     VARCHAR(30) NOT NULL,
    agentLogin    VARCHAR(45) UNIQUE NOT NULL,
    agentPassword VARCHAR(16) NOT NULL,
    schoolId      INTEGER     NOT NULL
);

ALTER TABLE Competitions
ADD CONSTRAINT Competitions_schoolId
FOREIGN KEY(schoolId) REFERENCES Schools(schoolId) ON DELETE CASCADE;

ALTER TABLE Results
ADD CONSTRAINT FK_Results_competitionId
FOREIGN KEY(competitionId) REFERENCES Competitions(competitionId);

ALTER TABLE Results
ADD CONSTRAINT FK_Results_agentId
FOREIGN KEY(agentId) REFERENCES Agents(agentId) ON DELETE CASCADE;

ALTER TABLE Agents
ADD CONSTRAINT FK_Agents_schoolId
FOREIGN KEY(schoolId) REFERENCES Schools(schoolId) ON DELETE CASCADE;


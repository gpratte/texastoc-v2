CREATE TABLE tocconfig
(
    kittyDebit           int NOT NULL,
    annualTocCost        int NOT NULL,
    quarterlyTocCost     int NOT NULL,
    quarterlyNumPayouts  int NOT NULL,
    regularBuyInCost     int NOT NULL,
    regularRebuyCost     int NOT NULL,
    regularRebuyTocDebit int NOT NULL,
    doubleBuyInCost      int NOT NULL,
    doubleRebuyCost      int NOT NULL,
    doubleRebuyTocDebit  int NOT NULL
);
INSERT INTO tocconfig
VALUES (10, 20, 20, 3, 40, 40, 20, 80, 40, 30);

CREATE TABLE season
(
    id                                int NOT NULL AUTO_INCREMENT,
    startDate                         date    DEFAULT NULL,
    endDate                           date    DEFAULT NULL,
    kittyPerGame                      int     DEFAULT NULL,
    tocPerGame                        int     DEFAULT NULL,
    quarterlyTocPerGame               int     DEFAULT NULL,
    quarterlyTocPayouts               int     DEFAULT NULL,
    buyInCost                         int     DEFAULT NULL,
    rebuyAddOnCost                    int     DEFAULT NULL,
    rebuyAddOnTocDebit                int     DEFAULT NULL,
    doubleBuyInCost                   int     DEFAULT NULL,
    doubleRebuyAddOnCost              int     DEFAULT NULL,
    doubleRebuyAddOnTocDebit          int     DEFAULT NULL,
    buyInCollected                    int     DEFAULT NULL,
    rebuyAddOnCollected               int     DEFAULT NULL,
    annualTocCollected                int     DEFAULT NULL,
    totalCollected                    int     DEFAULT NULL,
    annualTocFromRebuyAddOnCalculated int     DEFAULT NULL,
    rebuyAddOnLessAnnualTocCalculated int     DEFAULT NULL,
    totalCombinedAnnualTocCalculated  int     DEFAULT NULL,
    kittyCalculated                   int     DEFAULT NULL,
    prizePotCalculated                int     DEFAULT NULL,
    numGames                          int     DEFAULT NULL,
    numGamesPlayed                    int     DEFAULT NULL,
    finalized                         boolean DEFAULT NULL,
    lastCalculated                    date    DEFAULT NULL,
    PRIMARY KEY (id)
);
INSERT INTO season
VALUES (1, '2020-05-01', '2021-04-30', 10, 20, 20, 3, 40, 40, 20, 80, 40, 30, 920, 0, 300, 1320, 0, 0, 300, 20, 900, 52,
        2, 0, '2020-06-28');

CREATE TABLE quarterlyseason
(
    id             int NOT NULL AUTO_INCREMENT,
    seasonId       int NOT NULL,
    startDate      date    DEFAULT NULL,
    endDate        date    DEFAULT NULL,
    finalized      boolean DEFAULT NULL,
    quarter        int NOT NULL,
    numGames       int     DEFAULT NULL,
    numGamesPlayed int     DEFAULT NULL,
    qTocCollected  int     DEFAULT NULL,
    qTocPerGame    int     DEFAULT NULL,
    numPayouts     int NOT NULL,
    lastCalculated date    DEFAULT NULL,
    PRIMARY KEY (id)
);
INSERT INTO quarterlyseason
VALUES (1, 1, '2020-05-01', '2020-07-31', 0, 1, 13, 2, 100, 0, 0, '2020-06-28'),
       (2, 1, '2020-08-01', '2020-10-31', 0, 2, 13, 0, 0, 20, 3, NULL),
       (3, 1, '2020-11-01', '2021-01-31', 0, 3, 13, 0, 0, 20, 3, NULL),
       (4, 1, '2021-02-01', '2021-04-30', 0, 4, 13, 0, 0, 20, 3, NULL);

CREATE TABLE seasonplayer
(
    id       int NOT NULL AUTO_INCREMENT,
    playerId int NOT NULL,
    seasonId int NOT NULL,
    name     varchar(64) DEFAULT NULL,
    entries  int         DEFAULT 0,
    points   int         DEFAULT 0,
    place    int         DEFAULT 0,
    forfeit  boolean     DEFAULT FALSE,
    PRIMARY KEY (id),
    UNIQUE KEY SPlayer_Unique (playerId, seasonId)
);
INSERT INTO seasonplayer
VALUES (9, 1, 1, 'Gil Pratte', 2, 126, 1, 0),
       (10, 12, 1, 'Amina Mcmanus', 2, 64, 2, 0),
       (11, 15, 1, 'Edge', 2, 49, 3, 0),
       (12, 9, 1, 'Anniyah Conroy', 2, 38, 4, 0),
       (13, 20, 1, 'Elara Andrews', 1, 37, 5, 0),
       (14, 22, 1, 'Chloe-Ann Redmond', 2, 23, 6, 0),
       (15, 3, 1, 'Casey Greig', 1, 16, 7, 0),
       (16, 5, 1, 'Dolly Chamberlain', 1, 8, 8, 0),
       (17, 10, 1, 'Chelsea Mcknight', 1, 8, 9, 0),
       (18, 13, 1, 'Elodie Morrison', 1, 0, NULL, 0);

CREATE TABLE quarterlyseasonplayer
(
    id        int NOT NULL AUTO_INCREMENT,
    playerId  int NOT NULL,
    seasonId  int NOT NULL,
    qSeasonId int NOT NULL,
    name      varchar(64) DEFAULT NULL,
    entries   int         DEFAULT 0,
    points    int         DEFAULT 0,
    place     int         DEFAULT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY QSPlayer_Unique (playerId, seasonId, qSeasonId)
);
INSERT INTO quarterlyseasonplayer
VALUES (4, 12, 1, 1, 'Amina Mcmanus', 2, 64, 1),
       (5, 15, 1, 1, 'Edge', 1, 22, 2),
       (6, 22, 1, 1, 'Chloe-Ann Redmond', 1, 10, 3),
       (7, 10, 1, 1, 'Chelsea Mcknight', 1, 8, 4);

CREATE TABLE player
(
    id        int NOT NULL AUTO_INCREMENT,
    firstName varchar(32)  DEFAULT NULL,
    lastName  varchar(32)  DEFAULT NULL,
    phone     varchar(32)  DEFAULT NULL,
    email     varchar(64)  DEFAULT NULL,
    password  varchar(255) DEFAULT NULL,
    PRIMARY KEY (id)
);
# password is password
INSERT INTO player
VALUES (1, 'Gil', 'Pratte', '5121231235', 'gilpratte@texastoc.com',
        '$2a$10$qXQo4z4oXKPEKyYO7bAQmOQ9PhIcHK4LOo/L1U9j/xkLEmseLWECK'),
       (2, 'Guest', 'User', '5121231235', 'guest@texastoc.com',
        '$2a$10$qXQo4z4oXKPEKyYO7bAQmOQ9PhIcHK4LOo/L1U9j/xkLEmseLWECK'),
       (3, 'Casey', 'Greig', NULL, NULL, NULL),
       (4, 'Roscoe', NULL, NULL, NULL, NULL),
       (5, 'Dolly', 'Chamberlain', NULL, NULL, NULL),
       (6, 'Mary', 'Leblanc', NULL, NULL, NULL),
       (7, 'Herbert', 'Cameron', NULL, NULL, NULL),
       (8, 'Sania', 'Lennon', NULL, NULL, NULL),
       (9, 'Anniyah', 'Conroy', NULL, NULL, NULL),
       (10, 'Chelsea', 'Mcknight', NULL, NULL, NULL),
       (11, 'Lexie', NULL, NULL, NULL, NULL),
       (12, 'Amina', 'Mcmanus', NULL, NULL, NULL),
       (13, 'Elodie', 'Morrison', NULL, NULL, NULL),
       (14, 'Rhonda', 'Bautista', NULL, NULL, NULL),
       (15, NULL, 'Edge', NULL, NULL, NULL),
       (16, 'Skyla', 'Gallegos', NULL, NULL, NULL),
       (17, 'Priscilla', 'Lin', NULL, NULL, NULL),
       (18, 'Farzana', 'Vu', NULL, NULL, NULL),
       (19, 'Tamanna', 'Dorsey', NULL, NULL, NULL),
       (20, 'Elara', 'Andrews', NULL, NULL, NULL),
       (21, 'Seb', 'Marin', NULL, NULL, NULL),
       (22, 'Chloe-Ann', 'Redmond', NULL, NULL, NULL);

CREATE TABLE game
(
    id                                int       NOT NULL AUTO_INCREMENT,
    seasonId                          int       NOT NULL,
    qSeasonId                         int       NOT NULL,
    hostId                            int            DEFAULT NULL,
    gameDate                          date      NOT NULL,
    hostName                          varchar(64)    DEFAULT NULL,
    quarter                           int            DEFAULT NULL,
    doubleBuyIn                       boolean        DEFAULT FALSE,
    transportRequired                 boolean        DEFAULT FALSE,
    kittyCost                         int            DEFAULT 0,
    buyInCost                         int            DEFAULT 0,
    rebuyAddOnCost                    int            DEFAULT 0,
    rebuyAddOnTocDebit                int            DEFAULT 0,
    annualTocCost                     int            DEFAULT 0,
    quarterlyTocCost                  int            DEFAULT 0,
    started                           timestamp NULL DEFAULT NULL,
    numPlayers                        int            DEFAULT 0,
    buyInCollected                    int            DEFAULT 0,
    rebuyAddOnCollected               int            DEFAULT 0,
    annualTocCollected                int            DEFAULT 0,
    quarterlyTocCollected             int            DEFAULT 0,
    totalCollected                    int            DEFAULT 0,
    kittyCalculated                   int            DEFAULT 0,
    annualTocFromRebuyAddOnCalculated int            DEFAULT 0,
    rebuyAddOnLessAnnualTocCalculated int            DEFAULT 0,
    totalCombinedTocCalculated        int            DEFAULT 0,
    prizePotCalculated                int            DEFAULT 0,
    payoutDelta                       int            DEFAULT NULL,
    seasonGameNum                     int            DEFAULT NULL,
    quarterlyGameNum                  int            DEFAULT NULL,
    finalized                         boolean        DEFAULT FALSE,
    lastCalculated                    date           DEFAULT NULL,
    canRebuy                          boolean        DEFAULT TRUE,
    PRIMARY KEY (id)
);
INSERT INTO game
VALUES (1, 1, 1, 12, '2020-06-11', 'Amina Mcmanus', 1, 0, 0, 10, 40, 40, 20, 20, 20, NULL, 12, 480, 0, 160, 60, 700, 10,
        0, 0, 220, 470, 0, 1, 1, 1, '2020-06-28', 1),
       (2, 1, 1, 4, '2020-06-18', 'Roscoe', 1, 0, 0, 10, 40, 40, 20, 20, 20, NULL, 11, 440, 0, 140, 40, 620, 10, 0, 0,
        180, 430, 0, 2, 2, 1, '2020-06-28', 1),
       (3, 1, 1, 4, '2020-06-25', 'Roscoe', 1, 0, 0, 10, 40, 40, 20, 20, 20, NULL, 2, 80, 0, 40, 20, 140, 10, 0, 0, 60,
        70, 0, 3, 3, 0, '2020-06-28', 1);

CREATE TABLE gameplayer
(
    id                    int         NOT NULL AUTO_INCREMENT,
    playerId              int         NOT NULL,
    gameId                int         NOT NULL,
    qSeasonId             int         NOT NULL,
    seasonId              int         NOT NULL,
    name                  varchar(64) NOT NULL,
    place                 int     DEFAULT NULL,
    points                int     DEFAULT NULL,
    knockedOut            boolean DEFAULT FALSE,
    roundUpdates          boolean DEFAULT FALSE,
    buyInCollected        int     DEFAULT NULL,
    rebuyAddOnCollected   int     DEFAULT NULL,
    annualTocCollected    int     DEFAULT NULL,
    quarterlyTocCollected int     DEFAULT NULL,
    chop                  int     DEFAULT NULL,
    PRIMARY KEY (id)
);

INSERT INTO gameplayer
VALUES (1, 12, 1, 1, 1, 'Amina Mcmanus', 5, 29, 1, 0, 40, NULL, 20, 20, NULL),
       (2, 9, 1, 1, 1, 'Anniyah Conroy', 7, 17, 1, 0, 40, NULL, 20, NULL, NULL),
       (3, 3, 1, 1, 1, 'Casey Greig', 9, NULL, 1, 0, 40, 40, NULL, NULL, NULL),
       (4, 10, 1, 1, 1, 'Chelsea Mcknight', 10, 8, 1, 0, 40, 40, 20, 20, NULL),
       (5, 22, 1, 1, 1, 'Chloe-Ann Redmond', 8, 13, 1, 0, 40, 40, 20, NULL, NULL),
       (6, 5, 1, 1, 1, 'Dolly Chamberlain', NULL, NULL, NULL, NULL, 40, NULL, NULL, NULL, NULL),
       (7, 15, 1, 1, 1, 'Edge', 6, 22, 1, 0, 40, NULL, 20, 20, NULL),
       (8, 20, 1, 1, 1, 'Elara Andrews', 4, 37, 1, 0, 40, 40, 20, NULL, NULL),
       (9, 13, 1, 1, 1, 'Elodie Morrison', NULL, NULL, 0, 0, 40, 40, 20, NULL, NULL),
       (10, 18, 1, 1, 1, 'Farzana Vu', 3, NULL, 1, 0, 40, 40, NULL, NULL, NULL),
       (11, 1, 1, 1, 1, 'Gil Pratte', 2, 62, 1, 0, 40, NULL, 20, NULL, NULL),
       (12, 7, 1, 1, 1, 'Herbert Cameron', 1, NULL, 1, 0, 40, 40, NULL, NULL, NULL),
       (13, 12, 2, 1, 1, 'Amina Mcmanus', 4, 35, 1, 0, 40, NULL, 20, 20, NULL),
       (14, 9, 2, 1, 1, 'Anniyah Conroy', 6, 21, 1, 0, 40, NULL, 20, NULL, NULL),
       (15, 10, 2, 1, 1, 'Chelsea Mcknight', 8, NULL, 1, 0, 40, NULL, NULL, NULL, NULL),
       (16, 22, 2, 1, 1, 'Chloe-Ann Redmond', 9, 10, 1, 0, 40, NULL, 20, 20, NULL),
       (17, 15, 2, 1, 1, 'Edge', 5, 27, 1, 0, 40, 40, 20, NULL, NULL),
       (18, 20, 2, 1, 1, 'Elara Andrews', 3, NULL, 1, 0, 40, NULL, NULL, NULL, NULL),
       (19, 13, 2, 1, 1, 'Elodie Morrison', 1, NULL, 1, 0, 40, 40, NULL, NULL, 90000),
       (20, 1, 2, 1, 1, 'Gil Pratte', 2, 64, 1, 0, 40, 40, 20, NULL, 50000),
       (21, 3, 2, 1, 1, 'Casey Greig', 7, 16, 1, 0, 40, 40, 20, NULL, NULL),
       (22, 5, 2, 1, 1, 'Dolly Chamberlain', 10, 8, 1, 0, 40, 40, 20, NULL, NULL),
       (23, 18, 2, 1, 1, 'Farzana Vu', NULL, NULL, 0, 0, 40, 40, NULL, NULL, NULL),
       (24, 9, 3, 1, 1, 'Anniyah Conroy', NULL, NULL, 0, 0, 40, NULL, 20, NULL, NULL),
       (25, 5, 3, 1, 1, 'Dolly Chamberlain', NULL, 23, 0, 0, 40, NULL, 20, 20, NULL);

CREATE TABLE gamepayout
(
    id          int NOT NULL AUTO_INCREMENT,
    gameId      int NOT NULL,
    place       int NOT NULL,
    amount      int    DEFAULT NULL,
    chopAmount  int    DEFAULT NULL,
    chopPercent double DEFAULT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY GPayout_Unique (gameId, place)
);
INSERT INTO gamepayout
VALUES (52, 1, 1, 436, NULL, NULL),
       (53, 1, 2, 234, NULL, NULL),
       (64, 2, 1, 384, 321, 0.6428571428571429),
       (65, 2, 2, 206, 269, 0.35714285714285715),
       (69, 3, 1, 70, NULL, NULL);

CREATE TABLE quarterlyseasonpayout
(
    id        int NOT NULL AUTO_INCREMENT,
    seasonId  int NOT NULL,
    qSeasonId int NOT NULL,
    place     int NOT NULL,
    amount    int DEFAULT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY QSPayout_Unique (seasonId, qSeasonId, place)
);
INSERT INTO quarterlyseasonpayout
VALUES (4, 1, 1, 1, 50),
       (5, 1, 1, 2, 30),
       (6, 1, 1, 3, 20);

CREATE TABLE role
(
    id          int NOT NULL AUTO_INCREMENT,
    description varchar(255) DEFAULT NULL,
    name        varchar(255) DEFAULT NULL,
    PRIMARY KEY (id)
);
INSERT INTO role
VALUES (1, 'Admin role', 'ADMIN'),
       (2, 'User role', 'USER');

CREATE TABLE player_roles
(
    playerId int NOT NULL,
    roleId   int NOT NULL,
    PRIMARY KEY (playerId, roleId)
);
INSERT INTO player_roles
VALUES (1, 1),
       (2, 1),
       (1, 2),
       (2, 2),
       (3, 2),
       (4, 2),
       (5, 2),
       (6, 2),
       (7, 2),
       (8, 2),
       (9, 2),
       (10, 2),
       (11, 2),
       (12, 2),
       (13, 2),
       (14, 2),
       (15, 2),
       (16, 2),
       (17, 2),
       (18, 2),
       (19, 2),
       (20, 2),
       (21, 2),
       (22, 2);


CREATE TABLE seasonpayout
(
    id         int NOT NULL AUTO_INCREMENT,
    seasonId   int NOT NULL,
    place      int NOT NULL,
    amount     int     DEFAULT NULL,
    guarenteed boolean DEFAULT false,
    estimated  boolean DEFAULT false,
    cash       boolean DEFAULT false,
    PRIMARY KEY (id),
    UNIQUE KEY SPayout_Unique (seasonId, place, estimated)
);

CREATE TABLE seasonpayoutsettings
(
    id       int           NOT NULL AUTO_INCREMENT,
    seasonId int           NOT NULL,
    settings varchar(8192) NOT NULL,
    PRIMARY KEY (id)
);
INSERT INTO seasonpayoutsettings
VALUES (1, 1,
        '[{"lowRange" : 5000,"highRange" : 7000,
           "guaranteed": [{"place" : 1,"amount" : 1400,"percent" : 20}],
           "finalTable": [{"place" : 2,"amount" : 1350,"percent" : 20},
                          {"place" : 3,"amount" : 1150,"percent" : 16},
                          {"place" : 4,"amount" : 1100,"percent" : 14},
                          {"place" : 5,"amount" : 0,"percent" : 30}]}]');


DROP TABLE IF EXISTS seating;
CREATE TABLE seating
(
    gameId   int           NOT NULL,
    settings varchar(8192) NOT NULL,
    PRIMARY KEY (gameId)
);

DROP TABLE IF EXISTS settings;
CREATE TABLE settings
(
    id       int NOT NULL AUTO_INCREMENT,
    settings varchar(1024) DEFAULT NULL,
    PRIMARY KEY (id)
);
INSERT INTO settings
VALUES (1,
        '{"uiVersions": [{"env": "local", "version": "2.16"}, {"env": "heroku", "version": "2.16"}]}');

DROP TABLE IF EXISTS supply;
CREATE TABLE supply
(
    id          int         NOT NULL AUTO_INCREMENT,
    amount      int         NOT NULL,
    date        date        NOT NULL,
    type        varchar(16) NOT NULL,
    description varchar(64) DEFAULT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE payout
(
    numPayouts int NOT NULL,
    place      int NOT NULL,
    percent    double DEFAULT NULL,
    PRIMARY KEY (numPayouts, place)
);
INSERT INTO payout
VALUES (2, 1, 0.65),
       (2, 2, 0.35),
       (3, 1, 0.5),
       (3, 2, 0.3),
       (3, 3, 0.2),
       (4, 1, 0.45),
       (4, 2, 0.25),
       (4, 3, 0.18),
       (4, 4, 0.12),
       (5, 1, 0.4),
       (5, 2, 0.23),
       (5, 3, 0.16),
       (5, 4, 0.12),
       (5, 5, 0.09),
       (6, 1, 0.38),
       (6, 2, 0.22),
       (6, 3, 0.15),
       (6, 4, 0.11),
       (6, 5, 0.08),
       (6, 6, 0.06),
       (7, 1, 0.35),
       (7, 2, 0.21),
       (7, 3, 0.15),
       (7, 4, 0.11),
       (7, 5, 0.08),
       (7, 6, 0.06),
       (7, 7, 0.04),
       (8, 1, 0.335),
       (8, 2, 0.2),
       (8, 3, 0.145),
       (8, 4, 0.11),
       (8, 5, 0.08),
       (8, 6, 0.06),
       (8, 7, 0.04),
       (8, 8, 0.03),
       (9, 1, 0.32),
       (9, 2, 0.195),
       (9, 3, 0.14),
       (9, 4, 0.11),
       (9, 5, 0.08),
       (9, 6, 0.06),
       (9, 7, 0.04),
       (9, 8, 0.03),
       (9, 9, 0.025),
       (10, 1, 0.3),
       (10, 2, 0.19),
       (10, 3, 0.1325),
       (10, 4, 0.105),
       (10, 5, 0.075),
       (10, 6, 0.055),
       (10, 7, 0.0375),
       (10, 8, 0.03),
       (10, 9, 0.0225),
       (10, 10, 0.015);

alter table player_roles
    add constraint fk_role_id foreign key (roleId) references role (id);
alter table player_roles
    add constraint fk_player_id foreign key (playerId) references player (id);

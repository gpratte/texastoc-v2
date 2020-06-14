create table if not exists tocconfig
(
    kittyDebit           INT NOT NULL,
    annualTocCost        INT NOT NULL,
    quarterlyTocCost     INT NOT NULL,
    quarterlyNumPayouts  INT NOT NULL,
    regularBuyInCost     INT NOT NULL,
    regularRebuyCost     INT NOT NULL,
    regularRebuyTocDebit INT NOT NULL,
    doubleBuyInCost      INT NOT NULL,
    doubleRebuyCost      INT NOT NULL,
    doubleRebuyTocDebit  INT NOT NULL
);

insert into tocconfig (kittyDebit, annualTocCost, quarterlyTocCost, quarterlyNumPayouts, regularBuyInCost,
                       regularRebuyCost, regularRebuyTocDebit, doubleBuyInCost, doubleRebuyCost, doubleRebuyTocDebit)
values (9, 8, 7, 3, 6, 5, 4, 12, 10, 8);

create table if not exists season
(
    id                                INT auto_increment,
    startDate                         DATE,
    endDate                           DATE,
    kittyPerGame                      INT,
    tocPerGame                        INT,
    quarterlyTocPerGame               INT,
    quarterlyTocPayouts               INT,
    buyInCost                         INT,
    rebuyAddOnCost                    INT,
    rebuyAddOnTocDebit                INT,
    doubleBuyInCost                   INT,
    doubleRebuyAddOnCost              INT,
    doubleRebuyAddOnTocDebit          INT,
    buyInCollected                    INT,
    rebuyAddOnCollected               INT,
    annualTocCollected                INT,
    totalCollected                    INT,
    annualTocFromRebuyAddOnCalculated INT,
    rebuyAddOnLessAnnualTocCalculated INT,
    totalCombinedAnnualTocCalculated  INT,
    kittyCalculated                   INT,
    prizePotCalculated                INT,
    numGames                          INT,
    numGamesPlayed                    INT,
    finalized                         BOOLEAN,
    lastCalculated                    DATE,
    primary key (id)
);

create table if not exists quarterlyseason
(
    id             INT auto_increment,
    seasonId       INT NOT NULL,
    startDate      DATE,
    endDate        DATE,
    finalized      BOOLEAN,
    quarter        INT NOT NULL,
    numGames       INT,
    numGamesPlayed INT,
    qTocCollected  INT,
    qTocPerGame    INT,
    numPayouts     INT NOT NULL,
    lastCalculated DATE,
    primary key (id)
);

create table if not exists seasonplayer
(
    id       INT auto_increment,
    playerId INT NOT NULL,
    seasonId INT NOT NULL,
    name     varchar(64) DEFAULT NULL,
    entries  INT         DEFAULT 0,
    points   INT         DEFAULT 0,
    place    INT         DEFAULT 0,
    forfeit  BOOLEAN     DEFAULT false,
    primary key (id)
);

ALTER TABLE seasonplayer
    ADD CONSTRAINT SPlayer_Unique UNIQUE (playerId, seasonId);

create table if not exists quarterlyseasonplayer
(
    id        INT auto_increment,
    playerId  INT NOT NULL,
    seasonId  INT NOT NULL,
    qSeasonId INT NOT NULL,
    name      varchar(64) DEFAULT NULL,
    entries   INT         DEFAULT 0,
    points    INT         DEFAULT 0,
    place     INT,
    primary key (id)
);

ALTER TABLE quarterlyseasonplayer
    ADD CONSTRAINT QSPlayer_Unique UNIQUE (playerId, seasonId, qSeasonId);

create table if not exists supply
(
    id          INT auto_increment,
    amount      INT         NOT NULL,
    date        DATE        NOT NULL,
    type        varchar(16) NOT NULL,
    description varchar(64),
    primary key (id)
);

create table if not exists player
(
    id        INT auto_increment,
    firstName varchar(32)  DEFAULT NULL,
    lastName  varchar(32)  DEFAULT NULL,
    phone     varchar(32)  DEFAULT NULL,
    email     varchar(64)  DEFAULT NULL,
    password  varchar(255) DEFAULT NULL,
    primary key (id)
);

# password is password
insert into player (id, firstName, lastName, phone, email, password)
values (1, 'Gil', 'Pratte', '5121231235', 'gilpratte@texastoc.com',
        '$2a$10$qXQo4z4oXKPEKyYO7bAQmOQ9PhIcHK4LOo/L1U9j/xkLEmseLWECK');
insert into player (id, firstName, lastName, phone, email, password)
values (2, 'Guest', 'User', '5121231235', 'guestuser@texastoc.com',
        '$2a$10$qXQo4z4oXKPEKyYO7bAQmOQ9PhIcHK4LOo/L1U9j/xkLEmseLWECK');
insert into player (id, firstName, lastName)
values (3, 'Casey', 'Greig');
insert into player (id, firstName, lastName)
values (4, 'Roscoe', null);
insert into player (id, firstName, lastName)
values (5, 'Dolly', 'Chamberlain');
insert into player (id, firstName, lastName)
values (6, 'Mary', 'Leblanc');
insert into player (id, firstName, lastName)
values (7, 'Herbert', 'Cameron');
insert into player (id, firstName, lastName)
values (8, 'Sania', 'Lennon');
insert into player (id, firstName, lastName)
values (9, 'Anniyah', 'Conroy');
insert into player (id, firstName, lastName)
values (10, 'Chelsea', 'Mcknight');
insert into player (id, firstName, lastName)
values (11, 'Lexie', null);
insert into player (id, firstName, lastName)
values (12, 'Aamina', 'Mcmanus');
insert into player (id, firstName, lastName)
values (13, 'Elodie', 'Morrison');
insert into player (id, firstName, lastName)
values (14, 'Rhonda', 'Bautista');
insert into player (id, firstName, lastName)
values (15, null, 'Edge');
insert into player (id, firstName, lastName)
values (16, 'Skyla', 'Gallegos');
insert into player (id, firstName, lastName)
values (17, 'Priscilla', 'Lin');
insert into player (id, firstName, lastName)
values (18, 'Farzana', 'Vu');
insert into player (id, firstName, lastName)
values (19, 'Tamanna', 'Dorsey');
insert into player (id, firstName, lastName)
values (20, 'Elara', 'Andrews');
insert into player (id, firstName, lastName)
values (21, 'Seb', 'Marin');
insert into player (id, firstName, lastName)
values (22, 'Chloe-Ann', 'Redmond');
insert into player (id, firstName, lastName, phone, email, password)
values (23, 'Guest', 'Admin', '5121231236', 'guestadmin@texastoc.com',
        '$2a$10$qXQo4z4oXKPEKyYO7bAQmOQ9PhIcHK4LOo/L1U9j/xkLEmseLWECK');

create table game
(
    id                                INT AUTO_INCREMENT,
    seasonId                          INT  NOT NULL,
    qSeasonId                         INT  NOT NULL,
    hostId                            INT         DEFAULT NULL,
    gameDate                          DATE NOT NULL,
    hostName                          varchar(64) DEFAULT NULL,
    quarter                           INT         DEFAULT NULL,
    doubleBuyIn                       BOOLEAN     DEFAULT FALSE,
    transportRequired                 BOOLEAN     DEFAULT FALSE,
    kittyCost                         INT         DEFAULT 0,
    buyInCost                         INT         DEFAULT 0,
    rebuyAddOnCost                    INT         DEFAULT 0,
    rebuyAddOnTocDebit                INT         DEFAULT 0,
    annualTocCost                     INT         DEFAULT 0,
    quarterlyTocCost                  INT         DEFAULT 0,
    started                           TIMESTAMP   DEFAULT NULL,
    numPlayers                        INT         DEFAULT 0,
    buyInCollected                    INT         DEFAULT 0,
    rebuyAddOnCollected               INT         DEFAULT 0,
    annualTocCollected                INT         DEFAULT 0,
    quarterlyTocCollected             INT         DEFAULT 0,
    totalCollected                    INT         DEFAULT 0,
    kittyCalculated                   INT         DEFAULT 0,
    annualTocFromRebuyAddOnCalculated INT         DEFAULT 0,
    rebuyAddOnLessAnnualTocCalculated INT         DEFAULT 0,
    totalCombinedTocCalculated        INT         DEFAULT 0,
    prizePotCalculated                INT         DEFAULT 0,
    payoutDelta                       INT         DEFAULT NULL,
    seasonGameNum                     INT,
    quarterlyGameNum                  INT,
    finalized                         BOOLEAN     DEFAULT FALSE,
    lastCalculated                    DATE        DEFAULT NULL,
    canRebuy                          BOOLEAN     DEFAULT TRUE,
    PRIMARY KEY (id)
);

create table if not exists gameplayer
(
    id                    INT auto_increment,
    playerId              INT         NOT NULL,
    gameId                INT         NOT NULL,
    qSeasonId             INT         NOT NULL,
    seasonId              INT         NOT NULL,
    name                  varchar(64) NOT NULL,
    place                 INT     DEFAULT NULL,
    points                INT     DEFAULT NULL,
    knockedOut            BOOLEAN DEFAULT FALSE,
    roundUpdates          BOOLEAN DEFAULT FALSE,
    buyInCollected        INT     DEFAULT NULL,
    rebuyAddOnCollected   INT     DEFAULT NULL,
    annualTocCollected    INT     DEFAULT NULL,
    quarterlyTocCollected INT     DEFAULT NULL,
    chop                  INT     DEFAULT NULL,
    primary key (id)
);

create table if not exists gamepayout
(
    id          INT auto_increment,
    gameId      INT NOT NULL,
    place       INT NOT NULL,
    amount      INT    DEFAULT NULL,
    chopAmount  INT    DEFAULT NULL,
    chopPercent DOUBLE DEFAULT NULL,
    PRIMARY KEY (id)
);

ALTER TABLE gamepayout
    ADD CONSTRAINT GPayout_Unique UNIQUE (gameId, place);

create table if not exists seasonpayout
(
    id       INT auto_increment,
    seasonId INT NOT NULL,
    place    INT NOT NULL,
    amount   INT DEFAULT NULL,
    PRIMARY KEY (id)
);

ALTER TABLE seasonpayout
    ADD CONSTRAINT SPayout_Unique UNIQUE (seasonId, place);

create table if not exists quarterlyseasonpayout
(
    id        INT auto_increment,
    seasonId  INT NOT NULL,
    qSeasonId INT NOT NULL,
    place     INT NOT NULL,
    amount    INT DEFAULT NULL,
    PRIMARY KEY (id)
);

ALTER TABLE quarterlyseasonpayout
    ADD CONSTRAINT QSPayout_Unique UNIQUE (seasonId, qSeasonId, place);

create table if not exists seating
(
    gameId   INT           NOT NULL,
    settings varchar(8192) NOT NULL,
    PRIMARY KEY (gameId)
);

create table if not exists settings
(
    id       INT auto_increment,
    settings varchar(1024),
    PRIMARY KEY (id)
);
INSERT INTO settings (id, settings)
VALUES (1, '{"uiVersion": "2.14"}');

create table if not exists role
(
    id          int auto_increment,
    description varchar(255),
    name        varchar(255),
    primary key (id)
);

INSERT INTO role (id, description, name)
VALUES (1, 'Admin role', 'ADMIN');
INSERT INTO role (id, description, name)
VALUES (2, 'User role', 'USER');

create table if not exists player_roles
(
    playerId int not null,
    roleId   int not null,
    primary key (playerId, roleId)
);

alter table player_roles
    add constraint fk_role_id foreign key (roleId) references role (id);

alter table player_roles
    add constraint fk_player_id foreign key (playerId) references player (id);

# Admins
INSERT INTO player_roles (playerId, roleId)
VALUES (1, 1);
INSERT INTO player_roles (playerId, roleId)
VALUES (23, 1);

INSERT INTO player_roles (playerId, roleId)
VALUES (1, 2);
INSERT INTO player_roles (playerId, roleId)
VALUES (2, 2);
INSERT INTO player_roles (playerId, roleId)
VALUES (3, 2);
INSERT INTO player_roles (playerId, roleId)
VALUES (4, 2);
INSERT INTO player_roles (playerId, roleId)
VALUES (5, 2);
INSERT INTO player_roles (playerId, roleId)
VALUES (6, 2);
INSERT INTO player_roles (playerId, roleId)
VALUES (7, 2);
INSERT INTO player_roles (playerId, roleId)
VALUES (8, 2);
INSERT INTO player_roles (playerId, roleId)
VALUES (9, 2);
INSERT INTO player_roles (playerId, roleId)
VALUES (10, 2);
INSERT INTO player_roles (playerId, roleId)
VALUES (11, 2);
INSERT INTO player_roles (playerId, roleId)
VALUES (12, 2);
INSERT INTO player_roles (playerId, roleId)
VALUES (13, 2);
INSERT INTO player_roles (playerId, roleId)
VALUES (14, 2);
INSERT INTO player_roles (playerId, roleId)
VALUES (15, 2);
INSERT INTO player_roles (playerId, roleId)
VALUES (16, 2);
INSERT INTO player_roles (playerId, roleId)
VALUES (17, 2);
INSERT INTO player_roles (playerId, roleId)
VALUES (18, 2);
INSERT INTO player_roles (playerId, roleId)
VALUES (19, 2);
INSERT INTO player_roles (playerId, roleId)
VALUES (20, 2);
INSERT INTO player_roles (playerId, roleId)
VALUES (21, 2);
INSERT INTO player_roles (playerId, roleId)
VALUES (22, 2);
INSERT INTO player_roles (playerId, roleId)
VALUES (23, 2);


create table if not exists payout
(
    numPayouts INT NOT NULL,
    place      INT NOT NULL,
    percent    DOUBLE DEFAULT NULL,
    PRIMARY KEY (numPayouts, place)
);
insert into payout (numPayouts, place, percent)
values (2, 1, 0.65);
insert into payout (numPayouts, place, percent)
values (2, 2, 0.35);
insert into payout (numPayouts, place, percent)
values (3, 1, 0.50);
insert into payout (numPayouts, place, percent)
values (3, 2, 0.30);
insert into payout (numPayouts, place, percent)
values (3, 3, 0.20);
insert into payout (numPayouts, place, percent)
values (4, 1, 0.45);
insert into payout (numPayouts, place, percent)
values (4, 2, 0.25);
insert into payout (numPayouts, place, percent)
values (4, 3, 0.18);
insert into payout (numPayouts, place, percent)
values (4, 4, 0.12);
insert into payout (numPayouts, place, percent)
values (5, 1, 0.40);
insert into payout (numPayouts, place, percent)
values (5, 2, 0.23);
insert into payout (numPayouts, place, percent)
values (5, 3, 0.16);
insert into payout (numPayouts, place, percent)
values (5, 4, 0.12);
insert into payout (numPayouts, place, percent)
values (5, 5, 0.09);
insert into payout (numPayouts, place, percent)
values (6, 1, 0.38);
insert into payout (numPayouts, place, percent)
values (6, 2, 0.22);
insert into payout (numPayouts, place, percent)
values (6, 3, 0.15);
insert into payout (numPayouts, place, percent)
values (6, 4, 0.11);
insert into payout (numPayouts, place, percent)
values (6, 5, 0.08);
insert into payout (numPayouts, place, percent)
values (6, 6, 0.06);
insert into payout (numPayouts, place, percent)
values (7, 1, 0.35);
insert into payout (numPayouts, place, percent)
values (7, 2, 0.21);
insert into payout (numPayouts, place, percent)
values (7, 3, 0.15);
insert into payout (numPayouts, place, percent)
values (7, 4, 0.11);
insert into payout (numPayouts, place, percent)
values (7, 5, 0.08);
insert into payout (numPayouts, place, percent)
values (7, 6, 0.06);
insert into payout (numPayouts, place, percent)
values (7, 7, 0.04);
insert into payout (numPayouts, place, percent)
values (8, 1, 0.335);
insert into payout (numPayouts, place, percent)
values (8, 2, 0.20);
insert into payout (numPayouts, place, percent)
values (8, 3, 0.145);
insert into payout (numPayouts, place, percent)
values (8, 4, 0.11);
insert into payout (numPayouts, place, percent)
values (8, 5, 0.08);
insert into payout (numPayouts, place, percent)
values (8, 6, 0.06);
insert into payout (numPayouts, place, percent)
values (8, 7, 0.04);
insert into payout (numPayouts, place, percent)
values (8, 8, 0.03);
insert into payout (numPayouts, place, percent)
values (9, 1, 0.32);
insert into payout (numPayouts, place, percent)
values (9, 2, 0.195);
insert into payout (numPayouts, place, percent)
values (9, 3, 0.14);
insert into payout (numPayouts, place, percent)
values (9, 4, 0.11);
insert into payout (numPayouts, place, percent)
values (9, 5, 0.08);
insert into payout (numPayouts, place, percent)
values (9, 6, 0.06);
insert into payout (numPayouts, place, percent)
values (9, 7, 0.04);
insert into payout (numPayouts, place, percent)
values (9, 8, 0.03);
insert into payout (numPayouts, place, percent)
values (9, 9, 0.025);
insert into payout (numPayouts, place, percent)
values (10, 1, 0.30);
insert into payout (numPayouts, place, percent)
values (10, 2, 0.19);
insert into payout (numPayouts, place, percent)
values (10, 3, 0.1325);
insert into payout (numPayouts, place, percent)
values (10, 4, 0.105);
insert into payout (numPayouts, place, percent)
values (10, 5, 0.075);
insert into payout (numPayouts, place, percent)
values (10, 6, 0.055);
insert into payout (numPayouts, place, percent)
values (10, 7, 0.0375);
insert into payout (numPayouts, place, percent)
values (10, 8, 0.03);
insert into payout (numPayouts, place, percent)
values (10, 9, 0.0225);
insert into payout (numPayouts, place, percent)
values (10, 10, 0.015);

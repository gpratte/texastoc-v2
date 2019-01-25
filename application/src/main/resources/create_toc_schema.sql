-- noinspection SqlNoDataSourceInspectionForFile

create table if not exists tocconfig (kittyDebit INT NOT NULL, annualTocCost INT NOT NULL, quarterlyTocCost INT NOT NULL, quarterlyNumPayouts INT NOT NULL, regularBuyInCost INT NOT NULL, regularRebuyCost INT NOT NULL, regularRebuyTocDebit INT NOT NULL, doubleBuyInCost INT NOT NULL, doubleRebuyCost INT NOT NULL, doubleRebuyTocDebit INT NOT NULL)

insert into tocconfig (kittyDebit, annualTocCost, quarterlyTocCost, quarterlyNumPayouts, regularBuyInCost, regularRebuyCost, regularRebuyTocDebit, doubleBuyInCost, doubleRebuyCost, doubleRebuyTocDebit) values (9, 8, 7, 3, 6, 5, 4, 12, 10, 8)

create table if not exists season (id INT auto_increment, startDate DATE, endDate DATE, kittyPerGame INT, tocPerGame INT, quarterlyTocPerGame INT, quarterlyTocPayouts INT, buyInCost INT, rebuyAddOnCost INT, rebuyAddOnTocDebit INT, doubleBuyInCost INT, doubleRebuyAddOnCost INT, doubleRebuyAddOnTocDebit INT, buyInCollected INT, rebuyAddOnCollected INT, annualTocCollected INT, totalCollected INT, annualTocFromRebuyAddOnCalculated INT, rebuyAddOnLessAnnualTocCalculated INT, totalCombinedAnnualTocCalculated INT, kittyCalculated INT, prizePotCalculated INT, numGames INT, numGamesPlayed INT, finalized BOOLEAN, lastCalculated DATE, primary key(id))

create table if not exists quarterlyseason (id INT auto_increment, seasonId INT NOT NULL, startDate DATE, endDate DATE, finalized BOOLEAN, quarter INT NOT NULL, numGames INT, numGamesPlayed INT, qTocCollected INT, qTocPerGame INT, numPayouts INT NOT NULL, lastCalculated DATE, primary key(id))

create table if not exists seasonplayer (playerId INT NOT NULL, seasonId INT NOT NULL, name varchar(64) DEFAULT NULL, entries INT DEFAULT 0, points INT DEFAULT 0, place INT DEFAULT 0, forfeit BOOLEAN DEFAULT false)

create table if not exists quarterlyseasonplayer (playerId INT NOT NULL, seasonId INT NOT NULL, qSeasonId INT NOT NULL, name varchar(64) DEFAULT NULL, entries INT DEFAULT 0, points INT DEFAULT 0, place INT)

create table if not exists supply (id INT auto_increment, amount INT NOT NULL, date DATE NOT NULL, type varchar(16) NOT NULL, description varchar(64), primary key(id))

create table if not exists player (id INT auto_increment, firstName varchar(32) DEFAULT NULL, lastName varchar(32) DEFAULT NULL, phone varchar(32) DEFAULT NULL, email varchar(64) DEFAULT NULL, password varchar(255) DEFAULT NULL, primary key (id))

insert into player (id, firstName, lastName, phone, email) values (1, 'Brian', 'Baker', '5121231234', 'brianbaker@texastoc.com')
insert into player (id, firstName, lastName, phone, email) values (2, 'Andy', 'Thomas', '5121231235', 'andythomas@texastoc.com')
-- password is password
insert into player (id, firstName, lastName, phone, email, password) values (3, 'Gil', 'Pratte', '5121231235', 'gilpratte@texastoc.com', '$2a$10$qXQo4z4oXKPEKyYO7bAQmOQ9PhIcHK4LOo/L1U9j/xkLEmseLWECK')

create table game (id INT AUTO_INCREMENT, seasonId INT NOT NULL, qSeasonId INT NOT NULL, hostId INT DEFAULT NULL, gameDate DATE NOT NULL, hostName varchar(64) DEFAULT NULL, quarter INT DEFAULT NULL, doubleBuyIn BOOLEAN DEFAULT FALSE, transportRequired BOOLEAN DEFAULT FALSE, kittyCost INT DEFAULT 0, buyInCost INT DEFAULT 0, rebuyAddOnCost INT DEFAULT 0, rebuyAddOnTocDebit INT DEFAULT 0, annualTocCost INT DEFAULT 0, quarterlyTocCost INT DEFAULT 0, started TIMESTAMP DEFAULT NULL, numPlayers INT DEFAULT 0, buyInCollected INT DEFAULT 0, rebuyAddOnCollected INT DEFAULT 0, annualTocCollected INT DEFAULT 0, quarterlyTocCollected INT DEFAULT 0, totalCollected INT DEFAULT 0, kittyCalculated INT DEFAULT 0, annualTocFromRebuyAddOnCalculated INT DEFAULT 0, rebuyAddOnLessAnnualTocCalculated INT DEFAULT 0, totalCombinedTocCalculated INT DEFAULT 0, prizePotCalculated INT DEFAULT 0, payoutDelta INT DEFAULT NULL, finalized BOOLEAN DEFAULT FALSE, lastCalculated DATE DEFAULT NULL, PRIMARY KEY (id))

create table if not exists gameplayer (id INT auto_increment, playerId INT NOT NULL, gameId INT NOT NULL, qSeasonId INT NOT NULL, seasonId INT NOT NULL, name varchar(64) NOT NULL, place INT DEFAULT NULL, points INT DEFAULT NULL, finish INT DEFAULT NULL, knockedOut BOOLEAN DEFAULT FALSE, roundUpdates BOOLEAN DEFAULT FALSE, buyInCollected INT DEFAULT NULL, rebuyAddOnCollected INT DEFAULT NULL, annualTocCollected INT DEFAULT NULL, quarterlyTocCollected INT DEFAULT NULL, chop INT DEFAULT NULL, primary key (id))

create table if not exists gamepayout (gameId INT NOT NULL, place INT NOT NULL, amount INT DEFAULT NULL, chopAmount INT DEFAULT NULL, chopPercent DOUBLE DEFAULT NULL, PRIMARY KEY (gameId, place))

create table if not exists seasonpayout (seasonId INT NOT NULL, place INT NOT NULL, amount INT DEFAULT NULL, PRIMARY KEY (seasonId, place))

create table if not exists quarterlyseasonpayout (seasonId INT NOT NULL, qSeasonId INT NOT NULL, place INT NOT NULL, amount INT DEFAULT NULL, PRIMARY KEY (seasonId, qSeasonId, place))

create table if not exists gameseat (gameId INT NOT NULL, seatNumber INT NOT NULL, tableNumber INT NOT NULL, gamePlayerId INT, gamePlayerName varchar(64), PRIMARY KEY (gameId, seatNumber, tableNumber))

create table if not exists role (id int auto_increment, description varchar(255), name varchar(255), primary key (id));

INSERT INTO role (id, description, name) VALUES (1, 'Admin role', 'ADMIN');
INSERT INTO role (id, description, name) VALUES (2, 'User role', 'USER');

create table if not exists player_roles (player_id int not null, role_id int not null, primary key (player_id, role_id));

alter table player_roles add constraint fk_role_id foreign key (role_id) references role (id);

alter table player_roles add constraint fk_player_id foreign key (player_id) references player (id);

INSERT INTO player_roles (player_id, role_id) VALUES (3, 1);


create table if not exists payout (numPayouts INT NOT NULL, place INT NOT NULL, percent DOUBLE DEFAULT NULL, PRIMARY KEY (numPayouts, place))
insert into payout (numPayouts, place, percent) values (2, 1, 0.65)
insert into payout (numPayouts, place, percent) values (2, 2, 0.35)
insert into payout (numPayouts, place, percent) values (3, 1, 0.50)
insert into payout (numPayouts, place, percent) values (3, 2, 0.30)
insert into payout (numPayouts, place, percent) values (3, 3, 0.20)
insert into payout (numPayouts, place, percent) values (4, 1, 0.45)
insert into payout (numPayouts, place, percent) values (4, 2, 0.25)
insert into payout (numPayouts, place, percent) values (4, 3, 0.18)
insert into payout (numPayouts, place, percent) values (4, 4, 0.12)
insert into payout (numPayouts, place, percent) values (5, 1, 0.40)
insert into payout (numPayouts, place, percent) values (5, 2, 0.23)
insert into payout (numPayouts, place, percent) values (5, 3, 0.16)
insert into payout (numPayouts, place, percent) values (5, 4, 0.12)
insert into payout (numPayouts, place, percent) values (5, 5, 0.09)
insert into payout (numPayouts, place, percent) values (6, 1, 0.38)
insert into payout (numPayouts, place, percent) values (6, 2, 0.22)
insert into payout (numPayouts, place, percent) values (6, 3, 0.15)
insert into payout (numPayouts, place, percent) values (6, 4, 0.11)
insert into payout (numPayouts, place, percent) values (6, 5, 0.08)
insert into payout (numPayouts, place, percent) values (6, 6, 0.06)
insert into payout (numPayouts, place, percent) values (7, 1, 0.35)
insert into payout (numPayouts, place, percent) values (7, 2, 0.21)
insert into payout (numPayouts, place, percent) values (7, 3, 0.15)
insert into payout (numPayouts, place, percent) values (7, 4, 0.11)
insert into payout (numPayouts, place, percent) values (7, 5, 0.08)
insert into payout (numPayouts, place, percent) values (7, 6, 0.06)
insert into payout (numPayouts, place, percent) values (7, 7, 0.04)
insert into payout (numPayouts, place, percent) values (8, 1, 0.335)
insert into payout (numPayouts, place, percent) values (8, 2, 0.20)
insert into payout (numPayouts, place, percent) values (8, 3, 0.145)
insert into payout (numPayouts, place, percent) values (8, 4, 0.11)
insert into payout (numPayouts, place, percent) values (8, 5, 0.08)
insert into payout (numPayouts, place, percent) values (8, 6, 0.06)
insert into payout (numPayouts, place, percent) values (8, 7, 0.04)
insert into payout (numPayouts, place, percent) values (8, 8, 0.03)
insert into payout (numPayouts, place, percent) values (9, 1, 0.32)
insert into payout (numPayouts, place, percent) values (9, 2, 0.195)
insert into payout (numPayouts, place, percent) values (9, 3, 0.14)
insert into payout (numPayouts, place, percent) values (9, 4, 0.11)
insert into payout (numPayouts, place, percent) values (9, 5, 0.08)
insert into payout (numPayouts, place, percent) values (9, 6, 0.06)
insert into payout (numPayouts, place, percent) values (9, 7, 0.04)
insert into payout (numPayouts, place, percent) values (9, 8, 0.03)
insert into payout (numPayouts, place, percent) values (9, 9, 0.025)
insert into payout (numPayouts, place, percent) values (10, 1, 0.30)
insert into payout (numPayouts, place, percent) values (10, 2, 0.19)
insert into payout (numPayouts, place, percent) values (10, 3, 0.1325)
insert into payout (numPayouts, place, percent) values (10, 4, 0.105)
insert into payout (numPayouts, place, percent) values (10, 5, 0.075)
insert into payout (numPayouts, place, percent) values (10, 6, 0.055)
insert into payout (numPayouts, place, percent) values (10, 7, 0.0375)
insert into payout (numPayouts, place, percent) values (10, 8, 0.03)
insert into payout (numPayouts, place, percent) values (10, 9, 0.0225)
insert into payout (numPayouts, place, percent) values (10, 10, 0.015)

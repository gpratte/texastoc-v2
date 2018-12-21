
create table if not exists tocconfig (kittyDebit INT NOT NULL, annualTocCost INT NOT NULL, quarterlyTocCost INT NOT NULL, quarterlyNumPayouts INT NOT NULL, regularBuyInCost INT NOT NULL, regularRebuyCost INT NOT NULL, regularRebuyTocDebit INT NOT NULL, doubleBuyInCost INT NOT NULL, doubleRebuyCost INT NOT NULL, doubleRebuyTocDebit INT NOT NULL)

insert into tocconfig (kittyDebit, annualTocCost, quarterlyTocCost, quarterlyNumPayouts, regularBuyInCost, regularRebuyCost, regularRebuyTocDebit, doubleBuyInCost, doubleRebuyCost, doubleRebuyTocDebit) values (9, 8, 7, 3, 6, 5, 4, 12, 10, 8)

create table if not exists season (id INT auto_increment, startDate DATE, endDate DATE, finalized BOOLEAN, numGames INT, numGamesPlayed INT, buyInCost INT, rebuyAddOnCost INT, rebuyAddOnTocDebit INT, doubleBuyInCost INT, doubleRebuyAddOnCost INT, doubleRebuyAddOnTocDebit INT, buyInCollected INT, rebuyAddOnCollected INT, tocCollected INT, tocPerGame INT, kittyPerGame INT, quarterlyTocPerGame INT, quarterlyTocPayouts INT, lastCalculated DATE, primary key(id))

create table if not exists quarterlyseason (id INT auto_increment, seasonId INT NOT NULL, startDate DATE, endDate DATE, finalized BOOLEAN, quarter INT NOT NULL, numGames INT, numGamesPlayed INT, totalQuarterlyToc INT, tocPerGame INT, numPayouts INT NOT NULL, lastCalculated DATE, primary key(id))

create table if not exists player (id INT auto_increment, firstName varchar(32) DEFAULT NULL, lastName varchar(32) DEFAULT NULL, phone varchar(32) DEFAULT NULL, email varchar(64) DEFAULT NULL, primary key (id))

insert into player (id, firstName, lastName, phone, email) values (1, 'Brian', 'Baker', '5121231234', 'brianbaker@texastoc.com')

create table game (id INT AUTO_INCREMENT, seasonId INT NOT NULL, qSeasonId INT NOT NULL, hostId INT DEFAULT NULL, gameDate DATE NOT NULL, hostName varchar(64) DEFAULT NULL, quarter INT DEFAULT NULL, doubleBuyIn BOOLEAN DEFAULT FALSE, transportRequired BOOLEAN DEFAULT FALSE, kittyCost INT DEFAULT 0, buyInCost INT DEFAULT 0, rebuyAddOnCost INT DEFAULT 0, rebuyAddOnTocDebit INT DEFAULT 0, annualTocCost INT DEFAULT 0, quarterlyTocCost INT DEFAULT 0, started TIMESTAMP DEFAULT NULL, numPlayers INT DEFAULT 0, kittyCollected INT DEFAULT 0, buyInCollected INT DEFAULT 0, rebuyAddOnCollected INT DEFAULT 0, annualTocCollected INT DEFAULT 0, quarterlyTocCollected INT DEFAULT 0, finalized BOOLEAN DEFAULT FALSE, lastCalculated DATE DEFAULT NULL, PRIMARY KEY (id))

create table if not exists gameplayer (id INT auto_increment, playerId INT NOT NULL, gameId INT NOT NULL, name varchar(64) NOT NULL, place INT DEFAULT NULL, points INT DEFAULT NULL, finish INT DEFAULT NULL, knockedOut BOOLEAN DEFAULT FALSE, roundUpdates BOOLEAN DEFAULT FALSE, buyInCollected INT DEFAULT NULL, rebuyAddOnCollected INT DEFAULT NULL, annualTocCollected INT DEFAULT NULL, quarterlyTocCollected INT DEFAULT NULL, chop INT DEFAULT NULL, primary key (id))

create table if not exists gamepayout (gameId INT NOT NULL, place INT NOT NULL, amount INT DEFAULT NULL, chopAmount INT DEFAULT NULL, chopPercent DOUBLE DEFAULT NULL, PRIMARY KEY (gameId, place))




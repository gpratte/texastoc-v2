
create table if not exists tocconfig (kittyDebit INT NOT NULL, annualTocCost INT NOT NULL, quarterlyTocCost INT NOT NULL, quarterlyNumPayouts INT NOT NULL, gameRegularBuyInCost INT NOT NULL, gameRegularRebuyCost INT NOT NULL, gameRegularRebuyTocDebit INT NOT NULL, gameDoubleBuyInCost INT NOT NULL, gameDoubleRebuyCost INT NOT NULL, gameDoubleRebuyTocDebit INT NOT NULL)

insert into tocconfig (kittyDebit, annualTocCost, quarterlyTocCost, quarterlyNumPayouts, gameRegularBuyInCost, gameRegularRebuyCost, gameRegularRebuyTocDebit, gameDoubleBuyInCost, gameDoubleRebuyCost, gameDoubleRebuyTocDebit) values (9, 8, 7, 3, 6, 5, 4, 12, 10, 8)

create table if not exists season (id INT auto_increment, startDate DATE, endDate DATE, finalized BOOLEAN, numGames INT, numGamesPlayed INT, totalBuyIn INT, totalReBuy INT, totalAnnualToc INT, annualTocAmount INT, kittyPerGame INT, quarterlyTocAmount INT, quarterlyTocPayouts INT, lastCalculated DATE, primary key(id))

create table if not exists quarterlyseason (id INT auto_increment, seasonId INT NOT NULL, startDate DATE, endDate DATE, finalized BOOLEAN, quarter INT NOT NULL, numGames INT, numGamesPlayed INT, totalQuarterlyToc INT, tocPerGame INT, numPayouts INT NOT NULL, lastCalculated DATE, primary key(id))

create table if not exists player (id INT auto_increment, firstName varchar(32) DEFAULT NULL, lastName varchar(32) DEFAULT NULL, phone varchar(32) DEFAULT NULL, email varchar(64) DEFAULT NULL, primary key (id))

insert into player (id, firstName, lastName, phone, email) values (1, 'Brian', 'Baker', '5121231234', 'brianbaker@texastoc.com')

create table game (id INT AUTO_INCREMENT, seasonId INT NOT NULL, qSeasonId INT NOT NULL, hostId INT DEFAULT NULL, gameDate DATE NOT NULL, hostName varchar(64) DEFAULT NULL, quarter INT DEFAULT NULL, doubleBuyIn BOOLEAN DEFAULT FALSE, transportRequired BOOLEAN DEFAULT FALSE, kittyCost INT DEFAULT 0, buyInCost INT DEFAULT 0, rebuyAddOnCost INT DEFAULT 0, rebuyAddOnTocDebit INT DEFAULT 0, annualTocCost INT DEFAULT 0, quarterlyTocCost INT DEFAULT 0, started TIMESTAMP DEFAULT NULL, numPlayers INT DEFAULT 0, kittyCollected INT DEFAULT 0, buyInCollected INT DEFAULT 0, rebuyAddOnCollected INT DEFAULT 0, annualTocCollected INT DEFAULT 0, quarterlyTocCollected INT DEFAULT 0, finalized BOOLEAN DEFAULT FALSE, lastCalculated DATE DEFAULT NULL, PRIMARY KEY (id))

create table if not exists season (id INT auto_increment, startDate DATE, endDate DATE, finalized BOOLEAN, numGames INT, numGamesPlayed INT, totalBuyIn INT, totalReBuy INT, totalAnnualToc INT, annualTocAmount INT, kittyPerGame INT, quarterlyTocAmount INT, quarterlyTocPayouts INT, lastCalculated DATE, primary key(id))

create table if not exists quarterlyseason (id INT auto_increment, seasonId INT NOT NULL, startDate DATE, endDate DATE, finalized BOOLEAN, quarter INT NOT NULL, numGames INT, numGamesPlayed INT, totalQuarterlyToc INT, tocPerGame INT, numPayouts INT NOT NULL, lastCalculated DATE, primary key(id))

create table if not exists player (id INT auto_increment, firstName varchar(32) DEFAULT NULL, lastName varchar(32) DEFAULT NULL, phone varchar(32) DEFAULT NULL, email varchar(64) DEFAULT NULL, primary key (id))

insert into player (id, firstName, lastName, phone, email) values (1, 'Brian', 'Baker', '5121231234', 'brianbaker@texastoc.com')

create table game (id INT AUTO_INCREMENT, seasonId INT NOT NULL, gameDate DATE NOT NULL, hostId INT DEFAULT NULL, numPlayers INT DEFAULT '0', totalBuyIn INT DEFAULT '0', totalReBuy INT DEFAULT '0', totalAnnualToc INT DEFAULT '0', totalQuarterlyToc INT DEFAULT '0', finalized BOOLEAN DEFAULT FALSE, doubleBuyIn BOOLEAN DEFAULT FALSE, lastCalculated DATE DEFAULT NULL, quarter INT DEFAULT NULL, kittyDebit INT DEFAULT '0', startTime TIMESTAMP DEFAULT NULL, transportRequired BOOLEAN DEFAULT FALSE, PRIMARY KEY (`id`))

create table if not exists season (id INT auto_increment, startDate DATE, endDate DATE, finalized BOOLEAN, numGames INT, numGamesPlayed INT, totalBuyIn INT, totalReBuy INT, totalAnnualToc INT, annualTocAmount INT, kittyPerGame INT, quarterlyTocAmount INT, quarterlyTocPayouts INT, lastCalculated DATE, primary key(id))

create table if not exists quarterlyseason (id INT auto_increment, seasonId INT NOT NULL, startDate DATE, endDate DATE, finalized BOOLEAN, quarter INT NOT NULL, numGames INT, numGamesPlayed INT, totalQuarterlyToc INT, tocPerGame INT, numPayouts INT NOT NULL, lastCalculated DATE, primary key(id))


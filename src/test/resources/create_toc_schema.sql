create table season (id INT auto_increment, startDate DATE, endDate DATE, numGames INT, numGamesPlayed INT, buyInCollected INT, rebuyAddOnCollected INT, tocCollected INT, tocPerGame INT, kittyPerGame INT, quarterlyTocPerGame INT, quarterlyNumPayouts INT, primary key(id))

create table quarterlyseason (id INT auto_increment, seasonId INT NOT NULL, startDate DATE, endDate DATE, quarter INT NOT NULL, numGames INT, numGamesPlayed INT, tocCollected INT, tocPerGame INT, numPayouts INT NOT NULL, primary key(id))


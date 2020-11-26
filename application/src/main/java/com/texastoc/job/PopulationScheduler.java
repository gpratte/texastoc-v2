package com.texastoc.job;

import com.texastoc.controller.request.CreateGamePlayerRequest;
import com.texastoc.controller.request.UpdateGamePlayerRequest;
import com.texastoc.model.game.FirstTimeGamePlayer;
import com.texastoc.model.game.Game;
import com.texastoc.model.game.GamePlayer;
import com.texastoc.model.season.Season;
import com.texastoc.model.user.Player;
import com.texastoc.service.GameService;
import com.texastoc.service.PlayerService;
import com.texastoc.service.SeasonService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * When running with an embedded H2 database populate the current season with games.
 */
@Profile("!mysql")
@Slf4j
@Component
public class PopulationScheduler {

  private final SeasonService seasonService;
  private final GameService gameService;
  private final PlayerService playerService;

  private final Random random = new Random(System.currentTimeMillis());

  public PopulationScheduler(SeasonService seasonService, GameService gameService, PlayerService playerService) {
    this.seasonService = seasonService;
    this.gameService = gameService;
    this.playerService = playerService;
  }

  // delay one minute then run every hour
  @Scheduled(fixedDelay = 3600000, initialDelay = 60000)
  public void populate() {
    createSeason();
  }

  private void createSeason() {
    LocalDate now = LocalDate.now();

    try {
      List<Season> seasons = seasonService.getSeasons();
      if (seasons.size() > 0) {
        return;
      }
      int year;
      switch (now.getMonth()) {
        case JANUARY:
        case FEBRUARY:
        case MARCH:
        case APRIL:
          // if before May then create for the previous year
          year = now.getYear() - 1;
          break;
        default:
          year = now.getYear();
      }
      Season season = seasonService.createSeason(year);
      log.info("created season " + season.getStart());
      createGames(season);
    } catch (Exception e) {
      // do nothing
    }
  }

  private void createGames(Season season) {
    LocalDate now = LocalDate.now();

    LocalDate seasonStart = season.getStart();
    LocalDate gameDate = findNextThursday(seasonStart);

    Player player = playerService.get().get(0);
    while (!gameDate.isAfter(now)) {
      Game game = gameService.createGame(Game.builder()
        .hostId(player.getId())
        .date(gameDate)
        .doubleBuyIn(false)
        .transportRequired(false)
        .build());

      addGamePlayers(game);

      // Is this the last game? Check if the next game is after now.
      LocalDate nextGameDate = findNextThursday(gameDate.plusDays(1));
      if (!nextGameDate.isAfter(now)) {
        // Add some rebuys
        game = gameService.getGame(game.getId());
        List<GamePlayer> gamePlayers = game.getPlayers();
        for (GamePlayer gamePlayer : gamePlayers) {
          if (random.nextBoolean()) {
            UpdateGamePlayerRequest ugpr = new UpdateGamePlayerRequest();
            ugpr.setBuyInCollected(true);
            ugpr.setRebuyAddOnCollected(true);
            Integer annualTocCollect = gamePlayer.getAnnualTocCollected();
            if (annualTocCollect != null && annualTocCollect > 0) {
              ugpr.setAnnualTocCollected(true);
            }
            Integer qAnnualTocCollect = gamePlayer.getQuarterlyTocCollected();
            if (qAnnualTocCollect != null && qAnnualTocCollect > 0) {
              ugpr.setQuarterlyTocCollected(true);
            }
            gameService.updateGamePlayer(game.getId(), gamePlayer.getId(), ugpr);
          }
        }

        // finalize the game
        gameService.endGame(game.getId());
      }
      gameDate = findNextThursday(gameDate.plusDays(1));
    }
  }

  private void addGamePlayers(Game game) {
    int numPlayersToAddToGame = game.getDate().getDayOfMonth();
    if (numPlayersToAddToGame < 2) {
      numPlayersToAddToGame = 2;
    }

    List<Player> existingPlayers = playerService.get();

    // Always add two new players to the game
    for (int i = 0; i < 2; i++) {
      addNewPlayer(game);
    }
    numPlayersToAddToGame -= 2;

    if (existingPlayers.size() >= numPlayersToAddToGame) {
      // use the existing players
      List<Integer> existingPlayersIdsInGame = new ArrayList<>(existingPlayers.size());

      // Grab an existing player if not already added to game
      while (numPlayersToAddToGame > 0) {
        Player existingPlayer = existingPlayers.get(random.nextInt(existingPlayers.size()));
        if (existingPlayersIdsInGame.contains(existingPlayer.getId())) {
          continue;
        }
        // Add existing player to the game
        addExistingPlayer(game, existingPlayer);
        existingPlayersIdsInGame.add(existingPlayer.getId());
        --numPlayersToAddToGame;
      }
    } else {
      // not enough existing players so use all existing players and then add new players
      for (Player existingPlayer : existingPlayers) {
        addExistingPlayer(game, existingPlayer);
        --numPlayersToAddToGame;
      }

      // now add new players
      for (int i = 0; i < numPlayersToAddToGame; i++) {
        addNewPlayer(game);
      }
    }
  }

  private void addExistingPlayer(Game game, Player existingPlayer) {
    CreateGamePlayerRequest cgpr = new CreateGamePlayerRequest();
    cgpr.setPlayerId(existingPlayer.getId());
    cgpr.setBuyInCollected(true);
    cgpr.setAnnualTocCollected(random.nextBoolean());
    cgpr.setQuarterlyTocCollected(random.nextBoolean());
    gameService.createGamePlayer(game.getId(), cgpr);
  }

  private void addNewPlayer(Game game) {
    FirstTimeGamePlayer ftgp = new FirstTimeGamePlayer();
    int firstNameIndex = random.nextInt(300);
    ftgp.setFirstName(firstNames[firstNameIndex]);
    int lastNameIndex = random.nextInt(300);
    ftgp.setLastName(lastNames[lastNameIndex]);
    ftgp.setBuyInCollected(true);
    ftgp.setAnnualTocCollected(random.nextBoolean());
    ftgp.setQuarterlyTocCollected(random.nextBoolean());
    gameService.createFirstTimeGamePlayer(game.getId(), ftgp);
  }

  private LocalDate findNextThursday(LocalDate date) {
    while (date.getDayOfWeek() != DayOfWeek.THURSDAY) {
      date = date.plusDays(1);
    }
    return date;
  }

  static final String[] firstNames = {"James", "John", "Robert", "Michael", "Mary", "William", "David", "Joseph", "Richard", "Charles", "Thomas", "Christopher", "Daniel", "Elizabeth", "Matthew", "Patricia", "George", "Jennifer", "Linda", "Anthony", "Barbara", "Donald", "Paul", "Mark", "Andrew", "Edward", "Steven", "Kenneth", "Margaret", "Joshua", "Kevin", "Brian", "Susan", "Dorothy", "Ronald", "Sarah", "Timothy", "Jessica", "Jason", "Helen", "Nancy", "Betty", "Karen", "Jeffrey", "Lisa", "Ryan", "Jacob", "Frank", "Gary", "Nicholas", "Anna", "Eric", "Sandra", "Stephen", "Emily", "Ashley", "Jonathan", "Kimberly", "Donna", "Ruth", "Carol", "Michelle", "Larry", "Laura", "Amanda", "Justin", "Raymond", "Scott", "Samuel", "Brandon", "Melissa", "Benjamin", "Rebecca", "Deborah", "Stephanie", "Sharon", "Kathleen", "Cynthia", "Gregory", "Jack", "Amy", "Henry", "Shirley", "Patrick", "Alexander", "Emma", "Angela", "Catherine", "Virginia", "Katherine", "Walter", "Dennis", "Jerry", "Brenda", "Pamela", "Frances", "Tyler", "Nicole", "Christine", "Aaron", "Peter", "Samantha", "Evelyn", "Jose", "Rachel", "Alice", "Douglas", "Janet", "Carolyn", "Adam", "Debra", "Harold", "Nathan", "Martha", "Maria", "Marie", "Zachary", "Arthur", "Heather", "Diane", "Julie", "Joyce", "Carl", "Grace", "Victoria", "Albert", "Rose", "Joan", "Kyle", "Christina", "Kelly", "Ann", "Lauren", "Doris", "Julia", "Jean", "Lawrence", "Judith", "Olivia", "Kathryn", "Joe", "Mildred", "Willie", "Gerald", "Lillian", "Roger", "Cheryl", "Megan", "Jeremy", "Keith", "Hannah", "Andrea", "Ethan", "Sara", "Terry", "Jacqueline", "Christian", "Harry", "Jesse", "Sean", "Teresa", "Ralph", "Austin", "Gloria", "Janice", "Roy", "Theresa", "Louis", "Noah", "Bruce", "Billy", "Judy", "Bryan", "Madison", "Eugene", "Beverly", "Jordan", "Denise", "Jane", "Marilyn", "Amber", "Dylan", "Danielle", "Abigail", "Charlotte", "Diana", "Brittany", "Russell", "Natalie", "Wayne", "Irene", "Ruby", "Annie", "Sophia", "Alan", "Juan", "Gabriel", "Howard", "Fred", "Vincent", "Lori", "Philip", "Kayla", "Alexis", "Tiffany", "Florence", "Isabella", "Kathy", "Louise", "Logan", "Lois", "Tammy", "Crystal", "Randy", "Bonnie", "Phyllis", "Anne", "Taylor", "Victor", "Bobby", "Erin", "Johnny", "Phillip", "Martin", "Josephine", "Alyssa", "Bradley", "Ella", "Shawn", "Clarence", "Travis", "Ernest", "Stanley", "Allison", "Craig", "Shannon", "Elijah", "Edna", "Peggy", "Tina", "Leonard", "Robin", "Dawn", "Carlos", "Earl", "Eleanor", "Jimmy", "Francis", "Cody", "Caleb", "Mason", "Rita", "Danny", "Isaac", "Audrey", "Todd", "Wanda", "Clara", "Ethel", "Paula", "Cameron", "Norma", "Dale", "Ellen", "Luis", "Alex", "Marjorie", "Luke", "Jamie", "Nathaniel", "Allen", "Leslie", "Joel", "Evan", "Edith", "Connie", "Eva", "Gladys", "Carrie", "Ava", "Frederick", "Wendy", "Hazel", "Valerie", "Curtis", "Elaine", "Courtney", "Esther", "Cindy", "Vanessa", "Brianna", "Lucas", "Norman", "Marvin", "Tracy", "Tony", "Monica", "Antonio", "Glenn", "Melanie"};

  static final String[] lastNames = {"SMITH", "JOHNSON", "WILLIAMS", "JONES", "BROWN", "DAVIS", "MILLER", "WILSON", "MOORE", "TAYLOR", "ANDERSON", "THOMAS", "JACKSON", "WHITE", "HARRIS", "MARTIN", "THOMPSON", "GARCIA", "MARTINEZ", "ROBINSON", "CLARK", "RODRIGUEZ", "LEWIS", "LEE", "WALKER", "HALL", "ALLEN", "YOUNG", "HERNANDEZ", "KING", "WRIGHT", "LOPEZ", "HILL", "SCOTT", "GREEN", "ADAMS", "BAKER", "GONZALEZ", "NELSON", "CARTER", "MITCHELL", "PEREZ", "ROBERTS", "TURNER", "PHILLIPS", "CAMPBELL", "PARKER", "EVANS", "EDWARDS", "COLLINS", "STEWART", "SANCHEZ", "MORRIS", "ROGERS", "REED", "COOK", "MORGAN", "BELL", "MURPHY", "BAILEY", "RIVERA", "COOPER", "RICHARDSON", "COX", "HOWARD", "WARD", "TORRES", "PETERSON", "GRAY", "RAMIREZ", "JAMES", "WATSON", "BROOKS", "KELLY", "SANDERS", "PRICE", "BENNETT", "WOOD", "BARNES", "ROSS", "HENDERSON", "COLEMAN", "JENKINS", "PERRY", "POWELL", "LONG", "PATTERSON", "HUGHES", "FLORES", "WASHINGTON", "BUTLER", "SIMMONS", "FOSTER", "GONZALES", "BRYANT", "ALEXANDER", "RUSSELL", "GRIFFIN", "DIAZ", "HAYES", "MYERS", "FORD", "HAMILTON", "GRAHAM", "SULLIVAN", "WALLACE", "WOODS", "COLE", "WEST", "JORDAN", "OWENS", "REYNOLDS", "FISHER", "ELLIS", "HARRISON", "GIBSON", "MCDONALD", "CRUZ", "MARSHALL", "ORTIZ", "GOMEZ", "MURRAY", "FREEMAN", "WELLS", "WEBB", "SIMPSON", "STEVENS", "TUCKER", "PORTER", "HUNTER", "HICKS", "CRAWFORD", "HENRY", "BOYD", "MASON", "MORALES", "KENNEDY", "WARREN", "DIXON", "RAMOS", "REYES", "BURNS", "GORDON", "SHAW", "HOLMES", "RICE", "ROBERTSON", "HUNT", "BLACK", "DANIELS", "PALMER", "MILLS", "NICHOLS", "GRANT", "KNIGHT", "FERGUSON", "ROSE", "STONE", "HAWKINS", "DUNN", "PERKINS", "HUDSON", "SPENCER", "GARDNER", "STEPHENS", "PAYNE", "PIERCE", "BERRY", "MATTHEWS", "ARNOLD", "WAGNER", "WILLIS", "RAY", "WATKINS", "OLSON", "CARROLL", "DUNCAN", "SNYDER", "HART", "CUNNINGHAM", "BRADLEY", "LANE", "ANDREWS", "RUIZ", "HARPER", "FOX", "RILEY", "ARMSTRONG", "CARPENTER", "WEAVER", "GREENE", "LAWRENCE", "ELLIOTT", "CHAVEZ", "SIMS", "AUSTIN", "PETERS", "KELLEY", "FRANKLIN", "LAWSON", "FIELDS", "GUTIERREZ", "RYAN", "SCHMIDT", "CARR", "VASQUEZ", "CASTILLO", "WHEELER", "CHAPMAN", "OLIVER", "MONTGOMERY", "RICHARDS", "WILLIAMSON", "JOHNSTON", "BANKS", "MEYER", "BISHOP", "MCCOY", "HOWELL", "ALVAREZ", "MORRISON", "HANSEN", "FERNANDEZ", "GARZA", "HARVEY", "LITTLE", "BURTON", "STANLEY", "NGUYEN", "GEORGE", "JACOBS", "REID", "KIM", "FULLER", "LYNCH", "DEAN", "GILBERT", "GARRETT", "ROMERO", "WELCH", "LARSON", "FRAZIER", "BURKE", "HANSON", "DAY", "MENDOZA", "MORENO", "BOWMAN", "MEDINA", "FOWLER", "BREWER", "HOFFMAN", "CARLSON", "SILVA", "PEARSON", "HOLLAND", "DOUGLAS", "FLEMING", "JENSEN", "VARGAS", "BYRD", "DAVIDSON", "HOPKINS", "MAY", "TERRY", "HERRERA", "WADE", "SOTO", "WALTERS", "CURTIS", "NEAL", "CALDWELL", "LOWE", "JENNINGS", "BARNETT", "GRAVES", "JIMENEZ", "HORTON", "SHELTON", "BARRETT", "OBRIEN", "CASTRO", "SUTTON", "GREGORY", "MCKINNEY", "LUCAS", "MILES", "CRAIG", "RODRIQUEZ", "CHAMBERS", "HOLT", "LAMBERT", "FLETCHER", "WATTS", "BATES", "HALE", "RHODES", "PENA", "BECK", "NEWMAN"
  };
}

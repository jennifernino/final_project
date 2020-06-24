package edu.brown.cs.student.client;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableMap;

import edu.brown.cs.student.sonicSkillz.Game;
import edu.brown.cs.student.sonicSkillz.gameunits.GameSession;
import edu.brown.cs.student.sonicSkillz.gameunits.Track;
import edu.brown.cs.student.sonicSkillz.gameunits.User;
import spark.ExceptionHandler;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.TemplateViewRoute;

/**
 * Class for GUIHandlers.
 *
 */
public final class GUIHandlers {
  /**
   * Constructor for GUIHandlers.
   */
  public GUIHandlers() {
  }

  /**
   * Displays the homepage of our application.
   *
   */
  protected static class HomepageHandler implements TemplateViewRoute {
    @Override
    public ModelAndView handle(Request request, Response res) {
      Map<String, Object> variables = ImmutableMap.of("title", "Sonic Skillz", "showScoreboard",
          false);
      String userId = request.cookies().get("userID");
      String gameCode = request.cookies().get("gameCode");
      // TODO: Check
      if (userId != null && gameCode != null) {
        if (GameHandlers.userIdToUser.containsKey(userId)) {
          User user = GameHandlers.userIdToUser.get(userId);
          System.out.println("nickname: " + user.getNickname());
        }
        GameHandlers.gameCleanup(userId, gameCode);
        if (gameCode != null) {
          res.removeCookie("gameCode");
        }
      }

      return new ModelAndView(variables, "Home.ftl");
    }
  }

  /**
   * Displays after a user chooses to play guess the song.
   *
   */
  protected static class GuessTheSongHandler implements TemplateViewRoute {
    @Override
    public ModelAndView handle(Request request, Response res) {
      Map<String, Object> variables = ImmutableMap.of("title", "Guess the Song", "showScoreboard",
          false);

      return new ModelAndView(variables, "GTSHome.ftl");
    }
  }

  /**
   * Displays after a user chooses to start a new session.
   *
   */
  protected static class GTSNewSessionHandler implements TemplateViewRoute {
    @Override
    public ModelAndView handle(Request request, Response res) {
      Map<String, Object> variables = ImmutableMap.of("title", "Guess the Song", "showScoreboard",
          false);

      return new ModelAndView(variables, "NewSession.ftl");
    }
  }

  /**
   * Displays after a user chooses to join a session.
   */
  protected static class GTSTryJoinSessionHandler implements TemplateViewRoute {
    @Override
    public ModelAndView handle(Request request, Response res) {
      Map<String, Object> variables = ImmutableMap.of("title", "Guess the Song", "showScoreboard",
          false);
      return new ModelAndView(variables, "JoinSession.ftl");

    }
  }

  /**
   * Handles all error-ing on the front end of the application by catching
   * potential things that can go wrong and displaying the error on the screen.
   *
   */
  protected static class GTSGeneralError implements TemplateViewRoute {
    @Override
    public ModelAndView handle(Request request, Response res) {
      String error = request.params(":error");
      System.out.println(error);
      Map<String, Object> variables;
      switch (error) {
        case "unf":
          variables = ImmutableMap.of("title", "Guess the Song", "showScoreboard", false, "error",
              "Unable to find user id.");
          break;
        case "gcnf":
          variables = ImmutableMap.of("title", "Guess the Song", "showScoreboard", false, "error",
              "Unable to find game code.");
          break;
        case "pnf":
          variables = ImmutableMap.of("title", "Guess the Song", "showScoreboard", false, "error",
              "Page not found.");
          break;
        case "gnf":
          variables = ImmutableMap.of("title", "Guess the Song", "showScoreboard", false, "error",
              "Game not found.");
          break;
        case "ie":
          variables = ImmutableMap.of("title", "Guess the Song", "showScoreboard", false, "error",
              "Internal error. Please try again.");
          break;
        case "dna":
          variables = ImmutableMap.of("title", "Guess the Song", "showScoreboard", false, "error",
              "User did not authenticate, you need to authenticate.");
          break;
        case "aig":
          variables = ImmutableMap.of("title", "Guess the Song", "showScoreboard", false, "error",
              "User is already in another game. Must wait for it to finish before you can begin a new one.");
          break;
        case "uls":
          variables = ImmutableMap.of("title", "Guess the Song", "showScoreboard", false, "error",
              "A user has left your game, the game is being ended. Please do not leave the game when it has begun.");
          break;
        case "hls":
          variables = ImmutableMap.of("title", "Guess the Song", "showScoreboard", false, "error",
              "The host has left the session. Feel free to create a new game.");
          break;
        default:
          variables = ImmutableMap.of("title", "Guess the Song", "showScoreboard", false, "error",
              "Issue with Guess the Song game. Please try again.");
          break;
      }
      return new ModelAndView(variables, "HomeError.ftl");
    }
  }

  /**
   * Displays after a user chooses to join a session.
   */
  protected static class GTSErrorJoinSessionHandler implements TemplateViewRoute {
    @Override
    public ModelAndView handle(Request request, Response res) {
      String error = request.params(":error");
      System.out.println(error);
      Map<String, Object> variables;
      switch (error) {
        case "dne":
          variables = ImmutableMap.of("title", "Guess the Song", "showScoreboard", false, "error",
              "Unable to find session id.");
          break;
        case "locked":
          variables = ImmutableMap.of("title", "Guess the Song", "showScoreboard", false, "error",
              "Unable to join game. That session already started.");
          break;
        case "exists":
          variables = ImmutableMap.of("title", "Guess the Song", "showScoreboard", false, "error",
              "Unable to join game. Nickname already being used. Please choose another.");
          break;
        case "max":
          variables = ImmutableMap.of("title", "Guess the Song", "showScoreboard", false, "error",
              "Unable to join game. Max number of players reached in this game session.");
        default:
          variables = ImmutableMap.of("title", "Guess the Song", "showScoreboard", false, "error",
              "Issue joining game. Please try again.");
          break;
      }
      return new ModelAndView(variables, "JoinSessionError.ftl");

    }
  }

  /**
   * Gets the waiting room with GETS.
   */
  protected static class GTSGetWaitingRoom implements TemplateViewRoute {
    @Override
    public ModelAndView handle(Request request, Response res) {
      System.out.println("In GTSGetWaitingRoom");
      String userId = request.cookies().get("userID");
      String gameCode = request.cookies().get("gameCode");
      if (userId == null || !GameHandlers.userIdToUser.containsKey(userId)) {
        res.redirect("/error/unf");
        return null;
      }

      // Get variables to use for page
      boolean isHost = GameHandlers.userIdToUser.get(userId).isHost();
      if (!GameHandlers.gameCodeToGameSession.containsKey(gameCode)) {
        res.redirect("/error/gcnf");
        return null;
      }
      System.out.println("GameCode:" + gameCode);
      GameSession session = GameHandlers.gameCodeToGameSession.get(gameCode);
      System.out.println("Session: ");
      System.out.println(session);
      int userCount = session.getUsersInSession().size();
      String hostNickname = session.getHost().getNickname();
      List<String> memberNicknames = session.getUsersInSession().stream().map(u -> u.getNickname())
          .collect(Collectors.toList());

      Map<String, Object> variables = ImmutableMap.<String, Object>builder()
          .put("title", "Sonic Skillz").put("showScoreboard", false).put("host", hostNickname)
          .put("isHost", isHost).put("members", memberNicknames).put("code", gameCode)
          .put("userCount", userCount).build();

      return new ModelAndView(variables, "WaitingRoom.ftl");
    }
  }

  /**
   * Displays the GET creating game.
   *
   */
  protected static class GTSGetStartGameHandler implements TemplateViewRoute {
    @Override
    public ModelAndView handle(Request request, Response res) {
      // TODO Error check here?
      Map<String, Object> variables = ImmutableMap.of("title", "Guess the Song", "showScoreboard",
          false);
      return new ModelAndView(variables, "Instructions.ftl");

    }
  }

  /**
   * Handles screen when game is ready to play - allows host to start the game.
   *
   */
  protected static class GTSGameReady implements TemplateViewRoute {
    @Override
    public ModelAndView handle(Request request, Response res) {
      String userId = request.cookies().get("userID");
      String gameCode = request.cookies().get("gameCode");
      if (userId == null || !GameHandlers.userIdToUser.containsKey(userId)) {
        res.redirect("/error/unf");
        return null;
      } else if (gameCode == null || !GameHandlers.gameCodeToGameSession.containsKey(gameCode)) {
        res.redirect("/error/gcnf");
        return null;
      }

      res.cookie("gameCode", gameCode);

      // Get variables to use for page
      GameSession gameSession = GameHandlers.gameCodeToGameSession.get(gameCode);
      User host = gameSession.getHost();
      boolean isHost = userId.equals(host.getId());

      Map<String, Object> variables = ImmutableMap.of("title", "Guess the Song", "showScoreboard",
          false, "isHost", isHost);
      return new ModelAndView(variables, "GameReady.ftl");
    }
  }

  /**
   * Handles the display of the opening page once user chooses the game.
   *
   */
  protected static class GTSWelcomePage implements TemplateViewRoute {

    @Override
    public ModelAndView handle(Request request, Response res) throws Exception {

      String userId = request.cookies().get("userID");
      String gameCode = request.cookies().get("gameCode");
      if (userId == null || !GameHandlers.userIdToUser.containsKey(userId)) {
        res.redirect("/error/unf");
        return null;
      } else if (gameCode == null || !GameHandlers.gameCodeToGameSession.containsKey(gameCode)) {
        res.redirect("/error/gcnf");
        return null;
      }

      // Get variables to use for page
      Game game = GameHandlers.codeToGameInstance.get(gameCode);
      String level = game.getLevel();
      User nextPlayer = game.nextTurnPeek();
      String nextNickname = nextPlayer.getNickname();
      boolean isNextPlayer = userId.equals(nextPlayer.getId());

      Map<String, Object> variables = ImmutableMap.<String, Object>builder()
          .put("title", "Guess the Song").put("showScoreboard", false).put("level", level)
          .put("firstPlayer", nextNickname).put("isNextPlayer", isNextPlayer).build();
      return new ModelAndView(variables, "Welcome.ftl");
    }
  }

  /**
   * Handles displays for the player whose turn it is next and for the other
   * players.
   *
   */
  protected static class GTSNextRound implements TemplateViewRoute {

    @Override
    public ModelAndView handle(Request request, Response res) throws Exception {
      String gameCode = request.cookies().get("gameCode");
      String userId = request.cookies().get("userID");
      if (userId == null || !GameHandlers.userIdToUser.containsKey(userId)) {
        res.redirect("/error/unf");
        return null;
      } else if (gameCode == null || !GameHandlers.codeToGameInstance.containsKey(gameCode)) {
        res.redirect("/error/gcnf");
        return null;
      }

      // Get variables to use for page
      Game game = GameHandlers.codeToGameInstance.get(gameCode);
      boolean yourTurn = game.currentPlayer().getId().equals(userId);
      User nextPlayer = game.nextTurnPeek();

      User currentUser = game.currentPlayer();
      String currentPlayer = currentUser.getNickname();

      Track track = game.getCurrentTrack();
      String link = track.getPreviewUrl();
      String album = track.getImageURL();

      ImmutableMap.Builder<String, Object> vBuilder = ImmutableMap.<String, Object>builder()
          .put("title", "Guess the Song").put("audioLink", link);

      if (yourTurn) { // Your turn
        if (nextPlayer == null) {
          // No next player to show or score board
          vBuilder.put("showScoreboard", false).build();
        } else {
          // Need next player info and score board
          vBuilder.put("showScoreboard", true).put("nextNickname", nextPlayer.getNickname())
              .put("nextScore", game.getPlayerScore(nextPlayer));
        }
        return new ModelAndView(vBuilder.build(), "YourTurn.ftl");
      } else { // Not your turn
        // Need to see current player and audio and image links
        vBuilder.put("currPlayer", currentPlayer).put("imageUrl", album);
        if (nextPlayer == null) {
          // No next player to show or score board
          vBuilder.put("showScoreboard", false);
        } else {
          vBuilder.put("showScoreboard", true).put("nextNickname", nextPlayer.getNickname())
              .put("nextScore", game.getPlayerScore(nextPlayer));
        }
        return new ModelAndView(vBuilder.build(), "OtherTurn.ftl");
      }
    }
  }

  /**
   * Handles the display of the scoreboard for all players in the game.
   *
   */
  protected static class GTSScoreBoard implements TemplateViewRoute {
    @Override
    public ModelAndView handle(Request request, Response res) throws Exception {
      // Show scoreboard
      String result = request.params(":correct");

      String gameCode = request.cookies().get("gameCode");
      String userId = request.cookies().get("userID");
      if (userId == null || !GameHandlers.userIdToUser.containsKey(userId)) {
        res.redirect("/error/unf");
        return null;
      } else if (gameCode == null || !GameHandlers.codeToGameInstance.containsKey(gameCode)) {
        res.redirect("/error/gcnf");
        return null;
      }

      Game game = GameHandlers.codeToGameInstance.get(gameCode);

      // Variables to use on the page
      String guess = game.getGuess();
      Track track = game.getCurrentTrack();
      String currentPlayerName = game.currentPlayer().getNickname();
      String imageUrl = track.getImages().get(0).getUrl();
      String audioLink = track.getPreviewUrl();
      String songName = track.getName();
      String artists = String.join(", ",
          track.getArtists().stream().map(artist -> artist.getName()).collect(Collectors.toList()));

      User nextPlayer = game.nextTurnPeek();

      ImmutableMap.Builder<String, Object> vBuilder = ImmutableMap.<String, Object>builder()
          .put("title", "Guess the Song").put("imageUrl", imageUrl).put("audioLink", audioLink)
          .put("guess", guess).put("result", result).put("currentPlayer", currentPlayerName)
          .put("songName", songName).put("artist", artists);

      if (nextPlayer == null) {
        // Last turn was taken
        game.setInactive();
        vBuilder.put("showScoreboard", false).build();
        return new ModelAndView(vBuilder.build(), "FinalScoreboard.ftl");
      } else {
        // Not last turn so still need next round info
        String nextNickname = nextPlayer.getNickname();
        int nextScore = game.getPlayerScore(nextPlayer);
        boolean isNextTurn = nextPlayer.getId().equals(userId);

        vBuilder.put("showScoreboard", true).put("isNextTurn", isNextTurn)
            .put("nextNickname", nextNickname).put("nextScore", nextScore).build();
        return new ModelAndView(vBuilder.build(), "Scoreboard.ftl");
      }
    }
  }

  /**
   * Displays after a game ends.
   *
   */
  protected static class GTSFinalScore implements TemplateViewRoute {
    @Override
    public ModelAndView handle(Request request, Response res) {
      String gameCode = request.cookies().get("gameCode");
      String userId = request.cookies().get("userID");

      if (userId == null || !GameHandlers.userIdToUser.containsKey(userId)) {
        res.redirect("/error/unf");
        return null;
      } else if (gameCode == null) {
        res.redirect("/error/gcnf");
        return null;
      } else if (!GameHandlers.gameCodeToGameSession.containsKey(gameCode)
          || !GameHandlers.codeToGameInstance.containsKey(gameCode)) {
        res.redirect("/error/gcnf");
        return null;
      }

      Game game = GameHandlers.codeToGameInstance.get(gameCode);

      if (game.getTotalTurns() < ((game.getPlayerCount() * game.getGameSession().getNumberSongs())
          - 1)) {
        res.redirect("/guess-the-song/game-round");
        return null;
      }

      // Get variables to use for page
      List<List<String>> sorted = game.getSortedUsers();
      System.out.println(sorted.toString());
      String topScore = sorted.get(0).get(1);
      List<List<String>> winners = sorted.stream().filter(x -> x.get(1).equals(topScore))
          .collect(Collectors.toList());
      List<List<String>> rest = sorted.stream().filter(x -> !x.get(1).equals(topScore))
          .collect(Collectors.toList());

      Map<String, Object> variables = ImmutableMap.<String, Object>builder()
          .put("title", "Guess the Song").put("showScoreboard", false).put("winners", winners)
          .put("others", rest).build();
      return new ModelAndView(variables, "FinalScore.ftl");
    }
  }

  /**
   * Display an error page when an exception occurs in the server.
   *
   */
  protected static class ExceptionPrinter implements ExceptionHandler {
    @Override
    public void handle(Exception e, Request req, Response res) {
      e.printStackTrace();
      res.redirect("/error/ie");
    }
  }

  /**
   * Display when a page is not found.
   *
   */
  protected static class NotFoundHandler implements TemplateViewRoute {
    @Override
    public ModelAndView handle(Request req, Response res) throws Exception {
      // TODO Auto-generated method stub
      Map<String, Object> variables = ImmutableMap.of("title", "Sonic Skillz", "showScoreboard",
          false);
      return new ModelAndView(variables, "PageNotFound.ftl");
    }
  }
}

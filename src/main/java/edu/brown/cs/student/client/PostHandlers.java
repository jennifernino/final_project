package edu.brown.cs.student.client;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import edu.brown.cs.student.sonicSkillz.Game;
import edu.brown.cs.student.sonicSkillz.gameunits.GameSession;
import spark.QueryParamsMap;
import spark.Request;
import spark.Response;
import spark.Route;

/**
 * Contains all helper handlers for Spark POST routes in
 * Main.
 */
public class PostHandlers
{
  /**
   * Handler for "guess-the-song/waiting-room", executed when
   * a user tries to join a game session (submits join session
   * form).
   */
  protected static class WaitingRoomHandler implements Route
  {
    @Override
    public String handle(Request request, Response response) {
      String userId = request.cookies().get("userID");
      String gameCode = request.queryMap().value("gameCode");
      String nickname = request.queryMap().value("nickname");
      System.out.println(
          "UserId:" + userId + " gameCode:" + gameCode + " nickname:" + nickname);
      if (userId == null) {
        response.redirect("/error/unf");
        return null;
      } else if (gameCode == null) {
        response.redirect("/error/gcnf");
        return null;
      }

      GameSession gameSession = GameHandlers.gameCodeToGameSession.get(gameCode);
      if (gameSession == null) {
        response.redirect("/guess-the-song/join-session/dne");
        return null;
      }

      if (gameSession.isLocked()) {
        response.redirect("/guess-the-song/join-session/locked");
      } else if (gameSession.getUsersInSession().size() >= 6) {
        response.redirect("/guess-the-song/join-session/max");
      } else {
        List<String> nicknames = gameSession.getUsersInSession().stream()
            .map(u -> u.getNickname()).collect(Collectors.toList());
        if (nicknames.contains(nickname)) {
          response.redirect("/guess-the-song/join-session/exists");
        } else {
          response.cookie("gameCode", gameCode); // add gameCode to user's cookies

          // associate potential user to their inputted gameCode
          GameHandlers.userIdToUser.get(userId).setGameCode(gameCode);

          GameHandlers.userIdToUser.get(userId).setNickname(nickname);
          GameHandlers.userIdToUser.get(userId).setGameCode(gameCode);

          response.redirect("/start-spotify-authorization");
        }
      }
      return null;
    }
  }

  /**
   * Handler for "guess-the-song/host-waiting-room", executed
   * when a user tries to create a new game session (submits
   * create session form).
   */
  protected static class HostWaitingRoomHandler implements Route
  {
    @Override
    public String handle(Request request, Response response) {
      String hostId = request.cookies().get("userID");
      String nickname = request.queryMap().value("nickname");
      String num = request.queryMap().value("numSongs");
      int numberSongs = Integer.parseInt(num);

      System.out.println("Host ID: " + hostId);
      System.out.println("Nickname: " + nickname);
      System.out.println("Number of songs: " + num);

      GameSession gameSession = GameHandlers.createGameSession(hostId, nickname,
          numberSongs);
      String gameCode = gameSession.getGameCode();
      System.out.println(
          "GameSes exists:" + GameHandlers.gameCodeToGameSession.containsKey(gameCode));
      response.cookie("gameCode", gameCode);

      // associate potential host to the created gameCode
      // GameHandlers.userToGameCode.put(GameHandlers.userIdToUser.get(hostId),
      // gameCode);
      // start authorization process
      response.redirect("/start-spotify-authorization");
      return null;
    }
  }

  /**
   * Handler for "guess-the-song/start-game", executed when a
   * user starts a game (clicks "Start Game"). Due to the way
   * WaitingRoom.ftl is set up, only host would be able to
   * access this route, and therefore start the game.
   */
  protected static class StartGameHandler implements Route
  {
    @Override
    public String handle(Request request, Response response) {
      String hostId = request.cookies().get("userID");
      String gameCode = request.cookies().get("gameCode");
      GameSession gameSession = GameHandlers.gameCodeToGameSession.get(gameCode);
      for (String key : request.queryMap().toMap().keySet()) {
        System.out.println("-> -> -> Key: " + key);
        System.out.println("-> -> -> Value: " + request.queryMap().value(key));

      }
      String playlistOption = request.queryMap().value("playlistOption");
      if (playlistOption == null || playlistOption.length() == 0) {
        playlistOption = "None";
      }
//      for (User user : gameSession.getUsersInSession()) {
//        if (user.getSpotifyAccountType().equals("premium")) {
//          gameSession.setPremiumUser(user);
//        }
//      }
//      if (gameSession.getPremiumUser() == null) {
//        Spark.halt(401, "This game session does not contain a premium user.");
//      }
      gameSession.setPlaylistOption(playlistOption);
      GameHandlers.initGame(gameSession); // start game
      WebSocketHandlers.replyLocked(hostId, gameCode);
      response.redirect("/guess-the-song/start-game"); // GET
      return null;
    }
  }

  /**
   * Handler for "/get-next-round", executed when a user goes
   * to next round (clicks "Let's Go").
   */
  protected static class NextRoundHandler implements Route
  {
    @Override
    public String handle(Request request, Response response) {
      String userId = request.cookies().get("userID");
      String gameCode = GameHandlers.userIdToUser.get(userId).getGameCode();
      Game game = GameHandlers.codeToGameInstance.get(gameCode);
      // Iterates to next turn
      game.nextTurn();
      if (game.isGameActive()) {
        response.redirect("/guess-the-song/game-round");
      } else {
        response.redirect("/guess-the-song/final-score");
      }
      WebSocketHandlers.replyRound(userId);
      return null;
    }
  }

  /**
   * Handler for "/begin-round".
   */
  protected static class BeginRoundHandler implements Route
  {
    @Override
    public String handle(Request request, Response response) {
      Map<String, String> cookies = request.cookies();
      QueryParamsMap input = request.queryMap();
      String level = input.value("selected");
      String hostId = cookies.get("userID");
      String gameCode = GameHandlers.userIdToUser.get(hostId).getGameCode();

      GameHandlers.codeToGameInstance.get(gameCode).setLevel(level);

      WebSocketHandlers.replyBegin(hostId, gameCode);
      response.redirect("/guess-the-song/welcome-page");
      response.type("application/json");
      response.status(200);
      return null;
    }
  }

  /**
   * Handler for "/guess-the-song/guess-submitted", executed
   * when a user submits their guess (clicks "Submit"). Checks
   * if a user's guess is correct, and updates the gui.
   */
  protected static class GuessSubmittedHandler implements Route
  {
    @Override
    public String handle(Request request, Response response) {
      String gameCode = request.cookies().get("gameCode");
      String userId = request.cookies().get("userID");
      QueryParamsMap input = request.queryMap();
      String guess = input.value("songguess");

      Game game = GameHandlers.codeToGameInstance.get(gameCode);

      boolean wasCorrect = game.userGuess(guess);

      if (wasCorrect) {
        response.redirect("/guess-the-song/scoreboard/correct");
        WebSocketHandlers.replyScoreboard(userId, gameCode, "correct");
      } else {
        response.redirect("/guess-the-song/scoreboard/incorrect");
        WebSocketHandlers.replyScoreboard(userId, gameCode, "incorrect");
      }

      return null;
    }
  }

}

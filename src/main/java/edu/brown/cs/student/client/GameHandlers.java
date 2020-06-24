package edu.brown.cs.student.client;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ThreadLocalRandom;

import edu.brown.cs.student.Constants;
import edu.brown.cs.student.game.GuessTheSongGame;
import edu.brown.cs.student.sonicSkillz.Game;
import edu.brown.cs.student.sonicSkillz.gameunits.GameSession;
import edu.brown.cs.student.sonicSkillz.gameunits.User;

/**
 * Class that manages all game information (sessions, users, game instances).
 */
public class GameHandlers {

  protected static final Map<String, Game> codeToGameInstance = new ConcurrentHashMap<>();
  protected static final Map<String, User> userIdToUser = new ConcurrentHashMap<>();
  protected static final Map<String, GameSession> gameCodeToGameSession = new ConcurrentHashMap<>();
  protected static final Queue<String> usedIds = new ConcurrentLinkedQueue<>();
  // protected static final Map<User, String> userToGameCode = new
  // ConcurrentHashMap<>();

  /**
   * Create a unique user id
   *
   * @return the unique id
   */
  public static String creatUniqueRandomId() {
    String random = Integer.toString(
        ThreadLocalRandom.current().nextInt(0, Constants.KEEP_WITHIN) + Constants.USER_ID_START);
    while (usedIds.contains(random)) {
      random = Integer.toString(
          ThreadLocalRandom.current().nextInt(0, Constants.KEEP_WITHIN) + Constants.USER_ID_START);
    }
    usedIds.add(random);
    return random;
  }

  /**
   * Create a unique user code
   *
   * @return the unique code
   */
  public static String creatUniqueRandomCode() {
    String gameCode = Integer.toString(
        (int) (Math.floor(Math.random() * Constants.KEEP_WITHIN) + Constants.GAME_CODE_START));

    while (gameCodeToGameSession.containsKey(gameCode)) {
      gameCode = Integer.toString(
          (int) (Math.floor(Math.random() * Constants.KEEP_WITHIN) + Constants.GAME_CODE_START));
    }

    return gameCode;
  }

  /**
   * Creates a game session when given a host id and nickname.
   *
   * @param hostId      of the user
   * @param nickname    of the user
   * @param numberSongs each user should get in the game
   * @return the gamesession object after creation
   */
  public static GameSession createGameSession(String hostId, String nickname, int numberSongs) {
    String gameCode = creatUniqueRandomCode();

    User host = userIdToUser.get(hostId);
    userIdToUser.get(hostId).setGameCode(gameCode);
    userIdToUser.get(hostId).setNickname(nickname);
    userIdToUser.get(hostId).setHost(true);

    GameSession gameSession = new GameSession(host, gameCode, numberSongs);
    gameCodeToGameSession.put(gameCode, gameSession);
    return gameSession;
  }

  /**
   * Adds a user to a to a game session.
   *
   * @param userId   of user to add to session
   * @param gameCode of the session we want to add the user to
   * @param nickname of the user
   */
  public static void addUser(String userId, String gameCode, String nickname) {
    User user = userIdToUser.get(userId);
    userIdToUser.get(userId).setNickname(nickname);
    userIdToUser.get(userId).setGameCode(gameCode);
    gameCodeToGameSession.get(gameCode).addUser(user);
  }

  /**
   * Initializes the existence of the game.
   *
   * @param session the game session that wants to initialize a game
   *
   */
  public static void initGame(GameSession session) {
    // Locks the game such that no additional users can be added
    session.setLocked(true);

    String code = session.getGameCode();
    Game game = new GuessTheSongGame(session);
    GameHandlers.codeToGameInstance.put(code, game);
    game.initializeGame();
  }

  /**
   * Removes a user from a session.
   *
   * @param userId   the userId of the user to remove
   * @param gameCode the gamecode of the session to remove from
   */
  public static void removeUser(String userId, String gameCode) {
    System.out.println("Removing user herre!! in gamehadnler & userNickname is:"
        + userIdToUser.get(userId).getNickname());
    userIdToUser.get(userId).setNickname(null);
    userIdToUser.get(userId).setGameCode(null);
    userIdToUser.get(userId).setHost(false);

    GameSession gameSession = gameCodeToGameSession.get(gameCode);
    gameSession.removeUser(userId);
  }

  /**
   * Removes a game from our program, this should include session and game
   * instance.
   *
   * @param gameCode of the game to remove from our program
   */
  public static void removeGameInstance(String gameCode) {
    if (codeToGameInstance.containsKey(gameCode)) {
      Game game = codeToGameInstance.get(gameCode);

      System.out.println("Game will be removed");
      game.setRemoved(true);

//      GameSession gameSession = gameCodeToGameSession.get(gameCode);
//      List<User> users = new ArrayList<User>(gameSession.getUsersInSession());
//      for (User user : users) {
//        removeUser(user.getId(), gameCode);
//      }
      codeToGameInstance.remove(gameCode);
      gameCodeToGameSession.remove(gameCode);
      System.out.println("Game removed:" + gameCode);

    }

  }

  /**
   * Removes a game session from our program,
   *
   * @param gameCode of the game session to remove
   */
  public static void removeGameSession(String gameCode) {
    if (gameCodeToGameSession.containsKey(gameCode)) {
      GameSession gameSession = gameCodeToGameSession.get(gameCode);
      List<User> users = new ArrayList<User>(gameSession.getUsersInSession());
      for (User user : users) {
        removeUser(user.getId(), gameCode);
      }
      gameCodeToGameSession.remove(gameCode);
    }
  }

  /**
   * If a user is on a homepage and they have a gamecode, we need to cleanup the
   * game if necessary.
   *
   * @param userId   of the user who needs cleaning
   * @param gameCode of the session they are tied to
   */
  public static void gameCleanup(String userId, String gameCode) {
    System.out.println("Calling game cleanup");
    if (userIdToUser.containsKey(userId)) {
      userIdToUser.get(userId).resetForNextRound();
    }

    if (gameCodeToGameSession.containsKey(gameCode)) { // gameCode Session exists
      GameSession gameSession = gameCodeToGameSession.get(gameCode);

      if (codeToGameInstance.containsKey(gameCode)) { // gameCode also has game instance
        Game game = codeToGameInstance.get(gameCode);

        if (game.isGameActive()) {
          WebSocketHandlers.replyExited(userId, gameCode);
          System.out.println("Removing game instance because you left");
          if (!game.beingRemoved()) {
            removeGameInstance(gameCode);
          }

        } else {
          System.out.println("Removing game instance because it is over");
          if (!game.beingRemoved()) {
            Thread removeThread = new Thread(() -> {
              try {
                System.out.println("Removing game, but sleep first");
                Thread.sleep(Constants.REMOVE_GAME_AFTER);
              } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
              }

              removeGameInstance(gameCode);
            });
            removeThread.start();
          }
        }
      } else { // gameCode has session, not an instance
        if (!gameSession.isLocked()) {
          if (gameSession.getUsersInSession().size() == 1) {
            System.out.println("Removing because 1 player left");
            removeGameSession(gameCode);

          } else {
            String hostId = gameSession.getHost().getId();
            if (!hostId.equals(userId)) {
              removeUser(userId, gameCode);

              // Force users to see new members (it shouldn't include the user we just
              // removed)!
              WebSocketHandlers.replyNewMember(gameCode, userId);
            } else {
              System.out.println("Removing because host left");
              WebSocketHandlers.replyHostLeft(userId, gameCode);
              removeGameSession(gameCode);
            }
          }
        }
      }
    }
  }
}

package edu.brown.cs.student.client;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.jetty.websocket.api.Session;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import edu.brown.cs.student.sonicSkillz.gameunits.GameSession;
import edu.brown.cs.student.sonicSkillz.gameunits.User;

/**
 * Class that handles socket broadcasts.
 *
 */
public final class WebSocketHandlers {

  /**
   * Types of messages the sockets send out.
   */
  private static enum GAMETYPE {
    CONNECTED, NEWMEMBER, LOCKED, READY, BEGIN, SCOREBOARD, ROUND, EXITED, HOSTLEFT
  }

  private static final Gson GSON = new Gson();
  protected static final Map<String, Session> idToSession = new ConcurrentHashMap<>();
  protected static final Map<String, String> gameCodeToHostUserId = new ConcurrentHashMap<>();

  /**
   * This function returns the userId to the client after one is created for them.
   *
   * @param session A session object in which the game and players exists
   * @param userId  Used to assign specific Spotify users/players to sessions
   */
  public static void replyConnected(Session session, String userId) {
    JsonObject message = new JsonObject();
    message.addProperty("type", GAMETYPE.CONNECTED.ordinal());
    JsonObject payload = new JsonObject();
    payload.addProperty("id", userId);
    message.add("payload", payload);
    try {
      if (session.isOpen() && (session.getRemote() != null)) {
        session.getRemote().sendString(GSON.toJson(message));
      }
    } catch (IOException e) {
      System.out
          .println("ERROR: Unable to send update to " + session.getLocalAddress().getHostName());
    }
  }

  /**
   * A new user has joined the session, and we need to notify everyone else in the
   * game (currently others are in the waiting room).
   *
   * @param code of game who user joined
   * @param id   of user who joined
   */
  public static void replyNewMember(String code, String id) {
    GameSession session = GameHandlers.gameCodeToGameSession.get(code);
    List<User> users = session.getUsersInSession();
    for (User user : users) {
      String userId = user.getId();
      if (!userId.equals(id)) {
        Session socketSession = idToSession.get(userId);

        JsonObject message = new JsonObject();
        message.addProperty("type", GAMETYPE.NEWMEMBER.ordinal());
        JsonObject payload = new JsonObject();
        String nickname = user.getNickname();
        payload.addProperty("member", nickname);
        message.add("payload", payload);

        try {
          if (socketSession.isOpen() && (socketSession.getRemote() != null)) {
            socketSession.getRemote().sendString(GSON.toJson(message));
          }
        } catch (IOException e) {
          System.out.println(
              "ERROR: Unable to send update to " + socketSession.getLocalAddress().getHostName());
        }
      }
    }
  }

  /**
   * When host clicks start game, all users in the same game session are
   * redirected to the loading screen.
   *
   * @param hostID The user ID of the game's host
   * @param code   Game code used specify a session
   */
  public static void replyLocked(String hostID, String code) {
    GameSession session = GameHandlers.gameCodeToGameSession.get(code);
    List<User> users = session.getUsersInSession();
    for (User user : users) {
      String userId = user.getId();
      if (!userId.equals(hostID)) {
        Session socketSession = idToSession.get(userId);

        JsonObject message = new JsonObject();
        message.addProperty("type", GAMETYPE.LOCKED.ordinal());

        try {
          if (socketSession.isOpen() && (socketSession.getRemote() != null)) {
            socketSession.getRemote().sendString(GSON.toJson(message));
          }
        } catch (IOException e) {
          System.out.println(
              "ERROR: Unable to send update to " + socketSession.getLocalAddress().getHostName());
        }
      }
    }
  }

  /**
   * After playlists have been created, we should send out a message indicating
   * that the game is ready to begin.
   *
   * @param hostId The user ID of the game's host
   * @param code   The code specifying the game session
   */
  public static void replyReady(String hostId, String code) {
    GameSession gameSession = GameHandlers.gameCodeToGameSession.get(code);
    List<User> users = gameSession.getUsersInSession();
    for (User user : users) {
      String userId = user.getId();
      if (userId.equals(hostId)) {
        Session session = idToSession.get(userId);
        JsonObject message = new JsonObject();
        message.addProperty("type", GAMETYPE.READY.ordinal());

        try {
          if (session.isOpen() && (session.getRemote() != null)) {
            session.getRemote().sendString(GSON.toJson(message));
          }
          return;
        } catch (IOException e) {
          System.out.println(
              "ERROR: Unable to send update to " + session.getLocalAddress().getHostName());
        }
      }

    }
  }

  /**
   * Initiates the game for the group of players via setting appropriate enums and
   * sending players into the session.
   *
   * @param hostId The user ID of the host of the session
   * @param code   The code associated with the current session
   */
  public static void replyBegin(String hostId, String code) {
    GameSession gameSession = GameHandlers.gameCodeToGameSession.get(code);
    List<User> users = gameSession.getUsersInSession();
    for (User user : users) {
      String userId = user.getId();
      Session session = idToSession.get(userId);
      JsonObject message = new JsonObject();
      message.addProperty("type", GAMETYPE.BEGIN.ordinal());
      try {
        if (session.isOpen() && (session.getRemote() != null)) {
          session.getRemote().sendString(GSON.toJson(message));
        }
      } catch (IOException e) {
        System.out
            .println("ERROR: Unable to send update to " + session.getLocalAddress().getHostName());
      }

    }

  }

  /**
   * Handles the displaying of the scoreboard.
   *
   * @param hostID The user ID of the host of the session
   * @param code   The code associated with the current session
   * @param result To set the scores obtained by players and display on the page
   */
  public static void replyScoreboard(String hostID, String code, String result) {
    GameSession gameSession = GameHandlers.gameCodeToGameSession.get(code);
    List<User> users = gameSession.getUsersInSession();
    for (User user : users) {
      String userId = user.getId();
      if (!userId.equals(hostID)) {
        Session socketSession = idToSession.get(userId);

        JsonObject message = new JsonObject();
        message.addProperty("type", GAMETYPE.SCOREBOARD.ordinal());
        JsonObject payload = new JsonObject();
        payload.addProperty("result", result);
        message.add("payload", payload);
        try {
          if (socketSession.isOpen() && (socketSession.getRemote() != null)) {
            socketSession.getRemote().sendString(GSON.toJson(message));
          }
        } catch (IOException e) {
          System.out.println(
              "ERROR: Unable to send update to " + socketSession.getLocalAddress().getHostName());
        }
      }
    }
  }

  /**
   * Sends information about the number of rounds that have passed in the game.
   *
   * @param hostId The user ID of the host of the game session
   */
  public static void replyRound(String hostId) {
    String code = GameHandlers.userIdToUser.get(hostId).getGameCode();
    GameSession gameSession = GameHandlers.gameCodeToGameSession.get(code);
    List<User> users = gameSession.getUsersInSession();

    for (User user : users) {
      String userId = user.getId();
      if (!userId.equals(hostId)) {
        Session session = idToSession.get(userId);

        JsonObject message = new JsonObject();
        message.addProperty("type", GAMETYPE.ROUND.ordinal());

        try {
          if (session.isOpen() && (session.getRemote() != null)) {
            session.getRemote().sendString(GSON.toJson(message));
          }

          // todo: NullPointer Deflater; clean up.
        } catch (IOException e) {
          System.out.println(
              "ERROR: Unable to send update to " + session.getLocalAddress().getHostName());
        }
      }
    }
  }

  /**
   * Boot users from game if on user leaves.
   *
   * @param userId   id of user
   * @param gameCode game code of session
   */
  public static void replyExited(String userId, String gameCode) {
    GameSession gameSession = GameHandlers.gameCodeToGameSession.get(gameCode);
    List<User> users = gameSession.getUsersInSession();

    for (User user : users) {
      String currId = user.getId();
      if (!currId.equals(userId)) {
        Session session = idToSession.get(currId);

        JsonObject message = new JsonObject();
        message.addProperty("type", GAMETYPE.EXITED.ordinal());

        try {
          if (session.isOpen() && (session.getRemote() != null)) {
            session.getRemote().sendString(GSON.toJson(message));
          }
        } catch (IOException e) {
          System.out.println(
              "ERROR: Unable to send update to " + session.getLocalAddress().getHostName());
        }
      }
    }

  }

  /**
   * Boot users from game if on user leaves.
   *
   * @param userId   id of user
   * @param gameCode code of game
   */
  public static void replyHostLeft(String userId, String gameCode) {
    GameSession gameSession = GameHandlers.gameCodeToGameSession.get(gameCode);
    List<User> users = gameSession.getUsersInSession();

    for (User user : users) {
      String currId = user.getId();
      Session session = idToSession.get(currId);

      JsonObject message = new JsonObject();
      message.addProperty("type", GAMETYPE.HOSTLEFT.ordinal());

      try {
        if (session.isOpen() && (session.getRemote() != null)) {
          session.getRemote().sendString(GSON.toJson(message));
        }
      } catch (IOException e) {
        System.out
            .println("ERROR: Unable to send update to " + session.getLocalAddress().getHostName());
      }
    }

  }
}

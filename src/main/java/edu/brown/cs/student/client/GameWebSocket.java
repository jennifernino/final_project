package edu.brown.cs.student.client;

import java.io.IOException;
import java.net.HttpCookie;
import java.util.List;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketError;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

import edu.brown.cs.student.sonicSkillz.gameunits.User;

/**
 * Builds the websockets connections for the game.
 *
 */
@WebSocket
public class GameWebSocket {

  /**
   * Establishes connection to a WebSocket to allow multiplayer functionalities of
   * game.
   *
   * @param session Session object for which websocket is being connected
   * @throws Exception Thrown if UserID is invalid
   */
  @OnWebSocketConnect
  public void onConnect(Session session) throws Exception {
    String userID = null;
    List<HttpCookie> cookies = session.getUpgradeRequest().getCookies();

    if (cookies == null) {
      userID = GameHandlers.creatUniqueRandomId();
      User user = new User(userID);
      GameHandlers.userIdToUser.put(userID, user);
    } else {
      for (HttpCookie cookie : cookies) {
        if (cookie.getName().equals("userID")) {
          if (!cookie.getValue().equals("")) {
            userID = cookie.getValue();
            try {
              if (!GameHandlers.userIdToUser.containsKey(userID)) {
                userID = GameHandlers.creatUniqueRandomId();
                User user = new User(userID);
                GameHandlers.userIdToUser.put(userID, user);
              }
            } catch (NumberFormatException e) {
              System.out.println("Not a real userId. Creating a new one");
            }
          }
        }
      }

      // Never found users in cookies
      if (userID == null) {
        userID = GameHandlers.creatUniqueRandomId();
        User user = new User(userID);
        GameHandlers.userIdToUser.put(userID, user);
      }
    }
    assert (GameHandlers.userIdToUser.get(userID) != null);
    WebSocketHandlers.idToSession.put(userID, session);
    WebSocketHandlers.replyConnected(session, userID);
  }

  /**
   * Receives message when sent by socket. This message is sent to keep the socket
   * alive
   *
   * @param session that is sending the message
   * @param message arbitrary message send
   * @throws IOException String pertaining to reason for closing
   */
  @OnWebSocketMessage
  public void message(Session session, String message) throws IOException {
    // onMessage
  }

  /**
   * Closes the websocket and provides us with information in the command line.
   *
   * @param user       Session object
   * @param statusCode Int representing the status code
   * @param reason     String pertaining to reason for closing
   */
  @OnWebSocketClose
  public void onClose(Session user, int statusCode, String reason) {
    // onClose
  }

  /**
   * Errors gracefully on the websocket.
   *
   * @param err Throwable that is thrown when an error occurs
   */
  @OnWebSocketError
  public void onError(Throwable err) {
    System.out.print("WebSocket Error: ");
    err.printStackTrace(System.out);
  }
}

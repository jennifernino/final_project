package edu.brown.cs.student.sonicSkillz.gameunits;

import java.util.ArrayList;
import java.util.List;

/**
 * GameSession class, holds a game within a session.
 *
 */
public class GameSession {
  private final String gameCode;
  private final User host;
  private final int numSongs;
  private final List<User> usersInSession;
  private boolean isLocked;
  private String webPlayerToken;

  /**
   * Instantiates a game session with relevant information set.
   *
   * @param host     User who is marked as host of the game
   * @param gameCode String pertaining to the code used to join a game
   * @param numSongs Number of songs needed in a Playlist for the game
   */
  public GameSession(User host, String gameCode, int numSongs) {
    this.gameCode = gameCode;
    this.host = host;
    this.usersInSession = new ArrayList<>();
    this.usersInSession.add(this.host);
    this.numSongs = numSongs;
  }

  /**
   * Getter method for number of songs needed in game.
   *
   * @return Int pertaining to number of songs
   */
  public int getNumberSongs() {
    return this.numSongs;
  }

  /**
   * Getter method for the game code used to refer to the game.
   *
   * @return String pertaining to code
   */
  public String getGameCode() {
    return this.gameCode;
  }

  /**
   * Getter method for the User that is marked as the host of the game.
   *
   * @return User object for the host
   */
  public User getHost() {
    return this.host;
  }

  /**
   * Getter method for web player.
   *
   * @return String pertaining to token
   */
//  public String getWebPlayerToken() {
//    return this.webPlayerToken;
//  }

  /**
   * Sets the web player for the game.
   *
   * @param token String referring to token of the web player
   */
//  public void setWebPlayerToken(String token) {
//    this.webPlayerToken = token;
//  }

  /**
   * Getter method for the users in the session.
   *
   * @return List of Users currently playing the game
   */
  public List<User> getUsersInSession() {
    return this.usersInSession;
  }

  /**
   * Adds a User to the session.
   *
   * @param user User to be added
   */
  public void addUser(User user) {
    this.usersInSession.add(user);
  }

  /**
   * Returns whether or not new users can join the session.
   *
   * @return true or false value of locked or not
   */
  public boolean isLocked() {
    return this.isLocked;
  }

  /**
   * Sets the boolean value of the locked-unlocked status of the session.
   *
   * @param locked boolean pertaining to locking the session
   */
  public void setLocked(boolean locked) {
    this.isLocked = locked;
  }

  /**
   * Removes a user from a game session.
   *
   * @param userId id of user
   */
  public void removeUser(String userId) {
    int toRemove = -1;
    for (int i = 0; i < this.getUsersInSession().size(); i += 1) {
      User curr = this.getUsersInSession().get(i);
      if (curr.getId().equals(userId)) {
        toRemove = i;
      }
    }
    if (toRemove > -1) {
      this.getUsersInSession().remove(toRemove);
    }
  }
}

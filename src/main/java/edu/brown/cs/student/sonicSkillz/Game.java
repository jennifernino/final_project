package edu.brown.cs.student.sonicSkillz;

import java.util.List;

import edu.brown.cs.student.sonicSkillz.gameunits.GameSession;
import edu.brown.cs.student.sonicSkillz.gameunits.Track;
import edu.brown.cs.student.sonicSkillz.gameunits.User;

/**
 * Interface Game is to be used for all Games added to the program, and exists
 * as an extensibility genric measure for our program. All Games pertaining to
 * the Spotify API on our webpage will have these methods.
 *
 */
public interface Game {
  /**
   * Obtains number of players in a game.
   *
   * @return Integer
   */
  Integer getPlayerCount();

  /**
   * Initializes the game within a session.
   *
   * @return List of Strings
   */
  List<String> initializeGame();

  /**
   * Sets the level of difficulty for a game.
   *
   * @param level easy, medium, hard
   */
  void setLevel(String level);

  /**
   * Status of game - if it has ended or if it's still going.
   *
   * @return true or false status of game
   */
  boolean isGameActive();

  /**
   * Number of turns altogether.
   *
   * @return int
   */
  int getTotalTurns();

  /**
   * Getter method for game level.
   *
   * @return game level
   */
  String getLevel();

  /**
   * Retrieves the player whose turn it is.
   *
   * @return User corresponding to player
   */
  User currentPlayer();

  /**
   * Adding to a player's score.
   *
   * @param player whose turn it is
   */
  void incrementPlayerScore(User player);

  /**
   * See whose turn it is next.
   *
   * @return User of the player who gets the next turn
   */
  User nextTurnPeek();

  /**
   * Deals with turn-taking within the game.
   */
  void nextTurn();

  /**
   * Users sorted by when their turn is.
   *
   * @return A list of users
   */
  List<List<String>> getSortedUsers();

  /**
   * Obtains the track being played in a game.
   *
   * @return Track object
   */
  Track getCurrentTrack();

  /**
   * Gets the session in which a game is occurring.
   *
   * @return GameSession
   */
  GameSession getGameSession();

  /**
   * Gets a player's score.
   *
   * @param player whose score we want
   * @return Integer for their score
   */
  Integer getPlayerScore(User player);

  /**
   * Obtains user input for a guess in a game.
   *
   * @return String input
   */
  String getGuess();

  /**
   * Cannot call nextTurn before checking the guess since this uses the current
   * track to check to see if the user input is correct.
   *
   * @param userInput for any game, retrieves the answer a user puts in
   * @return a boolean for whether or not the given answer is correct
   */
  boolean userGuess(String userInput);

  /**
   * Makes a game inactive.
   */
  void setInactive();

  /**
   * If the game is being removed
   */
  boolean beingRemoved();

  /**
   * Sets the games is being removed variable to whatever to is
   * 
   * @param to the boolean to change to
   */
  void setRemoved(boolean to);
}

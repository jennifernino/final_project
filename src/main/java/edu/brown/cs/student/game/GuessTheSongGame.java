package edu.brown.cs.student.game;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

import edu.brown.cs.student.Constants;
import edu.brown.cs.student.client.WebSocketHandlers;
import edu.brown.cs.student.sonicSkillz.Game;
import edu.brown.cs.student.sonicSkillz.PlaylistGenerator;
import edu.brown.cs.student.sonicSkillz.SpotifyPlaylistGenerator;
import edu.brown.cs.student.sonicSkillz.gameunits.GameSession;
import edu.brown.cs.student.sonicSkillz.gameunits.Playlist;
import edu.brown.cs.student.sonicSkillz.gameunits.Track;
import edu.brown.cs.student.sonicSkillz.gameunits.User;

/**
 * Game Class.
 */
public class GuessTheSongGame implements Game
{

  private GameSession gameSession;
  private List<User> players;
  private List<Integer> score;
  private String guess;
  private Integer turn; // refers to index in players
  private Integer totalTurns; // refers to the number of guesses total

  private Integer numSongsPerPlayer;

  private Playlist easy;
  private Playlist medium;
  private Playlist hard;

  private PlaylistGenerator generator;
  private boolean gameActive;

  private String level;
  private boolean beingRemoved;
  private List<List<String>> sortedUsers;

  /**
   * Constructor for Game class.
   *
   * @param gameSession Allows the game to be run within a
   *                    certain session on the main webpage
   */
  public GuessTheSongGame(GameSession gameSession) {
    this.beingRemoved = false;
    this.gameSession = gameSession;
    this.gameActive = true;
    String playOption = this.gameSession.getPlaylistOption();
    if (playOption.equals("None")) {
      this.generator = new PlaylistGenerator();
    } else {
      this.generator = new SpotifyPlaylistGenerator(playOption);
    }

    this.players = gameSession.getUsersInSession();
    this.numSongsPerPlayer = gameSession.getNumberSongs();

    this.turn = (new Random()).nextInt(1000) % this.players.size();
    this.totalTurns = Constants.TURN_COUNT_START;
    this.score = new ArrayList<Integer>(Collections.nCopies(this.players.size(), 0));
    this.guess = "";
    this.sortedUsers = null;
  }

  /**
   * Our program's main algorithm. The playlist generating
   * algorithm obtains the top songs and artists of all
   * players in the game and assigns weights to them based on
   * the difficulty of the game. It then returns lists of
   * playlists. This method produces all of the needed
   * playlists with the exact number of songs needed for a
   * full game.
   *
   * @return A list of Playlists, one of which will be used
   *         for gameplay based on chosen difficulty
   */
  public List<Playlist> generatePlaylists() {
    List<Playlist> playlists = this.generator.generate(this.players,
        this.numSongsPerPlayer);
    return playlists;
  }

  @Override
  public GameSession getGameSession() {
    return this.gameSession;
  }

  /**
   * Getter method for players in the game.
   *
   * @return List of Users
   */
  public List<User> getPlayers() {
    return this.players;
  }

  /**
   * Getter method for scores of players in the game.
   *
   * @return List of Integers pertaining to scores for each
   *         User.
   */
  public List<Integer> getScores() {
    return this.score;
  }

  /**
   * Gets a Playlist based on level of difficulty.
   *
   * @return Playlist object adjusted to easy, medium, hard
   */
  public Playlist getCurrentPlaylist() {
    switch (this.level) {
    case "hard":
      return this.hard;
    case "medium":
      return this.medium;
    default:
      return this.easy;
    }
  }

  /**
   * A method for testing. Sets the easy playlist.
   *
   * @param list the playlist to be set to easy.
   */
  public void setEasyPlaylist(Playlist list) {
    this.easy = list;
  }

  @Override
  public Integer getPlayerScore(User player) {
    int idx = 0;
    for (int i = 0; i < this.players.size(); i += 1) {
      if (this.players.get(i).getId().equals(player.getId())) {
        idx = i;
        break;
      }
    }
    return this.score.get(idx);
  }

  // SCORING
  @Override
  public void incrementPlayerScore(User player) {
    for (int i = 0; i < this.players.size(); i += 1) {
      if (this.players.get(i).getId().equals(player.getId())) {
        int currScore = this.score.get(i);
        this.score.set(i, currScore + 1);
        return;
      }
    }
  }

  /**
   * Sets the score for a particular player to be a certain
   * value.
   *
   * @param player   User whose score we are changing
   * @param newScore Integer value of their new score
   */
  public void setScore(User player, Integer newScore) {
    Integer index = this.players.indexOf(player);
    this.score.set(index, newScore);
  }

  // TURNS
  @Override
  public User currentPlayer() {
    if (this.totalTurns > this.getCurrentPlaylist().getSize()) {
      this.gameActive = false;
      return null;
    }
    if (this.totalTurns >= this.numSongsPerPlayer * this.players.size()) {
      this.gameActive = false;
      return null;
    }
    return this.players.get(this.turn);
  }

  @Override
  public User nextTurnPeek() {
    if ((this.totalTurns + 1) >= this.numSongsPerPlayer * this.players.size()) {
      return null;
    }
    int id = (this.turn + 1) % this.players.size();
    return this.players.get(id);
  }

  @Override
  public void nextTurn() {
    // Similar to iterator, when called, increments to next turn
    if (this.totalTurns > this.getCurrentPlaylist().getSize()) {
      this.gameActive = false;
      return;
    }
    if (this.totalTurns >= this.numSongsPerPlayer * this.players.size()) {
      this.gameActive = false;
      return;
    }

    this.turn++;
    this.turn = this.turn % this.players.size();
    this.totalTurns++;

    return;
  }

  // GET PLAYLISTS
  @Override
  public Track getCurrentTrack() {
    int index = this.totalTurns;
    Playlist using = this.getCurrentPlaylist();
    Track track = using.getTrack(index);
    return track;
  }

  @Override
  public String getLevel() {
    return this.level;
  }

  @Override
  public int getTotalTurns() {
    return this.totalTurns;
  }

  @Override
  public boolean isGameActive() {
    return this.gameActive;
  }

  @Override
  public List<List<String>> getSortedUsers() {
    if (this.sortedUsers != null) {
      return this.sortedUsers;
    }

    Map<String, User> users = new HashMap<String, User>();
    Map<String, Integer> scores = new HashMap<String, Integer>();

    for (int i = 0; i < this.players.size(); i++) {
      User user = this.players.get(i);
      Integer val = this.score.get(i);
      String id = user.getId();
      users.put(id, user);
      scores.put(id, val);
    }
    // Builds a list of sorted (nickname, score) pairs
    List<List<String>> sorted = scores.entrySet().stream()
        .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder())).map(x -> Arrays
            .asList(users.get(x.getKey()).getNickname(), x.getValue().toString()))
        .collect(Collectors.toList());
    this.sortedUsers = sorted;
    return sorted;
  }

  @Override
  public boolean userGuess(String userInput) {
    // NOTE: Cannot call nextTurn before checking the guess
    // since this uses the
    // current
    // track to check to see if the user input is correct
    this.guess = userInput;
    Track currTrack = this.getCurrentTrack();
    System.out.println("Track: \n" + currTrack.toString());
    List<String> validNames = currTrack.getCleanNames();
    User player = this.currentPlayer();
    for (String name : validNames) {
      if (this.getLedDistance(userInput.toLowerCase(), name) <= 1) {
        this.incrementPlayerScore(player);
        return true;
      }
    }
    return false;
  }

  /**
   * Creates a LED distance metric for words inputted, so we
   * can implement an autocorrecting function on song guesses.
   *
   * @param word1 String pertaining to word to be compared
   * @param word2 String pertaining to word to be compared
   *
   * @return Int pertaining to LED distance
   */
  public int getLedDistance(String word1, String word2) {
    int size1 = word1.length();
    int size2 = word2.length();
    int[][] ledMatrix = new int[size1 + 1][size2 + 1];

    for (int i = 0; i < size1 + 1; i++) {
      for (int j = 0; j < size2 + 1; j++) {

        // Comparing if either word was empty.
        if (i == 0) {
          ledMatrix[i][j] = j;
        } else if (j == 0) {
          ledMatrix[i][j] = i;
        } else {

          // See if adding corresponding characters would not increase
          // led.
          int substitution = 1;
          if (word1.charAt(i - 1) == word2.charAt(j - 1)) {
            substitution = 0;
          }
          // Take min of insertion, deletion, substituion.
          ledMatrix[i][j] = Math.min(
              Math.min(ledMatrix[i][j - 1] + 1, ledMatrix[i - 1][j] + 1),
              ledMatrix[i - 1][j - 1] + substitution);
        }
      }
    }
    return ledMatrix[size1][size2];
  }

  @Override
  public void setLevel(String level) {
    // TODO Auto-generated method stub
    this.level = level;
  }

  @Override
  public List<String> initializeGame() {
    Thread gameThread = new Thread(() -> {
      // Create a new thread to handle the task of the algorithm
      try {
        Thread.sleep(Constants.THREAD_SLEEP_TIME);
      } catch (InterruptedException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      List<Playlist> playlists = this.generatePlaylists();
      this.easy = playlists.get(0);
      this.medium = playlists.get(1);
      this.hard = playlists.get(2);
      String hostId = this.gameSession.getHost().getId();
      String gameCode = this.gameSession.getGameCode();
      WebSocketHandlers.replyReady(hostId, gameCode);
    });
    gameThread.start();
    return null;
  }

  @Override
  public Integer getPlayerCount() {
    return this.getPlayers().size();
  }

  @Override
  public String getGuess() {
    // TODO Auto-generated method stub
    return this.guess;
  }

  /**
   * Gets the easy playlist.
   *
   * @return returns the easy playlist.
   */
  public Playlist getEasy() {
    return this.easy;
  }

  /**
   * Gets the medium playlist.
   *
   * @return returns the medium playlist.
   */
  public Playlist getMedium() {
    return this.medium;
  }

  /**
   * Gets the hard playlist.
   *
   * @return the hard playlist
   */
  public Playlist getHard() {
    return this.hard;
  }

  @Override
  public void setInactive() {
    this.gameActive = false;
  }

  @Override
  public boolean beingRemoved() {
    // TODO Auto-generated method stub
    return this.beingRemoved;
  }

  @Override
  public void setRemoved(boolean to) {
    this.beingRemoved = to;
  }
}

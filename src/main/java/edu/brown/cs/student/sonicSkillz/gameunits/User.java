package edu.brown.cs.student.sonicSkillz.gameunits;

import java.util.List;

/**
 * User object used to model and access user data in backend.
 */
public class User {
  // initialized when object is created
  private final String id;
  private String gameCode;
  private String nickname;
  private boolean isHost;

  // set when user goes through authorization process
  private String spotifyDisplayName;
  private String apiAccessToken;
  private String apiRefreshToken;
  private String apiAuthorizationCode;
  private long apiLastRefreshTime;
//  private String spotifyAccountType;
  private String spotifyProfileImageUrl; // jpeg, url to download

  // initialized when specific api calls are called
  private List<Artist> topArtists;
  private List<Track> topTracks;
  private List<Artist> topRelatedArtists;
  private List<Track> topRelatedArtistTracks;

  /**
   * Instantiates a user using their Spotify ID.
   *
   * @param userId Obtained directly from Spotify to use as User's id in-game
   */
  public User(String userId) {
    this.id = userId;
    this.isHost = false;
  }

  /**
   * Getter method for user's ID.
   *
   * @return what id holds
   */
  public String getId() {
    return this.id;
  }

  /**
   * Getter method for the game session in which the user is playing.
   *
   * @return what gameCode holds
   */
  public String getGameCode() {
    return this.gameCode;
  }

  /**
   * Getter method for user's nickname.
   *
   * @return what nickname holds
   */
  public String getNickname() {
    return this.nickname;
  }

  /**
   * True when you create session, false when you join.
   *
   * @return result of whether or not user is a host
   */
  public boolean isHost() {
    return this.isHost;
  }

  /**
   * Sets whether or a not a given User is the host of a game.
   *
   * @param host true or false value of whether or not player is a host
   */
  public void setHost(boolean host) {
    this.isHost = host;
  }

  /**
   * Getter method for user's spotify display name.
   *
   * @return what spotifyDisplayName holds
   */
  public String getSpotifyDisplayName() {
    return this.spotifyDisplayName;
  }

  /**
   * Getter method for user's access token, needed to obtain information.
   *
   * @return what apiAccessToken holds
   */
  public String getApiAccessToken() {
    return this.apiAccessToken;
  }

  /**
   * Getter method for user's refresh token, needed to refresh access token.
   *
   * @return what apiRefreshToken holds
   */
  public String getApiRefreshToken() {
    return this.apiRefreshToken;
  }

  /**
   * Getter method for user's top artists.
   *
   * @return what topArtists holds
   */
  public List<Artist> getTopArtists() {
    return this.topArtists;
  }

  /**
   * Getter method for user's top tracks.
   *
   * @return what topTracks holds
   */
  public List<Track> getTopTracks() {
    return this.topTracks;
  }

  /**
   * Getter method for top related artists.
   *
   * @return what topRelatedArtists holds
   */
  public List<Artist> getTopRelatedArtists() {
    return this.topRelatedArtists;
  }

  /**
   * Getter method for top related artist tracks.
   *
   * @return what topRelatedArtistTracks holds
   */
  public List<Track> getTopRelatedArtistTracks() {
    return this.topRelatedArtistTracks;
  }

  /**
   * Holds the nickname a user sets for themselves in game.
   *
   * @param nickname passed in String, user input
   */
  public void setNickname(String nickname) {
    this.nickname = nickname;
  }

  /**
   * Holds user's spotify username from spotify.
   *
   * @param spotifyDisplayName from api
   */
  public void setSpotifyUsername(String spotifyDisplayName) {
    this.spotifyDisplayName = spotifyDisplayName;
  }

  /**
   * Obtains user's refresh token from spotify after authorization to refresh
   * access.
   *
   * @param apiRefreshToken user's refresh token from spotify api
   */
  public void setApiRefreshToken(String apiRefreshToken) {

    this.apiRefreshToken = apiRefreshToken;
  }

  /**
   * Obtains user's access token from spotify to get information.
   *
   * @param apiAccessToken user's access token from spotify api
   */
  public void setApiAccessToken(String apiAccessToken) {
    this.apiAccessToken = apiAccessToken;
  }

  /**
   * Stores user's top artists.
   *
   * @param topArtists List of Artists from Spotify API
   */
  public void setTopArtists(List<Artist> topArtists) {
    this.topArtists = topArtists;
  }

  /**
   * Stores user's top tracks.
   *
   * @param topTracks List of Tracks from Spotify API
   */
  public void setTopTracks(List<Track> topTracks) {
    this.topTracks = topTracks;
  }

  /**
   * Stores the related artists to user's top artists.
   *
   * @param topRelatedArtists a List of Artists obtained from Spotify API
   */
  public void setTopRelatedArtists(List<Artist> topRelatedArtists) {
    this.topRelatedArtists = topRelatedArtists;
  }

  /**
   * Stores the top tracks of artists related to user's top artists.
   *
   * @param topRelatedArtistTracks a List of Tracks obtained from Spotify API
   */
  public void setTopRelatedArtistTracks(List<Track> topRelatedArtistTracks) {
    this.topRelatedArtistTracks = topRelatedArtistTracks;
  }

  /**
   * Obtains the game session's code to associate with the player.
   *
   * @param gameCode session code to place player in session
   */
  public void setGameCode(String gameCode) {
    this.gameCode = gameCode;
  }

  /**
   * Getter method for user's authorization.
   *
   * @return what apiAuthorizationCode was set to
   */
  public String getApiAuthorizationCode() {
    return this.apiAuthorizationCode;
  }

  /**
   * Obtains authorization for the user's spotify account.
   *
   * @param apiAuthorizationCode valid authorization so spotify data can be used
   */
  public void setApiAuthorizationCode(String apiAuthorizationCode) {
    this.apiAuthorizationCode = apiAuthorizationCode;
  }

  /**
   * Getter method for refresh time, used to refresh access token.
   *
   * @return what apiLastRefreshTime was set to
   */
  public long getApiLastRefreshTime() {
    return this.apiLastRefreshTime;
  }

  /**
   * Obtains the last time the API was refreshed from Spotify for a given user.
   *
   * @param apiLastRefreshTime amount of time since refresh
   */
  public void setApiLastRefreshTime(long apiLastRefreshTime) {
    this.apiLastRefreshTime = apiLastRefreshTime;
  }

  /**
   * Getter method for user account type.
   *
   * @return what spotifyAccountType was set to
   */
//  public String getSpotifyAccountType() {
//    return this.spotifyAccountType;
//  }

  /**
   * Obtains type of account from spotify.
   *
   * @param spotifyAccountType free, premium, etc
   */
//  public void setSpotifyAccountType(String spotifyAccountType) {
//    this.spotifyAccountType = spotifyAccountType;
//  }

  /**
   * Getter method for user profile image.
   *
   * @return what spotifyProfileImageUrl was set to
   */
  public String getSpotifyProfileImageUrl() {
    return this.spotifyProfileImageUrl;
  }

  /**
   * Obtains profile image from spotify to set as user's image.
   *
   * @param spotifyProfileImageUrl url of image
   */
  public void setSpotifyProfileImageUrl(String spotifyProfileImageUrl) {
    this.spotifyProfileImageUrl = spotifyProfileImageUrl;
  }

  /**
   * Resets a user to play the next round
   */
  public void resetForNextRound() {
    this.gameCode = null;
    this.nickname = null;
    this.isHost = false;
  }
}

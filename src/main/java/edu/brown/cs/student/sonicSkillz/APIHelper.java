package edu.brown.cs.student.sonicSkillz;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.concurrent.TimeUnit;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import edu.brown.cs.student.Constants;
import edu.brown.cs.student.sonicSkillz.gameunits.Artist;
import edu.brown.cs.student.sonicSkillz.gameunits.Image;
import edu.brown.cs.student.sonicSkillz.gameunits.Track;
import edu.brown.cs.student.sonicSkillz.gameunits.User;

/**
 * Accesses the Spotify API and converts JSON objects from the API into Java
 * objects that we can use within our program. These fields are cached with
 * Databases or with Google Guava Caches depending on permanence.
 *
 */
public class APIHelper {

  /**
   * Given a User, queries Spotify API to retrieve a ranked list of their
   * top-played artists, parses into Artist objects, and sets the user's
   * "topArtists" property to this list. Ranking is determined by Spotify API and
   * formula is not public, but takes in at least time (the more recent the
   * higher) and frequency (the more frequent the higher) as factors.
   *
   * @param user the user we are finding top-played artists of
   * @return a list of the user's top artists
   * @throws IOException if API request is rejected by Spotify server for whatever
   *                     reason
   */

  public List<Artist> getUserArtists(User user) throws IOException {
    // check if access token expired
    if (isAccessExpired(user)) {
      refreshAccessToken(user);
      user.setApiAccessToken(refreshAccessToken(user));
    }

    String accessToken = user.getApiAccessToken();
    String request = "https://api.spotify.com/v1/me/top/artists?time_range=medium_term&limit"
        + "=50";
    JsonObject result = getRequest(accessToken, request); // GET request
    if (result.has("SONIC_SKILLZ_ERROR")) {
      System.err.println("ERROR: getUserArtists failed.");
      return null;
    }
    JsonArray items = result.getAsJsonArray("items");

    return parseArtists(items);
  }

  /**
   * Gets a ranked list of top tracks for a given user as determined by the
   * Spotify API.
   *
   * @param user user to find top tracks of
   * @return a list of user's top tracks
   * @throws IOException if API request is rejected by Spotify server for whatever
   *                     reason
   */
  public List<Track> getUserTracks(User user) throws IOException {
    // check if access token expired
    if (isAccessExpired(user)) {
      user.setApiAccessToken(refreshAccessToken(user));
    }
    String accessToken = user.getApiAccessToken();
    String request = "https://api.spotify.com/v1/me/top/tracks?time_range=medium_term&limit=50";
    JsonObject result = getRequest(accessToken, request); // GET request
    if (result.has("SONIC_SKILLZ_ERROR")) {
      System.err.println("ERROR: getUserTracks failed.");
      return null;
    }
    JsonArray items = result.getAsJsonArray("items");
    List<Track> tracks = parseTracks(items);
    List<Track> filtered = new ArrayList<Track>();
    for (Track track : tracks) {
      if (track.getPreviewUrl() == null) {
        System.out.println("Track is missing preview:" + track.getName());
      } else {
        filtered.add(track);
      }
    }
    return filtered;
  }

  /**
   * Gets a ranked list of up to 20 related artists for a given artist, as
   * determined by the Spotify API. Rank is weighted by artist similarity and
   * popularity, and possibly other factors (not public).
   *
   * @param user   user whose access token we will be using to access the API
   *               request
   * @param artist artist who we want to retrieve related artists of
   * @return a list of related artists
   * @throws IOException if API request is rejected by Spotify server for whatever
   *                     reason
   */
  public List<Artist> getRelatedArtists(User user, Artist artist) throws IOException {
    // check if access token expired
    if (isAccessExpired(user)) {
      user.setApiAccessToken(refreshAccessToken(user));
    }
    String accessToken = user.getApiAccessToken();
    String artistId = artist.getId();
    String request = "https://api.spotify.com/v1/artists/" + artistId + "/related-artists";
    JsonObject result = getRequest(accessToken, request); // GET request
    if (result.has("SONIC_SKILLZ_ERROR")) {
      System.err.println("ERROR: getRelatedArtists failed.");
      return null;
    }
    JsonArray artists = result.getAsJsonArray("artists");
    return parseArtists(artists);
  }

  /**
   * Gets a ranked list of up to 10 top tracks for a given artist, as determined
   * by the Spotify API.
   *
   * @param user   user we are using access token of
   * @param artist artist to find tracks of
   * @return a list of tracks
   * @throws IOException if API request is rejected by Spotify server for whatever
   *                     reason
   */
  public List<Track> getArtistTracks(User user, Artist artist) throws IOException {
    // check if the artist is in the cahce
    List<Track> topTracks = ArtistsTopTrackCache.getTopTracks(artist);
    if (topTracks != null) {
      return topTracks;
    }
    // check if access token expired
    if (isAccessExpired(user)) {
      user.setApiAccessToken(refreshAccessToken(user));
    }
    String accessToken = user.getApiAccessToken();
    String artistId = artist.getId();
    String request = "https://api.spotify.com/v1/artists/" + artistId + "/top-tracks?country="
        + Constants.SPOTIFY_MARKET;
    JsonObject result = getRequest(accessToken, request); // GET request
    if (result.has("SONIC_SKILLZ_ERROR")) {
      System.err.println("ERROR: getArtistTracks failed.");
      return null;
    }
    JsonArray tracksArr = result.getAsJsonArray("tracks");
    List<Track> out = parseTracks(tracksArr);

    List<Track> tracks = parseTracks(tracksArr);
    List<Track> filtered = new ArrayList<Track>();
    for (Track track : tracks) {
      if (track.getPreviewUrl() == null) {
        System.out.println("Track is missing preview:" + track.getName());
      } else {
        filtered.add(track);
      }
    }

    // add the results to the cache
    ArtistsTopTrackCache.putTopTracks(artist, filtered);
    return filtered;
  }

  /**
   * Gets all the tracks in a public Spotify playlist.
   *
   * @param user       user we are using access token of
   * @param playlistId id of the playlist so we can request the songs from Spotify
   * @return list of tracks inside the playlist
   * @throws IOException if API request is rejected by Spotify server for whatever
   *                     reason
   */
  public List<Track> getSpotifyPlaylist(User user, String playlistId) throws IOException {
    // check if access token expired
    if (isAccessExpired(user)) {
      user.setApiAccessToken(refreshAccessToken(user));
    }
    String accessToken = user.getApiAccessToken();
    String fields = "tracks.items(track(id,name,artists,uri,preview_url,popularity,duration_ms,"
        + "album))";
    String market = Constants.SPOTIFY_MARKET;
    String request = "https://api.spotify.com/v1/playlists/" + playlistId + "?" + "market=" + market
        + "&fields=" + fields;
    JsonObject result = getRequest(accessToken, request);
    if (result.has("SONIC_SKILLZ_ERROR")) {
      System.err.println("ERROR: getArtistTracks failed.");
      return null;
    }
    JsonArray itemsArr = result.get("tracks").getAsJsonObject().get("items").getAsJsonArray();

    // parse
    if (itemsArr.size() == 0) {
      System.err.println("ERROR: JsonArray is empty.");
    }
    List<Track> tracks = new ArrayList<>();
    for (int i = 0; i < itemsArr.size(); i++) { // for each item JSON object
      JsonObject trackData = itemsArr.get(i).getAsJsonObject().get("track").getAsJsonObject();
      String jsonString = trackData.toString();
      // directly convert JSON into Java object
      Track track = new Gson().fromJson(jsonString, Track.class);
      JsonArray imageArr = trackData.get("album").getAsJsonObject().get("images").getAsJsonArray();
      // custom parsing for images
      List<Image> images = new ArrayList<>();
      for (int n = 0; n < imageArr.size(); n++) {
        JsonObject imageJ = imageArr.get(n).getAsJsonObject();
        Image image = new Gson().fromJson(imageJ.toString(), Image.class);
        images.add(image);
      }
      track.setImages(images);
      tracks.add(track);
    }

    return tracks;
  }

  /**
   * Gets a link to download the user's Spotify profile image (JPEG), if
   * available.
   *
   * @param user user
   * @return image download url
   * @throws IOException if API request is rejected by Spotify server for whatever
   *                     reason
   */
  public String getUserImage(User user) throws IOException {
    // check if access token expired
    if (isAccessExpired(user)) {
      user.setApiAccessToken(refreshAccessToken(user));
    }
    String accessToken = user.getApiAccessToken();
    String request = "https://api.spotify.com/v1/me";
    JsonObject result = getRequest(accessToken, request); // GET request
    if (result.has("SONIC_SKILLZ_ERROR")) {
      System.err.println("ERROR: getArtistTracks failed.");
    }
    String profileUrl = "";
    if (result.get("images").getAsJsonArray() != null
        && result.get("images").getAsJsonArray().size() != 0) {
      // profile image
      profileUrl = result.get("images").getAsJsonArray().get(0).getAsJsonObject().get("url")
          .getAsString();
    }
    return profileUrl;
  }

  /**
   * Gets the user account type (premium, free, or open).
   *
   * @param user user
   * @return account type
   * @throws IOException if API request is rejected by Spotify server for whatever
   *                     reason
   */
  public String getAccountType(User user) throws IOException {
    // check if access token expired
    if (isAccessExpired(user)) {
      user.setApiAccessToken(refreshAccessToken(user));
    }
    String accessToken = user.getApiAccessToken();
    String request = "https://api.spotify.com/v1/me";
    JsonObject result = getRequest(accessToken, request); // GET request
    if (result.has("SONIC_SKILLZ_ERROR")) {
      System.err.println("ERROR: getArtistTracks failed.");
      return null;
    }
    return result.get("product").getAsString();
  }

  /**
   * POST request to retrieve a user's refresh token.
   *
   * @param user        user
   * @param redirectUri redirect URI supplied when requesting the authorization
   *                    code
   * @return user's refresh token
   */
  public String getRefreshToken(User user, String redirectUri) {
    String authCode = user.getApiAuthorizationCode();
    if (user.getApiAuthorizationCode() == null) {
      System.err.println("ERROR: User does not have an authorization code.");
      return null;
    }

    try {
      String request = "https://accounts.spotify.com/api/token";
      URL url = new URL(request);
      HttpURLConnection conn = (HttpURLConnection) url.openConnection();
      conn.setRequestMethod("POST");
      // set headers
      conn.setRequestProperty("Content-Type",
          "application/x-www-form-urlencoded; " + "charset=UTF-8");
      conn.setRequestProperty("Authorization", "Basic " + Constants.CLIENT_ID_SECRET);
      // set the “Accept” request header to “application/json” to read the response in
      // JSON
      conn.setRequestProperty("Accept", "application/json");
      // enable connection's doOutput property to true so we can write content to the
      // connection
      // output stream (i.e. send request content)
      conn.setDoOutput(true);

      // set parameters
      Map<String, String> arguments = new HashMap<>();
      arguments.put("grant_type", "authorization_code");
      arguments.put("code", authCode);
      // This parameter is used for validation only (there is no actual redirection).
      // The value of this parameter must exactly match the value of redirect_uri
      // supplied when
      // requesting the authorization code.
      arguments.put("redirect_uri", redirectUri);
      StringJoiner sj = new StringJoiner("&");
      for (Map.Entry<String, String> entry : arguments.entrySet()) {
        sj.add(URLEncoder.encode(entry.getKey(), "UTF-8") + "="
            + URLEncoder.encode(entry.getValue(), "UTF-8"));
      }
      byte[] out = sj.toString().getBytes(StandardCharsets.UTF_8);
      int length = out.length;
      conn.setFixedLengthStreamingMode(length);

      // write to output stream
      DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
      dos.write(out);
      dos.close();

      // connect to endpoint
      conn.connect();

      // get response body
      BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
      String responseLine;
      StringBuilder response = new StringBuilder();
      while ((responseLine = br.readLine()) != null) {
        response.append(responseLine);
      }
      JsonObject data = new Gson().fromJson(String.valueOf(response), JsonObject.class);
      conn.disconnect();
      return data.get("refresh_token").getAsString();
    } catch (MalformedURLException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
    System.err.println("ERROR: cannot retrieve access token.");
    return null;
  }

  /**
   * POST request to refresh a user's access token. Implicitly sets the user's
   * last refreshed time.
   *
   * @param user user to refresh access token of
   * @return returns a new access token
   */
  public String refreshAccessToken(User user) {
    String refreshToken = user.getApiRefreshToken();
    if (refreshToken == null) {
      System.err.println("ERROR: User does not have a refresh token.");
      return null;
    }

    try {
      String request = "https://accounts.spotify.com/api/token";
      URL url = new URL(request);
      HttpURLConnection conn = (HttpURLConnection) url.openConnection();
      conn.setRequestMethod("POST");
      // set headers
      conn.setRequestProperty("Content-Type",
          "application/x-www-form-urlencoded; " + "charset=UTF-8");
      conn.setRequestProperty("Authorization", "Basic " + Constants.CLIENT_ID_SECRET);
      // set the “Accept” request header to “application/json” to read the response in
      // the desired format
      conn.setRequestProperty("Accept", "application/json");
      // enable the URLConnection object's doOutput property to true to write content
      // to the connection output stream and send request content
      conn.setDoOutput(true);

      // create parameters
      Map<String, String> arguments = new HashMap<>();
      arguments.put("grant_type", "refresh_token");
      arguments.put("refresh_token", refreshToken);
      StringJoiner sj = new StringJoiner("&");
      for (Map.Entry<String, String> entry : arguments.entrySet()) {
        sj.add(URLEncoder.encode(entry.getKey(), "UTF-8") + "="
            + URLEncoder.encode(entry.getValue(), "UTF-8"));
      }
      byte[] out = sj.toString().getBytes(StandardCharsets.UTF_8);
      int length = out.length;
      conn.setFixedLengthStreamingMode(length);

      // write content to connection output stream
      DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
      dos.write(out);
      dos.close();

      conn.connect(); // connect to endpoint

      // read response body
      BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
      String responseLine;
      StringBuilder response = new StringBuilder();
      while ((responseLine = br.readLine()) != null) {
        response.append(responseLine);
      }
      // convert to JSON
      JsonObject data = new Gson().fromJson(String.valueOf(response), JsonObject.class);
      conn.disconnect();
      user.setApiLastRefreshTime(System.currentTimeMillis() / 1000); // seconds since Unix
                                                                     // epoch
      user.setApiAccessToken(data.get("access_token").getAsString());
      return data.get("access_token").getAsString();
    } catch (MalformedURLException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }

    System.err.println("ERROR: cannot refresh access token.");
    return null;
  }

  /**
   * Helper method to check if user's access token has expired.
   *
   * @param user user
   * @return true if access token has expired; else returns false.
   */
  private boolean isAccessExpired(User user) {
    long currTime = System.currentTimeMillis() / 1000; // in seconds
    return currTime - user.getApiLastRefreshTime() > Constants.FIFTY_FIVE_MINUTES;
  }

  /**
   * Helper method to parse a JsonArray of JsonObjects into Artist objects.
   *
   * @param artistsArr JsonArray
   * @return a list of Artist objects
   */
  private List<Artist> parseArtists(JsonArray artistsArr) {
    if (artistsArr.size() == 0) {
      System.err.println("ERROR: JsonArray is empty.");
    }

    List<Artist> artists = new ArrayList<>();
    for (int i = 0; i < artistsArr.size(); i++) { // for each artist JSON object
      JsonObject artistData = artistsArr.get(i).getAsJsonObject();
      String jsonString = artistData.toString();
      // directly convert JSON into Java object
      Artist artist = new Gson().fromJson(jsonString, Artist.class);
      artists.add(artist);
    }
    return artists;
  }

  /**
   * Helper method to parse a JsonArray of JsonObjects into Track objects.
   *
   * @param tracksArr JsonArray
   * @return a list of Track objects
   */
  private List<Track> parseTracks(JsonArray tracksArr) {
    if (tracksArr.size() == 0) {
      System.err.println("ERROR: JsonArray is empty.");
    }

    List<Track> tracks = new ArrayList<>();
    for (int i = 0; i < tracksArr.size(); i++) { // for each track JSON object
      JsonObject trackData = tracksArr.get(i).getAsJsonObject();
      String jsonString = trackData.toString();

      // directly convert JSON into Java object custom parsing for images
      Track track = new Gson().fromJson(jsonString, Track.class);
      JsonArray imageArr = trackData.get("album").getAsJsonObject().get("images").getAsJsonArray();
      List<Image> images = new ArrayList<>();
      for (int n = 0; n < imageArr.size(); n++) {
        JsonObject imageJ = imageArr.get(n).getAsJsonObject();
        Image image = new Gson().fromJson(imageJ.toString(), Image.class);
        images.add(image);
      }
      track.setImages(images);
      tracks.add(track);
    }
    return tracks;
  }

  /**
   * Helper method for generic GET request to an API that requires authorization.
   *
   * @param accessToken access token for API request
   * @param request     API request
   * @return JSON object of request result
   * @throws IOException if API request is rejected by Spotify server for whatever
   *                     reason
   */
  private JsonObject getRequest(String accessToken, String request) throws IOException {
    try {
      URL url = new URL(request);
      HttpURLConnection conn = (HttpURLConnection) url.openConnection();
      conn.setRequestMethod("GET");
      conn.setRequestProperty("Authorization", "Bearer " + accessToken);

      // GET request error
      if (conn.getResponseCode() != 200) {
        // Too many requests
        if (conn.getResponseCode() == 429) {
          // Spotify api will allow requests after this time interval (in seconds)
          long waitTime = Long.parseLong(conn.getHeaderField("Retry-After"));
          // if we can afford to wait
          if (waitTime < Constants.MAX_WAIT_TIME_S) {
            System.err.println("GET request failed, HTTP error code: " + conn.getResponseCode()
                + ", retrying after (s): " + conn.getHeaderField("Retry-After"));
            TimeUnit.SECONDS.sleep(waitTime + 1L);
            return getRequest(accessToken, request);
          } else {
            throw new RuntimeException(
                "GET request failed, HTTP error code: " + conn.getResponseCode()
                    + ", require retry after (s): " + conn.getHeaderField("Retry-After"));
          }
        } else {
          throw new RuntimeException("GET request failed, HTTP error code: "
              + conn.getResponseCode() + ", error message: " + conn.getResponseMessage());
        }
      }

      BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

      String output;
      StringBuilder sb = new StringBuilder();
      while ((output = br.readLine()) != null) {
        sb.append(output);
      }
      JsonObject data = new Gson().fromJson(String.valueOf(sb), JsonObject.class);
      conn.disconnect();
      return data;
    } catch (MalformedURLException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    return new Gson().fromJson("{ \"SONIC_SKILLZ_ERROR\": \"getRequest failed\" }",
        JsonObject.class);
  }

}

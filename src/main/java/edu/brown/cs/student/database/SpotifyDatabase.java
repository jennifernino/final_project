package edu.brown.cs.student.database;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import edu.brown.cs.student.Constants;
import edu.brown.cs.student.sonicSkillz.APIHelper;
import edu.brown.cs.student.sonicSkillz.gameunits.SpotifyPlaylist;
import edu.brown.cs.student.sonicSkillz.gameunits.Track;
import edu.brown.cs.student.sonicSkillz.gameunits.User;

public class SpotifyDatabase
{
  private static String filename = null;
  private static Connection conn = null;
  private static final int CACHE_SIZE = 50000;
  private static boolean playlistsLoaded = false;
  private static Set<SpotifyPlaylist> thisIsPlaylists = new HashSet<>();
  private static Set<SpotifyPlaylist> spotifyPlaylists = new HashSet<>();
  private static Cache<String, SpotifyPlaylist> idToSpotifyPlaylist = CacheBuilder
      .newBuilder().build();
  private static Cache<String, Track> idToTrack = CacheBuilder.newBuilder().build();

  public static void createConnection(String file)
      throws SQLException, ClassNotFoundException {
    if (conn != null) {

      // Clear caches if any

      // close connection variables
      filename = null;
      conn.close();
      conn = null;
    }

    filename = file;
    Class.forName("org.sqlite.JDBC");
    String urlToDB = "jdbc:sqlite:" + filename;
    conn = DriverManager.getConnection(urlToDB);
    Statement stat = conn.createStatement();
    stat.executeUpdate("PRAGMA foreign_keys=ON;");
    stat.close();
  }

  public static Set<SpotifyPlaylist> getSetOfThisIsPlaylists(User user)
      throws IOException {
    System.out.println("getSetOfThisIsPlaylists");
    if (!playlistsLoaded) {
      loadAllSpotifyPlaylists(user);
    }
    return thisIsPlaylists;
  }

  public static Set<SpotifyPlaylist> getSetOfSpotifyPlaylists(User user)
      throws IOException {
    System.out.println("getSetOfSpotifyPlaylists");
    if (!playlistsLoaded) {
      loadAllSpotifyPlaylists(user);
    }
    return spotifyPlaylists;
  }

  public static SpotifyPlaylist getSpotifyPlaylist(String playlistId) throws IOException {
    // System.out.println("getSpotifyPlaylist");
    SpotifyPlaylist playlist = idToSpotifyPlaylist.getIfPresent(playlistId);
    APIHelper api = new APIHelper();
    if (playlist == null) {
      playlist = api.getSpotifyPlaylist(Constants.OAUTH_TOKEN, playlistId);
      if (playlist != null) {
        idToSpotifyPlaylist.put(playlistId, playlist);
      }
    } else {
      if (playlist.getTracks() == null || playlist.getTracks().size() < 1) {
        List<Track> tracks = api.getSpotifyPlaylistTracks(Constants.OAUTH_TOKEN,
            playlistId);
        idToSpotifyPlaylist.getIfPresent(playlistId).setTracks(tracks);
      }
    }
    return idToSpotifyPlaylist.getIfPresent(playlistId);
  }

  public static SpotifyPlaylist getSpotifyPlaylist(String playlistId, String token)
      throws IOException {
    // System.out.println("getSpotifyPlaylist");
    SpotifyPlaylist playlist = idToSpotifyPlaylist.getIfPresent(playlistId);
    APIHelper api = new APIHelper();
    if (playlist == null) {

      playlist = api.getSpotifyPlaylist(token, playlistId);
      if (playlist != null) {
        idToSpotifyPlaylist.put(playlistId, playlist);
      }
    } else {
      if (playlist.getTracks() == null || playlist.getTracks().size() < 1) {
        List<Track> tracks = api.getSpotifyPlaylistTracks(Constants.OAUTH_TOKEN,
            playlistId);
        idToSpotifyPlaylist.getIfPresent(playlistId).setTracks(tracks);
      }
    }
    return idToSpotifyPlaylist.getIfPresent(playlistId);
  }

  public static Track getTrackNoQuery(String trackId) {
    // System.out.println("getTrackNoQuery");
    return idToTrack.getIfPresent(trackId);
  }

//  public static Track getTrack(String trackId) {
//    return null;
//  }
//
//  public static Track getTrack(String trackId, String token) {
//    return null;
//  }

  public static void setTrack(String trackId, Track track) {
    // System.out.println("setTrack");
    idToTrack.put(trackId, track);
  }

  public static void loadAllSpotifyPlaylists() throws IOException {
    System.out.println("loadAllSpotifyPlaylists");
    APIHelper api = new APIHelper();
    List<SpotifyPlaylist> playlists = api.getAllSpotifyPlaylists(Constants.OAUTH_TOKEN);
    for (SpotifyPlaylist playlist : playlists) {
      if (playlist.getName().toLowerCase().contains("this is")
          || playlist.getName().toLowerCase().contains("this is:")) {
        thisIsPlaylists.add(playlist);
      } else {
        spotifyPlaylists.add(playlist);
      }
    }
    playlistsLoaded = true;
  }

  public static void loadAllSpotifyPlaylists(User user) throws IOException {
    System.out.println("loadAllSpotifyPlaylists");
    APIHelper api = new APIHelper();
    String token = user.getApiAccessToken();
    List<SpotifyPlaylist> playlists = api.getAllSpotifyPlaylists(token);
    for (SpotifyPlaylist playlist : playlists) {
      if (playlist.getName().toLowerCase().contains("this is")
          || playlist.getName().toLowerCase().contains("this is:")) {
        thisIsPlaylists.add(playlist);
      } else {
        spotifyPlaylists.add(playlist);
      }
    }
    playlistsLoaded = true;
  }
}

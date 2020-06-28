package edu.brown.cs.student.sonicSkillz;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import edu.brown.cs.student.database.SpotifyDatabase;
import edu.brown.cs.student.sonicSkillz.gameunits.Playlist;
import edu.brown.cs.student.sonicSkillz.gameunits.SpotifyPlaylist;
import edu.brown.cs.student.sonicSkillz.gameunits.Track;
import edu.brown.cs.student.sonicSkillz.gameunits.User;

public class SpotifyPlaylistGenerator extends PlaylistGenerator
{
  public APIHelper api;
  public DatabaseHelper database;
  private final String playlistOption;

  /**
   * Instantiates class and the APIHelper and DatabaseHelper.
   */
  public SpotifyPlaylistGenerator(String playlistOption) {
    this.api = new APIHelper();
    this.database = new DatabaseHelper();
    this.playlistOption = playlistOption;
  }

  @Override
  public List<Playlist> generate(List<User> users, Integer numSongsPerPlayer) {
    List<Track> tracks = new ArrayList<>();
    User user = users.get(0);
    try {
      SpotifyPlaylist playlist = SpotifyDatabase.getSpotifyPlaylist(this.playlistOption,
          user.getApiAccessToken());
      tracks = playlist.getTracks();
      tracks.forEach(n -> n.cleanNames());

    } catch (IOException e1) {
      // TODO Auto-generated catch block
      e1.printStackTrace();
      return Arrays.asList();
    }
    Collections.shuffle(tracks);

    int n = tracks.size();
    int k = numSongsPerPlayer;
    List<Playlist> lists = this.playlistDivisions(tracks, n, k);

    return lists;
  }

  @Override
  public APIHelper getAPIHelper() {
    return this.api;
  }

  @Override
  public DatabaseHelper getDatabaseHelper() {
    return this.database;
  }

  private List<Playlist> playlistDivisions(List<Track> tracks, int n, int k) {
    Playlist easyPlaylist = new Playlist();
    Playlist medPlaylist = new Playlist();
    Playlist hardPlaylist = new Playlist();
    if (k <= n) {
      for (Track t : tracks) {
        easyPlaylist.addTrack(t);
        medPlaylist.addTrack(t);
        hardPlaylist.addTrack(t);
      }
    } else {
      int nmid = (int) Math.floor(n / 2.0);
      int kmid = (int) Math.floor(k / 2.0);
      int start = nmid - kmid;
      int end = nmid + kmid;
      if (k % 2 > 0) {
        // is odd
        end += 1;
      }
      System.out.println("start: " + start);
      System.out.println("start: " + end);
      tracks.subList(start, end).forEach(t -> medPlaylist.addTrack(t));
      for (int i = tracks.size() - k; i < tracks.size(); i += 1) {
        hardPlaylist.addTrack(tracks.get(i));
      }
      for (int i = 0; i < k; i += 1) {
        easyPlaylist.addTrack(tracks.get(i));
      }
    }
    easyPlaylist.shufflePlaylist();
    medPlaylist.shufflePlaylist();
    hardPlaylist.shufflePlaylist();
    List<Playlist> lists = Arrays.asList(easyPlaylist, medPlaylist, hardPlaylist);
    return lists;
  }
}

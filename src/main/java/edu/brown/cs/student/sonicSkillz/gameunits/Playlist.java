package edu.brown.cs.student.sonicSkillz.gameunits;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Class to compile a bunch of songs. Treated as a singular unit so we can refer
 * to the whole Playlist to obtain songs, rather than having to deal with a load
 * of Track objects. Basically, a wrapper for Tracks.
 *
 *
 */
public class Playlist implements Serializable {
  private static final long serialVersionUID = 1L;
  private List<Track> playlist;

  /**
   * Instantiates a Playlist object.
   */
  public Playlist() {
    this.playlist = new ArrayList<Track>();
  }

  /**
   * Adds a Track object to a playlist.
   *
   * @param track Track we want to add to the playlist
   */
  public void addTrack(Track track) {
    this.playlist.add(track);
  }

  /**
   * Overload method to allow multiple tracks to be added at once.
   *
   * @param tracks Track objects we want to add to the Playlist
   */
  public void addTrack(Track... tracks) {
    Collections.addAll(this.playlist, tracks);
  }

  /**
   * Getter method to get all of the tracks contained in the playlist.
   *
   * @return A List of Track objects in the playlist
   */
  public List<Track> getTracks() {
    return this.playlist;
  }

  /**
   * Reorders the playlist so tracks are in a different order.
   */
  public void shufflePlaylist() {
    Collections.shuffle(this.playlist);
  }

  /**
   * Gets a particular track from the Playlist based on its index in the playlist.
   *
   * @param target Int for the index of the track
   * @return Track object at that index of the playlist
   */
  public Track getTrack(int target) {
    return this.playlist.get(target);
  }

  /**
   * Gets the size of the playlist, used for adjustment in gameplay.
   *
   * @return Integer pertaining to number of tracks in playlist
   */
  public Integer getSize() {
    return this.playlist.size();
  }
}

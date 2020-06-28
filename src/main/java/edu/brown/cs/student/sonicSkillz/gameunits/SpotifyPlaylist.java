package edu.brown.cs.student.sonicSkillz.gameunits;

import java.io.Serializable;
import java.util.List;

public class SpotifyPlaylist implements Comparable<SpotifyPlaylist>, Serializable
{
  // autofill
  private static final long serialVersionUID = 1L;
  private final String id;
  private String name;
  private String uri;
  private String description;
  private String image_url;
  private String owner;
  private String external_url;

  // non-autofill
  private List<Track> tracks;

  public SpotifyPlaylist(String spotifyPlaylistId, String name, String uri,
      String description, String imageUrl, String owner, String externalUrl) {
    this.id = spotifyPlaylistId;
    this.name = name;
    this.uri = uri;
    this.description = description;
    this.image_url = imageUrl;
    this.owner = owner;
    this.external_url = externalUrl;
    this.tracks = null;
  }

  @Override
  public int compareTo(SpotifyPlaylist o) {
    // TODO Auto-generated method stub
    return 0;
  }

  public List<Track> getTracks() {
    return this.tracks;
  }

  public void setTracks(List<Track> tracks) {
    this.tracks = tracks;
  }

  public String getId() {
    return this.id;
  }

  public String getName() {
    return this.name;
  }

  public String getUri() {
    return this.uri;
  }

  public String getDescription() {
    return this.description;
  }

  public String getImage_url() {
    return this.image_url;
  }

  public String getOwner() {
    return this.owner;
  }

  public String getExternal_url() {
    return this.external_url;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof SpotifyPlaylist) {
      SpotifyPlaylist toCompare = (SpotifyPlaylist) obj;
      return this.id.equals(toCompare.getId());
    }
    return false;
  }

  @Override
  public int hashCode() {
    return this.id.hashCode();
  }
}

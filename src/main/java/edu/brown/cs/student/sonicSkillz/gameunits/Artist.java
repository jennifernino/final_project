package edu.brown.cs.student.sonicSkillz.gameunits;

import java.io.Serializable;
import java.util.List;

/**
 * Directly translates Spotify's artist information from JSON into Java objects
 * used to get information on songs to generate relevant playlists for games.
 *
 */
public class Artist implements Serializable {
  private static final long serialVersionUID = 1L;
  // autofill from JSON object
  private final String id;
  private String name;
  private String uri;
  private List<String> genres; // possible to be empty (no data available)
  private int popularity;

  // non autofill
  private List<Artist> relatedArtists;
  private List<Track> topArtistTracks;

  /**
   * Instantiates an Artist object and sets their id upon instantiation.
   *
   * @param artistId As retrieved from Spotify, and the id used to refer to them
   *                 in-game
   */
  public Artist(String artistId) {
    this.id = artistId;
  }

  /**
   * Getter method for the artist ID on spotify.
   *
   * @return String id
   */
  public String getId() {
    return this.id;
  }

  /**
   * Sets the name of a Spotify artist by their ID.
   *
   * @param n The name of the artist as retrieved from Spotify
   */
  public void setName(String n) {
    this.name = n;
  }

  /**
   * Getter method for the name of an Artist object.
   *
   * @return String name
   */
  public String getName() {
    return this.name;
  }

  /**
   * Obtained from Spotify API to set artist's Uri.
   *
   * @param artistUri A String that is their Uri
   */
  public void setUri(String artistUri) {
    this.uri = artistUri;
  }

  /**
   * Getter method for an artist's Uri.
   *
   * @return String that is the Uri
   */
  public String getUri() {
    return this.uri;
  }

  /**
   * Obtained from Spotify API, a numerical rating of an artist's popularity.
   *
   * @param p The int representing popularity
   */
  public void setPopularity(int p) {
    this.popularity = p;
  }

  /**
   * Getter method for an artist's popularity as measured by Spotify metrics.
   *
   * @return int representing popularity
   */
  public int getPopularity() {
    return this.popularity;
  }

  /**
   * Getter method for an artist's associated genres.
   *
   * @return List of String objects, what is stored in genres
   */
  public List<String> getGenres() {
    return this.genres;
  }

  /**
   * Queries Spotify API to get genres associated with an artist's music.
   *
   * @param genres A List of Strings pertaining to genres of music
   */
  public void setGenres(List<String> genres) {
    this.genres = genres;
  }

  /**
   * Getter method for an artist's similar artists.
   *
   * @return List of Artist objects
   */
  public List<Artist> getRelatedArtists() {
    return this.relatedArtists;
  }

  /**
   * Queries API to get all artists related to a given artist.
   *
   * @param relatedArtists List of Artists similar to an artist
   */
  public void setRelatedArtists(List<Artist> relatedArtists) {
    this.relatedArtists = relatedArtists;
  }

  /**
   * Getter method for an artist's top tracks.
   *
   * @return List of Track objects
   */
  public List<Track> getTopArtistTracks() {
    return this.topArtistTracks;
  }

  /**
   * Queries API to get the artist's top tracks.
   *
   * @param topArtistTracks A list of Tracks that are the most popular by an
   *                        artist
   */
  public void setTopArtistTracks(List<Track> topArtistTracks) {
    this.topArtistTracks = topArtistTracks;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof Artist) {
      Artist toCompare = (Artist) obj;
      return this.id.equals(toCompare.getId());
    }
    return false;
  }

  @Override
  public int hashCode() {
    return this.id.hashCode();
  }
}

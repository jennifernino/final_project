package edu.brown.cs.student.sonicSkillz.gameunits;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The rack class gives us a metric of comparing songs and holds all relevant
 * song information from Spotify as well as weighting and information directly
 * relevant to games being played within a session.
 *
 */

public class Track implements Comparable<Track>, Serializable {
  private static final long serialVersionUID = 1L;
  // autofill from JSON object
  private final String id;
  private String name;
  private List<Artist> artists;
  // Checkstyle appeal: due to JSON to Java object conversion, this instance
  // variable must match JSON property name
  private String preview_url; // possible to be null
  private String uri;
  private int popularity;
  private List<Image> images;
  // Checkstyle appeal: due to JSON to Java object conversion, this instance
  // variable must match JSON property name
  private int duration_ms; // in milliseconds

  // non-autofill
  private String imageUrl; // 640x640 px album image
  private double weight;
  private List<String> cleanedNames;

  /**
   * Instantiates a Track object using its ID from Spotify.
   *
   * @param trackId String pertaining to ID
   */
  public Track(String trackId) {
    this.id = trackId;
    this.weight = 0;
  }

  /**
   * Getter method for the URL of the album/track image.
   *
   * @return String pertaining to image URL
   */
  public String getImageURL() {
    if (this.images.size() > 0) {
      return this.images.get(0).getUrl();
    }
    return "";
  }

  /**
   * Getter method for the ID of a song.
   *
   * @return String pertaining to ID
   */
  public String getId() {
    return this.id;
  }

  /**
   * Sets the name of the song as given by Spotify API.
   *
   * @param n String for the name
   */
  public void setName(String n) {
    this.name = n;
  }

  /**
   * Getter method for the name of a song as given by Spotify.
   *
   * @return String pertaining to name
   */
  public String getName() {
    return this.name;
  }

  /**
   * An editor for names that can be compared with user input for games, so that
   * items such as case, features, etc. are not necessary to get a correct answer.
   */
  public void cleanNames() {
    if (this.name == null) {
      return;
    }
    this.cleanedNames = new ArrayList<String>();
    // Lowercase and remove puncuation.
    String lower = this.name.toLowerCase().trim();
    this.cleanedNames.add(lower);
    // Remove brackets + dashes and their contents.
    while (lower.contains("(") && lower.contains(")")) {
      int start = lower.indexOf("(");
      int end = lower.indexOf(")");
      String remove = lower.substring(start, end + 1);
      // Edge case: Add the string with everything removed after the first bracket
      this.cleanedNames.add(lower.substring(0, start).trim());
      lower = lower.replace(remove, "");
      // Add alternative titles found in the brackets.
      remove.replace("(", "");
      remove.replace(")", "");
      if (!remove.contains("feat.") && !remove.contains("ft.") && !remove.contains("remix")) {
        this.cleanedNames.add(remove);
      }
    }
    while (lower.contains("[") && lower.contains("]")) {
      int start = lower.indexOf("[");
      int end = lower.indexOf("]");
      String remove = lower.substring(start, end + 1);
      lower = lower.replace(remove, "");
      // Add alternative titles found in the brackets.
      remove.replace("[", "");
      remove.replace("]", "");
      if (!remove.contains("feat.") && !remove.contains("ft.") && !remove.contains("remix")) {
        this.cleanedNames.add(remove);
      }
    }
    while (lower.contains("-")) {
      int start = lower.indexOf("-");
      String remove = lower.substring(start);
      lower = lower.replace(remove, "");
    }
    lower = lower.trim().replaceAll("  +", " ");
    if (!this.cleanedNames.contains(lower)) {
      this.cleanedNames.add(lower);
    }
    // Remove punctuation and extra spaces
    lower = lower.replaceAll("\\p{Punct}", "");
    lower = lower.trim().replaceAll("  +", " ");
    if (!this.cleanedNames.contains(lower)) {
      this.cleanedNames.add(lower);
    }
    // Word Replacement c
    ArrayList<String> replaced = new ArrayList<String>();
    replaced.add(this.replaceWord(lower, "you", "u"));
    replaced.add(this.replaceWord(lower, "u", "you"));
    replaced.add(this.replaceWord(lower, "ok", "okay"));
    replaced.add(this.replaceWord(lower, "okay", "ok"));
    for (String replace : replaced) {
      if (replace != null) {
        this.cleanedNames.add(replace);
      }
    }
  }

  /**
   * Replaces words within the song title as part of a further search for possible
   * answers.
   *
   * @param str  The name of the song
   * @param alt  String we want to replace
   * @param alt2 String we want to replace with
   * @return String pertaining to an acceptable answer for song name
   */
  public String replaceWord(String str, String alt, String alt2) {
    if (str.startsWith(alt + " ") || str.contains(" " + alt + " ") || str.endsWith(" " + alt)) {
      // Swap instances from the middle.
      String newString = str.replace(" " + alt + " ", " " + alt2 + " ");
      // End
      if (newString.endsWith(" " + alt)) {
        newString = newString.substring(0, newString.length() - alt.length()) + alt2;
      }
      // Beginning
      if (newString.startsWith(alt + " ")) {
        newString = alt2 + newString.substring(alt.length(), newString.length());
      }
      return newString;
    }
    return null;
  }

  /**
   * Gets a List of acceptable answers a user can pass in to get the correct
   * answer for a song name.
   *
   * @return List of Strings for possible answers - the answer key, basically
   */
  public List<String> getCleanNames() {
    return this.cleanedNames;
  }

  /**
   * Getter method for the song's artist.
   *
   * @return List of Artists
   */
  public List<Artist> getArtists() {
    return this.artists;
  }

  /**
   * Sets the artist(s) who created the particular song.
   *
   * @param artists List of Artists who created the song
   */
  public void setArtists(List<Artist> artists) {
    this.artists = artists;
  }

  /**
   * Getter method for the URL on Spotify server of the song.
   *
   * @return String pertaining to URL
   */
  public String getPreviewUrl() {
    return this.preview_url;
  }

  /**
   * Sets the preview URL of a song on Spotify.
   *
   * @param preview_url String pertaining to URL
   */
  public void setPreviewUrl(String preview_url) {
    this.preview_url = preview_url;
  }

  /**
   * Sets the uri of a song from Spotify API.
   *
   * @param trackUri A String pertaining to the track's Uri.
   */
  public void setUri(String trackUri) {
    this.uri = trackUri;
  }

  /**
   * Getter method for Uri of a song.
   *
   * @return String pertaining to Uri
   */
  public String getUri() {
    return this.uri;
  }

  /**
   * Sets the overall popularity of a song based on Spotify API info.
   *
   * @param p Int pertaining to popularity
   */
  public void setPopularity(int p) {
    this.popularity = p;
  }

  /**
   * Getter method for overall popularity from spotify API.
   *
   * @return Int pertaining to popularity
   */
  public int getPopularity() {
    return this.popularity;
  }

  /**
   * Getter method for duration_ms.
   *
   * @return String pertaining to track duration
   */
  public int getDuration_ms() {
    return this.duration_ms;
  }

  /**
   * Sets the duration of the the track from spotify api.
   *
   * @param duration_ms String pertaining to ms
   */
  public void setDuration_ms(int duration_ms) {
    this.duration_ms = duration_ms;
  }

  /**
   * Sets the weight of a song in priority queue based on overall and user
   * popularity.
   *
   * @param w Double pertaining to the weight assigned
   */
  public void setWeight(double w) {
    this.weight = w;
  }

  /**
   * Getter method for the weight of a track in our priotiy queue.
   *
   * @return Double pertaining to weight
   */
  public double getWeight() {
    return this.weight;
  }

  /**
   * Getter method for album image.
   *
   * @return A list of images
   */
  public List<Image> getImages() {
    return this.images;
  }

  /**
   * Sets album image associated with track from spotify api.
   *
   * @param images A list of images to set
   */
  public void setImages(List<Image> images) {
    this.images = images;
  }

  @Override
  public String toString() {
    String trackString = "";
    trackString += "Track: {Name:" + this.name + ", Artists:";
    List<String> names = this.artists.stream().map(x -> x.getName()).collect(Collectors.toList());

    trackString += String.join(",", names);
    trackString += ", " + this.uri;
    trackString += "}";
    return trackString;
  }

  @Override
  public int compareTo(Track other) {
    Double dif = this.weight - other.getWeight();
    return (int) Math.signum(dif);
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof Track) {
      Track toCompare = (Track) obj;
      return this.id.equals(toCompare.getId());
    }
    return false;
  }

  @Override
  public int hashCode() {
    return this.id.hashCode();
  }

}

package edu.brown.cs.student.sonicSkillz.gameunits;

import java.io.Serializable;

/**
 * Image is structured after the Spotify API "image" JSON object so it can be directly parsed
 * from API calls.
 * Image uses a URL from the Spotify API so that each Track can have a corresponding image, and
 * we can display that image on our webpage.
 *
 */
public class Image implements Serializable {
  private static final long serialVersionUID = 1L;
  private final String url;
  private int width;
  private int height;

  /**
   * Instantiates Image object using the URL from Spotify.
   *
   * @param url String from Spotify JSON
   */
  public Image(String url) {
    this.url = url;
  }

  /**
   * Getter method for the URL of an image.
   *
   * @return String pertaining to URL.
   */
  public String getUrl() {
    return this.url;
  }

  /**
   * Getter method for the width of an image.
   *
   * @return Int measurement
   */
  public int getWidth() {
    return this.width;
  }

  /**
   * Sets the width of the image.
   *
   * @param width Int measurement
   */
  public void setWidth(int width) {
    this.width = width;
  }

  /**
   * Getter method for the length of an image.
   *
   * @return Int measurement
   */
  public int getHeight() {
    return this.height;
  }

  /**
   * Sets the length of an image.
   *
   * @param height Int measurement
   */
  public void setHeight(int height) {
    this.height = height;
  }
}

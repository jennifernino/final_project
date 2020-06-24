package edu.brown.cs.student.sonicSkillz;

import java.util.List;
import java.util.concurrent.TimeUnit;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import edu.brown.cs.student.Constants;
import edu.brown.cs.student.sonicSkillz.gameunits.Artist;
import edu.brown.cs.student.sonicSkillz.gameunits.Track;

/**
 * Artists Top Track Cache! Stores the top tracks of artists to optimize API
 * calls.
 *
 * @author noell
 *
 */
public class ArtistsTopTrackCache {

  private static Cache<Artist, List<Track>> artistsTopTrackCache; // the cache variable

  /**
   * This static block acts as a constructor for the Artists' Top Track Cache.
   */
  static {
    artistsTopTrackCache = CacheBuilder.newBuilder().maximumSize(Constants.MAX_CACHE_CAPACITY)
        .expireAfterAccess(Constants.CACHE_EXPIRATION_DAYS, TimeUnit.DAYS).build();
  }

  /**
   * Retrieves an artist's top tracks from the cache.
   *
   * @param artist the artist being retrieved.
   * @return a list of the artist's top tracks.
   */
  public static List<Track> getTopTracks(Artist artist) {
    return artistsTopTrackCache.getIfPresent(artist);
  }

  /**
   * Adds an artist's top tracks to the cache.
   *
   * @param artist    the artist being added to the cache.
   * @param topTracks a list of the artist's top tracks.
   */
  public static void putTopTracks(Artist artist, List<Track> topTracks) {
    artistsTopTrackCache.put(artist, topTracks);
  }

  /**
   * Gets the cache of artists and tracks.
   *
   * @return a cache
   */
  public Cache<Artist, List<Track>> getCache() {
    return artistsTopTrackCache;
  }

}
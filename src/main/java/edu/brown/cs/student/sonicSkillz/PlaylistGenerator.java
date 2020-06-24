package edu.brown.cs.student.sonicSkillz;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.Set;

import edu.brown.cs.student.Constants;
import edu.brown.cs.student.sonicSkillz.gameunits.Artist;
import edu.brown.cs.student.sonicSkillz.gameunits.Playlist;
import edu.brown.cs.student.sonicSkillz.gameunits.Track;
import edu.brown.cs.student.sonicSkillz.gameunits.User;

/**
 * Holds the playlist generating algorithm. Playlist uses the API as well as
 * information stored in a remote cached Database in order to generate
 * customized playlists based on user songs, user artists, and level of
 * difficulty.
 *
 *
 */
public class PlaylistGenerator {

  public APIHelper api;
  public DatabaseHelper database;

  /**
   * Instantiates class and the APIHelper and DatabaseHelper.
   */
  public PlaylistGenerator() {
    this.api = new APIHelper();
    this.database = new DatabaseHelper();
  }

  /**
   * Generates a random easy, medium, and hard playlist based on a combination of
   * the input users libraries.
   *
   * @param users             the set of users who's libraries the playlist will
   *                          be based on
   * @param numSongsPerPlayer the number of songs per user in the playlist (does
   *                          not correlate to the number of songs that comes from
   *                          each library)
   * @return a list of playlists, in the order easy, medium, hard
   */
  public List<Playlist> generate(List<User> users, Integer numSongsPerPlayer) {
    Map<Track, Integer> dictSong = new HashMap<Track, Integer>();
    ArrayList<Track> balanceSongs = new ArrayList<Track>();
    Integer numForBalance = numSongsPerPlayer / Constants.DENOMINATOR_OF_BALANCE_FRACTION;
    // 1. Get music from API
    try {
      for (User player : users) {

        player.setTopTracks(this.api.getUserTracks(player));
        player.setTopArtists(this.api.getUserArtists(player));
        List<Track> topTracks = player.getTopTracks();
        if (topTracks != null && topTracks.size() > 0) {
          // Get balance songs
          LinkedList<Integer> toBalance = this.getPermutation(0, topTracks.size());
          for (int i = 0; i < toBalance.size() && i < numForBalance; i++) {
            Track song = topTracks.get(toBalance.get(i));
            song.cleanNames();
            if (!balanceSongs.contains(song)) {
              balanceSongs.add(topTracks.get(toBalance.get(i)));
            } else {
              toBalance.remove(toBalance.get(i));
              i--;
            }
          }
          // ADD TOP TRACKS TO MEGALIST
          for (int i = 0; i < Constants.MAX_NUMBER_TOP_SONGS_FROM_LIBRARY
              && i < topTracks.size(); i++) {
            topTracks.get(i).cleanNames();
            Integer result = dictSong.putIfAbsent(topTracks.get(i), 1);
            // If it is present update the value.
            if (result != null) {
              result++;
              dictSong.remove(topTracks.get(i));
              dictSong.put(topTracks.get(i), result);
            }
          }
        }

        // ADD THE SONGS OF TOP ARTISTS TO MEGALIST
        List<Artist> topArtists = player.getTopArtists();
        if (topArtists != null && topArtists.size() > 0) {
          for (int i = 0; i < Constants.MAX_NUMBER_OF_TOP_ARTISTS
              && i < player.getTopArtists().size(); i++) {
            List<Track> topArtistTracks = this.api.getArtistTracks(player, topArtists.get(i));
            for (int j = 0; j < Constants.MAX_NUMBER_SONGS_FROM_TOP_ARTISTS
                && j < topArtistTracks.size(); j++) {
              topArtistTracks.get(j).cleanNames();
              dictSong.putIfAbsent(topArtistTracks.get(j), 0);
            }
          }
        }

      }
      Integer shortage = (numSongsPerPlayer * users.size()) - dictSong.size();
      if (shortage > 0) {
        DatabaseHelper data = new DatabaseHelper();
        List<Track> songs = data.openPlaylist("data/top2019").getTracks();
        for (Track song : songs) {
          song.cleanNames();
          dictSong.putIfAbsent(song, 0);
          shortage--;
          if (shortage == 0) {
            break;
          }
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    // Remove balance songs from dict.
    for (Track bsong : balanceSongs) {
      dictSong.remove(bsong);
    }
    // 2. Weight the tracks and add to the priority queue.
    Set<Track> megaTrackSet = dictSong.keySet();
    Iterator<Track> track = megaTrackSet.iterator();
    PriorityQueue<Track> pq = new PriorityQueue<Track>();
    Double simNumerator = 0.0;
    while (track.hasNext()) {
      Track song = track.next();
      Double occurences = dictSong.get(song).doubleValue();
      simNumerator += occurences;
      // Weighting math
      Double w1;
      if (occurences.equals(0.0)) {
        w1 = Math.pow(Constants.FRACTION_OF_APPEARANCES_OUTSIDE_OF_TOP_TRACKS / users.size(),
            Constants.EXPONENT_OF_LIBRARY_APPEARNCES);
      } else {
        w1 = Math.pow(occurences / users.size(), Constants.EXPONENT_OF_LIBRARY_APPEARNCES);
      }
      Double w2 = Math.pow(song.getPopularity() / Constants.POPULARITY_MAX,
          Constants.EXPONENT_OF_POPULARITY);
      song.setWeight(w1 * w2);
      System.out.println("EARLIER PRINTS: " + song.getName() + ": weight=" + song.getWeight()
          + "; w1=" + w1 + "; w2=" + w2 + "; occurences=" + dictSong.get(song) + "; popularity="
          + song.getPopularity());
      pq.add(song);
    }
    Double similarity = simNumerator
        / (Constants.MAX_NUMBER_TOP_SONGS_FROM_LIBRARY * users.size() * users.size());
    System.out.println("Similarity score: " + similarity);
    if (similarity < Constants.SIMILARITY_MINIMUM) {
      DatabaseHelper data = new DatabaseHelper();
      List<Track> songs = data.openPlaylist("data/top2019").getTracks();
      List<Integer> songIndices = getPermutation(0, songs.size());
      for (int i = 0; i < users.size() * Constants.NUM_SIMILARITY_SONGS_PER_PERSON; i++) {
        Track song = songs.get(songIndices.get(i));
        song.cleanNames();
        song.setWeight(1.0);
        if (!pq.contains(song)) {
          pq.add(song);
        } else {
          pq.remove(song);
          song.cleanNames();
          song.setWeight(1.0);
          pq.add(song);
        }
      }
    }
    // PRINT PRIORITY QUEUE
    PriorityQueue<Track> pq2 = new PriorityQueue<Track>();
    Object[] arr = pq.toArray();
    System.out.println("ARRAY SIZE:" + arr.length);
    for (int i = 0; i < arr.length; i++) {
      pq2.add((Track) arr[i]);
    }
    System.out
        .println("SIZE OF SECOND PQ:" + pq2.size() + " (size of original is " + pq.size() + ".");
    while (!pq2.isEmpty()) {
      Track song = pq2.poll();
      System.out.println(song.getName() + ": weight=" + song.getWeight() + "; occurences="
          + dictSong.get(song) + "; popularity=" + song.getPopularity());
    }
    System.out.println("Priority queue is finished printing.");
    // 3. Get random distributions from the PQ
    ArrayList<Playlist> out = this.pqToRandomPlaylistAll(pq,
        (numSongsPerPlayer * users.size()) - balanceSongs.size());
    for (Playlist list : out) {
      for (Track bsong : balanceSongs) {
        list.addTrack(bsong);
        list.shufflePlaylist();
      }
    }
    return out;
  }

  /**
   * Returns a random playlist based on a priority queue of weighted tracks.
   *
   * @param pq             Priority queue of tracks, given weights based on
   *                       popularity and existence in user libraries
   * @param sizeOfPlaylist Min size of playlist needed for gameplay
   * @param dif            Level of difficulty indicated
   * @return The specific playlist to be used in a game
   */
  public Playlist pqToRandomPlaylist(PriorityQueue<Track> pq, Integer sizeOfPlaylist, String dif) {
    if (pq.size() < sizeOfPlaylist) {
      return null;
    }
    // Get tier indices and sizes from the priority queue; create permutations of
    // indices from each tier
    Integer top = pq.size();
    Integer topMiddle = (int) (top * Constants.TOP_OF_MIDDLE_TIER_REGION);
    Integer middleBottom = (int) (top * Constants.BOTTOM_OF_MIDDLE_TIER_REGION);
    ArrayList<Integer> tierSizes = new ArrayList<Integer>();
    tierSizes.add(top - topMiddle);
    tierSizes.add(topMiddle - middleBottom);
    tierSizes.add(middleBottom);
    // Calculate the number of songs being requested from the priority queue in each
    // tier
    ArrayList<Integer> tierReqs = this.getTierReqs(sizeOfPlaylist, dif);
    // Adjust requests to be smaller than size of tiers
    this.balanceTiers(tierSizes, tierReqs);
    // Get random positions to pop to pop from tier requests
    ArrayList<Integer> positions = this.getPositionsToPop(tierReqs, top, topMiddle, middleBottom);
    // Pop everything in the priority queue and save the song at each relevant
    // index to a playlist. Return
    Playlist playlist = new Playlist();
    Integer count = 0;
    Integer indexToPoll = 0;
    while ((!pq.isEmpty()) && playlist.getSize() < sizeOfPlaylist) {
      Track curr = pq.poll();
      curr.cleanNames();
      if (count.equals(positions.get(indexToPoll))) {
        playlist.addTrack(curr);
        indexToPoll++;
      }
      count++;
    }
    return playlist;
  }

  /**
   * Returns a list of 3 random playlists based on a priority queue of weighted
   * tracks.
   *
   * @param pq             the priority queue from which the playlist's tracks are
   *                       selected.
   * @param sizeOfPlaylist the size of the output playlists.
   * @return a list of playlists
   */
  public ArrayList<Playlist> pqToRandomPlaylistAll(PriorityQueue<Track> pq,
      Integer sizeOfPlaylist) {
    if (pq.size() < sizeOfPlaylist) {
      return null;
    }
    // Get tier indices and sizes from the priority queue; create permutations of
    // indices from each tier
    Integer top = pq.size();
    Integer topMiddle = (int) (top * Constants.TOP_OF_MIDDLE_TIER_REGION);
    Integer middleBottom = (int) (top * Constants.BOTTOM_OF_MIDDLE_TIER_REGION);
    ArrayList<Integer> tierSizes = new ArrayList<Integer>();
    tierSizes.add(top - topMiddle);
    tierSizes.add(topMiddle - middleBottom);
    tierSizes.add(middleBottom);
    // Calculate the number of songs being requested from the priority queue in each
    // tier
    ArrayList<Integer> tierReqsEasy = this.getTierReqs(sizeOfPlaylist, "easy");
    ArrayList<Integer> tierReqsMed = this.getTierReqs(sizeOfPlaylist, "medium");
    ArrayList<Integer> tierReqsHard = this.getTierReqs(sizeOfPlaylist, "hard");
    // Adjust requests to be smaller than size of tiers
    this.balanceTiers(tierSizes, tierReqsEasy);
    this.balanceTiers(tierSizes, tierReqsMed);
    this.balanceTiers(tierSizes, tierReqsHard);
    // Get random positions to pop to pop from tier requests
    ArrayList<Integer> positionsEasy = this.getPositionsToPop(tierReqsEasy, top, topMiddle,
        middleBottom);
    ArrayList<Integer> positionsMed = this.getPositionsToPop(tierReqsMed, top, topMiddle,
        middleBottom);
    ArrayList<Integer> positionsHard = this.getPositionsToPop(tierReqsHard, top, topMiddle,
        middleBottom);
    // Pop everything in the priority queue and save the song at each relevant
    // index to a playlist. Return
    Playlist playlistEasy = new Playlist();
    Playlist playlistMed = new Playlist();
    Playlist playlistHard = new Playlist();
    Integer indexToPollEasy = 0;
    Integer indexToPollMed = 0;
    Integer indexToPollHard = 0;
    Integer count = 0;
    // Pop songs from the priority queue and add them to relevant playlist if
    // they are at a position that's specified in the positions arrays.
    while ((!pq.isEmpty()) && (playlistEasy.getSize() < sizeOfPlaylist
        || playlistMed.getSize() < sizeOfPlaylist || playlistHard.getSize() < sizeOfPlaylist)) {
      Track curr = pq.poll();
      curr.cleanNames();
      if (indexToPollEasy < positionsEasy.size()
          && count.equals(positionsEasy.get(indexToPollEasy))) {
        playlistEasy.addTrack(curr);
        indexToPollEasy++;
      }
      if (indexToPollMed < positionsMed.size() && count.equals(positionsMed.get(indexToPollMed))) {
        playlistMed.addTrack(curr);
        indexToPollMed++;
      }
      if (indexToPollHard < positionsHard.size()
          && count.equals(positionsHard.get(indexToPollHard))) {
        playlistHard.addTrack(curr);
        indexToPollHard++;
      }
      count++;
    }
    ArrayList<Playlist> out = new ArrayList<Playlist>();
    out.add(playlistEasy);
    out.add(playlistMed);
    out.add(playlistHard);
    return out;
  }

  /**
   * Returns the numbers of songs that need to come from the top third, middle
   * third, and bottom third of a playlist given its size and difficulty.
   *
   * @param sizeOfPlaylist size of the playlist.
   * @param dif            difficulty level for the playlist.
   * @return an array of integers that represent the number of songs to be taken
   *         from each tier Determines sizes of tiers for each level of difficulty
   */
  public ArrayList<Integer> getTierReqs(Integer sizeOfPlaylist, String dif) {
    ArrayList<Integer> tierReqs = new ArrayList<Integer>();
    Integer numTopTier = 0;
    Integer numMiddleTier = 0;
    Integer numBottomTier = 0;
    if (dif.equals("easy")) {
      numTopTier = (int) (sizeOfPlaylist * Constants.EASY_TOP);
      numMiddleTier = (int) (sizeOfPlaylist * Constants.EASY_MID);
      numBottomTier = (int) (sizeOfPlaylist * Constants.EASY_BOT);
      Integer remaining = sizeOfPlaylist - (numTopTier + numMiddleTier + numBottomTier);
      numTopTier += remaining;
    }
    if (dif.equals("medium")) {
      numTopTier = (int) (sizeOfPlaylist * Constants.MED_TOP);
      numMiddleTier = (int) (sizeOfPlaylist * Constants.MED_MID);
      numBottomTier = (int) (sizeOfPlaylist * Constants.MED_BOT);
      Integer remaining = sizeOfPlaylist - (numTopTier + numMiddleTier + numBottomTier);
      numMiddleTier += remaining;
    }
    if (dif.equals("hard")) {
      numTopTier = (int) (sizeOfPlaylist * Constants.HARD_TOP);
      numMiddleTier = (int) (sizeOfPlaylist * Constants.HARD_MID);
      numBottomTier = (int) (sizeOfPlaylist * Constants.HARD_BOT);
      Integer remaining = sizeOfPlaylist - (numTopTier + numMiddleTier + numBottomTier);
      numBottomTier += remaining;
    }
    tierReqs.add(numTopTier);
    tierReqs.add(numMiddleTier);
    tierReqs.add(numBottomTier);
    return tierReqs;
  }

  /**
   * Returns a list of positions (or indices) in the priority queue that have been
   * selected to form the playlist.
   *
   * @param tierReqs     an array containing the number of songs required from
   *                     each of the three tiers
   * @param top          the number that bounds the top of the priority queue and
   *                     the top tier.
   * @param topMiddle    the number that bounds the bottom two third of the
   *                     priotiy queue and the middle tier.
   * @param middleBottom the number that bounds the bottom third of the priotity
   *                     queue and the bottom tier.
   * @return a list of indices/positions.
   */
  public ArrayList<Integer> getPositionsToPop(ArrayList<Integer> tierReqs, Integer top,
      Integer topMiddle, Integer middleBottom) {
    LinkedList<Integer> topTierPermutation = getPermutation(topMiddle, top);
    LinkedList<Integer> middleTierPermutation = getPermutation(middleBottom, topMiddle);
    LinkedList<Integer> bottomTierPermutation = getPermutation(0, middleBottom);
    ArrayList<Integer> positions = new ArrayList<Integer>();
    for (int i = 0; i < tierReqs.get(0); i++) {
      positions.add(topTierPermutation.poll());
    }
    for (int i = 0; i < tierReqs.get(1); i++) {
      positions.add(middleTierPermutation.poll());
    }
    for (int i = 0; i < tierReqs.get(2); i++) {
      positions.add(bottomTierPermutation.poll());
    }
    Collections.sort(positions);
    return positions;
  }

  /**
   * Ensures that the number of songs being selected from each tier are not
   * greater than the size of the tiers themselves.
   *
   * @param greater an array that represents the upper boundary of tier sizes.
   * @param smaller an array that represents the number of songs being requested
   *                from each tier.
   * @return a modified version of the smaller array, so that it is bounded by the
   *         greater but so that the sum of all elements is the same, if possible.
   */
  public Boolean balanceTiers(ArrayList<Integer> greater, ArrayList<Integer> smaller) {
    if (greater.size() != smaller.size()) {
      return false;
    }
    Integer counter = 0;
    // Iterate through arrays and count excess in smaller array.
    for (int i = 0; i < greater.size(); i++) {
      if (greater.get(i) < smaller.get(i)) {
        Integer dif = smaller.get(i) - greater.get(i);
        smaller.set(i, smaller.get(i) - dif);
        counter += dif;
      }
    }
    for (int i = 0; i < greater.size(); i++) {
      if (counter == 0) {
        return true;
      }
      if (greater.get(i) > smaller.get(i)) {
        Integer dif = greater.get(i) - smaller.get(i);
        Integer add = (dif > counter) ? counter : dif;
        counter -= add;
        smaller.set(i, smaller.get(i) + add);
      }
    }
    if (counter == 0) {
      return true;
    }
    return false;
  }

  /**
   * Randomly orders numbers from start (inclusive) to end (exclusive) and places
   * them in an array.
   *
   * @param start the first number to be included in the array.
   * @param end   the number to bound the array.
   * @return an array of randomly ordered numbers.
   */
  public LinkedList<Integer> getPermutation(Integer start, Integer end) {
    Random rd = new Random();
    ArrayList<Integer> nperm = new ArrayList<Integer>();
    for (int i = start; i < end; i++) {
      nperm.add(i);
    }
    LinkedList<Integer> perm = new LinkedList<Integer>();
    while (!nperm.isEmpty()) {
      int nex = rd.nextInt(nperm.size());
      perm.add(nperm.remove(nex));
    }
    return perm;
  }

  /**
   * Returns the APIHelper object
   *
   * @return the APIHelper object
   */
  public APIHelper getAPIHelper() {
    return this.api;
  }

  /**
   * Returns the database helper object.
   *
   * @return the database helper object
   */
  public DatabaseHelper getDatabaseHelper() {
    return this.database;
  }
}

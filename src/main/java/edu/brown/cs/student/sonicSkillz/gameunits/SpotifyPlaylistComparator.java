package edu.brown.cs.student.sonicSkillz.gameunits;

import java.util.Comparator;

public class SpotifyPlaylistComparator implements Comparator<SpotifyPlaylist>
{

  @Override
  public int compare(SpotifyPlaylist o1, SpotifyPlaylist o2) {
    // TODO Auto-generated method stub
    return o1.getName().compareTo(o2.getName());
  }

}

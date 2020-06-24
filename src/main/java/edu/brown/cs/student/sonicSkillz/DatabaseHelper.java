package edu.brown.cs.student.sonicSkillz;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import edu.brown.cs.student.sonicSkillz.gameunits.Playlist;

/**
 * DatabaseHelper Class! This class contains methods for reading and writing
 * .sonic files, which contain playlists.
 *
 * @author noell
 *
 */
public class DatabaseHelper {

  /**
   * Saves a playlist to a .sonic file.
   *
   * @param songs the playlist to be saved.
   * @param title the intended title of the .sonic file
   */
  public void savePlaylist(Playlist songs, String title) {
    try {
      FileOutputStream fout;
      fout = new FileOutputStream(title + ".sonic");
      ObjectOutputStream objout = new ObjectOutputStream(fout);
      objout.writeObject(songs);
      objout.close();
      fout.close();
    } catch (FileNotFoundException e) {
    } catch (IOException e) {
    }
  }

  /**
   * Opens a playlist that has been saved as a .sonic file.
   *
   * @param title the title of the the .sonic file to be read.
   * @return the playlist in the .sonic file.
   */
  public Playlist openPlaylist(String title) {
    Playlist songs = null;
    try {
      FileInputStream fin = new FileInputStream(title + ".sonic");
      ObjectInputStream objin = new ObjectInputStream(fin);
      songs = (Playlist) objin.readObject();
      objin.close();
      fin.close();
    } catch (ClassNotFoundException e) {
      return null;
    } catch (IOException e) {
      e.printStackTrace();
      return null;
    }
    return songs;
  }
}

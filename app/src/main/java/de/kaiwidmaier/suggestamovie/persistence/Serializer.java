package de.kaiwidmaier.suggestamovie.persistence;

import android.content.Context;
import android.util.Log;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import de.kaiwidmaier.suggestamovie.activities.MainActivity;
import de.kaiwidmaier.suggestamovie.data.DataHelper;
import de.kaiwidmaier.suggestamovie.data.Movie;

/**
 * Created by Kai on 12.03.2018.
 */

public class Serializer {

  private Context context;
  private static final String TAG = Serializer.class.getSimpleName();
  private String fileName = "watchlist.ser";

  public Serializer(Context context){
    this.context = context;
  }

  public void writeWatchlist(ArrayList<Movie> watchlist){
    try {
      FileOutputStream fos = context.openFileOutput(fileName, Context.MODE_PRIVATE);
      ObjectOutputStream oos = new ObjectOutputStream(fos);
      oos.writeObject(watchlist);
      oos.close();
      fos.close();
    }
    catch (IOException e){
      e.printStackTrace();
    }
  }

  public ArrayList<Movie> readWatchlist() {
    try {
      FileInputStream fis = context.openFileInput(fileName);
      ObjectInputStream ois = new ObjectInputStream(fis);
      ArrayList<Movie> watchlist = (ArrayList<Movie>) ois.readObject();
      ois.close();
      fis.close();
      return watchlist;
    }
    catch(IOException e){
      e.printStackTrace();
      return new ArrayList<>();
    }
    catch(ClassNotFoundException e){
      e.printStackTrace();
      return new ArrayList<>();
    }
  }


}

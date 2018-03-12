package de.kaiwidmaier.suggestamovie.data;

import android.app.Application;

import java.util.ArrayList;

import de.kaiwidmaier.suggestamovie.persistence.Serializer;

/**
 * Created by Kai on 12.03.2018.
 */

public class DataHelper extends Application{

  //Holds watchlist to make it accessible from every activity
  private ArrayList<Movie> watchlist = new ArrayList<>();

  public void setWatchlist(ArrayList<Movie> watchlist){
    this.watchlist = watchlist;
  }

  @Override
  public void onCreate() {
    super.onCreate();

    Serializer serializer = new Serializer(this);
    watchlist = serializer.readWatchlist();
  }
}

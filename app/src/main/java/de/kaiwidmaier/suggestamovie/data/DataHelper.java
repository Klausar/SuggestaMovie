package de.kaiwidmaier.suggestamovie.data;

import android.app.Application;

import java.util.ArrayList;

import de.kaiwidmaier.suggestamovie.BuildConfig;
import de.kaiwidmaier.suggestamovie.persistence.Serializer;

/**
 * Created by Kai on 12.03.2018.
 */

public class DataHelper extends Application{

  //TheMovieDB API Key
  public final static String API_KEY = BuildConfig.API_KEY;
  //Holds watchlist to make it accessible from every activity
  private ArrayList<Movie> watchlist = new ArrayList<>();

  public void setWatchlist(ArrayList<Movie> watchlist){
    this.watchlist = watchlist;
  }

  public ArrayList<Movie> getWatchlist(){
    return watchlist;
  }

  @Override
  public void onCreate() {
    super.onCreate();

    Serializer serializer = new Serializer(this);
    watchlist = serializer.readWatchlist();
  }
}

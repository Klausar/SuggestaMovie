package de.kaiwidmaier.suggestamovie.data;

import android.app.Application;

import java.util.ArrayList;

import de.kaiwidmaier.suggestamovie.BuildConfig;
import de.kaiwidmaier.suggestamovie.R;
import de.kaiwidmaier.suggestamovie.persistence.Serializer;

/**
 * Created by Kai on 12.03.2018.
 */

public class DataHelper extends Application{

  //TheMovieDB API Key
  public final static String API_KEY = BuildConfig.API_KEY;
  //Holds watchlist to make it accessible from every activity
  private ArrayList<Movie> watchlist = new ArrayList<>();
  private ArrayList<Genre> genres = new ArrayList<>();

  public void setWatchlist(ArrayList<Movie> watchlist){
    this.watchlist = watchlist;
  }

  public ArrayList<Movie> getWatchlist(){
    return watchlist;
  }

  public ArrayList<Genre> getGenres(){
    return genres;
  }

  public Genre getGenre(int id){
    Genre genre = null;
    for(Genre g : genres){
      if(g.getId() == id){
        genre = g;
      }
    }
    return genre;
  }

  private void setupGenres(){
    genres.add(new Genre(getString(R.string.genre_action), 28));
    genres.add(new Genre(getString(R.string.genre_adventure), 12));
    genres.add(new Genre(getString(R.string.genre_animation), 16));
    genres.add(new Genre(getString(R.string.genre_comedy), 35));
    genres.add(new Genre(getString(R.string.genre_crime), 80));
    genres.add(new Genre(getString(R.string.genre_documentary), 99));
    genres.add(new Genre(getString(R.string.genre_drama), 18));
    genres.add(new Genre(getString(R.string.genre_family), 10751));
    genres.add(new Genre(getString(R.string.genre_fantasy), 14));
    genres.add(new Genre(getString(R.string.genre_history), 36));
    genres.add(new Genre(getString(R.string.genre_horror), 27));
    genres.add(new Genre(getString(R.string.genre_music), 10402));
    genres.add(new Genre(getString(R.string.genre_mystery), 9648));
    genres.add(new Genre(getString(R.string.genre_romance), 10749));
    genres.add(new Genre(getString(R.string.genre_scifi), 878));
    genres.add(new Genre(getString(R.string.genre_thriller), 53));
    genres.add(new Genre(getString(R.string.genre_war), 10752));
    genres.add(new Genre(getString(R.string.genre_western), 37));
  }

  @Override
  public void onCreate() {
    super.onCreate();

    Serializer serializer = new Serializer(this);
    watchlist = serializer.readWatchlist();
    setupGenres();
  }
}

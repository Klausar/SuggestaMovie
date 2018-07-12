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
  //Youtube API Key
  public final static String API_KEY_YOUTUBE = BuildConfig.API_KEY_YOUTUBE;
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
    genres.add(new Genre(getString(R.string.genre_action), 28, R.drawable.ic_genre_action));
    genres.add(new Genre(getString(R.string.genre_adventure), 12, R.drawable.ic_genre_adventure));
    genres.add(new Genre(getString(R.string.genre_animation), 16, R.drawable.ic_genre_animation));
    genres.add(new Genre(getString(R.string.genre_comedy), 35, R.drawable.ic_genre_comedy));
    genres.add(new Genre(getString(R.string.genre_crime), 80, R.drawable.ic_genre_crime));
    genres.add(new Genre(getString(R.string.genre_documentary), 99, R.drawable.ic_genre_documentary));
    genres.add(new Genre(getString(R.string.genre_drama), 18, R.drawable.ic_genre_drama));
    genres.add(new Genre(getString(R.string.genre_family), 10751, R.drawable.ic_genre_family));
    genres.add(new Genre(getString(R.string.genre_fantasy), 14, R.drawable.ic_genre_fantasy));
    genres.add(new Genre(getString(R.string.genre_history), 36, R.drawable.ic_genre_history));
    genres.add(new Genre(getString(R.string.genre_horror), 27, R.drawable.ic_genre_horror));
    genres.add(new Genre(getString(R.string.genre_music), 10402, R.drawable.ic_genre_music));
    genres.add(new Genre(getString(R.string.genre_mystery), 9648, R.drawable.ic_genre_mystery));
    genres.add(new Genre(getString(R.string.genre_romance), 10749, R.drawable.ic_genre_romance));
    genres.add(new Genre(getString(R.string.genre_scifi), 878, R.drawable.ic_genre_scifi));
    genres.add(new Genre(getString(R.string.genre_thriller), 53, R.drawable.ic_genre_thriller));
    genres.add(new Genre(getString(R.string.genre_war), 10752, R.drawable.ic_genre_war));
    genres.add(new Genre(getString(R.string.genre_western), 37, R.drawable.ic_genre_western));
  }

  @Override
  public void onCreate() {
    super.onCreate();

    Serializer serializer = new Serializer(this);
    watchlist = serializer.readWatchlist();
    setupGenres();
  }
}

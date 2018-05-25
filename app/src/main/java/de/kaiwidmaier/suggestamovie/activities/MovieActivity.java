package de.kaiwidmaier.suggestamovie.activities;

import android.content.Intent;
import android.os.Parcelable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.like.LikeButton;
import com.like.OnLikeListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.kaiwidmaier.suggestamovie.R;
import de.kaiwidmaier.suggestamovie.adapters.RecyclerGenreChipAdapter;
import de.kaiwidmaier.suggestamovie.adapters.RecyclerMovieAdapter;
import de.kaiwidmaier.suggestamovie.data.DataHelper;
import de.kaiwidmaier.suggestamovie.data.Genre;
import de.kaiwidmaier.suggestamovie.data.Movie;
import de.kaiwidmaier.suggestamovie.data.MovieResponse;
import de.kaiwidmaier.suggestamovie.persistence.Serializer;
import de.kaiwidmaier.suggestamovie.rest.MovieApiService;
import de.kaiwidmaier.suggestamovie.rest.ResultType;
import de.kaiwidmaier.suggestamovie.utils.LocalizationUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static de.kaiwidmaier.suggestamovie.activities.MainActivity.BASE_URL;
import static de.kaiwidmaier.suggestamovie.data.DataHelper.API_KEY;

public class MovieActivity extends AppCompatActivity {

  private Movie movie;
  private TextView textTitle;
  private TextView textDescription;
  private TextView textRating;
  private TextView textRelease;
  private ImageView imgPoster;
  private LikeButton btnFavorite;
  private ArrayList<Movie> watchlist;
  Serializer serializer = new Serializer(MovieActivity.this);
  public static final String TAG = MovieActivity.class.getSimpleName();

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_movie);

    getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    Intent intent = getIntent();
    movie = intent.getParcelableExtra("movie");
    ArrayList<Genre> movieGenres = new ArrayList<>();

    if(movie.getGenreIds() != null){
      for(Integer i : movie.getGenreIds()){
        movieGenres.add(((DataHelper) this.getApplication()).getGenre(i));
      }
    }

    textTitle = findViewById(R.id.text_movie_title);
    textDescription = findViewById(R.id.text_movie_description);
    textRating = findViewById(R.id.text_movie_rating);
    textRelease = findViewById(R.id.text_movie_release);
    imgPoster = findViewById(R.id.img_thumbnail_movie);
    btnFavorite = findViewById(R.id.btn_favorite);
    RecyclerView recyclerGenreChips = findViewById(R.id.recycler_genre_chips);
    recyclerGenreChips.setAdapter(new RecyclerGenreChipAdapter(this, movieGenres));
    watchlist = ((DataHelper) this.getApplicationContext()).getWatchlist();
    Button btnFindSimilar = findViewById(R.id.btn_find_similar);
    btnFindSimilar.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        Intent similarIntent = new Intent(MovieActivity.this, ResultActivity.class);
        similarIntent.putExtra("movieId", movie.getId());
        similarIntent.putExtra("resultTitle", getString(R.string.similar));
        similarIntent.putExtra("resultDescr", String.format(getString(R.string.similar_descr), movie.getTitle()));
        similarIntent.putExtra("resultType", ResultType.SIMILAR);
        startActivity(similarIntent);
      }
    });

    //Refresh serialized movies in watchlist
    if(watchlist.contains(movie)){
      refreshMovieData();
    }

    //Fill Activity views with data
    fillData();
  }

  private void fillData(){
    textTitle.setText(movie.getTitle());
    textDescription.setText(movie.getOverview());
    textRating.setText(String.format(getString(R.string.rating_format), movie.getVoteAverage()));
    if(movie.getReleaseDate().length() >= 4){
      textRelease.setText(String.format(getString(R.string.release_format), movie.getReleaseDate().substring(0, 4)));
    }
    else{
      textRelease.setText(String.format(getString(R.string.release_format), "?"));
    }
    String imgUrlBasePath = "http://image.tmdb.org/t/p/w342//";
    Picasso.with(this).load(imgUrlBasePath + movie.getPosterPath()).fit().centerCrop().placeholder(R.drawable.placeholder_thumbnail).error(R.drawable.placeholder_thumbnail).into(imgPoster);

    if(watchlist.contains(movie)){
      btnFavorite.setLiked(true);
    }
    else{
      btnFavorite.setLiked(false);
    }
    btnFavorite.setOnLikeListener(new OnLikeListener() {
      @Override
      public void liked(LikeButton likeButton) {
        watchlist.add(movie);
        serializer.writeWatchlist(watchlist);
      }

      @Override
      public void unLiked(LikeButton likeButton) {
        watchlist.remove(movie);
        serializer.writeWatchlist(watchlist);
      }
    });
  }

  //If movie data changes, replace movie object
  private void refreshMovieData(){
    Retrofit retrofit = new Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
    MovieApiService movieApiService = retrofit.create(MovieApiService.class);
    Call<Movie> call = movieApiService.getMovieDetails(movie.getId(), API_KEY, LocalizationUtils.getLanguage(), LocalizationUtils.getCountry());

    assert call != null;
    call.enqueue(new Callback<Movie>() {
      @Override
      public void onResponse(Call<Movie> call, Response<Movie> response) {
        Movie newMovie = response.body();
        newMovie.setGenreIds(movie.getGenreIds());

        if(movie == null){
          Log.d(TAG, "Movie not found");
          return;
        }

        //Replace movie in watchlist with new movie
        for(int i = 0; i < watchlist.size(); i++){
          if(watchlist.get(i).equals(movie)){
            watchlist.set(i, newMovie);
            Log.d(TAG, "Movie replaced");
            serializer.writeWatchlist(watchlist);
          }
        }

        Log.d(TAG, "Request URL: " + response.raw().request().url());
        Log.d(TAG, "For Movie: " + movie.getTitle());
        movie = newMovie;
        fillData();
      }

      @Override
      public void onFailure(Call<Movie> call, Throwable throwable) {
        //Do nothing
      }
    });
  }

  public boolean onOptionsItemSelected(MenuItem item){
    onBackPressed();
    return true;
  }

}

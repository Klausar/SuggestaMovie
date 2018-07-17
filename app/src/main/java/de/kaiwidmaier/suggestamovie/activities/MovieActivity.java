package de.kaiwidmaier.suggestamovie.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;
import com.google.android.youtube.player.YouTubePlayerView;
import com.like.LikeButton;
import com.like.OnLikeListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Objects;

import de.kaiwidmaier.suggestamovie.R;
import de.kaiwidmaier.suggestamovie.adapters.RecyclerActorAdapter;
import de.kaiwidmaier.suggestamovie.adapters.RecyclerGenreChipAdapter;
import de.kaiwidmaier.suggestamovie.data.DataHelper;
import de.kaiwidmaier.suggestamovie.data.Movie;
import de.kaiwidmaier.suggestamovie.data.MovieDetail;
import de.kaiwidmaier.suggestamovie.data.Video;
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
import static de.kaiwidmaier.suggestamovie.data.DataHelper.API_KEY_YOUTUBE;

public class MovieActivity extends BaseMenuActivity {

  private Movie movie;

  /*
   *  This information is already contained in the "movie" object
   *  and doesn't require an instance of "MovieDetail"
   */
  private TextView textTitle;
  private TextView textDescription;
  private TextView textRating;
  private TextView textRelease;
  private TextView textVideos;
  private TextView textCast;
  private ImageView imgPoster;

  private boolean dataLoaded;

  private LinearLayout layoutMovie;
  private ProgressBar progress;

  private boolean activityCreated;

  /*
   *  This information is loaded by "loadMovieDetails"
   */
  private TextView textBudget;
  private TextView textRevenue;
  private TextView textRuntime;

  private YouTubePlayerSupportFragment frag;

  private LikeButton btnFavorite;
  private ArrayList<Movie> watchlist;
  private RecyclerView recyclerGenreChips;
  private Serializer serializer = new Serializer(MovieActivity.this);
  public static final String TAG = MovieActivity.class.getSimpleName();

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_movie);

    Intent intent = getIntent();
    movie = intent.getParcelableExtra("movie");

    textTitle = findViewById(R.id.text_movie_title);
    textDescription = findViewById(R.id.text_movie_description);
    textRating = findViewById(R.id.text_movie_rating);
    textRelease = findViewById(R.id.text_movie_release);
    textVideos = findViewById(R.id.text_videos);
    textCast = findViewById(R.id.text_cast);

    layoutMovie = findViewById(R.id.layout_movie);
    progress = findViewById(R.id.progress);
    progress.setVisibility(View.GONE);

    textBudget = findViewById(R.id.text_budget);
    textRevenue = findViewById(R.id.text_revenue);
    textRuntime = findViewById(R.id.text_runtime);

    frag = (YouTubePlayerSupportFragment) getSupportFragmentManager().findFragmentById(R.id.youtube_fragment);

    imgPoster = findViewById(R.id.img_thumbnail_movie);
    btnFavorite = findViewById(R.id.btn_favorite);
    recyclerGenreChips = findViewById(R.id.recycler_genre_chips);
    watchlist = ((DataHelper) this.getApplicationContext()).getWatchlist();
    Button btnFindSimilar = findViewById(R.id.btn_find_similar);
    btnFindSimilar.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        Intent similarIntent = new Intent(MovieActivity.this, ResultActivity.class);
        similarIntent.putExtra("movieId", movie.getId());
        similarIntent.putExtra("resultTitle", getString(R.string.similar));
        similarIntent.putExtra("resultDescr", String.format(getString(R.string.similar_descr), movie.getTitle(MovieActivity.this)));
        similarIntent.putExtra("resultType", ResultType.SIMILAR);
        startActivity(similarIntent);
      }
    });

    //Refresh serialized movie data (Object instance of "Movie") in watchlist in case something changed
    if (watchlist.contains(movie)) {
      refreshMovieData();
    }

    //Load more information by creating object of instance "MoveDetail" (Different API Call than Movie)
    loadMovieDetails();

    //Fill views with data that's already contained in "movie" object
    fillData();
  }

  private void fillData() {
    textTitle.setText(movie.getTitle(this));
    textDescription.setText(movie.getOverview());
    textRating.setText(String.format(getString(R.string.rating_format), movie.getVoteAverage()));
    if (movie.getReleaseDate().length() >= 4) {
      textRelease.setText(String.format(getString(R.string.release_format), LocalizationUtils.getLocalDateFormat(movie.getReleaseDate(), this)));
    } else {
      textRelease.setText(String.format(getString(R.string.release_format), "?"));
    }
    String imgUrlBasePath = "http://image.tmdb.org/t/p/w342//";
    Picasso.with(this).load(imgUrlBasePath + movie.getPosterPath()).fit().centerCrop().placeholder(R.drawable.placeholder_thumbnail).error(R.drawable.placeholder_thumbnail).into(imgPoster);

    btnFavorite.setLiked(watchlist.contains(movie));
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
  private void loadMovieDetails() {
    progress.setVisibility(View.VISIBLE);
    Retrofit retrofit = new Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
    MovieApiService movieApiService = retrofit.create(MovieApiService.class);
    Call<MovieDetail> call = movieApiService.getMovieDetails(movie.getId(), API_KEY, LocalizationUtils.getLanguage(), LocalizationUtils.getCountry());

    assert call != null;
    call.enqueue(new Callback<MovieDetail>() {
      @Override
      public void onResponse(Call<MovieDetail> call, Response<MovieDetail> response) {
        final MovieDetail movieDetail = response.body();
        if (movieDetail.getGenres() != null) {
          recyclerGenreChips.setAdapter(new RecyclerGenreChipAdapter(MovieActivity.this, movieDetail.getGenres()));
        }

        if (movieDetail.getBudget() != 0) {
          textBudget.setText(String.format(getString(R.string.budget), movieDetail.getBudgetFormatted()));
        }
        else{
          textBudget.setText(getString(R.string.no_budget));
        }
        if (movieDetail.getRevenue() != 0) {
          textRevenue.setText(String.format(getString(R.string.revenue), movieDetail.getRevenueFormatted()));
        }
        else{
          textRevenue.setText(getString(R.string.no_revenue));
        }
        if (movieDetail.getRuntime() != 0) {
          textRuntime.setText(String.format(getString(R.string.runtime), movieDetail.getRuntime()));
        }
        else{
          textRuntime.setText(getString(R.string.no_runtime));
        }

        if(movieDetail.getVideos() != null && !movieDetail.getVideos().isEmpty()){
          Log.d(TAG, "First video: " + movieDetail.getVideos().get(0).getName());
          loadYoutubeVideo(movieDetail.getVideos().get(0).getKey());
          textVideos.setText(movieDetail.getVideos().get(0).getType());
        }
        else{
          Objects.requireNonNull(frag.getView()).setVisibility(View.GONE);
          textVideos.setVisibility(View.GONE);
        }

        RecyclerView recyclerActors = findViewById(R.id.recycler_actors);
        LinearLayoutManager layoutManager = new LinearLayoutManager(MovieActivity.this, LinearLayoutManager.HORIZONTAL, false);
        recyclerActors.setLayoutManager(layoutManager);
        if(movieDetail.getCast() != null && !movieDetail.getCast().isEmpty()){
          recyclerActors.setAdapter(new RecyclerActorAdapter(MovieActivity.this, movieDetail.getCast()));
        }
        else {
          recyclerActors.setVisibility(View.GONE);
          textCast.setVisibility(View.GONE);
        }

        progress.setVisibility(View.GONE);
        layoutMovie.setVisibility(View.VISIBLE);
        dataLoaded = true;
        Log.d(TAG, "Request URL: " + response.raw().request().url());
        Log.d(TAG, "For Movie: " + movie.getTitle(MovieActivity.this));
      }

      @Override
      public void onFailure(Call<MovieDetail> call, Throwable throwable) {
        progress.setVisibility(View.GONE);
        layoutMovie.setVisibility(View.VISIBLE);
        dataLoaded = true;
        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), getString(R.string.unable_connect_movie_detail), Snackbar.LENGTH_INDEFINITE)
          .setAction(getString(R.string.retry), new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              layoutMovie.setVisibility(View.GONE);
              loadMovieDetails();
            }
          });
        snackbar.show();
      }
    });
  }

  private void refreshMovieData() {
    Retrofit retrofit = new Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
    MovieApiService movieApiService = retrofit.create(MovieApiService.class);
    Call<Movie> call = movieApiService.getMovieDetailsAsMovie(movie.getId(), API_KEY, LocalizationUtils.getLanguage(), LocalizationUtils.getCountry());

    assert call != null;
    call.enqueue(new Callback<Movie>() {
      @Override
      public void onResponse(Call<Movie> call, Response<Movie> response) {
        Movie newMovie = response.body();
        newMovie.setGenreIds(movie.getGenreIds());

        //Replace movie in watchlist with new movie
        watchlist.set(watchlist.indexOf(movie), newMovie);
        Log.d(TAG, "Movie replaced");
        serializer.writeWatchlist(watchlist);

        fillData();

        Log.d(TAG, "Request URL: " + response.raw().request().url());
        Log.d(TAG, "For Movie: " + movie.getTitle(MovieActivity.this));
      }

      @Override
      public void onFailure(Call<Movie> call, Throwable throwable) {
        //Do nothing
      }
    });
  }

  private void loadYoutubeVideo(final String videoKey){
    frag.initialize(API_KEY_YOUTUBE, new YouTubePlayer.OnInitializedListener() {
      @Override
      public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
        if (!b) {
          youTubePlayer.cueVideo(videoKey);
        }
      }

      @Override
      public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
        Snackbar.make(findViewById(android.R.id.content), "There was a problem loading Youtube", Snackbar.LENGTH_SHORT).show();
      }
    });
  }

  @Override
  protected void onResume() {
    super.onResume();
    fillData();
    if(dataLoaded){
      progress.setVisibility(View.GONE); //Temporary bug fix
    }
  }
}

package de.kaiwidmaier.suggestamovie.activities;

import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ScrollView;

import java.util.List;
import java.util.Locale;

import de.kaiwidmaier.suggestamovie.BuildConfig;
import de.kaiwidmaier.suggestamovie.R;
import de.kaiwidmaier.suggestamovie.adapters.RecyclerViewMovieAdapter;
import de.kaiwidmaier.suggestamovie.adapters.RecyclerViewThumbnailAdapter;
import de.kaiwidmaier.suggestamovie.data.Movie;
import de.kaiwidmaier.suggestamovie.data.MovieResponse;
import de.kaiwidmaier.suggestamovie.rest.MovieApiService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static de.kaiwidmaier.suggestamovie.activities.MainActivity.BASE_URL;

public class ResultActivity extends AppCompatActivity {

  private static final String TAG = ResultActivity.class.getSimpleName();
  private static Retrofit retrofit;
  private RecyclerView recyclerResults;
  private ScrollView scrollView;

  //TheMovieDB API Key
  public final static String API_KEY = BuildConfig.API_KEY;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_result);

    recyclerResults = findViewById(R.id.recycler_results);
    DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerResults.getContext(), DividerItemDecoration.VERTICAL);
    recyclerResults.addItemDecoration(dividerItemDecoration);

    connectAndGetApiData();
  }

  public void connectAndGetApiData() {

    Intent intent = getIntent();
    String releaseDateMin = intent.getStringExtra("releaseDateMin");
    String releaseDateMax = intent.getStringExtra("releaseDateMax");
    int ratingMin = intent.getIntExtra("ratingMin", 0);
    int ratingMax =  intent.getIntExtra("ratingMax", 10);
    boolean adult =  intent.getBooleanExtra("adult", false);

    if (retrofit == null) {
      retrofit = new Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
    }

    MovieApiService movieApiService = retrofit.create(MovieApiService.class);

    Call<MovieResponse> call = movieApiService.getMovie(API_KEY, Locale.getDefault().getLanguage(), Locale.getDefault().getCountry(),
      null, adult, releaseDateMin, releaseDateMax, ratingMin, ratingMax, null, null);

    Log.d(TAG, "Current language: " + Locale.getDefault().toString());
    Log.d(TAG, "Current region: " + Locale.getDefault().getCountry());

    call.enqueue(new Callback<MovieResponse>() {
      @Override
      public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {
        List<Movie> movies = response.body().getResults();
        final RecyclerViewMovieAdapter movieAdapter = new RecyclerViewMovieAdapter(ResultActivity.this, movies, true);
        recyclerResults.setAdapter(movieAdapter);

        Log.d(TAG, "Request URL: " + response.raw().request().url());
        Log.d(TAG, "Number of movies received: " + movies.size());

        movieAdapter.setClickListener(new RecyclerViewMovieAdapter.ItemClickListener() {
          @Override
          public void onItemClick(View view, int position) {
            Log.d(TAG, "Clicked on: " + movieAdapter.getItem(position).getTitle());
            Intent movieIntent = new Intent(ResultActivity.this, MovieActivity.class);
            movieIntent.putExtra("movie", (Parcelable) movieAdapter.getItem(position));
            startActivity(movieIntent);
          }
        });
      }

      @Override
      public void onFailure(Call<MovieResponse> call, Throwable throwable) {
        Log.e(TAG, throwable.toString());
      }
    });
  }

  @Override
  public void onBackPressed() {
    finish();
    Intent intent = new Intent(this, MainActivity.class);
    startActivity(intent);
  }
}

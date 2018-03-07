package de.kaiwidmaier.suggestamovie.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

import de.kaiwidmaier.suggestamovie.BuildConfig;
import de.kaiwidmaier.suggestamovie.R;
import de.kaiwidmaier.suggestamovie.adapters.RecyclerViewMovieAdapter;
import de.kaiwidmaier.suggestamovie.data.Movie;
import de.kaiwidmaier.suggestamovie.data.MovieResponse;
import de.kaiwidmaier.suggestamovie.rest.MovieApiService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class MainActivity extends AppCompatActivity {

  private static final String TAG = MainActivity.class.getSimpleName();
  public static final String BASE_URL = "http://api.themoviedb.org/3/";
  private static Retrofit retrofit;
  private RecyclerView recyclerView;
  private Button btnDiscover;

  //TheMovieDB API Key
  private final static String API_KEY = BuildConfig.API_KEY;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    recyclerView = findViewById(R.id.recycler_now_playing);
    btnDiscover = findViewById(R.id.btn_discover);
    btnDiscover.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        Intent discoverIntent = new Intent(MainActivity.this, DiscoverActivity.class);
        startActivity(discoverIntent);
      }
    });
    connectAndGetApiData();
  }

  public void connectAndGetApiData() {

    if (retrofit == null) {
      retrofit = new Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
    }

    MovieApiService movieApiService = retrofit.create(MovieApiService.class);

    Call<MovieResponse> call = movieApiService.getNowPlayingMovies(API_KEY, Locale.getDefault().getLanguage(), Locale.getDefault().getCountry());

    Log.d(TAG, "Current language: " + Locale.getDefault().toString());
    Log.d(TAG, "Current region: " + Locale.getDefault().getCountry());

    call.enqueue(new Callback<MovieResponse>() {
      @Override
      public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {
        List<Movie> movies = response.body().getResults();
        final RecyclerViewMovieAdapter movieAdapter = new RecyclerViewMovieAdapter(MainActivity.this, movies);
        recyclerView.setAdapter(movieAdapter);

        Log.d(TAG, "Request URL: " + response.raw().request().url());
        Log.d(TAG, "Number of movies received: " + movies.size());

        movieAdapter.setClickListener(new RecyclerViewMovieAdapter.ItemClickListener() {
          @Override
          public void onItemClick(View view, int position) {
            Log.d(TAG, "Clicked on: " + movieAdapter.getItem(position).getTitle());
            Intent movieIntent = new Intent(MainActivity.this, MovieActivity.class);
            movieIntent.putExtra("movie", movieAdapter.getItem(position));
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

}

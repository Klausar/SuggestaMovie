package de.kaiwidmaier.suggestamovie.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import de.kaiwidmaier.suggestamovie.BuildConfig;
import de.kaiwidmaier.suggestamovie.R;
import de.kaiwidmaier.suggestamovie.adapters.RecyclerViewMovieAdapter;
import de.kaiwidmaier.suggestamovie.adapters.RecyclerViewThumbnailAdapter;
import de.kaiwidmaier.suggestamovie.data.Movie;
import de.kaiwidmaier.suggestamovie.data.MovieResponse;
import de.kaiwidmaier.suggestamovie.rest.MovieApiService;
import de.kaiwidmaier.suggestamovie.data.DataHelper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class MainActivity extends AppCompatActivity {

  private static final String TAG = MainActivity.class.getSimpleName();
  public static final String BASE_URL = "http://api.themoviedb.org/3/";
  private RecyclerView recyclerWatchlist;
  private FloatingActionButton btnDiscover;
  private RecyclerViewMovieAdapter movieAdapter;
  private ArrayList<Movie> watchlist;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    recyclerWatchlist = findViewById(R.id.recycler_watchlist);

    watchlist = ((DataHelper) this.getApplication()).getWatchlist();

    movieAdapter = new RecyclerViewMovieAdapter(MainActivity.this, watchlist, false);
    movieAdapter.setClickListener(new RecyclerViewMovieAdapter.ItemClickListener() {
      @Override
      public void onItemClick(View view, int position) {
        Log.d(TAG, "Clicked on: " + movieAdapter.getItem(position).getTitle());
        Intent movieIntent = new Intent(MainActivity.this, MovieActivity.class);
        movieIntent.putExtra("movie", (Parcelable) movieAdapter.getItem(position));
        startActivity(movieIntent);
      }
    });

    recyclerWatchlist.setAdapter(movieAdapter);

    btnDiscover = findViewById(R.id.btn_discover);
    btnDiscover.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        Intent discoverIntent = new Intent(MainActivity.this, DiscoverActivity.class);
        startActivity(discoverIntent);
      }
    });
  }


  @Override
  protected void onResume() {
    if (movieAdapter != null) {
      movieAdapter.notifyDataSetChanged();
    }
    super.onResume();
  }

  @Override
  public void onBackPressed() {
    finish();
  }
}

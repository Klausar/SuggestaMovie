package de.kaiwidmaier.suggestamovie.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;

import de.kaiwidmaier.suggestamovie.R;
import de.kaiwidmaier.suggestamovie.adapters.RecyclerViewMovieAdapter;
import de.kaiwidmaier.suggestamovie.adapters.utils.SimpleItemTouchHelperCallback;
import de.kaiwidmaier.suggestamovie.data.Movie;
import de.kaiwidmaier.suggestamovie.data.DataHelper;


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
    DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerWatchlist.getContext(), DividerItemDecoration.VERTICAL);
    recyclerWatchlist.addItemDecoration(dividerItemDecoration);

    watchlist = ((DataHelper) this.getApplication()).getWatchlist();

    movieAdapter = new RecyclerViewMovieAdapter(MainActivity.this, watchlist, false);
    movieAdapter.setClickListener(new RecyclerViewMovieAdapter.ItemClickListener() {
      @Override
      public void onItemClick(View view, int position) {
        Log.d(TAG, "Clicked on: " + movieAdapter.getItem(position).getTitle());
        Intent movieIntent = new Intent(MainActivity.this, MovieActivity.class);
        movieIntent.putExtra("movie", (Parcelable) movieAdapter.getItem(position));
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(MainActivity.this, view, getString(R.string.transition_movie));
        ActivityCompat.startActivity(MainActivity.this, movieIntent, options.toBundle());
      }
    });

    recyclerWatchlist.setAdapter(movieAdapter);

    //Swipe to remove and long press to change position
    ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(movieAdapter);
    ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
    touchHelper.attachToRecyclerView(recyclerWatchlist);

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
}

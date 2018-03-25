package de.kaiwidmaier.suggestamovie.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.widget.LinearLayout;

import com.sergiocasero.revealfab.RevealFAB;

import java.util.ArrayList;

import de.kaiwidmaier.suggestamovie.R;
import de.kaiwidmaier.suggestamovie.adapters.RecyclerViewMovieAdapter;
import de.kaiwidmaier.suggestamovie.adapters.utils.SimpleItemTouchHelperCallback;
import de.kaiwidmaier.suggestamovie.data.Movie;
import de.kaiwidmaier.suggestamovie.data.DataHelper;


public class MainActivity extends AppCompatActivity {

  public static final String BASE_URL = "http://api.themoviedb.org/3/";
  private RecyclerView recyclerWatchlist;
  private RevealFAB btnDiscover;
  private RecyclerViewMovieAdapter movieAdapter;
  private ArrayList<Movie> watchlist;
  private LinearLayout layoutWatchlistEmpty;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    layoutWatchlistEmpty = findViewById(R.id.layout_watchlist_empty);
    recyclerWatchlist = findViewById(R.id.recycler_watchlist);
    DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerWatchlist.getContext(), DividerItemDecoration.VERTICAL);
    recyclerWatchlist.addItemDecoration(dividerItemDecoration);

    watchlist = ((DataHelper) this.getApplication()).getWatchlist();

    movieAdapter = new RecyclerViewMovieAdapter(MainActivity.this, watchlist, false);

    recyclerWatchlist.setAdapter(movieAdapter);
    checkEmpty();

    //Swipe to remove and long press to change position
    ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(movieAdapter);
    ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
    touchHelper.attachToRecyclerView(recyclerWatchlist);

    btnDiscover = findViewById(R.id.btn_discover);
    Intent discoverIntent = new Intent(MainActivity.this, FilterActivity.class);
    discoverIntent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
    btnDiscover.setIntent(discoverIntent);
    btnDiscover.setOnClickListener(new RevealFAB.OnClickListener() {
      @Override
      public void onClick(RevealFAB btn, View view) {
        btnDiscover.startActivityWithAnimation();
      }
    });
  }

  private void checkEmpty(){
    if(watchlist.isEmpty()){
      layoutWatchlistEmpty.setVisibility(View.VISIBLE);
    }
    else{
      layoutWatchlistEmpty.setVisibility(View.GONE);
    }
  }

  @Override
  protected void onResume() {
    if (movieAdapter != null) {
      movieAdapter.notifyDataSetChanged();
    }
    super.onResume();
    btnDiscover.onResume();
    checkEmpty();
  }
}

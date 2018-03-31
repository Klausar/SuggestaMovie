package de.kaiwidmaier.suggestamovie.activities;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;


import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import java.util.ArrayList;

import de.kaiwidmaier.suggestamovie.R;
import de.kaiwidmaier.suggestamovie.activities.fragments.NowPlayingFragment;
import de.kaiwidmaier.suggestamovie.activities.fragments.WatchlistFragment;
import de.kaiwidmaier.suggestamovie.adapters.RecyclerMovieAdapter;
import de.kaiwidmaier.suggestamovie.adapters.utils.SimpleItemTouchHelperCallback;
import de.kaiwidmaier.suggestamovie.data.Movie;
import de.kaiwidmaier.suggestamovie.data.DataHelper;
import it.sephiroth.android.library.bottomnavigation.BottomNavigation;


public class MainActivity extends AppCompatActivity {

  public static final String BASE_URL = "http://api.themoviedb.org/3/";

  private FrameLayout frameLayout;
  private FloatingActionButton fabSearch;
  private FloatingActionButton fabFilter;
  private FloatingActionMenu fabMenu;
  private BottomNavigation bottomNavigation;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    bottomNavigation = findViewById(R.id.bottom_navigation);
    bottomNavigation.setOnMenuItemClickListener(new BottomNavigation.OnMenuItemSelectionListener() {
      @Override
      public void onMenuItemSelect(int i, int i1) {
        Log.d("i", "i = " + i);
        Log.d("i1", "i1 = " + i1);
        switch(i1){
          case 0: //Watchlist
            setFragment(new WatchlistFragment());
            Log.d("Fragmentselection", "Switched to Watchlist");
            break;
          case 1: //Now Playing
            setFragment(new NowPlayingFragment());
            Log.d("Fragmentselection", "Switched to now playing");
            break;
        }
      }

      @Override
      public void onMenuItemReselect(int i, int i1) {

      }
    });
    fabMenu = findViewById(R.id.fab_menu);
    fabFilter = findViewById(R.id.fab_item_filter);
    fabFilter.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        Intent intent = new Intent(MainActivity.this, FilterActivity.class);
        startActivity(intent);
      }
    });
    fabSearch = findViewById(R.id.fab_item_search);
    fabSearch.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        Intent intent = new Intent(MainActivity.this, SearchActivity.class);
        startActivity(intent);
      }
    });

    frameLayout = findViewById(R.id.frame_main);
    setFragment(new WatchlistFragment());
  }

  @Override
  protected void onResume() {
    super.onResume();
    fabMenu.close(false);
  }

  public void setFragment(Fragment fragment){
    FragmentManager fragMan = getFragmentManager();
    FragmentTransaction fragTransaction = fragMan.beginTransaction();
    fragTransaction.replace(frameLayout.getId(), fragment);
    fragTransaction.addToBackStack(null);
    fragTransaction.commit();
  }
}

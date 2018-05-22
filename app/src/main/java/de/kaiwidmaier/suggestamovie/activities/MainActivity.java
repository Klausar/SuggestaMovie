package de.kaiwidmaier.suggestamovie.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;


import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import de.kaiwidmaier.suggestamovie.R;
import de.kaiwidmaier.suggestamovie.activities.fragments.NowPlayingFragment;
import de.kaiwidmaier.suggestamovie.activities.fragments.RecommendationsFragment;
import de.kaiwidmaier.suggestamovie.activities.fragments.WatchlistFragment;
import it.sephiroth.android.library.bottomnavigation.BottomNavigation;


public class MainActivity extends AppCompatActivity {

  public static final String BASE_URL = "http://api.themoviedb.org/3/";

  private FrameLayout frameLayout;
  private FloatingActionMenu fabMenu;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    BottomNavigation bottomNavigation = findViewById(R.id.bottom_navigation);
    bottomNavigation.setOnMenuItemClickListener(new BottomNavigation.OnMenuItemSelectionListener() {
      @Override
      public void onMenuItemSelect(int i, int i1, boolean b) {
        switch(i1){
          case 0: //Watchlist
            setFragment(new WatchlistFragment());
            Log.d("Fragmentselection", "Switched to watchlist");
            break;
          case 1: //Now Playing
            setFragment(new NowPlayingFragment());
            Log.d("Fragmentselection", "Switched to now playing");
            break;
          case 2: //Recommendations
            setFragment(new RecommendationsFragment());
            Log.d("Fragmentselection", "Switched to now playing");
            break;
        }
      }

      @Override
      public void onMenuItemReselect(int i, int i1, boolean b) {

      }
    });

    fabMenu = findViewById(R.id.fab_menu);
    setFabMenuAnimation();
    FloatingActionButton fabFilter = findViewById(R.id.fab_item_filter);
    fabFilter.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        Intent intent = new Intent(MainActivity.this, FilterActivity.class);
        startActivity(intent);
      }
    });
    FloatingActionButton fabSearch = findViewById(R.id.fab_item_search);
    fabSearch.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        Intent intent = new Intent(MainActivity.this, SearchActivity.class);
        startActivity(intent);
      }
    });

    frameLayout = findViewById(R.id.frame_main);

    if(savedInstanceState == null){
      setFragment(new WatchlistFragment());
    }

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
    fragTransaction.commit();
  }

  private void setFabMenuAnimation(){
    AnimatorSet set = new AnimatorSet();

    ObjectAnimator scaleOutX = ObjectAnimator.ofFloat(fabMenu.getMenuIconView(), "scaleX", 1.0f, 0.2f);
    ObjectAnimator scaleOutY = ObjectAnimator.ofFloat(fabMenu.getMenuIconView(), "scaleY", 1.0f, 0.2f);

    ObjectAnimator scaleInX = ObjectAnimator.ofFloat(fabMenu.getMenuIconView(), "scaleX", 0.2f, 1.0f);
    ObjectAnimator scaleInY = ObjectAnimator.ofFloat(fabMenu.getMenuIconView(), "scaleY", 0.2f, 1.0f);

    scaleOutX.setDuration(50);
    scaleOutY.setDuration(50);

    scaleInX.setDuration(150);
    scaleInY.setDuration(150);

    scaleInX.addListener(new AnimatorListenerAdapter() {
      @Override
      public void onAnimationStart(Animator animation) {
        fabMenu.getMenuIconView().setImageResource(fabMenu.isOpened()
          ? R.drawable.ic_movie_white_24dp : R.drawable.ic_close_white_24dp);
      }
    });
    set.play(scaleOutX).with(scaleOutY);
    set.play(scaleInX).with(scaleInY).after(scaleOutX);
    set.setInterpolator(new OvershootInterpolator(2));

    fabMenu.setIconToggleAnimatorSet(set);
  }
}

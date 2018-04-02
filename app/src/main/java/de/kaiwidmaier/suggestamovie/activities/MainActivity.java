package de.kaiwidmaier.suggestamovie.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.FrameLayout;
import android.widget.LinearLayout;


import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import java.util.ArrayList;
import java.util.List;

import de.kaiwidmaier.suggestamovie.R;
import de.kaiwidmaier.suggestamovie.activities.fragments.NowPlayingFragment;
import de.kaiwidmaier.suggestamovie.activities.fragments.RecommendationsFragment;
import de.kaiwidmaier.suggestamovie.activities.fragments.WatchlistFragment;
import de.kaiwidmaier.suggestamovie.adapters.RecyclerMovieAdapter;
import de.kaiwidmaier.suggestamovie.adapters.utils.SimpleItemTouchHelperCallback;
import de.kaiwidmaier.suggestamovie.data.Movie;
import de.kaiwidmaier.suggestamovie.data.DataHelper;
import it.sephiroth.android.library.bottomnavigation.BottomNavigation;

import static android.util.Log.INFO;
import static it.sephiroth.android.library.bottomnavigation.MiscUtils.log;


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

  public BottomNavigation getBottomNavigation() {
    return bottomNavigation;
  }

  public static class FabBehavior extends CoordinatorLayout.Behavior<FloatingActionMenu> {
    public FabBehavior() {
      super();
    }

    public FabBehavior(Context context, AttributeSet attrs) {
      super(context, attrs);
    }


    @Override
    public boolean layoutDependsOn(final CoordinatorLayout parent, final FloatingActionMenu child, final View dependency) {
      if (BottomNavigation.class.isInstance(dependency)) {
        return true;
      } else if (Snackbar.SnackbarLayout.class.isInstance(dependency)) {
        return true;
      }
      return super.layoutDependsOn(parent, child, dependency);
    }

    @Override
    public boolean onDependentViewChanged(
      final CoordinatorLayout parent, final FloatingActionMenu child, final View dependency) {
      log("BEHAVIOR", INFO, "onDependentViewChanged: " + dependency);

      final List<View> list = parent.getDependencies(child);
      int bottomMargin = ((ViewGroup.MarginLayoutParams) child.getLayoutParams()).bottomMargin;

      float t = 0;
      boolean result = false;

      for (View dep : list) {
        if (Snackbar.SnackbarLayout.class.isInstance(dep)) {
          t += dep.getTranslationY() - dep.getHeight();
          result = true;
        } else if (BottomNavigation.class.isInstance(dep)) {
          BottomNavigation navigation = (BottomNavigation) dep;
          t += navigation.getTranslationY() - navigation.getHeight() + bottomMargin;
          result = true;
        }
      }

      child.setTranslationY(t);
      return result;
    }

    @Override
    public void onDependentViewRemoved(
      final CoordinatorLayout parent, final FloatingActionMenu child, final View dependency) {
      super.onDependentViewRemoved(parent, child, dependency);
    }
  }
}

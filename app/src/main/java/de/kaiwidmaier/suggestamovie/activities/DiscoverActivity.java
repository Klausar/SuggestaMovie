package de.kaiwidmaier.suggestamovie.activities;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import java.util.ArrayList;

import de.kaiwidmaier.suggestamovie.R;
import de.kaiwidmaier.suggestamovie.activities.fragments.FilterFragment;
import de.kaiwidmaier.suggestamovie.activities.fragments.GenreFragment;
import de.kaiwidmaier.suggestamovie.activities.fragments.RangeFragment;
import de.kaiwidmaier.suggestamovie.adapters.FragmentDiscoverAdapter;
import de.kaiwidmaier.suggestamovie.utils.NetworkUtils;

public class DiscoverActivity extends AppCompatActivity {

  private static final String TAG = DiscoverActivity.class.getSimpleName();
  private FloatingActionButton btnSearch;
  private ViewPager pager;

  private GenreFragment genreFragment;
  private RangeFragment rangeFragment;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_discover);

    rangeFragment = new RangeFragment();
    genreFragment = new GenreFragment();

    btnSearch = findViewById(R.id.fab_search);
    btnSearch.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        if (NetworkUtils.isNetworkAvailable(DiscoverActivity.this)) {
          startResultIntent(rangeFragment.getReleaseDateMin(), rangeFragment.getReleaseDateMax(), rangeFragment.getRatingMin(),
            rangeFragment.getRatingMax(), genreFragment.getIncludedGenres(), genreFragment.getExcludedGenres());
        } else {
          Snackbar snackbar = Snackbar.make(pager, getString(R.string.unable_connect), Snackbar.LENGTH_SHORT);
          snackbar.show();
        }
      }
    });
    pager = findViewById(R.id.viewpager_discover);
    pager.setAdapter(buildAdapter());
    pager.setOffscreenPageLimit(2);
  }

  private void startResultIntent(String releaseDateMin, String releaseDateMax, int ratingMin, int ratingMax, String includedGenres, String excludedGenres) {
    Intent resultIntent = new Intent(this, ResultActivity.class);
    resultIntent.putExtra("releaseDateMin", releaseDateMin);
    resultIntent.putExtra("releaseDateMax", releaseDateMax);
    resultIntent.putExtra("ratingMin", ratingMin);
    resultIntent.putExtra("ratingMax", ratingMax);
    resultIntent.putExtra("includedGenres", includedGenres);
    resultIntent.putExtra("excludedGenres", excludedGenres);

    startActivity(resultIntent);
  }

  private PagerAdapter buildAdapter() {
    ArrayList<FilterFragment> fragments = new ArrayList<>();
    fragments.add(rangeFragment);
    fragments.add(genreFragment);
    return (new FragmentDiscoverAdapter(this, getSupportFragmentManager(), fragments));
  }

  @Override
  public void onBackPressed() {
    Intent intent = new Intent(this, MainActivity.class);
    intent.setFlags(intent.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
    startActivity(intent);
    overridePendingTransition(R.anim.slide_in_bottom_top, R.anim.slide_out_bottom_top);
  }
}

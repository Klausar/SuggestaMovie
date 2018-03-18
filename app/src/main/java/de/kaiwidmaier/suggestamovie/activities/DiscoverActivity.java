package de.kaiwidmaier.suggestamovie.activities;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.appyvet.materialrangebar.RangeBar;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import de.kaiwidmaier.suggestamovie.R;
import de.kaiwidmaier.suggestamovie.activities.fragments.GenreFragment;
import de.kaiwidmaier.suggestamovie.activities.fragments.RangeFragment;
import de.kaiwidmaier.suggestamovie.adapters.FragmentDiscoverAdapter;
import de.kaiwidmaier.suggestamovie.adapters.RecyclerViewGenreAdapter;
import de.kaiwidmaier.suggestamovie.data.Genre;
import de.kaiwidmaier.suggestamovie.data.GenreResponse;
import de.kaiwidmaier.suggestamovie.persistence.Serializer;
import de.kaiwidmaier.suggestamovie.rest.GenreApiService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static de.kaiwidmaier.suggestamovie.activities.MainActivity.BASE_URL;
import static de.kaiwidmaier.suggestamovie.data.DataHelper.API_KEY;

public class DiscoverActivity extends AppCompatActivity {

  private static final String TAG = DiscoverActivity.class.getSimpleName();
  private RangeBar seekbarRelease;
  private RangeBar seekbarRating;
  private FloatingActionButton btnSearch;
  private Retrofit retrofit;
  private RecyclerView recyclerGenres;
  private RecyclerViewGenreAdapter genreAdapter;
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
        startResultIntent(rangeFragment.getReleaseDateMin(), rangeFragment.getReleaseDateMax(), rangeFragment.getRatingMin(), rangeFragment.getRatingMax(), genreFragment.getIncludedGenres());
      }
    });
    pager = findViewById(R.id.viewpager_discover);
    pager.setAdapter(buildAdapter());
  }

  private void startResultIntent(String releaseDateMin, String releaseDateMax, int ratingMin, int ratingMax, String includedGenres) {
    Intent resultIntent = new Intent(this, ResultActivity.class);
    resultIntent.putExtra("releaseDateMin", releaseDateMin);
    resultIntent.putExtra("releaseDateMax", releaseDateMax);
    resultIntent.putExtra("ratingMin", ratingMin);
    resultIntent.putExtra("ratingMax", ratingMax);
    resultIntent.putExtra("includedGenres", includedGenres);

    startActivity(resultIntent);
  }

  private PagerAdapter buildAdapter() {
    ArrayList<Fragment> fragments = new ArrayList<>();
    fragments.add(rangeFragment);
    fragments.add(genreFragment);
    return (new FragmentDiscoverAdapter(this, getSupportFragmentManager(), fragments));
  }

  @Override
  public void onBackPressed() {
    super.onBackPressed();
    overridePendingTransition(R.anim.slide_in_bottom_top, R.anim.slide_out_bottom_top);
  }
}

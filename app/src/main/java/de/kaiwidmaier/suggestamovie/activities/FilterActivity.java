package de.kaiwidmaier.suggestamovie.activities;

import android.app.ActionBar;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.appyvet.materialrangebar.RangeBar;

import java.util.Calendar;

import de.kaiwidmaier.suggestamovie.R;
import de.kaiwidmaier.suggestamovie.adapters.RecyclerGenreAdapter;
import de.kaiwidmaier.suggestamovie.data.DataHelper;
import de.kaiwidmaier.suggestamovie.data.Movie;
import de.kaiwidmaier.suggestamovie.utils.NetworkUtils;
import de.kaiwidmaier.suggestamovie.rest.ResultType;
import retrofit2.Retrofit;

public class FilterActivity extends AppCompatActivity {

  private static final String TAG = FilterActivity.class.getSimpleName();
  private FloatingActionButton fabSearch;
  private RangeBar rangeBarRelease;
  private RangeBar rangeBarRating;
  private final int currentYear = Calendar.getInstance().get(Calendar.YEAR);
  private RecyclerView recyclerGenres;
  private RecyclerGenreAdapter genreAdapter;
  private Spinner sortSpinner;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_filter);

    getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    sortSpinner = findViewById(R.id.spinner_sort_by);
    ArrayAdapter<ResultType.SortType> adapter = new ArrayAdapter<ResultType.SortType>(this, R.layout.spinner_item_sort, ResultType.SortType.getSortTypes(this));
    adapter.setDropDownViewResource(R.layout.spinner_item_sort);
    sortSpinner.setAdapter(adapter);

    rangeBarRelease = findViewById(R.id.rangebar_release);
    rangeBarRelease.setTickEnd(currentYear+1); //Allow selection up to one year from today
    rangeBarRating = findViewById(R.id.rangebar_rating);
    recyclerGenres = findViewById(R.id.recycler_genres);
    recyclerGenres.setLayoutManager(new GridLayoutManager(this, 3));
    recyclerGenres.setNestedScrollingEnabled(false);
    setupGenres();

    fabSearch = findViewById(R.id.fab_search);
    fabSearch.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        if (NetworkUtils.isNetworkAvailable(FilterActivity.this)) {
          startResultIntent(getReleaseDateMin(), getReleaseDateMax(), getRatingMin(),
            getRatingMax(), getIncludedGenres(), getExcludedGenres());
        } else {
          Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), getString(R.string.unable_connect), Snackbar.LENGTH_SHORT);
          snackbar.show();
        }
      }
    });
  }

  private void startResultIntent(String releaseDateMin, String releaseDateMax, int ratingMin, int ratingMax, String includedGenres, String excludedGenres) {
    Intent resultIntent = new Intent(this, ResultActivity.class);
    resultIntent.putExtra("releaseDateMin", releaseDateMin);
    resultIntent.putExtra("releaseDateMax", releaseDateMax);
    resultIntent.putExtra("ratingMin", ratingMin);
    resultIntent.putExtra("ratingMax", ratingMax);
    resultIntent.putExtra("includedGenres", includedGenres);
    resultIntent.putExtra("excludedGenres", excludedGenres);
    resultIntent.putExtra("resultTitle", getString(R.string.filter));
    resultIntent.putExtra("resultDescr", getString(R.string.filter_descr));
    resultIntent.putExtra("resultType", ResultType.FILTER);
    resultIntent.putExtra("sortBy", ((ResultType.SortType) sortSpinner.getSelectedItem()).getValue());

    startActivity(resultIntent);
  }

  private void setupGenres() {
    genreAdapter = new RecyclerGenreAdapter(this, ((DataHelper) this.getApplication()).getGenres());
    recyclerGenres.setAdapter(genreAdapter);
  }

  public String getReleaseDateMin() {
    return rangeBarRelease.getLeftPinValue() + "-01-01"; //e.g. 1954-01-01
  }

  public String getReleaseDateMax() {
    return rangeBarRelease.getRightPinValue() + "-12-31"; //e.g. 2018-12-31;
  }

  public int getRatingMin() {
    return Integer.valueOf(rangeBarRating.getLeftPinValue());
  }

  public int getRatingMax() {
    return Integer.valueOf(rangeBarRating.getRightPinValue());
  }

  public String getIncludedGenres(){
    return android.text.TextUtils.join("|", genreAdapter.getSelectedGenresIds());
  }

  public String getExcludedGenres(){
    return android.text.TextUtils.join("|", genreAdapter.getUnselectedGenresIds());
  }

  public boolean onOptionsItemSelected(MenuItem item){
    onBackPressed();
    return true;
  }

}

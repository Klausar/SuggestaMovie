package de.kaiwidmaier.suggestamovie.activities;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.appyvet.materialrangebar.RangeBar;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import de.kaiwidmaier.suggestamovie.R;
import de.kaiwidmaier.suggestamovie.adapters.RecyclerViewGenreAdapter;
import de.kaiwidmaier.suggestamovie.data.Genre;
import de.kaiwidmaier.suggestamovie.data.GenreResponse;
import de.kaiwidmaier.suggestamovie.persistence.Serializer;
import de.kaiwidmaier.suggestamovie.rest.GenreApiService;
import de.kaiwidmaier.suggestamovie.utils.NetworkUtils;
import de.kaiwidmaier.suggestamovie.rest.ResultType;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static de.kaiwidmaier.suggestamovie.activities.MainActivity.BASE_URL;
import static de.kaiwidmaier.suggestamovie.data.DataHelper.API_KEY;

public class FilterActivity extends AppCompatActivity {

  private static final String TAG = FilterActivity.class.getSimpleName();
  private FloatingActionButton btnSearch;
  private RangeBar rangeBarRelease;
  private RangeBar rangeBarRating;
  final int currentYear = Calendar.getInstance().get(Calendar.YEAR);
  private RecyclerView recyclerGenres;
  private RecyclerViewGenreAdapter genreAdapter;
  private Retrofit retrofit;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_filter);

    rangeBarRelease = findViewById(R.id.rangebar_release);
    rangeBarRelease.setTickEnd(currentYear);
    rangeBarRating = findViewById(R.id.rangebar_rating);
    recyclerGenres = findViewById(R.id.recycler_genres);
    recyclerGenres.setLayoutManager(new GridLayoutManager(this, 3));
    recyclerGenres.setNestedScrollingEnabled(false);
    setupGenres();

    btnSearch = findViewById(R.id.fab_search);
    btnSearch.setOnClickListener(new View.OnClickListener() {
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

    startActivity(resultIntent);
  }

  private void setupGenres() {
    Serializer serializer = new Serializer(this);
    ArrayList<Genre> genres = serializer.readGenres();
    //Get genres from API and save them to file if not yet saved to file
    //else use existing file
    if (genres.size() <= 0) {
      connectAndGetApiData();
    } else {
      genreAdapter = new RecyclerViewGenreAdapter(this, genres);
      recyclerGenres.setAdapter(genreAdapter);
    }
  }

  private void connectAndGetApiData() {

    if (retrofit == null) {
      retrofit = new Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
    }

    GenreApiService genreApiService = retrofit.create(GenreApiService.class);

    Call<GenreResponse> call = genreApiService.getGenres(API_KEY, Locale.getDefault().getLanguage());

    Log.d(TAG, "Current language: " + Locale.getDefault().toString());

    call.enqueue(new Callback<GenreResponse>() {
      @Override
      public void onResponse(Call<GenreResponse> call, Response<GenreResponse> response) {
        List<Genre> genres = response.body().getGenres();
        Serializer serializer = new Serializer(FilterActivity.this);
        serializer.writeGenres((ArrayList<Genre>) genres);

        genreAdapter = new RecyclerViewGenreAdapter(FilterActivity.this, genres);
        recyclerGenres.setAdapter(genreAdapter);

        Log.d(TAG, "Request URL: " + response.raw().request().url());
      }

      @Override
      public void onFailure(Call<GenreResponse> call, Throwable throwable) {
        Log.e(TAG, throwable.toString());
      }
    });
  }

  public String getReleaseDateMin() {
    return rangeBarRelease.getLeftPinValue() + "-01-01"; //e.g. 1954-01-01
  }

  public String getReleaseDateMax() {
    String releaseDateMax;
    //Only allow dates up to today to exclude movies that haven't been released yet
    if (Integer.valueOf(rangeBarRelease.getRightPinValue()) == currentYear) {
      releaseDateMax = String.format("%s-%s-%s", currentYear, Calendar.getInstance().get(Calendar.MONTH) + 1, Calendar.getInstance().get(Calendar.DAY_OF_MONTH)); //e.g. 2018-03-12
    } else {
      releaseDateMax = rangeBarRelease.getRightPinValue() + "-12-31"; //e.g. 2018-12-31;
    }
    return releaseDateMax;
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

}

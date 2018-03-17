package de.kaiwidmaier.suggestamovie.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Parcelable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;

import org.florescu.android.rangeseekbar.RangeSeekBar;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import de.kaiwidmaier.suggestamovie.BuildConfig;
import de.kaiwidmaier.suggestamovie.R;
import de.kaiwidmaier.suggestamovie.adapters.RecyclerViewGenreAdapter;
import de.kaiwidmaier.suggestamovie.adapters.RecyclerViewMovieAdapter;
import de.kaiwidmaier.suggestamovie.data.Genre;
import de.kaiwidmaier.suggestamovie.data.GenreResponse;
import de.kaiwidmaier.suggestamovie.data.Movie;
import de.kaiwidmaier.suggestamovie.data.MovieResponse;
import de.kaiwidmaier.suggestamovie.rest.GenreApiService;
import de.kaiwidmaier.suggestamovie.rest.MovieApiService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static de.kaiwidmaier.suggestamovie.activities.MainActivity.BASE_URL;
import static de.kaiwidmaier.suggestamovie.data.DataHelper.API_KEY;

public class DiscoverActivity extends AppCompatActivity {

  private static final String TAG = DiscoverActivity.class.getSimpleName();
  private RangeSeekBar<Integer> seekbarRelease;
  private RangeSeekBar<Integer> seekbarRating;
  private Switch switchAdult;
  private FloatingActionButton btnSearch;
  private Retrofit retrofit;
  private RecyclerView recyclerGenres;
  RecyclerViewGenreAdapter genreAdapter;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_discover);

    seekbarRelease = findViewById(R.id.seekbar_release);
    seekbarRating = findViewById(R.id.seekbar_rating);
    switchAdult = findViewById(R.id.checkbox_include_adult);
    btnSearch = findViewById(R.id.btn_start_discover);
    recyclerGenres = findViewById(R.id.recycler_genres);
    recyclerGenres.setLayoutManager(new GridLayoutManager(this, 3));
    recyclerGenres.setNestedScrollingEnabled(false);

    connectAndGetApiData();

    final int currentYear = Calendar.getInstance().get(Calendar.YEAR);
    seekbarRelease.setRangeValues(1930, currentYear);
    seekbarRating.setRangeValues(0, 10);

    btnSearch.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        String releaseDateMin = seekbarRelease.getSelectedMinValue() + "-01-01"; //e.g. 1954-01-01
        String releaseDateMax;

        //Only allow dates up to today to exclude movies that haven't been released yet
        if (seekbarRelease.getSelectedMaxValue() == currentYear) {
          releaseDateMax = String.format("%s-%s-%s", currentYear, Calendar.getInstance().get(Calendar.MONTH) + 1, Calendar.getInstance().get(Calendar.DAY_OF_MONTH)); //e.g. 2018-03-12
        } else {
          releaseDateMax = seekbarRelease.getSelectedMaxValue() + "-12-31"; //e.g. 2018-12-31;
        }
        Log.d(TAG, releaseDateMax);

        int ratingMin = seekbarRating.getSelectedMinValue();
        int ratingMax = seekbarRating.getSelectedMaxValue();
        boolean adult = switchAdult.isChecked();

        startResultIntent(releaseDateMin, releaseDateMax, ratingMin, ratingMax, adult);
      }
    });
  }

  private void startResultIntent(String releaseDateMin, String releaseDateMax, int ratingMin, int ratingMax, boolean adult) {
    Intent resultIntent = new Intent(this, ResultActivity.class);
    resultIntent.putExtra("releaseDateMin", releaseDateMin);
    resultIntent.putExtra("releaseDateMax", releaseDateMax);
    resultIntent.putExtra("ratingMin", ratingMin);
    resultIntent.putExtra("ratingMax", ratingMax);
    resultIntent.putExtra("adult", adult);

    startActivity(resultIntent);
  }

  public void connectAndGetApiData() {

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

        genreAdapter = new RecyclerViewGenreAdapter(DiscoverActivity.this, genres);
        recyclerGenres.setAdapter(genreAdapter);

        Log.d(TAG, "Request URL: " + response.raw().request().url());
      }

      @Override
      public void onFailure(Call<GenreResponse> call, Throwable throwable) {
        Log.e(TAG, throwable.toString());
      }
    });
  }
}

package de.kaiwidmaier.suggestamovie.activities;

import android.content.Intent;
import android.os.Parcelable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.util.List;
import java.util.Locale;

import de.kaiwidmaier.suggestamovie.BuildConfig;
import de.kaiwidmaier.suggestamovie.R;
import de.kaiwidmaier.suggestamovie.adapters.RecyclerViewMovieAdapter;
import de.kaiwidmaier.suggestamovie.data.Movie;
import de.kaiwidmaier.suggestamovie.data.MovieResponse;
import de.kaiwidmaier.suggestamovie.rest.MovieApiService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static de.kaiwidmaier.suggestamovie.activities.MainActivity.BASE_URL;
import static de.kaiwidmaier.suggestamovie.data.DataHelper.API_KEY;

public class ResultActivity extends AppCompatActivity {

  public static final String TAG = ResultActivity.class.getSimpleName();
  private static Retrofit retrofit;
  private RecyclerView recyclerResults;
  private RecyclerViewMovieAdapter movieAdapter;
  private TextView textHead;
  private TextView textDescr;

  //Intent extras
  private String releaseDateMin;
  private String releaseDateMax;
  private int ratingMin;
  private int ratingMax;
  private int page;
  private String includedGenres;
  private String excludedGenres;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_result);

    Intent intent = getIntent();
    releaseDateMin = intent.getStringExtra("releaseDateMin");
    releaseDateMax = intent.getStringExtra("releaseDateMax");
    ratingMin = intent.getIntExtra("ratingMin", 0);
    ratingMax =  intent.getIntExtra("ratingMax", 10);
    includedGenres = intent.getStringExtra("includedGenres");
    excludedGenres = intent.getStringExtra("excludedGenres");
    page = 1;

    textHead = findViewById(R.id.text_head_results);
    textDescr = findViewById(R.id.text_descr_results);
    textHead.setText(getString(R.string.results));
    textDescr.setText(getString(R.string.results_descr));

    recyclerResults = findViewById(R.id.recycler_results);
    DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerResults.getContext(), DividerItemDecoration.VERTICAL);
    recyclerResults.addItemDecoration(dividerItemDecoration);
    recyclerResults.addOnScrollListener(new RecyclerView.OnScrollListener() {

      @Override
      public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);
        if (movieAdapter != null && !recyclerView.canScrollVertically(1) && !movieAdapter.isLoading()) {
          movieAdapter.setLoading(true);
          Log.d(TAG, "Updating RecyclerView");
          connectAndGetApiData(page);
        }
      }
    });

    connectAndGetApiData(page);
  }

  public void connectAndGetApiData(final int page) {

    if (retrofit == null) {
      retrofit = new Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
    }

    MovieApiService movieApiService = retrofit.create(MovieApiService.class);

    Call<MovieResponse> call = movieApiService.getMovie(API_KEY, Locale.getDefault().getLanguage(), Locale.getDefault().getCountry(),
      null, false, releaseDateMin, releaseDateMax, ratingMin, ratingMax, includedGenres, excludedGenres, page);

    Log.d(TAG, "Current language: " + Locale.getDefault().toString());
    Log.d(TAG, "Current region: " + Locale.getDefault().getCountry());

    call.enqueue(new Callback<MovieResponse>() {
      @Override
      public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {
        List<Movie> movies = response.body().getResults();
        if(movieAdapter == null){
          movieAdapter = new RecyclerViewMovieAdapter(ResultActivity.this, movies, true);
          recyclerResults.setAdapter(movieAdapter);
        }
        else{
          Parcelable recyclerViewState;
          recyclerViewState = recyclerResults.getLayoutManager().onSaveInstanceState();

          //Prevents duplicates with bad connection
          if(!movieAdapter.containsAll(movies)){
            movieAdapter.addAll(movies);
          }
          recyclerResults.getLayoutManager().onRestoreInstanceState(recyclerViewState); //Restores scroll position after notifyDataSetChanged()
        }
        ResultActivity.this.page++;
        movieAdapter.setLoading(false);
        Log.d(TAG, "Request URL: " + response.raw().request().url());
        Log.d(TAG, "Current Page: " + response.body().getPage());
        Log.d(TAG, "Number of movies received: " + movies.size());

      }

      @Override
      public void onFailure(Call<MovieResponse> call, Throwable throwable) {
        Log.e(TAG, throwable.toString());
        Snackbar snackbar = Snackbar.make(recyclerResults, getString(R.string.unable_connect), Snackbar.LENGTH_INDEFINITE)
          .setAction(getString(R.string.retry), new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              connectAndGetApiData(page);
            }
          });
        snackbar.show();
      }
    });
  }
}

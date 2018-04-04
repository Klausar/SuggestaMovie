package de.kaiwidmaier.suggestamovie.activities;

import android.content.Intent;
import android.os.Parcelable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;
import java.util.Locale;

import de.kaiwidmaier.suggestamovie.R;
import de.kaiwidmaier.suggestamovie.adapters.RecyclerMovieAdapter;
import de.kaiwidmaier.suggestamovie.data.Movie;
import de.kaiwidmaier.suggestamovie.data.MovieResponse;
import de.kaiwidmaier.suggestamovie.rest.MovieApiService;
import de.kaiwidmaier.suggestamovie.rest.ResultType;
import de.kaiwidmaier.suggestamovie.utils.LocalizationUtils;
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
  private RecyclerMovieAdapter movieAdapter;
  private LinearLayout layoutResultsEmpty;
  private Intent intent;
  private ResultType resultType;
  private int page;
  private ProgressBar progressBar;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_result);

    getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    progressBar = findViewById(R.id.progress);
    intent = getIntent();
    resultType = (ResultType) intent.getSerializableExtra("resultType");
    page = 1;

    TextView textHead = findViewById(R.id.text_head_results);
    TextView textDescr = findViewById(R.id.text_descr_results);
    textHead.setText(intent.getStringExtra("resultTitle"));
    textDescr.setText(intent.getStringExtra("resultDescr"));

    layoutResultsEmpty = findViewById(R.id.layout_searchresults_empty);

    recyclerResults = findViewById(R.id.recycler_results);
    DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerResults.getContext(), DividerItemDecoration.VERTICAL);
    recyclerResults.addItemDecoration(dividerItemDecoration);
    recyclerResults.addOnScrollListener(new RecyclerView.OnScrollListener() {

      private int visibleThreshold = 5;
      int firstVisibleItem;
      int visibleItemCount;
      int totalItemCount;

      @Override
      public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        visibleItemCount = recyclerResults.getChildCount();
        totalItemCount = recyclerResults.getLayoutManager().getItemCount();
        firstVisibleItem = ((LinearLayoutManager) recyclerResults.getLayoutManager()).findFirstVisibleItemPosition();

        if (!movieAdapter.isLoading() && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) {
          //End has been reached, load more
          connectAndGetApiData(page);
          movieAdapter.setLoading(true);
        }
      }
    });

    connectAndGetApiData(page);
  }

  private void checkEmpty(){
    if(movieAdapter == null || movieAdapter.getItemCount() == 0){
      layoutResultsEmpty.setVisibility(View.VISIBLE);
    }
    else{
      layoutResultsEmpty.setVisibility(View.GONE);
    }
  }

  public void connectAndGetApiData(final int page) {

    if (retrofit == null) {
      retrofit = new Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
    }

    MovieApiService movieApiService = retrofit.create(MovieApiService.class);

    Call<MovieResponse> call = null;

    if(resultType == ResultType.FILTER){
      call = movieApiService.getMovie(API_KEY, LocalizationUtils.getLanguage(), LocalizationUtils.getCountry(),
        null, false, intent.getStringExtra("releaseDateMin"), intent.getStringExtra("releaseDateMax"),
        intent.getIntExtra("ratingMin", 0), intent.getIntExtra("ratingMax", 10),
        intent.getStringExtra("includedGenres"), intent.getStringExtra("excludedGenres"), page);
    }
    else if(resultType == ResultType.SIMILAR){
      call = movieApiService.getSimilarMovies(intent.getIntExtra("movieId", 0), API_KEY, LocalizationUtils.getLanguage(), LocalizationUtils.getCountry(), page);
    }
    else if(resultType == ResultType.SEARCH){
      call = movieApiService.getMoviesByName(API_KEY, LocalizationUtils.getLanguage(), LocalizationUtils.getCountry(), intent.getStringExtra("searchString"), page);
    }

    Log.d(TAG, "Current language: " + Locale.getDefault().toString());
    Log.d(TAG, "Current region: " + Locale.getDefault().getCountry());

    call.enqueue(new Callback<MovieResponse>() {
      @Override
      public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {
        List<Movie> movies = response.body().getResults();
        progressBar.setVisibility(View.GONE);
        if(movies == null || movies.size() == 0){
          checkEmpty();
          return;
        }
        if(movieAdapter == null){
          movieAdapter = new RecyclerMovieAdapter(ResultActivity.this, movies, true);
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
        progressBar.setVisibility(View.GONE);
        Snackbar snackbar = Snackbar.make(recyclerResults, getString(R.string.unable_connect), Snackbar.LENGTH_INDEFINITE)
          .setAction(getString(R.string.retry), new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              connectAndGetApiData(page);
              progressBar.setVisibility(View.VISIBLE);
            }
          });
        snackbar.show();
        checkEmpty();
      }
    });
  }

  public boolean onOptionsItemSelected(MenuItem item){
    onBackPressed();
    return true;
  }

}

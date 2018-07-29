package de.kaiwidmaier.suggestamovie.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.DividerItemDecoration;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;
import java.util.Locale;

import de.kaiwidmaier.suggestamovie.R;
import de.kaiwidmaier.suggestamovie.activities.interfaces.EndlessAPILoader;
import de.kaiwidmaier.suggestamovie.adapters.RecyclerMovieAdapter;
import de.kaiwidmaier.suggestamovie.data.Movie;
import de.kaiwidmaier.suggestamovie.data.MovieResponse;
import de.kaiwidmaier.suggestamovie.rest.MovieApiService;
import de.kaiwidmaier.suggestamovie.rest.ResultType;
import de.kaiwidmaier.suggestamovie.utils.LocalizationUtils;
import de.kaiwidmaier.suggestamovie.views.EndlessRecyclerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static de.kaiwidmaier.suggestamovie.activities.MainActivity.BASE_URL;
import static de.kaiwidmaier.suggestamovie.data.DataHelper.API_KEY;

public class ResultActivity extends BaseMenuActivity implements EndlessAPILoader {

  public static final String TAG = ResultActivity.class.getSimpleName();
  private static Retrofit retrofit;
  private EndlessRecyclerView recyclerResults;
  private RecyclerMovieAdapter movieAdapter;
  private LinearLayout layoutResultsEmpty;
  private Intent intent;
  private ResultType resultType;
  private ProgressBar progressBar;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_result);

    progressBar = findViewById(R.id.progress);
    intent = getIntent();
    resultType = (ResultType) intent.getSerializableExtra("resultType");

    TextView textHead = findViewById(R.id.text_head_results);
    TextView textDescr = findViewById(R.id.text_descr_results);
    textHead.setText(intent.getStringExtra("resultTitle"));
    textDescr.setText(intent.getStringExtra("resultDescr"));

    layoutResultsEmpty = findViewById(R.id.layout_searchresults_empty);

    recyclerResults = findViewById(R.id.recycler_results);
    DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerResults.getContext(), DividerItemDecoration.VERTICAL);
    recyclerResults.addItemDecoration(dividerItemDecoration);

    connectAndGetApiData(recyclerResults.getPage());
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
        intent.getStringExtra("sortBy"), false, intent.getStringExtra("releaseDateMin"), intent.getStringExtra("releaseDateMax"),
        intent.getIntExtra("ratingMin", 0), intent.getIntExtra("ratingMax", 10),
        intent.getStringExtra("includedGenres"), intent.getStringExtra("excludedGenres"), page);
    }
    else if(resultType == ResultType.SIMILAR){
      call = movieApiService.getSimilarMovies(intent.getIntExtra("movieId", 0), API_KEY, LocalizationUtils.getLanguage(), LocalizationUtils.getCountry(), page);
    }
    else if(resultType == ResultType.SEARCH){
      call = movieApiService.getMoviesByName(API_KEY, LocalizationUtils.getLanguage(), LocalizationUtils.getCountry(), intent.getStringExtra("searchString"), page);
    }

    assert call != null;
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
            movieAdapter.addMovies(movies);
          }
          recyclerResults.getLayoutManager().onRestoreInstanceState(recyclerViewState); //Restores scroll position after notifyDataSetChanged()
        }
        recyclerResults.setPage(recyclerResults.getPage() + 1);
        recyclerResults.setLoading(false);
        checkEmpty();
      }

      @Override
      public void onFailure(Call<MovieResponse> call, Throwable throwable) {
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

  @Override
  protected void onResume() {
    super.onResume();
    if(movieAdapter != null){
      movieAdapter.notifyDataSetChanged(); //In case a movie was added to watchlist in MovieActivity
    }
  }
}

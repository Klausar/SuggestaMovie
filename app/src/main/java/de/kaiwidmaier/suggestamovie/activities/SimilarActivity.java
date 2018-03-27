package de.kaiwidmaier.suggestamovie.activities;

import android.content.Intent;
import android.os.Parcelable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;
import java.util.Locale;

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

public class SimilarActivity extends AppCompatActivity{

  public static final String TAG = SimilarActivity.class.getSimpleName();
  private static Retrofit retrofit;
  private TextView textHead;
  private TextView textDescr;
  private RecyclerView recyclerSimilar;
  private RecyclerViewMovieAdapter movieAdapter;
  private int page;
  private Movie movie;
  private LinearLayout layoutResultsEmpty;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_similar);

    Intent intent = getIntent();
    movie = intent.getParcelableExtra("movie");

    page = 1;

    textHead = findViewById(R.id.text_head_results);
    textDescr = findViewById(R.id.text_descr_results);
    textHead.setText(getString(R.string.similar));
    textDescr.setText(String.format(getString(R.string.similar_descr), movie.getTitle()));
    layoutResultsEmpty = findViewById(R.id.layout_searchresults_empty);
    recyclerSimilar = findViewById(R.id.recycler_results);
    recyclerSimilar.addOnScrollListener(new RecyclerView.OnScrollListener() {

      private int visibleThreshold = 5;
      int firstVisibleItem;
      int visibleItemCount;
      int totalItemCount;

      @Override
      public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        visibleItemCount = recyclerSimilar.getChildCount();
        totalItemCount = recyclerSimilar.getLayoutManager().getItemCount();
        firstVisibleItem = ((LinearLayoutManager) recyclerSimilar.getLayoutManager()).findFirstVisibleItemPosition();

        if (!movieAdapter.isLoading() && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) {
          // End has been reached
          connectAndGetApiData(page);
          movieAdapter.setLoading(true);
        }
      }
    });

    connectAndGetApiData(page);
  }

  public void checkEmpty(){
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

    Call<MovieResponse> call = movieApiService.getSimilarMovies(movie.getId(), API_KEY, Locale.getDefault().getLanguage(), Locale.getDefault().getCountry(), page);

    Log.d(TAG, "Current language: " + Locale.getDefault().toString());
    Log.d(TAG, "Current region: " + Locale.getDefault().getCountry());

    call.enqueue(new Callback<MovieResponse>() {
      @Override
      public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {
        List<Movie> movies = response.body().getResults();

        if(movieAdapter == null){
          movieAdapter = new RecyclerViewMovieAdapter(SimilarActivity.this, movies, true);
          recyclerSimilar.setAdapter(movieAdapter);
        }
        else{
          Parcelable recyclerViewState;
          recyclerViewState = recyclerSimilar.getLayoutManager().onSaveInstanceState();

          //Prevents duplicates with bad connection
          if(!movieAdapter.containsAll(movies)){
            movieAdapter.addAll(movies);
          }
          recyclerSimilar.getLayoutManager().onRestoreInstanceState(recyclerViewState); //Restores scroll position after notifyDataSetChanged()
        }
        SimilarActivity.this.page++;
        movieAdapter.setLoading(false);
        Log.d(TAG, "Request URL: " + response.raw().request().url());
        Log.d(TAG, "Current Page: " + response.body().getPage());
        Log.d(TAG, "Number of movies received: " + movies.size());
        checkEmpty();
      }

      @Override
      public void onFailure(Call<MovieResponse> call, Throwable throwable) {
        Log.e(TAG, throwable.toString());
        Snackbar snackbar = Snackbar.make(recyclerSimilar, getString(R.string.unable_connect), Snackbar.LENGTH_INDEFINITE)
          .setAction(getString(R.string.retry), new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              connectAndGetApiData(page);
            }
          });
        snackbar.show();
        checkEmpty();
      }
    });
  }


}

package de.kaiwidmaier.suggestamovie.activities.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import de.kaiwidmaier.suggestamovie.R;
import de.kaiwidmaier.suggestamovie.adapters.RecyclerThumbnailAdapter;
import de.kaiwidmaier.suggestamovie.adapters.utils.AdapterUtils;
import de.kaiwidmaier.suggestamovie.data.DataHelper;
import de.kaiwidmaier.suggestamovie.data.Movie;
import de.kaiwidmaier.suggestamovie.data.MovieResponse;
import de.kaiwidmaier.suggestamovie.rest.MovieApiService;
import de.kaiwidmaier.suggestamovie.utils.LocalizationUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static de.kaiwidmaier.suggestamovie.activities.MainActivity.BASE_URL;
import static de.kaiwidmaier.suggestamovie.data.DataHelper.API_KEY;

public class RecommendationsFragment extends Fragment {


  private static final String TAG = NowPlayingFragment.class.getSimpleName();
  private RecyclerView recycler;
  private RecyclerThumbnailAdapter movieAdapter;
  private Retrofit retrofit;
  private Snackbar connectionFailedSnackbar;
  private ProgressBar progressBar;
  private ArrayList<Movie> watchlist;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    View result = inflater.inflate(R.layout.fragment_recommendations, container, false);

    TextView title = result.findViewById(R.id.text_recommend);
    TextView descr = result.findViewById(R.id.text_recommend_descr);
    progressBar = result.findViewById(R.id.progress);
    recycler = result.findViewById(R.id.recycler_recommend);
    LinearLayout watchlistEmpty = result.findViewById(R.id.watchlist_empty);

    watchlist = ((DataHelper) getActivity().getApplication()).getWatchlist();

    //Don't load recommendations if watchlist is empty
    if(watchlist == null || watchlist.size() == 0){
      watchlistEmpty.setVisibility(View.VISIBLE);
      progressBar.setVisibility(View.GONE);
      return result;
    }

    Movie randomMovie = getRandomMovie();
    int movieId = randomMovie.getId();
    title.setText(getString(R.string.recommendations));
    descr.setText(String.format(getString(R.string.recommendations_descr), randomMovie.getTitle()));

    recycler.setLayoutManager(new GridLayoutManager(getActivity(), AdapterUtils.calculateNumberOfColumns(getActivity())));
    connectAndGetApiData(movieId);
    return result;
  }

  public void connectAndGetApiData(final int movieId) {

    if (retrofit == null) {
      retrofit = new Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
    }

    MovieApiService movieApiService = retrofit.create(MovieApiService.class);

    Call<MovieResponse> call = movieApiService.getRecommendedMovies(movieId, API_KEY, LocalizationUtils.getLanguage(), LocalizationUtils.getCountry());

    call.enqueue(new Callback<MovieResponse>() {
      @Override
      public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {
        List<Movie> movies = response.body().getResults();
        if(!RecommendationsFragment.this.isVisible() || movies == null || movies.size() == 0){
          return;
        }
        progressBar.setVisibility(View.GONE);
        if(movieAdapter == null){
          movieAdapter = new RecyclerThumbnailAdapter(getActivity(), movies);
          recycler.setAdapter(movieAdapter);
        }
        else{
          Parcelable recyclerViewState;
          recyclerViewState = recycler.getLayoutManager().onSaveInstanceState();

          //Prevents duplicates with bad connection
          if(!movieAdapter.containsAll(movies)){
            movieAdapter.addAll(movies);
          }
          recycler.getLayoutManager().onRestoreInstanceState(recyclerViewState); //Restores scroll position after notifyDataSetChanged()
        }
        movieAdapter.setLoading(false);
        Log.d(TAG, "Request URL: " + response.raw().request().url());
        Log.d(TAG, "Current Page: " + response.body().getPage());
        Log.d(TAG, "Number of movies received: " + movies.size());
      }

      @Override
      public void onFailure(Call<MovieResponse> call, Throwable throwable) {
        if(!RecommendationsFragment.this.isVisible()){
          return;
        }
        progressBar.setVisibility(View.GONE);
        Log.e(TAG, throwable.toString());
        connectionFailedSnackbar = Snackbar.make(recycler, getString(R.string.unable_connect), Snackbar.LENGTH_INDEFINITE)
          .setAction(getString(R.string.retry), new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              connectAndGetApiData(movieId);
              progressBar.setVisibility(View.VISIBLE);
            }
          });
        connectionFailedSnackbar.show();
      }
    });
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    Log.d(TAG, "Fragment Destroied");
    if(connectionFailedSnackbar != null && connectionFailedSnackbar.isShown()){
      connectionFailedSnackbar.dismiss();
    }
  }

  public Movie getRandomMovie()
  {
    Random random = new Random();
    int index = random.nextInt(watchlist.size());
    return watchlist.get(index);
  }

}

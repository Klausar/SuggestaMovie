package de.kaiwidmaier.suggestamovie.activities.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import java.util.List;

import de.kaiwidmaier.suggestamovie.R;
import de.kaiwidmaier.suggestamovie.adapters.RecyclerThumbnailAdapter;
import de.kaiwidmaier.suggestamovie.adapters.utils.AdapterUtils;
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

public class NowPlayingFragment extends Fragment {

  private static final String TAG = NowPlayingFragment.class.getSimpleName();
  private RecyclerView recycler;
  private RecyclerThumbnailAdapter movieAdapter;
  private Retrofit retrofit;
  private Snackbar connectionFailedSnackbar;
  private ProgressBar progressBar;
  private int page;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    View result = inflater.inflate(R.layout.fragment_now_playing, container, false);

    page = 1;
    progressBar = result.findViewById(R.id.progress);
    recycler = result.findViewById(R.id.recycler_now_playing);
    recycler.setLayoutManager(new GridLayoutManager(getActivity(), AdapterUtils.calculateNumberOfColumns(getActivity())));
    recycler.addOnScrollListener(new RecyclerView.OnScrollListener() {

      private int visibleThreshold = 5;
      int firstVisibleItem;
      int visibleItemCount;
      int totalItemCount;

      @Override
      public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        visibleItemCount = recycler.getChildCount();
        totalItemCount = recycler.getLayoutManager().getItemCount();
        firstVisibleItem = ((LinearLayoutManager) recycler.getLayoutManager()).findFirstVisibleItemPosition();

        if (!movieAdapter.isLoading() && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) {
          //End has been reached, load more
          connectAndGetApiData(page);
          movieAdapter.setLoading(true);
        }
      }
    });
    connectAndGetApiData(page);
    return result;
  }

  public void connectAndGetApiData(final int page) {

    if (retrofit == null) {
      retrofit = new Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
    }

    MovieApiService movieApiService = retrofit.create(MovieApiService.class);

    Call<MovieResponse> call = movieApiService.getNowPlayingMovies(API_KEY, LocalizationUtils.getLanguage(), LocalizationUtils.getCountry(), page);

    call.enqueue(new Callback<MovieResponse>() {
      @Override
      public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {
        List<Movie> movies = response.body().getResults();
        if(!NowPlayingFragment.this.isVisible() || movies == null || movies.size() == 0){
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
        NowPlayingFragment.this.page++;
        movieAdapter.setLoading(false);
        Log.d(TAG, "Request URL: " + response.raw().request().url());
        Log.d(TAG, "Current Page: " + response.body().getPage());
        Log.d(TAG, "Number of movies received: " + movies.size());
      }

      @Override
      public void onFailure(Call<MovieResponse> call, Throwable throwable) {
        if(!NowPlayingFragment.this.isVisible()){
          return;
        }
        progressBar.setVisibility(View.GONE);
        Log.e(TAG, throwable.toString());
        connectionFailedSnackbar = Snackbar.make(recycler, getString(R.string.unable_connect), Snackbar.LENGTH_INDEFINITE)
          .setAction(getString(R.string.retry), new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              connectAndGetApiData(page);
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
}

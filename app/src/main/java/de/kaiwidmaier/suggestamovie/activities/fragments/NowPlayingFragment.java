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
import de.kaiwidmaier.suggestamovie.activities.interfaces.EndlessAPILoader;
import de.kaiwidmaier.suggestamovie.adapters.RecyclerThumbnailAdapter;
import de.kaiwidmaier.suggestamovie.adapters.utils.AdapterUtils;
import de.kaiwidmaier.suggestamovie.data.Movie;
import de.kaiwidmaier.suggestamovie.data.MovieResponse;
import de.kaiwidmaier.suggestamovie.rest.MovieApiService;
import de.kaiwidmaier.suggestamovie.utils.LocalizationUtils;
import de.kaiwidmaier.suggestamovie.views.EndlessRecyclerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static de.kaiwidmaier.suggestamovie.activities.MainActivity.BASE_URL;
import static de.kaiwidmaier.suggestamovie.data.DataHelper.API_KEY;

public class NowPlayingFragment extends Fragment implements EndlessAPILoader{

  private static final String TAG = NowPlayingFragment.class.getSimpleName();
  private EndlessRecyclerView recyclerView;
  private RecyclerThumbnailAdapter movieAdapter;
  private Retrofit retrofit;
  private Snackbar connectionFailedSnackbar;
  private ProgressBar progressBar;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setHasOptionsMenu(true);
    View result = inflater.inflate(R.layout.fragment_now_playing, container, false);

    progressBar = result.findViewById(R.id.progress);
    recyclerView = result.findViewById(R.id.recycler_now_playing);
    recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), AdapterUtils.calculateNumberOfColumns(getActivity())));

    /*
     * Scrolllistener has to be implemented seperately inside Fragments,
     * because "context" inside EndlessRecyclerView references the activity,
     * not the fragment
     */
    recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
      @Override
      public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        int visibleThreshold = 5;
        int visibleItemCount = recyclerView.getChildCount();
        int totalItemCount = recyclerView.getLayoutManager().getItemCount();
        int firstVisibleItem = ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPosition();

        if (!NowPlayingFragment.this.recyclerView.isLoading() && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) {
          //End has been reached, load more
          connectAndGetApiData(NowPlayingFragment.this.recyclerView.getPage());
          NowPlayingFragment.this.recyclerView.setLoading(true);
        }
      }
    });

    connectAndGetApiData(recyclerView.getPage());
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
          recyclerView.setAdapter(movieAdapter);
        }
        else{
          Parcelable recyclerViewState;
          recyclerViewState = recyclerView.getLayoutManager().onSaveInstanceState();

          //Prevents duplicates with bad connection
          if(!movieAdapter.containsAll(movies)){
            movieAdapter.addAll(movies);
          }
          recyclerView.getLayoutManager().onRestoreInstanceState(recyclerViewState); //Restores scroll position after notifyDataSetChanged()
        }
        recyclerView.setPage(recyclerView.getPage() + 1);
        recyclerView.setLoading(false);
      }

      @Override
      public void onFailure(Call<MovieResponse> call, Throwable throwable) {
        if(!NowPlayingFragment.this.isVisible()){
          return;
        }
        progressBar.setVisibility(View.GONE);
        connectionFailedSnackbar = Snackbar.make(recyclerView, getString(R.string.unable_connect), Snackbar.LENGTH_INDEFINITE)
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
    if(connectionFailedSnackbar != null && connectionFailedSnackbar.isShown()){
      connectionFailedSnackbar.dismiss();
    }
  }
}

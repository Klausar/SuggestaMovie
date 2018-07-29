package de.kaiwidmaier.suggestamovie.activities.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
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
  private Spinner spinner;
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
    spinner = result.findViewById(R.id.spinner_movies);

    watchlist = ((DataHelper) getActivity().getApplication()).getWatchlist();

    //Don't load recommendations if watchlist is empty
    if(watchlist == null || watchlist.size() == 0){
      watchlistEmpty.setVisibility(View.VISIBLE);
      spinner.setVisibility(View.GONE);
      progressBar.setVisibility(View.GONE);
      return result;
    }

    ArrayAdapter<Movie> adapter = new ArrayAdapter<>(getActivity(), R.layout.support_simple_spinner_dropdown_item, watchlist);
    adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
    spinner.setAdapter(adapter);
    spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
      @Override
      public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        connectAndGetApiData(((Movie) spinner.getSelectedItem()).getId());
      }

      @Override
      public void onNothingSelected(AdapterView<?> parent) {

      }
    });

    title.setText(getString(R.string.recommendations));
    descr.setText(getString(R.string.recommendations_descr));

    recycler.setLayoutManager(new GridLayoutManager(getActivity(), AdapterUtils.calculateNumberOfColumns(getActivity())));
    connectAndGetApiData(getRandomMovie().getId());
    return result;
  }

  public void connectAndGetApiData(final int movieId) {
    progressBar.setVisibility(View.VISIBLE);
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
        movieAdapter = new RecyclerThumbnailAdapter(getActivity(), movies);
        recycler.setAdapter(movieAdapter);
      }

      @Override
      public void onFailure(Call<MovieResponse> call, Throwable throwable) {
        if(!RecommendationsFragment.this.isVisible()){
          return;
        }
        progressBar.setVisibility(View.GONE);
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
    if(connectionFailedSnackbar != null && connectionFailedSnackbar.isShown()){
      connectionFailedSnackbar.dismiss();
    }
  }

  public Movie getRandomMovie()
  {
    Random random = new Random();
    int index = random.nextInt(watchlist.size());
    spinner.setSelection(index);
    return watchlist.get(index);
  }

}

package de.kaiwidmaier.suggestamovie.activities.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import de.kaiwidmaier.suggestamovie.R;
import de.kaiwidmaier.suggestamovie.adapters.RecyclerViewGenreAdapter;
import de.kaiwidmaier.suggestamovie.data.Genre;
import de.kaiwidmaier.suggestamovie.data.GenreResponse;
import de.kaiwidmaier.suggestamovie.persistence.Serializer;
import de.kaiwidmaier.suggestamovie.rest.GenreApiService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static de.kaiwidmaier.suggestamovie.activities.MainActivity.BASE_URL;
import static de.kaiwidmaier.suggestamovie.data.DataHelper.API_KEY;

/**
 * Created by Kai on 18.03.2018.
 */

public class GenreFragment extends Fragment {

  private static final String KEY_POSITION="position";
  private RecyclerView recyclerGenres;
  private RecyclerViewGenreAdapter genreAdapter;
  private static final String TAG = GenreFragment.class.getSimpleName();
  private Retrofit retrofit;

  public static GenreFragment newInstance(int position) {
    GenreFragment fragment = new GenreFragment();
    Bundle args = new Bundle();

    args.putInt(KEY_POSITION, position);
    fragment.setArguments(args);

    return(fragment);
  }

  public static String getTitle(Context context, int position) {
    return(String.format(context.getString(R.string.hint), position + 1));
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View result=inflater.inflate(R.layout.fragment_genre, container, false);
    recyclerGenres = result.findViewById(R.id.recycler_genres);
    recyclerGenres.setLayoutManager(new GridLayoutManager(getActivity(), 3));
    recyclerGenres.setNestedScrollingEnabled(false);
    setupGenres();

    return result;
  }

  private void setupGenres(){
    Serializer serializer = new Serializer(getActivity());
    ArrayList<Genre> genres = serializer.readGenres();
    //Get genres from API and save them to file if not yet saved to file
    //else use existing file
    if(genres.size() <= 0){
      connectAndGetApiData();
    }
    else{
      genreAdapter = new RecyclerViewGenreAdapter(getActivity(), genres);
      recyclerGenres.setAdapter(genreAdapter);
    }
  }

  public String getIncludedGenres(){
    return android.text.TextUtils.join("|", genreAdapter.getSelectedGenresIds());
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
        Serializer serializer = new Serializer(GenreFragment.this.getActivity());
        serializer.writeGenres((ArrayList<Genre>) genres);

        genreAdapter = new RecyclerViewGenreAdapter(GenreFragment.this.getActivity(), genres);
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

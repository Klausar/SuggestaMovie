package de.kaiwidmaier.suggestamovie.rest;

import de.kaiwidmaier.suggestamovie.data.GenreResponse;
import de.kaiwidmaier.suggestamovie.data.MovieResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Kai on 16.03.2018.
 */

public interface GenreApiService {

  @GET("genre/movie/list")
  Call<GenreResponse> getGenres(@Query("api_key") String apiKey, @Query("language") String language);

}

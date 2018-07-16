package de.kaiwidmaier.suggestamovie.rest;

import de.kaiwidmaier.suggestamovie.data.Movie;
import de.kaiwidmaier.suggestamovie.data.MovieDetail;
import de.kaiwidmaier.suggestamovie.data.MovieResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by Kai on 04.03.2018.
 */

public interface MovieApiService {

    @GET("movie/top_rated")
    Call<MovieResponse> getTopRatedMovies(@Query("api_key") String apiKey);

    @GET("movie/{id}/recommendations")
    Call<MovieResponse> getRecommendedMovies(@Path("id") int id, @Query("api_key") String apiKey, @Query("language") String language, @Query("region") String region);

    @GET("movie/{id}?append_to_response=videos,credits")
    Call<MovieDetail> getMovieDetails(@Path("id") int id, @Query("api_key") String apiKey, @Query("language") String language, @Query("region") String region);

    @GET("movie/{id}")
    Call<Movie> getMovieDetailsAsMovie(@Path("id") int id, @Query("api_key") String apiKey, @Query("language") String language, @Query("region") String region);

    @GET("movie/upcoming")
    Call<MovieResponse> getUpcomingMovies(@Query("api_key") String apiKey, @Query("language") String language, @Query("region") String region);

    @GET("movie/now_playing")
    Call<MovieResponse> getNowPlayingMovies(@Query("api_key") String apiKey, @Query("language") String language, @Query("region") String region, @Query("page") int page);

    @GET("discover/movie")
    Call<MovieResponse> getMovie(@Query("api_key") String apiKey, @Query("language") String language, @Query("region") String region, @Query("sort_by") String sortBy,
                                 @Query("include_adult") boolean adult, @Query("primary_release_date.gte") String releaseDateMin, @Query("primary_release_date.lte") String releaseDateMax,
                                 @Query("vote_average.gte") int ratingMin, @Query("vote_average.lte") int ratingMax, @Query("with_genres") String includedGenres,
                                 @Query("without_genres") String excludedGenres, @Query("page") int page);

    @GET("movie/{id}/similar")
    Call<MovieResponse> getSimilarMovies(@Path("id") int id, @Query("api_key") String apiKey, @Query("language") String language, @Query("region") String region, @Query("page") int page);

    @GET("search/movie")
    Call<MovieResponse> getMoviesByName(@Query("api_key") String apiKey, @Query("language") String language, @Query("region") String region, @Query("query") String query, @Query("page") int page);
}

package de.kaiwidmaier.suggestamovie.data;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Kai on 16.03.2018.
 */

public class GenreResponse {

  @SerializedName("genres")
  public List<Genre> genres;

  public List<Genre> getGenres(){
    return genres;
  }

}

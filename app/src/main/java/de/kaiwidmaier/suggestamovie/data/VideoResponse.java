package de.kaiwidmaier.suggestamovie.data;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/*
 * Used to hold videos of movie
 */
public class VideoResponse {

  @SerializedName("results")
  private ArrayList<Video> videos;

  public ArrayList<Video> getVideos() {
    return videos;
  }
}

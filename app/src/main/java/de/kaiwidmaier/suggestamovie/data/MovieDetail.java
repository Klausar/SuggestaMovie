package de.kaiwidmaier.suggestamovie.data;

import android.os.Parcel;

import com.google.gson.annotations.SerializedName;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

public class MovieDetail extends Movie {

  @SerializedName("budget")
  private int budget;
  @SerializedName("genres")
  private ArrayList<Genre> genres;
  @SerializedName("homepage")
  private String homepage;
  @SerializedName("revenue")
  private int revenue;
  @SerializedName("runtime")
  private int runtime;
  @SerializedName("videos")
  private VideoResponse videoResponse;
  @SerializedName("credits")
  private CreditResponse creditResponse;

  protected MovieDetail(Parcel in) {
    super(in);
  }

  public ArrayList<Video> getVideos(){
    return videoResponse == null ? null : videoResponse.getVideos();
  }

  public ArrayList<Actor> getCast(){
    return creditResponse == null ? null : creditResponse.getCast();
  }

  public ArrayList<CrewMember> getCrew(){
    return creditResponse == null ? null : creditResponse.getCrew();
  }

  public int getRuntime() {
    return runtime;
  }

  public void setRuntime(int runtime) {
    this.runtime = runtime;
  }

  public int getBudget() {
    return budget;
  }

  public void setBudget(int budget) {
    this.budget = budget;
  }

  public String getBudgetFormatted(){
    NumberFormat format = NumberFormat.getInstance(Locale.getDefault());
    return format.format(getBudget()) + "$";
  }

  public String getRevenueFormatted(){
    NumberFormat format = NumberFormat.getInstance(Locale.getDefault());
    return format.format(getRevenue()) + "$";
  }

  public ArrayList<Genre> getGenres() {
    return genres;
  }

  public void setGenres(ArrayList<Genre> genres) {
    this.genres = genres;
  }

  public String getHomepage() {
    return homepage;
  }

  public void setHomepage(String homepage) {
    this.homepage = homepage;
  }

  public int getRevenue() {
    return revenue;
  }

  public void setRevenue(int revenue) {
    this.revenue = revenue;
  }
}

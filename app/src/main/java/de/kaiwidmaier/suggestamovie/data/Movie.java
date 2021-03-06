package de.kaiwidmaier.suggestamovie.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Parcel;
import android.os.Parcelable;
import android.preference.PreferenceManager;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by Kai on 04.03.2018.
 */

@SuppressWarnings("unused")
public class Movie implements Parcelable, Serializable{

  private static final long serialVersionUID = -3321935085974625347L;

  @SerializedName("poster_path")
  private String posterPath;
  @SerializedName("adult")
  private boolean adult;
  @SerializedName("overview")
  private String overview;
  @SerializedName("release_date")
  private String releaseDate;
  @SerializedName("genre_ids")
  private List<Integer> genreIds;
  @SerializedName("id")
  private Integer id;
  @SerializedName("original_title")
  private String originalTitle;
  @SerializedName("original_language")
  private String originalLanguage;
  @SerializedName("title")
  private String title;
  @SerializedName("backdrop_path")
  private String backdropPath;
  @SerializedName("popularity")
  private Double popularity;
  @SerializedName("vote_count")
  private Integer voteCount;
  @SerializedName("video")
  private Boolean video;
  @SerializedName("vote_average")
  private Double voteAverage;

  public String getPosterPath() {
    return posterPath;
  }

  public void setPosterPath(String posterPath) {
    this.posterPath = posterPath;
  }

  public boolean isAdult() {
    return adult;
  }

  public void setAdult(boolean adult) {
    this.adult = adult;
  }

  public String getOverview() {
    return overview;
  }

  public void setOverview(String overview) {
    this.overview = overview;
  }

  public String getReleaseDate() {
    return releaseDate;
  }

  public void setReleaseDate(String releaseDate) {
    this.releaseDate = releaseDate;
  }

  public List<Integer> getGenreIds() {
    return genreIds;
  }

  public void setGenreIds(List<Integer> genreIds) {
    this.genreIds = genreIds;
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getOriginalTitle() {
    return originalTitle;
  }

  public void setOriginalTitle(String originalTitle) {
    this.originalTitle = originalTitle;
  }

  public String getOriginalLanguage() {
    return originalLanguage;
  }

  public void setOriginalLanguage(String originalLanguage) {
    this.originalLanguage = originalLanguage;
  }

  public String getTitle(Context context) {
    SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
    boolean showOriginal = sharedPref.getBoolean("original_title", false);
    if(showOriginal){
      return originalTitle;
    }
    else{
      return title;
    }
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getBackdropPath() {
    return backdropPath;
  }

  public void setBackdropPath(String backdropPath) {
    this.backdropPath = backdropPath;
  }

  public Double getPopularity() {
    return popularity;
  }

  public void setPopularity(Double popularity) {
    this.popularity = popularity;
  }

  public Integer getVoteCount() {
    return voteCount;
  }

  public void setVoteCount(Integer voteCount) {
    this.voteCount = voteCount;
  }

  public Boolean getVideo() {
    return video;
  }

  public void setVideo(Boolean video) {
    this.video = video;
  }

  public Double getVoteAverage() {
    return voteAverage;
  }

  public void setVoteAverage(Double voteAverage) {
    this.voteAverage = voteAverage;
  }

  protected Movie(Parcel in) {
    posterPath = in.readString();
    adult = in.readByte() != 0x00;
    overview = in.readString();
    releaseDate = in.readString();
    if (in.readByte() == 0x01) {
      genreIds = new ArrayList<>();
      in.readList(genreIds, Integer.class.getClassLoader());
    } else {
      genreIds = null;
    }
    id = in.readByte() == 0x00 ? null : in.readInt();
    originalTitle = in.readString();
    originalLanguage = in.readString();
    title = in.readString();
    backdropPath = in.readString();
    popularity = in.readByte() == 0x00 ? null : in.readDouble();
    voteCount = in.readByte() == 0x00 ? null : in.readInt();
    byte videoVal = in.readByte();
    video = videoVal == 0x02 ? null : videoVal != 0x00;
    voteAverage = in.readByte() == 0x00 ? null : in.readDouble();
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(posterPath);
    dest.writeByte((byte) (adult ? 0x01 : 0x00));
    dest.writeString(overview);
    dest.writeString(releaseDate);
    if (genreIds == null) {
      dest.writeByte((byte) (0x00));
    } else {
      dest.writeByte((byte) (0x01));
      dest.writeList(genreIds);
    }
    if (id == null) {
      dest.writeByte((byte) (0x00));
    } else {
      dest.writeByte((byte) (0x01));
      dest.writeInt(id);
    }
    dest.writeString(originalTitle);
    dest.writeString(originalLanguage);
    dest.writeString(title);
    dest.writeString(backdropPath);
    if (popularity == null) {
      dest.writeByte((byte) (0x00));
    } else {
      dest.writeByte((byte) (0x01));
      dest.writeDouble(popularity);
    }
    if (voteCount == null) {
      dest.writeByte((byte) (0x00));
    } else {
      dest.writeByte((byte) (0x01));
      dest.writeInt(voteCount);
    }
    if (video == null) {
      dest.writeByte((byte) (0x02));
    } else {
      dest.writeByte((byte) (video ? 0x01 : 0x00));
    }
    if (voteAverage == null) {
      dest.writeByte((byte) (0x00));
    } else {
      dest.writeByte((byte) (0x01));
      dest.writeDouble(voteAverage);
    }
  }

  //Because data changes, equal movies have to be determined by id & title
  @Override
  public boolean equals(final Object o){
    if(this == o) return true;
    if(o == null || !(o instanceof Movie)) return false;
    Movie movie = (Movie) o;
    return movie.getId().equals(id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(title, id);
  }

  @Override
  public String toString() {
    return title;
  }

  @SuppressWarnings("unused")
  public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {
    @Override
    public Movie createFromParcel(Parcel in) {
      return new Movie(in);
    }

    @Override
    public Movie[] newArray(int size) {
      return new Movie[size];
    }
  };
}

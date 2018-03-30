package de.kaiwidmaier.suggestamovie.data;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import de.kaiwidmaier.suggestamovie.R;

/**
 * Created by Kai on 16.03.2018.
 */

public class Genre implements Parcelable, Serializable{


  private static final long serialVersionUID = 8614403915122234634L;

  private String name;
  private int id;
  private GenreSelection selection;

  public Genre(String name, int id) {
    this.name = name;
    this.id = id;
  }

  public void setSelection(GenreSelection selection){
    this.selection = selection;
  }

  public GenreSelection getSelection(){
    return selection;
  }

  public void toggleSelection(){
    switch(selection){
      case INCLUDED:
        selection = GenreSelection.NEUTRAL;
        break;
      case NEUTRAL:
        selection = GenreSelection.EXCLUDED;
        break;
      case EXCLUDED:
        selection = GenreSelection.INCLUDED;
        break;
    }
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public int getDrawableResId(){
    int drawableResId = R.drawable.ic_genre_action;
    switch(id){
      case 28:
        drawableResId = R.drawable.ic_genre_action;
        break;
      case 12:
        drawableResId = R.drawable.ic_genre_adventure;
        break;
      case 16:
        drawableResId = R.drawable.ic_genre_animation;
        break;
      case 35:
        drawableResId = R.drawable.ic_genre_comedy;
        break;
      case 80:
        drawableResId = R.drawable.ic_genre_crime;
        break;
      case 99:
        drawableResId = R.drawable.ic_genre_documentary;
        break;
      case 18:
        drawableResId = R.drawable.ic_genre_drama;
        break;
      case 10751:
        drawableResId = R.drawable.ic_genre_family;
        break;
      case 14:
        drawableResId = R.drawable.ic_genre_fantasy;
        break;
      case 36:
        drawableResId = R.drawable.ic_genre_history;
        break;
      case 27:
        drawableResId = R.drawable.ic_genre_horror;
        break;
      case 10402:
        drawableResId = R.drawable.ic_genre_music;
        break;
      case 9648:
        drawableResId = R.drawable.ic_genre_mystery;
        break;
      case 10749:
        drawableResId = R.drawable.ic_genre_romance;
        break;
      case 878:
        drawableResId = R.drawable.ic_genre_scifi;
        break;
      case 10770:
        drawableResId = R.drawable.ic_genre_tv_movie;
        break;
      case 53:
        drawableResId = R.drawable.ic_genre_thriller;
        break;
      case 10752:
        drawableResId = R.drawable.ic_genre_war;
        break;
      case 37:
        drawableResId = R.drawable.ic_genre_western;
        break;
      default:
        return drawableResId;
    }
    return drawableResId;
  }


  private Genre(Parcel in) {
    name = in.readString();
    id = in.readInt();
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(name);
    dest.writeInt(id);
  }

  @SuppressWarnings("unused")
  public static final Parcelable.Creator<Genre> CREATOR = new Parcelable.Creator<Genre>() {
    @Override
    public Genre createFromParcel(Parcel in) {
      return new Genre(in);
    }

    @Override
    public Genre[] newArray(int size) {
      return new Genre[size];
    }
  };
}

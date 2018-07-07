package de.kaiwidmaier.suggestamovie.data;

import android.os.Parcel;
import android.support.annotation.DrawableRes;

/**
 * Created by Kai on 16.03.2018.
 */

public class Genre{

  private static final long serialVersionUID = 8614403915122234634L;

  private String name;
  private int id;
  @DrawableRes
  private int drawableResId;
  private GenreSelection selection;

  public Genre(String name, int id, @DrawableRes int drawableResId) {
    this.name = name;
    this.id = id;
    this.drawableResId = drawableResId;
  }

  public void setSelection(GenreSelection selection) {
    this.selection = selection;
  }

  public GenreSelection getSelection() {
    return selection;
  }

  public void toggleSelection() {
    switch (selection) {
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

  public int getDrawableResId() {
    return drawableResId;
  }

  private Genre(Parcel in) {
    name = in.readString();
    id = in.readInt();
  }
}

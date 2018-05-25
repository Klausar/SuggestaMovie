package de.kaiwidmaier.suggestamovie.rest;

import android.content.Context;

import java.util.ArrayList;

import de.kaiwidmaier.suggestamovie.R;

public enum ResultType {
  FILTER,
  SIMILAR,
  SEARCH;

  public static class SortType{

    private String displayText;
    private String value;
    private static ArrayList<SortType> sortTypes;

    private SortType(String displayText, String value){
      this.displayText = displayText;
      this.value = value;
    }

    public static ArrayList<SortType> getSortTypes(Context context){
      sortTypes = new ArrayList<>();
      sortTypes.add(new SortType(context.getString(R.string.sort_popularity), "popularity.desc"));
      sortTypes.add(new SortType(context.getString(R.string.sort_release_date), "release_date.desc"));
      sortTypes.add(new SortType(context.getString(R.string.sort_vote_average), "vote_average.desc"));
      return sortTypes;
    }

    @Override
    public String toString() {
      return displayText;
    }

    public String getValue(){
      return value;
    }
  }
}

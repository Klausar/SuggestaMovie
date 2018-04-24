package de.kaiwidmaier.suggestamovie.adapters.utils;

import android.content.Context;
import android.util.DisplayMetrics;

public class AdapterUtils {

  public static int calculateNumberOfColumns(Context context){
    DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
    float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
    return (int) (dpWidth / 140);
  }

}

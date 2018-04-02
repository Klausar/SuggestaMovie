package de.kaiwidmaier.suggestamovie.utils;

import java.util.Locale;

public class LocalizationUtils {

  public static String getLanguage(){
    return Locale.getDefault().getLanguage();
  }

  public static String getCountry(){
    return Locale.getDefault().getCountry();
  }

}

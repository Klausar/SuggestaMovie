package de.kaiwidmaier.suggestamovie.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;

public class LocalizationUtils {

  public static String getLanguage(){
    return Locale.getDefault().getLanguage();
  }

  public static String getCountry(){
    return Locale.getDefault().getCountry();
  }

  public static String getLocalDateFormat(String strDate, Context context) {
    SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
    String format = sharedPref.getString("date_format", "medium");
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    Date date = null;
    try {
      date = sdf.parse(strDate);
    } catch (ParseException e) {
      e.printStackTrace();
    }
    java.text.DateFormat dateFormat;
    switch(format){
      case "short":
        dateFormat = android.text.format.DateFormat.getDateFormat(context);
        break;
      case "medium":
        dateFormat = android.text.format.DateFormat.getMediumDateFormat(context);
        break;
      case "long":
        dateFormat = android.text.format.DateFormat.getLongDateFormat(context);
        break;
      default:
        dateFormat = android.text.format.DateFormat.getMediumDateFormat(context);
        break;
    }
    return dateFormat.format(date);
  }

}

package de.kaiwidmaier.suggestamovie.utils;

import android.content.Context;

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
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    Date date = null;
    try {
      date = sdf.parse(strDate);
    } catch (ParseException e) {
      e.printStackTrace();
    }
    java.text.DateFormat dateFormat = android.text.format.DateFormat.getLongDateFormat(context);
    return dateFormat.format(date);
  }

}

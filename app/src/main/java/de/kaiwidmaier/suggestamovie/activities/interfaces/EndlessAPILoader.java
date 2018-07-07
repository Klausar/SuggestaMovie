package de.kaiwidmaier.suggestamovie.activities.interfaces;

public interface EndlessAPILoader {

  /*
   * Loads API Data, increments page and puts data into recyclerview
   */
  void connectAndGetApiData(final int page);

}

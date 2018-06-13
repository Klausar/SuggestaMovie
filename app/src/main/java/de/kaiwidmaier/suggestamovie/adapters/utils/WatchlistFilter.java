package de.kaiwidmaier.suggestamovie.adapters.utils;

import android.content.Context;
import android.widget.Filter;

import java.util.ArrayList;

import de.kaiwidmaier.suggestamovie.adapters.RecyclerMovieAdapter;
import de.kaiwidmaier.suggestamovie.data.Movie;

public class WatchlistFilter extends Filter {

  private RecyclerMovieAdapter adapter;
  private ArrayList<Movie> filterList;
  private Context context;

  public WatchlistFilter(ArrayList<Movie> filterList, RecyclerMovieAdapter adapter, Context context){
    this.filterList = filterList;
    this.adapter = adapter;
    this.context = context;
  }

  @Override
  protected FilterResults performFiltering(CharSequence constraint) {
    FilterResults results = new FilterResults();

    if(constraint != null){
      constraint = constraint.toString().toUpperCase();
      ArrayList<Movie> filteredMovies = new ArrayList<>();
      for (int i=0;i<filterList.size();i++) {
        if(filterList.get(i).getTitle(context).toUpperCase().contains(constraint))
        {
          filteredMovies.add(filterList.get(i));
        }
      }

      results.count = filteredMovies.size();
      results.values = filteredMovies;

    }
    else{
      results.count = filterList.size();
      results.values = filterList;
    }

    return results;
  }

  @Override
  protected void publishResults(CharSequence constraint, FilterResults results) {

    adapter.setMovies((ArrayList<Movie>) results.values);
    adapter.notifyDataSetChanged();

  }
}

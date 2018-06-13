package de.kaiwidmaier.suggestamovie.activities.fragments;

import android.app.Fragment;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import java.util.ArrayList;

import de.kaiwidmaier.suggestamovie.R;
import de.kaiwidmaier.suggestamovie.activities.FilterActivity;
import de.kaiwidmaier.suggestamovie.activities.MainActivity;
import de.kaiwidmaier.suggestamovie.activities.SearchActivity;
import de.kaiwidmaier.suggestamovie.adapters.RecyclerMovieAdapter;
import de.kaiwidmaier.suggestamovie.adapters.utils.SimpleItemTouchHelperCallback;
import de.kaiwidmaier.suggestamovie.data.DataHelper;
import de.kaiwidmaier.suggestamovie.data.Movie;

public class WatchlistFragment extends Fragment implements SearchView.OnQueryTextListener{

  private RecyclerMovieAdapter movieAdapter;
  private ArrayList<Movie> watchlist;
  private LinearLayout layoutWatchlistEmpty;
  private RecyclerView recyclerWatchlist;
  private SimpleItemTouchHelperCallback callback;


  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    View result=inflater.inflate(R.layout.fragment_watchlist, container, false);


    layoutWatchlistEmpty = result.findViewById(R.id.layout_watchlist_empty);
    recyclerWatchlist = result.findViewById(R.id.recycler_watchlist);
    DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerWatchlist.getContext(), DividerItemDecoration.VERTICAL);
    recyclerWatchlist.addItemDecoration(dividerItemDecoration);

    watchlist = ((DataHelper) getActivity().getApplication()).getWatchlist();

    movieAdapter = new RecyclerMovieAdapter(getActivity(), watchlist, false);

    recyclerWatchlist.setAdapter(movieAdapter);
    checkEmpty();

    //Swipe to remove and long press to change position
    callback = new SimpleItemTouchHelperCallback(movieAdapter);
    ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
    touchHelper.attachToRecyclerView(recyclerWatchlist);

    //Make watchlist have it's own actionbar for search functionality
    return result;
  }

  @Override
  public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    setHasOptionsMenu(true);
  }

  private void checkEmpty(){
    if(watchlist.isEmpty()){
      layoutWatchlistEmpty.setVisibility(View.VISIBLE);
    }
    else{
      layoutWatchlistEmpty.setVisibility(View.GONE);
    }
  }

  @Override
  public void onResume() {
    if (movieAdapter != null) {
      movieAdapter.notifyDataSetChanged();
    }
    super.onResume();
    checkEmpty();
  }

  @Override
  public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    inflater.inflate(R.menu.watchlist_actionbar, menu);
    MenuItem searchItem;
    SearchView searchView;

    searchItem = menu.findItem(R.id.search);
    searchItem.setVisible(true);
    searchView = (SearchView) searchItem.getActionView();
    searchView.setOnQueryTextListener(this);
  }

  @Override
  public boolean onQueryTextSubmit(String query) {
    return false;
  }

  @Override
  public boolean onQueryTextChange(String newText) {
    if(newText.isEmpty()){
      movieAdapter.getFilter().filter("");
      callback.setDraggable(true);
    }
    else{
      movieAdapter.getFilter().filter(newText);
      callback.setDraggable(false); //Disable item drag in filtered list
    }
    return false;
  }
}

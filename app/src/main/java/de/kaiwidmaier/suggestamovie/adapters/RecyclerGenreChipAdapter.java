package de.kaiwidmaier.suggestamovie.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import de.kaiwidmaier.suggestamovie.R;
import de.kaiwidmaier.suggestamovie.data.Genre;
import de.kaiwidmaier.suggestamovie.data.GenreSelection;

public class RecyclerGenreChipAdapter extends RecyclerView.Adapter<RecyclerGenreChipAdapter.ViewHolder> {

  private static final String TAG = RecyclerGenreChipAdapter.class.getSimpleName();
  private List<Genre> genres;
  private LayoutInflater inflater;


  public RecyclerGenreChipAdapter(Context context, List<Genre> genres) {
    this.inflater = LayoutInflater.from(context);
    this.genres = genres;
  }

  @Override
  public RecyclerGenreChipAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = inflater.inflate(R.layout.genre_chip, parent, false);
    return new RecyclerGenreChipAdapter.ViewHolder(view);
  }

  @Override
  public void onBindViewHolder(final RecyclerGenreChipAdapter.ViewHolder holder, final int position) {
    final Genre genre = genres.get(position);
    holder.textName.setText(genre.getName());
  }

  @Override
  public int getItemCount() {
    return genres.size();
  }

  public class ViewHolder extends RecyclerView.ViewHolder{
    TextView textName;

    private ViewHolder(View itemView) {
      super(itemView);
      textName = itemView.findViewById(R.id.text_genre_chip);
    }
  }
}

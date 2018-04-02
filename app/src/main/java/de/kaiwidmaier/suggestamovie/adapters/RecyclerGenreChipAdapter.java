package de.kaiwidmaier.suggestamovie.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import de.kaiwidmaier.suggestamovie.R;
import de.kaiwidmaier.suggestamovie.data.Genre;

public class RecyclerGenreChipAdapter extends RecyclerView.Adapter<RecyclerGenreChipAdapter.ViewHolder> {

  private List<Genre> genres;
  private LayoutInflater inflater;

  public RecyclerGenreChipAdapter(Context context, List<Genre> genres) {
    this.inflater = LayoutInflater.from(context);
    this.genres = genres;
  }

  @NonNull
  @Override
  public RecyclerGenreChipAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View view = inflater.inflate(R.layout.genre_chip, parent, false);
    return new RecyclerGenreChipAdapter.ViewHolder(view);
  }

  @Override
  public void onBindViewHolder(@NonNull final RecyclerGenreChipAdapter.ViewHolder holder, int position) {
    final Genre genre = genres.get(position);
    if (genre != null) {
      holder.textName.setText(genre.getName());
    }
  }

  @Override
  public int getItemCount() {
    return genres.size();
  }

  class ViewHolder extends RecyclerView.ViewHolder {
    TextView textName;

    private ViewHolder(View itemView) {
      super(itemView);
      textName = itemView.findViewById(R.id.text_genre_chip);
    }
  }
}

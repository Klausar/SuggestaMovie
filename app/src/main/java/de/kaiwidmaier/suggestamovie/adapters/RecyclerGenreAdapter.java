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

/**
 * Created by Kai on 17.03.2018.
 */

public class RecyclerGenreAdapter extends RecyclerView.Adapter<RecyclerGenreAdapter.ViewHolder> {

  private static final String TAG = RecyclerGenreAdapter.class.getSimpleName();
  private List<Genre> genres;
  private LayoutInflater inflater;
  private RecyclerGenreAdapter.ItemClickListener clickListener;


  public RecyclerGenreAdapter(Context context, List<Genre> genres) {
    this.inflater = LayoutInflater.from(context);
    this.genres = genres;
  }

  @Override
  public RecyclerGenreAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = inflater.inflate(R.layout.recyclerview_genre_item, parent, false);
    return new RecyclerGenreAdapter.ViewHolder(view);
  }

  @Override
  public void onBindViewHolder(final RecyclerGenreAdapter.ViewHolder holder, final int position) {
    final Genre genre = genres.get(position);
    genre.setSelection(GenreSelection.INCLUDED);
    final int includedColor = Color.parseColor("#8DEF88");
    final int neutralColor = Color.parseColor("#00000000");
    final int excludedColor = Color.parseColor("#ff6856");
    holder.textName.setText(genre.getName());
    holder.imgIcon.setImageResource(genre.getDrawableResId());
    holder.layoutGenre.setBackgroundColor(includedColor);
    holder.layoutGenre.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        genre.toggleSelection();
        int color = includedColor;

        if(genre.getSelection() == GenreSelection.INCLUDED){
          holder.textName.setPaintFlags(holder.textName.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
          color = includedColor;
        }
        else if(genre.getSelection() == GenreSelection.NEUTRAL){
          holder.textName.setPaintFlags(holder.textName.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
          color = neutralColor;
        }
        else if(genre.getSelection() == GenreSelection.EXCLUDED){
          holder.textName.setPaintFlags(holder.textName.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
          color = excludedColor;
        }

        holder.layoutGenre.setBackgroundColor(color);
      }
    });
  }

  @Override
  public int getItemCount() {
    return genres.size();
  }

  public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    ImageView imgIcon;
    TextView textName;
    LinearLayout layoutGenre;

    private ViewHolder(View itemView) {
      super(itemView);
      imgIcon = itemView.findViewById(R.id.img_genre_icon);
      textName = itemView.findViewById(R.id.text_genre_name);
      layoutGenre = itemView.findViewById(R.id.layout_genre);
      itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
      if (clickListener != null) clickListener.onItemClick(view, getAdapterPosition());
    }
  }

  public Genre getItem(int position) {
    return genres.get(position);
  }

  public void setClickListener(RecyclerGenreAdapter.ItemClickListener itemClickListener) {
    this.clickListener = itemClickListener;
  }


  public interface ItemClickListener {
    void onItemClick(View view, int position);
  }

  public ArrayList<Genre> getSelectedGenres(){
    ArrayList<Genre> selectedGenres = new ArrayList<>();
    for(Genre genre : genres){
      if(genre.getSelection() == GenreSelection.INCLUDED) selectedGenres.add(genre);
    }
    return selectedGenres;
  }

  public ArrayList<Integer> getSelectedGenresIds(){
    ArrayList<Integer> ids = new ArrayList<>();
    for(Genre genre : genres){
      if(genre.getSelection() == GenreSelection.INCLUDED) ids.add(genre.getId());
    }
    return ids;
  }

  public ArrayList<Integer> getUnselectedGenresIds(){
    ArrayList<Integer> ids = new ArrayList<>();
    for(Genre genre : genres){
      if(genre.getSelection() == GenreSelection.EXCLUDED) ids.add(genre.getId());
    }
    return ids;
  }
}

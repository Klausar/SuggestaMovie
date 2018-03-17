package de.kaiwidmaier.suggestamovie.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.like.LikeButton;
import com.like.OnLikeListener;
import com.squareup.picasso.Picasso;

import java.util.Collections;
import java.util.List;

import de.kaiwidmaier.suggestamovie.R;
import de.kaiwidmaier.suggestamovie.data.Genre;

/**
 * Created by Kai on 17.03.2018.
 */

public class RecyclerViewGenreAdapter extends RecyclerView.Adapter<RecyclerViewGenreAdapter.ViewHolder> {

  private static final String TAG = RecyclerViewGenreAdapter.class.getSimpleName();
  private List<Genre> genres;
  private LayoutInflater inflater;
  private RecyclerViewGenreAdapter.ItemClickListener clickListener;


  public RecyclerViewGenreAdapter(Context context, List<Genre> movies) {
    this.inflater = LayoutInflater.from(context);
    this.genres = movies;
  }


  @Override
  public RecyclerViewGenreAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = inflater.inflate(R.layout.recyclerview_genre_item, parent, false);
    RecyclerViewGenreAdapter.ViewHolder viewHolder = new RecyclerViewGenreAdapter.ViewHolder(view);
    return viewHolder;
  }

  public boolean containsAll(List<Genre> movies) {
    return this.genres.containsAll(movies);
  }

  @Override
  public void onBindViewHolder(final RecyclerViewGenreAdapter.ViewHolder holder, int position) {
    Genre genre = genres.get(position);
    holder.textName.setText(genre.getName());
    holder.imgIcon.setImageResource(genre.getDrawableResId());
  }


  @Override
  public int getItemCount() {
    return genres.size();
  }

  public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    ImageView imgIcon;
    TextView textName;

    private ViewHolder(View itemView) {
      super(itemView);
      imgIcon = itemView.findViewById(R.id.img_genre_icon);
      textName = itemView.findViewById(R.id.text_genre_name);
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

  public void setClickListener(RecyclerViewGenreAdapter.ItemClickListener itemClickListener) {
    this.clickListener = itemClickListener;
  }


  public interface ItemClickListener {
    void onItemClick(View view, int position);
  }

}

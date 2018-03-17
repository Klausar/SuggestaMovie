package de.kaiwidmaier.suggestamovie.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.CardView;
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
    return new RecyclerViewGenreAdapter.ViewHolder(view);
  }

  @Override
  public void onBindViewHolder(final RecyclerViewGenreAdapter.ViewHolder holder, final int position) {
    final Genre genre = genres.get(position);
    genre.setSelected(true);
    final int selectedColor = Color.parseColor("#8DEF88");
    final int unselectedColor = Color.parseColor("#fff3f3f3");
    holder.textName.setText(genre.getName());
    holder.imgIcon.setImageResource(genre.getDrawableResId());
    holder.layoutGenre.setCardBackgroundColor(selectedColor);
    holder.layoutGenre.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        genre.setSelected(!genre.isSelected());
        int color = genre.isSelected() ? selectedColor : unselectedColor;
        holder.layoutGenre.setCardBackgroundColor(color);

        if(genre.isSelected()){
          holder.textName.setPaintFlags(holder.textName.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
        }
        else{
          holder.textName.setPaintFlags(holder.textName.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }

        Log.d(TAG, "Clicked on: " + genre.getName() + " " + genre.isSelected());
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
    CardView layoutGenre;

    private ViewHolder(View itemView) {
      super(itemView);
      imgIcon = itemView.findViewById(R.id.img_genre_icon);
      textName = itemView.findViewById(R.id.text_genre_name);
      layoutGenre = itemView.findViewById(R.id.layout_genre_item);
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

package de.kaiwidmaier.suggestamovie.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.kaiwidmaier.suggestamovie.R;
import de.kaiwidmaier.suggestamovie.data.Movie;

/**
 * Created by Kai on 04.03.2018.
 */

public class RecyclerViewThumbnailAdapter extends RecyclerView.Adapter<RecyclerViewThumbnailAdapter.ViewHolder> {

  private static final String TAG = RecyclerViewThumbnailAdapter.class.getSimpleName();
  private List<Movie> movies;
  private LayoutInflater inflater;
  private ItemClickListener clickListener;
  private Context context;
  private final String imgUrlBasePath ="http://image.tmdb.org/t/p/w500//";


  public RecyclerViewThumbnailAdapter(Context context, List<Movie> movies) {
    this.inflater = LayoutInflater.from(context);
    this.movies = movies;
    this.context = context;
  }


  @Override
  public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = inflater.inflate(R.layout.recyclerview_thumbnail_item, parent, false);
    ViewHolder viewHolder = new ViewHolder(view);
    return viewHolder;
  }


  @Override
  public void onBindViewHolder(ViewHolder holder, int position) {
    final Movie movie = movies.get(position);
    if(movie.getPosterPath() != null) {
      String posterUrl = imgUrlBasePath + movie.getPosterPath();
      Log.d(TAG, "Poster URL " + movie.getTitle() + ": " + posterUrl);
      Picasso.with(context).load(posterUrl).placeholder(R.drawable.placeholder_thumbnail).error(R.drawable.placeholder_thumbnail).into(holder.thumbnail);
    }
  }


  @Override
  public int getItemCount() {
    return movies.size();
  }


  public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    ImageView thumbnail = itemView.findViewById(R.id.img_thumbnail_recycler);

    public ViewHolder(View itemView) {
      super(itemView);
      thumbnail = itemView.findViewById(R.id.img_thumbnail_recycler);
      itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
      if (clickListener != null) clickListener.onItemClick(view, getAdapterPosition());
    }
  }


  public Movie getItem(int id) {
    return movies.get(id);
  }

  public void setClickListener(ItemClickListener itemClickListener) {
    this.clickListener = itemClickListener;
  }


  public interface ItemClickListener {
    void onItemClick(View view, int position);
  }
}

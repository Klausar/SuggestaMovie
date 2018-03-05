package de.kaiwidmaier.suggestamovie.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.Collections;
import java.util.List;

import de.kaiwidmaier.suggestamovie.R;
import de.kaiwidmaier.suggestamovie.activities.MainActivity;
import de.kaiwidmaier.suggestamovie.data.Movie;

/**
 * Created by Kai on 04.03.2018.
 */

public class RecyclerViewMovieAdapter extends RecyclerView.Adapter<RecyclerViewMovieAdapter.ViewHolder> {

  private List<Integer> viewColors = Collections.emptyList();
  private List<Movie> movies;
  private LayoutInflater inflater;
  private ItemClickListener mClickListener;
  private Context context;
  private final String imgUrlBasePath ="http://image.tmdb.org/t/p/w342//";


  public RecyclerViewMovieAdapter(Context context, List<Movie> movies) {
    this.inflater = LayoutInflater.from(context);
    this.movies = movies;
    this.context = context;
  }


  @Override
  public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = inflater.inflate(R.layout.recyclerview_movie_item, parent, false);
    ViewHolder viewHolder = new ViewHolder(view);
    return viewHolder;
  }


  @Override
  public void onBindViewHolder(ViewHolder holder, int position) {
    Movie movie = movies.get(position);
    if(movie.getPosterPath() != null) {
      String image_url = imgUrlBasePath + movie.getPosterPath();
      Picasso.with(context).load(image_url).placeholder(android.R.drawable.sym_def_app_icon).error(android.R.drawable.sym_def_app_icon).into(holder.thumbnail);
    }
  }


  @Override
  public int getItemCount() {
    return movies.size();
  }


  public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public ImageView thumbnail = itemView.findViewById(R.id.img_thumbnail);

    public ViewHolder(View itemView) {
      super(itemView);
      ImageView thumbnail = itemView.findViewById(R.id.img_thumbnail);
      itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
      if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
    }
  }


  public Movie getItem(int id) {
    return movies.get(id);
  }


  public void setClickListener(ItemClickListener itemClickListener) {
    this.mClickListener = itemClickListener;
  }


  public interface ItemClickListener {
    void onItemClick(View view, int position);
  }
}

package de.kaiwidmaier.suggestamovie.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.kaiwidmaier.suggestamovie.R;
import de.kaiwidmaier.suggestamovie.data.DataHelper;
import de.kaiwidmaier.suggestamovie.data.Movie;
import de.kaiwidmaier.suggestamovie.persistence.Serializer;

/**
 * Created by Kai on 12.03.2018.
 */

public class RecyclerViewMovieAdapter extends RecyclerView.Adapter<RecyclerViewMovieAdapter.ViewHolder> {


  private static final String TAG = RecyclerViewMovieAdapter.class.getSimpleName();
  private List<Movie> movies;
  private LayoutInflater inflater;
  private final String imgUrlBasePath ="http://image.tmdb.org/t/p/w500//";
  private RecyclerViewMovieAdapter.ItemClickListener clickListener;
  private Context context;
  private ArrayList<Movie> watchlist;


  public RecyclerViewMovieAdapter(Context context, List<Movie> movies) {
    this.inflater = LayoutInflater.from(context);
    this.movies = movies;
    this.context = context;
    this.watchlist = ((DataHelper) context.getApplicationContext()).getWatchlist();
  }


  @Override
  public RecyclerViewMovieAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = inflater.inflate(R.layout.recyclerview_movie_item, parent, false);
    RecyclerViewMovieAdapter.ViewHolder viewHolder = new RecyclerViewMovieAdapter.ViewHolder(view);
    return viewHolder;
  }


  @Override
  public void onBindViewHolder(final RecyclerViewMovieAdapter.ViewHolder holder, int position) {
    final Movie movie = movies.get(position);
    if(movie.getPosterPath() != null) {
      String posterUrl = imgUrlBasePath + movie.getPosterPath();
      Log.d(TAG, "Poster URL " + movie.getTitle() + ": " + posterUrl);
      Picasso.with(context).load(posterUrl).placeholder(R.drawable.placeholder_thumbnail).error(R.drawable.placeholder_thumbnail).into(holder.imgThumbnail);
      holder.textTitle.setText(movie.getTitle());
      holder.textRating.setText(String.format(context.getString(R.string.rating_format), movie.getVoteAverage()));
      holder.textRelease.setText(String.format(context.getString(R.string.release_format), movie.getReleaseDate()));

      //Favorite Button
      if(watchlist.contains(movie)){
        holder.btnFavorite.setImageResource(R.drawable.ic_star_yellow_48dp);
      }
      else{
        holder.btnFavorite.setImageResource(R.drawable.ic_star_border_yellow_48dp);
      }
      holder.btnFavorite.setOnClickListener(new View.OnClickListener() {
        Serializer serializer = new Serializer(context);
        boolean watchlistContains = false;

        @Override
        public void onClick(View view) {

          if(watchlist.contains(movie)){
            watchlist.remove(movie);
            holder.btnFavorite.setImageResource(R.drawable.ic_star_border_yellow_48dp);
            serializer.writeWatchlist(watchlist);
          }
          else{
            watchlist.add(movie);
            holder.btnFavorite.setImageResource(R.drawable.ic_star_yellow_48dp);
            serializer.writeWatchlist(watchlist);
          }
        }
      });
    }
  }


  @Override
  public int getItemCount() {
    return movies.size();
  }


  public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    ImageView imgThumbnail;
    TextView textTitle;
    TextView textRating;
    TextView textRelease;
    ImageButton btnFavorite;

    public ViewHolder(View itemView) {
      super(itemView);
      imgThumbnail = itemView.findViewById(R.id.img_thumbnail_movie_recycler);
      textTitle = itemView.findViewById(R.id.text_movie_title_recycler);
      textRating = itemView.findViewById(R.id.text_movie_rating_recycler);
      textRelease = itemView.findViewById(R.id.text_movie_release_recycler);
      btnFavorite = itemView.findViewById(R.id.btn_favorite_recycler);
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

  public void setClickListener(RecyclerViewMovieAdapter.ItemClickListener itemClickListener) {
    this.clickListener = itemClickListener;
  }


  public interface ItemClickListener {
    void onItemClick(View view, int position);
  }

}

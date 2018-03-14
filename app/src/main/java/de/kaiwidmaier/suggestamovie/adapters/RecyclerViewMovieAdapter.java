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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.kaiwidmaier.suggestamovie.R;
import de.kaiwidmaier.suggestamovie.data.DataHelper;
import de.kaiwidmaier.suggestamovie.data.Movie;
import de.kaiwidmaier.suggestamovie.persistence.Serializer;

/**
 * Created by Kai on 12.03.2018.
 */

public class RecyclerViewMovieAdapter extends RecyclerView.Adapter<RecyclerViewMovieAdapter.ViewHolder> implements ItemTouchHelperAdapter{


  private static final String TAG = RecyclerViewMovieAdapter.class.getSimpleName();
  private List<Movie> movies;
  private LayoutInflater inflater;
  private final String imgUrlBasePath = "http://image.tmdb.org/t/p/w500//";
  private RecyclerViewMovieAdapter.ItemClickListener clickListener;
  private Context context;
  private ArrayList<Movie> watchlist;
  private boolean showBtnFavorite;
  final Serializer serializer;


  public RecyclerViewMovieAdapter(Context context, List<Movie> movies, boolean showBtnFavorite) {
    this.inflater = LayoutInflater.from(context);
    this.movies = movies;
    this.context = context;
    this.watchlist = ((DataHelper) context.getApplicationContext()).getWatchlist();
    this.showBtnFavorite = showBtnFavorite;
    serializer = new Serializer(context);
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
    if (movie.getPosterPath() != null) {
      String posterUrl = imgUrlBasePath + movie.getPosterPath();
      Log.d(TAG, "Poster URL " + movie.getTitle() + ": " + posterUrl);
      Picasso.with(context).load(posterUrl).placeholder(R.drawable.placeholder_thumbnail).error(R.drawable.placeholder_thumbnail).into(holder.imgThumbnail);
      holder.textTitle.setText(movie.getTitle());
      holder.textRating.setText(String.format(context.getString(R.string.rating_format), movie.getVoteAverage()));
      holder.textRelease.setText(String.format(context.getString(R.string.release_format), movie.getReleaseDate()));

      if (!showBtnFavorite) {
        holder.btnFavorite.setVisibility(View.GONE);
      }
      if (watchlist.contains(movie)) {
        holder.btnFavorite.setLiked(true);
      } else {
        holder.btnFavorite.setLiked(false);
      }
      holder.btnFavorite.setOnLikeListener(new OnLikeListener() {
        @Override
        public void liked(LikeButton likeButton) {
          watchlist.add(movie);
          notifyDataSetChanged();
          serializer.writeWatchlist(watchlist);
        }

        @Override
        public void unLiked(LikeButton likeButton) {
          watchlist.remove(movie);
          notifyDataSetChanged();
          serializer.writeWatchlist(watchlist);
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
    LikeButton btnFavorite;

    public ViewHolder(View itemView) {
      super(itemView);
      imgThumbnail = itemView.findViewById(R.id.img_thumbnail_movie);
      textTitle = itemView.findViewById(R.id.text_movie_title);
      textRating = itemView.findViewById(R.id.text_movie_rating);
      textRelease = itemView.findViewById(R.id.text_movie_release);
      btnFavorite = itemView.findViewById(R.id.btn_favorite);
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

  @Override
  public void onItemDismiss(int position) {
    watchlist.remove(position);
    notifyItemRemoved(position);
    serializer.writeWatchlist(watchlist);
  }

  @Override
  public boolean onItemMove(int fromPosition, int toPosition) {
    if (fromPosition < toPosition) {
      for (int i = fromPosition; i < toPosition; i++) {
        Collections.swap(watchlist, i, i + 1);
      }
    } else {
      for (int i = fromPosition; i > toPosition; i--) {
        Collections.swap(watchlist, i, i - 1);
      }
    }
    notifyItemMoved(fromPosition, toPosition);
    serializer.writeWatchlist(watchlist);
    return true;
  }

}

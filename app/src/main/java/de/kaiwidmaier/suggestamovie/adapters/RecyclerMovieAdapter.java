package de.kaiwidmaier.suggestamovie.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.like.LikeButton;
import com.like.OnLikeListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.kaiwidmaier.suggestamovie.R;
import de.kaiwidmaier.suggestamovie.activities.MovieActivity;
import de.kaiwidmaier.suggestamovie.activities.ResultActivity;
import de.kaiwidmaier.suggestamovie.data.DataHelper;
import de.kaiwidmaier.suggestamovie.data.Movie;
import de.kaiwidmaier.suggestamovie.persistence.Serializer;
import de.kaiwidmaier.suggestamovie.rest.ResultType;

/**
 * Created by Kai on 12.03.2018.
 */

public class RecyclerMovieAdapter extends RecyclerView.Adapter<RecyclerMovieAdapter.ViewHolder> implements ItemTouchHelperAdapter{


  private static final String TAG = RecyclerMovieAdapter.class.getSimpleName();
  private List<Movie> movies;
  private LayoutInflater inflater;
  private Context context;
  private ArrayList<Movie> watchlist;
  private boolean showBtnFavorite;
  private final Serializer serializer;
  private boolean loading;


  public RecyclerMovieAdapter(Context context, List<Movie> movies, boolean showBtnFavorite) {
    this.inflater = LayoutInflater.from(context);
    this.movies = movies;
    this.context = context;
    this.watchlist = ((DataHelper) context.getApplicationContext()).getWatchlist();
    this.showBtnFavorite = showBtnFavorite;
    serializer = new Serializer(context);
    this.loading = true;
  }


  @Override
  public RecyclerMovieAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = inflater.inflate(R.layout.recyclerview_movie_item, parent, false);
    return new RecyclerMovieAdapter.ViewHolder(view);
  }

  public boolean containsAll(List<Movie> movies){
    return this.movies.containsAll(movies);
  }

  @Override
  public void onBindViewHolder(final RecyclerMovieAdapter.ViewHolder holder, int position) {
    final Movie movie = movies.get(position);
      String imgUrlBasePath = "http://image.tmdb.org/t/p/w342//";
      String posterUrl = imgUrlBasePath + movie.getPosterPath();
      //Log.d(TAG, "Poster URL " + movie.getTitle() + ": " + posterUrl);
      Picasso.with(context).load(posterUrl).fit().centerCrop().placeholder(R.drawable.placeholder_thumbnail).error(R.drawable.placeholder_thumbnail).into(holder.imgThumbnail);
      holder.textTitle.setText(movie.getTitle());
      holder.textRating.setText(String.format(context.getString(R.string.rating_format), movie.getVoteAverage()));
      if(movie.getReleaseDate().length() >= 4){
        holder.textRelease.setText(String.format(context.getString(R.string.release_format), movie.getReleaseDate().substring(0, 4)));
      }
      else{
        holder.textRelease.setText(String.format(context.getString(R.string.release_format), "?"));
      }
      holder.btnFindSimilar.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          Intent similarIntent = new Intent(context, ResultActivity.class);
          similarIntent.putExtra("movieId", movie.getId());
          similarIntent.putExtra("resultTitle", context.getString(R.string.similar));
          similarIntent.putExtra("resultDescr", String.format(context.getString(R.string.similar_descr), movie.getTitle()));
          similarIntent.putExtra("resultType", ResultType.SIMILAR);
          context.startActivity(similarIntent);
        }
      });

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
          serializer.writeWatchlist(watchlist);
        }

        @Override
        public void unLiked(LikeButton likeButton) {
          watchlist.remove(movie);
          serializer.writeWatchlist(watchlist);
        }
      });
  }

  @Override
  public int getItemCount() {
    return movies.size();
  }

  public void addAll(List<Movie> movies){
    this.movies.addAll(movies);
    Log.d(TAG, "Movies added: " + movies.size());
    notifyDataSetChanged();
  }

  public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    ImageView imgThumbnail;
    TextView textTitle;
    TextView textRating;
    TextView textRelease;
    LikeButton btnFavorite;
    Button btnFindSimilar;

    private ViewHolder(View itemView) {
      super(itemView);
      imgThumbnail = itemView.findViewById(R.id.img_thumbnail_movie);
      textTitle = itemView.findViewById(R.id.text_movie_title);
      textRating = itemView.findViewById(R.id.text_movie_rating);
      textRelease = itemView.findViewById(R.id.text_movie_release);
      btnFavorite = itemView.findViewById(R.id.btn_favorite);
      btnFindSimilar = itemView.findViewById(R.id.btn_find_similar);
      itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
      Movie movie = movies.get(getAdapterPosition());
      Log.d(TAG, "Clicked on: " + movie.getTitle());
      Intent intent = new Intent(context, MovieActivity.class);
      intent.putExtra("movie", (Parcelable) movie);
      ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) context, view, context.getString(R.string.transition_movie));
      ActivityCompat.startActivity(context, intent, options.toBundle());
    }
  }

  private Movie getItem(int id) {
    return movies.get(id);
  }

  public void setClickListener(RecyclerMovieAdapter.ItemClickListener itemClickListener) {
    ItemClickListener clickListener = itemClickListener;
  }


  public interface ItemClickListener {
    void onItemClick(View view, int position);
  }

  @Override
  public void onItemDismiss(final int position) {
    final Movie movie = getItem(position);
    Snackbar snackbar = Snackbar.make(((Activity) context).getWindow().getDecorView().findViewById(android.R.id.content),
      String.format(context.getString(R.string.movie_removed), movie.getTitle()), Snackbar.LENGTH_LONG)
      .setAction(context.getString(R.string.undo), new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          watchlist.add(position, movie);
          notifyItemInserted(position);
          serializer.writeWatchlist(watchlist);
        }
      });
    snackbar.show();
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

  public void setLoading(boolean loading){
    this.loading = loading;
  }

  public boolean isLoading(){
    return loading;
  }

}

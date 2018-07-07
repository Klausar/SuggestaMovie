package de.kaiwidmaier.suggestamovie.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import de.kaiwidmaier.suggestamovie.R;
import de.kaiwidmaier.suggestamovie.activities.MovieActivity;
import de.kaiwidmaier.suggestamovie.data.Movie;
import de.kaiwidmaier.suggestamovie.utils.NetworkUtils;

/**
 * Created by Kai on 12.03.2018.
 */

public class RecyclerThumbnailAdapter extends RecyclerView.Adapter<RecyclerThumbnailAdapter.ViewHolder>{


  private static final String TAG = RecyclerThumbnailAdapter.class.getSimpleName();
  private List<Movie> movies;
  private LayoutInflater inflater;
  private Context context;


  public RecyclerThumbnailAdapter(Context context, List<Movie> movies) {
    this.inflater = LayoutInflater.from(context);
    this.movies = movies;
    this.context = context;
  }


  @NonNull
  @Override
  public RecyclerThumbnailAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View view = inflater.inflate(R.layout.recyclerview_thumbnail_item, parent, false);
    return new RecyclerThumbnailAdapter.ViewHolder(view);
  }

  public boolean containsAll(List<Movie> movies){
    return this.movies.containsAll(movies);
  }

  @Override
  public void onBindViewHolder(@NonNull final RecyclerThumbnailAdapter.ViewHolder holder, int position) {
    final Movie movie = movies.get(position);
    String imgUrlBasePath = "http://image.tmdb.org/t/p/w342//";
    String posterUrl = imgUrlBasePath + movie.getPosterPath();
    if(NetworkUtils.loadThumbnail(context)){
      Picasso.with(context).load(posterUrl).fit().centerCrop().placeholder(R.drawable.placeholder_thumbnail).error(R.drawable.placeholder_thumbnail).into(holder.imgThumbnail);
    }
    else{
      holder.imgThumbnail.setImageResource(R.drawable.placeholder_thumbnail);
    }
    holder.textTitle.setText(movie.getTitle(context));
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

    private ViewHolder(View itemView) {
      super(itemView);
      imgThumbnail = itemView.findViewById(R.id.img_thumbnail);
      textTitle = itemView.findViewById(R.id.text_movie_title);
      itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
      Movie movie = movies.get(getAdapterPosition());
      Log.d(TAG, "Clicked on: " + movie.getTitle(context));
      Intent intent = new Intent(context, MovieActivity.class);
      intent.putExtra("movie", (Parcelable) movie);
      ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) context, view, context.getString(R.string.transition_movie));
      ActivityCompat.startActivity(context, intent, options.toBundle());
    }
  }

}

package de.kaiwidmaier.suggestamovie.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.like.LikeButton;
import com.like.OnLikeListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.kaiwidmaier.suggestamovie.R;
import de.kaiwidmaier.suggestamovie.adapters.RecyclerGenreChipAdapter;
import de.kaiwidmaier.suggestamovie.data.DataHelper;
import de.kaiwidmaier.suggestamovie.data.Genre;
import de.kaiwidmaier.suggestamovie.data.Movie;
import de.kaiwidmaier.suggestamovie.persistence.Serializer;

public class MovieActivity extends AppCompatActivity {

  private Movie movie;
  private TextView textTitle;
  private TextView textDescription;
  private TextView textRating;
  private TextView textRelease;
  private ImageView imgPoster;
  private LikeButton btnFavorite;
  private ArrayList<Movie> watchlist;
  private RecyclerView recyclerGenreChips;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_movie);

    Intent intent = getIntent();
    movie = intent.getParcelableExtra("movie");
    ArrayList<Genre> movieGenres = new ArrayList<>();
    for(Integer i : movie.getGenreIds()){
      movieGenres.add(((DataHelper) this.getApplication()).getGenre(i));
    }

    textTitle = findViewById(R.id.text_movie_title);
    textDescription = findViewById(R.id.text_movie_description);
    textRating = findViewById(R.id.text_movie_rating);
    textRelease = findViewById(R.id.text_movie_release);
    imgPoster = findViewById(R.id.img_thumbnail_movie);
    btnFavorite = findViewById(R.id.btn_favorite);
    recyclerGenreChips = findViewById(R.id.recycler_genre_chips);
    recyclerGenreChips.setAdapter(new RecyclerGenreChipAdapter(this, movieGenres));
    watchlist = ((DataHelper) this.getApplicationContext()).getWatchlist();

    fillData();
  }

  private void fillData(){
    textTitle.setText(movie.getTitle());
    textDescription.setText(movie.getOverview());
    textRating.setText(String.format(getString(R.string.rating_format), movie.getVoteAverage()));
    if(movie.getReleaseDate().length() >= 4){
      textRelease.setText(String.format(getString(R.string.release_format), movie.getReleaseDate().substring(0, 4)));
    }
    else{
      textRelease.setText(String.format(getString(R.string.release_format), "?"));
    }
    String imgUrlBasePath = "http://image.tmdb.org/t/p/w342//";
    Picasso.with(this).load(imgUrlBasePath + movie.getPosterPath()).fit().centerCrop().placeholder(R.drawable.placeholder_thumbnail).error(R.drawable.placeholder_thumbnail).into(imgPoster);

    if(watchlist.contains(movie)){
      btnFavorite.setLiked(true);
    }
    else{
      btnFavorite.setLiked(false);
    }
    btnFavorite.setOnLikeListener(new OnLikeListener() {
      Serializer serializer = new Serializer(MovieActivity.this);
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



}

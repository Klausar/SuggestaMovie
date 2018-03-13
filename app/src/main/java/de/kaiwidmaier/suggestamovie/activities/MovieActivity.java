package de.kaiwidmaier.suggestamovie.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.kaiwidmaier.suggestamovie.R;
import de.kaiwidmaier.suggestamovie.data.DataHelper;
import de.kaiwidmaier.suggestamovie.data.Movie;
import de.kaiwidmaier.suggestamovie.persistence.Serializer;

public class MovieActivity extends AppCompatActivity {

  private final String imgUrlBasePath ="http://image.tmdb.org/t/p/w500//";
  private Movie movie;
  private TextView textTitle;
  private TextView textDescription;
  private TextView textRating;
  private TextView textRelease;
  private ImageView imgPoster;
  private ImageButton btnFavorite;
  private ArrayList<Movie> watchlist;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_movie);

    Intent intent = getIntent();
    movie = intent.getParcelableExtra("movie");

    textTitle = findViewById(R.id.text_movie_title);
    textDescription = findViewById(R.id.text_movie_description);
    textRating = findViewById(R.id.text_movie_rating);
    textRelease = findViewById(R.id.text_movie_release);
    imgPoster = findViewById(R.id.img_thumbnail_movie);
    btnFavorite = findViewById(R.id.btn_favorite);
    watchlist = ((DataHelper) this.getApplicationContext()).getWatchlist();

    fillData();
  }

  private void fillData(){
    textTitle.setText(movie.getTitle());
    textDescription.setText(movie.getOverview());
    textRating.setText(String.format(getString(R.string.rating_format), movie.getVoteAverage()));
    textRelease.setText(String.format(getString(R.string.release_format), movie.getReleaseDate()));
    Picasso.with(this).load(imgUrlBasePath + movie.getPosterPath()).into(imgPoster);

    //Favorite Button
    if(watchlist.contains(movie)){
      btnFavorite.setImageResource(R.drawable.ic_star_yellow_48dp);
    }
    else{
      btnFavorite.setImageResource(R.drawable.ic_star_border_yellow_48dp);
    }
    btnFavorite.setOnClickListener(new View.OnClickListener() {
      Serializer serializer = new Serializer(MovieActivity.this);
      boolean watchlistContains = false;

      @Override
      public void onClick(View view) {

        if(watchlist.contains(movie)){
          watchlist.remove(movie);
          btnFavorite.setImageResource(R.drawable.ic_star_border_yellow_48dp);
          serializer.writeWatchlist(watchlist);
        }
        else{
          watchlist.add(movie);
          btnFavorite.setImageResource(R.drawable.ic_star_yellow_48dp);
          serializer.writeWatchlist(watchlist);
        }
      }
    });
  }



}

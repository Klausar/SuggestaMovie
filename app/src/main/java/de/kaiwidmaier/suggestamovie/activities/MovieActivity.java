package de.kaiwidmaier.suggestamovie.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import de.kaiwidmaier.suggestamovie.R;
import de.kaiwidmaier.suggestamovie.data.Movie;

public class MovieActivity extends AppCompatActivity {

  private final String imgUrlBasePath ="http://image.tmdb.org/t/p/w500//";
  private Movie movie;
  private TextView textTitle;
  private TextView textDescription;
  private TextView textRating;
  private ImageView imgPoster;
  private LinearLayout layoutMovie;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_movie);

    textTitle = findViewById(R.id.text_movie_title);
    textDescription = findViewById(R.id.text_movie_description);
    textRating = findViewById(R.id.text_movie_rating);
    imgPoster = findViewById(R.id.img_thumbnail_movie);
    layoutMovie = findViewById(R.id.layout_movie);

    Intent intent = getIntent();
    movie = intent.getParcelableExtra("movie");
    fillData();
  }

  private void fillData(){
    textTitle.setText(movie.getTitle());
    textDescription.setText(movie.getOverview());
    textRating.setText(String.format(getString(R.string.rating_format), movie.getVoteAverage()));
    Picasso.with(this).load(imgUrlBasePath + movie.getPosterPath()).into(imgPoster);

  }



}

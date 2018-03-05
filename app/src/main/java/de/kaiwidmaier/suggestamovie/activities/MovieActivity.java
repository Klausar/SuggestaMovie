package de.kaiwidmaier.suggestamovie.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import de.kaiwidmaier.suggestamovie.R;
import de.kaiwidmaier.suggestamovie.data.Movie;

public class MovieActivity extends AppCompatActivity {

  private Movie movie;
  private TextView textTitle;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_movie);

    textTitle = findViewById(R.id.text_movie_title);

    Intent intent = getIntent();
    movie = intent.getParcelableExtra("movie");
    fillData();
  }

  private void fillData(){
    textTitle.setText(movie.getTitle());
  }
}

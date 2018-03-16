package de.kaiwidmaier.suggestamovie.activities;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;

import org.florescu.android.rangeseekbar.RangeSeekBar;

import java.util.Calendar;

import de.kaiwidmaier.suggestamovie.R;

public class DiscoverActivity extends AppCompatActivity {

  private static final String TAG = DiscoverActivity.class.getSimpleName();
  private RangeSeekBar<Integer> seekbarRelease;
  private RangeSeekBar<Integer> seekbarRating;
  private Switch switchAdult;
  private FloatingActionButton btnSearch;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_discover);

    seekbarRelease = findViewById(R.id.seekbar_release);
    seekbarRating = findViewById(R.id.seekbar_rating);
    switchAdult = findViewById(R.id.checkbox_include_adult);
    btnSearch = findViewById(R.id.btn_start_discover);

    final int currentYear = Calendar.getInstance().get(Calendar.YEAR);
    seekbarRelease.setRangeValues(1930, currentYear);
    seekbarRating.setRangeValues(0, 10);

    btnSearch.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        String releaseDateMin = seekbarRelease.getSelectedMinValue() + "-01-01"; //e.g. 1954-01-01
        String releaseDateMax;

        //Only allow dates up to today to exclude movies that haven't been released yet
        if(seekbarRelease.getSelectedMaxValue() == currentYear){
          releaseDateMax = String.format("%s-%s-%s", currentYear, Calendar.getInstance().get(Calendar.MONTH)+1, Calendar.getInstance().get(Calendar.DAY_OF_MONTH)); //e.g. 2018-03-12
        }
        else{
          releaseDateMax = seekbarRelease.getSelectedMaxValue() + "-12-31"; //e.g. 2018-12-31;
        }
        Log.d(TAG, releaseDateMax);

        int ratingMin = seekbarRating.getSelectedMinValue();
        int ratingMax = seekbarRating.getSelectedMaxValue();
        boolean adult = switchAdult.isChecked();

        startResultIntent(releaseDateMin, releaseDateMax, ratingMin, ratingMax, adult);
      }
    });
  }

  private void startResultIntent(String releaseDateMin, String releaseDateMax, int ratingMin, int ratingMax, boolean adult){
    Intent resultIntent = new Intent(this, ResultActivity.class);
    resultIntent.putExtra("releaseDateMin", releaseDateMin);
    resultIntent.putExtra("releaseDateMax", releaseDateMax);
    resultIntent.putExtra("ratingMin", ratingMin);
    resultIntent.putExtra("ratingMax", ratingMax);
    resultIntent.putExtra("adult", adult);

    startActivity(resultIntent);
  }
}

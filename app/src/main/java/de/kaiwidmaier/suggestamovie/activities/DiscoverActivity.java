package de.kaiwidmaier.suggestamovie.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.CheckBox;

import org.florescu.android.rangeseekbar.RangeSeekBar;

import java.time.Year;
import java.util.Calendar;

import de.kaiwidmaier.suggestamovie.R;

public class DiscoverActivity extends AppCompatActivity {

  private RangeSeekBar<Integer> seekbarRelease;
  private RangeSeekBar<Integer> seekbarRating;
  private CheckBox checkBoxAdult;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_discover);

    seekbarRelease = findViewById(R.id.seekbar_release);
    seekbarRating = findViewById(R.id.seekbar_rating);
    checkBoxAdult = findViewById(R.id.checkbox_include_adult);

    seekbarRelease.setRangeValues(1930, Calendar.getInstance().get(Calendar.YEAR));
    seekbarRating.setRangeValues(0, 10);
  }
}

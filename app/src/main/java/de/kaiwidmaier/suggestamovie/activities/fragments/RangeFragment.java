package de.kaiwidmaier.suggestamovie.activities.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.appyvet.materialrangebar.RangeBar;

import java.util.Calendar;

import de.kaiwidmaier.suggestamovie.R;

/**
 * Created by Kai on 18.03.2018.
 */

public class RangeFragment extends Fragment {

  private static final String KEY_POSITION="position";
  private RangeBar rangeBarRelease;
  private RangeBar rangeBarRating;
  final int currentYear = Calendar.getInstance().get(Calendar.YEAR);

  public static RangeFragment newInstance(int position) {
    RangeFragment fragment = new RangeFragment();
    Bundle args = new Bundle();

    args.putInt(KEY_POSITION, position);
    fragment.setArguments(args);

    return(fragment);
  }

  public String getReleaseDateMin(){
    return rangeBarRelease.getLeftPinValue() + "-01-01"; //e.g. 1954-01-01
  }

  public String getReleaseDateMax(){
    String releaseDateMax;

    //Only allow dates up to today to exclude movies that haven't been released yet
    if (Integer.valueOf(rangeBarRelease.getRightPinValue()) == currentYear) {
      releaseDateMax = String.format("%s-%s-%s", currentYear, Calendar.getInstance().get(Calendar.MONTH) + 1, Calendar.getInstance().get(Calendar.DAY_OF_MONTH)); //e.g. 2018-03-12
    } else {
      releaseDateMax = rangeBarRelease.getRightPinValue() + "-12-31"; //e.g. 2018-12-31;
    }

    return releaseDateMax;
  }

  public int getRatingMin(){
    return Integer.valueOf(rangeBarRating.getLeftPinValue());
  }

  public int getRatingMax(){
    return Integer.valueOf(rangeBarRating.getRightPinValue());
  }

  public static String getTitle(Context context, int position) {
    return(String.format(context.getString(R.string.hint), position + 1));
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View result=inflater.inflate(R.layout.fragment_range, container, false);
    LinearLayout editor = result.findViewById(R.id.layout_range);
    rangeBarRelease = result.findViewById(R.id.rangebar_release);
    rangeBarRelease.setTickEnd(currentYear);
    rangeBarRating = result.findViewById(R.id.rangebar_rating);

    return result;
  }
}

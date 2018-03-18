package de.kaiwidmaier.suggestamovie.activities.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import de.kaiwidmaier.suggestamovie.R;
import de.kaiwidmaier.suggestamovie.adapters.FragmentDiscoverAdapter;

/**
 * Created by Kai on 18.03.2018.
 */

public class RangeFragment extends Fragment {
  private static final String KEY_POSITION="position";

  public static RangeFragment newInstance(int position) {
    RangeFragment fragment = new RangeFragment();
    Bundle args = new Bundle();

    args.putInt(KEY_POSITION, position);
    fragment.setArguments(args);

    return(fragment);
  }

  public static String getTitle(Context context, int position) {
    return(String.format(context.getString(R.string.hint), position + 1));
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View result=inflater.inflate(R.layout.fragment_range, container, false);
    LinearLayout editor = (LinearLayout) result.findViewById(R.id.editor);
    //int position=getArguments().getInt(KEY_POSITION, -1);

    return result;
  }
}

package de.kaiwidmaier.suggestamovie.activities.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import de.kaiwidmaier.suggestamovie.R;
import de.kaiwidmaier.suggestamovie.adapters.FragmentDiscoverAdapter;

public class DiscoverFragment extends Fragment {
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View result = inflater.inflate(R.layout.fragment_discover, container, false);
    ViewPager pager = result.findViewById(R.id.pager);

    pager.setAdapter(buildAdapter());

    return result;
  }

  private PagerAdapter buildAdapter() {
    ArrayList<Fragment> fragments = new ArrayList<>();
    fragments.add(new RangeFragment());
    fragments.add(new GenreFragment());
    return (new FragmentDiscoverAdapter(getActivity(), getChildFragmentManager(), fragments));
  }
}

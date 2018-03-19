package de.kaiwidmaier.suggestamovie.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.logging.Filter;

import de.kaiwidmaier.suggestamovie.activities.fragments.FilterFragment;
import de.kaiwidmaier.suggestamovie.activities.fragments.GenreFragment;
import de.kaiwidmaier.suggestamovie.activities.fragments.RangeFragment;

/**
 * Created by Kai on 18.03.2018.
 */

public class FragmentDiscoverAdapter extends FragmentPagerAdapter {
  Context context;
  ArrayList<FilterFragment> fragments;

  public FragmentDiscoverAdapter(Context context, FragmentManager manager, ArrayList<FilterFragment> fragments) {
    super(manager);
    this.context = context;
    this.fragments = fragments;
  }

  @Override
  public int getCount() {
    return fragments.size();
  }

  @Override
  public Fragment getItem(int position) {
    return (Fragment) fragments.get(position);
  }

  @Override
  public String getPageTitle(int position) {
    return fragments.get(position).getTitle();
  }

}

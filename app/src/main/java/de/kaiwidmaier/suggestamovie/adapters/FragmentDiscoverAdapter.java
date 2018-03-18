package de.kaiwidmaier.suggestamovie.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;

import de.kaiwidmaier.suggestamovie.activities.fragments.GenreFragment;
import de.kaiwidmaier.suggestamovie.activities.fragments.RangeFragment;

/**
 * Created by Kai on 18.03.2018.
 */

public class FragmentDiscoverAdapter extends FragmentPagerAdapter {
  Context context;
  ArrayList<Fragment> fragments;

  public FragmentDiscoverAdapter(Context context, FragmentManager manager, ArrayList<Fragment> fragments) {
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
    return fragments.get(position);
  }

  @Override
  public String getPageTitle(int position) {
    return fragments.get(position).getTag();
  }
}

package de.kaiwidmaier.suggestamovie.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import de.kaiwidmaier.suggestamovie.activities.fragments.RangeFragment;

/**
 * Created by Kai on 18.03.2018.
 */

public class FragmentDiscoverAdapter extends FragmentPagerAdapter {
  Context context;

  public FragmentDiscoverAdapter(Context context, FragmentManager manager) {
    super(manager);
    this.context = context;
  }

  @Override
  public int getCount() {
    return(10);
  }

  @Override
  public Fragment getItem(int position) {
    return(RangeFragment.newInstance(position));
  }

  @Override
  public String getPageTitle(int position) {
    return(RangeFragment.getTitle(context, position));
  }
}

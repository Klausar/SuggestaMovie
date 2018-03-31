package de.kaiwidmaier.suggestamovie.adapters;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.view.View;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import it.sephiroth.android.library.bottomnavigation.BottomBehavior;

public class BottomNavigationFabBehavior extends BottomBehavior {

  @SuppressWarnings ("unused")
  public BottomNavigationFabBehavior(final Context context, AttributeSet attributeSet) {
    super(context, attributeSet);
  }

  @Override
  protected boolean isFloatingActionButton(final View dependency) {
    return super.isFloatingActionButton(dependency) || dependency instanceof FloatingActionMenu;
  }
}

package de.kaiwidmaier.suggestamovie.adapters;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.github.clans.fab.FloatingActionMenu;

import java.util.List;

import it.sephiroth.android.library.bottomnavigation.BottomNavigation;

public class FloatingActionMenuBehavior extends CoordinatorLayout.Behavior<FloatingActionMenu> {
  public FloatingActionMenuBehavior() {
    super();
  }

  public FloatingActionMenuBehavior(Context context, AttributeSet attrs) {
    super(context, attrs);
  }


  @Override
  public boolean layoutDependsOn(final CoordinatorLayout parent, final FloatingActionMenu child, final View dependency) {
    if (BottomNavigation.class.isInstance(dependency)) {
      return true;
    } else if (Snackbar.SnackbarLayout.class.isInstance(dependency)) {
      return true;
    }
    return super.layoutDependsOn(parent, child, dependency);
  }

  @Override
  public boolean onDependentViewChanged(
    final CoordinatorLayout parent, final FloatingActionMenu child, final View dependency) {

    final List<View> list = parent.getDependencies(child);
    int bottomMargin = ((ViewGroup.MarginLayoutParams) child.getLayoutParams()).bottomMargin;

    float t = 0;
    boolean result = false;

    for (View dep : list) {
      if (Snackbar.SnackbarLayout.class.isInstance(dep)) {
        t += dep.getTranslationY() - dep.getHeight();
        result = true;
      } else if (BottomNavigation.class.isInstance(dep)) {
        BottomNavigation navigation = (BottomNavigation) dep;
        t += navigation.getTranslationY() - navigation.getHeight() + bottomMargin;
        result = true;
      }
    }

    child.setTranslationY(t);
    return result;
  }

  @Override
  public void onDependentViewRemoved(
    final CoordinatorLayout parent, final FloatingActionMenu child, final View dependency) {
    super.onDependentViewRemoved(parent, child, dependency);
  }
}

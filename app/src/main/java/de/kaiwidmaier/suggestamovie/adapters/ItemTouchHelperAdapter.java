package de.kaiwidmaier.suggestamovie.adapters;

/**
 * Created by Kai on 14.03.2018.
 */

public interface ItemTouchHelperAdapter {

  boolean onItemMove(int fromPosition, int toPosition);

  void onItemDismiss(int position);

}

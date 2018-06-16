package de.kaiwidmaier.suggestamovie.adapters.utils;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

import de.kaiwidmaier.suggestamovie.adapters.ItemTouchHelperAdapter;

/**
 * Created by Kai on 14.03.2018.
 */

public class SimpleItemTouchHelperCallback extends ItemTouchHelper.Callback{

  private final ItemTouchHelperAdapter touchHelperAdapter;
  private boolean touchAllowed = true;

  public SimpleItemTouchHelperCallback(ItemTouchHelperAdapter adapter) {
    touchHelperAdapter = adapter;
  }

  @Override
  public boolean isLongPressDragEnabled() {
    return true;
  }

  @Override
  public boolean isItemViewSwipeEnabled() {
    return true;
  }

  @Override
  public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
    int dragFlags = touchAllowed ? ItemTouchHelper.UP | ItemTouchHelper.DOWN : 0;
    int swipeFlags = touchAllowed ? ItemTouchHelper.START | ItemTouchHelper.END : 0;
    return makeMovementFlags(dragFlags, swipeFlags);
  }

  public void setTouchAllowed(boolean touchAllowed){
    this.touchAllowed = touchAllowed;
  }

  @Override
  public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                        RecyclerView.ViewHolder target) {
    touchHelperAdapter.onItemMove(viewHolder.getAdapterPosition(), target.getAdapterPosition());
    return true;
  }

  @Override
  public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
    touchHelperAdapter.onItemDismiss(viewHolder.getAdapterPosition());
  }

}

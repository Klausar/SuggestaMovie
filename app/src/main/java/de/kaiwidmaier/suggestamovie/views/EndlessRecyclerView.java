package de.kaiwidmaier.suggestamovie.views;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;

import de.kaiwidmaier.suggestamovie.activities.interfaces.EndlessAPILoader;

public class EndlessRecyclerView extends RecyclerView{

  private boolean loading = true;
  private int page = 1;

  public EndlessRecyclerView(Context context) {
    super(context);
  }

  public EndlessRecyclerView(Context context, @Nullable AttributeSet attrs) {
    super(context, attrs);
  }

  public EndlessRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
  }

  public void setLoading(boolean loading){
    this.loading = loading;
  }

  public boolean isLoading(){
    return loading;
  }

  public void setPage(int page){
    this.page = page;
  }

  public int getPage(){
    return page;
  }

  @Override
  public void onScrolled(int dx, int dy) {
    super.onScrolled(dx, dy);

    if(getContext() instanceof EndlessAPILoader){
      int visibleThreshold = 5;
      int visibleItemCount = getChildCount();
      int totalItemCount = getLayoutManager().getItemCount();
      int firstVisibleItem = ((LinearLayoutManager) getLayoutManager()).findFirstVisibleItemPosition();

      if (!isLoading() && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) {
        //End has been reached, load more
        ((EndlessAPILoader) getContext()).connectAndGetApiData(page);
        setLoading(true);
      }
    }
  }
}

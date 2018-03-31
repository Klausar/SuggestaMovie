package de.kaiwidmaier.suggestamovie.activities.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.kaiwidmaier.suggestamovie.R;

public class NowPlayingFragment extends Fragment {

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    View result = inflater.inflate(R.layout.fragment_now_playing, container, false);
    return result;
  }

}

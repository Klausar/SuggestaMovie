package de.kaiwidmaier.suggestamovie.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import de.kaiwidmaier.suggestamovie.R;
import de.kaiwidmaier.suggestamovie.data.Actor;
import de.kaiwidmaier.suggestamovie.utils.NetworkUtils;

public class RecyclerActorAdapter extends RecyclerView.Adapter<RecyclerActorAdapter.ViewHolder>{


  private static final String TAG = RecyclerThumbnailAdapter.class.getSimpleName();
  private List<Actor> actors;
  private LayoutInflater inflater;
  private Context context;


  public RecyclerActorAdapter(Context context, List<Actor> actors) {
    this.inflater = LayoutInflater.from(context);
    this.actors = actors;
    this.context = context;
  }


  @NonNull
  @Override
  public RecyclerActorAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View view = inflater.inflate(R.layout.recyclerview_actor_item, parent, false);
    return new RecyclerActorAdapter.ViewHolder(view);
  }

  @Override
  public void onBindViewHolder(@NonNull final RecyclerActorAdapter.ViewHolder holder, int position) {
    Actor actor = actors.get(position);
    String imgUrlBasePath = "http://image.tmdb.org/t/p/w342//";
    String posterUrl = imgUrlBasePath + actor.getProfilePath();
    if(NetworkUtils.loadThumbnail(context)){
      Picasso.with(context).load(posterUrl).fit().centerCrop().placeholder(R.drawable.placeholder_thumbnail).error(R.drawable.placeholder_thumbnail).into(holder.imgActor);
    }
    else{
      holder.imgActor.setImageResource(R.drawable.placeholder_thumbnail);
    }
    holder.textName.setText(actor.getName());
    holder.textCharacter.setText(actor.getCharacter());
  }

  @Override
  public int getItemCount() {
    return actors.size();
  }

  class ViewHolder extends RecyclerView.ViewHolder{
    ImageView imgActor;
    TextView textName;
    TextView textCharacter;

    private ViewHolder(View itemView) {
      super(itemView);
      imgActor = itemView.findViewById(R.id.img_actor);
      textName = itemView.findViewById(R.id.text_actor_name);
      textCharacter = itemView.findViewById(R.id.text_character_name);
    }
  }

}

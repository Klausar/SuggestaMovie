package de.kaiwidmaier.suggestamovie.data;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class CreditResponse {

  @SerializedName("cast")
  ArrayList<Actor> cast;
  @SerializedName("crew")
  ArrayList<CrewMember> crew;

  public ArrayList<Actor> getCast() {
    return cast;
  }

  public void setActors(ArrayList<Actor> actors) {
    this.cast = cast;
  }

  public ArrayList<CrewMember> getCrew() {
    return crew;
  }

  public void setCrew(ArrayList<CrewMember> crew) {
    this.crew = crew;
  }
}

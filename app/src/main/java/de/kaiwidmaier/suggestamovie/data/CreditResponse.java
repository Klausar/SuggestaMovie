package de.kaiwidmaier.suggestamovie.data;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class CreditResponse {

  @SerializedName("cast")
  ArrayList<Actor> actors;
  @SerializedName("crew")
  ArrayList<CrewMember> crew;

}

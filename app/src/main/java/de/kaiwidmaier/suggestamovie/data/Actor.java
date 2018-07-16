package de.kaiwidmaier.suggestamovie.data;

import com.google.gson.annotations.SerializedName;

public class Actor {

  @SerializedName("credit_id")
  private String creditId;
  @SerializedName("character")
  private String character;
  @SerializedName("name")
  private String name;
  @SerializedName("profile_path")
  private String profilePath;

  public String getCreditId() {
    return creditId;
  }

  public void setCreditId(String creditId) {
    this.creditId = creditId;
  }

  public String getCharacter() {
    return character;
  }

  public void setCharacter(String character) {
    this.character = character;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getProfilePath() {
    return profilePath;
  }

  public void setProfilePath(String profilePath) {
    this.profilePath = profilePath;
  }
}

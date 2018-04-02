# Suggest a Movie

Suggest a Movie is an Android app that helps you to find a movie to watch. It uses the [TheMovieDB API](https://www.themoviedb.org/documentation/api) to find movies by release date, actors, genre and many other things. [Retrofit](https://square.github.io/retrofit/) is used as a ReST Client.

This product uses the TMDb API but is not endorsed or certified by TMDb

## Planned Features:
- Get suggestions based on values specified by the user
- Let user add movies to a watchlist that's displayed in MainActivity
- Search function to search through watchlist
- Drag and drop sort for watchlist
- Get recommended and similar movies based on movies in watchlist

## How to set up:
1. Create a file `config.properties` in the root directory of the project
2. Make a new key called API_KEY with your API key as value e.g. `API_KEY=12345` where `12345` is your API key
3. You can now Access the API key within the project by using `BuildConfig.API_KEY`

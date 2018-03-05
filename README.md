# Suggest a Movie

Suggest a Movie is an Android app that helps you to find a movie to watch. It uses the [TheMovieDB API](https://www.themoviedb.org/documentation/api) to find movies by release date, actors, genre and many other things. 

## Planned Features:
- Get suggestions based on values specified by the user
- Settings to show either upcoming, popular, top or now playing movies in MainActivity
- Let user add movies to a list so he can remember to watch them later

## How to set up:
1. Create a file `config.properties` in the root directory of the project
2. Make a new key called API_KEY with your API key as value e.g. `API_KEY=12345` where `12345` is your API key
3. You can now Access the API key within the project by using `BuildConfig.API_KEY`

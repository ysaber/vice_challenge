# VICE Media Coding Challenge (Yusuf Saber)

This app is designed to load the list of most popular movies, as well as currently playing movies from TMBD (https://www.themoviedb.org).

A quick rundown of how the app is designed is as such:
1) The app checks if an internet connection is present. If it is, it invokes 2 API calls. The first gets the most popular movies, and the second gets the currently playing movies.
2) While those async calls are happening, the UI is initiated. The main screen consist of a bottom navigation bar that is used to switch between home (all movies) and favourites. The main UI actually only consists of 1 ListView. Pressing on Home/Favourites merely swaps the adapter for this ListView. Two adapters are created (one for home, one for favourites), and the home adapter is set to the ListView, while nothing is in either adapter.
3) Once the API calls return, the movies adapter are filled and the UI is populated. 
4) Clicking on a movie (whether it be in 'Currently Playing' or 'Most Popular') opens up the movie's detail screen. From there, the user can see more information about the movie including it's release date, trailers, and reviews. On top of that, the user can save this movie to their favourites. The list of favourite movies persists even after the app is restarted.
5) The data is persisted using Google's Room API. The server calls are made via Google's Volley API, and images are loaded from the server via the Picasso API. Other APIs used in this app are Butterknife, Gson, and CardView.

I hope you enjoy using/testing this app as much as I did making it :)

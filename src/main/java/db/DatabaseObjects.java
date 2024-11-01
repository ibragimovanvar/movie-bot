package db;

import model.Movie;
import model.MovieCategory;
import model.MovieQuality;

import java.util.ArrayList;
import java.util.List;

public class DatabaseObjects {
   public static List<Movie> movies = new ArrayList<>();
   public static List<MovieCategory> movieCategories = new ArrayList<>();

   static {
      movieCategories.addAll(
              List.of(
                      new MovieCategory("Horror"),
                      new MovieCategory("Komediya")
              )
      );

      movies.add(
              new Movie(
                      "Jangchi bahodir",
                      "movies/kino.mp4",
                      movieCategories.get(0),
                      MovieQuality.FHD_1080,
                      235,
                      1,
                      35,
                      40,
                      2023,
                      9,
                      0)
      );
      movies.add(
              new Movie(
                      "O'lmas Bahodir",
                      "movies/kino.mp4",
                      movieCategories.get(0),
                      MovieQuality.FHD_1080,
                      236,
                      1,
                      35,
                      40,
                      2023,
                      9,
                      0)
      );
   }

   public static Movie getMovieByCode(Integer code){
      for (Movie movie : movies) {
         if(movie.getCode().equals(code)){
            return movie;
         }
      }

      return null;
   }
}

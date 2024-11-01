package model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Movie {
    private String name;
    private String url;
    private MovieCategory category;
    private MovieQuality quality;
    private Integer code;
    private Integer hourLen;
    private Integer minuteLen;
    private Integer secondsLen;
    private Integer releaseDate;
    private Integer movieRate;
    private Integer viewerCount;

    public void setMovieRate(Integer movieRate) {
        if(movieRate > 10){
            System.err.println("Kino reytingi 10 dan yuqori bo'la olmaydi!");
            this.movieRate = 10;
        }else{
            this.movieRate = movieRate;
        }
    }
}

package utils;

import model.Movie;

public class MovieUtil {

    public static String movieCaptionGenerator(Movie movieByCode) {
        return
                "Kino nomi: " + movieByCode.getName() + "\n" +
                        "Kino sifati: " + movieByCode.getQuality().alias + "\n" +
                        "Kino kodi: " + movieByCode.getCode() + "\n" +
                        "Uzunligi: " + movieByCode.getHourLen() + ":" + movieByCode.getMinuteLen() + ":" + movieByCode.getSecondsLen() + "\n" +
                        "Kino janri: " + movieByCode.getCategory().getName() + "\n" +
                        "Chiqgan yili: " + movieByCode.getReleaseDate() + "\n" +
                        "Reytingi: " + movieByCode.getMovieRate() + "⭐️\n" +
                        "Ko'rilgan : " + movieByCode.getViewerCount() + " marta ko'rilgan";
    }

}

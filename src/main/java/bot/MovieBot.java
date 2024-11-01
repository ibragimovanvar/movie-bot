package bot;

import db.DatabaseObjects;
import model.Movie;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendVideo;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;

public class MovieBot extends TelegramLongPollingBot {

    @Override
    public void onUpdateReceived(Update update) {
        String chatId = update.getMessage().getChatId().toString();
        String text = update.getMessage().getText();

        Movie movieByCode = DatabaseObjects.getMovieByCode(Integer.parseInt(text));
        if (movieByCode == null) {
            SendMessage sendMessage = new SendMessage(chatId, "Bunaqa kodli kino yo'q");
            sendMyMessage(sendMessage);
        } else {
            SendVideo sendVideo = new SendVideo(chatId, new InputFile(new File(movieByCode.getUrl())));
            sendVideo.setCaption(movieCaptionGenerator(movieByCode));
            sendMyMessage(sendVideo);
        }

    }

    private void sendMyMessage(SendMessage sendMessage) {
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void sendMyMessage(SendVideo sendVideo) {
        try {
            execute(sendVideo);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private String movieCaptionGenerator(Movie movieByCode) {
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

    @Override
    public String getBotUsername() {
        return "kinoteatrd28bot";
    }

    @Override
    public String getBotToken() {
        return "7249460019:AAEOcKBjmmYCqFCtVUEEZ6mfZYTjS-msu8U";
    }
}

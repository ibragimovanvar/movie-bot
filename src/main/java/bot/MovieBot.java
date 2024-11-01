package bot;

import db.DatabaseObjects;
import model.Movie;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendVideo;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MovieBot extends TelegramLongPollingBot {

    @Override
    public void onUpdateReceived(Update update) {
        String chatId = update.getMessage().getChatId().toString();
        String text = update.getMessage().getText();

        if (text.equals("/start")) {
            SendMessage sendMessage = new SendMessage(chatId, "Kategoriyani tanlang");

            ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();

            List<KeyboardRow> keyboardRows = new ArrayList<>();

            KeyboardRow keyboardRow1 = new KeyboardRow();
            KeyboardRow keyboardRow2 = new KeyboardRow();
            KeyboardRow keyboardRow3 = new KeyboardRow();

            KeyboardButton keyboardButton1 = new KeyboardButton("Kino kodini kiriting");
            KeyboardButton keyboardButton2 = new KeyboardButton("Kategorya");
            KeyboardButton keyboardButton3 = new KeyboardButton("Eng sarra kinolar");
            KeyboardButton keyboardButton4 = new KeyboardButton("Haftadagi TOP kinolar");

            keyboardRow1.add(keyboardButton1);
            keyboardRow2.add(keyboardButton2);
            keyboardRow2.add(keyboardButton3);
            keyboardRow3.add(keyboardButton4);

            keyboardRows.add(keyboardRow1);
            keyboardRows.add(keyboardRow2);
            keyboardRows.add(keyboardRow3);

            replyKeyboardMarkup.setKeyboard(keyboardRows);
            replyKeyboardMarkup.setResizeKeyboard(true);
            replyKeyboardMarkup.setOneTimeKeyboard(true);
            sendMessage.setReplyMarkup(replyKeyboardMarkup);
            sendMyMessage(sendMessage);
        }

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

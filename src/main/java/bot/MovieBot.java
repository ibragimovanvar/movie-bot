package bot;

import consts.MovieBotConstants;
import db.DatabaseObjects;
import model.Movie;
import model.MovieCategory;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendVideo;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import user.TelegramUser;
import user.TelegramUserState;

import javax.xml.crypto.Data;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MovieBot extends TelegramLongPollingBot {

    @Override
    public void onUpdateReceived(Update update) {
        String chatId = update.getMessage().getChatId().toString();
        String text = update.getMessage().getText();

        TelegramUser currentUser = null;

        for (TelegramUser telegramUser : DatabaseObjects.telegramUsers) {
            if (telegramUser.getChatId().equals(update.getMessage().getChatId().toString())) {
                currentUser = telegramUser;
            }
        }

        if (currentUser == null) {
            currentUser = new TelegramUser();
            currentUser.setChatId(chatId);
            DatabaseObjects.telegramUsers.add(currentUser);
        }

        if (text.equals("/start")) {
            sendStarterKeyboard(chatId, currentUser);
        }
        if(text.equals(MovieBotConstants.backButton)){
            currentUser.setState(TelegramUserState.MAIN_MENU);
        }
        if(currentUser.getState().equals(TelegramUserState.CATEGORY)){
            List<Movie> moviesByCategory = DatabaseObjects.getMoviesByCategory(text);
            if(moviesByCategory.size() > 0){
                for (Movie movie : moviesByCategory) {
                    SendVideo sendVideo = new SendVideo(chatId, new InputFile(new File(movie.getUrl())));
                    sendVideo.setCaption(movieCaptionGenerator(movie));
                    sendMyMessage(sendVideo);
                }
            }else{
                SendMessage sendMessage = new SendMessage(chatId, "Bu kategoriyada kinolar topilmadi!");
                sendMyMessage(sendMessage);
            }
        }
        if (currentUser.getState().equals(TelegramUserState.MAIN_MENU)) {
            sendStarterKeyboard(chatId, currentUser);
            switch (text) {
                case MovieBotConstants.movieStarterButton1 -> {
                    SendMessage sendMessage = new SendMessage(chatId, "Izlayotgan kinoni raqamini kiriting!");
                    sendMyMessage(sendMessage);
                }
                case MovieBotConstants.movieStarterButton2 -> {
                    SendMessage sendMessage = new SendMessage(chatId, "Kategoriyani tanlang!");
                    sendMyMessage(sendMessage);
                    sendCategoryKeyboard(chatId, currentUser);
                }
                case MovieBotConstants.movieStarterButton3 -> {
                    SendMessage sendMessage = new SendMessage(chatId, "Eng sara kinolar ro'yxati");
                    sendMyMessage(sendMessage);
                }
                case MovieBotConstants.movieStarterButton4 -> {
                    SendMessage sendMessage = new SendMessage(chatId, "Haftadagi TOP kinolar ro'yxati:");
                    sendMyMessage(sendMessage);
                }
            }
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

    private void sendStarterKeyboard(String chatId, TelegramUser user) {
        SendMessage sendMessage = new SendMessage(chatId, "Starter tugmalardan birini tanlang");
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();

        List<KeyboardRow> keyboardRows = new ArrayList<>();

        KeyboardRow keyboardRow1 = new KeyboardRow();
        KeyboardRow keyboardRow2 = new KeyboardRow();
        KeyboardRow keyboardRow3 = new KeyboardRow();

        KeyboardButton keyboardButton1 = new KeyboardButton(MovieBotConstants.movieStarterButton1);
        KeyboardButton keyboardButton2 = new KeyboardButton(MovieBotConstants.movieStarterButton2);
        KeyboardButton keyboardButton3 = new KeyboardButton(MovieBotConstants.movieStarterButton3);
        KeyboardButton keyboardButton4 = new KeyboardButton(MovieBotConstants.movieStarterButton4);

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

        user.setState(TelegramUserState.MAIN_MENU);

        sendMyMessage(sendMessage);
    }
    private void sendCategoryKeyboard(String chatId, TelegramUser user) {
        SendMessage sendMessage = new SendMessage(chatId, "Starter tugmalardan birini tanlang");
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboardRows = new ArrayList<>();

        for (MovieCategory movieCategory : DatabaseObjects.movieCategories) {
            KeyboardRow keyboardRow = new KeyboardRow();
            KeyboardButton keyboardButton = new KeyboardButton(movieCategory.getName());
            keyboardRow.add(keyboardButton);
            keyboardRows.add(keyboardRow);
        }

        KeyboardRow keyboardRow = new KeyboardRow();
        KeyboardButton keyboardButton = new KeyboardButton(MovieBotConstants.backButton);
        keyboardRow.add(keyboardButton);
        keyboardRows.add(keyboardRow);

        replyKeyboardMarkup.setKeyboard(keyboardRows);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(true);
        sendMessage.setReplyMarkup(replyKeyboardMarkup);

        user.setState(TelegramUserState.CATEGORY);
        sendMyMessage(sendMessage);
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
        return "token";
    }
}

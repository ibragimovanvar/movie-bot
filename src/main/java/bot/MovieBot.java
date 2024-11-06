package bot;

import buttons.ButtonUtil;
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
import utils.MovieUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MovieBot extends TelegramLongPollingBot {

    @Override
    public void onUpdateReceived(Update update) {
        String chatId = update.getMessage().getChatId().toString();
        String text = update.getMessage().getText();

        TelegramUser currentUser = registerOrGetCurrentUser(chatId, update);

        if (text.equals("/start") || text.equals(MovieBotConstants.backButton)) {
            SendMessage sendMessage = ButtonUtil.sendStarterKeyboard(chatId, currentUser);
            messageSender(sendMessage);
        }

        if(currentUser.getState().equals(TelegramUserState.CATEGORY)){
            sendCategoryButtons(chatId, text, currentUser);
        }else if(currentUser.getState().equals(TelegramUserState.CATEGORY_FILM)){
            sendCategoryFilmButtons(chatId, text, currentUser);
        }else if(currentUser.getState().equals(TelegramUserState.CATEGORY_FILM_CHOSEN)){
            sendChosenFilm(chatId, text);
        }else if(currentUser.getState().equals(TelegramUserState.MAIN_MENU)){
            sendStarterButtons(text, chatId, currentUser);
        }

    }

    private void sendChosenFilm(String chatId, String text) {
        Movie movieByName = DatabaseObjects.getMovieByName(text.substring(3));
        if(movieByName == null){
            SendMessage sendMessage = new SendMessage(chatId, "Bunday kino topilmadi!");
            messageSender(sendMessage);
        }else{
            SendVideo sendVideo = new SendVideo();
            sendVideo.setChatId(chatId);
            sendVideo.setVideo(new InputFile(new File(movieByName.getUrl())));
            sendVideo.setCaption(MovieUtil.movieCaptionGenerator(movieByName));
            messageSender(sendVideo);
        }
    }
    private void sendCategoryFilmButtons(String chatId, String text, TelegramUser currentUser) {
        List<Movie> moviesByCategory = DatabaseObjects.getMoviesByCategory(text);
        SendMessage sendMessage = new SendMessage(chatId, "Kinolardan birini tanlang!");
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboardRowList = new ArrayList<>();
        if(moviesByCategory.size() > 0){
            int i = 1;
            for (Movie movie : moviesByCategory) {
                KeyboardRow keyboardRow = new KeyboardRow();
                KeyboardButton keyboardButton = new KeyboardButton();
                keyboardButton.setText(i + ". " +movie.getName());
                keyboardRow.add(keyboardButton);
                keyboardRowList.add(keyboardRow);
                i++;
            }
        }else{
            sendMessage = new SendMessage(chatId, "Bu kategoriyada kinolar topilmadi!");
            messageSender(sendMessage);
        }
        KeyboardRow backButtonRow = new KeyboardRow();
        KeyboardButton backButton = new KeyboardButton();
        backButton.setText(MovieBotConstants.backButton);
        backButtonRow.add(backButton);
        keyboardRowList.add(backButtonRow);
        replyKeyboardMarkup.setKeyboard(keyboardRowList);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(true);
        sendMessage.setReplyMarkup(replyKeyboardMarkup);
        messageSender(sendMessage);
        currentUser.setState(TelegramUserState.CATEGORY_FILM_CHOSEN);
    }
    private TelegramUser registerOrGetCurrentUser(String chatId, Update update) {
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

        return currentUser;
    }
    private void sendStarterButtons(String text, String chatId, TelegramUser currentUser) {
        switch (text) {
            case MovieBotConstants.movieStarterButton1 -> {
                SendMessage sendMessage = new SendMessage(chatId, "Izlayotgan kinoni raqamini kiriting!");
                messageSender(sendMessage);
            }
            case MovieBotConstants.movieStarterButton2 -> {
                SendMessage sendMessage = new SendMessage(chatId, "Kategoriyani tanlang!");
                messageSender(sendMessage);
                sendCategoryKeyboard(chatId, currentUser);
            }
            case MovieBotConstants.movieStarterButton3 -> {
                SendMessage sendMessage = new SendMessage(chatId, "Eng sara kinolar ro'yxati");
                messageSender(sendMessage);
            }
            case MovieBotConstants.movieStarterButton4 -> {
                SendMessage sendMessage = new SendMessage(chatId, "Haftadagi TOP kinolar ro'yxati:");
                messageSender(sendMessage);
            }
        }
    }
    private void sendCategoryButtons(String chatId, String text, TelegramUser currentUser) {
        List<Movie> moviesByCategory = DatabaseObjects.getMoviesByCategory(text);
        if(moviesByCategory.size() > 0){
            for (Movie movie : moviesByCategory) {
                SendVideo sendVideo = new SendVideo(chatId, new InputFile(new File(movie.getUrl())));
                sendVideo.setCaption(MovieUtil.movieCaptionGenerator(movie));
                messageSender(sendVideo);
            }
        }else{
            SendMessage sendMessage = new SendMessage(chatId, "Bu kategoriyada kinolar topilmadi!");
            messageSender(sendMessage);
        }
        currentUser.setState(TelegramUserState.CATEGORY_FILM);
    }
    private void sendCategoryKeyboard(String chatId, TelegramUser user) {
        SendMessage sendMessage = new SendMessage(chatId, "Category tugmalardan birini tanlang");
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
        messageSender(sendMessage);
    }

    private void messageSender(SendMessage sendMessage) {
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
    private void messageSender(SendVideo sendVideo) {
        try {
            execute(sendVideo);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
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

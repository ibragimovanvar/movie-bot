package buttons;

import consts.MovieBotConstants;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import user.TelegramUser;
import user.TelegramUserState;

import java.util.ArrayList;
import java.util.List;

public class ButtonUtil {

    public static SendMessage sendStarterKeyboard(String chatId, TelegramUser user) {
        SendMessage sendMessage = new SendMessage(chatId, "Menyuga xush kelibsiz biror knopkani tanlang");
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
        return sendMessage;
    }

}

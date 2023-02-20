package isthatkirill.fileConverterBot.service;

import isthatkirill.fileConverterBot.config.BotConfig;
import isthatkirill.fileConverterBot.utils.DownloadFile;
import isthatkirill.fileConverterBot.utils.DownloadPhoto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.File;

@Slf4j
@Component
public class TelegramBot extends TelegramLongPollingBot {

    final BotConfig config;

    public TelegramBot(BotConfig config) {
        this.config = config;
    }

    @Override
    public String getBotUsername() {
        return config.getBotName();
    }

    @Override
    public String getBotToken() {
        return config.getToken();
    }

    @Override
    public void onUpdateReceived(Update update) {

        if (update.getMessage().hasDocument()) {
            DownloadFile df = new DownloadFile(config);
            df.download(update);
        } else if (update.getMessage().hasPhoto()) {
            DownloadPhoto dp = new DownloadPhoto(config);
            dp.download(update);
        }

        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            //long chatId = update.getMessage().getChatId();

            if ("/start".equals(messageText)) {
                registerUser(update.getMessage());
                log.info("[/start] " + update.getMessage().getChat().getFirstName());
            }
        }
    }

    private void registerUser(Message message) {

        File directory = new File("src/main/resources/uploadedFiles/" + message.getChat().getUserName());
        File dirPhotos = new File("src/main/resources/uploadedFiles/" + message.getChat().getUserName() + "/photos");
        File dirFiles = new File("src/main/resources/uploadedFiles/" + message.getChat().getUserName() + "/files");
        if (!directory.exists()) {
            directory.mkdir();
            dirPhotos.mkdir();
            dirFiles.mkdir();
        }

        log.info("User's directory created: " + message.getChat().getUserName());
    }

}

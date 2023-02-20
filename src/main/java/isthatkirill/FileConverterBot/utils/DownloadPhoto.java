package isthatkirill.fileConverterBot.utils;

import isthatkirill.fileConverterBot.config.BotConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;


@Slf4j
public class DownloadPhoto extends DownloadFile {

    private List<PhotoSize> photoList;

    public DownloadPhoto(BotConfig config) {
        super(config);
    }

    @Override
    public void download(Update update) {
        photoList = update.getMessage().getPhoto();
        userName = update.getMessage().getChat().getUserName();

        for (int i = 3; i < photoList.size(); i += 3) {

            fileId = photoList.get(i).getFileId();
            filename = photoList.get(i).getFileUniqueId();

            try {
                File localFile = new File("src/main/resources/uploadedFiles/" + userName + "/photos/" + filename + ".jpeg");
                InputStream is = new URL("https://api.telegram.org/file/bot" + config.getToken() + "/" + formatLink(fileId)).openStream();
                FileUtils.copyInputStreamToFile(is, localFile);

                br.close();
                is.close();

            } catch (IOException e) {
                log.error("Error while downloading file: " + e.getMessage());
            }
        }
    }

}

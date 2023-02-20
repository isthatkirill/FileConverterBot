package isthatkirill.fileConverterBot.utils;

import isthatkirill.fileConverterBot.config.BotConfig;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.json.JSONObject;
import org.telegram.telegrambots.meta.api.objects.Document;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.*;
import java.net.URL;

@Data
@Slf4j
public class DownloadFile implements Downloable {

    protected Document document;
    protected String fileId;
    protected String filename;
    protected BotConfig config;
    protected BufferedReader br;
    protected String userName;

    public DownloadFile(BotConfig config) {
        this.config = config;
    }

    @Override
    public void download(Update update) {
        document = update.getMessage().getDocument();
        fileId = document.getFileId();
        filename = document.getFileName();
        userName = update.getMessage().getChat().getUserName();

        try {
            File localFile = new File("src/main/resources/uploadedFiles/" + userName + "/files/" + filename);
            InputStream is = new URL("https://api.telegram.org/file/bot" + config.getToken() + "/" + formatLink(fileId)).openStream();
            FileUtils.copyInputStreamToFile(is, localFile);

            br.close();
            is.close();

        } catch (IOException e) {
            log.error("Error while downloading file: " + e.getMessage());
        }
    }

    protected String formatLink(String fileId) throws IOException {
        URL url = new URL("https://api.telegram.org/bot" + config.getToken() + "/getFile?file_id=" + fileId);
        br = new BufferedReader(new InputStreamReader(url.openStream()));

        String getFileResponse = br.readLine();

        JSONObject result = new JSONObject(getFileResponse);
        JSONObject path = result.getJSONObject("result");

        return path.getString("file_path");
    }
}

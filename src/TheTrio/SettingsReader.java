package TheTrio;
import com.google.gson.Gson;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;

public class SettingsReader {
    Settings currentSetting;
    public SettingsReader(){
        String inputString = "";
        try {
            inputString = new String(Files.readAllBytes(Paths.get("C:\\Users\\Shashwat\\Desktop\\SlickConvert\\src\\Settings.json")));
        } catch (IOException e) {
            e.printStackTrace();
        }
        Gson gson = new Gson();
        currentSetting = gson.fromJson(inputString, Settings.class);
    }

    public String getTheme() {
        return currentSetting.getTheme();
    }

    public void setTheme(String theme) {
        currentSetting.setTheme(theme);
    }

    public boolean getMultipleThreading() {
        return currentSetting.getMultipleThreading().equals("Enabled");
    }

    public void setMultipleThreading(boolean multipleThreading) {
        currentSetting.setMultipleThreading(multipleThreading?"Enabled":"Disabled");
    }

    public String getSaveTo() {
        return currentSetting.getSaveTo();
    }

    public void setSaveTo(String saveTo) {
        currentSetting.setSaveTo(saveTo);
    }
}

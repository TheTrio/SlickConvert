package TheTrio;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

public class SettingsReader {
    Settings currentSetting;
    public SettingsReader(){
        Gson gson = new Gson();
        try {
            currentSetting = gson.fromJson(new FileReader(new File("Settings.json")), Settings.class);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
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

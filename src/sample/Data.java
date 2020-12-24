package sample;

import javafx.scene.control.Toggle;

public class Data {
    private String name;
    private String inputFormat;
    private String outputFormat;
    private String fullPath;
    private String done;
    private Toggle selectedToggle;

    public Data(String name, String fullPath, String format) {
        this.name = name;
        this.inputFormat = format;
        this.fullPath = fullPath;
        this.done = "Not started";
        this.outputFormat = "Not selected";
        this.selectedToggle = null;
    }

    public Data() {
        this.name = "";
        this.inputFormat = "";
    }

    public boolean isExtensionSet(){
        return !outputFormat.equals("Not selected");
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFullPath() {
        return fullPath;
    }

    public void setFullPath(String fullPath) {
        this.fullPath = fullPath;
    }

    public String getDone() {
        return done;
    }

    public void setDone(String done) {
        this.done = done;
    }

    public String getOutputFormat() {
        return outputFormat;
    }

    public void setOutputFormat(String outputFormat) {
        this.outputFormat = outputFormat;
    }

    public String getInputFormat() {
        return inputFormat;
    }

    public void setInputFormat(String inputFormat) {
        this.inputFormat = inputFormat;
    }

    public Toggle getSelectedToggle() {
        return selectedToggle;
    }

    public void setSelectedToggle(Toggle selectedToggle) {
        this.selectedToggle = selectedToggle;
    }
}

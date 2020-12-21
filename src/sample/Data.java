package sample;
public class Data {
    private String name;
    private String format;
    private String fullPath;
    private String done;

    public Data(String name, String fullPath, String format) {
        this.name = name;
        this.format = format;
        this.fullPath = fullPath;
        this.done = "Not started";
    }

    public Data(String name, String fullPath, String format, String done) {
        this.name = name;
        this.format = format;
        this.fullPath = fullPath;
        this.done = done;
    }

    public Data() {
        this.name = "";
        this.format = "";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
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
}

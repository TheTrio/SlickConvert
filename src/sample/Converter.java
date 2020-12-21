package sample;
import javafx.application.Platform;
import javafx.scene.control.ProgressBar;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
public class Converter implements Runnable{

    private BufferedReader b;
    private Pattern p;
    private Matcher m;
    public double percent = 0;
    private int duration = 0;
    private ProgressBar progressBar;
    private boolean isTrue = false;
    private String Item;

    public Converter(BufferedReader b, Pattern p, Matcher m, int duration, ProgressBar progressBar, String Item) {
        this.b = b;
        this.p = p;
        this.m = m;
        this.duration = duration;
        this.progressBar = progressBar;
        this.Item = Item;
    }

    @Override
    public void run() {
        String s;
        try {
            while((s=b.readLine())!=null){
                if(isTrue){
                    System.out.println("Quiting");
                    return;
                }
                System.out.println(s);
                m = p.matcher(s);
                if(m.find()){
                    System.out.println("hello world");
                    percent = Integer.parseInt(m.group(1))* 3600 + Integer.parseInt(m.group(2))* 60+ Integer.parseInt(m.group(3));
                }
                Platform.runLater(()->{
                    progressBar.setProgress(percent/duration);
                });
            }
            System.out.println(Item + " completed");
            //progressBar.getScene().getWindow().hide();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setTrue(boolean value){
        isTrue = value;
    }
}

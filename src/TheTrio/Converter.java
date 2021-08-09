package TheTrio;
import javafx.application.Platform;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TableView;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
public class Converter implements Runnable{

    private final BufferedReader b;
    private final Pattern p;
    private Matcher m;
    public double percent = 0;
    private int duration = 0;
    private final ProgressBar progressBar;
    private boolean isTrue = false;
    private final String Item;
    private final Data data;
    private final TableView<Data> tableView;
    public static int totalDuration;
    private final int doneBefore;
    public static double durationDone = 0;
    private double prevDuration;
    public static int noOfThreads = -1;

    public Converter(BufferedReader b, Pattern p, int duration, ProgressBar progressBar, String Item, Data data, TableView<Data> tableView, int totalDuration, int doneBefore) {
        this.b = b;
        this.p = p;
        this.duration = duration;
        this.progressBar = progressBar;
        this.Item = Item;
        this.data = data;
        this.tableView = tableView;
        Converter.totalDuration = totalDuration;
        this.doneBefore = doneBefore;
        if(noOfThreads==-1) noOfThreads = 0;
        Converter.noOfThreads++;
    }

    @Override
    public void run() {
        String s;
        try {
            while((s=b.readLine())!=null){
                System.out.println(s);
                if(isTrue){
                    return;
                }
                m = p.matcher(s);
                if(m.find()){
                    percent = Integer.parseInt(m.group(1))* 3600 + Integer.parseInt(m.group(2))* 60+ Integer.parseInt(m.group(3));
                    durationDone += (percent-prevDuration);
                    prevDuration = percent;
                    String output = String.format("%.2f", percent/duration*100);
                    data.setDone(output + "% done");
                    tableView.refresh();
                }
                if(!Controller.multipleThreads) {
                    Platform.runLater(()->{
                        progressBar.setProgress((percent+doneBefore)/totalDuration);
                    });
                }
            }
            System.out.println(Item + " completed");
            Platform.runLater(()->{
                progressBar.setProgress(1);
            });
            noOfThreads--;
            if(noOfThreads==0){
                noOfThreads = -1;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public double getPercent(){
        return percent;
    }
}

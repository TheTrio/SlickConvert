package sample;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.URL;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Controller implements Initializable {


    @FXML
    private BorderPane bp;

    private String FilePath;

    @FXML
    private JFXButton convertBut;

    @FXML
    private ToggleGroup group;

    @FXML
    private Spinner<Integer> frameRate;

    @FXML
    private Spinner<Integer> height;

    @FXML
    private Spinner<Integer> width;

    @FXML
    private JFXCheckBox presRatio;

    @FXML
    private JFXCheckBox inputSetting;

    @FXML
    private ProgressBar bar;

    public TreeView<String> treeView;
    private String selected;
    private String ItemName;
    private HashSet<String> set;
    private HashMap<String, String> map;
    private ObservableList<Data> list;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        map = new HashMap<>();
        list = FXCollections.observableArrayList();
        String name = "computer";
        try {
            name = InetAddress.getLocalHost().getHostName();
        } catch (Exception e) {

        }
        bar.prefWidthProperty().bind(bp.widthProperty());
        //Name Column
        TableColumn<Data, String> nameColumn = new TableColumn<>("FileName");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameColumn.setPrefWidth(200);

        //FilePath Column
        TableColumn<Data, String> filePathC = new TableColumn<>("Path");
        filePathC.setCellValueFactory(new PropertyValueFactory<>("fullPath"));
        filePathC.setPrefWidth(300);

        //Format Column
        TableColumn<Data, String> formatColumn = new TableColumn<>("Input Format");
        formatColumn.setCellValueFactory(new PropertyValueFactory<>("format"));
        formatColumn.setPrefWidth(150);

        TableColumn<Data, String> percentageDone = new TableColumn<>("Percentage Converted");
        percentageDone.setCellValueFactory(new PropertyValueFactory<>("done"));
        percentageDone.setPrefWidth(170);

        TableView<Data> tableView = new TableView<>();
        tableView.setItems(list);
        tableView.getColumns().addAll(nameColumn, filePathC, formatColumn, percentageDone);
        tableView.setOnMousePressed(e -> {
            if (e.isPrimaryButtonDown() && e.getClickCount() == 2) {
                set.remove(tableView.getSelectionModel().getSelectedItem().getFullPath());
                list.remove(tableView.getSelectionModel().getSelectedItem());
            }
        });
        tableView.setOnDragOver(e -> {
            if (e.getDragboard().hasFiles()) {
                e.acceptTransferModes(TransferMode.ANY);
            }
            e.consume();
        });
        tableView.setOnDragDropped(e -> {
            if (e.getDragboard().hasFiles()) {
                List<File> files = e.getDragboard().getFiles();
                for (File f : files) {
                    if (f.isFile()) {
                        addFile(f.getName(), f.getPath());
                    }
                }
            }
            e.consume();
        });
        bp.setCenter(tableView);

        set = new HashSet<>();

        TreeItem<String> rootNode = new TreeItem<>(name);
        Iterable<Path> rootDirectories = FileSystems.getDefault().getRootDirectories();
        for (Path names : rootDirectories) {
            FilePathTreeItem treeNode = new FilePathTreeItem(names.toFile());
            rootNode.getChildren().add(treeNode);
        }
        rootNode.setExpanded(true);

        treeView = new TreeView<>(rootNode);
        //add everything to the tree pane

        treeView.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.getClickCount() == 2) {
                    if (event.getSource() != null) {
                        String filePath = (getString(treeView.getSelectionModel().getSelectedItem()).replace(treeView.getRoot().getValue(), "").replaceAll("//", "").replaceAll("(\\\\)/", "$1"));
                        FilePath = filePath;
                        ItemName = treeView.getSelectionModel().getSelectedItem().getValue();
                        Pattern p = Pattern.compile(".*(\\..*)");
                        Matcher m = p.matcher(ItemName);
                        String extension = null;
                        if (m.find()) {
                            addFile(ItemName, filePath);
                        }

                    }
                }
            }


        });
        group.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
            @Override
            public void changed(ObservableValue<? extends Toggle> observableValue, Toggle old, Toggle newT) {
                selected = (newT.toString().replaceAll(".*'(.*)'", "$1"));
            }
        });

        convertBut.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                if (selected != null) {
                    try {
                        List<Integer> durationList = new ArrayList<>();
                        AtomicInteger totalDuration = new AtomicInteger(0);
                        for (int i = 0; i < list.size(); i++) {
                            Data data = list.get(i);
                            String FilePath = data.getFullPath();
                            ItemName = map.get(FilePath);
                            String extension = ItemName.replaceAll(".*(\\..*)", "$1");
                            ItemName = ItemName.replace(extension, "");
                            ItemName = ItemName + "1";
                            ItemName = "\"" + ItemName + "." + selected + "\"";
                            FilePath = "\"" + FilePath + "\"";

                            Process p = new ProcessBuilder("cmd", "/c", "ffmpeg", "-i", FilePath).start();
                            BufferedReader b = new BufferedReader(new InputStreamReader(p.getErrorStream()));
                            String string = "";
                            Pattern pattern = Pattern.compile("Duration: (\\d\\d):(\\d\\d):(\\d\\d)\\.\\d\\d,");
                            Matcher matcher = null;

                            while ((string = b.readLine()) != null) {
                                matcher = pattern.matcher(string);
                                if (matcher.find()) {
                                    totalDuration.getAndAdd(Integer.parseInt(matcher.group(1)) * 3600 + Integer.parseInt(matcher.group(2)) * 60 + Integer.parseInt(matcher.group(3)));
                                    durationList.add(Integer.parseInt(matcher.group(1)) * 3600 + Integer.parseInt(matcher.group(2)) * 60 + Integer.parseInt(matcher.group(3)));
                                    break;
                                }
                            }
                        }
                        Thread backgroundThread = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                int doneBefore = 0;
                                for (int i = 0; i < list.size(); i++) {
                                    Data data = list.get(i);
                                    String FilePath = data.getFullPath();
                                    ItemName = map.get(FilePath);
                                    String extension = ItemName.replaceAll(".*(\\..*)", "$1");
                                    ItemName = ItemName.replace(extension, "");
                                    ItemName = ItemName + "1";
                                    ItemName = "\"" + ItemName + "." + selected + "\"";
                                    FilePath = "\"" + FilePath + "\"";
                                    Process d = null;
                                    try {
                                        d = new ProcessBuilder("cmd", "/c", "ffmpeg", "-i", FilePath, ItemName).start();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    BufferedReader b = new BufferedReader(new InputStreamReader(d.getErrorStream()));
                                    Pattern pattern = Pattern.compile("time=(\\d\\d):(\\d\\d):(\\d\\d)\\.\\d\\d");
                                    Converter c = new Converter(b, pattern,durationList.get(i), bar, ItemName, data, tableView, totalDuration.get(), doneBefore);

                                    Thread thread = new Thread(c);
                                    thread.start();
                                    try {
                                        thread.join();
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                doneBefore += durationList.get(i);
                                }
                                convertBut.setDisable(false);
                                list.clear();
                            }
                        });
                        backgroundThread.start();
                        convertBut.setDisable(true);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                } else {
                    //Error Dialog
                }
            }
        });
        bp.setLeft(treeView);
        inputSetting.selectedProperty().

                addListener(new ChangeListener<Boolean>() {
                    @Override
                    public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean
                            newValue) {
                        presRatio.setDisable(!presRatio.isDisable());
                        frameRate.setDisable(!frameRate.isDisable());
                        height.setDisable(!height.isDisable());
                        width.setDisable(!width.isDisable());
                    }
                });

    }

    private void addFile(String itemName, String filePath) {
        String extension = (itemName.replaceAll(".*(\\..*)", "$1"));
        if (!set.contains(filePath)) {
            list.add(new Data(itemName.replace(extension, ""), filePath, extension));
            map.put(filePath, itemName);
            set.add(filePath);
            ItemName = itemName;
        }
    }

    public String getString(TreeItem<String> treeView) {
        if (treeView == null)
            return "";
        return getString(treeView.getParent()) + "/" + treeView.getValue();
    }


}

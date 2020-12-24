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
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.*;

import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.URL;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.*;
import java.util.List;
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
    private String ItemName;
    private HashSet<String> set;
    private HashMap<String, String> map;
    private ObservableList<Data> list;
    private MenuBar menuBar;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        menuBar = new MenuBar();
        Menu fileMenu = new Menu("File");
        Menu helpMenu = new Menu("Help");
        menuBar.getMenus().addAll(fileMenu, helpMenu);
        bp.setTop(menuBar);

        map = new HashMap<>();
        list = FXCollections.observableArrayList();
        String name = "computer";
        try {
            name = InetAddress.getLocalHost().getHostName();
        } catch (Exception e) {

        }
        TableView<Data> tableView = new TableView<>();
        bar.prefWidthProperty().bind(bp.widthProperty());
        //Name Column
        TableColumn<Data, String> nameColumn = new TableColumn<>("FileName");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameColumn.prefWidthProperty().bind(tableView.widthProperty().multiply(350.0/833.0));

        //Input Format Column
        TableColumn<Data, String> inputFormatColumn = new TableColumn<>("Input Format");
        inputFormatColumn.setCellValueFactory(new PropertyValueFactory<>("inputFormat"));
        inputFormatColumn.prefWidthProperty().bind(tableView.widthProperty().multiply(151.0/833.0));

        //Input Format Column
        TableColumn<Data, String> outputFormatColumn = new TableColumn<>("Output Format");
        outputFormatColumn.setCellValueFactory(new PropertyValueFactory<>("outputFormat"));
        outputFormatColumn.prefWidthProperty().bind(tableView.widthProperty().multiply(150.0/833.0));

        TableColumn<Data, String> percentageDone = new TableColumn<>("Percentage Converted");
        percentageDone.setCellValueFactory(new PropertyValueFactory<>("done"));
        percentageDone.prefWidthProperty().bind(tableView.widthProperty().multiply(182.0/833.0));

        tableView.setItems(list);
        tableView.getColumns().addAll(nameColumn, inputFormatColumn,outputFormatColumn , percentageDone);
        tableView.setOnMousePressed(e -> {
            if (e.isPrimaryButtonDown() && e.getClickCount() == 2) {
                set.remove(tableView.getSelectionModel().getSelectedItem().getFullPath());
                list.remove(tableView.getSelectionModel().getSelectedItem());
            }else if(e.isPrimaryButtonDown()){
                Data selectedRow = tableView.getSelectionModel().getSelectedItem();
                if(selectedRow!=null && selectedRow.isExtensionSet()){
                    group.selectToggle(selectedRow.getSelectedToggle());
                }else{
                    group.selectToggle(null);
                }
            }
        });
        group.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
            @Override
            public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {
                if (newValue != null) {
                    String extracted = newValue.toString();
                    Pattern togglePattern = Pattern.compile("'(\\w+)'");
                    Matcher matcherToggle = togglePattern.matcher(extracted);
                    if (matcherToggle.find()) {
                        extracted = matcherToggle.group(1);
                    }
                    if (tableView.getSelectionModel().getSelectedItem() == null) {
                        System.out.println("Please select an item");
                    } else {
                        tableView.getSelectionModel().getSelectedItem().setOutputFormat(extracted);
                        tableView.getSelectionModel().getSelectedItem().setSelectedToggle(newValue);
                        tableView.refresh();
                    }
                }
            }
        });
        tableView.setPlaceholder(new Label("Drag and Drop files here, or choose them from the Slick File Explorer on the left.\n"));
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

        convertBut.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                boolean selected = true;
                for(Data d: list){
                    if(!d.isExtensionSet()) {
                        selected = false;
                        break;
                    }
                }
                if (selected) {
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
                            ItemName = "\"" + ItemName + "." + data.getOutputFormat() + "\"";
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
                                    String tempOutputName = ItemName;
                                    ItemName = "\"" + ItemName + "." + data.getOutputFormat() + "\"";
                                    int fileIndex = 0;
                                    while(true) {
                                        File outputFile = new File(ItemName.replace("\"", ""));
                                        if (outputFile.exists()) {
                                            ItemName = tempOutputName + "_copy_"+ fileIndex;
                                            ItemName = "\"" + ItemName + "." + data.getOutputFormat() + "\"";
                                            fileIndex++;
                                        }else{
                                            System.out.println("Breaking!");
                                            break;
                                        }
                                    }
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
                                set.clear();
                                map.clear();
                                list.clear();
                                group.selectToggle(null);
                                bar.setProgress(0);
                                tableView.refresh();
                                SystemTray tray = SystemTray.getSystemTray();

                                //If the icon is a file
//                                Image image = Toolkit.getDefaultToolkit().createImage("icon.png");
                                //Alternative (if the icon is on the classpath):
                                Image image = Toolkit.getDefaultToolkit().createImage(getClass().getResource("icon.png"));
//
                                TrayIcon trayIcon = new TrayIcon(image, "Tray Demo");
//                                //Let the system resize the image if needed
                                trayIcon.setImageAutoSize(true);
                                //Set tooltip text for the tray icon
                                trayIcon.setToolTip("SlickConverter");
                                try {
                                    tray.add(trayIcon);
                                } catch (AWTException e) {
                                    e.printStackTrace();
                                }

                                trayIcon.displayMessage("Yours files have been converted successfully", "Slick Converter", TrayIcon.MessageType.INFO);
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

package sample;

import java.io.File;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.TreeItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

public class FilePathTreeItem extends TreeItem<String> {
    public static Image folderCollapseImage = new Image(ClassLoader.getSystemResourceAsStream("folder.png"));
    public static Image fileImage = new Image(ClassLoader.getSystemResourceAsStream("text-x-generic.png"));
    private boolean isLeaf;
    private boolean isFirstTimeChildren = true;
    private boolean isFirstTimeLeaf = true;
    private final File file;
    private final String absolutePath;
    private final boolean isDirectory;

    public File getFile() {
        return this.file;
    }

    public String getAbsolutePath() {
        return this.absolutePath;
    }

    public boolean isDirectory() {
        return this.isDirectory;
    }

    public FilePathTreeItem(File file) {
        super(file.toString());
        this.file = file;
        this.absolutePath = file.getAbsolutePath();
        this.isDirectory = file.isDirectory();
        if(this.isDirectory) {
            this.setGraphic(new ImageView(folderCollapseImage));
            this.addEventHandler(TreeItem.branchCollapsedEvent(), new EventHandler() {
                public void handle(Event e) {
                    FilePathTreeItem source = (FilePathTreeItem)e.getSource();
                    if(!source.isExpanded()) {
                        ImageView iv = (ImageView)source.getGraphic();
                        iv.setImage(FilePathTreeItem.folderCollapseImage);
                    }

                }
            });
        } else {
            this.setGraphic(new ImageView(fileImage));
        }

        String fullPath = file.getAbsolutePath();
        if(!fullPath.endsWith(File.separator)) {
            String value = file.toString();
            int indexOf = value.lastIndexOf(File.separator);
            if(indexOf > 0) {
                this.setValue(value.substring(indexOf + 1));
            } else {
                this.setValue(value);
            }
        }

    }

    public ObservableList<TreeItem<String>> getChildren() {
        if(this.isFirstTimeChildren) {
            this.isFirstTimeChildren = false;
            super.getChildren().setAll(this.buildChildren(this));
        }

        return super.getChildren();
    }

    public boolean isLeaf() {
        if(this.isFirstTimeLeaf) {
            this.isFirstTimeLeaf = false;
            this.isLeaf = this.file.isFile();
        }

        return this.isLeaf;
    }

    private ObservableList<FilePathTreeItem> buildChildren(FilePathTreeItem treeItem) {
        File f = treeItem.getFile();
        if(f != null && f.isDirectory()) {
            File[] files = f.listFiles();
            if(files != null) {
                ObservableList children = FXCollections.observableArrayList();
                File[] var5 = files;
                int var6 = files.length;

                for(int var7 = 0; var7 < var6; ++var7) {
                    File childFile = var5[var7];
                    if(!childFile.isDirectory()){
                        if(childFile.getName().endsWith("mkv") || childFile.getName().endsWith("mp4")|| childFile.getName().endsWith("avi")|| childFile.getName().endsWith("webm")|| childFile.getName().endsWith("flv")|| childFile.getName().endsWith("mp3")|| childFile.getName().endsWith("gif")|| childFile.getName().endsWith("wmv")|| childFile.getName().endsWith("m4a")|| childFile.getName().endsWith("mp4")|| childFile.getName().endsWith("mpg")|| childFile.getName().endsWith("vob")|| childFile.getName().endsWith("mov")|| childFile.getName().endsWith("3gp")|| childFile.getName().endsWith("m4v")){
                            children.add(new FilePathTreeItem(childFile));
                        }
                    }else{
                        children.add(new FilePathTreeItem(childFile));
                    }
                }

                return children;
            }
        }

        return FXCollections.emptyObservableList();
    }
}

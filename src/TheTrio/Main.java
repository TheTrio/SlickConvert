package TheTrio;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("Window.fxml"));
        primaryStage.setTitle("Slick Convert");
        primaryStage.setScene(new Scene(root, 1366, 768));
        primaryStage.setMinHeight(820);
        primaryStage.setMinWidth(1366);
        primaryStage.show();
        primaryStage.setOnCloseRequest(e->{
            System.exit(0);
        });
    }


    public static void main(String[] args) {
        launch(args);
    }
}

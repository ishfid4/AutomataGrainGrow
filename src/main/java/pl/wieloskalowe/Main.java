package pl.wieloskalowe;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Created by ishfi on 02.05.2017.
 */
public class Main extends Application{

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/layout/automatonWindow.fxml"));
        Parent root = loader.load();

        primaryStage.setTitle("Multiscale Modeling");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }
}

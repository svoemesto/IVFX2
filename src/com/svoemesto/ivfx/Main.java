package com.svoemesto.ivfx;

import com.svoemesto.ivfx.tables.Database;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.sql.Connection;

public class Main extends Application {

    public static Stage mainWindow;
    public static Connection mainConnection;


    @Override
    public void start(Stage primaryStage) throws Exception{

        mainWindow = primaryStage;
        mainConnection = Database.getConnection();

        Parent root = FXMLLoader.load(getClass().getResource("resources/fxml/project.fxml"));
        primaryStage.setTitle("Interactive Video FX © svoёmesto");
        Scene scene = new Scene(root, 800, 800);
        primaryStage.setScene(scene);
        primaryStage.show();

        primaryStage.setOnCloseRequest(we -> {
            we.consume();
//            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
//            alert.setTitle("Выход из приложения");
//            alert.setHeaderText("Вы действительно хотите выйти из приложения?");
//            alert.setContentText("Подтвердите своё решение.");
//            Optional<ButtonType> option = alert.showAndWait();
//            if (option.get() == ButtonType.OK) {
//                primaryStage.close();
//            } else {
//                we.consume();
//            }

        });

    }


    public static void main(String[] args) {
        launch(args);
    }
}

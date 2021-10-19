package com.svoemesto.ivfx;

import com.svoemesto.ivfx.tables.Database;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.File;
import java.sql.Connection;

public class Main extends Application {

    public static Stage mainWindow;
    public static Connection mainConnection;
    public static final String PATH_TO_FOLDER_OPENCVPROJECT = "D:\\Dropbox\\Py\\MyOpenCv";
//    public static String mainFolder = "F:\\iGot";
//    public static String mainTasgFolder = "F:\\iGot\\Tags";
//    public static String mainTypesFolder = "F:\\iGot\\Types";

//    public static String mainFolder = "F:\\CELL";
//    public static String mainTasgFolder = "F:\\CELL\\Tags";
//    public static String mainTypesFolder = "F:\\CELL\\Types";

    public static String getMainTasgFolder() {
        return (new File("Tags")).getAbsolutePath();
    }

    public static String PATH_TO_CASCADE_CLASSIFIER = "D:\\Dropbox\\Java\\opencv_4_5_3\\sources\\data\\haarcascades\\";

    public static String getMainTypesFolder() {
        return (new File("Types")).getAbsolutePath();
    }

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

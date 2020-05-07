package com.svoemesto.ivfx.controllers;


import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;

public class ProgressController extends Thread {

    private Label lblHeader = new Label();
    private Label lblText = new Label();
    private ProgressBar ctlProgress = new ProgressBar();

    private Stage myStage;
    private Scene myScene;

    private String headerText;
    private int maxProgressValue;
    private boolean isWorking;
    private int counter;
    private String progressText;

    public ProgressController(String headerText, int maxProgressValue) {

        this.headerText = headerText;
        this.maxProgressValue = maxProgressValue;
        this.isWorking = true;

        //            Parent root = FXMLLoader.load(this.getClass().getResource("../resources/fxml/progress.fxml"));
//        Parent root = mainPane();

        Pane pane = new Pane();
        pane.setPrefSize(600, 80);

//        this.lblHeader = new Label(this.headerText);
//        this.lblText = new Label();
//        this.ctlProgress = new ProgressBar();

        pane.getChildren().addAll(this.lblHeader, this.lblText, this.ctlProgress);

        this.myScene = new Scene(pane);
        this.myStage = new Stage();
        this.myStage.setScene(this.myScene);
        this.myStage.setTitle("Progress Bar");
        this.myStage.show();

        this.start();


    }

    private AnchorPane mainPane() {
        AnchorPane pane = new AnchorPane();
        pane.setPrefSize(600, 80);

        this.lblHeader = new Label(this.headerText);
        this.lblText = new Label();
        this.ctlProgress = new ProgressBar();

        pane.getChildren().addAll(this.lblHeader, this.lblText, this.ctlProgress);

        return pane;
    }

    public void initialize() {
        this.lblHeader.setText(this.headerText);
    }

    public void progress(String text) {
        this.counter++;
        String textCounter = "[" + this.counter + " из " + this.maxProgressValue + "]";
        this.progressText = (text == null ? "" : text + " ") + textCounter;
        double progress = (1.0d * this.counter) / (this.maxProgressValue);
//        Platform.runLater(() -> {
        this.ctlProgress.setProgress(progress);
        this.lblText.setText(this.progressText);
//        });
        if (progress >= 1 ) this.isWorking = false;
    }

    @Override
    public void run() {
        while (this.isWorking) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
//            double progress = (1.0d * this.counter) / (this.maxProgressValue);
//            Platform.runLater(() -> {
//                ctlProgress.setProgress(progress);
//                lblText.setText(this.progressText);
//            });
//            if (progress >= 1 ) this.isWorking = false;
        }
        Platform.runLater(() -> {
            this.myStage.close();
        });

    }

    public void close() {
        this.isWorking = false;
    }

}
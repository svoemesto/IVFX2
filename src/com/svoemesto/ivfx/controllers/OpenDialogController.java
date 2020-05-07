package com.svoemesto.ivfx.controllers;

import com.svoemesto.ivfx.tables.IVFXProjects;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;

public class OpenDialogController {



    @FXML
    private TableView<IVFXProjects> tblProjects;

    @FXML
    private TableColumn<IVFXProjects, Integer> colOrder;

    @FXML
    private TableColumn<IVFXProjects, String> colName;

    @FXML
    private Button btnOK;

    @FXML
    private Button btnCancel;

    private static IVFXProjects project;

    private static Stage openDialogWindow;

    private ObservableList<IVFXProjects> listProjects = FXCollections.observableArrayList();

    public static IVFXProjects getProject() {

        try {
            Parent root = FXMLLoader.load(OpenDialogController.class.getResource("../resources/fxml/OpenDialog.fxml"));
            openDialogWindow = new Stage();
            openDialogWindow.setTitle("Открыть проект...");
            openDialogWindow.setScene(new Scene(root));
            openDialogWindow.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return project;
    }

    @FXML
    void doCancel(ActionEvent event) {
        project = null;
        openDialogWindow.close();
    }

    @FXML
    void doOK(ActionEvent event) {
        openDialogWindow.close();
    }

    @FXML
    void initialize() {

        listProjects = FXCollections.observableArrayList(IVFXProjects.loadList());
        colOrder.setCellValueFactory(new PropertyValueFactory<>("order"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));

        tblProjects.setItems(listProjects);

        tblProjects.getSelectionModel().selectedItemProperty().addListener((v, oldValue, newValue) -> {
            if (newValue != null) {
                project = newValue;
            }
        });

    }

}

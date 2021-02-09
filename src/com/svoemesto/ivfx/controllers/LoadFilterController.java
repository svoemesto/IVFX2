package com.svoemesto.ivfx.controllers;

import com.svoemesto.ivfx.tables.*;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import javafx.fxml.FXML;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class LoadFilterController extends Application {

    @FXML
    private Label lblFilters;

    @FXML
    private TableView<IVFXFilters> tblFilters;

    @FXML
    private TableColumn<IVFXFilters, String> colFilterName;

    @FXML
    private Button btnLoad;

    @FXML
    private Button btnCancel;

    @FXML
    private Button btnDelete;

    private static IVFXProjects currentProject;
    private static IVFXFilters currentFilter;
    private static Stage controllerStage;
    private ObservableList<IVFXFilters> listFilters = FXCollections.observableArrayList();

    @FXML
    void doBtnDelete(ActionEvent event) {

        if (currentFilter != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Удаление фильтра");
            alert.setHeaderText("Вы действительно хотите удалить фильтр «" + currentFilter.getName() + "»?");
            alert.setContentText("В случае утвердительного ответа фильтр будет удален из базы данных и его восстановление будет невозможно.\nВы уверены, что хотите удалить фильтр?");
            Optional<ButtonType> option = alert.showAndWait();
            if (option.get() == ButtonType.OK) {
                currentFilter.delete();
                currentFilter = null;
                initialize();
            }
        }

    }

    private void load() {
        IVFXFilters ivfxFilterDefault = IVFXFilters.getDefaultFilter(currentProject);
        List<IVFXFiltersFrom> listFiltersFrom = IVFXFiltersFrom.loadList(currentFilter);
        List<IVFXFiltersSelect> listFiltersSelect = IVFXFiltersSelect.loadList(currentFilter);

        for (IVFXFiltersFrom ivfxFiltersFrom: IVFXFiltersFrom.loadList(ivfxFilterDefault)) {
            ivfxFiltersFrom.delete();
        }
        for (IVFXFiltersSelect ivfxFiltersSelect: IVFXFiltersSelect.loadList(ivfxFilterDefault)) {
            ivfxFiltersSelect.delete();
        }


        for (IVFXFiltersFrom ivfxFiltersFrom: listFiltersFrom) {
            IVFXFiltersFrom ivfxFiltersFromDefault = IVFXFiltersFrom.getNewDbInstance(ivfxFilterDefault, ivfxFiltersFrom.getIvfxFile());
            ivfxFiltersFromDefault.setRecordId(ivfxFiltersFrom.getRecordId());
            ivfxFiltersFromDefault.setRecordType(ivfxFiltersFrom.getRecordType());
            ivfxFiltersFromDefault.setRecordTypeId(ivfxFiltersFrom.getRecordTypeId());
            ivfxFiltersFromDefault.save();
        }

        for (IVFXFiltersSelect ivfxFiltersSelect: listFiltersSelect) {

            IVFXFiltersSelect ivfxFiltersSelectDefault = IVFXFiltersSelect.getNewDbInstance(ivfxFilterDefault);
            ivfxFiltersSelectDefault.setProjectId(currentProject.getId());
            ivfxFiltersSelectDefault.setFilterId(ivfxFilterDefault.getId());
            ivfxFiltersSelectDefault.setRecordType(ivfxFiltersSelect.getRecordType());
            ivfxFiltersSelectDefault.setRecordTypeId(ivfxFiltersSelect.getRecordTypeId());
            ivfxFiltersSelectDefault.setObjectId(ivfxFiltersSelect.getObjectId());
            ivfxFiltersSelectDefault.setObjectTypeId(ivfxFiltersSelect.getObjectTypeId());
            ivfxFiltersSelectDefault.setFilterSelectObjectType(ivfxFiltersSelect.getFilterSelectObjectType());
            ivfxFiltersSelectDefault.setIsAnd(ivfxFiltersSelect.getIsAnd());
            ivfxFiltersSelectDefault.setIsIncluded(ivfxFiltersSelect.getIsIncluded());
            ivfxFiltersSelectDefault.save();
        }

        currentFilter = ivfxFilterDefault;
    }


    @FXML
    void doBtnCancel(ActionEvent event) {
        currentFilter = null;
        controllerStage.close();
    }

    @FXML
    void doBtnLoad(ActionEvent event) {

        load();

        controllerStage.close();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

    }



    public static IVFXFilters getFilter(IVFXProjects ivfxProject) {

        IVFXFilters ivfxFilter = null;
        currentProject = ivfxProject;

        try {
            Parent root = FXMLLoader.load(PersonController.class.getResource("../resources/fxml/LoadFilter.fxml"));
            controllerStage = new Stage();
            controllerStage.setTitle("Фильтры проекта");
            controllerStage.setScene(new Scene(root));
            controllerStage.initModality(Modality.APPLICATION_MODAL);
            controllerStage.showAndWait();
            ivfxFilter = currentFilter;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ivfxFilter;

    }

    @FXML
    void initialize() {

        lblFilters.setText("Фильтры проекта «" + currentProject.getName() + "»");
        listFilters = FXCollections.observableArrayList(IVFXFilters.loadListWithoutDefault(currentProject));
        tblFilters.setItems(listFilters);
        colFilterName.setCellValueFactory(new PropertyValueFactory<>("name"));

        // скрываем заголовок у таблицы tblFilters
        tblFilters.widthProperty().addListener((source,oldWidth,newWidth) -> {
            Pane header = (Pane) tblFilters.lookup("TableHeaderRow");
            if (header.isVisible()){
                header.setMaxHeight(0);
                header.setMinHeight(0);
                header.setPrefHeight(0);
                header.setVisible(false);
            }
        });

        // событие выбора в таблице tblFilters
        tblFilters.getSelectionModel().selectedItemProperty().addListener((v, oldValue, newValue) -> {
            if (newValue != null) {
                IVFXFilters selectedFilters = (IVFXFilters) newValue;
                if (currentFilter != selectedFilters) {
                    currentFilter = selectedFilters;
                }
            }
        });

        // событие двойного клика в таблице tblFilters
        tblFilters.setOnMouseClicked(new EventHandler<MouseEvent>()  { //событие двойного клика
            @Override
            public void handle(MouseEvent mouseEvent) {
                if(mouseEvent.getButton().equals(MouseButton.PRIMARY)){
                    if(mouseEvent.getClickCount() == 2){
                        load();
                        controllerStage.close();
                    }
                }
            }
        });

        // нажатие Enter в таблице tblFilters
        tblFilters.setOnKeyPressed(ke -> {
            if (ke.getCode().equals(KeyCode.ENTER))
            {
                btnLoad.requestFocus();
            }
        });

    }

}

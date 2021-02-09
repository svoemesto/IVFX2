package com.svoemesto.ivfx.controllers;

import com.svoemesto.ivfx.tables.IVFXFilters;
import com.svoemesto.ivfx.tables.IVFXFiltersFrom;
import com.svoemesto.ivfx.tables.IVFXFiltersSelect;
import com.svoemesto.ivfx.tables.IVFXProjects;
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

public class SaveFilterController  extends Application {

    @FXML
    private Label lblFilters;

    @FXML
    private TableView<IVFXFilters> tblFilters;

    @FXML
    private TableColumn<IVFXFilters, String> colFilterName;

    @FXML
    private TextField fldNewFilterName;

    @FXML
    private Button btnSave;

    @FXML
    private Button btnCancel;

    private static IVFXProjects currentProject;
    private static IVFXFilters currentFilter;
    private static Stage controllerStage;
    private ObservableList<IVFXFilters> listFilters = FXCollections.observableArrayList();


    @FXML
    void doBtnCancel(ActionEvent event) {
        currentFilter = null;
        controllerStage.close();
    }

    @FXML
    void doBtnSave(ActionEvent event) {

        boolean filterAlreadyPresent = false;
        for (IVFXFilters ivfxFilter: listFilters) {
            if (ivfxFilter.getName().equals(fldNewFilterName.getText())) {
                filterAlreadyPresent = true;
                currentFilter = ivfxFilter;
                break;
            }
        }


        if (filterAlreadyPresent) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Перезапись фильтра");
            alert.setHeaderText("Фильтр с названием «" + fldNewFilterName.getText() + "» уже существует.");
            alert.setContentText("Хотите его перезаписать?");
            Optional<ButtonType> option = alert.showAndWait();
            if (option.get() == ButtonType.OK) {
                currentFilter.delete();
                currentFilter = null;
            } else {
                return;
            }
        }

        IVFXFilters ivfxFilterDefault = IVFXFilters.getDefaultFilter(currentProject);
        List<IVFXFiltersFrom> listFiltersFromDefault = IVFXFiltersFrom.loadList(ivfxFilterDefault);
        List<IVFXFiltersSelect> listFiltersSelectDefault = IVFXFiltersSelect.loadList(ivfxFilterDefault);

        IVFXFilters ivfxFilter = IVFXFilters.getNewDbInstance(currentProject);
        ivfxFilter.setName(fldNewFilterName.getText());
        ivfxFilter.save();

        for (IVFXFiltersFrom ivfxFiltersFromDefault: listFiltersFromDefault) {
            IVFXFiltersFrom ivfxFiltersFrom = IVFXFiltersFrom.getNewDbInstance(ivfxFilter, ivfxFiltersFromDefault.getIvfxFile());
            ivfxFiltersFrom.setRecordId(ivfxFiltersFromDefault.getRecordId());
            ivfxFiltersFrom.setRecordType(ivfxFiltersFromDefault.getRecordType());
            ivfxFiltersFrom.setRecordTypeId(ivfxFiltersFromDefault.getRecordTypeId());
            ivfxFiltersFrom.save();
        }

        for (IVFXFiltersSelect ivfxFiltersSelectDefault: listFiltersSelectDefault) {

            IVFXFiltersSelect ivfxFiltersSelect = IVFXFiltersSelect.getNewDbInstance(ivfxFilter);

            ivfxFiltersSelect.setProjectId(currentProject.getId());
            ivfxFiltersSelect.setFilterId(ivfxFilter.getId());
            ivfxFiltersSelect.setRecordType(ivfxFiltersSelectDefault.getRecordType());
            ivfxFiltersSelect.setRecordTypeId(ivfxFiltersSelectDefault.getRecordTypeId());
            ivfxFiltersSelect.setObjectId(ivfxFiltersSelectDefault.getObjectId());
            ivfxFiltersSelect.setObjectTypeId(ivfxFiltersSelectDefault.getObjectTypeId());
            ivfxFiltersSelect.setFilterSelectObjectType(ivfxFiltersSelectDefault.getFilterSelectObjectType());
            ivfxFiltersSelect.setIsAnd(ivfxFiltersSelectDefault.getIsAnd());
            ivfxFiltersSelect.setIsIncluded(ivfxFiltersSelectDefault.getIsIncluded());
            ivfxFiltersSelect.save();
        }

        currentFilter = ivfxFilter;
        controllerStage.close();

    }

    public static IVFXFilters getFilter(IVFXProjects ivfxProject) {

        IVFXFilters ivfxFilter = null;
        currentProject = ivfxProject;

        try {
            Parent root = FXMLLoader.load(PersonController.class.getResource("../resources/fxml/SaveFilter.fxml"));
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

    @Override
    public void start(Stage primaryStage) throws Exception {

    }

    @FXML
    void initialize() {

        lblFilters.setText("Фильтры проекта «" + currentProject.getName() + "»");
        fldNewFilterName.setText("Новый фильтр");
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
                    fldNewFilterName.setText(currentFilter.getName());
                }
            }
        });

        // событие двойного клика в таблице tblFilters
        tblFilters.setOnMouseClicked(new EventHandler<MouseEvent>()  { //событие двойного клика
            @Override
            public void handle(MouseEvent mouseEvent) {
                if(mouseEvent.getButton().equals(MouseButton.PRIMARY)){
                    if(mouseEvent.getClickCount() == 2){
                        controllerStage.close();
                    }
                }
            }
        });

        // нажатие Enter в таблице tblFilters
        tblFilters.setOnKeyPressed(ke -> {
            if (ke.getCode().equals(KeyCode.ENTER))
            {
                btnSave.requestFocus();
            }
        });

    }

}

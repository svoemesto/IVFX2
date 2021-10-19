package com.svoemesto.ivfx.controllers;

import com.svoemesto.ivfx.Main;
import com.svoemesto.ivfx.tables.*;
import com.svoemesto.ivfx.utils.FFmpeg;
import com.svoemesto.ivfx.utils.Utils;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.fxml.FXML;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class FiltersController extends Application {

    @FXML
    private Tab tabFiles;

    @FXML
    private TableView<IVFXFiles> tblFilesInTabFiles;

    @FXML
    private TableColumn<IVFXFiles, Integer> colFileOrderTabFiles;

    @FXML
    private TableColumn<IVFXFiles, String> colFileNameTabFiles;

    @FXML
    private Button btnSelectAllFilesInTabFiles;

    @FXML
    private Tab tabShots;

    @FXML
    private TableView<IVFXFiles> tblFilesInTabShots;

    @FXML
    private TableColumn<IVFXFiles, Integer> colFileOrderTabShots;

    @FXML
    private TableColumn<IVFXFiles, String> colFileNameTabShots;

    @FXML
    private Button btnSelectAllFilesInTabShots;

    @FXML
    private TableView<IVFXShots> tblShotsInTabShots;

    @FXML
    private TableColumn<IVFXShots, String> colShotFrom;

    @FXML
    private TableColumn<IVFXShots, String> colShotTo;

    @FXML
    private Button btnSelectAllShotsInTabShots;

    @FXML
    private Tab tabScenes;

    @FXML
    private TableView<IVFXFiles> tblFilesInTabScenes;

    @FXML
    private TableColumn<IVFXFiles, Integer> colFileOrderTabScenes;

    @FXML
    private TableColumn<IVFXFiles, String> colFileNameTabScenes;

    @FXML
    private Button btnSelectAllFilesInTabScenes;

    @FXML
    private TableView<IVFXScenes> tblScenesInTabScenes;

    @FXML
    private TableColumn<IVFXScenes, String> colSceneFile;

    @FXML
    private TableColumn<IVFXScenes, String> colSceneStart;

    @FXML
    private TableColumn<IVFXScenes, String> colSceneName;

    @FXML
    private Button btnSelectAllScenesInTabScenes;

    @FXML
    private Tab tabEvents;

    @FXML
    private TableView<IVFXFiles> tblFilesInTabEvents;

    @FXML
    private TableColumn<IVFXFiles, Integer> colFileOrderTabEvents;

    @FXML
    private TableColumn<IVFXFiles, String> colFileNameTabEvents;

    @FXML
    private Button btnSelectAllFilesInTabEvents;

    @FXML
    private TableView<IVFXEvents> tblEventsInTabEvents;

    @FXML
    private TableColumn<IVFXEvents, String> colEventStart;

    @FXML
    private TableColumn<IVFXEvents, String> colEventEnd;

    @FXML
    private TableColumn<IVFXEvents, String> colEventType;

    @FXML
    private TableColumn<IVFXEvents, String> colEventName;

    @FXML
    private Button btnSelectAllEventsInTabEvents;

    @FXML
    private Button btnAddToFiltersFrom;

    @FXML
    private TableView<IVFXFiltersFrom> tblFiltersFrom;

    @FXML
    private TableColumn<IVFXFiltersFrom, String> colOrderTblFromRecords;

    @FXML
    private TableColumn<IVFXFiltersFrom, String> colFileTitleTblFromRecords;

    @FXML
    private TableColumn<IVFXFiltersFrom, String> colRecordTypeNameTblTabFromFiles;

    @FXML
    private TableColumn<IVFXFiltersFrom, String> colRecordDurationStringTblTabFromFiles;

    @FXML
    private TableColumn<IVFXFiltersFrom, String> colRecordNameTblTabFromFiles;

    @FXML
    private Button btnSelectAllFiltersFrom;

    @FXML
    private Button btnDeleteSelectedFiltersFrom;

    @FXML
    private Button btnClearFiltersFrom;

    @FXML
    private Button btnSelectAllFiltersSelect;

    @FXML
    private Button btnDeleteSelectedFiltersSelect;

    @FXML
    private Button btnClearFiltersSelect;

    @FXML
    private Button btnUpFiltersFrom;

    @FXML
    private Button btnDownFiltersFrom;

    @FXML
    private Tab tabPersons;

    @FXML
    private TextField fldFindPersonsInTabPersons;

    @FXML
    private TableView<IVFXPersons> tblPersonsInTabPersons;

    @FXML
    private TableColumn<IVFXPersons, String> colPersonsPreview;

    @FXML
    private Tab tabGroups;

    @FXML
    private TableView<IVFXGroups> tblGroupsInTabGroups;

    @FXML
    private TableColumn<IVFXGroups, String> colGroups;

    @FXML
    private TableView<IVFXIncluding> tblSelectIncluding;

    @FXML
    private TableColumn<IVFXIncluding, String> colIncluding;

    @FXML
    private TableView<IVFXFilterFromTypes> tblSelectTypes;

    @FXML
    private TableColumn<IVFXFilterFromTypes, String> colFilterFromType;

    @FXML
    private TableView<IVFXAndOr> tblSelectAndOr;

    @FXML
    private TableColumn<IVFXAndOr, String> colAndOr;

    @FXML
    private Button btnAddRecordsSelect;

    @FXML
    private Button btnAddRecordsSelectTypeSeparatorAnd;

    @FXML
    private Button btnAddRecordsSelectTypeSeparatorOr;

    @FXML
    private TableView<IVFXFiltersSelect> tblSelectRecords;

    @FXML
    private TableColumn<IVFXFiltersSelect, String> colSelectRecordText;

    @FXML
    private Button btnLoadFilter;

    @FXML
    private Button btnSaveFilter;

    @FXML
    private TextField fldNameVideo;

    @FXML
    private Button btnGenerateFileName;

    @FXML
    private CheckBox checkBoxDontRepeatShots;

    @FXML
    private CheckBox checkCopyShotsToFolder;

    @FXML
    private CheckBox checkBoxSaveShotsList;

    @FXML
    private CheckBox checkBoxSaveCmd;

    @FXML
    private CheckBox checkCreateVideo;

    @FXML
    private CheckBox checkBoxOpenVideoAfterCreation;

    @FXML
    private Button btnCreate;

    @FXML
    private RadioButton rbMKV;

    @FXML
    private ToggleGroup grpContainer;

    @FXML
    private RadioButton rbMP4;

    private static FiltersController filtersController = new FiltersController();
    private Stage filterStage;
    private Scene filterScene;

    private static final String FX_SELECTION_TABLES_RED = "-fx-selection-bar: red; -fx-selection-bar-non-focused: red;";
    private static final String FX_SELECTION_TABLES_BLUE = "-fx-selection-bar: blue; -fx-selection-bar-non-focused: blue;";

    private ObservableList<IVFXPersons> listPersons = FXCollections.observableArrayList();
    private ObservableList<IVFXGroups> listGroups = FXCollections.observableArrayList();
    private ObservableList<IVFXFiles> listFilesInTabFiles = FXCollections.observableArrayList();
    private ObservableList<IVFXFiles> listFilesInTabShots = FXCollections.observableArrayList();
    private ObservableList<IVFXFiles> listFilesInTabScenes = FXCollections.observableArrayList();
    private ObservableList<IVFXFiles> listFilesInTabEvents = FXCollections.observableArrayList();
    private ObservableList<IVFXShots> listShotsInTabShots = FXCollections.observableArrayList();
    private ObservableList<IVFXScenes> listScenesInTabScenes = FXCollections.observableArrayList();
    private ObservableList<IVFXEvents> listEventsInTabEvents = FXCollections.observableArrayList();

    private ObservableList<IVFXShots> listShotsForFrom = FXCollections.observableArrayList();
    private ObservableList<IVFXScenes> listScenesForFrom = FXCollections.observableArrayList();
    private ObservableList<IVFXEvents> listEventsForFrom = FXCollections.observableArrayList();

    private static IVFXProjects currentProject;
    private IVFXFilters mainFilter;
    private IVFXEnumFilterFromTypes mainFilterFromType = IVFXEnumFilterFromTypes.FILE;
    private boolean mainIsIncluded = true;
    private boolean mainIsAnd = true;
    private IVFXPersons mainSelectedPerson;
    private IVFXGroups mainSelectedGroup;
    private FilteredList<IVFXPersons> listPersonsFiltered;

    private ObservableList<IVFXFiltersFrom> listFilterFrom = FXCollections.observableArrayList();
    private ObservableList<IVFXFiltersSelect> listFilterSelect = FXCollections.observableArrayList();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Main.mainConnection = Database.getConnection();
//        currentProject = ProjectController.mainProject;
//        currentProject = IVFXProjects.load(1);
        currentProject = IVFXProjects.load(2);
    }

    public void editFilters(IVFXProjects ivfxProject) {
        currentProject = ivfxProject;
        try {

            AnchorPane root = FXMLLoader.load(filtersController.getClass().getResource("../resources/fxml/filters.fxml")); // в этот момент вызывается initialize()

            filtersController.filterScene = new Scene(root);
            filtersController.filterStage = new Stage();
            filtersController.filterStage.setTitle("Редактор фильтров. Проект: " + currentProject.getName());
            filtersController.filterStage.setScene(filtersController.filterScene);
            filtersController.filterStage.initModality(Modality.APPLICATION_MODAL);

            filtersController.filterStage.setOnCloseRequest(we -> {
                System.out.println("Закрытые окна редактора фильтров.");
            });


            filtersController.onStart();
            filtersController.filterStage.showAndWait();


            System.out.println("Завершение работы редактора фильтров.");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void onStart() {

    }

    @FXML
    void initialize() {

        mainFilter = IVFXFilters.getDefaultFilter(currentProject);

        listPersons = FXCollections.observableArrayList(IVFXPersons.loadList(currentProject,true));
        listPersonsFiltered = new FilteredList<>(listPersons, e -> true);
        listGroups = FXCollections.observableArrayList(IVFXGroups.loadList(currentProject));
        listFilesInTabFiles = FXCollections.observableArrayList(IVFXFiles.loadList(currentProject));
        listFilesInTabShots = FXCollections.observableArrayList(IVFXFiles.loadList(currentProject));
        listFilesInTabScenes = FXCollections.observableArrayList(IVFXFiles.loadList(currentProject));
        listFilesInTabEvents = FXCollections.observableArrayList(IVFXFiles.loadList(currentProject));
        listFilterSelect = FXCollections.observableArrayList(IVFXFiltersSelect.loadList(mainFilter));
        listFilterFrom = FXCollections.observableArrayList(IVFXFiltersFrom.loadList(mainFilter));

        // tblFilesInTabFiles - таблица файлов во вкладке Файлы
        colFileOrderTabFiles.setCellValueFactory(new PropertyValueFactory<>("order"));
        colFileNameTabFiles.setCellValueFactory(new PropertyValueFactory<>("title"));
        tblFilesInTabFiles.setItems(listFilesInTabFiles);
        tblFilesInTabFiles.setStyle(FX_SELECTION_TABLES_RED);
        tblFilesInTabFiles.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        // событие двойного клика в таблице файлов во вкладке Файлы
        tblFilesInTabFiles.setOnMouseClicked(new EventHandler<MouseEvent>()  { //событие двойного клика
            @Override
            public void handle(MouseEvent mouseEvent) {
                if(mouseEvent.getButton().equals(MouseButton.PRIMARY)){
                    if(mouseEvent.getClickCount() == 2){
                        addRecordsFrom();
                    }
                }
            }
        });

        // tblFilesInTabShots - таблица файлов во вкладке Планы
        colFileOrderTabShots.setCellValueFactory(new PropertyValueFactory<>("order"));
        colFileNameTabShots.setCellValueFactory(new PropertyValueFactory<>("title"));
        tblFilesInTabShots.setItems(listFilesInTabShots);
        tblFilesInTabShots.setStyle(FX_SELECTION_TABLES_BLUE);
        tblFilesInTabShots.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        // tblFilesInTabScenes - таблица файлов во вкладке Сцены
        colFileOrderTabScenes.setCellValueFactory(new PropertyValueFactory<>("order"));
        colFileNameTabScenes.setCellValueFactory(new PropertyValueFactory<>("title"));
        tblFilesInTabScenes.setItems(listFilesInTabScenes);
        tblFilesInTabScenes.setStyle(FX_SELECTION_TABLES_BLUE);
        tblFilesInTabScenes.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        // tblFilesInTabEvents - таблица файлов во вкладке События
        colFileOrderTabEvents.setCellValueFactory(new PropertyValueFactory<>("order"));
        colFileNameTabEvents.setCellValueFactory(new PropertyValueFactory<>("title"));
        tblFilesInTabEvents.setItems(listFilesInTabEvents);
        tblFilesInTabEvents.setStyle(FX_SELECTION_TABLES_BLUE);
        tblFilesInTabEvents.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        // tblShotsInTabShots - таблица Планов
        colShotFrom.setCellValueFactory(new PropertyValueFactory<>("labelFirst1"));
        colShotTo.setCellValueFactory(new PropertyValueFactory<>("labelLast1"));
        tblShotsInTabShots.setStyle(FX_SELECTION_TABLES_RED);
        tblShotsInTabShots.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        // событие двойного клика в таблице Планов
        tblShotsInTabShots.setOnMouseClicked(new EventHandler<MouseEvent>()  { //событие двойного клика
            @Override
            public void handle(MouseEvent mouseEvent) {
                if(mouseEvent.getButton().equals(MouseButton.PRIMARY)){
                    if(mouseEvent.getClickCount() == 2){
                        addRecordsFrom();
                    }
                }
            }
        });

        // tblScenesInTabScenes - таблица Сцен
        colSceneFile.setCellValueFactory(new PropertyValueFactory<>("title"));
        colSceneStart.setCellValueFactory(new PropertyValueFactory<>("start"));
        colSceneName.setCellValueFactory(new PropertyValueFactory<>("name"));
        tblScenesInTabScenes.setStyle(FX_SELECTION_TABLES_RED);
        tblScenesInTabScenes.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        // событие двойного клика в таблице Сцен
        tblScenesInTabScenes.setOnMouseClicked(new EventHandler<MouseEvent>()  { //событие двойного клика
            @Override
            public void handle(MouseEvent mouseEvent) {
                if(mouseEvent.getButton().equals(MouseButton.PRIMARY)){
                    if(mouseEvent.getClickCount() == 2){
                        addRecordsFrom();
                    }
                }
            }
        });

        // tblEventsInTabEvents - таблица Событий
        colEventStart.setCellValueFactory(new PropertyValueFactory<>("start"));
        colEventEnd.setCellValueFactory(new PropertyValueFactory<>("end"));
        colEventType.setCellValueFactory(new PropertyValueFactory<>("eventTypeName"));
        colEventName.setCellValueFactory(new PropertyValueFactory<>("name"));
        tblEventsInTabEvents.setStyle(FX_SELECTION_TABLES_RED);
        tblEventsInTabEvents.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        // событие двойного клика в таблице Событий
        tblEventsInTabEvents.setOnMouseClicked(new EventHandler<MouseEvent>()  { //событие двойного клика
            @Override
            public void handle(MouseEvent mouseEvent) {
                if(mouseEvent.getButton().equals(MouseButton.PRIMARY)){
                    if(mouseEvent.getClickCount() == 2){
                        addRecordsFrom();
                    }
                }
            }
        });

        // tblPersonsInTabPersons - таблица Персонажей
        colPersonsPreview.setCellValueFactory(new PropertyValueFactory<>("preview1"));
        tblPersonsInTabPersons.setItems(listPersons);
        tblPersonsInTabPersons.setStyle(FX_SELECTION_TABLES_RED);

        // событие выбора в таблице Персонажи
        tblPersonsInTabPersons.getSelectionModel().selectedItemProperty().addListener((v, oldValue, newValue) -> {
            if (newValue != null) {
                IVFXPersons selectedPerson = (IVFXPersons) newValue;
                if (mainSelectedPerson != selectedPerson) {
                    mainSelectedPerson = selectedPerson;
                }
            }
        });

        // событие двойного клика в таблице Персонажи
        tblPersonsInTabPersons.setOnMouseClicked(new EventHandler<MouseEvent>()  { //событие двойного клика
            @Override
            public void handle(MouseEvent mouseEvent) {
                if(mouseEvent.getButton().equals(MouseButton.PRIMARY)){
                    if(mouseEvent.getClickCount() == 2){
                        addRecordsSelect();
                    }
                }
            }
        });

        // скрываем заголовок у таблицы tblPersonsInTabPersons
        tblPersonsInTabPersons.widthProperty().addListener((source,oldWidth,newWidth) -> {
            Pane header = (Pane) tblPersonsInTabPersons.lookup("TableHeaderRow");
            if (header.isVisible()){
                header.setMaxHeight(0);
                header.setMinHeight(0);
                header.setPrefHeight(0);
                header.setVisible(false);
            }
        });


        // фильтация таблицы всех персонажей
        fldFindPersonsInTabPersons.setOnKeyReleased(e -> {
            fldFindPersonsInTabPersons.textProperty().addListener((v, oldValue, newValue) -> {
                listPersonsFiltered.setPredicate((Predicate<? super IVFXPersons>) ivfxPerson-> {
                    if (newValue == null || newValue.isEmpty()) {
                        return true;
                    }
                    String lowerCaseFilter = newValue.toLowerCase();
                    if (ivfxPerson.getName().toLowerCase().contains(lowerCaseFilter)) {
                        return true;
                    } if (ivfxPerson.getDescription().toLowerCase().contains(lowerCaseFilter)) {
                        return true;
                    }
                    return false;
                });

            });
            SortedList<IVFXPersons> sortedPersonAll = new SortedList<>(listPersonsFiltered);
            sortedPersonAll.comparatorProperty().bind(tblPersonsInTabPersons.comparatorProperty());
            tblPersonsInTabPersons.setItems(sortedPersonAll);
            if (sortedPersonAll.size() > 0) {
                tblPersonsInTabPersons.getSelectionModel().select(sortedPersonAll.get(0));
                tblPersonsInTabPersons.scrollTo(sortedPersonAll.get(0));
            }
        });

        // нажатие Enter в поле ctlFindPersonsAll - переход на первую запись в таблице tblPersonsAll
        fldFindPersonsInTabPersons.setOnKeyPressed(ke -> {
            if (ke.getCode().equals(KeyCode.ENTER))
            {
                tblPersonsInTabPersons.requestFocus();
                tblPersonsInTabPersons.getSelectionModel().select(0);
                tblPersonsInTabPersons.scrollTo(0);
            }
        });

        // нажатие Enter в поле в таблице персонажей проекта
        tblPersonsInTabPersons.setOnKeyPressed(ke -> {
            if (ke.getCode().equals(KeyCode.ENTER))
            {
                addRecordsSelect();
                fldFindPersonsInTabPersons.requestFocus();
                fldFindPersonsInTabPersons.setText("");
            }
        });




        // tblGroupsInTabGroups - таблица Групп персонажей
        colGroups.setCellValueFactory(new PropertyValueFactory<>("name"));
        tblGroupsInTabGroups.setItems(listGroups);
        tblGroupsInTabGroups.setStyle(FX_SELECTION_TABLES_RED);

        // событие выбора в таблице Группы персонажей
        tblGroupsInTabGroups.getSelectionModel().selectedItemProperty().addListener((v, oldValue, newValue) -> {
            if (newValue != null) {
                IVFXGroups selectedGroup = (IVFXGroups) newValue;
                if (mainSelectedGroup != selectedGroup) {
                    mainSelectedGroup = selectedGroup;
                }
            }
        });

        // событие двойного клика в таблице Групп персонажей
        tblGroupsInTabGroups.setOnMouseClicked(new EventHandler<MouseEvent>()  { //событие двойного клика
            @Override
            public void handle(MouseEvent mouseEvent) {
                if(mouseEvent.getButton().equals(MouseButton.PRIMARY)){
                    if(mouseEvent.getClickCount() == 2){
                        addRecordsSelect();
                    }
                }
            }
        });

        // tblFiltersFrom - таблица записей группы источников выбора
        colOrderTblFromRecords.setCellValueFactory(new PropertyValueFactory<>("order"));
        colFileTitleTblFromRecords.setCellValueFactory(new PropertyValueFactory<>("fileTitle"));
        colRecordTypeNameTblTabFromFiles.setCellValueFactory(new PropertyValueFactory<>("recordTypeName"));
        colRecordDurationStringTblTabFromFiles.setCellValueFactory(new PropertyValueFactory<>("recordDurationString"));
        colRecordNameTblTabFromFiles.setCellValueFactory(new PropertyValueFactory<>("recordName"));
        tblFiltersFrom.setItems(listFilterFrom);
        tblFiltersFrom.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        tblFiltersFrom.setStyle(FX_SELECTION_TABLES_RED);

        // событие двойного клика в таблице записей группы источников выбора
        tblFiltersFrom.setOnMouseClicked(new EventHandler<MouseEvent>()  { //событие двойного клика
            @Override
            public void handle(MouseEvent mouseEvent) {
                if(mouseEvent.getButton().equals(MouseButton.PRIMARY)){
                    if(mouseEvent.getClickCount() == 2){
                        deleteSelectedRecordsFromTblFiltersFrom();
                    }
                }
            }
        });

        // tblSelectRecords - таблица записей группы выбора
        colSelectRecordText.setCellValueFactory(new PropertyValueFactory<>("textForColumn"));
        tblSelectRecords.setItems(listFilterSelect);
        tblSelectRecords.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        tblSelectRecords.setStyle(FX_SELECTION_TABLES_RED);

        // событие двойного клика в таблице записей группы выбора
        tblSelectRecords.setOnMouseClicked(new EventHandler<MouseEvent>()  { //событие двойного клика
            @Override
            public void handle(MouseEvent mouseEvent) {
                if(mouseEvent.getButton().equals(MouseButton.PRIMARY)){
                    if(mouseEvent.getClickCount() == 2){
                        deleteSelectedRecordsFromTblSelectRecords();
                    }
                }
            }
        });


        // событие выбора в таблице файлов вкладки "Планы"
        tblFilesInTabShots.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                List<IVFXShots> listShots = new ArrayList<>();
                List<IVFXFiles> listFiles = (List<IVFXFiles>) tblFilesInTabShots.getSelectionModel().getSelectedItems().stream().collect(Collectors.toList());
                for (IVFXFiles file : listFiles) {
                    List<IVFXShots> tmpList = IVFXShots.loadList(file,true);
                    listShots.addAll(tmpList);
                }
                listShotsForFrom = FXCollections.observableArrayList(listShots);
                tblShotsInTabShots.setItems(listShotsForFrom);
            }
        });

        // событие выбора в таблице файлов вкладки "Сцены"
        tblFilesInTabScenes.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                List<IVFXScenes> listScenes = new ArrayList<>();
                List<IVFXFiles> listFiles = (List<IVFXFiles>) tblFilesInTabScenes.getSelectionModel().getSelectedItems().stream().collect(Collectors.toList());
                for (IVFXFiles file : listFiles) {
                    List<IVFXScenes> tmpList = IVFXScenes.loadList(file);
                    listScenes.addAll(tmpList);
                }
                listScenesForFrom = FXCollections.observableArrayList(listScenes);
                tblScenesInTabScenes.setItems(listScenesForFrom);
            }
        });

        // событие выбора в таблице файлов вкладки "Событие"
        tblFilesInTabEvents.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                List<IVFXEvents> listEvents = new ArrayList<>();
                List<IVFXFiles> listFiles = (List<IVFXFiles>) tblFilesInTabEvents.getSelectionModel().getSelectedItems().stream().collect(Collectors.toList());
                for (IVFXFiles file : listFiles) {
                    List<IVFXEvents> tmpList = IVFXEvents.loadList(file);
                    listEvents.addAll(tmpList);
                }
                listEventsForFrom = FXCollections.observableArrayList(listEvents);
                tblEventsInTabEvents.setItems(listEventsForFrom);
            }
        });

        // таблица "Вхождение"

        ObservableList<IVFXIncluding> listTblSelectIncluding = FXCollections.observableArrayList();
        IVFXIncluding ivfxIncluding = new IVFXIncluding();
        ivfxIncluding.setIsIncluding(true);
        ivfxIncluding.setName("Содержится");
        listTblSelectIncluding.add(ivfxIncluding);

        ivfxIncluding = new IVFXIncluding();
        ivfxIncluding.setIsIncluding(false);
        ivfxIncluding.setName("Не содержится");
        listTblSelectIncluding.add(ivfxIncluding);

        tblSelectIncluding.setItems(listTblSelectIncluding);
        tblSelectIncluding.setStyle(FX_SELECTION_TABLES_RED);
        colIncluding.setCellValueFactory(new PropertyValueFactory<>("name"));
        for (IVFXIncluding including :  listTblSelectIncluding) {
            if (including.getIsIncluding() == mainIsIncluded) {
                tblSelectIncluding.getSelectionModel().select(including);
                break;
            }
        }

        tblSelectIncluding.getSelectionModel().selectedItemProperty().addListener((v, oldValue, newValue) -> {
            if (newValue != null) {
                IVFXIncluding selectedIncluding = (IVFXIncluding) newValue;
                if (mainIsIncluded != selectedIncluding.getIsIncluding()) {
                    mainIsIncluded = selectedIncluding.getIsIncluding();
                }
            }
        });


        // таблица "Типы"
        ObservableList<IVFXFilterFromTypes> listTblSelectFilterFromTypes = FXCollections.observableArrayList();
        IVFXFilterFromTypes ivfxFilterFromTypes = new IVFXFilterFromTypes();
        ivfxFilterFromTypes.setIvfxEnumFilterFromTypes(IVFXEnumFilterFromTypes.FILE);
        ivfxFilterFromTypes.setName("в файле");
        listTblSelectFilterFromTypes.add(ivfxFilterFromTypes);

        ivfxFilterFromTypes = new IVFXFilterFromTypes();
        ivfxFilterFromTypes.setIvfxEnumFilterFromTypes(IVFXEnumFilterFromTypes.SCENE);
        ivfxFilterFromTypes.setName("в сцене");
        listTblSelectFilterFromTypes.add(ivfxFilterFromTypes);

        ivfxFilterFromTypes = new IVFXFilterFromTypes();
        ivfxFilterFromTypes.setIvfxEnumFilterFromTypes(IVFXEnumFilterFromTypes.EVENT);
        ivfxFilterFromTypes.setName("в событии");
        listTblSelectFilterFromTypes.add(ivfxFilterFromTypes);

        ivfxFilterFromTypes = new IVFXFilterFromTypes();
        ivfxFilterFromTypes.setIvfxEnumFilterFromTypes(IVFXEnumFilterFromTypes.SHOT);
        ivfxFilterFromTypes.setName("в плане");
        listTblSelectFilterFromTypes.add(ivfxFilterFromTypes);

        tblSelectTypes.setItems(listTblSelectFilterFromTypes);
        tblSelectTypes.setStyle(FX_SELECTION_TABLES_RED);
        colFilterFromType.setCellValueFactory(new PropertyValueFactory<>("name"));

        for (IVFXFilterFromTypes filterFromType :  listTblSelectFilterFromTypes) {
            if (filterFromType.getIvfxEnumFilterFromTypes() == mainFilterFromType) {
                tblSelectTypes.getSelectionModel().select(filterFromType);
                break;
            }
        }

        tblSelectTypes.getSelectionModel().selectedItemProperty().addListener((v, oldValue, newValue) -> {
            if (newValue != null) {
                IVFXFilterFromTypes selectedFilterFromTypes = (IVFXFilterFromTypes) newValue;
                if (mainFilterFromType != selectedFilterFromTypes.getIvfxEnumFilterFromTypes()) {
                    mainFilterFromType = selectedFilterFromTypes.getIvfxEnumFilterFromTypes();
                }
            }
        });

        // таблица "AndOr"
        ObservableList<IVFXAndOr> listTblSelectAndOr = FXCollections.observableArrayList();
        IVFXAndOr ivfxAndOr = new IVFXAndOr();
        ivfxAndOr.setIsAnd(true);
        ivfxAndOr.setName("[И]");
        listTblSelectAndOr.add(ivfxAndOr);

        ivfxAndOr = new IVFXAndOr();
        ivfxAndOr.setIsAnd(false);
        ivfxAndOr.setName("[ИЛИ]");
        listTblSelectAndOr.add(ivfxAndOr);

        tblSelectAndOr.setItems(listTblSelectAndOr);
        tblSelectAndOr.setStyle(FX_SELECTION_TABLES_RED);
        colAndOr.setCellValueFactory(new PropertyValueFactory<>("name"));
        for (IVFXAndOr andOr :  listTblSelectAndOr) {
            if (andOr.getIsAnd() == mainIsAnd) {
                tblSelectAndOr.getSelectionModel().select(andOr);
                break;
            }
        }

        tblSelectAndOr.getSelectionModel().selectedItemProperty().addListener((v, oldValue, newValue) -> {
            if (newValue != null) {
                IVFXAndOr selectedAndOr = (IVFXAndOr) newValue;
                if (mainIsAnd != selectedAndOr.getIsAnd()) {
                    mainIsAnd = selectedAndOr.getIsAnd();
                }
            }
        });

    }

    private void deleteSelectedRecordsFromTblFiltersFrom() {
        for (IVFXFiltersFrom selected : (List<IVFXFiltersFrom>) tblFiltersFrom.getSelectionModel().getSelectedItems().stream().collect(Collectors.toList())) {
            listFilterFrom.remove(selected);
            selected.delete();
        }
        int order = 0;
        for (IVFXFiltersFrom item : listFilterFrom) {
            order++;
            item.setOrder(order);
            item.save();
        }
    }

    private void deleteSelectedRecordsFromTblSelectRecords() {
        for (IVFXFiltersSelect selected : (List<IVFXFiltersSelect>) tblSelectRecords.getSelectionModel().getSelectedItems().stream().collect(Collectors.toList())) {
            listFilterSelect.remove(selected);
            selected.delete();
        }
        int order = 0;
        for (IVFXFiltersSelect item : listFilterSelect) {
            order++;
            item.setOrder(order);
            item.save();
        }
    }

    private void addRecordsFrom() {
        if (tabFiles.isSelected()) {
            for (IVFXFiles selected : (List<IVFXFiles>) tblFilesInTabFiles.getSelectionModel().getSelectedItems().stream().collect(Collectors.toList())) {
                IVFXFiltersFrom filterFrom = IVFXFiltersFrom.getNewDbInstance(mainFilter, selected);

                filterFrom.setIvfxFilter(mainFilter);
                filterFrom.setFilterId(mainFilter.getId());
                filterFrom.setIvfxProject(mainFilter.getIvfxProject());
                filterFrom.setProjectId(mainFilter.getProjectId());
                filterFrom.setFileId(selected.getId());
                filterFrom.setRecordId(selected.getId());
                filterFrom.setRecordTypeId(1);
                filterFrom.setIvfxFile(selected);
                filterFrom.setRecordType(IVFXEnumFilterFromTypes.FILE);

                filterFrom.save();
                listFilterFrom.add(filterFrom);
            }
        }

        if (tabShots.isSelected()) {
            for (IVFXShots selected : (List<IVFXShots>) tblShotsInTabShots.getSelectionModel().getSelectedItems().stream().collect(Collectors.toList())) {
                IVFXFiltersFrom filterFrom = IVFXFiltersFrom.getNewDbInstance(mainFilter, selected.getIvfxFile());

                filterFrom.setIvfxFilter(mainFilter);
                filterFrom.setFilterId(mainFilter.getId());
                filterFrom.setIvfxProject(mainFilter.getIvfxProject());
                filterFrom.setProjectId(mainFilter.getProjectId());
                filterFrom.setFileId(selected.getFileId());
                filterFrom.setRecordId(selected.getId());
                filterFrom.setRecordTypeId(2);
                filterFrom.setIvfxFile(selected.getIvfxFile());
                filterFrom.setRecordType(IVFXEnumFilterFromTypes.SHOT);

                filterFrom.save();
                listFilterFrom.add(filterFrom);
            }
        }

        if (tabScenes.isSelected()) {
            for (IVFXScenes selected : (List<IVFXScenes>) tblScenesInTabScenes.getSelectionModel().getSelectedItems().stream().collect(Collectors.toList())) {
                IVFXFiltersFrom filterFrom = IVFXFiltersFrom.getNewDbInstance(mainFilter, selected.getIvfxFile());

                filterFrom.setIvfxFilter(mainFilter);
                filterFrom.setFilterId(mainFilter.getId());
                filterFrom.setIvfxProject(mainFilter.getIvfxProject());
                filterFrom.setProjectId(mainFilter.getProjectId());
                filterFrom.setFileId(selected.getFileId());
                filterFrom.setRecordId(selected.getId());
                filterFrom.setRecordTypeId(3);
                filterFrom.setIvfxFile(selected.getIvfxFile());
                filterFrom.setRecordType(IVFXEnumFilterFromTypes.SCENE);

                filterFrom.save();
                listFilterFrom.add(filterFrom);
            }
        }

        if (tabEvents.isSelected()) {
            for (IVFXEvents selected : (List<IVFXEvents>) tblEventsInTabEvents.getSelectionModel().getSelectedItems().stream().collect(Collectors.toList())) {
                IVFXFiltersFrom filterFrom = IVFXFiltersFrom.getNewDbInstance(mainFilter,selected.getIvfxFile());

                filterFrom.setIvfxFilter(mainFilter);
                filterFrom.setFilterId(mainFilter.getId());
                filterFrom.setIvfxProject(mainFilter.getIvfxProject());
                filterFrom.setProjectId(mainFilter.getProjectId());
                filterFrom.setFileId(selected.getFileId());
                filterFrom.setRecordId(selected.getId());
                filterFrom.setRecordTypeId(4);
                filterFrom.setIvfxFile(selected.getIvfxFile());
                filterFrom.setRecordType(IVFXEnumFilterFromTypes.EVENT);

                filterFrom.save();
                listFilterFrom.add(filterFrom);
            }
        }

    }

    private void addRecordsSelect() {
        if (tabPersons.isSelected()) {
            if (mainSelectedPerson != null) {

                IVFXFiltersSelect filterSelect = IVFXFiltersSelect.getNewDbInstance(mainFilter);

                filterSelect.setRecordTypeId(1);
                filterSelect.setFilterSelectObjectType(IVFXEnumFilterSelectObjectTypes.PERSON);

                filterSelect.setRecordType(mainFilterFromType);
                switch (mainFilterFromType) {
                    case FILE:
                        filterSelect.setObjectTypeId(1);
                        break;
                    case SHOT:
                        filterSelect.setObjectTypeId(2);
                        break;
                    case SCENE:
                        filterSelect.setObjectTypeId(3);
                        break;
                    case EVENT:
                        filterSelect.setObjectTypeId(4);
                        break;
                    default:
                        filterSelect.setObjectTypeId(0);
                }

                filterSelect.setObjectId(mainSelectedPerson.getId());
                filterSelect.setIvfxFilter(mainFilter);
                filterSelect.setFilterId(mainFilter.getId());
                filterSelect.setIvfxProject(mainFilter.getIvfxProject());
                filterSelect.setProjectId(mainFilter.getProjectId());
                filterSelect.setFilterSelectObjectType(IVFXEnumFilterSelectObjectTypes.PERSON);
                filterSelect.setRecordType(mainFilterFromType);
                filterSelect.setIsIncluded(mainIsIncluded);
                filterSelect.setIsAnd(mainIsAnd);

                filterSelect.save();

                listFilterSelect.add(filterSelect);
            }
        }
        if (tabGroups.isSelected()) {
            if (mainSelectedGroup != null) {

                IVFXFiltersSelect filterSelect = IVFXFiltersSelect.getNewDbInstance(mainFilter);

                filterSelect.setRecordTypeId(2);
                filterSelect.setFilterSelectObjectType(IVFXEnumFilterSelectObjectTypes.GROUP);

                filterSelect.setRecordType(mainFilterFromType);
                switch (mainFilterFromType) {
                    case FILE:
                        filterSelect.setObjectTypeId(1);
                        break;
                    case SHOT:
                        filterSelect.setObjectTypeId(2);
                        break;
                    case SCENE:
                        filterSelect.setObjectTypeId(3);
                        break;
                    case EVENT:
                        filterSelect.setObjectTypeId(4);
                        break;
                    default:
                        filterSelect.setObjectTypeId(0);
                }

                filterSelect.setObjectId(mainSelectedGroup.getId());

                filterSelect.setIvfxFilter(mainFilter);
                filterSelect.setFilterId(mainFilter.getId());
                filterSelect.setIvfxProject(mainFilter.getIvfxProject());
                filterSelect.setProjectId(mainFilter.getProjectId());
                filterSelect.setFilterSelectObjectType(IVFXEnumFilterSelectObjectTypes.GROUP);
                filterSelect.setRecordType(mainFilterFromType);
                filterSelect.setIsIncluded(mainIsIncluded);
                filterSelect.setIsAnd(mainIsAnd);

                filterSelect.save();

                listFilterSelect.add(filterSelect);
            }
        }
    }

    @FXML
    void doBtnAddRecordsSelect(ActionEvent event) {                                                                     // Нажатие кнопки "--->" RecordsSelect
        addRecordsSelect();
    }

    @FXML
    void doBtnAddToFiltersFrom(ActionEvent event) {                                                                     // Нажатие кнопки "--->" FiltersFrom
        addRecordsFrom();
    }

    @FXML
    void doBtnAddRecordsSelectTypeSeparatorAnd(ActionEvent event) {                                                     // Нажатие кнопки "=== AND ===>"
        IVFXFiltersSelect filterSelect = IVFXFiltersSelect.getNewDbInstance(mainFilter);

        filterSelect.setRecordTypeId(4);
        filterSelect.setFilterSelectObjectType(IVFXEnumFilterSelectObjectTypes.AND);

        filterSelect.setRecordType(mainFilterFromType);
        switch (mainFilterFromType) {
            case FILE:
                filterSelect.setObjectTypeId(1);
                break;
            case SHOT:
                filterSelect.setObjectTypeId(2);
                break;
            case SCENE:
                filterSelect.setObjectTypeId(3);
                break;
            case EVENT:
                filterSelect.setObjectTypeId(4);
                break;
            default:
                filterSelect.setObjectTypeId(0);
        }

        filterSelect.setProjectId(currentProject.getId());
        filterSelect.setFilterId(mainFilter.getId());
        filterSelect.save();
        listFilterSelect.add(filterSelect);
    }

    @FXML
    void doBtnAddRecordsSelectTypeSeparatorOr(ActionEvent event) {                                                      // Нажатие кнопки "=== OR ===>"
        IVFXFiltersSelect filterSelect = IVFXFiltersSelect.getNewDbInstance(mainFilter);
        filterSelect.setRecordTypeId(3);
        filterSelect.setFilterSelectObjectType(IVFXEnumFilterSelectObjectTypes.OR);

        filterSelect.setRecordType(mainFilterFromType);
        switch (mainFilterFromType) {
            case FILE:
                filterSelect.setObjectTypeId(1);
                break;
            case SHOT:
                filterSelect.setObjectTypeId(2);
                break;
            case SCENE:
                filterSelect.setObjectTypeId(3);
                break;
            case EVENT:
                filterSelect.setObjectTypeId(4);
                break;
            default:
                filterSelect.setObjectTypeId(0);
        }

        filterSelect.setProjectId(currentProject.getId());
        filterSelect.setFilterId(mainFilter.getId());
        filterSelect.save();
        listFilterSelect.add(filterSelect);
    }

    private void moveUpDownSelectedRecordsFromTblFromRecords(boolean moveUp) {
        List<IVFXFiltersFrom> listSelectedItems = (List<IVFXFiltersFrom>) tblFiltersFrom.getSelectionModel().getSelectedItems().stream().collect(Collectors.toList());
        int countSelectedItems = listSelectedItems.size();
        if (countSelectedItems > 0) {
            IVFXFiltersFrom firstSelected = listSelectedItems.get(0);
            IVFXFiltersFrom lastSelected = listSelectedItems.get(countSelectedItems-1);
            if (moveUp) {
                if (firstSelected.getOrder() > 1) {

                    int newStartPosition = firstSelected.getOrder()-1;
                    for (int i = 0; i < countSelectedItems ; i++) {
                        IVFXFiltersFrom current = listSelectedItems.get(i);
                        current.setOrder(newStartPosition + i);
                        current.save();
                    }

                    boolean isFinded = false;
                    int tale = 0;
                    for (IVFXFiltersFrom item : listFilterFrom) {

                        if (!(item.getId() == firstSelected.getId()) && item.getOrder() == firstSelected.getOrder()) {
                            item.setOrder(lastSelected.getOrder()+1);
                            item.save();
                            isFinded = true;
                        } else if (isFinded) {
                            boolean thisIsAlreadySorted = false;
                            for (IVFXFiltersFrom selected : listSelectedItems) {
                                if (item.getId() == selected.getId()) {
                                    item.setOrder(selected.getOrder());
                                    item.save();
                                    thisIsAlreadySorted = true;
                                    break;
                                }
                            }
                            if (!thisIsAlreadySorted) {
                                tale++;
                                item.setOrder(lastSelected.getOrder()+1+tale);
                                item.save();
                            }
                        }

                    }
                }


            } else {
                if (lastSelected.getOrder() < listFilterFrom.size()) {
                    int newStartPosition = lastSelected.getOrder()+1;
                    for (int i = countSelectedItems - 1; i >= 0 ; i--) {
                        IVFXFiltersFrom current = listSelectedItems.get(i);
                        current.setOrder(newStartPosition - (countSelectedItems - (i + 1)));
                        current.save();
                    }

                    boolean isFinded = false;
                    int tale = 0;
                    for (int i = listFilterFrom.size()-1; i >=0  ; i--) {
                        IVFXFiltersFrom item = listFilterFrom.get(i);
                        if (!(item.getId() == lastSelected.getId()) && item.getOrder() == lastSelected.getOrder()) {
                            item.setOrder(firstSelected.getOrder()-1);
                            item.save();
                            isFinded = true;
                        } else if (isFinded) {
                            boolean thisIsAlreadySorted = false;
                            for (IVFXFiltersFrom selected : listSelectedItems) {
                                if (item.getId() == selected.getId()) {
                                    item.setOrder(selected.getOrder());
                                    item.save();
                                    thisIsAlreadySorted = true;
                                    break;
                                }
                            }
                            if (!thisIsAlreadySorted) {
                                tale++;
                                item.setOrder(firstSelected.getOrder()-1-tale);
                                item.save();
                            }
                        }

                    }
                }
            }
        }


        Comparator<IVFXFiltersFrom> comparator = Comparator.comparingInt(IVFXFiltersFrom::getOrder); // comparator = comparator.reversed();
        FXCollections.sort(listFilterFrom, comparator);

        tblFiltersFrom.refresh();

    }

    private void deleteSelectedRecordsFromTblFromRecords() {
        for (IVFXFiltersFrom selected : (List<IVFXFiltersFrom>) tblFiltersFrom.getSelectionModel().getSelectedItems().stream().collect(Collectors.toList())) {
            listFilterFrom.remove(selected);
            selected.delete();
        }
        int order = 0;
        for (IVFXFiltersFrom item : listFilterFrom) {
            order++;
            item.setOrder(order);
            item.save();
        }
    }

    @FXML
    void doBtnSelectAllFiltersFrom(ActionEvent event) {                                                                 // Нажатие кнопки "Выбрать всё" в таблице Filters From
        tblFiltersFrom.getSelectionModel().selectAll();
    }

    @FXML
    void doBtnDeleteSelectedFiltersFrom(ActionEvent event) {                                                            // Нажатие кнопки "Удалить выбранные записи" в таблице Filters From
        deleteSelectedRecordsFromTblFromRecords();
    }

    @FXML
    void doBtnClearFiltersFrom(ActionEvent event) {                                                                     // Нажатие кнопки "Очистить список" в таблице Filters From
        tblFiltersFrom.getSelectionModel().selectAll();
        deleteSelectedRecordsFromTblFromRecords();
    }

    @FXML
    void doBtnSelectAllFiltersSelect(ActionEvent event) {                                                                 // Нажатие кнопки "Выбрать всё" в таблице SelectRecords
        tblSelectRecords.getSelectionModel().selectAll();
    }

    @FXML
    void doBtnDeleteSelectedFiltersSelect(ActionEvent event) {                                                            // Нажатие кнопки "Удалить выбранные записи" в таблице SelectRecords
        deleteSelectedRecordsFromTblSelectRecords();
    }

    @FXML
    void doBtnClearFiltersSelect(ActionEvent event) {                                                                     // Нажатие кнопки "Очистить список" в таблице SelectRecords
        tblSelectRecords.getSelectionModel().selectAll();
        deleteSelectedRecordsFromTblSelectRecords();
    }

    @FXML
    void doBtnCreate(ActionEvent event) {                                                                               // Нажатие кнопки "СОЗДАТЬ!!!"
        create();
    }



    @FXML
    void doBtnGenerateFileName(ActionEvent event) {                                                                     // Нажатие кнопки "Сгенерировать имя"
        fldNameVideo.setText(generateFileName());
    }

    @FXML
    void doBtnLoadFilter(ActionEvent event) {                                                                           // Нажатие кнопки "Загрузить фильтр"
        IVFXFilters temp = LoadFilterController.getFilter(currentProject);
        if (temp != null) initialize();
    }

    @FXML
    void doBtnSaveFilter(ActionEvent event) {                                                                           // Нажатие кнопки "Сохранить фильтр"
        SaveFilterController.getFilter(currentProject);
    }

    @FXML
    void doBtnSelectAllEventsInTabEvents(ActionEvent event) {                                                           // Нажатие кнопки "Выбрать всё" в таблице Событий
        tblEventsInTabEvents.getSelectionModel().selectAll();
    }

    @FXML
    void doBtnSelectAllFilesInTabEvents(ActionEvent event) {                                                            // Нажатие кнопки "Выбрать всё" в таблице файлов вкладки События
        tblFilesInTabEvents.getSelectionModel().selectAll();
    }

    @FXML
    void doBtnSelectAllFilesInTabFiles(ActionEvent event) {                                                             // Нажатие кнопки "Выбрать всё" в таблице файлов вкладки Файлы
        tblFilesInTabFiles.getSelectionModel().selectAll();
    }

    @FXML
    void doBtnSelectAllFilesInTabScenes(ActionEvent event) {                                                            // Нажатие кнопки "Выбрать всё" в таблице файлов вкладки Сцены
        tblFilesInTabScenes.getSelectionModel().selectAll();
    }

    @FXML
    void doBtnSelectAllFilesInTabShots(ActionEvent event) {                                                             // Нажатие кнопки "Выбрать всё" в таблице файлов вкладки Планы
        tblFilesInTabShots.getSelectionModel().selectAll();
    }


    @FXML
    void doBtnSelectAllScenesInTabScenes(ActionEvent event) {                                                           // Нажатие кнопки "Выбрать всё" в таблице Сцены
        tblScenesInTabScenes.getSelectionModel().selectAll();
    }

    @FXML
    void doBtnSelectAllShotsInTabShots(ActionEvent event) {                                                             // Нажатие кнопки "Выбрать всё" в таблице Планы
        tblShotsInTabShots.getSelectionModel().selectAll();
    }

    @FXML
    void doBtnUpFiltersFrom(ActionEvent event) {                                                                        // Нажатие кнопки "Вверх"
        moveUpDownSelectedRecordsFromTblFromRecords(true);
    }

    @FXML
    void doBtnDownFiltersFrom(ActionEvent event) {                                                                      // Нажатие кнопки "Вниз"
        moveUpDownSelectedRecordsFromTblFromRecords(false);
    }


    private String generateFileName() {
        String newName = "";

        for (IVFXFiltersSelect filtersSelect: listFilterSelect) {

            if (!newName.equals("")) newName = newName + "_";

            switch (filtersSelect.getRecordTypeId()) {

                case 1: // Person
                    String personName = IVFXPersons.load(filtersSelect.getObjectId(), false).getName();
                    String personShortName;
                    if (personName.contains(" ")) {
                        personShortName = personName.substring(0,personName.indexOf(" "));
                    } else {
                        personShortName = personName;
                    }
                    newName = newName + personShortName;
                    if (!filtersSelect.getIsIncluded()) newName = newName + "_NOT";

                    switch (filtersSelect.getObjectTypeId()) {
                        case 1: // FILE
                            newName = newName + "_FL";
                            break;
                        case 2: // SHOT
                            newName = newName + "_SH";
                            break;
                        case 3: // SCENE
                            newName = newName + "_SC";
                            break;
                        case 4: // EVENT
                            newName = newName + "_EV";
                            break;
                        default:
                    }

                    break;
                case 2: // Group
                    newName = newName + IVFXGroups.load(filtersSelect.getObjectId()).getName();
                    if (!filtersSelect.getIsIncluded()) newName = newName + "_NOT";
                    break;
                case 3: // OR
                    newName = newName + "OR";
                    break;
                case 4: // AND
                    newName = newName + "AND";
                    break;
                default:
            }

        }

        newName = Utils.convertCyrilic(newName);

        return newName;
    }



    private void create() {

        List<IVFXShots> listShotsInFilterFrom = new ArrayList<>();

        String sql;
        PreparedStatement ps;

        sql = "DELETE FROM tbl_filters_shots";

        try {
            ps = Main.mainConnection.prepareStatement(sql);
            ps.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // собираем список планов
        for (IVFXFiltersFrom filterFrom : listFilterFrom) {
            switch (filterFrom.getRecordType()) {
                case FILE:
                    sql = "INSERT INTO tbl_filters_shots (shot_id) " +
                            "SELECT id " +
                            "FROM tbl_shots " +
                            "WHERE file_id = ?";

                    listShotsInFilterFrom.addAll(IVFXShots.loadList(filterFrom.getIvfxFile(),false));
                    break;
                case SHOT:
                    sql = "INSERT INTO tbl_filters_shots (shot_id) " +
                            "SELECT id " +
                            "FROM tbl_shots " +
                            "WHERE id = ?";

                    listShotsInFilterFrom.add(IVFXShots.load(filterFrom.getRecordId(),false));
                    break;
                case SCENE:
                    sql = "INSERT INTO tbl_filters_shots (shot_id) " +
                            "SELECT shot_id " +
                            "FROM tbl_scenes_shots " +
                            "WHERE scene_id = ?";

                    listShotsInFilterFrom.addAll(IVFXScenes.loadListShots(IVFXScenes.load(filterFrom.getRecordId())));
                    break;
                case EVENT:
                    sql = "INSERT INTO tbl_filters_shots (shot_id) " +
                            "SELECT shot_id " +
                            "FROM tbl_events_shots " +
                            "WHERE event_id = ?";

                    listShotsInFilterFrom.addAll(IVFXEvents.loadListShots(IVFXEvents.load(filterFrom.getRecordId())));
                    break;
            }

            try {
                ps = Main.mainConnection.prepareStatement(sql);
                ps.setInt   (1, filterFrom.getRecordId());
                ps.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }

        }


        // удаляем повторы (если надо)
        if (checkBoxDontRepeatShots.isSelected()) {
            List<IVFXShots> listShotsTmp = new ArrayList<>();
            for (IVFXShots shotFrom : listShotsInFilterFrom) {
                boolean isAlreadyAdded = false;
                for (IVFXShots shotTmp : listShotsTmp) {
                    if (shotTmp.getId() == shotFrom.getId()) {
                        isAlreadyAdded = true;
                        break;
                    }
                }
                if (!isAlreadyAdded) listShotsTmp.add(shotFrom);
            }
            listShotsInFilterFrom = listShotsTmp;
        }

        // на данный момент у нас сформирован список FROM планов listShotsInFilterFrom
        // теперь надо применить фильтр

        if (listFilterSelect.size() > 0) {

            class MyScene {
                IVFXScenes ivfxScene;
                List<IVFXPersons> listPersons = new ArrayList<>();
                List<IVFXShots> listShots = new ArrayList<>();
            }
            List<MyScene> listMyScenes = new ArrayList<>();

            class MyEvent {
                IVFXEvents ivfxEvent;
                List<IVFXPersons> listPersons = new ArrayList<>();
                List<IVFXShots> listShots = new ArrayList<>();
            }
            List<MyEvent> listMyEvents = new ArrayList<>();

            class MyFile {
                IVFXFiles ivfxVideoFile;
                List<IVFXPersons> listPersons = new ArrayList<>();
                List<IVFXShots> listShots = new ArrayList<>();
            }
            List<MyFile> listMyFiles = new ArrayList<>();

            class MyShot {
                IVFXShots ivfxShot;
                List<IVFXPersons> listPersons = new ArrayList<>();
                MyScene myScene;
                List<MyEvent> listMyEvents = new ArrayList<>();
                MyFile myFile;
            }
            List<MyShot> listMyShots = new ArrayList<>();

            // цикл по отобранным планам
            for (IVFXShots shot : listShotsInFilterFrom) {

                // ищем план в списке МоихПланов
                boolean isShotFoundInMyListShots = false;
                for (MyShot myShot : listMyShots) {
                    if (myShot.ivfxShot.getId() == shot.getId()) {
                        isShotFoundInMyListShots = true;
                        break;
                    }
                }

                // если не нашли план в списке моих планов - его надо туда добавить
                if (!isShotFoundInMyListShots) {

                    // создаем новый МойПлан
                    MyShot myShot = new MyShot();
                    myShot.ivfxShot = shot;    // прописываем в МойПлан - план
                    myShot.listPersons = shot.getListPersons();   // прописываем в МойПлан - список персонажей плана

                    // ищем файл плана в списке МоихФайлов
                    boolean isFileFoundInFilesList = false;
                    for (MyFile myFile : listMyFiles) {
                        if (myFile.ivfxVideoFile.getId() == shot.getFileId()) {
                            isFileFoundInFilesList = true;
                            myShot.myFile = myFile;
                            break;
                        }
                    }

                    // если файл плана отсутствует в списке файлов - надо его туда добавить
                    if (!isFileFoundInFilesList) {
                        MyFile myFile = new MyFile();
                        myFile.ivfxVideoFile = shot.getIvfxFile();

                        // получаем список сцен файла
                        List<IVFXScenes> listFileScenes = IVFXScenes.loadList(myFile.ivfxVideoFile);
                        // проходимся по ним циклом
                        for (IVFXScenes fileScene : listFileScenes) {
                            // ищем текущую сцену файла в списке МоихСцен
                            boolean isSceneFoundInScenesList = false;
                            for (MyScene myScene : listMyScenes) {
                                if (fileScene.getId() == myScene.ivfxScene.getId()) {
                                    isSceneFoundInScenesList = true;
                                    // если находим - добавляем список персонажей сцены к списку пресонажей файла
                                    myFile.listPersons.addAll(myScene.listPersons);
                                    break;
                                }
                            }
                            // если сцену файла не нашли в списке МоихСцен - надо ее создать
                            if (!isSceneFoundInScenesList) {
                                MyScene myScene = new MyScene();
                                myScene.ivfxScene = fileScene;
                                myScene.listPersons = IVFXScenes.loadListPersons(myScene.ivfxScene,false);
                                myScene.listShots = IVFXScenes.loadListShots(myScene.ivfxScene);
                                myFile.listPersons.addAll(myScene.listPersons);
                                myFile.listShots.addAll(myScene.listShots);
                                listMyScenes.add(myScene);
                            }
                        }

                        // получаем список событий файла
                        List<IVFXEvents> listFileEvents = IVFXEvents.loadList(myFile.ivfxVideoFile);
                        // проходимся по ним циклом
                        for (IVFXEvents fileEvent : listFileEvents) {
                            // ищем текущее событие в списке МоихСобытий
                            boolean isEventFoundInEventsList = false;
                            for (MyEvent myEvent : listMyEvents) {
                                if (fileEvent.getId() == myEvent.ivfxEvent.getId()) {
                                    isEventFoundInEventsList = true;
                                    break;
                                }
                            }
                            // если событие файла не нашли в списке МоихСобытийн - надо ее создать
                            if (!isEventFoundInEventsList) {
                                MyEvent myEvent = new MyEvent();
                                myEvent.ivfxEvent = fileEvent;
                                myEvent.listPersons = IVFXEvents.loadListPersons(myEvent.ivfxEvent,false);
                                myEvent.listShots = IVFXEvents.loadListShots(myEvent.ivfxEvent);
                                listMyEvents.add(myEvent);
                            }
                        }

                        myShot.myFile = myFile;
                        listMyFiles.add(myFile);
                    }


                    // находим сцену, в которую включен план

                    // проходим циклом по списку МоихСцен и ищем для каждой МойСцены в ее списке планов текущий план
                    boolean isShotFoundInSceneShotList = false;
                    for (MyScene myScene : listMyScenes) {
                        for (IVFXShots sceneShot : myScene.listShots) {
                            if (sceneShot.getId() == shot.getId()) {
                                isShotFoundInSceneShotList = true;
                                myShot.myScene = myScene;
                                break;
                            }
                        }
                        if (isShotFoundInSceneShotList) break;
                    }

                    // проходим циклом по списку МоихСобытий и ищем для каждой МойСобытие в ее списке планов текущий план
                    boolean isShotFoundInEventShotList = false;
                    for (MyEvent myEvent : listMyEvents) {
                        for (IVFXShots eventShot : myEvent.listShots) {
                            if (eventShot.getId() == shot.getId()) {
                                isShotFoundInEventShotList = true;
                                myShot.listMyEvents.add(myEvent);
                                break;
                            }
                        }
                        if (isShotFoundInEventShotList) break;
                    }

                    listMyShots.add(myShot);
                }
            }


            class SelList {
                boolean isAnd;
                List<IVFXShots> listShots = new ArrayList<>();
            }

            class SelListGroup {
                boolean isAnd;
                List<SelList> listSelList = new ArrayList<>();
                List<IVFXShots> listShots = new ArrayList<>();
            }

            List<SelListGroup> listSelListGroup = new ArrayList<>(); // спискок селлистов - группы
            List<SelList> listSelList = new ArrayList<>(); // спискок селлистов - селекты

            List<IVFXShots> listSelListShots;

            SelListGroup selListGroup = new SelListGroup();
            selListGroup.isAnd = true;
            selListGroup.listSelList = listSelList;
            listSelListGroup.add(selListGroup);



            // цикл по спрокам FilterSelect
            for (IVFXFiltersSelect select : listFilterSelect) {
                listSelListShots = new ArrayList<>();
                if (select.getFilterSelectObjectType() == IVFXEnumFilterSelectObjectTypes.AND ||
                        select.getFilterSelectObjectType() == IVFXEnumFilterSelectObjectTypes.OR) {
                    // если строка - разделитель
                    // создаем новый элемент в массиве listSelList
                    selListGroup = new SelListGroup();
                    listSelList = new ArrayList<>(); // спискок селлистов - селекты
                    selListGroup.isAnd = select.getFilterSelectObjectType() == IVFXEnumFilterSelectObjectTypes.AND ? true : false;
                    selListGroup.listSelList = listSelList;
                    listSelListGroup.add(selListGroup);
                } else {

                    // формируем список планов для текушего селекта

                    SelList selList = new SelList();
                    selList.isAnd = select.getIsAnd();
                    selList.listShots = listSelListShots;
                    listSelList.add(selList);

                    List<IVFXPersons> listSelectedPersons = new ArrayList<>();

                    switch (select.getFilterSelectObjectType()) {
                        case PERSON:
                            listSelectedPersons.add(IVFXPersons.load(select.getObjectId(),false));
                            break;
                        case GROUP:
//                            List<IVFXGroupPersons> listGroupPersons = IVFXGroupPersons.loadGroupPersons(IVFXGroup.load(select.getObjectUuid(),ivfxProject),false);
//                            for (IVFXGroupPersons groupPersons : listGroupPersons) {
//                                listSelectedPersons.add(IVFXPerson.loadByUuidFromFiles(groupPersons.getPersonUuid(), ivfxProject,false));
//                            }
                            break;
                        default:
                            break;
                    }

                    // проходимся по списку ФРОМ-планов и для каждого проверяем, должен ли он входить в выборку
                    boolean needToAdd;
                    for (IVFXShots shot : listShotsInFilterFrom) {
                        MyShot myShot = new MyShot();
                        for (MyShot mySeg : listMyShots) {
                            if (shot.getId() == mySeg.ivfxShot.getId()) {
                                myShot = mySeg;
                                break;
                            }
                        }

                        switch (select.getRecordType()) {
                            case SHOT:   // вхождение с точностью до плана
                                needToAdd = false;
                                for (IVFXPersons person : listSelectedPersons) {
                                    boolean isPresent = IVFXPersons.isPersonPresentInList(person, myShot.listPersons);
                                    if (isPresent == select.getIsIncluded()) {
                                        needToAdd = true;
                                        break;
                                    }
                                }
                                if (needToAdd) {
                                    listSelListShots.add(shot);
                                }
                                break;

                            case EVENT: // вхождение с точностью до события

                                needToAdd = false;
                                for (IVFXPersons person : listSelectedPersons) {

                                    for (MyEvent myEvent : myShot.listMyEvents) {
                                        boolean isPresent = IVFXPersons.isPersonPresentInList(person, myEvent.listPersons);
                                        if (isPresent == select.getIsIncluded()) {
                                            needToAdd = true;
                                            break;
                                        }
                                    }

                                }
                                if (needToAdd) {
                                    listSelListShots.add(shot);
                                }
                                break;

                            case SCENE: // вхождение с точностью до сцены

                                if (myShot.myScene != null) {
                                    needToAdd = false;
                                    for (IVFXPersons person : listSelectedPersons) {
                                        boolean isPresent = IVFXPersons.isPersonPresentInList(person, myShot.myScene.listPersons);
                                        if (isPresent == select.getIsIncluded()) {
                                            needToAdd = true;
                                            break;
                                        }
                                    }
                                    if (needToAdd) {
                                        listSelListShots.add(shot);
                                    }
                                    break;
                                }

                            case FILE:  // вхождение с точностью до файла
                                needToAdd = false;
                                for (IVFXPersons person : listSelectedPersons) {
                                    boolean isPresent = IVFXPersons.isPersonPresentInList(person, myShot.myFile.listPersons);
                                    if (isPresent == select.getIsIncluded()) {
                                        needToAdd = true;
                                        break;
                                    }
                                }
                                if (needToAdd) {
                                    listSelListShots.add(shot);
                                }
                                break;

                            default:
                                break;
                        }
                    }  // END for (IVFXShot shot : listShotsInFilterFrom)

                }
            }  // КОНЕЦ цикла по спрокам FilterSelect

            // теперь надо объединить блоки планов в списках listSelList для каждого селлиста в listSelListGroup
            for (SelListGroup currentSelListGroup : listSelListGroup) {
                List<IVFXShots> listResultedShots = new ArrayList<>();
                List<IVFXShots> listResultedShotsTemp = new ArrayList<>();
                for (SelList currentSelList : currentSelListGroup.listSelList) {
                    if (listResultedShots.size() == 0) {
                        listResultedShots.addAll(currentSelList.listShots);
                    } else {
                        if (currentSelList.isAnd) { // пересечение listResultedShots и currentSelList.listShots

                            for (IVFXShots shotInResult : listResultedShots) {
                                boolean isFinded = false;
                                for (IVFXShots shotInCurrentSelList : currentSelList.listShots) {
                                    if (shotInResult.getId() == shotInCurrentSelList.getId()) {
                                        isFinded = true;
                                        break;
                                    }
                                }
                                if (isFinded) {
                                    listResultedShotsTemp.add(shotInResult);
                                }
                            }
                            listResultedShots = listResultedShotsTemp;

                        } else { // объединение listResultedShots и currentSelList.listShots
                            listResultedShotsTemp.addAll(listResultedShots);
                            listResultedShotsTemp.addAll(currentSelList.listShots);
                            listResultedShots = listResultedShotsTemp;
                        }
                    }
                }
                // удаляем дубли из listResultedShots
                listResultedShotsTemp = new ArrayList<>();
                for (IVFXShots shotInResultedShots : listResultedShots) {
                    boolean isFind = false;
                    for (IVFXShots shotInResultedShotsTemp : listResultedShotsTemp) {
                        if (shotInResultedShots.getId() == shotInResultedShotsTemp.getId()) {
                            isFind = true;
                            break;
                        }
                    }
                    if (!isFind) {
                        listResultedShotsTemp.add(shotInResultedShots);
                    }
                }
                listResultedShots = listResultedShotsTemp;
                currentSelListGroup.listShots = listResultedShots;

            }

            // теперь надо объединить блоки планов в списках listSelListGroup
            List<IVFXShots> listResultedShots = new ArrayList<>();
            List<IVFXShots> listResultedShotsTemp = new ArrayList<>();
            for (SelListGroup currentSelList : listSelListGroup) {
                if (listResultedShots.size() == 0) {
                    listResultedShots.addAll(currentSelList.listShots);
                } else {
                    if (currentSelList.isAnd) { // пересечение listResultedShots и currentSelList.listShots

                        for (IVFXShots shotInResult : listResultedShots) {
                            boolean isFinded = false;
                            for (IVFXShots shotInCurrentSelList : currentSelList.listShots) {
                                if (shotInResult.getId() == shotInCurrentSelList.getId()) {
                                    isFinded = true;
                                    break;
                                }
                            }
                            if (isFinded) {
                                listResultedShotsTemp.add(shotInResult);
                            }
                        }
                        listResultedShots = listResultedShotsTemp;

                    } else { // объединение listResultedShots и currentSelList.listShots
                        listResultedShotsTemp.addAll(listResultedShots);
                        listResultedShotsTemp.addAll(currentSelList.listShots);
                        listResultedShots = listResultedShotsTemp;
                    }
                }
                // удаляем дубли из listResultedShots
                listResultedShotsTemp = new ArrayList<>();
                for (IVFXShots shotInResultedShots : listResultedShots) {
                    boolean isFind = false;
                    for (IVFXShots shotInResultedShotsTemp : listResultedShotsTemp) {
                        if (shotInResultedShots.getId() == shotInResultedShotsTemp.getId()) {
                            isFind = true;
                            break;
                        }
                    }
                    if (!isFind) {
                        listResultedShotsTemp.add(shotInResultedShots);
                    }
                }
                listResultedShots = listResultedShotsTemp;
            }

            // сортируем список listResultedShots
            Collections.sort(listResultedShots);
            listResultedShotsTemp = new ArrayList<>();
            List<IVFXFiles> listFiles = IVFXFiles.loadList(currentProject);
            Collections.sort(listFiles);
            for (IVFXFiles file : listFiles) {
                for (IVFXShots shot : listResultedShots) {
                    if (shot.getFileId() == file.getId()) {
                        listResultedShotsTemp.add(shot);
                    }
                }
            }
            listResultedShots = listResultedShotsTemp;
            listShotsInFilterFrom = listResultedShots;

        } else {

        }
    // формируем список планов и команды на сборку файла
        String fileNameWithoutExt = fldNameVideo.getText().replace("^\\.+", "_").replaceAll("[\\\\/:*?\"<>|]", "_").replaceAll(" ", "_");
        String folderOutput = currentProject.getFolder() + "\\Output";
        String folderOutputSupport = currentProject.getFolder() + "\\Output\\" + fileNameWithoutExt;
        File folder = new File(folderOutput);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        File folder1 = new File(folderOutputSupport);
        if (!folder1.exists()) {
            folder1.mkdirs();
        }

        String fileExt = ".mp4";
        if (rbMKV.isSelected()) fileExt = ".mkv";
        if (rbMP4.isSelected()) fileExt = ".mp4";

        String fileListShotsName = folderOutputSupport + "\\" + fileNameWithoutExt + ".list";
        String fileListShotsConcat = folderOutputSupport + "\\" + fileNameWithoutExt + "_concat.txt";
        String fileCmdName = folderOutputSupport + "\\" + fileNameWithoutExt + ".cmd";
        String fileCmdCopyName = folderOutputSupport + "\\" + fileNameWithoutExt + "_CopyShots.cmd";
        String fileVideoName = folderOutput + "\\" + fileNameWithoutExt + fileExt;
        String fileVideoNameTmp = folderOutputSupport + "\\" + fileNameWithoutExt + "_temp.mkv";
        String fileVideoNameTmpWithoutPath = fileNameWithoutExt + "_temp.mkv";
        String fileVideoNameWithoutPath = fileNameWithoutExt + ".mkv";

        String textListShots = "";
        String textListShotsConcat = "";
        String textListShotsToCopy = "MKDIR \"" + folderOutputSupport + "\\shots_" + fileNameWithoutExt + "\"\n";
        String textCmd;
        String textCmdFull = "";

        List<String> paramCmd = new ArrayList<String>();
        List<List<String>> listParam = new ArrayList<>();
        List<String> listTextCmd = new ArrayList<>();

        paramCmd.add("-o");
        paramCmd.add("\"" + fileVideoName + "\"");


        textCmd = "";
        int numTempFiles = 0;
        for (IVFXShots shot : listShotsInFilterFrom) {

            textListShots = textListShots + "\"" + shot.getShotVideoFileNameX264() + "\"\n";
            textListShotsConcat = textListShotsConcat + "file '" + shot.getShotVideoFileNameX264() + "'\n";
            textListShotsToCopy = textListShotsToCopy + "COPY \"" + shot.getShotVideoFileNameX264() + "\" \"" + folderOutputSupport + "\\shots_" + fileNameWithoutExt + "\" /Y\n";

        }


        // заменяем textCmdFull на конкатенацию.
        textCmdFull = FFmpeg.PATH_TO_FFMPEG + " -f concat -safe 0 -i " + fileListShotsConcat + " -map 0 -c copy -y " + fileVideoName + "\n";

        if (checkBoxOpenVideoAfterCreation.isSelected()) {
            textCmdFull = textCmdFull + fileVideoName;
        }

        // сохраняем список планов для конкатинации
        File fileConcat = new File(fileListShotsConcat);
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(fileConcat));
            writer.write(textListShotsConcat);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // сохраняем список сегметнов если надо
        if (checkBoxSaveShotsList.isSelected()) {
            File file = new File(fileListShotsName);
            try {
                BufferedWriter writer = new BufferedWriter(new FileWriter(file));
                writer.write(textListShots);
                writer.flush();
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // копируем планы в отдельную папку если надо
        if (checkCopyShotsToFolder.isSelected()) {
            File file = new File(fileCmdCopyName);
            try {
                BufferedWriter writer = new BufferedWriter(new FileWriter(file));
                writer.write(textListShotsToCopy);
                writer.flush();
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // сохраняем CMD если надо
        if (checkBoxSaveCmd.isSelected()) {
            File file = new File(fileCmdName);
            try {
                BufferedWriter writer = new BufferedWriter(new FileWriter(file));
                writer.write(textCmdFull);
                writer.flush();
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // создаем видео если надо
        if (checkCreateVideo.isSelected()) {
            File file = new File(fileCmdName);
            if (file.exists()) {
                try {
                    Process process = Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + fileCmdName);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }

    }




}

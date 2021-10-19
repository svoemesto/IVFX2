package com.svoemesto.ivfx.controllers;

import com.sun.javafx.scene.control.skin.TableViewSkin;
import com.sun.javafx.scene.control.skin.VirtualFlow;
import com.svoemesto.ivfx.Main;
import com.svoemesto.ivfx.tables.*;
import com.svoemesto.ivfx.utils.FFmpeg;
import com.svoemesto.ivfx.utils.Utils;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
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

public class FiltersTagsController extends Application {

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
    private TextField fldFindTagsAll;

    @FXML
    private CheckBox chFindTagsAllInProperties;

    @FXML
    private TableView<IVFXTags> tblTagsAll;

    @FXML
    private TableColumn<IVFXTags, String> colTypeTblTagsAll;

    @FXML
    private TableColumn<IVFXTags, String> colNameTblTagsAll;


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

    private static FiltersTagsController filtersTagsController = new FiltersTagsController();
    private Stage filterStage;
    private Scene filterScene;

    private static final String FX_SELECTION_TABLES_RED = "-fx-selection-bar: red; -fx-selection-bar-non-focused: red;";
    private static final String FX_SELECTION_TABLES_BLUE = "-fx-selection-bar: blue; -fx-selection-bar-non-focused: blue;";

    private ObservableList<IVFXTags> listTagsAll = FXCollections.observableArrayList();
    private ObservableList<IVFXFiles> listFilesInTabFiles = FXCollections.observableArrayList();

    private ObservableList<IVFXShots> listShotsForFrom = FXCollections.observableArrayList();
    private ObservableList<IVFXScenes> listScenesForFrom = FXCollections.observableArrayList();
    private ObservableList<IVFXEvents> listEventsForFrom = FXCollections.observableArrayList();

    private static IVFXProjects currentProject;
    private IVFXFilters mainFilter;
    private IVFXEnumFilterFromTypes mainFilterFromType = IVFXEnumFilterFromTypes.SHOT;
    private boolean mainIsIncluded = true;
    private boolean mainIsAnd = true;

    private ObservableList<IVFXFiltersFrom> listFilterFrom = FXCollections.observableArrayList();
    private ObservableList<IVFXFiltersSelect> listFilterSelect = FXCollections.observableArrayList();

    public static IVFXTags currentTagAll;
    private FilteredList<IVFXTags> filteredTagsAll;
    private VirtualFlow flowTblTagsAll;

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

    public void editFiltersTags(IVFXProjects ivfxProject) {
        currentProject = ivfxProject;
        try {

            AnchorPane root = FXMLLoader.load(filtersTagsController.getClass().getResource("../resources/fxml/filters_tags.fxml")); // в этот момент вызывается initialize()

            filtersTagsController.filterScene = new Scene(root);
            filtersTagsController.filterStage = new Stage();
            filtersTagsController.filterStage.setTitle("Редактор фильтров. Проект: " + currentProject.getName());
            filtersTagsController.filterStage.setScene(filtersTagsController.filterScene);
            filtersTagsController.filterStage.initModality(Modality.APPLICATION_MODAL);

            filtersTagsController.filterStage.setOnCloseRequest(we -> {
                System.out.println("Закрытые окна редактора фильтров.");
            });


            filtersTagsController.onStart();
            filtersTagsController.filterStage.showAndWait();


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

        int[] arr = {1,2};
        listTagsAll = FXCollections.observableArrayList(IVFXTags.loadList(currentProject,true, arr));
        tblTagsAll.setItems(listTagsAll);
        tblTagsAll.setStyle(FX_SELECTION_TABLES_RED);
        filteredTagsAll = new FilteredList<>(listTagsAll, e -> true);
        fldFindTagsAll.setDisable(false);

        // столбцы таблицы tblTagsAll
        colTypeTblTagsAll.setCellValueFactory(new PropertyValueFactory<>("tagTypeLetter"));
        colNameTblTagsAll.setCellValueFactory(new PropertyValueFactory<>("label1"));

        // обработка события отслеживания видимости на экране текущего тэга в таблице tblTags
        tblTagsAll.skinProperty().addListener((ChangeListener<Skin>) (ov, t, t1) -> {
            if (t1 == null) {
                return;
            }
            TableViewSkin tvs = (TableViewSkin) t1;
            ObservableList<Node> kids = tvs.getChildren();//    getChildrenUnmodifiable();
            if (kids == null || kids.isEmpty()) {
                return;
            }
            flowTblTagsAll = (VirtualFlow) kids.get(1);
        });

        // событие выбора записи в таблице tblTagsAll
        tblTagsAll.getSelectionModel().selectedItemProperty().addListener((v, oldValue, newValue) -> {
            if (newValue != null) {
                currentTagAll = newValue;  // устанавливаем выбранное значение в таблице как текущий персонаж
            }
        });

        // событие двойного клика в таблице tblTagsAll
        tblTagsAll.setOnMouseClicked(mouseEvent -> {
            if (mouseEvent.getButton().equals(MouseButton.PRIMARY) && mouseEvent.getClickCount() == 2) {
                addRecordsSelect();
            }
        });

        // скрываем заголовок у таблицы tblTagsAll
        tblTagsAll.widthProperty().addListener((source,oldWidth,newWidth) -> {
            Pane header = (Pane) tblTagsAll.lookup("TableHeaderRow");
            if (header.isVisible()){
                header.setMaxHeight(0);
                header.setMinHeight(0);
                header.setPrefHeight(0);
                header.setVisible(false);
            }
        });


        // обработка события отпускания кнопки в поле поиска fldFindTagsAll
        fldFindTagsAll.setOnKeyReleased(e -> {
            fldFindTagsAll.textProperty().addListener((v, oldValue, newValue) -> {
                filteredTagsAll.setPredicate((Predicate<? super IVFXTags>) ivfxTags-> {
                    if (newValue == null || newValue.isEmpty()) {
                        return true;
                    }
                    String lowerCaseFilter = newValue.toLowerCase();
                    if (!chFindTagsAllInProperties.isSelected() && ivfxTags.getName().toLowerCase().contains(lowerCaseFilter)) {
                        return true;
                    } if (chFindTagsAllInProperties.isSelected() && ivfxTags.getPropertiesValues().toLowerCase().contains(lowerCaseFilter)) {
                        return true;
                    }
                    return false;
                });

            });
            SortedList<IVFXTags> sortedTags = new SortedList<>(filteredTagsAll);
            sortedTags.comparatorProperty().bind(tblTagsAll.comparatorProperty());
            tblTagsAll.setItems(sortedTags);
            if (sortedTags.size() > 0) {
                tblTagsAll.getSelectionModel().select(sortedTags.get(0));
                tblTagsAll.scrollTo(sortedTags.get(0));
            }
        });

        // нажатие Enter в поле fldFindTagsAll - переход на первую запись в таблице tblTagsAll
        fldFindTagsAll.setOnKeyPressed(ke -> {
            if (ke.getCode().equals(KeyCode.ENTER))
            {
                tblTagsAll.requestFocus();
                tblTagsAll.getSelectionModel().select(0);
                tblTagsAll.scrollTo(0);
            }
        });

        // нажатие Enter в поле в таблице tblTagsAll
        tblTagsAll.setOnKeyPressed(ke -> {
            if (ke.getCode().equals(KeyCode.ENTER))
            {
                addRecordsSelect();
                fldFindTagsAll.requestFocus();
                fldFindTagsAll.setText("");
            }
        });

        listFilesInTabFiles = FXCollections.observableArrayList(IVFXFiles.loadList(currentProject));
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
//        IVFXFilterFromTypes ivfxFilterFromTypes = new IVFXFilterFromTypes();
//        ivfxFilterFromTypes.setIvfxEnumFilterFromTypes(IVFXEnumFilterFromTypes.FILE);
//        ivfxFilterFromTypes.setName("в файле");
//        listTblSelectFilterFromTypes.add(ivfxFilterFromTypes);

        IVFXFilterFromTypes ivfxFilterFromTypes = new IVFXFilterFromTypes();
        ivfxFilterFromTypes.setIvfxEnumFilterFromTypes(IVFXEnumFilterFromTypes.SHOT);
        ivfxFilterFromTypes.setName("в плане");
        listTblSelectFilterFromTypes.add(ivfxFilterFromTypes);

        ivfxFilterFromTypes = new IVFXFilterFromTypes();
        ivfxFilterFromTypes.setIvfxEnumFilterFromTypes(IVFXEnumFilterFromTypes.SCENE);
        ivfxFilterFromTypes.setName("в сцене");
        listTblSelectFilterFromTypes.add(ivfxFilterFromTypes);

        ivfxFilterFromTypes = new IVFXFilterFromTypes();
        ivfxFilterFromTypes.setIvfxEnumFilterFromTypes(IVFXEnumFilterFromTypes.EVENT);
        ivfxFilterFromTypes.setName("в событии");
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
        ivfxAndOr.setName("[AND]");
        listTblSelectAndOr.add(ivfxAndOr);

        ivfxAndOr = new IVFXAndOr();
        ivfxAndOr.setIsAnd(false);
        ivfxAndOr.setName("[OR]");
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

    }

    private void addRecordsSelect() {
        if (tabPersons.isSelected()) {
            if (currentTagAll != null) {

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

                filterSelect.setObjectId(currentTagAll.getId());
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
    void doBtnSelectAllFilesInTabFiles(ActionEvent event) {                                                             // Нажатие кнопки "Выбрать всё" в таблице файлов вкладки Файлы
        tblFilesInTabFiles.getSelectionModel().selectAll();
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
                    String personName = IVFXTags.load(filtersSelect.getObjectId(), false).getName();
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

        // Очищаем таблицу tbl_filters_shots
        sql = "DELETE FROM tbl_filters_shots";

        try {
            ps = Main.mainConnection.prepareStatement(sql);
            ps.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // собираем список планов
        sql = "INSERT INTO tbl_filters_shots (shot_id) " +
                "SELECT id " +
                "FROM tbl_shots " +
                "WHERE file_id = ?";
        for (IVFXFiltersFrom filterFrom : listFilterFrom) {
            listShotsInFilterFrom.addAll(IVFXShots.loadList(filterFrom.getIvfxFile(),false));
            try {
                ps = Main.mainConnection.prepareStatement(sql);
                ps.setInt   (1, filterFrom.getRecordId());
                ps.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        // Сейчас в таблице tbl_filters_shots и списке listShotsInFilterFrom находятся все планы всех файлов, выбранных в filterFrom

        // удаляем повторы (если надо)
//        if (checkBoxDontRepeatShots.isSelected()) {
//            List<IVFXShots> listShotsTmp = new ArrayList<>();
//            for (IVFXShots shotFrom : listShotsInFilterFrom) {
//                boolean isAlreadyAdded = false;
//                for (IVFXShots shotTmp : listShotsTmp) {
//                    if (shotTmp.getId() == shotFrom.getId()) {
//                        isAlreadyAdded = true;
//                        break;
//                    }
//                }
//                if (!isAlreadyAdded) listShotsTmp.add(shotFrom);
//            }
//            listShotsInFilterFrom = listShotsTmp;
//        }

        // на данный момент у нас сформирован список FROM планов listShotsInFilterFrom
        // теперь надо применить фильтр

        if (listFilterSelect.size() > 0) {

            class SelList {
                boolean isAnd;
                boolean isIncluded;
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

                    // формируем список планов для текущего селекта

                    IVFXTags selectedTag = IVFXTags.load(select.getObjectId(),false);

                    switch (select.getRecordType()) {

                        case SHOT:   // вхождение с точностью до плана
                            // Загружаем для выбранного тэга список его тэг-планов и берем из них планы
                            List<IVFXTagsShots> listTagsShots = IVFXTagsShots.loadList(selectedTag,false);
                            for (IVFXTagsShots tagShot: listTagsShots) {
                                listSelListShots.add(tagShot.getIvfxShot());
                            }
                            break;

                        case EVENT: // вхождение с точностью до события
                            // Сначала получаем список событий, в которые входит выбранный тэг, а потом берем планы этих событий
                            int[] arrTagsTypes1 = {4};
                            List<IVFXTagsTags> listTagTag1 = IVFXTagsTags.loadList(selectedTag, true, false, arrTagsTypes1);
                            for (IVFXTagsTags tagTag: listTagTag1) {
                                IVFXTags tmpTag = tagTag.getIvfxTagParent(); // Тэг-событие
                                List<IVFXTagsShots> listTagsShots2 = IVFXTagsShots.loadList(tmpTag,false);
                                for (IVFXTagsShots tagShot: listTagsShots2) {
                                    listSelListShots.add(tagShot.getIvfxShot());
                                }
                            }
                            break;

                        case SCENE: // вхождение с точностью до сцены

                            // Сначала получаем список сцен, в которые входит выбранный тэг, а потом берем планы этих сцен
                            int[] arrTagsTypes2 = {3};
                            List<IVFXTagsTags> listTagTag2 = IVFXTagsTags.loadList(selectedTag, true, false, arrTagsTypes2);
                            for (IVFXTagsTags tagTag: listTagTag2) {
                                IVFXTags tmpTag = tagTag.getIvfxTagParent(); // Тэг-сцена
                                List<IVFXTagsShots> listTagsShots2 = IVFXTagsShots.loadList(tmpTag,false);
                                for (IVFXTagsShots tagShot: listTagsShots2) {
                                    listSelListShots.add(tagShot.getIvfxShot());
                                }
                            }
                            break;

                        case FILE:  // вхождение с точностью до файла

                            break;

                        default:
                            break;

                    }

                    // Если текущий селект "не включает" тэг - надо "инвертировать" выборку

                    List<IVFXShots> tmpList = new ArrayList<>();
                    for (IVFXShots shotInFullList: listShotsInFilterFrom) {
                        boolean isFinded = false;
                        for (IVFXShots shotInSelList : listSelListShots) {
                            if (shotInFullList.isEqual(shotInSelList)) {
                                isFinded = true;
                                break;
                            }
                        }
                        if (select.getIsIncluded()) {
                            if (isFinded) {
                                tmpList.add(shotInFullList);
                            }
                        } else {
                            if (!isFinded) {
                                tmpList.add(shotInFullList);
                            }
                        }
                    }
                    listSelListShots = tmpList;


                    SelList selList = new SelList();
                    selList.isAnd = select.getIsAnd();
                    selList.isIncluded = select.getIsIncluded();
                    selList.listShots = listSelListShots;
                    listSelList.add(selList);

                }
            }  // КОНЕЦ цикла по строкам FilterSelect

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
        textCmdFull = "\"" + FFmpeg.PATH_TO_FFMPEG + "\"" + " -f concat -safe 0 -i " + "\"" + fileListShotsConcat + "\"" + " -map 0:v:0 ";
        for (IVFXFilesTracks track:IVFXFilesTracks.loadList(IVFXFiles.loadList(currentProject).get(0))) {
            if (track.getType().equals("Audio")) {
                if (track.isUse()) {
                    String typeorder = track.getProperty("@typeorder");
                    if (typeorder == null) typeorder = "1";
                    if (typeorder != null) {
                        textCmdFull = textCmdFull + "-map 0:a:" + (Integer.parseInt(typeorder)-1) + " ";
                    }
                }
            }
        }
        textCmdFull = textCmdFull + " -c copy -y " +"\"" +  fileVideoName + "\"" + "\n";


        if (checkBoxOpenVideoAfterCreation.isSelected()) {
            textCmdFull = textCmdFull + "\"" + fileVideoName + "\"";
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

    // Событие переключения флажка chFindTagsAllInProperties
    @FXML
    void doChFindTagsAllInProperties(ActionEvent event) {

    }


}

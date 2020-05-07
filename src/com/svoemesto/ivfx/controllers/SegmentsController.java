package com.svoemesto.ivfx.controllers;

import com.sun.javafx.scene.control.skin.TableViewSkin;
import com.sun.javafx.scene.control.skin.VirtualFlow;
import com.svoemesto.ivfx.tables.*;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import java.util.function.Predicate;

public class SegmentsController {

    @FXML
    private AnchorPane mainPane;

    @FXML
    private TableView<IVFXSegments> tblSegments;

    @FXML
    private TableColumn<IVFXSegments, String> colSegmentFrom;

    @FXML
    private TableColumn<IVFXSegments, String> colSegmentTo;

    @FXML
    private TextField ctlFindPersonsAll;

    @FXML
    private TableView<IVFXPersons> tblPersonsAll;

    @FXML
    private TableColumn<IVFXPersons, String> colPersonsAllPreview;

    @FXML
    private TextField ctlFindSegmentPersons;

    @FXML
    private TableView<IVFXSegmentsPersons> tblSegmentPersons;

    @FXML
    private TableColumn<IVFXSegmentsPersons, String> colSegmentPersonsPreview;

    @FXML
    private Button btnSegmentPersonsStep1;

    @FXML
    private TableView<IVFXSegmentsPersons> tblSegmentPersonsStep1;

    @FXML
    private TableColumn<IVFXSegmentsPersons, String> colSegmentPersonsStep1Preview;

    @FXML
    private Button btnSegmentPersonsStep2;

    @FXML
    private TableView<IVFXSegmentsPersons> tblSegmentPersonsStep2;

    @FXML
    private TableColumn<IVFXSegmentsPersons, String> colSegmentPersonsStep2Preview;

    @FXML
    private Button btnSegmentPersonsStep3;

    @FXML
    private TableView<IVFXSegmentsPersons> tblSegmentPersonsStep3;

    @FXML
    private TableColumn<IVFXSegmentsPersons, String> colSegmentPersonsStep3Preview;

    @FXML
    private Button btnScenePersonsToSegment;

    @FXML
    private TableView<IVFXScenesPersons> tblScenePersons;

    @FXML
    private TableColumn<IVFXScenesPersons, String> colScenePersonsPreview;

    @FXML
    private TableColumn<IVFXScenesPersons, String> colScenePersonsIsMain;

    @FXML
    private Button btnAddNewScene;

    @FXML
    private TableView<IVFXScenes> tblScenes;

    @FXML
    private TableColumn<IVFXScenes, String> colSceneStart;

    @FXML
    private TableColumn<IVFXScenes, String> colSceneName;

    @FXML
    private Button btnDeleteScene;

    @FXML
    private TableView<IVFXSegments> tblSceneSegments;

    @FXML
    private TableColumn<IVFXSegments, String> colSceneSegmentFrom;

    @FXML
    private TableColumn<IVFXSegments, String> colSceneSegmentTo;

    @FXML
    private Button btnAddNewEvent;

    @FXML
    private TableView<?> tblEvents;

    @FXML
    private TableColumn<?, ?> colEventStart;

    @FXML
    private TableColumn<?, ?> colEventEnd;

    @FXML
    private TableColumn<?, ?> colEventType;

    @FXML
    private TableColumn<?, ?> colEventName;

    @FXML
    private Button btnDeleteEvent;

    @FXML
    private TableView<?> tblEventSegments;

    @FXML
    private TableColumn<?, ?> colEventSegmentFrom;

    @FXML
    private TableColumn<?, ?> colEventSegmentTo;

    @FXML
    private TableView<?> tblEventPersons;

    @FXML
    private TableColumn<?, ?> colEventPersonsPreview;

    @FXML
    private TableColumn<?, ?> colEventPersonsIsMain;

    @FXML
    private TableView<?> tblScenePersonsToEvents;

    @FXML
    private TableColumn<?, ?> colScenePersonsToEventsPreview;

    @FXML
    private TableColumn<?, ?> colScenePersonsToEventsIsMain;

    @FXML
    private Button btnOK;

    private static SegmentsController segmentsController = new SegmentsController();
    private Stage controllerStage;
    private Scene controllerScene;
    private static boolean isWorking;

    private static IVFXFiles currentFile;
    private static int initFrameNumber;

    private IVFXSegments currentSegment;
    private IVFXSegments currentSegmentForScene;
    private IVFXScenes currentScene;

    private ObservableList<IVFXSegments> listSegments = FXCollections.observableArrayList();
    private List<IVFXSegments> listSegmentsOriginal;
    private ObservableList<IVFXPersons> listPersonsAll = FXCollections.observableArrayList();
    private ObservableList<IVFXSegmentsPersons> listSegmentPersons = FXCollections.observableArrayList();
    private ObservableList<IVFXSegmentsPersons> listSegmentPersonsStep1 = FXCollections.observableArrayList();
    private ObservableList<IVFXSegmentsPersons> listSegmentPersonsStep2 = FXCollections.observableArrayList();
    private ObservableList<IVFXSegmentsPersons> listSegmentPersonsStep3 = FXCollections.observableArrayList();
    private ObservableList<IVFXScenes> listScenes = FXCollections.observableArrayList();

    private ObservableList<IVFXSegments> listSegmentsForScene = FXCollections.observableArrayList();
    private ObservableList<IVFXScenesPersons> listScenePersons = FXCollections.observableArrayList();

    private FilteredList<IVFXSegmentsPersons> filteredSegmentPersons;

    private VirtualFlow flowTblSegments;
    private VirtualFlow flowTblScenes;
    private VirtualFlow flowTblSceneSegments;

    public void editSegments(IVFXFiles ivfxFile, int initFrameNum) {

        currentFile = ivfxFile;
        initFrameNumber = initFrameNum;

        try {

            AnchorPane root = FXMLLoader.load(segmentsController.getClass().getResource("../resources/fxml/segments.fxml")); // в этот момент вызывается initialize()

            segmentsController.controllerScene = new Scene(root);
            segmentsController.controllerStage = new Stage();
            segmentsController.controllerStage.setTitle("Редактор сегментов. " + segmentsController.currentFile.getTitle());
            segmentsController.controllerStage.setScene(segmentsController.controllerScene);
            segmentsController.controllerStage.initModality(Modality.APPLICATION_MODAL);

            segmentsController.controllerStage.setOnCloseRequest(we -> {
                segmentsController.isWorking = false;
                System.out.println("Закрытые окна редактора сегментов.");
            });


            segmentsController.onStart();
            segmentsController.controllerStage.showAndWait();


            System.out.println("Завершение работы editSegments");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private class LoadListSegments extends Thread {
        public boolean isLoaded;
        @Override
        public void run() {
            isLoaded = false;
            Platform.runLater(()->{
                List<IVFXSegments> list = IVFXSegments.loadList(currentFile, true);
                listSegments = FXCollections.observableArrayList(list);
                isLoaded = true;
            });
            while (!isLoaded) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            };

        }
    }
    private void loadListSegments() {

        LoadListSegments loadListSegments = new LoadListSegments();
        loadListSegments.start();
        try {
            loadListSegments.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void initialize() {

//        loadListSegments();
        listSegmentsOriginal = IVFXSegments.loadList(currentFile, true);
        listSegments = FXCollections.observableArrayList(IVFXSegments.loadList(currentFile, true));

        listPersonsAll = FXCollections.observableArrayList(IVFXPersons.loadList(currentFile.getIvfxProject(),true));
        listScenes = FXCollections.observableArrayList(IVFXScenes.loadList(currentFile));

        for (IVFXScenes scene : listScenes) {
            List<IVFXScenesSegments> sceneSegments = IVFXScenesSegments.loadList(scene);
            for (IVFXScenesSegments sceneSegment: sceneSegments) {
                for (IVFXSegments segment: listSegmentsOriginal) {
                    if (segment.isEqual(sceneSegment.getIvfxSegment())) {
                        scene.getSegments().add(segment);
                        break;
                    }
                }
            }
            scene.setScenePersons(IVFXScenesPersons.loadList(scene,true));
        }

        FilteredList<IVFXPersons> filteredPersonAll = new FilteredList<>(listPersonsAll, e -> true);
        filteredSegmentPersons = new FilteredList<>(listSegmentPersons, e -> true);

        // таблица сегментов
        colSegmentFrom.setCellValueFactory(new PropertyValueFactory<>("labelFirst"));
        colSegmentTo.setCellValueFactory(new PropertyValueFactory<>("labelLast"));
        tblSegments.setItems(listSegments);

        // таблица всех персонажей
        colPersonsAllPreview.setCellValueFactory(new PropertyValueFactory<>("preview"));
        tblPersonsAll.setItems(listPersonsAll);

        // таблица персонажей сегмента
        colSegmentPersonsPreview.setCellValueFactory(new PropertyValueFactory<>("preview"));
        tblSegmentPersons.setItems(listSegmentPersons);

        // таблица персонажей сегмента -1
        colSegmentPersonsStep1Preview.setCellValueFactory(new PropertyValueFactory<>("preview"));
        tblSegmentPersonsStep1.setItems(listSegmentPersonsStep1);

        // таблица персонажей сегмента -2
        colSegmentPersonsStep2Preview.setCellValueFactory(new PropertyValueFactory<>("preview"));
        tblSegmentPersonsStep2.setItems(listSegmentPersonsStep2);

        // таблица персонажей сегмента -3
        colSegmentPersonsStep3Preview.setCellValueFactory(new PropertyValueFactory<>("preview"));
        tblSegmentPersonsStep3.setItems(listSegmentPersonsStep3);

        // таблица сцен
        colSceneStart.setCellValueFactory(new PropertyValueFactory<>("start"));
        colSceneName.setCellValueFactory(new PropertyValueFactory<>("name"));
        tblScenes.setItems(listScenes);

        // таблица сегментов сцен
        colSceneSegmentFrom.setCellValueFactory(new PropertyValueFactory<>("labelFirst"));
        colSceneSegmentTo.setCellValueFactory(new PropertyValueFactory<>("labelLast"));
        tblSceneSegments.setItems(listSegmentsForScene);

        // таблица персонажей сцены
        colScenePersonsPreview.setCellValueFactory(new PropertyValueFactory<>("preview"));
        colScenePersonsIsMain.setCellValueFactory(new PropertyValueFactory<>("personIsMainStr"));
        tblScenePersons.setItems(listScenePersons);


        // событие выбора записи в таблице сегментов
        tblSegments.getSelectionModel().selectedItemProperty().addListener((v, oldValue, newValue) -> {
            if (newValue != null) {
                currentSegment = newValue;  // устанавливаем выбранное значение в таблице как текущий сегмент

                // загружаем список персонажей текущего сегмента и привязываем его к таблице персонажей сегмента (и фильтра по ней)
                listSegmentPersons = FXCollections.observableArrayList(IVFXSegmentsPersons.loadList(currentSegment,true));
                tblSegmentPersons.setItems(listSegmentPersons);
                filteredSegmentPersons = new FilteredList<>(listSegmentPersons, e -> true);

                // загружаем списки персонажей сегментов на 3 шага назад от текущего и привязываем их к таблицам
                IVFXSegments prevSeg1 = getPreviousSegment(currentSegment);
                if (prevSeg1 != null) {
                    listSegmentPersonsStep1 = FXCollections.observableArrayList(IVFXSegmentsPersons.loadList(prevSeg1,true));
                    IVFXSegments prevSeg2 = getPreviousSegment(prevSeg1);
                    if (prevSeg2 != null) {
                        listSegmentPersonsStep2 = FXCollections.observableArrayList(IVFXSegmentsPersons.loadList(prevSeg2,true));
                        IVFXSegments prevSeg3 = getPreviousSegment(prevSeg2);
                        if (prevSeg3 != null) {
                            listSegmentPersonsStep3 = FXCollections.observableArrayList(IVFXSegmentsPersons.loadList(prevSeg3, true));
                        } else {
                            listSegmentPersonsStep3.clear();
                        }
                    } else {
                        listSegmentPersonsStep2.clear();
                    }
                } else {
                    listSegmentPersonsStep1.clear();
                }
                tblSegmentPersonsStep1.setItems(listSegmentPersonsStep1);
                tblSegmentPersonsStep2.setItems(listSegmentPersonsStep2);
                tblSegmentPersonsStep3.setItems(listSegmentPersonsStep3);

                boolean isFind = false;
                for (IVFXScenes scene : listScenes) { // цикл по списку сцен
                    for (IVFXSegments segment: scene.getSegments()) {   // цикл по списку сегментов сцены
                        if (segment.isEqual(currentSegment)) {  // если цегмент сцены совпадает с текущим сегментом

                            if (currentScene == null) { // если текущая сцена не выбрана
                                currentScene = scene;   // устанавливаем текущую сцену сценой из сегмента
                                tblScenes.getSelectionModel().select(currentScene); // переходим на нее в таблице сцен
                                tblScenesSmartScrollToCurrent();
                            }

                            if (!(currentScene.isEqual(scene))) {   // если текущая сцена не равна сцене сегмента
                                currentScene = scene;   // устанавливаем текущую сцену сценой из сегмента
                                tblScenes.getSelectionModel().select(currentScene); // переходим на нее в таблице сцен
                                tblScenesSmartScrollToCurrent();
                            }

                            for (IVFXSegments segmentForScene: currentScene.getSegments()) {   // цикл по списку сегментов сцены
                                if (segmentForScene.isEqual(currentSegment)) { // если сегмент сцены совпадает с текущим сегментом
                                    currentSegmentForScene = segmentForScene;   // устанавливаем текущий сегмент сцены раным сегменту сцены
                                    tblSceneSegments.getSelectionModel().select(currentSegmentForScene);    // переходим на него в таблице сегментов сцены
                                    tblSceneSegmentsSmartScrollToCurrent();
                                    break;
                                }
                            }

                            if (currentSegmentForScene == null || !(currentSegmentForScene.isEqual(currentSegment))) {
                                currentSegmentForScene = currentSegment; // устанавливаем сцену сегмента как текущая сцена
                                tblSceneSegments.getSelectionModel().select(currentSegmentForScene);    // переходим на нее в таблице сегментов сцены
                                tblSceneSegmentsSmartScrollToCurrent();
                            }
                            isFind = true;
                            break;
                        }
                        if (isFind) break;
                    }
                }


            }
        });

        // событие выбора записи в таблице сцен
        tblScenes.getSelectionModel().selectedItemProperty().addListener((v, oldValue, newValue) -> {
            if (newValue != null) {
                currentScene = newValue;    // устанавливаем текущую сцену
                listSegmentsForScene = FXCollections.observableArrayList(currentScene.getSegments()); // загражаем список сегментов для нее
                tblSceneSegments.setItems(listSegmentsForScene);    // привязываем таблицу
                boolean isFound = false;
                for (IVFXSegments segmentForScene: listSegmentsForScene) {  // цикл по сегментам текущей сцены
                    if (currentSegment == null) { // если текущий сегмент пустой
                        for (IVFXSegments segment: listSegments) { // цикл по списку сегментов
                            if (segment.isEqual(segmentForScene)) {
                                currentSegment = segment;
                                tblSceneSegments.getSelectionModel().select(currentSegment);
                                tblSegmentsSmartScrollToCurrent();
                                break;
                            }
                        }
                    }

                    if (segmentForScene.isEqual(currentSegment)) {
                        currentSegmentForScene = segmentForScene;
                        tblSceneSegments.getSelectionModel().select(currentSegmentForScene);
                        tblSceneSegmentsSmartScrollToCurrent();
                        isFound = true;
                    }
                }
                if (!isFound) {
                    for (IVFXSegments segment: listSegments) { // цикл по списку сегментов
                        if (segment.isEqual(currentScene.getSegments().get(0))) {
                            currentSegment = segment;
                            tblSceneSegments.getSelectionModel().select(currentSegment);
                            tblSegmentsSmartScrollToCurrent();
                            currentSegmentForScene = currentScene.getSegments().get(0);
                            tblSceneSegments.getSelectionModel().select(currentSegmentForScene);
                            tblSceneSegmentsSmartScrollToCurrent();
                            break;
                        }
                    }
                }

                listScenePersons = FXCollections.observableArrayList(currentScene.getScenePersons());
                tblScenePersons.setItems(listScenePersons);

            }
        });

        // событие выбора записи в таблице сегментов сцены
        tblSceneSegments.getSelectionModel().selectedItemProperty().addListener((v, oldValue, newValue) -> {
            if (newValue != null) {
                currentSegmentForScene = newValue;
                for (IVFXSegments segment: listSegments) {
                    if (segment.isEqual(currentSegmentForScene)) {
                        tblSegments.getSelectionModel().select(segment);
                        tblSegmentsSmartScrollToCurrent();
                    }
                }
            }
        });

        // фильтация таблицы всех персонажей
        ctlFindPersonsAll.setOnKeyReleased(e -> {
            ctlFindPersonsAll.textProperty().addListener((v, oldValue, newValue) -> {
                filteredPersonAll.setPredicate((Predicate<? super IVFXPersons>) ivfxPerson-> {
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
            SortedList<IVFXPersons> sortedPersonAll = new SortedList<>(filteredPersonAll);
            sortedPersonAll.comparatorProperty().bind(tblPersonsAll.comparatorProperty());
            tblPersonsAll.setItems(sortedPersonAll);
            if (sortedPersonAll.size() > 0) {
                tblPersonsAll.getSelectionModel().select(sortedPersonAll.get(0));
                tblPersonsAll.scrollTo(sortedPersonAll.get(0));
            }
        });

        // фильтация таблицы персонажей сегмента
        ctlFindSegmentPersons.setOnKeyReleased(e -> {
            ctlFindSegmentPersons.textProperty().addListener((v, oldValue, newValue) -> {
                filteredSegmentPersons.setPredicate((Predicate<? super IVFXSegmentsPersons>) ivfxSegmentsPersons-> {
                    if (newValue == null || newValue.isEmpty()) {
                        return true;
                    }
                    String lowerCaseFilter = newValue.toLowerCase();
                    if (ivfxSegmentsPersons.getName().toLowerCase().contains(lowerCaseFilter)) {
                        return true;
                    } if (ivfxSegmentsPersons.getDescription().toLowerCase().contains(lowerCaseFilter)) {
                        return true;
                    }
                    return false;
                });

            });
            SortedList<IVFXSegmentsPersons> sortedSegmentPerson = new SortedList<>(filteredSegmentPersons);
            sortedSegmentPerson.comparatorProperty().bind(tblSegmentPersons.comparatorProperty());
            tblSegmentPersons.setItems(sortedSegmentPerson);
            if (sortedSegmentPerson.size() > 0) {
                tblSegmentPersons.getSelectionModel().select(sortedSegmentPerson.get(0));
                tblSegmentPersons.scrollTo(sortedSegmentPerson.get(0));
            }
        });

        // событие отслеживани видимости на экране текущей записи в таблице tblSegments
        tblSegments.skinProperty().addListener((ChangeListener<Skin>) (ov, t, t1) -> {
            if (t1 == null) return;
            TableViewSkin tvs = (TableViewSkin) t1;
            ObservableList<Node> kids = tvs.getChildren();
            if (kids == null || kids.isEmpty()) return;
            flowTblSegments = (VirtualFlow) kids.get(1);
        });

        // событие отслеживани видимости на экране текущей записи в таблице tblScenes
        tblScenes.skinProperty().addListener((ChangeListener<Skin>) (ov, t, t1) -> {
            if (t1 == null) return;
            TableViewSkin tvs = (TableViewSkin) t1;
            ObservableList<Node> kids = tvs.getChildren();
            if (kids == null || kids.isEmpty()) return;
            flowTblScenes = (VirtualFlow) kids.get(1);
        });

        // событие отслеживани видимости на экране текущей записи в таблице tblSceneSegments
        tblSceneSegments.skinProperty().addListener((ChangeListener<Skin>) (ov, t, t1) -> {
            if (t1 == null) return;
            TableViewSkin tvs = (TableViewSkin) t1;
            ObservableList<Node> kids = tvs.getChildren();
            if (kids == null || kids.isEmpty()) return;
            flowTblSceneSegments = (VirtualFlow) kids.get(1);
        });


        // ГОРЯЧИЕ КЛАВИШИ
        mainPane.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.F7) goToPreviousSegment();
            if (e.getCode() == KeyCode.F8) goToNextSegment();
        });



        // скрываем заголовок у таблицы tblPersonsAll
        tblPersonsAll.widthProperty().addListener((source,oldWidth,newWidth) -> {
            Pane header = (Pane) tblPersonsAll.lookup("TableHeaderRow");
            if (header.isVisible()){
                header.setMaxHeight(0);
                header.setMinHeight(0);
                header.setPrefHeight(0);
                header.setVisible(false);
            }
        });

        // скрываем заголовок у таблицы tblSegmentPersons
        tblSegmentPersons.widthProperty().addListener((source,oldWidth,newWidth) -> {
            Pane header = (Pane) tblSegmentPersons.lookup("TableHeaderRow");
            if (header.isVisible()){
                header.setMaxHeight(0);
                header.setMinHeight(0);
                header.setPrefHeight(0);
                header.setVisible(false);
            }
        });

        // скрываем заголовок у таблицы tblSegmentPersonsStep1
        tblSegmentPersonsStep1.widthProperty().addListener((source,oldWidth,newWidth) -> {
            Pane header = (Pane) tblSegmentPersonsStep1.lookup("TableHeaderRow");
            if (header.isVisible()){
                header.setMaxHeight(0);
                header.setMinHeight(0);
                header.setPrefHeight(0);
                header.setVisible(false);
            }
        });

        // скрываем заголовок у таблицы tblSegmentPersonsStep2
        tblSegmentPersonsStep2.widthProperty().addListener((source,oldWidth,newWidth) -> {
            Pane header = (Pane) tblSegmentPersonsStep2.lookup("TableHeaderRow");
            if (header.isVisible()){
                header.setMaxHeight(0);
                header.setMinHeight(0);
                header.setPrefHeight(0);
                header.setVisible(false);
            }
        });

        // скрываем заголовок у таблицы tblSegmentPersonsStep3
        tblSegmentPersonsStep3.widthProperty().addListener((source,oldWidth,newWidth) -> {
            Pane header = (Pane) tblSegmentPersonsStep3.lookup("TableHeaderRow");
            if (header.isVisible()){
                header.setMaxHeight(0);
                header.setMinHeight(0);
                header.setPrefHeight(0);
                header.setVisible(false);
            }
        });

    }

    private void goToPreviousSegment() {
        if (currentSegment != null) {
            int currIndex = listSegments.indexOf(currentSegment);
            int prevIndex = currIndex - 1;
            if (prevIndex >= 0) {
                currentSegment = listSegments.get(prevIndex);
                tblSegments.getSelectionModel().select(currentSegment);
                tblSegmentsSmartScrollToCurrent();
            }
        }
    }

    private void goToNextSegment() {
        if (currentSegment != null) {
            int currIndex = listSegments.indexOf(currentSegment);
            int nextIndex = currIndex + 1;
            if (nextIndex < listSegments.size()) {
                currentSegment = listSegments.get(nextIndex);
                tblSegments.getSelectionModel().select(currentSegment);
                tblSegmentsSmartScrollToCurrent();
            }
        }
    }
    private void tblSegmentsSmartScrollToCurrent() {
        if (flowTblSegments != null && flowTblSegments.getCellCount() > 0) {
            int first = flowTblSegments.getFirstVisibleCell().getIndex();
            int last = flowTblSegments.getLastVisibleCell().getIndex();
            int selected = tblSegments.getSelectionModel().getSelectedIndex();
            if (selected < first || selected > last) {
                tblSegments.scrollTo(currentSegment);
            }
        }
    }

    private void tblScenesSmartScrollToCurrent() {
        if (flowTblScenes != null && flowTblScenes.getCellCount() > 0) {
            int first = flowTblScenes.getFirstVisibleCell().getIndex();
            int last = flowTblScenes.getLastVisibleCell().getIndex();
            int selected = tblScenes.getSelectionModel().getSelectedIndex();
            if (selected < first || selected > last) {
                tblScenes.scrollTo(currentScene);
            }
        }
    }

    private void tblSceneSegmentsSmartScrollToCurrent() {
        if (flowTblSceneSegments != null && flowTblSceneSegments.getCellCount() > 0) {
            int first = flowTblSceneSegments.getFirstVisibleCell().getIndex();
            int last = flowTblSceneSegments.getLastVisibleCell().getIndex();
            int selected = tblSceneSegments.getSelectionModel().getSelectedIndex();
            if (selected < first || selected > last) {
                tblSceneSegments.scrollTo(currentSegmentForScene);
            }
        }
    }

    public void onStart() {

    }

    @FXML
    void doAddNewEvent(ActionEvent event) {

    }

    @FXML
    void doAddNewScene(ActionEvent event) {

    }

    @FXML
    void doDeleteEvent(ActionEvent event) {

    }

    @FXML
    void doDeleteScene(ActionEvent event) {

    }

    @FXML
    void doScenePersonsToSegment(ActionEvent event) {

    }

    @FXML
    void doSegmentPersonsStep1(ActionEvent event) {

    }

    @FXML
    void doSegmentPersonsStep2(ActionEvent event) {

    }

    @FXML
    void doSegmentPersonsStep3(ActionEvent event) {

    }



    private IVFXSegments getPreviousSegment(IVFXSegments segment) {

        for (IVFXSegments seg: listSegments) {
            if (seg.getLastFrameNumber() == segment.getFirstFrameNumber()-1) {
                return seg;
            }
        }

        return null;
    }

}

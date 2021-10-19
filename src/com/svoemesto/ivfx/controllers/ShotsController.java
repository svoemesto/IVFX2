package com.svoemesto.ivfx.controllers;

import com.sun.javafx.scene.control.skin.TableViewSkin;
import com.sun.javafx.scene.control.skin.VirtualFlow;
import com.svoemesto.ivfx.tables.*;
import com.svoemesto.ivfx.tasks.*;
import com.svoemesto.ivfx.utils.*;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class ShotsController {

    private final static boolean DO_IN_MULTITHREADS = false;

    @FXML
    private GridPane gridpanePreviwe;

    @FXML
    private Label mainLabelFullSizePicture;

    @FXML
    private Pane panePlayer;

    @FXML
    private MediaView mainMediaView;

    @FXML
    private Label mainLabelPlayer;

    @FXML
    private Slider mainSlider;

    @FXML
    private AnchorPane mainPane;

    @FXML
    private TableView<IVFXShots> tblShots;

    @FXML
    private TableColumn<IVFXShots, String> colShotFrom;

    @FXML
    private TableColumn<IVFXShots, String> colShotTo;

    @FXML
    private TextField ctlFindPersonsAll;

    @FXML
    private TableView<IVFXPersons> tblPersonsAll;

    @FXML
    private TableColumn<IVFXPersons, String> colPersonsAllPreview;

    @FXML
    private TextField ctlFindShotPersons;

    @FXML
    private TableView<IVFXShotsPersons> tblShotPersons;

    @FXML
    private TableColumn<IVFXShotsPersons, String> colShotPersonsPreview;

    @FXML
    private Button btnShotPersonsStep1;

    @FXML
    private TableView<IVFXShotsPersons> tblShotPersonsStep1;

    @FXML
    private TableColumn<IVFXShotsPersons, String> colShotPersonsStep1Preview;

    @FXML
    private Button btnShotPersonsStep2;

    @FXML
    private TableView<IVFXShotsPersons> tblShotPersonsStep2;

    @FXML
    private TableColumn<IVFXShotsPersons, String> colShotPersonsStep2Preview;

    @FXML
    private Button btnShotPersonsStep3;

    @FXML
    private TableView<IVFXShotsPersons> tblShotPersonsStep3;

    @FXML
    private TableColumn<IVFXShotsPersons, String> colShotPersonsStep3Preview;

    @FXML
    private Button btnScenePersonsToShot;

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
    private TableView<IVFXShots> tblSceneShots;

    @FXML
    private TableColumn<IVFXShots, String> colSceneShotFrom;

    @FXML
    private TableColumn<IVFXShots, String> colSceneShotTo;

    @FXML
    private Button btnAddNewEvent;

    @FXML
    private ComboBox<String> comboEventType;

    @FXML
    private Button btnEndEvent;

    @FXML
    private Button btnStartEvent;

    @FXML
    private TableView<IVFXEvents> tblEvents;

    @FXML
    private TableColumn<IVFXEvents, String> colEventStart;

    @FXML
    private TableColumn<IVFXEvents, String> colEventEnd;

    @FXML
    private TableColumn<IVFXEvents, String> colEventType;

    @FXML
    private TableColumn<IVFXEvents, String> colEventName;

    @FXML
    private Button btnDeleteEvent;

    @FXML
    private TableView<IVFXShots> tblEventShots;

    @FXML
    private TableColumn<IVFXShots, String> colEventShotFrom;

    @FXML
    private TableColumn<IVFXShots, String> colEventShotTo;

    @FXML
    private TableView<IVFXEventsPersons> tblEventPersons;

    @FXML
    private TableColumn<IVFXEventsPersons, String> colEventPersonsPreview;

    @FXML
    private TableColumn<IVFXEventsPersons, String> colEventPersonsIsMain;

    @FXML
    private TableView<IVFXScenesPersons> tblScenePersonsToEvents;

    @FXML
    private TableColumn<IVFXScenesPersons, String> colScenePersonsToEventsPreview;

    @FXML
    private TableColumn<IVFXScenesPersons, String> colScenePersonsToEventsIsMain;

    @FXML
    private Button btnOK;

    @FXML
    private ContextMenu contxtMenuFrame;

    @FXML
    private ToggleButton tbPlayerLoop;

    @FXML
    private ToggleGroup tgPlayerLoopNextStop;

    @FXML
    private ToggleButton tbPlayerNext;

    @FXML
    private ToggleButton tbPlayerStop;

    @FXML
    private ToggleButton tbPlayerSpeed1;

    @FXML
    private ToggleGroup tgPlayerSpeed;

    @FXML
    private ToggleButton tbPlayerSpeed2;

    @FXML
    private ToggleButton tbPlayerSpeed3;

    @FXML
    private ToggleButton tbPlayerSpeed4;

    @FXML
    private ToggleButton tbPlayerSpeed5;

    @FXML
    private ProgressBar ctlProgressPreview;

    @FXML
    private ProgressBar ctlProgressShots;

    @FXML
    private ProgressBar ctlProgressPersonsAll;

    @FXML
    private ProgressBar ctlProgressShotPersons;

    @FXML
    private ProgressBar ctlProgressShotPersonsStep1;

    @FXML
    private ProgressBar ctlProgressShotPersonsStep2;

    @FXML
    private ProgressBar ctlProgressShotPersonsStep3;

    @FXML
    private ProgressBar ctlProgressScenePersons;

    @FXML
    private ProgressBar ctlProgressScenes;

    @FXML
    private ProgressBar ctlProgressSceneShots;

    @FXML
    private ProgressBar ctlProgressEvents;

    @FXML
    private ProgressBar ctlProgressEventShots;

    @FXML
    private ProgressBar ctlProgressEventPersons;

    @FXML
    private ProgressBar ctlProgressScenePersonsToEvents;


    private static ShotsController shotsController = new ShotsController();
    private Stage controllerStage;
    private Scene controllerScene;
    private static boolean isWorking;

    private static IVFXFiles currentFile;
    private static int initFrameNumber;

    private IVFXShots currentShot;
    private IVFXShots currentShotForScene;
    private IVFXShots currentShotForEvent;
    private IVFXScenes currentScene;
    private IVFXEvents currentEvent;
    public IVFXEventsTypes currentEventType;

    private ObservableList<IVFXShots> listShots = FXCollections.observableArrayList();
    private List<IVFXShots> listShotsOriginal;
    private ObservableList<IVFXPersons> listPersonsAll = FXCollections.observableArrayList();
    private ObservableList<IVFXShotsPersons> listShotPersons = FXCollections.observableArrayList();
    private ObservableList<IVFXShotsPersons> listShotPersonsStep1 = FXCollections.observableArrayList();
    private ObservableList<IVFXShotsPersons> listShotPersonsStep2 = FXCollections.observableArrayList();
    private ObservableList<IVFXShotsPersons> listShotPersonsStep3 = FXCollections.observableArrayList();
    private ObservableList<IVFXScenes> listScenes = FXCollections.observableArrayList();

    private ObservableList<IVFXShots> listShotsForScene = FXCollections.observableArrayList();
    private ObservableList<IVFXShots> listShotsForEvent = FXCollections.observableArrayList();
    private ObservableList<IVFXScenesPersons> listScenePersons = FXCollections.observableArrayList();
    private ObservableList<IVFXScenesPersons> listScenePersonsForEvents = FXCollections.observableArrayList();
    private ObservableList<IVFXEventsPersons> listEventPersons = FXCollections.observableArrayList();
    private ObservableList<IVFXEvents> listEvents = FXCollections.observableArrayList();
    private ObservableList<String> listEventTypesNames = FXCollections.observableArrayList();

    private FilteredList<IVFXPersons> filteredPersonAll;
    private FilteredList<IVFXShotsPersons> filteredShotPersons;

    private VirtualFlow flowTblShots;
    private VirtualFlow flowTblScenes;
    private VirtualFlow flowTblEvents;
    private VirtualFlow flowTblSceneShots;
    private VirtualFlow flowTblEventShots;

    private final static int COLUMNS_COUNT = 7;
    private final static int ROWS_COUNT = 5;
    private final static int PICTURE_WIDTH = 135;
    private final static int PICTURE_HEIGHT = 75;
    private List<MatrixLabels> listLabels = new ArrayList<>();

    public Media mainMedia;
    public MediaPlayer mainMediaPlayer;

    private String fxBorderDefault = "-fx-border-color:#0f0f0f;-fx-border-width:1";  // стиль бордюра лейбла по-умолчаснию
    private String fxBorderNone = "-fx-border-color:NONE;-fx-border-width:1";
    private String fxBorderYellow = "-fx-border-color:YELLOW;-fx-border-width:1";
    private String fxBorderMagenta = "-fx-border-color:MAGENTA;-fx-border-width:1";
    private String fxBorderRed = "-fx-border-color:RED;-fx-border-width:1";
    private String mainFramePreviewFile;
    private String mainFrameFullSizeFile;

    private IVFXPersons mainSelectedPerson;

    private boolean updateFullPreviewDuringMoveMouse = true;

    private PlayerLoopNextStop playerLoopNextStop = PlayerLoopNextStop.LOOP;
    private boolean playerIsEndOfMedia = false;
    private double playerSpeed = 1.0;
    private List<Label> listLabelsPreview = new ArrayList<>();

    private int currentFrameToScroll = 0;
    private MatrixLabels prevMatrixLabel = null;

    private enum PlayerLoopNextStop {
        LOOP,
        NEXT,
        STOP
    }

    private class MatrixLabels {
        Label label;
        int column;
        int row;
        ImageView imageView;
        int frameNumber;
    }


    private void initializeLabelsPreview() {

        IVFXFiles ivfxFiles = currentFile;
        IVFXShots ivfxShots = currentShot;

        int firstFrameNumber = ivfxShots.getFirstFrameNumber(); // Номер первого кардра
        int lastFrameNumber = ivfxShots.getLastFrameNumber();   // Номер последнего кадра
        int countLabels = COLUMNS_COUNT * ROWS_COUNT;             // Количество лейблов в пэйне
        int countFrames = lastFrameNumber - firstFrameNumber;      // Количество кадров в плане
        double step = (double)countFrames / (double)countLabels;
        if (step < 1) step = 1;

        double finalStep = step;

        Platform.runLater(()->{ctlProgressPreview.setVisible(true);});

        for (int i = 0; i < countLabels; i++) {

            int finalI = i;
            Platform.runLater(()->{ctlProgressPreview.setProgress((double) finalI /countLabels);});


            double diff = i * finalStep;
            int frameNumber = firstFrameNumber + (int) Math.round(diff);
            if (frameNumber > lastFrameNumber) frameNumber = 0;
            if (i == countLabels - 1 && frameNumber != 0) frameNumber = lastFrameNumber;
            MatrixLabels matrixLabels = listLabels.get(i);
            matrixLabels.frameNumber = frameNumber;
            if (frameNumber != 0) {
                currentFrameToScroll = frameNumber;
                String fileName = ivfxFiles.getFolderFramesPreview() + "\\" + ivfxFiles.getShortName() + ivfxFiles.FRAMES_PREFIX + String.format("%06d", frameNumber) + ".jpg";
                String fileNameFullSize = ivfxFiles.getFolderFramesFull() + "\\" + ivfxFiles.getShortName() + ivfxFiles.FRAMES_PREFIX + String.format("%06d", frameNumber) + ".jpg";
                if (i == 0) {
                    onMouseClickLabel(fileName, fileNameFullSize, mainLabelFullSizePicture);
                }
                File file = new File(fileName);
                if (file.exists()) {
                    try {
                        BufferedImage bufferedImage = ImageIO.read(file);
                        matrixLabels.imageView = new ImageView(ConvertToFxImage.convertToFxImage(bufferedImage));
                        matrixLabels.label.setGraphic(matrixLabels.imageView);
                    } catch (IOException e) {
                    }
                    matrixLabels.label.setStyle(fxBorderDefault);

                    matrixLabels.label.setOnMouseEntered(e -> {
                        onMouseEnterLabel(matrixLabels);
                    });
                    matrixLabels.label.setOnMouseExited(e -> {
                        onMouseExitLabel(matrixLabels);
                    });

                    int finalFrameNumber = frameNumber;
                    currentFrameToScroll = frameNumber;
                    matrixLabels.label.setOnMouseClicked(mouseEvent -> {
                        if (mouseEvent.getButton().equals(MouseButton.PRIMARY) && mouseEvent.getClickCount() == 1) {
                            onMouseClickLabel(fileName, fileNameFullSize, mainLabelFullSizePicture);
                        } else if (mouseEvent.getButton().equals(MouseButton.PRIMARY) && mouseEvent.getClickCount() == 2) {
                            updateFullPreviewDuringMoveMouse = !updateFullPreviewDuringMoveMouse;
                        } else if (mouseEvent.getButton().equals(MouseButton.PRIMARY) && mouseEvent.getClickCount() == 3) {
                            int firstShotFrame = currentShot.getFirstFrameNumber();
                            new FramesController().editTransitions(currentFile, finalFrameNumber);
                            initialize();
                            for (IVFXShots shot: listShots) {
                                if (shot.getFirstFrameNumber() >= firstShotFrame && shot.getLastFrameNumber() <= firstShotFrame) {
                                    currentShot = shot;
                                    goToShot(currentShot);
                                    break;
                                }
                            }

                        }
                    });

                } else {
                    matrixLabels.label.setGraphic(null);
                    matrixLabels.label.setOnMouseEntered(e -> {
                    });
                    matrixLabels.label.setOnMouseExited(e -> {
                    });
                    matrixLabels.label.setOnMouseClicked(e -> {
                    });
                    matrixLabels.label.setStyle(fxBorderNone);
                }
            } else {
                matrixLabels.label.setGraphic(null);
                matrixLabels.label.setOnMouseEntered(e -> {
                });
                matrixLabels.label.setOnMouseExited(e -> {
                });
                matrixLabels.label.setOnMouseClicked(e -> {
                });
                matrixLabels.label.setStyle(fxBorderNone);
            }

        }
        Platform.runLater(()->{ctlProgressPreview.setVisible(false);});

    }


    // обработчик события наведения мыши на лейбл
    private void onMouseEnterLabel(MatrixLabels matrixLabels) {
        if (updateFullPreviewDuringMoveMouse) {
            matrixLabels.label.setStyle(fxBorderYellow);
        } else {
            matrixLabels.label.setStyle(fxBorderRed);
        }

        matrixLabels.label.toFront();
        if (updateFullPreviewDuringMoveMouse) {
            String fileName = currentFile.getFolderFramesPreview() + "\\" + currentFile.getShortName() + currentFile.FRAMES_PREFIX + String.format("%06d", matrixLabels.frameNumber) + ".jpg";
            String fileNameFullSize = currentFile.getFolderFramesFull() + "\\" + currentFile.getShortName() + currentFile.FRAMES_PREFIX + String.format("%06d", matrixLabels.frameNumber) + ".jpg";
            onMouseClickLabel(fileName, fileNameFullSize, mainLabelFullSizePicture);
        }
    }

    // обреботчик события ухода мыши с лейбла
    private void onMouseExitLabel(MatrixLabels matrixLabels) {
        matrixLabels.label.setStyle(fxBorderDefault);
    }

    // обработчик события двойного клика на лейбле
    private void onMouseDoubleClickLabel(MatrixLabels matrixLabels) {

    }

    // обработчик события клика на лейбле
    private void onMouseClickLabel(String fileNamePreview, String fileNameFullSize, Label label) {

        File file = new File(fileNameFullSize);
        if (file.exists()) {
            try {
                BufferedImage bufferedImage = ImageIO.read(file);
                ImageView imageView = new ImageView(ConvertToFxImage.convertToFxImage(bufferedImage));
                label.setGraphic(imageView);
            } catch (IOException e) {
            }
            mainFramePreviewFile = fileNamePreview;
            mainFrameFullSizeFile = fileNameFullSize;
        }
    }

    public void editShots(IVFXFiles ivfxFile, int initFrameNum) {

        currentFile = ivfxFile;
        initFrameNumber = initFrameNum;

        try {

            AnchorPane root = FXMLLoader.load(shotsController.getClass().getResource("../resources/fxml/shots.fxml")); // в этот момент вызывается initialize()

            shotsController.controllerScene = new Scene(root);
            shotsController.controllerStage = new Stage();
            shotsController.controllerStage.setTitle("Редактор сцен. " + shotsController.currentFile.getTitle());
            shotsController.controllerStage.setScene(shotsController.controllerScene);
            shotsController.controllerStage.initModality(Modality.APPLICATION_MODAL);

            shotsController.controllerStage.setOnCloseRequest(we -> {
                shotsController.isWorking = false;
                if (mainMediaPlayer != null) {
                    mainMediaPlayer.pause();
                }
                System.out.println("Закрытые окна редактора сцен.");
            });


            shotsController.onStart();
            shotsController.controllerStage.showAndWait();


            System.out.println("Завершение работы editShots");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @FXML
    void initialize() {

        currentFile.checkIntegrity(); // Проверка целостности планов и сцен

        initPlayer();               // Инициализация плеера и слайдера
        initLabelsPreview();        // Инициализация лейблов превью
        initLabelsFull();           // Инициализация лейблов просмотра
        initShots();             // Инициализация планов
        initPersonsAll();           // Инициализация всех персонажей
        initShotPersons();       // Инициализация персонажей плана
        initShotPersonsStep1();  // Инициализация персонажей плана шаг -1
        initShotPersonsStep2();  // Инициализация персонажей плана шаг -2
        initShotPersonsStep3();  // Инициализация персонажей плана шаг -3
        initScenes();               // Инициализация сцен
        initScenePersons();         // Инициализация персонажей сцены
        initScenePersonsToEvents(); // Инициализация персонажей сцены для события
        initSceneShots();        // Инициализация планов сцены
        initEventsTypes();          // Инициализация типов событий
        initEvents();               // Инициализация событий
        initEventShots();        // Инициализация планов событий
        initEventPersons();         // Инициализация персонажей событий
        initHotkeys();              // Инициализация горячих клавиш
        initContextMenuFrame();     // Инициализация контенстного меню лейбла превью

    }

    // добавление выбранного персонажа к списку персонажей события
    private void addSelectedPersonToEventPersonList(IVFXPersons ivfxPerson) {
        IVFXPersons ivfxPersonsToAdd = ivfxPerson;
        // Проверяем, есть ли персонаж в списке персонажей события. Если нет - его надо добавить
        boolean needToAdd = true;
        for (IVFXEventsPersons eventPerson : listEventPersons) {
            if (eventPerson.getPersonId() == ivfxPersonsToAdd.getId()) {
                needToAdd = false;
                break;
            }
        }
        if (needToAdd) {

            IVFXEventsPersons.getNewDbInstance (currentEvent, ivfxPersonsToAdd);

            LoadListEventPersons loadListEventPersons = new LoadListEventPersons(currentEvent, true, ctlProgressEventPersons);
            loadListEventPersons.addEventHandler(WorkerStateEvent.WORKER_STATE_SUCCEEDED, t->{
                currentEvent.setEventPersons(loadListEventPersons.getValue());
                currentEvent.setName(currentEvent.getPersonsNames());
                currentEvent.save();
                listEventPersons = FXCollections.observableArrayList(currentEvent.getEventPersons());
                tblEventPersons.setItems(listEventPersons);
                tblEvents.refresh();
            });

            Thread thread = new Thread(loadListEventPersons);
            thread.start();
            if (!DO_IN_MULTITHREADS) {
                try {
                    thread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }

    }

    // удаление выбранного персонажа из списка персонажей события
    private void deleteSelectedPersonFromEventPersonList(IVFXPersons ivfxPerson) {
        IVFXPersons ivfxPersonToDel = ivfxPerson;

        for (IVFXEventsPersons eventPerson : listEventPersons) {
            if (eventPerson.getPersonId() == ivfxPersonToDel.getId()) {

                // Удаляем персонажа плана
                eventPerson.delete();

                LoadListEventPersons loadListEventPersons = new LoadListEventPersons(currentEvent, true, ctlProgressEventPersons);
                loadListEventPersons.addEventHandler(WorkerStateEvent.WORKER_STATE_SUCCEEDED, t->{
                    currentEvent.setEventPersons(loadListEventPersons.getValue());
                    currentEvent.setName(currentEvent.getPersonsNames());
                    currentEvent.save();
                    listEventPersons = FXCollections.observableArrayList(currentEvent.getEventPersons());
                    tblEventPersons.setItems(listEventPersons);
                    tblEvents.refresh();
                });

                Thread thread = new Thread(loadListEventPersons);
                thread.start();
                if (!DO_IN_MULTITHREADS) {
                    try {
                        thread.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                break;
            }
        }

    }

    // удаление выбранного персонажа из списка персонажей плана
    private void deleteSelectedPersonFromShotPersonList(IVFXPersons ivfxPerson) {
        IVFXPersons ivfxPersonToDel = ivfxPerson;

        for (IVFXShotsPersons shotPerson : listShotPersons) {
            if (shotPerson.getPersonId() == ivfxPersonToDel.getId()) {

                // Удаляем персонажа плана
                shotPerson.delete();
                // загружаем список персонажей текущего плана и привязываем его к таблице персонажей плана (и фильтра по ней)
                LoadListShotPersonsTask loadListShotPersonsTask = new LoadListShotPersonsTask(currentShot,true, ctlProgressShotPersons);
                loadListShotPersonsTask.addEventHandler(WorkerStateEvent.WORKER_STATE_SUCCEEDED, t->{
                    listShotPersons = FXCollections.observableArrayList(loadListShotPersonsTask.getValue());
                    tblShotPersons.setItems(listShotPersons);
                    filteredShotPersons = new FilteredList<>(listShotPersons, e -> true);
                    deleteSelectedPersonFromScenePersonList(ivfxPerson);
                });
                Thread thread = new Thread(loadListShotPersonsTask);
                thread.start();
                if (!DO_IN_MULTITHREADS) {
                    try {
                        thread.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                break;
            }
        }

    }

    // удаление выбранного персонажа из списка персонажей сцены
    private void deleteSelectedPersonFromScenePersonList(IVFXPersons ivfxPerson) {
        IVFXPersons ivfxPersonToDel = ivfxPerson;

        LoadListScenShotsTask loadListScenShotsTask = new LoadListScenShotsTask(currentScene, ctlProgressSceneShots);
        loadListScenShotsTask.addEventHandler(WorkerStateEvent.WORKER_STATE_SUCCEEDED, t->{
            List<IVFXScenesShots> listSceneShots = loadListScenShotsTask.getValue(); // список планов сцены
            for (IVFXScenesShots sceneShot : listSceneShots) {
                List<IVFXShotsPersons> listShotPersons = IVFXShotsPersons.loadList(sceneShot.getIvfxShot(), false); // список персонажей плана
                for (IVFXShotsPersons shotPerson : listShotPersons) {
                    if (shotPerson.getPersonId() == ivfxPerson.getId()) {
                        // если в списке хоть один раз найден указанный персонаж - его удалять из списка персонажей сцены не надо - просто выходим
                        return;
                    }
                }
            }

            for (IVFXScenesPersons scenePerson : listScenePersons) {
                if (scenePerson.getPersonId() == ivfxPersonToDel.getId()) {
                    scenePerson.delete();


                    LoadListScenePersonsTask loadListScenePersonsTask = new LoadListScenePersonsTask(currentScene,true, ctlProgressScenePersons);
                    loadListScenePersonsTask.addEventHandler(WorkerStateEvent.WORKER_STATE_SUCCEEDED, t1->{
                        currentScene.setScenePersons(loadListScenePersonsTask.getValue());
                        listScenePersons = FXCollections.observableArrayList(new ArrayList<>(currentScene.getScenePersons()));
                        tblScenePersons.setItems(listScenePersons);

                        currentScene.setScenePersons2(loadListScenePersonsTask.getValue());
                        listScenePersonsForEvents = FXCollections.observableArrayList(new ArrayList<>(currentScene.getScenePersons2()));
                        tblScenePersonsToEvents.setItems(listScenePersonsForEvents);


                    });
                    Thread thread = new Thread(loadListScenePersonsTask);
                    thread.start();
                    if (!DO_IN_MULTITHREADS) {
                        try {
                            thread.join();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

//                    LoadListScenePersonsTask loadListScenePersonsTask2 = new LoadListScenePersonsTask(currentScene,true, ctlProgressScenePersonsToEvents);
//                    loadListScenePersonsTask.addEventHandler(WorkerStateEvent.WORKER_STATE_SUCCEEDED, t1->{
//                        currentScene.setScenePersons2(loadListScenePersonsTask2.getValue());
//                        listScenePersonsForEvents = FXCollections.observableArrayList(new ArrayList<>(currentScene.getScenePersons2()));
//                        tblScenePersonsToEvents.setItems(listScenePersonsForEvents);
//                    });
//                    new Thread(loadListScenePersonsTask2).start();

                    return;
                }
            }
        });
        Thread thread = new Thread(loadListScenShotsTask);
        thread.start();
        if (!DO_IN_MULTITHREADS) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    // добавление выбранного персонажа к списку персонажей плана
    private void addSelectedPersonToShotPersonList(IVFXPersons ivfxPerson) {
        IVFXPersons ivfxPersonsToAdd = ivfxPerson;
        // Проверяем, есть ли персонаж в списке персонажей плана. Если нет - его надо добавить
        boolean needToAdd = true;
        for (IVFXShotsPersons shotPerson : listShotPersons) {
            if (shotPerson.getPersonId() == ivfxPersonsToAdd.getId()) {
                needToAdd = false;
                break;
            }
        }
        if (needToAdd) {

            IVFXShotsPersons.getNewDbInstance (currentShot, ivfxPersonsToAdd);

            // загружаем список персонажей текущего плана и привязываем его к таблице персонажей плана (и фильтра по ней)
            LoadListShotPersonsTask loadListShotPersonsTask = new LoadListShotPersonsTask(currentShot,true, ctlProgressShotPersons);
            loadListShotPersonsTask.addEventHandler(WorkerStateEvent.WORKER_STATE_SUCCEEDED, t->{
                listShotPersons = FXCollections.observableArrayList(loadListShotPersonsTask.getValue());
                tblShotPersons.setItems(listShotPersons);
                filteredShotPersons = new FilteredList<>(listShotPersons, e -> true);
                addSelectedPersonToScenePersonList(ivfxPerson);
            });
            Thread thread = new Thread(loadListShotPersonsTask);
            thread.start();
            if (!DO_IN_MULTITHREADS) {
                try {
                    thread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }

    }

    // добавление выбранного персонажа к списку персонажей сцены
    private void addSelectedPersonToScenePersonList(IVFXPersons ivfxPerson) {
        IVFXPersons ivfxPersonsToAdd = ivfxPerson;
        boolean needToAdd = true;
        for (IVFXScenesPersons scenePerson : listScenePersons) {
            if (scenePerson.getPersonId() == ivfxPersonsToAdd.getId()) {
                needToAdd = false;
                break;
            }
        }
        if (needToAdd) {

            IVFXScenesPersons.getNewDbInstance(currentScene, ivfxPersonsToAdd);

            LoadListScenePersonsTask loadListScenePersonsTask = new LoadListScenePersonsTask(currentScene,true, ctlProgressScenePersons);
            loadListScenePersonsTask.addEventHandler(WorkerStateEvent.WORKER_STATE_SUCCEEDED, t->{
                currentScene.setScenePersons(loadListScenePersonsTask.getValue());
                listScenePersons = FXCollections.observableArrayList(new ArrayList<>(currentScene.getScenePersons()));
                tblScenePersons.setItems(listScenePersons);

                currentScene.setScenePersons2(loadListScenePersonsTask.getValue());
                listScenePersonsForEvents = FXCollections.observableArrayList(new ArrayList<>(currentScene.getScenePersons2()));
                tblScenePersonsToEvents.setItems(listScenePersonsForEvents);

            });
            Thread thread = new Thread(loadListScenePersonsTask);
            thread.start();
            if (!DO_IN_MULTITHREADS) {
                try {
                    thread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

//            LoadListScenePersonsTask loadListScenePersonsTask2 = new LoadListScenePersonsTask(currentScene,true, ctlProgressScenePersonsToEvents);
//            loadListScenePersonsTask.addEventHandler(WorkerStateEvent.WORKER_STATE_SUCCEEDED, t->{
//                currentScene.setScenePersons2(loadListScenePersonsTask2.getValue());
//                listScenePersonsForEvents = FXCollections.observableArrayList(new ArrayList<>(currentScene.getScenePersons2()));
//                tblScenePersonsToEvents.setItems(listScenePersonsForEvents);
//            });
//            new Thread(loadListScenePersonsTask2).start();

        }

    }

    private void goToPreviousShot() {
        if (currentShot != null) {
            int currIndex = listShots.indexOf(currentShot);
            int prevIndex = currIndex - 1;
            if (prevIndex >= 0) {
                currentShot = listShots.get(prevIndex);
                tblShots.getSelectionModel().select(currentShot);
                tblShotsSmartScrollToCurrent();
            }
        }
    }

    private void goToNextShot() {
        if (currentShot != null) {
            int currIndex = listShots.indexOf(currentShot);
            int nextIndex = currIndex + 1;
            if (nextIndex < listShots.size()) {
                currentShot = listShots.get(nextIndex);
                tblShots.getSelectionModel().select(currentShot);
                tblShotsSmartScrollToCurrent();
            }
        }
    }

    private void goToShot(IVFXShots ivfxShot) {
        if (ivfxShot != null) {
            for (IVFXShots shot: listShots) {
                if (shot.getId() == ivfxShot.getId()) {
                    currentShot = shot;
                    tblShots.getSelectionModel().select(currentShot);
                    tblShotsSmartScrollToCurrent();
                }
            }
        }
    }

    private void tblShotsSmartScrollToCurrent() {
        if (flowTblShots != null && flowTblShots.getCellCount() > 0) {
            int first = flowTblShots.getFirstVisibleCell().getIndex();
            int last = flowTblShots.getLastVisibleCell().getIndex();
            int selected = tblShots.getSelectionModel().getSelectedIndex();
            if (selected < first || selected > last) {
                tblShots.scrollTo(currentShot);
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

    private void tblEventsSmartScrollToCurrent() {
        if (flowTblEvents != null && flowTblEvents.getCellCount() > 0) {
            int first = flowTblEvents.getFirstVisibleCell().getIndex();
            int last = flowTblEvents.getLastVisibleCell().getIndex();
            int selected = tblEvents.getSelectionModel().getSelectedIndex();
            if (selected < first || selected > last) {
                tblEvents.scrollTo(currentEvent);
            }
        }
    }

    private void tblSceneShotsSmartScrollToCurrent() {
        if (flowTblSceneShots != null && flowTblSceneShots.getCellCount() > 0) {
            int first = flowTblSceneShots.getFirstVisibleCell().getIndex();
            int last = flowTblSceneShots.getLastVisibleCell().getIndex();
            int selected = tblSceneShots.getSelectionModel().getSelectedIndex();
            if (selected < first || selected > last) {
                tblSceneShots.scrollTo(currentShotForScene);
            }
        }
    }

    private void tblEventShotsSmartScrollToCurrent() {
        if (flowTblEventShots != null && flowTblEventShots.getCellCount() > 0) {
            int first = flowTblEventShots.getFirstVisibleCell().getIndex();
            int last = flowTblEventShots.getLastVisibleCell().getIndex();
            int selected = tblEventShots.getSelectionModel().getSelectedIndex();
            if (selected < first || selected > last) {
                tblEventShots.scrollTo(currentShotForEvent);
            }
        }
    }

    public void onStart() {

    }

    @FXML
    void doAddNewEvent(ActionEvent event) {

        if (!currentShot.isPresentInEvents()) {
            IVFXEvents newEvent = IVFXEvents.createEvent(currentEventType, currentFile, currentShot, currentShot, false);

            listEvents = FXCollections.observableArrayList(IVFXEvents.loadList(currentFile));
            tblEvents.setItems(listEvents);
            for (IVFXEvents ivfxEvent: listEvents) {
                if (newEvent.getId() == ivfxEvent.getId()) {
                    currentEvent = ivfxEvent;
                    break;
                }
            }

            for (IVFXEvents eventTmp : listEvents) {
                List<IVFXEventsShots> eventShots = IVFXEventsShots.loadList(eventTmp);
                List<IVFXShots> list = new ArrayList<>();
                for (IVFXEventsShots eventShot: eventShots) {
                    for (IVFXShots shot: listShotsOriginal) {
                        if (shot.isEqual(eventShot.getIvfxShot())) {
                            list.add(shot);
                            break;
                        }
                    }
                }
                eventTmp.setShots(list);
                eventTmp.setEventPersons(IVFXEventsPersons.loadList(eventTmp,true));
            }

            tblEvents.getSelectionModel().select(currentEvent);
            tblEventsSmartScrollToCurrent();
        } else {
            System.out.println("План уже принадлежит какому-то событию и на его основе нельзя создать новое событие.");
        }

    }

    @FXML
    void doSetStartEvent(ActionEvent event) {

        IVFXShots lastShotInEvent = currentEvent.getShots().get(currentEvent.getShots().size()-1);
        boolean canSetStart = false;
        if (currentShot.getId() == lastShotInEvent.getId()) {
            canSetStart = true;
        } else {
            for (IVFXShots shot: listShots) {
                if (shot.getId() == lastShotInEvent.getId()) {
                    break;
                }
                if (shot.getId() == currentShot.getId()) {
                    canSetStart = true;
                    break;
                }

            }
        }

        if (canSetStart) {

            currentEvent.setStart(currentShot, false);

            listEvents = FXCollections.observableArrayList(IVFXEvents.loadList(currentFile));
            tblEvents.setItems(listEvents);
            for (IVFXEvents ivfxEvent: listEvents) {
                if (currentEvent.getId() == ivfxEvent.getId()) {
                    currentEvent = ivfxEvent;
                    break;
                }
            }

            for (IVFXEvents eventTmp : listEvents) {
                List<IVFXEventsShots> eventShots = IVFXEventsShots.loadList(eventTmp);
                List<IVFXShots> list = new ArrayList<>();
                for (IVFXEventsShots eventShot: eventShots) {
                    for (IVFXShots shot: listShotsOriginal) {
                        if (shot.isEqual(eventShot.getIvfxShot())) {
                            list.add(shot);
                            break;
                        }
                    }
                }
                eventTmp.setShots(list);
                eventTmp.setEventPersons(IVFXEventsPersons.loadList(eventTmp,true));
            }

            tblEvents.getSelectionModel().select(currentEvent);
            tblEventsSmartScrollToCurrent();

        } else {
            System.out.println("Нельзя установеить этот план как первый план события, так как он находится позже последнего плана события.");
        }

    }

    @FXML
    void doSetEndEvent(ActionEvent event) {

        IVFXShots firstShotInEvent = currentEvent.getShots().get(0);
        boolean canSetEnd = false;
        if (currentShot.getId() == firstShotInEvent.getId()) {
            canSetEnd = true;
        } else {
            for (IVFXShots shot: listShots) {
                if (shot.getId() == firstShotInEvent.getId()) {
                    canSetEnd = true;
                    break;
                }
                if (shot.getId() == currentShot.getId()) {
                    break;
                }
            }
        }

        if (canSetEnd) {

            currentEvent.setEnd(currentShot, false);

            listEvents = FXCollections.observableArrayList(IVFXEvents.loadList(currentFile));
            tblEvents.setItems(listEvents);
            for (IVFXEvents ivfxEvent: listEvents) {
                if (currentEvent.getId() == ivfxEvent.getId()) {
                    currentEvent = ivfxEvent;
                    break;
                }
            }

            for (IVFXEvents eventTmp : listEvents) {
                List<IVFXEventsShots> eventShots = IVFXEventsShots.loadList(eventTmp);
                List<IVFXShots> list = new ArrayList<>();
                for (IVFXEventsShots eventShot: eventShots) {
                    for (IVFXShots shot: listShotsOriginal) {
                        if (shot.isEqual(eventShot.getIvfxShot())) {
                            list.add(shot);
                            break;
                        }
                    }
                }
                eventTmp.setShots(list);
                eventTmp.setEventPersons(IVFXEventsPersons.loadList(eventTmp,true));
            }

            IVFXShots temp = currentShot;

            tblEvents.getSelectionModel().select(currentEvent);
            tblEventsSmartScrollToCurrent();

            goToShot(temp);

        } else {
            System.out.println("Нельзя установеить этот план как последний план события, так как он находится раньше первого плана события.");
        }

    }

    @FXML
    void doDeleteEvent(ActionEvent event) {

        IVFXEvents prevEventTemp = null;
        IVFXEvents prevEvent = null;
        IVFXEvents nextEvent = null;
        boolean isFindCurrentEvent = false;
        for (IVFXEvents eventTemp: listEvents) {
            if (isFindCurrentEvent) {
                nextEvent = eventTemp;
                break;
            }
            if (eventTemp.getId() == currentEvent.getId()) {
                isFindCurrentEvent = true;
                prevEvent = prevEventTemp;
            }
            prevEventTemp = eventTemp;
        }

        currentEvent.delete();

        if (nextEvent != null) {
            currentEvent = nextEvent;
        } else if (prevEvent != null) {
            currentEvent = prevEvent;
        } else {
            currentEvent = null;
        }

        listEvents = FXCollections.observableArrayList(IVFXEvents.loadList(currentFile));
        tblEvents.setItems(listEvents);

        if (currentEvent != null) {
            for (IVFXEvents ivfxEvent: listEvents) {
                if (currentEvent.getId() == ivfxEvent.getId()) {
                    currentEvent = ivfxEvent;
                    break;
                }
            }
        }

        for (IVFXEvents eventTmp : listEvents) {
            List<IVFXEventsShots> eventShots = IVFXEventsShots.loadList(eventTmp);
            List<IVFXShots> list = new ArrayList<>();
            for (IVFXEventsShots eventShot: eventShots) {
                for (IVFXShots shot: listShotsOriginal) {
                    if (shot.isEqual(eventShot.getIvfxShot())) {
                        list.add(shot);
                        break;
                    }
                }
            }
            eventTmp.setShots(list);
            eventTmp.setEventPersons(IVFXEventsPersons.loadList(eventTmp,true));
        }

        if (currentEvent != null) {
            tblEvents.getSelectionModel().select(currentEvent);
            tblEventsSmartScrollToCurrent();
        }

    }




    @FXML
    void doAddNewScene(ActionEvent event) {
        IVFXScenes scene = IVFXScenes.getNewDbInstance(currentShot);
        if (scene != null) {
            listScenes = FXCollections.observableArrayList(IVFXScenes.loadList(currentFile));
            tblScenes.setItems(listScenes);
            for (IVFXScenes tmpScene: listScenes) {
                if (tmpScene.getId() == scene.getId()) {
                    scene = tmpScene;
                    break;
                }
            }

            currentScene = scene;

            for (IVFXScenes ivfxScene: listScenes) {
                List<IVFXScenesShots> sceneShots = IVFXScenesShots.loadList(ivfxScene);
                List<IVFXShots> list = new ArrayList<>();
                for (IVFXScenesShots sceneShot: sceneShots) {
                    for (IVFXShots shot: listShotsOriginal) {
                        if (shot.isEqual(sceneShot.getIvfxShot())) {
                            list.add(shot);
                            break;
                        }
                    }
                }
                ivfxScene.setShots(list);
                ivfxScene.setScenePersons(IVFXScenesPersons.loadList(ivfxScene,true));
                ivfxScene.setScenePersons2(IVFXScenesPersons.loadList(ivfxScene,true));

            }


            listShotsForScene = FXCollections.observableArrayList(currentScene.getShots()); // загражаем список планов для нее
            tblSceneShots.setItems(listShotsForScene);    // привязываем таблицу
            tblScenes.getSelectionModel().select(currentScene); // переходим на нее в таблице сцен
            tblScenesSmartScrollToCurrent();

        }
    }



    @FXML
    void doDeleteScene(ActionEvent event) {
        IVFXScenes sceneToMoveFocusAfterDelete = null;
        if (listScenes.size() > 1) {
            if (currentScene.getId() == listScenes.get(0).getId()) {
                sceneToMoveFocusAfterDelete = listScenes.get(1);
            } else {
                for (int i = 0; i < listScenes.size(); i++) {
                    if (currentScene.getId() == listScenes.get(i).getId()) {
                        sceneToMoveFocusAfterDelete = listScenes.get(i-1);
                        break;
                    }
                }
            }
        }
        currentScene.delete();
        listScenes = FXCollections.observableArrayList(IVFXScenes.loadList(currentFile));
        tblScenes.setItems(listScenes);

        for (IVFXScenes ivfxScene: listScenes) {
            if (sceneToMoveFocusAfterDelete.getId() == ivfxScene.getId()) {
                currentScene = ivfxScene;
            }
            List<IVFXScenesShots> sceneShots = IVFXScenesShots.loadList(ivfxScene);
            List<IVFXShots> list = new ArrayList<>();
            for (IVFXScenesShots sceneShot: sceneShots) {
                for (IVFXShots shot: listShotsOriginal) {
                    if (shot.isEqual(sceneShot.getIvfxShot())) {
                        list.add(shot);
                        break;
                    }
                }
            }
            ivfxScene.setShots(list);
            ivfxScene.setScenePersons(IVFXScenesPersons.loadList(ivfxScene,true));
            ivfxScene.setScenePersons2(IVFXScenesPersons.loadList(ivfxScene,true));

        }

        if (currentScene != null) {
            listShotsForScene = FXCollections.observableArrayList(currentScene.getShots()); // загражаем список планов для нее
            tblSceneShots.setItems(listShotsForScene);    // привязываем таблицу
            tblScenes.getSelectionModel().select(currentScene); // переходим на нее в таблице сцен
            tblScenesSmartScrollToCurrent();
        } else {
            listShotsForScene.clear();
            tblSceneShots.setItems(listShotsForScene);
        }

    }

    @FXML
    void doScenePersonsToShot(ActionEvent event) {
        // Добавление всех пресонажей сцены к текущему плану
        int iProgress = 0;
        for (IVFXScenesPersons ivfxScenePerson: listScenePersons) {
            addSelectedPersonToShotPersonList(ivfxScenePerson.getIvfxPerson());
        }
        goToNextShot();
    }

    @FXML
    void doShotPersonsStep1(ActionEvent event) {
        // Добавление всех пресонажей плана с шагом -1 к текущему плану
        int iProgress = 0;
        for (IVFXShotsPersons ivfxShotPerson: listShotPersonsStep1) {
            addSelectedPersonToShotPersonList(ivfxShotPerson.getIvfxPerson());
        }
        goToNextShot();
    }

    @FXML
    void doShotPersonsStep2(ActionEvent event) {
        // Добавление всех пресонажей плана с шагом -2 к текущему плану
        int iProgress = 0;
        for (IVFXShotsPersons ivfxShotPerson: listShotPersonsStep2) {
            addSelectedPersonToShotPersonList(ivfxShotPerson.getIvfxPerson());
        }
        goToNextShot();
    }

    @FXML
    void doShotPersonsStep3(ActionEvent event) {
        // Добавление всех пресонажей плана с шагом -3 к текущему плану
        int iProgress = 0;
        for (IVFXShotsPersons ivfxShotPerson: listShotPersonsStep3) {
            addSelectedPersonToShotPersonList(ivfxShotPerson.getIvfxPerson());
        }
        goToNextShot();
    }

    @FXML
    void doTgPlayerLoopNextStop(ActionEvent event) {

        if (tbPlayerLoop.isSelected()) playerLoopNextStop = PlayerLoopNextStop.LOOP;
        if (tbPlayerNext.isSelected()) playerLoopNextStop = PlayerLoopNextStop.NEXT;
        if (tbPlayerStop.isSelected()) playerLoopNextStop = PlayerLoopNextStop.STOP;

        mainMediaPlayer.setCycleCount(playerLoopNextStop.equals(PlayerLoopNextStop.LOOP) ? MediaPlayer.INDEFINITE : 1);

    }

    @FXML
    void doTgPlayerSpeed(ActionEvent event) {

        if (tbPlayerSpeed1.isSelected()) playerSpeed = 1.0;
        if (tbPlayerSpeed2.isSelected()) playerSpeed = 1.5;
        if (tbPlayerSpeed3.isSelected()) playerSpeed = 2.0;
        if (tbPlayerSpeed4.isSelected()) playerSpeed = 2.5;
        if (tbPlayerSpeed5.isSelected()) playerSpeed = 3.0;

        mainMediaPlayer.setRate(playerSpeed);

    }


    private IVFXShots getPreviousShot(IVFXShots shot) {

        int iProgress = 0;
        for (IVFXShots seg: listShots) {
            if (seg.getLastFrameNumber() == shot.getFirstFrameNumber()-1) {
                return seg;
            }
        }

        return null;
    }


    private void initContextMenuFrame() {

//        Platform.runLater(() -> {
            List<IVFXGroups> listGroups = IVFXGroups.loadList(currentFile.getIvfxProject());

            IVFXGroups groupAllPersons = IVFXGroups.loadByName("Все персонажи проекта",currentFile.getIvfxProject());

            Menu menu = new Menu("(без группы)");
            MenuItem menuItem = new MenuItem("(Новый персонаж)");
//            menuItem.setOnAction(e -> {addNewPerson(null);});
            menuItem.setOnAction(e -> {addNewPerson(groupAllPersons);});
            menu.getItems().add(menuItem);

            List<IVFXPersons> listPersons = IVFXPersons.loadListPersonsWithoutGroups(currentFile.getIvfxProject(),false);
            for (IVFXPersons person : listPersons) {
                menuItem = new MenuItem(person.getName());
                menuItem.setOnAction(e -> {setCurrentFrameAsPictureToPerson(person);});
                menu.getItems().add(menuItem);
            }

            contxtMenuFrame.getItems().clear();
            contxtMenuFrame.getItems().add(menu);
            contxtMenuFrame.getItems().add(new SeparatorMenuItem());
            for (IVFXGroups group : listGroups) {
                menu = new Menu(group.getName());
                contxtMenuFrame.getItems().add(menu);
                menuItem = new MenuItem("(Новый персонаж)");
                if (group.getId() == groupAllPersons.getId()) {
                    menuItem.setOnAction(e -> {addNewPerson(group);});
                } else {
                    menuItem.setOnAction(e -> {addNewPerson(groupAllPersons, group);});
                }
                menu.getItems().add(menuItem);
                menu.getItems().add(new SeparatorMenuItem());
                List<IVFXGroupsPersons> listGroupPerson = IVFXGroupsPersons.loadList(group,false);
                for (IVFXGroupsPersons groupPerson : listGroupPerson) {
                    menuItem = new MenuItem(groupPerson.getIvfxPerson().getName());
                    menuItem.setOnAction(e -> {setCurrentFrameAsPictureToPerson(groupPerson.getIvfxPerson());});
                    menu.getItems().add(menuItem);
                }
            }

            contxtMenuFrame.getItems().add(new SeparatorMenuItem());
            menuItem = new MenuItem("(Новая группа)");
            menuItem.setOnAction(e -> {addNewGroup();});
            contxtMenuFrame.getItems().add(menuItem);
//        });

    }

    private void addNewPerson(IVFXGroups... groups) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Новый персонаж");
        dialog.setHeaderText("Создание нового персонажа");
        dialog.setContentText("Введите имя нового персонажа");
        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            String personName = result.get();
            if (!personName.equals("")) {
                IVFXPersons person = IVFXPersons.loadByName(personName, currentFile.getIvfxProject(), false);
                if (person == null) {

                    person = IVFXPersons.getNewDbInstance(currentFile.getIvfxProject());
                    person.setName(personName);
                    person.setIvfxProject(currentFile.getIvfxProject());
                    person.save();

                    if (groups != null) {
                        for (IVFXGroups group: groups) {
                            IVFXGroupsPersons groupPerson = IVFXGroupsPersons.getNewDbInstance(group,person, false);
                        }
                    }

                    try {
                        IVFXFrames.setPictureToPerson(mainFramePreviewFile, mainFrameFullSizeFile, person);
                    } catch (IOException exception) {
                    }

                    listPersonsAll = FXCollections.observableArrayList(IVFXPersons.loadList(currentFile.getIvfxProject(),true));
                    tblPersonsAll.setItems(listPersonsAll);
                    filteredPersonAll = new FilteredList<>(listPersonsAll, e -> true);

                }
                initContextMenuFrame();
            }
        }
    }

    private void addNewGroup() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Новая группа");
        dialog.setHeaderText("Создание новой группы");
        dialog.setContentText("Введите имя новой группы персонажей");
        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            String groupName = result.get();
            if (!groupName.equals("")) {
                IVFXGroups group = IVFXGroups.loadByName(groupName, currentFile.getIvfxProject());
                if (group == null) {
                    group = IVFXGroups.getNewDbInstance(currentFile.getIvfxProject());
                    group.setName(groupName);
                    group.save();

                }
            }
        }
        initContextMenuFrame();
    }

    private void setCurrentFrameAsPictureToPerson(IVFXPersons person) {
        try {
            IVFXFrames.setPictureToPerson(mainFramePreviewFile, mainFrameFullSizeFile, person);
        } catch (IOException exception) {
        }
    }

    private void initPlayer() {


        try {
            mainMedia = new Media(new File(currentFile.getFileSourceNamePreview()).toURI().toURL().toExternalForm());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        tbPlayerLoop.setSelected(playerLoopNextStop.equals(PlayerLoopNextStop.LOOP));
        tbPlayerNext.setSelected(playerLoopNextStop.equals(PlayerLoopNextStop.NEXT));
        tbPlayerStop.setSelected(playerLoopNextStop.equals(PlayerLoopNextStop.STOP));

        tbPlayerSpeed1.setSelected(playerSpeed == 1.0);
        tbPlayerSpeed2.setSelected(playerSpeed == 1.5);
        tbPlayerSpeed3.setSelected(playerSpeed == 2.0);
        tbPlayerSpeed4.setSelected(playerSpeed == 2.5);
        tbPlayerSpeed5.setSelected(playerSpeed == 3.0);

        mainMediaPlayer = new MediaPlayer(mainMedia);
        mainMediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
        mainMediaPlayer.setRate(playerSpeed);
        mainMediaPlayer.setVolume(0.5);
        mainMediaPlayer.setMute(false);
        mainMediaView.setMediaPlayer(mainMediaPlayer);
        mainLabelPlayer.setText(currentFile.getTitle() + ". Длительность: [" + FFmpeg.convertDurationToString(currentFile.getDuration()) + "]");
        mainMediaPlayer.setOnError(() -> System.out.println("Player Error : " + mainMediaPlayer.getError().toString()));

        mainMediaPlayer.play();

        mainSlider.setMin(0.0);
        mainSlider.setMax(currentFile.getDuration());
        mainSlider.setValue(0.0);

        // событие изменения позиции проигрывания в плеере
        mainMediaPlayer.currentTimeProperty().addListener((observable, oldValue, current) -> mainSlider.setValue(current.toMillis()));

        // событие клика на слайдере
        mainSlider.setOnMouseClicked(event -> {
            if (mainMediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
                mainMediaPlayer.pause();
                mainMediaPlayer.seek(Duration.millis(mainSlider.getValue()));
                mainMediaPlayer.play();
            } else if (mainMediaPlayer.getStatus() == MediaPlayer.Status.PAUSED) {
                mainMediaPlayer.seek(Duration.millis(mainSlider.getValue()));
            }
        });

        // событие клика на плеере (pause-play)
        mainMediaView.setOnMouseClicked(event -> {
            if (mainMediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
                if (!playerIsEndOfMedia) {
                    mainMediaPlayer.pause();
                } else {
                    playerIsEndOfMedia = false;
                    if (!playerLoopNextStop.equals(PlayerLoopNextStop.LOOP)) {
                        mainMediaPlayer.seek(mainMediaPlayer.getStartTime());
                    } else {
                        mainMediaPlayer.pause();
                    }

                }

            } else if (mainMediaPlayer.getStatus() == MediaPlayer.Status.PAUSED) {
                playerIsEndOfMedia = false;
                mainMediaPlayer.play();
            }
        });

        mainMediaPlayer.setOnEndOfMedia(() -> {
            playerIsEndOfMedia = true;
            if (playerLoopNextStop.equals(PlayerLoopNextStop.NEXT)) {
                goToNextShot();
            }
        });

    }

    private void initLabelsPreview() {

        gridpanePreviwe.getChildren().clear();
        listLabels.clear();

        for (int iRow = 0; iRow < ROWS_COUNT; iRow++) {
            for (int iCol = 0; iCol < COLUMNS_COUNT; iCol++) {
                Label label = new Label();
                label.setPrefSize(PICTURE_WIDTH, PICTURE_HEIGHT);
                label.setStyle("-fx-border-color:NONE;-fx-border-width:1");

                MatrixLabels matrixLabels = new MatrixLabels();
                matrixLabels.label = label;
                matrixLabels.column = iCol;
                matrixLabels.row = iRow;

                listLabels.add(matrixLabels);
                listLabelsPreview.add(label);

                gridpanePreviwe.add(label, iCol, iRow);
            }
        }

    }

    private void initLabelsFull() {

        mainLabelFullSizePicture.setOnScroll(e->{

            IVFXFiles ivfxFiles = currentFile;
            IVFXShots ivfxShots = currentShot;

            int firstFrameNumber = ivfxShots.getFirstFrameNumber(); // Номер первого кардра
            int lastFrameNumber = ivfxShots.getLastFrameNumber();   // Номер последнего кадра
            int countLabels = COLUMNS_COUNT * ROWS_COUNT;             // Количество лейблов в пэйне
            int countFrames = lastFrameNumber - firstFrameNumber;      // Количество кадров в плане


            int delta = e.getDeltaY() > 0 ? -1 : 1;
            currentFrameToScroll += delta;
            if (currentFrameToScroll < firstFrameNumber) currentFrameToScroll = lastFrameNumber;
            if (currentFrameToScroll > lastFrameNumber) currentFrameToScroll = firstFrameNumber;


            MatrixLabels matrixLabels = null;
            for (MatrixLabels temp: listLabels) {
                if (temp.frameNumber >= currentFrameToScroll) {
                    matrixLabels = temp;
                    break;
                }
            }

            if (matrixLabels != null) {
                if (prevMatrixLabel != null) prevMatrixLabel.label.setStyle(fxBorderDefault);
                matrixLabels.label.setStyle(fxBorderMagenta);
                matrixLabels.label.toFront();
                prevMatrixLabel = matrixLabels;
            }

            String fileName = currentFile.getFolderFramesPreview() + "\\" + currentFile.getShortName() + currentFile.FRAMES_PREFIX + String.format("%06d", currentFrameToScroll) + ".jpg";
            String fileNameFullSize = currentFile.getFolderFramesFull() + "\\" + currentFile.getShortName() + currentFile.FRAMES_PREFIX + String.format("%06d", currentFrameToScroll) + ".jpg";
            onMouseClickLabel(fileName, fileNameFullSize, mainLabelFullSizePicture);

        });

    }

    /********************************************************************
     * ИНИЦИАЛИЗАЦИЯ
     ********************************************************************/

    private void initShots() {

        LoadListShotsTask loadListShotsTask = new LoadListShotsTask(currentFile, true,ctlProgressShots);
        loadListShotsTask.addEventHandler(WorkerStateEvent.WORKER_STATE_SUCCEEDED, //
                t -> {
                    listShotsOriginal = loadListShotsTask.getValue();
                    listShots = FXCollections.observableArrayList(listShotsOriginal);

                    // таблица планов
                    colShotFrom.setCellValueFactory(new PropertyValueFactory<>("labelFirst1"));
                    colShotTo.setCellValueFactory(new PropertyValueFactory<>("labelLast1"));
                    tblShots.setItems(listShots);

                    for (IVFXScenes scene : listScenes) {
                        List<IVFXScenesShots> sceneShots = IVFXScenesShots.loadList(scene);
                        List<IVFXShots> list = new ArrayList<>();
                        for (IVFXScenesShots sceneShot: sceneShots) {
                            for (IVFXShots shot: listShotsOriginal) {
                                if (shot.isEqual(sceneShot.getIvfxShot())) {
                                    list.add(shot);
                                    break;
                                }
                            }
                        }
                        scene.setShots(list);
                        scene.setScenePersons(IVFXScenesPersons.loadList(scene,true));
                        scene.setScenePersons2(IVFXScenesPersons.loadList(scene,true));
                    }

                    for (IVFXEvents event : listEvents) {
                        List<IVFXEventsShots> eventShots = IVFXEventsShots.loadList(event);
                        List<IVFXShots> list = new ArrayList<>();
                        for (IVFXEventsShots eventShot: eventShots) {
                            for (IVFXShots shot: listShotsOriginal) {
                                if (shot.isEqual(eventShot.getIvfxShot())) {
                                    list.add(shot);
                                    break;
                                }
                            }
                        }
                        event.setShots(list);
                        event.setEventPersons(IVFXEventsPersons.loadList(event,true));
                    }

                });
        Thread thread = new Thread(loadListShotsTask);
        thread.start();
        if (!DO_IN_MULTITHREADS) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // событие выбора записи в таблице планов
        tblShots.getSelectionModel().selectedItemProperty().addListener((v, oldValue, newValue) -> {
            if (newValue != null) {
                currentShot = newValue;  // устанавливаем выбранное значение в таблице как текущий план

                // загружаем список персонажей текущего плана и привязываем его к таблице персонажей плана (и фильтра по ней)
                LoadListShotPersonsTask loadListShotPersonsTask = new LoadListShotPersonsTask(currentShot,true, ctlProgressShotPersons);
                loadListShotPersonsTask.addEventHandler(WorkerStateEvent.WORKER_STATE_SUCCEEDED, //
                        t -> {
                            listShotPersons = FXCollections.observableArrayList(loadListShotPersonsTask.getValue());
                            tblShotPersons.setItems(listShotPersons);
                            filteredShotPersons = new FilteredList<>(listShotPersons, e -> true);
                        });

                Thread thread1 = new Thread(loadListShotPersonsTask);
                thread1.start();
                if (!DO_IN_MULTITHREADS) {
                    try {
                        thread1.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                // загружаем списки персонажей планов на 3 шага назад от текущего и привязываем их к таблицам
                IVFXShots prevSeg1 = getPreviousShot(currentShot);
                if (prevSeg1 != null) {

                    LoadListShotPersonsTask loadListShotPersonsTaskStep1 = new LoadListShotPersonsTask(prevSeg1,true, ctlProgressShotPersonsStep1);
                    loadListShotPersonsTaskStep1.addEventHandler(WorkerStateEvent.WORKER_STATE_SUCCEEDED, //
                            t -> {
                                listShotPersonsStep1 = FXCollections.observableArrayList(loadListShotPersonsTaskStep1.getValue());
                                tblShotPersonsStep1.setItems(listShotPersonsStep1);
                            });
                    Thread thread2 = new Thread(loadListShotPersonsTaskStep1);
                    thread2.start();
                    if (!DO_IN_MULTITHREADS) {
                        try {
                            thread2.join();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    IVFXShots prevSeg2 = getPreviousShot(prevSeg1);
                    if (prevSeg2 != null) {
                        LoadListShotPersonsTask loadListShotPersonsTaskStep2 = new LoadListShotPersonsTask(prevSeg2,true, ctlProgressShotPersonsStep2);
                        loadListShotPersonsTaskStep2.addEventHandler(WorkerStateEvent.WORKER_STATE_SUCCEEDED, //
                                t -> {
                                    listShotPersonsStep2 = FXCollections.observableArrayList(loadListShotPersonsTaskStep2.getValue());
                                    tblShotPersonsStep2.setItems(listShotPersonsStep2);
                                });
                        Thread thread3 = new Thread(loadListShotPersonsTaskStep2);
                        thread3.start();
                        if (!DO_IN_MULTITHREADS) {
                            try {
                                thread3.join();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }

                        IVFXShots prevSeg3 = getPreviousShot(prevSeg2);
                        if (prevSeg3 != null) {

                            LoadListShotPersonsTask loadListShotPersonsTaskStep3 = new LoadListShotPersonsTask(prevSeg3,true, ctlProgressShotPersonsStep3);
                            loadListShotPersonsTaskStep3.addEventHandler(WorkerStateEvent.WORKER_STATE_SUCCEEDED, //
                                    t -> {
                                        listShotPersonsStep3 = FXCollections.observableArrayList(loadListShotPersonsTaskStep3.getValue());
                                        tblShotPersonsStep3.setItems(listShotPersonsStep3);
                                    });
                            Thread thread4 = new Thread(loadListShotPersonsTaskStep3);
                            thread4.start();
                            if (!DO_IN_MULTITHREADS) {
                                try {
                                    thread4.join();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }

                        } else {
                            listShotPersonsStep3.clear();
                            tblShotPersonsStep3.setItems(listShotPersonsStep3);
                        }
                    } else {
                        listShotPersonsStep2.clear();
                        tblShotPersonsStep2.setItems(listShotPersonsStep2);
                    }
                } else {
                    listShotPersonsStep1.clear();
                    tblShotPersonsStep1.setItems(listShotPersonsStep1);
                }
                tblShotPersonsStep1.setItems(listShotPersonsStep1);
                tblShotPersonsStep2.setItems(listShotPersonsStep2);
                tblShotPersonsStep3.setItems(listShotPersonsStep3);

                boolean isFind = false;
                for (IVFXScenes scene : listScenes) { // цикл по списку сцен
                    for (IVFXShots shot: scene.getShots()) {   // цикл по списку планов сцены
                        if (shot.isEqual(currentShot)) {  // если цегмент сцены совпадает с текущим планом

                            if (currentScene == null) { // если текущая сцена не выбрана
                                currentScene = scene;   // устанавливаем текущую сцену сценой из плана
                                tblScenes.getSelectionModel().select(currentScene); // переходим на нее в таблице сцен
                                tblScenesSmartScrollToCurrent();
                            }

                            if (!(currentScene.isEqual(scene))) {   // если текущая сцена не равна сцене плана
                                currentScene = scene;   // устанавливаем текущую сцену сценой из плана
                                tblScenes.getSelectionModel().select(currentScene); // переходим на нее в таблице сцен
                                tblScenesSmartScrollToCurrent();
                            }

                            for (IVFXShots shotForScene: currentScene.getShots()) {   // цикл по списку планов сцены
                                if (shotForScene.isEqual(currentShot)) { // если план сцены совпадает с текущим планом
                                    currentShotForScene = shotForScene;   // устанавливаем текущий план сцены раным плану сцены
                                    tblSceneShots.getSelectionModel().select(currentShotForScene);    // переходим на него в таблице планов сцены
                                    tblSceneShotsSmartScrollToCurrent();
                                    break;
                                }
                            }

                            if (currentShotForScene == null || !(currentShotForScene.isEqual(currentShot))) {
                                currentShotForScene = currentShot; // устанавливаем сцену плана как текущая сцена
                                tblSceneShots.getSelectionModel().select(currentShotForScene);    // переходим на нее в таблице планов сцены
                                tblSceneShotsSmartScrollToCurrent();
                            }
                            isFind = true;
                            break;
                        }
                        if (isFind) break;
                    }
                }


                isFind = false;
                for (IVFXEvents event : listEvents) { // цикл по списку Events
                    for (IVFXShots shot: event.getShots()) {   // цикл по списку планов Events
                        if (shot.isEqual(currentShot)) {  // если цегмент сцены совпадает с текущим планом

                            if (currentEvent == null) { // если текущая Event не выбрана
                                currentEvent = event;   // устанавливаем текущую Event Event из плана
                                tblEvents.getSelectionModel().select(currentEvent); // переходим на нее в таблице Events
                                tblEventsSmartScrollToCurrent();
                            }

                            if (!(currentEvent.isEqual(event))) {   // если текущая Event не равна Event плана
                                currentEvent = event;   // устанавливаем текущую Event Event из плана
                                tblEvents.getSelectionModel().select(currentEvent); // переходим на нее в таблице Events
                                tblEventsSmartScrollToCurrent();
                            }

                            for (IVFXShots shotForEvent: currentEvent.getShots()) {   // цикл по списку планов Event
                                if (shotForEvent.isEqual(currentShot)) { // если план Event совпадает с текущим планом
                                    currentShotForEvent = shotForEvent;   // устанавливаем текущий план Event раным плану Event
                                    tblEventShots.getSelectionModel().select(currentShotForEvent);    // переходим на него в таблице планов Event
                                    tblEventShotsSmartScrollToCurrent();
                                    break;
                                }
                            }

                            if (currentShotForEvent == null || !(currentShotForEvent.isEqual(currentShot))) {
                                currentShotForEvent = currentShot; // устанавливаем Event плана как текущая Event
                                tblEventShots.getSelectionModel().select(currentShotForEvent);    // переходим на нее в таблице планов Event
                                tblEventShotsSmartScrollToCurrent();
                            }
                            isFind = true;
                            break;
                        }
                        if (isFind) break;
                    }
                }

                // Инициализируем лейблы превью
                initializeLabelsPreview();

                // настраиваем плеер
                playerIsEndOfMedia = false;
                int durationStart = FFmpeg.getDurationByFrameNumber(currentShot.getFirstFrameNumber(), currentShot.getIvfxFile().getFrameRate());
                int durationEnd = FFmpeg.getDurationByFrameNumber(currentShot.getLastFrameNumber() + 1, currentShot.getIvfxFile().getFrameRate());
                mainMediaPlayer.setStartTime(Duration.millis(durationStart));
                mainMediaPlayer.setStopTime(Duration.millis(durationEnd));
                mainMediaPlayer.seek(Duration.millis(durationStart));
                mainSlider.setMin(durationStart);
                mainSlider.setMax(durationEnd);
                mainSlider.setValue(durationStart);
                mainLabelPlayer.setText("План: (" + FFmpeg.convertDurationToString(durationStart) + " - " + FFmpeg.convertDurationToString(durationEnd) + "). [" + FFmpeg.convertDurationToString(durationEnd - durationStart) + "]");

            }
        });

        // событие отслеживани видимости на экране текущей записи в таблице tblShots
        tblShots.skinProperty().addListener((ChangeListener<Skin>) (ov, t, t1) -> {
            if (t1 == null) return;
            TableViewSkin tvs = (TableViewSkin) t1;
            ObservableList<Node> kids = tvs.getChildren();
            if (kids == null || kids.isEmpty()) return;
            flowTblShots = (VirtualFlow) kids.get(1);
        });

    }

    private void initPersonsAll() {
        LoadListPersonsTask loadListPersonsTask = new LoadListPersonsTask(currentFile.getIvfxProject(),true, ctlProgressPersonsAll);
        loadListPersonsTask.addEventHandler(WorkerStateEvent.WORKER_STATE_SUCCEEDED, //
                t -> {
                    listPersonsAll = FXCollections.observableArrayList(loadListPersonsTask.getValue());
                    filteredPersonAll = new FilteredList<>(listPersonsAll, e -> true);

                    // таблица всех персонажей
                    colPersonsAllPreview.setCellValueFactory(new PropertyValueFactory<>("preview1"));
                    tblPersonsAll.setItems(listPersonsAll);
                });
        Thread thread = new Thread(loadListPersonsTask);
        thread.start();
        if (!DO_IN_MULTITHREADS) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

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

        // событие выбора записи в таблице персонажей проекта
        tblPersonsAll.getSelectionModel().selectedItemProperty().addListener((v, oldValue, newValue) -> {
            if (newValue != null) {
                IVFXPersons selectedPerson = (IVFXPersons) newValue;
                if (mainSelectedPerson == null || !mainSelectedPerson.equals(selectedPerson)) {
                    mainSelectedPerson = selectedPerson;
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

        // нажатие Enter в поле ctlFindPersonsAll - переход на первую запись в таблице tblPersonsAll
        ctlFindPersonsAll.setOnKeyPressed(ke -> {
            if (ke.getCode().equals(KeyCode.ENTER))
            {
                tblPersonsAll.requestFocus();
                tblPersonsAll.getSelectionModel().select(0);
                tblPersonsAll.scrollTo(0);
            }
            if (ke.getCode() == KeyCode.F7) goToPreviousShot();
            if (ke.getCode() == KeyCode.F8) goToNextShot();
        });

        // событие двойного клика в таблице персонажей проекта
        tblPersonsAll.setOnMouseClicked(mouseEvent -> {
            if (mouseEvent.getButton().equals(MouseButton.PRIMARY) && mouseEvent.getClickCount() == 2) {
                addSelectedPersonToShotPersonList(mainSelectedPerson);
            }
        });

        // нажатие Enter в поле в таблице персонажей проекта
        tblPersonsAll.setOnKeyPressed(ke -> {
            if (ke.getCode().equals(KeyCode.ENTER))
            {
                addSelectedPersonToShotPersonList(mainSelectedPerson);
                ctlFindPersonsAll.requestFocus();
                ctlFindPersonsAll.setText("");
            }
        });

    }

    private void initShotPersons() {


        // таблица персонажей плана
        colShotPersonsPreview.setCellValueFactory(new PropertyValueFactory<>("preview2"));
        tblShotPersons.setItems(listShotPersons);

        filteredShotPersons = new FilteredList<>(listShotPersons, e -> true);

        // скрываем заголовок у таблицы tblShotPersons
        tblShotPersons.widthProperty().addListener((source,oldWidth,newWidth) -> {
            Pane header = (Pane) tblShotPersons.lookup("TableHeaderRow");
            if (header.isVisible()){
                header.setMaxHeight(0);
                header.setMinHeight(0);
                header.setPrefHeight(0);
                header.setVisible(false);
            }
        });

        // событие выбора записи в таблице персонажей плана
        tblShotPersons.getSelectionModel().selectedItemProperty().addListener((v, oldValue, newValue) -> {
            if (newValue != null) {
                IVFXPersons selectedPerson = (IVFXPersons) newValue.getIvfxPerson();
                if (mainSelectedPerson == null || !mainSelectedPerson.equals(selectedPerson)) {
                    mainSelectedPerson = selectedPerson;
                }

            }
        });

        // фильтация таблицы персонажей плана
        ctlFindShotPersons.setOnKeyReleased(e -> {
            ctlFindShotPersons.textProperty().addListener((v, oldValue, newValue) -> {
                filteredShotPersons.setPredicate((Predicate<? super IVFXShotsPersons>) ivfxShotsPersons -> {
                    if (newValue == null || newValue.isEmpty()) {
                        return true;
                    }
                    String lowerCaseFilter = newValue.toLowerCase();
                    if (ivfxShotsPersons.getName().toLowerCase().contains(lowerCaseFilter)) {
                        return true;
                    } if (ivfxShotsPersons.getDescription().toLowerCase().contains(lowerCaseFilter)) {
                        return true;
                    }
                    return false;
                });

            });
            SortedList<IVFXShotsPersons> sortedShotPerson = new SortedList<>(filteredShotPersons);
            sortedShotPerson.comparatorProperty().bind(tblShotPersons.comparatorProperty());
            tblShotPersons.setItems(sortedShotPerson);
            if (sortedShotPerson.size() > 0) {
                tblShotPersons.getSelectionModel().select(sortedShotPerson.get(0));
                tblShotPersons.scrollTo(sortedShotPerson.get(0));
            }
        });

        // нажатие Enter в поле ctlFindShotPersons - переход на первую запись в таблице tblShotPersons
        ctlFindShotPersons.setOnKeyPressed(ke -> {
            if (ke.getCode().equals(KeyCode.ENTER))
            {
                tblShotPersons.requestFocus();
                tblShotPersons.getSelectionModel().select(0);
                tblShotPersons.scrollTo(0);
            }
            if (ke.getCode() == KeyCode.F7) goToPreviousShot();
            if (ke.getCode() == KeyCode.F8) goToNextShot();
        });

        // событие двойного клика в таблице персонажей плана
        tblShotPersons.setOnMouseClicked(mouseEvent -> {
            if (mouseEvent.getButton().equals(MouseButton.PRIMARY) && mouseEvent.getClickCount() == 2) {
                deleteSelectedPersonFromShotPersonList(mainSelectedPerson);
            }
        });

    }

    private void initShotPersonsStep1() {

        // таблица персонажей плана -1
        colShotPersonsStep1Preview.setCellValueFactory(new PropertyValueFactory<>("preview3"));
        tblShotPersonsStep1.setItems(listShotPersonsStep1);

        // скрываем заголовок у таблицы tblShotPersonsStep1
        tblShotPersonsStep1.widthProperty().addListener((source,oldWidth,newWidth) -> {
            Pane header = (Pane) tblShotPersonsStep1.lookup("TableHeaderRow");
            if (header.isVisible()){
                header.setMaxHeight(0);
                header.setMinHeight(0);
                header.setPrefHeight(0);
                header.setVisible(false);
            }
        });

        // событие выбора записи в таблице персонажей плана шаг-1
        tblShotPersonsStep1.getSelectionModel().selectedItemProperty().addListener((v, oldValue, newValue) -> {
            if (newValue != null) {
                IVFXPersons selectedPerson = (IVFXPersons) newValue.getIvfxPerson();
                if (mainSelectedPerson == null || !mainSelectedPerson.equals(selectedPerson)) {
                    mainSelectedPerson = selectedPerson;
                }

            }
        });

        // событие двойного клика в таблице персонажей предыдущего плана шаг-1
        tblShotPersonsStep1.setOnMouseClicked(mouseEvent -> {
            if (mouseEvent.getButton().equals(MouseButton.PRIMARY) && mouseEvent.getClickCount() == 2) {
                addSelectedPersonToShotPersonList(mainSelectedPerson);
            }
        });

    }

    private void initShotPersonsStep2() {

        // таблица персонажей плана -2
        colShotPersonsStep2Preview.setCellValueFactory(new PropertyValueFactory<>("preview4"));
        tblShotPersonsStep2.setItems(listShotPersonsStep2);

        // скрываем заголовок у таблицы tblShotPersonsStep2
        tblShotPersonsStep2.widthProperty().addListener((source,oldWidth,newWidth) -> {
            Pane header = (Pane) tblShotPersonsStep2.lookup("TableHeaderRow");
            if (header.isVisible()){
                header.setMaxHeight(0);
                header.setMinHeight(0);
                header.setPrefHeight(0);
                header.setVisible(false);
            }
        });

        // событие выбора записи в таблице персонажей плана шаг-2
        tblShotPersonsStep2.getSelectionModel().selectedItemProperty().addListener((v, oldValue, newValue) -> {
            if (newValue != null) {
                IVFXPersons selectedPerson = (IVFXPersons) newValue.getIvfxPerson();
                if (mainSelectedPerson == null || !mainSelectedPerson.equals(selectedPerson)) {
                    mainSelectedPerson = selectedPerson;
                }

            }
        });

        // событие двойного клика в таблице персонажей предыдущего плана шаг-2
        tblShotPersonsStep2.setOnMouseClicked(mouseEvent -> {
            if (mouseEvent.getButton().equals(MouseButton.PRIMARY) && mouseEvent.getClickCount() == 2) {
                addSelectedPersonToShotPersonList(mainSelectedPerson);
            }
        });

    }

    private void initShotPersonsStep3() {

        // таблица персонажей плана -3
        colShotPersonsStep3Preview.setCellValueFactory(new PropertyValueFactory<>("preview5"));
        tblShotPersonsStep3.setItems(listShotPersonsStep3);

        // скрываем заголовок у таблицы tblShotPersonsStep3
        tblShotPersonsStep3.widthProperty().addListener((source,oldWidth,newWidth) -> {
            Pane header = (Pane) tblShotPersonsStep3.lookup("TableHeaderRow");
            if (header.isVisible()){
                header.setMaxHeight(0);
                header.setMinHeight(0);
                header.setPrefHeight(0);
                header.setVisible(false);
            }
        });

        // событие выбора записи в таблице персонажей плана шаг-3
        tblShotPersonsStep3.getSelectionModel().selectedItemProperty().addListener((v, oldValue, newValue) -> {
            if (newValue != null) {
                IVFXPersons selectedPerson = (IVFXPersons) newValue.getIvfxPerson();
                if (mainSelectedPerson == null || !mainSelectedPerson.equals(selectedPerson)) {
                    mainSelectedPerson = selectedPerson;
                }

            }
        });

        // событие двойного клика в таблице персонажей предыдущего плана шаг-3
        tblShotPersonsStep3.setOnMouseClicked(mouseEvent -> {
            if (mouseEvent.getButton().equals(MouseButton.PRIMARY) && mouseEvent.getClickCount() == 2) {
                addSelectedPersonToShotPersonList(mainSelectedPerson);
            }
        });

    }

    private void initScenes() {

        LoadListScenesTask loadListScenesTask = new LoadListScenesTask(currentFile, ctlProgressScenes);
        loadListScenesTask.addEventHandler(WorkerStateEvent.WORKER_STATE_SUCCEEDED, //
                t2 -> {
                    listScenes = FXCollections.observableArrayList(loadListScenesTask.getValue());

                    // таблица сцен
                    colSceneStart.setCellValueFactory(new PropertyValueFactory<>("start"));
                    colSceneName.setCellValueFactory(new PropertyValueFactory<>("name"));
                    tblScenes.setItems(listScenes);
                    tblScenes.setEditable(true);
                    colSceneName.setCellFactory(TextFieldTableCell.forTableColumn());

                    for (IVFXScenes scene : listScenes) {
                        List<IVFXScenesShots> sceneShots = IVFXScenesShots.loadList(scene);
                        List<IVFXShots> list = new ArrayList<>();
                        for (IVFXScenesShots sceneShot: sceneShots) {
                            if (listShotsOriginal != null) {
                                for (IVFXShots shot: listShotsOriginal) {
                                    if (shot.isEqual(sceneShot.getIvfxShot())) {
                                        list.add(shot);
                                        break;
                                    }
                                }
                            }

                        }
                        scene.setShots(list);

                        LoadListScenePersonsTask loadListScenePersonsTask = new LoadListScenePersonsTask(scene,true,ctlProgressScenePersons);
                        loadListScenePersonsTask.addEventHandler(WorkerStateEvent.WORKER_STATE_SUCCEEDED, t3 -> {
                            scene.setScenePersons(loadListScenePersonsTask.getValue());
                            scene.setScenePersons2(loadListScenePersonsTask.getValue());
                        });
                        Thread thread = new Thread(loadListScenePersonsTask);
                        thread.start();
                        if (!DO_IN_MULTITHREADS) {
                            try {
                                thread.join();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
//                        LoadListScenePersonsTask loadListScenePersonsTask2 = new LoadListScenePersonsTask(scene,true,ctlProgressScenePersonsToEvents);
//                        loadListScenePersonsTask2.addEventHandler(WorkerStateEvent.WORKER_STATE_SUCCEEDED, t3 -> {
//                            scene.setScenePersons2(loadListScenePersonsTask2.getValue());
//                        });

                    }

                });

        Thread thread = new Thread(loadListScenesTask);
        thread.start();
        if (!DO_IN_MULTITHREADS) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // скрываем заголовок у таблицы tblScenes
        tblScenes.widthProperty().addListener((source,oldWidth,newWidth) -> {
            Pane header = (Pane) tblScenes.lookup("TableHeaderRow");
            if (header.isVisible()){
                header.setMaxHeight(0);
                header.setMinHeight(0);
                header.setPrefHeight(0);
                header.setVisible(false);
            }
        });

        // событие выбора записи в таблице сцен
        tblScenes.getSelectionModel().selectedItemProperty().addListener((v, oldValue, newValue) -> {
            if (newValue != null) {
                currentScene = newValue;    // устанавливаем текущую сцену
                listShotsForScene = FXCollections.observableArrayList(currentScene.getShots()); // загружаем список планов для нее

                tblSceneShots.setItems(listShotsForScene);    // привязываем таблицу
                boolean isFound = false;

                final int[] iProgress = {0};
                ctlProgressSceneShots.setVisible(true);
                for (IVFXShots shotForScene: listShotsForScene) {  // цикл по планам текущей сцены
                    Platform.runLater(()->{
                        ctlProgressSceneShots.setProgress((double)++iProgress[0] / listShotsForScene.size());
                    });
                    if (currentShot == null) { // если текущий план пустой
                        for (IVFXShots shot: listShots) { // цикл по списку планов
                            if (shot.isEqual(shotForScene)) {
                                currentShot = shot;
                                tblSceneShots.getSelectionModel().select(currentShot);
                                tblShotsSmartScrollToCurrent();
                                break;
                            }
                        }
                    }

                    if (shotForScene.isEqual(currentShot)) {
                        currentShotForScene = shotForScene;
                        tblSceneShots.getSelectionModel().select(currentShotForScene);
                        tblSceneShotsSmartScrollToCurrent();
                        isFound = true;
                    }
                }
                ctlProgressSceneShots.setVisible(false);

                if (!isFound) {
                    for (IVFXShots shot: listShots) { // цикл по списку планов
                        if (shot.isEqual(currentScene.getShots().get(0))) {
                            currentShot = shot;
                            tblSceneShots.getSelectionModel().select(currentShot);
                            tblShotsSmartScrollToCurrent();
                            currentShotForScene = currentScene.getShots().get(0);
                            tblSceneShots.getSelectionModel().select(currentShotForScene);
                            tblSceneShotsSmartScrollToCurrent();
                            break;
                        }
                    }
                }

                listScenePersons = FXCollections.observableArrayList(new ArrayList<>(currentScene.getScenePersons()));
                listScenePersonsForEvents = FXCollections.observableArrayList(new ArrayList<>(currentScene.getScenePersons2()));
                tblScenePersons.setItems(listScenePersons);
                tblScenePersonsToEvents.setItems(listScenePersonsForEvents);

                // настраиваем плеер
                int durationStart = FFmpeg.getDurationByFrameNumber(currentScene.getFirstShot().getFirstFrameNumber(), currentFile.getFrameRate());
                int durationEnd = FFmpeg.getDurationByFrameNumber(currentScene.getLastShot().getLastFrameNumber() + 1, currentFile.getFrameRate());
                mainMediaPlayer.setStartTime(Duration.millis(durationStart));
                mainMediaPlayer.setStopTime(Duration.millis(durationEnd));
                mainMediaPlayer.seek(Duration.millis(durationStart));
                mainSlider.setMin(durationStart);
                mainSlider.setMax(durationEnd);
                mainSlider.setValue(durationStart);
                mainLabelPlayer.setText("Сцена: «" + currentScene.getName() + "». [" + FFmpeg.convertDurationToString(durationEnd - durationStart) + "]");

            }
        });

        // событие отслеживани видимости на экране текущей записи в таблице tblScenes
        tblScenes.skinProperty().addListener((ChangeListener<Skin>) (ov, t, t1) -> {
            if (t1 == null) return;
            TableViewSkin tvs = (TableViewSkin) t1;
            ObservableList<Node> kids = tvs.getChildren();
            if (kids == null || kids.isEmpty()) return;
            flowTblScenes = (VirtualFlow) kids.get(1);
        });

        // событие редактирования столбца sceneNameColumn
        colSceneName.setOnEditCommit(e -> {
            currentScene.setName(e.getNewValue());
            currentScene.save();
        });

    }

    private void initScenePersons() {

        // таблица персонажей сцены
        colScenePersonsPreview.setCellValueFactory(new PropertyValueFactory<>("preview6"));
        colScenePersonsIsMain.setCellValueFactory(new PropertyValueFactory<>("personIsMainStr"));
        tblScenePersons.setItems(listScenePersons);

        // скрываем заголовок у таблицы tblScenePersons
        tblScenePersons.widthProperty().addListener((source,oldWidth,newWidth) -> {
            Pane header = (Pane) tblScenePersons.lookup("TableHeaderRow");
            if (header.isVisible()){
                header.setMaxHeight(0);
                header.setMinHeight(0);
                header.setPrefHeight(0);
                header.setVisible(false);
            }
        });

        // событие выбора записи в таблице персонажей сцены
        tblScenePersons.getSelectionModel().selectedItemProperty().addListener((v, oldValue, newValue) -> {
            if (newValue != null) {
                IVFXPersons selectedPerson = (IVFXPersons) newValue.getIvfxPerson();
                if (mainSelectedPerson == null || !mainSelectedPerson.equals(selectedPerson)) {
                    mainSelectedPerson = selectedPerson;
                }

            }
        });

        // событие двойного клика в таблице персонажей сцены
        tblScenePersons.setOnMouseClicked(mouseEvent -> {
            if (mouseEvent.getButton().equals(MouseButton.PRIMARY) && mouseEvent.getClickCount() == 2) {
                addSelectedPersonToShotPersonList(mainSelectedPerson);
            }
        });

    }

    private void initScenePersonsToEvents() {

        // таблица персонажей сцены для Events
        colScenePersonsToEventsPreview.setCellValueFactory(new PropertyValueFactory<>("preview7"));
        colScenePersonsToEventsIsMain.setCellValueFactory(new PropertyValueFactory<>("personIsMainStr"));
        tblScenePersonsToEvents.setItems(listScenePersonsForEvents);

        // скрываем заголовок у таблицы tblScenePersonsToEvents
        tblScenePersonsToEvents.widthProperty().addListener((source,oldWidth,newWidth) -> {
            Pane header = (Pane) tblScenePersonsToEvents.lookup("TableHeaderRow");
            if (header.isVisible()){
                header.setMaxHeight(0);
                header.setMinHeight(0);
                header.setPrefHeight(0);
                header.setVisible(false);
            }
        });

        // событие выбора записи в таблице персонажей cцены для события
        tblScenePersonsToEvents.getSelectionModel().selectedItemProperty().addListener((v, oldValue, newValue) -> {
            if (newValue != null) {
                IVFXPersons selectedPerson = (IVFXPersons) newValue.getIvfxPerson();
                if (mainSelectedPerson == null || !mainSelectedPerson.equals(selectedPerson)) {
                    mainSelectedPerson = selectedPerson;
                }

            }
        });

        // событие двойного клика в таблице персонажей сцены для события
        tblScenePersonsToEvents.setOnMouseClicked(mouseEvent -> {
            if (mouseEvent.getButton().equals(MouseButton.PRIMARY) && mouseEvent.getClickCount() == 2) {
                addSelectedPersonToEventPersonList(mainSelectedPerson);
            }
        });

    }

    private void initSceneShots() {

        // таблица планов сцен
        colSceneShotFrom.setCellValueFactory(new PropertyValueFactory<>("labelFirst2"));
        colSceneShotTo.setCellValueFactory(new PropertyValueFactory<>("labelLast2"));
        tblSceneShots.setItems(listShotsForScene);

        // скрываем заголовок у таблицы tblSceneShots
        tblSceneShots.widthProperty().addListener((source,oldWidth,newWidth) -> {
            Pane header = (Pane) tblSceneShots.lookup("TableHeaderRow");
            if (header.isVisible()){
                header.setMaxHeight(0);
                header.setMinHeight(0);
                header.setPrefHeight(0);
                header.setVisible(false);
            }
        });

        // событие выбора записи в таблице планов сцены
        tblSceneShots.getSelectionModel().selectedItemProperty().addListener((v, oldValue, newValue) -> {
            if (newValue != null) {
                currentShotForScene = newValue;
                for (IVFXShots shot: listShots) {
                    if (shot.isEqual(currentShotForScene)) {
                        tblShots.getSelectionModel().select(shot);
                        tblShotsSmartScrollToCurrent();
                    }
                }
            }
        });

        // событие отслеживани видимости на экране текущей записи в таблице tblSceneShots
        tblSceneShots.skinProperty().addListener((ChangeListener<Skin>) (ov, t, t1) -> {
            if (t1 == null) return;
            TableViewSkin tvs = (TableViewSkin) t1;
            ObservableList<Node> kids = tvs.getChildren();
            if (kids == null || kids.isEmpty()) return;
            flowTblSceneShots = (VirtualFlow) kids.get(1);
        });

    }

    private void initEventsTypes() {

        // Список типов событий
        listEventTypesNames = FXCollections.observableArrayList(IVFXEventsTypes.loadListNames(currentFile.getIvfxProject()));
        comboEventType.setItems(listEventTypesNames);
        currentEventType = IVFXEventsTypes.load(1);
        comboEventType.setValue(currentEventType.getName());

        // событие выбора типа события из списка типов события
        comboEventType.setOnAction(e -> {
            currentEventType = IVFXEventsTypes.loadByName(comboEventType.getValue(), currentFile.getIvfxProject());
        });

    }

    private void initEvents() {

        LoadListEventsTask loadListEventsTask = new LoadListEventsTask(currentFile, ctlProgressEvents);
        loadListEventsTask.addEventHandler(WorkerStateEvent.WORKER_STATE_SUCCEEDED, t2 -> {
            listEvents = FXCollections.observableArrayList(loadListEventsTask.getValue());

            // таблица событий
            colEventStart.setCellValueFactory(new PropertyValueFactory<>("start"));
            colEventEnd.setCellValueFactory(new PropertyValueFactory<>("end"));
            colEventType.setCellValueFactory(new PropertyValueFactory<>("eventTypeName"));
            colEventName.setCellValueFactory(new PropertyValueFactory<>("name"));
            tblEvents.setItems(listEvents);

            for (IVFXEvents event : listEvents) {
                List<IVFXEventsShots> eventShots = IVFXEventsShots.loadList(event);
                List<IVFXShots> list = new ArrayList<>();
                for (IVFXEventsShots eventShot: eventShots) {
                    if (listShotsOriginal != null) {
                        for (IVFXShots shot: listShotsOriginal) {
                            if (shot.isEqual(eventShot.getIvfxShot())) {
                                list.add(shot);
                                break;
                            }
                        }
                    }

                }
                event.setShots(list);
                event.setEventPersons(IVFXEventsPersons.loadList(event,true));
            }

        });
        Thread thread = new Thread(loadListEventsTask);
        thread.start();
        if (!DO_IN_MULTITHREADS) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // скрываем заголовок у таблицы tblEvents
        tblEvents.widthProperty().addListener((source,oldWidth,newWidth) -> {
            Pane header = (Pane) tblEvents.lookup("TableHeaderRow");
            if (header.isVisible()){
                header.setMaxHeight(0);
                header.setMinHeight(0);
                header.setPrefHeight(0);
                header.setVisible(false);
            }
        });

        // событие выбора записи в таблице эвентов
        tblEvents.getSelectionModel().selectedItemProperty().addListener((v, oldValue, newValue) -> {
            if (newValue != null) {
                currentEvent = newValue;    // устанавливаем текущую сцену
                listShotsForEvent = FXCollections.observableArrayList(currentEvent.getShots()); // загружаем список планов для нее
                tblEventShots.setItems(listShotsForEvent);    // привязываем таблицу
                boolean isFound = false;
                for (IVFXShots shotForEvent: listShotsForEvent) {  // цикл по планам текущей сцены
                    if (currentShot == null) { // если текущий план пустой
                        for (IVFXShots shot: listShots) { // цикл по списку планов
                            if (shot.isEqual(shotForEvent)) {
                                currentShot = shot;
                                tblEventShots.getSelectionModel().select(currentShot);
                                tblShotsSmartScrollToCurrent();
                                break;
                            }
                        }
                    }

                    if (shotForEvent.isEqual(currentShot)) {
                        currentShotForEvent = shotForEvent;
                        tblEventShots.getSelectionModel().select(currentShotForEvent);
                        tblEventShotsSmartScrollToCurrent();
                        isFound = true;
                    }
                }
                if (!isFound) {
                    for (IVFXShots shot: listShots) { // цикл по списку планов
                        if (shot.isEqual(currentEvent.getShots().get(0))) {
                            currentShot = shot;
                            tblEventShots.getSelectionModel().select(currentShot);
                            tblShotsSmartScrollToCurrent();
                            currentShotForEvent = currentEvent.getShots().get(0);
                            tblEventShots.getSelectionModel().select(currentShotForEvent);
                            tblEventShotsSmartScrollToCurrent();
                            break;
                        }
                    }
                }

                listEventPersons = FXCollections.observableArrayList(currentEvent.getEventPersons());
                tblEventPersons.setItems(listEventPersons);

                // настраиваем плеер
                if (listShotsForEvent.size() > 0) {
                    IVFXShots firstShot = listShotsForEvent.get(0);
                    IVFXShots lastShot = listShotsForEvent.get(listShotsForEvent.size()-1);
                    int durationStart = FFmpeg.getDurationByFrameNumber(firstShot.getFirstFrameNumber(), currentFile.getFrameRate());
                    int durationEnd = FFmpeg.getDurationByFrameNumber(lastShot.getLastFrameNumber() + 1, currentFile.getFrameRate());
                    mainMediaPlayer.setStartTime(Duration.millis(durationStart));
                    mainMediaPlayer.setStopTime(Duration.millis(durationEnd));
                    mainMediaPlayer.seek(Duration.millis(durationStart));
                    mainSlider.setMin(durationStart);
                    mainSlider.setMax(durationEnd);
                    mainSlider.setValue(durationStart);
                    mainLabelPlayer.setText("Событие: «" + currentEvent.getEventTypeName() + "» " + currentEvent.getName() + ". [" + FFmpeg.convertDurationToString(durationEnd - durationStart) + "]");
                }


            }
        });

        // событие отслеживани видимости на экране текущей записи в таблице tblEvents
        tblEvents.skinProperty().addListener((ChangeListener<Skin>) (ov, t, t1) -> {
            if (t1 == null) return;
            TableViewSkin tvs = (TableViewSkin) t1;
            ObservableList<Node> kids = tvs.getChildren();
            if (kids == null || kids.isEmpty()) return;
            flowTblEvents = (VirtualFlow) kids.get(1);
        });

    }

    private void initEventShots() {

        // таблица планов событий
        colEventShotFrom.setCellValueFactory(new PropertyValueFactory<>("labelFirst3"));
        colEventShotTo.setCellValueFactory(new PropertyValueFactory<>("labelLast3"));
        tblEventShots.setItems(listShotsForEvent);

        // скрываем заголовок у таблицы tblEventShots
        tblEventShots.widthProperty().addListener((source,oldWidth,newWidth) -> {
            Pane header = (Pane) tblEventShots.lookup("TableHeaderRow");
            if (header.isVisible()){
                header.setMaxHeight(0);
                header.setMinHeight(0);
                header.setPrefHeight(0);
                header.setVisible(false);
            }
        });

        // событие выбора записи в таблице планов Events
        tblEventShots.getSelectionModel().selectedItemProperty().addListener((v, oldValue, newValue) -> {
            if (newValue != null) {
                currentShotForEvent = newValue;
                for (IVFXShots shot: listShots) {
                    if (shot.isEqual(currentShotForEvent)) {
                        tblShots.getSelectionModel().select(shot);
                        tblShotsSmartScrollToCurrent();
                    }
                }
            }
        });

        // событие отслеживани видимости на экране текущей записи в таблице tblEventShots
        tblEventShots.skinProperty().addListener((ChangeListener<Skin>) (ov, t, t1) -> {
            if (t1 == null) return;
            TableViewSkin tvs = (TableViewSkin) t1;
            ObservableList<Node> kids = tvs.getChildren();
            if (kids == null || kids.isEmpty()) return;
            flowTblEventShots = (VirtualFlow) kids.get(1);
        });

    }

    private void initEventPersons() {

        // таблица персонажей событий
        colEventPersonsPreview.setCellValueFactory(new PropertyValueFactory<>("preview8"));
        colEventPersonsIsMain.setCellValueFactory(new PropertyValueFactory<>("personIsMainStr"));
        tblEventPersons.setItems(listEventPersons);

        // скрываем заголовок у таблицы tblEventPersons
        tblEventPersons.widthProperty().addListener((source,oldWidth,newWidth) -> {
            Pane header = (Pane) tblEventPersons.lookup("TableHeaderRow");
            if (header.isVisible()){
                header.setMaxHeight(0);
                header.setMinHeight(0);
                header.setPrefHeight(0);
                header.setVisible(false);
            }
        });

        // событие выбора записи в таблице персонажей события
        tblEventPersons.getSelectionModel().selectedItemProperty().addListener((v, oldValue, newValue) -> {
            if (newValue != null) {
                IVFXPersons selectedPerson = (IVFXPersons) newValue.getIvfxPerson();
                if (mainSelectedPerson == null || !mainSelectedPerson.equals(selectedPerson)) {
                    mainSelectedPerson = selectedPerson;
                }

            }
        });

        // событие двойного клика в таблице персонажей события
        tblEventPersons.setOnMouseClicked(mouseEvent -> {
            if (mouseEvent.getButton().equals(MouseButton.PRIMARY) && mouseEvent.getClickCount() == 2) {
                deleteSelectedPersonFromEventPersonList(mainSelectedPerson);
            }
        });

    }

    private void initHotkeys() {

        // ГОРЯЧИЕ КЛАВИШИ
        mainPane.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.F1) doShotPersonsStep1(new ActionEvent());
            if (e.getCode() == KeyCode.F2) doShotPersonsStep2(new ActionEvent());
            if (e.getCode() == KeyCode.F3) doShotPersonsStep3(new ActionEvent());
            if (e.getCode() == KeyCode.F4) doScenePersonsToShot(new ActionEvent());
            if (e.getCode() == KeyCode.F5) doAddNewScene(new ActionEvent());
            if (e.getCode() == KeyCode.F6) doAddNewEvent(new ActionEvent());
            if (e.getCode() == KeyCode.F7) goToPreviousShot();
            if (e.getCode() == KeyCode.F8) goToNextShot();
            if (e.getCode() == KeyCode.F9) createShotMXFaudioON();
            if (e.getCode() == KeyCode.F10) createShotMXFaudioOFF();
        });

    }

    @FXML
    void doOK(ActionEvent event) {
        shotsController.controllerStage.close();
    }

    private void createShotMXFaudioOFF() {

        System.out.println("createShotMXF");

        IVFXShots shot = currentShot;
        IVFXFiles ivfxFile = shot.getIvfxFile();

        int w = 1920;
        int h = 1080;

        int fileWidth = ivfxFile.getWidth();
        int fileHeight = ivfxFile.getHeight();
        double fileAspect = (double) fileWidth / (double) fileHeight;
        double frameAspect = (double) w / (double) h;

        String filter = "";

        if (fileAspect > frameAspect) {
            int frameHeight = (int)((double) w / fileAspect);
            filter = "\"scale="+w+":"+frameHeight+",pad="+w+":"+h+":0:" + (int)((h-frameHeight)/2.0) + ":black\"";
        } else {
            int frameWidth = (int)((double) h * fileAspect);
            filter = "\"scale="+frameWidth+":"+h+",pad="+w+":"+h+":" + (int)((w-frameWidth)/2.0) + ":0:black\"";
        }

        String pathToX264Folder = ivfxFile.getIvfxProject().getFolder() + "\\Video\\1920x1080";
        String fileNameOutMXF = pathToX264Folder + "\\" + shot.getShotVideoFileNameWihoutFolderMXFaudioOFF();
        int firstFrame = shot.getFirstFrameNumber();
        int lastFrame = shot.getLastFrameNumber();
        double frameRate = ivfxFile.getFrameRate();
        int framesToCode = lastFrame - firstFrame + 1;
        String start = ((Double)(((double)FFmpeg.getDurationByFrameNumber(firstFrame,frameRate))/1000)).toString();
        String pathToFFmpeg = FFmpeg.PATH_TO_FFMPEG;
        String pathToLosslessFolder =  ivfxFile.getIvfxProject().getFolder() + "\\Video\\Lossless";
        String pathToLosslessFile = pathToLosslessFolder + "\\" + ivfxFile.getShortName() + "_lossless.mkv";
        String resolution = w + "x" + h;

        String audioCodec = "aac";
        int audioBitrate = 196608;
        int audioFreq = 48000;

        // создание плана
        List<String> param = new ArrayList<String>();
        param.add(pathToFFmpeg);
        if (firstFrame != 0) {
            param.add("-ss");
            param.add(start);
        }
        param.add("-i");
        param.add(pathToLosslessFile);
        param.add("-map");
        param.add("0:v:0");
        param.add("-vframes");
        param.add(String.valueOf(framesToCode));
        param.add("-s");
        param.add(resolution);
        param.add("-c:v");
        param.add("dnxhd");
        param.add("-b:v");
        param.add("36M");
//        param.add("-vf");
//        param.add(filter);
        param.add("-y");
        param.add(fileNameOutMXF);

        String line = "";
        for (String parameter : param) {
            line = line + parameter + " ";
        }
        line = line.substring(0,line.length()-1)+"\n";

        String textCmdFull = line;
        File folder = new File(pathToX264Folder);
        File file = new File(ivfxFile.getSourceName());
        String fileName = file.getName();
        String fileNameWOExt = fileName.substring(0,fileName.lastIndexOf("."));
        String fileCmdName = pathToX264Folder + "\\_" + fileNameWOExt + ".cmd";
        File fileCMD = new File(fileCmdName);
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(fileCMD));
            writer.write(textCmdFull);
            writer.flush();
            writer.close();
            Process process = Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + fileCmdName);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void createShotMXFaudioON() {

        System.out.println("createShotMXF");

        IVFXShots shot = currentShot;
        IVFXFiles ivfxFile = shot.getIvfxFile();

        int w = 1920;
        int h = 1080;

        int fileWidth = ivfxFile.getWidth();
        int fileHeight = ivfxFile.getHeight();
        double fileAspect = (double) fileWidth / (double) fileHeight;
        double frameAspect = (double) w / (double) h;

        String filter = "";

        if (fileAspect > frameAspect) {
            int frameHeight = (int)((double) w / fileAspect);
            filter = "\"scale="+w+":"+frameHeight+",pad="+w+":"+h+":0:" + (int)((h-frameHeight)/2.0) + ":black\"";
        } else {
            int frameWidth = (int)((double) h * fileAspect);
            filter = "\"scale="+frameWidth+":"+h+",pad="+w+":"+h+":" + (int)((w-frameWidth)/2.0) + ":0:black\"";
        }

        String pathToX264Folder = ivfxFile.getIvfxProject().getFolder() + "\\Video\\1920x1080";
        String fileNameOutMXF = pathToX264Folder + "\\" + shot.getShotVideoFileNameWihoutFolderMXFaudioON();
        int firstFrame = shot.getFirstFrameNumber();
        int lastFrame = shot.getLastFrameNumber();
        double frameRate = ivfxFile.getFrameRate();
        int framesToCode = lastFrame - firstFrame + 1;
        String start = ((Double)(((double)FFmpeg.getDurationByFrameNumber(firstFrame,frameRate))/1000)).toString();
        String pathToFFmpeg = FFmpeg.PATH_TO_FFMPEG;
        String pathToLosslessFolder =  ivfxFile.getIvfxProject().getFolder() + "\\Video\\Lossless";
        String pathToLosslessFile = pathToLosslessFolder + "\\" + ivfxFile.getShortName() + "_lossless.mkv";
        String resolution = w + "x" + h;

        String audioCodec = "aac";
        int audioBitrate = 196608;
        int audioFreq = 48000;

        // создание плана
        List<String> param = new ArrayList<String>();
        param.add(pathToFFmpeg);
        if (firstFrame != 0) {
            param.add("-ss");
            param.add(start);
        }
        param.add("-i");
        param.add(pathToLosslessFile);
        param.add("-map");
        param.add("0:v:0");
        param.add("-map");
        param.add("0:a:0");
        param.add("-vframes");
        param.add(String.valueOf(framesToCode));
        param.add("-s");
        param.add(resolution);
        param.add("-c:v");
        param.add("dnxhd");
        param.add("-b:v");
        param.add("36M");
        param.add("-acodec");
        param.add("pcm_s16le");
//        param.add("-vf");
//        param.add(filter);
        param.add("-y");
        param.add(fileNameOutMXF);

        String line = "";
        for (String parameter : param) {
            line = line + parameter + " ";
        }
        line = line.substring(0,line.length()-1)+"\n";

        String textCmdFull = line;
        File folder = new File(pathToX264Folder);
        File file = new File(ivfxFile.getSourceName());
        String fileName = file.getName();
        String fileNameWOExt = fileName.substring(0,fileName.lastIndexOf("."));
        String fileCmdName = pathToX264Folder + "\\_" + fileNameWOExt + ".cmd";
        File fileCMD = new File(fileCmdName);
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(fileCMD));
            writer.write(textCmdFull);
            writer.flush();
            writer.close();
            Process process = Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + fileCmdName);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}

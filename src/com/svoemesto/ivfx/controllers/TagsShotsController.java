package com.svoemesto.ivfx.controllers;

import com.sun.javafx.scene.control.skin.TableViewSkin;
import com.sun.javafx.scene.control.skin.VirtualFlow;
import com.svoemesto.ivfx.Main;
import com.svoemesto.ivfx.tables.*;
import com.svoemesto.ivfx.utils.ConvertToFxImage;
import com.svoemesto.ivfx.utils.FFmpeg;
import com.svoemesto.ivfx.utils.OverlayImage;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.apache.commons.io.FileUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.util.List;
import java.util.function.Predicate;

public class TagsShotsController extends Application {


    @FXML
    private AnchorPane mainPane;

    @FXML
    private ComboBox<IVFXProjects> cbProject;

    @FXML
    private ComboBox<IVFXFiles> cbFiles;

    @FXML
    private Button btnUpdateShotTagsByFaces;

    @FXML
    private TableView<IVFXShots> tblShots;

    @FXML
    private Button btnKeepShotsWithZeroTagSize;

    @FXML
    private TableColumn<IVFXShots, String> colShotFrom;

    @FXML
    private TableColumn<IVFXShots, String> colShotTo;

    @FXML
    private GridPane gpPreview;

    @FXML
    private Label lblFullSizePicture;

    @FXML
    private ContextMenu contxtMenuFrame;

    @FXML
    private ContextMenu contxtMenuTagShotTypeSize;

    @FXML
    private ContextMenu contxtMenuTagShotTypeSizeForAll;

    @FXML
    private AnchorPane apPlayer;

    @FXML
    private Button btnCreateNewTag;

    @FXML
    private MediaView mainMediaView;

    @FXML
    private Label mainLabelPlayer;

    @FXML
    private Slider mainSlider;

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
    private TableView<IVFXShotsTypeSize> tblShotsTypesSize;

    @FXML
    private TableColumn<IVFXShotsTypeSize, String> colNameTblShotsTypesSize;


    @FXML
    private TableView<IVFXShotsTypePersons> tblShotsTypesPersons;

    @FXML
    private TableColumn<IVFXShotsTypePersons, String> colNameTblShotsTypesPersons;

    @FXML
    private TextField fldFindTagsAll;


    @FXML
    private CheckBox chFindTagsAllInProperties;

    @FXML
    private CheckBox cbTagsAllOnlyFromHtml;

    @FXML
    private TableView<IVFXTags> tblTagsAll;

    @FXML
    private TableColumn<IVFXTags, String> colTypeTblTagsAll;

    @FXML
    private TableColumn<IVFXTags, String> colNameTblTagsAll;

    @FXML
    private TableView<IVFXTagsShots> tblTagsShots;

    @FXML
    private TableColumn<IVFXTagsShots, String> colTypeTblTagsShots;

    @FXML
    private TableColumn<IVFXTagsShots, String> colNameTblTagsShots;

    @FXML
    private TableColumn<IVFXTagsShots, Boolean> colIsMainTblTagsShots;

    @FXML
    private TableColumn<IVFXTagsShots, String> colTblTagsShotsSize;

    @FXML
    private TableColumn<IVFXTagsShots, String> colTblTagsShotsProba;

    @FXML
    private Button btnAddNewTagShot;


    @FXML
    private Button btnCopyFromStep1;

    @FXML
    private CheckBox cbToNextStep1;

    @FXML
    private TableView<IVFXTagsShots> tblTagsShotsStep1;

    @FXML
    private TableColumn<IVFXTagsShots, String> colTypeTblTagsShotsStep1;

    @FXML
    private TableColumn<IVFXTagsShots, String> colNameTblTagsShotsStep1;

    @FXML
    private TableColumn<IVFXTagsShots, Boolean> colIsMainTblTagsShotsStep1;

    @FXML
    private Button btnCopyFromStep2;

    @FXML
    private CheckBox cbToNextStep2;

    @FXML
    private TableView<IVFXTagsShots> tblTagsShotsStep2;

    @FXML
    private TableColumn<IVFXTagsShots, String> colTypeTblTagsShotsStep2;

    @FXML
    private TableColumn<IVFXTagsShots, String> colNameTblTagsShotsStep2;

    @FXML
    private TableColumn<IVFXTagsShots, Boolean> colIsMainTblTagsShotsStep2;

    @FXML
    private Button btnCopyFromStep3;

    @FXML
    private CheckBox cbToNextStep3;

    @FXML
    private TableView<IVFXTagsShots> tblTagsShotsStep3;

    @FXML
    private TableColumn<IVFXTagsShots, String> colTypeTblTagsShotsStep3;

    @FXML
    private TableColumn<IVFXTagsShots, String> colNameTblTagsShotsStep3;

    @FXML
    private TableColumn<IVFXTagsShots, Boolean> colIsMainTblTagsShotsStep3;

    @FXML
    private Button btnCopyFromStep4;

    @FXML
    private CheckBox cbToNextStep4;

    @FXML
    private TableView<IVFXTagsShots> tblTagsShotsStep4;

    @FXML
    private TableColumn<IVFXTagsShots, String> colTypeTblTagsShotsStep4;

    @FXML
    private TableColumn<IVFXTagsShots, String> colNameTblTagsShotsStep4;

    @FXML
    private TableColumn<IVFXTagsShots, Boolean> colIsMainTblTagsShotsStep4;

    @FXML
    private TableView<IVFXTagsShotsProperties> tblTagsShotsProperties;

    @FXML
    private TableColumn<IVFXTagsShotsProperties, String> colNameTblTagsShotsProperties;

    @FXML
    private TableColumn<IVFXTagsShotsProperties, String> colValueTblTagsShotsProperties;

    @FXML
    private TextField fldTagShotPropertyName;

    @FXML
    private TextArea fldTagShotPropertyValue;

    @FXML
    private Button btnAddNewTagsShotsProperties;

    @FXML
    private Button btnDeleteTagsProperties;

    @FXML
    private TableView<IVFXTagsScenes> tblScenes;

    @FXML
    private TableColumn<IVFXTagsScenes, String> colStartTblScenes;

    @FXML
    private TableColumn<IVFXTagsScenes, String> colNameTblScenes;
    
    @FXML
    private Button btnUnionScenes;

    @FXML
    private TableView<IVFXTagsProperties> tblSceneProperties;

    @FXML
    private TableColumn<IVFXTagsProperties, String> colNameTblSceneProperties;

    @FXML
    private TableColumn<IVFXTagsProperties, String> colValueTblSceneProperties;

    @FXML
    private TextField fldScenePropertyName;

    @FXML
    private TextArea fldScenePropertyValue;

    @FXML
    private Button btnAddNewSceneProperty;

    @FXML
    private Button btnDeleteSceneProperty;


    @FXML
    private TableView<IVFXTagsShots> tblSceneShots;

    @FXML
    private TableColumn<IVFXTagsShots, String> colShotFromTblSceneShots;

    @FXML
    private TableColumn<IVFXTagsShots, String> colShotToTblSceneShots;

    @FXML
    private Button btnSceneCutUp;

    @FXML
    private Button btnSceneCutDown;

    @FXML
    private TableView<IVFXTags> tblTagsScene;

    @FXML
    private TableColumn<IVFXTags, String> colTypeTblTagsScene;

    @FXML
    private TableColumn<IVFXTags, String> colNameTblTagsScene;

    
    @FXML
    private TableView<IVFXTagsEvents> tblEvents;

    @FXML
    private TableColumn<IVFXTagsEvents, String> colStartTblEvents;

    @FXML
    private TableColumn<IVFXTagsEvents, String> colNameTblEvents;

    @FXML
    private Button btnUnionEvents;

    @FXML
    private Button btnCreateNewEvent;

    @FXML
    private Button btnDeleteEvent;

    @FXML
    private TableView<IVFXTagsProperties> tblEventProperties;

    @FXML
    private TableColumn<IVFXTagsProperties, String> colNameTblEventProperties;

    @FXML
    private TableColumn<IVFXTagsProperties, String> colValueTblEventProperties;

    @FXML
    private TextField fldEventPropertyName;

    @FXML
    private TextArea fldEventPropertyValue;

    @FXML
    private Button btnAddNewEventProperty;

    @FXML
    private Button btnDeleteEventProperty;


    @FXML
    private TableView<IVFXTagsShots> tblEventShots;

    @FXML
    private TableColumn<IVFXTagsShots, String> colShotFromTblEventShots;

    @FXML
    private TableColumn<IVFXTagsShots, String> colShotToTblEventShots;

    @FXML
    private Button btnEventCutUp;

    @FXML
    private Button btnEventCutDown;

    @FXML
    private Button btnEventExtendUp;

    @FXML
    private Button btnEventExtendDown;

    @FXML
    private Button btnEventCollapseUp;

    @FXML
    private Button btnEventCollapseDown;

    @FXML
    private TableView<IVFXTags> tblTagsEvent;

    @FXML
    private TableColumn<IVFXTags, String> colTypeTblTagsEvent;

    @FXML
    private TableColumn<IVFXTags, String> colNameTblTagsEvent;

    @FXML
    private Button btnAddTagToEvent;

    @FXML
    private TableView<IVFXTags> tblTagsShotsEvent;

    @FXML
    private TableColumn<IVFXTags, String> colTypeTblTagsShotsEvent;

    @FXML
    private TableColumn<IVFXTags, String> colNameTblTagsShotsEvent;


    @FXML
    private Button btnOK;

    private static TagsShotsController tagsShotsController = new TagsShotsController();
    private Stage controllerStage;
    private Scene controllerScene;
    public static IVFXProjects currentProject;                                                                          // Текуший проект
    public static IVFXFiles currentFile;                                                                                // Текущий файл
    public static IVFXShots currentShotStep1;                                                                                // Текущий план
    public static IVFXShots currentShotStep2;                                                                                // Текущий план
    public static IVFXShots currentShotStep3;                                                                                // Текущий план
    public static IVFXShots currentShotStep4;                                                                                // Текущий план
    public static IVFXShots currentShot;                                                                                // Текущий план
    public static IVFXTagsShots currentSceneShot;                                                                                // Текущий план
    public static IVFXTagsShots currentEventShot;                                                                                // Текущий план
    public static IVFXTags currentScene;                                                                                // Текущий план
    public static IVFXTags currentEvent;                                                                                // Текущий план
    public static IVFXTagsShots currentTagShot;                                                                                // Текущий план
    public static IVFXTagsShots currentTagShotStep1;                                                                                // Текущий план
    public static IVFXTagsShots currentTagShotStep2;                                                                                // Текущий план
    public static IVFXTagsShots currentTagShotStep3;                                                                                // Текущий план
    public static IVFXTagsShots currentTagShotStep4;                                                                                // Текущий план
    public static IVFXTags currentTagAll;                                                                                // Текущий план
    public static IVFXTags currentTagEvent;                                                                                // Текущий план
    public static IVFXTags currentTagShotEvent;                                                                                // Текущий план
    public static IVFXTagsShotsProperties currentTagShotProperty;                                                                                // Текущий план
    public static IVFXTagsProperties currentSceneProperty;                                                                                // Текущий план
    public static IVFXTagsProperties currentEventProperty;                                                                                // Текущий план

    private TimerTask playFullPreviewThread = new PlayFullPreview();
    private boolean playFullPreviewIsPlaying = false;
    private int playFullPreviewDelta = 1;

    public Media mainMedia;
    public MediaPlayer mainMediaPlayer;

    private ObservableList<IVFXProjects> listProjects = FXCollections.observableArrayList();
    private ObservableList<IVFXFiles> listFiles = FXCollections.observableArrayList();
    private ObservableList<IVFXShots> listShots = FXCollections.observableArrayList();
    private ObservableList<IVFXTagsShots> listSceneShots = FXCollections.observableArrayList();
    private ObservableList<IVFXTagsShots> listEventShots = FXCollections.observableArrayList();
    private ObservableList<IVFXTagsShots> listTagsShots = FXCollections.observableArrayList();
    private ObservableList<IVFXTagsScenes> listScenes = FXCollections.observableArrayList();
    private ObservableList<IVFXTagsEvents> listEvents = FXCollections.observableArrayList();
    private ObservableList<IVFXTagsShots> listTagsShotsStep1 = FXCollections.observableArrayList();
    private ObservableList<IVFXTagsShots> listTagsShotsStep2 = FXCollections.observableArrayList();
    private ObservableList<IVFXTagsShots> listTagsShotsStep3 = FXCollections.observableArrayList();
    private ObservableList<IVFXTagsShots> listTagsShotsStep4 = FXCollections.observableArrayList();
    private ObservableList<IVFXShotsTypeSize> listShotsTypeSize = FXCollections.observableArrayList();
    private ObservableList<IVFXShotsTypePersons> listShotsTypePersons = FXCollections.observableArrayList();
    private ObservableList<IVFXTags> listTagsAll = FXCollections.observableArrayList();
    private ObservableList<IVFXTags> listTagsScene = FXCollections.observableArrayList();
    private ObservableList<IVFXTags> listTagsEvent = FXCollections.observableArrayList();
    private ObservableList<IVFXTags> listTagsShotsEvent = FXCollections.observableArrayList();
    private ObservableList<IVFXTagsShotsProperties> listTagsShotsProperties = FXCollections.observableArrayList();
    private ObservableList<IVFXTagsProperties> listSceneProperties = FXCollections.observableArrayList();
    private ObservableList<IVFXTagsProperties> listEventProperties = FXCollections.observableArrayList();

    private String fxBorderDefault = "-fx-border-color:#0f0f0f;-fx-border-width:1";  // стиль бордюра лейбла по-умолчаснию
    private String fxBorderNone = "-fx-border-color:NONE;-fx-border-width:1";
    private String fxBorderYellow = "-fx-border-color:YELLOW;-fx-border-width:1";
    private String fxBorderMagenta = "-fx-border-color:MAGENTA;-fx-border-width:1";
    private String fxBorderRed = "-fx-border-color:RED;-fx-border-width:1";

    private VirtualFlow flowTblShots;
    private VirtualFlow flowTblSceneShots;
    private VirtualFlow flowTblEventShots;

    private final static int COLUMNS_COUNT = 5;
    private final static int ROWS_COUNT = 4;
    private final static int PICTURE_WIDTH = 135;
    private final static int PICTURE_HEIGHT = 75;
    private List<MatrixLabels> listLabels = new ArrayList<>();
    private List<Label> listLabelsPreview = new ArrayList<>();
    private int currentFrameToScroll = 0;
    private MatrixLabels prevMatrixLabel = null;
    private String mainFramePreviewFile;
    private String mainFrameFullSizeFile;
    private boolean updateFullPreviewDuringMoveMouse = true;

    private PlayerLoopNextStop playerLoopNextStop = PlayerLoopNextStop.LOOP;
    private boolean playerIsEndOfMedia = false;
    private double playerSpeed = 1.0;

    private VirtualFlow flowTblTagsAll;
    private FilteredList<IVFXTags> filteredTagsAll;

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


    class PlayFullPreview extends TimerTask {

        @Override
        public void run() {

            if (playFullPreviewIsPlaying && currentShot != null) {

                int firstFrameNumber = currentShot.getFirstFrameNumber(); // Номер первого кардра
                int lastFrameNumber = currentShot.getLastFrameNumber();   // Номер последнего кадра
                int countLabels = COLUMNS_COUNT * ROWS_COUNT;             // Количество лейблов в пэйне
                int countFrames = lastFrameNumber - firstFrameNumber;      // Количество кадров в плане
                currentFrameToScroll += playFullPreviewDelta;
                if (currentFrameToScroll < firstFrameNumber) {
                    currentFrameToScroll = firstFrameNumber;
                    playFullPreviewDelta *= -1;
                }
                if (currentFrameToScroll > lastFrameNumber) {
                    currentFrameToScroll = lastFrameNumber;
                    playFullPreviewDelta *= -1;
                }
                MatrixLabels matrixLabels = null;
                for (MatrixLabels temp: listLabels) {
                    if (temp.frameNumber >= currentFrameToScroll) {
                        matrixLabels = temp;
                        break;
                    }
                }

                if (matrixLabels != null) {
                    if (prevMatrixLabel != null) prevMatrixLabel.label.setStyle(fxBorderDefault);
                    MatrixLabels finalMatrixLabels = matrixLabels;
                    Platform.runLater(()->{
                        finalMatrixLabels.label.setStyle(fxBorderMagenta);
                        finalMatrixLabels.label.toFront();
                    });

                    prevMatrixLabel = matrixLabels;
                }

                String fileName = currentFile.getFolderFramesPreview() + "\\" + currentFile.getShortName() + currentFile.FRAMES_PREFIX + String.format("%06d", currentFrameToScroll) + ".jpg";
                String fileNameMediumSize = currentFile.getFolderFramesMedium() + "\\" + currentFile.getShortName() + currentFile.FRAMES_PREFIX + String.format("%06d", currentFrameToScroll) + ".jpg";
                onMouseClickLabel(fileName, fileNameMediumSize, lblFullSizePicture);

            }


        }
    }


    @FXML
    void initialize() {

        tblScenes.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        tblEvents.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        /*********************
         * Дисейблим контролы
         *********************/

        // Планы
        tblShots.setDisable(true);
//        btnUpdateShotTagsByFaces.setDisable(true);

        // Сцены, планы сцен, персонажи сцен
        tblScenes.setDisable(true);
        tblSceneShots.setDisable(true);
        tblTagsScene.setDisable(true);
        btnUnionScenes.setDisable(true);
        btnSceneCutUp.setDisable(true);
        btnSceneCutDown.setDisable(true);

        // Свойства сцен
        tblSceneProperties.setDisable(true);
        fldScenePropertyName.setDisable(true);
        fldScenePropertyValue.setDisable(true);
        btnAddNewSceneProperty.setDisable(true);
        btnDeleteSceneProperty.setDisable(true);

        // События, планы событий, персонажи событий
        tblEvents.setDisable(true);
        tblEventShots.setDisable(true);
        tblTagsEvent.setDisable(true);
        tblTagsShotsEvent.setDisable(true);
        btnAddTagToEvent.setDisable(true);
        btnUnionEvents.setDisable(true);
        btnEventCutUp.setDisable(true);
        btnEventCutDown.setDisable(true);
        btnEventExtendUp.setDisable(true);
        btnEventExtendDown.setDisable(true);
        btnEventCollapseUp.setDisable(true);
        btnEventCollapseDown.setDisable(true);
        btnCreateNewEvent.setDisable(true);
        btnDeleteEvent.setDisable(true);

        // Свойства событий
        tblEventProperties.setDisable(true);
        fldEventPropertyName.setDisable(true);
        fldEventPropertyValue.setDisable(true);
        btnAddNewEventProperty.setDisable(true);
        btnDeleteEventProperty.setDisable(true);

        
        // Типы планов по размеру и персонажам
        tblShotsTypesSize.setDisable(true);
        tblShotsTypesPersons.setDisable(true);

        // Персонажи и объекты
        fldFindTagsAll.setDisable(true);
        chFindTagsAllInProperties.setDisable(true);
        cbToNextStep1.setDisable(true);
        cbToNextStep2.setDisable(true);
        cbToNextStep3.setDisable(true);
        cbToNextStep4.setDisable(true);
        btnCopyFromStep1.setDisable(true);
        btnCopyFromStep2.setDisable(true);
        btnCopyFromStep3.setDisable(true);
        btnCopyFromStep4.setDisable(true);
        btnAddNewTagShot.setDisable(true);

        // Свойства персонажей и объектов
        tblTagsShotsProperties.setDisable(true);
        fldTagShotPropertyName.setDisable(true);
        fldTagShotPropertyValue.setDisable(true);
        btnAddNewTagsShotsProperties.setDisable(true);
        btnDeleteTagsProperties.setDisable(true);


        /*********************
         * Настраиваем таймер
         *********************/

        Timer timer = new Timer(true);
        try {
            timer.scheduleAtFixedRate(playFullPreviewThread, 0, 1);
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }


        listProjects = FXCollections.observableArrayList(IVFXProjects.loadList());
        cbProject.setItems(listProjects);
        if (currentProject != null) {
            cbProject.getSelectionModel().select(currentProject);
        }

        listShotsTypeSize = FXCollections.observableArrayList(IVFXShotsTypeSize.loadList(true));
        tblShotsTypesSize.setItems(listShotsTypeSize);

        listShotsTypePersons = FXCollections.observableArrayList(IVFXShotsTypePersons.loadList(true));
        tblShotsTypesPersons.setItems(listShotsTypePersons);

        // Инициализация превью-лейблов
        gpPreview.getChildren().clear();
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

                gpPreview.add(label, iCol, iRow);
            }
        }

        /****************************
         * Настройка столбцов таблиц
         ****************************/

        // столбцы таблицы tblShots
        colShotFrom.setCellValueFactory(new PropertyValueFactory<>("labelFirst1"));
        colShotTo.setCellValueFactory(new PropertyValueFactory<>("labelLast1"));

        // столбцы таблицы tblSceneShots
        colShotFromTblSceneShots.setCellValueFactory(new PropertyValueFactory<>("shotLabelFirst1"));
        colShotToTblSceneShots.setCellValueFactory(new PropertyValueFactory<>("shotLabelLast1"));

        // столбцы таблицы tblEventShots
        colShotFromTblEventShots.setCellValueFactory(new PropertyValueFactory<>("shotLabelFirst1"));
        colShotToTblEventShots.setCellValueFactory(new PropertyValueFactory<>("shotLabelLast1"));

        // столбцы таблицы tblTagsShots
        colTypeTblTagsShots.setCellValueFactory(new PropertyValueFactory<>("enumTagTypeLetter"));
        colNameTblTagsShots.setCellValueFactory(new PropertyValueFactory<>("tagLabel1"));
        colTblTagsShotsSize.setCellValueFactory(new PropertyValueFactory<>("typeSizeLabel1"));
        colTblTagsShotsProba.setCellValueFactory(new PropertyValueFactory<>("probaText"));
        colIsMainTblTagsShots.setCellValueFactory(param -> param.getValue().isMainProperty());
        colIsMainTblTagsShots.setCellFactory(p -> {
            CheckBox checkBox = new CheckBox();
            TableCell<IVFXTagsShots, Boolean> cell = new TableCell<IVFXTagsShots, Boolean>() {
                @Override
                public void updateItem(Boolean item, boolean empty) {
                    if (empty) {
                        setGraphic(null);
                    } else {
                        checkBox.setSelected(item);
                        setGraphic(checkBox);
                    }
                }
            };
            checkBox.selectedProperty().addListener((obs, wasSelected, isSelected) ->
            {
                if ((cell.getTableRow().getItem()) != null) {
                    ((IVFXTagsShots)cell.getTableRow().getItem()).setMain(isSelected);
                    ((IVFXTagsShots)cell.getTableRow().getItem()).save();
                }
            });
            cell.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            cell.setAlignment(Pos.CENTER);
            return cell ;
        });
        tblTagsShots.setEditable(true);

        // столбцы таблицы tblScenes
        colStartTblScenes.setCellValueFactory(new PropertyValueFactory<>("start"));
        colNameTblScenes.setCellValueFactory(new PropertyValueFactory<>("name"));

        // столбцы таблицы tblEvents
        colStartTblEvents.setCellValueFactory(new PropertyValueFactory<>("start"));
        colNameTblEvents.setCellValueFactory(new PropertyValueFactory<>("name"));

        
        // столбцы таблицы tblTagsShotsStep1
        colTypeTblTagsShotsStep1.setCellValueFactory(new PropertyValueFactory<>("enumTagTypeLetter"));
        colNameTblTagsShotsStep1.setCellValueFactory(new PropertyValueFactory<>("tagLabel1"));
        colIsMainTblTagsShotsStep1.setCellValueFactory(param -> param.getValue().isMainProperty());
        colIsMainTblTagsShotsStep1.setCellFactory(p -> {
            CheckBox checkBox = new CheckBox();
            TableCell<IVFXTagsShots, Boolean> cell = new TableCell<IVFXTagsShots, Boolean>() {
                @Override
                public void updateItem(Boolean item, boolean empty) {
                    if (empty) {
                        setGraphic(null);
                    } else {
                        checkBox.setSelected(item);
                        setGraphic(checkBox);
                    }
                }
            };
            checkBox.selectedProperty().addListener((obs, wasSelected, isSelected) ->
            {
                if ((cell.getTableRow().getItem()) != null) {
                    ((IVFXTagsShots)cell.getTableRow().getItem()).setMain(isSelected);
                    ((IVFXTagsShots)cell.getTableRow().getItem()).save();
                }
            });
            cell.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            cell.setAlignment(Pos.CENTER);
            checkBox.setDisable(true);
            return cell ;
        });
        tblTagsShotsStep1.setEditable(false);

        // столбцы таблицы tblTagsShotsStep2
        colTypeTblTagsShotsStep2.setCellValueFactory(new PropertyValueFactory<>("enumTagTypeLetter"));
        colNameTblTagsShotsStep2.setCellValueFactory(new PropertyValueFactory<>("tagLabel1"));
        colIsMainTblTagsShotsStep2.setCellValueFactory(param -> param.getValue().isMainProperty());
        colIsMainTblTagsShotsStep2.setCellFactory(p -> {
            CheckBox checkBox = new CheckBox();
            TableCell<IVFXTagsShots, Boolean> cell = new TableCell<IVFXTagsShots, Boolean>() {
                @Override
                public void updateItem(Boolean item, boolean empty) {
                    if (empty) {
                        setGraphic(null);
                    } else {
                        checkBox.setSelected(item);
                        setGraphic(checkBox);
                    }
                }
            };
            checkBox.selectedProperty().addListener((obs, wasSelected, isSelected) ->
            {
                if ((cell.getTableRow().getItem()) != null) {
                    ((IVFXTagsShots)cell.getTableRow().getItem()).setMain(isSelected);
                    ((IVFXTagsShots)cell.getTableRow().getItem()).save();
                }
            });
            cell.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            cell.setAlignment(Pos.CENTER);
            checkBox.setDisable(true);
            return cell ;
        });
        tblTagsShotsStep2.setEditable(false);

        // столбцы таблицы tblTagsShotsStep3
        colTypeTblTagsShotsStep3.setCellValueFactory(new PropertyValueFactory<>("enumTagTypeLetter"));
        colNameTblTagsShotsStep3.setCellValueFactory(new PropertyValueFactory<>("tagLabel1"));
        colIsMainTblTagsShotsStep3.setCellValueFactory(param -> param.getValue().isMainProperty());
        colIsMainTblTagsShotsStep3.setCellFactory(p -> {
            CheckBox checkBox = new CheckBox();
            TableCell<IVFXTagsShots, Boolean> cell = new TableCell<IVFXTagsShots, Boolean>() {
                @Override
                public void updateItem(Boolean item, boolean empty) {
                    if (empty) {
                        setGraphic(null);
                    } else {
                        checkBox.setSelected(item);
                        setGraphic(checkBox);
                    }
                }
            };
            checkBox.selectedProperty().addListener((obs, wasSelected, isSelected) ->
            {
                if ((cell.getTableRow().getItem()) != null) {
                    ((IVFXTagsShots)cell.getTableRow().getItem()).setMain(isSelected);
                    ((IVFXTagsShots)cell.getTableRow().getItem()).save();
                }
            });
            cell.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            cell.setAlignment(Pos.CENTER);
            checkBox.setDisable(true);
            return cell ;
        });
        tblTagsShotsStep3.setEditable(false);

        // столбцы таблицы tblTagsShotsStep4
        colTypeTblTagsShotsStep4.setCellValueFactory(new PropertyValueFactory<>("enumTagTypeLetter"));
        colNameTblTagsShotsStep4.setCellValueFactory(new PropertyValueFactory<>("tagLabel1"));
        colIsMainTblTagsShotsStep4.setCellValueFactory(param -> param.getValue().isMainProperty());
        colIsMainTblTagsShotsStep4.setCellFactory(p -> {
            CheckBox checkBox = new CheckBox();
            TableCell<IVFXTagsShots, Boolean> cell = new TableCell<IVFXTagsShots, Boolean>() {
                @Override
                public void updateItem(Boolean item, boolean empty) {
                    if (empty) {
                        setGraphic(null);
                    } else {
                        checkBox.setSelected(item);
                        setGraphic(checkBox);
                    }
                }
            };
            checkBox.selectedProperty().addListener((obs, wasSelected, isSelected) ->
            {
                if ((cell.getTableRow().getItem()) != null) {
                    ((IVFXTagsShots)cell.getTableRow().getItem()).setMain(isSelected);
                    ((IVFXTagsShots)cell.getTableRow().getItem()).save();
                }
            });
            cell.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            cell.setAlignment(Pos.CENTER);
            checkBox.setDisable(true);
            return cell ;
        });
        tblTagsShotsStep4.setEditable(false);
        
        // столбцы таблицы tblShotsTypesSize
        colNameTblShotsTypesSize.setCellValueFactory(new PropertyValueFactory<>("preview1"));

        // столбцы таблицы tblShotsTypesPersons
        colNameTblShotsTypesPersons.setCellValueFactory(new PropertyValueFactory<>("preview1"));

        // столбцы таблицы tblTagsAll
        colTypeTblTagsAll.setCellValueFactory(new PropertyValueFactory<>("tagTypeLetter"));
        colNameTblTagsAll.setCellValueFactory(new PropertyValueFactory<>("label1"));

        // столбцы таблицы tblTagsScene
        colTypeTblTagsScene.setCellValueFactory(new PropertyValueFactory<>("tagTypeLetter"));
        colNameTblTagsScene.setCellValueFactory(new PropertyValueFactory<>("label1"));

        // столбцы таблицы tblTagsEvent
        colTypeTblTagsEvent.setCellValueFactory(new PropertyValueFactory<>("tagTypeLetter"));
        colNameTblTagsEvent.setCellValueFactory(new PropertyValueFactory<>("label1"));

        // столбцы таблицы tblTagsShotsEvent
        colTypeTblTagsShotsEvent.setCellValueFactory(new PropertyValueFactory<>("tagTypeLetter"));
        colNameTblTagsShotsEvent.setCellValueFactory(new PropertyValueFactory<>("label1"));


        // инициализируем поля таблицы tblTagsShotsProperties
        colNameTblTagsShotsProperties.setCellValueFactory(new PropertyValueFactory<>("name"));
        colValueTblTagsShotsProperties.setCellValueFactory(new PropertyValueFactory<>("value"));

        // инициализируем поля таблицы tblSceneProperties
        colNameTblSceneProperties.setCellValueFactory(new PropertyValueFactory<>("name"));
        colValueTblSceneProperties.setCellValueFactory(new PropertyValueFactory<>("value"));

        // инициализируем поля таблицы tblEventProperties
        colNameTblEventProperties.setCellValueFactory(new PropertyValueFactory<>("name"));
        colValueTblEventProperties.setCellValueFactory(new PropertyValueFactory<>("value"));

        
        // делаем поле Value таблицы tblTagsShotsProperties с переносом по словам и расширяемым по высоте
        colValueTblTagsShotsProperties.setCellFactory(param -> {
            TableCell<IVFXTagsShotsProperties, String> cell = new TableCell<>();
            Text text = new Text();
            text.setStyle("");
            cell.setGraphic(text);
            cell.setPrefHeight(Control.USE_COMPUTED_SIZE);
            text.textProperty().bind(cell.itemProperty());
            text.wrappingWidthProperty().bind(colValueTblTagsShotsProperties.widthProperty());
            return cell;
        });


        /*************************************
         * Скрытие заголовков столбцов таблиц
         *************************************/

        // скрываем заголовок у таблицы tblShots
        tblShots.widthProperty().addListener((source,oldWidth,newWidth) -> {
            Pane header = (Pane) tblShots.lookup("TableHeaderRow");
            if (header.isVisible()){
                header.setMaxHeight(0);
                header.setMinHeight(0);
                header.setPrefHeight(0);
                header.setVisible(false);
            }
        });

        // скрываем заголовок у таблицы tblShotsTypesSize
        tblShotsTypesSize.widthProperty().addListener((source,oldWidth,newWidth) -> {
            Pane header = (Pane) tblShotsTypesSize.lookup("TableHeaderRow");
            if (header.isVisible()){
                header.setMaxHeight(0);
                header.setMinHeight(0);
                header.setPrefHeight(0);
                header.setVisible(false);
            }
        });

        // скрываем заголовок у таблицы tblShotsTypesSize
        tblShotsTypesPersons.widthProperty().addListener((source,oldWidth,newWidth) -> {
            Pane header = (Pane) tblShotsTypesPersons.lookup("TableHeaderRow");
            if (header.isVisible()){
                header.setMaxHeight(0);
                header.setMinHeight(0);
                header.setPrefHeight(0);
                header.setVisible(false);
            }
        });

        // скрываем заголовок у таблицы tblTagsShots
        tblTagsShots.widthProperty().addListener((source,oldWidth,newWidth) -> {
            Pane header = (Pane) tblTagsShots.lookup("TableHeaderRow");
            if (header.isVisible()){
                header.setMaxHeight(0);
                header.setMinHeight(0);
                header.setPrefHeight(0);
                header.setVisible(false);
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

        /************************************************
         * Обработчики событий - События изменения полей
         ************************************************/

        // обработка события изменение поля fldTagShotPropertyValue
        fldTagShotPropertyValue.textProperty().addListener((observable, oldValue, newValue) -> {
            doFldTagShotPropertyValue();
        });

        // обработка события изменение поля fldScenePropertyValue
        fldScenePropertyValue.textProperty().addListener((observable, oldValue, newValue) -> {
            doFldScenePropertyValue();
        });


        // обработка события изменение поля fldEventPropertyValue
        fldEventPropertyValue.textProperty().addListener((observable, oldValue, newValue) -> {
            doFldEventPropertyValue();
        });

        
        /*************************************************************************
         * Обработчики событий - События отслеживания видимости записей на экране
         *************************************************************************/

        // событие отслеживани видимости на экране текущей записи в таблице tblShots
        tblShots.skinProperty().addListener((ChangeListener<Skin>) (ov, t, t1) -> {
            if (t1 == null) return;
            TableViewSkin tvs = (TableViewSkin) t1;
            ObservableList<Node> kids = tvs.getChildren();
            if (kids == null || kids.isEmpty()) return;
            flowTblShots = (VirtualFlow) kids.get(1);
        });

        // событие отслеживани видимости на экране текущей записи в таблице tblSceneShots
        tblSceneShots.skinProperty().addListener((ChangeListener<Skin>) (ov, t, t1) -> {
            if (t1 == null) return;
            TableViewSkin tvs = (TableViewSkin) t1;
            ObservableList<Node> kids = tvs.getChildren();
            if (kids == null || kids.isEmpty()) return;
            flowTblSceneShots = (VirtualFlow) kids.get(1);
        });

        // событие отслеживани видимости на экране текущей записи в таблице tblEventShots
        tblEventShots.skinProperty().addListener((ChangeListener<Skin>) (ov, t, t1) -> {
            if (t1 == null) return;
            TableViewSkin tvs = (TableViewSkin) t1;
            ObservableList<Node> kids = tvs.getChildren();
            if (kids == null || kids.isEmpty()) return;
            flowTblEventShots = (VirtualFlow) kids.get(1);
        });
        
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


        /**********************************************************
         * Обработчики событий - События выбора записей в таблицах
         ***********************************************************/

        // событие выбора записи в комбобоксе cbProject
        cbProject.getSelectionModel().selectedItemProperty().addListener((v, oldValue, newValue) -> {
            if (newValue != null) {
                IVFXProjects selectedProject = (IVFXProjects) newValue;
                if (currentProject == null || !currentProject.equals(selectedProject)) {
                    currentProject = selectedProject;
                    currentFile = null;
                    doOnSelectRecordInCbProjects();
                }
            }
        });

        // событие выбора записи в комбобоксе cbFiles
        cbFiles.getSelectionModel().selectedItemProperty().addListener((v, oldValue, newValue) -> {
            if (newValue != null) {
                IVFXFiles selectedFile = (IVFXFiles) newValue;
                if (currentFile == null || !currentFile.isEqual(selectedFile)) {
                    currentFile = selectedFile;
                    btnUpdateShotTagsByFaces.setDisable(false);
                    currentShot = null;
                    System.out.println("Выбран файл: " + currentFile.getTitle());
                    int[] arr = {1,2};
                    if (cbTagsAllOnlyFromHtml.isSelected() && currentFile != null) {
                        listTagsAll = FXCollections.observableArrayList(currentFile.getListTagsFromHTML());
                        tblTagsAll.setItems(listTagsAll);
                    }

                    doOnSelectRecordInCbFiles();


                }
            }
        });

        // событие выбора записи в таблице tblShots
        tblShots.getSelectionModel().selectedItemProperty().addListener((v, oldValue, newValue) -> {
            if (newValue != null) {

                currentShot = newValue;  // устанавливаем выбранное значение в таблице как текущий план

                // Инициализируем лейблы превью
                initializeLabelsPreview();
                tblShotsSmartScrollToCurrent();

                currentFrameToScroll = currentShot.getFirstFrameNumber();
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

                doOnSelectRecordInTblShots();

            }
        });

        // событие выбора записи в таблице tblTagsAll
        tblTagsAll.getSelectionModel().selectedItemProperty().addListener((v, oldValue, newValue) -> {
            if (newValue != null) {

                currentTagAll = newValue;  // устанавливаем выбранное значение в таблице как текущий план

            }
        });

        // событие выбора записи в таблице tblTagsShots
        tblTagsShots.getSelectionModel().selectedItemProperty().addListener((v, oldValue, newValue) -> {
            if (newValue != null) {

                currentTagShot = newValue;  // устанавливаем выбранное значение в таблице как текущий план

                doOnSelectRecordInTblTagsShots();

            }
        });

        // событие выбора записи в таблице tblShotsTypesSize
        tblShotsTypesSize.getSelectionModel().selectedItemProperty().addListener((v, oldValue, newValue) -> {
            if (newValue != null) {
                currentShot.setShotsTypeSizeId(newValue.getId());
                currentShot.setIvfxShotsTypeSize(newValue);
                currentShot.save();
            }
        });

        // событие выбора записи в таблице tblShotsTypesSize
        tblShotsTypesPersons.getSelectionModel().selectedItemProperty().addListener((v, oldValue, newValue) -> {
            if (newValue != null) {
                currentShot.setShotsTypePersonId(newValue.getId());
                currentShot.setIvfxShotsTypePersons(newValue);
                currentShot.save();
            }
        });

        // событие выбора записи в таблице tblTagsShotsProperties
        tblTagsShotsProperties.getSelectionModel().selectedItemProperty().addListener((v, oldValue, newValue) -> {
            if (newValue != null) {
                currentTagShotProperty = newValue;  // устанавливаем выбранное значение в таблице как текущий план
                doOnSelectRecordInTblTagsShotsProperties();
            }
        });

        // событие выбора записи в таблице tblTagsShotsStep1
        tblTagsShotsStep1.getSelectionModel().selectedItemProperty().addListener((v, oldValue, newValue) -> {
            if (newValue != null) {
                currentTagShotStep1 = newValue;  // устанавливаем выбранное значение в таблице как текущий план
            }
        });

        // событие выбора записи в таблице tblTagsShotsStep2
        tblTagsShotsStep2.getSelectionModel().selectedItemProperty().addListener((v, oldValue, newValue) -> {
            if (newValue != null) {
                currentTagShotStep2 = newValue;  // устанавливаем выбранное значение в таблице как текущий план
            }
        });

        // событие выбора записи в таблице tblTagsShotsStep1
        tblTagsShotsStep3.getSelectionModel().selectedItemProperty().addListener((v, oldValue, newValue) -> {
            if (newValue != null) {
                currentTagShotStep3 = newValue;  // устанавливаем выбранное значение в таблице как текущий план
            }
        });

        // событие выбора записи в таблице tblTagsShotsStep1
        tblTagsShotsStep4.getSelectionModel().selectedItemProperty().addListener((v, oldValue, newValue) -> {
            if (newValue != null) {
                currentTagShotStep4 = newValue;  // устанавливаем выбранное значение в таблице как текущий план
            }
        });

        // событие выбора записи в таблице tblScenes
        tblScenes.getSelectionModel().selectedItemProperty().addListener((v, oldValue, newValue) -> {
            if (newValue != null) {
                List<IVFXTagsScenes> listSelected = tblScenes.getSelectionModel().getSelectedItems();
                if (listSelected.size() == 1) {
                    currentScene = listSelected.get(0);
                } else {
                    currentScene = null;
                }
                doOnSelectRecordInTblScenes();
            }
        });

        // событие выбора записи в таблице tblSceneProperties
        tblSceneProperties.getSelectionModel().selectedItemProperty().addListener((v, oldValue, newValue) -> {
            if (newValue != null) {
                currentSceneProperty = newValue;
                doOnSelectRecordInTblSceneProperties();
            }
        });

        // событие выбора записи в таблице tblSceneShots
        tblSceneShots.getSelectionModel().selectedItemProperty().addListener((v, oldValue, newValue) -> {
            if (newValue != null) {
                if (currentSceneShot == null || !currentSceneShot.isEqual(newValue)) {
                    currentSceneShot = newValue;
                    doOnSelectRecordInTblSceneShots();
                }
            }
        });

        // событие выбора записи в таблице tblEventProperties
        tblEventProperties.getSelectionModel().selectedItemProperty().addListener((v, oldValue, newValue) -> {
            if (newValue != null) {
                currentEventProperty = newValue;
                doOnSelectRecordInTblEventProperties();
            }
        });

        // событие выбора записи в таблице tblEvents
        tblEvents.getSelectionModel().selectedItemProperty().addListener((v, oldValue, newValue) -> {
            if (newValue != null) {
                List<IVFXTagsEvents> listSelected = tblEvents.getSelectionModel().getSelectedItems();
                if (listSelected.size() == 1) {
                    currentEvent = listSelected.get(0);
                } else {
                    currentEvent = null;
                }
                doOnSelectRecordInTblEvents();
            }
        });

        // событие выбора записи в таблице tblEventShots
        tblEventShots.getSelectionModel().selectedItemProperty().addListener((v, oldValue, newValue) -> {
            if (newValue != null) {
                if (currentEventShot == null || !currentEventShot.isEqual(newValue)) {
                    currentEventShot = newValue;
                    doOnSelectRecordInTblEventShots();
                }
            }
        });

        // событие выбора записи в таблице tblEventProperties
        tblEventProperties.getSelectionModel().selectedItemProperty().addListener((v, oldValue, newValue) -> {
            if (newValue != null) {
                currentEventProperty = newValue;
                doOnSelectRecordInTblEventProperties();
            }
        });

        // событие выбора записи в таблице tblTagsEvent
        tblTagsEvent.getSelectionModel().selectedItemProperty().addListener((v, oldValue, newValue) -> {
            if (newValue != null) {
                currentTagEvent = newValue;
            }
        });

        // событие выбора записи в таблице tblTagsShotsEvent
        tblTagsShotsEvent.getSelectionModel().selectedItemProperty().addListener((v, oldValue, newValue) -> {
            if (newValue != null) {
                currentTagShotEvent = newValue;
            }
        });

        /***********************************************
         * Обработчики событий - События двойного клика
         ************************************************/

        // событие двойного клика в таблице tblTagsAll
        tblTagsAll.setOnMouseClicked(mouseEvent -> {
            if (mouseEvent.getButton().equals(MouseButton.PRIMARY) && mouseEvent.getClickCount() == 2) {
                addTagToTagsShots(currentTagAll, currentShot);
            }
        });

        // событие двойного клика в таблице tblTagsShots
        tblTagsShots.setOnMouseClicked(mouseEvent -> {
            if (mouseEvent.getButton().equals(MouseButton.PRIMARY) && mouseEvent.getClickCount() == 2) {
                doOnDeleteTagShot(currentTagShot);
            }
        });

        // событие двойного клика в таблице tblTagsShotsStep1
        tblTagsShotsStep1.setOnMouseClicked(mouseEvent -> {
            if (mouseEvent.getButton().equals(MouseButton.PRIMARY) && mouseEvent.getClickCount() == 2) {
                if (currentTagShotStep1 != null && currentShot != null) {
                    currentTagShotStep1.copyTo(currentShot, true, false);
                    doOnSelectRecordInTblShots();
                }
            }
        });

        // событие двойного клика в таблице tblTagsShotsStep2
        tblTagsShotsStep2.setOnMouseClicked(mouseEvent -> {
            if (mouseEvent.getButton().equals(MouseButton.PRIMARY) && mouseEvent.getClickCount() == 2) {
                if (currentTagShotStep2 != null && currentShot != null) {
                    currentTagShotStep2.copyTo(currentShot, true, false);
                    doOnSelectRecordInTblShots();
                }
            }
        });

        // событие двойного клика в таблице tblTagsShotsStep3
        tblTagsShotsStep3.setOnMouseClicked(mouseEvent -> {
            if (mouseEvent.getButton().equals(MouseButton.PRIMARY) && mouseEvent.getClickCount() == 2) {
                if (currentTagShotStep3 != null && currentShot != null) {
                    currentTagShotStep3.copyTo(currentShot, true, false);
                    doOnSelectRecordInTblShots();
                }
            }
        });

        // событие двойного клика в таблице tblTagsShotsStep4
        tblTagsShotsStep4.setOnMouseClicked(mouseEvent -> {
            if (mouseEvent.getButton().equals(MouseButton.PRIMARY) && mouseEvent.getClickCount() == 2) {
                if (currentTagShotStep4 != null && currentShot != null) {
                    currentTagShotStep4.copyTo(currentShot, true, false);
                    doOnSelectRecordInTblShots();
                }
            }
        });

        // событие двойного клика в таблице tblTagsEvent - удаление записи из таблицы tblTagsEvent
        tblTagsEvent.setOnMouseClicked(mouseEvent -> {
            if (mouseEvent.getButton().equals(MouseButton.PRIMARY) && mouseEvent.getClickCount() == 2) {
                if (currentTagEvent != null && currentEvent != null) {
                    List<IVFXTagsTags> listTagsTags = IVFXTagsTags.loadList(currentEvent,false);
                    if (listTagsTags != null) {
                        for (IVFXTagsTags tagTag: listTagsTags) {
                            if (tagTag.getIvfxTagChild().isEqual(currentTagEvent)) {
                                tagTag.delete();
                                currentTagEvent = null;
                                doOnSelectRecordInTblEvents();
                                break;
                            }
                        }
                    }
                }
            }
        });

        // событие двойного клика в таблице tblTagsShotsEvent - добавление записи в таблицу tblTagsEvent
        tblTagsShotsEvent.setOnMouseClicked(mouseEvent -> {
            if (mouseEvent.getButton().equals(MouseButton.PRIMARY) && mouseEvent.getClickCount() == 2) {
                if (currentTagShotEvent != null && currentEvent != null) {
                    List<IVFXTagsTags> listTagsTags = IVFXTagsTags.loadList(currentEvent,false);
                    if (listTagsTags != null) {
                        boolean isAlreadyPresent = false;
                        for (IVFXTagsTags tagTag: listTagsTags) {
                            if (tagTag.getIvfxTagChild().isEqual(currentTagShotEvent)) {
                                isAlreadyPresent = true;
                                currentTagEvent = null;

                            }
                        }
                        if (!isAlreadyPresent) {
                            IVFXTagsTags.getNewDbInstance(currentEvent, currentTagShotEvent,true);
                            doOnSelectRecordInTblEvents();
                        }
                    }
                }
            }
        });


        /****************************************
         * Обработчики событий - Прочие события
         ****************************************/

        // Обработка события прокрутки колеса мыши над большой картинкой
        lblFullSizePicture.setOnScroll(e->{

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
            String fileNameMediumSize = currentFile.getFolderFramesMedium() + "\\" + currentFile.getShortName() + currentFile.FRAMES_PREFIX + String.format("%06d", currentFrameToScroll) + ".jpg";
            onMouseClickLabel(fileName, fileNameMediumSize, lblFullSizePicture);

        });

        // обработка события клика на большой картинке
        lblFullSizePicture.setOnMouseClicked(mouseEvent -> {
            if (mouseEvent.getButton().equals(MouseButton.PRIMARY) && mouseEvent.getClickCount() == 1) {
                playFullPreviewIsPlaying = !playFullPreviewIsPlaying;
            }
        });

        // ГОРЯЧИЕ КЛАВИШИ
        mainPane.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.F7) goToPreviousShot();
            if (e.getCode() == KeyCode.F8) goToNextShot();
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
            if (ke.getCode() == KeyCode.F7) goToPreviousShot();
            if (ke.getCode() == KeyCode.F8) goToNextShot();
        });

        // нажатие Enter в поле в таблице tblTagsAll
        tblTagsAll.setOnKeyPressed(ke -> {
            if (ke.getCode().equals(KeyCode.ENTER))
            {
                addTagToTagsShots(currentTagAll, currentShot);
                fldFindTagsAll.requestFocus();
                fldFindTagsAll.setText("");
            }
        });

        initContextMenuFrame();
        initContextMenuTagShotTypeSize();
        initContextMenuTagShotTypeSizeForAll();

        tblTagsAll.setRowFactory(tableView -> {
            final TableRow<IVFXTags> row = new TableRow<>();
            row.hoverProperty().addListener((observable) -> {
                final IVFXTags tag = row.getItem();
                if (row.isHover() && tag != null) {

                    ContextMenu contextMenu = new ContextMenu();

                    MenuItem menuItem = new MenuItem("Редактировать тэг «" + tag.getName() +"»");
                    menuItem.setOnAction(e -> {new TagsController().editTags(tag);});
                    contextMenu.getItems().add(menuItem);

                    String url = tag.getPropertyValue("url");
                    if (url != null) {
                        menuItem = new MenuItem("Go to URL «" + tag.getName() +"»");
                        menuItem.setOnAction(e -> {
                            getHostServices().showDocument(url);
                        });
                        contextMenu.getItems().add(menuItem);
                    }

                    menuItem = new MenuItem("Перезагрузить картинку для тэга «" + tag.getName() +"»");
                    menuItem.setOnAction(e -> {
                        IVFXTags tmp = IVFXTags.load(tag.getId(), true);
                        assert tmp != null;
                        tag.setPreview(tmp.getPreview());
                        tag.setLabel(tmp.getLabel());
                        tblTagsAll.refresh();
                    });
                    contextMenu.getItems().add(menuItem);

                    row.setContextMenu(contextMenu);

                }
            });

            return row;
        });


    }


/*****************************************
 * Действия при выборе записей в таблицах
 *****************************************/

    // Действие при выборе записи в комбобоксе cbProjects
    private void doOnSelectRecordInCbProjects() {
        if (currentProject != null) {
            listFiles = FXCollections.observableArrayList(IVFXFiles.loadList(currentProject));
            cbFiles.setItems(listFiles);
            if (currentFile != null) {
                cbFiles.getSelectionModel().select(currentFile);
            }

            int[] arr = {1,2};
            if (!(cbTagsAllOnlyFromHtml.isSelected() && currentFile != null)) {
                listTagsAll = FXCollections.observableArrayList(IVFXTags.loadList(currentProject,true, arr));
            } else {
                listTagsAll = FXCollections.observableArrayList(currentFile.getListTagsFromHTML());
            }

            tblTagsAll.setItems(listTagsAll);
            filteredTagsAll = new FilteredList<>(listTagsAll, e -> true);

            fldFindTagsAll.setDisable(false);
            chFindTagsAllInProperties.setDisable(false);

        } else {
            fldFindTagsAll.setDisable(true);
            chFindTagsAllInProperties.setDisable(true);
        }

        // Планы
        tblShots.setDisable(true);

        // Сцены
        tblScenes.setDisable(true);
        tblSceneShots.setDisable(true);
        tblTagsScene.setDisable(true);
        btnUnionScenes.setDisable(true);

        // Свойства сцен
        tblSceneProperties.setDisable(true);
        fldScenePropertyName.setDisable(true);
        fldScenePropertyValue.setDisable(true);
        btnAddNewSceneProperty.setDisable(true);
        btnDeleteSceneProperty.setDisable(true);

        // Типы планов по размеру и персонажам
        tblShotsTypesSize.setDisable(true);
        tblShotsTypesPersons.setDisable(true);

        // Персонажи и объекты
        cbToNextStep1.setDisable(true);
        cbToNextStep2.setDisable(true);
        cbToNextStep3.setDisable(true);
        cbToNextStep4.setDisable(true);
        btnCopyFromStep1.setDisable(true);
        btnCopyFromStep2.setDisable(true);
        btnCopyFromStep3.setDisable(true);
        btnCopyFromStep4.setDisable(true);

        // Свойства персонажей и объектов
        tblTagsShotsProperties.setDisable(true);
        fldTagShotPropertyName.setDisable(true);
        fldTagShotPropertyValue.setDisable(true);
        btnAddNewTagsShotsProperties.setDisable(true);
        btnDeleteTagsProperties.setDisable(true);

    }

    // Действие при выборе записи в комбобоксе cbFiles
    private void doOnSelectRecordInCbFiles() {

        if (currentFile != null) {

            initPlayer();

            listShots = FXCollections.observableArrayList(IVFXShots.loadList(currentFile, true));
            tblShots.setItems(listShots);

            tblShots.setDisable(false);

            listScenes = FXCollections.observableArrayList(IVFXTagsScenes.loadListScenes(currentFile, true));
            tblScenes.setItems(listScenes);
            tblScenes.setDisable(false);

            listEvents = FXCollections.observableArrayList(IVFXTagsEvents.loadListEvents(currentFile, true));
            tblEvents.setItems(listEvents);
            tblEvents.setDisable(false);
            
            if (currentShot == null && listShots.size() > 0) {
                currentShot = listShots.get(0);
            }
            if (currentShot != null) {
                boolean isFind = false;
                for (IVFXShots shot: listShots) {
                    if (shot.getId() == currentShot.getId()) {
                        currentShot = shot;
                        isFind = true;
                        break;
                    }
                }
                if (!isFind && listShots.size() > 0) {
                    currentShot = listShots.get(0);
                }
                if (currentShot != null) {
                    tblShots.getSelectionModel().select(currentShot);
                }

            }

        } else {
            tblShots.setDisable(true);
            tblScenes.setItems(null);
            tblScenes.setDisable(true);
            tblEvents.setItems(null);
            tblEvents.setDisable(true);
        }

        // Сцены
        tblSceneShots.setDisable(true);
        tblSceneShots.setItems(null);
        tblTagsScene.setDisable(true);
        tblTagsScene.setItems(null);
        btnUnionScenes.setDisable(true);

        // Свойства сцен
        tblSceneProperties.setDisable(true);
        tblSceneProperties.setItems(null);
        fldScenePropertyName.setDisable(true);
        fldScenePropertyValue.setDisable(true);
        btnAddNewSceneProperty.setDisable(true);
        btnDeleteSceneProperty.setDisable(true);

        // События
        tblEventShots.setDisable(true);
        tblEventShots.setItems(null);
        tblTagsEvent.setDisable(true);
        tblTagsEvent.setItems(null);
        btnUnionEvents.setDisable(true);
        btnCreateNewEvent.setDisable(true);
        tblTagsShotsEvent.setDisable(true);
        tblTagsShotsEvent.setItems(null);
        btnAddTagToEvent.setDisable(true);
        btnDeleteEvent.setDisable(true);

        // Свойства событий
        tblEventProperties.setDisable(true);
        tblEventProperties.setItems(null);
        fldEventPropertyName.setDisable(true);
        fldEventPropertyValue.setDisable(true);
        btnAddNewEventProperty.setDisable(true);
        btnDeleteEventProperty.setDisable(true);
        
        // Типы планов по размеру и персонажам
        tblShotsTypesSize.setDisable(true);
        tblShotsTypesPersons.setDisable(true);

        // Персонажи и объекты
        cbToNextStep1.setDisable(true);
        cbToNextStep2.setDisable(true);
        cbToNextStep3.setDisable(true);
        cbToNextStep4.setDisable(true);
        btnCopyFromStep1.setDisable(true);
        btnCopyFromStep2.setDisable(true);
        btnCopyFromStep3.setDisable(true);
        btnCopyFromStep4.setDisable(true);

        // Свойства персонажей и объектов
        tblTagsShotsProperties.setDisable(true);
        tblTagsShotsProperties.setItems(null);
        fldTagShotPropertyName.setDisable(true);
        fldTagShotPropertyValue.setDisable(true);
        btnAddNewTagsShotsProperties.setDisable(true);
        btnDeleteTagsProperties.setDisable(true);
    }

    // Действие при выборе записи в таблице tblScenes
    private void doOnSelectRecordInTblScenes() {

        List<IVFXTagsScenes> listSelected = tblScenes.getSelectionModel().getSelectedItems();
        if (listSelected.size() > 0) {
            List<IVFXTags> listTags = new ArrayList<>();
            for (IVFXTagsScenes tagScene: listSelected) {
                listTags.add(tagScene);
            }
            tblSceneShots.setDisable(false);
            listSceneShots = FXCollections.observableArrayList(IVFXTagsShots.loadList(listTags, true));
            tblSceneShots.setItems(listSceneShots);

            int[] arrTagsTypes = {1,2};
            tblTagsScene.setDisable(false);
            listTagsScene = FXCollections.observableArrayList(IVFXTags.loadListShotsTagsForScenes(listTags, true, arrTagsTypes));
            tblTagsScene.setItems(listTagsScene);

        } else {
            tblSceneShots.setItems(null);
            tblSceneShots.setDisable(true);
            tblTagsScene.setItems(null);
            tblTagsScene.setDisable(true);
        }

        if (listSelected.size() > 1) {
            btnUnionScenes.setDisable(false);
        } else {
            btnUnionScenes.setDisable(true);
        }

        if (currentScene != null) {

            listSceneProperties = FXCollections.observableArrayList(IVFXTagsProperties.loadList(currentScene, true));
            tblSceneProperties.setItems(listSceneProperties);

            btnAddNewSceneProperty.setDisable(false);

            tblSceneProperties.setDisable(false);
            currentSceneProperty = null;
            fldScenePropertyName.setDisable(true);
            fldScenePropertyValue.setDisable(true);
            fldScenePropertyName.setText("");
            fldScenePropertyValue.setText("");


        } else {

            tblSceneProperties.setDisable(true);
            tblSceneProperties.setItems(null);
            currentSceneProperty = null;
            fldScenePropertyName.setDisable(true);
            fldScenePropertyValue.setDisable(true);
            fldScenePropertyName.setText("");
            fldScenePropertyValue.setText("");
            btnAddNewSceneProperty.setDisable(true);
        }

        btnDeleteSceneProperty.setDisable(true);
        btnSceneCutUp.setDisable(true);
        btnSceneCutDown.setDisable(true);
    }

    // Действие при выборе записи в таблице tblEvents
    private void doOnSelectRecordInTblEvents() {

        List<IVFXTagsEvents> listSelected = tblEvents.getSelectionModel().getSelectedItems();
        if (listSelected.size() > 0) {
            List<IVFXTags> listTags = new ArrayList<>();
            for (IVFXTagsEvents tagEvent: listSelected) {
                listTags.add(tagEvent);
            }
            tblEventShots.setDisable(false);
            listEventShots = FXCollections.observableArrayList(IVFXTagsShots.loadList(listTags, true));
            tblEventShots.setItems(listEventShots);

            if (listSelected.size() == 1) {
                currentEvent = listSelected.get(0);
                int[] arrTagsTypes = {0,1,2};
                tblTagsEvent.setDisable(false);
                listTagsEvent = FXCollections.observableArrayList(currentEvent.loadListChildrens(true, arrTagsTypes));
                tblTagsEvent.setItems(listTagsEvent);

                int[] arrTagsTypes2 = {1,2};
                tblTagsShotsEvent.setDisable(false);
                btnAddTagToEvent.setDisable(false);
                listTagsShotsEvent = FXCollections.observableArrayList(IVFXTags.loadListShotsTagsForEvents(listTags, true, arrTagsTypes2));
                tblTagsShotsEvent.setItems(listTagsShotsEvent);

            } else {
                tblTagsEvent.setDisable(true);
                tblTagsEvent.setItems(null);
                tblTagsShotsEvent.setDisable(true);
                tblTagsShotsEvent.setItems(null);
                btnAddTagToEvent.setDisable(true);
            }

            if (listSelected.size() > 1) {
                btnUnionEvents.setDisable(false);
                btnAddNewEventProperty.setDisable(true);
                btnDeleteEvent.setDisable(true);
            } else {
                btnUnionEvents.setDisable(true);
                btnAddNewEventProperty.setDisable(false);
                btnDeleteEvent.setDisable(false);
            }

        } else {
            tblEventShots.setItems(null);
            tblEventShots.setDisable(true);
            tblTagsEvent.setItems(null);
            tblTagsEvent.setDisable(true);
            tblTagsShotsEvent.setDisable(true);
            tblTagsShotsEvent.setItems(null);
            btnAddTagToEvent.setDisable(true);
        }

        if (currentEvent != null) {

            listEventProperties = FXCollections.observableArrayList(IVFXTagsProperties.loadList(currentEvent, true));
            tblEventProperties.setItems(listEventProperties);

            btnAddNewEventProperty.setDisable(false);

            tblEventProperties.setDisable(false);
            currentEventProperty = null;
            fldEventPropertyName.setDisable(true);
            fldEventPropertyValue.setDisable(true);
            fldEventPropertyName.setText("");
            fldEventPropertyValue.setText("");

            btnDeleteEvent.setDisable(false);

        } else {

            tblEventProperties.setDisable(true);
            tblEventProperties.setItems(null);
            currentEventProperty = null;
            fldEventPropertyName.setDisable(true);
            fldEventPropertyValue.setDisable(true);
            fldEventPropertyName.setText("");
            fldEventPropertyValue.setText("");
            btnAddNewEventProperty.setDisable(true);
            btnDeleteEvent.setDisable(true);

        }

        btnDeleteEventProperty.setDisable(true);
        btnEventCutUp.setDisable(true);
        btnEventCutDown.setDisable(true);
        btnEventExtendUp.setDisable(true);
        btnEventExtendDown.setDisable(true);
        btnEventCollapseUp.setDisable(true);
        btnEventCollapseDown.setDisable(true);
    }
    
    
    // Действие при выборе записи в таблице tblShots
    private void doOnSelectRecordInTblShots() {

        if (currentShot != null) {
            int[] arrTagsTypes = {1,2};
            listTagsShots = FXCollections.observableArrayList(IVFXTagsShots.loadList(currentShot, true, arrTagsTypes));
            tblTagsShots.setItems(listTagsShots);
            tblShotsTypesSize.setDisable(false);
            tblShotsTypesPersons.setDisable(false);

            for (IVFXShotsTypeSize shotsTypeSize: listShotsTypeSize) {
                if (shotsTypeSize.getId() == currentShot.getShotsTypeSizeId()) {
                    tblShotsTypesSize.getSelectionModel().select(shotsTypeSize);
                    break;
                }
            }

            for (IVFXShotsTypePersons shotsTypePersons: listShotsTypePersons) {
                if (shotsTypePersons.getId() == currentShot.getShotsTypePersonId()) {
                    tblShotsTypesPersons.getSelectionModel().select(shotsTypePersons);
                    break;
                }
            }

            currentShotStep1 = currentShot.loadPrevious(true);
            if (currentShotStep1 != null) {
                currentShotStep2 = currentShotStep1.loadPrevious(true);
            } else {
                currentShotStep2 = null;
            }
            if (currentShotStep2 != null) {
                currentShotStep3 = currentShotStep2.loadPrevious(true);
            } else {
                currentShotStep3 = null;
            }
            if (currentShotStep3 != null) {
                currentShotStep4 = currentShotStep3.loadPrevious(true);
            } else {
                currentShotStep4 = null;
            }
            currentTagShotStep1 = null;
            currentTagShotStep2 = null;
            currentTagShotStep3 = null;
            currentTagShotStep4 = null;

            if (currentShotStep1 != null) {
                listTagsShotsStep1 = FXCollections.observableArrayList(IVFXTagsShots.loadList(currentShotStep1, true, arrTagsTypes));
                tblTagsShotsStep1.setItems(listTagsShotsStep1);
                cbToNextStep1.setDisable(false);
                btnCopyFromStep1.setDisable(false);
            } else {
                tblTagsShotsStep1.setItems(null);
                cbToNextStep1.setDisable(true);
                btnCopyFromStep1.setDisable(true);
            }

            if (currentShotStep2 != null) {
                listTagsShotsStep2 = FXCollections.observableArrayList(IVFXTagsShots.loadList(currentShotStep2, true, arrTagsTypes));
                tblTagsShotsStep2.setItems(listTagsShotsStep2);
                cbToNextStep2.setDisable(false);
                btnCopyFromStep2.setDisable(false);
            } else {
                tblTagsShotsStep2.setItems(null);
                cbToNextStep2.setDisable(true);
                btnCopyFromStep2.setDisable(true);
            }

            if (currentShotStep3 != null) {
                listTagsShotsStep3 = FXCollections.observableArrayList(IVFXTagsShots.loadList(currentShotStep3, true, arrTagsTypes));
                tblTagsShotsStep3.setItems(listTagsShotsStep3);
                cbToNextStep3.setDisable(false);
                btnCopyFromStep3.setDisable(false);
            } else {
                tblTagsShotsStep3.setItems(null);
                cbToNextStep3.setDisable(true);
                btnCopyFromStep3.setDisable(true);
            }

            if (currentShotStep4 != null) {
                listTagsShotsStep4 = FXCollections.observableArrayList(IVFXTagsShots.loadList(currentShotStep4, true, arrTagsTypes));
                tblTagsShotsStep4.setItems(listTagsShotsStep4);
                cbToNextStep4.setDisable(false);
                btnCopyFromStep4.setDisable(false);
            } else {
                tblTagsShotsStep4.setItems(null);
                cbToNextStep4.setDisable(true);
                btnCopyFromStep4.setDisable(true);
            }

            // Находим сцену плана. Находим, не выделена ли она в таблице сцен. Если нет - выделяем.
            int sceneId = currentShot.getTagIdScene();
            List<IVFXTagsScenes> lst = tblScenes.getSelectionModel().getSelectedItems();
            boolean isFind = false;
            for (IVFXTagsScenes scn: lst) {
                if (scn.getId() == sceneId) {
                    isFind = true;
                    break;
                }
            }
            if (!isFind) {
                for (IVFXTagsScenes scn: listScenes) {
                    if (scn.getId() == sceneId) {
                        currentScene = scn;
                        tblScenes.getSelectionModel().clearSelection();
                        tblScenes.getSelectionModel().select(scn);
                        doOnSelectRecordInTblScenes();
                        break;
                    }
                }
            }
            IVFXTagsShots scnShot = tblSceneShots.getSelectionModel().getSelectedItem();
            if (scnShot == null || scnShot.getShotId() != currentShot.getId()) {
                for (IVFXTagsShots sceneShot: listSceneShots) {
                    if (sceneShot.getShotId() == currentShot.getId()) {
                        tblSceneShots.getSelectionModel().select(sceneShot);
                        tblSceneShotsSmartScrollToCurrent();
                        doOnSelectRecordInTblSceneShots();
                        break;
                    }
                }
            }


            // Находим событие плана. Находим, не выделена ли она в таблице сцен. Если нет - выделяем.
            int eventId = currentShot.getTagIdEvent();
            List<IVFXTagsEvents> lstE = tblEvents.getSelectionModel().getSelectedItems();
            boolean isFindE = false;
            for (IVFXTagsEvents evn: lstE) {
                if (evn.getId() == eventId) {
                    isFindE = true;
                    break;
                }
            }
            if (!isFindE) {
                for (IVFXTagsEvents evn: listEvents) {
                    if (evn.getId() == eventId) {
                        currentEvent = evn;
                        tblEvents.getSelectionModel().clearSelection();
                        tblEvents.getSelectionModel().select(evn);
                        doOnSelectRecordInTblEvents();
                        break;
                    }
                }
            }
            IVFXTagsShots evnShot = tblEventShots.getSelectionModel().getSelectedItem();
            if (evnShot == null || evnShot.getShotId() != currentShot.getId()) {
                for (IVFXTagsShots eventShot: listEventShots) {
                    if (eventShot.getShotId() == currentShot.getId()) {
                        tblEventShots.getSelectionModel().select(eventShot);
                        tblEventShotsSmartScrollToCurrent();
                        doOnSelectRecordInTblEventShots();
                        break;
                    }
                }
            }

            btnCreateNewEvent.setDisable(false);
            btnAddNewTagShot.setDisable(false);

        } else {
            tblShotsTypesSize.setDisable(true);
            tblShotsTypesPersons.setDisable(true);
            btnCreateNewEvent.setDisable(true);
            btnAddNewTagShot.setDisable(true);
        }

    }

    // Действие при выборе записи в таблице tblTagsShots
    private void doOnSelectRecordInTblTagsShots() {

        if (currentTagShot != null) {

            tblTagsShotsProperties.setDisable(false);
            btnAddNewTagsShotsProperties.setDisable(false);

            listTagsShotsProperties = FXCollections.observableArrayList(IVFXTagsShotsProperties.loadList(currentTagShot,true));
            tblTagsShotsProperties.setItems(listTagsShotsProperties);
            currentTagShotProperty = null;

            fldTagShotPropertyName.setText("");
            fldTagShotPropertyValue.setText("");

            fldTagShotPropertyName.setDisable(true);
            fldTagShotPropertyValue.setDisable(true);
            btnDeleteTagsProperties.setDisable(true);


        } else {
            tblTagsShotsProperties.setItems(null);

            fldTagShotPropertyName.setText("");
            fldTagShotPropertyValue.setText("");

            tblTagsShotsProperties.setDisable(true);
            fldTagShotPropertyName.setDisable(true);
            fldTagShotPropertyValue.setDisable(true);
            btnAddNewTagsShotsProperties.setDisable(true);
            btnDeleteTagsProperties.setDisable(true);

        }

    }

    // Действие при выборе записи в таблице tblTagsShotsProperties
    private void doOnSelectRecordInTblTagsShotsProperties() {

        if (currentTagShotProperty != null) {

            fldTagShotPropertyName.setDisable(false);
            fldTagShotPropertyValue.setDisable(false);
            btnDeleteTagsProperties.setDisable(false);

            fldTagShotPropertyName.setText(currentTagShotProperty.getName());
            fldTagShotPropertyValue.setText(currentTagShotProperty.getValue());

        } else {
            fldTagShotPropertyName.setText("");
            fldTagShotPropertyValue.setText("");
            fldTagShotPropertyName.setDisable(true);
            fldTagShotPropertyValue.setDisable(true);
            btnDeleteTagsProperties.setDisable(true);
        }

    }

    // Действие при выборе записи в таблице tblSceneProperties
    private void doOnSelectRecordInTblSceneProperties() {

        if (currentSceneProperty != null) {

            fldScenePropertyName.setDisable(false);
            fldScenePropertyValue.setDisable(false);
            btnDeleteSceneProperty.setDisable(false);

            fldScenePropertyName.setText(currentSceneProperty.getName());
            fldScenePropertyValue.setText(currentSceneProperty.getValue());

        } else {
            fldScenePropertyName.setText("");
            fldScenePropertyValue.setText("");
            fldScenePropertyName.setDisable(true);
            fldScenePropertyValue.setDisable(true);
            btnDeleteSceneProperty.setDisable(true);
        }

    }

    // Действие при выборе записи в таблице tblEventProperties
    private void doOnSelectRecordInTblEventProperties() {

        if (currentEventProperty != null) {

            fldEventPropertyName.setDisable(false);
            fldEventPropertyValue.setDisable(false);
            btnDeleteEventProperty.setDisable(false);

            fldEventPropertyName.setText(currentEventProperty.getName());
            fldEventPropertyValue.setText(currentEventProperty.getValue());

        } else {
            fldEventPropertyName.setText("");
            fldEventPropertyValue.setText("");
            fldEventPropertyName.setDisable(true);
            fldEventPropertyValue.setDisable(true);
            btnDeleteEventProperty.setDisable(true);
        }

    }

    // Действие при выборе записи в таблице tblSceneShots
    private void doOnSelectRecordInTblSceneShots() {
        if (currentSceneShot != null) {
            btnSceneCutUp.setDisable(currentSceneShot.isEqual(listSceneShots.get(0)));
            btnSceneCutDown.setDisable(currentSceneShot.isEqual(listSceneShots.get(listSceneShots.size()-1)));

            if (currentSceneShot.getShotId() != currentShot.getId()) {
                goToShot(currentSceneShot.getShotId());
            }
        }
    }

    // Действие при выборе записи в таблице tblEventShots
    private void doOnSelectRecordInTblEventShots() {
        if (currentEventShot != null) {
            btnEventCutUp.setDisable(currentEventShot.isEqual(listEventShots.get(0)));
            btnEventCutDown.setDisable(currentEventShot.isEqual(listEventShots.get(listEventShots.size()-1)));

            btnEventExtendUp.setDisable(!(currentEventShot.getIvfxShot().isStartEvent() || currentEventShot.getIvfxShot().isStartScene()));
            btnEventCollapseDown.setDisable(!(currentEventShot.getIvfxShot().isStartEvent() || currentEventShot.getIvfxShot().isStartScene()) || (currentEventShot.getIvfxShot().isEndEvent() || currentEventShot.getIvfxShot().isEndScene()));

            btnEventExtendDown.setDisable(!(currentEventShot.getIvfxShot().isEndEvent() || currentEventShot.getIvfxShot().isEndScene()));
            btnEventCollapseUp.setDisable(!(currentEventShot.getIvfxShot().isEndEvent() || currentEventShot.getIvfxShot().isEndScene()) || (currentEventShot.getIvfxShot().isStartEvent() || currentEventShot.getIvfxShot().isStartScene()));


            if (currentEventShot.getShotId() != currentShot.getId()) {
                goToShot(currentEventShot.getShotId());
            }
        }
    }
/********
 * Методы
 ********/

    // Инициализация плеера
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

    // Инициализация лейблов превью
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

        for (int i = 0; i < countLabels; i++) {

            int finalI = i;

            double diff = i * finalStep;
            int frameNumber = firstFrameNumber + (int) Math.round(diff);
            if (frameNumber > lastFrameNumber) frameNumber = 0;
            if (i == countLabels - 1 && frameNumber != 0) frameNumber = lastFrameNumber;
            MatrixLabels matrixLabels = listLabels.get(i);
            matrixLabels.frameNumber = frameNumber;
            if (frameNumber != 0) {
                currentFrameToScroll = frameNumber;
                String fileName = ivfxFiles.getFolderFramesPreview() + "\\" + ivfxFiles.getShortName() + ivfxFiles.FRAMES_PREFIX + String.format("%06d", frameNumber) + ".jpg";
                String fileNameMediumSize = ivfxFiles.getFolderFramesMedium() + "\\" + ivfxFiles.getShortName() + ivfxFiles.FRAMES_PREFIX + String.format("%06d", frameNumber) + ".jpg";
                if (i == 0) {
                    onMouseClickLabel(fileName, fileNameMediumSize, lblFullSizePicture);
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
                            onMouseClickLabel(fileName, fileNameMediumSize, lblFullSizePicture);
                        } else if (mouseEvent.getButton().equals(MouseButton.PRIMARY) && mouseEvent.getClickCount() == 2) {
                            updateFullPreviewDuringMoveMouse = !updateFullPreviewDuringMoveMouse;
                        } else if (mouseEvent.getButton().equals(MouseButton.PRIMARY) && mouseEvent.getClickCount() == 3) {
                            int firstShotFrame = currentShot.getFirstFrameNumber();
                            new FramesController().editTransitions(currentFile, finalFrameNumber);
                            initialize();
                            for (IVFXShots shot: listShots) {
                                if (shot.getFirstFrameNumber() >= firstShotFrame && shot.getLastFrameNumber() <= firstShotFrame) {
                                    currentShot = shot;
                                    goToShot(currentShot.getId());
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

    }

    // main
    public static void main(String[] args) {
        launch(args);
    }

    // start
    @Override
    public void start(Stage primaryStage) throws Exception {
        Main.mainConnection = Database.getConnection();
        Main.mainWindow = primaryStage;

        AnchorPane root = FXMLLoader.load(tagsShotsController.getClass().getResource("../resources/fxml/TagsShots.fxml")); // в этот момент вызывается initialize()

        tagsShotsController.controllerScene = new Scene(root);
        tagsShotsController.controllerStage = new Stage();
        tagsShotsController.controllerStage.setTitle("Редактор планов и тэгов.");
        tagsShotsController.controllerStage.setScene(tagsShotsController.controllerScene);
        tagsShotsController.controllerStage.initModality(Modality.APPLICATION_MODAL);

        tagsShotsController.controllerStage.setOnCloseRequest(we -> {
//                shotsTagsController.isWorking = false;
                if (mainMediaPlayer != null) {
                    mainMediaPlayer.pause();
                }
            System.out.println("Закрытые окна редактора планов и тэгов.");
        });

        tagsShotsController.controllerStage.showAndWait();
        System.out.println("Завершение работы editShotsTags");


    }

    // editShotsTags
    public void editShotsTags() {

        try {

            AnchorPane root = FXMLLoader.load(tagsShotsController.getClass().getResource("../resources/fxml/TagsShots.fxml")); // в этот момент вызывается initialize()

            tagsShotsController.controllerScene = new Scene(root);
            tagsShotsController.controllerStage = new Stage();
            tagsShotsController.controllerStage.setTitle("Редактор планов и тэгов.");
            tagsShotsController.controllerStage.setScene(tagsShotsController.controllerScene);
            tagsShotsController.controllerStage.initModality(Modality.APPLICATION_MODAL);

            tagsShotsController.controllerStage.setOnCloseRequest(we -> {
//                shotsTagsController.isWorking = false;
                if (mainMediaPlayer != null) {
                    mainMediaPlayer.pause();
                }
                System.out.println("Закрытые окна редактора планов и тэгов.");
            });

            tagsShotsController.controllerStage.showAndWait();


            System.out.println("Завершение работы editShotsTags");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // обработчик события клика на лейбле
    private void onMouseClickLabel(String fileNamePreview, String fileNameMediumSize, Label label) {

        File file = new File(fileNameMediumSize);
        if (file.exists()) {
            try {
                BufferedImage bufferedImage = ImageIO.read(file);
//                bufferedImage = OverlayImage.resizeImage(bufferedImage,720,400, Color.BLACK);
                ImageView imageView = new ImageView(ConvertToFxImage.convertToFxImage(bufferedImage));
                Platform.runLater(() -> {
                    label.setGraphic(imageView);
                });

            } catch (IOException e) {
            }
            mainFramePreviewFile = fileNamePreview;
            mainFrameFullSizeFile = fileNameMediumSize;
        }
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
            String fileNameMediumSize = currentFile.getFolderFramesMedium() + "\\" + currentFile.getShortName() + currentFile.FRAMES_PREFIX + String.format("%06d", matrixLabels.frameNumber) + ".jpg";
            onMouseClickLabel(fileName, fileNameMediumSize, lblFullSizePicture);
        }
    }

    // обработчик события ухода мыши с лейбла
    private void onMouseExitLabel(MatrixLabels matrixLabels) {
        matrixLabels.label.setStyle(fxBorderDefault);
    }

    // скролл к текущей записи в таблице tblShots
    private void tblShotsSmartScrollToCurrent() {
        if (flowTblShots != null && flowTblShots.getCellCount() > 0) {
            int first = flowTblShots.getFirstVisibleCell().getIndex();
            int last = flowTblShots.getLastVisibleCell().getIndex();
            int selected = tblShots.getSelectionModel().getSelectedIndex();
            if (selected < first || selected > last) {
                tblShots.scrollTo(selected);
            }
        }
    }

    // скролл к текущей записи в таблице tblShots
    private void tblSceneShotsSmartScrollToCurrent() {
        if (flowTblSceneShots != null && flowTblSceneShots.getCellCount() > 0) {
            int first = flowTblSceneShots.getFirstVisibleCell().getIndex();
            int last = flowTblSceneShots.getLastVisibleCell().getIndex();
            int selected = tblSceneShots.getSelectionModel().getSelectedIndex();
            if (selected < first || selected > last) {
                tblSceneShots.scrollTo(selected);
            }
        }
    }

    // скролл к текущей записи в таблице tblShots
    private void tblEventShotsSmartScrollToCurrent() {
        if (flowTblEventShots != null && flowTblEventShots.getCellCount() > 0) {
            int first = flowTblEventShots.getFirstVisibleCell().getIndex();
            int last = flowTblEventShots.getLastVisibleCell().getIndex();
            int selected = tblEventShots.getSelectionModel().getSelectedIndex();
            if (selected < first || selected > last) {
                tblEventShots.scrollTo(selected);
            }
        }
    }

    
    // переход на предыдущий план
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

    // переход на следующий план
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

    // переход на указанный план
    private void goToShot(int shotId) {
        if (shotId != 0) {
            for (int i = 0; i < listShots.size(); i++) {
                IVFXShots shot = listShots.get(i);
                if (shot.getId() == shotId) {
                    currentShot = shot;
                    tblShots.getSelectionModel().select(i);
                    tblShotsSmartScrollToCurrent();
                    break;
                }
            }
        }
    }

    // добавление тэга к плану
    private void addTagToTagsShots(IVFXTags tag, IVFXShots shot) {

        if (tag != null && shot != null) {
            IVFXTagsShots tagShot = IVFXTagsShots.load(tag,shot, true);
            if (tagShot == null) {
                tagShot = IVFXTagsShots.getNewDbInstance(tag, shot);
            }
            doOnSelectRecordInTblShots();
            if (tagShot != null) {
                for (IVFXTagsShots tmp: listTagsShots) {
                    if (tmp.isEqual(tagShot)) {
                        currentTagShot = tmp;
                        break;
                    }
                }
                if (currentTagShot != null) {
                    tblTagsShots.getSelectionModel().select(currentTagShot);
                    doOnSelectRecordInTblTagsShots();
                }
            }
        }

    }



    /******************************
     * Методы обработчиков событий
     ******************************/
    // Событие нажатия кнопки btnOK
    @FXML
    void doBtnOK(ActionEvent event) {
        tagsShotsController.controllerStage.close();
    }

    // Событие переключения группы tgPlayerLoopNextStop
    @FXML
    void doTgPlayerLoopNextStop(ActionEvent event) {

    }

    // Событие переключения группы tgPlayerSpeed
    @FXML
    void doTgPlayerSpeed(ActionEvent event) {

    }

    // Событие переключения флажка chFindTagsAllInProperties
    @FXML
    void doChFindTagsAllInProperties(ActionEvent event) {

    }

    // Событие нажатия кнопки btnCopyFromStep1
    @FXML
    void doBtnCopyFromStep1(ActionEvent event) {

        if (currentShotStep1 != null && currentShot != null) {
            for (IVFXTagsShots tmp: listTagsShotsStep1) {
                tmp.copyTo(currentShot,true, false);
            }
        }
        if (cbToNextStep1.isSelected()) {
            goToNextShot();
        } else {
            doOnSelectRecordInTblShots();
        }

    }

    // Событие нажатия кнопки btnCopyFromStep2
    @FXML
    void doBtnCopyFromStep2(ActionEvent event) {
        if (currentShotStep2 != null && currentShot != null) {
            for (IVFXTagsShots tmp: listTagsShotsStep2) {
                tmp.copyTo(currentShot,true, false);
            }
        }
        if (cbToNextStep2.isSelected()) {
            goToNextShot();
        } else {
            doOnSelectRecordInTblShots();
        }
    }

    // Событие нажатия кнопки btnCopyFromStep3
    @FXML
    void doBtnCopyFromStep3(ActionEvent event) {
        if (currentShotStep3 != null && currentShot != null) {
            for (IVFXTagsShots tmp: listTagsShotsStep3) {
                tmp.copyTo(currentShot,true, false);
            }
        }
        if (cbToNextStep3.isSelected()) {
            goToNextShot();
        } else {
            doOnSelectRecordInTblShots();
        }
    }

    // Событие нажатия кнопки btnCopyFromStep4
    @FXML
    void doBtnCopyFromStep4(ActionEvent event) {
        if (currentShotStep4 != null && currentShot != null) {
            for (IVFXTagsShots tmp: listTagsShotsStep4) {
                tmp.copyTo(currentShot,true, false);
            }
        }
        if (cbToNextStep4.isSelected()) {
            goToNextShot();
        } else {
            doOnSelectRecordInTblShots();
        }
    }

    // Событие нажатия кнопки btnAddNewTagsShotsProperties
    @FXML
    void doBtnAddNewTagsShotsProperties(ActionEvent event) {
        if (currentTagShot != null) {

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Добавление свойства связки план-тег");
            alert.setHeaderText("Вы действительно хотите добавить новое свойство для связки план-тег?");
            alert.setContentText("Имя и значение свойства будут сгенерированы автоматически.");
            Optional<ButtonType> option = alert.showAndWait();
            if (option.get() == ButtonType.OK) {

                IVFXTagsShotsProperties tagShotProperty = IVFXTagsShotsProperties.getNewDbInstance(currentTagShot);
                listTagsShotsProperties = FXCollections.observableArrayList(IVFXTagsShotsProperties.loadList(currentTagShot, true));
                tblTagsShotsProperties.setItems(listTagsShotsProperties);
                for (IVFXTagsShotsProperties tmp: listTagsShotsProperties) {
                    if (tmp.isEqual(tagShotProperty)) {
                        currentTagShotProperty = tmp;
                        break;
                    }
                }
                if (currentTagShotProperty != null) {
                    tblTagsShotsProperties.getSelectionModel().select(currentTagShotProperty);
                    doOnSelectRecordInTblTagsShotsProperties();
                }

            }

        }
    }

    // Событие нажатия кнопки btnDeleteTagsShotsProperties
    @FXML
    void doBtnDeleteTagsShotsProperties(ActionEvent event) {
        if (currentTagShotProperty != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Удаление свойства тега");
            alert.setHeaderText("Вы действительно хотите удалить свойство связки план-тег с именем «" + currentTagShotProperty.getName() + "» и значением «" + currentTagShotProperty.getValue() + "»?");
            alert.setContentText("В случае утвердительного ответа свойствобудет удалено из базы данных и его восстановление будет невозможно.\nВы уверены, что хотите удалить свойство?");
            Optional<ButtonType> option = alert.showAndWait();
            if (option.get() == ButtonType.OK) {
                currentTagShotProperty.delete();
                listTagsShotsProperties = FXCollections.observableArrayList(IVFXTagsShotsProperties.loadList(currentTagShot, true));
                tblTagsShotsProperties.setItems(listTagsShotsProperties);
                currentTagShotProperty = null;
                doOnSelectRecordInTblTagsShotsProperties();
            }
        }
    }

    // Событие изменения значения в поле fldTagShotPropertyName
    @FXML
    void doFldTagShotPropertyName(ActionEvent event) {
        if (currentTagShotProperty != null) {
            currentTagShotProperty.setName(fldTagShotPropertyName.getText());
            currentTagShotProperty.save();
            tblTagsShotsProperties.refresh();
            tblTagsShots.refresh();
        }
    }

    // Событие нажатия кнопки btnAddNewSceneProperty
    @FXML
    void doBtnAddNewSceneProperty(ActionEvent event) {

        if (currentScene != null) {

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Добавление свойства сцены");
            alert.setHeaderText("Вы действительно хотите добавить новое свойство для сцены?");
            alert.setContentText("Имя и значение свойства будут сгенерированы автоматически.");
            Optional<ButtonType> option = alert.showAndWait();
            if (option.get() == ButtonType.OK) {

                IVFXTagsProperties tagProperty = IVFXTagsProperties.getNewDbInstance(currentScene);
                listSceneProperties = FXCollections.observableArrayList(IVFXTagsProperties.loadList(currentScene, true));
                tblSceneProperties.setItems(listSceneProperties);
                for (IVFXTagsProperties tmp: listSceneProperties) {
                    if (tmp.isEqual(tagProperty)) {
                        currentSceneProperty = tmp;
                        break;
                    }
                }
                if (currentSceneProperty != null) {
                    tblSceneProperties.getSelectionModel().select(currentSceneProperty);
                    doOnSelectRecordInTblSceneProperties();
                }

            }

        }

    }

    // Событие нажатия кнопки btnDeleteSceneProperty
    @FXML
    void doBtnDeleteSceneProperty(ActionEvent event) {

        if (currentSceneProperty != null) {
            if (!currentSceneProperty.getName().equals("name")) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Удаление свойства сцены");
                alert.setHeaderText("Вы действительно хотите удалить свойство сцены с именем «" + currentSceneProperty.getName() + "» и значением «" + currentSceneProperty.getValue() + "»?");
                alert.setContentText("В случае утвердительного ответа свойство будет удалено из базы данных и его восстановление будет невозможно.\nВы уверены, что хотите удалить свойство?");
                Optional<ButtonType> option = alert.showAndWait();
                if (option.get() == ButtonType.OK) {
                    currentSceneProperty.delete();
                    listSceneProperties = FXCollections.observableArrayList(IVFXTagsProperties.loadList(currentScene, true));
                    tblSceneProperties.setItems(listSceneProperties);
                    currentSceneProperty = null;
                    doOnSelectRecordInTblSceneProperties();
                }
            }
        }

    }

    // Событие изменения значения в поле fldScenePropertyName
    @FXML
    void doFldScenePropertyName(ActionEvent event) {

        if (currentSceneProperty != null) {
            if (!currentSceneProperty.getName().equals("name")) {
                currentSceneProperty.setName(fldScenePropertyName.getText());
                currentSceneProperty.save();
                tblSceneProperties.refresh();
                tblScenes.refresh();
            }
        }

    }

    // Метод изменения значения поля fldTagShotPropertyValue (обработчик события прописан в initialize)
    void doFldTagShotPropertyValue() {
        if (currentTagShotProperty != null) {
            currentTagShotProperty.setValue(fldTagShotPropertyValue.getText());
            currentTagShotProperty.save();
            tblTagsShotsProperties.refresh();
            tblTagsShots.refresh();
        }
    }

    // Метод изменения значения поля flddScenePropertyValue (обработчик события прописан в initialize)
    void doFldScenePropertyValue() {

        if (currentSceneProperty != null) {
            currentSceneProperty.setValue(fldScenePropertyValue.getText());
            currentSceneProperty.save();
            tblSceneProperties.refresh();
            if (currentSceneProperty.getName().equals("name")) {
                currentScene.setName(currentSceneProperty.getValue());
                tblScenes.refresh();
            }

        }
    }

    // Метод удаления тэга у плана
    private void doOnDeleteTagShot(IVFXTagsShots tagShot) {
        if (tagShot != null) {
            tagShot.delete();
            currentTagShot = null;
            doOnSelectRecordInTblShots();
        }
    }

    // Событие нажатия кнопки btnUnionScenes
    @FXML
    void doBtnUnionScenes(ActionEvent event) {

        List<IVFXTagsScenes> listSelected = tblScenes.getSelectionModel().getSelectedItems();
        if (listSelected.size() > 0) {
            List<IVFXTags> listTags = new ArrayList<>();
            for (IVFXTagsScenes tagScene: listSelected) {
                listTags.add(tagScene);
            }


            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Объединение сцен");
            alert.setHeaderText("Вы действительно хотите объединить выбранные сцены?");
            alert.setContentText("В случае утвердительного ответа планы всех выбранных сцен будут привязаны к первой выбранной сцене, а сами сцены будут удалены.\nВы уверены?");
            Optional<ButtonType> option = alert.showAndWait();
            if (option.get() == ButtonType.OK) {

                IVFXTagsScenes mainTagScene = IVFXTagsScenes.unionScenes(listSelected);
                listScenes = FXCollections.observableArrayList(IVFXTagsScenes.loadListScenes(currentFile, true));
                tblScenes.setItems(listScenes);
                tblScenes.setDisable(false);
                for (IVFXTagsScenes tmp: listScenes) {
                    if (tmp.isEqual(mainTagScene)) {
                        currentScene = tmp;
                        break;
                    }
                }

                tblScenes.getSelectionModel().select((IVFXTagsScenes) currentScene);
                List<IVFXTagsShots> lst = FXCollections.observableArrayList(IVFXTagsShots.loadList(currentScene, true));
                tblSceneShots.setItems(listSceneShots);


                for (IVFXShots shot: listShots) {
                    for (IVFXTagsShots tagShot: lst) {
                        if (shot.getId() == tagShot.getShotId()) {
                            shot.setImageViewFirst(tagShot.getIvfxShot().getImageViewFirst());
                            shot.setImageViewLast(tagShot.getIvfxShot().getImageViewLast());
                            shot.setLabelFirst(tagShot.getIvfxShot().getLabelFirst());
                            shot.setLabelLast(tagShot.getIvfxShot().getLabelLast());
                        }
                    }
                }

                for (IVFXTagsShots shot: listEventShots) {
                    for (IVFXTagsShots tagShot: lst) {
                        if (shot.getId() == tagShot.getShotId()) {
                            shot.getIvfxShot().setImageViewFirst(tagShot.getIvfxShot().getImageViewFirst());
                            shot.getIvfxShot().setImageViewLast(tagShot.getIvfxShot().getImageViewLast());
                            shot.getIvfxShot().setLabelFirst(tagShot.getIvfxShot().getLabelFirst());
                            shot.getIvfxShot().setLabelLast(tagShot.getIvfxShot().getLabelLast());
                        }
                    }
                }


                currentShot = listShots.get(0);
                tblShots.refresh();
                tblEventShots.refresh();

                doOnSelectRecordInTblShots();
                
            }

        }

    }

    // Событие нажатия кнопки btnUnionScenes
    @FXML
    void doBtnUnionEvents(ActionEvent event) {

        List<IVFXTagsEvents> listSelected = tblEvents.getSelectionModel().getSelectedItems();
        if (listSelected.size() > 0) {
            List<IVFXTags> listTags = new ArrayList<>();
            for (IVFXTagsEvents tagEvent: listSelected) {
                listTags.add(tagEvent);
            }


            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Объединение событий");
            alert.setHeaderText("Вы действительно хотите объединить выбранные события?");
            alert.setContentText("В случае утвердительного ответа планы и тэги всех выбранных событий будут привязаны к первому выбранному событию, а сами события будут удалены.\nВы уверены?");
            Optional<ButtonType> option = alert.showAndWait();
            if (option.get() == ButtonType.OK) {

                IVFXTagsEvents mainTagEvent = IVFXTagsEvents.unionEvents(listSelected);
                listEvents = FXCollections.observableArrayList(IVFXTagsEvents.loadListEvents(currentFile, true));
                tblEvents.setItems(listEvents);
                tblEvents.setDisable(false);
                for (IVFXTagsEvents tmp: listEvents) {
                    if (tmp.isEqual(mainTagEvent)) {
                        currentEvent = tmp;
                        break;
                    }
                }

                tblEvents.getSelectionModel().select((IVFXTagsEvents) currentEvent);
                List<IVFXTagsShots> lst = FXCollections.observableArrayList(IVFXTagsShots.loadList(currentEvent, true));
                tblEventShots.setItems(listEventShots);


                for (IVFXShots shot: listShots) {
                    for (IVFXTagsShots tagShot: lst) {
                        if (shot.getId() == tagShot.getShotId()) {
                            shot.setImageViewFirst(tagShot.getIvfxShot().getImageViewFirst());
                            shot.setImageViewLast(tagShot.getIvfxShot().getImageViewLast());
                            shot.setLabelFirst(tagShot.getIvfxShot().getLabelFirst());
                            shot.setLabelLast(tagShot.getIvfxShot().getLabelLast());
                        }
                    }
                }

                for (IVFXTagsShots shot: listSceneShots) {
                    for (IVFXTagsShots tagShot: lst) {
                        if (shot.getId() == tagShot.getShotId()) {
                            shot.getIvfxShot().setImageViewFirst(tagShot.getIvfxShot().getImageViewFirst());
                            shot.getIvfxShot().setImageViewLast(tagShot.getIvfxShot().getImageViewLast());
                            shot.getIvfxShot().setLabelFirst(tagShot.getIvfxShot().getLabelFirst());
                            shot.getIvfxShot().setLabelLast(tagShot.getIvfxShot().getLabelLast());
                        }
                    }
                }

                currentShot = listShots.get(0);
                tblShots.refresh();
                tblSceneShots.refresh();

                doOnSelectRecordInTblShots();

            }

        }

    }

    
    // Событие нажатия кнопки "Разрезать сцену снизу"
    @FXML
    void doBtnSceneCutDown(ActionEvent event) {
        cutScene(false);
    }

    // Событие нажатия кнопки "Разрезать сцену сверху"
    @FXML
    void doBtnSceneCutUp(ActionEvent event) {
        cutScene(true);
    }

    // Разрезаем сцену
    private void cutScene(boolean cutUp) {
        if (currentProject != null &&  currentScene != null && currentSceneShot != null) {
            IVFXTags tagScene = currentScene.cutScene(currentProject, currentSceneShot, cutUp);

            List<IVFXTags> lst = new ArrayList<>();
            if (cutUp) {
                lst.add(tagScene);
                lst.add(currentScene);
            } else {
                lst.add(currentScene);
                lst.add(tagScene);
            }
            List<IVFXTagsShots> lstTagsShots = IVFXTagsShots.loadList(lst,true);

            for (IVFXShots shot: listShots) {
                for (IVFXTagsShots tagShot: lstTagsShots) {
                    if (shot.getId() == tagShot.getShotId()) {
                        shot.setImageViewFirst(tagShot.getIvfxShot().getImageViewFirst());
                        shot.setImageViewLast(tagShot.getIvfxShot().getImageViewLast());
                        shot.setLabelFirst(tagShot.getIvfxShot().getLabelFirst());
                        shot.setLabelLast(tagShot.getIvfxShot().getLabelLast());
                    }
                }
            }

            for (IVFXTagsShots shot: listEventShots) {
                for (IVFXTagsShots tagShot: lstTagsShots) {
                    if (shot.getId() == tagShot.getShotId()) {
                        shot.getIvfxShot().setImageViewFirst(tagShot.getIvfxShot().getImageViewFirst());
                        shot.getIvfxShot().setImageViewLast(tagShot.getIvfxShot().getImageViewLast());
                        shot.getIvfxShot().setLabelFirst(tagShot.getIvfxShot().getLabelFirst());
                        shot.getIvfxShot().setLabelLast(tagShot.getIvfxShot().getLabelLast());
                    }
                }
            }

            // Обновляем список сцен
            listScenes = FXCollections.observableArrayList(IVFXTagsScenes.loadListScenes(currentFile, true));
            tblScenes.setItems(listScenes);

            tblShots.refresh();
            tblEventShots.refresh();

            for (IVFXShots shot: listShots) {
                if (shot.getId() == currentShot.getId()) {
                    currentShot = shot;
                    tblShots.getSelectionModel().select(currentShot);
                    tblShotsSmartScrollToCurrent();
                }
            }
            doOnSelectRecordInTblShots();

        }
    }

    // Событие нажатия кнопки btnAddNewSceneProperty
    @FXML
    void doBtnAddNewEventProperty(ActionEvent event) {

        if (currentEvent != null) {

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Добавление свойства события");
            alert.setHeaderText("Вы действительно хотите добавить новое свойство для события?");
            alert.setContentText("Имя и значение свойства будут сгенерированы автоматически.");
            Optional<ButtonType> option = alert.showAndWait();
            if (option.get() == ButtonType.OK) {

                IVFXTagsProperties tagProperty = IVFXTagsProperties.getNewDbInstance(currentEvent);
                listEventProperties = FXCollections.observableArrayList(IVFXTagsProperties.loadList(currentEvent, true));
                tblEventProperties.setItems(listEventProperties);
                for (IVFXTagsProperties tmp: listEventProperties) {
                    if (tmp.isEqual(tagProperty)) {
                        currentEventProperty = tmp;
                        break;
                    }
                }
                if (currentEventProperty != null) {
                    tblEventProperties.getSelectionModel().select(currentEventProperty);
                    doOnSelectRecordInTblEventProperties();
                }

            }

        }

    }

    // Событие нажатия кнопки btnDeleteSceneProperty
    @FXML
    void doBtnDeleteEventProperty(ActionEvent event) {

        if (currentEventProperty != null) {
            if (!currentEventProperty.getName().equals("name")) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Удаление свойства события");
                alert.setHeaderText("Вы действительно хотите удалить свойство события с именем «" + currentEventProperty.getName() + "» и значением «" + currentEventProperty.getValue() + "»?");
                alert.setContentText("В случае утвердительного ответа свойство будет удалено из базы данных и его восстановление будет невозможно.\nВы уверены, что хотите удалить свойство?");
                Optional<ButtonType> option = alert.showAndWait();
                if (option.get() == ButtonType.OK) {
                    currentEventProperty.delete();
                    listEventProperties = FXCollections.observableArrayList(IVFXTagsProperties.loadList(currentEvent, true));
                    tblEventProperties.setItems(listEventProperties);
                    currentEventProperty = null;
                    doOnSelectRecordInTblEventProperties();
                }
            }
        }

    }

    // Событие изменения значения в поле fldEventPropertyName
    @FXML
    void doFldEventPropertyName(ActionEvent event) {

        if (currentEventProperty != null) {
            if (!currentEventProperty.getName().equals("name")) {
                currentEventProperty.setName(fldEventPropertyName.getText());
                currentEventProperty.save();
                tblEventProperties.refresh();
                tblEvents.refresh();
            }
        }

    }

    // Метод изменения значения поля fldEventPropertyValue (обработчик события прописан в initialize)
    void doFldEventPropertyValue() {

        if (currentEventProperty != null) {
            currentEventProperty.setValue(fldEventPropertyValue.getText());
            currentEventProperty.save();
            tblEventProperties.refresh();
            if (currentEventProperty.getName().equals("name")) {
                currentEvent.setName(currentEventProperty.getValue());
                tblEvents.refresh();
            }

        }
    }

    // Событие нажатия кнопки "Разрезать событие сверху"
    @FXML
    void doBtnEventCutDown(ActionEvent event) {
        cutEvent(false);
    }

    // Событие нажатия кнопки "Разрезать событие снизу"
    @FXML
    void doBtnEventCutUp(ActionEvent event) {
        cutEvent(true);
    }

    // Разрезаем событие
    private void cutEvent(boolean cutUp) {
        if (currentProject != null &&  currentEvent != null && currentEventShot != null) {
            IVFXTags tagEvent = currentEvent.cutEvent(currentProject, currentEventShot, cutUp);

            List<IVFXTags> lst = new ArrayList<>();
            if (cutUp) {
                lst.add(tagEvent);
                lst.add(currentEvent);
            } else {
                lst.add(currentEvent);
                lst.add(tagEvent);
            }
            List<IVFXTagsShots> lstTagsShots = IVFXTagsShots.loadList(lst,true);

            for (IVFXShots shot: listShots) {
                for (IVFXTagsShots tagShot: lstTagsShots) {
                    if (shot.getId() == tagShot.getShotId()) {
                        shot.setImageViewFirst(tagShot.getIvfxShot().getImageViewFirst());
                        shot.setImageViewLast(tagShot.getIvfxShot().getImageViewLast());
                        shot.setLabelFirst(tagShot.getIvfxShot().getLabelFirst());
                        shot.setLabelLast(tagShot.getIvfxShot().getLabelLast());
                    }
                }
            }

            for (IVFXTagsShots shot: listSceneShots) {
                for (IVFXTagsShots tagShot: lstTagsShots) {
                    if (shot.getId() == tagShot.getShotId()) {
                        shot.getIvfxShot().setImageViewFirst(tagShot.getIvfxShot().getImageViewFirst());
                        shot.getIvfxShot().setImageViewLast(tagShot.getIvfxShot().getImageViewLast());
                        shot.getIvfxShot().setLabelFirst(tagShot.getIvfxShot().getLabelFirst());
                        shot.getIvfxShot().setLabelLast(tagShot.getIvfxShot().getLabelLast());
                    }
                }
            }

            // Обновляем список сцен
            listEvents = FXCollections.observableArrayList(IVFXTagsEvents.loadListEvents(currentFile, true));
            tblEvents.setItems(listEvents);

            tblShots.refresh();
            tblSceneShots.refresh();

            for (IVFXShots shot: listShots) {
                if (shot.getId() == currentShot.getId()) {
                    currentShot = shot;
                    tblShots.getSelectionModel().select(currentShot);
                    tblShotsSmartScrollToCurrent();
                }
            }
            doOnSelectRecordInTblShots();

        }
    }

    // Метод расширения/сужения события
    private void doAfterExpandCollapseEvent() {
        if (currentEvent != null && currentEventShot != null) {
            List<IVFXTagsEvents> listSelected = tblEvents.getSelectionModel().getSelectedItems();
            List<IVFXTags> listTags = new ArrayList<>();
            for (IVFXTagsEvents tagEvent: listSelected) {
                listTags.add(tagEvent);
            }
            listEventShots = FXCollections.observableArrayList(IVFXTagsShots.loadList(listTags, true));
            tblEventShots.setItems(listEventShots);

            List<IVFXTagsShots> lstToUpdateShots = IVFXTagsShots.loadList(listTags, true);
            List<IVFXTagsShots> lstToUpdateEventsShots = IVFXTagsShots.loadList(listTags, true);

            for (IVFXShots shot: listShots) {
                for (IVFXTagsShots tagShot: lstToUpdateShots) {
                    if (shot.getId() == tagShot.getShotId()) {
                        shot.setImageViewFirst(tagShot.getIvfxShot().getImageViewFirst());
                        shot.setImageViewLast(tagShot.getIvfxShot().getImageViewLast());
                        shot.setLabelFirst(tagShot.getIvfxShot().getLabelFirst());
                        shot.setLabelLast(tagShot.getIvfxShot().getLabelLast());
                    }
                }
            }

            for (IVFXTagsShots shot: listSceneShots) {
                for (IVFXTagsShots tagShot: lstToUpdateEventsShots) {
                    if (shot.getId() == tagShot.getShotId()) {
                        shot.getIvfxShot().setImageViewFirst(tagShot.getIvfxShot().getImageViewFirst());
                        shot.getIvfxShot().setImageViewLast(tagShot.getIvfxShot().getImageViewLast());
                        shot.getIvfxShot().setLabelFirst(tagShot.getIvfxShot().getLabelFirst());
                        shot.getIvfxShot().setLabelLast(tagShot.getIvfxShot().getLabelLast());
                    }
                }
            }

            tblShots.refresh();
            tblSceneShots.refresh();

            for (IVFXTagsShots tmp: listEventShots) {
                if (tmp.isEqual(currentEventShot)) {
                    currentEventShot = tmp;
                    tblEventShots.getSelectionModel().select(tmp);
                    doOnSelectRecordInTblEventShots();
                }
            }
        }
    }

    // Событие нажатия кнопки "Расширить вниз"
    @FXML
    void doBtnEventExtendDown(ActionEvent event) {
        if (currentEvent != null && currentEventShot != null) {
            currentEventShot = IVFXTagsEvents.eventShotExpandCollapse(currentEvent,currentEventShot,true,false,true);
            doAfterExpandCollapseEvent();
        }
    }

    // Событие нажатия кнопки "Расширить вверх"
    @FXML
    void doBtnEventExtendUp(ActionEvent event) {
        if (currentEvent != null && currentEventShot != null) {
            currentEventShot = IVFXTagsEvents.eventShotExpandCollapse(currentEvent,currentEventShot,true,true,true);
            doAfterExpandCollapseEvent();
        }
    }

    // Событие нажатия кнопки "Сузить вниз"
    @FXML
    void doBtnEventCollapseDown(ActionEvent event) {
        if (currentEvent != null && currentEventShot != null) {
            currentEventShot = IVFXTagsEvents.eventShotExpandCollapse(currentEvent,currentEventShot,false,false,true);
            doAfterExpandCollapseEvent();
        }
    }

    // Событие нажатия кнопки "Сузить вверх"
    @FXML
    void doBtnEventCollapseUp(ActionEvent event) {
        if (currentEvent != null && currentEventShot != null) {
            currentEventShot = IVFXTagsEvents.eventShotExpandCollapse(currentEvent,currentEventShot,false,true,true);
            doAfterExpandCollapseEvent();
        }
    }

    // Событие нажатия кнопки "Добавить тэг к событию
    @FXML
    void doBtnAddTagToEvent(ActionEvent event) {
        if (currentScene != null) {
            List<Integer> listTagTypesId = new ArrayList<>();
            listTagTypesId.add(0);
            listTagTypesId.add(1);
            listTagTypesId.add(2);
            IVFXTags tag = TagsGetController.getTag(currentProject, IVFXTagsTypes.load(0),listTagTypesId,null);
            boolean isAlreadyPresent = false;
            for (IVFXTags tmp: listTagsEvent) {
                if (tmp.isEqual(tag)) {
                    isAlreadyPresent = true;
                    break;
                }
            }
            if (!isAlreadyPresent) {
                IVFXTagsTags.getNewDbInstance(currentEvent, tag, true);
                doOnSelectRecordInTblEvents();
            }
        }
    }

    // Событие нажатия кнопки doBtnCreateNewEvent - создание новой сцены на основе выбранного плана
    @FXML
    void doBtnCreateNewEvent(ActionEvent event) {

        if (currentShot != null && currentProject != null && currentFile != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Создание нового события");
            alert.setHeaderText("Вы действительно хотите создать новое событие на основе выбранного плана?");
            alert.setContentText("Вы уверены?");
            Optional<ButtonType> option = alert.showAndWait();
            if (option.get() == ButtonType.OK) {

                boolean confirm = true;
                if (currentShot.isBodyEvent()) {

                    confirm = false;
                    alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Перекрестное событие");
                    alert.setHeaderText("План, на основе которого вы хотите создать новое событие, уже присутствует в другом событии.");
                    alert.setContentText("При создании события на основе этого плана возникнут перекрестные события, в которых планы будут повторятся.\nВы уверены, что хотите допустить перекрестные события?");
                    Optional<ButtonType> option1 = alert.showAndWait();
                    if (option1.get() == ButtonType.OK) {
                        confirm = true;
                    }

                }

                if (confirm) {
                    currentEvent = IVFXTags.getNewDbInstance(currentProject, IVFXEnumTagsTypes.EVENT);
                    currentEventShot = IVFXTagsShots.getNewDbInstance(currentEvent, currentShot);

                    listEvents = FXCollections.observableArrayList(IVFXTagsEvents.loadListEvents(currentFile, true));
                    tblEvents.setItems(listEvents);
                    for (IVFXTags tmp : listEvents) {
                        if (tmp.isEqual(currentEvent)) {
                            currentEvent = tmp;
                            tblEvents.getSelectionModel().select((IVFXTagsEvents)currentEvent);
                            doOnSelectRecordInTblEvents();
                            break;
                        }
                    }
                }

            }
        }

    }


    // Событие нажатия кнопки doBtnAddNewTagShot - Добавление тэга к плану
    @FXML
    void doBtnAddNewTagShot(ActionEvent event) {

        if (currentShot != null) {
            List<Integer> listTagTypesId = new ArrayList<>();
            listTagTypesId.add(0);
            listTagTypesId.add(1);
            listTagTypesId.add(2);
            IVFXTags tag = TagsGetController.getTag(currentProject, IVFXTagsTypes.load(1),listTagTypesId,null);
            boolean isAlreadyPresent = false;
            for (IVFXTagsShots tmp: listTagsShots) {
                if (tmp.getIvfxTag().isEqual(tag)) {
                    isAlreadyPresent = true;
                    break;
                }
            }
            if (!isAlreadyPresent) {
                IVFXTagsShots.getNewDbInstance(tag,currentShot);
                doOnSelectRecordInTblShots();
            }
        }

    }

    // Событие нажатие кнопки doBtnDeleteEvent - удаление события
    @FXML
    void doBtnDeleteEvent(ActionEvent event) {
        if (currentEvent != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Удаление события");
            alert.setHeaderText("Вы действительно хотите удалить событие с именем «" + currentEvent.getName() + "»?");
            alert.setContentText("В случае утвердительного ответа событие будет удалено из базы данных и его восстановление будет невозможно.\nВы уверены?");
            Optional<ButtonType> option = alert.showAndWait();
            if (option.get() == ButtonType.OK) {
                currentEvent.delete();
                listEvents = FXCollections.observableArrayList(IVFXTagsEvents.loadListEvents(currentFile, true));
                tblEvents.setItems(listEvents);
                currentEvent = null;
                doOnSelectRecordInTblEvents();
            }
        }
    }


    // Событие нажатие кнопки doBtnCreateNewTag - создание нового тэга персонажа или объекта и добавление его в текущий план
    @FXML
    void doBtnCreateNewTag(ActionEvent event) {

        IVFXEnumTagsTypes enumTagType = IVFXEnumTagsTypes.PERSON;
//        if (currentTagType != null) enumTagType = IVFXTagsTypes.getEnumTagsTypes(currentTagType.getId());
        String name = "New " + enumTagType;
        IVFXFrames ivfxFrame = IVFXFrames.load(currentFile, currentFrameToScroll, true);
        IVFXTags createdTag = CreateNewTagController.getNewTag(currentProject, enumTagType, name, ivfxFrame, false);

        if (createdTag != null) {
            int[] arr = {1,2};
            if (!(cbTagsAllOnlyFromHtml.isSelected() && currentFile != null)) {
                listTagsAll = FXCollections.observableArrayList(IVFXTags.loadList(currentProject,true, arr));
            } else {
                listTagsAll = FXCollections.observableArrayList(currentFile.getListTagsFromHTML());
            }
            tblTagsAll.setItems(listTagsAll);
            filteredTagsAll = new FilteredList<>(listTagsAll, e -> true);
            for (IVFXTags tag: listTagsAll) {
                if (tag.isEqual(createdTag)) {
                    currentTagAll = tag;
                    addTagToTagsShots(currentTagAll, currentShot);
                }
            }

        }
    }

    private void initContextMenuFrame() {

        MenuItem menuItem = new MenuItem("Add To Favorite");
        menuItem.setOnAction(e -> {addToFavorite();});

        contxtMenuFrame.getItems().clear();
        contxtMenuFrame.getItems().add(menuItem);

    }

    private void initContextMenuTagShotTypeSize() {

        contxtMenuTagShotTypeSize.getItems().clear();
        List<IVFXShotsTypeSize> list = IVFXShotsTypeSize.loadList(true);
        for (IVFXShotsTypeSize shotsTypeSize : list) {
            MenuItem menuItem = new MenuItem(shotsTypeSize.getName());
            menuItem.setOnAction(e -> {
                if (currentTagShot != null) {
                    currentTagShot.setTypeSizeId(shotsTypeSize.getId());
                    currentTagShot.save();
                    for (IVFXTagsShots tagShot : listTagsShots) {
                        if (tagShot.getId() == currentTagShot.getId()) {
                            tagShot.setTypeSizeId(shotsTypeSize.getId());
                            IVFXShotsTypeSize cloneTypeSize =  IVFXShotsTypeSize.load(shotsTypeSize.getId(), true);
                            tagShot.setIvfxShotTypeSize(cloneTypeSize);
                            tblTagsShots.refresh();
                            break;
                        }
                    }
                }
            });
            contxtMenuTagShotTypeSize.getItems().add(menuItem);
        }

    }

    private void initContextMenuTagShotTypeSizeForAll() {

        contxtMenuTagShotTypeSizeForAll.getItems().clear();
        List<IVFXShotsTypeSize> list = IVFXShotsTypeSize.loadList(true);
        for (IVFXShotsTypeSize shotsTypeSize : list) {
            MenuItem menuItem = new MenuItem(shotsTypeSize.getName() + " for All N/A");
            menuItem.setOnAction(e -> {
                if (currentShot != null) {
                    for (IVFXTagsShots tagShot : listTagsShots) {
                        if (tagShot.getTypeSizeId() == 0) {
                            tagShot.setTypeSizeId(shotsTypeSize.getId());
                            IVFXShotsTypeSize cloneTypeSize =  IVFXShotsTypeSize.load(shotsTypeSize.getId(), true);
                            tagShot.setIvfxShotTypeSize(cloneTypeSize);
                            tagShot.save();
                        }
                    }
                    tblTagsShots.refresh();
                }
            });
            contxtMenuTagShotTypeSizeForAll.getItems().add(menuItem);
        }

    }


    private void addToFavorite() {

        IVFXFrames ivfxFrame = IVFXFrames.load(currentFile, currentFrameToScroll, true);
        String pathToFavoriteFolder = currentFile.getFolderFavorite();
        String pathToFavoriteFile = pathToFavoriteFolder + "\\" + ivfxFrame.getFileNameFullSizeWithoutFolder();
        try {
            FileUtils.copyFile(new File(ivfxFrame.getFileNameFullSize()), new File(pathToFavoriteFile));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @FXML
    void doBtnUpdateShotTagsByFaces(ActionEvent event) {
        if (currentFile != null) {
            currentFile.updateTagsByFaces(true);
        } else {
            if (currentProject != null) {
                for (IVFXFiles ivfxFile : IVFXFiles.loadList(currentProject)) {
                    ivfxFile.updateTagsByFaces(false);
                }
            }
        }
    }

    @FXML
    void doBtnKeepShotsWithZeroTagSize(ActionEvent event) {

        Statement statement = null;
        ResultSet rs = null;
        String sql;

        try {
            statement = Main.mainConnection.createStatement();

            sql = "SELECT tbl_tags_shots.shot_id FROM tbl_shots INNER JOIN tbl_tags_shots ON tbl_shots.id = tbl_tags_shots.shot_id INNER JOIN tbl_tags ON tbl_tags_shots.tag_id = tbl_tags.id" +
                    " WHERE tbl_tags_shots.tag_shot_type_size_id = 0" +
                    " AND tbl_shots.file_id = " + currentFile.getId() +
                    " AND tbl_tags.tag_type_id = 1" +
                    " GROUP BY tbl_tags_shots.shot_id";

            rs = statement.executeQuery(sql);

            List<Integer> list = new ArrayList<>();

            while (rs.next()) {
                list.add(rs.getInt("shot_id"));
            }

            ObservableList<IVFXShots> listShotsTmp = FXCollections.observableArrayList();

            for (IVFXShots shot : listShots) {
                boolean isFound = false;
                for (Integer i : list) {
                    if (shot.getId() == i) {
                        isFound = true;
                        break;
                    }
                }
                if (isFound) {
                    listShotsTmp.add(shot);
                }
            }
            listShots = listShotsTmp;
            tblShots.setItems(listShots);


        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close(); // close result set
                if (statement != null) statement.close(); // close statement
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }


    }


    @FXML
    void doTagsAllOnlyFromHtml(ActionEvent event) {

        int[] arr = {1,2};
        if (!(cbTagsAllOnlyFromHtml.isSelected() && currentFile != null)) {
            listTagsAll = FXCollections.observableArrayList(IVFXTags.loadList(currentProject,true, arr));
        } else {
            listTagsAll = FXCollections.observableArrayList(currentFile.getListTagsFromHTML());
        }
        tblTagsAll.setItems(listTagsAll);
    }

}
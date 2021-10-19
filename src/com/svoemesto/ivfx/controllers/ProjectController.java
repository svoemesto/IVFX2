package com.svoemesto.ivfx.controllers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.svoemesto.ivfx.tables.*;
import com.svoemesto.ivfx.utils.*;

import java.io.*;
import java.net.URL;
import java.util.*;

import com.sun.javafx.scene.control.skin.TableViewSkin;
import com.sun.javafx.scene.control.skin.VirtualFlow;
import com.svoemesto.ivfx.Main;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

public class ProjectController extends Application {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private AnchorPane filePane;

    @FXML
    private MenuItem menuNewProject;

    @FXML
    private MenuItem menuOpenProject;

    @FXML
    private MenuItem menuDeleteProject;

    @FXML
    private MenuItem menuExit;

    @FXML
    private AnchorPane projectPane;

    @FXML
    private TextField prjName;

    @FXML
    private TextField prjShortName;

    @FXML
    private TextField prjFolder;

    @FXML
    private Button btnBrowsePrjFolder;

    @FXML
    private TextField fldPrjVideoWidth;

    @FXML
    private TextField fldPrjVideoHeight;

    @FXML
    private TextField fldPrjVideoFPS;

    @FXML
    private TextField fldPrjVideoBitrate;

    @FXML
    private ComboBox<String> cbPrjVideoCodec;

    @FXML
    private ComboBox<String> cbPrjVideoContainer;

    @FXML
    private TextField fldPrjAudioBitrate;

    @FXML
    private TextField fldPrjAudioFreq;

    @FXML
    private ComboBox<String> cbPrjAudioCodec;

    @FXML
    private TableView<IVFXFiles> tblFiles;

    @FXML
    private TableColumn<IVFXFiles, Integer> colOrder;

    @FXML
    private TableColumn<IVFXFiles, String> colName;

    @FXML
    private Button btnTop;

    @FXML
    private Button btnUp;

    @FXML
    private Button btnDown;

    @FXML
    private Button btnBottom;

    @FXML
    private Button btnAddFile;

    @FXML
    private Button btnAddFilesInFolder;

    @FXML
    private Button btnDelFile;

    @FXML
    private Button btnPersons;

    @FXML
    private Button btnFilters;

    @FXML
    private Button btnFiltersTags;

    @FXML
    private Button btnFaceDetect;

    @FXML
    private TextField ctlFileSourceName;

    @FXML
    private Button btnBrowseFile;

    @FXML
    private TextField ctlFileShortName;

    @FXML
    private TextField ctlFileTitle;

    @FXML
    private TextField ctlFileSeasonNum;

    @FXML
    private TextField ctlFileEpisodeNum;

    @FXML
    private TextField ctlFileFrameRate;

    @FXML
    private TextField ctlFileWidth;

    @FXML
    private TextField ctlFileHeight;

    @FXML
    private TextField ctlFileFramesCount;

    @FXML
    private TextField ctlFileDuration;

    @FXML
    private Button btnGetMediaInfo;

    @FXML
    private TextField ctlFileDescription;


    @FXML
    private TableView<IVFXFiles> tblFileToAction;

    @FXML
    private TableColumn<IVFXFiles, Integer> colOrderAction;

    @FXML
    private TableColumn<IVFXFiles, String> colNameAction;

    @FXML
    private CheckBox checkCreateFaces;

    @FXML
    private CheckBox checkCreatePreviewMP4;

    @FXML
    private CheckBox checkCreateFramesPreview;

    @FXML
    private CheckBox checkCreateFramesMedium;

    @FXML
    private CheckBox checkCreateFramesFull;

    @FXML
    private CheckBox checkClearShots;

    @FXML
    private CheckBox checkCreateShotsMXFaudioYES;

    @FXML
    private CheckBox checkCreateShotsMXFaudioNO;

    @FXML
    private CheckBox checkAnalize;

    @FXML
    private CheckBox checkFindTransitions;

    @FXML
    private CheckBox checkCreateShots;

    @FXML
    private CheckBox checkCreateLossless;

    @FXML
    private CheckBox checkDeleteLossless;

    @FXML
    private CheckBox checkAddFacesToDatabase;

    @FXML
    private CheckBox checkRunCmd;

    @FXML
    private Button btnDoActions;

    @FXML
    private Button btnTransitions;

    @FXML
    private Button btnShots;

    @FXML
    private CheckBox checkFolderPreviewMP4;

    @FXML
    private TextField fldFolderPreviewMP4;

    @FXML
    private Button btnFolderPreviewMP4;

    @FXML
    private CheckBox checkFolderPreviewFrames;

    @FXML
    private CheckBox checkFolderMediumFrames;


    @FXML
    private TextField fldFolderPreviewFrames;

    @FXML
    private TextField fldFolderMediumFrames;


    @FXML
    private Button btnFolderPreviewFrames;

    @FXML
    private Button btnFolderMediumFrames;


    @FXML
    private CheckBox checkFolderFullFrames;

    @FXML
    private TextField fldFolderFullFrames;

    @FXML
    private Button btnFolderFullFrames;

    @FXML
    private CheckBox checkFolderLossless;

    @FXML
    private TextField fldFolderLossless;

    @FXML
    private Button btnFolderLossless;

    @FXML
    private CheckBox checkFolderFavorite;

    @FXML
    private TextField fldFolderFavorite;

    @FXML
    private Button btnFolderFavorite;

    @FXML
    private CheckBox checkFolderShots;

    @FXML
    private TextField fldFolderShots;

    @FXML
    private Button btnFolderShots;

    @FXML
    private ComboBox<String> cbLosslessVideoCodec;

    @FXML
    private ComboBox<String> cbLosslessContainer;

    @FXML
    private TableView<IVFXFilesTracks> tblFilesTracks;

    @FXML
    private TableColumn<IVFXFilesTracks, Boolean> colFileTrackUse;

    @FXML
    private TableColumn<IVFXFilesTracks, String> colFileTrackType;

    @FXML
    private TableView<IVFXFilesTracksProperties> tblFilesTracksProperties;

    @FXML
    private TableColumn<IVFXFilesTracksProperties, String> colFileTrackPropertyKey;

    @FXML
    private TableColumn<IVFXFilesTracksProperties, String> colFileTrackPropertyValue;

    @FXML
    private TextArea ctlConsole;

    @FXML
    private Button btnTagsShots;

    @FXML
    private Button btnTags;

    @FXML
    private TableView<IVFXFilesProperties> tblFilesProperties;

    @FXML
    private TableColumn<IVFXFilesProperties, String> colNameTblFilesProperties;

    @FXML
    private TableColumn<IVFXFilesProperties, String> colValueTblFilesProperties;

    @FXML
    private TextField fldFilePropertyName;

    @FXML
    private TextArea fldFilePropertyValue;

    @FXML
    private Button btnAddNewFilesProperties;

    @FXML
    private Button btnDeleteFilesProperties;

    @FXML
    private Button btnAddNewFilesPropertiesUrl;

    @FXML
    private Button btnAddNewFilesPropertiesHtml;


    public static IVFXProjects mainProject;
    public static IVFXFiles currentFile;
    public static IVFXFilesTracks currentFileTrack;
    public static IVFXFilesProperties currentFileProperty;

    private ObservableList<IVFXFiles> listFiles = FXCollections.observableArrayList();
    private ObservableList<IVFXFilesTracks> listFilesTracks = FXCollections.observableArrayList();
    private ObservableList<IVFXFilesTracksProperties> listFilesTracksProperties = FXCollections.observableArrayList();
    private VirtualFlow flowTblFileToAction;

    private ObservableList<String> listVideoCodecs = FXCollections.observableArrayList();
    private ObservableList<String> listLosslessVideoCodecs = FXCollections.observableArrayList();
    private ObservableList<String> listAudioCodecs = FXCollections.observableArrayList();
    private ObservableList<String> listVideoContainers = FXCollections.observableArrayList();
    private ObservableList<String> listLosslessContainers = FXCollections.observableArrayList();
    private ObservableList<IVFXFilesProperties> listFilesProperties = FXCollections.observableArrayList();


    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        Main.mainConnection = Database.getConnection();
        Main.mainWindow = primaryStage;

//        IVFXProjects project = IVFXProjects.load(1);
//        IVFXProjects project = IVFXProjects.load(2);

        IVFXProjects project = OpenDialogController.getProject();
//        if (project != null) mainProject = project;
//        currentFile = null;
//        initialize();

        if (project != null) {
            mainProject = project;
            Parent root = FXMLLoader.load(getClass().getResource("../resources/fxml/project.fxml"));
            primaryStage.setTitle("Interactive Video FX © svoёmesto");
            Scene scene = new Scene(root, 800, 800);
            primaryStage.setScene(scene);
            primaryStage.show();
            saveCurrentFile();
        }


    }

    public void appendTextToConsoleControl(String str) {
        Platform.runLater(() -> ctlConsole.appendText(str));
    }

    @FXML
    void initialize() {

//        OutputStream out = new OutputStream() {
//            @Override
//            public void write(int b) throws IOException {
//                appendTextToConsoleControl(String.valueOf((char) b));
//            }
//        };
//        System.setOut(new PrintStream(out, true));

        // Устанавливаем видимость/доступность элементов
        projectPane.setVisible(!(mainProject == null));     // область проекта не видна, если проекта нет
        menuDeleteProject.setDisable(mainProject == null);  // меню menuDeleteProject недоступно, если проекта нет
        filePane.setVisible(!(currentFile == null));    // область файла не видна, если файл не выбран
        setEnabledFilesButtons();

        listVideoContainers.clear();
        listVideoContainers.add("mp4");
        listVideoContainers.add("mkv");
        listVideoContainers.add("mxf");
        cbPrjVideoContainer.setItems(listVideoContainers);

        listLosslessContainers.clear();
        listLosslessContainers.add("mp4");
        listLosslessContainers.add("mkv");
        listLosslessContainers.add("mxf");
        cbLosslessContainer.setItems(listLosslessContainers);

        listVideoCodecs.clear();
        listVideoCodecs.add("libx264");
        listVideoCodecs.add("dnxhd");
        cbPrjVideoCodec.setItems(listVideoCodecs);

        listLosslessVideoCodecs.clear();
        listLosslessVideoCodecs.add("rawvideo");
        listLosslessVideoCodecs.add("dnxhd");
        cbLosslessVideoCodec.setItems(listLosslessVideoCodecs);

        listAudioCodecs.clear();
        listAudioCodecs.add("aac");
        listAudioCodecs.add("ac3");
        listAudioCodecs.add("mp3");
        cbPrjAudioCodec.setItems(listAudioCodecs);

        // Устанавливаем tblFileToAction возможность мультивыбора строк
        tblFileToAction.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        colNameTblFilesProperties.setCellValueFactory(new PropertyValueFactory<>("name"));
        colValueTblFilesProperties.setCellValueFactory(new PropertyValueFactory<>("value"));
        
        if (mainProject != null) {

            // Заполняем поля формы для проекта: prjName, prjShortName, prjFolder etc.
            prjName.setText(mainProject.getName());
            prjShortName.setText(mainProject.getShortName());
            prjFolder.setText(mainProject.getFolder());
            fldPrjVideoWidth.setText(String.valueOf(mainProject.getVideoWidth()));
            fldPrjVideoHeight.setText(String.valueOf(mainProject.getVideoHeight()));
            fldPrjVideoFPS.setText(String.valueOf(mainProject.getVideoFPS()));
            fldPrjVideoBitrate.setText(String.valueOf(mainProject.getVideoBitrate()));
            fldPrjAudioBitrate.setText(String.valueOf(mainProject.getAudioBitrate()));
            fldPrjAudioFreq.setText(String.valueOf(mainProject.getAudioFreq()));
            cbPrjVideoContainer.getSelectionModel().select(mainProject.getVideoContainer());
            cbPrjVideoCodec.getSelectionModel().select(mainProject.getVideoCodec());
            cbPrjAudioCodec.getSelectionModel().select(mainProject.getAudioCodec());



            // загружаем список файлов проекта
            listFiles = FXCollections.observableArrayList(IVFXFiles.loadList(mainProject));

            // Заполняем таблицу файлов и столбцы этой таблицы
            colOrder.setCellValueFactory(new PropertyValueFactory<>("order"));
            colName.setCellValueFactory(new PropertyValueFactory<>("title"));
            tblFiles.setItems(listFiles);

            // Заполняем таблицу файлов для действий и столбцы этой таблицы
            colOrderAction.setCellValueFactory(new PropertyValueFactory<>("order"));
            colNameAction.setCellValueFactory(new PropertyValueFactory<>("title"));
            tblFileToAction.setItems(listFiles);

        }

        // изменение поля "Name"
        prjName.textProperty().addListener((observable, oldValue, newValue) -> {
            mainProject.setName(prjName.getText());
            mainProject.save();
        });

        // изменение поля "ShortName"
        prjShortName.textProperty().addListener((observable, oldValue, newValue) -> {
            mainProject.setShortName(prjShortName.getText());
            mainProject.save();
        });

        // изменение поля "Folder"
        prjFolder.textProperty().addListener((observable, oldValue, newValue) -> {
            mainProject.setFolder(prjFolder.getText());
            mainProject.save();
        });

        // изменение поля "VideoWidth"
        fldPrjVideoWidth.textProperty().addListener((observable, oldValue, newValue) -> {
            mainProject.setVideoWidth(Integer.parseInt(fldPrjVideoWidth.getText()));
            mainProject.save();
        });

        // изменение поля "VideoHeight"
        fldPrjVideoHeight.textProperty().addListener((observable, oldValue, newValue) -> {
            mainProject.setVideoHeight(Integer.parseInt(fldPrjVideoHeight.getText()));
            mainProject.save();
        });

        // изменение поля "VideoBitrate"
        fldPrjVideoBitrate.textProperty().addListener((observable, oldValue, newValue) -> {
            mainProject.setVideoBitrate(Integer.parseInt(fldPrjVideoBitrate.getText()));
            mainProject.save();
        });

        // изменение поля "VideoFPS"
        fldPrjVideoFPS.textProperty().addListener((observable, oldValue, newValue) -> {
            mainProject.setVideoFPS(Double.parseDouble(fldPrjVideoFPS.getText()));
            mainProject.save();
        });

        // изменение поля "AudioBitrate"
        fldPrjAudioBitrate.textProperty().addListener((observable, oldValue, newValue) -> {
            mainProject.setAudioBitrate(Integer.parseInt(fldPrjAudioBitrate.getText()));
            mainProject.save();
        });

        // изменение поля "AudioFreq"
        fldPrjAudioFreq.textProperty().addListener((observable, oldValue, newValue) -> {
            mainProject.setAudioFreq(Integer.parseInt(fldPrjAudioFreq.getText()));
            mainProject.save();
        });

        // изменение комбобокса "VideoCodec"
        cbPrjVideoCodec.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            mainProject.setVideoCodec(newValue);
            mainProject.save();
        });

        // изменение комбобокса "VideoContainer"
        cbPrjVideoContainer.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            mainProject.setVideoContainer(newValue);
            mainProject.save();
        });

        // изменение комбобокса "AudioCodec"
        cbPrjAudioCodec.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            mainProject.setAudioCodec(newValue);
            mainProject.save();
        });

        // изменение комбобокса "LosslessVideoCodec"
        cbLosslessVideoCodec.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            currentFile.setLosslessVideoCodec(newValue);
            currentFile.save();
        });

        // изменение комбобокса "LosslessContainer"
        cbLosslessContainer.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            currentFile.setLosslessContainer(newValue);
            currentFile.save();
        });

        // изменение поля "Title"
        ctlFileTitle.textProperty().addListener((observable, oldValue, newValue) -> {
            currentFile.setTitle(ctlFileTitle.getText());
            for (IVFXFiles file: listFiles) {
                if (file.getId() == currentFile.getId()) {
                    file.setTitle(ctlFileTitle.getText());
                    break;
                }
            }
            tblFiles.refresh();
            tblFileToAction.refresh();
            currentFile.save();
        });

        // выбор файла в таблице файлов
        tblFiles.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                System.out.println(newValue);
                for (IVFXFiles ivfxFiles : listFiles) {
                    if (newValue.equals(ivfxFiles)) {
                        saveCurrentFile();
                        currentFile = ivfxFiles;                                                                        // устанавливаем выбранный файл как текущий
                        setFileFields();
                        filePane.setVisible(true);                                                                      // делаем видимой область элементов файла
                        setEnabledFilesButtons();                                                                       // устанавливаем доступ к кнопкам сортировки файлов
                        tblFileToAction.getSelectionModel().clearSelection();                                           // сбрасываем выбор в таблице tblFileToAction
                        tblFileToAction.getSelectionModel().select(currentFile);                                        // выбираем текущий файл в таблице tblFileToAction

                        // если в таблице tblFileToAction есть записи на экране
                        if (flowTblFileToAction != null && flowTblFileToAction.getCellCount() > 0) {
                            int first = flowTblFileToAction.getFirstVisibleCell().getIndex();                           // находим первую видимую строку
                            int last = flowTblFileToAction.getLastVisibleCell().getIndex();                             // находим последнюю видимую строку
                            int selected = tblFileToAction.getSelectionModel().getSelectedIndex();                      // находим выбранную строку (она может быть за границей видимости)
                            // если выбранная строка находится за границей видимости
                            if (selected < first || selected > last) {
                                tblFileToAction.scrollTo(currentFile);                                                  // проматываем таблицу tblFileToAction так, чтобы выбранная строка стала первой видимой
                            }
                        }

                        // загружаем список свойств для выбранного файла и привязываем к нему таблицу tblFilesProperties
                        listFilesProperties = FXCollections.observableArrayList(IVFXFilesProperties.loadList(currentFile));
                        tblFilesProperties.setItems(listFilesProperties);

                        // Сбрасываем выбранные ранее свойства
                        currentFileProperty = null;
                        fldFilePropertyName.setText("");
                        fldFilePropertyValue.setText("");
                        fldFilePropertyName.setDisable(true);
                        fldFilePropertyValue.setDisable(true);
                        btnDeleteFilesProperties.setDisable(true);
                        
                        
                    }
                }
            }
        });

        // событие отслеживани видимости на экране текущего файла в таблице tblFileToAction
        tblFileToAction.skinProperty().addListener((ChangeListener<Skin>) (ov, t, t1) -> {
            if (t1 == null) {
                return;
            }

            TableViewSkin tvs = (TableViewSkin) t1;
            ObservableList<Node> kids = tvs.getChildren();//    getChildrenUnmodifiable();

            if (kids == null || kids.isEmpty()) {
                return;
            }
            flowTblFileToAction = (VirtualFlow) kids.get(1);
        });

        // выбор файла в таблице треков
        tblFilesTracks.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                System.out.println(newValue);
                for (IVFXFilesTracks ivfxFilesTracks : listFilesTracks) {
                    if (newValue.equals(ivfxFilesTracks)) {
                        currentFileTrack = ivfxFilesTracks;                                                                        // устанавливаем выбранный файл как текущий

                        System.out.println("currentFileTrack = " + currentFileTrack);
                        // загружаем список пропертей треков
                        listFilesTracksProperties = FXCollections.observableArrayList(IVFXFilesTracksProperties.loadList(currentFileTrack));

                        // Заполняем таблицу пропертей треков и столбцы этой таблицы
                        colFileTrackPropertyKey.setCellValueFactory(new PropertyValueFactory<>("file_track_property_key"));
                        colFileTrackPropertyValue.setCellValueFactory(new PropertyValueFactory<>("file_track_property_value"));
                        tblFilesTracksProperties.setItems(listFilesTracksProperties);

                    }
                }
            }
        });

        // даблклик к треке
        tblFilesTracks.setOnMouseClicked(me -> {
            if(me.getButton().equals(MouseButton.PRIMARY)){
                if(me.getClickCount() == 1) {

                }
                if(me.getClickCount() == 2){
                    System.out.println(currentFileTrack);
                    if (currentFileTrack != null) {
                        if (!currentFileTrack.getType().equals("General") && !currentFileTrack.getType().equals("Video")) {
                            for (int i = 0; i <= listFilesTracks.size()-1; i++) {
                                IVFXFilesTracks ivfxFilesTracks = listFilesTracks.get(i);
                                if (currentFileTrack.equals(ivfxFilesTracks)) {
                                    currentFileTrack.setUse(!currentFileTrack.isUse());
                                    currentFileTrack.save();
                                    listFilesTracks.set(i, currentFileTrack);
                                    tblFilesTracks.refresh();
                                    break;
                                }
                            }

                        }
                    }
                }
            } else {
                if (me.getButton().equals(MouseButton.SECONDARY)){
                    if(me.getClickCount() == 1){

                    }
                }
            }
        });

        // обработка события изменение поля fldFilePropertyValue
        fldFilePropertyValue.textProperty().addListener((observable, oldValue, newValue) -> {
            doFldFilePropertyValue();
        });

        // делаем поле Value таблицы tblFilesProperties с переносом по словам и расширяемым по высоте
        colValueTblFilesProperties.setCellFactory(param -> {
            TableCell<IVFXFilesProperties, String> cell = new TableCell<>();
            Text text = new Text();
            text.setStyle("");
            cell.setGraphic(text);
            cell.setPrefHeight(Control.USE_COMPUTED_SIZE);
            text.textProperty().bind(cell.itemProperty());
            text.wrappingWidthProperty().bind(colValueTblFilesProperties.widthProperty());
            return cell;
        });

        // обработка события выбора записи в таблице tblFilesProperties
        tblFilesProperties.getSelectionModel().selectedItemProperty().addListener((v, oldValue, newValue) -> {
            if (newValue != null) {
                currentFileProperty = newValue;
                doOnSelectRecordInTblFilesProperties();
            }
        });
        
    }

    private void doFldFilePropertyValue() {
        if (currentFileProperty != null) {
            currentFileProperty.setValue(fldFilePropertyValue.getText());
            currentFileProperty.save();
            tblFilesProperties.refresh();
            tblFiles.refresh();
        }
    }

    private void saveCurrentFile() {
        if (currentFile != null) {
            boolean needToSave = false;
            String tmp = "";

            tmp = ctlFileSourceName.getText();
            if (!tmp.equals(String.valueOf(currentFile.getSourceName()))) {
                currentFile.setSourceName(tmp);
                needToSave = true;
            }

            tmp = ctlFileShortName.getText();
            if (!tmp.equals(String.valueOf(currentFile.getShortName()))) {
                currentFile.setShortName(tmp);
                needToSave = true;
            }

            tmp = ctlFileTitle.getText();
            if (!tmp.equals(String.valueOf(currentFile.getTitle()))) {
                currentFile.setTitle(tmp);
                needToSave = true;
            }

            tmp = ctlFileSeasonNum.getText();
            if (!tmp.equals(String.valueOf(currentFile.getSeasonNumber()))) {
                currentFile.setSeasonNumber(Integer.parseInt(tmp));
                needToSave = true;
            }

            tmp = ctlFileEpisodeNum.getText();
            if (!tmp.equals(String.valueOf(currentFile.getEpisodeNumber()))) {
                currentFile.setEpisodeNumber(Integer.parseInt(tmp));
                needToSave = true;
            }

            tmp = ctlFileFrameRate.getText();
            if (!tmp.equals(String.valueOf(currentFile.getFrameRate()))) {
                currentFile.setFrameRate(Double.parseDouble(tmp));
                needToSave = true;
            }

            tmp = ctlFileWidth.getText();
            if (!tmp.equals(String.valueOf(currentFile.getWidth()))) {
                currentFile.setWidth(Integer.parseInt(tmp));
                needToSave = true;
            }

            tmp = ctlFileHeight.getText();
            if (!tmp.equals(String.valueOf(currentFile.getHeight()))) {
                currentFile.setHeight(Integer.parseInt(tmp));
                needToSave = true;
            }

            tmp = ctlFileFramesCount.getText();
            if (!tmp.equals(String.valueOf(currentFile.getFramesCount()))) {
                currentFile.setFramesCount(Integer.parseInt(tmp));
                needToSave = true;
            }

            tmp = ctlFileDuration.getText();
            if (!tmp.equals(String.valueOf(currentFile.getDuration()))) {
                currentFile.setDuration(Integer.parseInt(tmp));
                needToSave = true;
            }

            tmp = ctlFileDescription.getText();
            if (!tmp.equals(String.valueOf(currentFile.getDescription()))) {
                currentFile.setDescription(tmp);
                needToSave = true;
            }

            tmp = fldFolderPreviewMP4.getText();
            if (tmp != null && !tmp.equals(String.valueOf(currentFile.getFolderMp4()))) {
                currentFile.setFolderMp4(tmp);
                needToSave = true;
            }

            tmp = fldFolderPreviewFrames.getText();
            if (tmp != null && !tmp.equals(String.valueOf(currentFile.getFolderFramesPreview()))) {
                currentFile.setFolderFramesPreview(tmp);
                needToSave = true;
            }

            tmp = fldFolderMediumFrames.getText();
            if (tmp != null && !tmp.equals(String.valueOf(currentFile.getFolderFramesMedium()))) {
                currentFile.setFolderFramesMedium(tmp);
                needToSave = true;
            }


            tmp = fldFolderFullFrames.getText();
            if (tmp != null && !tmp.equals(String.valueOf(currentFile.getFolderFramesFull()))) {
                currentFile.setFolderFramesFull(tmp);
                needToSave = true;
            }

            tmp = fldFolderLossless.getText();
            if (tmp != null && !tmp.equals(String.valueOf(currentFile.getFolderLossless()))) {
                currentFile.setFolderLossless(tmp);
                needToSave = true;
            }

            tmp = fldFolderFavorite.getText();
            if (tmp != null && !tmp.equals(String.valueOf(currentFile.getFolderFavorite()))) {
                currentFile.setFolderFavorite(tmp);
                needToSave = true;
            }

            tmp = fldFolderShots.getText();
            if (tmp != null && !tmp.equals(String.valueOf(currentFile.getFolderShots()))) {
                currentFile.setFolderShots(tmp);
                needToSave = true;
            }

            boolean tmpBool = checkFolderPreviewMP4.isSelected();
            if (tmpBool != currentFile.isUseFolderMp4()) {
                currentFile.setUseFolderMp4(tmpBool);
                needToSave = true;
            }

            tmpBool = checkFolderPreviewFrames.isSelected();
            if (tmpBool != currentFile.isUseFolderFramesPreview()) {
                currentFile.setUseFolderFramesPreview(tmpBool);
                needToSave = true;
            }

            tmpBool = checkFolderMediumFrames.isSelected();
            if (tmpBool != currentFile.isUseFolderFramesMedium()) {
                currentFile.setUseFolderFramesMedium(tmpBool);
                needToSave = true;
            }


            tmpBool = checkFolderFullFrames.isSelected();
            if (tmpBool != currentFile.isUseFolderFramesFull()) {
                currentFile.setUseFolderFramesFull(tmpBool);
                needToSave = true;
            }

            tmpBool = checkFolderLossless.isSelected();
            if (tmpBool != currentFile.isUseFolderLossless()) {
                currentFile.setUseFolderLossless(tmpBool);
                needToSave = true;
            }

            tmpBool = checkFolderFavorite.isSelected();
            if (tmpBool != currentFile.isUseFolderFavorite()) {
                currentFile.setUseFolderFavorite(tmpBool);
                needToSave = true;
            }

            tmpBool = checkFolderShots.isSelected();
            if (tmpBool != currentFile.isUseFolderShots()) {
                currentFile.setUseFolderShots(tmpBool);
                needToSave = true;
            }

            if (needToSave) {
                currentFile.save();
            }

        }
    }

    @FXML
    void doMenuDeleteProject(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Удаление проекта");
        alert.setHeaderText("Вы действительно хотите удалить проект «" + mainProject.getName() + "»?");
        alert.setContentText("В случае утвердительного ответа проект будет удален из базы данных и его восстановление будет невозможно.\nВы уверены, что хотите удалить проект?");
        Optional<ButtonType> option = alert.showAndWait();
        if (option.get() == ButtonType.OK) {
            alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Удаление проекта - финальное подтверждение");
            alert.setHeaderText("Вы ТОЧНО хотите удалить проект «" + mainProject.getName() + "»?");
            alert.setContentText("ВОССТАНОВЛЕНИЕ БУДЕТ НЕВОЗМОЖНО!\nВы уверены???");
            option = alert.showAndWait();
            if (option.get() == ButtonType.OK) {
                mainProject.delete();
                mainProject = null;
                initialize();
            }
        }
    }

    @FXML
    void doMenuNewProject(ActionEvent event) {
        // получаем папку
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File dir = directoryChooser.showDialog(new Stage());
        // если папка указана
        if (dir != null) {
            saveCurrentFile();
            IVFXProjects ivfxProjects = IVFXProjects.getNewDbInstance();                                                // создаем запись проекта в базе данных
            ivfxProjects.setFolder(dir.getAbsolutePath());                                                              // присваиваем проекту путь
            ivfxProjects.setShortName(dir.getName());                                                                   // присваиваем проекту короткое имя по имени папки
            ivfxProjects.setName(dir.getName());                                                                        // присваиваем проекту имя по имени папки
            ivfxProjects.save();                                                                                        // сохраняем проект в БД
            mainProject = ivfxProjects;                                                                                 // устанавливаем текущий проект как только что созданный
            initialize();                                                                                               // инициализируем форму
        }
    }

    @FXML
    void doMoveCurrentFileUp(ActionEvent event) {
        for (IVFXFiles file: listFiles) {
            if (file.getOrder() == currentFile.getOrder()-1) {
                file.setOrder(currentFile.getOrder());
                file.save();
                currentFile.setOrder(currentFile.getOrder()-1);
                currentFile.save();
                Collections.sort(listFiles);
                setEnabledFilesButtons();
                break;
            }
        }
    }

    @FXML
    void doMoveCurrentFileDown(ActionEvent event) {
        for (IVFXFiles file: listFiles) {
            if (file.getOrder() == currentFile.getOrder()+1) {
                file.setOrder(currentFile.getOrder());
                file.save();
                currentFile.setOrder(currentFile.getOrder()+1);
                currentFile.save();
                Collections.sort(listFiles);
                setEnabledFilesButtons();
                break;
            }
        }
    }

    @FXML
    void doMoveCurrentFileTop(ActionEvent event) {
        for (IVFXFiles file: listFiles) {
            if (file.getOrder() < currentFile.getOrder()) {
                file.setOrder(file.getOrder()+1);
                file.save();
            }
        }
        currentFile.setOrder(1);
        currentFile.save();
        Collections.sort(listFiles);
        setEnabledFilesButtons();
    }

    @FXML
    void doMoveCurrentFileBottom(ActionEvent event) {
        for (IVFXFiles file: listFiles) {
            if (file.getOrder() > currentFile.getOrder()) {
                file.setOrder(file.getOrder()-1);
                file.save();
            }
        }
        currentFile.setOrder(listFiles.size());
        currentFile.save();
        Collections.sort(listFiles);
        setEnabledFilesButtons();
    }

    @FXML
    void doAddFile(ActionEvent event) {

        saveCurrentFile();
        // инициализируем диалог выбора файла
        String initialDirectory = "";
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Добавить файл к проекту");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("All Files", "*.*")
        );

        // если выбран текущий файл - назначаем его папку как стартовую для выбора нового файла
        if (currentFile != null) {
            initialDirectory = (new File(currentFile.getSourceName())).getParent();
        }
        if (!initialDirectory.equals("")) fileChooser.setInitialDirectory(new File(initialDirectory));

        // вызываем диалог выбора файла
        File file = fileChooser.showOpenDialog(new Stage());

        // если был выбран какой-нибудь файл
        if (file != null) {

            // получаем полный путь к этому файлу
            String fileSourceName = file.getAbsolutePath();

            // ищем, нет ли уже в списке файлов такого файла и если есть - просто встаем на него
            boolean isAlreadyPresent = false;
            for (IVFXFiles ivfxFile : listFiles) {
                if (ivfxFile.getSourceName().equals(fileSourceName)) {
                    isAlreadyPresent = true;
                    tblFiles.getSelectionModel().select(ivfxFile);
                    break;
                }
            }

            // если файла в списке нет - его надо добавить
            if (!isAlreadyPresent) {

                // создаем новый файл в БД
                IVFXFiles ivfxFile = IVFXFiles.getNewDbInstance(mainProject);

                // иницилизируем его поля
                ivfxFile.setSourceName(fileSourceName);
                ivfxFile.setIvfxProject(mainProject);
                ivfxFile.setProjectId(mainProject.getId());
                ivfxFile.setShortName(file.getName().substring(0,file.getName().lastIndexOf(".")));
                ivfxFile.setTitle(ivfxFile.getShortName());

                // пытаемся получить информацию о частоте кадров, длительности и кол-ве кадров из МедиаИнфо
                try {
                    ivfxFile.setMediaInfoJson(MediaInfo.executeMediaInfo(fileSourceName,"--Output=JSON"));

                    ivfxFile.setFrameRate(Double.parseDouble(MediaInfo.getInfoBySectionAndParameter(fileSourceName,"Video","FrameRate")));
                    ivfxFile.setDuration((int)Double.parseDouble(MediaInfo.getInfoBySectionAndParameter(fileSourceName,"Video","Duration")));
                    ivfxFile.setFramesCount(Integer.parseInt(MediaInfo.getInfoBySectionAndParameter(fileSourceName,"Video","FrameCount"))-1);
                    ivfxFile.setWidth(Integer.parseInt(MediaInfo.getInfoBySectionAndParameter(fileSourceName,"Video","Width")));
                    ivfxFile.setHeight(Integer.parseInt(MediaInfo.getInfoBySectionAndParameter(fileSourceName,"Video","Height")));
                    ctlFileFrameRate.setText(String.valueOf(ivfxFile.getFrameRate()));
                    ctlFileDuration.setText(String.valueOf(ivfxFile.getDuration()));
                    ctlFileFramesCount.setText(String.valueOf(ivfxFile.getFramesCount()));
                    ctlFileWidth.setText(String.valueOf(ivfxFile.getWidth()));
                    ctlFileHeight.setText(String.valueOf(ivfxFile.getHeight()));

                } catch (IOException | InterruptedException ex) {
                    System.out.println("Не удалось получить данные через MediaInfo");
                }

                ivfxFile.save();                                // сохраняем запись в БД
                ivfxFile.createTracksFromMediaInfo();
                listFiles.add(ivfxFile);                        // добавляем файл в список
                tblFiles.refresh();                             // обновляем таблицу файлов
                tblFileToAction.refresh();                      // обновляем таблицу файлов действий
                tblFiles.getSelectionModel().select(ivfxFile);  // "встаем" на этот файл в таблице файлов
            }

        }
    }

    @FXML
    void doAddFilesInFolder(ActionEvent event) {

        saveCurrentFile();

        String initialDirectory = "";
        DirectoryChooser directoryChooser = new DirectoryChooser();

        // если выбран текущий файл - назначаем его папку как стартовую для выбора нового файла
        if (currentFile != null) {
            initialDirectory = (new File(currentFile.getSourceName())).getParent();
        }
        if (!initialDirectory.equals("")) directoryChooser.setInitialDirectory(new File(initialDirectory));

        // вызываем диалог выбора папки
        File directorySelected = directoryChooser.showDialog(new Stage());

        // если папка выбрана
        if (directorySelected != null) {

            // проходимся по списку файлов в папке
            File[] fileList = directorySelected.listFiles();
            for (File file : fileList) {

                // получаем полный путь к файлу
                String fileSourceName = file.getAbsolutePath();

                // ищем, нет ли уже в списке файлов такого файла
                boolean isAlreadyPresent = false;
                for (IVFXFiles ivfxFile : listFiles) {
                    if (ivfxFile.getSourceName().equals(fileSourceName)) {
                        isAlreadyPresent = true;
                        break;
                    }
                }

                // если файла в списке нет - его надо добавить
                if (!isAlreadyPresent) {

                    // создаем новый файл в БД
                    IVFXFiles ivfxFile = IVFXFiles.getNewDbInstance(mainProject);

                    // иницилизируем его поля
                    ivfxFile.setSourceName(fileSourceName);
                    ivfxFile.setIvfxProject(mainProject);
                    ivfxFile.setProjectId(mainProject.getId());
                    ivfxFile.setShortName(file.getName().substring(0,file.getName().lastIndexOf(".")));
                    ivfxFile.setTitle(ivfxFile.getShortName());

                    // пытаемся получить информацию о частоте кадров, длительности и кол-ве кадров из МедиаИнфо
                    try {
                        ivfxFile.setMediaInfoJson(MediaInfo.executeMediaInfo(fileSourceName,"--Output=JSON"));

                        ivfxFile.setFrameRate(Double.parseDouble(MediaInfo.getInfoBySectionAndParameter(fileSourceName,"Video","FrameRate")));
                        ivfxFile.setDuration(Integer.parseInt(MediaInfo.getInfoBySectionAndParameter(fileSourceName,"Video","Duration")));
                        ivfxFile.setFramesCount(Integer.parseInt(MediaInfo.getInfoBySectionAndParameter(fileSourceName,"Video","FrameCount"))-1);
                    } catch (IOException | InterruptedException ex) {
                        System.out.println("Не удалось получить данные через MediaInfo");
                    }

                    ivfxFile.save();                                // сохраняем запись в БД
                    ivfxFile.createTracksFromMediaInfo();
                    listFiles.add(ivfxFile);                        // добавляем файл в список

                }

            }

            if (fileList.length > 0) {
                tblFiles.refresh();                             // обновляем таблицу файлов
                tblFileToAction.refresh();                      // обновляем таблицу файлов действий
                tblFiles.getSelectionModel().select(listFiles.get(listFiles.size()-1));  // "встаем" на последний файл в таблице файлов
            }
        }


    }

    @FXML
    void doActions(ActionEvent event) {

        List<IVFXFiles> listSelectedFiles = tblFileToAction.getSelectionModel().getSelectedItems();
        if (listSelectedFiles != null) {
            if (listSelectedFiles.size() > 0) {
                for (IVFXFiles ivfxFile: listSelectedFiles) {
//                    System.out.println(ivfxFile.getSourceName());
                    if (checkAnalize.isSelected()) {       // Выбран флажок "Проанализировать кадры"
                        AnalizeFrames.analizeFrames(ivfxFile);
                    }

                    if (checkFindTransitions.isSelected()) {       // Выбран флажок "Найти переходы"
                        AnalizeFrames.findTransition(ivfxFile);
                    }

                    if (checkClearShots.isSelected()) {       // Выбран флажок "Очистить планы"
                        AnalizeFrames.clearShots(ivfxFile);
                    }

                    if (checkAddFacesToDatabase.isSelected()) {       // Выбран флажок "Очистить планы"
                        String pathToFileJSON = ivfxFile.getFolderFramesFull()  + "\\faces.json";
                        FaceRecognizer.reloadFaces(pathToFileJSON);
                    }

                    if (checkCreateFaces.isSelected()) {       // Выбран флажок "создать faces"
                        GsonBuilder builder = new GsonBuilder();
                        Gson gson = builder.create();

                        String pathToFileJSON = ivfxFile.getFolderFramesFull()  + "\\frames.json";

                        FrameFace[] arrFrameFaces = FaceRecognizer.getArrayFrameFaces(ivfxFile);

                        // сохраняем список всех лиц для распознания в json
                        try (final FileWriter fileWriter = new FileWriter(pathToFileJSON)) {
                            gson.toJson(arrFrameFaces, fileWriter);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }

                }

                if (checkCreatePreviewMP4.isSelected() ||
                        checkCreateFramesPreview.isSelected() ||
                        checkCreateFramesMedium.isSelected() ||
                        checkCreateFramesFull.isSelected() ||
                        checkCreateFaces.isSelected() ||
                        checkCreateLossless.isSelected() ||
                        checkCreateShots.isSelected() ||
                        checkCreateShotsMXFaudioYES.isSelected() ||
                        checkCreateShotsMXFaudioNO.isSelected() ||
                        checkDeleteLossless.isSelected()) {
                    CreateVideo.createCmdToAllStepsConvertVideofile(listSelectedFiles,
                            checkCreatePreviewMP4.isSelected(),
                            checkCreateFramesPreview.isSelected(),
                            checkCreateFramesMedium.isSelected(),
                            checkCreateFramesFull.isSelected(),
                            checkCreateFaces.isSelected(),
                            checkCreateLossless.isSelected(),
                            checkCreateShots.isSelected(),
                            checkCreateShotsMXFaudioYES.isSelected(),
                            checkCreateShotsMXFaudioNO.isSelected(),
                            checkDeleteLossless.isSelected(),
                            checkRunCmd.isSelected());
                }
            }
        }


    }

    private void createFaces(IVFXFiles ivfxFile) {


        String pathToFolderFaces = ivfxFile.getFolderFaces();
        File folderFramesFull = new File(pathToFolderFaces);
        if (!folderFramesFull.exists()) {
            folderFramesFull.mkdirs();
        }

        CascadeClassifier face_detector = new CascadeClassifier();
        String path = Main.PATH_TO_CASCADE_CLASSIFIER;
        String name = "haarcascade_frontalface_alt.xml";

        if (!face_detector.load(path + name)) {
            System.out.println("Не удалось загрузить классификатор " + name);
            return;
        }

        List<IVFXFrames> listFrames = IVFXFrames.loadList(ivfxFile,false);

        for (IVFXFrames ivfxFrame: listFrames) {

            String pathToFrameFile = ivfxFrame.getFileNameFullSize();
            String pathToFacesFile = ivfxFrame.getFacesName();

            Mat img = Imgcodecs.imread(pathToFrameFile);
            if (img.empty()) {
                System.out.println("Не удалось загрузить изображение");
                return;
            }

            MatOfRect facesInPic = new MatOfRect();
            face_detector.detectMultiScale(img, facesInPic);

            List<Rect> listRects = facesInPic.toList();
            List<Mat> listFaces = new ArrayList<>();

            if (listRects.size() > 0) {
                for (Rect r : listRects) {

                    double coeff = 1;
                    int x1New = (int) (r.x - r.width * coeff);
                    int y1New = (int) (r.y - r.height * coeff);
                    if (x1New < 0) x1New = 0;
                    if (y1New < 0) y1New = 0;

                    int wNew = (int) (r.width + r.width * coeff * 2);
                    int hNew = (int) (r.height + r.height * coeff * 2);

                    int x2New = x1New + wNew;
                    int y2New = y1New + hNew;
                    if (x2New > img.width()) x2New = img.width();
                    if (y2New > img.height()) y2New = img.height();


                    Rect bigRect = new Rect(new Point(x1New, y1New), new Point(x2New, y2New));

                    System.out.println(bigRect);
                    Mat faceMat = new Mat(img, bigRect);
//                    Mat faceMat = new Mat(img, r);

//                    Imgproc.resize(faceMat, faceMat, new Size(128, 128));
                    listFaces.add(faceMat);
                    break;
//                    faceMat.release();
                }

                Mat conc = new Mat();
                Core.hconcat(listFaces, conc);
                Imgcodecs.imwrite(pathToFacesFile, conc);

                conc.release();

            }

            img.release();
            facesInPic.release();

            System.out.println("Creating faces for frame " + ivfxFrame.getFrameNumber() + " / " + listFrames.size() + ". Found faces: " + listRects.size());
        }

    }


    @FXML
    void doFilters(ActionEvent event) {
        saveCurrentFile();
        new FiltersController().editFilters(mainProject);
    }


    @FXML
    void doBtnFiltersTags(ActionEvent event) {
        saveCurrentFile();
        new FiltersTagsController().editFiltersTags(mainProject);
    }


    @FXML
    void doPersons(ActionEvent event) {
        saveCurrentFile();
        PersonController.getPerson(mainProject);
    }

    @FXML
    void doShots(ActionEvent event) {
        saveCurrentFile();
        new ShotsController().editShots(currentFile, 1);
    }

    @FXML
    void doTransitions(ActionEvent event) {
        saveCurrentFile();
        new FramesController().editTransitions(currentFile, 1);

    }

    @FXML
    void doBrowseFile(ActionEvent event) {
        // инициализируем диалог выбора файла
        String initialDirectory = "";
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Выберите файл");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("All Files", "*.*")
        );

        // если выбран текущий файл - назначаем его папку как стартовую для выбора нового файла
        if (currentFile != null) {
            initialDirectory = (new File(currentFile.getSourceName())).getParent();
        }
        if (!initialDirectory.equals("")) fileChooser.setInitialDirectory(new File(initialDirectory));

        // вызываем диалог выбора файла
        File file = fileChooser.showOpenDialog(new Stage());

        // если был выбран какой-нибудь файл
        if (file != null) {
            String fileSourceName = file.getAbsolutePath();
            currentFile.setSourceName(fileSourceName);
            ctlFileSourceName.setText(currentFile.getSourceName());
            // пытаемся получить информацию о частоте кадров, длительности и кол-ве кадров из МедиаИнфо
            try {
                currentFile.setFrameRate(Double.parseDouble(MediaInfo.getInfoBySectionAndParameter(fileSourceName,"Video","FrameRate")));
                currentFile.setDuration(Integer.parseInt(MediaInfo.getInfoBySectionAndParameter(fileSourceName,"Video","Duration")));
                currentFile.setFramesCount(Integer.parseInt(MediaInfo.getInfoBySectionAndParameter(fileSourceName,"Video","FrameCount"))-1);
                ctlFileFrameRate.setText(String.valueOf(currentFile.getFrameRate()));
                ctlFileWidth.setText(String.valueOf(currentFile.getWidth()));
                ctlFileHeight.setText(String.valueOf(currentFile.getHeight()));
                ctlFileDuration.setText(String.valueOf(currentFile.getDuration()));
                ctlFileFramesCount.setText(String.valueOf(currentFile.getFramesCount()));
            } catch (IOException | InterruptedException ex) {
                System.out.println("Не удалось получить данные через MediaInfo");
            }
            currentFile.save();
        }
    }

    @FXML
    void doBrowsePrjFolder(ActionEvent event) {
        String initialDirectory = "";
        DirectoryChooser directoryChooser = new DirectoryChooser();

        // если выбран проект - назначаем его папку как стартовую для выбора новой папки проекта
        if (mainProject != null) {
            initialDirectory = (new File(mainProject.getFolder())).getParent();
        }
        if (!initialDirectory.equals("")) directoryChooser.setInitialDirectory(new File(initialDirectory));

        // вызываем диалог выбора папки
        File directorySelected = directoryChooser.showDialog(new Stage());

        // если папка выбрана
        if (directorySelected != null) {
            prjFolder.setText(directorySelected.getAbsolutePath());
            mainProject.setFolder(directorySelected.getAbsolutePath());
            mainProject.save();
        }
    }

    @FXML
    void doGetMediaInfo(ActionEvent event) {
        // если был выбран какой-нибудь файл
        if (currentFile != null) {
            String fileSourceName = currentFile.getSourceName();
            // пытаемся получить информацию о частоте кадров, длительности и кол-ве кадров из МедиаИнфо
            try {

                currentFile.setMediaInfoJson(MediaInfo.executeMediaInfo(fileSourceName,"--Output=JSON"));

                currentFile.setFrameRate(Double.parseDouble(MediaInfo.getInfoBySectionAndParameter(fileSourceName,"Video","FrameRate")));
                currentFile.setDuration((int)Double.parseDouble(MediaInfo.getInfoBySectionAndParameter(fileSourceName,"Video","Duration")));
                currentFile.setFramesCount(Integer.parseInt(MediaInfo.getInfoBySectionAndParameter(fileSourceName,"Video","FrameCount"))-1);
                currentFile.setWidth(Integer.parseInt(MediaInfo.getInfoBySectionAndParameter(fileSourceName,"Video","Width")));
                currentFile.setHeight(Integer.parseInt(MediaInfo.getInfoBySectionAndParameter(fileSourceName,"Video","Height")));
                ctlFileFrameRate.setText(String.valueOf(currentFile.getFrameRate()));
                ctlFileDuration.setText(String.valueOf(currentFile.getDuration()));
                ctlFileFramesCount.setText(String.valueOf(currentFile.getFramesCount()));
                ctlFileWidth.setText(String.valueOf(currentFile.getWidth()));
                ctlFileHeight.setText(String.valueOf(currentFile.getHeight()));

                currentFile.createTracksFromMediaInfo();

            } catch (IOException | InterruptedException ex) {
                System.out.println("Не удалось получить данные через MediaInfo");
            }
            currentFile.save();
        }
    }

    @FXML
    void doDeleteFile(ActionEvent event) {

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Удаление файла");
        alert.setHeaderText("Вы действительно хотите удалить файл «" + currentFile.getTitle() + "»?");
        alert.setContentText("В случае утвердительного ответа файл будет удален из базы данных и его восстановление будет невозможно.\n\nВы уверены, что хотите удалить файл?");
        Optional<ButtonType> option = alert.showAndWait();
        if (option.get() == ButtonType.OK) {
            currentFile.delete();
            removeCurrentFileFromList();
            currentFile = null;
            initialize();
        }

    }

    private void removeCurrentFileFromList() {
        IVFXFiles toDel = null;
        for (IVFXFiles file: listFiles) {
            if (file.equals(currentFile)) toDel = file;
            if (file.getOrder() > currentFile.getOrder()) {
                file.setOrder(file.getOrder()-1);
                file.save();
            }
        }
        listFiles.remove(toDel);
    }

    private void setFileFields() {

        ctlFileSourceName.setText(currentFile.getSourceName());
        ctlFileShortName.setText(currentFile.getShortName());
        ctlFileTitle.setText(currentFile.getTitle());
        ctlFileSeasonNum.setText(String.valueOf(currentFile.getSeasonNumber()));
        ctlFileEpisodeNum.setText(String.valueOf(currentFile.getEpisodeNumber()));
        ctlFileFrameRate.setText(String.valueOf(currentFile.getFrameRate()));
        ctlFileFramesCount.setText(String.valueOf(currentFile.getFramesCount()));
        ctlFileDuration.setText(String.valueOf(currentFile.getDuration()));
        ctlFileWidth.setText(String.valueOf(currentFile.getWidth()));
        ctlFileHeight.setText(String.valueOf(currentFile.getHeight()));
        ctlFileDescription.setText(currentFile.getDescription());

        checkFolderPreviewMP4.setSelected(currentFile.isUseFolderMp4());
        fldFolderPreviewMP4.setDisable(!currentFile.isUseFolderMp4());
        btnFolderPreviewMP4.setDisable(!currentFile.isUseFolderMp4());
        fldFolderPreviewMP4.setText(currentFile.getFolderMp4());

        checkFolderPreviewFrames.setSelected(currentFile.isUseFolderFramesPreview());
        fldFolderPreviewFrames.setDisable(!currentFile.isUseFolderFramesPreview());
        btnFolderPreviewFrames.setDisable(!currentFile.isUseFolderFramesPreview());
        fldFolderPreviewFrames.setText(currentFile.getFolderFramesPreview());

        checkFolderMediumFrames.setSelected(currentFile.isUseFolderFramesMedium());
        fldFolderMediumFrames.setDisable(!currentFile.isUseFolderFramesMedium());
        btnFolderMediumFrames.setDisable(!currentFile.isUseFolderFramesMedium());
        fldFolderMediumFrames.setText(currentFile.getFolderFramesMedium());

        checkFolderFullFrames.setSelected(currentFile.isUseFolderFramesFull());
        fldFolderFullFrames.setDisable(!currentFile.isUseFolderFramesFull());
        btnFolderFullFrames.setDisable(!currentFile.isUseFolderFramesFull());
        fldFolderFullFrames.setText(currentFile.getFolderFramesFull());

        checkFolderLossless.setSelected(currentFile.isUseFolderLossless());
        fldFolderLossless.setDisable(!currentFile.isUseFolderLossless());
        btnFolderLossless.setDisable(!currentFile.isUseFolderLossless());
        fldFolderLossless.setText(currentFile.getFolderLossless());

        checkFolderFavorite.setSelected(currentFile.isUseFolderFavorite());
        fldFolderFavorite.setDisable(!currentFile.isUseFolderFavorite());
        btnFolderFavorite.setDisable(!currentFile.isUseFolderFavorite());
        fldFolderFavorite.setText(currentFile.getFolderFavorite());

        checkFolderShots.setSelected(currentFile.isUseFolderShots());
        fldFolderShots.setDisable(!currentFile.isUseFolderShots());
        btnFolderShots.setDisable(!currentFile.isUseFolderShots());
        fldFolderShots.setText(currentFile.getFolderShots());

        cbLosslessVideoCodec.getSelectionModel().select(currentFile.getLosslessVideoCodec());
        cbLosslessContainer.getSelectionModel().select(currentFile.getLosslessContainer());

        // загружаем список треков
        listFilesTracks = FXCollections.observableArrayList(IVFXFilesTracks.loadList(currentFile));

        // Заполняем таблицу треков и столбцы этой таблицы
        colFileTrackUse.setCellValueFactory(new PropertyValueFactory<>("file_track_use"));
        colFileTrackType.setCellValueFactory(new PropertyValueFactory<>("file_track_type"));
        tblFilesTracks.setItems(listFilesTracks);

    }

    // установка доступности кнопок сортировки файлов
    private void setEnabledFilesButtons() {

        btnDelFile.setDisable(currentFile == null);     // btnDelFile недоступна, если файл не выбран
        btnTop.setDisable(currentFile == null);         // btnTop недоступна, если файл не выбран
        btnUp.setDisable(currentFile == null);          // btnUp недоступна, если файл не выбран
        btnDown.setDisable(currentFile == null);        // btnDown недоступна, если файл не выбран
        btnBottom.setDisable(currentFile == null);      // btnBottom недоступна, если файл не выбран

        if (currentFile != null) {

            btnDelFile.setDisable(false);
            int firstNum = listFiles.get(0).getOrder();
            int lastNum = listFiles.get(listFiles.size()-1).getOrder();
            int currNum = currentFile.getOrder();

            btnTop.setDisable(currNum == firstNum);
            btnUp.setDisable(currNum == firstNum);
            btnDown.setDisable(currNum == lastNum);
            btnBottom.setDisable(currNum == lastNum);

        }

    }

    @FXML
    void doMenuExit(ActionEvent event) {    // Меню "Выход"
        saveCurrentFile();
        if (mainProject != null) mainProject.save();
        Main.mainWindow.close();
    }

    @FXML
    void doMenuOpen(ActionEvent event) {    // Меню "Выход"
        saveCurrentFile();
        IVFXProjects project = OpenDialogController.getProject();
        if (project != null) mainProject = project;
        currentFile = null;
        initialize();
    }

    @FXML
    void doBtnFolderFavorite(ActionEvent event) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File dir = directoryChooser.showDialog(new Stage());
        if (dir != null) {
            fldFolderFavorite.setText(dir.getAbsolutePath());
            saveCurrentFile();
        }
    }

    @FXML
    void doBtnFolderFullFrames(ActionEvent event) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File dir = directoryChooser.showDialog(new Stage());
        if (dir != null) {
            fldFolderFullFrames.setText(dir.getAbsolutePath());
            saveCurrentFile();
        }
    }

    @FXML
    void doBtnFolderLossless(ActionEvent event) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File dir = directoryChooser.showDialog(new Stage());
        if (dir != null) {
            fldFolderLossless.setText(dir.getAbsolutePath());
            saveCurrentFile();
        }
    }

    @FXML
    void doBtnFolderPreviewFrames(ActionEvent event) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File dir = directoryChooser.showDialog(new Stage());
        if (dir != null) {
            fldFolderPreviewFrames.setText(dir.getAbsolutePath());
            saveCurrentFile();
        }
    }

    @FXML
    void doBtnFolderMediumFrames(ActionEvent event) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File dir = directoryChooser.showDialog(new Stage());
        if (dir != null) {
            fldFolderMediumFrames.setText(dir.getAbsolutePath());
            saveCurrentFile();
        }
    }

    @FXML
    void doBtnFolderPreviewMP4(ActionEvent event) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File dir = directoryChooser.showDialog(new Stage());
        if (dir != null) {
            fldFolderPreviewMP4.setText(dir.getAbsolutePath());
            saveCurrentFile();
        }
    }

    @FXML
    void doBtnFolderShots(ActionEvent event) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File dir = directoryChooser.showDialog(new Stage());
        if (dir != null) {
            fldFolderShots.setText(dir.getAbsolutePath());
            saveCurrentFile();
        }
    }

    @FXML
    void doCheckFolderFavorite(ActionEvent event) {
        btnFolderFavorite.setDisable(!checkFolderFavorite.isSelected());
        fldFolderFavorite.setDisable(!checkFolderFavorite.isSelected());
        saveCurrentFile();
    }

    @FXML
    void doCheckFolderFullFrames(ActionEvent event) {
        btnFolderFullFrames.setDisable(!checkFolderFullFrames.isSelected());
        fldFolderFullFrames.setDisable(!checkFolderFullFrames.isSelected());
        saveCurrentFile();
    }

    @FXML
    void doCheckFolderLossless(ActionEvent event) {
        btnFolderLossless.setDisable(!checkFolderLossless.isSelected());
        fldFolderLossless.setDisable(!checkFolderLossless.isSelected());
        saveCurrentFile();
    }

    @FXML
    void doCheckFolderPreviewFrames(ActionEvent event) {
        btnFolderPreviewFrames.setDisable(!checkFolderPreviewFrames.isSelected());
        fldFolderPreviewFrames.setDisable(!checkFolderPreviewFrames.isSelected());
        saveCurrentFile();
    }

    @FXML
    void doCheckFolderMediumFrames(ActionEvent event) {
        btnFolderMediumFrames.setDisable(!checkFolderMediumFrames.isSelected());
        fldFolderMediumFrames.setDisable(!checkFolderMediumFrames.isSelected());
        saveCurrentFile();
    }

    @FXML
    void doCheckFolderPreviewMP4(ActionEvent event) {
        btnFolderPreviewMP4.setDisable(!checkFolderPreviewMP4.isSelected());
        fldFolderPreviewMP4.setDisable(!checkFolderPreviewMP4.isSelected());
        saveCurrentFile();
    }

    @FXML
    void doCheckFolderShots(ActionEvent event) {
        btnFolderShots.setDisable(!checkFolderShots.isSelected());
        fldFolderShots.setDisable(!checkFolderShots.isSelected());
        saveCurrentFile();
    }


    @FXML
    void doBtnTags(ActionEvent event) {
        saveCurrentFile();
        new TagsController().editTags();
    }

    @FXML
    void doBtnTagsShots(ActionEvent event) {
        saveCurrentFile();
        new TagsShotsController().editShotsTags();
    }

    @FXML
    void doBtnFaceDetect(ActionEvent event) {
        saveCurrentFile();
        new FacesController().editFaces();
    }

    @FXML
    void doFldFilePropertyName(ActionEvent event) {

        if (currentFileProperty != null) {
            if (!currentFileProperty.getName().equals("name")) {
                currentFileProperty.setName(fldFilePropertyName.getText());
                currentFileProperty.save();
                tblFilesProperties.refresh();
                tblFiles.refresh();
            }
        }
        
    }

    @FXML
    void doBtnAddNewFilesProperties(ActionEvent event) {

        if (currentFile != null) {

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Добавление свойства файла");
            alert.setHeaderText("Вы действительно хотите добавить новое свойство для файла?");
            alert.setContentText("Имя и значение свойства будут сгенерированы автоматически.");
            Optional<ButtonType> option = alert.showAndWait();
            if (option.get() == ButtonType.OK) {

                IVFXFilesProperties fileProperty = IVFXFilesProperties.getNewDbInstance(currentFile);
                listFilesProperties = FXCollections.observableArrayList(IVFXFilesProperties.loadList(currentFile));
                tblFilesProperties.setItems(listFilesProperties);
                for (IVFXFilesProperties tmp: listFilesProperties) {
                    if (tmp.isEqual(fileProperty)) {
                        currentFileProperty = tmp;
                        break;
                    }
                }
                if (currentFileProperty != null) {
                    tblFilesProperties.getSelectionModel().select(currentFileProperty);
                    doOnSelectRecordInTblFilesProperties();
                }

            }

        }

    }

    @FXML
    void doBtnAddNewFilesPropertiesUrl(ActionEvent event) {

        if (currentFile != null) {

            IVFXFilesProperties fileProperty = IVFXFilesProperties.getNewDbInstance(currentFile, "url", "");
            listFilesProperties = FXCollections.observableArrayList(IVFXFilesProperties.loadList(currentFile));
            tblFilesProperties.setItems(listFilesProperties);
            for (IVFXFilesProperties tmp: listFilesProperties) {
                if (tmp.isEqual(fileProperty)) {
                    currentFileProperty = tmp;
                    break;
                }
            }
            if (currentFileProperty != null) {
                tblFilesProperties.getSelectionModel().select(currentFileProperty);
                doOnSelectRecordInTblFilesProperties();
            }

        }

    }

    @FXML
    void doBtnAddNewFilesPropertiesHtml(ActionEvent event) {

        if (currentFile != null) {
            String url = currentFile.getPropertyValue("url");
            if (url != null) {
                String html = Utils.getHTMLtextFromUrl(url);
                if (html != null) {

                    IVFXFilesProperties fileProperty = IVFXFilesProperties.getNewDbInstance(currentFile, "html", html);
                    listFilesProperties = FXCollections.observableArrayList(IVFXFilesProperties.loadList(currentFile));
                    tblFilesProperties.setItems(listFilesProperties);
                    for (IVFXFilesProperties tmp: listFilesProperties) {
                        if (tmp.isEqual(fileProperty)) {
                            currentFileProperty = tmp;
                            break;
                        }
                    }
                    if (currentFileProperty != null) {
                        tblFilesProperties.getSelectionModel().select(currentFileProperty);
                        doOnSelectRecordInTblFilesProperties();
                    }
                }

            }

        }

    }

    private void doOnSelectRecordInTblFilesProperties() {

        if (currentFileProperty != null) {
            fldFilePropertyName.setText(currentFileProperty.getName());
            fldFilePropertyValue.setText(currentFileProperty.getValue());
            fldFilePropertyName.setDisable(false);
            fldFilePropertyValue.setDisable(false);
            btnDeleteFilesProperties.setDisable(false);
        } else {
            fldFilePropertyName.setText("");
            fldFilePropertyValue.setText("");
            fldFilePropertyName.setDisable(true);
            fldFilePropertyValue.setDisable(true);
            btnDeleteFilesProperties.setDisable(true);
        }
        
    }

    @FXML
    void doBtnDeleteFilesProperties(ActionEvent event) {

        if (currentFileProperty != null) {
            if (!currentFileProperty.getName().equals("name")) {

                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Удаление свойства файла");
                alert.setHeaderText("Вы действительно хотите удалить свойство файла с именем «" + currentFileProperty.getName() + "» и значением «" + currentFileProperty.getValue() + "»?");
                alert.setContentText("В случае утвердительного ответа свойство файла будет удалено из базы данных и его восстановление будет невозможно.\nВы уверены, что хотите удалить свойство файла?");
                Optional<ButtonType> option = alert.showAndWait();
                if (option.get() == ButtonType.OK) {
                    currentFileProperty.delete();
                    listFilesProperties = FXCollections.observableArrayList(IVFXFilesProperties.loadList(currentFile));
                    tblFilesProperties.setItems(listFilesProperties);
                    currentFileProperty = null;
                    doOnSelectRecordInTblFilesProperties();
                }
            }
        }
        
    }




}

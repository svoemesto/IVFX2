package com.svoemesto.ivfx.controllers;

import com.svoemesto.ivfx.tables.Database;
import com.svoemesto.ivfx.utils.MediaInfo;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.URL;
import java.util.Collections;
import java.util.Optional;
import java.util.ResourceBundle;

import com.sun.javafx.scene.control.skin.TableViewSkin;
import com.sun.javafx.scene.control.skin.VirtualFlow;
import com.svoemesto.ivfx.Main;
import com.svoemesto.ivfx.tables.IVFXFiles;
import com.svoemesto.ivfx.tables.IVFXProjects;
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
import javafx.scene.layout.AnchorPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

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
    private CheckBox checkCreatePreviewMP4;

    @FXML
    private CheckBox checkCreateFramesPreview;

    @FXML
    private CheckBox checkCreateFramesFull;

    @FXML
    private CheckBox checkAnalize;

    @FXML
    private CheckBox checkFindTransitions;

    @FXML
    private CheckBox checkCreateCMD;

    @FXML
    private Button btnDoActions;

    @FXML
    private Button btnTransitions;

    @FXML
    private Button btnSegments;

    @FXML
    private TextArea ctlConsole;

    public static IVFXProjects mainProject;
    public static IVFXFiles currentFile;

    private ObservableList<IVFXFiles> listFiles = FXCollections.observableArrayList();
    private VirtualFlow flowTblFileToAction;


    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        Main.mainConnection = Database.getConnection();
        Main.mainWindow = primaryStage;

        IVFXProjects project = IVFXProjects.loadById(1);
        if (project != null) {
            mainProject = project;
            Parent root = FXMLLoader.load(getClass().getResource("../resources/fxml/project.fxml"));
            primaryStage.setTitle("Interactive Video FX © svoёmesto");
            Scene scene = new Scene(root, 800, 800);
            primaryStage.setScene(scene);
            primaryStage.show();
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

        // Устанавливаем tblFileToAction возможность мультивыбора строк
        tblFileToAction.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        if (mainProject != null) {

            // Заполняем поля формы для проекта: prjName, prjShortName, prjFolder
            prjName.setText(mainProject.getName());
            prjShortName.setText(mainProject.getShortName());
            prjFolder.setText(mainProject.getFolder());

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

        // изменение поля "Title"
        ctlFileTitle.textProperty().addListener((observable, oldValue, newValue) -> {
            currentFile.setTitle(ctlFileTitle.getText());
            for (IVFXFiles file: listFiles) {
                if (file.getUuid().toString().equals(currentFile.getUuid().toString())) {
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
                ivfxFile.setProjectUuid(mainProject.getUuid());
                ivfxFile.setShortName(file.getName().substring(0,file.getName().lastIndexOf(".")));
                ivfxFile.setTitle(ivfxFile.getShortName());

                // пытаемся получить информацию о частоте кадров, длительности и кол-ве кадров из МедиаИнфо
                try {
                    ivfxFile.setFrameRate(Double.parseDouble(MediaInfo.getInfoBySectionAndParameter(fileSourceName,"Video","FrameRate")));
                    ivfxFile.setDuration(Integer.parseInt(MediaInfo.getInfoBySectionAndParameter(fileSourceName,"Video","Duration")));
                    ivfxFile.setFramesCount(Integer.parseInt(MediaInfo.getInfoBySectionAndParameter(fileSourceName,"Video","FrameCount"))-1);
                } catch (IOException | InterruptedException ex) {
                    System.out.println("Не удалось получить данные через MediaInfo");
                }

                ivfxFile.save();                                // сохраняем запись в БД
                listFiles.add(ivfxFile);                        // добавляем файл в список
                tblFiles.refresh();                             // обновляем таблицу файлов
                tblFileToAction.refresh();                      // обновляем таблицу файлов действий
                tblFiles.getSelectionModel().select(ivfxFile);  // "встаем" на этот файл в таблице файлов
            }

        }
    }

    @FXML
    void doAddFilesInFolder(ActionEvent event) {

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
                    ivfxFile.setProjectUuid(mainProject.getUuid());
                    ivfxFile.setShortName(file.getName().substring(0,file.getName().lastIndexOf(".")));
                    ivfxFile.setTitle(ivfxFile.getShortName());

                    // пытаемся получить информацию о частоте кадров, длительности и кол-ве кадров из МедиаИнфо
                    try {
                        ivfxFile.setFrameRate(Double.parseDouble(MediaInfo.getInfoBySectionAndParameter(fileSourceName,"Video","FrameRate")));
                        ivfxFile.setDuration(Integer.parseInt(MediaInfo.getInfoBySectionAndParameter(fileSourceName,"Video","Duration")));
                        ivfxFile.setFramesCount(Integer.parseInt(MediaInfo.getInfoBySectionAndParameter(fileSourceName,"Video","FrameCount"))-1);
                    } catch (IOException | InterruptedException ex) {
                        System.out.println("Не удалось получить данные через MediaInfo");
                    }

                    ivfxFile.save();                                // сохраняем запись в БД
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

    }

    @FXML
    void doFilters(ActionEvent event) {

    }

    @FXML
    void doPersons(ActionEvent event) {
        PersonController.getPerson(mainProject);
    }

    @FXML
    void doSegments(ActionEvent event) {
        new SegmentsController().editSegments(currentFile, 1);
    }

    @FXML
    void doTransitions(ActionEvent event) {

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
                currentFile.setFrameRate(Double.parseDouble(MediaInfo.getInfoBySectionAndParameter(fileSourceName,"Video","FrameRate")));
                currentFile.setDuration(Integer.parseInt(MediaInfo.getInfoBySectionAndParameter(fileSourceName,"Video","Duration")));
                currentFile.setFramesCount(Integer.parseInt(MediaInfo.getInfoBySectionAndParameter(fileSourceName,"Video","FrameCount"))-1);
                ctlFileFrameRate.setText(String.valueOf(currentFile.getFrameRate()));
                ctlFileDuration.setText(String.valueOf(currentFile.getDuration()));
                ctlFileFramesCount.setText(String.valueOf(currentFile.getFramesCount()));
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
        ctlFileDescription.setText(currentFile.getDescription());

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
        IVFXProjects project = OpenDialogController.getProject();
        if (project != null) mainProject = project;
        currentFile = null;
        initialize();
    }


}

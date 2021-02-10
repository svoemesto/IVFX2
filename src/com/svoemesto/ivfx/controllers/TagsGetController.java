package com.svoemesto.ivfx.controllers;

import com.sun.javafx.scene.control.skin.TableViewSkin;
import com.sun.javafx.scene.control.skin.VirtualFlow;
import com.svoemesto.ivfx.Main;
import com.svoemesto.ivfx.tables.*;
import javafx.application.Application;
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
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class TagsGetController extends Application {

    @FXML
    private AnchorPane apTags;

    @FXML
    private CheckBox chUseTagType;

    @FXML
    private ComboBox<IVFXTagsTypes> cbTagType;

    @FXML
    private CheckBox chUseProject;

    @FXML
    private ComboBox<IVFXProjects> cbProject;

    @FXML
    private CheckBox chUseFile;

    @FXML
    private ComboBox<IVFXFiles> cbFile;

    @FXML
    private CheckBox chFindInProperties;

    @FXML
    private TextField fldFindTag;

    @FXML
    private TableView<IVFXTags> tblTags;

    @FXML
    private TableColumn<IVFXTags, String> colTagTypeTblTags;

    @FXML
    private TableColumn<IVFXTags, String> colPreviewTblTags;

    @FXML
    private TableColumn<IVFXTags, String> colPropertiesTblTags;

    @FXML
    private Button btnOK;

    @FXML
    private Button btnCancel;

    private static TagsGetController tagsGetController = new TagsGetController();
    private Stage controllerStage;
    private Scene controllerScene;
    public static IVFXProjects currentProject;                                                                          // Текуший проект
    public static IVFXFiles currentFile;                                                                                // Текущий файл
    public static IVFXTagsTypes currentTagType;                                                                         // Текущий тип тэга
    public static List<IVFXTagsTypes> currentListTagsTypes;                                                                         // Текущий тип тэга
    public static IVFXTags currentTag;                                                                                  // Текущий тэг

    private ObservableList<IVFXProjects> listProjects = FXCollections.observableArrayList();                            // список проектов
    private ObservableList<IVFXFiles> listFiles = FXCollections.observableArrayList();                                  // список файлов
    private ObservableList<IVFXTags> listTags = FXCollections.observableArrayList();                                    // список тэгов
    private ObservableList<IVFXTagsTypes> listTagsTypes = FXCollections.observableArrayList();                          // список типов тэгов

    private VirtualFlow flowTblTags;

    private FilteredList<IVFXTags> filteredTags;

    // Инициализация
    @FXML
    void initialize() {

        if (currentListTagsTypes != null) {
            listTagsTypes = FXCollections.observableArrayList(currentListTagsTypes);;
        } else {
            listTagsTypes = FXCollections.observableArrayList(IVFXTagsTypes.loadList());
        }

        chUseTagType.setSelected(currentTagType != null);
        chUseProject.setSelected(currentProject != null);
        chUseFile.setSelected(currentFile != null);

        doChUseTagType(null);
        doChUseProject(null);
        doChUseFile(null);

        initListTags();

        // инициализация тэга, если он указан
        if (currentTag != null) {
            for (IVFXTags tag : listTags) {
                if (tag.getId() == currentTag.getId()) {
                    currentTag = tag;
                    selectAndSmartScrollTblTags(currentTag);
                    break;
                }
            }
            tblTags.getSelectionModel().select(currentTag);
            selectAndSmartScrollTblTags(currentTag);

        }

        doOnSelectRecordInTblTags();

        /*********************
         * Настройка элементов
         *********************/

        // инициализируем поля таблицы tblTags (таблица "Тэги")
        colTagTypeTblTags.setCellValueFactory(new PropertyValueFactory<>("tagTypeName"));
        colPreviewTblTags.setCellValueFactory(new PropertyValueFactory<>("label1"));
        colPropertiesTblTags.setCellValueFactory(new PropertyValueFactory<>("propertiesValues"));

        // скрываем заголовок у таблицы tblTags
        tblTags.widthProperty().addListener((source,oldWidth,newWidth) -> {
            Pane header = (Pane) tblTags.lookup("TableHeaderRow");
            if (header.isVisible()){
                header.setMaxHeight(0);
                header.setMinHeight(0);
                header.setPrefHeight(0);
                header.setVisible(false);
            }
        });

        // делаем поле Properties таблицы tblTags с переносом по словам и расширяемым по высоте
        colPropertiesTblTags.setCellFactory(param -> {
            TableCell<IVFXTags, String> cell = new TableCell<>();
            Text text = new Text();
            text.setStyle("");
            cell.setGraphic(text);
            cell.setPrefHeight(Control.USE_COMPUTED_SIZE);
            text.textProperty().bind(cell.itemProperty());
            text.wrappingWidthProperty().bind(colPropertiesTblTags.widthProperty());
            return cell;
        });

        /*********************
         * Обработчики событий
         *********************/

        // обработка события выбора записи в комбобоксе cbTagType
        cbTagType.getSelectionModel().selectedItemProperty().addListener((v, oldValue, newValue) -> {
            if (newValue != null) {
                IVFXTagsTypes selectedTagType = (IVFXTagsTypes) newValue;
                if (currentTagType == null || !currentTagType.isEqual(selectedTagType)) {
                    currentTagType = selectedTagType;
                    initListTags();
                }
            }
        });

        // обработка события выбора записи в комбобоксе cbProject
        cbProject.getSelectionModel().selectedItemProperty().addListener((v, oldValue, newValue) -> {
            if (newValue != null) {
                IVFXProjects selectedProject = (IVFXProjects) newValue;
                if (currentProject == null || !currentProject.equals(selectedProject)) {
                    currentProject = selectedProject;
                    currentFile = null;
                    chUseFile.setDisable(false);
                    doChUseFile(null);
                    initListTags();
                }
            }
        });

        // обработка события выбора записи в комбобоксе cbFile
        cbFile.getSelectionModel().selectedItemProperty().addListener((v, oldValue, newValue) -> {
            if (newValue != null) {
                IVFXFiles selectedFile = (IVFXFiles) newValue;
                if (currentFile == null || !currentFile.isEqual(selectedFile)) {
                    currentFile = selectedFile;
                    initListTags();
                }
            }
        });

        // обработка события отпускания кнопки в поле поиска
        fldFindTag.setOnKeyReleased(e -> {
            fldFindTag.textProperty().addListener((v, oldValue, newValue) -> {
                filteredTags.setPredicate((Predicate<? super IVFXTags>) ivfxTags-> {
                    if (newValue == null || newValue.isEmpty()) {
                        return true;
                    }
                    String lowerCaseFilter = newValue.toLowerCase();
                    if (!chFindInProperties.isSelected() && ivfxTags.getName().toLowerCase().contains(lowerCaseFilter)) {
                        return true;
                    } if (chFindInProperties.isSelected() && ivfxTags.getPropertiesValues().toLowerCase().contains(lowerCaseFilter)) {
                        return true;
                    }
                    return false;
                });

            });
            SortedList<IVFXTags> sortedTags = new SortedList<>(filteredTags);
            sortedTags.comparatorProperty().bind(tblTags.comparatorProperty());
            tblTags.setItems(sortedTags);
            if (sortedTags.size() > 0) {
                tblTags.getSelectionModel().select(sortedTags.get(0));
                tblTags.scrollTo(sortedTags.get(0));
            }
        });

        // обработка события нажатия Enter в поле ctlSearch - переход на первую запись в таблице tblTags
        fldFindTag.setOnKeyPressed(ke -> {
            if (ke.getCode().equals(KeyCode.ENTER))
            {
                tblTags.requestFocus();
                tblTags.getSelectionModel().select(0);
                tblTags.scrollTo(0);
            }
        });

        // обработка события нажатия Enter в таблице tblTags - переход на кнопку OK
        tblTags.setOnKeyPressed(ke -> {
            if (ke.getCode().equals(KeyCode.ENTER))
            {
                btnOK.requestFocus();
            }
        });

        // обработка события нажатия Enter на кнопке btnOK - нажатие на кнопку OK
        btnOK.setOnKeyPressed(ke -> {
            if (ke.getCode().equals(KeyCode.ENTER))
            {
                doBtnOK(null);
            }
        });

        // обработка события отслеживания видимости на экране текущего тэга в таблице tblTags
        tblTags.skinProperty().addListener((ChangeListener<Skin>) (ov, t, t1) -> {
            if (t1 == null) {
                return;
            }
            TableViewSkin tvs = (TableViewSkin) t1;
            ObservableList<Node> kids = tvs.getChildren();//    getChildrenUnmodifiable();
            if (kids == null || kids.isEmpty()) {
                return;
            }
            flowTblTags = (VirtualFlow) kids.get(1);
        });

        // обработка события выбора записи в таблице tblTags
        tblTags.getSelectionModel().selectedItemProperty().addListener((v, oldValue, newValue) -> {
            if (newValue != null) {
                currentTag = newValue;
                doOnSelectRecordInTblTags();
            }
        });

        // обработка события двойного клика в таблице tblTags
        tblTags.setOnMouseClicked(mouseEvent -> {
            if (mouseEvent.getButton().equals(MouseButton.PRIMARY) && mouseEvent.getClickCount() == 2) {
                doBtnOK(null);
            }
        });

    }

    /*****************************************
     * Действия при выборе записей в таблицах
     *****************************************/

    // Действия при выборе записи в таблице tblTags
    private void doOnSelectRecordInTblTags() {
        if (currentTag != null) {
            selectAndSmartScrollTblTags(currentTag);
        }
    }


    /******************************
     * Методы обработчиков событий
     ******************************/

    // Событие переключения флажка "Тип"
    @FXML
    void doChUseTagType(ActionEvent event) {
        if (chUseTagType.isSelected()) {
            cbTagType.setDisable(false);
            cbTagType.setItems(listTagsTypes);
            if (currentTagType != null) {
                for (IVFXTagsTypes tmp: listTagsTypes) {
                    if (tmp.isEqual(currentTagType)) {
                        currentTagType = tmp;
                        cbTagType.setValue(currentTagType);
                    }
                }
            }
        } else {
            cbTagType.setDisable(true);
            cbTagType.setItems(null);
            currentTagType = null;
        }
    }

    // Событие переключения флажка "Файл"
    @FXML
    void doChUseFile(ActionEvent event) {
        if (chUseFile.isSelected()) {
            cbFile.setDisable(false);
            listFiles = FXCollections.observableArrayList(IVFXFiles.loadList(currentProject));
            cbFile.setItems(listFiles);
            if (currentFile != null) {
                for (IVFXFiles tmp: listFiles) {
                    if (tmp.isEqual(currentFile)) {
                        currentFile = tmp;
                        cbFile.setValue(currentFile);
                    }
                }
            }
        } else {
            cbFile.setDisable(true);
            cbFile.setItems(null);
            currentFile = null;
        }
    }

    // Событие переключения флажка "Проект"
    @FXML
    void doChUseProject(ActionEvent event) {
        if (chUseProject.isSelected()) {
            cbProject.setDisable(false);
            listProjects = FXCollections.observableArrayList(IVFXProjects.loadList());
            cbProject.setItems(listProjects);
            if (currentProject != null) {
                for (IVFXProjects tmp: listProjects) {
                    if (tmp.getId() == currentProject.getId()) {
                        currentProject = tmp;
                        cbProject.setValue(currentProject);
                    }
                }
            }
        } else {
            cbProject.setDisable(true);
            cbProject.setItems(null);
            currentProject = null;
        }

        if (currentFile == null) {
            chUseFile.setDisable(true);
            chUseFile.setSelected(false);
            doChUseFile(null);
        }

    }

    // Событие переключения флажка "Искать в свойствах"
    @FXML
    void doChFindInProperties(ActionEvent event) {

    }

    // Событие нажатия кнопки "ОК"
    @FXML
    void doBtnOK(ActionEvent event) {
        tagsGetController.controllerStage.close();
    }

    // Событие нажатия кнопки "ОК"
    @FXML
    void doBtnCancel(ActionEvent event) {
        currentTag = null;
        tagsGetController.controllerStage.close();
    }

    /********
     * Методы
     ********/

    public static void main(String[] args) {
        launch(args);
    }

    // Метод старта класса как отдельного приложения
    @Override
    public void start(Stage primaryStage) throws Exception {
        Main.mainConnection = Database.getConnection();
        Main.mainWindow = primaryStage;

        AnchorPane root = FXMLLoader.load(tagsGetController.getClass().getResource("../resources/fxml/GetTags.fxml")); // в этот момент вызывается initialize()

        tagsGetController.controllerScene = new Scene(root);
        tagsGetController.controllerStage = new Stage();
        tagsGetController.controllerStage.setTitle("Выбор тэга.");
        tagsGetController.controllerStage.setScene(tagsGetController.controllerScene);
        tagsGetController.controllerStage.initModality(Modality.APPLICATION_MODAL);

        tagsGetController.controllerStage.setOnCloseRequest(we -> {
            System.out.println("Закрытые окна выбора тэгов.");
        });

        tagsGetController.controllerStage.showAndWait();
        System.out.println("Завершение работы getTags");
    }


    public static IVFXTags getTag() {
        return getTag(null, null, null, null);
    }
    // Метод вызова класса из других классов
    public static IVFXTags getTag(IVFXProjects initProject, IVFXTagsTypes initTagType, List<Integer> initListTagsTypesId, IVFXFiles initFile) {

        IVFXTags ivfxTag = null;

        currentProject = initProject;
        currentFile = initFile;
        currentTagType = initTagType;
        if (initListTagsTypesId != null) {
            currentListTagsTypes = new ArrayList<>();
            for (int id: initListTagsTypesId) {
                currentListTagsTypes.add(IVFXTagsTypes.load(id));
            }
        }

        try {

            AnchorPane root = FXMLLoader.load(tagsGetController.getClass().getResource("../resources/fxml/GetTags.fxml")); // в этот момент вызывается initialize()

            tagsGetController.controllerScene = new Scene(root);
            tagsGetController.controllerStage = new Stage();
            tagsGetController.controllerStage.setTitle("Выбор тэга.");
            tagsGetController.controllerStage.setScene(tagsGetController.controllerScene);
            tagsGetController.controllerStage.initModality(Modality.APPLICATION_MODAL);

            tagsGetController.controllerStage.setOnCloseRequest(we -> {
                System.out.println("Закрытые окна выбора тэгов.");
            });

            tagsGetController.controllerStage.showAndWait();

            ivfxTag = currentTag;

            System.out.println("Завершение работы getTags");

        } catch (IOException e) {
            e.printStackTrace();
        }

        return ivfxTag;

    }

    // Инициализация списка тэгов
    private void initListTags() {
        if (currentProject == null) {
            if (currentTagType == null) {
                listTags = FXCollections.observableArrayList(IVFXTags.loadList(currentProject, true));
            } else {
                listTags = FXCollections.observableArrayList(IVFXTags.loadList(currentProject, true, currentTagType.getId()));
            }
        } else {
            if (currentFile == null) {
                if (currentTagType == null) {
                    listTags = FXCollections.observableArrayList(IVFXTags.loadList(currentProject, true));
                } else {
                    listTags = FXCollections.observableArrayList(IVFXTags.loadList(currentProject, true, currentTagType.getId()));
                }
            } else {
                if (currentTagType == null) {
                    listTags = FXCollections.observableArrayList(IVFXTags.loadList(currentFile, true));
                } else {
                    listTags = FXCollections.observableArrayList(IVFXTags.loadList(currentFile, true, currentTagType.getId()));
                }
            }
        }

        tblTags.setItems(listTags);
        filteredTags = new FilteredList<>(listTags, e -> true);
    }

    private void doGoToTag(IVFXTags tag) {

        chUseProject.setSelected(false);
        doChUseProject(null);
        chUseTagType.setSelected(true);
        doChUseTagType(null);
        // устанавливаем значени комбобокса типа тэга для выбранного тэга
        for (IVFXTagsTypes tagsTypes: listTagsTypes) {
            if (tagsTypes.getName().equals(tag.getIvfxEnumTagsTypes().name())) {
                cbTagType.setValue(tagsTypes);
                break;
            }
        }
        fldFindTag.setText("");

        currentTagType = cbTagType.getValue();

        initListTags();

        for (IVFXTags tmp: listTags) {
            if (tmp.isEqual(tag)) {
                currentTag = tmp;
                selectAndSmartScrollTblTags(currentTag);
                break;
            }
        }
        doOnSelectRecordInTblTags();
    }

    private void selectAndSmartScrollTblTags(IVFXTags tagToSelectAndScroll) {

        IVFXTags tagToSelect = (IVFXTags)getItemFromList(listTags, tagToSelectAndScroll);
        if (tagToSelect != null) {
            tblTags.getSelectionModel().select(tagToSelect);
        }

        if (flowTblTags != null && flowTblTags.getCellCount() > 0) {
            int first = flowTblTags.getFirstVisibleCell().getIndex();
            int last = flowTblTags.getLastVisibleCell().getIndex();
            int selected = tblTags.getSelectionModel().getSelectedIndex();
            if (selected < first || selected > last) {
                tblTags.scrollTo(selected);
            }
        }
    }

    private Object getItemFromList(List<?> list, Object obj) {
        Object result = null;
        for (Object object: list) {
            if (object.equals(obj)) {
                return object;
            }
        }
        return result;
    }

}

package com.svoemesto.ivfx.controllers;

import com.sun.javafx.scene.control.skin.TableViewSkin;
import com.sun.javafx.scene.control.skin.VirtualFlow;
import com.svoemesto.ivfx.Main;
import com.svoemesto.ivfx.tables.*;
import com.svoemesto.ivfx.utils.ConvertToFxImage;
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
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class TagsController extends Application {

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
    private Label lblFullPreview;


    @FXML
    private Button btnSetTagPicture;

    @FXML
    private Button btnClearTagPicture;


    @FXML
    private ComboBox<IVFXTagsTypes> cbCurrentTagType;

    @FXML
    private TableView<IVFXTagsProperties> tblTagsProperties;

    @FXML
    private TableColumn<IVFXTagsProperties, String> colNameTblTagsProperties;

    @FXML
    private TableColumn<IVFXTagsProperties, String> colValueTblTagsProperties;

    @FXML
    private TextField fldTagPropertyName;

    @FXML
    private TextArea fldTagPropertyValue;


    @FXML
    private Button btnAddNewTag;

    @FXML
    private Button btnDeleteTag;

    @FXML
    private Button btnEditTagsProperties;

    @FXML
    private Button btnAddNewTagsProperties;

    @FXML
    private Button btnDeleteTagsProperties;


    @FXML
    private TableView<IVFXTagsTags> tblTagsTagsParent;

    @FXML
    private TableColumn<IVFXTagsTags, String> colTypeTblTagsTagsParent;

    @FXML
    private TableColumn<IVFXTagsTags, String> colNameTblTagsTagsParent;

    @FXML
    private Button btnAddNewTagsTagsParent;

    @FXML
    private Button btnDeleteTagsTagsParent;

    @FXML
    private Button btnGoToTagsTagsParent;

    @FXML
    private TableView<IVFXTagsTagsProperties> tblPropertiesTagsTagsParent;

    @FXML
    private TableColumn<IVFXTagsTagsProperties, String> colNameTblPropertiesTagsTagsParent;

    @FXML
    private TableColumn<IVFXTagsTagsProperties, String> colValueTblPropertiesTagsTagsParent;

    @FXML
    private TextField fldPropertyNameTagsTagsParent;

    @FXML
    private TextArea fldPropertyValueTagsTagsParent;

    @FXML
    private Button btnAddNewPropertyTagsTagsParent;

    @FXML
    private Button btnDeletePropertyTagsTagsParent;

    @FXML
    private TableView<IVFXTagsTags> tblTagsTagsChildren;

    @FXML
    private TableColumn<IVFXTagsTags, String> colTypeTblTagsTagsChildren;

    @FXML
    private TableColumn<IVFXTagsTags, String> colNameTblTagsTagsChildren;

    @FXML
    private Button btnAddNewTagsTagsChildren;

    @FXML
    private Button btnDeleteTagsTagsChildren;

    @FXML
    private Button btnGoToTagsTagsChildren;

    @FXML
    private TableView<IVFXTagsTagsProperties> tblPropertiesTagsTagsChildren;

    @FXML
    private TableColumn<IVFXTagsTagsProperties, String> colNameTblPropertiesTagsTagsChildren;

    @FXML
    private TableColumn<IVFXTagsTagsProperties, String> colValueTblPropertiesTagsTagsChildren;

    @FXML
    private TextField fldPropertyNameTagsTagsChildren;

    @FXML
    private TextArea fldPropertyValueTagsTagsChildren;

    @FXML
    private Button btnAddNewPropertyTagsTagsChildren;

    @FXML
    private Button btnDeletePropertyTagsTagsChildren;

    @FXML
    private Button btnOK;


    private static TagsController tagsController = new TagsController();
    private Stage controllerStage;
    private Scene controllerScene;
    public static IVFXProjects currentProject;                                                                          // Текуший проект
    public static IVFXFiles currentFile;                                                                                // Текущий файл
    public static IVFXTagsTypes currentTagType;                                                                         // Текущий тип тэга
    public static IVFXTags currentTag;                                                                                  // Текущий тэг
    public static IVFXTagsProperties currentTagProperty;                                                                // Текущий свойство тэга
    public static IVFXTagsTags currentTagTagParent;                                                                     // Текущая связка с родительским тэгом
    public static IVFXTagsTags currentTagTagChildren;                                                                   // Текущая связка с досерним тэгом
    public static IVFXTagsTagsProperties currentPropertyTagTagParent;                                                   // Текущее свойство текущей связки с родительским тэгом
    public static IVFXTagsTagsProperties currentPropertyTagTagChildren;                                                 // Текущее свойство текущей связки с дочерним тэгом

    private ObservableList<IVFXProjects> listProjects = FXCollections.observableArrayList();                            // список проектов
    private ObservableList<IVFXFiles> listFiles = FXCollections.observableArrayList();                                  // список файлов
    private ObservableList<IVFXTags> listTags = FXCollections.observableArrayList();                                    // список тэгов
    private ObservableList<IVFXTagsTypes> listTagsTypes = FXCollections.observableArrayList();                          // список типов тэгов
    private ObservableList<IVFXTagsProperties> listTagsProperties = FXCollections.observableArrayList();                // список свойств тэгов
    private ObservableList<IVFXTagsTags> listTagsTagsParent = FXCollections.observableArrayList();                      // список тэгов, родительских для текущего
    private ObservableList<IVFXTagsTags> listTagsTagsChildren = FXCollections.observableArrayList();                    // список тэгов, дочерних для текущего
    private ObservableList<IVFXTagsTagsProperties> listPropertiesTagsTagsParent = FXCollections.observableArrayList();  // список свойств связок тэга с родительскими тегами
    private ObservableList<IVFXTagsTagsProperties> listPropertiesTagsTagsChildren = FXCollections.observableArrayList();// список свойств связок тэга с дочерними тегами

    private VirtualFlow flowTblTags;

    private FilteredList<IVFXTags> filteredTags;

    // Инициализация
    @FXML
    void initialize() {

        listTagsTypes = FXCollections.observableArrayList(IVFXTagsTypes.loadList());
        cbCurrentTagType.setItems(listTagsTypes);

        doChUseTagType(null);
        doChUseProject(null);
        doChUseFile(null);

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
            btnDeleteTag.setDisable(false);
        } else {
            btnDeleteTag.setDisable(true);
        }

        doOnSelectRecordInTblTags();
        doOnSelectRecordInTblTagsProperties();
        doOnSelectRecordInTblTagsTagsParent();
        doOnSelectRecordInTblTagsTagsChildren();
        doOnSelectRecordInTblPropertiesTagsTagsParent();
        doOnSelectRecordInTblPropertiesTagsTagsChildren();

        /*********************
         * Настройка элементов
         *********************/

        // инициализируем поля таблицы tblTags (таблица "Тэги")
        colTagTypeTblTags.setCellValueFactory(new PropertyValueFactory<>("tagTypeName"));
        colPreviewTblTags.setCellValueFactory(new PropertyValueFactory<>("label1"));
        colPropertiesTblTags.setCellValueFactory(new PropertyValueFactory<>("propertiesValues"));

        // инициализируем поля таблицы tblTagsProperties (таблица "Свойства тэга")
        colNameTblTagsProperties.setCellValueFactory(new PropertyValueFactory<>("name"));
        colValueTblTagsProperties.setCellValueFactory(new PropertyValueFactory<>("value"));

        // инициализируем поля таблицы tblTagsTagsParent (таблица "Родительские тэги")
        colTypeTblTagsTagsParent.setCellValueFactory(new PropertyValueFactory<>("tagTypeNameParent"));
        colNameTblTagsTagsParent.setCellValueFactory(new PropertyValueFactory<>("labelParent"));

        // инициализируем поля таблицы tblTagsTagsChildren (таблица "Дочерние тэги")
        colTypeTblTagsTagsChildren.setCellValueFactory(new PropertyValueFactory<>("tagTypeNameChildren"));
        colNameTblTagsTagsChildren.setCellValueFactory(new PropertyValueFactory<>("labelChildren"));

        // инициализируем поля таблицы tblPropertiesTagsTagsParent (таблица "Свойства связки с родительским тэгом")
        colNameTblPropertiesTagsTagsParent.setCellValueFactory(new PropertyValueFactory<>("name"));
        colValueTblPropertiesTagsTagsParent.setCellValueFactory(new PropertyValueFactory<>("value"));

        // инициализируем поля таблицы tblPropertiesTagsTagsChildren (таблица "Свойства связки с дочерним тэгом")
        colNameTblPropertiesTagsTagsChildren.setCellValueFactory(new PropertyValueFactory<>("name"));
        colValueTblPropertiesTagsTagsChildren.setCellValueFactory(new PropertyValueFactory<>("value"));

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

        // делаем поле Value таблицы tblTagsProperties с переносом по словам и расширяемым по высоте
        colValueTblTagsProperties.setCellFactory(param -> {
            TableCell<IVFXTagsProperties, String> cell = new TableCell<>();
            Text text = new Text();
            text.setStyle("");
            cell.setGraphic(text);
            cell.setPrefHeight(Control.USE_COMPUTED_SIZE);
            text.textProperty().bind(cell.itemProperty());
            text.wrappingWidthProperty().bind(colValueTblTagsProperties.widthProperty());
            return cell;
        });

        // делаем поле Value таблицы tblPropertiesTagsTagsParent с переносом по словам и расширяемым по высоте
        colValueTblPropertiesTagsTagsParent.setCellFactory(param -> {
            TableCell<IVFXTagsTagsProperties, String> cell = new TableCell<>();
            Text text = new Text();
            text.setStyle("");
            cell.setGraphic(text);
            cell.setPrefHeight(Control.USE_COMPUTED_SIZE);
            text.textProperty().bind(cell.itemProperty());
            text.wrappingWidthProperty().bind(colValueTblPropertiesTagsTagsParent.widthProperty());
            return cell;
        });

        // делаем поле Value таблицы tblPropertiesTagsTagsChildren с переносом по словам и расширяемым по высоте
        colValueTblPropertiesTagsTagsChildren.setCellFactory(param -> {
            TableCell<IVFXTagsTagsProperties, String> cell = new TableCell<>();
            Text text = new Text();
            text.setStyle("");
            cell.setGraphic(text);
            cell.setPrefHeight(Control.USE_COMPUTED_SIZE);
            text.textProperty().bind(cell.itemProperty());
            text.wrappingWidthProperty().bind(colValueTblPropertiesTagsTagsChildren.widthProperty());
            return cell;
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

        // обработка события изменение поля fldTagPropertyValue
        fldTagPropertyValue.textProperty().addListener((observable, oldValue, newValue) -> {
            doFldTagPropertyValue();
        });

        // обработка события изменение поля fldPropertyValueTagsTagsParent
        fldPropertyValueTagsTagsParent.textProperty().addListener((observable, oldValue, newValue) -> {
            doFldPropertyValueTagsTagsParent();
        });

        // обработка события изменение поля fldPropertyValueTagsTagsChildren
        fldPropertyValueTagsTagsChildren.textProperty().addListener((observable, oldValue, newValue) -> {
            doFldPropertyValueTagsTagsChildren();
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

        // обработка события нажатия Enter в поле ctlSearch - переход на первую запись в таблице tblPersons
        fldFindTag.setOnKeyPressed(ke -> {
            if (ke.getCode().equals(KeyCode.ENTER))
            {
                tblTags.requestFocus();
                tblTags.getSelectionModel().select(0);
                tblTags.scrollTo(0);
            }
        });

        // обработка события отслеживания видимости на экране текущего персонажа в таблице tblTags
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

        // обработка события выбора записи в таблице tblTagsTagsParent
        tblTagsTagsParent.getSelectionModel().selectedItemProperty().addListener((v, oldValue, newValue) -> {
            if (newValue != null) {
                currentTagTagParent = newValue;
                doOnSelectRecordInTblTagsTagsParent();
            }
        });

        // обработка события выбора записи в таблице tblTagsTagsChildren
        tblTagsTagsChildren.getSelectionModel().selectedItemProperty().addListener((v, oldValue, newValue) -> {
            if (newValue != null) {
                currentTagTagChildren = newValue;
                doOnSelectRecordInTblTagsTagsChildren();
            }
        });

        // обработка события выбора записи в таблице tblPropertiesTagsTagsParent
        tblPropertiesTagsTagsParent.getSelectionModel().selectedItemProperty().addListener((v, oldValue, newValue) -> {
            if (newValue != null) {
                currentPropertyTagTagParent = newValue;
                doOnSelectRecordInTblPropertiesTagsTagsParent();
            }
        });

        // обработка события выбора записи в таблице tblPropertiesTagsTagsChildren
        tblPropertiesTagsTagsChildren.getSelectionModel().selectedItemProperty().addListener((v, oldValue, newValue) -> {
            if (newValue != null) {
                currentPropertyTagTagChildren = newValue;
                doOnSelectRecordInTblPropertiesTagsTagsChildren();
            }
        });

        // обработка события выбора записи в таблице tblTagsProperties
        tblTagsProperties.getSelectionModel().selectedItemProperty().addListener((v, oldValue, newValue) -> {
            if (newValue != null) {
                currentTagProperty = newValue;
                doOnSelectRecordInTblTagsProperties();
            }
        });


    }

    /*****************************************
     * Действия при выборе записей в таблицах
     *****************************************/

    // Действия при выборе записи в таблице tblTags
    private void doOnSelectRecordInTblTags() {

        if (currentTag != null) {

            btnDeleteTag.setDisable(false);

            btnSetTagPicture.setDisable(false);
            btnClearTagPicture.setDisable(false);
            btnAddNewTagsProperties.setDisable(false);
            btnAddNewTagsTagsParent.setDisable(false);
            btnAddNewTagsTagsChildren.setDisable(false);
            cbCurrentTagType.setDisable(false);

            // Загружаем большую картинку тэга, если она есть
            String pictureName = currentTag.getTagPictureFullSize();
            String pictureNameStub = currentTag.getTagPictureFullSizeStub();
            File picture = new File(pictureName);
            if(!picture.exists()) {
                picture = new File(pictureNameStub);
            }
            try {
                BufferedImage bufferedImage = ImageIO.read(picture);
                ImageView imageView = new ImageView(ConvertToFxImage.convertToFxImage(bufferedImage));
                lblFullPreview.setGraphic(imageView);
            } catch (IOException e) {
                e.printStackTrace();
            }

            // устанавливаем значени комбобокса типа тэга для выбранного тэга
            for (IVFXTagsTypes tagsTypes: listTagsTypes) {
                if (tagsTypes.getName().equals(currentTag.getIvfxEnumTagsTypes().name())) {
                    cbCurrentTagType.setValue(tagsTypes);
                    break;
                }
            }

            // загружаем список свойств для выбранного тега и привязываем к нему таблицу tblTagsProperties
            listTagsProperties = FXCollections.observableArrayList(IVFXTagsProperties.loadList(currentTag, true));
            tblTagsProperties.setItems(listTagsProperties);

            // Сбрасываем выбранные ранее свойства
            currentTagProperty = null;
            fldTagPropertyName.setText("");
            fldTagPropertyValue.setText("");
            fldTagPropertyName.setDisable(true);
            fldTagPropertyValue.setDisable(true);
            btnDeleteTagsProperties.setDisable(true);

            // загружаем список родителей текущего тэга и привязываем к нему таблицу tblTagsTagsParent
            listTagsTagsParent = FXCollections.observableArrayList(IVFXTagsTags.loadList(currentTag, true,true, null));
            tblTagsTagsParent.setItems(listTagsTagsParent);

            // Сбрасываем выбранные ранее свойства Parent
            currentTagTagParent = null;
            btnDeleteTagsTagsParent.setDisable(true);
            btnGoToTagsTagsParent.setDisable(true);
            tblPropertiesTagsTagsParent.setItems(null);
            currentPropertyTagTagParent = null;
            fldPropertyNameTagsTagsParent.setText("");
            fldPropertyValueTagsTagsParent.setText("");
            btnAddNewPropertyTagsTagsParent.setDisable(true);
            fldPropertyNameTagsTagsParent.setDisable(true);
            fldPropertyValueTagsTagsParent.setDisable(true);
            btnDeletePropertyTagsTagsParent.setDisable(true);

            // загружаем список детей текущего тэга и привязываем к нему таблицу tblTagsTagsChildren
            listTagsTagsChildren = FXCollections.observableArrayList(IVFXTagsTags.loadList(currentTag, false,true,null));
            tblTagsTagsChildren.setItems(listTagsTagsChildren);

            // Сбрасываем выбранные ранее свойства Children
            currentTagTagChildren = null;
            btnDeleteTagsTagsChildren.setDisable(true);
            btnGoToTagsTagsChildren.setDisable(true);
            tblPropertiesTagsTagsChildren.setItems(null);
            currentPropertyTagTagChildren = null;
            fldPropertyNameTagsTagsChildren.setText("");
            fldPropertyValueTagsTagsChildren.setText("");
            fldPropertyNameTagsTagsChildren.setDisable(true);
            fldPropertyValueTagsTagsChildren.setDisable(true);
            btnAddNewPropertyTagsTagsChildren.setDisable(true);
            btnDeletePropertyTagsTagsChildren.setDisable(true);

            selectAndSmartScrollTblTags(currentTag);

        } else {

            tblTagsProperties.setItems(null);
            cbCurrentTagType.setValue(null);
            btnSetTagPicture.setDisable(true);
            btnClearTagPicture.setDisable(true);
            btnDeleteTag.setDisable(true);
            btnAddNewTagsProperties.setDisable(true);
            btnAddNewTagsTagsParent.setDisable(true);
            btnAddNewTagsTagsChildren.setDisable(true);
            cbCurrentTagType.setDisable(true);
        }

    }

    // Действия при выборе записи в таблице tblTagsProperties
    private void doOnSelectRecordInTblTagsProperties() {
        if (currentTagProperty != null) {
            fldTagPropertyName.setText(currentTagProperty.getName());
            fldTagPropertyValue.setText(currentTagProperty.getValue());
            fldTagPropertyName.setDisable(false);
            fldTagPropertyValue.setDisable(false);
            btnDeleteTagsProperties.setDisable(false);
        } else {
            fldTagPropertyName.setText("");
            fldTagPropertyValue.setText("");
            fldTagPropertyName.setDisable(true);
            fldTagPropertyValue.setDisable(true);
            btnDeleteTagsProperties.setDisable(true);
        }

    }

    // Действия при выборе записи в таблице tblTagsTagsParent
    private void doOnSelectRecordInTblTagsTagsParent() {
        
        if (currentTagTagParent != null) {
            btnAddNewPropertyTagsTagsParent.setDisable(false);
            btnAddNewTagsTagsParent.setDisable(false);
            btnDeleteTagsTagsParent.setDisable(false);
            btnGoToTagsTagsParent.setDisable(false);

            // загружаем список свойств для выбранного родитель-тег и привязываем к нему таблицу tblPropertiesTagsTagsParent
            listPropertiesTagsTagsParent = FXCollections.observableArrayList(IVFXTagsTagsProperties.loadList(currentTagTagParent, true));
            tblPropertiesTagsTagsParent.setItems(listPropertiesTagsTagsParent);
        } else {
            btnAddNewPropertyTagsTagsParent.setDisable(true);
            btnAddNewTagsTagsParent.setDisable(true);
            btnDeleteTagsTagsParent.setDisable(true);
            btnGoToTagsTagsParent.setDisable(true);
        }

        // Сбрасываем выбранные ранее свойства Parent
        currentPropertyTagTagParent = null;
        doOnSelectRecordInTblPropertiesTagsTagsParent();
        
    }

    // Действия при выборе записи в таблице tblTagsTagsChildren
    private void doOnSelectRecordInTblTagsTagsChildren() {

        if (currentTagTagChildren != null) {
            btnAddNewPropertyTagsTagsChildren.setDisable(false);
            btnAddNewTagsTagsChildren.setDisable(false);
            btnDeleteTagsTagsChildren.setDisable(false);
            btnGoToTagsTagsChildren.setDisable(false);

            // загружаем список свойств для выбранного родитель-тег и привязываем к нему таблицу tblPropertiesTagsTagsChildren
            listPropertiesTagsTagsChildren = FXCollections.observableArrayList(IVFXTagsTagsProperties.loadList(currentTagTagChildren, true));
            tblPropertiesTagsTagsChildren.setItems(listPropertiesTagsTagsChildren);
        } else {
            btnAddNewPropertyTagsTagsChildren.setDisable(true);
            btnAddNewTagsTagsChildren.setDisable(true);
            btnDeleteTagsTagsChildren.setDisable(true);
            btnGoToTagsTagsChildren.setDisable(true);
        }

        // Сбрасываем выбранные ранее свойства Children
        currentPropertyTagTagChildren = null;
        doOnSelectRecordInTblPropertiesTagsTagsChildren();

    }

    // Действия при выборе записи в таблице tblTagsPropertiesTagsTagsParent
    private void doOnSelectRecordInTblPropertiesTagsTagsParent() {

        if (currentPropertyTagTagParent != null) {
            fldPropertyNameTagsTagsParent.setText(currentPropertyTagTagParent.getName());
            fldPropertyValueTagsTagsParent.setText(currentPropertyTagTagParent.getValue());
            fldPropertyNameTagsTagsParent.setDisable(false);
            fldPropertyValueTagsTagsParent.setDisable(false);
            btnDeletePropertyTagsTagsParent.setDisable(false);

        } else {
            fldPropertyNameTagsTagsParent.setText("");
            fldPropertyValueTagsTagsParent.setText("");
            fldPropertyNameTagsTagsParent.setDisable(true);
            fldPropertyValueTagsTagsParent.setDisable(true);
            btnDeletePropertyTagsTagsParent.setDisable(true);
        }

    }

    // Действия при выборе записи в таблице tblTagsPropertiesTagsTagsChildren
    private void doOnSelectRecordInTblPropertiesTagsTagsChildren() {

        if (currentPropertyTagTagChildren != null) {
            fldPropertyNameTagsTagsChildren.setText(currentPropertyTagTagChildren.getName());
            fldPropertyValueTagsTagsChildren.setText(currentPropertyTagTagChildren.getValue());
            fldPropertyNameTagsTagsChildren.setDisable(false);
            fldPropertyValueTagsTagsChildren.setDisable(false);
            btnDeletePropertyTagsTagsChildren.setDisable(false);

        } else {
            fldPropertyNameTagsTagsChildren.setText("");
            fldPropertyValueTagsTagsChildren.setText("");
            fldPropertyNameTagsTagsChildren.setDisable(true);
            fldPropertyValueTagsTagsChildren.setDisable(true);
            btnDeletePropertyTagsTagsChildren.setDisable(true);
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
        } else {
            cbProject.setDisable(true);
            cbProject.setItems(null);
            currentProject = null;
        }

        chUseFile.setDisable(true);
        chUseFile.setSelected(false);
        doChUseFile(null);
    }

    // Событие переключения флажка "Искать в свойствах"
    @FXML
    void doChFindInProperties(ActionEvent event) {

    }

    // Событие редактирование поля имени свойства тэга
    @FXML
    void doFldTagPropertyName(ActionEvent event) {
        if (currentTagProperty != null) {
            if (!currentTagProperty.getName().equals("name")) {
                currentTagProperty.setName(fldTagPropertyName.getText());
                currentTagProperty.save();
                tblTagsProperties.refresh();
                tblTags.refresh();
            }
        }

    }

    // Событие редактирование поля имени свойства для связки с дочерним тэгом
    @FXML
    void doFldPropertyNameTagsTagsChildren(ActionEvent event) {
        if (currentPropertyTagTagChildren != null) {
            currentPropertyTagTagChildren.setName(fldPropertyNameTagsTagsChildren.getText());
            currentPropertyTagTagChildren.save();
            tblPropertiesTagsTagsChildren.refresh();
        }
    }

    // Событие редактирование поля имени свойства для связки с родительским тэгом
    @FXML
    void doFldPropertyNameTagsTagsParent(ActionEvent event) {
        if (currentPropertyTagTagParent != null) {
            currentPropertyTagTagParent.setName(fldPropertyNameTagsTagsParent.getText());
            currentPropertyTagTagParent.save();
            tblPropertiesTagsTagsParent.refresh();
        }
    }

    // Метод изменения значения свойства тэга (обработчик события прописан в initialize)
    void doFldTagPropertyValue() {
        if (currentTagProperty != null) {
            currentTagProperty.setValue(fldTagPropertyValue.getText());
            currentTagProperty.save();
            tblTagsProperties.refresh();
            tblTags.refresh();
        }
    }

    // Метод изменения значения свойства родительского тэга (обработчик события прописан в initialize)
    void doFldPropertyValueTagsTagsParent() {
        if (currentPropertyTagTagParent != null) {
            currentPropertyTagTagParent.setValue(fldPropertyValueTagsTagsParent.getText());
            currentPropertyTagTagParent.save();
            tblPropertiesTagsTagsParent.refresh();
        }
    }

    // Метод изменения значения свойства дочернего тэга (обработчик события прописан в initialize)
    void doFldPropertyValueTagsTagsChildren() {
        if (currentPropertyTagTagChildren != null) {
            currentPropertyTagTagChildren.setValue(fldPropertyValueTagsTagsChildren.getText());
            currentPropertyTagTagChildren.save();
            tblPropertiesTagsTagsChildren.refresh();
        }
    }

    // Событие выбора типа тэга для комбобокса типов тегов текущего тега
    @FXML
    void doCbCurrentTagType(ActionEvent event) {
        if (currentTag != null) {
            currentTag.setIvfxEnumTagsTypes(IVFXTagsTypes.getEnumTagsTypes(cbCurrentTagType.getValue().getId()));
            currentTag.save();
        }
    }

    // Событие нажатие кнопки "Перейти к дочернему тэгу"
    @FXML
    void doBtnGoToTagsTagsChildren(ActionEvent event) {
        if (currentTagTagChildren != null) {
            doGoToTag(currentTagTagChildren.getIvfxTagChild());
        }
    }

    // Событие нажатие кнопки "Перейти к родительскому тэгу"
    @FXML
    void doBtnGoToTagsTagsParent(ActionEvent event) {
        if (currentTagTagParent != null) {
            doGoToTag(currentTagTagParent.getIvfxTagParent());
        }
    }


    // Событие нажатия кнопки "Добавить новый тэг"
    @FXML
    void doBtnAddNewTag(ActionEvent event) {

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Создание нового тэга.");
        alert.setHeaderText("Вы действительно хотите создать новый тэг?");
        alert.setContentText("Вы уверены?");
        Optional<ButtonType> option = alert.showAndWait();
        if (option.get() == ButtonType.OK) {

            IVFXEnumTagsTypes enumTagType = IVFXEnumTagsTypes.DESCRIPTION;
            if (currentTagType != null) enumTagType = IVFXTagsTypes.getEnumTagsTypes(currentTagType.getId());
            String name = "New " + enumTagType;
            IVFXTags createdTag = CreateNewTagController.getNewTag(currentProject, enumTagType,name, null, false);

            if (createdTag != null) {
                doGoToTag(createdTag);
            }

        }

    }

    // Событие нажатие кнопки "Добавить новую связку с новым дочерним тэгом"
    @FXML
    void doBtnAddNewTagsTagsChildren(ActionEvent event) {


        if (currentTag != null) {

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Добавление связки с новым дочерним тегом");
            alert.setHeaderText("Вы действительно хотите добавить связку с новым дочерним тегом?");
            alert.setContentText("Вы уверены?");
            Optional<ButtonType> option = alert.showAndWait();
            if (option.get() == ButtonType.OK) {

                IVFXTags childTag = TagsGetController.getTag();

                if (childTag != null) {
                    IVFXTagsTags tagsTags = IVFXTagsTags.getNewDbInstance(currentTag, childTag,true);
                    listTagsTagsChildren = FXCollections.observableArrayList(IVFXTagsTags.loadList(currentTag, false,true,null));
                    tblTagsTagsChildren.setItems(listTagsTagsChildren);
                    for (IVFXTagsTags tmp: listTagsTagsChildren) {
                        if (tmp.isEqual(tagsTags)) {
                            currentTagTagChildren = tmp;
                            break;
                        }
                    }
                    if (currentTagTagChildren != null) {
                        tblTagsTagsChildren.getSelectionModel().select(currentTagTagChildren);
                        doOnSelectRecordInTblTagsTagsChildren();
                    }
                }

            }

        }

    }

    // Событие нажатие кнопки "Добавить новую связку с новым родительским тэгом"
    @FXML
    void doBtnAddNewTagsTagsParent(ActionEvent event) {

        if (currentTag != null) {

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Добавление связки с новым родительским тегом");
            alert.setHeaderText("Вы действительно хотите добавить связку с новым родительским тегом?");
            alert.setContentText("Вы уверены?");
            Optional<ButtonType> option = alert.showAndWait();
            if (option.get() == ButtonType.OK) {

                IVFXTags parentTag = TagsGetController.getTag();

                if (parentTag != null) {
                    IVFXTagsTags tagsTags = IVFXTagsTags.getNewDbInstance(parentTag, currentTag,true);
                    listTagsTagsParent = FXCollections.observableArrayList(IVFXTagsTags.loadList(currentTag, true,true, null));
                    tblTagsTagsParent.setItems(listTagsTagsParent);
                    for (IVFXTagsTags tmp: listTagsTagsParent) {
                        if (tmp.isEqual(tagsTags)) {
                            currentTagTagParent = tmp;
                            break;
                        }
                    }
                    if (currentTagTagParent != null) {
                        tblTagsTagsParent.getSelectionModel().select(currentTagTagParent);
                        doOnSelectRecordInTblTagsTagsParent();
                    }
                }

            }

        }

    }

    // Событие нажатия кнопки "Добавить новое свойство тэга"
    @FXML
    void doBtnAddNewTagsProperties(ActionEvent event) {
        if (currentTag != null) {

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Добавление свойства тега");
            alert.setHeaderText("Вы действительно хотите добавить новое свойство для тега?");
            alert.setContentText("Имя и значение свойства будут сгенерированы автоматически.");
            Optional<ButtonType> option = alert.showAndWait();
            if (option.get() == ButtonType.OK) {

                IVFXTagsProperties tagProperty = IVFXTagsProperties.getNewDbInstance(currentTag);
                listTagsProperties = FXCollections.observableArrayList(IVFXTagsProperties.loadList(currentTag, true));
                tblTagsProperties.setItems(listTagsProperties);
                for (IVFXTagsProperties tmp: listTagsProperties) {
                    if (tmp.isEqual(tagProperty)) {
                        currentTagProperty = tmp;
                        break;
                    }
                }
                if (currentTagProperty != null) {
                    tblTagsProperties.getSelectionModel().select(currentTagProperty);
                    doOnSelectRecordInTblTagsProperties();
                }

            }

        }
    }

    // Событие нажатие кнопки "Добавить новое свойство для связки с дочерним тэгом"
    @FXML
    void doBtnAddNewPropertyTagsTagsChildren(ActionEvent event) {

        if (currentTagTagChildren != null) {

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Добавление свойства для связки с дочерним тэгом");
            alert.setHeaderText("Вы действительно хотите добавить новое свойство для связки с дочерним тэгом?");
            alert.setContentText("Имя и значение свойства будут сгенерированы автоматически.");
            Optional<ButtonType> option = alert.showAndWait();
            if (option.get() == ButtonType.OK) {

                IVFXTagsTagsProperties tagTagProperty = IVFXTagsTagsProperties.getNewDbInstance(currentTagTagChildren);
                listPropertiesTagsTagsChildren = FXCollections.observableArrayList(IVFXTagsTagsProperties.loadList(currentTagTagChildren, false));
                tblPropertiesTagsTagsChildren.setItems(listPropertiesTagsTagsChildren);
                for (IVFXTagsTagsProperties tmp: listPropertiesTagsTagsChildren) {
                    if (tmp.isEqual(tagTagProperty)) {
                        currentPropertyTagTagChildren = tmp;
                        break;
                    }
                }
                if (currentPropertyTagTagChildren != null) {
                    tblPropertiesTagsTagsChildren.getSelectionModel().select(currentPropertyTagTagChildren);
                    doOnSelectRecordInTblPropertiesTagsTagsChildren();
                }
            }

        }

    }

    // Событие нажатие кнопки "Добавить новое свойство для связки с родительским тэгом"
    @FXML
    void doBtnAddNewPropertyTagsTagsParent(ActionEvent event) {

        if (currentTagTagParent != null) {

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Добавление свойства для связки с родительским тэгом");
            alert.setHeaderText("Вы действительно хотите добавить новое свойство для связки с родительским тэгом?");
            alert.setContentText("Имя и значение свойства будут сгенерированы автоматически.");
            Optional<ButtonType> option = alert.showAndWait();
            if (option.get() == ButtonType.OK) {

                IVFXTagsTagsProperties tagTagProperty = IVFXTagsTagsProperties.getNewDbInstance(currentTagTagParent);
                listPropertiesTagsTagsParent = FXCollections.observableArrayList(IVFXTagsTagsProperties.loadList(currentTagTagParent, false));
                tblPropertiesTagsTagsParent.setItems(listPropertiesTagsTagsParent);
                for (IVFXTagsTagsProperties tmp: listPropertiesTagsTagsParent) {
                    if (tmp.isEqual(tagTagProperty)) {
                        currentPropertyTagTagParent = tmp;
                        break;
                    }
                }
                if (currentPropertyTagTagParent != null) {
                    tblPropertiesTagsTagsParent.getSelectionModel().select(currentPropertyTagTagParent);
                    doOnSelectRecordInTblPropertiesTagsTagsParent();
                }
            }

        }

    }


    // Событие нажатия кнопки "Удалить тэг"
    @FXML
    void doBtnDeleteTag(ActionEvent event) {

        if (currentTag != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Удаление тега.");
            alert.setHeaderText("Вы действительно хотите удалить тег?");
            alert.setContentText("В случае утвердительного ответа тег будет удалениз базы данных вместе со всеми своими потомками и его восстановление будет невозможно.\nВы уверены?");
            Optional<ButtonType> option = alert.showAndWait();
            if (option.get() == ButtonType.OK) {
                currentTag.delete();
                initListTags();
                currentTag = null;
                doOnSelectRecordInTblTags();
            }
        }

    }

    // Событие нажатие кнопки "Удалить связку с дочерним тэгом"
    @FXML
    void doBtnDeleteTagsTagsChildren(ActionEvent event) {

        if (currentTagTagChildren != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Удаление связи с дочерним тегом.");
            alert.setHeaderText("Вы действительно хотите удалить связь текущего тега с дочерним тегом?");
            alert.setContentText("В случае утвердительного ответа дочерний тег будет удален из потомков текущего из базы данных и его восстановление будет невозможно.\nВы уверены?");
            Optional<ButtonType> option = alert.showAndWait();
            if (option.get() == ButtonType.OK) {
                currentTagTagChildren.delete();
                listTagsTagsChildren = FXCollections.observableArrayList(IVFXTagsTags.loadList(currentTag, false,true, null));
                tblTagsTagsChildren.setItems(listTagsTagsChildren);
                currentTagTagParent = null;
                doOnSelectRecordInTblTagsTagsChildren();
            }
        }

    }

    // Событие нажатие кнопки "Удалить связку с родительским тэгом"
    @FXML
    void doBtnDeleteTagsTagsParent(ActionEvent event) {

        if (currentTagTagParent != null) {

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Удаление связи с родительским тегом.");
            alert.setHeaderText("Вы действительно хотите удалить связь текущего тега с родительским тегом?");
            alert.setContentText("В случае утвердительного ответа текущей тег будет удален из потомков родительского из базы данных и его восстановление будет невозможно.\nВы уверены?");
            Optional<ButtonType> option = alert.showAndWait();
            if (option.get() == ButtonType.OK) {
                currentTagTagParent.delete();
                listTagsTagsParent = FXCollections.observableArrayList(IVFXTagsTags.loadList(currentTag, true,true, null));
                tblTagsTagsParent.setItems(listTagsTagsParent);
                currentTagTagParent = null;
                doOnSelectRecordInTblTagsTagsParent();
            }
        }

    }


    // Событие нажатия кнопки "Удалить свойство тэга"
    @FXML
    void doBtnDeleteTagsProperties(ActionEvent event) {
        if (currentTagProperty != null) {
            if (!currentTagProperty.getName().equals("name")) {

                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Удаление свойства тега");
                alert.setHeaderText("Вы действительно хотите удалить свойство тега с именем «" + currentTagProperty.getName() + "» и значением «" + currentTagProperty.getValue() + "»?");
                alert.setContentText("В случае утвердительного ответа свойство тега будет удалено из базы данных и его восстановление будет невозможно.\nВы уверены, что хотите удалить свойство тега?");
                Optional<ButtonType> option = alert.showAndWait();
                if (option.get() == ButtonType.OK) {
                    currentTagProperty.delete();
                    listTagsProperties = FXCollections.observableArrayList(IVFXTagsProperties.loadList(currentTag, true));
                    tblTagsProperties.setItems(listTagsProperties);
                    currentTagProperty = null;
                    doOnSelectRecordInTblTagsProperties();
                }
            }
        }
    }

    // Событие нажатие кнопки "Удалить свойство для связки с дочерним тэгом"
    @FXML
    void doBtnDeletePropertyTagsTagsChildren(ActionEvent event) {
        if (currentPropertyTagTagChildren != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Удаление свойства связки с дочерним тегом");
            alert.setHeaderText("Вы действительно хотите удалить свойство связки с дочерним тегом с именем «" + currentPropertyTagTagChildren.getName() + "» и значением «" + currentPropertyTagTagChildren.getValue() + "»?");
            alert.setContentText("В случае утвердительного ответа свойство связки с дочерним тегом будет удалено из базы данных и его восстановление будет невозможно.\nВы уверены?");
            Optional<ButtonType> option = alert.showAndWait();
            if (option.get() == ButtonType.OK) {
                currentPropertyTagTagChildren.delete();
                listPropertiesTagsTagsChildren = FXCollections.observableArrayList(IVFXTagsTagsProperties.loadList(currentTagTagChildren, true));
                tblPropertiesTagsTagsChildren.setItems(listPropertiesTagsTagsChildren);
                currentPropertyTagTagChildren = null;
                doOnSelectRecordInTblPropertiesTagsTagsChildren();
            }
        }
    }

    // Событие нажатие кнопки "Удалить свойство для связки с родительским тэгом"
    @FXML
    void doBtnDeletePropertyTagsTagsParent(ActionEvent event) {
        if (currentPropertyTagTagParent != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Удаление свойства связки с родительским тегом");
            alert.setHeaderText("Вы действительно хотите удалить свойство связки с родительским тегом с именем «" + currentPropertyTagTagParent.getName() + "» и значением «" + currentPropertyTagTagParent.getValue() + "»?");
            alert.setContentText("В случае утвердительного ответа свойство связки с родительским тегом будет удалено из базы данных и его восстановление будет невозможно.\nВы уверены?");
            Optional<ButtonType> option = alert.showAndWait();
            if (option.get() == ButtonType.OK) {
                currentPropertyTagTagParent.delete();
                listPropertiesTagsTagsParent = FXCollections.observableArrayList(IVFXTagsTagsProperties.loadList(currentTagTagParent, true));
                tblPropertiesTagsTagsParent.setItems(listPropertiesTagsTagsParent);
                currentPropertyTagTagParent = null;
                doOnSelectRecordInTblPropertiesTagsTagsParent();
            }
        }
    }

    // Событие нажатие кнопки "Загрузить картинку"
    @FXML
    void doBtnSetTagPicture(ActionEvent event) {
        if (currentTag != null) {

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

                // получаем полный путь к этому файлу
                String fileSourceName = file.getAbsolutePath();

                currentTag.setPicture(fileSourceName);
                initListTags();
                for (IVFXTags tmp: listTags) {
                    if (tmp.isEqual(currentTag)) {
                        currentTag = tmp;
                        break;
                    }
                }
                tblTags.getSelectionModel().select(currentTag);
                doOnSelectRecordInTblTags();

            }

        }
    }

    // Событие нажатие кнопки "Очистить картинку"
    @FXML
    void doBtnClearTagPicture(ActionEvent event) {
        if (currentTag != null) {
            currentTag.clearPicture();
            initListTags();
            for (IVFXTags tmp: listTags) {
                if (tmp.isEqual(currentTag)) {
                    currentTag = tmp;
                    break;
                }
            }
            tblTags.getSelectionModel().select(currentTag);
            doOnSelectRecordInTblTags();
        }
    }

    // Событие нажатия кнопки "ОК"
    @FXML
    void doBtnOK(ActionEvent event) {
        tagsController.controllerStage.close();
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

        AnchorPane root = FXMLLoader.load(tagsController.getClass().getResource("../resources/fxml/Tags.fxml")); // в этот момент вызывается initialize()

        tagsController.controllerScene = new Scene(root);
        tagsController.controllerStage = new Stage();
        tagsController.controllerStage.setTitle("Редактор тэгов.");
        tagsController.controllerStage.setScene(tagsController.controllerScene);
        tagsController.controllerStage.initModality(Modality.APPLICATION_MODAL);

        tagsController.controllerStage.setOnCloseRequest(we -> {
            System.out.println("Закрытые окна редактора тэгов.");
        });

        tagsController.controllerStage.showAndWait();
        System.out.println("Завершение работы editTags");
    }


    // Метод вызова класса из других классов
    public void editTags() {

        try {

            AnchorPane root = FXMLLoader.load(tagsController.getClass().getResource("../resources/fxml/Tags.fxml")); // в этот момент вызывается initialize()

            tagsController.controllerScene = new Scene(root);
            tagsController.controllerStage = new Stage();
            tagsController.controllerStage.setTitle("Редактор тэгов.");
            tagsController.controllerStage.setScene(tagsController.controllerScene);
            tagsController.controllerStage.initModality(Modality.APPLICATION_MODAL);

            tagsController.controllerStage.setOnCloseRequest(we -> {
                System.out.println("Закрытые окна редактора тэгов.");
            });

            tagsController.controllerStage.showAndWait();


            System.out.println("Завершение работы editTags");

        } catch (IOException e) {
            e.printStackTrace();
        }
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
                    listTags = FXCollections.observableArrayList(IVFXTags.loadList(currentProject,true, currentTagType.getId()));
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

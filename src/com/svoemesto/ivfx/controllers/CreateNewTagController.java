package com.svoemesto.ivfx.controllers;

import com.sun.javafx.scene.control.skin.TableViewSkin;
import com.sun.javafx.scene.control.skin.VirtualFlow;
import com.svoemesto.ivfx.tables.*;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import java.util.function.Predicate;

public class CreateNewTagController {

    @FXML
    private AnchorPane ap;

    @FXML
    private ComboBox<IVFXEnumTagsTypes> cbTagType;

    @FXML
    private TextField fldTagName;

    @FXML
    private Button btnOK;

    @FXML
    private Button btnCancel;

    private static CreateNewTagController createNewTagController = new CreateNewTagController();
    private Stage controllerStage;
    private Scene controllerScene;

    private static IVFXProjects initProject;
    private static IVFXEnumTagsTypes initEnumTagsTypes;
    private static String initName;
    private static IVFXFrames initFrame;
    private static boolean initDisableChoiceTagType;
    private static IVFXTags createdTag;

    private IVFXEnumTagsTypes currentEnumTagsTypes;
    private ObservableList<IVFXEnumTagsTypes> listTagsTypes = FXCollections.observableArrayList();                          // список типов тэгов

    // Инициализация
    @FXML
    void initialize() {

        listTagsTypes = FXCollections.observableArrayList(IVFXEnumTagsTypes.loadList());
        cbTagType.setItems(listTagsTypes);

        if (currentEnumTagsTypes == null) currentEnumTagsTypes = IVFXEnumTagsTypes.DESCRIPTION;

        if (initEnumTagsTypes != null) {
            for (IVFXEnumTagsTypes enumTagsTypes: listTagsTypes) {
                if (enumTagsTypes.equals(initEnumTagsTypes)) {
                    currentEnumTagsTypes = enumTagsTypes;

                    break;
                }
            }
        }
        cbTagType.setValue(currentEnumTagsTypes);

        cbTagType.setDisable(initDisableChoiceTagType);
        fldTagName.setText(initName);

        /*********************
         * Обработчики событий
         *********************/

        // обработка события выбора записи в комбобоксе cbTagType
        cbTagType.getSelectionModel().selectedItemProperty().addListener((v, oldValue, newValue) -> {
            if (newValue != null) {
                currentEnumTagsTypes = newValue;
            }
        });

        // обработка события нажатия Enter в поле fldTagName - переход на кнопку OK
        fldTagName.setOnKeyPressed(ke -> {
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

    }


    /******************************
     * Методы обработчиков событий
     ******************************/

    @FXML
    void doBtnCancel(ActionEvent event) {
        createdTag = null;
        createNewTagController.controllerStage.close();
    }

    @FXML
    void doBtnOK(ActionEvent event) {

        createdTag = IVFXTags.getNewDbInstance(initProject, currentEnumTagsTypes, fldTagName.getText(), initFrame);
        createNewTagController.controllerStage.close();

    }

    /********
     * Методы
     ********/

    // Метод вызова класса из других классов
    public static IVFXTags getNewTag(IVFXProjects project, IVFXEnumTagsTypes enumTagsTypes, String name, IVFXFrames frame, boolean disableChoiceTagType) {

        initProject = project;
        initEnumTagsTypes = enumTagsTypes;
        initName = name;
        initFrame = frame;
        initDisableChoiceTagType = disableChoiceTagType;

        IVFXTags ivfxTag = null;

        try {

            AnchorPane root = FXMLLoader.load(createNewTagController.getClass().getResource("../resources/fxml/CreateNewTag.fxml")); // в этот момент вызывается initialize()

            createNewTagController.controllerScene = new Scene(root);
            createNewTagController.controllerStage = new Stage();
            createNewTagController.controllerStage.setTitle("Создание нового тэга.");
            createNewTagController.controllerStage.setScene(createNewTagController.controllerScene);
            createNewTagController.controllerStage.initModality(Modality.APPLICATION_MODAL);

            createNewTagController.controllerStage.setOnCloseRequest(we -> {
                System.out.println("Закрытые окна создания нового тэга.");
            });

            createNewTagController.controllerStage.showAndWait();

            ivfxTag = createdTag;

        } catch (IOException e) {
            e.printStackTrace();
        }

        return ivfxTag;

    }

}

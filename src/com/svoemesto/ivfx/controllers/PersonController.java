package com.svoemesto.ivfx.controllers;

import com.sun.javafx.scene.control.skin.TableViewSkin;
import com.sun.javafx.scene.control.skin.VirtualFlow;
import com.svoemesto.ivfx.Main;
import com.svoemesto.ivfx.tables.*;
import com.svoemesto.ivfx.utils.ConvertToFxImage;

import com.svoemesto.ivfx.utils.OverlayImage;
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
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class PersonController extends Application {

    @FXML
    private TextField ctlSearch;

    @FXML
    private TableView<IVFXPersons> tblPersons;

    @FXML
    private TableColumn<IVFXPersons, String> colPreview;

    @FXML
    private TableColumn<IVFXPersons, String> colName;

    @FXML
    private TableColumn<IVFXPersons, String> colDescription;

    @FXML
    private Label ctlPicture;

    @FXML
    private TextField ctlName;

    @FXML
    private TextField ctlDescription;

    @FXML
    private Button btnOK;

    @FXML
    private Button btnCancel;

    @FXML
    private Button btnCreateNewPerson;

    @FXML
    private TableView<IVFXGroupsPersons> tblGroupsForPerson;

    @FXML
    private TableColumn<IVFXGroupsPersons, String> colGrpNameForPerson;

    @FXML
    private TableView<IVFXGroups> tblGroupsForAll;

    @FXML
    private TableColumn<IVFXGroups, String> colGrpNameForAll;
    @FXML
    private Button btnAllGroupsToPerson;

    @FXML
    private Button btnSelGroupsToPerson;

    @FXML
    private Button btnSelGroupsFromPerson;

    @FXML
    private Button btnAllGroupsFromPerson;

    @FXML
    private ContextMenu contMenuAllGroups;

    @FXML
    private MenuItem contMenuAllGroupsDoAddNewGroup;

    @FXML
    private MenuItem contMenuAllGroupsDoRenameGroup;

    @FXML
    private MenuItem contMenuAllGroupsDoDeleteGroup;

    @FXML
    private Button btnDeletePerson;

    @FXML
    private Button btnChangePersonPicture;

    private ObservableList<IVFXPersons> listPersons = FXCollections.observableArrayList();
    private ObservableList<IVFXGroups> listGroupsForAll = FXCollections.observableArrayList();
    private ObservableList<IVFXGroupsPersons> listGroupsForPerson = FXCollections.observableArrayList();
    private VirtualFlow flowTblPersons;

    private static IVFXProjects currentProject;
    private static IVFXPersons currentPerson;
    private static Stage controllerStage;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Main.mainConnection = Database.getConnection();
//        IVFXProjects project = IVFXProjects.load(1);
        IVFXProjects project = IVFXProjects.load(2);
        IVFXPersons person = PersonController.getPerson(project);
        if (person != null) {
            System.out.println(person.getName());
        }

    }

    public static IVFXPersons getPerson(IVFXProjects ivfxProject, IVFXPersons initializePerson) {
        currentPerson = initializePerson;
        return getPerson(ivfxProject);
    }

    public static IVFXPersons getPerson(IVFXProjects ivfxProject) {

        IVFXPersons ivfxPerson = currentPerson;
        currentProject = ivfxProject;

        try {
            Parent root = FXMLLoader.load(PersonController.class.getResource("../resources/fxml/Persons.fxml"));
            controllerStage = new Stage();
            controllerStage.setTitle("Персонажи проекта");
            controllerStage.setScene(new Scene(root));
            controllerStage.initModality(Modality.APPLICATION_MODAL);
            controllerStage.showAndWait();
            ivfxPerson = currentPerson;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ivfxPerson;
    }


    @FXML
    void initialize() {

        listPersons = FXCollections.observableArrayList(IVFXPersons.loadList(currentProject,true));
        listGroupsForAll = FXCollections.observableArrayList(IVFXGroups.loadList(currentProject));
        FilteredList<IVFXPersons> filteredPerson = new FilteredList<>(listPersons, e -> true);

        ctlName.setDisable(currentPerson == null);
        ctlDescription.setDisable(currentPerson == null);
        btnDeletePerson.setDisable(currentPerson == null);
        btnChangePersonPicture.setDisable(currentPerson == null);

        colPreview.setCellValueFactory(new PropertyValueFactory<>("preview1"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        tblPersons.setItems(listPersons);

        colGrpNameForAll.setCellValueFactory(new PropertyValueFactory<>("name"));
        tblGroupsForAll.setItems(listGroupsForAll);
        tblGroupsForAll.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        // выбор записи в таблице tblPersons
        tblPersons.getSelectionModel().selectedItemProperty().addListener((v, oldValue, newValue) -> {
            if (newValue != null) {
                currentPerson = newValue;

                listGroupsForPerson = FXCollections.observableArrayList(IVFXGroupsPersons.loadList(currentPerson,false));
                colGrpNameForPerson.setCellValueFactory(new PropertyValueFactory<>("groupName"));
                tblGroupsForPerson.setItems(listGroupsForPerson);

                ctlName.setDisable(false);
                ctlDescription.setDisable(false);
                btnDeletePerson.setDisable(false);
                btnChangePersonPicture.setDisable(false);

                ctlName.setText(currentPerson.getName());
                ctlDescription.setText(currentPerson.getDescription());

                String pictureName = currentPerson.getPersonPictureFullSize();
                String pictureNameStub = currentPerson.getPersonPictureFullSizeStub();
                File picture = new File(pictureName);
                if(!picture.exists()) {
                    picture = new File(pictureNameStub);
                }
                try {
                    BufferedImage bufferedImage = ImageIO.read(picture);
                    ImageView imageView = new ImageView(ConvertToFxImage.convertToFxImage(bufferedImage));
                    ctlPicture.setGraphic(imageView);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        // изменение поля "Name"
        ctlName.textProperty().addListener((observable, oldValue, newValue) -> {
            if (currentPerson != null) {
                currentPerson.setName(ctlName.getText());
                for (IVFXPersons person: listPersons) {
                    if (person.getId() == currentPerson.getId()) {
                        person.setName(ctlName.getText());

                        String fileName = person.getPersonPicturePreview();
                        File file = new File(fileName);
                        for (int i = 0; i < 8; i++) {
                            person.setLabel(new Label(person.getName()), i);
                            person.getLabel()[i].setPrefWidth(135);
                        }
                        if (!file.exists()) {
                            fileName = person.getPersonPicturePreviewStub();
                            file = new File(fileName);
                        }
                        try {
                            BufferedImage bufferedImage = ImageIO.read(file);
                            for (int i = 0; i < 8; i++) {
                                person.setPreview(new ImageView(ConvertToFxImage.convertToFxImage(OverlayImage.setOverlayUnderlineText(bufferedImage, person.getName()))), i);
                                person.getLabel()[i].setGraphic(person.getPreview()[i]);
                                person.getLabel()[i].setContentDisplay(ContentDisplay.TOP);
                            }

                        } catch (IOException e) {}


                        break;
                    }
                }
                tblPersons.refresh();
                currentPerson.save();
            }

        });

        // изменение поля "Description"
        ctlDescription.textProperty().addListener((observable, oldValue, newValue) -> {
            if (currentPerson != null) {
                currentPerson.setDescription(ctlDescription.getText());
                for (IVFXPersons person: listPersons) {
                    if (person.getId() == currentPerson.getId()) {
                        person.setDescription(ctlDescription.getText());
                        break;
                    }
                }
                tblPersons.refresh();
                currentPerson.save();
            }

        });

        ctlSearch.setOnKeyReleased(e -> {
            ctlSearch.textProperty().addListener((v, oldValue, newValue) -> {
                filteredPerson.setPredicate((Predicate<? super IVFXPersons>) ivfxPerson-> {
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
            SortedList<IVFXPersons> sortedPerson = new SortedList<>(filteredPerson);
            sortedPerson.comparatorProperty().bind(tblPersons.comparatorProperty());
            tblPersons.setItems(sortedPerson);
            if (sortedPerson.size() > 0) {
                tblPersons.getSelectionModel().select(sortedPerson.get(0));
                tblPersons.scrollTo(sortedPerson.get(0));
            } else {
                currentPerson = null;
                ctlName.setText("");
                ctlDescription.setText("");
                ctlName.setDisable(true);
                ctlDescription.setDisable(true);
                ctlPicture.setGraphic(null);
            }
        });

        // нажатие Enter в поле ctlSearch - переход на первую запись в таблице tblPersons
        ctlSearch.setOnKeyPressed(ke -> {
            if (ke.getCode().equals(KeyCode.ENTER))
            {
                tblPersons.requestFocus();
                tblPersons.getSelectionModel().select(0);
                tblPersons.scrollTo(0);
            }
        });

        // событие отслеживани видимости на экране текущего персонажа в таблице tblPersons
        tblPersons.skinProperty().addListener((ChangeListener<Skin>) (ov, t, t1) -> {
            if (t1 == null) {
                return;
            }

            TableViewSkin tvs = (TableViewSkin) t1;
            ObservableList<Node> kids = tvs.getChildren();//    getChildrenUnmodifiable();

            if (kids == null || kids.isEmpty()) {
                return;
            }
            flowTblPersons = (VirtualFlow) kids.get(1);
        });


        if (currentPerson != null) {
            for (IVFXPersons person : listPersons) {
                if (person.getId() == currentPerson.getId()) {
                    currentPerson = person;
                    break;
                }
            }
            tblPersons.getSelectionModel().select(currentPerson);
            tblPersons.scrollTo(currentPerson);


        }

    }


    @FXML
    void doChangePersonPicture(ActionEvent event) {

    }

    @FXML
    void doGroupsOnDragDetected(MouseEvent event) {
        Dragboard db = tblGroupsForAll.startDragAndDrop(TransferMode.ANY);
        ClipboardContent content = new ClipboardContent();
        content.putString("tblGroupsForAll");
        db.setContent(content);
        event.consume();
    }

    @FXML
    void doGroupPersonOnDragDetected(MouseEvent event) {
        Dragboard db = tblGroupsForPerson.startDragAndDrop(TransferMode.ANY);
        ClipboardContent content = new ClipboardContent();
        content.putString("tblGroupsForPerson");
        db.setContent(content);
        event.consume();
    }



    @FXML
    void doGroupPersonOnDragOver(DragEvent event) {

        if (event.getGestureSource() == tblGroupsForAll) {
            event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
        }

        event.consume();
    }

    @FXML
    void doGroupsOnDragOver(DragEvent event) {
        if (event.getGestureSource() == tblGroupsForPerson) {
            event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
        }

        event.consume();
    }

    @FXML
    void doGroupPersonOnDragDropped(DragEvent event) {

        boolean success = false;
        if (event.getGestureSource() == tblGroupsForAll) {
            List<IVFXGroups> listGroups = tblGroupsForAll.getSelectionModel().getSelectedItems();
            addNewGroupsToPerson(listGroups, currentPerson);
            listGroupsForPerson = FXCollections.observableArrayList(IVFXGroupsPersons.loadList(currentPerson,false));
            tblGroupsForPerson.setItems(listGroupsForPerson);

            success = true;
        }

        event.setDropCompleted(success);
        event.consume();
    }

    @FXML
    void doGroupsOnDragDropped(DragEvent event) {

        boolean success = false;
        if (event.getGestureSource() == tblGroupsForPerson) {
            List<IVFXGroupsPersons> listGroupsPersons = tblGroupsForPerson.getSelectionModel().getSelectedItems();
            deleteGroupsFromPerson(listGroupsPersons);
            listGroupsForPerson = FXCollections.observableArrayList(IVFXGroupsPersons.loadList(currentPerson,false));
            tblGroupsForPerson.setItems(listGroupsForPerson);

            success = true;
        }

        event.setDropCompleted(success);
        event.consume();
    }


    @FXML
    void doCancel(ActionEvent event) {
        currentPerson = null;
        controllerStage.close();
    }



    @FXML
    void doOK(ActionEvent event) {
        controllerStage.close();
    }

    @FXML
    void doCreateNewPerson(ActionEvent event) {
        ClearFilter();
        IVFXPersons newPerson = IVFXPersons.getNewDbInstance(currentProject);
        currentPerson = IVFXPersons.load(newPerson.getId(),true);
        initialize();
    }

    @FXML
    void onDeletePerson(ActionEvent event) {
        if (currentPerson != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Удаление персонажа");
            alert.setHeaderText("Вы действительно хотите удалить персонажа «" + currentPerson.getName() + "»?");
            alert.setContentText("В случае утвердительного ответа персонаж будет удален из базы данных и его восстановление будет невозможно.\nВы уверены, что хотите удалить персонажа?");
            Optional<ButtonType> option = alert.showAndWait();
            if (option.get() == ButtonType.OK) {
                currentPerson.delete();
                ClearFilter();
                currentPerson = null;
                initialize();
            }
        }

    }

    @FXML
    void doOnActionContMenuAllGroupsDoAddNewGroup(ActionEvent event) {

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Новая группа");
        dialog.setHeaderText("Создание новой группы");
        dialog.setContentText("Введите имя новой группы персонажей");
        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            String groupName = result.get();
            if (!groupName.equals("")) {
                IVFXGroups group = IVFXGroups.loadByName(groupName, currentProject);
                if (group == null) {
                    group = IVFXGroups.getNewDbInstance(currentProject);
                    group.setName(groupName);
                    group.save();

                    listGroupsForAll = FXCollections.observableArrayList(IVFXGroups.loadList(currentProject));
                    tblGroupsForAll.setItems(listGroupsForAll);

                }
            }
        }

    }

    @FXML
    void doOnActionContMenuAllGroupsDoDeleteGroup(ActionEvent event) {

        List<IVFXGroups> selectedGroups = tblGroupsForAll.getSelectionModel().getSelectedItems();
        if (selectedGroups.size() == 1) {
            IVFXGroups group = selectedGroups.get(0);

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Удаление группы");
            alert.setHeaderText("Вы действительно хотите удалить группу «" + group.getName() + "»?");
            alert.setContentText("В случае утвердительного ответа группа будет удалена из базы данных и её восстановление будет невозможно.\nВы уверены, что хотите удалить группу?");
            Optional<ButtonType> option = alert.showAndWait();
            if (option.get() == ButtonType.OK) {
                group.delete();

                listGroupsForAll = FXCollections.observableArrayList(IVFXGroups.loadList(currentProject));
                tblGroupsForAll.setItems(listGroupsForAll);

                listGroupsForPerson = FXCollections.observableArrayList(IVFXGroupsPersons.loadList(currentPerson,false));
                tblGroupsForPerson.setItems(listGroupsForPerson);

            }
        }



    }

    @FXML
    void doOnActionContMenuAllGroupsDoRenameGroup(ActionEvent event) {

        List<IVFXGroups> selectedGroups = tblGroupsForAll.getSelectionModel().getSelectedItems();
        if (selectedGroups.size() == 1) {
            IVFXGroups group = selectedGroups.get(0);

            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Переименование группы");
            dialog.setHeaderText("Переименование группы");
            dialog.setContentText("Введите новое имя группы «" + group.getName() + "»");
            Optional<String> result = dialog.showAndWait();
            if (result.isPresent()) {
                String groupName = result.get();
                if (!groupName.equals("")) {
                    IVFXGroups tmpGroup = IVFXGroups.loadByName(groupName, currentProject);
                    if (tmpGroup == null) {
                        group.setName(groupName);
                        group.save();

                        listGroupsForAll = FXCollections.observableArrayList(IVFXGroups.loadList(currentProject));
                        tblGroupsForAll.setItems(listGroupsForAll);

                        listGroupsForPerson = FXCollections.observableArrayList(IVFXGroupsPersons.loadList(currentPerson,false));
                        tblGroupsForPerson.setItems(listGroupsForPerson);

                    }
                }
            }
        }


    }

    private void addNewGroupsToPerson(List<IVFXGroups> listGroups, IVFXPersons ivfxPerson) {
        if (listGroups.size() > 0 && ivfxPerson != null) {
            for (IVFXGroups group : listGroups) {
                IVFXGroupsPersons.getNewDbInstance(group, ivfxPerson, false);
            }
        }
    }

    private void deleteGroupsFromPerson(List<IVFXGroupsPersons> listGroupsPersons) {
        if (listGroupsPersons.size() > 0) {
            for (IVFXGroupsPersons groupPerson : listGroupsPersons) {
                groupPerson.delete();
            }
        }
    }

    void ClearFilter() {
        ctlSearch.setText("");
    }


}

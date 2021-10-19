package com.svoemesto.ivfx.controllers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;
import com.sun.javafx.scene.control.skin.TableViewSkin;
import com.sun.javafx.scene.control.skin.VirtualFlow;
import com.svoemesto.ivfx.Main;
import com.svoemesto.ivfx.tables.*;
import com.svoemesto.ivfx.utils.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.controlsfx.control.RangeSlider;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class FacesController extends Application {

    @FXML
    private ComboBox<IVFXProjects> cbProject;

    @FXML
    private ComboBox<IVFXFiles> cbFile;

    @FXML
    private TableView<IVFXFaces> tblFaces;

    @FXML
    private Button btnLoadFacesAndTags;

    @FXML
    private Button btnClearNonFavoriteFaces;

    @FXML
    private Button btnClearNonHTMLFaces;


    @FXML
    private CheckBox cbLoadOnlyManualFaces;

    @FXML
    private VBox vbBoxForSliderFaces;

    @FXML
    private TableColumn<IVFXFaces, String> colFaceLabel;

    @FXML
    private TableColumn<IVFXFaces, String> colFaceLabelPerson;

    @FXML
    private TableColumn<IVFXFaces, Double> colFaceProba;

    @FXML
    private Pane paneTagFav;

    @FXML
    private Label lblDragTagToAddFav;

    @FXML
    private Label lblDragTagToDelFav;

    @FXML
    private Label lblPictureFullFrame;

    @FXML
    private CheckBox cbPictureLoadAuto;

    @FXML
    private CheckBox cbPictureShowFaces;

    @FXML
    private Button btnPictureCreateNewTag;

    @FXML
    private TableColumn<IVFXFaces, String> colFacePersonName;

    @FXML
    private Button btnOK;

    @FXML
    private TextField fldFindTagsAll;

    @FXML
    private Circle shapePictureDrag;

    @FXML
    private CheckBox chFindTagsAllInProperties;

    @FXML
    private TableView<IVFXTags> tblTagsAll;

    @FXML
    private TableColumn<IVFXTags, String> colTypeTblTagsAll;

    @FXML
    private TableColumn<IVFXTags, String> colNameTblTagsAll;

    @FXML
    private TableView<IVFXFaces> tblFacesTagManual;

    @FXML
    private TableColumn<IVFXFaces, String> colFaceTagManualLabel;

    @FXML
    private TableView<IVFXFaces> tblFacesTagRecognized;

    @FXML
    private TableColumn<IVFXFaces, String> colFaceTagRecognizedLabel;

    @FXML
    private TableColumn<IVFXFaces, Double> colFaceTagRecognizedProba;

    @FXML
    private TableView<IVFXFaces> tblNoFaces;

    @FXML
    private TableColumn<IVFXFaces, String> colNoFaceLabel;

    @FXML
    private TableView<IVFXFaces> tblFacesExtras;

    @FXML
    private TableColumn<IVFXFaces, String> colFaceExtrasLabel;

    @FXML
    private Button btnExtractAndTrain;


    @FXML
    private Button btnReloadFaces;

    @FXML
    private Button btnExportAndRecognize;

    @FXML
    private Button btnLoadFacesManual;

    @FXML
    private Button btnLoadFacesRecognized;


    @FXML
    void doBtnOK(ActionEvent event) {

    }

    @FXML
    void doChFindTagsAllInProperties(ActionEvent event) {

    }

    private static FacesController facesController = new FacesController();
    private Stage controllerStage;
    private Scene controllerScene;

    public static IVFXProjects currentProject;                                                                          // Текуший проект     // Текущий файл
    public static IVFXFiles currentFile;                                                                          // Текуший проект     // Текущий файл
    public static IVFXTags currentTagAll;
    public static IVFXTags currentTagAllHovered;

    public static boolean isNeedToAddFacesDraggedToTag = false;

    private VirtualFlow flowTblFaces;
    private VirtualFlow flowTblNoFaces;
    private VirtualFlow flowTblFacesExtras;
    private VirtualFlow flowTblFacesTagManual;
    private VirtualFlow flowTblFacesTagRecognized;
    private VirtualFlow flowTblTagsAll;

    private FilteredList<IVFXTags> filteredTagsAll;

    private ObservableList<IVFXProjects> listProjects = FXCollections.observableArrayList();
    private ObservableList<IVFXFiles> listFiles = FXCollections.observableArrayList();
    private ObservableList<IVFXFaces> listFaces = FXCollections.observableArrayList();
    private ObservableList<IVFXFaces> listFacesTagManual = FXCollections.observableArrayList();
    private ObservableList<IVFXFaces> listFacesTagRecognized = FXCollections.observableArrayList();
    private ObservableList<IVFXFaces> listNoFaces = FXCollections.observableArrayList();
    private ObservableList<IVFXFaces> listFacesExtras = FXCollections.observableArrayList();
    private ObservableList<IVFXTags> listTagsAll = FXCollections.observableArrayList();
    private ObservableList<IVFXFaces> listFacesDragged = FXCollections.observableArrayList();

    private int pictW = 135;  // ширина картинки
    private int pictH = 75;   // высота картинки
    private int heightPadding = 5;   // по высоте двойной отступ
    private int widthPadding = 5; // по ширине двойной отступ
    private String fxBorderDefault = "-fx-border-color:#0f0f0f;-fx-border-width:1";  // стиль бордюра лейбла по-умолчаснию
    private String fxBorderFocused = "-fx-border-color:YELLOW;-fx-border-width:1";  // стиль бордюра лейбла по-умолчаснию
    private String fxBorderHovered = "-fx-border-color:GREEN;-fx-border-width:2";  // стиль бордюра лейбла по-умолчаснию
    private String fxBorderSelected = "-fx-border-color:RED;-fx-border-width:1";  // стиль бордюра лейбла по-умолчаснию

    private static IVFXTagsFavorites hoveredTagFav;
    private static IVFXTagsFavorites currentTagFav;
    private static IVFXTagsFavorites draggedTagFav;
    private String currentFramePreviewFile;
    private String currentFrameFullSizeFile;
    private IVFXFrames currentFrame;

    public static double minProba = 0.5;
    private static RangeSlider sliderFaces = new RangeSlider(0, 1, 0, 0.5);

    private void showTagFavPage() {
        Pane pane = paneTagFav;

        int countRows = (int) ((pane.getHeight()-heightPadding)/pictH); // кол-во строк на странице
        int countCols = (int) ((pane.getWidth()-widthPadding)/pictW); // кол-во столбцов на странице
        boolean isFillColumns = false; // признак "по столбцам"
        pane.getChildren().clear(); // очищаем пэйн от старых лейблов
        List<IVFXTagsFavorites> listTagFav = IVFXTagsFavorites.loadList();
        for (IVFXTagsFavorites tagFav: listTagFav) {
            // получаем координаты строка-столбец для текущей записи
            int[] coord = getRowCol(countRows, countCols,tagFav.getOrder(), isFillColumns);
            int row = coord[0];
            int col = coord[1];

            // если какая-то из координат нулевая - матрица заполнена, прекращаем перебор
            if (row == 0 || col == 0) break;

            int x = widthPadding + (col-1) * (pictW+2);
            int y = heightPadding + (row-1) * (pictH+2);

            IVFXTags tag = tagFav.getIvfxTag();
            if (tag != null) {

                Label lbl = tag.getLabel2();

                lbl.setTranslateX(x);
                lbl.setTranslateY(y);
                lbl.setMinSize(pictW+2, pictH+2);
                lbl.setPrefSize(pictW, pictH);
                lbl.setMaxSize(pictW+2, pictH+2);
                lbl.setStyle(fxBorderDefault);
                lbl.setAlignment(Pos.CENTER);

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


                lbl.setContextMenu(contextMenu);
                pane.getChildren().add(lbl);

                //событие "наведение мыши"
                lbl.setOnMouseEntered(e -> {
                    lbl.setStyle(fxBorderFocused);
                    lbl.toFront();
                    hoveredTagFav = tagFav;
                });

                //событие "уход мыши"
                lbl.setOnMouseExited(e -> {
                    if (currentTagFav != null && hoveredTagFav != null && hoveredTagFav.isEqual(currentTagFav)) {
                        lbl.setStyle(fxBorderSelected);
                    } else {
                        lbl.setStyle(fxBorderDefault);
                    }
                });

                //событие клика
                lbl.setOnMouseClicked(new EventHandler<MouseEvent>()  {
                    @Override
                    public void handle(MouseEvent mouseEvent) {
                        if(mouseEvent.getButton().equals(MouseButton.PRIMARY)){
                            if(mouseEvent.getClickCount() == 1) { // одинарный клик ЛКМ
                                currentTagFav = tagFav;
                            }
                            if(mouseEvent.getClickCount() == 2){ // двойной клик ЛКМ
                                currentTagFav = tagFav;
                                for (int i = 0; i < listTagsAll.size(); i++) {
                                    IVFXTags tag = listTagsAll.get(i);
                                    if (tag.getId() == tagFav.getTagId()) {
                                        currentTagAll = tag;
                                        tblTagsAll.getSelectionModel().select(i);
                                        tblTagsAllSmartScrollToCurrent();
                                        break;
                                    }
                                }
                            }
                        } else {
                            if (mouseEvent.getButton().equals(MouseButton.SECONDARY)){
                                if(mouseEvent.getClickCount() == 1){ // клик ПКМ

                                }
                            }
                        }
                    }
                });

                // событие начала перетаскивания лейбла
                lbl.setOnDragDetected(e -> {
                    draggedTagFav = tagFav;
                    Dragboard db = lbl.startDragAndDrop(TransferMode.ANY);
                    ClipboardContent content = new ClipboardContent();
                    content.putString("lblTagFav");
                    db.setContent(content);
                    e.consume();
                });

                lbl.setOnDragEntered(e->{
                    lbl.setStyle(fxBorderFocused);
                    lbl.toFront();
                });

                lbl.setOnDragExited(e -> {
                    if (currentTagFav != null && hoveredTagFav != null && hoveredTagFav.isEqual(currentTagFav)) {
                        lbl.setStyle(fxBorderSelected);
                    } else {
                        lbl.setStyle(fxBorderDefault);
                    }
                });

                // событие перетаскивания над лейблом
                lbl.setOnDragOver(e -> {
                    if (e.getGestureSource() == tblTagsAll ||
                            e.getDragboard().getString().equals("lblTagFav") ||
                            e.getDragboard().getString().equals("shapePictureDrag") ||
                            e.getGestureSource() == tblFaces ||
                            e.getGestureSource() == tblFacesTagManual ||
                            e.getGestureSource() == tblFacesTagRecognized ||
                            e.getGestureSource() == tblFacesExtras) {
                        e.acceptTransferModes(TransferMode.COPY_OR_MOVE);

                    }
                    e.consume();
                });

                // событие отпускания перетаскивания над лейблом
                lbl.setOnDragDropped(event -> {

                    IVFXTagsFavorites droppedTagFav = tagFav;

                    boolean isNeedToAdd = false;

                    boolean success = false;
                    if (event.getGestureSource() == tblTagsAll) {

                        System.out.println("Drop «" + currentTagAll.getName() + "» to «" + droppedTagFav.getIvfxTag().getName() + "»");
                        showTagFavPage();

                        success = true;
                    } else if (event.getDragboard().getString().equals("lblTagFav")) {

                        System.out.println("Drop «" + draggedTagFav.getIvfxTag().getName() + "» to «" + droppedTagFav.getIvfxTag().getName() + "»");
                        draggedTagFav.setOrderBefore(droppedTagFav);
                        showTagFavPage();

                    } else if (event.getGestureSource() == tblFaces) { // перетаскивание было из таблицы tblFaces - добавляем лица в "ручные"
                        listFacesDragged = tblFaces.getSelectionModel().getSelectedItems();
                        isNeedToAdd = true;
                        success = true;
                    } else if (event.getGestureSource() == tblFacesTagManual) { // перетаскивание было из таблицы tblFacesTagManual - добавляем лица в "ручные" для другого ? тега
                        listFacesDragged = tblFacesTagManual.getSelectionModel().getSelectedItems();
                        isNeedToAdd = true;
                        success = true;
                    } else if (event.getGestureSource() == tblFacesTagRecognized) { // перетаскивание было из таблицы tblFacesTagRecognized - добавляем лица в "ручные" для другого ? тега
                        listFacesDragged = tblFacesTagRecognized.getSelectionModel().getSelectedItems();
                        isNeedToAdd = true;
                        success = true;
                    } else if (event.getGestureSource() == tblNoFaces) { // перетаскивание было из таблицы tblFacesTagRecognized - добавляем лица в "ручные" для другого ? тега
                        listFacesDragged = tblNoFaces.getSelectionModel().getSelectedItems();
                        isNeedToAdd = true;
                        success = true;
                    } else if (event.getGestureSource() == tblFacesExtras) { // перетаскивание было из таблицы tblFacesTagRecognized - добавляем лица в "ручные" для другого ? тега
                        listFacesDragged = tblFacesExtras.getSelectionModel().getSelectedItems();
                        isNeedToAdd = true;
                        success = true;
                    } else if (event.getDragboard().getString().equals("shapePictureDrag")) {
                        if (currentFrameFullSizeFile != null) {
                            droppedTagFav.getIvfxTag().setPicture(currentFrameFullSizeFile);
                            showTagFavPage();
                        }
                    }

                    if (isNeedToAdd) {
                        System.out.println("Добавляем " + listFacesDragged.size() + " лиц для тэга " + droppedTagFav.getIvfxTag().getName());
                        addFacesToTag(listFacesDragged, droppedTagFav.getIvfxTag());
                    }

                    event.setDropCompleted(success);
                    event.consume();

                });

            }


        }
    }

    private static int[] getRowCol(int countRows, int countCols, int order, boolean isFillColumns) {
        int[] coord = {0,0};
        int row = 0, col = 0;

        if (isFillColumns) {
            col = ((order-1) / countRows) + 1;
            row = ((order-1) % countRows) + 1;
        } else {
            col = ((order-1) % countCols) + 1;
            row = ((order-1) / countCols) + 1;
        }

        col = col > countCols ? 0 : col;
        row = row > countRows ? 0 : row;

        coord[0] = row;
        coord[1] = col;

        return coord;
    }

    @FXML
    void onDragDroppedTagToAddFav(DragEvent event) {
        // Событие отпускания мыши при перетаскивании над лейблом "Добавить тэг в избранное"
        boolean success = false;
        if (event.getGestureSource() == tblTagsAll) {
            if (currentTagAll != null) {
                System.out.println("Добавляем в избранное тэг «" + currentTagAll.getName() + "»");
                IVFXTagsFavorites tagFav = IVFXTagsFavorites.getNewDbInstance(currentTagAll);
                showTagFavPage();
            }
            success = true;
        }

        event.setDropCompleted(success);
        event.consume();
    }

    @FXML
    void onDragDroppedTagToDelFav(DragEvent e) {
        // Событие отпускания мыши при перетаскивании над лейблом "Удалить тэг из избранного"

        boolean success = false;
        if (e.getDragboard().getString().equals("lblTagFav")) {

            if (draggedTagFav != null) {
                System.out.println("Remove «" + draggedTagFav.getIvfxTag().getName() + "» from favorites.");
                draggedTagFav.delete();
                showTagFavPage();
            }

        }

        e.setDropCompleted(success);
        e.consume();
    }

    @FXML
    void onDragOverTagToAddFav(DragEvent event) {
        // Событие перетаскивания мыши над лейблом "Добавить тэг в избранное"
        // Можно перетаскивать из таблицы tblTagsAll
        if (event.getGestureSource() == tblTagsAll) {
            event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
        }
        event.consume();
    }

    @FXML
    void onDragOverTagToDelFav(DragEvent event) {
        // Событие перетаскивания мыши над лейблом "Удалить тэг из избранного"
        // Можно перетаскивать из любого лейбла матрицы избранного
        if (event.getDragboard().getString().equals("lblTagFav")) {
            event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
        }
        event.consume();
    }


    @FXML
    void onShapePictureDragDetected(MouseEvent event) {
        // начинаем перетаскивать кружок на картинке
        Dragboard db = shapePictureDrag.startDragAndDrop(TransferMode.ANY);
        ClipboardContent content = new ClipboardContent();
        content.putString("shapePictureDrag");
        db.setContent(content);
        event.consume();
    }

    // скролл к текущей записи в таблице tblTagsAll
    private void tblTagsAllSmartScrollToCurrent() {
        if (flowTblTagsAll != null && flowTblTagsAll.getCellCount() > 0) {
            int first = flowTblTagsAll.getFirstVisibleCell().getIndex();
            int last = flowTblTagsAll.getLastVisibleCell().getIndex();
            int selected = tblTagsAll.getSelectionModel().getSelectedIndex();
            if (selected < first || selected > last) {
                tblTagsAll.scrollTo(selected);
            }
        }
    }

    // скролл к текущей записи в таблице tblFaces
    private void tblFacesSmartScrollToCurrent() {
        if (flowTblFaces != null && flowTblFaces.getCellCount() > 0) {
            int first = flowTblFaces.getFirstVisibleCell().getIndex();
            int last = flowTblFaces.getLastVisibleCell().getIndex();
            int selected = tblFaces.getSelectionModel().getSelectedIndex();
            if (selected < first || selected > last) {
                tblFaces.scrollTo(selected);
            }
        }
    }

    private void loadPicture(IVFXFaces face) {
        if (cbPictureLoadAuto.isSelected()) {

            String fileNamePreview = face.getIvfxFrame().getFileNamePreview();
            String fileNameFullSize = face.getIvfxFrame().getFileNameFullSize();

            File file = new File(fileNameFullSize);
            if (file.exists()) {

                currentFramePreviewFile = fileNamePreview;
                currentFrameFullSizeFile = fileNameFullSize;
                currentFrame = face.getIvfxFrame();

                try {
                    BufferedImage bufferedImage = ImageIO.read(file);
                    bufferedImage = OverlayImage.resizeImage(bufferedImage,720,400, Color.BLACK);

                    if (cbPictureShowFaces.isSelected()) {

                        List<IVFXFaces> listFacesForFrame = IVFXFaces.loadList(currentFrame);
                        for (IVFXFaces faceInFrame: listFacesForFrame) {
                            int frameWidth = bufferedImage.getWidth();
                            int frameHeight = bufferedImage.getHeight();
                            int faceSourceFrameWidth = 1920;
                            double scaleFactor = frameWidth / (double)faceSourceFrameWidth;

                            int startX = (int) (scaleFactor * faceInFrame.getStartX());
                            int startY = (int) (scaleFactor * faceInFrame.getStartY());
                            int endX = (int) (scaleFactor * faceInFrame.getEndX());
                            int endY = (int) (scaleFactor * faceInFrame.getEndY());

                            float opaque = 1.0f;

                            Color textColor = Color.YELLOW;
                            if (face.getId() == faceInFrame.getId()) {
                                textColor = Color.GREEN;
                            }

                            Font textFont = new Font(Font.SANS_SERIF,Font.PLAIN, 12);
                            int imageType = BufferedImage.TYPE_INT_ARGB;
                            Pos textPosition = Pos.BOTTOM_CENTER;

                            int tagId = faceInFrame.getTagId();
                            int tagRecId = faceInFrame.getTagRecognizedId();

                            String textToOverlay = "";

                            if (tagId == -2) textToOverlay = "(массовка)";
                            if (tagId == -1) textToOverlay = "(не лицо)";
                            if (tagId > 0) textToOverlay = IVFXTags.load(tagId,false).getName();
                            if (tagId == 0 && tagRecId != 0) textToOverlay = IVFXTags.load(tagRecId,false).getName() + " (" + String.format("%.02f",faceInFrame.getRecognizeProbability()*100) + "%)";

                            BufferedImage resultImage = new BufferedImage(frameWidth, frameHeight, imageType);
                            Graphics2D graphics2D = (Graphics2D) resultImage.getGraphics();
                            graphics2D.drawImage(bufferedImage,0,0,null);
                            AlphaComposite alphaChannel = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opaque);
                            graphics2D.setComposite(alphaChannel);

                            graphics2D.setFont(textFont);
                            FontMetrics fontMetrics = graphics2D.getFontMetrics();
                            Rectangle2D rect = fontMetrics.getStringBounds(textToOverlay, graphics2D);
                            int rectW = (int)rect.getWidth();
                            int rectH = (int)rect.getHeight();



                            int centerX=startX, centerY=startY;
                            if (centerY < 20) {
                                centerY = endY-startY+25;
                            } else {
                                centerY = centerY - 3;
                            }
                            if (centerY > frameHeight) centerY = frameHeight-5;

                            graphics2D.setColor(Color.BLACK);
                            graphics2D.fillRect(centerX-3, centerY-rectH, rectW+6, rectH+6);
                            graphics2D.setColor(textColor);

                            graphics2D.drawString(textToOverlay, centerX, centerY);
                            graphics2D.drawRect(startX, startY, endX-startX, endY-startY);
                            if (face.getId() == faceInFrame.getId()) {
                                graphics2D.drawRect(startX-1, startY-1, endX-startX+2, endY-startY+2);
                            }
                            graphics2D.dispose();

                            bufferedImage = resultImage;
                        }

                    }

                    ImageView imageView = new ImageView(ConvertToFxImage.convertToFxImage(bufferedImage));

                    Platform.runLater(() -> {
                        lblPictureFullFrame.setGraphic(imageView);
                    });

                } catch (IOException e) {
                }

            }


        }
    }

    @FXML
    void doBtnPictureCreateNewTag(ActionEvent event) {

        if (currentFrame != null) {
            IVFXEnumTagsTypes enumTagType = IVFXEnumTagsTypes.PERSON;
            String name = "New " + enumTagType;
            IVFXTags createdTag = CreateNewTagController.getNewTag(currentProject, enumTagType, name, currentFrame, false);

            if (createdTag != null) {
                int[] arr = {1,2};
                listTagsAll = FXCollections.observableArrayList(IVFXTags.loadList(currentProject,true, arr));
                tblTagsAll.setItems(listTagsAll);
                filteredTagsAll = new FilteredList<>(listTagsAll, e -> true);
            }
        }

    }

    // editFaces
    public void editFaces() {

        try {

            AnchorPane root = FXMLLoader.load(facesController.getClass().getResource("../resources/fxml/faces.fxml")); // в этот момент вызывается initialize()

            facesController.controllerScene = new Scene(root);
            facesController.controllerStage = new Stage();
            facesController.controllerStage.setTitle("FACES.");
            facesController.controllerStage.setScene(facesController.controllerScene);
            facesController.controllerStage.initModality(Modality.APPLICATION_MODAL);

            facesController.controllerStage.setOnCloseRequest(we -> {
                System.out.println("Закрытые окна редактора FACES.");
            });

            facesController.controllerStage.showAndWait();
            System.out.println("Завершение работы facesController");


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        Main.mainConnection = Database.getConnection();
        Main.mainWindow = primaryStage;

        AnchorPane root = FXMLLoader.load(facesController.getClass().getResource("../resources/fxml/faces.fxml")); // в этот момент вызывается initialize()

        facesController.controllerScene = new Scene(root);
        facesController.controllerStage = new Stage();
        facesController.controllerStage.setTitle("FACES.");
        facesController.controllerStage.setScene(facesController.controllerScene);
        facesController.controllerStage.initModality(Modality.APPLICATION_MODAL);

        facesController.controllerStage.setOnCloseRequest(we -> {
            System.out.println("Закрытые окна редактора FACES.");
        });

        facesController.controllerStage.showAndWait();
        System.out.println("Завершение работы facesController");

    }

    @FXML
    void initialize() {

        // Создаем слайдер
        if (vbBoxForSliderFaces.getChildren().size() == 0) {
            sliderFaces.setShowTickLabels(true);
            sliderFaces.setShowTickMarks(true);
            sliderFaces.setMajorTickUnit(0.1);
            sliderFaces.setMinorTickCount(3);
            sliderFaces.setBlockIncrement(0.05);
            sliderFaces.setSnapToTicks(true);
            vbBoxForSliderFaces.setPadding(new Insets(15));
            vbBoxForSliderFaces.getChildren().addAll(sliderFaces);
        }

        tblFaces.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        tblFacesTagManual.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        tblFacesTagRecognized.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        tblNoFaces.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        tblFacesExtras.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        fldFindTagsAll.setDisable(true);
        chFindTagsAllInProperties.setDisable(true);

        listProjects = FXCollections.observableArrayList(IVFXProjects.loadList());
        cbProject.setItems(listProjects);
        if (currentProject != null) {
            cbProject.getSelectionModel().select(currentProject);
        }

        /****************************
         * Настройка столбцов таблиц
         ****************************/

        // столбцы таблицы tblFaces
        colFaceLabel.setCellValueFactory(new PropertyValueFactory<>("label"));
        colFaceLabelPerson.setCellValueFactory(new PropertyValueFactory<>("labelPerson"));
        colFacePersonName.setCellValueFactory(new PropertyValueFactory<>("personName"));
        colFaceProba.setCellValueFactory(new PropertyValueFactory<>("recognizeProbability"));

        // столбцы таблицы tblTagsAll
        colTypeTblTagsAll.setCellValueFactory(new PropertyValueFactory<>("tagTypeLetter"));
        colNameTblTagsAll.setCellValueFactory(new PropertyValueFactory<>("label1"));

        // столбцы таблицы tblFacesTagManual
        colFaceTagManualLabel.setCellValueFactory(new PropertyValueFactory<>("label"));

        // столбцы таблицы tblFacesTagRecognized
        colFaceTagRecognizedLabel.setCellValueFactory(new PropertyValueFactory<>("label"));
        colFaceTagRecognizedProba.setCellValueFactory(new PropertyValueFactory<>("recognizeProbability"));

        // столбцы таблицы tblNoFaces
        colNoFaceLabel.setCellValueFactory(new PropertyValueFactory<>("label"));

        // столбцы таблицы tblFacesExtras
        colFaceExtrasLabel.setCellValueFactory(new PropertyValueFactory<>("label"));

        /*************************************
         * Скрытие заголовков столбцов таблиц
         *************************************/

//        // скрываем заголовок у таблицы tblFaces
//        tblFaces.widthProperty().addListener((source, oldWidth, newWidth) -> {
//            Pane header = (Pane) tblFaces.lookup("TableHeaderRow");
//            if (header.isVisible()) {
//                header.setMaxHeight(0);
//                header.setMinHeight(0);
//                header.setPrefHeight(0);
//                header.setVisible(false);
//            }
//        });

        // скрываем заголовок у таблицы tblTagsAll
        tblTagsAll.widthProperty().addListener((source, oldWidth, newWidth) -> {
            Pane header = (Pane) tblTagsAll.lookup("TableHeaderRow");
            if (header.isVisible()) {
                header.setMaxHeight(0);
                header.setMinHeight(0);
                header.setPrefHeight(0);
                header.setVisible(false);
            }
        });

        // скрываем заголовок у таблицы tblNoFaces
        tblNoFaces.widthProperty().addListener((source, oldWidth, newWidth) -> {
            Pane header = (Pane) tblNoFaces.lookup("TableHeaderRow");
            if (header.isVisible()) {
                header.setMaxHeight(0);
                header.setMinHeight(0);
                header.setPrefHeight(0);
                header.setVisible(false);
            }
        });

        // скрываем заголовок у таблицы tblFacesTagManual
        tblFacesTagManual.widthProperty().addListener((source, oldWidth, newWidth) -> {
            Pane header = (Pane) tblFacesTagManual.lookup("TableHeaderRow");
            if (header.isVisible()) {
                header.setMaxHeight(0);
                header.setMinHeight(0);
                header.setPrefHeight(0);
                header.setVisible(false);
            }
        });

/*        // скрываем заголовок у таблицы tblFacesTagRecognized
        tblFacesTagRecognized.widthProperty().addListener((source, oldWidth, newWidth) -> {
            Pane header = (Pane) tblFacesTagRecognized.lookup("TableHeaderRow");
            if (header.isVisible()) {
                header.setMaxHeight(0);
                header.setMinHeight(0);
                header.setPrefHeight(0);
                header.setVisible(false);
            }
        });*/

        // скрываем заголовок у таблицы tblNoFaces
        tblNoFaces.widthProperty().addListener((source, oldWidth, newWidth) -> {
            Pane header = (Pane) tblNoFaces.lookup("TableHeaderRow");
            if (header.isVisible()) {
                header.setMaxHeight(0);
                header.setMinHeight(0);
                header.setPrefHeight(0);
                header.setVisible(false);
            }
        });

        // скрываем заголовок у таблицы tblFacesExtras
        tblFacesExtras.widthProperty().addListener((source, oldWidth, newWidth) -> {
            Pane header = (Pane) tblFacesExtras.lookup("TableHeaderRow");
            if (header.isVisible()) {
                header.setMaxHeight(0);
                header.setMinHeight(0);
                header.setPrefHeight(0);
                header.setVisible(false);
            }
        });

        /*************************************************************************
         * Обработчики событий - События отслеживания видимости записей на экране
         *************************************************************************/

        // событие отслеживани видимости на экране текущей записи в таблице tblShots
        tblFaces.skinProperty().addListener((ChangeListener<Skin>) (ov, t, t1) -> {
            if (t1 == null) return;
            TableViewSkin tvs = (TableViewSkin) t1;
            ObservableList<Node> kids = tvs.getChildren();
            if (kids == null || kids.isEmpty()) return;
            flowTblFaces = (VirtualFlow) kids.get(1);
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
                    showTagFavPage();
                    doOnSelectRecordInCbProjects();
                }

                listFiles = FXCollections.observableArrayList(IVFXFiles.loadList(currentProject));
                cbFile.setItems(listFiles);

                int[] arr = {1, 2};
                listTagsAll = FXCollections.observableArrayList(IVFXTags.loadList(currentProject, true, arr));
                tblTagsAll.setItems(listTagsAll);
                filteredTagsAll = new FilteredList<>(listTagsAll, e -> true);

                fldFindTagsAll.setDisable(false);
                chFindTagsAllInProperties.setDisable(false);

            } else {
                fldFindTagsAll.setDisable(true);
                chFindTagsAllInProperties.setDisable(true);
            }
        });


        // событие выбора записи в комбобоксе cbFile
        cbFile.getSelectionModel().selectedItemProperty().addListener((v, oldValue, newValue) -> {
            if (newValue != null) {
                currentFile = newValue;
                doOnSelectRecordInCbFile();
            }
        });

        // событие выбора записи в таблице tblTagsAll
        tblTagsAll.getSelectionModel().selectedItemProperty().addListener((v, oldValue, newValue) -> {
            if (newValue != null) {

                currentTagAll = newValue;  // устанавливаем выбранное значение в таблице как текущий план

//                listFacesTagManual = FXCollections.observableArrayList(IVFXFaces.loadList(currentTagAll, currentProject, currentFile, true, true, false, 0));
//                tblFacesTagManual.setItems(listFacesTagManual);
//
//                listFacesTagRecognized = FXCollections.observableArrayList(IVFXFaces.loadList(currentTagAll, currentProject, currentFile, true, false, true, minProba));
//                tblFacesTagRecognized.setItems(listFacesTagRecognized);

            }
        });

        // событие выбора записи в таблице tblFaces
        tblFaces.getSelectionModel().selectedItemProperty().addListener((v, oldValue, newValue) -> {
            if (newValue != null) {
                loadPicture(newValue);
//                int w = newValue.getEndX()-newValue.getStartX();
//                int h = newValue.getEndY()-newValue.getStartY();
//                double aspect = w/(double)h;
//                if (h > w ) aspect = h/(double)w;
//                System.out.println("x = " + newValue.getStartX() + ":" + newValue.getEndX() +  ", y = " + newValue.getStartY() + ":" + newValue.getEndY() + ", w = " + w + ", h = " + h + " a = " +aspect);
            }
        });

        // событие выбора записи в таблице tblFacesTagManual
        tblFacesTagManual.getSelectionModel().selectedItemProperty().addListener((v, oldValue, newValue) -> {
            if (newValue != null) {
                loadPicture(newValue);
            }
        });

        // событие выбора записи в таблице tblFacesTagRecognized
        tblFacesTagRecognized.getSelectionModel().selectedItemProperty().addListener((v, oldValue, newValue) -> {
            if (newValue != null) {
                loadPicture(newValue);
            }
        });

        // событие выбора записи в таблице tblNoFaces
        tblNoFaces.getSelectionModel().selectedItemProperty().addListener((v, oldValue, newValue) -> {
            if (newValue != null) {
                loadPicture(newValue);
            }
        });

        // событие выбора записи в таблице tblFacesExtras
        tblFacesExtras.getSelectionModel().selectedItemProperty().addListener((v, oldValue, newValue) -> {
            if (newValue != null) {
                loadPicture(newValue);
            }
        });

        /****************************************
         * Обработчики событий - Прочие события
         ****************************************/


        // обработка события отпускания кнопки в поле поиска fldFindTagsAll
        fldFindTagsAll.setOnKeyReleased(e -> {
            fldFindTagsAll.textProperty().addListener((v, oldValue, newValue) -> {
                filteredTagsAll.setPredicate((Predicate<? super IVFXTags>) ivfxTags -> {
                    if (newValue == null || newValue.isEmpty()) {
                        return true;
                    }
                    String lowerCaseFilter = newValue.toLowerCase();
                    if (!chFindTagsAllInProperties.isSelected() && ivfxTags.getName().toLowerCase().contains(lowerCaseFilter)) {
                        return true;
                    }
                    if (chFindTagsAllInProperties.isSelected() && ivfxTags.getPropertiesValues().toLowerCase().contains(lowerCaseFilter)) {
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
            if (ke.getCode().equals(KeyCode.ENTER)) {
                tblTagsAll.requestFocus();
                tblTagsAll.getSelectionModel().select(0);
                tblTagsAll.scrollTo(0);
            }
        });

        tblTagsAll.setRowFactory(tableView -> {
            final TableRow<IVFXTags> row = new TableRow<>();
            row.hoverProperty().addListener((observable) -> {
                final IVFXTags tag = row.getItem();
                if (row.isHover() && tag != null) {
                    currentTagAllHovered = tag;
                    if (isNeedToAddFacesDraggedToTag) {
                        isNeedToAddFacesDraggedToTag = false;
                        System.out.println("Добавляем " + listFacesDragged.size() + " лиц для тэга " + currentTagAllHovered.getName());
                        addFacesToTag(listFacesDragged, currentTagAllHovered);
                    }

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

                } else {
                    currentTagAllHovered = null;
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
//        if (currentProject != null) {
//
//            listFaces = FXCollections.observableArrayList(IVFXFaces.loadList(currentProject, true, false, true, false, false));
//            tblFaces.setItems(listFaces);
//
//            listNoFaces = FXCollections.observableArrayList(IVFXFaces.loadList(currentProject, true, false, false, true, false));
//            tblNoFaces.setItems(listNoFaces);
//
//            listFacesExtras = FXCollections.observableArrayList(IVFXFaces.loadList(currentProject, true, false, false, false, true));
//            tblFacesExtras.setItems(listFacesExtras);
//
//            listFiles = FXCollections.observableArrayList(IVFXFiles.loadList(currentProject));
//            cbFile.setItems(listFiles);
//
//        }

    }

    // Действие при выборе записи в комбобоксе cbFile
    private void doOnSelectRecordInCbFile() {

//        if (currentFile != null) {
//
//            listFaces = FXCollections.observableArrayList(IVFXFaces.loadList(currentFile, true, false, true, false, false));
//            tblFaces.setItems(listFaces);
//
//            listNoFaces = FXCollections.observableArrayList(IVFXFaces.loadList(currentFile, true, false, false, true, false));
//            tblNoFaces.setItems(listNoFaces);
//
//            listFacesExtras = FXCollections.observableArrayList(IVFXFaces.loadList(currentFile, true, false, false, false, true));
//            tblFacesExtras.setItems(listFacesExtras);
//
//        }

    }


    private void addFacesToTag(List<IVFXFaces> list, IVFXTags tag) {
        if (list != null && list.size() > 0 && tag != null) {
            for (IVFXFaces face : list) {
                if (face.getTagId() != tag.getId()) {
                    face.setTagId(tag.getId());
                    face.setTagRecognizedId(tag.getId());
                    face.setRecognizeProbability(1.0);
                    face.save();
                }
            }
            smartRemoveRecordsFromListFaces(list);

            listFacesTagRecognized.removeAll(list);
            tblFacesTagRecognized.setItems(listFacesTagRecognized);

            listFacesExtras.removeAll(list);
            tblFacesExtras.setItems(listFacesExtras);

            if (currentTagAllHovered != null && currentTagAll != null) {
                listFacesTagManual = FXCollections.observableArrayList(IVFXFaces.loadList(currentTagAll, currentProject, currentFile, true, true, false, 0));
                tblFacesTagManual.setItems(listFacesTagManual);
            }
        }
    }

    private void removeFacesFromTag(List<IVFXFaces> list) {
        if (list != null && list.size() > 0) {
            for (IVFXFaces face : list) {
                if (face.getTagId() != 0) {
                    face.setTagId(0);
                    face.setTagRecognizedId(0);
                    face.setRecognizeProbability(0.0);
                    face.save();
                }
            }
            smartRemoveRecordsFromListFaces(list);
            if (currentTagAll != null) {
                listFacesTagManual = FXCollections.observableArrayList(IVFXFaces.loadList(currentTagAll, currentProject, currentFile,true, true, false, 0));
                tblFacesTagManual.setItems(listFacesTagManual);
            }
        }
    }

    private void addFacesToNoFace(List<IVFXFaces> list) {
        if (list != null && list.size() > 0) {
            for (IVFXFaces face : list) {
                if (face.getTagId() != -1) {
                    face.setTagId(-1);
                    face.setTagRecognizedId(0);
                    face.setRecognizeProbability(1.0);
                    face.save();
                }
            }
            listNoFaces.addAll(list);
            tblNoFaces.setItems(listNoFaces);
            smartRemoveRecordsFromListFaces(list);
            listFacesTagManual.removeAll(list);
            listFacesTagRecognized.removeAll(list);
            listFacesExtras.removeAll(list);
            tblFacesTagManual.setItems(listFacesTagManual);
            tblFacesTagRecognized.setItems(listFacesTagRecognized);
            tblFacesExtras.setItems(listFacesExtras);


        }
    }

    private void addFacesToExtras(List<IVFXFaces> list) {
        if (list != null && list.size() > 0) {
            for (IVFXFaces face : list) {
                if (face.getTagId() != -2) {
                    face.setTagId(-2);
                    face.setTagRecognizedId(0);
                    face.setRecognizeProbability(1.0);
                    face.save();
                }
            }
            listFacesExtras.addAll(list);
            tblFacesExtras.setItems(listFacesExtras);
            smartRemoveRecordsFromListFaces(list);
            listFacesTagManual.removeAll(list);
            listFacesTagRecognized.removeAll(list);
            listNoFaces.removeAll(list);
            tblFacesTagManual.setItems(listFacesTagManual);
            tblFacesTagRecognized.setItems(listFacesTagRecognized);
            tblNoFaces.setItems(listNoFaces);

        }
    }

    private void smartRemoveRecordsFromListFaces(List<IVFXFaces> listToRemove) {
        List<IVFXFaces> listFromRemove = listFaces;
        TableView tbl = tblFaces;
        IVFXFaces firstItemInListRemove = listToRemove.get(0);
        int indexInList = listFromRemove.indexOf(firstItemInListRemove) - 1;
        for (int i = 0; i < listFromRemove.size(); i++) {
            IVFXFaces face = listFromRemove.get(i);
            if (face.getId() == firstItemInListRemove.getId()) {
                indexInList = i-1;
                break;
            }
        }
        listFromRemove.removeAll(listToRemove);
        tbl.setItems((ObservableList) listFromRemove);
        if (indexInList >= 0) {
            tbl.getSelectionModel().clearSelection();
//            tbl.getSelectionModel().select(indexInList);
//            tblFacesSmartScrollToCurrent();

            if (flowTblFaces != null && flowTblFaces.getCellCount() > 0) {
                int first = flowTblFaces.getFirstVisibleCell().getIndex();
                int last = flowTblFaces.getLastVisibleCell().getIndex();
                if (indexInList < first || indexInList > last) {
                    tblFaces.scrollTo(indexInList);
                }
            }

        }
    }

    @FXML
    void doFacesOnDragDetected(MouseEvent event) {
        // начинаем перетаскивать записи из таблицы tblFaces
        Dragboard db = tblFaces.startDragAndDrop(TransferMode.ANY);
        ClipboardContent content = new ClipboardContent();
        content.putString("tblFaces");
        db.setContent(content);
        event.consume();
    }

    @FXML
    void doTagsAllOnDragDetected(MouseEvent event) {
        // начинаем перетаскивать записи из таблицы tblTagsAll
        Dragboard db = tblTagsAll.startDragAndDrop(TransferMode.ANY);
        ClipboardContent content = new ClipboardContent();
        content.putString("tblTagsAll");
        db.setContent(content);
        event.consume();
    }

    @FXML
    void doFacesTagManualOnDragDetected(MouseEvent event) {
        // начинаем перетаскивать записи из таблицы tblFacesTagManual
        Dragboard db = tblFacesTagManual.startDragAndDrop(TransferMode.ANY);
        ClipboardContent content = new ClipboardContent();
        content.putString("tblFacesTagManual");
        db.setContent(content);
        event.consume();
    }

    @FXML
    void doFacesTagRecognizedOnDragDetected(MouseEvent event) {
        // начинаем перетаскивать записи из таблицы tblFacesTagRecognized
        Dragboard db = tblFacesTagRecognized.startDragAndDrop(TransferMode.ANY);
        ClipboardContent content = new ClipboardContent();
        content.putString("tblFacesTagRecognized");
        db.setContent(content);
        event.consume();
    }

    @FXML
    void doNoFacesOnDragDetected(MouseEvent event) {
        // начинаем перетаскивать записи из таблицы tblNoFaces
        Dragboard db = tblNoFaces.startDragAndDrop(TransferMode.ANY);
        ClipboardContent content = new ClipboardContent();
        content.putString("tblNoFaces");
        db.setContent(content);
        event.consume();
    }

    @FXML
    void doFacesExtrasOnDragDetected(MouseEvent event) {
        // начинаем перетаскивать записи из таблицы tblFacesExtras
        Dragboard db = tblFacesExtras.startDragAndDrop(TransferMode.ANY);
        ClipboardContent content = new ClipboardContent();
        content.putString("tblFacesExtras");
        db.setContent(content);
        event.consume();
    }

    @FXML
    void doFacesOnDragDropped(DragEvent event) {
        // отпускаем перетаскивание над таблицей tblFaces
        boolean success = false;
        if (event.getGestureSource() == tblFacesTagManual) { // перетаскивание было из таблицы tblFacesTagManual - удаляем лица из "ручных"
            listFacesDragged = tblFacesTagManual.getSelectionModel().getSelectedItems();
            removeFacesFromTag(listFacesDragged);
            success = true;
        } else if (event.getGestureSource() == tblFacesExtras) { // перетаскивание было из таблицы tblFacesTagManual - удаляем лица из "ручных"
            listFacesDragged = tblFacesExtras.getSelectionModel().getSelectedItems();
            removeFacesFromTag(listFacesDragged);
            success = true;
        }

        event.setDropCompleted(success);
        event.consume();
    }

    @FXML
    void doFacesTagManualOnDragDropped(DragEvent event) {
        // отпускаем перетаскивание над таблицей tblFacesTagManual
        boolean success = false;
        if (event.getGestureSource() == tblFaces) { // перетаскивание было из таблицы tblFaces - добавляем лица в "ручные"
            listFacesDragged = tblFaces.getSelectionModel().getSelectedItems();
            if (currentTagAll != null) {
                addFacesToTag(listFacesDragged, currentTagAll);
            }
            success = true;
        } else if (event.getGestureSource() == tblFacesTagRecognized) { // перетаскивание было из таблицы tblFacesTagRecognized - добавляем лица в "ручные"
            listFacesDragged = tblFacesTagRecognized.getSelectionModel().getSelectedItems();
            if (currentTagAll != null) {
                addFacesToTag(listFacesDragged, currentTagAll);
            }
            success = true;
        } else if (event.getGestureSource() == tblFacesExtras) { // перетаскивание было из таблицы tblFacesTagRecognized - добавляем лица в "ручные"
            listFacesDragged = tblFacesExtras.getSelectionModel().getSelectedItems();
            if (currentTagAll != null) {
                addFacesToTag(listFacesDragged, currentTagAll);
            }
            success = true;
        }

        event.setDropCompleted(success);
        event.consume();
    }

    @FXML
    void doTagsAllOnDragDropped(DragEvent event) {
        // отпускаем перетаскивание над таблицей tblTagsAll
        boolean success = false;
        if (event.getGestureSource() == tblFaces) { // перетаскивание было из таблицы tblFaces - добавляем лица в "ручные"
            listFacesDragged = tblFaces.getSelectionModel().getSelectedItems();
            isNeedToAddFacesDraggedToTag = true;
            success = true;
        } else if (event.getGestureSource() == tblFacesTagManual) { // перетаскивание было из таблицы tblFacesTagManual - добавляем лица в "ручные" для другого ? тега
            listFacesDragged = tblFacesTagManual.getSelectionModel().getSelectedItems();
            isNeedToAddFacesDraggedToTag = true;
            success = true;
        } else if (event.getGestureSource() == tblFacesTagRecognized) { // перетаскивание было из таблицы tblFacesTagRecognized - добавляем лица в "ручные" для другого ? тега
            listFacesDragged = tblFacesTagRecognized.getSelectionModel().getSelectedItems();
            isNeedToAddFacesDraggedToTag = true;
            success = true;
        } else if (event.getGestureSource() == tblNoFaces) { // перетаскивание было из таблицы tblFacesTagRecognized - добавляем лица в "ручные" для другого ? тега
            listFacesDragged = tblNoFaces.getSelectionModel().getSelectedItems();
            isNeedToAddFacesDraggedToTag = true;
            success = true;
        } else if (event.getGestureSource() == tblFacesExtras) { // перетаскивание было из таблицы tblFacesTagRecognized - добавляем лица в "ручные" для другого ? тега
            listFacesDragged = tblFacesExtras.getSelectionModel().getSelectedItems();
            isNeedToAddFacesDraggedToTag = true;
            success = true;
        }

        event.setDropCompleted(success);
        event.consume();
    }

    @FXML
    void doNoFacesOnDragDropped(DragEvent event) {

        // отпускаем перетаскивание над таблицей tblFacesTagManual
        boolean success = false;
        if (event.getGestureSource() == tblFaces) { // перетаскивание было из таблицы tblFaces - добавляем лица в "ручные"
            listFacesDragged = tblFaces.getSelectionModel().getSelectedItems();
            addFacesToNoFace(listFacesDragged);
            success = true;
        } else if (event.getGestureSource() == tblFacesTagManual) { // перетаскивание было из таблицы tblFacesTagRecognized - добавляем лица в "ручные"
            listFacesDragged = tblFacesTagManual.getSelectionModel().getSelectedItems();
            addFacesToNoFace(listFacesDragged);
            success = true;
        } else if (event.getGestureSource() == tblFacesTagRecognized) { // перетаскивание было из таблицы tblFacesTagRecognized - добавляем лица в "ручные"
            listFacesDragged = tblFacesTagRecognized.getSelectionModel().getSelectedItems();
            addFacesToNoFace(listFacesDragged);
            success = true;
        } else if (event.getGestureSource() == tblFacesExtras) { // перетаскивание было из таблицы tblFacesTagRecognized - добавляем лица в "ручные"
            listFacesDragged = tblFacesExtras.getSelectionModel().getSelectedItems();
            addFacesToNoFace(listFacesDragged);
            success = true;
        }

        event.setDropCompleted(success);
        event.consume();

    }

    @FXML
    void doFacesExtrasOnDragDropped(DragEvent event) {

        // отпускаем перетаскивание над таблицей tblFacesExtras
        boolean success = false;
        if (event.getGestureSource() == tblFaces) { // перетаскивание было из таблицы tblFaces - добавляем лица в "ручные"
            listFacesDragged = tblFaces.getSelectionModel().getSelectedItems();
            addFacesToExtras(listFacesDragged);
            success = true;
        } else if (event.getGestureSource() == tblFacesTagManual) { // перетаскивание было из таблицы tblFacesTagRecognized - добавляем лица в "ручные"
            listFacesDragged = tblFacesTagManual.getSelectionModel().getSelectedItems();
            addFacesToExtras(listFacesDragged);
            success = true;
        } else if (event.getGestureSource() == tblFacesTagRecognized) { // перетаскивание было из таблицы tblFacesTagRecognized - добавляем лица в "ручные"
            listFacesDragged = tblFacesTagRecognized.getSelectionModel().getSelectedItems();
            addFacesToExtras(listFacesDragged);
            success = true;
        }

        event.setDropCompleted(success);
        event.consume();

    }


    @FXML
    void doFacesOnDragOver(DragEvent event) {
        // перетаскиваем мышь над таблицей tblFaces
        if (event.getGestureSource() == tblFacesTagManual || event.getGestureSource() == tblFacesTagRecognized || event.getGestureSource() == tblNoFaces || event.getGestureSource() == tblFacesExtras) {
            event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
        }
        event.consume();
    }

    @FXML
    void doFacesTagManualOnDragOver(DragEvent event) {
        // перетаскиваем мышь над таблицей tblFacesTagManual
        if (event.getGestureSource() == tblFaces || event.getGestureSource() == tblFacesTagRecognized || event.getGestureSource() == tblNoFaces || event.getGestureSource() == tblFacesExtras) {
            event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
        }
        event.consume();
    }

    @FXML
    void doTagsAllOnDragOver(DragEvent event) {
        // перетаскиваем мышь над таблицей tblTagsAll
        if (event.getGestureSource() == tblFaces || event.getGestureSource() == tblFacesTagManual || event.getGestureSource() == tblFacesTagRecognized || event.getGestureSource() == tblFacesExtras) {
            event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
        }
        event.consume();
    }

    @FXML
    void doNoFacesOnDragOver(DragEvent event) {
        // перетаскиваем мышь над таблицей tblNoFaces
        if (event.getGestureSource() == tblFaces || event.getGestureSource() == tblFacesTagManual || event.getGestureSource() == tblFacesTagRecognized || event.getGestureSource() == tblFacesExtras) {
            event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
        }
        event.consume();
    }

    @FXML
    void doFacesExtrasOnDragOver(DragEvent event) {
        // перетаскиваем мышь над таблицей tblFacesExtras
        if (event.getGestureSource() == tblFaces || event.getGestureSource() == tblFacesTagManual || event.getGestureSource() == tblFacesTagRecognized || event.getGestureSource() == tblNoFaces) {
            event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
        }
        event.consume();
    }




    @FXML
    void doBtnExtractAndTrain(ActionEvent event) {

        if (currentProject != null) {

            String cmdFileName = currentProject.getFolder() + "\\face_train.cmd";

            String pathToFileJsonEmbeddings = currentProject.getFolder() + "\\embeddings.json";
            List<IVFXFaces> listOfManualRecognizedFaces = IVFXFaces.loadList(currentProject,false,true,false,false,false,0,1);

            System.out.println("Количество вручную распознанных лиц: " + listOfManualRecognizedFaces.size());

            Embeddings embeddings = new Embeddings();
            embeddings.tags = new String[listOfManualRecognizedFaces.size()];
            embeddings.vectors = new double[listOfManualRecognizedFaces.size()][];

            for (int i = 0; i < listOfManualRecognizedFaces.size(); i++) {
                IVFXFaces face = listOfManualRecognizedFaces.get(i);
                String tag = String.valueOf(face.getTagId());
                double[] vector = face.getVector();
                embeddings.vectors[i] = vector;
                embeddings.tags[i] = tag;
            }

            GsonBuilder builder = new GsonBuilder();
            Gson gson = builder.create();

            // сохраняем список "вручную распознанных" лиц в json
            try (final FileWriter fileWriter = new FileWriter(pathToFileJsonEmbeddings)) {
                gson.toJson(embeddings, fileWriter);
            } catch (IOException e) {
                e.printStackTrace();
            }

            List<String> param;
            List<String> cmd = new ArrayList<>();
            String line;

            param = new ArrayList<String>();

            cmd.add("d:" + "\n");
            cmd.add("cd " + "\"" + Main.PATH_TO_FOLDER_OPENCVPROJECT + "\"" + "\n");

            cmd.add("\n\n");
            cmd.add("REM =======================================================" + "\n");
            cmd.add("REM Train model " + "\n");
            cmd.add("REM =======================================================" + "\n\n");

            param.add("py");
            param.add("\"" + Main.PATH_TO_FOLDER_OPENCVPROJECT + "\\train_model_json.py" + "\"");
            param.add("-e");
            param.add("\"" + pathToFileJsonEmbeddings + "\"");
            param.add("-r");
            param.add("\"" + currentProject.getFolder() + "\\recognizer.pickle" + "\"");
            param.add("-l");
            param.add("\"" + currentProject.getFolder() + "\\le.pickle" + "\"");

            line = "";
            for (String parameter : param) {
                line = line + parameter + " ";
            }
            line = line.substring(0, line.length() - 1) + "\n";
            cmd.add(line);

            String text = "";
            for (String parameter : cmd) {
                text = text + parameter;
            }

            try {
                File cmdFile = new File(cmdFileName);
                BufferedWriter writer = new BufferedWriter(new FileWriter(cmdFile));
                writer.write(text);
                writer.flush();
                writer.close();
                Process process = Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + cmdFileName);
//                process.waitFor();
//                System.out.println("Train model done");

            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }

    @FXML
    void doBtnExportAndRecognize(ActionEvent event) {

        if (currentProject != null) {

            String cmdFileName = currentProject.getFolder() + "\\face_recognize.cmd";
            String pathToFileJsonFaces = currentProject.getFolder() + "\\faces.json";

            GsonBuilder builder = new GsonBuilder();
            Gson gson = builder.create();
            List<IVFXFaces> listOfAllFaces;

            if (currentFile == null) {
                listOfAllFaces = IVFXFaces.loadList(currentProject,false,false,true,false,false,0,1);
            } else {
                listOfAllFaces = IVFXFaces.loadList(currentFile,false,false,true,false,false,0,1);
            }

            FrameFace[] arrFrameFaces = new FrameFace[listOfAllFaces.size()];
            for (int i = 0; i < listOfAllFaces.size(); i++) {
                IVFXFaces face = listOfAllFaces.get(i);
                arrFrameFaces[i] = face.getFrameFace();
            }

            // сохраняем список всех лиц для распознания в json
            try (final FileWriter fileWriter = new FileWriter(pathToFileJsonFaces)) {
                gson.toJson(arrFrameFaces, fileWriter);
            } catch (IOException e) {
                e.printStackTrace();
            }

            List<String> param;
            List<String> cmd = new ArrayList<>();
            String line;

            param = new ArrayList<String>();

            cmd.add("d:" + "\n");
            cmd.add("cd " + "\"" + Main.PATH_TO_FOLDER_OPENCVPROJECT + "\"" + "\n");

            cmd.add("\n\n");
            cmd.add("REM =======================================================" + "\n");
            cmd.add("REM Recognize faces " + "\n");
            cmd.add("REM =======================================================" + "\n\n");

            param.add("py");
            param.add("\"" + Main.PATH_TO_FOLDER_OPENCVPROJECT + "\\recognize_faces.py" + "\"");
            param.add("-i");
            param.add("\"" + currentProject.getFolder() + "\\faces.json" + "\"");
            param.add("-d");
            param.add("\"" + Main.PATH_TO_FOLDER_OPENCVPROJECT + "\\face_detection_model" + "\"");
            param.add("-m");
            param.add("\"" + Main.PATH_TO_FOLDER_OPENCVPROJECT + "\\openface_nn4.small2.v1.t7" + "\"");
            param.add("-r");
            param.add("\"" + currentProject.getFolder() + "\\recognizer.pickle" + "\"");
            param.add("-l");
            param.add("\"" + currentProject.getFolder() + "\\le.pickle" + "\"");
            param.add("-c");
            param.add(String.valueOf(0.3));

            line = "";
            for (String parameter : param) {
                line = line + parameter + " ";
            }
            line = line.substring(0, line.length() - 1) + "\n";
            cmd.add(line);

            cmd.add("\n\n");

            String text = "";
            for (String parameter : cmd) {
                text = text + parameter;
            }

            try {
                File cmdFile = new File(cmdFileName);
                BufferedWriter writer = new BufferedWriter(new FileWriter(cmdFile));
                writer.write(text);
                writer.flush();
                writer.close();
                Process process = Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + cmdFileName);
//                process.waitFor();
//                System.out.println("Recognize faces done");

            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }


    @FXML
    void doBtnReloadFaces(ActionEvent event) {

        if (currentProject != null) {

            String pathToFileJSON = currentProject.getFolder() + "\\faces.json";
            FaceRecognizer.reloadFaces(pathToFileJSON);
            initialize();
            doOnSelectRecordInCbProjects();
        }

    }


    @FXML
    void doBtlLoadFacesAndTags(ActionEvent event) {

        if (currentProject != null) {

            if (currentFile != null) {

                listFaces = FXCollections.observableArrayList(IVFXFaces.loadList(currentFile, true, cbLoadOnlyManualFaces.isSelected(), true, true, true, sliderFaces.getLowValue(),sliderFaces.getHighValue()));
                tblFaces.setItems(listFaces);

//                listNoFaces = FXCollections.observableArrayList(IVFXFaces.loadList(currentFile, true, false, false, true, false, 0,1));
//                tblNoFaces.setItems(listNoFaces);
//
//                listFacesExtras = FXCollections.observableArrayList(IVFXFaces.loadList(currentFile, true, false, false, false, true, 0, 1));
//                tblFacesExtras.setItems(listFacesExtras);

            } else {

                listFaces = FXCollections.observableArrayList(IVFXFaces.loadList(currentProject, true, cbLoadOnlyManualFaces.isSelected(), true, true, true,sliderFaces.getLowValue(),sliderFaces.getHighValue()));
                tblFaces.setItems(listFaces);

//                listNoFaces = FXCollections.observableArrayList(IVFXFaces.loadList(currentProject, true, false, false, true, false,0,1));
//                tblNoFaces.setItems(listNoFaces);
//
//                listFacesExtras = FXCollections.observableArrayList(IVFXFaces.loadList(currentProject, true, false, false, false, true,0,1));
//                tblFacesExtras.setItems(listFacesExtras);

                listFiles = FXCollections.observableArrayList(IVFXFiles.loadList(currentProject));
                cbFile.setItems(listFiles);

            }

        }



    }

    @FXML
    void doBtnLoadFacesManual(ActionEvent event) {

        if (currentTagAll != null) {
            listFacesTagManual = FXCollections.observableArrayList(IVFXFaces.loadList(currentTagAll, currentProject, currentFile, true, true, false, 0));
            tblFacesTagManual.setItems(listFacesTagManual);
        }

    }

    @FXML
    void doBtnLoadFacesRecognized(ActionEvent event) {

        if (currentTagAll != null) {
            listFacesTagRecognized = FXCollections.observableArrayList(IVFXFaces.loadList(currentTagAll, currentProject, currentFile, true, false, true, minProba));
            tblFacesTagRecognized.setItems(listFacesTagRecognized);
        }

    }

    @FXML
    void doBtnClearNonFavoriteFaces(ActionEvent event) {

        List<IVFXTagsFavorites> listTagsFav = IVFXTagsFavorites.loadList();
        for (IVFXFaces face : listFaces) {
            int w = face.getEndX()-face.getStartX();
            int h = face.getEndY()-face.getStartY();
            double aspect = w/(double)h;
            if (h > w ) aspect = h/(double)w;
            if (aspect > 4) {
                System.out.println(aspect);
            }

            if (aspect > 4) {
                System.out.println(aspect);
                face.setRecognizeProbability(1);
                face.setTagId(-1);
                face.setTagRecognizedId(-1);
                face.save();
            } else {
                boolean isInFav = false;
                for (IVFXTagsFavorites tagFav : listTagsFav) {
                    if (face.getRecognizeProbability() !=1 && face.getTagRecognizedId() == tagFav.getTagId()) {
                        isInFav = true;
                        break;
                    }
                }
                if (!isInFav) {
                    face.setRecognizeProbability(0);
                    face.setTagRecognizedId(0);
                    face.setLabelPerson(null);
                    face.save();
                }
            }
        }
        tblFaces.setItems(listFaces);
    }


    @FXML
    void doBtnClearNonHTMLFaces(ActionEvent event) {

        if (currentFile != null) {
            List<IVFXTags> listTagsHTML = currentFile.getListTagsFromHTML();

            for (IVFXFaces face : listFaces) {
                int w = face.getEndX()-face.getStartX();
                int h = face.getEndY()-face.getStartY();
                double aspect = w/(double)h;
                if (h > w ) aspect = h/(double)w;
                if (aspect > 4) {
                    System.out.println(aspect);
                }

                if (aspect > 4) {
                    System.out.println(aspect);
                    face.setRecognizeProbability(1);
                    face.setTagId(-1);
                    face.setTagRecognizedId(-1);
                    face.save();
                } else {
                    boolean isInHTML = false;
                    for (IVFXTags tagFromHTML : listTagsHTML) {
                        if (face.getRecognizeProbability() !=1 && face.getTagRecognizedId() == tagFromHTML.getId()) {
                            isInHTML = true;
                            break;
                        }
                    }
                    if (!isInHTML) {
                        face.setRecognizeProbability(0);
                        face.setTagRecognizedId(0);
                        face.setLabelPerson(null);
                        face.save();
                    }
                }
            }
            tblFaces.setItems(listFaces);

            List<IVFXTagsFavorites> listTagsFav = IVFXTagsFavorites.loadList();
            for (IVFXTags tagFromHTML : listTagsHTML) {
                boolean isFinded = false;
                for (IVFXTagsFavorites tagFav : listTagsFav) {
                    if (tagFromHTML.getId() == tagFav.getIvfxTag().getId()) {
                        isFinded = true;
                        break;
                    }
                }
                if (!isFinded) {
                    IVFXTagsFavorites.getNewDbInstance(tagFromHTML);
                }
            }
            showTagFavPage();
        }

    }
}
package com.svoemesto.ivfx.controllers;

import com.sun.javafx.scene.control.skin.TableViewSkin;
import com.sun.javafx.scene.control.skin.VirtualFlow;
import com.svoemesto.ivfx.tables.*;
import com.svoemesto.ivfx.utils.ConvertToFxImage;
import com.svoemesto.ivfx.utils.FFmpeg;
import com.svoemesto.ivfx.utils.OverlayImage;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import javafx.scene.layout.Pane;
import javafx.util.StringConverter;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FramesController {

    @FXML
    private TableView<MatrixPages> tblPages;

    @FXML
    private TableColumn<MatrixPages, String> colDurationStart;

    @FXML
    private TableColumn<MatrixPages, String> colDurationEnd;

    @FXML
    private TableColumn<MatrixPages, Integer> colFrameStart;

    @FXML
    private TableColumn<MatrixPages, Integer> colFrameEnd;

    @FXML
    private Label ctlFullFrame;

    @FXML
    private Button btnOK;

    @FXML
    private Pane ctlCenterPane;

    @FXML
    private Slider ctlSlider;

    @FXML
    private ProgressBar ctlProgressBar;

    @FXML
    private ContextMenu contxtMenuFrame;

    private static IVFXFiles currentFile;
    private static int initFrameNumber;
    private static FramesController framesController = new FramesController();

    private Stage controllerStage;
    private Scene currentScene;
    private int pictW = 135;  // ширина картинки
    private int pictH = 75;   // высота картинки
    private String fxBorderDefault = "-fx-border-color:#0f0f0f;-fx-border-width:1";  // стиль бордюра лейбла по-умолчаснию
    private String fxBorderFocused = "-fx-border-color:YELLOW;-fx-border-width:1";  // стиль бордюра лейбла по-умолчаснию
    private String fxBorderSelected = "-fx-border-color:RED;-fx-border-width:1";  // стиль бордюра лейбла по-умолчаснию

    private final static int COUNT_LOADED_PAGE_BEFORE_CURRENT = 50;
    private final static int COUNT_LOADED_PAGE_AFTER_CURRENT = 100;

    private int countColumnsInPage;
    private int countRowsInPage;
    private MatrixPages currentPage;
    private MatrixFrames currentFrame;
    private int currentNumPage;
    private static boolean isWorking;
    private static boolean isPressedControl;
    private static boolean isPlayingForward;
    public static SimpleBooleanProperty isPressedPlayForward = new SimpleBooleanProperty();
    public static SimpleBooleanProperty isPressedPlayBackward = new SimpleBooleanProperty();
    private volatile int countLoadedPages;
    private volatile List<IVFXFrames> listFrames = new ArrayList<>();
    private volatile  List<IVFXSegments> listSegments = new ArrayList<>();

    private ObservableList<MatrixPages> listPages = FXCollections.observableArrayList();
    private VirtualFlow flowTblPages;

    private FramesImageLoader fil = new FramesImageLoader();
    private ProgressLoadFrames plf = new ProgressLoadFrames();



    public void editTransitions(IVFXFiles ivfxFile, int initFrameNum) {

        currentFile = ivfxFile;
        initFrameNumber = initFrameNum;

        try {

            AnchorPane root = FXMLLoader.load(framesController.getClass().getResource("../resources/fxml/frames.fxml")); // в этот момент вызывается initialize()

            framesController.currentScene = new Scene(root);
            framesController.controllerStage = new Stage();
            framesController.controllerStage.setTitle("Редактор переходов. " + framesController.currentFile.getTitle());
            framesController.controllerStage.setScene(framesController.currentScene);
            framesController.controllerStage.initModality(Modality.APPLICATION_MODAL);

            framesController.controllerStage.setOnCloseRequest(we -> {
                framesController.isWorking = false;
                System.out.println("Закрытые окна редактора переходов.");
            });


            framesController.onStart();
            framesController.controllerStage.showAndWait();


            System.out.println("Завершение работы editTransitions");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    @FXML
    void doOK(ActionEvent event) {
        isWorking = false;
        framesController.controllerStage.close();
    }


    @FXML
    void initialize() {

        // запускается при инициализации (load) контроллера

        listFrames = IVFXFrames.loadList(currentFile, false);
        listSegments = IVFXSegments.loadList(currentFile, false);
        if (listSegments.size() == 0 ) {
            listSegments = createSegments(currentFile);
        }


        isWorking = true;
        fil.start();
        plf.start();

        // устанавливаем соответствия для столбцов и таблицы
        colDurationStart.setCellValueFactory(new PropertyValueFactory<>("strDurationStart"));
        colDurationEnd.setCellValueFactory(new PropertyValueFactory<>("strDurationEnd"));
        colFrameStart.setCellValueFactory(new PropertyValueFactory<>("firstFrameNumber"));
        colFrameEnd.setCellValueFactory(new PropertyValueFactory<>("lastFrameNumber"));

        tblPages.setItems(listPages);

        ctlSlider.setMin(-(listPages.size()-1));
        ctlSlider.setMax(0);

        // выбор записи в таблице tblPages
        tblPages.getSelectionModel().selectedItemProperty().addListener((v, oldValue, newValue) -> {
            if (newValue != null) {
                currentPage = newValue; // текущая страница = выбранной

                if (currentPage != getPageByFrame(initFrameNumber)) {
                    // если инитный фрейм не в текущей странице - назначаем инитным фреймом первый фрейм текущей страницы
                    initFrameNumber = currentPage.firstFrameNumber;
                }
                while (!currentPage.isReadyToShow());
                for (int i = 0; i < listPages.size(); i++) {
                    if (currentPage == listPages.get(i)) {
                        currentNumPage = i+1;
                        ctlSlider.setValue(-(double)(currentNumPage));
                        break;
                    }
                }

                showPage(currentPage);

            }
        });

        // прокрутка колеса мыши над CenterPane
        ctlCenterPane.setOnScroll(e -> {
            int delta = e.getDeltaY() > 0 ? -1 : 1;

            if (isPressedControl) {
                if (delta > 0) {
                    goToNextSegment();
                } else {
                    goToPreviousSegment();
                }
            } else {
                if (!(((currentPage.equals(listPages.get(0)) && delta < 0)) || ((currentPage.equals(listPages.get(listPages.size()-1)) && delta > 0)))) {
                    if (delta > 0) {
                        goToFrame(currentPage.listMatrixFrames.get(currentPage.listMatrixFrames.size()-1).nextMatrixFrame.page.listMatrixFrames.get(0));
                    } else {
                        goToFrame(currentPage.listMatrixFrames.get(0).prevMatrixFrame.page.listMatrixFrames.get(0));
                    }
                }
            }



        });

        // прокрутка колеса мыши над FullFrame
        ctlFullFrame.setOnScroll(e -> {
            int delta = e.getDeltaY() > 0 ? -1 : 1;
            int newIndex = 0;

            if (!currentPage.listMatrixFrames.contains(currentFrame) || currentFrame == null) {
                currentFrame = currentPage.listMatrixFrames.get(0);
                currentFrame.frame.getLabel().setStyle(fxBorderSelected);
            }

            if (isPressedControl) {
                if (currentFrame != null) {
                    if (delta > 0) {
                        goToNextSegment();
                    } else {
                        goToPreviousSegment();
                    }
                }
            } else {
                if (currentFrame != null) {
                    if (delta > 0) {
                        goToFrame(currentFrame.nextMatrixFrame);
                    } else {
                        goToFrame(currentFrame.prevMatrixFrame);
                    }
                }
            }

        });


        ctlFullFrame.setOnMouseClicked(me -> {
            if(me.getButton().equals(MouseButton.PRIMARY)){
                if(me.getClickCount() == 1) {
                    isPlayingForward = !isPlayingForward;
                    isPressedPlayForward.set(!isPressedPlayForward.getValue());
                    if (isPlayingForward) {
//                        while (isPlayingForward) {
//                            goToFrame(currentFrame.nextMatrixFrame);
//                        }

//                        new PlayForward().start();
                    }
                }
                if(me.getClickCount() == 2){

                }
            } else {
                if (me.getButton().equals(MouseButton.SECONDARY)){
                    if(me.getClickCount() == 1){

                    }
                }
            }
        });

        isPressedPlayForward.addListener((v, oldValue, newValue) -> {
            if (isPressedPlayForward.getValue()) {
//                goToNextSegment();
                new PlayForward().start();
            }
        });

        ctlSlider.setLabelFormatter(new StringConverter<Double>() {
            @Override
            public String toString(Double object) {
                // преобразум номер страницы в текст времени
                if (listPages.size() > 0) {
                    return listPages.get(-(object.intValue())).strDurationStart;
                }
                return "";
            }

            @Override
            public Double fromString(String string) {
                // плучаем время из текста и получаем номер страницы из времени
                for (int i = 0; i < listPages.size(); i++) {
                    MatrixPages page = listPages.get(i);
                    if (page.strDurationStart.equals(string)) return -(double)(i+1);
                }
                return 0.0;
            }
        });

        //лисенер на изменение значения слайдера
        ctlSlider.valueProperty().addListener((v, oldValue, newValue) -> {
            Integer numNewPage = -(int)newValue.doubleValue();
            if (currentNumPage != numNewPage) { // если слайдер указывает на новую страницу
                currentNumPage = numNewPage;
                currentPage = listPages.get(currentNumPage-1);
                tblPages.getSelectionModel().select(currentPage);
                tblPagesSmartScroll(currentPage);
            }
        });

        //лисенер на изменение ширины пейна
        ctlCenterPane.widthProperty().addListener((v, oldValue, newValue) -> {
            listenToChangePaneSize();
        });

        //лисенер на изменение высоты пейна
        ctlCenterPane.heightProperty().addListener((v, oldValue, newValue) -> {
            listenToChangePaneSize();
        });

        ctlCenterPane.setOnKeyPressed( e -> {
            System.out.println("ctlCenterPane setOnKeyPressed");
        });

        // событие отслеживани видимости на экране текущего персонажа в таблице tblPages
        tblPages.skinProperty().addListener((ChangeListener<Skin>) (ov, t, t1) -> {
            if (t1 == null) {
                return;
            }

            TableViewSkin tvs = (TableViewSkin) t1;
            ObservableList<Node> kids = tvs.getChildren();//    getChildrenUnmodifiable();

            if (kids == null || kids.isEmpty()) {
                return;
            }
            flowTblPages = (VirtualFlow) kids.get(1);
        });



        initContextMenuFrame(); // инициализируем контекстное меню для лейбла фулфрейма



    }

    public void onStart() {

// слушаем нажатие клавиш в сцене
        framesController.currentScene.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (event.getCode() == KeyCode.CONTROL) { // если нажата клавиша Ctrl - устанавливаем флаг isControlPressed
                    isPressedControl = true;
//                    System.out.println(isPressedControl);
                }
                if (event.getCode() == KeyCode.Z) { // если нажата клавиша Z - устанавливаем флаг isPressedPlayBackward
                    isPressedPlayBackward.set(true);
//                    goToPreviousFrame();
                }
                if (event.getCode() == KeyCode.X) { // если нажата клавиша X - устанавливаем флаг isPressedPlayForward
                    isPressedPlayForward.set(true);
//                    goToNextFrame();
                }
            }
        });

        // слушаем отпускание клавиш в сцене
        framesController.currentScene.setOnKeyReleased(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (event.getCode() == KeyCode.CONTROL) { // если отпушена клавиша Ctrl - сбрасываем флаг isControlPressed
                    isPressedControl = false;
//                    System.out.println(isPressedControl);
                }
                if (event.getCode() == KeyCode.Z) { // если отпушена клавиша Z - сбрасываем флаг isPressedPlayBackward
                    isPressedPlayBackward.set(false);
                }
                if (event.getCode() == KeyCode.X) { // если отпушена клавиша X - сбрасываем флаг isPressedPlayForward
                    isPressedPlayForward.set(false);
                }
            }
        });

//        initContextMenuFrame(); // инициализируем контекстное меню для лейбла фулфрейма


    }



    private void goToNextSegment() {

        MatrixFrames frame = currentFrame.nextMatrixFrame;
        if (frame == null) return;
        while (!frame.frame.getIsFinalFind()) {
            frame = frame.nextMatrixFrame;
            if (frame == null) return;
        }
        goToFrame(frame);

    }

    private void goToPreviousSegment() {

        MatrixFrames frame = currentFrame.prevMatrixFrame;
        if (frame == null) return;
        while (!frame.frame.getIsFinalFind()) {
            frame = frame.prevMatrixFrame;
            if (frame == null) return;
        }
        goToFrame(frame);

    }

    private void goToNextFrame() {

//        Platform.runLater(new Runnable() {
//            @Override
//            public void run() {
//
//            }
//        });

        goToFrame(currentFrame.nextMatrixFrame);

    }

    private void goToPreviousFrame() {
        goToFrame(currentFrame.prevMatrixFrame);
    }

    private void goToFrame(MatrixFrames frame) {

        Platform.runLater(() -> {

            if (frame != null) {
                if (!frame.equals(currentFrame)) {
                    // если новый фрейм находится на текущей странице
                    if (frame.page.equals(currentPage)) {
                        currentFrame.frame.getLabel().setStyle(fxBorderDefault);
                    } else {
                        currentPage = frame.page;
                        tblPages.getSelectionModel().select(currentPage);
                        tblPagesSmartScroll(currentPage);
                    }
                    currentFrame = frame;
                    currentFrame.frame.getLabel().setStyle(fxBorderSelected);

                    loadPictureToFullFrameLabel(currentFrame);

                }
            }




        });



    }

    private void loadPictureToFullFrameLabel(MatrixFrames matrixFrame) {

        if (matrixFrame != null) {
            try {
                File file = new File(matrixFrame.frame.getFileNameFullSize());
                if (file.exists()) {
                    BufferedImage bufferedImage = ImageIO.read(file);
                    ImageView imageView = new ImageView(ConvertToFxImage.convertToFxImage(bufferedImage));
                    ctlFullFrame.setGraphic(imageView);
                }
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }

    }

    private void tblPagesSmartScroll(MatrixPages page) {
        if (flowTblPages != null && flowTblPages.getCellCount() > 0) {
            int first = flowTblPages.getFirstVisibleCell().getIndex();
            int last = flowTblPages.getLastVisibleCell().getIndex();
            int selected = tblPages.getSelectionModel().getSelectedIndex();
            if (selected < first || selected > last) {
                tblPages.scrollTo(page);
            }
        }
    }


    private List<IVFXSegments> createSegments(IVFXFiles file) {

        List<IVFXSegments> list = new ArrayList<>();
        if (listFrames.size() != 0) {
            int firstFrameNumber = 1;
            int lastFrameNumber = 0;
            int currentFrameNumber = 0;
            int currentIFrame = 1;
            int previousIFrame = 1;

            // цикл по фреймам
            for (IVFXFrames ivfxFrame : listFrames) {
                if (ivfxFrame.getIsIFrame()) currentIFrame = ivfxFrame.getFrameNumber();
                currentFrameNumber = ivfxFrame.getFrameNumber();
                // если нашли переход кадров
                if (ivfxFrame.getIsFinalFind()) {
                    lastFrameNumber = currentFrameNumber-1;

                    IVFXSegments tempSegment = IVFXSegments.getNewDbInstance(file);

                    tempSegment.setFileId(file.getId());
                    tempSegment.setFileUuid(file.getUuid());
                    tempSegment.setFirstFrameNumber(firstFrameNumber);
                    tempSegment.setLastFrameNumber(lastFrameNumber);
                    tempSegment.setNearestIFrame(previousIFrame);
                    tempSegment.setIvfxFile(file);
                    tempSegment.save();

                    list.add(tempSegment);

                    firstFrameNumber = currentFrameNumber;
                    previousIFrame = currentIFrame;
                }
            }

            IVFXSegments tempSegment = IVFXSegments.getNewDbInstance(file);

            tempSegment.setFileId(file.getId());
            tempSegment.setFileUuid(file.getUuid());
            tempSegment.setFirstFrameNumber(firstFrameNumber);
            tempSegment.setLastFrameNumber(lastFrameNumber);
            tempSegment.setNearestIFrame(previousIFrame);
            tempSegment.setIvfxFile(file);
            tempSegment.save();

            list.add(tempSegment);
        }

        return list;
    }

    private void actualizeSegments(IVFXFrames frame) {

        if (frame != null) {
            for (int i = 0; i < listSegments.size(); i++) {
                IVFXSegments segment = listSegments.get(i);

                // находим сегмент, в котором сидит фрейм
                if (frame.getFrameNumber() >= segment.getFirstFrameNumber() && frame.getFrameNumber() <= segment.getLastFrameNumber()) {
                    // если фрейм переходной, а в сегменте он не на первом месте - надо разрезать сегмент на 2 части
                    if (frame.getIsFinalFind() && segment.getFirstFrameNumber() != frame.getFrameNumber()) {
                        int lastFrameNumber = segment.getLastFrameNumber(); // запоминаем последний фрейм сегмента
                        segment.setLastFrameNumber(frame.getFrameNumber() - 1); // устанавливаем конец текущего семента предыдущий кадр от переданного
                        segment.save();

                        IVFXSegments newSegment = IVFXSegments.getNewDbInstance(frame.getIvfxFile());
                        newSegment.setIvfxFile(frame.getIvfxFile());
                        newSegment.setFileId(newSegment.getIvfxFile().getId());
                        newSegment.setFileUuid(newSegment.getIvfxFile().getUuid());
                        newSegment.setFirstFrameNumber(frame.getFrameNumber());
                        newSegment.setLastFrameNumber(lastFrameNumber);
                        int nearestIFrame = 1;
                        for (int j = frame.getFrameNumber()-1; j >= 0 ; j--) {
                            if (listFrames.get(j).getIsIFrame()) {
                                nearestIFrame = j + 1;
                                break;
                            }
                        }
                        newSegment.setNearestIFrame(nearestIFrame);
                        newSegment.save();
                        listSegments.add(newSegment);

                    // если фрейм не переходной, но с него начинается сегмент - надо сегмент склеить с предыдущим
                    } else if (!frame.getIsFinalFind() && segment.getFirstFrameNumber() == frame.getFrameNumber()) {
                        IVFXSegments segmentToUnion = null;
                        for (int j = 0; j < listSegments.size(); j++) {
                            IVFXSegments tmp = listSegments.get(j);
                            if (tmp.getLastFrameNumber() == frame.getFrameNumber() - 1) {
                                segmentToUnion = tmp;
                                break;
                            }
                        }
                        segmentToUnion.setLastFrameNumber(segment.getLastFrameNumber());
                        segmentToUnion.save();

                        listSegments.remove(segment);
                        segment.delete();

                    }

                    break;
                }
            }
        }


    }

    private void showPage(MatrixPages page) {

        int heightPadding = 10;   // по высоте двойной отступ
        int widthPadding = 10; // по ширине двойной отступ

        Pane pane = ctlCenterPane;
        List<MatrixFrames> listMatrix = page.listMatrixFrames;
        pane.getChildren().clear(); // очищаем пэйн от старых лейблов

        for (MatrixFrames matrix : listMatrix) {
            Label lbl = matrix.frame.getLabel();

            int x = widthPadding + matrix.frameColumn * (pictW+2);  // X = отступ по ширине + столбец*ширину картинки
            int y = heightPadding + matrix.frameRow * (pictH+2); //Y = отступ по высоте + строка*высоту картинки

            lbl.setTranslateX(x);
            lbl.setTranslateY(y);
            lbl.setPrefSize(pictW,pictH);   //устанавливаем ширину и высоту лейбла
            lbl.setStyle(fxBorderDefault);  //устанавливаем стиль бордюра по-дефолту
            lbl.setAlignment(Pos.CENTER);   //устанавливаем позиционирование по центру

            BufferedImage currImage = null;
            BufferedImage resultImage = null;

            // если кадр ключевой
            if (matrix.frame.getIsIFrame()) {
                if (currImage == null) {
                    try {
                        currImage = ImageIO.read(new File(matrix.frame.getFileNamePreview()));
                    } catch (IOException e) {}
                }
                resultImage = OverlayImage.setOverlayIFrame(currImage);
                currImage = resultImage;
            }

            // если кадр найденый
            if (matrix.frame.getIsFind()) {
                if (currImage == null) {
                    try {
                        currImage = ImageIO.read(new File(matrix.frame.getFileNamePreview()));
                    } catch (IOException e) {}
                }
                if (!matrix.frame.getIsManualCancel()) { // и не отменен
                    resultImage = OverlayImage.setOverlayFirstFrameFound(currImage);
                    currImage = resultImage;
                } else { // и отменен
                    resultImage = OverlayImage.cancelOverlayFirstFrameManual(currImage);
                    currImage = resultImage;
                }
            }

            // если следующий кадр найденый
            if (matrix.nextMatrixFrame != null) {
                if (matrix.nextMatrixFrame.frame.getIsFind()) {
                    if (currImage == null) {
                        try {
                            currImage = ImageIO.read(new File(matrix.frame.getFileNamePreview()));
                        } catch (IOException e) {}
                    }
                    if (!(matrix.nextMatrixFrame.frame.getIsManualCancel())) { // и не отменен
                        resultImage = OverlayImage.setOverlayLastFrameFound(currImage);
                        currImage = resultImage;
                    } else { // и отменен
                        resultImage = OverlayImage.cancelOverlayLastFrameManual(currImage);
                        currImage = resultImage;
                    }
                }
            }

            // если кадр устанвлен вручную
            if (matrix.frame.getIsManualAdd()) {
                if (currImage == null) {
                    try {
                        currImage = ImageIO.read(new File(matrix.frame.getFileNamePreview()));
                    } catch (IOException e) {}
                }
                resultImage = OverlayImage.setOverlayFirstFrameManual(currImage);
                currImage = resultImage;
            }

            // если следующий кадр устанвлен вручную
            if (matrix.nextMatrixFrame != null) {
                if (currImage == null) {
                    try {
                        currImage = ImageIO.read(new File(matrix.frame.getFileNamePreview()));
                    } catch (IOException e) {}
                }
                if (matrix.nextMatrixFrame.frame.getIsManualAdd()) {
                    resultImage = OverlayImage.setOverlayLastFrameManual(currImage);
                    currImage = resultImage;
                }
            }

            if (resultImage != null) {
                ImageView screenImageView = new ImageView(ConvertToFxImage.convertToFxImage(resultImage)); // загружаем ресайзный буфер в новый вьювер
                screenImageView.setFitWidth(pictW);   // устанавливаем ширину вьювера
                screenImageView.setFitHeight(pictH);  // устанавливаем высоту вьювера
                lbl.setGraphic(null); //сбрасываем графику лейбла
                lbl.setGraphic(screenImageView);    // устанавливаем вьювер источником графики для лейбла
            }

            pane.getChildren().add(lbl);

            //событие "наведение мыши"
            lbl.setOnMouseEntered(e -> {
                lbl.setStyle(fxBorderFocused);
                lbl.toFront();
            });

            //событие "уход мыши"
            lbl.setOnMouseExited(e -> {
                if (matrix == currentFrame) {
                    lbl.setStyle(fxBorderSelected);
                } else {
                    lbl.setStyle(fxBorderDefault);
                }
                });


            lbl.setOnMouseClicked(new EventHandler<MouseEvent>()  { //событие двойного клика
                @Override
                public void handle(MouseEvent mouseEvent) {
                    if(mouseEvent.getButton().equals(MouseButton.PRIMARY)){
                        if(mouseEvent.getClickCount() == 1) {
                            if (currentFrame != null) currentFrame.frame.getLabel().setStyle(fxBorderDefault);
                            currentFrame = matrix;
                            loadPictureToFullFrameLabel(currentFrame);

                        }
                        if(mouseEvent.getClickCount() == 2){

                            if (matrix.frame.getIsFind()) { //фрейм найден
                                if (!matrix.frame.getIsManualCancel()) { // и не отменен вручную
                                    matrix.frame.setIsManualCancel(true); // отменяем
                                    matrix.frame.setIsFinalFind(false);
                                    matrix.frame.save();
                                } else { // и отменен вручную
                                    matrix.frame.setIsManualCancel(false); // восстанавливаем отметку
                                    matrix.frame.setIsFinalFind(true);
                                    matrix.frame.save();
                                }
                            } else { // не найден
                                if (!matrix.frame.getIsManualAdd()) { // и не отменен вручную
                                    matrix.frame.setIsManualAdd(true); // отмечаем
                                    matrix.frame.setIsFinalFind(true);
                                    matrix.frame.save();
                                } else { // и отменен вручную
                                    matrix.frame.setIsManualAdd(false); // снимаем отметку
                                    matrix.frame.setIsFinalFind(false);
                                    matrix.frame.getLabel().setGraphic(null);
                                    matrix.frame.getLabel().setGraphic(matrix.frame.getPreview());
                                    if (matrix.prevMatrixFrame != null) {
                                        matrix.prevMatrixFrame.frame.getLabel().setGraphic(null);
                                        matrix.prevMatrixFrame.frame.getLabel().setGraphic(matrix.prevMatrixFrame.frame.getPreview());
                                    }
                                    matrix.frame.save();
                                }
                            }

                            actualizeSegments(matrix.frame);

                            initFrameNumber = currentPage.firstFrameNumber;
                            createPages();
                            tblPages.setItems(listPages);
                            currentPage = getPageByFrame(initFrameNumber);
                            tblPages.getSelectionModel().select(currentPage);

                        }
                    } else {
                        if (mouseEvent.getButton().equals(MouseButton.SECONDARY)){
                            if(mouseEvent.getClickCount() == 1){
//                                currentScreenFrame = finalMatrixScreen.matrixFrames.frame;
//                                contextMenuLabel.show(screenLabel,mouseEvent.getScreenX(), mouseEvent.getScreenY());
                            }
                        }
                    }
                }
            });

        }

    }


    // лисенер изменения размера центрального пэйна
    private void listenToChangePaneSize() {

        double paneWidth = ctlCenterPane.getWidth(); // ширина центрального пэйна
        double paneHeight = ctlCenterPane.getHeight();   // высота центрального пейна

        int widthPadding = (pictW+2) * 2 + 20; // по ширине двойной отступ
        int heightPadding = (pictH+2) * 2 + 20;   // по высоте двойной отступ
        if (paneWidth > widthPadding && paneHeight > heightPadding) {
            int prevCountColumnsInPage = countColumnsInPage;
            int prevCountRowsInPage = countRowsInPage;

            countColumnsInPage = (int)((paneWidth - widthPadding) / (pictW+2)); // количество столбцов, которое влезет на экран
            countRowsInPage = (int)((paneHeight - heightPadding) / (pictH+2));  // количество строк, которое влезет на экран

            // если значения кол-ва столбцов и/или строк изменилось при ресайзе
            if (prevCountColumnsInPage != countColumnsInPage || prevCountRowsInPage != countRowsInPage) {
                createPages(); // заново создаем список страниц



                // если список страниц не пустой
                if (listPages.size() > 0) {


                    tblPages.setItems(listPages);   // запихиваем список в таблицу
                    tblPages.refresh();             // рефрешим таблицу
                    MatrixPages currPage = getPageByFrame(initFrameNumber); // узнаем, в какой странице сидит инитный фрейм
                    ctlSlider.setMin(-(listPages.size()-1));
                    ctlSlider.setMax(0);
                    for (int i = 0; i < listPages.size(); i++) {
                        if (currPage == listPages.get(i)) {
                            ctlSlider.setValue(-(double)(i+1));
                            break;
                        }
                    }
                    tblPages.getSelectionModel().select(currPage); // переходим на эту страницу в таблице
                    tblPagesSmartScroll(currPage);

                }

            }

        }

    }

    // возвращает страницу из листа, в которой сидит переданный фрейм
    private MatrixPages getPageByFrame(int frameNumber) {
        for (MatrixPages page : listPages) {
            if (page.firstFrameNumber <= frameNumber && page.lastFrameNumber >= frameNumber) return page;
        }
        return  null;
    }

    private void createPages() {
        int countFrames = listFrames.size();    // запоминаем кол-во фреймов в файле

        if (countFrames > 0) {

            listPages.clear();  // очищаем список страниц

            MatrixPages matrixPage = new MatrixPages(); // создаем новую страницу

            MatrixFrames nextMatrixFrame = null, prevMatrixFrame = null, lastMatrixFrameInPage = null;
            int currentColumn = 1, currentRow = 1;
            boolean wasAddedNewPage = false;

            // цикл по списку всех фреймов
            for (int i = 0; i < countFrames; i++) {

                // считываем из списка фреймов текущий, предыдущий и последующие фреймы
                IVFXFrames currFrame = listFrames.get(i), previousFrame = null, nextFrame = null;
                if (i > 0) previousFrame = listFrames.get(i-1);
                if (i < countFrames - 1) nextFrame = listFrames.get(i+1);

                MatrixFrames matrixFrame = new MatrixFrames(); // создаем матрикс-фрейм для текущего фрейма
                matrixFrame.frame = currFrame;   // записываем текущий фрейм в матрикс-фрейм
                matrixFrame.frameColumn = currentColumn;    // записываем в матрикс-фрейм значение текущего столбца
                matrixFrame.frameRow = currentRow;          // записываем в матрикс-фрейм значение текущей строки
                matrixFrame.prevMatrixFrame = prevMatrixFrame;  // записываем в матрикс-фрейм ссылку на предыдущий матрикс-фрейм
                if (prevMatrixFrame != null) prevMatrixFrame.nextMatrixFrame = matrixFrame; // в предыдущий матрикс-фрейме записываем текущий матрикс-фрейм как "следующий"
                matrixFrame.page = matrixPage; // сохраняем в матрикс-фрейме его страницу
                matrixPage.listMatrixFrames.add(matrixFrame);   // добавляем матрикс-фрейм к списку матрикс-фреймов текущей страницы
                prevMatrixFrame = matrixFrame;  // записываем в "предыдущий" матрикс-фрейм ссылку на текущий матрикс-фрейм

                if (wasAddedNewPage) {
                    wasAddedNewPage = false;
                    lastMatrixFrameInPage.nextMatrixFrame = matrixFrame;
                }

                // если оказалось, что мы только что добавили матрикс-фрейм за границы матрицы страницы - пора создавать новую страницу
                if (currentColumn == countColumnsInPage + 1 || currentRow == countRowsInPage + 1) {
                    matrixFrame.isNext = true; // помечаем текущий матрикс-фрейм как "следующий"
                    lastMatrixFrameInPage = matrixFrame;
                    listPages.add(matrixPage);  // добавляем текущую страницу в список всех страниц
//                if (currentPage == null) {
//                    currentPage = matrixPage;
//                    currentFrame = currentPage.listMatrixFrames.get(0);
//                    currFrame.getLabel().setStyle(fxBorderSelected);
//                }

                    // теперь можно создавать новую страницу
                    matrixPage = new MatrixPages(); // создаем новую страницу (перезаписываем переменную новым объектом)

                    MatrixFrames zeroFrame = new MatrixFrames();    // создаем "нулевой" матрикс-фрейм, который будет копией пред-последнего матрикс-фрейма предыдущей страницы
                    zeroFrame.isPrevious = true;    // помечаем "нулевой" матрикс-фрейм как "предыдущий"
                    zeroFrame.frame = matrixFrame.prevMatrixFrame.frame; // помещаем в "нулевой" матрикс-фрейм текущий фрейм
                    zeroFrame.prevMatrixFrame = matrixFrame.prevMatrixFrame.prevMatrixFrame;   // "обнуляем" у "нулевого" матрикс-фрейма "предыдущий" матрикс-фрейм

                    if (matrixFrame.frame.getIsFinalFind()) {
                        // если текущий фрейм явлется переходным, то "нулевой" матрикс-фрейм располагаем "над" первым матрикс-фреймом новой страницы (столбец 1, строка 0)
                        currentColumn = 1;
                        currentRow = 0;
                    } else {
                        // если текущий фрейм обычный, то "нулевой" матрикс-фрейм располагаем "слева" от первого матрикс-фрейма новой страницы (столбец 0, строка 1)
                        currentColumn = 0;
                        currentRow = 1;
                    }
                    zeroFrame.frameColumn = currentColumn;  // записываем в "нулевой" матрикс-фрейм значение его столбца
                    zeroFrame.frameRow = currentRow;    // записываем в "нулевой" матрикс-фрейм значение его строки
                    zeroFrame.page = matrixPage; // сохраняем в матрикс-фрейме его страницу
                    matrixPage.listMatrixFrames.add(zeroFrame); // добавляем "нулевой" матрикс-фрейм к списку матрикс-фреймов вновь созданной страницы - он будет в этом списке первым
                    lastMatrixFrameInPage.nextMatrixFrame = zeroFrame;
                    prevMatrixFrame = zeroFrame;    // записываем в "предыдущий" матрикс-фрейм ссылку на "нулевой" матрикс-фрейм

                    currentColumn = 1;  // устанавливаем значение текущего столбцу = 1
                    currentRow = 1;     // устанавливаем значение текущей строки = 1

                    MatrixFrames firstFrame = new MatrixFrames();   // создаем новый матрикс-фрейм, который будет первым на новой странице
                    firstFrame.frame = matrixFrame.frame;   // присваиваем ему текущий фрейм
                    firstFrame.frameColumn = currentColumn; // устанавливаем столбец = 1
                    firstFrame.frameRow = currentRow;   // устанавливаем строку = 1
                    firstFrame.prevMatrixFrame = prevMatrixFrame; // в поле "предыдущий" записываем ссылку на "нулевой" матрикс-фрейм
                    prevMatrixFrame.nextMatrixFrame = firstFrame; // у "нулевого" мартикс-фрейма в поле "следующий" записываем ссылку на созданный матрикс-фрейм
                    firstFrame.page = matrixPage; // сохраняем в матрикс-фрейме его страницу
                    matrixPage.listMatrixFrames.add(firstFrame);    // добавляем созданный матрикс-фрейм к списку матрикс-фреймов новой страницы - он будет там вторым по счету
                    prevMatrixFrame = firstFrame;    // записываем в "предыдущий" матрикс-фрейм ссылку на "первый" матрикс-фрейм

                    wasAddedNewPage = true; // устанавливаем флаг, что мы только что создали новую страницу и уже установили для нее координаты для "текущего" матрикс-фрейма, который будет создан на следующем шаге цикла

                }

                // надо правильно изменить значения текущих столбца и строки для следущюего шага

                if (nextFrame != null && nextFrame.getIsFinalFind()) {
                    // если следущий фрейм "найденый" - он 100% должен быть в том же столбце, но на новой строке
                    currentRow++;
                } else {
                    // если следущий фрейм "обычный" - надо проверить, куда его поместить - в продолжении текущей строки или начать им новую строку
                    if (currentColumn < countColumnsInPage) {
                        // если на данный момент мы еще не дошли до последнего столбца страницы - "передвигаемся" на один столбец вправо
                        currentColumn++;
                    } else if (currentColumn == countColumnsInPage && currentRow == countRowsInPage){
                        // если мы стоим на последней яцейке страницы - тоже "передвигаемся" вправо
                        currentColumn++;
                    } else if (currentColumn == countColumnsInPage && currentRow < countRowsInPage){
                        // если мы уже стоим на последнем столбце сраницы, но еще не на последней строке - "перемещаемся" в начало новой строки
                        currentColumn = 1;
                        currentRow++;
                    }
                }

            }

            // добавляем последнюю страницу в список всех страниц
            listPages.add(matrixPage);

            // теперь для каждой страницы из списка нужно прописать время начала, время конца, первый и последний кадры
            for (int i = 0; i < listPages.size(); i++) {
                MatrixPages page = listPages.get(i);

                page.pageNumber = i + 1;
                MatrixFrames firstMatrixFrame = i == 0 ? page.listMatrixFrames.get(0) : page.listMatrixFrames.get(1);
                MatrixFrames lastMatrixFrame = i == listPages.size()-1 ? page.listMatrixFrames.get(page.listMatrixFrames.size()-1) : page.listMatrixFrames.get(page.listMatrixFrames.size()-2);
                page.firstFrameNumber = firstMatrixFrame.frame.getFrameNumber();
                page.lastFrameNumber = lastMatrixFrame.frame.getFrameNumber();
                page.strDurationStart = FFmpeg.convertDurationToString(FFmpeg.getDurationByFrameNumber(page.firstFrameNumber,currentFile.getFrameRate()));
                page.strDurationEnd = FFmpeg.convertDurationToString(FFmpeg.getDurationByFrameNumber(page.lastFrameNumber,currentFile.getFrameRate()));
                page.countFramesInPage = page.lastFrameNumber - page.firstFrameNumber;
                page.countColumns = countColumnsInPage;
                page.countRows = countRowsInPage;

            }

            System.out.println("Создано реальных страниц: " + listPages.size());


        }



    }



    private class PlayForward extends Thread {
        public void run() {
            while (isPlayingForward) {
                try {
                    Thread.sleep((int) (1000 / currentFrame.frame.getIvfxFile().getFrameRate()));

                    MatrixFrames frame = currentFrame.nextMatrixFrame;

                    Platform.runLater(() -> {


                        if (frame != null) {
                            if (!frame.equals(currentFrame)) {
                                // если новый фрейм находится на текущей странице
                                if (frame.page.equals(currentPage)) {
                                    currentFrame.frame.getLabel().setStyle(fxBorderDefault);
                                } else {
                                    currentPage = frame.page;
                                    tblPages.getSelectionModel().select(currentPage);
                                    tblPagesSmartScroll(currentPage);
                                }
                                currentFrame = frame;
                                currentFrame.frame.getLabel().setStyle(fxBorderSelected);


                                loadPictureToFullFrameLabel(currentFrame);


                            }
                        }
                    });


                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }
    }

    private class ProgressLoadFrames extends  Thread {
        public void run() {

            while (isWorking) {
                double progress = (1.0d * countLoadedPages) / (COUNT_LOADED_PAGE_AFTER_CURRENT + COUNT_LOADED_PAGE_BEFORE_CURRENT);
                ctlProgressBar.setProgress(progress);
            }
            System.out.println("ProgressLoadFrames завершил работу.");

        }
    }

    private class FramesImageLoader extends Thread {
        public void run() {
            MatrixPages previousCurrentPage = null, prevPage = null, nextPage = null;
            MatrixFrames prevMatFr = null, nextMatFr = null;


            while (isWorking) { // работаем, пока на закроется форма

                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if (currentPage != null) {
                    if (!currentPage.equals(previousCurrentPage)) { // если на очередном шаге цикла работа оказалось, что текущая страница поменялась

                        previousCurrentPage = currentPage;
                        // начинаем идти от первого фрейма текущуй страницы в разные стороны
                        prevMatFr = currentPage.listMatrixFrames.get(0);
                        nextMatFr = currentPage.listMatrixFrames.get(0);
                        prevPage = currentPage;
                        nextPage = currentPage;

                        int prevCounterPages = 0;
                        int nextCounterPages = 0;

                        while (!(prevMatFr == null && nextMatFr == null)) {

                            if (!currentPage.equals(previousCurrentPage) || !isWorking) break;

                            countLoadedPages = prevCounterPages + nextCounterPages;

                            if (prevMatFr != null) {

                                if (!prevMatFr.page.equals(prevPage)) {
                                    prevPage = prevMatFr.page;
                                    prevCounterPages++;
                                }
                                IVFXFrames frame = prevMatFr.frame;
                                if (prevCounterPages <= COUNT_LOADED_PAGE_BEFORE_CURRENT) {

                                    if (frame.getLabel() == null) {
                                        String fileName = frame.getFileNamePreview();
                                        File file = new File(fileName);
                                        Label label = new Label();

                                        label.setMinSize(pictW+2, pictH+2);
                                        label.setPrefSize(pictW, pictH);
                                        label.setMaxSize(pictW+2, pictH+2);

                                        if (!file.exists()) {
                                            fileName = frame.getFileNamePreviewStub();
                                            file = new File(fileName);
                                        }
                                        try {
                                            BufferedImage bufferedImage = ImageIO.read(file);
                                            ImageView preview = new ImageView(ConvertToFxImage.convertToFxImage(bufferedImage));
                                            label.setGraphic(preview);
                                            label.setContentDisplay(ContentDisplay.TOP);
                                            frame.setLabel(label);
                                            frame.setPreview(preview);
                                        } catch (IOException e) {}
                                    }
                                } else {
                                    if (frame.getLabel() != null) {
                                        frame.setLabel(null);
                                        frame.setPreview(null);
                                    }
                                }
                                prevMatFr = prevMatFr.prevMatrixFrame;
                            }

                            if (nextMatFr != null) {

                                if (!nextMatFr.page.equals(nextPage)) {
                                    nextPage = nextMatFr.page;
                                    nextCounterPages++;
                                }
                                IVFXFrames frame = nextMatFr.frame;
                                if (nextCounterPages <= COUNT_LOADED_PAGE_AFTER_CURRENT) {

                                    if (frame.getLabel() == null) {
                                        String fileName = frame.getFileNamePreview();
                                        File file = new File(fileName);
                                        Label label = new Label();

                                        label.setMinSize(pictW+2, pictH+2);
                                        label.setPrefSize(pictW, pictH);
                                        label.setMaxSize(pictW+2, pictH+2);

                                        if (!file.exists()) {
                                            fileName = frame.getFileNamePreviewStub();
                                            file = new File(fileName);
                                        }
                                        try {
                                            BufferedImage bufferedImage = ImageIO.read(file);
                                            ImageView preview = new ImageView(ConvertToFxImage.convertToFxImage(bufferedImage));
                                            label.setGraphic(preview);
                                            label.setContentDisplay(ContentDisplay.TOP);
                                            frame.setLabel(label);
                                            frame.setPreview(preview);
                                        } catch (IOException e) {}
                                    }
                                } else {
                                    if (frame.getLabel() != null) {
                                        frame.setLabel(null);
                                        frame.setPreview(null);
                                    }
                                }
                                nextMatFr = nextMatFr.nextMatrixFrame;
                            }

                        }

                    }

                }


            }

            System.out.println("FramesImageLoader завершил работу.");

        }
    }

    private void initContextMenuFrame() {

        Platform.runLater(() -> {
            List<IVFXGroups> listGroups = IVFXGroups.loadList(currentFile.getIvfxProject());

            Menu menu = new Menu("(без группы)");
            MenuItem menuItem = new MenuItem("(Новый персонаж)");
            menuItem.setOnAction(e -> {addNewPerson(null);});
            menu.getItems().add(menuItem);

            List<IVFXPersons> listPersons = IVFXPersons.loadListPersonsWithoutGroups(currentFile.getIvfxProject(),false);
            for (IVFXPersons person : listPersons) {
                menuItem = new MenuItem(person.getName());
                menuItem.setOnAction(e -> {setCurrentFrameAsPictureToPerson(person);});
                menu.getItems().add(menuItem);
            }
            contxtMenuFrame.getItems().add(menu);
            contxtMenuFrame.getItems().add(new SeparatorMenuItem());
            for (IVFXGroups group : listGroups) {
                menu = new Menu(group.getName());
                contxtMenuFrame.getItems().add(menu);
                menuItem = new MenuItem("(Новый персонаж)");
                menuItem.setOnAction(e -> {addNewPerson(group);});
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
        });

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

    private void addNewPerson(IVFXGroups group) {
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

                    if (group != null) {
                        IVFXGroupsPersons groupPerson = IVFXGroupsPersons.getNewDbInstance(group,person, false);
                    }

                    try {
                        IVFXFrames.setPictureToPerson(currentFrame.frame, person);
                    } catch (IOException exception) {
                    }

                }
                initContextMenuFrame();
            }
        }
    }

    private void setCurrentFrameAsPictureToPerson(IVFXPersons person) {
        try {
            IVFXFrames.setPictureToPerson(currentFrame.frame, person);
        } catch (IOException exception) {
        }
    }

    private class MatrixFrames {
        int frameColumn;
        int frameRow;
        boolean isPrevious; // лейбл - перед первым
        boolean isNext; // лейбл - после последнего
        IVFXFrames frame;
        MatrixFrames nextMatrixFrame;
        MatrixFrames prevMatrixFrame;
        MatrixPages page;
    }

    public class MatrixPages {
        int pageNumber;     // номер страницы
        int firstFrameNumber;   // номер первого кадра страницы
        int lastFrameNumber;   // номер последнего кадра страницы
        String strDurationStart;
        String strDurationEnd;
        int countFramesInPage;  // количество кадров на странице
        int countColumns; // количество столбцов на странице
        int countRows; // количество строк на странице
        List<MatrixFrames> listMatrixFrames = new ArrayList<>();

        public boolean isReadyToShow() {
            int countAll = this.listMatrixFrames.size();
            int countReady = 0;
            for (MatrixFrames matrix : this.listMatrixFrames) {
                if (matrix.frame.getLabel() == null) return false;
            }
            return true;
        }

        public int getPageNumber() {
            return pageNumber;
        }

        public int getFirstFrameNumber() {
            return firstFrameNumber;
        }

        public int getLastFrameNumber() {
            return lastFrameNumber;
        }

        public String getStrDurationStart() {
            return strDurationStart;
        }

        public String getStrDurationEnd() {
            return strDurationEnd;
        }

        public void setPageNumber(int pageNumber) {
            this.pageNumber = pageNumber;
        }

        public void setFirstFrameNumber(int firstFrameNumber) {
            this.firstFrameNumber = firstFrameNumber;
        }

        public void setLastFrameNumber(int lastFrameNumber) {
            this.lastFrameNumber = lastFrameNumber;
        }

        public void setStrDurationStart(String strDurationStart) {
            this.strDurationStart = strDurationStart;
        }

        public void setStrDurationEnd(String strDurationEnd) {
            this.strDurationEnd = strDurationEnd;
        }

        @Override
        public String toString() {
            return "Page: #" + pageNumber + ", [" + firstFrameNumber + "-" + lastFrameNumber + "] (" + strDurationStart + "-" + strDurationEnd + "), frames = " + countFramesInPage;
        }
    }


}

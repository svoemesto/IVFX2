package com.svoemesto.ivfx.tables;

import com.svoemesto.ivfx.Main;
import com.svoemesto.ivfx.utils.ConvertToFxImage;
import com.svoemesto.ivfx.utils.FFmpeg;
import com.svoemesto.ivfx.utils.OverlayImage;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.ImageView;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class IVFXShots implements Comparable<IVFXShots>  {

    private int id;
    private int fileId;
    private int shotsTypeSizeId = 0;
    private int shotsTypePersonId = 0;
    private IVFXShotsTypeSize ivfxShotsTypeSize;
    private IVFXShotsTypePersons ivfxShotsTypePersons;
    private IVFXFiles ivfxFile;
    private int firstFrameNumber;
    private int lastFrameNumber;
    private int nearestIFrame;
    private ImageView[] imageViewFirst = new ImageView[3];
    private ImageView[] imageViewLast = new ImageView[3];
    private Label[] labelFirst = new Label[3];
    private Label[] labelLast = new Label[3];
    private boolean isBodyScene;
    private boolean isStartScene;
    private boolean isEndScene;
    private boolean isBodyEvent;
    private boolean isStartEvent;
    private boolean isEndEvent;

    protected transient static List<IVFXShots> ivfxShotsList;

//TODO ISEQUAL

    public boolean isEqual(IVFXShots o) {
        return (this.id == o.id &&
                this.fileId == o.fileId &&
                this.firstFrameNumber == o.firstFrameNumber &&
                this.lastFrameNumber == o.lastFrameNumber &&
                this.nearestIFrame == o.nearestIFrame);
    }

    // TODO OVERRIDE

    @Override
    public int compareTo(IVFXShots o) {
        Integer a = (Integer)getFirstFrameNumber();
        Integer b = (Integer)o.getFirstFrameNumber();
        return a.compareTo(b);
    }

// TODO КОНСТРУКТОРЫ


    // пустой конструктор
    public IVFXShots() {
    }

    public static IVFXShots getNewDbInstance(IVFXFiles file) {

        IVFXShots ivfxShot = new IVFXShots();

        Statement statement = null;
        ResultSet rs = null;
        String sql;

        ivfxShot.fileId = file.getId();
        ivfxShot.shotsTypeSizeId = 0;
        ivfxShot.shotsTypePersonId = 0;
        ivfxShot.ivfxShotsTypeSize = IVFXShotsTypeSize.load(ivfxShot.shotsTypeSizeId);
        ivfxShot.ivfxShotsTypePersons = IVFXShotsTypePersons.load(ivfxShot.shotsTypePersonId);
        ivfxShot.fileId = file.getId();
        ivfxShot.ivfxFile = file;

        try {

            sql = "INSERT INTO tbl_shots (" +
                    "file_id, " +
                    "shot_type_size_id, " +
                    "shot_type_person_id, " +
                    "firstFrameNumber, " +
                    "lastFrameNumber, " +
                    "nearestIFrame) " +
                    "VALUES(" +
                    ivfxShot.fileId + "," +
                    ivfxShot.shotsTypeSizeId + "," +
                    ivfxShot.shotsTypePersonId + "," +
                    ivfxShot.firstFrameNumber + "," +
                    ivfxShot.lastFrameNumber + "," +
                    ivfxShot.nearestIFrame + ")";

            PreparedStatement ps = Main.mainConnection.prepareStatement(sql);
            ps.executeUpdate(sql, Statement.RETURN_GENERATED_KEYS);
            rs = ps.getGeneratedKeys();
            if (rs.next()) {
                ivfxShot.id = rs.getInt(1);
                System.out.println("Создана запись для плана " + ivfxShot.firstFrameNumber + "-" + ivfxShot.lastFrameNumber + ", файл «" + ivfxShot.ivfxFile.getTitle() + "» с идентификатором " + rs.getInt(1));
            }

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

        return ivfxShot;

    }

    public static IVFXShots load(int id, boolean withPreview) {

        Statement statement = null;
        ResultSet rs = null;
        String sql;

        try {
            statement = Main.mainConnection.createStatement();

//            sql = "SELECT * FROM tbl_shots WHERE id = " + id;
            sql = "SELECT " +
                    "qry_shots_in_scenes_and_events.id, " +
                    "qry_shots_in_scenes_and_events.file_id, " +
                    "qry_shots_in_scenes_and_events.shot_type_size_id, " +
                    "qry_shots_in_scenes_and_events.shot_type_person_id, " +
                    "qry_shots_in_scenes_and_events.firstFrameNumber, " +
                    "qry_shots_in_scenes_and_events.lastFrameNumber, " +
                    "qry_shots_in_scenes_and_events.nearestIFrame, " +
                    "SUM(qry_shots_in_scenes_and_events.isBodyEvent) > 0 AS isBodyEvent, " +
                    "SUM(qry_shots_in_scenes_and_events.isStartEvent) > 0 AS isStartEvent, " +
                    "SUM(qry_shots_in_scenes_and_events.isEndEvent) > 0 AS isEndEvent, " +
                    "SUM(qry_shots_in_scenes_and_events.isBodyScene) > 0 AS isBodyScene, " +
                    "SUM(qry_shots_in_scenes_and_events.isStartScene) > 0 AS isStartScene, " +
                    "SUM(qry_shots_in_scenes_and_events.isEndScene) > 0 AS isEndScene " +
                    "FROM qry_shots_in_scenes_and_events " +
                    "WHERE qry_shots_in_scenes_and_events.id = " + id + " " +
                    "GROUP BY qry_shots_in_scenes_and_events.id, " +
                    "qry_shots_in_scenes_and_events.file_id, " +
                    "qry_shots_in_scenes_and_events.shot_type_size_id, " +
                    "qry_shots_in_scenes_and_events.shot_type_person_id, " +
                    "qry_shots_in_scenes_and_events.firstFrameNumber, " +
                    "qry_shots_in_scenes_and_events.lastFrameNumber, " +
                    "qry_shots_in_scenes_and_events.nearestIFrame";

            rs = statement.executeQuery(sql);
            if (rs.next()) {
                IVFXShots shot = new IVFXShots();
                shot.id = rs.getInt("id");
                shot.fileId = rs.getInt("file_id");
                shot.shotsTypeSizeId = rs.getInt("shot_type_size_id");
                shot.shotsTypePersonId = rs.getInt("shot_type_person_id");
                shot.firstFrameNumber = rs.getInt("firstFrameNumber");
                shot.lastFrameNumber = rs.getInt("lastFrameNumber");
                shot.nearestIFrame = rs.getInt("nearestIFrame");
                shot.ivfxFile = IVFXFiles.load(shot.fileId);
                shot.ivfxShotsTypeSize = IVFXShotsTypeSize.load(shot.shotsTypeSizeId);
                shot.ivfxShotsTypePersons = IVFXShotsTypePersons.load(shot.shotsTypePersonId);
                shot.isBodyScene = rs.getBoolean("isBodyScene");
                shot.isStartScene = rs.getBoolean("isStartScene");
                shot.isEndScene = rs.getBoolean("isEndScene");
                shot.isBodyEvent = rs.getBoolean("isBodyEvent");
                shot.isStartEvent = rs.getBoolean("isStartEvent");
                shot.isEndEvent = rs.getBoolean("isEndEvent");

                if (withPreview) {

                    BufferedImage bufferedImage;
                    String fileName;
                    File file;

                    fileName = shot.getFirstFramePicture();
                    file = new File(fileName);
                    for (int i = 0; i < 3; i++) {
                        shot.labelFirst[i] = new Label();
                        shot.labelFirst[i].setMinSize(135, 75);
                        shot.labelFirst[i].setMaxSize(135, 75);
                        shot.labelFirst[i].setPrefSize(135, 75);
                    }

                    if (file.exists()) {
                        try {
                            bufferedImage = ImageIO.read(file);
                            for (int i = 0; i < 3; i++) {
                                BufferedImage bi = OverlayImage.setOverlayUnderlineText(bufferedImage, shot.getStart());
                                if (shot.isBodyScene) {
                                    bi = OverlayImage.setOverlayIsBodyScene(bi);
                                }
                                if (shot.isStartScene) {
                                    bi = OverlayImage.setOverlayIsStartScene(bi);
                                }
                                if (shot.isEndScene) {
                                    bi = OverlayImage.setOverlayIsEndScene(bi);
                                }
                                shot.imageViewFirst[i] = new ImageView(ConvertToFxImage.convertToFxImage(bi));
//                                shot.imageViewFirst[i] = new ImageView(ConvertToFxImage.convertToFxImage(OverlayImage.setOverlayUnderlineText(bufferedImage, shot.getStart())));
                                shot.labelFirst[i].setGraphic(shot.imageViewFirst[i]);
                                shot.labelFirst[i].setContentDisplay(ContentDisplay.TOP);
                            }


                        } catch (IOException e) {}

                    }

                    for (int i = 0; i < 3; i++) {
                        shot.labelLast[i] = new Label();
                        shot.labelLast[i].setMinSize(135, 75);
                        shot.labelLast[i].setMaxSize(135, 75);
                        shot.labelLast[i].setPrefSize(135, 75);
                    }

                    fileName = shot.getLastFramePicture();
                    file = new File(fileName);
                    if (file.exists()) {
                        try {
                            bufferedImage = ImageIO.read(file);
                            for (int i = 0; i < 3; i++) {
                                BufferedImage bi = OverlayImage.setOverlayUnderlineText(bufferedImage, shot.getEnd());
                                if (shot.isBodyEvent) {
                                    bi = OverlayImage.setOverlayIsBodyEvent(bi);
                                }
                                if (shot.isStartEvent) {
                                    bi = OverlayImage.setOverlayIsStartEvent(bi);
                                }
                                if (shot.isEndEvent) {
                                    bi = OverlayImage.setOverlayIsEndEvent(bi);
                                }
                                shot.imageViewLast[i] = new ImageView(ConvertToFxImage.convertToFxImage(bi));
//                                shot.imageViewLast[i] = new ImageView(ConvertToFxImage.convertToFxImage(OverlayImage.setOverlayUnderlineText(bufferedImage, shot.getEnd())));
                                shot.labelLast[i].setGraphic(shot.imageViewLast[i]);
                                shot.labelLast[i].setContentDisplay(ContentDisplay.TOP);
                            }
                        } catch (IOException e) {}

                    }

                }

                return shot;
            }

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

        return null;
    }


    public IVFXShots loadPrevious(boolean withPreview) {

        Statement statement = null;
        ResultSet rs = null;
        String sql;

        try {
            statement = Main.mainConnection.createStatement();

            sql = "SELECT tbl_shots.* FROM tbl_shots WHERE tbl_shots.lastFrameNumber < " + this.firstFrameNumber + " AND tbl_shots.file_id = " + this.fileId +
                    " ORDER BY tbl_shots.lastFrameNumber DESC LIMIT 1";
            rs = statement.executeQuery(sql);
            if (rs.next()) {
                IVFXShots shot = new IVFXShots();
                shot.id = rs.getInt("id");
                shot.fileId = rs.getInt("file_id");
                shot.shotsTypeSizeId = rs.getInt("shot_type_size_id");
                shot.shotsTypePersonId = rs.getInt("shot_type_person_id");
                shot.firstFrameNumber = rs.getInt("firstFrameNumber");
                shot.lastFrameNumber = rs.getInt("lastFrameNumber");
                shot.nearestIFrame = rs.getInt("nearestIFrame");
                shot.ivfxFile = IVFXFiles.load(shot.fileId);
                shot.ivfxShotsTypeSize = IVFXShotsTypeSize.load(shot.shotsTypeSizeId);
                shot.ivfxShotsTypePersons = IVFXShotsTypePersons.load(shot.shotsTypePersonId);

                if (withPreview) {

                    BufferedImage bufferedImage;
                    String fileName;
                    File file;

                    fileName = shot.getFirstFramePicture();
                    file = new File(fileName);
                    for (int i = 0; i < 3; i++) {
                        shot.labelFirst[i] = new Label();
                        shot.labelFirst[i].setMinSize(135, 75);
                        shot.labelFirst[i].setMaxSize(135, 75);
                        shot.labelFirst[i].setPrefSize(135, 75);
                    }

                    if (file.exists()) {
                        try {
                            bufferedImage = ImageIO.read(file);
                            for (int i = 0; i < 3; i++) {
                                BufferedImage bi = OverlayImage.setOverlayUnderlineText(bufferedImage, shot.getStart());
                                if (shot.isBodyScene) {
                                    bi = OverlayImage.setOverlayIsBodyScene(bi);
                                }
                                if (shot.isStartScene) {
                                    bi = OverlayImage.setOverlayIsStartScene(bi);
                                }
                                if (shot.isEndScene) {
                                    bi = OverlayImage.setOverlayIsEndScene(bi);
                                }
                                shot.imageViewFirst[i] = new ImageView(ConvertToFxImage.convertToFxImage(bi));
//                                shot.imageViewFirst[i] = new ImageView(ConvertToFxImage.convertToFxImage(OverlayImage.setOverlayUnderlineText(bufferedImage, shot.getStart())));
                                shot.labelFirst[i].setGraphic(shot.imageViewFirst[i]);
                                shot.labelFirst[i].setContentDisplay(ContentDisplay.TOP);
                            }


                        } catch (IOException e) {}

                    }

                    for (int i = 0; i < 3; i++) {
                        shot.labelLast[i] = new Label();
                        shot.labelLast[i].setMinSize(135, 75);
                        shot.labelLast[i].setMaxSize(135, 75);
                        shot.labelLast[i].setPrefSize(135, 75);
                    }

                    fileName = shot.getLastFramePicture();
                    file = new File(fileName);
                    if (file.exists()) {
                        try {
                            bufferedImage = ImageIO.read(file);
                            for (int i = 0; i < 3; i++) {
                                BufferedImage bi = OverlayImage.setOverlayUnderlineText(bufferedImage, shot.getEnd());
                                if (shot.isBodyEvent) {
                                    bi = OverlayImage.setOverlayIsBodyEvent(bi);
                                }
                                if (shot.isStartEvent) {
                                    bi = OverlayImage.setOverlayIsStartEvent(bi);
                                }
                                if (shot.isEndEvent) {
                                    bi = OverlayImage.setOverlayIsEndEvent(bi);
                                }
                                shot.imageViewLast[i] = new ImageView(ConvertToFxImage.convertToFxImage(bi));
//                                shot.imageViewLast[i] = new ImageView(ConvertToFxImage.convertToFxImage(OverlayImage.setOverlayUnderlineText(bufferedImage, shot.getEnd())));
                                shot.labelLast[i].setGraphic(shot.imageViewLast[i]);
                                shot.labelLast[i].setContentDisplay(ContentDisplay.TOP);
                            }
                        } catch (IOException e) {}

                    }

                }

                return shot;
            }

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

        return null;

    }

    public static List<IVFXShots> loadList(IVFXFiles ivfxFiles, boolean withPreview) {
        return loadList(ivfxFiles, withPreview, null);
    }
    public static List<IVFXShots> loadList(IVFXFiles ivfxFiles, boolean withPreview, ProgressBar progressBar) {

        List<IVFXShots> listShots = new ArrayList<>();

        int iProgress = 0;
        Statement statement = null;
        ResultSet rs = null;
        String sql;

        try {
            statement = Main.mainConnection.createStatement();

//            sql = "SELECT * FROM tbl_shots WHERE file_id = " + ivfxFiles.getId() + " ORDER BY firstFrameNumber";

            sql = "SELECT " +
                    "qry_shots_in_scenes_and_events.id, " +
                    "qry_shots_in_scenes_and_events.file_id, " +
                    "qry_shots_in_scenes_and_events.shot_type_size_id, " +
                    "qry_shots_in_scenes_and_events.shot_type_person_id, " +
                    "qry_shots_in_scenes_and_events.firstFrameNumber, " +
                    "qry_shots_in_scenes_and_events.lastFrameNumber, " +
                    "qry_shots_in_scenes_and_events.nearestIFrame, " +
                    "SUM(qry_shots_in_scenes_and_events.isBodyEvent) > 0 AS isBodyEvent, " +
                    "SUM(qry_shots_in_scenes_and_events.isStartEvent) > 0 AS isStartEvent, " +
                    "SUM(qry_shots_in_scenes_and_events.isEndEvent) > 0 AS isEndEvent, " +
                    "SUM(qry_shots_in_scenes_and_events.isBodyScene) > 0 AS isBodyScene, " +
                    "SUM(qry_shots_in_scenes_and_events.isStartScene) > 0 AS isStartScene, " +
                    "SUM(qry_shots_in_scenes_and_events.isEndScene) > 0 AS isEndScene " +
                    "FROM qry_shots_in_scenes_and_events " +
                    "WHERE qry_shots_in_scenes_and_events.file_id = " + ivfxFiles.getId() + " " +
                    "GROUP BY qry_shots_in_scenes_and_events.id, " +
                    "qry_shots_in_scenes_and_events.file_id, " +
                    "qry_shots_in_scenes_and_events.shot_type_size_id, " +
                    "qry_shots_in_scenes_and_events.shot_type_person_id, " +
                    "qry_shots_in_scenes_and_events.firstFrameNumber, " +
                    "qry_shots_in_scenes_and_events.lastFrameNumber, " +
                    "qry_shots_in_scenes_and_events.nearestIFrame";

            String sqlCnt = "SELECT COUNT(*) AS CNT FROM (" + sql + ") AS tmp";
            ResultSet rsCnt = null;
            rsCnt = statement.executeQuery(sqlCnt);
            rsCnt.next();
            int countRows = rsCnt.getInt("CNT");
            rsCnt.close();

            rs = statement.executeQuery(sql);

            while (rs.next()) {

                if (progressBar != null) progressBar.setProgress((double)++iProgress / countRows);

                IVFXShots shot = new IVFXShots();
                shot.id = rs.getInt("id");
                shot.fileId = rs.getInt("file_id");
                shot.shotsTypeSizeId = rs.getInt("shot_type_size_id");
                shot.shotsTypePersonId = rs.getInt("shot_type_person_id");
                shot.firstFrameNumber = rs.getInt("firstFrameNumber");
                shot.lastFrameNumber = rs.getInt("lastFrameNumber");
                shot.nearestIFrame = rs.getInt("nearestIFrame");
                shot.ivfxFile = ivfxFiles;
                shot.ivfxShotsTypeSize = IVFXShotsTypeSize.load(shot.shotsTypeSizeId);
                shot.ivfxShotsTypePersons = IVFXShotsTypePersons.load(shot.shotsTypePersonId);
                shot.isBodyScene = rs.getBoolean("isBodyScene");
                shot.isStartScene = rs.getBoolean("isStartScene");
                shot.isEndScene = rs.getBoolean("isEndScene");
                shot.isBodyEvent = rs.getBoolean("isBodyEvent");
                shot.isStartEvent = rs.getBoolean("isStartEvent");
                shot.isEndEvent = rs.getBoolean("isEndEvent");

                listShots.add(shot);

            }

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

        if (withPreview) {
            iProgress = 0;
            for (IVFXShots ivfxShots : listShots) {

                if (progressBar != null) progressBar.setProgress((double)++iProgress / listShots.size());

                BufferedImage bufferedImage;
                String fileName;
                File file;

                fileName = ivfxShots.getFirstFramePicture();
                file = new File(fileName);
                for (int i = 0; i < 3; i++) {
                    ivfxShots.labelFirst[i] = new Label();
                    ivfxShots.labelFirst[i].setMinSize(135, 75);
                    ivfxShots.labelFirst[i].setMaxSize(135, 75);
                    ivfxShots.labelFirst[i].setPrefSize(135, 75);
                }

                if (file.exists()) {
                    try {
                        bufferedImage = ImageIO.read(file);
                        for (int i = 0; i < 3; i++) {
                            BufferedImage bi = OverlayImage.setOverlayUnderlineText(bufferedImage, ivfxShots.getStart());
                            if (ivfxShots.isBodyScene) {
                                bi = OverlayImage.setOverlayIsBodyScene(bi);
                            }
                            if (ivfxShots.isStartScene) {
                                bi = OverlayImage.setOverlayIsStartScene(bi);
                            }
                            if (ivfxShots.isEndScene) {
                                bi = OverlayImage.setOverlayIsEndScene(bi);
                            }
                            ivfxShots.imageViewFirst[i] = new ImageView(ConvertToFxImage.convertToFxImage(bi));
//                            ivfxShots.imageViewFirst[i] = new ImageView(ConvertToFxImage.convertToFxImage(OverlayImage.setOverlayUnderlineText(bufferedImage, ivfxShots.getStart())));
                            ivfxShots.labelFirst[i].setGraphic(ivfxShots.imageViewFirst[i]);
                            ivfxShots.labelFirst[i].setContentDisplay(ContentDisplay.TOP);
                        }

                    } catch (IOException e) {}

                }

                for (int i = 0; i < 3; i++) {

                    ivfxShots.labelLast[i] = new Label();
                    ivfxShots.labelLast[i].setMinSize(135, 75);
                    ivfxShots.labelLast[i].setMaxSize(135, 75);
                    ivfxShots.labelLast[i].setPrefSize(135, 75);
                }
                fileName = ivfxShots.getLastFramePicture();
                file = new File(fileName);
                if (file.exists()) {
                    try {
                        bufferedImage = ImageIO.read(file);
                        for (int i = 0; i < 3; i++) {
                            BufferedImage bi = OverlayImage.setOverlayUnderlineText(bufferedImage, ivfxShots.getEnd());
                            if (ivfxShots.isBodyEvent) {
                                bi = OverlayImage.setOverlayIsBodyEvent(bi);
                            }
                            if (ivfxShots.isStartEvent) {
                                bi = OverlayImage.setOverlayIsStartEvent(bi);
                            }
                            if (ivfxShots.isEndEvent) {
                                bi = OverlayImage.setOverlayIsEndEvent(bi);
                            }
                            ivfxShots.imageViewLast[i] = new ImageView(ConvertToFxImage.convertToFxImage(bi));

//                            ivfxShots.imageViewLast[i] = new ImageView(ConvertToFxImage.convertToFxImage(OverlayImage.setOverlayUnderlineText(bufferedImage, ivfxShots.getEnd())));
                            ivfxShots.labelLast[i].setGraphic(ivfxShots.imageViewLast[i]);
                            ivfxShots.labelLast[i].setContentDisplay(ContentDisplay.TOP);
                        }
                    } catch (IOException e) {}

                }
            }
        }

        return listShots;
    }


// TODO SAVE


    public void save() {

        String sql = "UPDATE tbl_shots SET " +
                "file_id = ?, " +
                "shot_type_size_id = ?, " +
                "shot_type_person_id = ?, " +
                "firstFrameNumber = ?, " +
                "lastFrameNumber = ?, " +
                "nearestIFrame = ? " +
                "WHERE id = ?";
        try {
            PreparedStatement ps = Main.mainConnection.prepareStatement(sql);
            ps.setInt   (1, this.fileId);
            ps.setInt   (2, this.shotsTypeSizeId);
            ps.setInt   (3, this.shotsTypePersonId);
            ps.setInt   (4, this.firstFrameNumber);
            ps.setInt   (5, this.lastFrameNumber);
            ps.setInt   (6, this.nearestIFrame);
            ps.setInt   (7, this.id);
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



    // TODO DELETE

    public void delete() {
        String sql = "DELETE FROM tbl_shots WHERE id = ?";
        try {
            PreparedStatement ps = Main.mainConnection.prepareStatement(sql);
            ps.setInt   (1, this.id);
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // создает и возвращает список планов для видеофайла по фреймам
    public static List<IVFXShots> createListShotsByFrames(IVFXFiles ivfxFile) {
        List<IVFXShots> listShots = new ArrayList<>();
        List<IVFXShots> listShotsTemp = new ArrayList<>();
        List<IVFXShots> listShotsFinal = new ArrayList<>();
        List<IVFXFrames> listFrames = new ArrayList<>();


        listShotsTemp = IVFXShots.loadList(ivfxFile, true);
        listFrames = IVFXFrames.loadList(ivfxFile);

        // если список фреймов не пустой - продолжаем работу
        if (listFrames.size() != 0) {

            // сначала создаем список планов по списку фреймов

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

                    IVFXShots tempShot = new IVFXShots();

                    tempShot.fileId = ivfxFile.getId();
                    tempShot.firstFrameNumber = firstFrameNumber;
                    tempShot.lastFrameNumber = lastFrameNumber;
                    tempShot.nearestIFrame = previousIFrame;
                    tempShot.ivfxFile = ivfxFile;

                    listShots.add(tempShot);

                    firstFrameNumber = currentFrameNumber;
                    previousIFrame = currentIFrame;
                }
            }

            IVFXShots tempShot = new IVFXShots();

            tempShot.fileId = ivfxFile.getId();
            tempShot.firstFrameNumber = firstFrameNumber;
            tempShot.lastFrameNumber = currentFrameNumber;
            tempShot.nearestIFrame = previousIFrame;
            tempShot.ivfxFile = ivfxFile;

            listShots.add(tempShot);

            // теперь сравниваем получившийся список с временным и формируем финальный список

            // цикл по только что созданному списку планов
            for (IVFXShots ivfxShot : listShots) {
                boolean isFinded = false;
                //цикл по временному списку планов
                for (IVFXShots ivfxShotTemp : listShotsTemp) {
                    // если у планов совпадают начальные и конечные кадры
                    if (ivfxShot.firstFrameNumber == ivfxShotTemp.firstFrameNumber && ivfxShot.lastFrameNumber == ivfxShotTemp.lastFrameNumber) {
                        if (ivfxShot.nearestIFrame != ivfxShotTemp.nearestIFrame) {
                            ivfxShotTemp.nearestIFrame = ivfxShot.nearestIFrame;
                        }
                        isFinded = true;
                        listShotsFinal.add(ivfxShotTemp); // добаляем в финальный список план из временного списка
                        break;
                    }
                }
                if (!isFinded) {
                    listShotsFinal.add(ivfxShot); // добаляем в финальный список план из списка
                }
            }

        }

        return listShotsFinal;
    }



    public int getTagIdScene() {

        int id = 0;
        Statement statement = null;
        ResultSet rs = null;
        String sql;

        try {
            statement = Main.mainConnection.createStatement();

            sql = "SELECT tbl_tags_shots.tag_id FROM tbl_tags_shots INNER JOIN tbl_tags ON tbl_tags_shots.tag_id = tbl_tags.id WHERE tbl_tags.tag_type_id = 3 AND tbl_tags_shots.shot_id = " + this.id;
            rs = statement.executeQuery(sql);
            if (rs.next()) {
                id = rs.getInt("tag_id");
            }

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
        return id;
    }

    public int getTagIdEvent() {

        int id = 0;
        Statement statement = null;
        ResultSet rs = null;
        String sql;

        try {
            statement = Main.mainConnection.createStatement();

            sql = "SELECT tbl_tags_shots.tag_id FROM tbl_tags_shots INNER JOIN tbl_tags ON tbl_tags_shots.tag_id = tbl_tags.id WHERE tbl_tags.tag_type_id = 4 AND tbl_tags_shots.shot_id = " + this.id;
            rs = statement.executeQuery(sql);
            if (rs.next()) {
                id = rs.getInt("tag_id");
            }

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
        return id;
    }

    // возвращает сцену, в которой находится план
    public IVFXScenes getScene() {

        Statement statement = null;
        ResultSet rs = null;
        String sql;

        try {
            statement = Main.mainConnection.createStatement();

            sql = "SELECT * FROM tbl_scenes_shots WHERE shot_id = " + this.id;
            rs = statement.executeQuery(sql);
            if (rs.next()) {
                return IVFXScenes.load(rs.getInt("scene_id"));
            }

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

        return null;
    }

    // возвращает сцену, в которой находится план
    public IVFXEvents getEvent() {

        Statement statement = null;
        ResultSet rs = null;
        String sql;

        try {
            statement = Main.mainConnection.createStatement();

            sql = "SELECT * FROM tbl_events_shots WHERE shot_id = " + this.id;
            rs = statement.executeQuery(sql);
            if (rs.next()) {
                return IVFXEvents.load(rs.getInt("event_id"));
            }

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

        return null;
    }

    // Проверка, входит ли план в какое-то событие. Если да - на его основе нельзя создать новое событие
    public boolean isPresentInEvents() {

        Statement statement = null;
        ResultSet rs = null;
        String sql;
        int count = 0;

        try {
            statement = Main.mainConnection.createStatement();

            sql = "SELECT COUNT(*) FROM tbl_events_shots WHERE shot_id = " + this.id;
            rs = statement.executeQuery(sql);
            if (rs.next()) {
                count = rs.getInt(1);
            }
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

        return count != 0;

    }


    public String getShotVideoFileNameWihoutFolderX264() {
        return this.ivfxFile.getShortName() + "_[" +
                FFmpeg.convertDurationToString(FFmpeg.getDurationByFrameNumber(this.getFirstFrameNumber()-1,this.ivfxFile.getFrameRate())).replace(":",".") + "-" +
                FFmpeg.convertDurationToString(FFmpeg.getDurationByFrameNumber(this.getLastFrameNumber(),this.ivfxFile.getFrameRate())).replace(":",".") + "](" +
                this.getFirstFrameNumber() + "-" +
                this.getLastFrameNumber() + ")" +
                ".mp4";
    }

    public String getShotVideoFileNameX264() {
        return this.ivfxFile.getIvfxProject().getFolder() + "\\Video\\1920x1080\\" + this.ivfxFile.getShortName() + "_[" +
                FFmpeg.convertDurationToString(FFmpeg.getDurationByFrameNumber(this.getFirstFrameNumber()-1,this.ivfxFile.getFrameRate())).replace(":",".") + "-" +
                FFmpeg.convertDurationToString(FFmpeg.getDurationByFrameNumber(this.getLastFrameNumber(),this.ivfxFile.getFrameRate())).replace(":",".") + "](" +
                this.getFirstFrameNumber() + "-" +
                this.getLastFrameNumber() + ")" +
                ".mp4";
    }

    public List<IVFXPersons> getListPersons() {
        List<IVFXShotsPersons> listShotPersons = IVFXShotsPersons.loadList(this, false);
        List<IVFXPersons> listPersons = new ArrayList<>();
        for (IVFXShotsPersons shotPerson : listShotPersons) {
            listPersons.add(shotPerson.getIvfxPerson());
        }
        return listPersons;
    }








    // TODO GETTERS SETTERS

    public String getTitle() {
        return this.ivfxFile.getTitle();
    }

    public int getDuration() {
        return FFmpeg.getDurationByFrameNumber(this.getLastFrameNumber()-this.getFirstFrameNumber()+1, this.ivfxFile.getFrameRate());
    }

    public String getStart() {
        return FFmpeg.convertDurationToString(FFmpeg.getDurationByFrameNumber(this.firstFrameNumber-1,this.ivfxFile.getFrameRate()));
    }

    public String getEnd() {
        return FFmpeg.convertDurationToString(FFmpeg.getDurationByFrameNumber(this.getLastFrameNumber(),this.ivfxFile.getFrameRate()));
    }

    public IVFXFiles getIvfxFile() {
        return ivfxFile;
    }

    public void setIvfxFile(IVFXFiles ivfxFile) {
        this.ivfxFile = ivfxFile;
    }

    public int getFirstFrameNumber() {
        return firstFrameNumber;
    }

    public void setFirstFrameNumber(int firstFrameNumber) {
        this.firstFrameNumber = firstFrameNumber;
    }

    public int getLastFrameNumber() {
        return lastFrameNumber;
    }

    public void setLastFrameNumber(int lastFrameNumber) {
        this.lastFrameNumber = lastFrameNumber;
    }

    public String getFirstFramePicture() {
        return this.ivfxFile.getFramesFolderPreview()+"\\"+this.ivfxFile.getShortName()+this.ivfxFile.FRAMES_PREFIX+String.format("%06d", this.firstFrameNumber)+".jpg";
    }

    public String getLastFramePicture() {
        return this.ivfxFile.getFramesFolderPreview()+"\\"+this.ivfxFile.getShortName()+this.ivfxFile.FRAMES_PREFIX+String.format("%06d", this.lastFrameNumber)+".jpg";
    }

    public ImageView[] getImageViewFirst() {
        return imageViewFirst;
    }

    public ImageView getImageViewFirst(int i) {
        return imageViewFirst[i];
    }

    public ImageView getImageViewFirst1() {
        return imageViewFirst[0];
    }

    public ImageView getImageViewFirst2() {
        return imageViewFirst[1];
    }
    public ImageView getImageViewFirst3() {
        return imageViewFirst[2];
    }

    public void setImageViewFirst(ImageView[] imageViewFirst) {
        this.imageViewFirst = imageViewFirst;
    }

    public void setImageViewFirst(ImageView imageViewFirst, int i) {
        this.imageViewFirst[i] = imageViewFirst;
    }

    public ImageView getImageViewLast(int i) {
        return imageViewLast[i];
    }

    public ImageView[] getImageViewLast() {
        return imageViewLast;
    }

    public ImageView getImageViewLast1() {
        return imageViewLast[0];
    }

    public ImageView getImageViewLast2() {
        return imageViewLast[1];
    }

    public ImageView getImageViewLast3() {
        return imageViewLast[2];
    }

    public void setImageViewLast(ImageView[] imageViewLast) {
        this.imageViewLast = imageViewLast;
    }

    public void setImageViewLast(ImageView imageViewLast, int i) {
        this.imageViewLast[i] = imageViewLast;
    }

    public int getNearestIFrame() {
        return nearestIFrame;
    }

    public void setNearestIFrame(int nearestIFrame) {
        this.nearestIFrame = nearestIFrame;
    }

    public Label getLabelFirst(int i) {
        return labelFirst[i];
    }

    public Label[] getLabelFirst() {
        return labelFirst;
    }

    public Label getLabelFirst1() {
        return labelFirst[0];
    }

    public Label getLabelFirst2() {
        return labelFirst[1];
    }

    public Label getLabelFirst3() {
        return labelFirst[2];
    }

    public void setLabelFirst(Label[] labelFirst) {
        this.labelFirst = labelFirst;
    }

    public void setLabelFirst(Label labelFirst, int i) {
        this.labelFirst[i] = labelFirst;
    }

    public Label[] getLabelLast() {
        return labelLast;
    }

    public Label getLabelLast(int i) {
        return labelLast[i];
    }

    public Label getLabelLast1() {
        return labelLast[0];
    }

    public Label getLabelLast2() {
        return labelLast[1];
    }

    public Label getLabelLast3() {
        return labelLast[2];
    }

    public void setLabelLast(Label[] labelLast) {
        this.labelLast = labelLast;
    }

    public void setLabelLast(Label labelLast, int i) {
        this.labelLast[i] = labelLast;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getFileId() {
        return fileId;
    }

    public void setFileId(int fileId) {
        this.fileId = fileId;
    }

    public int getShotsTypeSizeId() {
        return shotsTypeSizeId;
    }

    public void setShotsTypeSizeId(int shotsTypeSizeId) {
        this.shotsTypeSizeId = shotsTypeSizeId;
    }

    public int getShotsTypePersonId() {
        return shotsTypePersonId;
    }

    public void setShotsTypePersonId(int shotsTypePersonId) {
        this.shotsTypePersonId = shotsTypePersonId;
    }

    public IVFXShotsTypeSize getIvfxShotsTypeSize() {
        return ivfxShotsTypeSize;
    }

    public void setIvfxShotsTypeSize(IVFXShotsTypeSize ivfxShotsTypeSize) {
        this.ivfxShotsTypeSize = ivfxShotsTypeSize;
    }

    public IVFXShotsTypePersons getIvfxShotsTypePersons() {
        return ivfxShotsTypePersons;
    }

    public void setIvfxShotsTypePersons(IVFXShotsTypePersons ivfxShotsTypePersons) {
        this.ivfxShotsTypePersons = ivfxShotsTypePersons;
    }

    public boolean isBodyScene() {
        return isBodyScene;
    }

    public boolean isStartScene() {
        return isStartScene;
    }

    public boolean isEndScene() {
        return isEndScene;
    }

    public boolean isBodyEvent() {
        return isBodyEvent;
    }

    public boolean isStartEvent() {
        return isStartEvent;
    }

    public boolean isEndEvent() {
        return isEndEvent;
    }
}

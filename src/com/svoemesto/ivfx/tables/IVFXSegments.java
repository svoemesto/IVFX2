package com.svoemesto.ivfx.tables;

import com.svoemesto.ivfx.Main;
import com.svoemesto.ivfx.controllers.ProgressController;
import com.svoemesto.ivfx.utils.ConvertToFxImage;
import com.svoemesto.ivfx.utils.FFmpeg;
import com.svoemesto.ivfx.utils.OverlayImage;
import javafx.application.Platform;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import com.svoemesto.ivfx.controllers.PersonController;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class IVFXSegments implements Comparable<IVFXSegments>  {

    private int id;
    private int fileId;
    private UUID uuid = UUID.randomUUID();;   // UUID
    private UUID fileUuid;
    private IVFXFiles ivfxFile;
    private int firstFrameNumber;
    private int lastFrameNumber;
    private int nearestIFrame;
    private transient ImageView imageViewFirst;
    private transient ImageView imageViewLast;
    private transient Label labelFirst;
    private transient Label labelLast;

//TODO ISEQUAL

    public boolean isEqual(IVFXSegments o) {
        return (this.id == o.id &&
                this.fileId == o.fileId &&
                this.uuid.equals(o.uuid) &&
                this.fileUuid.equals(o.fileUuid) &&
                this.firstFrameNumber == o.firstFrameNumber &&
                this.lastFrameNumber == o.lastFrameNumber &&
                this.nearestIFrame == o.nearestIFrame);
    }

    // TODO OVERRIDE

    @Override
    public int compareTo(IVFXSegments o) {
        Integer a = (Integer)getFirstFrameNumber();
        Integer b = (Integer)o.getFirstFrameNumber();
        return a.compareTo(b);
    }

// TODO КОНСТРУКТОРЫ

    // пустой конструктор
    public IVFXSegments() {
    }

    public static IVFXSegments getNewDbInstance(IVFXFiles file) {

        IVFXSegments ivfxSegment = new IVFXSegments();

        Statement statement = null;
        ResultSet rs = null;
        String sql;

        ivfxSegment.fileId = file.getId();
        ivfxSegment.fileUuid = file.getUuid();
        ivfxSegment.ivfxFile = file;

        try {

            sql = "INSERT INTO tbl_segments (" +
                    "file_id, " +
                    "firstFrameNumber, " +
                    "lastFrameNumber, " +
                    "nearestIFrame, " +
                    "uuid, " +
                    "file_uuid) " +
                    "VALUES(" +
                    ivfxSegment.fileId + "," +
                    ivfxSegment.firstFrameNumber + "," +
                    ivfxSegment.lastFrameNumber + "," +
                    ivfxSegment.nearestIFrame + "," +
                    "'" + ivfxSegment.uuid.toString() + "'" + "," +
                    "'" + ivfxSegment.fileUuid.toString() + "'" + ")";

            PreparedStatement ps = Main.mainConnection.prepareStatement(sql);
            ps.executeUpdate(sql, Statement.RETURN_GENERATED_KEYS);
            rs = ps.getGeneratedKeys();
            if (rs.next()) {
                ivfxSegment.id = rs.getInt(1);
                System.out.println("Создана запись для сегмента " + ivfxSegment.firstFrameNumber + "-" + ivfxSegment.lastFrameNumber + ", файл «" + ivfxSegment.ivfxFile.getTitle() + "» " + ivfxSegment.uuid + " с идентификатором " + rs.getInt(1));
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

        return ivfxSegment;

    }

    public static IVFXSegments loadById(int id, boolean withPreview) {

        Statement statement = null;
        ResultSet rs = null;
        String sql;

        try {
            statement = Main.mainConnection.createStatement();

            sql = "SELECT * FROM tbl_segments WHERE id = " + id;
            rs = statement.executeQuery(sql);
            if (rs.next()) {
                IVFXSegments segment = new IVFXSegments();
                segment.id = rs.getInt("id");
                segment.fileId = rs.getInt("file_id");
                segment.firstFrameNumber = rs.getInt("firstFrameNumber");
                segment.lastFrameNumber = rs.getInt("lastFrameNumber");
                segment.nearestIFrame = rs.getInt("nearestIFrame");
                segment.uuid = UUID.fromString(rs.getString("uuid"));
                segment.fileUuid = UUID.fromString(rs.getString("file_uuid"));
                segment.ivfxFile = IVFXFiles.loadById(segment.fileId);

                if (withPreview) {

                    BufferedImage bufferedImage;
                    String fileName;
                    File file;

                    fileName = segment.getFirstFramePicture();
                    file = new File(fileName);
                    segment.labelFirst = new Label();
                    segment.labelFirst.setMinSize(135, 75);
                    segment.labelFirst.setMaxSize(135, 75);
                    segment.labelFirst.setPrefSize(135, 75);
                    if (file.exists()) {
                        try {
                            bufferedImage = ImageIO.read(file);
                            segment.imageViewFirst = new ImageView(ConvertToFxImage.convertToFxImage(OverlayImage.setOverlayUnderlineText(bufferedImage, segment.getStart())));
                            segment.labelFirst.setGraphic(segment.imageViewFirst);
                            segment.labelFirst.setContentDisplay(ContentDisplay.TOP);

                        } catch (IOException e) {}

                    }

                    segment.labelLast = new Label();
                    segment.labelLast.setMinSize(135, 75);
                    segment.labelLast.setMaxSize(135, 75);
                    segment.labelLast.setPrefSize(135, 75);
                    fileName = segment.getLastFramePicture();
                    file = new File(fileName);
                    if (file.exists()) {
                        try {
                            bufferedImage = ImageIO.read(file);
                            segment.imageViewLast = new ImageView(ConvertToFxImage.convertToFxImage(OverlayImage.setOverlayUnderlineText(bufferedImage, segment.getEnd())));
                            segment.labelLast.setGraphic(segment.imageViewLast);
                            segment.labelLast.setContentDisplay(ContentDisplay.TOP);
                        } catch (IOException e) {}

                    }

                }

                return segment;
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

    public static List<IVFXSegments> loadList(IVFXFiles ivfxFiles, boolean withPreview) {


        List<IVFXSegments> listSegments = new ArrayList<>();

        Statement statement = null;
        ResultSet rs = null, rsCnt = null;
        String sql;

        try {
            statement = Main.mainConnection.createStatement();

            sql = "SELECT * FROM tbl_segments WHERE file_id = " + ivfxFiles.getId() + " ORDER BY firstFrameNumber";

            String sqlCnt = "SELECT COUNT(*) AS CNT FROM (" + sql + ") AS tmp";
            rsCnt = statement.executeQuery(sqlCnt);
            rsCnt.next();
            int countRows = rsCnt.getInt("CNT");
            rsCnt.close();

            ProgressController progressController = new ProgressController("Загрузка сегментов файла «" + ivfxFiles.getTitle() + "»...", countRows);


            rs = statement.executeQuery(sql);

            while (rs.next()) {
                IVFXSegments segment = new IVFXSegments();
                segment.id = rs.getInt("id");
                segment.fileId = rs.getInt("file_id");
                segment.firstFrameNumber = rs.getInt("firstFrameNumber");
                segment.lastFrameNumber = rs.getInt("lastFrameNumber");
                segment.nearestIFrame = rs.getInt("nearestIFrame");
                segment.uuid = UUID.fromString(rs.getString("uuid"));
                segment.fileUuid = UUID.fromString(rs.getString("file_uuid"));
                segment.ivfxFile = ivfxFiles;

                listSegments.add(segment);
                progressController.progress(null);


            }

            progressController.close();

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
            for (IVFXSegments ivfxSegments : listSegments) {
                BufferedImage bufferedImage;
                String fileName;
                File file;

                fileName = ivfxSegments.getFirstFramePicture();
                file = new File(fileName);
                ivfxSegments.labelFirst = new Label();
                ivfxSegments.labelFirst.setMinSize(135, 75);
                ivfxSegments.labelFirst.setMaxSize(135, 75);
                ivfxSegments.labelFirst.setPrefSize(135, 75);
                if (file.exists()) {
                    try {
                        bufferedImage = ImageIO.read(file);
                        ivfxSegments.imageViewFirst = new ImageView(ConvertToFxImage.convertToFxImage(OverlayImage.setOverlayUnderlineText(bufferedImage, ivfxSegments.getStart())));
                        ivfxSegments.labelFirst.setGraphic(ivfxSegments.imageViewFirst);
                        ivfxSegments.labelFirst.setContentDisplay(ContentDisplay.TOP);

                    } catch (IOException e) {}

                }

                ivfxSegments.labelLast = new Label();
                ivfxSegments.labelLast.setMinSize(135, 75);
                ivfxSegments.labelLast.setMaxSize(135, 75);
                ivfxSegments.labelLast.setPrefSize(135, 75);
                fileName = ivfxSegments.getLastFramePicture();
                file = new File(fileName);
                if (file.exists()) {
                    try {
                        bufferedImage = ImageIO.read(file);
                        ivfxSegments.imageViewLast = new ImageView(ConvertToFxImage.convertToFxImage(OverlayImage.setOverlayUnderlineText(bufferedImage, ivfxSegments.getEnd())));
                        ivfxSegments.labelLast.setGraphic(ivfxSegments.imageViewLast);
                        ivfxSegments.labelLast.setContentDisplay(ContentDisplay.TOP);
                    } catch (IOException e) {}

                }
            }
        }

        return listSegments;
    }

// TODO SAVE


    public void save() {

        String sql = "UPDATE tbl_segments SET " +
                "file_id = ?, " +
                "firstFrameNumber = ?, " +
                "lastFrameNumber = ?, " +
                "nearestIFrame = ?, " +
                "uuid = ?, " +
                "file_uuid = ? " +
                "WHERE id = ?";
        try {
            PreparedStatement ps = Main.mainConnection.prepareStatement(sql);
            ps.setInt   (1, this.fileId);
            ps.setInt   (2, this.firstFrameNumber);
            ps.setInt   (3, this.lastFrameNumber);
            ps.setInt   (4, this.nearestIFrame);
            ps.setString(5, this.uuid.toString());
            ps.setString(6, this.fileUuid.toString());
            ps.setInt   (7, this.id);
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



    // TODO DELETE

    public void delete() {
        String sql = "DELETE FROM tbl_segments WHERE id = ?";
        try {
            PreparedStatement ps = Main.mainConnection.prepareStatement(sql);
            ps.setInt   (1, this.id);
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // создает и возвращает список сегментов для видеофайла по фреймам
    public static List<IVFXSegments> createListSegmentsByFrames(IVFXFiles ivfxFile) {
        List<IVFXSegments> listSegments = new ArrayList<>();
        List<IVFXSegments> listSegmentsTemp = new ArrayList<>();
        List<IVFXSegments> listSegmentsFinal = new ArrayList<>();
        List<IVFXFrames> listFrames = new ArrayList<>();


        listSegmentsTemp = IVFXSegments.loadList(ivfxFile, true);
        listFrames = IVFXFrames.loadList(ivfxFile);

        // если список фреймов не пустой - продолжаем работу
        if (listFrames.size() != 0) {

            // сначала создаем список сегментов по списку фреймов

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

                    IVFXSegments tempSegment = new IVFXSegments();

                    tempSegment.fileId = ivfxFile.getId();
                    tempSegment.fileUuid = ivfxFile.getUuid();
                    tempSegment.firstFrameNumber = firstFrameNumber;
                    tempSegment.lastFrameNumber = lastFrameNumber;
                    tempSegment.nearestIFrame = previousIFrame;
                    tempSegment.ivfxFile = ivfxFile;

                    listSegments.add(tempSegment);

                    firstFrameNumber = currentFrameNumber;
                    previousIFrame = currentIFrame;
                }
            }

            IVFXSegments tempSegment = new IVFXSegments();

            tempSegment.fileId = ivfxFile.getId();
            tempSegment.fileUuid = ivfxFile.getUuid();
            tempSegment.firstFrameNumber = firstFrameNumber;
            tempSegment.lastFrameNumber = currentFrameNumber;
            tempSegment.nearestIFrame = previousIFrame;
            tempSegment.ivfxFile = ivfxFile;

            listSegments.add(tempSegment);

            // теперь сравниваем получившийся список с временным и формируем финальный список

            // цикл по только что созданному списку сегментов
            for (IVFXSegments ivfxSegment : listSegments) {
                boolean isFinded = false;
                //цикл по временному списку сегментов
                for (IVFXSegments ivfxSegmentTemp : listSegmentsTemp) {
                    // если у сегментов совпадают начальные и конечные кадры
                    if (ivfxSegment.firstFrameNumber == ivfxSegmentTemp.firstFrameNumber && ivfxSegment.lastFrameNumber == ivfxSegmentTemp.lastFrameNumber) {
                        if (ivfxSegment.nearestIFrame != ivfxSegmentTemp.nearestIFrame) {
                            ivfxSegmentTemp.nearestIFrame = ivfxSegment.nearestIFrame;
                        }
                        isFinded = true;
                        listSegmentsFinal.add(ivfxSegmentTemp); // добаляем в финальный список сегмент из временного списка
                        break;
                    }
                }
                if (!isFinded) {
                    listSegmentsFinal.add(ivfxSegment); // добаляем в финальный список сегмент из списка
                }
            }

        }

        return listSegmentsFinal;
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

    public UUID getFileUuid() {
        return fileUuid;
    }

    public void setFileUuid(UUID fileUuid) {
        this.fileUuid = fileUuid;
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

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public String getFirstFramePicture() {
        return this.ivfxFile.getFramesFolderPreview()+"\\"+this.ivfxFile.getShortName()+this.ivfxFile.FRAMES_PREFIX+String.format("%06d", this.firstFrameNumber)+".jpg";
    }

    public String getLastFramePicture() {
        return this.ivfxFile.getFramesFolderPreview()+"\\"+this.ivfxFile.getShortName()+this.ivfxFile.FRAMES_PREFIX+String.format("%06d", this.lastFrameNumber)+".jpg";
    }

    public ImageView getImageViewFirst() {
        return imageViewFirst;
    }

    public void setImageViewFirst(ImageView imageViewFirst) {
        this.imageViewFirst = imageViewFirst;
    }

    public ImageView getImageViewLast() {
        return imageViewLast;
    }

    public void setImageViewLast(ImageView imageViewLast) {
        this.imageViewLast = imageViewLast;
    }

    public int getNearestIFrame() {
        return nearestIFrame;
    }

    public void setNearestIFrame(int nearestIFrame) {
        this.nearestIFrame = nearestIFrame;
    }

    public Label getLabelFirst() {
        return labelFirst;
    }

    public void setLabelFirst(Label labelFirst) {
        this.labelFirst = labelFirst;
    }

    public Label getLabelLast() {
        return labelLast;
    }

    public void setLabelLast(Label labelLast) {
        this.labelLast = labelLast;
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

}

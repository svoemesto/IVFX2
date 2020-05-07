package com.svoemesto.ivfx.tables;

import com.svoemesto.ivfx.Main;
import com.svoemesto.ivfx.utils.ConvertToFxImage;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import org.apache.commons.io.FileUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class IVFXFrames {
    public static transient final String FRAMES_SUFFIX = "_Frames";
    public static transient final String FRAME_FULLSIZE_STUB = "blank_frame_400p.jpg";
    public static transient final String FRAME_PREVIEW_STUB = "blank_frame.jpg";

    private int id;
    private int fileId;
    private UUID uuid  = UUID.randomUUID();
    private UUID fileUuid;
    private IVFXFiles ivfxFile;
    private int frameNumber;
    private boolean isIFrame=false;
    private boolean isFind=false;
    private boolean isManualAdd=false;
    private boolean isManualCancel=false;
    private boolean isFinalFind=false;
    private double simScoreNext1=0.0;
    private double simScoreNext2=0.0;
    private double simScoreNext3=0.0;
    private double simScorePrev1=0.0;
    private double simScorePrev2=0.0;
    private double simScorePrev3=0.0;
    private double diffNext1=0.0;
    private double diffNext2=0.0;
    private double diffPrev1=0.0;
    private double diffPrev2=0.0;

    private transient ImageView preview;
    private transient Label label;

    //TODO ISEQUAL

    public boolean isEqual(IVFXFrames o) {
        return (this.id == o.id &&
                this.fileId == o.fileId &&
                this.uuid.equals(o.uuid) &&
                this.fileUuid.equals(o.fileUuid) &&
                this.frameNumber == o.frameNumber &&
                this.isIFrame == o.isIFrame &&
                this.isFind == o.isFind &&
                this.isManualAdd == o.isManualAdd &&
                this.isManualCancel == o.isManualCancel &&
                this.isFinalFind == o.isFinalFind &&
                this.simScoreNext1 == o.simScoreNext1 &&
                this.simScoreNext2 == o.simScoreNext2 &&
                this.simScoreNext3 == o.simScoreNext3 &&
                this.simScorePrev1 == o.simScorePrev1 &&
                this.simScorePrev2 == o.simScorePrev2 &&
                this.simScorePrev3 == o.simScorePrev3 &&
                this.diffNext1 == o.diffNext1 &&
                this.diffNext2 == o.diffNext2 &&
                this.diffPrev1 == o.diffPrev1 &&
                this.diffPrev2 == o.diffPrev2);
    }

// TODO КОНСТРУКТОРЫ

    // пустой конструктор
    public IVFXFrames() {
    }


    public static IVFXFrames getNewDbInstance(IVFXFiles file, int frameNumber) {
        IVFXFrames frame = new IVFXFrames();
        frame.frameNumber = frameNumber;
        frame.ivfxFile = file;
        frame.fileId = file.getId();
        frame.fileUuid = file.getUuid();

        Statement statement = null;
        ResultSet rs = null;
        String sql;

        try {

            sql = "INSERT INTO tbl_frames (" +
                    "file_id, " +
                    "frameNumber, " +
                    "isIFrame, " +
                    "isFind, " +
                    "isManualAdd, " +
                    "isManualCancel, " +
                    "isFinalFind, " +
                    "simScoreNext1, " +
                    "simScoreNext2, " +
                    "simScoreNext3, " +
                    "simScorePrev1, " +
                    "simScorePrev2, " +
                    "simScorePrev3, " +
                    "diffNext1, " +
                    "diffNext2, " +
                    "diffPrev1, " +
                    "diffPrev2, " +
                    "uuid, " +
                    "file_uuid) " +
                    "VALUES(" +
                    frame.fileId + "," +
                    frame.frameNumber + "," +
                    (frame.isIFrame ? 1 : 0) + "," +
                    (frame.isFind ? 1 : 0) + "," +
                    (frame.isManualAdd ? 1 : 0) + "," +
                    (frame.isManualCancel ? 1 : 0) + "," +
                    (frame.isFinalFind ? 1 : 0) + "," +
                    frame.simScoreNext1 + "," +
                    frame.simScoreNext2 + "," +
                    frame.simScoreNext3 + "," +
                    frame.simScorePrev1 + "," +
                    frame.simScorePrev2 + "," +
                    frame.simScorePrev3 + "," +
                    frame.diffNext1 + "," +
                    frame.diffNext2 + "," +
                    frame.diffPrev1 + "," +
                    frame.diffPrev2 + "," +
                    "'" + frame.uuid.toString() + "'" + "," +
                    "'" + frame.fileUuid.toString() + "'" + ")";

            PreparedStatement ps = Main.mainConnection.prepareStatement(sql);
            ps.executeUpdate(sql, Statement.RETURN_GENERATED_KEYS);
            rs = ps.getGeneratedKeys();
            if (rs.next()) {
                frame.id = rs.getInt(1);
                System.out.println("Создана запись для фрейма № " + frame.frameNumber + ", файл «" + frame.ivfxFile.getTitle() + "» " + frame.uuid + " с идентификатором " + rs.getInt(1));
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

        return frame;
    }
// TODO LOAD

    public static IVFXFrames loadByUuid(UUID frameUuid) {
        return loadByUuid(frameUuid, false);
    }

    public static IVFXFrames loadByUuid(UUID frameUuid, boolean withPreview) {
        Statement statement = null;
        ResultSet rs = null;
        String sql;

        try {
            statement = Main.mainConnection.createStatement();

            sql = "SELECT * FROM tbl_frames WHERE uuid = '" + frameUuid.toString() + "'";
            rs = statement.executeQuery(sql);
            if (rs.next()) {
                IVFXFrames frame = new IVFXFrames();
                frame.id = rs.getInt("id");
                frame.fileId = rs.getInt("file_id");
                frame.frameNumber = rs.getInt("frameNumber");
                frame.isIFrame = rs.getBoolean("isIFrame");
                frame.isFind = rs.getBoolean("isFind");
                frame.isManualAdd = rs.getBoolean("isManualAdd");
                frame.isManualCancel = rs.getBoolean("isManualCancel");
                frame.isFinalFind = rs.getBoolean("isFinalFind");
                frame.simScoreNext1 = rs.getDouble("simScoreNext1");
                frame.simScoreNext2 = rs.getDouble("simScoreNext2");
                frame.simScoreNext3 = rs.getDouble("simScoreNext3");
                frame.simScorePrev1 = rs.getDouble("simScorePrev1");
                frame.simScorePrev2 = rs.getDouble("simScorePrev2");
                frame.simScorePrev3 = rs.getDouble("simScorePrev3");
                frame.diffNext1 = rs.getDouble("diffNext1");
                frame.diffNext2 = rs.getDouble("diffNext2");
                frame.diffPrev1 = rs.getDouble("diffPrev1");
                frame.diffPrev2 = rs.getDouble("diffPrev2");
                frame.uuid = UUID.fromString(rs.getString("uuid"));
                frame.fileUuid = UUID.fromString(rs.getString("file_uuid"));
                frame.ivfxFile = IVFXFiles.loadById(frame.fileId);

                if (withPreview) {
                    String fileName = frame.getFileNamePreview();
                    File file = new File(fileName);
                    frame.label = new Label(String.valueOf(frame.frameNumber));
                    frame.label.setPrefWidth(135);
                    if (!file.exists()) {
                        fileName = frame.getFileNamePreviewStub();
                        file = new File(fileName);
                    }
                    try {
                        BufferedImage bufferedImage = ImageIO.read(file);
                        frame.preview = new ImageView(ConvertToFxImage.convertToFxImage(bufferedImage));
                        frame.label.setGraphic(frame.preview);
                        frame.label.setContentDisplay(ContentDisplay.TOP);
                    } catch (IOException e) {}
                }

                return frame;
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

    public static IVFXFrames loadById(int id) {
        return loadById(id, false);
    }

    public static IVFXFrames loadById(int id, boolean withPreview) {
        Statement statement = null;
        ResultSet rs = null;
        String sql;

        try {
            statement = Main.mainConnection.createStatement();

            sql = "SELECT * FROM tbl_frames WHERE id = " + id;
            rs = statement.executeQuery(sql);
            if (rs.next()) {
                IVFXFrames frame = new IVFXFrames();
                frame.id = rs.getInt("id");
                frame.fileId = rs.getInt("file_id");
                frame.frameNumber = rs.getInt("frameNumber");
                frame.isIFrame = rs.getBoolean("isIFrame");
                frame.isFind = rs.getBoolean("isFind");
                frame.isManualAdd = rs.getBoolean("isManualAdd");
                frame.isManualCancel = rs.getBoolean("isManualCancel");
                frame.isFinalFind = rs.getBoolean("isFinalFind");
                frame.simScoreNext1 = rs.getDouble("simScoreNext1");
                frame.simScoreNext2 = rs.getDouble("simScoreNext2");
                frame.simScoreNext3 = rs.getDouble("simScoreNext3");
                frame.simScorePrev1 = rs.getDouble("simScorePrev1");
                frame.simScorePrev2 = rs.getDouble("simScorePrev2");
                frame.simScorePrev3 = rs.getDouble("simScorePrev3");
                frame.diffNext1 = rs.getDouble("diffNext1");
                frame.diffNext2 = rs.getDouble("diffNext2");
                frame.diffPrev1 = rs.getDouble("diffPrev1");
                frame.diffPrev2 = rs.getDouble("diffPrev2");
                frame.uuid = UUID.fromString(rs.getString("uuid"));
                frame.fileUuid = UUID.fromString(rs.getString("file_uuid"));
                frame.ivfxFile = IVFXFiles.loadById(frame.fileId);

                if (withPreview) {
                    String fileName = frame.getFileNamePreview();
                    File file = new File(fileName);
                    frame.label = new Label(String.valueOf(frame.frameNumber));
                    frame.label.setPrefWidth(135);
                    if (!file.exists()) {
                        fileName = frame.getFileNamePreviewStub();
                        file = new File(fileName);
                    }
                    try {
                        BufferedImage bufferedImage = ImageIO.read(file);
                        frame.preview = new ImageView(ConvertToFxImage.convertToFxImage(bufferedImage));
                        frame.label.setGraphic(frame.preview);
                        frame.label.setContentDisplay(ContentDisplay.TOP);
                    } catch (IOException e) {}
                }

                return frame;
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

    public static List<IVFXFrames> loadList(IVFXFiles ivfxFile) {
        return loadList(ivfxFile, false);
    }

    public static List<IVFXFrames> loadList(IVFXFiles ivfxFile, boolean withPreview) {
        List<IVFXFrames> listFrames = new ArrayList<>();

        Statement statement = null;
        ResultSet rs = null;
        String sql;

        try {
            statement = Main.mainConnection.createStatement();

            sql = "SELECT * FROM tbl_frames WHERE file_id = " + ivfxFile.getId() + " ORDER BY frameNumber";
            rs = statement.executeQuery(sql);
            while (rs.next()) {

                IVFXFrames frame = new IVFXFrames();
                frame.id = rs.getInt("id");
                frame.fileId = rs.getInt("file_id");
                frame.frameNumber = rs.getInt("frameNumber");
                frame.isIFrame = rs.getBoolean("isIFrame");
                frame.isFind = rs.getBoolean("isFind");
                frame.isManualAdd = rs.getBoolean("isManualAdd");
                frame.isManualCancel = rs.getBoolean("isManualCancel");
                frame.isFinalFind = rs.getBoolean("isFinalFind");
                frame.simScoreNext1 = rs.getDouble("simScoreNext1");
                frame.simScoreNext2 = rs.getDouble("simScoreNext2");
                frame.simScoreNext3 = rs.getDouble("simScoreNext3");
                frame.simScorePrev1 = rs.getDouble("simScorePrev1");
                frame.simScorePrev2 = rs.getDouble("simScorePrev2");
                frame.simScorePrev3 = rs.getDouble("simScorePrev3");
                frame.diffNext1 = rs.getDouble("diffNext1");
                frame.diffNext2 = rs.getDouble("diffNext2");
                frame.diffPrev1 = rs.getDouble("diffPrev1");
                frame.diffPrev2 = rs.getDouble("diffPrev2");
                frame.uuid = UUID.fromString(rs.getString("uuid"));
                frame.fileUuid = UUID.fromString(rs.getString("file_uuid"));
                frame.ivfxFile = ivfxFile;

                if (withPreview) {
                    String fileName = frame.getFileNamePreview();
                    File file = new File(fileName);
                    frame.label = new Label(String.valueOf(frame.frameNumber));
                    frame.label.setPrefWidth(135);
                    if (!file.exists()) {
                        fileName = frame.getFileNamePreviewStub();
                        file = new File(fileName);
                    }
                    try {
                        BufferedImage bufferedImage = ImageIO.read(file);
                        frame.preview = new ImageView(ConvertToFxImage.convertToFxImage(bufferedImage));
                        frame.label.setGraphic(frame.preview);
                        frame.label.setContentDisplay(ContentDisplay.TOP);
                    } catch (IOException e) {}
                }

                listFrames.add(frame);
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

        return listFrames;
    }

// TODO SAVE

    public void save() {
        String sql = "UPDATE tbl_frames SET " +
                "file_id = ?, " +
                "frameNumber = ?, " +
                "isIFrame = ?, " +
                "isFind = ?, " +
                "isManualAdd = ?, " +
                "isManualCancel = ?, " +
                "isFinalFind = ?, " +
                "simScoreNext1 = ?, " +
                "simScoreNext2 = ?, " +
                "simScoreNext3 = ?, " +
                "simScorePrev1 = ?, " +
                "simScorePrev2 = ?, " +
                "simScorePrev3 = ?, " +
                "diffNext1 = ?, " +
                "diffNext2 = ?, " +
                "diffPrev1 = ?, " +
                "diffPrev2 = ?, " +
                "uuid = ?, " +
                "file_uuid = ? " +
                "WHERE id = ?";
        try {
            PreparedStatement ps = Main.mainConnection.prepareStatement(sql);
            ps.setInt   (1, this.fileId);
            ps.setInt   (2, this.frameNumber);
            ps.setBoolean(3, this.isIFrame);
            ps.setBoolean(4, this.isFind);
            ps.setBoolean(5, this.isManualAdd);
            ps.setBoolean(6, this.isManualCancel);
            ps.setBoolean(7, this.isFinalFind);
            ps.setDouble(8, this.simScoreNext1);
            ps.setDouble(9, this.simScoreNext2);
            ps.setDouble(10, this.simScoreNext3);
            ps.setDouble(11, this.simScorePrev1);
            ps.setDouble(12, this.simScorePrev2);
            ps.setDouble(13, this.simScorePrev3);
            ps.setDouble(14, this.diffNext1);
            ps.setDouble(15, this.diffNext2);
            ps.setDouble(16, this.diffPrev1);
            ps.setDouble(17, this.diffPrev2);
            ps.setString(18, this.uuid.toString());
            ps.setString(19, this.fileUuid.toString());
            ps.setInt   (20, this.id);
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

// TODO DELETE

    public void delete() {
        String sql = "DELETE FROM tbl_frames WHERE id = ?";
        try {
            PreparedStatement ps = Main.mainConnection.prepareStatement(sql);
            ps.setInt   (1, this.id);
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

// TODO FUNCTIONS

    public static void setPictureToPerson(IVFXFrames ivfxFrames, IVFXPersons ivfxPersons) throws IOException {
        FileUtils.copyFile(new File(ivfxFrames.getFileNamePreview()), new File(ivfxPersons.getPersonPicturePreview()));
        FileUtils.copyFile(new File(ivfxFrames.getFileNameFullSize()), new File(ivfxPersons.getPersonPictureFullSize()));
    }

    public static void setPictureToPerson(String framePreviewFile, String frameFullSizeFile, IVFXPersons ivfxPersons) throws IOException {
        FileUtils.copyFile(new File(framePreviewFile), new File(ivfxPersons.getPersonPicturePreview()));
        FileUtils.copyFile(new File(frameFullSizeFile), new File(ivfxPersons.getPersonPictureFullSize()));
    }

// TODO GETTERS SETTERS

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
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

    public String getFileNameFullSize() {
        return this.ivfxFile.getFramesFolderFullSize()+"\\"+this.ivfxFile.getShortName()+this.ivfxFile.FRAMES_PREFIX+String.format("%06d", this.frameNumber)+".jpg";
    }

    public int getFrameNumber() {
        return frameNumber;
    }

    public void setFrameNumber(int frameNumber) {
        this.frameNumber = frameNumber;
    }

    public boolean getIsIFrame() {
        return isIFrame;
    }

    public void setIsIFrame(boolean IFrame) {
        isIFrame = IFrame;
    }

    public String getFileNamePreview() {
        return this.ivfxFile.getFramesFolderPreview()+"\\"+this.ivfxFile.getShortName()+this.ivfxFile.FRAMES_PREFIX+String.format("%06d", this.frameNumber)+".jpg";
    }

    public String getFileNamePreviewStub() {
        String fileName = this.getIvfxFile().getIvfxProject().getFramesFolder() + "\\" + FRAME_PREVIEW_STUB;
        return fileName;
    }

    public boolean getIsFind() {
        return isFind;
    }

    public void setIsFind(boolean find) {
        isFind = find;
    }

    public boolean getIsManualAdd() {
        return isManualAdd;
    }

    public void setIsManualAdd(boolean manualAdd) {
        isManualAdd = manualAdd;
    }

    public boolean getIsManualCancel() {
        return isManualCancel;
    }

    public void setIsManualCancel(boolean manualCancel) {
        isManualCancel = manualCancel;
    }

    public boolean getIsFinalFind() {
        return isFinalFind;
    }

    public void setIsFinalFind(boolean finalFind) {
        isFinalFind = finalFind;
    }

    public double getSimScoreNext1() {
        return simScoreNext1;
    }

    public void setSimScoreNext1(double simScoreNext1) {
        this.simScoreNext1 = simScoreNext1;
    }

    public double getSimScoreNext2() {
        return simScoreNext2;
    }

    public void setSimScoreNext2(double simScoreNext2) {
        this.simScoreNext2 = simScoreNext2;
    }

    public double getSimScoreNext3() {
        return simScoreNext3;
    }

    public void setSimScoreNext3(double simScoreNext3) {
        this.simScoreNext3 = simScoreNext3;
    }

    public double getSimScorePrev1() {
        return simScorePrev1;
    }

    public void setSimScorePrev1(double simScorePrev1) {
        this.simScorePrev1 = simScorePrev1;
    }

    public double getSimScorePrev2() {
        return simScorePrev2;
    }

    public void setSimScorePrev2(double simScorePrev2) {
        this.simScorePrev2 = simScorePrev2;
    }

    public double getSimScorePrev3() {
        return simScorePrev3;
    }

    public void setSimScorePrev3(double simScorePrev3) {
        this.simScorePrev3 = simScorePrev3;
    }

    public double getDiffNext1() {
        return diffNext1;
    }

    public void setDiffNext1(double diffNext1) {
        this.diffNext1 = diffNext1;
    }

    public double getDiffNext2() {
        return diffNext2;
    }

    public void setDiffNext2(double diffNext2) {
        this.diffNext2 = diffNext2;
    }

    public double getDiffPrev1() {
        return diffPrev1;
    }

    public void setDiffPrev1(double diffPrev1) {
        this.diffPrev1 = diffPrev1;
    }

    public double getDiffPrev2() {
        return diffPrev2;
    }

    public void setDiffPrev2(double diffPrev2) {
        this.diffPrev2 = diffPrev2;
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

    public ImageView getPreview() {
        return preview;
    }

    public void setPreview(ImageView preview) {
        this.preview = preview;
    }

    public Label getLabel() {
        return label;
    }

    public void setLabel(Label label) {
        this.label = label;
    }
}

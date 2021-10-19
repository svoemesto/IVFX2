package com.svoemesto.ivfx.tables;

import com.svoemesto.ivfx.Main;
import com.svoemesto.ivfx.utils.ConvertToFxImage;
import com.svoemesto.ivfx.utils.FrameFace;
import com.svoemesto.ivfx.utils.OverlayImage;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.ImageView;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class IVFXFaces {

    private int id;
    private IVFXFrames ivfxFrame;
    private int frameId = 0;
    private int faceNumberInFrame = 0;
    private int tagId = 0;
    private int tagRecognizedId = 0;
    private double recognizeProbability = 0.0;
    private int startX = 0;
    private int startY = 0;
    private int endX = 0;
    private int endY = 0;
    private double[] vector;

    private transient ImageView preview;
    private transient Label label;

    private transient ImageView previewPerson;
    private transient Label labelPerson;

    private static transient final int MAX_HEIGHT_FACE_IN_PREVIEW = 100;

    //TODO ISEQUAL

    public boolean isEqual(IVFXFaces o) {
        return (this.id == o.id &&
                this.frameId == o.frameId &&
                this.faceNumberInFrame == o.faceNumberInFrame &&
                this.tagId == o.tagId &&
                this.tagRecognizedId == o.tagRecognizedId &&
                this.recognizeProbability == o.recognizeProbability &&
                this.startX == o.startX &&
                this.startY == o.startY &&
                this.endX == o.endX &&
                this.endY == o.endY &&
                this.vector.equals(o.vector));
    }

    // TODO КОНСТРУКТОРЫ

    public String getTextFromVector() {
        if (this.vector.length == 0) return "";
        String result = "";
        int i  = 0;
        for (double v: vector) {
            result = result + v;
            i++;
            if (i != vector.length) result = result + "|";
        }
        return result;
    }

    public static double[] getVectorFromText(String text) {
        String[] textVector = text.split("\\|");
        double[] result = new double[textVector.length];
        for (int i = 0; i < textVector.length; i++) {
            result[i] = Double.parseDouble(textVector[i]);
        }
        return result;
    }

    // пустой конструктор
    public IVFXFaces() {
    }

    public String getImageFile() {
        return ivfxFrame.getFileNameFullSize();
    }

    public String getPersonName() {
        if (this.tagRecognizedId != 0) {
            return IVFXTags.load(this.tagRecognizedId, false).getName();
        } else {
            return "(еще не обработано)";
        }
    }

    public String getFaceFile() {
        String pathToFolderFramesFull = ivfxFrame.getIvfxFile().getFolderFramesFull() + ".faces";
        String fileNameFrameFull = ivfxFrame.getFileNameFullSizeWithoutFolder();
        String fileNameFaceWoFolder = fileNameFrameFull.substring(0, fileNameFrameFull.length()-4) + "_face_" + String.format("%02d", faceNumberInFrame) + ".jpg";
        String fileNameFace = pathToFolderFramesFull + "\\" + fileNameFaceWoFolder;
        return fileNameFace;
    }



    public static IVFXFaces createOrUpdateInstance(FrameFace frameFace, boolean withPreview) {

        if (frameFace.fileid == 0 || frameFace.frame_id == 0) {
            return null;
        }

        boolean needToUpdate = false;
        IVFXFaces face = load(frameFace, false);
        if (face != null) {
            needToUpdate = true;
        } else {
            face = new IVFXFaces();
        }

        IVFXFrames frame = IVFXFrames.load(frameFace.frame_id,false);
        if (frameFace.faceid != 0) face.id = frameFace.faceid;
        face.frameId = frame.getId();
        face.ivfxFrame = frame;
        face.faceNumberInFrame = frameFace.face_id;
        face.tagId = frameFace.person_id;
        face.tagRecognizedId = frameFace.person_id_recognized;
        face.recognizeProbability = frameFace.recognize_probability;
        face.startX = frameFace.start_x;
        face.startY = frameFace.start_y;
        face.endX = frameFace.end_x;
        face.endY = frameFace.end_y;
        face.vector = frameFace.vector;


        if (needToUpdate) {
            face.save();
            return face;
        }

        Statement statement = null;
        ResultSet rs = null;
        String sql;

        try {

            sql = "INSERT INTO tbl_faces (" +
                    "frame_id, " +
                    "face_number_in_frame, " +
                    "tag_id, " +
                    "tag_recognized_id, " +
                    "recognize_probability, " +
                    "start_x, " +
                    "start_y, " +
                    "end_x, " +
                    "end_y, " +
                    "vector) " +
                    "VALUES(" +
                    face.frameId + "," +
                    face.faceNumberInFrame + "," +
                    face.tagId + "," +
                    face.tagRecognizedId + "," +
                    face.recognizeProbability + "," +
                    face.startX + "," +
                    face.startY + "," +
                    face.endX + "," +
                    face.endY + "," +
                    "'" + face.getTextFromVector() + "'" + ")";

            PreparedStatement ps = Main.mainConnection.prepareStatement(sql);
            ps.executeUpdate(sql, Statement.RETURN_GENERATED_KEYS);
            rs = ps.getGeneratedKeys();
            if (rs.next()) {
                face.id = rs.getInt(1);
                System.out.println("Создана запись для лица № " + face.faceNumberInFrame + ", фрейма № " + frame.getFrameNumber() + " файла «" + frame.getIvfxFile().getTitle() + "» с идентификатором " + rs.getInt(1));
            }

            if (withPreview) {
                String fileName = face.getFaceFile();
                File file = new File(fileName);
                face.label = new Label();
                face.label.setPrefHeight(100);
                if (!file.exists()) {
                    fileName = face.ivfxFrame.getFileNamePreviewStub();
                    file = new File(fileName);
                }
                try {
                    BufferedImage bufferedImage = ImageIO.read(file);
                    if (bufferedImage.getHeight() > MAX_HEIGHT_FACE_IN_PREVIEW) {
                        bufferedImage = OverlayImage.resizeImage(bufferedImage,MAX_HEIGHT_FACE_IN_PREVIEW,MAX_HEIGHT_FACE_IN_PREVIEW, Color.BLACK);
                    }
                    face.preview = new ImageView(ConvertToFxImage.convertToFxImage(bufferedImage));
                    face.label.setGraphic(face.preview);
                    face.label.setContentDisplay(ContentDisplay.TOP);
                } catch (IOException e) {}

                if (face.tagRecognizedId != 0) {
                    IVFXTags tag = IVFXTags.load(face.tagRecognizedId,true);
                    face.previewPerson = tag.getPreview1();
                    face.labelPerson = tag.getLabel1();
                }

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

        return face;
    }

    public static IVFXFaces load(int id, boolean withPreview) {
        Statement statement = null;
        ResultSet rs = null;
        String sql;

        try {
            statement = Main.mainConnection.createStatement();

            sql = "SELECT * FROM tbl_faces WHERE id = " + id;
            rs = statement.executeQuery(sql);
            if (rs.next()) {
                IVFXFaces face = new IVFXFaces();
                face.id = rs.getInt("id");
                face.frameId = rs.getInt("frame_id");
                face.faceNumberInFrame = rs.getInt("face_number_in_frame");
                face.tagId = rs.getInt("tag_id");
                face.tagRecognizedId = rs.getInt("tag_recognized_id");
                face.recognizeProbability = rs.getDouble("recognize_probability");
                face.startX = rs.getInt("start_x");
                face.startY = rs.getInt("start_y");
                face.endX = rs.getInt("end_x");
                face.endY = rs.getInt("end_y");
                face.vector = getVectorFromText(rs.getString("vector"));
                face.ivfxFrame = IVFXFrames.load(face.frameId);

                if (withPreview) {
                    String fileName = face.getFaceFile();
                    File file = new File(fileName);
                    face.label = new Label();
                    face.label.setPrefHeight(100);
                    if (!file.exists()) {
                        fileName = face.ivfxFrame.getFileNamePreviewStub();
                        file = new File(fileName);
                    }
                    try {
                        BufferedImage bufferedImage = ImageIO.read(file);
                        if (bufferedImage.getHeight() > MAX_HEIGHT_FACE_IN_PREVIEW) {
                            bufferedImage = OverlayImage.resizeImage(bufferedImage,MAX_HEIGHT_FACE_IN_PREVIEW,MAX_HEIGHT_FACE_IN_PREVIEW, Color.BLACK);
                        }
                        face.preview = new ImageView(ConvertToFxImage.convertToFxImage(bufferedImage));
                        face.label.setGraphic(face.preview);
                        face.label.setContentDisplay(ContentDisplay.TOP);
                    } catch (IOException e) {}

                    if (face.tagRecognizedId != 0) {
                        IVFXTags tag = IVFXTags.load(face.tagRecognizedId,true);
                        face.previewPerson = tag.getPreview1();
                        face.labelPerson = tag.getLabel1();
                    }

                }

                return face;
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

    public static IVFXFaces load(FrameFace frameFace, boolean withPreview) {
        Statement statement = null;
        ResultSet rs = null;
        String sql;

        try {
            statement = Main.mainConnection.createStatement();

            if (frameFace.faceid != 0 ) {
                sql = "SELECT * FROM tbl_faces WHERE id = " + frameFace.faceid;
            } else {
                sql = "SELECT * FROM tbl_faces WHERE frame_id = " + frameFace.frame_id + " AND face_number_in_frame = " + frameFace.face_id;
            }

            rs = statement.executeQuery(sql);
            if (rs.next()) {
                IVFXFaces face = new IVFXFaces();
                face.id = rs.getInt("id");
                face.frameId = rs.getInt("frame_id");
                face.faceNumberInFrame = rs.getInt("face_number_in_frame");
                face.tagId = rs.getInt("tag_id");
                face.tagRecognizedId = rs.getInt("tag_recognized_id");
                face.recognizeProbability = rs.getDouble("recognize_probability");
                face.startX = rs.getInt("start_x");
                face.startY = rs.getInt("start_y");
                face.endX = rs.getInt("end_x");
                face.endY = rs.getInt("end_y");
                face.vector = getVectorFromText(rs.getString("vector"));
                face.ivfxFrame = IVFXFrames.load(face.frameId);

                if (withPreview) {
                    String fileName = face.getFaceFile();
                    File file = new File(fileName);
                    face.label = new Label();
                    face.label.setPrefHeight(100);
                    if (!file.exists()) {
                        fileName = face.ivfxFrame.getFileNamePreviewStub();
                        file = new File(fileName);
                    }
                    try {
                        BufferedImage bufferedImage = ImageIO.read(file);
                        if (bufferedImage.getHeight() > MAX_HEIGHT_FACE_IN_PREVIEW) {
                            bufferedImage = OverlayImage.resizeImage(bufferedImage,MAX_HEIGHT_FACE_IN_PREVIEW,MAX_HEIGHT_FACE_IN_PREVIEW, Color.BLACK);
                        }
                        face.preview = new ImageView(ConvertToFxImage.convertToFxImage(bufferedImage));
                        face.label.setGraphic(face.preview);
                        face.label.setContentDisplay(ContentDisplay.TOP);
                    } catch (IOException e) {}

                    if (face.tagRecognizedId != 0) {
                        IVFXTags tag = IVFXTags.load(face.tagRecognizedId,true);
                        face.previewPerson = tag.getPreview1();
                        face.labelPerson = tag.getLabel1();
                    }

                }

                return face;
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

    public static List<IVFXFaces> loadList(IVFXFiles ivfxFile) {
        return loadList(ivfxFile, false, true, true, true, true, 0, 1);
    }

    public static List<IVFXFaces> loadList(IVFXFiles ivfxFile, boolean withPreview, boolean includeSetManual, boolean includeNoSetManual, boolean includeNoFaces, boolean includeExtras, double minProba, double maxProba) {
        return loadList(ivfxFile, withPreview, includeSetManual, includeNoSetManual, includeNoFaces, includeExtras, minProba, maxProba, null);
    }

    public static List<IVFXFaces> loadList(IVFXFiles ivfxFile, boolean withPreview, boolean includeSetManual, boolean includeNoSetManual, boolean includeNoFaces, boolean includeExtras, double minProba, double maxProba, ProgressBar progressBar) {
        List<IVFXFaces> listFaces = new ArrayList<>();

        int iProgress = 0;
        Statement statement = null;
        ResultSet rs = null;
        String sql;

        try {
            statement = Main.mainConnection.createStatement();

            String whereCond = "";
            if (includeSetManual) whereCond = whereCond + "tbl_faces.tag_id > 0 OR ";
            if (includeNoSetManual) whereCond = whereCond + "tbl_faces.tag_id = 0 OR ";
            if (includeNoFaces) whereCond = whereCond + "tbl_faces.tag_id = -1 OR ";
            if (includeExtras) whereCond = whereCond + "tbl_faces.tag_id = -2 OR ";
            if (!whereCond.equals("")) whereCond = " AND (" + whereCond.substring(0, whereCond.length()-4) + ")";


            sql = "SELECT tbl_faces.* FROM tbl_files INNER JOIN tbl_frames ON tbl_files.id = tbl_frames.file_id INNER JOIN tbl_faces ON tbl_faces.frame_id = tbl_frames.id WHERE tbl_frames.file_id = " + ivfxFile.getId() +
                    " AND tbl_faces.recognize_probability >= " + minProba + " AND tbl_faces.recognize_probability <= " + maxProba +
                    whereCond + " ORDER BY tbl_faces.id";

            System.out.println(sql);
//            String sqlCnt = "SELECT COUNT(*) AS CNT FROM (" + sql + ") AS tmp";
//            ResultSet rsCnt = null;
//            rsCnt = statement.executeQuery(sqlCnt);
//            rsCnt.next();
//            int countRows = rsCnt.getInt("CNT");
//            rsCnt.close();

            rs = statement.executeQuery(sql);
            while (rs.next()) {

//                if (progressBar != null) progressBar.setProgress((double)++iProgress / countRows);

                IVFXFaces face = new IVFXFaces();
                face.id = rs.getInt("id");
                face.frameId = rs.getInt("frame_id");
                face.faceNumberInFrame = rs.getInt("face_number_in_frame");
                face.tagId = rs.getInt("tag_id");
                face.tagRecognizedId = rs.getInt("tag_recognized_id");
                face.recognizeProbability = rs.getDouble("recognize_probability");
                face.startX = rs.getInt("start_x");
                face.startY = rs.getInt("start_y");
                face.endX = rs.getInt("end_x");
                face.endY = rs.getInt("end_y");
                face.vector = getVectorFromText(rs.getString("vector"));
                face.ivfxFrame = IVFXFrames.load(face.frameId);

                if (withPreview) {
                    String fileName = face.getFaceFile();
                    File file = new File(fileName);
                    face.label = new Label();
                    face.label.setPrefHeight(100);
                    if (!file.exists()) {
                        fileName = face.ivfxFrame.getFileNamePreviewStub();
                        file = new File(fileName);
                    }
                    try {
                        BufferedImage bufferedImage = ImageIO.read(file);
                        if (bufferedImage.getHeight() > MAX_HEIGHT_FACE_IN_PREVIEW) {
                            bufferedImage = OverlayImage.resizeImage(bufferedImage,MAX_HEIGHT_FACE_IN_PREVIEW,MAX_HEIGHT_FACE_IN_PREVIEW, Color.BLACK);
                        }
                        face.preview = new ImageView(ConvertToFxImage.convertToFxImage(bufferedImage));
                        face.label.setGraphic(face.preview);
                        face.label.setContentDisplay(ContentDisplay.TOP);
                    } catch (IOException e) {}

                    if (face.tagRecognizedId != 0) {
                        IVFXTags tag = IVFXTags.load(face.tagRecognizedId,true);
                        face.previewPerson = tag.getPreview1();
                        face.labelPerson = tag.getLabel1();
                    }

                }

                listFaces.add(face);
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

        return listFaces;
    }

    public static List<IVFXFaces> loadList(IVFXFrames ivfxFrame) {
        return loadList(ivfxFrame, false, true, true, true, true, 0, 1);
    }

    public static List<IVFXFaces> loadList(IVFXFrames ivfxFrame, boolean withPreview, boolean includeSetManual, boolean includeNoSetManual, boolean includeNoFaces, boolean includeExtras, double minProba, double maxProba) {
        return loadList(ivfxFrame, withPreview, includeSetManual, includeNoSetManual, includeNoFaces, includeExtras, minProba, maxProba, null);
    }

    public static List<IVFXFaces> loadList(IVFXFrames ivfxFrame, boolean withPreview, boolean includeSetManual, boolean includeNoSetManual, boolean includeNoFaces, boolean includeExtras, double minProba, double maxProba, ProgressBar progressBar) {
        List<IVFXFaces> listFaces = new ArrayList<>();

        int iProgress = 0;
        Statement statement = null;
        ResultSet rs = null;
        String sql;

        try {
            statement = Main.mainConnection.createStatement();

            String whereCond = "";
            if (includeSetManual) whereCond = whereCond + "tbl_faces.tag_id > 0 OR ";
            if (includeNoSetManual) whereCond = whereCond + "tbl_faces.tag_id = 0 OR ";
            if (includeNoFaces) whereCond = whereCond + "tbl_faces.tag_id = -1 OR ";
            if (includeExtras) whereCond = whereCond + "tbl_faces.tag_id = -2 OR ";
            if (!whereCond.equals("")) whereCond = " AND (" + whereCond.substring(0, whereCond.length()-4) + ")";


            sql = "SELECT * FROM tbl_faces WHERE tbl_faces.frame_id = " + ivfxFrame.getId() +
                    " AND tbl_faces.recognize_probability >= " + minProba + " AND tbl_faces.recognize_probability <= " + maxProba +
                    whereCond + " ORDER BY tbl_faces.id";

//            String sqlCnt = "SELECT COUNT(*) AS CNT FROM (" + sql + ") AS tmp";
//            ResultSet rsCnt = null;
//            rsCnt = statement.executeQuery(sqlCnt);
//            rsCnt.next();
//            int countRows = rsCnt.getInt("CNT");
//            rsCnt.close();

            rs = statement.executeQuery(sql);
            while (rs.next()) {

//                if (progressBar != null) progressBar.setProgress((double)++iProgress / countRows);

                IVFXFaces face = new IVFXFaces();
                face.id = rs.getInt("id");
                face.frameId = rs.getInt("frame_id");
                face.faceNumberInFrame = rs.getInt("face_number_in_frame");
                face.tagId = rs.getInt("tag_id");
                face.tagRecognizedId = rs.getInt("tag_recognized_id");
                face.recognizeProbability = rs.getDouble("recognize_probability");
                face.startX = rs.getInt("start_x");
                face.startY = rs.getInt("start_y");
                face.endX = rs.getInt("end_x");
                face.endY = rs.getInt("end_y");
                face.vector = getVectorFromText(rs.getString("vector"));
                face.ivfxFrame = IVFXFrames.load(face.frameId);

                if (withPreview) {
                    String fileName = face.getFaceFile();
                    File file = new File(fileName);
                    face.label = new Label();
                    face.label.setPrefHeight(100);
                    if (!file.exists()) {
                        fileName = face.ivfxFrame.getFileNamePreviewStub();
                        file = new File(fileName);
                    }
                    try {
                        BufferedImage bufferedImage = ImageIO.read(file);
                        if (bufferedImage.getHeight() > MAX_HEIGHT_FACE_IN_PREVIEW) {
                            bufferedImage = OverlayImage.resizeImage(bufferedImage,MAX_HEIGHT_FACE_IN_PREVIEW,MAX_HEIGHT_FACE_IN_PREVIEW, Color.BLACK);
                        }
                        face.preview = new ImageView(ConvertToFxImage.convertToFxImage(bufferedImage));
                        face.label.setGraphic(face.preview);
                        face.label.setContentDisplay(ContentDisplay.TOP);
                    } catch (IOException e) {}

                    if (face.tagRecognizedId != 0) {
                        IVFXTags tag = IVFXTags.load(face.tagRecognizedId,true);
                        face.previewPerson = tag.getPreview1();
                        face.labelPerson = tag.getLabel1();
                    }

                }

                listFaces.add(face);
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

        return listFaces;
    }

    public static List<IVFXFaces> loadList(IVFXTags ivfxTag) {
        return loadList(ivfxTag, null, null, false, false, false, 0);
    }

    public static List<IVFXFaces> loadList(IVFXTags ivfxTag, IVFXProjects ivfxProject, IVFXFiles ivfxFile, boolean withPreview, boolean isManual, boolean isRecognized, double minProba) {
        return loadList(ivfxTag, ivfxProject, ivfxFile, withPreview, isManual, isRecognized, minProba,null);
    }

    public static List<IVFXFaces> loadList(IVFXTags ivfxTag, IVFXProjects ivfxProject, IVFXFiles ivfxFile, boolean withPreview, boolean isManual, boolean isRecognized, double minProba, ProgressBar progressBar) {
        List<IVFXFaces> listFaces = new ArrayList<>();

        int iProgress = 0;
        Statement statement = null;
        ResultSet rs = null;
        String sql;

        try {
            statement = Main.mainConnection.createStatement();

            sql = "SELECT tbl_faces.* FROM tbl_faces INNER JOIN tbl_frames ON tbl_faces.frame_id = tbl_frames.id INNER JOIN tbl_files ON tbl_frames.file_id = tbl_files.id WHERE tbl_faces.recognize_probability >= " + minProba + " AND ";
            if (isManual & !isRecognized) {
                sql = sql + "tbl_faces.tag_id = " + ivfxTag.getId() + " ";
            } else if(!isManual & isRecognized) {
                sql = sql + "tbl_faces.tag_recognized_id = " + ivfxTag.getId() + " ";
            } else if (isManual & isRecognized) {
                sql = sql + "(tbl_faces.tag_id = " + ivfxTag.getId() + " AND tbl_faces.tag_recognized_id = " + ivfxTag.getId() + ") ";
            } else {
                sql = sql + "(tbl_faces.tag_id = " + ivfxTag.getId() + " OR tbl_faces.tag_recognized_id = " + ivfxTag.getId() + ") ";
            }
            if (ivfxProject != null) sql = sql + " AND tbl_files.project_id = " + ivfxProject.getId();
            if (ivfxFile != null) sql = sql + " AND tbl_frames.file_id = " + ivfxFile.getId();
            sql = sql + " ORDER BY tbl_faces.id";

//            String sqlCnt = "SELECT COUNT(*) AS CNT FROM (" + sql + ") AS tmp";
//            ResultSet rsCnt = null;
//            rsCnt = statement.executeQuery(sqlCnt);
//            rsCnt.next();
//            int countRows = rsCnt.getInt("CNT");
//            rsCnt.close();

            rs = statement.executeQuery(sql);
            while (rs.next()) {

//                if (progressBar != null) progressBar.setProgress((double)++iProgress / countRows);

                IVFXFaces face = new IVFXFaces();
                face.id = rs.getInt("id");
                face.frameId = rs.getInt("frame_id");
                face.faceNumberInFrame = rs.getInt("face_number_in_frame");
                face.tagId = rs.getInt("tag_id");
                face.tagRecognizedId = rs.getInt("tag_recognized_id");
                face.recognizeProbability = rs.getDouble("recognize_probability");
                face.startX = rs.getInt("start_x");
                face.startY = rs.getInt("start_y");
                face.endX = rs.getInt("end_x");
                face.endY = rs.getInt("end_y");
                face.vector = getVectorFromText(rs.getString("vector"));
                face.ivfxFrame = IVFXFrames.load(face.frameId);

                if (withPreview) {
                    String fileName = face.getFaceFile();
                    File file = new File(fileName);
                    face.label = new Label();
                    face.label.setPrefHeight(100);
                    if (!file.exists()) {
                        fileName = face.ivfxFrame.getFileNamePreviewStub();
                        file = new File(fileName);
                    }
                    try {
                        BufferedImage bufferedImage = ImageIO.read(file);
                        if (bufferedImage.getHeight() > MAX_HEIGHT_FACE_IN_PREVIEW) {
                            bufferedImage = OverlayImage.resizeImage(bufferedImage,MAX_HEIGHT_FACE_IN_PREVIEW,MAX_HEIGHT_FACE_IN_PREVIEW, Color.BLACK);
                        }
                        face.preview = new ImageView(ConvertToFxImage.convertToFxImage(bufferedImage));
                        face.label.setGraphic(face.preview);
                        face.label.setContentDisplay(ContentDisplay.TOP);
                    } catch (IOException e) {}

                    if (face.tagRecognizedId != 0) {
                        IVFXTags tag = IVFXTags.load(face.tagRecognizedId,true);
                        face.previewPerson = tag.getPreview1();
                        face.labelPerson = tag.getLabel1();
                    }

                }

                listFaces.add(face);
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

        return listFaces;
    }

    public static List<IVFXFaces> loadList(IVFXProjects ivfxProject) {
        return loadList(ivfxProject, false, true, true, true, true, 0, 1);
    }

    public static List<IVFXFaces> loadList(IVFXProjects ivfxProject, boolean withPreview, boolean includeSetManual, boolean includeNoSetManual, boolean includeNoFaces, boolean includeExtras, double minProba, double maxProba) {
        return loadList(ivfxProject, withPreview, includeSetManual, includeNoSetManual, includeNoFaces, includeExtras, minProba, maxProba, null);
    }

    public static List<IVFXFaces> loadList(IVFXProjects ivfxProject, boolean withPreview, boolean includeSetManual, boolean includeNoSetManual, boolean includeNoFaces, boolean includeExtras, double minProba, double maxProba, ProgressBar progressBar) {
        List<IVFXFaces> listFaces = new ArrayList<>();

        int iProgress = 0;
        Statement statement = null;
        ResultSet rs = null;
        String sql;

        try {
            statement = Main.mainConnection.createStatement();

//            sql = "SELECT tbl_faces.* FROM tbl_files INNER JOIN tbl_frames ON tbl_files.id = tbl_frames.file_id INNER JOIN tbl_faces ON tbl_faces.frame_id = tbl_frames.id " +
//                    "WHERE tbl_files.project_id = " + ivfxProject.getId() + " AND tbl_faces.tag_id > 0 " +
//                    " ORDER BY tbl_faces.id";

            String whereCond = "";
            if (includeSetManual) whereCond = whereCond + "tbl_faces.tag_id > 0 OR ";
            if (includeNoSetManual) whereCond = whereCond + "tbl_faces.tag_id = 0 OR ";
            if (includeNoFaces) whereCond = whereCond + "tbl_faces.tag_id = -1 OR ";
            if (includeExtras) whereCond = whereCond + "tbl_faces.tag_id = -2 OR ";
            if (!whereCond.equals("")) whereCond = " AND (" + whereCond.substring(0, whereCond.length()-4) + ")";


            sql = "SELECT tbl_faces.* FROM tbl_files INNER JOIN tbl_frames ON tbl_files.id = tbl_frames.file_id INNER JOIN tbl_faces ON tbl_faces.frame_id = tbl_frames.id WHERE tbl_files.project_id = " + ivfxProject.getId() +
                    " AND tbl_faces.recognize_probability >= " + minProba + " AND tbl_faces.recognize_probability <= " + maxProba +
                    whereCond + " ORDER BY tbl_faces.id";


//            String sqlCnt = "SELECT COUNT(*) AS CNT FROM (" + sql + ") AS tmp";
//            ResultSet rsCnt = null;
//            rsCnt = statement.executeQuery(sqlCnt);
//            rsCnt.next();
//            int countRows = rsCnt.getInt("CNT");
//            rsCnt.close();

            rs = statement.executeQuery(sql);
            while (rs.next()) {

//                if (progressBar != null) progressBar.setProgress((double)++iProgress / countRows);

                IVFXFaces face = new IVFXFaces();
                face.id = rs.getInt("id");
                face.frameId = rs.getInt("frame_id");
                face.faceNumberInFrame = rs.getInt("face_number_in_frame");
                face.tagId = rs.getInt("tag_id");
                face.tagRecognizedId = rs.getInt("tag_recognized_id");
                face.recognizeProbability = rs.getDouble("recognize_probability");
                face.startX = rs.getInt("start_x");
                face.startY = rs.getInt("start_y");
                face.endX = rs.getInt("end_x");
                face.endY = rs.getInt("end_y");
                face.vector = getVectorFromText(rs.getString("vector"));
                face.ivfxFrame = IVFXFrames.load(face.frameId);

                if (withPreview) {
                    String fileName = face.getFaceFile();
                    File file = new File(fileName);
                    face.label = new Label();
                    face.label.setPrefHeight(100);
                    if (!file.exists()) {
                        fileName = face.ivfxFrame.getFileNamePreviewStub();
                        file = new File(fileName);
                    }
                    try {
                        BufferedImage bufferedImage = ImageIO.read(file);
                        if (bufferedImage.getHeight() > MAX_HEIGHT_FACE_IN_PREVIEW) {
                            bufferedImage = OverlayImage.resizeImage(bufferedImage,MAX_HEIGHT_FACE_IN_PREVIEW,MAX_HEIGHT_FACE_IN_PREVIEW, Color.BLACK);
                        }
                        face.preview = new ImageView(ConvertToFxImage.convertToFxImage(bufferedImage));
                        face.label.setGraphic(face.preview);
                        face.label.setContentDisplay(ContentDisplay.TOP);
                    } catch (IOException e) {}

                    if (face.tagRecognizedId != 0) {
                        IVFXTags tag = IVFXTags.load(face.tagRecognizedId,true);
                        face.previewPerson = tag.getPreview1();
                        face.labelPerson = tag.getLabel1();
                    }

                }

                listFaces.add(face);
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

        return listFaces;
    }


    // TODO SAVE

    public void save() {
        String sql = "UPDATE tbl_faces SET " +
                "frame_id = ?, " +
                "face_number_in_frame = ?, " +
                "tag_id = ?, " +
                "tag_recognized_id = ?, " +
                "recognize_probability = ?, " +
                "start_x = ?, " +
                "start_y = ?, " +
                "end_x = ?, " +
                "end_y = ?, " +
                "vector = ? " +
                "WHERE id = ?";
        try {
            PreparedStatement ps = Main.mainConnection.prepareStatement(sql);
            ps.setInt   (1, this.frameId);
            ps.setInt   (2, this.faceNumberInFrame);
            ps.setInt(3, this.tagId);
            ps.setInt(4, this.tagRecognizedId);
            ps.setDouble(5, this.recognizeProbability);
            ps.setInt(6, this.startX);
            ps.setInt(7, this.startY);
            ps.setInt(8, this.endX);
            ps.setInt(9, this.endY);
            ps.setString(10, this.getTextFromVector());
            ps.setInt   (11, this.id);
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public FrameFace getFrameFace() {
        FrameFace frameFace = new FrameFace();

        frameFace.faceid = this.getId();
        frameFace.projectid = this.getIvfxFrame().getIvfxFile().getProjectId();
        frameFace.fileid = this.getIvfxFrame().getFileId();
        frameFace.image_file = this.getImageFile();
        frameFace.face_id = this.getFaceNumberInFrame();
        frameFace.person_id = this.getTagId();
        frameFace.person_id_recognized = this.getTagRecognizedId();
        frameFace.recognize_probability = this.getRecognizeProbability();
        frameFace.frame_id = this.getFrameId();
        frameFace.face_file = this.getFaceFile();
        frameFace.start_x = this.getStartX();
        frameFace.start_y = this.getStartY();
        frameFace.end_x = this.getEndX();
        frameFace.end_y = this.getEndY();
        frameFace.vector = this.getVector();

        return frameFace;
    }

    public double getHf() {
        return (endY-startY)/1080.0;
    }

    public IVFXShotsTypeSize getTypeSize(boolean withPreview) {
        return IVFXShotsTypeSize.load(getHf(),withPreview);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public IVFXFrames getIvfxFrame() {
        return ivfxFrame;
    }

    public void setIvfxFrame(IVFXFrames ivfxFrame) {
        this.ivfxFrame = ivfxFrame;
    }

    public int getFrameId() {
        return frameId;
    }

    public void setFrameId(int frameId) {
        this.frameId = frameId;
    }

    public int getFaceNumberInFrame() {
        return faceNumberInFrame;
    }

    public void setFaceNumberInFrame(int faceNumberInFrame) {
        this.faceNumberInFrame = faceNumberInFrame;
    }

    public int getTagId() {
        return tagId;
    }

    public void setTagId(int tagId) {
        this.tagId = tagId;
    }

    public int getTagRecognizedId() {
        return tagRecognizedId;
    }

    public void setTagRecognizedId(int tagRecognizedId) {
        this.tagRecognizedId = tagRecognizedId;
    }

    public double getRecognizeProbability() {
        return recognizeProbability;
    }

    public void setRecognizeProbability(double recognizeProbability) {
        this.recognizeProbability = recognizeProbability;
    }

    public int getStartX() {
        return startX;
    }

    public void setStartX(int startX) {
        this.startX = startX;
    }

    public int getStartY() {
        return startY;
    }

    public void setStartY(int startY) {
        this.startY = startY;
    }

    public int getEndX() {

        return Math.min(endX,this.getIvfxFrame().getIvfxFile().getWidth());

    }

    public void setEndX(int endX) {
        this.endX = endX;
    }

    public int getEndY() {
        return Math.min(endY,this.getIvfxFrame().getIvfxFile().getHeight());

    }

    public void setEndY(int endY) {
        this.endY = endY;
    }

    public double[] getVector() {
        return vector;
    }

    public void setVector(double[] vector) {
        this.vector = vector;
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

    public ImageView getPreviewPerson() {
        return previewPerson;
    }

    public void setPreviewPerson(ImageView previewPerson) {
        this.previewPerson = previewPerson;
    }

    public Label getLabelPerson() {
        return labelPerson;
    }

    public void setLabelPerson(Label labelPerson) {
        this.labelPerson = labelPerson;
    }
}

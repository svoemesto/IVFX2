package com.svoemesto.ivfx.tables;

import com.svoemesto.ivfx.Main;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class IVFXTagsShotsFaces {

    private int projectId = 0;
    private int fileId = 0;
    private int shotId = 0;
    private int tagId = 0;
    private int typeSizeId = 0;
    private double proba = 0.0;
    private double hf = 0.0;

    private IVFXProjects ivfxProject = null;
    private IVFXFiles ivfxFile = null;
    private IVFXShots ivfxShot = null;
    private IVFXTags ivfxTag = null;
    private IVFXShotsTypeSize ivfxShotTypeSize = null;

    public IVFXTagsShotsFaces() {
    }

    public static List<IVFXTagsShotsFaces> loadList(IVFXShots shot, boolean withPreview) {
        List<IVFXTagsShotsFaces> list = new ArrayList<>();

        Statement statement = null;
        ResultSet rs = null;
        String sql;

        try {
            statement = Main.mainConnection.createStatement();

            sql = "SELECT" +
                    "  tbl_files.project_id," +
                    "  tbl_shots.file_id," +
                    "  tbl_shots.id AS shot_id," +
                    "  tbl_faces.tag_recognized_id AS tag_id," +
                    "  MAX(tbl_faces.recognize_probability) AS proba, " +
                    "  MAX((tbl_faces.end_y - tbl_faces.start_y)/tbl_files.height) AS hf " +
                    "FROM tbl_shots" +
                    "  INNER JOIN tbl_frames" +
                    "    ON tbl_shots.file_id = tbl_frames.file_id" +
                    "    AND tbl_shots.firstFrameNumber <= tbl_frames.frameNumber" +
                    "    AND tbl_shots.lastFrameNumber >= tbl_frames.frameNumber " +
                    "  INNER JOIN tbl_faces" +
                    "    ON tbl_faces.frame_id = tbl_frames.id" +
                    "  INNER JOIN tbl_files" +
                    "    ON tbl_shots.file_id = tbl_files.id" +
                    "    AND tbl_frames.file_id = tbl_files.id " +
                    "WHERE tbl_faces.tag_recognized_id > 0 " +
                    "AND tbl_shots.id = " + shot.getId() + " " +
                    "GROUP BY tbl_shots.id," +
                    "         tbl_faces.tag_recognized_id," +
                    "         tbl_shots.file_id," +
                    "         tbl_files.project_id";

            rs = statement.executeQuery(sql);
            while (rs.next()) {

                IVFXTagsShotsFaces tagShotFace = new IVFXTagsShotsFaces();
                tagShotFace.projectId = rs.getInt("project_id");
                tagShotFace.fileId = rs.getInt("file_id");
                tagShotFace.shotId = rs.getInt("shot_id");
                tagShotFace.tagId = rs.getInt("tag_id");
                tagShotFace.proba = rs.getDouble("proba");
                tagShotFace.hf = rs.getDouble("hf");

                tagShotFace.ivfxShot = shot;
                tagShotFace.ivfxFile = shot.getIvfxFile();
                tagShotFace.ivfxProject = shot.getIvfxFile().getIvfxProject();
                tagShotFace.ivfxTag = IVFXTags.load(tagShotFace.tagId, withPreview);
                tagShotFace.ivfxShotTypeSize = IVFXShotsTypeSize.load(tagShotFace.hf, withPreview);
                tagShotFace.typeSizeId = tagShotFace.ivfxShotTypeSize.getId();

                list.add(tagShotFace);
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


        return list;
    }

    public static List<IVFXTagsShotsFaces> loadList(IVFXFiles file, boolean withPreview) {
        List<IVFXTagsShotsFaces> list = new ArrayList<>();

        Statement statement = null;
        ResultSet rs = null;
        String sql;

        try {
            statement = Main.mainConnection.createStatement();

            sql = "SELECT" +
                    "  tbl_files.project_id," +
                    "  tbl_shots.file_id," +
                    "  tbl_shots.id AS shot_id," +
                    "  tbl_faces.tag_recognized_id AS tag_id," +
                    "  MAX(tbl_faces.recognize_probability) AS proba, " +
                    "  MAX((tbl_faces.end_y - tbl_faces.start_y)/tbl_files.height) AS hf " +
                    "FROM tbl_shots" +
                    "  INNER JOIN tbl_frames" +
                    "    ON tbl_shots.file_id = tbl_frames.file_id" +
                    "    AND tbl_shots.firstFrameNumber <= tbl_frames.frameNumber" +
                    "    AND tbl_shots.lastFrameNumber >= tbl_frames.frameNumber " +
                    "  INNER JOIN tbl_faces" +
                    "    ON tbl_faces.frame_id = tbl_frames.id" +
                    "  INNER JOIN tbl_files" +
                    "    ON tbl_shots.file_id = tbl_files.id" +
                    "    AND tbl_frames.file_id = tbl_files.id " +
                    "WHERE tbl_faces.tag_recognized_id > 0 " +
                    "AND tbl_shots.file_id = " + file.getId() + " " +
                    "GROUP BY tbl_shots.id," +
                    "         tbl_faces.tag_recognized_id," +
                    "         tbl_shots.file_id," +
                    "         tbl_files.project_id";

            rs = statement.executeQuery(sql);
            while (rs.next()) {

                IVFXTagsShotsFaces tagShotFace = new IVFXTagsShotsFaces();
                tagShotFace.projectId = rs.getInt("project_id");
                tagShotFace.fileId = rs.getInt("file_id");
                tagShotFace.shotId = rs.getInt("shot_id");
                tagShotFace.tagId = rs.getInt("tag_id");
                tagShotFace.proba = rs.getDouble("proba");
                tagShotFace.hf = rs.getDouble("hf");

                tagShotFace.ivfxShot = IVFXShots.load(tagShotFace.shotId, withPreview);
                tagShotFace.ivfxFile = file;
                tagShotFace.ivfxProject = file.getIvfxProject();
                tagShotFace.ivfxTag = IVFXTags.load(tagShotFace.tagId, withPreview);
                tagShotFace.ivfxShotTypeSize = IVFXShotsTypeSize.load(tagShotFace.hf, withPreview);
                tagShotFace.typeSizeId = tagShotFace.ivfxShotTypeSize.getId();

                list.add(tagShotFace);
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


        return list;
    }


    public int getProjectId() {
        return projectId;
    }

    public void setProjectId(int projectId) {
        this.projectId = projectId;
    }

    public int getFileId() {
        return fileId;
    }

    public void setFileId(int fileId) {
        this.fileId = fileId;
    }

    public int getShotId() {
        return shotId;
    }

    public void setShotId(int shotId) {
        this.shotId = shotId;
    }

    public int getTagId() {
        return tagId;
    }

    public void setTagId(int tagId) {
        this.tagId = tagId;
    }

    public double getProba() {
        return proba;
    }

    public void setProba(double proba) {
        this.proba = proba;
    }

    public double getHf() {
        return hf;
    }

    public void setHf(double hf) {
        this.hf = hf;
    }

    public int getTypeSizeId() {
        return typeSizeId;
    }

    public void setTypeSizeId(int typeSizeId) {
        this.typeSizeId = typeSizeId;
    }

    public IVFXProjects getIvfxProject() {
        return ivfxProject;
    }

    public void setIvfxProject(IVFXProjects ivfxProject) {
        this.ivfxProject = ivfxProject;
    }

    public IVFXFiles getIvfxFile() {
        return ivfxFile;
    }

    public void setIvfxFile(IVFXFiles ivfxFile) {
        this.ivfxFile = ivfxFile;
    }

    public IVFXShots getIvfxShot() {
        return ivfxShot;
    }

    public void setIvfxShot(IVFXShots ivfxShot) {
        this.ivfxShot = ivfxShot;
    }

    public IVFXTags getIvfxTag() {
        return ivfxTag;
    }

    public void setIvfxTag(IVFXTags ivfxTag) {
        this.ivfxTag = ivfxTag;
    }

    public IVFXShotsTypeSize getIvfxShotTypeSize() {
        return ivfxShotTypeSize;
    }

    public void setIvfxShotTypeSize(IVFXShotsTypeSize ivfxShotTypeSize) {
        this.ivfxShotTypeSize = ivfxShotTypeSize;
    }
}

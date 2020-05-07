package com.svoemesto.ivfx.tables;

import com.svoemesto.ivfx.Main;
import com.svoemesto.ivfx.utils.FFmpeg;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class IVFXScenes {

    public static transient final String SCENES_SUFFIX = "_Scenes";

    private int id;
    private int fileId;
    private UUID uuid = UUID.randomUUID();
    private UUID fileUuid;
    private IVFXFiles ivfxFile;
    private int order;
    private String name;
    private String description="";

    private transient List<IVFXSegments> segments = new ArrayList<>();
    private transient List<IVFXScenesPersons> scenePersons = new ArrayList<>();

//TODO ISEQUAL

    public boolean isEqual(IVFXScenes o) {
        if (o == null) return false;
        return (this.id == o.id &&
                this.fileId == o.fileId &&
                this.uuid.equals(o.uuid) &&
                this.fileUuid.equals(o.fileUuid) &&
                this.order == o.order &&
                this.name.equals(o.name) &&
                this.description.equals(o.description));
    }

// TODO КОНСТРУКТОРЫ

    // пустой конструктор
    public IVFXScenes() {

    }

    public static IVFXScenes loadByUuid(UUID sceneUUID) {

        Statement statement = null;
        ResultSet rs = null;
        String sql;

        try {
            statement = Main.mainConnection.createStatement();

            sql = "SELECT * FROM tbl_scenes WHERE uuid = '" + sceneUUID.toString() + "'";
            rs = statement.executeQuery(sql);
            if (rs.next()) {
                IVFXScenes scene = new IVFXScenes();
                scene.id = rs.getInt("id");
                scene.fileId = rs.getInt("file_id");
                scene.order = rs.getInt("order_scene");
                scene.name = rs.getString("name");
                scene.description = rs.getString("description");
                scene.uuid = UUID.fromString(rs.getString("uuid"));
                scene.fileUuid = UUID.fromString(rs.getString("file_uuid"));
                scene.ivfxFile = IVFXFiles.loadById(scene.fileId);

                return scene;
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

    public static IVFXScenes loadById(int id) {

        Statement statement = null;
        ResultSet rs = null;
        String sql;

        try {
            statement = Main.mainConnection.createStatement();

            sql = "SELECT * FROM tbl_scenes WHERE id = " + id;
            rs = statement.executeQuery(sql);
            if (rs.next()) {
                IVFXScenes scene = new IVFXScenes();
                scene.id = rs.getInt("id");
                scene.fileId = rs.getInt("file_id");
                scene.order = rs.getInt("order_scene");
                scene.name = rs.getString("name");
                scene.description = rs.getString("description");
                scene.uuid = UUID.fromString(rs.getString("uuid"));
                scene.fileUuid = UUID.fromString(rs.getString("file_uuid"));
                scene.ivfxFile = IVFXFiles.loadById(scene.fileId);

                return scene;
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


    public static List<IVFXScenes> loadList(IVFXFiles ivfxFiles) {

        List<IVFXScenes> listScenes = new ArrayList<>();

        Statement statement = null;
        ResultSet rs = null;
        String sql;

        try {
            statement = Main.mainConnection.createStatement();

            sql = "SELECT * FROM tbl_scenes WHERE file_id = " + ivfxFiles.getId() + " ORDER BY order_scene";
            rs = statement.executeQuery(sql);
            while (rs.next()) {
                IVFXScenes scene = new IVFXScenes();
                scene.id = rs.getInt("id");
                scene.fileId = rs.getInt("file_id");
                scene.order = rs.getInt("order_scene");
                scene.name = rs.getString("name");
                scene.description = rs.getString("description");
                scene.uuid = UUID.fromString(rs.getString("uuid"));
                scene.fileUuid = UUID.fromString(rs.getString("file_uuid"));
                scene.ivfxFile = ivfxFiles;

                listScenes.add(scene);
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

        return listScenes;
    }


// TODO FUNCTIONS


    public String getTitle() {
        return this.ivfxFile.getTitle();
    }

    // возвращает первый сегмент сцены
    public IVFXSegments getFirstSegment() {

        Statement statement = null;
        ResultSet rs = null;
        String sql;

        try {
            statement = Main.mainConnection.createStatement();

            sql = "SELECT * FROM tbl_scenes_segments WHERE scene_id = " + this.id + " ORDER BY order_scene_segment";
            rs = statement.executeQuery(sql);
            if (rs.next()) {
                return IVFXSegments.loadById(rs.getInt("segment_id"),false);
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

    // возвращает последний сегмент сцены
    public IVFXSegments getLastSegment() {

        Statement statement = null;
        ResultSet rs = null;
        String sql;

        try {
            statement = Main.mainConnection.createStatement();

            sql = "SELECT * FROM tbl_scenes_segments WHERE scene_id = " + this.id + " ORDER BY order_scene_segment DESC";
            rs = statement.executeQuery(sql);
            if (rs.next()) {
                return IVFXSegments.loadById(rs.getInt("segment_id"),false);
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

    public int getDuration() {
        return FFmpeg.getDurationByFrameNumber(this.getLastSegment().getLastFrameNumber() - this.getFirstSegment().getFirstFrameNumber()+1, this.ivfxFile.getFrameRate());
    }

    public String getStart() {

        int firstFrame = this.getFirstSegment().getFirstFrameNumber();
        return FFmpeg.convertDurationToString(FFmpeg.getDurationByFrameNumber(firstFrame-1,this.ivfxFile.getFrameRate()));

    }

    public String getEnd() {

        int lastFrame = this.getLastSegment().getLastFrameNumber();
        return FFmpeg.convertDurationToString(FFmpeg.getDurationByFrameNumber(lastFrame,this.ivfxFile.getFrameRate()));

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

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public List<IVFXSegments> getSegments() {
        return segments;
    }

    public void setSegments(List<IVFXSegments> segments) {
        this.segments = segments;
    }

    public List<IVFXScenesPersons> getScenePersons() {
        return scenePersons;
    }

    public void setScenePersons(List<IVFXScenesPersons> scenePersons) {
        this.scenePersons = scenePersons;
    }
}

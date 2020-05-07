package com.svoemesto.ivfx.tables;

import com.svoemesto.ivfx.Main;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class IVFXScenesSegments {

    private int id;
    private int sceneId;
    private int segmentId;
    private UUID uuid = UUID.randomUUID();
    private UUID sceneUuid;
    private UUID segmentUuid;
    private IVFXScenes ivfxScene;
    private IVFXSegments ivfxSegment;
    private int order = 0;


//TODO ISEQUAL

    public boolean isEqual(IVFXScenesSegments o) {
        return (this.id == o.id &&
                this.sceneId == o.sceneId &&
                this.segmentId == o.segmentId &&
                this.uuid.equals(o.uuid) &&
                this.sceneUuid.equals(o.sceneUuid) &&
                this.segmentUuid.equals(o.segmentUuid) &&
                this.order == o.order);
    }

// TODO КОНСТРУКТОРЫ

    // пустой конструктор
    public IVFXScenesSegments() {
    }


    public static List<IVFXScenesSegments> loadList(IVFXScenes ivfxScene) {
        List<IVFXScenesSegments> listScenesSegments = new ArrayList<>();

        Statement statement = null;
        ResultSet rs = null;
        String sql;

        try {
            statement = Main.mainConnection.createStatement();

            sql = "SELECT * FROM tbl_scenes_segments WHERE scene_id = " + ivfxScene.getId();
            rs = statement.executeQuery(sql);
            while (rs.next()) {
                IVFXScenesSegments sceneSegment = new IVFXScenesSegments();
                sceneSegment.id = rs.getInt("id");
                sceneSegment.sceneId = rs.getInt("scene_id");
                sceneSegment.segmentId = rs.getInt("segment_id");
                sceneSegment.order = rs.getInt("order_scene_segment");
                sceneSegment.uuid = UUID.fromString(rs.getString("uuid"));
                sceneSegment.sceneUuid = UUID.fromString(rs.getString("scene_uuid"));
                sceneSegment.segmentUuid = UUID.fromString(rs.getString("segment_uuid"));
                sceneSegment.ivfxScene = IVFXScenes.loadById(sceneSegment.sceneId);
                sceneSegment.ivfxSegment = IVFXSegments.loadById(sceneSegment.segmentId,false);
                listScenesSegments.add(sceneSegment);
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

        return listScenesSegments;
    }


// TODO GETTERS SETTERS



    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public UUID getSceneUuid() {
        return sceneUuid;
    }

    public void setSceneUuid(UUID sceneUuid) {
        this.sceneUuid = sceneUuid;
    }

    public UUID getSegmentUuid() {
        return segmentUuid;
    }

    public void setSegmentUuid(UUID segmentUuid) {
        this.segmentUuid = segmentUuid;
    }

    public IVFXScenes getIvfxScene() {
        return ivfxScene;
    }

    public void setIvfxScene(IVFXScenes ivfxScene) {
        this.ivfxScene = ivfxScene;
    }

    public IVFXSegments getIvfxSegment() {
        return ivfxSegment;
    }

    public void setIvfxSegment(IVFXSegments ivfxSegment) {
        this.ivfxSegment = ivfxSegment;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSceneId() {
        return sceneId;
    }

    public void setSceneId(int sceneId) {
        this.sceneId = sceneId;
    }

    public int getSegmentId() {
        return segmentId;
    }

    public void setSegmentId(int segmentId) {
        this.segmentId = segmentId;
    }
}
package com.svoemesto.ivfx.tables;

import com.svoemesto.ivfx.Main;
import javafx.scene.control.ProgressBar;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class IVFXScenesShots {

    private int id;
    private int sceneId;
    private int shotId;
    private IVFXScenes ivfxScene;
    private IVFXShots ivfxShot;
    private int order = 0;


//TODO ISEQUAL

    public boolean isEqual(IVFXScenesShots o) {
        return (this.id == o.id &&
                this.sceneId == o.sceneId &&
                this.shotId == o.shotId &&
                this.order == o.order);
    }

// TODO КОНСТРУКТОРЫ

    // пустой конструктор
    public IVFXScenesShots() {
    }

    public static IVFXScenesShots getNewDbInstance(IVFXScenes ivfxScene, IVFXShots ivfxShot) {
        IVFXScenesShots ivfxSceneShot = new IVFXScenesShots();

        ivfxSceneShot.sceneId = ivfxScene.getId();
        ivfxSceneShot.shotId = ivfxShot.getId();
        ivfxSceneShot.ivfxScene = ivfxScene;
        ivfxSceneShot.ivfxShot = ivfxShot;

        Statement statement = null;
        ResultSet rs = null;
        String sql;

        try {

            statement = Main.mainConnection.createStatement();

            sql = "SELECT COUNT(*) FROM tbl_scenes_shots WHERE scene_id = " + ivfxSceneShot.sceneId;
            rs = statement.executeQuery(sql);
            if (rs.next()) {
                ivfxSceneShot.order = rs.getInt(1) + 1;  // индексация столбцов начинается с единицы
            }

            sql = "INSERT INTO tbl_scenes_shots (" +
                    "scene_id, " +
                    "shot_id, " +
                    "order_scene_shot) " +
                    "VALUES(" +
                    ivfxSceneShot.sceneId + "," +
                    ivfxSceneShot.shotId + "," +
                    ivfxSceneShot.order + ")";

            PreparedStatement ps = Main.mainConnection.prepareStatement(sql);
            ps.executeUpdate(sql, Statement.RETURN_GENERATED_KEYS);
            rs = ps.getGeneratedKeys();
            if (rs.next()) {
                ivfxSceneShot.id = rs.getInt(1);
                System.out.println("Создана запись для сцены «" + ivfxSceneShot.ivfxScene.getName() + "» плана «" + ivfxSceneShot.ivfxShot.getFirstFrameNumber() + "-" + ivfxSceneShot.ivfxShot.getLastFrameNumber() + "» файла «" + ivfxSceneShot.ivfxScene.getIvfxFile().getTitle() + "» с идентификатором " + rs.getInt(1));
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

        return ivfxSceneShot;
    }


    public static List<IVFXScenesShots> loadList(IVFXScenes ivfxScene) {
        return loadList(ivfxScene, null);
    }

    public static List<IVFXScenesShots> loadList(IVFXScenes ivfxScene, ProgressBar progressBar) {
        List<IVFXScenesShots> listScenesShots = new ArrayList<>();

        int iProgress = 0;
        Statement statement = null;
        ResultSet rs = null;
        String sql;

        try {
            statement = Main.mainConnection.createStatement();

            sql = "SELECT * FROM tbl_scenes_shots WHERE scene_id = " + ivfxScene.getId() + " ORDER BY order_scene_shot";

            String sqlCnt = "SELECT COUNT(*) AS CNT FROM (" + sql + ") AS tmp";
            ResultSet rsCnt = null;
            rsCnt = statement.executeQuery(sqlCnt);
            rsCnt.next();
            int countRows = rsCnt.getInt("CNT");
            rsCnt.close();

            rs = statement.executeQuery(sql);
            while (rs.next()) {

                if (progressBar != null) {
                    progressBar.setProgress((double)++iProgress / countRows);
                }

                IVFXScenesShots sceneShot = new IVFXScenesShots();
                sceneShot.id = rs.getInt("id");
                sceneShot.sceneId = rs.getInt("scene_id");
                sceneShot.shotId = rs.getInt("shot_id");
                sceneShot.order = rs.getInt("order_scene_shot");
                sceneShot.ivfxScene = IVFXScenes.load(sceneShot.sceneId);
                sceneShot.ivfxShot = IVFXShots.load(sceneShot.shotId,false);
                listScenesShots.add(sceneShot);
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

        return listScenesShots;
    }


    public void save() {
        String sql = "UPDATE tbl_scenes_shots SET scene_id = ?, shot_id = ?, order_scene_shot = ? WHERE id = ?";
        try {
            PreparedStatement ps = Main.mainConnection.prepareStatement(sql);
            ps.setInt   (1, this.sceneId);
            ps.setInt   (2, this.shotId);
            ps.setInt   (3, this.order);
            ps.setInt(4, this.id);
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void delete() {
        String sql = "DELETE FROM tbl_scenes_shots WHERE id = ?";
        try {
            PreparedStatement ps = Main.mainConnection.prepareStatement(sql);
            ps.setInt   (1, this.id);
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

// TODO GETTERS SETTERS


    public IVFXScenes getIvfxScene() {
        return ivfxScene;
    }

    public void setIvfxScene(IVFXScenes ivfxScene) {
        this.ivfxScene = ivfxScene;
    }

    public IVFXShots getIvfxShot() {
        return ivfxShot;
    }

    public void setIvfxShot(IVFXShots ivfxShot) {
        this.ivfxShot = ivfxShot;
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

    public int getShotId() {
        return shotId;
    }

    public void setShotId(int shotId) {
        this.shotId = shotId;
    }
}
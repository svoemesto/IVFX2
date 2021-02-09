package com.svoemesto.ivfx.tasks;

import com.svoemesto.ivfx.Main;
import com.svoemesto.ivfx.tables.IVFXScenes;
import com.svoemesto.ivfx.tables.IVFXScenesShots;
import com.svoemesto.ivfx.tables.IVFXShots;
import javafx.concurrent.Task;
import javafx.scene.control.ProgressBar;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class LoadListScenShotsTask extends Task<List<IVFXScenesShots>> {

    private IVFXScenes ivfxScene;
    private ProgressBar progressBar;

    public LoadListScenShotsTask(IVFXScenes ivfxScene, ProgressBar progressBar) {
        this.ivfxScene = ivfxScene;
        this.progressBar = progressBar;
    }

    @Override
    protected List<IVFXScenesShots> call() throws Exception {
        
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
            if (progressBar != null) progressBar.setVisible(true);
            while (rs.next()) {

                if (progressBar != null) {
                    progressBar.setProgress((double)++iProgress / countRows);
                }

                IVFXScenesShots sceneShot = new IVFXScenesShots();
                sceneShot.setId(rs.getInt("id"));
                sceneShot.setSceneId(rs.getInt("scene_id"));
                sceneShot.setShotId(rs.getInt("shot_id"));
                sceneShot.setOrder(rs.getInt("order_scene_shot"));
                sceneShot.setIvfxScene(IVFXScenes.load(sceneShot.getSceneId()));
                sceneShot.setIvfxShot(IVFXShots.load(sceneShot.getShotId(),false));
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

        if (progressBar != null) progressBar.setVisible(false);
        return listScenesShots;
        
    }
}

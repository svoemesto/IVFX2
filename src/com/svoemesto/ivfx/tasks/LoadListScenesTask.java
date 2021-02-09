package com.svoemesto.ivfx.tasks;

import com.svoemesto.ivfx.Main;
import com.svoemesto.ivfx.tables.IVFXFiles;
import com.svoemesto.ivfx.tables.IVFXScenes;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.ProgressBar;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class LoadListScenesTask extends Task<List<IVFXScenes>> {

    private IVFXFiles ivfxFiles;
    private ProgressBar progressBar;

    public LoadListScenesTask(IVFXFiles ivfxFiles, ProgressBar progressBar) {
        this.ivfxFiles = ivfxFiles;
        this.progressBar = progressBar;
    }

    @Override
    protected List<IVFXScenes> call() throws Exception {
        List<IVFXScenes> listScenes = new ArrayList<>();

        final int[] iProgress = {0};
        Statement statement = null;
        ResultSet rs = null;
        String sql;

        try {
            statement = Main.mainConnection.createStatement();

            sql = "SELECT * FROM tbl_scenes WHERE file_id = " + ivfxFiles.getId() + " ORDER BY order_scene";

            String sqlCnt = "SELECT COUNT(*) AS CNT FROM (" + sql + ") AS tmp";
            ResultSet rsCnt = null;
            rsCnt = statement.executeQuery(sqlCnt);
            rsCnt.next();
            int countRows = rsCnt.getInt("CNT");
            rsCnt.close();

            rs = statement.executeQuery(sql);
            if (progressBar != null) progressBar.setVisible(true);
            while (rs.next()) {

                if (progressBar != null) progressBar.setProgress((double)++iProgress[0] / countRows);

                IVFXScenes scene = new IVFXScenes();
                scene.setId(rs.getInt("id"));
                scene.setFileId(rs.getInt("file_id"));
                scene.setOrder(rs.getInt("order_scene"));
                scene.setName(rs.getString("name"));
                scene.setDescription(rs.getString("description"));
                scene.setIvfxFile(ivfxFiles);

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
        if (progressBar != null) progressBar.setVisible(false);
        return listScenes;
    }
}

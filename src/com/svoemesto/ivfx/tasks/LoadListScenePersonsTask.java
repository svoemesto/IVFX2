package com.svoemesto.ivfx.tasks;

import com.svoemesto.ivfx.Main;
import com.svoemesto.ivfx.tables.IVFXPersons;
import com.svoemesto.ivfx.tables.IVFXScenes;
import com.svoemesto.ivfx.tables.IVFXScenesPersons;
import javafx.concurrent.Task;
import javafx.scene.control.ProgressBar;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class LoadListScenePersonsTask extends Task<List<IVFXScenesPersons>> {

    private IVFXScenes ivfxScene;
    private boolean withPreview;
    private ProgressBar progressBar;

    public LoadListScenePersonsTask(IVFXScenes ivfxScene, boolean withPreview, ProgressBar progressBar) {
        this.ivfxScene = ivfxScene;
        this.withPreview = withPreview;
        this.progressBar = progressBar;
    }

    @Override
    protected List<IVFXScenesPersons> call() throws Exception {

        List<IVFXScenesPersons> listScenesPersons = new ArrayList<>();

        final int[] iProgress = {0};
        Statement statement = null;
        ResultSet rs = null;
        String sql;

        try {
            statement = Main.mainConnection.createStatement();

            sql = "SELECT * FROM tbl_scenes_persons WHERE scene_id = " + ivfxScene.getId();

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

                IVFXScenesPersons scenePerson = new IVFXScenesPersons();
                scenePerson.setId(rs.getInt("id"));
                scenePerson.setSceneId(rs.getInt("scene_id"));
                scenePerson.setPersonId(rs.getInt("person_id"));
                scenePerson.setPersonIsMain(rs.getBoolean("personIsMain"));
                scenePerson.setIvfxScene(IVFXScenes.load(scenePerson.getSceneId()));
                scenePerson.setIvfxPerson(IVFXPersons.load(scenePerson.getPersonId(),withPreview));
                scenePerson.setPreview(scenePerson.getIvfxPerson().getPreview());
                scenePerson.setLabel(scenePerson.getIvfxPerson().getLabel());
                listScenesPersons.add(scenePerson);
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
        return listScenesPersons;
        
    }
}

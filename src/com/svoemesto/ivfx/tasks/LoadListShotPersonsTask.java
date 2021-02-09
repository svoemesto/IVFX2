package com.svoemesto.ivfx.tasks;

import com.svoemesto.ivfx.Main;
import com.svoemesto.ivfx.tables.IVFXPersons;
import com.svoemesto.ivfx.tables.IVFXShots;
import com.svoemesto.ivfx.tables.IVFXShotsPersons;
import javafx.concurrent.Task;
import javafx.scene.control.ProgressBar;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class LoadListShotPersonsTask extends Task<List<IVFXShotsPersons>> {

    private IVFXShots ivfxShot;
    private boolean withPreview;
    private ProgressBar progressBar;

    public LoadListShotPersonsTask(IVFXShots ivfxShot, boolean withPreview, ProgressBar progressBar) {
        this.ivfxShot = ivfxShot;
        this.withPreview = withPreview;
        this.progressBar = progressBar;
    }

    @Override
    protected List<IVFXShotsPersons> call() throws Exception {

        List<IVFXShotsPersons> listShotPersons = new ArrayList<>();

        final int[] iProgress = {0};
        Statement statement = null;
        ResultSet rs = null;
        String sql;

        try {
            statement = Main.mainConnection.createStatement();

            sql = "SELECT * FROM tbl_shots_persons WHERE shot_id = " + ivfxShot.getId();

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

                IVFXShotsPersons shotPerson = new IVFXShotsPersons();
                shotPerson.setId(rs.getInt("id"));
                shotPerson.setShotId(rs.getInt("shot_id"));
                shotPerson.setPersonId(rs.getInt("person_id"));
                shotPerson.setPersonIsMain(rs.getBoolean("personIsMain"));
                shotPerson.setIvfxShot(IVFXShots.load(shotPerson.getShotId(), false));
                shotPerson.setIvfxPerson(IVFXPersons.load(shotPerson.getPersonId(), withPreview));
                shotPerson.setPreview(shotPerson.getIvfxPerson().getPreview());
                shotPerson.setLabel(shotPerson.getIvfxPerson().getLabel());

                listShotPersons.add(shotPerson);
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
        return listShotPersons;
        
    }
}

package com.svoemesto.ivfx.tasks;

import com.svoemesto.ivfx.Main;
import com.svoemesto.ivfx.tables.IVFXEvents;
import com.svoemesto.ivfx.tables.IVFXEventsTypes;
import com.svoemesto.ivfx.tables.IVFXFiles;
import javafx.concurrent.Task;
import javafx.scene.control.ProgressBar;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class LoadListEventsTask extends Task<List<IVFXEvents>> {

    private IVFXFiles ivfxFiles;
    private ProgressBar progressBar;
    public LoadListEventsTask(IVFXFiles ivfxFiles, ProgressBar progressBar) {
        this.ivfxFiles = ivfxFiles;
        this.progressBar = progressBar;
    }

    @Override
    protected List<IVFXEvents> call() throws Exception {
        List<IVFXEvents> listEvents = new ArrayList<>();

        final int[] iProgress = {0};
        Statement statement = null;
        ResultSet rs = null;
        String sql;

        try {
            statement = Main.mainConnection.createStatement();

            sql = "SELECT * FROM tbl_events WHERE file_id = " + ivfxFiles.getId() + " ORDER BY order_event";

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


                IVFXEvents event = new IVFXEvents();
                event.setId(rs.getInt("id"));
                event.setFileId(rs.getInt("file_id"));
                event.setEventTypeId(rs.getInt("event_type_id"));
                event.setOrder(rs.getInt("order_event"));
                event.setName(rs.getString("name"));
                event.setEventTypeName(rs.getString("eventTypeName"));
                event.setFirstFrameNumber(rs.getInt("firstFrameNumber"));
                event.setLastFrameNumber(rs.getInt("lastFrameNumber"));
                event.setDescription(rs.getString("description"));
                event.setIvfxFile(ivfxFiles);
                event.setIvfxEventsTypes(IVFXEventsTypes.load(event.getEventTypeId()));

                listEvents.add(event);
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
        return listEvents;
    }
}

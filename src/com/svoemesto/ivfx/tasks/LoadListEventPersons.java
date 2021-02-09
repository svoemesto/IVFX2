package com.svoemesto.ivfx.tasks;

import com.svoemesto.ivfx.Main;
import com.svoemesto.ivfx.tables.IVFXEvents;
import com.svoemesto.ivfx.tables.IVFXEventsPersons;
import com.svoemesto.ivfx.tables.IVFXPersons;
import javafx.concurrent.Task;
import javafx.scene.control.ProgressBar;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class LoadListEventPersons extends Task<List<IVFXEventsPersons>> {

    private IVFXEvents ivfxEvent;
    private boolean withPreview;
    private ProgressBar progressBar;

    public LoadListEventPersons(IVFXEvents ivfxEvent, boolean withPreview, ProgressBar progressBar) {
        this.ivfxEvent = ivfxEvent;
        this.withPreview = withPreview;
        this.progressBar = progressBar;
    }

    @Override
    protected List<IVFXEventsPersons> call() throws Exception {

        List<IVFXEventsPersons> listEventsPersons = new ArrayList<>();

        int iProgress = 0;
        Statement statement = null;
        ResultSet rs = null;
        String sql;

        try {
            statement = Main.mainConnection.createStatement();

            sql = "SELECT * FROM tbl_events_persons WHERE event_id = " + ivfxEvent.getId() + " ORDER BY order_event_person";

            String sqlCnt = "SELECT COUNT(*) AS CNT FROM (" + sql + ") AS tmp";
            ResultSet rsCnt = null;
            rsCnt = statement.executeQuery(sqlCnt);
            rsCnt.next();
            int countRows = rsCnt.getInt("CNT");
            rsCnt.close();

            rs = statement.executeQuery(sql);
            if (progressBar != null) progressBar.setVisible(true);
            while (rs.next()) {

                if (progressBar != null) progressBar.setProgress((double)++iProgress / countRows);

                IVFXEventsPersons eventPerson = new IVFXEventsPersons();
                eventPerson.setId(rs.getInt("id"));
                eventPerson.setEventId(rs.getInt("event_id"));
                eventPerson.setPersonId(rs.getInt("person_id"));
                eventPerson.setOrder(rs.getInt("order_event_person"));
                eventPerson.setPersonIsMain(rs.getBoolean("personIsMain"));
                eventPerson.setIvfxEvent(IVFXEvents.load(eventPerson.getEventId()));
                eventPerson.setIvfxPerson(IVFXPersons.load(eventPerson.getPersonId(),withPreview));
                eventPerson.setPreview(eventPerson.getIvfxPerson().getPreview());
                eventPerson.setLabel(eventPerson.getIvfxPerson().getLabel());
                listEventsPersons.add(eventPerson);
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
        return listEventsPersons;
        
    }
}

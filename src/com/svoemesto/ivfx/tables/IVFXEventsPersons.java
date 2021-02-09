package com.svoemesto.ivfx.tables;

import com.svoemesto.ivfx.Main;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.ImageView;
import com.svoemesto.ivfx.utils.ConvertToFxImage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class IVFXEventsPersons {

    private int id;
    private int eventId;
    private int personId;
    private IVFXEvents ivfxEvent;
    private IVFXPersons ivfxPerson;
    private boolean personIsMain = false;
    private int order = 0;
    private ImageView[] preview = new ImageView[8];
    private Label[] label = new Label[8];

//TODO ISEQUAL

    public boolean isEqual(IVFXEventsPersons o) {
        return (this.id == o.id &&
                this.eventId == o.eventId &&
                this.personId == o.personId &&
                this.personIsMain == o.personIsMain &&
                this.order == o.order);
    }

// TODO КОНСТРУКТОРЫ

    // пустой конструктор
    public IVFXEventsPersons() {
    }

    public static IVFXEventsPersons getNewDbInstance(IVFXEvents ivfxEvent, IVFXPersons ivfxPerson) {
        IVFXEventsPersons ivfxEventsPersons = new IVFXEventsPersons();

        ivfxEventsPersons.eventId = ivfxEvent.getId();
        ivfxEventsPersons.personId = ivfxPerson.getId();
        ivfxEventsPersons.ivfxEvent = ivfxEvent;
        ivfxEventsPersons.ivfxPerson = ivfxPerson;

        Statement statement = null;
        ResultSet rs = null;
        String sql;

        try {
            statement = Main.mainConnection.createStatement();

            sql = "SELECT COUNT(*) FROM tbl_events_persons WHERE event_id = " + ivfxEventsPersons.eventId;
            rs = statement.executeQuery(sql);
            if (rs.next()) {
                ivfxEventsPersons.order = rs.getInt(1) + 1;  // индексация столбцов начинается с единицы
            }

            sql = "INSERT INTO tbl_events_persons (" +
                    "event_id, " +
                    "person_id, " +
                    "order_event_person, " +
                    "personIsMain) " +
                    "VALUES(" +
                    ivfxEventsPersons.eventId + "," +
                    ivfxEventsPersons.personId + "," +
                    ivfxEventsPersons.order + "," +
                    (ivfxEventsPersons.personIsMain ? 1 : 0) + ")";

            PreparedStatement ps = Main.mainConnection.prepareStatement(sql);
            ps.executeUpdate(sql, Statement.RETURN_GENERATED_KEYS);
            rs = ps.getGeneratedKeys();
            if (rs.next()) {
                ivfxEventsPersons.id = rs.getInt(1);
                System.out.println("Создана запись для персонажа «" + ivfxEventsPersons.ivfxPerson.getName() + "» события «" + ivfxEventsPersons.ivfxEvent.getName() + "» файла «" + ivfxEventsPersons.ivfxEvent.getIvfxFile().getTitle() + "»  с идентификатором " + rs.getInt(1));
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

        return ivfxEventsPersons;
    }



    public static IVFXEventsPersons load(int id, boolean withPreview) {

        Statement statement = null;
        ResultSet rs = null;
        String sql;

        try {
            statement = Main.mainConnection.createStatement();

            sql = "SELECT * FROM tbl_events_persons WHERE id = " + id;
            rs = statement.executeQuery(sql);
            if (rs.next()) {
                IVFXEventsPersons eventPerson = new IVFXEventsPersons();
                eventPerson.id = rs.getInt("id");
                eventPerson.eventId = rs.getInt("event_id");
                eventPerson.personId = rs.getInt("person_id");
                eventPerson.order = rs.getInt("order_event_person");
                eventPerson.personIsMain = rs.getBoolean("personIsMain");
                eventPerson.ivfxEvent = IVFXEvents.load(eventPerson.eventId);
                eventPerson.ivfxPerson = IVFXPersons.load(eventPerson.personId,withPreview);

                if (withPreview) {
                    String fileName = eventPerson.getIvfxPerson().getPersonPicturePreview();
                    File file = new File(fileName);
                    for (int i = 0; i < 8; i++) {
                        eventPerson.label[i] = new Label(eventPerson.ivfxPerson.getName());
                        eventPerson.label[i].setPrefWidth(135);
                    }

                    if (!file.exists()) {
                        fileName = eventPerson.getIvfxPerson().getPersonPicturePreviewStub();
                        file = new File(fileName);
                    }
                    try {
                        BufferedImage bufferedImage = ImageIO.read(file);
                        for (int i = 0; i < 8; i++) {
                            eventPerson.preview[i] = new ImageView(ConvertToFxImage.convertToFxImage(bufferedImage));
                            eventPerson.label[i].setGraphic(eventPerson.preview[i]);
                            eventPerson.label[i].setContentDisplay(ContentDisplay.TOP);
                        }

                    } catch (IOException e) {}
                }

                return eventPerson;
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

    public static List<IVFXEventsPersons> loadList(IVFXEvents ivfxEvent, boolean withPreview) {
        return loadList(ivfxEvent, withPreview, null);
    }

    public static List<IVFXEventsPersons> loadList(IVFXEvents ivfxEvent, boolean withPreview, ProgressBar progressBar) {
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
            while (rs.next()) {

                if (progressBar != null) progressBar.setProgress((double)++iProgress / countRows);

                IVFXEventsPersons eventPerson = new IVFXEventsPersons();
                eventPerson.id = rs.getInt("id");
                eventPerson.eventId = rs.getInt("event_id");
                eventPerson.personId = rs.getInt("person_id");
                eventPerson.order = rs.getInt("order_event_person");
                eventPerson.personIsMain = rs.getBoolean("personIsMain");
                eventPerson.ivfxEvent = IVFXEvents.load(eventPerson.eventId);
                eventPerson.ivfxPerson = IVFXPersons.load(eventPerson.personId,withPreview);
                eventPerson.preview = eventPerson.ivfxPerson.getPreview();
                eventPerson.label = eventPerson.ivfxPerson.getLabel();
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

        return listEventsPersons;
    }

    public void save() {
        String sql = "UPDATE tbl_events_persons SET event_id = ?, person_id = ?, personIsMain = ?, order_event_person = ? WHERE id = ?";
        try {
            PreparedStatement ps = Main.mainConnection.prepareStatement(sql);
            ps.setInt   (1, this.eventId);
            ps.setInt   (2, this.personId);
            ps.setBoolean(3, this.personIsMain);
            ps.setInt(4, this.order);
            ps.setInt(5, this.id);
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void delete() {
        String sql = "DELETE FROM tbl_events_persons WHERE id = ?";
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



    public ImageView[] getPreview() {
        return preview;
    }

    public ImageView getPreview1() {
        return preview[0];
    }

    public ImageView getPreview2() {
        return preview[1];
    }

    public ImageView getPreview3() {
        return preview[2];
    }

    public ImageView getPreview4() {
        return preview[3];
    }

    public ImageView getPreview5() {
        return preview[4];
    }

    public ImageView getPreview6() {
        return preview[5];
    }

    public ImageView getPreview7() {
        return preview[6];
    }

    public ImageView getPreview8() {
        return preview[7];
    }

    public void setPreview(ImageView[] preview) {
        this.preview = preview;
    }


    public Label[] getLabel() {
        return label;
    }

    public Label getLabel1() {
        return label[0];
    }

    public Label getLabel2() {
        return label[1];
    }

    public Label getLabel3() {
        return label[2];
    }

    public Label getLabel4() {
        return label[3];
    }

    public Label getLabel5() {
        return label[4];
    }

    public Label getLabel6() {
        return label[5];
    }

    public Label getLabel7() {
        return label[6];
    }

    public Label getLabel8() {
        return label[7];
    }

    public void setLabel(Label[] label) {
        this.label = label;
    }

    public IVFXEvents getIvfxEvent() {
        return ivfxEvent;
    }

    public void setIvfxEvent(IVFXEvents ivfxEvent) {
        this.ivfxEvent = ivfxEvent;
    }

    public IVFXPersons getIvfxPerson() {
        return ivfxPerson;
    }

    public void setIvfxPerson(IVFXPersons ivfxPerson) {
        this.ivfxPerson = ivfxPerson;
    }

    public boolean isPersonIsMain() {
        return personIsMain;
    }

    public void setPersonIsMain(boolean personIsMain) {
        this.personIsMain = personIsMain;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public String getPersonIsMainStr() {
        return this.personIsMain ? "+" : "";
    }

    public void setPersonIsMainStr(String str) {
        if (str.equals("") || str == null) {
            this.personIsMain = false;
        } else {
            this.personIsMain = true;
        }
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getEventId() {
        return eventId;
    }

    public void setEventId(int eventId) {
        this.eventId = eventId;
    }

    public int getPersonId() {
        return personId;
    }

    public void setPersonId(int personId) {
        this.personId = personId;
    }
}

package com.svoemesto.ivfx.tables;

import com.svoemesto.ivfx.Main;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.ImageView;
import com.svoemesto.ivfx.utils.FFmpeg;
import com.svoemesto.ivfx.utils.ConvertToFxImage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class IVFXEventsShots {

    private int id;
    private int eventId;
    private int shotId;
    private IVFXEvents ivfxEvent;
    private IVFXShots ivfxShot;
    private int order = 0;
    private ImageView imageViewFirst;
    private ImageView imageViewLast;
    private Label labelFirst;
    private Label labelLast;

//TODO ISEQUAL

    public boolean isEqual(IVFXEventsShots o) {
        return (this.id == o.id &&
                this.eventId == o.eventId &&
                this.shotId == o.shotId &&
                this.order == o.order);
    }

// TODO КОНСТРУКТОРЫ

    // пустой конструктор
    public IVFXEventsShots() {
    }


    public static IVFXEventsShots getNewDbInstance(IVFXEvents ivfxEvent, IVFXShots ivfxShot) {

        IVFXEventsShots ivfxEventsShots = new IVFXEventsShots();

        ivfxEventsShots.eventId = ivfxEvent.getId();
        ivfxEventsShots.shotId = ivfxShot.getId();
        ivfxEventsShots.ivfxEvent = ivfxEvent;
        ivfxEventsShots.ivfxShot = ivfxShot;

        Statement statement = null;
        ResultSet rs = null;
        String sql;

        try {
            statement = Main.mainConnection.createStatement();

            sql = "SELECT COUNT(*) FROM tbl_events_shots WHERE event_id = " + ivfxEventsShots.eventId;
            rs = statement.executeQuery(sql);
            if (rs.next()) {
                ivfxEventsShots.order = rs.getInt(1) + 1;  // индексация столбцов начинается с единицы
            }

            sql = "INSERT INTO tbl_events_shots (" +
                    "event_id, " +
                    "shot_id, " +
                    "order_event_shot) " +
                    "VALUES(" +
                    ivfxEventsShots.eventId + "," +
                    ivfxEventsShots.shotId + "," +
                    ivfxEventsShots.order + ")";

            PreparedStatement ps = Main.mainConnection.prepareStatement(sql);
            ps.executeUpdate(sql, Statement.RETURN_GENERATED_KEYS);
            rs = ps.getGeneratedKeys();
            if (rs.next()) {
                ivfxEventsShots.id = rs.getInt(1);
                System.out.println("Создана запись для плана «" + ivfxEventsShots.ivfxShot.getFirstFrameNumber() + "-" + ivfxEventsShots.ivfxShot.getLastFrameNumber() + "» события «" + ivfxEventsShots.ivfxEvent.getName() + "» файла «" + ivfxEventsShots.ivfxEvent.getIvfxFile().getTitle() + "» с идентификатором " + rs.getInt(1));
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

        return ivfxEventsShots;
    }


    public static IVFXEventsShots load(int id, boolean withPreview) {

        Statement statement = null;
        ResultSet rs = null;
        String sql;

        try {
            statement = Main.mainConnection.createStatement();

            sql = "SELECT * FROM tbl_events_shots WHERE id = " + id;
            rs = statement.executeQuery(sql);
            if (rs.next()) {
                IVFXEventsShots eventShot = new IVFXEventsShots();
                eventShot.id = rs.getInt("id");
                eventShot.eventId = rs.getInt("event_id");
                eventShot.shotId = rs.getInt("shot_id");
                eventShot.order = rs.getInt("order_event_shot");
                eventShot.ivfxEvent = IVFXEvents.load(eventShot.eventId);
                eventShot.ivfxShot = IVFXShots.load(eventShot.shotId,withPreview);

                if (withPreview) {
                    BufferedImage bufferedImage;
                    String fileName;
                    File file;

                    fileName = eventShot.getFirstFramePicture();
                    file = new File(fileName);
                    eventShot.labelFirst = new Label(eventShot.getStart());
                    eventShot.labelFirst.setPrefWidth(135);
                    if (file.exists()) {
                        try {
                            bufferedImage = ImageIO.read(file);
                            eventShot.imageViewFirst = new ImageView(ConvertToFxImage.convertToFxImage(bufferedImage));
                            eventShot.labelFirst.setGraphic(eventShot.imageViewFirst);
                            eventShot.labelFirst.setContentDisplay(ContentDisplay.TOP);

                        } catch (IOException e) {}

                    }

                    eventShot.labelLast = new Label(eventShot.getEnd());
                    eventShot.labelLast.setPrefWidth(135);
                    fileName = eventShot.getLastFramePicture();
                    file = new File(fileName);
                    if (file.exists()) {
                        try {
                            bufferedImage = ImageIO.read(file);
                            eventShot.imageViewLast = new ImageView(ConvertToFxImage.convertToFxImage(bufferedImage));
                            eventShot.labelLast.setGraphic(eventShot.imageViewLast);
                            eventShot.labelLast.setContentDisplay(ContentDisplay.TOP);
                        } catch (IOException e) {}

                    }
                }

                return eventShot;
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

    public static List<IVFXEventsShots> loadList(IVFXEvents ivfxEvent) {
        return loadList(ivfxEvent, null);
    }
    public static List<IVFXEventsShots> loadList(IVFXEvents ivfxEvent, ProgressBar progressBar) {
        List<IVFXEventsShots> listEventsShots = new ArrayList<>();

        int iProgress = 0;
        Statement statement = null;
        ResultSet rs = null;
        String sql;

        try {
            statement = Main.mainConnection.createStatement();

            sql = "SELECT * FROM tbl_events_shots WHERE event_id = " + ivfxEvent.getId() + " ORDER BY order_event_shot";


            String sqlCnt = "SELECT COUNT(*) AS CNT FROM (" + sql + ") AS tmp";
            ResultSet rsCnt = null;
            rsCnt = statement.executeQuery(sqlCnt);
            rsCnt.next();
            int countRows = rsCnt.getInt("CNT");
            rsCnt.close();

            rs = statement.executeQuery(sql);
            while (rs.next()) {

                if (progressBar != null) progressBar.setProgress((double)++iProgress / countRows);

                IVFXEventsShots eventShot = new IVFXEventsShots();
                eventShot.id = rs.getInt("id");
                eventShot.eventId = rs.getInt("event_id");
                eventShot.shotId = rs.getInt("shot_id");
                eventShot.order = rs.getInt("order_event_shot");
                eventShot.ivfxEvent = IVFXEvents.load(eventShot.eventId);
                eventShot.ivfxShot = IVFXShots.load(eventShot.shotId,false);
                listEventsShots.add(eventShot);
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

        return listEventsShots;
    }

    public void save() {
        String sql = "UPDATE tbl_events_shots SET event_id = ?, shot_id = ?, order_event_shot = ? WHERE id = ?";
        try {
            PreparedStatement ps = Main.mainConnection.prepareStatement(sql);
            ps.setInt   (1, this.eventId);
            ps.setInt   (2, this.shotId);
            ps.setInt(3, this.order);
            ps.setInt(4, this.id);
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void delete() {
        String sql = "DELETE FROM tbl_events_shots WHERE id = ?";
        try {
            PreparedStatement ps = Main.mainConnection.prepareStatement(sql);
            ps.setInt   (1, this.id);
            ps.executeUpdate();
            ps.close();

            String sql2 = "UPDATE tbl_events_shots SET order_event_shot = order_event_shot - 1 WHERE event_id = ? AND order_event_shot > ?";

            PreparedStatement ps2 = Main.mainConnection.prepareStatement(sql2);
            ps2.setInt   (1, this.eventId);
            ps2.setInt   (2, this.order);
            ps2.executeUpdate();
            ps2.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public static void saveList(List<IVFXEventsShots> listToSave, IVFXEvents ivfxEvent) {
        List<IVFXEventsShots> listFromDb = IVFXEventsShots.loadList(ivfxEvent);    // список элементов из базы
        List<IVFXEventsShots> listToUpdate = new ArrayList<>();    // список элементов на обновление
        List<IVFXEventsShots> listToDel = new ArrayList<>();       // список элементов на удаление

        for (IVFXEventsShots itemFromDb : listFromDb) {
            boolean isFinded = false;
            for (IVFXEventsShots itemToSave : listToSave) {
                if (itemFromDb.getId() == itemToSave.getId()) {
                    isFinded = true;
                    if (!itemFromDb.isEqual(itemToSave)) listToUpdate.add(itemToSave);
                    break;
                }
            }
            if (!isFinded) listToDel.add(itemFromDb);
        }

        System.out.println("Save list events shots. To Update: " + listToUpdate.size() + ", To Delete: " + listToDel.size());
        for (IVFXEventsShots itemToUpdate : listToUpdate) itemToUpdate.save();
        for (IVFXEventsShots itemToDel : listToDel) itemToDel.delete();
    }












// TODO GETTERS SETTERS


    public String getStart() {
        return FFmpeg.convertDurationToString(FFmpeg.getDurationByFrameNumber(this.ivfxShot.getFirstFrameNumber()-1,this.ivfxEvent.getIvfxFile().getFrameRate()));
    }

    public String getEnd() {
        return FFmpeg.convertDurationToString(FFmpeg.getDurationByFrameNumber(this.ivfxShot.getLastFrameNumber(),this.ivfxEvent.getIvfxFile().getFrameRate()));
    }

    public String getFirstFramePicture() {
        return this.ivfxEvent.getIvfxFile().getFramesFolderPreview()+"\\"+this.ivfxEvent.getIvfxFile().getShortName()+this.ivfxEvent.getIvfxFile().FRAMES_PREFIX+String.format("%06d", this.ivfxShot.getFirstFrameNumber())+".jpg";
    }

    public String getLastFramePicture() {
        return this.ivfxEvent.getIvfxFile().getFramesFolderPreview()+"\\"+this.ivfxEvent.getIvfxFile().getShortName()+this.ivfxEvent.getIvfxFile().FRAMES_PREFIX+String.format("%06d", this.ivfxShot.getLastFrameNumber())+".jpg";
    }

    public IVFXEvents getIvfxEvent() {
        return ivfxEvent;
    }

    public void setIvfxEvent(IVFXEvents ivfxEvent) {
        this.ivfxEvent = ivfxEvent;
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

    public ImageView getImageViewFirst() {
        return imageViewFirst;
    }

    public void setImageViewFirst(ImageView imageViewFirst) {
        this.imageViewFirst = imageViewFirst;
    }

    public ImageView getImageViewLast() {
        return imageViewLast;
    }

    public void setImageViewLast(ImageView imageViewLast) {
        this.imageViewLast = imageViewLast;
    }

    public Label getLabelFirst() {
        return labelFirst;
    }

    public void setLabelFirst(Label labelFirst) {
        this.labelFirst = labelFirst;
    }

    public Label getLabelLast() {
        return labelLast;
    }

    public void setLabelLast(Label labelLast) {
        this.labelLast = labelLast;
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

    public int getShotId() {
        return shotId;
    }

    public void setShotId(int shotId) {
        this.shotId = shotId;
    }
}



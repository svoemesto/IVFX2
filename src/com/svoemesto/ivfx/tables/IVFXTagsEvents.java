package com.svoemesto.ivfx.tables;

import com.svoemesto.ivfx.Main;
import com.svoemesto.ivfx.utils.ConvertToFxImage;
import com.svoemesto.ivfx.utils.FFmpeg;
import javafx.collections.FXCollections;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class IVFXTagsEvents extends IVFXTags{

    private int fileId;
    private int firstFrame;
    private IVFXFiles ivfxFile;

    public static List<IVFXTagsEvents> loadListEvents(IVFXFiles ivfxFile, boolean withPreview) {
        List<IVFXTagsEvents> listTagsEvents = new ArrayList<>();
        if (ivfxFile != null) {

            int iProgress = 0;
            Statement statement = null;
            ResultSet rs = null;
            String sql;

            try {
                statement = Main.mainConnection.createStatement();


                sql = "SELECT tbl_tags.id, tbl_tags.name, tbl_tags.tag_type_id, tbl_shots.file_id, tbl_files.order_file, MIN(tbl_shots.firstFrameNumber) AS firstFrameNumber " +
                        "FROM tbl_tags_shots " +
                        "INNER JOIN tbl_tags ON tbl_tags_shots.tag_id = tbl_tags.id INNER JOIN tbl_shots ON tbl_tags_shots.shot_id = tbl_shots.id INNER JOIN tbl_files ON tbl_shots.file_id = tbl_files.id " +
                        "WHERE tbl_tags.tag_type_id = 4 " +
                        "AND tbl_shots.file_id = " + ivfxFile.getId() + " " +
                        "GROUP BY tbl_tags.id, tbl_tags.name,  tbl_tags.tag_type_id, tbl_shots.file_id, tbl_files.order_file " +
                        "ORDER BY tbl_files.order_file, MIN(tbl_shots.firstFrameNumber)";

                rs = statement.executeQuery(sql);
                while (rs.next()) {

                    IVFXTagsEvents ivfxTag = new IVFXTagsEvents();
                    ivfxTag.id = rs.getInt("id");
                    ivfxTag.ivfxEnumTagsTypes = IVFXTagsTypes.getEnumTagsTypes(rs.getInt("tag_type_id"));
                    ivfxTag.name = rs.getString("name");
                    ivfxTag.fileId = rs.getInt("file_id");
                    ivfxTag.firstFrame = rs.getInt("firstFrameNumber");
                    ivfxTag.ivfxFile = IVFXFiles.load(ivfxTag.fileId);

                    if (withPreview) {
                        String fileName = ivfxTag.getTagPicturePreview();
                        File file = new File(fileName);
                        for (int i = 0; i < 8; i++) {
                            ivfxTag.label[i] = new Label(ivfxTag.name);
                            ivfxTag.label[i].setPrefWidth(135);
                            ivfxTag.label[i].setStyle(ivfxTag.fxLabelCaption);
                            ivfxTag.label[i].setWrapText(true);
                            ivfxTag.label[i].setMaxWidth(135);
                        }

                        if (!file.exists()) {
                            fileName = ivfxTag.getTagPicturePreviewStub();
                            file = new File(fileName);
                        }
                        try {
                            BufferedImage bufferedImage = ImageIO.read(file);
                            for (int i = 0; i < 8; i++) {
                                ivfxTag.preview[i] = new ImageView(ConvertToFxImage.convertToFxImage(bufferedImage));
//                                ivfxTag.preview[i] = new ImageView(ConvertToFxImage.convertToFxImage(OverlayImage.setOverlayUnderlineText(bufferedImage, ivfxTag.name)));
                                ivfxTag.label[i].setGraphic(ivfxTag.preview[i]);
                                ivfxTag.label[i].setContentDisplay(ContentDisplay.TOP);
                            }

                        } catch (IOException e) {
                        }

                    }

                    listTagsEvents.add(ivfxTag);
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
        }

        return listTagsEvents;
    }

    public static IVFXTagsEvents unionEvents(List<IVFXTagsEvents> listEvents) {

        if (!listEvents.isEmpty()) {
            if (listEvents.size() == 1) {
                return listEvents.get(0);
            } else {
                IVFXTagsEvents mainTagEvent = listEvents.get(0);

                String where1 = " WHERE (";
                String where2 = "";
                String where3 = " WHERE (";
                String where4 = " WHERE (";
                for (int i = 1; i < listEvents.size(); i++) {
                    IVFXTagsEvents tmp = listEvents.get(i);
                    where1 += "tag_id = " + tmp.id;
                    where2 += "tbl_tags_tags_1.tag_parent_id = "  + tmp.id;
                    where4 += "tag_parent_id = "  + tmp.id;
                    where3 += "id = " + tmp.id;
                    if (i != listEvents.size() - 1) {
                        where1 += " OR ";
                        where2 += " OR ";
                        where3 += " OR ";
                        where4 += " OR ";
                    }
                }
                where1 += ")";
                where3 += ")";
                where4 += ")";

                String sql1 = "UPDATE tbl_tags_shots SET tag_id = " + mainTagEvent.id + where1;
                String sql4 = "UPDATE tbl_tags_tags SET tag_parent_id = " + mainTagEvent.id + where4;

                String sql2 = "SELECT tbl_tags_tags_1.id AS id_to_del " +
                        "    FROM tbl_tags_tags " +
                        "      INNER JOIN tbl_tags " +
                        "        ON tbl_tags_tags.tag_child_id = tbl_tags.id " +
                        "      INNER JOIN tbl_tags_tags tbl_tags_tags_1 " +
                        "        ON tbl_tags_tags.tag_child_id = tbl_tags_tags_1.tag_child_id " +
                        "      INNER JOIN tbl_tags tbl_tags_1 " +
                        "        ON tbl_tags_tags_1.tag_child_id = tbl_tags_1.id " +
                        "    WHERE tbl_tags_tags.tag_parent_id = " + mainTagEvent.id + " AND (" + where2 + ")";

                String sql3 = "DELETE FROM tbl_tags" + where3;

                Statement statement = null;
                ResultSet rs = null;

                try {
                    statement = Main.mainConnection.createStatement();


                    rs = statement.executeQuery(sql2);
                    List<Integer> lst = new ArrayList<>();
                    while (rs.next()) {
                        lst.add(rs.getInt("id_to_del"));

                    }

                    String where0 = "";
                    for (int i = 0; i < lst.size(); i++) {
                        where0 += "id = " + lst.get(i);
                        if (i != lst.size() - 1) {
                            where0 += " OR ";
                        }
                    }

                    String sql0 = "DELETE FROM tbl_tags_tags WHERE " + where0;

                    PreparedStatement ps0 = Main.mainConnection.prepareStatement(sql0);
                    ps0.executeUpdate();
                    ps0.close();


                    PreparedStatement ps1 = Main.mainConnection.prepareStatement(sql1);
                    ps1.executeUpdate();
                    ps1.close();

                    PreparedStatement ps4 = Main.mainConnection.prepareStatement(sql4);
                    ps4.executeUpdate();
                    ps4.close();

                    PreparedStatement ps2 = Main.mainConnection.prepareStatement(sql3);
                    ps2.executeUpdate();
                    ps2.close();

                } catch (SQLException e) {
                    e.printStackTrace();
                }

                return mainTagEvent;
            }
        } else {
            return null;
        }
    }

    // Расширение или сужение сцены, возвращает план сцены до которого расширолось или сузилось
    public static IVFXTagsShots eventShotExpandCollapse(IVFXTags event, IVFXTagsShots eventShot, boolean isExpand, boolean directionUp, boolean askQuestions) {

        IVFXShots currentShot = eventShot.getIvfxShot();
        IVFXFiles currentFile = currentShot.getIvfxFile();
        IVFXTagsShots nextTagShot = null;

        boolean confirmAddShotToEvent = true;
        boolean nextShotIsAlreadyPresentInCurrentEvent = false;
        boolean nextShotIsPresentInEventsAnotherEvent = false;

        String sql = "";
        if (directionUp) {
            sql = "SELECT * FROM tbl_shots WHERE file_id = " + currentFile.getId() + " AND lastFrameNumber = " + (currentShot.getFirstFrameNumber() - 1);
        } else {
            sql = "SELECT * FROM tbl_shots WHERE file_id = " + currentFile.getId() + " AND firstFrameNumber = " + (currentShot.getLastFrameNumber() + 1);
        }

        Statement statement = null;
        ResultSet rs = null;

        try {
            statement = Main.mainConnection.createStatement();

            rs = statement.executeQuery(sql);
            if (rs.next()) {
                // находим соседний план
                IVFXShots nextShot = IVFXShots.load(rs.getInt("id"), true);

                // получаем список событий, в которых этот план уже участвует
                List<Integer> listTagIdEvents = nextShot.getTagIdEvents();
                nextShotIsAlreadyPresentInCurrentEvent = false;
                nextShotIsPresentInEventsAnotherEvent = listTagIdEvents.size() > 0;
                for (int eventId: listTagIdEvents) {
                    if (eventId == event.getId()) {
                        nextShotIsAlreadyPresentInCurrentEvent = true;
                        nextTagShot = IVFXTagsShots.load(event, nextShot, true);
                        break;
                    }
                }

                if (isExpand) { // если расширяемся
                    if (nextShotIsAlreadyPresentInCurrentEvent) { // если план уже есть в текущем событии - возвращаем его план-тэг
                        return nextTagShot;
                    } else {
                        if (nextShotIsPresentInEventsAnotherEvent && askQuestions) { // если план есть в каком-то другом событии - уточняем про перекрестные события (если надо)
                            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                            alert.setTitle("Перекрестное событие");
                            alert.setHeaderText("План, который вы ходите добавить к текущему событию, уже присутствует в другом событии.");
                            alert.setContentText("При добавлении плана к текущему собитию возникнут перекрестные события, в которых планы будут повторятся.\nВы уверены, что хотите допустить перекрестные события?");
                            Optional<ButtonType> option = alert.showAndWait();
                            if (option.get() != ButtonType.OK) {
                                confirmAddShotToEvent = false;
                            }
                        }

                        if (confirmAddShotToEvent) { // если принято решение все-таки расширить событие до этого плана - добавляем его к событию и возвращаем его
                            return IVFXTagsShots.getNewDbInstance(event,nextShot);
                        }

                    }
                } else { // если сужаемся
                    if (nextShotIsAlreadyPresentInCurrentEvent) { // если план уже есть в текущем событии - возвращаем его план-тэг, удалив текущий план-тэг
                        eventShot.delete();
                        return nextTagShot;
                    }
                }

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

        return eventShot;

    }


    public String getStart() {
        return FFmpeg.convertDurationToString(FFmpeg.getDurationByFrameNumber(this.firstFrame-1,this.ivfxFile.getFrameRate()));
    }

    public int getFileId() {
        return fileId;
    }

    public void setFileId(int fileId) {
        this.fileId = fileId;
    }

    public int getFirstFrame() {
        return firstFrame;
    }

    public void setFirstFrameNumber(int firstFrame) {
        this.firstFrame = firstFrame;
    }
}

package com.svoemesto.ivfx.tables;

import com.svoemesto.ivfx.Main;
import com.svoemesto.ivfx.utils.ConvertToFxImage;
import com.svoemesto.ivfx.utils.FFmpeg;
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

public class IVFXTagsScenes extends IVFXTags{

    private int fileId;
    private int firstFrame;
    private IVFXFiles ivfxFile;

    public static List<IVFXTagsScenes> loadListScenes(IVFXFiles ivfxFile, boolean withPreview) {
        List<IVFXTagsScenes> listTagsScenes = new ArrayList<>();
        if (ivfxFile != null) {

            int iProgress = 0;
            Statement statement = null;
            ResultSet rs = null;
            String sql;

            try {
                statement = Main.mainConnection.createStatement();


                sql = "SELECT tbl_tags.id, tbl_tags.name, tbl_tags.tag_type_id, tbl_shots.file_id, tbl_files.order_file, tbl_shots.firstFrameNumber " +
                        "FROM tbl_tags_shots " +
                        "INNER JOIN tbl_tags ON tbl_tags_shots.tag_id = tbl_tags.id INNER JOIN tbl_shots ON tbl_tags_shots.shot_id = tbl_shots.id INNER JOIN tbl_files ON tbl_shots.file_id = tbl_files.id " +
                        "WHERE tbl_tags.tag_type_id = 3 " +
                        "AND tbl_shots.file_id = " + ivfxFile.getId() + " " +
                        "GROUP BY tbl_tags.id, tbl_tags.name,  tbl_tags.tag_type_id, tbl_shots.file_id, tbl_files.order_file " +
                        "ORDER BY tbl_files.order_file, MIN(tbl_shots.firstFrameNumber)";

                rs = statement.executeQuery(sql);
                while (rs.next()) {

                    IVFXTagsScenes ivfxTag = new IVFXTagsScenes();
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

                    listTagsScenes.add(ivfxTag);
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

        return listTagsScenes;
    }

    public static IVFXTagsScenes unionScenes(List<IVFXTagsScenes> listScenes) {
        if (!listScenes.isEmpty()) {
            if (listScenes.size() == 1) {
                return listScenes.get(0);
            } else {
                IVFXTagsScenes mainTagScene = listScenes.get(0);

                String where1 = " WHERE (";
                String where2 = " WHERE (";
                for (int i = 1; i < listScenes.size(); i++) {
                    IVFXTagsScenes tmp = listScenes.get(i);
                    where1 += "tag_id = " + tmp.id;
                    where2 += "id = " + tmp.id;
                    if (i != listScenes.size() - 1) {
                        where1 += " OR ";
                        where2 += " OR ";
                    }
                }
                where1 += ")";
                where2 += ")";

                String sql1 = "UPDATE tbl_tags_shots SET tag_id = " + mainTagScene.id + where1;
                String sql2 = "DELETE FROM tbl_tags" + where2;


                try {
                    PreparedStatement ps1 = Main.mainConnection.prepareStatement(sql1);
                    ps1.executeUpdate();
                    ps1.close();


                    PreparedStatement ps2 = Main.mainConnection.prepareStatement(sql2);
                    ps2.executeUpdate();
                    ps2.close();

                } catch (SQLException e) {
                    e.printStackTrace();
                }

                return mainTagScene;
            }
        } else {
            return null;
        }
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

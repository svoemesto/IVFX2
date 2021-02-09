package com.svoemesto.ivfx.tables;

import com.svoemesto.ivfx.Main;
import com.svoemesto.ivfx.utils.ConvertToFxImage;
import com.svoemesto.ivfx.utils.OverlayImage;
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

public class IVFXShotsTypeSize {

    public static final String TYPE_PREFIX = "shot_type_size_";
    private String fxLabelCaption = "-fx-wrap-text: true; -fx-alignment: CENTER";

    private int id;
    private String name;
    private String nameShort;
    private String description;

    private ImageView[] preview = new ImageView[8];
    private Label[] label = new Label[8];

    @Override
    public String toString() {
        return name;
    }

    public boolean isEqual(IVFXShotsTypeSize o) {
        return (this.id == o.id &&
                this.name.equals(o.name));
    }

    public static IVFXShotsTypeSize load(int id) {
        Statement statement = null;
        ResultSet rs = null;
        String sql;

        try {
            statement = Main.mainConnection.createStatement();

            sql = "SELECT * FROM tbl_shots_type_size WHERE id = " + id;
            rs = statement.executeQuery(sql);
            if (rs.next()) {
                IVFXShotsTypeSize shot_type_size = new IVFXShotsTypeSize();
                shot_type_size.id = rs.getInt("id");
                shot_type_size.name = rs.getString("name");
                shot_type_size.nameShort = rs.getString("name_short");
                shot_type_size.description = rs.getString("description");

                String fileName = shot_type_size.getPicture();
                File file = new File(fileName);
                for (int i = 0; i < 8; i++) {
                    shot_type_size.label[i] = new Label(shot_type_size.name);
                    shot_type_size.label[i].setPrefWidth(135);
                    shot_type_size.label[i].setStyle(shot_type_size.fxLabelCaption);
                }

                if (file.exists()) {
                    try {
//                        BufferedImage bufferedImage = ImageIO.read(file);
                        BufferedImage bufferedImage = OverlayImage.resizeImage(ImageIO.read(file), 90,50, null);
                        for (int i = 0; i < 8; i++) {
                            shot_type_size.preview[i] = new ImageView(ConvertToFxImage.convertToFxImage(bufferedImage));
                            shot_type_size.label[i].setGraphic(shot_type_size.preview[i]);
                            shot_type_size.label[i].setContentDisplay(ContentDisplay.TOP);
                        }

                    } catch (IOException e) {
                    }
                }

                return shot_type_size;
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

    public static IVFXShotsTypeSize loadByName(String name) {
        Statement statement = null;
        ResultSet rs = null;
        String sql;

        try {
            statement = Main.mainConnection.createStatement();

            sql = "SELECT * FROM tbl_shots_type_size WHERE name = '" + name + "'";
            rs = statement.executeQuery(sql);
            if (rs.next()) {
                IVFXShotsTypeSize shot_type_size = new IVFXShotsTypeSize();
                shot_type_size.id = rs.getInt("id");
                shot_type_size.name = rs.getString("name");
                shot_type_size.nameShort = rs.getString("name_short");
                shot_type_size.description = rs.getString("description");

                String fileName = shot_type_size.getPicture();
                File file = new File(fileName);
                for (int i = 0; i < 8; i++) {
                    shot_type_size.label[i] = new Label(shot_type_size.name);
                    shot_type_size.label[i].setPrefWidth(135);
                    shot_type_size.label[i].setStyle(shot_type_size.fxLabelCaption);
                }

                if (file.exists()) {
                    try {
//                        BufferedImage bufferedImage = ImageIO.read(file);
                        BufferedImage bufferedImage = OverlayImage.resizeImage(ImageIO.read(file), 90,50, null);
                        for (int i = 0; i < 8; i++) {
                            shot_type_size.preview[i] = new ImageView(ConvertToFxImage.convertToFxImage(bufferedImage));
                            shot_type_size.label[i].setGraphic(shot_type_size.preview[i]);
                            shot_type_size.label[i].setContentDisplay(ContentDisplay.TOP);
                        }

                    } catch (IOException e) {
                    }
                }

                return shot_type_size;
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

    public static IVFXShotsTypeSize loadByShortName(String shortName) {
        Statement statement = null;
        ResultSet rs = null;
        String sql;

        try {
            statement = Main.mainConnection.createStatement();

            sql = "SELECT * FROM tbl_shots_type_size WHERE name_short = '" + shortName + "'";
            rs = statement.executeQuery(sql);
            if (rs.next()) {
                IVFXShotsTypeSize shot_type_size = new IVFXShotsTypeSize();
                shot_type_size.id = rs.getInt("id");
                shot_type_size.name = rs.getString("name");
                shot_type_size.nameShort = rs.getString("name_short");
                shot_type_size.description = rs.getString("description");

                String fileName = shot_type_size.getPicture();
                File file = new File(fileName);
                for (int i = 0; i < 8; i++) {
                    shot_type_size.label[i] = new Label(shot_type_size.name);
                    shot_type_size.label[i].setPrefWidth(135);
                    shot_type_size.label[i].setStyle(shot_type_size.fxLabelCaption);
                }

                if (file.exists()) {
                    try {
//                        BufferedImage bufferedImage = ImageIO.read(file);
                        BufferedImage bufferedImage = OverlayImage.resizeImage(ImageIO.read(file), 90,50, null);
                        for (int i = 0; i < 8; i++) {
                            shot_type_size.preview[i] = new ImageView(ConvertToFxImage.convertToFxImage(bufferedImage));
                            shot_type_size.label[i].setGraphic(shot_type_size.preview[i]);
                            shot_type_size.label[i].setContentDisplay(ContentDisplay.TOP);
                        }

                    } catch (IOException e) {
                    }
                }

                return shot_type_size;
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

    public static List<IVFXShotsTypeSize> loadList() {
        List<IVFXShotsTypeSize> listShotsTypeSize = new ArrayList<>();

        Statement statement = null;
        ResultSet rs = null;
        String sql;

        try {
            statement = Main.mainConnection.createStatement();

            sql = "SELECT * FROM tbl_shots_type_size";

            rs = statement.executeQuery(sql);
            while (rs.next()) {

                IVFXShotsTypeSize shot_type_size = new IVFXShotsTypeSize();
                shot_type_size.id = rs.getInt("id");
                shot_type_size.name = rs.getString("name");
                shot_type_size.nameShort = rs.getString("name_short");
                shot_type_size.description = rs.getString("description");

                String fileName = shot_type_size.getPicture();
                File file = new File(fileName);
                for (int i = 0; i < 8; i++) {
                    shot_type_size.label[i] = new Label(shot_type_size.name);
                    shot_type_size.label[i].setPrefWidth(135);
                    shot_type_size.label[i].setStyle(shot_type_size.fxLabelCaption);
                }

                if (file.exists()) {
                    try {
//                        BufferedImage bufferedImage = ImageIO.read(file);
                        BufferedImage bufferedImage = OverlayImage.resizeImage(ImageIO.read(file), 90,50, null);
                        for (int i = 0; i < 8; i++) {
                            shot_type_size.preview[i] = new ImageView(ConvertToFxImage.convertToFxImage(bufferedImage));
                            shot_type_size.label[i].setGraphic(shot_type_size.preview[i]);
                            shot_type_size.label[i].setContentDisplay(ContentDisplay.TOP);
                        }

                    } catch (IOException e) {
                    }
                }

                listShotsTypeSize.add(shot_type_size);
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

        return listShotsTypeSize;
    }

    // TODO SAVE

    public void save() {
        String sql = "UPDATE tbl_shots_type_size SET " +
                "name = ?, " +
                "name_short = ?, " +
                "description = ? " +
                "WHERE id = ?";
        try {
            PreparedStatement ps = Main.mainConnection.prepareStatement(sql);
            ps.setString(1, this.name);
            ps.setString(2, this.nameShort);
            ps.setString(3, this.description);
            ps.setInt   (4, this.id);
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int getId() {
        return id;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNameShort() {
        return nameShort;
    }

    public void setNameShort(String nameShort) {
        this.nameShort = nameShort;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public ImageView[] getPreview() {
        return preview;
    }

    public ImageView getPreview(int i) {
        return preview[i];
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

    public Label[] getLabel() {
        return label;
    }

    public Label getLabel(int i) {
        return label[i];
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

    public void setPreview(ImageView[] preview) {
        this.preview = preview;
    }

    public void setPreview(ImageView preview, int i) {
        this.preview[i] = preview;
    }


    public String getPicture() {
        String fileName = Main.getMainTypesFolder() + "\\" + TYPE_PREFIX + this.getId() + ".png";
        return fileName;
    }

}

package com.svoemesto.ivfx.tables;

import com.svoemesto.ivfx.Main;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class IVFXTagsShotsProperties {

    private int id;
    private int tagShotId;
    private String name;
    private String value;
    private int order = 0;
    private IVFXTagsShots ivfxTagShot;


    public boolean isEqual(IVFXTagsShotsProperties o) {
        return (this.id == o.id &&
                this.tagShotId == o.tagShotId &&
                this.name.equals(o.name) &&
                this.value.equals(o.value));
    }

    public static IVFXTagsShotsProperties getNewDbInstance(IVFXTagsShots ivfxTagShot) {
        return getNewDbInstance(ivfxTagShot, null, null);
    }
    public static IVFXTagsShotsProperties getNewDbInstance(IVFXTagsShots ivfxTagShot, String name, String value) {
        IVFXTagsShotsProperties tagShotProperty = new IVFXTagsShotsProperties();

        tagShotProperty.tagShotId = ivfxTagShot.getId();
        tagShotProperty.ivfxTagShot = ivfxTagShot;
        tagShotProperty.name = name;
        tagShotProperty.value = value;

        Statement statement = null;
        ResultSet rs = null;
        String sql;

        try {

            statement = Main.mainConnection.createStatement();
            sql = "SELECT COUNT(*) FROM tbl_tags_shots_properties WHERE tag_shot_id = " + tagShotProperty.tagShotId;
            rs = statement.executeQuery(sql);
            if (rs.next()) {
                tagShotProperty.order = rs.getInt(1) + 1;  // индексация столбцов начинается с единицы
            }

            sql = "INSERT INTO tbl_tags_shots_properties (" +
                    "tag_shot_id, tag_shot_property_name, tag_shot_property_value) " +
                    "VALUES(" +
                    tagShotProperty.tagShotId + ", "+
                    "'" + name + "'" + ", " +
                    "'" + value + "'" + ")";

            PreparedStatement ps = Main.mainConnection.prepareStatement(sql);
            ps.executeUpdate(sql, Statement.RETURN_GENERATED_KEYS);
            rs = ps.getGeneratedKeys();
            if (rs.next()) {
                tagShotProperty.id = rs.getInt(1);
                if (name == null && value == null) {
                    name = "свойство №" + tagShotProperty.id;
                    value = "значение №" + tagShotProperty.id;
                    tagShotProperty.name = name;
                    tagShotProperty.value = value;
                    tagShotProperty.save();
                }
                System.out.println("Создана запись для свойства тэга плана с идентификатором " + rs.getInt(1));
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

        return tagShotProperty;
    }

    public static IVFXTagsShotsProperties load(int id, boolean withPreview) {
        Statement statement = null;
        ResultSet rs = null;
        String sql;

        try {
            statement = Main.mainConnection.createStatement();

            sql = "SELECT * FROM tbl_tags_shots_properties WHERE id = " + id;
            rs = statement.executeQuery(sql);
            if (rs.next()) {
                IVFXTagsShotsProperties tagsShotsProperties = new IVFXTagsShotsProperties();
                tagsShotsProperties.id = rs.getInt("id");
                tagsShotsProperties.tagShotId = rs.getInt("tag_shot_id");
                tagsShotsProperties.name = rs.getString("tag_shot_property_name");
                tagsShotsProperties.value = rs.getString("tag_shot_property_value");
                tagsShotsProperties.order = rs.getInt("tag_shot_property_order");
                tagsShotsProperties.ivfxTagShot = IVFXTagsShots.load(tagsShotsProperties.tagShotId, withPreview);
                return tagsShotsProperties;
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

    public static List<IVFXTagsShotsProperties> loadList(IVFXTagsShots ivfxTagsShots, boolean withPreview) {
        List<IVFXTagsShotsProperties> listShotsTagsProperties = new ArrayList<>();

        Statement statement = null;
        ResultSet rs = null;
        String sql;

        try {
            statement = Main.mainConnection.createStatement();

            sql = "SELECT * FROM tbl_tags_shots_properties WHERE tag_shot_id = " + ivfxTagsShots.getId() + " ORDER BY tag_shot_property_order";

            rs = statement.executeQuery(sql);
            while (rs.next()) {

                IVFXTagsShotsProperties tagsShotsProperties = new IVFXTagsShotsProperties();
                tagsShotsProperties.id = rs.getInt("id");
                tagsShotsProperties.tagShotId = rs.getInt("tag_shot_id");
                tagsShotsProperties.name = rs.getString("tag_shot_property_name");
                tagsShotsProperties.value = rs.getString("tag_shot_property_value");
                tagsShotsProperties.order = rs.getInt("tag_shot_property_order");
                tagsShotsProperties.ivfxTagShot = IVFXTagsShots.load(tagsShotsProperties.tagShotId, withPreview);
                listShotsTagsProperties.add(tagsShotsProperties);
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

        return listShotsTagsProperties;
    }


    // TODO SAVE

    public void save() {
        String sql = "UPDATE tbl_tags_shots_properties SET " +
                "tag_shot_id = ?, tag_shot_property_name = ?, tag_shot_property_value = ?, tag_shot_property_order = ? " +
                "WHERE id = ?";
        try {
            PreparedStatement ps = Main.mainConnection.prepareStatement(sql);
            ps.setInt   (1, this.tagShotId);
            ps.setString(2, this.name);
            ps.setString(3, this.value);
            ps.setInt   (4, this.order);
            ps.setInt   (5, this.id);
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

// TODO DELETE


    public void delete() {

        String sql = "DELETE FROM tbl_tags_shots_properties WHERE id = ?";
        try {
            PreparedStatement ps = Main.mainConnection.prepareStatement(sql);
            ps.setInt   (1, this.id);
            ps.executeUpdate();
            ps.close();

            String sql2 = "UPDATE tbl_tags_shots_properties SET tag_shot_property_order = tag_shot_property_order - 1 WHERE tag_shot_id = ? AND tag_shot_property_order > ?";

            PreparedStatement ps2 = Main.mainConnection.prepareStatement(sql2);
            ps2.setInt   (1, this.tagShotId);
            ps2.setInt   (2, this.order);
            ps2.executeUpdate();
            ps2.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public int getId() {
        return id;
    }

    public int getTagShotId() {
        return tagShotId;
    }

    public void setTagShotId(int tagShotId) {
        this.tagShotId = tagShotId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public IVFXTagsShots getIvfxTagShot() {
        return ivfxTagShot;
    }

    public void setIvfxTagShot(IVFXTagsShots ivfxTagShot) {
        this.ivfxTagShot = ivfxTagShot;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }
}

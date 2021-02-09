package com.svoemesto.ivfx.tables;

import com.svoemesto.ivfx.Main;
import javafx.scene.control.ProgressBar;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class IVFXTagsProperties {

    private int id;
    private int tagId;
    private String name;
    private String value;
    private int order = 0;
    private IVFXTags ivfxTag;

    public boolean isEqual(IVFXTagsProperties o) {
        return (this.id == o.id &&
                this.tagId == o.tagId &&
                this.name.equals(o.name) &&
                this.value.equals(o.value));
    }

    public static IVFXTagsProperties getNewDbInstance(IVFXTags ivfxTag) {
        return getNewDbInstance(ivfxTag, null, null);
    }
    public static IVFXTagsProperties getNewDbInstance(IVFXTags ivfxTag, String name, String value) {
        IVFXTagsProperties tagsProperties = new IVFXTagsProperties();

        tagsProperties.tagId = ivfxTag.getId();
        tagsProperties.ivfxTag = ivfxTag;
        tagsProperties.name = name;
        tagsProperties.value = value;

        Statement statement = null;
        ResultSet rs = null;
        String sql;

        try {

            statement = Main.mainConnection.createStatement();
            sql = "SELECT COUNT(*) FROM tbl_tags_properties WHERE tag_id = " + tagsProperties.tagId;
            rs = statement.executeQuery(sql);
            if (rs.next()) {
                tagsProperties.order = rs.getInt(1) + 1;  // индексация столбцов начинается с единицы
            }

            sql = "INSERT INTO tbl_tags_properties (" +
                    "tag_id, tag_property_order, tag_property_name, tag_property_value) " +
                    "VALUES(" +
                    tagsProperties.tagId + ", "+
                    tagsProperties.order + ", "+
                    "'" + name + "'" + ", " +
                    "'" + value + "'" + ")";

            PreparedStatement ps = Main.mainConnection.prepareStatement(sql);
            ps.executeUpdate(sql, Statement.RETURN_GENERATED_KEYS);
            rs = ps.getGeneratedKeys();
            if (rs.next()) {
                tagsProperties.id = rs.getInt(1);
                if (name == null && value == null) {
                    name = "свойство №" + tagsProperties.id;
                    value = "значение №" + tagsProperties.id;
                    tagsProperties.name = name;
                    tagsProperties.value = value;
                    tagsProperties.save();
                }
                System.out.println("Создана запись для свойства тэга с идентификатором " + rs.getInt(1));
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

        return tagsProperties;
    }

    public static IVFXTagsProperties load(int id, boolean withPreview) {
        Statement statement = null;
        ResultSet rs = null;
        String sql;

        try {
            statement = Main.mainConnection.createStatement();

            sql = "SELECT * FROM tbl_tags_properties WHERE id = " + id;
            rs = statement.executeQuery(sql);
            if (rs.next()) {
                IVFXTagsProperties tagsProperties = new IVFXTagsProperties();
                tagsProperties.id = rs.getInt("id");
                tagsProperties.tagId = rs.getInt("tag_id");
                tagsProperties.order = rs.getInt("tag_property_order");
                tagsProperties.name = rs.getString("tag_property_name");
                tagsProperties.value = rs.getString("tag_property_value");
                tagsProperties.ivfxTag = IVFXTags.load(tagsProperties.tagId, withPreview);
                return tagsProperties;
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

    public static IVFXTagsProperties loadByName(int tagId, String name, boolean withPreview) {
        Statement statement = null;
        ResultSet rs = null;
        String sql;

        try {
            statement = Main.mainConnection.createStatement();

            sql = "SELECT * FROM tbl_tags_properties WHERE tag_id = " + tagId + " AND tag_property_name = '" + name + "'" + " ORDER BY tag_property_order";
            rs = statement.executeQuery(sql);
            if (rs.next()) {
                IVFXTagsProperties tagsProperties = new IVFXTagsProperties();
                tagsProperties.id = rs.getInt("id");
                tagsProperties.tagId = rs.getInt("tag_id");
                tagsProperties.order = rs.getInt("tag_property_order");
                tagsProperties.name = rs.getString("tag_property_name");
                tagsProperties.value = rs.getString("tag_property_value");
                tagsProperties.ivfxTag = IVFXTags.load(tagsProperties.tagId, withPreview);
                return tagsProperties;
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

    public static List<IVFXTagsProperties> loadList(IVFXTags ivfxTag, boolean withPreview) {
        List<IVFXTagsProperties> listTagsProperties = new ArrayList<>();

        Statement statement = null;
        ResultSet rs = null;
        String sql;

        try {
            statement = Main.mainConnection.createStatement();

            sql = "SELECT * FROM tbl_tags_properties WHERE tag_id = " + ivfxTag.getId() + " ORDER BY tag_property_order";

            rs = statement.executeQuery(sql);
            while (rs.next()) {

                IVFXTagsProperties tagsProperties = new IVFXTagsProperties();
                tagsProperties.id = rs.getInt("id");
                tagsProperties.tagId = rs.getInt("tag_id");
                tagsProperties.order = rs.getInt("tag_property_order");
                tagsProperties.name = rs.getString("tag_property_name");
                tagsProperties.value = rs.getString("tag_property_value");
                tagsProperties.ivfxTag = IVFXTags.load(tagsProperties.tagId,withPreview);
                listTagsProperties.add(tagsProperties);
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

        return listTagsProperties;
    }

    // TODO SAVE

    public void save() {
        String sql = "UPDATE tbl_tags_properties SET " +
                "tag_id = ?, tag_property_name = ?, tag_property_value = ?, tag_property_order = ? " +
                "WHERE id = ?";
        try {
            PreparedStatement ps = Main.mainConnection.prepareStatement(sql);
            ps.setInt   (1, this.tagId);
            ps.setString(2, this.name);
            ps.setString(3, this.value);
            ps.setInt   (4, this.order);
            ps.setInt   (5, this.id);
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (this.name.equals("name")) {
            this.ivfxTag.setName(this.value);
            this.ivfxTag.save();
        }
    }

// TODO DELETE


    public void delete() {

        String sql = "DELETE FROM tbl_tags_properties WHERE id = ?";
        try {
            PreparedStatement ps = Main.mainConnection.prepareStatement(sql);
            ps.setInt   (1, this.id);
            ps.executeUpdate();
            ps.close();

            String sql2 = "UPDATE tbl_tags_properties SET tag_property_order = tag_property_order - 1 WHERE tag_id = ? AND tag_property_order > ?";

            PreparedStatement ps2 = Main.mainConnection.prepareStatement(sql2);
            ps2.setInt   (1, this.tagId);
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

    public int getTagId() {
        return tagId;
    }

    public void setTagId(int tagId) {
        this.tagId = tagId;
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

    public IVFXTags getIvfxTag() {
        return ivfxTag;
    }

    public void setIvfxTag(IVFXTags ivfxTag) {
        this.ivfxTag = ivfxTag;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }
}

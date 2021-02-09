package com.svoemesto.ivfx.tables;

import com.svoemesto.ivfx.Main;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class IVFXTagsTagsProperties {

    private int id;
    private int tagTagId;
    private String name;
    private String value;
    private int order = 0;
    private IVFXTagsTags ivfxTagTag;


    public boolean isEqual(IVFXTagsTagsProperties o) {
        return (this.id == o.id &&
                this.tagTagId == o.tagTagId &&
                this.name.equals(o.name) &&
                this.value.equals(o.value));
    }

    public static IVFXTagsTagsProperties getNewDbInstance(IVFXTagsTags ivfxTagTag) {
        return getNewDbInstance(ivfxTagTag, null, null);
    }
    public static IVFXTagsTagsProperties getNewDbInstance(IVFXTagsTags ivfxTagTag, String name, String value) {
        IVFXTagsTagsProperties tagTagProperty = new IVFXTagsTagsProperties();

        tagTagProperty.tagTagId = ivfxTagTag.getId();
        tagTagProperty.ivfxTagTag = ivfxTagTag;
        tagTagProperty.name = name;
        tagTagProperty.value = value;

        Statement statement = null;
        ResultSet rs = null;
        String sql;

        try {

            statement = Main.mainConnection.createStatement();
            sql = "SELECT COUNT(*) FROM tbl_tags_tags_properties WHERE tag_tag_id = " + tagTagProperty.tagTagId;
            rs = statement.executeQuery(sql);
            if (rs.next()) {
                tagTagProperty.order = rs.getInt(1) + 1;  // индексация столбцов начинается с единицы
            }

            sql = "INSERT INTO tbl_tags_tags_properties (" +
                    "tag_tag_id, tag_tag_property_order, tag_tag_property_name, tag_tag_property_value) " +
                    "VALUES(" +
                    tagTagProperty.tagTagId + ", "+
                    tagTagProperty.order + ", "+
                    "'" + name + "'" + ", " +
                    "'" + value + "'" + ")";

            PreparedStatement ps = Main.mainConnection.prepareStatement(sql);
            ps.executeUpdate(sql, Statement.RETURN_GENERATED_KEYS);
            rs = ps.getGeneratedKeys();
            if (rs.next()) {
                tagTagProperty.id = rs.getInt(1);
                if (name == null && value == null) {
                    name = "свойство №" + tagTagProperty.id;
                    value = "значение №" + tagTagProperty.id;
                    tagTagProperty.name = name;
                    tagTagProperty.value = value;
                    tagTagProperty.save();
                }
                System.out.println("Создана запись для свойства тэга тэга с идентификатором " + rs.getInt(1));
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

        return tagTagProperty;
    }

    public static IVFXTagsTagsProperties load(int id, boolean withPreview) {
        Statement statement = null;
        ResultSet rs = null;
        String sql;

        try {
            statement = Main.mainConnection.createStatement();

            sql = "SELECT * FROM tbl_tags_tags_properties WHERE id = " + id;
            rs = statement.executeQuery(sql);
            if (rs.next()) {
                IVFXTagsTagsProperties tagsTagsProperties = new IVFXTagsTagsProperties();
                tagsTagsProperties.id = rs.getInt("id");
                tagsTagsProperties.tagTagId = rs.getInt("tag_tag_id");
                tagsTagsProperties.order = rs.getInt("tag_tag_property_order");
                tagsTagsProperties.name = rs.getString("tag_tag_property_name");
                tagsTagsProperties.value = rs.getString("tag_tag_property_value");
                tagsTagsProperties.ivfxTagTag = IVFXTagsTags.load(tagsTagsProperties.tagTagId, withPreview);
                return tagsTagsProperties;
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

    public static List<IVFXTagsTagsProperties> loadList(IVFXTagsTags ivfxTagsTags, boolean withPreview) {
        List<IVFXTagsTagsProperties> listTagsTagsProperties = new ArrayList<>();

        Statement statement = null;
        ResultSet rs = null;
        String sql;

        try {
            statement = Main.mainConnection.createStatement();

            sql = "SELECT * FROM tbl_tags_tags_properties WHERE tag_tag_id = " + ivfxTagsTags.getId() + " ORDER BY tag_tag_property_order";

            rs = statement.executeQuery(sql);
            while (rs.next()) {

                IVFXTagsTagsProperties tagsTagsProperties = new IVFXTagsTagsProperties();
                tagsTagsProperties.id = rs.getInt("id");
                tagsTagsProperties.tagTagId = rs.getInt("tag_tag_id");
                tagsTagsProperties.order = rs.getInt("tag_tag_property_order");
                tagsTagsProperties.name = rs.getString("tag_tag_property_name");
                tagsTagsProperties.value = rs.getString("tag_tag_property_value");
                tagsTagsProperties.ivfxTagTag = IVFXTagsTags.load(tagsTagsProperties.tagTagId, withPreview);
                listTagsTagsProperties.add(tagsTagsProperties);
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

        return listTagsTagsProperties;
    }


    // TODO SAVE

    public void save() {
        String sql = "UPDATE tbl_tags_tags_properties SET " +
                "tag_tag_id = ?, tag_tag_property_name = ?, tag_tag_property_value = ?, tag_tag_property_order = ? " +
                "WHERE id = ?";
        try {
            PreparedStatement ps = Main.mainConnection.prepareStatement(sql);
            ps.setInt   (1, this.tagTagId);
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

        String sql = "DELETE FROM tbl_tags_tags_properties WHERE id = ?";
        try {
            PreparedStatement ps = Main.mainConnection.prepareStatement(sql);
            ps.setInt   (1, this.id);
            ps.executeUpdate();
            ps.close();

            String sql2 = "UPDATE tbl_tags_tags_properties SET tag_tag_property_order = tag_tag_property_order - 1 WHERE tag_tag_id = ? AND tag_tag_property_order > ?";

            PreparedStatement ps2 = Main.mainConnection.prepareStatement(sql2);
            ps2.setInt   (1, this.tagTagId);
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

    public int getTagTagId() {
        return tagTagId;
    }

    public void setTagTagId(int tagTagId) {
        this.tagTagId = tagTagId;
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

    public IVFXTagsTags getIvfxTagTag() {
        return ivfxTagTag;
    }

    public void setIvfxTagTag(IVFXTagsTags ivfxTagTag) {
        this.ivfxTagTag = ivfxTagTag;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }
}

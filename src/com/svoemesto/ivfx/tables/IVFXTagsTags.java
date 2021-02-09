package com.svoemesto.ivfx.tables;

import com.svoemesto.ivfx.Main;
import javafx.scene.control.Label;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class IVFXTagsTags {

    private int id;
    private int tagParentId;
    private int tagChildId;
    private int order = 0;
    private IVFXTags ivfxTagParent;
    private IVFXTags ivfxTagChild;


    public boolean isEqual(IVFXTagsTags o) {
        return (this.id == o.id &&
                this.tagParentId == o.tagParentId &&
                this.tagChildId == o.tagChildId);
    }

    public static IVFXTagsTags getNewDbInstance(IVFXTags ivfxTagParent, IVFXTags ivfxTagChild, boolean withPreview) {
        IVFXTagsTags tagsTags = IVFXTagsTags.load(ivfxTagParent, ivfxTagChild, withPreview);

        if (tagsTags != null) {
            return tagsTags;
        }

        tagsTags = new IVFXTagsTags();

        tagsTags.tagParentId = ivfxTagParent.getId();
        tagsTags.tagChildId = ivfxTagChild.getId();
        tagsTags.ivfxTagParent = ivfxTagParent;
        tagsTags.ivfxTagChild = ivfxTagChild;

        Statement statement = null;
        ResultSet rs = null;
        String sql;

        try {

            statement = Main.mainConnection.createStatement();
            sql = "SELECT COUNT(*) FROM tbl_tags_tags WHERE tag_parent_id = " + tagsTags.tagParentId;
            rs = statement.executeQuery(sql);
            if (rs.next()) {
                tagsTags.order = rs.getInt(1) + 1;  // индексация столбцов начинается с единицы
            }

            sql = "INSERT INTO tbl_tags_tags (" +
                    "tag_tag_order, tag_parent_id, tag_child_id) " +
                    "VALUES(" +
                    tagsTags.order + ", "+
                    tagsTags.tagParentId + ", "+
                    tagsTags.tagChildId + ")";

            PreparedStatement ps = Main.mainConnection.prepareStatement(sql);
            ps.executeUpdate(sql, Statement.RETURN_GENERATED_KEYS);
            rs = ps.getGeneratedKeys();
            if (rs.next()) {
                tagsTags.id = rs.getInt(1);
                System.out.println("Создана запись для тэга-тэга с идентификатором " + rs.getInt(1));
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

        return tagsTags;
    }

    public static IVFXTagsTags load(int id, boolean withPreview) {
        Statement statement = null;
        ResultSet rs = null;
        String sql;

        try {
            statement = Main.mainConnection.createStatement();

            sql = "SELECT * FROM tbl_tags_tags WHERE id = " + id;
            rs = statement.executeQuery(sql);
            if (rs.next()) {
                IVFXTagsTags tagsTags = new IVFXTagsTags();
                tagsTags.id = rs.getInt("id");
                tagsTags.tagParentId = rs.getInt("tag_parent_id");
                tagsTags.tagChildId = rs.getInt("tag_child_id");
                tagsTags.order = rs.getInt("tag_tag_order");
                tagsTags.ivfxTagParent = IVFXTags.load(tagsTags.tagParentId,withPreview);
                tagsTags.ivfxTagChild = IVFXTags.load(tagsTags.tagChildId,withPreview);
                return tagsTags;
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

    public static IVFXTagsTags load(IVFXTags ivfxTagParent, IVFXTags ivfxTagChild, boolean withPreview) {
        Statement statement = null;
        ResultSet rs = null;
        String sql;

        try {
            statement = Main.mainConnection.createStatement();

            sql = "SELECT * FROM tbl_tags_tags WHERE tag_parent_id = " + ivfxTagParent.getId() + " AND tag_child_id = " + ivfxTagChild.getId() + " ORDER BY tag_tag_order";
            rs = statement.executeQuery(sql);
            if (rs.next()) {
                IVFXTagsTags tagsTags = new IVFXTagsTags();
                tagsTags.id = rs.getInt("id");
                tagsTags.tagParentId = rs.getInt("tag_parent_id");
                tagsTags.tagChildId = rs.getInt("tag_child_id");
                tagsTags.order = rs.getInt("tag_tag_order");
                tagsTags.ivfxTagParent = IVFXTags.load(tagsTags.tagParentId,withPreview);
                tagsTags.ivfxTagChild = IVFXTags.load(tagsTags.tagChildId,withPreview);
                return tagsTags;
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

    public static List<IVFXTagsTags> loadList(IVFXTags ivfxTagParent, boolean withPreview) {
        int[] arrayTagsTypesId = null;
        return loadList(ivfxTagParent, false, withPreview, arrayTagsTypesId);
    }
    public static List<IVFXTagsTags> loadList(IVFXTags ivfxTag, boolean forChild, boolean withPreview, int[] arrayTagsTypesId) {
        List<IVFXTagsTags> listTagsTags = new ArrayList<>();

        Statement statement = null;
        ResultSet rs = null;
        String sql;

        try {
            statement = Main.mainConnection.createStatement();

            if (forChild) {
                if (arrayTagsTypesId == null || arrayTagsTypesId.length == 0) {
                    sql = "SELECT * FROM tbl_tags_tags WHERE tag_child_id = " + ivfxTag.getId()+ " ORDER BY tag_tag_order";
                } else {
                    String tmpTagTypes = "";
                    for (int i = 0; i < arrayTagsTypesId.length; i++) {
                        tmpTagTypes += "tbl_tags.tag_type_id = " + arrayTagsTypesId[i];
                        if (i != arrayTagsTypesId.length - 1) tmpTagTypes += " OR ";
                    }
                    sql = "SELECT " +
                            "  tbl_tags_tags.* " +
                            "FROM tbl_tags_tags " +
                            "  INNER JOIN tbl_tags " +
                            "    ON tbl_tags_tags.tag_parent_id = tbl_tags.id " +
                            "WHERE tbl_tags_tags.tag_child_id = " + ivfxTag.getId() + " " +
                            "AND (" + tmpTagTypes + ") " +
                            "ORDER BY tbl_tags_tags.tag_tag_order";
                }
            } else {
                if (arrayTagsTypesId == null || arrayTagsTypesId.length == 0) {
                    sql = "SELECT * FROM tbl_tags_tags WHERE tag_parent_id = " + ivfxTag.getId()+ " ORDER BY tag_tag_order";
                } else {
                    String tmpTagTypes = "";
                    for (int i = 0; i < arrayTagsTypesId.length; i++) {
                        tmpTagTypes += "tbl_tags.tag_type_id = " + arrayTagsTypesId[i];
                        if (i != arrayTagsTypesId.length - 1) tmpTagTypes += " OR ";
                    }
                    sql = "SELECT " +
                            "  tbl_tags_tags.* " +
                            "FROM tbl_tags_tags " +
                            "  INNER JOIN tbl_tags " +
                            "    ON tbl_tags_tags.tag_child_id = tbl_tags.id " +
                            "WHERE tbl_tags_tags.tag_parent_id = " + ivfxTag.getId() + " " +
                            "AND (" + tmpTagTypes + ") " +
                            "ORDER BY tbl_tags_tags.tag_tag_order";
                }

            }

            rs = statement.executeQuery(sql);
            while (rs.next()) {

                IVFXTagsTags tagsTags = new IVFXTagsTags();
                tagsTags.id = rs.getInt("id");
                tagsTags.tagParentId = rs.getInt("tag_parent_id");
                tagsTags.tagChildId = rs.getInt("tag_child_id");
                tagsTags.order = rs.getInt("tag_tag_order");
                tagsTags.ivfxTagParent = IVFXTags.load(tagsTags.tagParentId,withPreview);
                tagsTags.ivfxTagChild = IVFXTags.load(tagsTags.tagChildId,withPreview);
                listTagsTags.add(tagsTags);
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

        return listTagsTags;
    }

    // TODO SAVE

    public void save() {
        String sql = "UPDATE tbl_tags_tags SET " +
                "tag_parent_id = ?, tag_child_id = ?, tag_tag_order = ? " +
                "WHERE id = ?";
        try {
            PreparedStatement ps = Main.mainConnection.prepareStatement(sql);
            ps.setInt   (1, this.tagParentId);
            ps.setInt   (2, this.tagChildId);
            ps.setInt   (3, this.order);
            ps.setInt   (4, this.id);
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

// TODO DELETE


    public void delete() {

        String sql = "DELETE FROM tbl_tags_tags WHERE id = ?";
        try {
            PreparedStatement ps = Main.mainConnection.prepareStatement(sql);
            ps.setInt   (1, this.id);
            ps.executeUpdate();
            ps.close();

            String sql2 = "UPDATE tbl_tags_tags SET tag_tag_order = tag_tag_order - 1 WHERE tag_parent_id = ? AND tag_tag_order > ?";

            PreparedStatement ps2 = Main.mainConnection.prepareStatement(sql2);
            ps2.setInt   (1, this.tagParentId);
            ps2.setInt   (2, this.order);
            ps2.executeUpdate();
            ps2.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String getNameParent() {
        return ivfxTagParent.getName();
    }

    public String getNameChildren() {
        return ivfxTagChild.getName();
    }

    public Label getLabelParent() {
        return ivfxTagParent.getLabel1();
    }

    public Label getLabelChildren() {
        return ivfxTagChild.getLabel1();
    }

    public String getTagTypeNameParent() {
        return ivfxTagParent.getTagTypeName();
    }

    public String getTagTypeNameChildren() {
        return ivfxTagChild.getTagTypeName();
    }

    public int getId() {
        return id;
    }

    public int getTagParentId() {
        return tagParentId;
    }

    public void setTagParentId(int tagParentId) {
        this.tagParentId = tagParentId;
    }

    public int getTagChildId() {
        return tagChildId;
    }

    public void setTagChildId(int tagChildId) {
        this.tagChildId = tagChildId;
    }

    public IVFXTags getIvfxTagParent() {
        return ivfxTagParent;
    }

    public void setIvfxTagParent(IVFXTags ivfxTagParent) {
        this.ivfxTagParent = ivfxTagParent;
    }

    public IVFXTags getIvfxTagChild() {
        return ivfxTagChild;
    }

    public void setIvfxTagChild(IVFXTags ivfxTagChild) {
        this.ivfxTagChild = ivfxTagChild;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }
}

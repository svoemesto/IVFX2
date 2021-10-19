package com.svoemesto.ivfx.tables;

import com.svoemesto.ivfx.Main;
import javafx.scene.control.ProgressBar;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class IVFXTagsFavorites implements Comparable<IVFXTagsFavorites>{

    private int id;
    private int order = 0;
    private int tagId;
    private IVFXTags ivfxTag;

    //TODO ISEQUAL

    public boolean isEqual(IVFXTagsFavorites o) {
        return (this.id == o.id &&
                this.order == o.order &&
                this.tagId == o.tagId);
    }

    @Override
    public String toString() {
        return "FAV" + ivfxTag.getName();
    }

    // пустой конструктор
    public IVFXTagsFavorites() {
    }

    public static IVFXTagsFavorites getNewDbInstance(IVFXTags ivfxTag) {

        IVFXTagsFavorites ivfxTagFavorite = load(ivfxTag);

        if (ivfxTagFavorite != null) return ivfxTagFavorite;

        ivfxTagFavorite = new IVFXTagsFavorites();
        ivfxTagFavorite.tagId = ivfxTag.getId();
        ivfxTagFavorite.ivfxTag = ivfxTag;
        ivfxTagFavorite.order = 0;

        Statement statement = null;
        ResultSet rs = null;
        String sql;

        try {
            statement = Main.mainConnection.createStatement();

            sql = "SELECT COUNT(*) FROM tbl_tags_favorites";
            rs = statement.executeQuery(sql);
            if (rs.next()) {
                ivfxTagFavorite.order = rs.getInt(1) + 1;  // индексация столбцов начинается с единицы
            }

            sql = "INSERT INTO tbl_tags_favorites (" +
                    "tag_id, " +
                    "order_tag_favorite) " +
                    "VALUES(" +
                    ivfxTag.getId() + "," +
                    ivfxTagFavorite.order + ")";

            PreparedStatement ps = Main.mainConnection.prepareStatement(sql);
            ps.executeUpdate(sql, Statement.RETURN_GENERATED_KEYS);
            rs = ps.getGeneratedKeys();
            if (rs.next()) {
                ivfxTagFavorite.id = rs.getInt(1);
                System.out.println("Создана запись для tags_favorites «" + ivfxTag.getName() + "» с идентификатором " + rs.getInt(1));
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
        return ivfxTagFavorite;
    }

    // TODO OVERRIDE

    @Override
    public int compareTo(IVFXTagsFavorites o) {
        Integer a = getOrder();
        Integer b = o.getOrder();
        return a.compareTo(b);
    }

    public static IVFXTagsFavorites load(int id) {
        Statement statement = null;
        ResultSet rs = null;
        String sql;

        try {
            statement = Main.mainConnection.createStatement();

            sql = "SELECT * FROM tbl_tags_favorites WHERE id = " + id;
            rs = statement.executeQuery(sql);
            if (rs.next()) {
                IVFXTagsFavorites tagFav = new IVFXTagsFavorites();
                tagFav.id = rs.getInt("id");
                tagFav.tagId = rs.getInt("tag_id");
                tagFav.order = rs.getInt("order_tag_favorite");
                tagFav.ivfxTag = IVFXTags.load(tagFav.tagId,true);
                return tagFav;
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

    public static IVFXTagsFavorites load(IVFXTags tag) {
        Statement statement = null;
        ResultSet rs = null;
        String sql;

        try {
            statement = Main.mainConnection.createStatement();

            sql = "SELECT * FROM tbl_tags_favorites WHERE tag_id = " + tag.getId();
            rs = statement.executeQuery(sql);
            if (rs.next()) {
                IVFXTagsFavorites tagFav = new IVFXTagsFavorites();
                tagFav.id = rs.getInt("id");
                tagFav.tagId = rs.getInt("tag_id");
                tagFav.order = rs.getInt("order_tag_favorite");
                tagFav.ivfxTag = IVFXTags.load(tagFav.tagId,true);
                return tagFav;
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

    public static List<IVFXTagsFavorites> loadList() {
        List<IVFXTagsFavorites> listTagsFavorites = new ArrayList<>();

        int iProgress = 0;
        Statement statement = null;
        ResultSet rs = null;
        String sql;

        try {
            statement = Main.mainConnection.createStatement();

            sql = "SELECT * FROM tbl_tags_favorites ORDER BY order_tag_favorite";

            rs = statement.executeQuery(sql);
            while (rs.next()) {

                IVFXTagsFavorites tagFav = new IVFXTagsFavorites();
                tagFav.id = rs.getInt("id");
                tagFav.tagId = rs.getInt("tag_id");
                tagFav.order = rs.getInt("order_tag_favorite");
                tagFav.ivfxTag = IVFXTags.load(tagFav.tagId, true);
                listTagsFavorites.add(tagFav);

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

        return listTagsFavorites;
    }

// TODO SAVE

    public void save() {
        String sql = "UPDATE tbl_tags_favorites SET " +
                "tag_id = ?, " +
                "order_tag_favorite = ? " +
                "WHERE id = ?";
        try {
            PreparedStatement ps = Main.mainConnection.prepareStatement(sql);
            ps.setInt   (1, this.tagId);
            ps.setInt   (2, this.order);
            ps.setInt   (3, this.id);
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

// TODO DELETE


    public void delete() {
        int oldOrder = this.getOrder();
        String sql1 = "DELETE FROM tbl_tags_favorites WHERE id = ?";
        try {
            PreparedStatement ps = Main.mainConnection.prepareStatement(sql1);
            ps.setInt   (1, this.id);
            ps.executeUpdate();
            ps.close();

            String sql2 = "UPDATE tbl_tags_favorites SET order_tag_favorite = order_tag_favorite - ? WHERE order_tag_favorite > ?";
            ps = Main.mainConnection.prepareStatement(sql2);
            ps.setInt   (1, 1);
            ps.setInt   (2, oldOrder);
            ps.executeUpdate();
            ps.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static boolean isTagExistInFavorites(IVFXTags tag) {

        Statement statement = null;
        ResultSet rs = null;
        String sql;

        try {
            statement = Main.mainConnection.createStatement();
            sql = "SELECT COUNT(*) FROM tbl_tags_favorites WHERE tag_id = " + tag.getId();
            rs = statement.executeQuery(sql);
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;

    }

    public void moveUp() {
        int newOrder = this.getOrder() - 1;
        this.setNewOrder(newOrder);
    }

    public void moveDown() {
        int newOrder = this.getOrder() + 1;
        this.setNewOrder(newOrder);
    }

    public void setOrderBefore(IVFXTagsFavorites tagFav) {
        this.setNewOrder(tagFav.getOrder());
    }

    public void setNewOrder(int newOrder) {

        Statement statement = null;
        ResultSet rs = null;
        String sql;

        int oldOrder = getOrder();
        int minOrder = 1;
        if (newOrder < minOrder || oldOrder == newOrder) return;
        int maxOrder = 0;

        try {
            statement = Main.mainConnection.createStatement();
            sql = "SELECT COUNT(*) FROM tbl_tags_favorites";
            rs = statement.executeQuery(sql);
            if (rs.next()) {
                maxOrder = rs.getInt(1);
            }
            if (newOrder > maxOrder) newOrder = maxOrder;
            if (maxOrder == 0) return;

            if (newOrder > oldOrder) {
                sql = "UPDATE tbl_tags_favorites SET order_tag_favorite = order_tag_favorite - ? WHERE order_tag_favorite > ? AND order_tag_favorite <= ?";
            } else {
                sql = "UPDATE tbl_tags_favorites SET order_tag_favorite = order_tag_favorite + ? WHERE order_tag_favorite < ? AND order_tag_favorite >= ?";
            }
            PreparedStatement ps = Main.mainConnection.prepareStatement(sql);
            ps.setInt   (1, 1);
            ps.setInt   (2, oldOrder);
            ps.setInt   (3, newOrder);
            ps.executeUpdate();
            ps.close();

            this.setOrder(newOrder);
            this.save();

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }



    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public int getTagId() {
        return tagId;
    }

    public void setTagId(int tagId) {
        this.tagId = tagId;
    }

    public IVFXTags getIvfxTag() {
        return ivfxTag;
    }

    public void setIvfxTag(IVFXTags ivfxTag) {
        this.ivfxTag = ivfxTag;
    }
}

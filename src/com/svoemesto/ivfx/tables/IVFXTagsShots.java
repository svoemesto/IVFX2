package com.svoemesto.ivfx.tables;

import com.svoemesto.ivfx.Main;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class IVFXTagsShots {
    private int id;
    private int tagId;
    private int shotId;
    private boolean isMain = true;
    private int parentTagsShotsId = 0;
    private int order = 0;
    private IVFXTags ivfxTag;
    private IVFXShots ivfxShot;
    private IVFXTagsShots parentIvfxTagsShots = null;
    private BooleanProperty propIsMain;

    public boolean isEqual(IVFXTagsShots o) {
        return (this.id == o.id &&
                this.tagId == o.tagId &&
                this.shotId == o.shotId &&
                this.parentTagsShotsId == o.parentTagsShotsId);
    }

    public static IVFXTagsShots getNewDbInstance(IVFXTags ivfxTag, IVFXShots ivfxShot) {
        return getNewDbInstance(ivfxTag, ivfxShot,  null);
    }
    public static IVFXTagsShots getNewDbInstance(IVFXTags ivfxTag, IVFXShots ivfxShot, IVFXTagsShots parentIvfxTagsShots) {
        IVFXTagsShots shotsTags = new IVFXTagsShots();

        shotsTags.tagId = ivfxTag.getId();
        shotsTags.shotId = ivfxShot.getId();
        shotsTags.isMain = true;
        shotsTags.ivfxTag = ivfxTag;
        shotsTags.ivfxShot = ivfxShot;
        if (parentIvfxTagsShots != null) {
            shotsTags.parentTagsShotsId = parentIvfxTagsShots.getId();
            shotsTags.parentIvfxTagsShots = parentIvfxTagsShots;
        } else {
            shotsTags.parentTagsShotsId = 0;
            shotsTags.parentIvfxTagsShots = null;
        }
        shotsTags.propIsMain = new SimpleBooleanProperty(shotsTags.isMain);

        Statement statement = null;
        ResultSet rs = null;
        String sql;

        try {

            statement = Main.mainConnection.createStatement();
            sql = "SELECT COUNT(*) FROM tbl_tags_shots WHERE shot_id = " + shotsTags.shotId;
            rs = statement.executeQuery(sql);
            if (rs.next()) {
                shotsTags.order = rs.getInt(1) + 1;  // индексация столбцов начинается с единицы
            }

            sql = "INSERT INTO tbl_tags_shots (" +
                    "shot_id, tag_id, tag_is_main, parent_tag_shot_id, tag_shot_order) " +
                    "VALUES(" +
                    shotsTags.shotId + ", "+
                    shotsTags.tagId + ", "+
                    (shotsTags.isMain ? 1 : 0) + ", "+
                    shotsTags.order + ", " +
                    shotsTags.parentTagsShotsId + ")";

            PreparedStatement ps = Main.mainConnection.prepareStatement(sql);
            ps.executeUpdate(sql, Statement.RETURN_GENERATED_KEYS);
            rs = ps.getGeneratedKeys();
            if (rs.next()) {
                shotsTags.id = rs.getInt(1);
                System.out.println("Создана запись для тэга плана с идентификатором " + rs.getInt(1));
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

        return shotsTags;
    }

    public static IVFXTagsShots load(int id, boolean withPreview) {
        Statement statement = null;
        ResultSet rs = null;
        String sql;

        try {
            statement = Main.mainConnection.createStatement();

            sql = "SELECT * FROM tbl_tags_shots WHERE id = " + id;
            rs = statement.executeQuery(sql);
            if (rs.next()) {
                IVFXTagsShots shotsTags = new IVFXTagsShots();
                shotsTags.id = rs.getInt("id");
                shotsTags.shotId = rs.getInt("shot_id");
                shotsTags.tagId = rs.getInt("tag_id");
                shotsTags.isMain = rs.getBoolean("tag_is_main");
                shotsTags.parentTagsShotsId = rs.getInt("parent_tag_shot_id");
                shotsTags.order = rs.getInt("tag_shot_order");
                shotsTags.ivfxShot = IVFXShots.load(shotsTags.shotId, withPreview);
                shotsTags.ivfxTag = IVFXTags.load(shotsTags.tagId, withPreview);
                if (shotsTags.parentTagsShotsId == 0) {
                    shotsTags.parentIvfxTagsShots = null;
                } else {
                    shotsTags.parentIvfxTagsShots = IVFXTagsShots.load(shotsTags.parentTagsShotsId,withPreview);
                }
                shotsTags.propIsMain = new SimpleBooleanProperty(shotsTags.isMain);
                return shotsTags;
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

    public static IVFXTagsShots load(IVFXTags tag, IVFXShots shot,  boolean withPreview) {
        Statement statement = null;
        ResultSet rs = null;
        String sql;

        try {
            statement = Main.mainConnection.createStatement();

            sql = "SELECT * FROM tbl_tags_shots WHERE tag_id = " + tag.getId() + " AND shot_id = " + shot.getId();
            rs = statement.executeQuery(sql);
            if (rs.next()) {
                IVFXTagsShots shotsTags = new IVFXTagsShots();
                shotsTags.id = rs.getInt("id");
                shotsTags.shotId = rs.getInt("shot_id");
                shotsTags.tagId = rs.getInt("tag_id");
                shotsTags.isMain = rs.getBoolean("tag_is_main");
                shotsTags.parentTagsShotsId = rs.getInt("parent_tag_shot_id");
                shotsTags.order = rs.getInt("tag_shot_order");
                shotsTags.ivfxShot = IVFXShots.load(shotsTags.shotId, withPreview);
                shotsTags.ivfxTag = IVFXTags.load(shotsTags.tagId, withPreview);
                if (shotsTags.parentTagsShotsId == 0) {
                    shotsTags.parentIvfxTagsShots = null;
                } else {
                    shotsTags.parentIvfxTagsShots = IVFXTagsShots.load(shotsTags.parentTagsShotsId,withPreview);
                }
                shotsTags.propIsMain = new SimpleBooleanProperty(shotsTags.isMain);
                return shotsTags;
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

    public static List<IVFXTagsShots> loadList(IVFXShots ivfxShot, boolean withPreview) {
        return loadList(ivfxShot, withPreview, null);
    }
    public static List<IVFXTagsShots> loadList(IVFXShots ivfxShot, boolean withPreview, int[] arrayTagsTypesId) {
        List<IVFXTagsShots> listShotsTags = new ArrayList<>();

        Statement statement = null;
        ResultSet rs = null;
        String sql;

        try {
            statement = Main.mainConnection.createStatement();

            if (arrayTagsTypesId == null) {
                sql = "SELECT tbl_tags_shots.* FROM tbl_tags_shots INNER JOIN tbl_tags ON tbl_tags_shots.tag_id = tbl_tags.id WHERE shot_id = " + ivfxShot.getId() + " ORDER BY tbl_tags.tag_type_id, tbl_tags_shots.tag_shot_order";
            } else {
                sql = "SELECT tbl_tags_shots.* FROM tbl_tags_shots INNER JOIN tbl_tags ON tbl_tags_shots.tag_id = tbl_tags.id WHERE tbl_tags_shots.shot_id = " + ivfxShot.getId() + " AND (";
                for (int i = 0; i < arrayTagsTypesId.length; i++) {
                    sql += "tbl_tags.tag_type_id = " + arrayTagsTypesId[i];
                    if (i != arrayTagsTypesId.length-1) sql += " OR ";
                }
                sql += ") ORDER BY tbl_tags.tag_type_id, tbl_tags_shots.tag_shot_order";
            }

            rs = statement.executeQuery(sql);
            while (rs.next()) {

                IVFXTagsShots shotsTags = new IVFXTagsShots();
                shotsTags.id = rs.getInt("id");
                shotsTags.shotId = rs.getInt("shot_id");
                shotsTags.tagId = rs.getInt("tag_id");
                shotsTags.isMain = rs.getBoolean("tag_is_main");
                shotsTags.parentTagsShotsId = rs.getInt("parent_tag_shot_id");
                shotsTags.order = rs.getInt("tag_shot_order");
                shotsTags.ivfxShot = IVFXShots.load(shotsTags.shotId, withPreview);
                shotsTags.ivfxTag = IVFXTags.load(shotsTags.tagId, withPreview);
                if (shotsTags.parentTagsShotsId == 0) {
                    shotsTags.parentIvfxTagsShots = null;
                } else {
                    shotsTags.parentIvfxTagsShots = IVFXTagsShots.load(shotsTags.parentTagsShotsId, withPreview);
                }
                shotsTags.propIsMain = new SimpleBooleanProperty(shotsTags.isMain);
                listShotsTags.add(shotsTags);
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

        return listShotsTags;
    }

    public static List<IVFXTagsShots> loadList(List<IVFXTags> listTags, boolean withPreview) {
        List<IVFXTagsShots> listShotsTags = new ArrayList<>();

        Statement statement = null;
        ResultSet rs = null;
        String sql;

        try {
            statement = Main.mainConnection.createStatement();

//            sql = "SELECT * FROM tbl_tags_shots WHERE tag_id = " + ivfxTag.getId() + " ORDER BY tag_shot_order";

            String where = "WHERE (";
            for (IVFXTags tag: listTags) {
                where += "tbl_tags_shots.tag_id = " + tag.getId();
                if (!tag.isEqual(listTags.get(listTags.size()-1))) where += " OR ";
            }
            where += ") ";

            sql = "SELECT " +
                    "  tbl_tags_shots.id, " +
                    "  tbl_tags_shots.tag_id, " +
                    "  tbl_tags_shots.shot_id, " +
                    "  tbl_tags_shots.tag_is_main, " +
                    "  tbl_tags_shots.parent_tag_shot_id, " +
                    "  tbl_tags_shots.tag_shot_order, " +
                    "  tbl_tags_shots.create_time, " +
                    "  tbl_tags_shots.update_time, " +
                    "  tbl_shots.firstFrameNumber " +
                    "FROM tbl_tags_shots " +
                    "  INNER JOIN tbl_shots " +
                    "    ON tbl_tags_shots.shot_id = tbl_shots.id " +
                    "  INNER JOIN tbl_files " +
                    "    ON tbl_shots.file_id = tbl_files.id " +
                    where +
                    "GROUP BY tbl_tags_shots.id, " +
                    "         tbl_tags_shots.tag_id, " +
                    "         tbl_tags_shots.shot_id, " +
                    "         tbl_tags_shots.tag_is_main, " +
                    "         tbl_tags_shots.parent_tag_shot_id, " +
                    "         tbl_tags_shots.tag_shot_order, " +
                    "         tbl_tags_shots.create_time, " +
                    "         tbl_tags_shots.update_time, " +
                    "         tbl_shots.firstFrameNumber " +
                    "ORDER BY tbl_shots.firstFrameNumber";


            rs = statement.executeQuery(sql);
            while (rs.next()) {

                IVFXTagsShots shotsTags = new IVFXTagsShots();
                shotsTags.id = rs.getInt("id");
                shotsTags.shotId = rs.getInt("shot_id");
                shotsTags.tagId = rs.getInt("tag_id");
                shotsTags.isMain = rs.getBoolean("tag_is_main");
                shotsTags.parentTagsShotsId = rs.getInt("parent_tag_shot_id");
                shotsTags.order = rs.getInt("tag_shot_order");
                shotsTags.ivfxShot = IVFXShots.load(shotsTags.shotId, withPreview);
                shotsTags.ivfxTag = IVFXTags.load(shotsTags.tagId, withPreview);
                if (shotsTags.parentTagsShotsId == 0) {
                    shotsTags.parentIvfxTagsShots = null;
                } else {
                    shotsTags.parentIvfxTagsShots = IVFXTagsShots.load(shotsTags.parentTagsShotsId, withPreview);
                }
                shotsTags.propIsMain = new SimpleBooleanProperty(shotsTags.isMain);
                listShotsTags.add(shotsTags);
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

        return listShotsTags;

    }

    public static List<IVFXTagsShots> loadList(IVFXTags ivfxTag, boolean withPreview) {
        List<IVFXTags> listTags = new ArrayList<>();
        listTags.add(ivfxTag);
        return loadList(listTags, withPreview);
    }


    // TODO SAVE

    public void save() {
        String sql = "UPDATE tbl_tags_shots SET " +
                "shot_id = ?, tag_id = ?, tag_is_main = ?, parent_tag_shot_id = ?, tag_shot_order = ? " +
                "WHERE id = ?";
        try {
            PreparedStatement ps = Main.mainConnection.prepareStatement(sql);
            ps.setInt    (1, this.shotId);
            ps.setInt    (2, this.tagId);
            ps.setBoolean(3, this.isMain);
            ps.setInt    (4, this.parentTagsShotsId);
            ps.setInt    (5, this.order);
            ps.setInt    (6, this.id);
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

// TODO DELETE


    public void delete() {

        String sql = "DELETE FROM tbl_tags_shots WHERE id = ?";
        try {
            PreparedStatement ps = Main.mainConnection.prepareStatement(sql);
            ps.setInt   (1, this.id);
            ps.executeUpdate();
            ps.close();

            String sql2 = "UPDATE tbl_tags_shots SET tag_shot_order = tag_shot_order - 1 WHERE tag_id = ? AND tag_shot_order > ?";

            PreparedStatement ps2 = Main.mainConnection.prepareStatement(sql2);
            ps2.setInt   (1, this.tagId);
            ps2.setInt   (2, this.order);
            ps2.executeUpdate();
            ps2.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public IVFXTagsShots copyTo(IVFXShots shot, boolean copyWithProperties, boolean move) {

        IVFXTagsShots tagShot = IVFXTagsShots.load(this.ivfxTag, shot, true);
        if (tagShot == null) {
            if (move) {
                this.ivfxShot = shot;
                this.shotId = shot.getId();
                this.save();
                tagShot = this;
            } else {
                tagShot = IVFXTagsShots.getNewDbInstance(this.ivfxTag, shot);
                tagShot.setMain(this.isMain);
                if (this.parentIvfxTagsShots != null) {
                    tagShot.parentTagsShotsId = this.parentIvfxTagsShots.getId();
                    tagShot.parentIvfxTagsShots = this.parentIvfxTagsShots;
                } else {
                    tagShot.parentTagsShotsId = 0;
                    tagShot.parentIvfxTagsShots = null;
                }
                tagShot.propIsMain = new SimpleBooleanProperty(tagShot.isMain);
                tagShot.save();
                if (copyWithProperties) {
                    List<IVFXTagsShotsProperties> list = IVFXTagsShotsProperties.loadList(this, true);
                    for (IVFXTagsShotsProperties tmp: list) {
                        IVFXTagsShotsProperties.getNewDbInstance(tagShot,tmp.getName(),tmp.getValue());
                    }
                }
            }
        }

        return tagShot;

    }

    public String getEnumTagType() {
        return ivfxTag.getTagTypeName();
    }
    public String getEnumTagTypeLetter() {
        return ivfxTag.getTagTypeName().substring(0,1);
    }

    public String getTagName() {
        return ivfxTag.getName();
    }

    public Label getTagLabel1() {
        return ivfxTag.getLabel1();
    }
    public ImageView getTagPreview1() {
        return ivfxTag.getPreview1();
    }

    public Label getShotLabelFirst1() {
        return ivfxShot.getLabelFirst1();
    }
    public ImageView getShotPreviewFirst1() {
        return ivfxShot.getImageViewFirst1();
    }

    public Label getShotLabelLast1() {
        return ivfxShot.getLabelLast1();
    }
    public ImageView getShotPreviewLast1() {
        return ivfxShot.getImageViewLast1();
    }

    public int getId() {
        return id;
    }

    public int getShotId() {
        return shotId;
    }

    public void setShotId(int shotId) {
        this.shotId = shotId;
    }

    public int getTagId() {
        return tagId;
    }

    public void setTagId(int tagId) {
        this.tagId = tagId;
    }

    public int getParentTagsShotsId() {
        return parentTagsShotsId;
    }

    public void setParentTagsShotsId(int parentTagsShotsId) {
        this.parentTagsShotsId = parentTagsShotsId;
    }

    public IVFXShots getIvfxShot() {
        return ivfxShot;
    }

    public void setIvfxShot(IVFXShots ivfxShot) {
        this.ivfxShot = ivfxShot;
    }

    public IVFXTags getIvfxTag() {
        return ivfxTag;
    }

    public void setIvfxTag(IVFXTags ivfxTag) {
        this.ivfxTag = ivfxTag;
    }

    public IVFXTagsShots getParentIvfxShotsTags() {
        return parentIvfxTagsShots;
    }

    public void setParentIvfxShotsTags(IVFXTagsShots parentIvfxTagsShots) {
        this.parentIvfxTagsShots = parentIvfxTagsShots;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public BooleanProperty isMainProperty() { return propIsMain; }

    public boolean isMain() { return this.propIsMain.get(); }

    public void setMain(boolean value) {
        this.propIsMain.set(value);
        this.isMain = value;
    }

    public boolean getIsMain() { return this.propIsMain.get(); }

    public void setIsMain(boolean value) {
        this.propIsMain.set(value);
        this.isMain = value;
    }
}

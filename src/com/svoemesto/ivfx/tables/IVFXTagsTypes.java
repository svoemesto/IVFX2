package com.svoemesto.ivfx.tables;

import com.svoemesto.ivfx.Main;
import javafx.scene.control.ProgressBar;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class IVFXTagsTypes {

    private int id;
    private String name;
    private int order = 0;

    @Override
    public String toString() {
        return name;
    }

    public boolean isEqual(IVFXTagsTypes o) {
        return (this.id == o.id &&
                this.name.equals(o.name));
    }

    public static IVFXTagsTypes load(int id) {
        Statement statement = null;
        ResultSet rs = null;
        String sql;

        try {
            statement = Main.mainConnection.createStatement();

            sql = "SELECT * FROM tbl_tags_types WHERE id = " + id;
            rs = statement.executeQuery(sql);
            if (rs.next()) {
                IVFXTagsTypes tags_types = new IVFXTagsTypes();
                tags_types.id = rs.getInt("id");
                tags_types.name = rs.getString("name");
                return tags_types;
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

    public static List<IVFXTagsTypes> loadList() {
        return loadList(null);
    }

    public static List<IVFXTagsTypes> loadList(ProgressBar progressBar) {
        List<IVFXTagsTypes> listTagsTypes = new ArrayList<>();

        int iProgress = 0;
        Statement statement = null;
        ResultSet rs = null;
        String sql;

        try {
            statement = Main.mainConnection.createStatement();

            sql = "SELECT * FROM tbl_tags_types";

            String sqlCnt = "SELECT COUNT(*) AS CNT FROM (" + sql + ") AS tmp";
            ResultSet rsCnt = null;
            rsCnt = statement.executeQuery(sqlCnt);
            rsCnt.next();
            int countRows = rsCnt.getInt("CNT");
            rsCnt.close();

            rs = statement.executeQuery(sql);
            while (rs.next()) {

                if (progressBar != null) progressBar.setProgress((double)++iProgress / countRows);

                IVFXTagsTypes tags_types = new IVFXTagsTypes();
                tags_types.id = rs.getInt("id");
                tags_types.name = rs.getString("name");
                listTagsTypes.add(tags_types);
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

        return listTagsTypes;
    }

    // TODO SAVE

    public void save() {
        String sql = "UPDATE tbl_tags_types SET " +
                "name = ? " +
                "WHERE id = ?";
        try {
            PreparedStatement ps = Main.mainConnection.prepareStatement(sql);
            ps.setString(1, this.name);
            ps.setInt   (2, this.id);
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

// TODO DELETE

    public static IVFXEnumTagsTypes getEnumTagsTypes(int id) {
        IVFXEnumTagsTypes result;
        switch (id) {
            case 0: result = IVFXEnumTagsTypes.DESCRIPTION; break;
            case 1: result = IVFXEnumTagsTypes.PERSON; break;
            case 2: result = IVFXEnumTagsTypes.OBJECT; break;
            case 3: result = IVFXEnumTagsTypes.SCENE; break;
            case 4: result = IVFXEnumTagsTypes.EVENT; break;
            default: result = IVFXEnumTagsTypes.DESCRIPTION;
        }
        return result;
    }

    public static int getID(IVFXEnumTagsTypes ivfxEnumTagsTypes) {
        int result = 0;
        switch (ivfxEnumTagsTypes) {
            case DESCRIPTION: result = 0; break;
            case PERSON: result = 1; break;
            case OBJECT: result = 2; break;
            case SCENE: result = 3; break;
            case EVENT: result = 4;
        }
        return result;
    }

    public void delete() {

        String sql = "DELETE FROM tbl_tags_types WHERE id = ?";
        try {
            PreparedStatement ps = Main.mainConnection.prepareStatement(sql);
            ps.setInt   (1, this.id);
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

}

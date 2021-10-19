package com.svoemesto.ivfx.tables;

import com.svoemesto.ivfx.Main;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class IVFXFilesProperties {

    private int id;
    private int fileId;
    private String name;
    private String value;
    private int order = 0;
    private IVFXFiles ivfxFile;

    public boolean isEqual(IVFXFilesProperties o) {
        return (this.id == o.id &&
                this.fileId == o.fileId &&
                this.name.equals(o.name) &&
                this.value.equals(o.value));
    }

    public static IVFXFilesProperties getNewDbInstance(IVFXFiles ivfxFile) {
        return getNewDbInstance(ivfxFile, null, null);
    }
    public static IVFXFilesProperties getNewDbInstance(IVFXFiles ivfxFile, String name, String value) {
        IVFXFilesProperties filesProperties = new IVFXFilesProperties();

        filesProperties.fileId = ivfxFile.getId();
        filesProperties.ivfxFile = ivfxFile;
        filesProperties.name = name;
        filesProperties.value = value;

        Statement statement = null;
        ResultSet rs = null;
        String sql;

        try {

            statement = Main.mainConnection.createStatement();
            sql = "SELECT COUNT(*) FROM tbl_files_properties WHERE file_id = " + filesProperties.fileId;
            rs = statement.executeQuery(sql);
            if (rs.next()) {
                filesProperties.order = rs.getInt(1) + 1;  // индексация столбцов начинается с единицы
            }

            value = value.replace("\'","\'\'");

            sql = "INSERT INTO tbl_files_properties (" +
                    "file_id, file_property_order, file_property_name, file_property_value) " +
                    "VALUES(" +
                    filesProperties.fileId + ", "+
                    filesProperties.order + ", "+
                    "'" + name + "'" + ", " +
                    "'" + value + "'" + ")";

            PreparedStatement ps = Main.mainConnection.prepareStatement(sql);
            ps.executeUpdate(sql, Statement.RETURN_GENERATED_KEYS);
            rs = ps.getGeneratedKeys();
            if (rs.next()) {
                filesProperties.id = rs.getInt(1);
                if (name == null && value == null) {
                    name = "свойство №" + filesProperties.id;
                    value = "значение №" + filesProperties.id;
                    filesProperties.name = name;
                    filesProperties.value = value;
                    filesProperties.save();
                }
                System.out.println("Создана запись для свойства файла с идентификатором " + rs.getInt(1));
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

        return filesProperties;
    }

    public static IVFXFilesProperties load(int id) {
        Statement statement = null;
        ResultSet rs = null;
        String sql;

        try {
            statement = Main.mainConnection.createStatement();

            sql = "SELECT * FROM tbl_files_properties WHERE id = " + id;
            rs = statement.executeQuery(sql);
            if (rs.next()) {
                IVFXFilesProperties filesProperties = new IVFXFilesProperties();
                filesProperties.id = rs.getInt("id");
                filesProperties.fileId = rs.getInt("file_id");
                filesProperties.order = rs.getInt("file_property_order");
                filesProperties.name = rs.getString("file_property_name");
                filesProperties.value = rs.getString("file_property_value");
                filesProperties.ivfxFile = IVFXFiles.load(filesProperties.fileId);
                return filesProperties;
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

    public static IVFXFilesProperties loadByName(int fileId, String name) {
        Statement statement = null;
        ResultSet rs = null;
        String sql;

        try {
            statement = Main.mainConnection.createStatement();

            sql = "SELECT * FROM tbl_files_properties WHERE file_id = " + fileId + " AND file_property_name = '" + name + "'" + " ORDER BY file_property_order";
            rs = statement.executeQuery(sql);
            if (rs.next()) {
                IVFXFilesProperties filesProperties = new IVFXFilesProperties();
                filesProperties.id = rs.getInt("id");
                filesProperties.fileId = rs.getInt("file_id");
                filesProperties.order = rs.getInt("file_property_order");
                filesProperties.name = rs.getString("file_property_name");
                filesProperties.value = rs.getString("file_property_value");
                filesProperties.ivfxFile = IVFXFiles.load(filesProperties.fileId);
                return filesProperties;
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

    public static List<IVFXFilesProperties> loadList(IVFXFiles ivfxFile) {
        List<IVFXFilesProperties> listFilesProperties = new ArrayList<>();

        Statement statement = null;
        ResultSet rs = null;
        String sql;

        try {
            statement = Main.mainConnection.createStatement();

            sql = "SELECT * FROM tbl_files_properties WHERE file_id = " + ivfxFile.getId() + " ORDER BY file_property_order";

            rs = statement.executeQuery(sql);
            while (rs.next()) {

                IVFXFilesProperties filesProperties = new IVFXFilesProperties();
                filesProperties.id = rs.getInt("id");
                filesProperties.fileId = rs.getInt("file_id");
                filesProperties.order = rs.getInt("file_property_order");
                filesProperties.name = rs.getString("file_property_name");
                filesProperties.value = rs.getString("file_property_value");
                filesProperties.ivfxFile = IVFXFiles.load(filesProperties.fileId);
                listFilesProperties.add(filesProperties);
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

        return listFilesProperties;
    }

    // TODO SAVE

    public void save() {
        String sql = "UPDATE tbl_files_properties SET " +
                "file_id = ?, file_property_name = ?, file_property_value = ?, file_property_order = ? " +
                "WHERE id = ?";
        try {
            PreparedStatement ps = Main.mainConnection.prepareStatement(sql);
            ps.setInt   (1, this.fileId);
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

        String sql = "DELETE FROM tbl_files_properties WHERE id = ?";
        try {
            PreparedStatement ps = Main.mainConnection.prepareStatement(sql);
            ps.setInt   (1, this.id);
            ps.executeUpdate();
            ps.close();

            String sql2 = "UPDATE tbl_files_properties SET file_property_order = file_property_order - 1 WHERE file_id = ? AND file_property_order > ?";

            PreparedStatement ps2 = Main.mainConnection.prepareStatement(sql2);
            ps2.setInt   (1, this.fileId);
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

    public void setId(int id) {
        this.id = id;
    }

    public int getFileId() {
        return fileId;
    }

    public void setFileId(int fileId) {
        this.fileId = fileId;
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

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public IVFXFiles getIvfxFile() {
        return ivfxFile;
    }

    public void setIvfxFile(IVFXFiles ivfxFile) {
        this.ivfxFile = ivfxFile;
    }
}

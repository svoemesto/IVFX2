package com.svoemesto.ivfx.tables;

import com.svoemesto.ivfx.Main;
import javafx.scene.control.ProgressBar;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class IVFXFilesTracksProperties {

    private int id;
    private int fileTrackId;
    private IVFXFilesTracks ivfxFileTrack;
    private String key;
    private String value;

    //TODO ISEQUAL

    public boolean isEqual(IVFXFilesTracksProperties o) {
        return (this.id == o.id &&
                this.fileTrackId == o.fileTrackId &&
                this.key.equals(o.key) &&
                this.value.equals(o.value));
    }

    @Override
    public String toString() {
        return key + ": " + value;
    }

    // пустой конструктор
    public IVFXFilesTracksProperties() {
    }

    public static IVFXFilesTracksProperties getNewDbInstance(IVFXFilesTracks ivfxFileTrack) {
        IVFXFilesTracksProperties ivfxFileTrackPropertie = new IVFXFilesTracksProperties();

        Statement statement = null;
        ResultSet rs = null;
        String sql;

        try {
            statement = Main.mainConnection.createStatement();

            sql = "INSERT INTO tbl_files_tracks_properties (" +
                    "file_track_id, " +
                    "file_track_property_key, " +
                    "file_track_property_value) " +
                    "VALUES(" +
                    ivfxFileTrack.getId() + "," +
                    "'" + ivfxFileTrackPropertie.key + "'" + "," +
                    "'" + ivfxFileTrackPropertie.value + "'" + ")";
            System.out.println(sql);
            PreparedStatement ps = Main.mainConnection.prepareStatement(sql);
            ps.executeUpdate(sql, Statement.RETURN_GENERATED_KEYS);
            rs = ps.getGeneratedKeys();
            if (rs.next()) {
                ivfxFileTrackPropertie.id = rs.getInt(1);
                System.out.println("Создана запись для свойства трека «" + ivfxFileTrackPropertie.key + "» с идентификатором " + rs.getInt(1));
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

        return ivfxFileTrackPropertie;
    }

    // TODO LOAD


    public static IVFXFilesTracksProperties load(int id) {
        Statement statement = null;
        ResultSet rs = null;
        String sql;

        try {
            statement = Main.mainConnection.createStatement();

            sql = "SELECT * FROM tbl_files_tracks_properties WHERE id = " + id;
            rs = statement.executeQuery(sql);
            if (rs.next()) {
                IVFXFilesTracksProperties fileTrackPropertie = new IVFXFilesTracksProperties();
                fileTrackPropertie.id = rs.getInt("id");
                fileTrackPropertie.fileTrackId = rs.getInt("file_track_id");
                fileTrackPropertie.key = rs.getString("file_track_property_key");
                fileTrackPropertie.value = rs.getString("file_track_property_value");
                fileTrackPropertie.ivfxFileTrack = IVFXFilesTracks.load(fileTrackPropertie.fileTrackId);
                return fileTrackPropertie;
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


    public static List<IVFXFilesTracksProperties> loadList(IVFXFilesTracks ivfxFileTrack) {
        return loadList(ivfxFileTrack, null);
    }

    public static List<IVFXFilesTracksProperties> loadList(IVFXFilesTracks ivfxFileTrack, ProgressBar progressBar) {
        List<IVFXFilesTracksProperties> listFilesTracksProperties = new ArrayList<>();

        int iProgress = 0;
        Statement statement = null;
        ResultSet rs = null;
        String sql;

        try {
            statement = Main.mainConnection.createStatement();

            sql = "SELECT * FROM tbl_files_tracks_properties WHERE file_track_id = " + ivfxFileTrack.getId();

            String sqlCnt = "SELECT COUNT(*) AS CNT FROM (" + sql + ") AS tmp";
            ResultSet rsCnt = null;
            rsCnt = statement.executeQuery(sqlCnt);
            rsCnt.next();
            int countRows = rsCnt.getInt("CNT");
            rsCnt.close();

            rs = statement.executeQuery(sql);
            while (rs.next()) {

                if (progressBar != null) progressBar.setProgress((double)++iProgress / countRows);

                IVFXFilesTracksProperties fileTrackPropertie = new IVFXFilesTracksProperties();
                fileTrackPropertie.id = rs.getInt("id");
                fileTrackPropertie.fileTrackId = rs.getInt("file_track_id");
                fileTrackPropertie.key = rs.getString("file_track_property_key");
                fileTrackPropertie.value = rs.getString("file_track_property_value");
                fileTrackPropertie.ivfxFileTrack = ivfxFileTrack;
                listFilesTracksProperties.add(fileTrackPropertie);
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

        return listFilesTracksProperties;
    }

// TODO SAVE

    public void save() {
        String sql = "UPDATE tbl_files_tracks_properties SET " +
                "file_track_id = ?, " +
                "file_track_property_key = ?, " +
                "file_track_property_value = ? " +
                "WHERE id = ?";
        try {
            PreparedStatement ps = Main.mainConnection.prepareStatement(sql);
            ps.setInt   (1, this.fileTrackId);
            ps.setString   (2, this.key);
            ps.setString(3, this.value);
            ps.setInt   (4, this.id);
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

// TODO DELETE


    public void delete() {

        String sql = "DELETE FROM tbl_files_tracks_properties WHERE id = ?";
        try {
            PreparedStatement ps = Main.mainConnection.prepareStatement(sql);
            ps.setInt   (1, this.id);
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

// TODO GETTERS SETTERS


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getFileTrackId() {
        return fileTrackId;
    }

    public void setFileTrackId(int fileTrackId) {
        this.fileTrackId = fileTrackId;
    }

    public IVFXFilesTracks getIvfxFileTrack() {
        return ivfxFileTrack;
    }

    public void setIvfxFileTrack(IVFXFilesTracks ivfxFileTrack) {
        this.ivfxFileTrack = ivfxFileTrack;
    }

    public String getKey() {
        return key;
    }
    public String getFile_track_property_key() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }
    public String getFile_track_property_value() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}

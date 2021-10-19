package com.svoemesto.ivfx.tables;

import com.svoemesto.ivfx.Main;
import javafx.scene.control.ProgressBar;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class IVFXFilesTracks implements Serializable{

    private int id;
    private int fileId;
    private IVFXFiles ivfxFile;
    private String name;
    private String type;
    private boolean use = true;

    //TODO ISEQUAL

    public boolean isEqual(IVFXFilesTracks o) {
        return (this.id == o.id &&
                this.fileId == o.fileId &&
                this.name.equals(o.name) &&
                this.type.equals(o.type) &&
                this.use == o.use);
    }

    @Override
    public String toString() {
        return id + " " +  type + " " + use;
    }

    // пустой конструктор
    public IVFXFilesTracks() {
    }


    public static IVFXFilesTracks getNewDbInstance(IVFXFiles ivfxFile) {
        IVFXFilesTracks ivfxFileTrack = new IVFXFilesTracks();

        Statement statement = null;
        ResultSet rs = null;
        String sql;

        try {
            statement = Main.mainConnection.createStatement();

            sql = "INSERT INTO tbl_files_tracks (" +
                    "file_id, " +
                    "file_track_name, " +
                    "file_track_type, " +
                    "file_track_use) " +
                    "VALUES(" +
                    ivfxFile.getId() + "," +
                    "'" + ivfxFileTrack.name + "'" + "," +
                    "'" + ivfxFileTrack.type + "'" + "," +
                    (ivfxFileTrack.use ? 1 : 0) + ")";
            System.out.println(sql);
            PreparedStatement ps = Main.mainConnection.prepareStatement(sql);
            ps.executeUpdate(sql, Statement.RETURN_GENERATED_KEYS);
            rs = ps.getGeneratedKeys();
            if (rs.next()) {
                ivfxFileTrack.id = rs.getInt(1);
                System.out.println("Создана запись для трека «" + ivfxFileTrack.type + "» с идентификатором " + rs.getInt(1));
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

        return ivfxFileTrack;
    }

    // TODO LOAD


    public static IVFXFilesTracks load(int id) {
        Statement statement = null;
        ResultSet rs = null;
        String sql;

        try {
            statement = Main.mainConnection.createStatement();

            sql = "SELECT * FROM tbl_files_tracks WHERE id = " + id;
            rs = statement.executeQuery(sql);
            if (rs.next()) {
                IVFXFilesTracks fileTrack = new IVFXFilesTracks();
                fileTrack.id = rs.getInt("id");
                fileTrack.fileId = rs.getInt("file_id");
                fileTrack.name = rs.getString("file_track_name");
                fileTrack.type = rs.getString("file_track_type");
                fileTrack.use = rs.getBoolean("file_track_use");
                fileTrack.ivfxFile = IVFXFiles.load(fileTrack.fileId);
                return fileTrack;
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


    public static List<IVFXFilesTracks> loadList(IVFXFiles ivfxFile) {
        return loadList(ivfxFile, null);
    }

    public static List<IVFXFilesTracks> loadList(IVFXFiles ivfxFile, ProgressBar progressBar) {
        List<IVFXFilesTracks> listFilesTracks = new ArrayList<>();

        int iProgress = 0;
        Statement statement = null;
        ResultSet rs = null;
        String sql;

        try {
            statement = Main.mainConnection.createStatement();

            sql = "SELECT * FROM tbl_files_tracks WHERE file_id = " + ivfxFile.getId();

            String sqlCnt = "SELECT COUNT(*) AS CNT FROM (" + sql + ") AS tmp";
            ResultSet rsCnt = null;
            rsCnt = statement.executeQuery(sqlCnt);
            rsCnt.next();
            int countRows = rsCnt.getInt("CNT");
            rsCnt.close();

            rs = statement.executeQuery(sql);
            while (rs.next()) {

                if (progressBar != null) progressBar.setProgress((double)++iProgress / countRows);

                IVFXFilesTracks fileTrack = new IVFXFilesTracks();
                fileTrack.id = rs.getInt("id");
                fileTrack.fileId = rs.getInt("file_id");
                fileTrack.name = rs.getString("file_track_name");
                fileTrack.type = rs.getString("file_track_type");
                fileTrack.use = rs.getBoolean("file_track_use");
                fileTrack.ivfxFile = ivfxFile;
                listFilesTracks.add(fileTrack);
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

        return listFilesTracks;
    }

// TODO SAVE

    public void save() {
        String sql = "UPDATE tbl_files_tracks SET " +
                "file_id = ?, " +
                "file_track_name = ?, " +
                "file_track_type = ?, " +
                "file_track_use = ? " +
                "WHERE id = ?";
        try {
            PreparedStatement ps = Main.mainConnection.prepareStatement(sql);
            ps.setInt   (1, this.fileId);
            ps.setString   (2, this.name);
            ps.setString(3, this.type);
            ps.setBoolean(4, this.use);
            ps.setInt   (5, this.id);
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

// TODO DELETE


    public void delete() {

        String sql = "DELETE FROM tbl_files_tracks WHERE id = ?";
        try {
            PreparedStatement ps = Main.mainConnection.prepareStatement(sql);
            ps.setInt   (1, this.id);
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void deleteAllTracks(IVFXFiles ivfxFile) {

        String sql = "DELETE FROM tbl_files_tracks WHERE file_id = ?";
        try {
            PreparedStatement ps = Main.mainConnection.prepareStatement(sql);
            ps.setInt   (1, ivfxFile.getId());
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

// TODO FUNCTIONS

    public String getProperty(String key) {
        List<IVFXFilesTracksProperties> listProperties = IVFXFilesTracksProperties.loadList(this);
        for (IVFXFilesTracksProperties trackProperty: listProperties) {
            if (trackProperty.getKey().equals(key)) {
                return trackProperty.getValue();
            }
        }
        return null;
    }

// TODO GETTERS SETTERS


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

    public IVFXFiles getIvfxFile() {
        return ivfxFile;
    }

    public void setIvfxFile(IVFXFiles ivfxFile) {
        this.ivfxFile = ivfxFile;
    }

    public String getName() {
        return name;
    }
    public String getFile_track_name() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }
    public String getFile_track_type() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isUse() {
        return use;
    }
    public boolean getFile_track_use() {
        return use;
    }

    public void setUse(boolean use) {
        this.use = use;
    }
}

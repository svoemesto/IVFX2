package com.svoemesto.ivfx.tables;

import com.svoemesto.ivfx.Main;
import javafx.scene.control.ProgressBar;

import java.io.File;
import java.io.Serializable;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class IVFXProjects implements Serializable, Comparable<IVFXProjects> {
    private int id;
    private int order = 0;
    private String name = "Название нового проекта"; // название проекта, например "Интерактивная Игра Престолов"
    private String shortName = "Короткое имя нового проекта"; // имя файла без расширения, например iGOT
    private String folder = "Папка нового проекта";   // папка проекта, например "D:\\Dropbox\\InteractiveVideoFXProjects\\iGOT"
    private int videoWidth = 1920;
    private int videoHeight = 1080;
    private double videoFPS = 29.970;
    private int videoBitrate = 10_000_000;
    private String videoCodec = "h264";
    private String videoContainer = "mp4";
    private int audioBitrate = 320_000;
    private int audioFreq = 48_000;
    private String audioCodec = "aac";

    // пустой конструктор
    public IVFXProjects() {
    }

    @Override
    public String toString() {
        return name;
    }

    public static IVFXProjects getNewDbInstance() {

        IVFXProjects ivfxProject = new IVFXProjects();

        ResultSet rs = null;
        String sql;
        PreparedStatement ps = null;
        Statement statement = null;

        try {

            statement = Main.mainConnection.createStatement();

            sql = "SELECT COUNT(*) FROM tbl_projects";
            rs = statement.executeQuery(sql);
            if (rs.next()) {
                ivfxProject.order = rs.getInt(1) + 1;  // индексация столбцов начинается с единицы
            }

            sql = "INSERT INTO tbl_projects (" +
                    "order_project, " +
                    "name, " +
                    "short_name, " +
                    "folder, " +
                    "video_width, " +
                    "video_height, " +
                    "video_fps, " +
                    "video_bitrate, " +
                    "video_codec, " +
                    "video_container, " +
                    "audio_bitrate, " +
                    "audio_freq, " +
                    "audio_codec) " +
                    "VALUES(" +
                    ivfxProject.order + "," +
                    "'" + ivfxProject.name + "'" + "," +
                    "'" + ivfxProject.shortName + "'" + "," +
                    "'" + ivfxProject.folder + "'" +
                    ivfxProject.videoWidth +
                    ivfxProject.videoHeight +
                    ivfxProject.videoFPS +
                    ivfxProject.videoBitrate +
                    "'" + ivfxProject.videoCodec + "'" +
                    "'" + ivfxProject.videoContainer + "'" +
                    ivfxProject.audioBitrate +
                    ivfxProject.audioFreq +
                    "'" + ivfxProject.audioCodec + "'" +
                    ")";

            ps = Main.mainConnection.prepareStatement(sql);
            ps.executeUpdate(sql, Statement.RETURN_GENERATED_KEYS);
            rs = ps.getGeneratedKeys();
            if (rs.next()) {
                ivfxProject.id = rs.getInt(1);
                System.out.println("Создана запись для проекта «" + ivfxProject.name + "» с идентификатором " + rs.getInt(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close(); // close result set
                if (statement != null) statement.close(); // close statement
                if (ps != null) ps.close(); // close ps
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return ivfxProject;
    }


    public static IVFXProjects load(int id) {

        Statement statement = null;
        ResultSet rs = null;
        String sql;

        try {
            statement = Main.mainConnection.createStatement();

            sql = "SELECT * FROM tbl_projects WHERE id = " + id;
            rs = statement.executeQuery(sql);
            if (rs.next()) {
                IVFXProjects project = new IVFXProjects();
                project.id = rs.getInt("id");
                project.order = rs.getInt("order_project");
                project.name = rs.getString("name");
                project.shortName = rs.getString("short_name");
                project.folder = rs.getString("folder");
                project.videoWidth = rs.getInt("video_width");
                project.videoHeight = rs.getInt("video_height");
                project.videoFPS = rs.getDouble("video_fps");
                project.videoBitrate = rs.getInt("video_bitrate");
                project.videoCodec = rs.getString("video_codec");
                project.videoContainer = rs.getString("video_container");
                project.audioBitrate = rs.getInt("audio_bitrate");
                project.audioFreq = rs.getInt("audio_freq");
                project.audioCodec = rs.getString("audio_codec");
                return project;
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

    public static List<IVFXProjects> loadList() {
        return loadList(null);
    }
    public static List<IVFXProjects> loadList(ProgressBar progressBar) {

        List<IVFXProjects> listProjects = new ArrayList<>();

        int iProgress = 0;
        Statement statement = null;
        ResultSet rs = null;
        String sql;

        try {
            statement = Main.mainConnection.createStatement();

            sql = "SELECT * FROM tbl_projects ORDER BY order_project";

            String sqlCnt = "SELECT COUNT(*) AS CNT FROM (" + sql + ") AS tmp";
            ResultSet rsCnt = null;
            rsCnt = statement.executeQuery(sqlCnt);
            rsCnt.next();
            int countRows = rsCnt.getInt("CNT");
            rsCnt.close();

            rs = statement.executeQuery(sql);
            while (rs.next()) {

                if (progressBar != null) progressBar.setProgress((double)++iProgress / countRows);

                IVFXProjects project = new IVFXProjects();
                project.id = rs.getInt("id");
                project.order = rs.getInt("order_project");
                project.name = rs.getString("name");
                project.shortName = rs.getString("short_name");
                project.folder = rs.getString("folder");
                project.videoWidth = rs.getInt("video_width");
                project.videoHeight = rs.getInt("video_height");
                project.videoFPS = rs.getDouble("video_fps");
                project.videoBitrate = rs.getInt("video_bitrate");
                project.videoCodec = rs.getString("video_codec");
                project.videoContainer = rs.getString("video_container");
                project.audioBitrate = rs.getInt("audio_bitrate");
                project.audioFreq = rs.getInt("audio_freq");
                project.audioCodec = rs.getString("audio_codec");
                listProjects.add(project);
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

        return listProjects;
    }


    // TODO SAVE

    public void save() {

        String sql = "UPDATE tbl_projects SET " +
                "order_project = ?, " +
                "name = ?, " +
                "short_name = ?, " +
                "folder = ?, " +
                "video_width = ?, " +
                "video_height = ?, " +
                "video_fps = ?, " +
                "video_bitrate = ?, " +
                "video_codec = ?, " +
                "video_container = ?, " +
                "audio_bitrate = ?, " +
                "audio_freq = ?, " +
                "audio_codec = ? " +
                "WHERE id = ?";

        try {
            PreparedStatement ps = Main.mainConnection.prepareStatement(sql);
            ps.setInt   (1, this.order);
            ps.setString(2, this.name);
            ps.setString(3, this.shortName);
            ps.setString(4, this.folder);
            ps.setInt(5, this.videoWidth);
            ps.setInt(6, this.videoHeight);
            ps.setDouble(7, this.videoFPS);
            ps.setInt(8, this.videoBitrate);
            ps.setString(9, this.videoCodec);
            ps.setString(10, this.videoContainer);
            ps.setInt(11, this.audioBitrate);
            ps.setInt(12, this.audioFreq);
            ps.setString(13, this.audioCodec);
            ps.setInt   (14, this.id);
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

// TODO DELETE

    public void delete() {

        String sql = "DELETE FROM tbl_projects WHERE id = ?";
        try {
            PreparedStatement ps = Main.mainConnection.prepareStatement(sql);
            ps.setInt   (1, this.id);
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    @Override
    public int compareTo(IVFXProjects o) {
        Integer a = (Integer)getOrder();
        Integer b = (Integer)o.getOrder();
        return a.compareTo(b);
    }

// TODO GETTERS SETTERS

    public String getVideofilesFolder() {
        String folder = this.folder + "\\InputFiles";
        File file = new File(folder + "\\");
        if(!file.exists()) file.mkdir();
        return folder;
    }
    public String getFramesFolder() {
        String folder = this.folder + "\\Frames";
        File file = new File(folder + "\\");
        if(!file.exists()) file.mkdir();
        return folder;
    }
    public String getDataFolder() {
        String folder = this.folder + "\\DataFiles";
        File file = new File(folder + "\\");
        if(!file.exists()) file.mkdir();
        return folder;
    }

    public String getPersonsFolder() {
        String folder = this.folder + "\\Persons";
        File file = new File(folder + "\\");
        if(!file.exists()) file.mkdir();
        return folder;
    }

    public String getShotsFolder() {
        String folder = this.folder + "\\Shots";
        File file = new File(folder + "\\");
        if(!file.exists()) file.mkdir();
        return folder;
    }




    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public String getFolder() {
        return folder;
    }

    public void setFolder(String folder) {
        this.folder = folder;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getVideoWidth() {
        return videoWidth;
    }

    public void setVideoWidth(int videoWidth) {
        this.videoWidth = videoWidth;
    }

    public int getVideoHeight() {
        return videoHeight;
    }

    public void setVideoHeight(int videoHeight) {
        this.videoHeight = videoHeight;
    }

    public double getVideoFPS() {
        return videoFPS;
    }

    public void setVideoFPS(double videoFPS) {
        this.videoFPS = videoFPS;
    }

    public int getVideoBitrate() {
        return videoBitrate;
    }

    public void setVideoBitrate(int videoBitrate) {
        this.videoBitrate = videoBitrate;
    }

    public String getVideoCodec() {
        return videoCodec;
    }

    public void setVideoCodec(String videoCodec) {
        this.videoCodec = videoCodec;
    }

    public String getVideoContainer() {
        return videoContainer;
    }

    public void setVideoContainer(String videoContainer) {
        this.videoContainer = videoContainer;
    }

    public int getAudioBitrate() {
        return audioBitrate;
    }

    public void setAudioBitrate(int audioBitrate) {
        this.audioBitrate = audioBitrate;
    }

    public int getAudioFreq() {
        return audioFreq;
    }

    public void setAudioFreq(int audioFreq) {
        this.audioFreq = audioFreq;
    }

    public String getAudioCodec() {
        return audioCodec;
    }

    public void setAudioCodec(String audioCodec) {
        this.audioCodec = audioCodec;
    }
}

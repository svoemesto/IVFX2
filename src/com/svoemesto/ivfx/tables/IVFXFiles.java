package com.svoemesto.ivfx.tables;

import com.svoemesto.ivfx.Main;
import javafx.scene.control.ProgressBar;

import java.io.File;
import java.io.Serializable;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class IVFXFiles implements Serializable, Comparable<IVFXFiles> {

    public static transient final String FRAMES_PREVIEW_FOLDER_SUFFIX = "_Frames";
    public static transient final String FRAMES_FULLSIZE_FOLDER_SUFFIX = "_FramesFullSize";
    public static transient final String FRAMES_PREFIX = "_frame_";
    public static transient final String FILE_SUFFIX = "_videofiles";

    private int id;
    private int projectId;
    private IVFXProjects ivfxProject; // родительский проект
    private int order=0; // порядковый номер файла в проекте
    private String sourceName;    // путь и имя файла, например "D:\\iGOT\\GOT_S1E1.mkv"
    private String shortName;    // короткое имя файла, например "GOT_S1E1"
    private String title;   // описание файла, например "Игра Престолов, сезон 1, эпизод 1"
    private String losslessFileName="";    // путь и имя lossless-файла
    private double frameRate = 0.0;   // частота кадров, например 23.976
    private int duration = 0;   // длительность видео в миллисекундах
    private int framesCount = 0;    // общее количество кадров в видео
    private int seasonNumber =0;
    private int episodeNumber =0;
    private String description = "Комментарий";

    //TODO ISEQUAL

    public boolean isEqual(IVFXFiles o) {
        return (this.id == o.id &&
                this.projectId == o.projectId &&
                this.order == o.order &&
//                this.uuid.equals(o.uuid) &&
//                this.projectUuid.equals(o.projectUuid) &&
                this.sourceName.equals(o.sourceName) &&
                this.shortName.equals(o.shortName) &&
                this.title.equals(o.title) &&
                this.losslessFileName.equals(o.losslessFileName) &&
                this.frameRate == o.frameRate &&
                this.duration == o.duration &&
                this.framesCount == o.framesCount &&
                this.seasonNumber == o.seasonNumber &&
                this.episodeNumber == o.episodeNumber &&
                this.description.equals(o.description));
    }

    @Override
    public String toString() {
        return title;
    }

    // пустой конструктор
    public IVFXFiles() {
    }

    public static IVFXFiles getNewDbInstance(IVFXProjects ivfxProject) {
        IVFXFiles ivfxFile = new IVFXFiles();

        Statement statement = null;
        ResultSet rs = null;
        String sql;

        try {
            statement = Main.mainConnection.createStatement();

            sql = "SELECT COUNT(*) FROM tbl_files WHERE project_id = " + ivfxProject.getId();
            rs = statement.executeQuery(sql);
            if (rs.next()) {
                ivfxFile.order = rs.getInt(1) + 1;  // индексация столбцов начинается с единицы
            }

            sql = "INSERT INTO tbl_files (" +
                    "project_id, " +
                    "order_file, " +
                    "source_name, " +
                    "short_name, " +
                    "title, " +
                    "frame_rate, " +
                    "duration, " +
                    "frames_count, " +
                    "season_number, " +
                    "episode_number, " +
                    "description) " +
                    "VALUES(" +
                    ivfxProject.getId() + "," +
                    ivfxFile.order + "," +
                    "'" + ivfxFile.sourceName + "'" + "," +
                    "'" + ivfxFile.shortName + "'" + "," +
                    "'" + ivfxFile.title + "'" + "," +
                    ivfxFile.frameRate + "," +
                    ivfxFile.duration + "," +
                    ivfxFile.framesCount + "," +
                    ivfxFile.seasonNumber + "," +
                    ivfxFile.episodeNumber + "," +
                    "'" + ivfxFile.description + "'" + ")";

            PreparedStatement ps = Main.mainConnection.prepareStatement(sql);
            ps.executeUpdate(sql, Statement.RETURN_GENERATED_KEYS);
            rs = ps.getGeneratedKeys();
            if (rs.next()) {
                ivfxFile.id = rs.getInt(1);
                System.out.println("Создана запись для файла «" + ivfxFile.title + "» с идентификатором " + rs.getInt(1));
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

        return ivfxFile;
    }

// TODO OVERRIDE

    @Override
    public int compareTo(IVFXFiles o) {
        Integer a = getOrder();
        Integer b = o.getOrder();
        return a.compareTo(b);
    }

// TODO LOAD


    public static IVFXFiles load(int id) {
        Statement statement = null;
        ResultSet rs = null;
        String sql;

        try {
            statement = Main.mainConnection.createStatement();

            sql = "SELECT * FROM tbl_files WHERE id = " + id;
            rs = statement.executeQuery(sql);
            if (rs.next()) {
                IVFXFiles videoFile = new IVFXFiles();
                videoFile.id = rs.getInt("id");
                videoFile.projectId = rs.getInt("project_id");
                videoFile.order = rs.getInt("order_file");
                videoFile.sourceName = rs.getString("source_name");
                videoFile.shortName = rs.getString("short_name");
                videoFile.title = rs.getString("title");
                videoFile.frameRate = rs.getDouble("frame_rate");
                videoFile.duration = rs.getInt("duration");
                videoFile.framesCount = rs.getInt("frames_count");
                videoFile.seasonNumber = rs.getInt("season_number");
                videoFile.episodeNumber = rs.getInt("episode_number");
                videoFile.description = rs.getString("description");
                videoFile.ivfxProject = IVFXProjects.load(videoFile.projectId);
                return videoFile;
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


    public static List<IVFXFiles> loadList(IVFXProjects ivfxProject) {
        return loadList(ivfxProject, null);
    }

    public static List<IVFXFiles> loadList(IVFXProjects ivfxProject, ProgressBar progressBar) {
        List<IVFXFiles> listVideoFiles = new ArrayList<>();

        int iProgress = 0;
        Statement statement = null;
        ResultSet rs = null;
        String sql;

        try {
            statement = Main.mainConnection.createStatement();

            sql = "SELECT * FROM tbl_files WHERE project_id = " + ivfxProject.getId() + " ORDER BY order_file";

            String sqlCnt = "SELECT COUNT(*) AS CNT FROM (" + sql + ") AS tmp";
            ResultSet rsCnt = null;
            rsCnt = statement.executeQuery(sqlCnt);
            rsCnt.next();
            int countRows = rsCnt.getInt("CNT");
            rsCnt.close();

            rs = statement.executeQuery(sql);
            while (rs.next()) {

                if (progressBar != null) progressBar.setProgress((double)++iProgress / countRows);

                IVFXFiles file = new IVFXFiles();
                file.id = rs.getInt("id");
                file.projectId = rs.getInt("project_id");
                file.order = rs.getInt("order_file");
                file.sourceName = rs.getString("source_name");
                file.shortName = rs.getString("short_name");
                file.title = rs.getString("title");
                file.frameRate = rs.getDouble("frame_rate");
                file.duration = rs.getInt("duration");
                file.framesCount = rs.getInt("frames_count");
                file.seasonNumber = rs.getInt("season_number");
                file.episodeNumber = rs.getInt("episode_number");
                file.description = rs.getString("description");
                file.ivfxProject = ivfxProject;
                listVideoFiles.add(file);
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

        return listVideoFiles;
    }

// TODO SAVE

    public void save() {
        String sql = "UPDATE tbl_files SET " +
                "project_id = ?, " +
                "order_file = ?, " +
                "source_name = ?, " +
                "short_name = ?, " +
                "title = ?, " +
                "frame_rate = ?, " +
                "duration = ?, " +
                "frames_count = ?, " +
                "season_number = ?, " +
                "episode_number = ?, " +
                "description = ? " +
                "WHERE id = ?";
        try {
            PreparedStatement ps = Main.mainConnection.prepareStatement(sql);
            ps.setInt   (1, this.projectId);
            ps.setInt   (2, this.order);
            ps.setString(3, this.sourceName);
            ps.setString(4, this.shortName);
            ps.setString(5, this.title);
            ps.setDouble(6, this.frameRate);
            ps.setInt(7, this.duration);
            ps.setInt(8, this.framesCount);
            ps.setInt(9, this.seasonNumber);
            ps.setInt(10, this.episodeNumber);
            ps.setString(11, this.description);
            ps.setInt   (12, this.id);
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

// TODO DELETE


    public void delete() {

        String sql = "DELETE FROM tbl_files WHERE id = ?";
        try {
            PreparedStatement ps = Main.mainConnection.prepareStatement(sql);
            ps.setInt   (1, this.id);
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

// TODO FUNCTIONS

    public String getFramesFolderFullSize() {
        String folder = ivfxProject.getFramesFolder() + "\\" + shortName + FRAMES_FULLSIZE_FOLDER_SUFFIX;
        File file = new File(folder + "\\");
        if(!file.exists()) file.mkdir();
        return folder;
    }

    public String getFramesFolderPreview() {
        String folder = ivfxProject.getFramesFolder() + "\\" + shortName + FRAMES_PREVIEW_FOLDER_SUFFIX;
        File file = new File(folder + "\\");
        if(!file.exists()) file.mkdir();
        return folder;
    }

    public String getFileSourceNamePreview() {
        String fileSourceName = this.sourceName;
        String folderPreview = this.ivfxProject.getFolder()+"\\Video\\Preview";
        File file = new File(fileSourceName);
        String fileNameWithoutPath = file.getName();
        String fileNameWithoutExtention = fileNameWithoutPath.substring(0,fileNameWithoutPath.lastIndexOf("."));
        return folderPreview + "\\" + fileNameWithoutExtention + ".mp4";
    }


    public void checkIntegrity() {

        IVFXFiles ivfxFile = this;
        List<IVFXShots> listShots = IVFXShots.loadList(ivfxFile, false);
        List<IVFXScenes> listScenes = IVFXScenes.loadList(ivfxFile);

        if (listShots.size() > 0) {
            if (listScenes.size() == 0) {
                // Если сцен нет - создаем одну и добавляем в нее все планы
                IVFXScenes.getNewDbInstance(listShots.get(0));
            } else {
                int iShot = 0;
                int lastShotIndex = listShots.size()-1;
                // Проходимся по сценам
                for (IVFXScenes scene: listScenes) {
                    List<IVFXScenesShots> listSceneShots = IVFXScenesShots.loadList(scene);
                    int iSceneShot = 0;
                    int lastSceneShotIndex = listSceneShots.size()-1;
                    while (iSceneShot <= lastSceneShotIndex && iShot <= lastShotIndex) {
                        IVFXScenesShots sceneShot = listSceneShots.get(iSceneShot);
                        IVFXShots shotGlobal = listShots.get(iShot);
                        if (sceneShot.getIvfxShot().getId() == shotGlobal.getId()) {
                            if (sceneShot.getOrder() != iSceneShot + 1) {
                                sceneShot.setOrder(iSceneShot + 1);
                                sceneShot.save();
                            }
                            iShot++;
                            iSceneShot++;
                        } else {
                            IVFXScenesShots sceneShotNew = IVFXScenesShots.getNewDbInstance(scene, shotGlobal);
                            sceneShotNew.setOrder(iSceneShot+1);
                            sceneShotNew.save();
                            listSceneShots.add(iSceneShot, sceneShotNew);
                            lastSceneShotIndex++;
                            iShot++;
                            iSceneShot++;
                        }
                    }
                }
            }
        }

    }

// TODO GETTERS SETTERS

    public IVFXProjects getIvfxProject() {
        return ivfxProject;
    }

    public void setIvfxProject(IVFXProjects ivfxProject) {
        this.ivfxProject = ivfxProject;
    }

    public String getSourceName() {
        return sourceName;
    }

    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }


    public String getLosslessFileName() {
        return losslessFileName;
    }

    public void setLosslessFileName(String losslessFileName) {
        this.losslessFileName = losslessFileName;
    }

    public double getFrameRate() {
        return frameRate;
    }

    public void setFrameRate(double frameRate) {
        this.frameRate = frameRate;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getFramesCount() {
        return framesCount;
    }

    public void setFramesCount(int framesCount) {
        this.framesCount = framesCount;
    }



    public int getSeasonNumber() {
        return seasonNumber;
    }

    public void setSeasonNumber(int seasonNumber) {
        this.seasonNumber = seasonNumber;
    }

    public int getEpisodeNumber() {
        return episodeNumber;
    }

    public void setEpisodeNumber(int episodeNumber) {
        this.episodeNumber = episodeNumber;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getProjectId() {
        return projectId;
    }

    public void setProjectId(int projectId) {
        this.projectId = projectId;
    }

}

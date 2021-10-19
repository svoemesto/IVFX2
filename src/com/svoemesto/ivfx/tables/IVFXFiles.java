package com.svoemesto.ivfx.tables;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.svoemesto.ivfx.Main;
import com.svoemesto.ivfx.utils.MediaInfo;
import com.svoemesto.ivfx.utils.Utils;
import javafx.scene.control.ProgressBar;

import java.io.File;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.sql.*;
import java.util.*;

public class IVFXFiles implements Serializable, Comparable<IVFXFiles> {

    public static transient final String FRAMES_PREVIEW_FOLDER_SUFFIX = "_Frames_Preview";
    public static transient final String FRAMES_MEDIUM_FOLDER_SUFFIX = "_Frames_Medium";
    public static transient final String FRAMES_FAVORITE_FOLDER_SUFFIX = "_Frames_Favorite";
    public static transient final String FRAMES_FULLSIZE_FOLDER_SUFFIX = "_Frames_FullSize";
    public static transient final String FRAMES_PREFIX = "_frame_";
    public static transient final String FACES_PREFIX = "_faces_";
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
    private int width = 0;    // ширина в пикселях
    private int height = 0;    // высота в пикселях
    private int seasonNumber =0;
    private int episodeNumber =0;
    private String description = "Комментарий";
    private boolean useFolderMp4 = false;
    private String folderMp4 = null;
    private boolean useFolderFramesPreview = false;
    private String folderFramesPreview = null;
    private boolean useFolderFramesMedium = false;
    private String folderFramesMedium = null;
    private boolean useFolderFramesFull = false;
    private String folderFramesFull = null;
    private boolean useFolderFavorite = false;
    private String folderFavorite = null;
    private boolean useFolderLossless = false;
    private String folderLossless = null;
    private boolean useFolderShots = false;
    private String folderShots = null;
    private String losslessVideoCodec = "dnxhd";
    private String losslessContainer = "mxf";
    private String mediaInfoJson = null;

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
                this.width == o.width &&
                this.height == o.height &&
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
                    "width, " +
                    "height, " +
                    "season_number, " +
                    "episode_number, " +
                    "use_folder_mp4, " +
                    "folder_mp4, " +
                    "use_folder_frames_preview, " +
                    "folder_frames_preview, " +
                    "use_folder_frames_medium, " +
                    "folder_frames_medium, " +
                    "use_folder_frames_full, " +
                    "folder_frames_full, " +
                    "use_folder_favorite, " +
                    "folder_favorite, " +
                    "use_folder_lossless, " +
                    "folder_lossless, " +
                    "use_folder_shots, " +
                    "folder_shots, " +
                    "lossless_video_codec, " +
                    "lossless_container, " +
                    "mediainfo_json, " +
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
                    ivfxFile.width + "," +
                    ivfxFile.height + "," +
                    ivfxFile.seasonNumber + "," +
                    ivfxFile.episodeNumber + "," +
                    ivfxFile.useFolderMp4 + "," +
                    "'" + ivfxFile.folderMp4 + "'" + "," +
                    ivfxFile.useFolderFramesPreview + "," +
                    "'" + ivfxFile.folderFramesPreview + "'" + "," +
                    ivfxFile.useFolderFramesMedium + "," +
                    "'" + ivfxFile.folderFramesMedium + "'" + "," +
                    ivfxFile.useFolderFramesFull + "," +
                    "'" + ivfxFile.folderFramesFull + "'" + "," +
                    ivfxFile.useFolderFavorite + "," +
                    "'" + ivfxFile.folderFavorite + "'" + "," +
                    ivfxFile.useFolderLossless + "," +
                    "'" + ivfxFile.folderLossless + "'" + "," +
                    ivfxFile.useFolderShots + "," +
                    "'" + ivfxFile.folderShots + "'" + "," +
                    "'" + ivfxFile.losslessVideoCodec + "'" + "," +
                    "'" + ivfxFile.losslessContainer + "'" + "," +
                    "'" + ivfxFile.mediaInfoJson + "'" + "," +
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
                videoFile.width = rs.getInt("width");
                videoFile.height = rs.getInt("height");
                videoFile.seasonNumber = rs.getInt("season_number");
                videoFile.episodeNumber = rs.getInt("episode_number");
                videoFile.useFolderMp4 = rs.getBoolean("use_folder_mp4");
                videoFile.folderMp4 = rs.getString("folder_mp4");
                videoFile.useFolderFramesPreview = rs.getBoolean("use_folder_frames_preview");
                videoFile.folderFramesPreview = rs.getString("folder_frames_preview");
                videoFile.useFolderFramesMedium = rs.getBoolean("use_folder_frames_medium");
                videoFile.folderFramesMedium = rs.getString("folder_frames_medium");
                videoFile.useFolderFramesFull = rs.getBoolean("use_folder_frames_full");
                videoFile.folderFramesFull = rs.getString("folder_frames_full");
                videoFile.useFolderFavorite = rs.getBoolean("use_folder_favorite");
                videoFile.folderFavorite = rs.getString("folder_favorite");
                videoFile.useFolderLossless = rs.getBoolean("use_folder_lossless");
                videoFile.folderLossless = rs.getString("folder_lossless");
                videoFile.useFolderShots = rs.getBoolean("use_folder_shots");
                videoFile.folderShots = rs.getString("folder_shots");
                videoFile.losslessVideoCodec = rs.getString("lossless_video_codec");
                videoFile.losslessContainer = rs.getString("lossless_container");
                videoFile.mediaInfoJson = rs.getString("mediainfo_json");
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


    public void updateTagsByFaces(boolean withPreview) {
        List<IVFXShots> listShots = IVFXShots.loadList(this, withPreview);
        List<IVFXTagsShotsFaces> listTagsShotsFaces = IVFXTagsShotsFaces.loadList(this, withPreview);

        int i = 0;
        for (IVFXShots shot : listShots) {
            i++;
            System.out.println("Обновление тэгов планов по лицам: " + i + "/" + listShots.size());

            List<IVFXTagsShotsFaces> listTagsShotsFacesForOneShot = new ArrayList<>();
            for (IVFXTagsShotsFaces tagShotFace : listTagsShotsFaces) {
                if (tagShotFace.getShotId() == shot.getId()) {
                    listTagsShotsFacesForOneShot.add(tagShotFace);
                }
            }

            int[] arrTagTypeId = {1,2};
            List<IVFXTagsShots> listTagsShots = IVFXTagsShots.loadList(shot, withPreview, arrTagTypeId);
            for (IVFXTagsShotsFaces tagShotFace : listTagsShotsFacesForOneShot) {
                boolean tagIsFinded = false;
                for (IVFXTagsShots tagShot : listTagsShots) {
                    if (tagShot.getTagId() == tagShotFace.getTagId()) {
                        tagShot.setProba(tagShotFace.getProba());
                        tagShot.setTypeSizeId(tagShotFace.getTypeSizeId());
                        tagShot.save();
                        tagIsFinded = true;
                        break;
                    }
                }
                if (!tagIsFinded) {
                    IVFXTagsShots tagShot = IVFXTagsShots.getNewDbInstance(tagShotFace.getIvfxTag(),shot);
                    if (tagShot != null) {
                        tagShot.setProba(tagShotFace.getProba());
                        tagShot.setTypeSizeId(tagShotFace.getTypeSizeId());
                        tagShot.save();
                    }
                }
            }
        }

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
                file.width = rs.getInt("width");
                file.height = rs.getInt("height");
                file.seasonNumber = rs.getInt("season_number");
                file.episodeNumber = rs.getInt("episode_number");
                file.useFolderMp4 = rs.getBoolean("use_folder_mp4");
                file.folderMp4 = rs.getString("folder_mp4");
                file.useFolderFramesPreview = rs.getBoolean("use_folder_frames_preview");
                file.folderFramesPreview = rs.getString("folder_frames_preview");
                file.useFolderFramesMedium = rs.getBoolean("use_folder_frames_medium");
                file.folderFramesMedium = rs.getString("folder_frames_medium");
                file.useFolderFramesFull = rs.getBoolean("use_folder_frames_full");
                file.folderFramesFull = rs.getString("folder_frames_full");
                file.useFolderFavorite = rs.getBoolean("use_folder_favorite");
                file.folderFavorite = rs.getString("folder_favorite");
                file.useFolderLossless = rs.getBoolean("use_folder_lossless");
                file.folderLossless = rs.getString("folder_lossless");
                file.useFolderShots = rs.getBoolean("use_folder_shots");
                file.folderShots = rs.getString("folder_shots");
                file.losslessVideoCodec = rs.getString("lossless_video_codec");
                file.losslessContainer = rs.getString("lossless_container");
                file.mediaInfoJson = rs.getString("mediainfo_json");
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
                "width = ?, " +
                "height = ?, " +
                "season_number = ?, " +
                "episode_number = ?, " +
                "use_folder_mp4 = ?, " +
                "folder_mp4 = ?, " +
                "use_folder_frames_preview = ?, " +
                "folder_frames_preview = ?, " +
                "use_folder_frames_medium = ?, " +
                "folder_frames_medium = ?, " +
                "use_folder_frames_full = ?, " +
                "folder_frames_full = ?, " +
                "use_folder_favorite = ?, " +
                "folder_favorite = ?, " +
                "use_folder_lossless = ?, " +
                "folder_lossless = ?, " +
                "use_folder_shots = ?, " +
                "folder_shots = ?, " +
                "lossless_video_codec = ?, " +
                "lossless_container = ?, " +
                "mediainfo_json = ?, " +
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
            ps.setInt(9, this.width);
            ps.setInt(10, this.height);
            ps.setInt(11, this.seasonNumber);
            ps.setInt(12, this.episodeNumber);
            ps.setBoolean(13, this.useFolderMp4);
            ps.setString(14, this.folderMp4);
            ps.setBoolean(15, this.useFolderFramesPreview);
            ps.setString(16, this.folderFramesPreview);
            ps.setBoolean(17, this.useFolderFramesMedium);
            ps.setString(18, this.folderFramesMedium);
            ps.setBoolean(19, this.useFolderFramesFull);
            ps.setString(20, this.folderFramesFull);
            ps.setBoolean(21, this.useFolderFavorite);
            ps.setString(22, this.folderFavorite);
            ps.setBoolean(23, this.useFolderLossless);
            ps.setString(24, this.folderLossless);
            ps.setBoolean(25, this.useFolderShots);
            ps.setString(26, this.folderShots);
            ps.setString(27, this.losslessVideoCodec);
            ps.setString(28, this.losslessContainer);
            ps.setString(29, this.mediaInfoJson);
            ps.setString(30, this.description);
            ps.setInt   (31, this.id);
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

    public List<IVFXTags> getListTagsFromHTML() {
        List<IVFXTags> result = new ArrayList<>();
        String url = getPropertyValue("url");
        if (!url.equals("")) {
            String html = getPropertyValue("html");
            if (html.equals("")) {
                html = Utils.getHTMLtextFromUrl(url);
            }
            if (html != null) {
                html = Utils.getTextBetween(html, "<span id=\"В_ролях\"></span>", "<span id=\"Навигация_по_сериям\"></span>");
                if (!html.equals("")) {
                    List<IVFXTags> listTags = IVFXTags.loadList(this, false);
                    for (IVFXTags tag : listTags) {
                        String tagUrl = tag.getPropertyValue("url");
                        if (tagUrl != null) {
                            tagUrl = Utils.getTextBetween(tagUrl, "https://gameofthrones.fandom.com", "");
                            if (!tagUrl.equals("")) {
                                tagUrl.replace("/", "\\");
                                if (html.contains(tagUrl)) {
                                    result.add(IVFXTags.load(tag.getId(),true));
                                }
                            }
                        }

                    }
                }
            }
        }
        Collections.sort(result);
        return result;
    }

    public void setPropertyValue(String propertyName, String propertyValue) {
        IVFXFilesProperties filesProperties = IVFXFilesProperties.loadByName(this.id, propertyName);
        if (filesProperties != null) {
            filesProperties.setValue(propertyValue);
            filesProperties.save();
        } else {
            IVFXFilesProperties.getNewDbInstance(this, propertyName, propertyValue);
        }
    }

    public String getPropertyValue(String propertyName) {
        String result = null;
        IVFXFilesProperties filesProperties = IVFXFilesProperties.loadByName(this.id, propertyName);
        if (filesProperties != null) result = filesProperties.getValue();
        return result;
    }


    public String getFramesFolderFavorite() {
        String folder = useFolderFavorite ? folderFavorite : ivfxProject.getFramesFolder() + "\\" + shortName + FRAMES_FAVORITE_FOLDER_SUFFIX;
        File file = new File(folder + "\\");
        if(!file.exists()) file.mkdir();
        return folder;
    }

    public String getFileSourceNamePreview() {
        String fileSourceName = this.sourceName;
        String folderPreview = useFolderMp4 ? folderMp4 : this.ivfxProject.getFolder()+"\\Video\\Preview";
        File file = new File(fileSourceName);
        String fileNameWithoutPath = file.getName();
        String fileNameWithoutExtention = fileNameWithoutPath.substring(0,fileNameWithoutPath.lastIndexOf("."));
        return folderPreview + "\\" + fileNameWithoutExtention + "_preview.mp4";
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

    public String getFromMediaInfoRef() {

        String json = getMediaInfoJson();
        if (json != null) {

            Type mapType = new TypeToken<Map<String, Map>>(){}.getType();
            Map<String, Map> son = new Gson().fromJson(json, mapType);

            Map<String, Object> media = son.get("media");

            String ref = media.get("@ref").toString();

            String result = ref;

            return result;

        }

        return null;

    }

    public ArrayList<Map<String, Object>> getFromMediaInfoTracks() {

        String json = getMediaInfoJson();
        if (json != null) {

            Type mapType = new TypeToken<Map<String, Map>>(){}.getType();
            Map<String, Map> son = new Gson().fromJson(json, mapType);

            Map<String, Object> media = son.get("media");

            String ref = media.get("@ref").toString();
            ArrayList<Map<String, Object>> tracks = (ArrayList<Map<String, Object>>)media.get("track");

            ArrayList<Map<String, Object>> result = tracks;

            return result;

        }

        return null;

    }

    public Map<String, String> getFromMediaInfoTrack(int trackNumber) {

        String json = getMediaInfoJson();
        if (json != null) {

            Type mapType = new TypeToken<Map<String, Map>>(){}.getType();
            Map<String, Map> son = new Gson().fromJson(json, mapType);

            Map<String, Object> media = son.get("media");

            String ref = media.get("@ref").toString();
            ArrayList<Map<String, String>> tracks = (ArrayList<Map<String, String>>)media.get("track");

            Map<String, String> result = tracks.get(trackNumber);

            return result;

        }

        return null;

    }

    public int createTracksFromMediaInfo() {
        ArrayList<Map<String, Object>> listTracks = getFromMediaInfoTracks();
        if (listTracks != null) {
            IVFXFilesTracks.deleteAllTracks(this);
            for (Map<String, Object> track: listTracks) {
                String trackType = track.get("@type").toString();
                String trackName = trackType;
                IVFXFilesTracks ivfxFileTrack = IVFXFilesTracks.getNewDbInstance(this);
                ivfxFileTrack.setType(trackType);
                ivfxFileTrack.setName(trackName);
                ivfxFileTrack.setUse(true);
                ivfxFileTrack.setFileId(this.getId());
                ivfxFileTrack.setIvfxFile(this);
                ivfxFileTrack.save();

                String trackPropertyKey, trackPropertyValue;

                for (Map.Entry<String, Object> entry : track.entrySet()) {

                    if (entry.getValue() instanceof LinkedTreeMap) {
                        Map<String, Object> track2 = (Map<String, Object>)entry.getValue();
                        for (Map.Entry<String, Object> entry2 : track2.entrySet()) {
                            trackPropertyKey = entry2.getKey();
                            trackPropertyValue = entry2.getValue().toString();

                            IVFXFilesTracksProperties ivfxFileTrackPropertie = IVFXFilesTracksProperties.getNewDbInstance(ivfxFileTrack);
                            ivfxFileTrackPropertie.setKey(trackPropertyKey);
                            ivfxFileTrackPropertie.setValue(trackPropertyValue);
                            ivfxFileTrackPropertie.setFileTrackId(ivfxFileTrack.getId());
                            ivfxFileTrackPropertie.setIvfxFileTrack(ivfxFileTrack);
                            ivfxFileTrackPropertie.save();

                        }

                    } else {
                        trackPropertyKey = entry.getKey();
                        trackPropertyValue = entry.getValue().toString();

                        IVFXFilesTracksProperties ivfxFileTrackPropertie = IVFXFilesTracksProperties.getNewDbInstance(ivfxFileTrack);
                        ivfxFileTrackPropertie.setKey(trackPropertyKey);
                        ivfxFileTrackPropertie.setValue(trackPropertyValue);
                        ivfxFileTrackPropertie.setFileTrackId(ivfxFileTrack.getId());
                        ivfxFileTrackPropertie.setIvfxFileTrack(ivfxFileTrack);
                        ivfxFileTrackPropertie.save();
                    }

                }
            }
            return listTracks.size();
        } else {
            return 0;
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

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
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

    public boolean isUseFolderMp4() {
        return useFolderMp4;
    }

    public void setUseFolderMp4(boolean useFolderMp4) {
        this.useFolderMp4 = useFolderMp4;
    }

    public String getFolderMp4() {
        if (folderMp4 == null || folderMp4.equals("") || !useFolderMp4) {
            return ivfxProject.getFolder() + "\\Video\\Preview";
        } else {
            return folderMp4;
        }
    }

    public void setFolderMp4(String folderMp4) {
        this.folderMp4 = folderMp4;
    }

    public boolean isUseFolderFramesPreview() {
        return useFolderFramesPreview;
    }

    public void setUseFolderFramesPreview(boolean useFolderFramesPreview) {
        this.useFolderFramesPreview = useFolderFramesPreview;
    }

    public String getFolderFramesPreview() {
        if (folderFramesPreview == null || folderFramesPreview.equals("") || !useFolderFramesPreview) {
            return ivfxProject.getFramesFolder() + "\\" + shortName + FRAMES_PREVIEW_FOLDER_SUFFIX;
        } else {
            return folderFramesPreview;
        }
    }

    public void setFolderFramesPreview(String folderFramesPreview) {
        this.folderFramesPreview = folderFramesPreview;
    }

    public String getFolderFramesMedium() {
        if (folderFramesMedium == null || folderFramesMedium.equals("") || !useFolderFramesMedium) {
            return ivfxProject.getFramesFolder() + "\\" + shortName + FRAMES_MEDIUM_FOLDER_SUFFIX;
        } else {
            return folderFramesMedium;
        }
    }

    public void setFolderFramesMedium(String folderFramesMedium) {
        this.folderFramesMedium = folderFramesMedium;
    }


    public boolean isUseFolderFramesFull() {
        return useFolderFramesFull;
    }

    public void setUseFolderFramesFull(boolean useFolderFramesFull) {
        this.useFolderFramesFull = useFolderFramesFull;
    }

    public String getFolderFramesFull() {
        if (folderFramesFull == null || folderFramesFull.equals("") || !useFolderFramesFull) {
            return ivfxProject.getFramesFolder() + "\\" + shortName + FRAMES_FULLSIZE_FOLDER_SUFFIX;
        } else {
            return folderFramesFull;
        }
    }

    public String getFolderFaces() {
        return getFolderFramesFull() + ".faces";
    }

    public void setFolderFramesFull(String folderFramesFull) {
        this.folderFramesFull = folderFramesFull;
    }

    public boolean isUseFolderFavorite() {
        return useFolderFavorite;
    }

    public void setUseFolderFavorite(boolean useFolderFavorite) {
        this.useFolderFavorite = useFolderFavorite;
    }

    public String getFolderFavorite() {
        if (folderFavorite == null || folderFavorite.equals("") || !useFolderFavorite) {
            return ivfxProject.getFramesFolder() + "\\" + shortName + FRAMES_FAVORITE_FOLDER_SUFFIX;
        } else {
            return folderFavorite;
        }
    }

    public void setFolderFavorite(String folderFavorite) {
        this.folderFavorite = folderFavorite;
    }

    public String getMediaInfoJson() {
        return mediaInfoJson;
    }

    public boolean isUseFolderLossless() {
        return useFolderLossless;
    }

    public void setUseFolderLossless(boolean useFolderLossless) {
        this.useFolderLossless = useFolderLossless;
    }

    public String getFolderLossless() {
        if (folderLossless == null || folderLossless.equals("") || !useFolderLossless) {
            return ivfxProject.getFolder() + "\\Video\\Lossless";
        } else {
            return folderLossless;
        }
    }

    public String getPathToFileLossless() {
        return getFolderLossless() + "\\" + shortName + "_lossless." + losslessContainer;
    }

    public void setFolderLossless(String folderLossless) {
        this.folderLossless = folderLossless;
    }

    public void setMediaInfoJson(String mediaInfoJson) {
        this.mediaInfoJson = mediaInfoJson;
    }

    public boolean isUseFolderShots() {
        return useFolderShots;
    }

    public void setUseFolderShots(boolean useFolderShots) {
        this.useFolderShots = useFolderShots;
    }

    public String getFolderShots() {
        if (folderShots == null || folderShots.equals("") || !useFolderShots) {
            return ivfxProject.getFolder() + "\\Video\\1920x1080";
        } else {
            return folderShots;
        }
    }

    public void setFolderShots(String folderShots) {
        this.folderShots = folderShots;
    }

    public String getLosslessVideoCodec() {
        return losslessVideoCodec;
    }

    public void setLosslessVideoCodec(String losslessVideoCodec) {
        this.losslessVideoCodec = losslessVideoCodec;
    }

    public String getLosslessContainer() {
        return losslessContainer;
    }

    public void setLosslessContainer(String losslessContainer) {
        this.losslessContainer = losslessContainer;
    }

    public boolean isUseFolderFramesMedium() {
        return useFolderFramesMedium;
    }

    public void setUseFolderFramesMedium(boolean useFolderFramesMedium) {
        this.useFolderFramesMedium = useFolderFramesMedium;
    }
}

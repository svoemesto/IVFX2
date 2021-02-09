package com.svoemesto.ivfx.tables;

import com.svoemesto.ivfx.Main;
import com.svoemesto.ivfx.utils.ConvertToFxImage;
import com.svoemesto.ivfx.utils.OverlayImage;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import org.apache.commons.io.FileUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class IVFXTags {

    public static final String TAG_SUFFIX = "_Tags";
    public static final String TAG_PREFIX = "tag_";
    public static final String TAG_SUFFIX_PREVIEW = "_preview";
    public static final String TAG_SUFFIX_FULLSIZE = "_fullsize";
    public static final String TAG_FULLSIZE_STUB = "blank_tag_400p.jpg";
    public static final String TAG_PREVIEW_STUB = "blank_tag.jpg";

    protected int id;
    protected String name;
    protected IVFXEnumTagsTypes ivfxEnumTagsTypes;
    protected ImageView[] preview = new ImageView[8];
    protected Label[] label = new Label[8];

    protected String fxLabelCaption = "-fx-wrap-text: true; -fx-alignment: CENTER";

    public static void main(String[] args) {

        Main.mainConnection = Database.getConnection();

        IVFXProjects currentProject = IVFXProjects.load(1);

//        IVFXTags tagDialog = IVFXTags.load(4858, false);
//
//        List<IVFXTags> listEvents = IVFXTags.loadList(currentProject,false, 4);
//        for (IVFXTags event: listEvents) {
//            IVFXTagsTags.getNewDbInstance(event, tagDialog,false);
//        }

    }

    public static void createTagsDatabase() {

        Main.mainConnection = Database.getConnection();

        IVFXProjects currentProject = IVFXProjects.load(1);

        List<IVFXFiles> listFiles = IVFXFiles.loadList(currentProject);
        List<IVFXPersons> listPersons = IVFXPersons.loadList(currentProject, false);

        // список персонажей проекта - для каждого создаем тэг
        for (IVFXPersons person: listPersons) {
            IVFXTags tag = IVFXTags.getNewDbInstance(currentProject, IVFXEnumTagsTypes.PERSON, person.getName());
            tag.setPropertyValue("person_id", person.getId()+"");
        }

        // для каждого файла проекта
        for (IVFXFiles file: listFiles) {

            // для каждой сцены проекта создаем тэг
            List<IVFXScenes> listScenes = IVFXScenes.loadList(file);
            for (IVFXScenes scene: listScenes) {
                IVFXTags tag = IVFXTags.getNewDbInstance(currentProject, IVFXEnumTagsTypes.SCENE, scene.getName());
                tag.setPropertyValue("scene_id", scene.getId()+"");
            }

            // для каждого события проекта создаем тэг
            List<IVFXEvents> listEvents = IVFXEvents.loadList(file);
            for (IVFXEvents event: listEvents) {
                IVFXTags tag = IVFXTags.getNewDbInstance(currentProject, IVFXEnumTagsTypes.EVENT, event.getName());
                tag.setPropertyValue("event_id", event.getId()+"");
                tag.setPropertyValue("event_type", event.getEventTypeName());
            }

            // для каждого плана проетка
            List<IVFXShots> listShots = IVFXShots.loadList(file, false);
            for (IVFXShots shot: listShots) {

                // для каждого персонажа плана создаем план-тэг
                List<IVFXPersons> listPersonsInShot = shot.getListPersons();
                for (IVFXPersons persons: listPersonsInShot) {
                    List<IVFXTags> listTagPersons = IVFXTags.loadListByPropertyValue("person_id", persons.getId()+"", false);
                    if (listTagPersons.size() > 0) {
                        IVFXTags tagPerson = listTagPersons.get(0);
                        IVFXTagsShots.getNewDbInstance(tagPerson, shot);
                    }
                }

                // для сцены плана создаем план-тэг
                IVFXScenes sceneInShot = shot.getScene();
                if (sceneInShot != null) {
                    List<IVFXTags> listTagScenes = IVFXTags.loadListByPropertyValue("scene_id", sceneInShot.getId()+"", false);
                    if (listTagScenes.size() > 0) {
                        IVFXTags tagScene = listTagScenes.get(0);
                        IVFXTagsShots.getNewDbInstance(tagScene, shot);

                        // для каждого персонажа сцены создаем тэг-тэг
                        List<IVFXPersons> listPersonsInScene = IVFXScenes.loadListPersons(sceneInShot, false);
                        for (IVFXPersons persons : listPersonsInScene) {
                            List<IVFXTags> listTagPersons = IVFXTags.loadListByPropertyValue("person_id", persons.getId()+"", false);
                            if (listTagPersons.size() > 0) {
                                IVFXTags tagPerson = listTagPersons.get(0);
                                IVFXTagsTags.getNewDbInstance(tagScene, tagPerson, false);
                            }
                        }

                    }
                }

                // для события плана создаем план-тэг
                IVFXEvents eventInShot = shot.getEvent();
                if (eventInShot != null) {
                    List<IVFXTags> listTagEvents = IVFXTags.loadListByPropertyValue("event_id", eventInShot.getId()+"", false);
                    if (listTagEvents.size() > 0) {
                        IVFXTags tagEvent = listTagEvents.get(0);
                        IVFXTagsShots.getNewDbInstance(tagEvent,shot);

                        // для каждого персонажа события создаем тэг-тэг
                        List<IVFXPersons> listPersonsInEvent = IVFXEvents.loadListPersons(eventInShot, false);
                        for (IVFXPersons persons : listPersonsInEvent) {
                            List<IVFXTags> listTagPersons = IVFXTags.loadListByPropertyValue("person_id", persons.getId()+"", false);
                            if (listTagPersons.size() > 0) {
                                IVFXTags tagPerson = listTagPersons.get(0);
                                IVFXTagsTags.getNewDbInstance(tagEvent, tagPerson, false);
                            }
                        }

                    }
                }


            }

        }

    }

    public boolean isEqual(IVFXTags o) {
        return (this.id == o.id);
    }

    public static IVFXTags getNewDbInstance(IVFXProjects ivfxProject, IVFXEnumTagsTypes ivfxEnumTagsTypes) {
        return getNewDbInstance(ivfxProject, ivfxEnumTagsTypes, null, null);
    }

    public static IVFXTags getNewDbInstance(IVFXProjects ivfxProject, IVFXEnumTagsTypes ivfxEnumTagsTypes, String name) {
        return getNewDbInstance(ivfxProject, ivfxEnumTagsTypes, name, null);
    }

    public static IVFXTags getNewDbInstance(IVFXProjects ivfxProject, IVFXEnumTagsTypes ivfxEnumTagsTypes, String name, IVFXFrames frame) {
        IVFXTags ivfxTag = new IVFXTags();

        ivfxTag.ivfxEnumTagsTypes = ivfxEnumTagsTypes;
        ivfxTag.name = name;

        ResultSet rs = null;
        String sql;

        try {

            sql = "INSERT INTO tbl_tags (" +
                    "tag_type_id, name) " +
                    "VALUES(" +
                    IVFXTagsTypes.getID(ivfxEnumTagsTypes) + ", '" + name + "')";

            PreparedStatement ps = Main.mainConnection.prepareStatement(sql);
            ps.executeUpdate(sql, Statement.RETURN_GENERATED_KEYS);
            rs = ps.getGeneratedKeys();
            if (rs.next()) {
                ivfxTag.id = rs.getInt(1);

                if (name == null || name.equals("")) {
                    name = ivfxEnumTagsTypes + " №" + ivfxTag.id;
                    ivfxTag.name = name;
                }

                IVFXTagsProjects.getNewDbInstance(ivfxTag,ivfxProject, true);
                IVFXTagsProperties.getNewDbInstance(ivfxTag,"name", name);
                ivfxTag.setPicture(frame);

                System.out.println("Создана запись для тэга «" + name + "» с идентификатором " + rs.getInt(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close(); // close result set
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return ivfxTag;
    }

    public static IVFXTags load(int id, boolean withPreview) {
        Statement statement = null;
        ResultSet rs = null;
        String sql;

        try {
            statement = Main.mainConnection.createStatement();

            sql = "SELECT * FROM tbl_tags WHERE id = " + id;
            rs = statement.executeQuery(sql);
            if (rs.next()) {
                IVFXTags ivfxTag = new IVFXTags();
                ivfxTag.id = rs.getInt("id");
                ivfxTag.ivfxEnumTagsTypes = IVFXTagsTypes.getEnumTagsTypes(rs.getInt("tag_type_id"));
                ivfxTag.name = rs.getString("name");

                if (withPreview) {
                    String fileName = ivfxTag.getTagPicturePreview();
                    File file = new File(fileName);
                    for (int i = 0; i < 8; i++) {
                        ivfxTag.label[i] = new Label(ivfxTag.name);
                        ivfxTag.label[i].setPrefWidth(135);
                        ivfxTag.label[i].setStyle(ivfxTag.fxLabelCaption);
                        ivfxTag.label[i].setWrapText(true);
                        ivfxTag.label[i].setMaxWidth(135);
                    }

                    if (!file.exists()) {
                        fileName = ivfxTag.getTagPicturePreviewStub();
                        file = new File(fileName);
                    }
                    try {
                        BufferedImage bufferedImage = ImageIO.read(file);
                        for (int i = 0; i < 8; i++) {
                            ivfxTag.preview[i] = new ImageView(ConvertToFxImage.convertToFxImage(bufferedImage));
//                            ivfxTag.preview[i] = new ImageView(ConvertToFxImage.convertToFxImage(OverlayImage.setOverlayUnderlineText(bufferedImage, ivfxTag.name)));
                            ivfxTag.label[i].setGraphic(ivfxTag.preview[i]);
                            ivfxTag.label[i].setContentDisplay(ContentDisplay.TOP);
                        }

                    } catch (IOException e) {}

                }

                return ivfxTag;
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

    public static IVFXTags loadByName(String name, IVFXEnumTagsTypes enumTagsTypes, boolean withPreview) {
        Statement statement = null;
        ResultSet rs = null;
        String sql;

        try {
            statement = Main.mainConnection.createStatement();

            sql = "SELECT * FROM tbl_tags WHERE tag_type_id = " + IVFXTagsTypes.getID(enumTagsTypes) + " AND name = '" + name + "'";
            rs = statement.executeQuery(sql);
            if (rs.next()) {
                IVFXTags ivfxTag = new IVFXTags();
                ivfxTag.id = rs.getInt("id");
                ivfxTag.ivfxEnumTagsTypes = IVFXTagsTypes.getEnumTagsTypes(rs.getInt("tag_type_id"));
                ivfxTag.name = rs.getString("name");

                if (withPreview) {
                    String fileName = ivfxTag.getTagPicturePreview();
                    File file = new File(fileName);
                    for (int i = 0; i < 8; i++) {
                        ivfxTag.label[i] = new Label(ivfxTag.name);
                        ivfxTag.label[i].setPrefWidth(135);
                        ivfxTag.label[i].setStyle(ivfxTag.fxLabelCaption);
                        ivfxTag.label[i].setWrapText(true);
                        ivfxTag.label[i].setMaxWidth(135);
                    }

                    if (!file.exists()) {
                        fileName = ivfxTag.getTagPicturePreviewStub();
                        file = new File(fileName);
                    }
                    try {
                        BufferedImage bufferedImage = ImageIO.read(file);
                        for (int i = 0; i < 8; i++) {
                            ivfxTag.preview[i] = new ImageView(ConvertToFxImage.convertToFxImage(bufferedImage));
//                            ivfxTag.preview[i] = new ImageView(ConvertToFxImage.convertToFxImage(OverlayImage.setOverlayUnderlineText(bufferedImage, ivfxTag.name)));
                            ivfxTag.label[i].setGraphic(ivfxTag.preview[i]);
                            ivfxTag.label[i].setContentDisplay(ContentDisplay.TOP);
                        }

                    } catch (IOException e) {}

                }

                return ivfxTag;
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

    public static IVFXTags loadByName(String name, boolean withPreview) {
        Statement statement = null;
        ResultSet rs = null;
        String sql;

        try {
            statement = Main.mainConnection.createStatement();

            sql = "SELECT * FROM tbl_tags WHERE name = '" + name + "'";
            rs = statement.executeQuery(sql);
            if (rs.next()) {
                IVFXTags ivfxTag = new IVFXTags();
                ivfxTag.id = rs.getInt("id");
                ivfxTag.ivfxEnumTagsTypes = IVFXTagsTypes.getEnumTagsTypes(rs.getInt("tag_type_id"));
                ivfxTag.name = rs.getString("name");

                if (withPreview) {
                    String fileName = ivfxTag.getTagPicturePreview();
                    File file = new File(fileName);
                    for (int i = 0; i < 8; i++) {
                        ivfxTag.label[i] = new Label(ivfxTag.name);
                        ivfxTag.label[i].setPrefWidth(135);
                        ivfxTag.label[i].setStyle(ivfxTag.fxLabelCaption);
                        ivfxTag.label[i].setWrapText(true);
                        ivfxTag.label[i].setMaxWidth(135);
                    }

                    if (!file.exists()) {
                        fileName = ivfxTag.getTagPicturePreviewStub();
                        file = new File(fileName);
                    }
                    try {
                        BufferedImage bufferedImage = ImageIO.read(file);
                        for (int i = 0; i < 8; i++) {
                            ivfxTag.preview[i] = new ImageView(ConvertToFxImage.convertToFxImage(bufferedImage));
//                            ivfxTag.preview[i] = new ImageView(ConvertToFxImage.convertToFxImage(OverlayImage.setOverlayUnderlineText(bufferedImage, ivfxTag.name)));
                            ivfxTag.label[i].setGraphic(ivfxTag.preview[i]);
                            ivfxTag.label[i].setContentDisplay(ContentDisplay.TOP);
                        }

                    } catch (IOException e) {}

                }

                return ivfxTag;
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

    public static List<IVFXTags> loadListByPropertyValue(String propertyName, String propertyValue, boolean withPreview) {
        List<IVFXTags> listTags = new ArrayList<>();

        Statement statement = null;
        ResultSet rs = null;
        String sql;

        try {
            statement = Main.mainConnection.createStatement();

            sql = "SELECT tbl_tags.* FROM tbl_tags JOIN tbl_tags_properties ttp on tbl_tags.id = ttp.tag_id " +
                    "WHERE ttp.tag_property_name = '" + propertyName + "' AND ttp.tag_property_value = '" + propertyValue + "'";

            rs = statement.executeQuery(sql);
            while (rs.next()) {

                IVFXTags ivfxTag = new IVFXTags();
                ivfxTag.id = rs.getInt("id");
                ivfxTag.ivfxEnumTagsTypes = IVFXTagsTypes.getEnumTagsTypes(rs.getInt("tag_type_id"));
                ivfxTag.name = rs.getString("name");

                if (withPreview) {
                    String fileName = ivfxTag.getTagPicturePreview();
                    File file = new File(fileName);
                    for (int i = 0; i < 8; i++) {
                        ivfxTag.label[i] = new Label(ivfxTag.name);
                        ivfxTag.label[i].setPrefWidth(135);
                        ivfxTag.label[i].setStyle(ivfxTag.fxLabelCaption);
                        ivfxTag.label[i].setWrapText(true);
                        ivfxTag.label[i].setMaxWidth(135);
                    }

                    if (!file.exists()) {
                        fileName = ivfxTag.getTagPicturePreviewStub();
                        file = new File(fileName);
                    }
                    try {
                        BufferedImage bufferedImage = ImageIO.read(file);
                        for (int i = 0; i < 8; i++) {
                            ivfxTag.preview[i] = new ImageView(ConvertToFxImage.convertToFxImage(bufferedImage));
//                            ivfxTag.preview[i] = new ImageView(ConvertToFxImage.convertToFxImage(OverlayImage.setOverlayUnderlineText(bufferedImage, ivfxTag.name)));
                            ivfxTag.label[i].setGraphic(ivfxTag.preview[i]);
                            ivfxTag.label[i].setContentDisplay(ContentDisplay.TOP);
                        }

                    } catch (IOException e) {}

                }

                listTags.add(ivfxTag);
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

        return listTags;
    }


    public static List<IVFXTags> loadList(IVFXProjects ivfxProject, boolean withPreview) {
        int[] a = new int[0];
        return loadList(ivfxProject, withPreview, a);
    }

    public static List<IVFXTags> loadList(IVFXFiles ivfxFile, boolean withPreview) {
        int[] a = new int[0];
        return loadList(ivfxFile, withPreview, a);
    }

    public static List<IVFXTags> loadList(IVFXProjects ivfxProject, boolean withPreview, IVFXEnumTagsTypes enumTagsTypes) {
        int[] args = {IVFXTagsTypes.getID(enumTagsTypes)};
        return loadList(ivfxProject, withPreview, args);
    }

    public static List<IVFXTags> loadList(IVFXProjects ivfxProject, boolean withPreview, int tagTypeId) {
        int[] args = {tagTypeId};
        return loadList(ivfxProject, withPreview, args);
    }


    public static List<IVFXTags> loadList(IVFXProjects ivfxProject, boolean withPreview, int[] arrayTagsTypesId) {
        List<IVFXTags> listTags = new ArrayList<>();

        int iProgress = 0;
        Statement statement = null;
        ResultSet rs = null;
        String sql;

        try {
            statement = Main.mainConnection.createStatement();

            if (ivfxProject != null) {
                if (arrayTagsTypesId != null && arrayTagsTypesId.length > 0) {
                    sql = "SELECT tbl_tags.* FROM tbl_tags_projects INNER JOIN tbl_tags ON tbl_tags_projects.tag_id = tbl_tags.id " +
                            "WHERE tbl_tags_projects.project_id = " + ivfxProject.getId() + " AND (";
                    for (int i = 0; i < arrayTagsTypesId.length; i++) {
                        sql += "tbl_tags.tag_type_id = " + arrayTagsTypesId[i];
                        if (i != arrayTagsTypesId.length - 1) sql += " OR ";
                    }
                    sql += ") ORDER BY tbl_tags.tag_type_id";
                } else {
                    sql = "SELECT tbl_tags.* FROM tbl_tags_projects INNER JOIN tbl_tags ON tbl_tags_projects.tag_id = tbl_tags.id " +
                            "WHERE tbl_tags_projects.project_id = " + ivfxProject.getId() + " ORDER BY tbl_tags.tag_type_id";
                }
            } else {
                if (arrayTagsTypesId != null && arrayTagsTypesId.length > 0) {
                    sql = "SELECT tbl_tags.* FROM tbl_tags WHERE (";
                    for (int i = 0; i < arrayTagsTypesId.length; i++) {
                        sql += "tbl_tags.tag_type_id = " + arrayTagsTypesId[i];
                        if (i != arrayTagsTypesId.length - 1) sql += " OR ";
                    }
                    sql += ") ORDER BY tbl_tags.tag_type_id";
                } else {
                    sql = "SELECT tbl_tags.* FROM tbl_tags ORDER BY tbl_tags.tag_type_id";
                }
            }

            rs = statement.executeQuery(sql);
            while (rs.next()) {

                IVFXTags ivfxTag = new IVFXTags();
                ivfxTag.id = rs.getInt("id");
                ivfxTag.ivfxEnumTagsTypes = IVFXTagsTypes.getEnumTagsTypes(rs.getInt("tag_type_id"));
                ivfxTag.name = rs.getString("name");

                if (withPreview) {
                    String fileName = ivfxTag.getTagPicturePreview();
                    File file = new File(fileName);
                    for (int i = 0; i < 8; i++) {
                        ivfxTag.label[i] = new Label(ivfxTag.name);
                        ivfxTag.label[i].setPrefWidth(135);
                        ivfxTag.label[i].setStyle(ivfxTag.fxLabelCaption);
                        ivfxTag.label[i].setWrapText(true);
                        ivfxTag.label[i].setMaxWidth(135);
                    }

                    if (!file.exists()) {
                        fileName = ivfxTag.getTagPicturePreviewStub();
                        file = new File(fileName);
                    }
                    try {
                        BufferedImage bufferedImage = ImageIO.read(file);
                        for (int i = 0; i < 8; i++) {
                            ivfxTag.preview[i] = new ImageView(ConvertToFxImage.convertToFxImage(bufferedImage));
//                            ivfxTag.preview[i] = new ImageView(ConvertToFxImage.convertToFxImage(OverlayImage.setOverlayUnderlineText(bufferedImage, ivfxTag.name)));
                            ivfxTag.label[i].setGraphic(ivfxTag.preview[i]);
                            ivfxTag.label[i].setContentDisplay(ContentDisplay.TOP);
                        }

                    } catch (IOException e) {}

                }

                listTags.add(ivfxTag);
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

        return listTags;
    }

    public static List<IVFXTags> loadList(IVFXFiles ivfxFile, boolean withPreview, IVFXEnumTagsTypes enumTagsTypes) {
        int[] args = {IVFXTagsTypes.getID(enumTagsTypes)};
        return loadList(ivfxFile, withPreview, args);
    }

    public static List<IVFXTags> loadList(IVFXFiles ivfxFile, boolean withPreview, int tagTypeId) {
        int[] args = {tagTypeId};
        return loadList(ivfxFile, withPreview, args);
    }


    public static List<IVFXTags> loadListShotsTags(List<IVFXTags> listScenes, boolean withPreview, int[] arrayTagsTypesId) {
        int[] arrayScenesId = new int[listScenes.size()];
        for (int i = 0; i < listScenes.size(); i++) {
            arrayScenesId[i] = listScenes.get(i).getId();
        }
        return loadListShotsTags(arrayScenesId, withPreview, arrayTagsTypesId);
    }
    // Для набора сцен возвращает список тегов планов, входящих в сцены
    public static List<IVFXTags> loadListShotsTags(int[] arrayScenesId, boolean withPreview, int[] arrayTagsTypesId) {

        List<IVFXTags> listTags = new ArrayList<>();
        if (arrayScenesId != null && arrayScenesId.length > 0) {

            int iProgress = 0;
            Statement statement = null;
            ResultSet rs = null;
            String sql;

            try {
                statement = Main.mainConnection.createStatement();

                String tmpScenesId = "";
                for (int i = 0; i < arrayScenesId.length; i++) {
                    tmpScenesId += "tbl_tags.id = " + arrayScenesId[i];
                    if (i != arrayScenesId.length - 1) tmpScenesId += " OR ";
                }

                if (arrayTagsTypesId != null && arrayTagsTypesId.length > 0) {


                    String tmpTagTypes = "";
                    for (int i = 0; i < arrayTagsTypesId.length; i++) {
                        tmpTagTypes += "tbl_tags_1.tag_type_id = " + arrayTagsTypesId[i];
                        if (i != arrayTagsTypesId.length - 1) tmpTagTypes += " OR ";
                    }

                    sql = "SELECT " +
                            "  tbl_tags.* " +
                            "FROM tbl_tags " +
                            "  INNER JOIN (SELECT " +
                            "      tbl_tags_shots_1.tag_id " +
                            "    FROM tbl_tags_shots " +
                            "      INNER JOIN tbl_tags " +
                            "        ON tbl_tags_shots.tag_id = tbl_tags.id " +
                            "      INNER JOIN tbl_tags_shots tbl_tags_shots_1 " +
                            "        ON tbl_tags_shots.shot_id = tbl_tags_shots_1.shot_id " +
                            "      INNER JOIN tbl_tags tbl_tags_1 " +
                            "        ON tbl_tags_shots_1.tag_id = tbl_tags_1.id " +
                            "    WHERE tbl_tags.tag_type_id = 3 " +
                            "    AND (" + tmpTagTypes + ") " +
                            "    AND (" + tmpScenesId + ") " +
                            "    GROUP BY tbl_tags_shots_1.tag_id) SubQuery " +
                            "    ON tbl_tags.id = SubQuery.tag_id " +
                            "ORDER BY tbl_tags.tag_type_id";

                } else {
                    sql = "SELECT " +
                            "  tbl_tags.* " +
                            "FROM tbl_tags " +
                            "  INNER JOIN (SELECT " +
                            "      tbl_tags_shots_1.tag_id " +
                            "    FROM tbl_tags_shots " +
                            "      INNER JOIN tbl_tags " +
                            "        ON tbl_tags_shots.tag_id = tbl_tags.id " +
                            "      INNER JOIN tbl_tags_shots tbl_tags_shots_1 " +
                            "        ON tbl_tags_shots.shot_id = tbl_tags_shots_1.shot_id " +
                            "      INNER JOIN tbl_tags tbl_tags_1 " +
                            "        ON tbl_tags_shots_1.tag_id = tbl_tags_1.id " +
                            "    WHERE tbl_tags.tag_type_id = 3 " +
                            "    AND (" + tmpScenesId + ") " +
                            "    GROUP BY tbl_tags_shots_1.tag_id) SubQuery " +
                            "    ON tbl_tags.id = SubQuery.tag_id " +
                            "ORDER BY tbl_tags.tag_type_id";
                }

                rs = statement.executeQuery(sql);
                while (rs.next()) {

                    IVFXTags ivfxTag = new IVFXTags();
                    ivfxTag.id = rs.getInt("id");
                    ivfxTag.ivfxEnumTagsTypes = IVFXTagsTypes.getEnumTagsTypes(rs.getInt("tag_type_id"));
                    ivfxTag.name = rs.getString("name");

                    if (withPreview) {
                        String fileName = ivfxTag.getTagPicturePreview();
                        File file = new File(fileName);
                        for (int i = 0; i < 8; i++) {
                            ivfxTag.label[i] = new Label(ivfxTag.name);
                            ivfxTag.label[i].setPrefWidth(135);
                            ivfxTag.label[i].setStyle(ivfxTag.fxLabelCaption);
                            ivfxTag.label[i].setWrapText(true);
                            ivfxTag.label[i].setMaxWidth(135);
                        }

                        if (!file.exists()) {
                            fileName = ivfxTag.getTagPicturePreviewStub();
                            file = new File(fileName);
                        }
                        try {
                            BufferedImage bufferedImage = ImageIO.read(file);
                            for (int i = 0; i < 8; i++) {
                                ivfxTag.preview[i] = new ImageView(ConvertToFxImage.convertToFxImage(bufferedImage));
//                                ivfxTag.preview[i] = new ImageView(ConvertToFxImage.convertToFxImage(OverlayImage.setOverlayUnderlineText(bufferedImage, ivfxTag.name)));
                                ivfxTag.label[i].setGraphic(ivfxTag.preview[i]);
                                ivfxTag.label[i].setContentDisplay(ContentDisplay.TOP);
                            }

                        } catch (IOException e) {
                        }

                    }

                    listTags.add(ivfxTag);
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
        }

        return listTags;

    }

    public static List<IVFXTags> loadList(IVFXFiles ivfxFile, boolean withPreview, int[] arrayTagsTypesId) {
        List<IVFXTags> listTags = new ArrayList<>();
        if (ivfxFile != null) {

            int iProgress = 0;
            Statement statement = null;
            ResultSet rs = null;
            String sql;

            try {
                statement = Main.mainConnection.createStatement();


                if (arrayTagsTypesId != null && arrayTagsTypesId.length > 0) {

                    String tmp = "";
                    for (int i = 0; i < arrayTagsTypesId.length; i++) {
                        tmp += "tbl_tags.tag_type_id = " + arrayTagsTypesId[i];
                        if (i != arrayTagsTypesId.length - 1) tmp += " OR ";
                    }

                    sql = "SELECT tbl_tags.* " +
                            "FROM (SELECT" +
                            "    tbl_tags.id," +
                            "    tbl_tags.name" +
                            "  FROM tbl_tags_shots" +
                            "    INNER JOIN tbl_tags" +
                            "      ON tbl_tags_shots.tag_id = tbl_tags.id" +
                            "    INNER JOIN tbl_shots" +
                            "      ON tbl_tags_shots.shot_id = tbl_shots.id" +
                            "  WHERE tbl_shots.file_id = " + ivfxFile.getId() + " AND (" + tmp + ") " +
                            "  GROUP BY tbl_tags.id," +
                            "           tbl_tags.name) SubQuery" +
                            "  INNER JOIN tbl_tags" +
                            "    ON SubQuery.id = tbl_tags.id" +
                            "    AND tbl_tags.id = SubQuery.id " +
                            " ORDER BY tbl_tags.tag_type_id";

                } else {
                    sql = "SELECT" +
                            "  tbl_tags.*" +
                            "FROM (SELECT" +
                            "    tbl_tags.id," +
                            "    tbl_tags.name" +
                            "  FROM tbl_tags_shots" +
                            "    INNER JOIN tbl_tags" +
                            "      ON tbl_tags_shots.tag_id = tbl_tags.id" +
                            "    INNER JOIN tbl_shots" +
                            "      ON tbl_tags_shots.shot_id = tbl_shots.id" +
                            "  WHERE tbl_shots.file_id = " + ivfxFile.getId() + " " +
                            "  GROUP BY tbl_tags.id," +
                            "           tbl_tags.name) SubQuery" +
                            "  INNER JOIN tbl_tags" +
                            "    ON SubQuery.id = tbl_tags.id" +
                            "    AND tbl_tags.id = SubQuery.id " +
                            " ORDER BY tbl_tags.tag_type_id";
                }

                rs = statement.executeQuery(sql);
                while (rs.next()) {

                    IVFXTags ivfxTag = new IVFXTags();
                    ivfxTag.id = rs.getInt("id");
                    ivfxTag.ivfxEnumTagsTypes = IVFXTagsTypes.getEnumTagsTypes(rs.getInt("tag_type_id"));
                    ivfxTag.name = rs.getString("name");

                    if (withPreview) {
                        String fileName = ivfxTag.getTagPicturePreview();
                        File file = new File(fileName);
                        for (int i = 0; i < 8; i++) {
                            ivfxTag.label[i] = new Label(ivfxTag.name);
                            ivfxTag.label[i].setPrefWidth(135);
                            ivfxTag.label[i].setStyle(ivfxTag.fxLabelCaption);
                            ivfxTag.label[i].setWrapText(true);
                            ivfxTag.label[i].setMaxWidth(135);
                        }

                        if (!file.exists()) {
                            fileName = ivfxTag.getTagPicturePreviewStub();
                            file = new File(fileName);
                        }
                        try {
                            BufferedImage bufferedImage = ImageIO.read(file);
                            for (int i = 0; i < 8; i++) {
                                ivfxTag.preview[i] = new ImageView(ConvertToFxImage.convertToFxImage(bufferedImage));
//                                ivfxTag.preview[i] = new ImageView(ConvertToFxImage.convertToFxImage(OverlayImage.setOverlayUnderlineText(bufferedImage, ivfxTag.name)));
                                ivfxTag.label[i].setGraphic(ivfxTag.preview[i]);
                                ivfxTag.label[i].setContentDisplay(ContentDisplay.TOP);
                            }

                        } catch (IOException e) {
                        }

                    }

                    listTags.add(ivfxTag);
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
        }

        return listTags;
    }



    // TODO SAVE

    public void save() {
        String sql = "UPDATE tbl_tags SET " +
                "tag_type_id = ?, name = ? " +
                "WHERE id = ?";
        try {
            PreparedStatement ps = Main.mainConnection.prepareStatement(sql);
            ps.setInt   (1, IVFXTagsTypes.getID(this.ivfxEnumTagsTypes));
            ps.setString(2, this.name);
            ps.setInt   (3, this.id);
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

// TODO DELETE


    public void delete() {

        String sql = "DELETE FROM tbl_tags WHERE id = ?";
        try {
            PreparedStatement ps = Main.mainConnection.prepareStatement(sql);
            ps.setInt   (1, this.id);
            ps.executeUpdate();
            ps.close();
            clearPicture();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Разрезает текущую сцену по переданному плану сверху или снизу, возвращает новую сцену
    public IVFXTags cutScene(IVFXProjects ivfxProject, IVFXTagsShots tagShot, boolean cutUp) {

        String name = this.name;
        if (cutUp) {
            this.setName(name + "-1");
        } else {
            name += "-1";
        }

        IVFXTags tagScene = IVFXTagsScenes.getNewDbInstance(ivfxProject, IVFXEnumTagsTypes.SCENE, name);

        List<IVFXTagsShots> listTagsShots = IVFXTagsShots.loadList(this, true);

        if (cutUp) {
            for (IVFXTagsShots tmp: listTagsShots) {
                if (tmp.getId() == tagShot.getId()) {
                    break;
                }
                tmp.setTagId(tagScene.getId());
                tmp.setIvfxTag(tagScene);
                tmp.save();
            }
        } else {
            boolean isFind = false;
            for (IVFXTagsShots tmp: listTagsShots) {
                if (isFind) {
                    tmp.setTagId(tagScene.getId());
                    tmp.setIvfxTag(tagScene);
                    tmp.save();
                }
                if (!isFind && tmp.getId() == tagShot.getId()) {
                    isFind = true;
                }
            }
        }

        return tagScene;

    }

    // Разрезает текущее событие по переданному плану сверху или снизу, возвращает новое событие

    public IVFXTags cutEvent(IVFXProjects ivfxProject, IVFXTagsShots tagShot, boolean cutUp) {

        String name = this.name;
        if (cutUp) {
            this.setName(name + "-1");
        } else {
            name += "-1";
        }

        IVFXTags tagEvent = IVFXTagsEvents.getNewDbInstance(ivfxProject, IVFXEnumTagsTypes.EVENT, name);

        // Копируем в новое событие все теги резрезаемого события
        List<IVFXTagsTags> lstTagTag = IVFXTagsTags.loadList(this, true);
        for (IVFXTagsTags tagTag: lstTagTag) {
            IVFXTagsTags.getNewDbInstance(tagEvent,tagTag.getIvfxTagChild(),true);
        }

        List<IVFXTagsShots> listTagsShots = IVFXTagsShots.loadList(this, true);

        if (cutUp) {
            for (IVFXTagsShots tmp: listTagsShots) {
                if (tmp.getId() == tagShot.getId()) {
                    break;
                }
                tmp.setTagId(tagEvent.getId());
                tmp.setIvfxTag(tagEvent);
                tmp.save();
            }
        } else {
            boolean isFind = false;
            for (IVFXTagsShots tmp: listTagsShots) {
                if (isFind) {
                    tmp.setTagId(tagEvent.getId());
                    tmp.setIvfxTag(tagEvent);
                    tmp.save();
                }
                if (!isFind && tmp.getId() == tagShot.getId()) {
                    isFind = true;
                }
            }
        }

        return tagEvent;

    }
    
    // Сбрасываем картинки тэга
    public void clearPicture() {
        String pathToPreview = getTagPicturePreview();
        String pathToFullView = getTagPictureFullSize();
        File filePreview = new File(pathToPreview);
        if (filePreview.exists()) filePreview.delete();
        File fileFullView = new File(pathToFullView);
        if (fileFullView.exists()) fileFullView.delete();
    }

    // Устанавливаем картинками тега картинки из переданного кадра
    public void setPicture(IVFXFrames frame) {

        if (frame != null) {
            try {
                FileUtils.copyFile(new File(frame.getFileNamePreview()), new File(getTagPicturePreview()));
                FileUtils.copyFile(new File(frame.getFileNameFullSize()), new File(getTagPictureFullSize()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    // Устанавливаем картинками тега картинку, переданную по ссылке на файл
    public void setPicture(String pathToFile) {

        File sourceFile = new File(pathToFile);
        if (sourceFile.exists()) {
            try {
                BufferedImage sourceBufferedImage = ImageIO.read(sourceFile);
                BufferedImage resizedPreview = OverlayImage.resizeImage(sourceBufferedImage, 135, 75, Color.BLACK);
                BufferedImage resizedFullView = OverlayImage.resizeImage(sourceBufferedImage, 720, 400, Color.BLACK);
                ImageIO.write(resizedPreview, "JPG", new File(getTagPicturePreview()));
                ImageIO.write(resizedFullView, "JPG", new File(getTagPictureFullSize()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public void setPropertyValue(String propertyName, String propertyValue) {
        IVFXTagsProperties tagsProperties = IVFXTagsProperties.loadByName(this.id, propertyName,false);
        if (tagsProperties != null) {
            tagsProperties.setValue(propertyValue);
            tagsProperties.save();
        } else {
            IVFXTagsProperties.getNewDbInstance(this, propertyName, propertyValue);
        }
    }

    public String getPropertyValue(String propertyName) {
        String result = null;
        IVFXTagsProperties tagsProperties = IVFXTagsProperties.loadByName(this.id, propertyName,false);
        if (tagsProperties != null) result = tagsProperties.getValue();
        return result;
    }

    public String getPropertiesValues() {
        boolean withChildrenProperties = true;
        String separator =";";
        String result = "";
        List<IVFXTagsProperties> listTagsProperties = IVFXTagsProperties.loadList(this,false);
        for (IVFXTagsProperties tagProperty: listTagsProperties) {
            if (!tagProperty.getValue().equals("")) {
                result += tagProperty.getValue() + separator;
            }
        }
        if (withChildrenProperties) {
            List<IVFXTags> listChildrenTags = loadListChildrens(false, null);
            for (IVFXTags children: listChildrenTags) {
                result += children.getPropertiesValues();
            }
        }
        return result;
    }

    public List<IVFXTags> loadListChildrens(boolean withPreview, int[] arrayTagsTypesId) {
        List<IVFXTags> listChildrenTags = new ArrayList<>();
        List<IVFXTagsTags> listTagsTags = IVFXTagsTags.loadList(this, false, withPreview, arrayTagsTypesId);
        for (IVFXTagsTags tagTag: listTagsTags) {
            listChildrenTags.add(tagTag.getIvfxTagChild());
        }
        return listChildrenTags;
    }

    public List<IVFXTags> loadListParents(boolean withPreview, int[] arrayTagsTypesId) {
        List<IVFXTags> listParentTags = new ArrayList<>();
        List<IVFXTagsTags> listTagsTags = IVFXTagsTags.loadList(this,true, withPreview, arrayTagsTypesId);
        for (IVFXTagsTags tagTag: listTagsTags) {
            listParentTags.add(tagTag.getIvfxTagParent());
        }
        return listParentTags;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        if (!this.getPropertyValue("name").equals(name)) {
           this.setPropertyValue("name", name);
        }
    }

    public IVFXEnumTagsTypes getIvfxEnumTagsTypes() {
        return ivfxEnumTagsTypes;
    }

    public void setIvfxEnumTagsTypes(IVFXEnumTagsTypes ivfxEnumTagsTypes) {
        this.ivfxEnumTagsTypes = ivfxEnumTagsTypes;
    }

    public ImageView[] getPreview() {
        return preview;
    }

    public ImageView getPreview(int i) {
        return preview[i];
    }

    public ImageView getPreview1() {
        return preview[0];
    }

    public ImageView getPreview2() {
        return preview[1];
    }

    public ImageView getPreview3() {
        return preview[2];
    }

    public ImageView getPreview4() {
        return preview[3];
    }

    public ImageView getPreview5() {
        return preview[4];
    }

    public ImageView getPreview6() {
        return preview[5];
    }

    public ImageView getPreview7() {
        return preview[6];
    }

    public ImageView getPreview8() {
        return preview[7];
    }

    public Label[] getLabel() {
        return label;
    }

    public Label getLabel(int i) {
        return label[i];
    }

    public Label getLabel1() {
        return label[0];
    }

    public Label getLabel2() {
        return label[1];
    }

    public Label getLabel3() {
        return label[2];
    }

    public Label getLabel4() {
        return label[3];
    }

    public Label getLabel5() {
        return label[4];
    }

    public Label getLabel6() {
        return label[5];
    }

    public Label getLabel7() {
        return label[6];
    }

    public Label getLabel8() {
        return label[7];
    }

    public void setPreview(ImageView[] preview) {
        this.preview = preview;
    }

    public void setPreview(ImageView preview, int i) {
        this.preview[i] = preview;
    }

    public String getTagTypeName() {
        return ivfxEnumTagsTypes.name();
    }

    public String getTagTypeLetter() {
        return getTagTypeName().substring(0,1);
    }

    public String getTagPicturePreview() {
        String fileName = Main.getMainTasgFolder() + "\\" + TAG_PREFIX + this.getId() + TAG_SUFFIX_PREVIEW + ".jpg";
        return fileName;
    }

    public String getTagPictureFullSize() {
        String fileName = Main.getMainTasgFolder() + "\\" + TAG_PREFIX + this.getId() + TAG_SUFFIX_FULLSIZE + ".jpg";
        return fileName;
    }

    public String getTagPicturePreviewStub() {
//        String fileName = Main.getMainTasgFolder() + "\\" + TAG_PREVIEW_STUB;
        String fileName = "";
        return fileName;
    }

    public String getTagPictureFullSizeStub() {
        String fileName = Main.getMainTasgFolder() + "\\" + TAG_FULLSIZE_STUB;
        return fileName;
    }

}

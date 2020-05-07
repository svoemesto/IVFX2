package com.svoemesto.ivfx.tables;

import com.svoemesto.ivfx.Main;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class IVFXScenesPersons {

    private int id;
    private int sceneId;
    private int personId;
    private UUID uuid = UUID.randomUUID();
    private UUID sceneUuid;
    private UUID personUuid;
    private IVFXScenes ivfxScene;
    private IVFXPersons ivfxPerson;
    private Boolean personIsMain  = true;
    private transient ImageView preview;
    private transient Label label;

    public static transient boolean WORK_WHITH_DATABASE = true;
//TODO ISEQUAL

    public boolean isEqual(IVFXScenesPersons o) {
        return (this.id == o.id &&
                this.sceneId == o.sceneId &&
                this.personId == o.personId &&
                this.uuid.equals(o.uuid) &&
                this.sceneUuid.equals(o.sceneUuid) &&
                this.personUuid.equals(o.personUuid) &&
                this.personIsMain == o.personIsMain);
    }

// TODO КОНСТРУКТОРЫ

    // пустой конструктор
    public IVFXScenesPersons() {
    }


    public static List<IVFXScenesPersons> loadList(IVFXScenes ivfxScene, boolean withPreview) {
        List<IVFXScenesPersons> listScenesPersons = new ArrayList<>();

        Statement statement = null;
        ResultSet rs = null;
        String sql;

        try {
            statement = Main.mainConnection.createStatement();

            sql = "SELECT * FROM tbl_scenes_persons WHERE scene_id = " + ivfxScene.getId();
            rs = statement.executeQuery(sql);
            while (rs.next()) {
                IVFXScenesPersons scenePerson = new IVFXScenesPersons();
                scenePerson.id = rs.getInt("id");
                scenePerson.sceneId = rs.getInt("scene_id");
                scenePerson.personId = rs.getInt("person_id");
                scenePerson.uuid = UUID.fromString(rs.getString("uuid"));
                scenePerson.sceneUuid = UUID.fromString(rs.getString("scene_uuid"));
                scenePerson.personIsMain = rs.getBoolean("personIsMain");
                scenePerson.personUuid = UUID.fromString(rs.getString("person_uuid"));
                scenePerson.ivfxScene = IVFXScenes.loadById(scenePerson.sceneId);
                scenePerson.ivfxPerson = IVFXPersons.loadById(scenePerson.personId,withPreview);
                scenePerson.preview = scenePerson.ivfxPerson.getPreview();
                scenePerson.label = scenePerson.ivfxPerson.getLabel();
                listScenesPersons.add(scenePerson);
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

        return listScenesPersons;
    }

    // TODO GETTERS SETTERS



    public ImageView getPreview() {
        return this.preview;
    }

    public void setPreview(ImageView preview) {
        this.preview = preview;
    }

    public String getName() {
        return this.ivfxPerson.getName();
    }



    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public UUID getSceneUuid() {
        return sceneUuid;
    }

    public void setSceneUuid(UUID sceneUuid) {
        this.sceneUuid = sceneUuid;
    }

    public UUID getPersonUuid() {
        return personUuid;
    }

    public void setPersonUuid(UUID personUuid) {
        this.personUuid = personUuid;
    }

    public IVFXScenes getIvfxScene() {
        return ivfxScene;
    }

    public void setIvfxScene(IVFXScenes ivfxScene) {
        this.ivfxScene = ivfxScene;
    }

    public IVFXPersons getIvfxPerson() {
        return ivfxPerson;
    }

    public void setIvfxPerson(IVFXPersons ivfxPerson) {
        this.ivfxPerson = ivfxPerson;
    }


    public String getPersonIsMainStr() {
        return this.personIsMain ? "+" : "";
    }

    public void setPersonIsMainStr(String str) {
        if (str.equals("") || str == null) {
            this.personIsMain = false;
        } else {
            this.personIsMain = true;
        }
    }

    public Boolean getPersonIsMain() {
        return personIsMain;
    }

    public void setPersonIsMain(Boolean personIsMain) {
        this.personIsMain = personIsMain;
    }

    public Label getLabel() {
        return label;
    }

    public void setLabel(Label label) {
        this.label = label;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSceneId() {
        return sceneId;
    }

    public void setSceneId(int sceneId) {
        this.sceneId = sceneId;
    }

    public int getPersonId() {
        return personId;
    }

    public void setPersonId(int personId) {
        this.personId = personId;
    }

}

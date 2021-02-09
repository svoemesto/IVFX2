package com.svoemesto.ivfx.tables;

import com.svoemesto.ivfx.Main;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.ImageView;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class IVFXScenesPersons {

    private int id;
    private int sceneId;
    private int personId;
    private IVFXScenes ivfxScene;
    private IVFXPersons ivfxPerson;
    private Boolean personIsMain  = true;
    private ImageView[] preview = new ImageView[8];
    private Label[] label = new Label[8];


    public static transient boolean WORK_WHITH_DATABASE = true;
//TODO ISEQUAL

    public boolean isEqual(IVFXScenesPersons o) {
        return (this.id == o.id &&
                this.sceneId == o.sceneId &&
                this.personId == o.personId &&
                this.personIsMain == o.personIsMain);
    }

// TODO КОНСТРУКТОРЫ

    // пустой конструктор
    public IVFXScenesPersons() {
    }


    public static IVFXScenesPersons getNewDbInstance(IVFXScenes ivfxScene, IVFXPersons ivfxPerson) {
        IVFXScenesPersons ivfxScenePerson = new IVFXScenesPersons();

        ivfxScenePerson.sceneId = ivfxScene.getId();
        ivfxScenePerson.personId = ivfxPerson.getId();
        ivfxScenePerson.ivfxScene = ivfxScene;
        ivfxScenePerson.ivfxPerson = ivfxPerson;

        Statement statement = null;
        ResultSet rs = null;
        String sql;

        try {

            sql = "INSERT INTO tbl_scenes_persons (" +
                    "scene_id, " +
                    "person_id, " +
                    "personIsMain) " +
                    "VALUES(" +
                    ivfxScenePerson.sceneId + "," +
                    ivfxScenePerson.personId + "," +
                    (ivfxScenePerson.personIsMain ? 1 : 0) + ")";

            PreparedStatement ps = Main.mainConnection.prepareStatement(sql);
            ps.executeUpdate(sql, Statement.RETURN_GENERATED_KEYS);
            rs = ps.getGeneratedKeys();
            if (rs.next()) {
                ivfxScenePerson.id = rs.getInt(1);
                System.out.println("Создана запись для персонажа «" + ivfxScenePerson.ivfxPerson.getName() + "» сцены «" + ivfxScenePerson.ivfxScene.getName() + "» файла «" + ivfxScenePerson.ivfxScene.getIvfxFile().getTitle() + "» с идентификатором " + rs.getInt(1));
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


        return ivfxScenePerson;
    }

    public static List<IVFXScenesPersons> loadList(IVFXScenes ivfxScene, boolean withPreview) {
        return loadList(ivfxScene, withPreview, null);
    }
    public static List<IVFXScenesPersons> loadList(IVFXScenes ivfxScene, boolean withPreview, ProgressBar progressBar) {
        List<IVFXScenesPersons> listScenesPersons = new ArrayList<>();

        int iProgress = 0;
        Statement statement = null;
        ResultSet rs = null;
        String sql;

        try {
            statement = Main.mainConnection.createStatement();

            sql = "SELECT * FROM tbl_scenes_persons WHERE scene_id = " + ivfxScene.getId();

            String sqlCnt = "SELECT COUNT(*) AS CNT FROM (" + sql + ") AS tmp";
            ResultSet rsCnt = null;
            rsCnt = statement.executeQuery(sqlCnt);
            rsCnt.next();
            int countRows = rsCnt.getInt("CNT");
            rsCnt.close();

            rs = statement.executeQuery(sql);
            while (rs.next()) {

                if (progressBar != null) {
                    progressBar.setProgress((double)++iProgress / countRows);
                }



                IVFXScenesPersons scenePerson = new IVFXScenesPersons();
                scenePerson.id = rs.getInt("id");
                scenePerson.sceneId = rs.getInt("scene_id");
                scenePerson.personId = rs.getInt("person_id");
                scenePerson.personIsMain = rs.getBoolean("personIsMain");
                scenePerson.ivfxScene = IVFXScenes.load(scenePerson.sceneId);
                scenePerson.ivfxPerson = IVFXPersons.load(scenePerson.personId,withPreview);
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

    public void save() {
        String sql = "UPDATE tbl_scenes_persons SET scene_id = ?, person_id = ?, personIsMain = ? WHERE id = ?";
        try {
            PreparedStatement ps = Main.mainConnection.prepareStatement(sql);
            ps.setInt   (1, this.sceneId);
            ps.setInt   (2, this.personId);
            ps.setBoolean(3, this.personIsMain);
            ps.setInt(4, this.id);
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void delete() {
        String sql = "DELETE FROM tbl_scenes_persons WHERE id = ?";
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



    public ImageView[] getPreview() {
        return preview;
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


    public void setPreview(ImageView[] preview) {
        this.preview = preview;
    }

    public String getName() {
        return this.ivfxPerson.getName();
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

    public Label[] getLabel() {
        return label;
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


    public void setLabel(Label[] label) {
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

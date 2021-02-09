package com.svoemesto.ivfx.tables;

import com.svoemesto.ivfx.Main;
import com.svoemesto.ivfx.utils.FFmpeg;
import javafx.scene.control.ProgressBar;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class IVFXScenes {

    public static transient final String SCENES_SUFFIX = "_Scenes";

    private int id;
    private int fileId;
    private IVFXFiles ivfxFile;
    private int order;
    private String name = "Новая сцена";
    private String description="";

    private List<IVFXShots> shots = new ArrayList<>();
    private List<IVFXScenesPersons> scenePersons = new ArrayList<>();
    private List<IVFXScenesPersons> scenePersons2 = new ArrayList<>();

//TODO ISEQUAL

    public boolean isEqual(IVFXScenes o) {
        if (o == null) return false;
        return (this.id == o.id &&
                this.fileId == o.fileId &&
                this.order == o.order &&
                this.name.equals(o.name) &&
                this.description.equals(o.description));
    }

// TODO КОНСТРУКТОРЫ

    // пустой конструктор
    public IVFXScenes() {

    }

    public static IVFXScenes getNewDbInstance(IVFXFiles ivfxFile) {
        if (ivfxFile != null) {
            List<IVFXShots> list = IVFXShots.loadList(ivfxFile, false);
            if (list.size() > 0) {
                return getNewDbInstance(list.get(0));
            }
        }
        return null;
    }

    // создание новой сцены, начиная с указанного плана
    public static IVFXScenes getNewDbInstance(IVFXShots ivfxShot) {

        IVFXFiles ivfxFile = ivfxShot.getIvfxFile(); // получаем файл, для котого будет создана новая сцена
        List<IVFXScenes> listScenes = IVFXScenes.loadList(ivfxFile); // получаем список сцен файла
        IVFXScenes returnedScene, tempScene;
        tempScene =  new IVFXScenes();
        tempScene.fileId = ivfxFile.getId();
        tempScene.ivfxFile = ivfxFile;

        List<IVFXScenesShots> listReturnedSceneShots = new ArrayList<>();
        List<IVFXScenesShots> listCuttedSceneShots = new ArrayList<>();
        List<IVFXScenes> listReturnedScenes = new ArrayList<>();

        // для начала надо найти, если ли в списке сцена, начинающаяся с указаного плана - тогда новую сцену создавать не надо
        for (IVFXScenes scene : listScenes) if (ivfxShot.getId() == scene.getFirstShot().getId()) return scene;

        IVFXScenes ivfxScene = ivfxShot.getScene(); // получаем сцену, содержащую план

        // надо разрезать эту сцену на две части, и "нижнюю" часть сформировать в новую сцену
        List<IVFXScenesShots> listSceneShots = new ArrayList<>();  // listSceneShots - пустой список
        if (ivfxScene != null) {   // если сцена есть
            listSceneShots = IVFXScenesShots.loadList(ivfxScene);   // загружаем список планов сцены
        } else { // если ни одной сцены еще не было создано

            ivfxScene = new IVFXScenes(); // создаем новую сцену для файла
            ivfxScene.fileId = ivfxFile.getId();
            ivfxScene.ivfxFile = ivfxFile;

            Statement statement = null;
            ResultSet rs = null;
            String sql;

            try {
                statement = Main.mainConnection.createStatement();

                sql = "SELECT COUNT(*) FROM tbl_scenes WHERE file_id = " + ivfxScene.fileId;
                rs = statement.executeQuery(sql);
                if (rs.next()) {
                    ivfxScene.order = rs.getInt(1) + 1;  // индексация столбцов начинается с единицы
                    ivfxScene.name = "Новая сцена # " + ivfxScene.order;
                }

                sql = "INSERT INTO tbl_scenes (" +
                        "file_id, " +
                        "order_scene, " +
                        "name, " +
                        "description) " +
                        "VALUES(" +
                        ivfxScene.fileId + "," +
                        ivfxScene.order + "," +
                        "'" + ivfxScene.name + "'" + "," +
                        "'" + ivfxScene.description + "'" + ")";

                PreparedStatement ps = Main.mainConnection.prepareStatement(sql);
                ps.executeUpdate(sql, Statement.RETURN_GENERATED_KEYS);
                rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    ivfxScene.id = rs.getInt(1);
                    System.out.println("Создана запись для сцены «" + ivfxScene.name + "», файл «" + ivfxScene.ivfxFile.getTitle() + "» с идентификатором " + rs.getInt(1));
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

            List<IVFXShots> listShots = IVFXShots.loadList(ivfxFile,false); // загружаем спискок планов файла
            // и добавляем из него всё в список планов сцены
            for (IVFXShots shot : listShots) {
                IVFXScenesShots.getNewDbInstance(ivfxScene, shot);
            }
            return ivfxScene;
        }

        boolean isFindShot = false;
        // цикл по планам сцены
        for (IVFXScenesShots sceneShot : listSceneShots) {
            // находим в списке план, совпадающий с выбранным в форме
            if (sceneShot.getShotId() == ivfxShot.getId()) {
                isFindShot = true;
            }
            // если нашли
            if (isFindShot) {
                listReturnedSceneShots.add(sceneShot);  // добавляем его в новый результирующий список
            } else {    // иначе
                listCuttedSceneShots.add(sceneShot); // добавляем его в список резки
            }
        }

        // В данной точке список listCuttedSceneShots содержит все планы с начального до выбранного (не включая выбранный)
        // В данной точке список listReturnedSceneShots содержит все планы с выбранного до конечного


        returnedScene = new IVFXScenes(); // создаем новую сцену для файла
        returnedScene.fileId = ivfxFile.getId();
        returnedScene.ivfxFile = ivfxFile;

        Statement statement = null;
        ResultSet rs = null;
        String sql;

        try {
            statement = Main.mainConnection.createStatement();

            sql = "SELECT COUNT(*) FROM tbl_scenes WHERE file_id = " + returnedScene.fileId;
            rs = statement.executeQuery(sql);
            if (rs.next()) {
                returnedScene.order = rs.getInt(1) + 1;  // индексация столбцов начинается с единицы
                returnedScene.name = "Новая сцена # " + returnedScene.order;
            }

            sql = "INSERT INTO tbl_scenes (" +
                    "file_id, " +
                    "order_scene, " +
                    "name, " +
                    "description) " +
                    "VALUES(" +
                    returnedScene.fileId + "," +
                    returnedScene.order + "," +
                    "'" + returnedScene.name + "'" + "," +
                    "'" + returnedScene.description + "'" + ")";

            PreparedStatement ps = Main.mainConnection.prepareStatement(sql);
            ps.executeUpdate(sql, Statement.RETURN_GENERATED_KEYS);
            rs = ps.getGeneratedKeys();
            if (rs.next()) {
                returnedScene.id = rs.getInt(1);
                System.out.println("Создана запись для сцены «" + returnedScene.name + "», файл «" + returnedScene.ivfxFile.getTitle() + "» с идентификатором " + rs.getInt(1));
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

        int order = 0;
        for (IVFXScenesShots sceneShot: listReturnedSceneShots) {
            order++;
            sceneShot.setOrder(order);
            sceneShot.setIvfxScene(returnedScene);
            sceneShot.setSceneId(returnedScene.id);
            sceneShot.save();
        }

        // теперь надо поместить созданную сцену в правильное место списка сцен файла

        int increment = 0;
        for (IVFXScenes scene : listScenes) {
            if (scene.getId() == ivfxScene.getId()) {
                listReturnedScenes.add(scene);
                increment = 1;
                returnedScene.order = scene.order + increment;
                returnedScene.save();
                listReturnedScenes.add(returnedScene);
            } else {
                if (increment != 0) {
                    scene.order = scene.order + increment;
                    scene.save();
                }
                listReturnedScenes.add(scene);
            }
        }

        return returnedScene;

    }



    public static IVFXScenes load(int id) {

        Statement statement = null;
        ResultSet rs = null;
        String sql;

        try {
            statement = Main.mainConnection.createStatement();

            sql = "SELECT * FROM tbl_scenes WHERE id = " + id;
            rs = statement.executeQuery(sql);
            if (rs.next()) {
                IVFXScenes scene = new IVFXScenes();
                scene.id = rs.getInt("id");
                scene.fileId = rs.getInt("file_id");
                scene.order = rs.getInt("order_scene");
                scene.name = rs.getString("name");
                scene.description = rs.getString("description");
                scene.ivfxFile = IVFXFiles.load(scene.fileId);

                return scene;
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

    public static List<IVFXScenes> loadList(IVFXFiles ivfxFiles) {
        return loadList(ivfxFiles, null);
    }
    public static List<IVFXScenes> loadList(IVFXFiles ivfxFiles, ProgressBar progressBar) {

        List<IVFXScenes> listScenes = new ArrayList<>();

        int iProgress = 0;
        Statement statement = null;
        ResultSet rs = null;
        String sql;

        try {
            statement = Main.mainConnection.createStatement();

            sql = "SELECT * FROM tbl_scenes WHERE file_id = " + ivfxFiles.getId() + " ORDER BY order_scene";

            String sqlCnt = "SELECT COUNT(*) AS CNT FROM (" + sql + ") AS tmp";
            ResultSet rsCnt = null;
            rsCnt = statement.executeQuery(sqlCnt);
            rsCnt.next();
            int countRows = rsCnt.getInt("CNT");
            rsCnt.close();

            rs = statement.executeQuery(sql);
            while (rs.next()) {

                if (progressBar != null) progressBar.setProgress((double)++iProgress / countRows);

                IVFXScenes scene = new IVFXScenes();
                scene.id = rs.getInt("id");
                scene.fileId = rs.getInt("file_id");
                scene.order = rs.getInt("order_scene");
                scene.name = rs.getString("name");
                scene.description = rs.getString("description");
                scene.ivfxFile = ivfxFiles;

                listScenes.add(scene);
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

        return listScenes;
    }

    public void save() {
        String sql = "UPDATE tbl_scenes SET " +
                "file_id = ?, " +
                "order_scene = ?, " +
                "name = ?, " +
                "description = ? " +
                "WHERE id = ?";
        try {
            PreparedStatement ps = Main.mainConnection.prepareStatement(sql);
            ps.setInt   (1, this.fileId);
            ps.setInt   (2, this.order);
            ps.setString(3, this.name);
            ps.setString(4, this.description);
            ps.setInt   (5, this.id);
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void delete() {

        IVFXScenes ivfxSceneToDelete = this;
        IVFXFiles ivfxFiles = ivfxSceneToDelete.ivfxFile;

        List<IVFXScenes> listScenes = IVFXScenes.loadList(ivfxFiles); // получаем список сцен файла
        for (int i = 0; i < listScenes.size(); i++) {
            if (listScenes.get(i).getId() == ivfxSceneToDelete.getId()) {  // если текущая сцена равна удаляемой
                if (i == 0) {   // если удаляемая сцена - первая в списке
                    if (listScenes.size() == 1) { // первая и единственная

                        // Удаляем все ScenePerson для этой сцены
                        List<IVFXScenesPersons> listScenePersons = IVFXScenesPersons.loadList(ivfxSceneToDelete, false);
                        for (IVFXScenesPersons scenePerson: listScenePersons) {
                            scenePerson.delete();
                        }

                        // Удаляем все SceneShot для этой сцены
                        List<IVFXScenesShots> listSceneShots = IVFXScenesShots.loadList(ivfxSceneToDelete);
                        for (IVFXScenesShots sceneShot: listSceneShots) {
                            sceneShot.delete();
                        }

                        // Удаляем сцену
                        String sql = "DELETE FROM tbl_scenes WHERE id = ?";
                        try {
                            PreparedStatement ps = Main.mainConnection.prepareStatement(sql);
                            ps.setInt   (1, ivfxSceneToDelete.id);
                            ps.executeUpdate();
                            ps.close();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }

                        return;
                    } else {  // первая и не единственная

                        // Планы и персонажи этой сцены надо подклеить к началу следующей сцены, саму сцену удалить и переписать ордеры сцен

                        List<IVFXScenesShots> listRemovedScenesShots = IVFXScenesShots.loadList(ivfxSceneToDelete);
                        for (IVFXScenesShots sceneShot : listRemovedScenesShots) {
                            sceneShot.setIvfxScene(listScenes.get(i+1));
                            sceneShot.setSceneId(listScenes.get(i+1).getId());
                            sceneShot.save();
                        }

                        int order = 0;
                        List<IVFXScenesShots> listAddedScenesShots = IVFXScenesShots.loadList(listScenes.get(i+1));
                        for (IVFXScenesShots sceneShot : listAddedScenesShots) {
                            order++;
                            sceneShot.setOrder(order);
                            sceneShot.save();
                        }

                        // Удаляем сцену
                        String sql = "DELETE FROM tbl_scenes WHERE id = ?";
                        try {
                            PreparedStatement ps = Main.mainConnection.prepareStatement(sql);
                            ps.setInt   (1, ivfxSceneToDelete.id);
                            ps.executeUpdate();
                            ps.close();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }

                        List<IVFXScenesPersons> listRemovedScenesPersons = IVFXScenesPersons.loadList(ivfxSceneToDelete,false);
                        for (IVFXScenesPersons scenesPerson : listRemovedScenesPersons) {
                            IVFXPersons ivfxPerson = scenesPerson.getIvfxPerson();
                            boolean needToAdd = true;
                            for (IVFXScenesPersons scenePerson : IVFXScenesPersons.loadList(listScenes.get(i+1),false)) {
                                if (scenePerson.getPersonId() == ivfxPerson.getId()) {
                                    needToAdd = false;
                                    break;
                                }
                            }
                            if (needToAdd) {
                                IVFXScenesPersons.getNewDbInstance(listScenes.get(i+1), ivfxPerson);
                            }
                            scenesPerson.delete();
                        }

                    }
                } else { // если удаляемая сцена - не первая в списке

                    // Планы и персонажи этой сцены надо подклеить к концу предыдущей сцены, саму сцену удалить и переписать ордеры сцен

                    List<IVFXScenesShots> listRemovedScenesShots = IVFXScenesShots.loadList(ivfxSceneToDelete);
                    List<IVFXScenesShots> listAddedScenesShots = IVFXScenesShots.loadList(listScenes.get(i-1));
                    for (IVFXScenesShots sceneShot : listRemovedScenesShots) {
                        sceneShot.setIvfxScene(listScenes.get(i-1));
                        sceneShot.setSceneId(listScenes.get(i-1).getId());
                        sceneShot.setOrder(sceneShot.getOrder()+listAddedScenesShots.size());
                        sceneShot.save();
                    }

                    // Удаляем сцену
                    String sql = "DELETE FROM tbl_scenes WHERE id = ?";
                    try {
                        PreparedStatement ps = Main.mainConnection.prepareStatement(sql);
                        ps.setInt   (1, ivfxSceneToDelete.id);
                        ps.executeUpdate();
                        ps.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }

                    List<IVFXScenesPersons> listRemovedScenesPersons = IVFXScenesPersons.loadList(ivfxSceneToDelete,false);
                    for (IVFXScenesPersons scenesPerson : listRemovedScenesPersons) {
                        IVFXPersons ivfxPerson = scenesPerson.getIvfxPerson();
                        boolean needToAdd = true;
                        for (IVFXScenesPersons scenePerson : IVFXScenesPersons.loadList(listScenes.get(i-1),false)) {
                            if (scenePerson.getPersonId() == ivfxPerson.getId()) {
                                needToAdd = false;
                                break;
                            }
                        }
                        if (needToAdd) {
                            IVFXScenesPersons.getNewDbInstance(listScenes.get(i+1), ivfxPerson);
                        }
                        scenesPerson.delete();
                    }

                }
                break;
            }
        }
        listScenes = IVFXScenes.loadList(ivfxFiles); // получаем список сцен файла
        int order = 1;
        for (IVFXScenes scene : listScenes) {
            if (scene.order != order) {
                scene.order = order;
                scene.save();
            }
            order++;
        }



    }


// TODO FUNCTIONS


    public String getTitle() {
        return this.ivfxFile.getTitle();
    }

    // возвращает первый план сцены
    public IVFXShots getFirstShot() {

        Statement statement = null;
        ResultSet rs = null;
        String sql;

        try {
            statement = Main.mainConnection.createStatement();

            sql = "SELECT * FROM tbl_scenes_shots WHERE scene_id = " + this.id + " ORDER BY order_scene_shot";
            rs = statement.executeQuery(sql);
            if (rs.next()) {
                return IVFXShots.load(rs.getInt("shot_id"),false);
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

    // возвращает последний план сцены
    public IVFXShots getLastShot() {

        Statement statement = null;
        ResultSet rs = null;
        String sql;

        try {
            statement = Main.mainConnection.createStatement();

            sql = "SELECT * FROM tbl_scenes_shots WHERE scene_id = " + this.id + " ORDER BY order_scene_shot DESC";
            rs = statement.executeQuery(sql);
            if (rs.next()) {
                return IVFXShots.load(rs.getInt("shot_id"),false);
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

    public int getDuration() {
        return FFmpeg.getDurationByFrameNumber(this.getLastShot().getLastFrameNumber() - this.getFirstShot().getFirstFrameNumber()+1, this.ivfxFile.getFrameRate());
    }

    public String getStart() {

        int firstFrame = this.getFirstShot().getFirstFrameNumber();
        return FFmpeg.convertDurationToString(FFmpeg.getDurationByFrameNumber(firstFrame-1,this.ivfxFile.getFrameRate()));

    }

    public String getEnd() {

        int lastFrame = this.getLastShot().getLastFrameNumber();
        return FFmpeg.convertDurationToString(FFmpeg.getDurationByFrameNumber(lastFrame,this.ivfxFile.getFrameRate()));

    }

    public static List<IVFXShots> loadListShots(IVFXScenes ivfxScenes) {
        List<IVFXShots> listShots = new ArrayList<>();
        List<IVFXScenesShots> listSceneShots = IVFXScenesShots.loadList(ivfxScenes);
        for (IVFXScenesShots sceneShot : listSceneShots) {
            listShots.add(sceneShot.getIvfxShot());
        }
        return listShots;
    }

    public static List<IVFXPersons> loadListPersons(IVFXScenes ivfxScenes, boolean withPreview) {
        List<IVFXPersons> listPersons = new ArrayList<>();
        List<IVFXScenesPersons> listScenePersons = IVFXScenesPersons.loadList(ivfxScenes,withPreview);
        for (IVFXScenesPersons scenePerson : listScenePersons) {
            listPersons.add(scenePerson.getIvfxPerson());
        }
        return listPersons;
    }

    // TODO GETTERS SETTERS

    public IVFXFiles getIvfxFile() {
        return ivfxFile;
    }

    public void setIvfxFile(IVFXFiles ivfxFile) {
        this.ivfxFile = ivfxFile;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public int getFileId() {
        return fileId;
    }

    public void setFileId(int fileId) {
        this.fileId = fileId;
    }

    public List<IVFXShots> getShots() {

        return shots;
    }

    public void setShots(List<IVFXShots> shots) {
        this.shots = shots;
    }

    public List<IVFXScenesPersons> getScenePersons() {
        return scenePersons;
    }

    public void setScenePersons(List<IVFXScenesPersons> scenePersons) {
        this.scenePersons = scenePersons;
    }

    public List<IVFXScenesPersons> getScenePersons2() {
        return scenePersons2;
    }

    public void setScenePersons2(List<IVFXScenesPersons> scenePersons2) {
        this.scenePersons2 = scenePersons2;
    }
}

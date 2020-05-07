package com.svoemesto.ivfx.tables;

import com.svoemesto.ivfx.Main;

import java.io.File;
import java.io.Serializable;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class IVFXProjects implements Serializable, Comparable<IVFXProjects> {
    private int id;
    private UUID uuid = UUID.randomUUID();   // UUID
    private int order = 0;
    private String name = "Название нового проекта"; // название проекта, например "Интерактивная Игра Престолов"
    private String shortName = "Короткое имя нового проекта"; // имя файла без расширения, например iGOT
    private String folder = "Папка нового проекта";   // папка проекта, например "D:\\Dropbox\\InteractiveVideoFXProjects\\iGOT"

    // пустой конструктор
    public IVFXProjects() {
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
                    "uuid) " +
                    "VALUES(" +
                    ivfxProject.order + "," +
                    "'" + ivfxProject.name + "'" + "," +
                    "'" + ivfxProject.shortName + "'" + "," +
                    "'" + ivfxProject.folder + "'" + "," +
                    "'" + ivfxProject.uuid.toString() + "'" +
                    ")";

            ps = Main.mainConnection.prepareStatement(sql);
            ps.executeUpdate(sql, Statement.RETURN_GENERATED_KEYS);
            rs = ps.getGeneratedKeys();
            if (rs.next()) {
                ivfxProject.id = rs.getInt(1);
                System.out.println("Создана запись для проекта «" + ivfxProject.name + "» " + ivfxProject.uuid + " с идентификатором " + rs.getInt(1));
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

    public static IVFXProjects loadByUuid(UUID uuid) {

        Statement statement = null;
        ResultSet rs = null;
        String sql;

        try {
            statement = Main.mainConnection.createStatement();

            sql = "SELECT * FROM tbl_projects WHERE uuid = '" + uuid.toString() + "'";
            rs = statement.executeQuery(sql);
            if (rs.next()) {
                IVFXProjects project = new IVFXProjects();
                project.id = rs.getInt("id");
                project.order = rs.getInt("order_project");
                project.uuid = UUID.fromString(rs.getString("uuid"));
                project.name = rs.getString("name");
                project.shortName = rs.getString("short_name");
                project.folder = rs.getString("folder");
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

    public static IVFXProjects loadById(int id) {

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
                project.uuid = UUID.fromString(rs.getString("uuid"));
                project.name = rs.getString("name");
                project.shortName = rs.getString("short_name");
                project.folder = rs.getString("folder");
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

        List<IVFXProjects> listProjects = new ArrayList<>();

        Statement statement = null;
        ResultSet rs = null;
        String sql;

        try {
            statement = Main.mainConnection.createStatement();

            sql = "SELECT * FROM tbl_projects ORDER BY order_project";
            rs = statement.executeQuery(sql);
            while (rs.next()) {
                IVFXProjects project = new IVFXProjects();
                project.id = rs.getInt("id");
                project.order = rs.getInt("order_project");
                project.uuid = UUID.fromString(rs.getString("uuid"));
                project.name = rs.getString("name");
                project.shortName = rs.getString("short_name");
                project.folder = rs.getString("folder");
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

        String sql = "UPDATE tbl_projects SET order_project = ?, name = ?, short_name = ?, folder = ? WHERE id = ?";

        try {
            PreparedStatement ps = Main.mainConnection.prepareStatement(sql);
            ps.setInt   (1, this.order);
            ps.setString(2, this.name);
            ps.setString(3, this.shortName);
            ps.setString(4, this.folder);
            ps.setInt   (5, this.id);
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

    public String getSegmentsFolder() {
        String folder = this.folder + "\\Segments";
        File file = new File(folder + "\\");
        if(!file.exists()) file.mkdir();
        return folder;
    }


    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
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

}

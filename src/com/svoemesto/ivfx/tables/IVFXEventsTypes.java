package com.svoemesto.ivfx.tables;

import com.svoemesto.ivfx.Main;
import javafx.scene.control.ProgressBar;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class IVFXEventsTypes {

    private int id;
    private int projectId;
    private IVFXProjects ivfxProject;
    private int order = 0;
    private String name = "New EventType";

//TODO ISEQUAL

    public boolean isEqual(IVFXEventsTypes o) {
        return (this.id == o.id &&
                this.projectId == o.projectId &&
                this.name.equals(o.name) &&
                this.order == o.order);
    }



// TODO КОНСТРУКТОРЫ

    // пустой конструктор
    public IVFXEventsTypes() {
    }

    public static IVFXEventsTypes getNewDbInstance(IVFXProjects ivfxProject) {

        IVFXEventsTypes ivfxEventsTypes = new IVFXEventsTypes();

        Statement statement = null;
        ResultSet rs = null;
        String sql;

        ivfxEventsTypes.projectId = ivfxProject.getId();
        ivfxEventsTypes.ivfxProject = ivfxProject;

        try {
            statement = Main.mainConnection.createStatement();

            sql = "SELECT COUNT(*) FROM tbl_events_types WHERE project_id = " + ivfxEventsTypes.projectId;
            rs = statement.executeQuery(sql);
            if (rs.next()) {
                ivfxEventsTypes.order = rs.getInt(1) + 1;  // индексация столбцов начинается с единицы
            }

            sql = "INSERT INTO tbl_events_types (" +
                    "project_id, " +
                    "order_event_type, " +
                    "name) " +
                    "VALUES(" +
                    ivfxEventsTypes.projectId + "," +
                    ivfxEventsTypes.order + "," +
                    "'" + ivfxEventsTypes.name + "'" + ")";

            PreparedStatement ps = Main.mainConnection.prepareStatement(sql);
            ps.executeUpdate(sql, Statement.RETURN_GENERATED_KEYS);
            rs = ps.getGeneratedKeys();
            if (rs.next()) {
                ivfxEventsTypes.id = rs.getInt(1);
                System.out.println("Создана запись для типа события «" + ivfxEventsTypes.name + "», проект «" + ivfxEventsTypes.ivfxProject.getName() + "» с идентификатором " + rs.getInt(1));
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

        return ivfxEventsTypes;
    }


    public static IVFXEventsTypes load(int id) {
        Statement statement = null;
        ResultSet rs = null;
        String sql;

        try {
            statement = Main.mainConnection.createStatement();

            sql = "SELECT * FROM tbl_events_types WHERE id = " + id;
            rs = statement.executeQuery(sql);
            if (rs.next()) {
                IVFXEventsTypes eventType = new IVFXEventsTypes();
                eventType.id = rs.getInt("id");
                eventType.projectId = rs.getInt("project_id");
                eventType.order = rs.getInt("order_event_type");
                eventType.name = rs.getString("name");
                eventType.ivfxProject = IVFXProjects.load(eventType.projectId);
                return eventType;
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

    public static IVFXEventsTypes loadByName(String eventTypeName, IVFXProjects ivfxProject) {
        Statement statement = null;
        ResultSet rs = null;
        String sql;

        try {
            statement = Main.mainConnection.createStatement();

            sql = "SELECT * FROM tbl_events_types WHERE project_id = " + ivfxProject.getId() + " AND name = '" + eventTypeName.toString() + "' ORDER BY order_event_type";
            rs = statement.executeQuery(sql);
            if (rs.next()) {
                IVFXEventsTypes eventType = new IVFXEventsTypes();
                eventType.id = rs.getInt("id");
                eventType.projectId = rs.getInt("project_id");
                eventType.order = rs.getInt("order_event_type");
                eventType.name = rs.getString("name");
                eventType.ivfxProject = IVFXProjects.load(eventType.projectId);
                return eventType;
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

    public static List<IVFXEventsTypes> loadList(IVFXProjects ivfxProject) {
        return loadList(ivfxProject, null);
    }
    public static List<IVFXEventsTypes> loadList(IVFXProjects ivfxProject, ProgressBar progressBar) {
        List<IVFXEventsTypes> listEventsTypes = new ArrayList<>();

        int iProgress = 0;
        Statement statement = null;
        ResultSet rs = null;
        String sql;

        try {
            statement = Main.mainConnection.createStatement();

            sql = "SELECT * FROM tbl_events_types WHERE project_id = " + ivfxProject.getId() + " ORDER BY order_event_type";

            String sqlCnt = "SELECT COUNT(*) AS CNT FROM (" + sql + ") AS tmp";
            ResultSet rsCnt = null;
            rsCnt = statement.executeQuery(sqlCnt);
            rsCnt.next();
            int countRows = rsCnt.getInt("CNT");
            rsCnt.close();

            rs = statement.executeQuery(sql);
            while (rs.next()) {

                if (progressBar != null) progressBar.setProgress((double)++iProgress / countRows);

                IVFXEventsTypes eventType = new IVFXEventsTypes();
                eventType.id = rs.getInt("id");
                eventType.projectId = rs.getInt("project_id");
                eventType.order = rs.getInt("order_event_type");
                eventType.name = rs.getString("name");
                eventType.ivfxProject = IVFXProjects.load(eventType.projectId);
                listEventsTypes.add(eventType);
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

        return listEventsTypes;
    }

    public void save() {
        String sql = "UPDATE tbl_events_types SET " +
                "project_id = ?, " +
                "order_event_type = ?, " +
                "name = ? " +
                "WHERE id = ?";
        try {
            PreparedStatement ps = Main.mainConnection.prepareStatement(sql);
            ps.setInt   (1, this.projectId);
            ps.setInt   (2, this.order);
            ps.setString(3, this.name);
            ps.setInt   (4, this.id);
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void delete() {
        String sql = "DELETE FROM tbl_events_types WHERE id = ?";
        try {
            PreparedStatement ps = Main.mainConnection.prepareStatement(sql);
            ps.setInt   (1, this.id);
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public static List<String> loadListNames(IVFXProjects ivfxProject) {

        List<String> listNames = new ArrayList<>();
        for (IVFXEventsTypes eventType : loadList(ivfxProject)) {
            listNames.add(eventType.name);
        }

        return listNames;

    }

    // TODO GETTERS SETTERS


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public int getProjectId() {
        return projectId;
    }

    public void setProjectId(int projectId) {
        this.projectId = projectId;
    }

    public IVFXProjects getIvfxProject() {
        return ivfxProject;
    }

    public void setIvfxProject(IVFXProjects ivfxProject) {
        this.ivfxProject = ivfxProject;
    }

}

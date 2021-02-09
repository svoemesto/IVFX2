package com.svoemesto.ivfx.tables;

import com.svoemesto.ivfx.Main;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class IVFXFilters {

    private int id;
    private int projectId;
    private int order = 0;
    private IVFXProjects ivfxProject;       // родительский проект
    private String name = "New Filter";

    //TODO ISEQUAL

    public boolean isEqual(IVFXFilters o) {
        return (this.id == o.id &&
                this.projectId == o.projectId &&
                this.name.equals(o.name));
    }

    // пустой конструктор
    public IVFXFilters() {
    }

    public static IVFXFilters getNewDbInstance(IVFXProjects ivfxProject) {

        IVFXFilters ivfxFilter = new IVFXFilters();

        ivfxFilter.id = 0;
        ivfxFilter.ivfxProject = ivfxProject;
        ivfxFilter.projectId = ivfxProject.getId();
        ivfxFilter.name = "New filter";

        Statement statement = null;
        ResultSet rs = null;
        String sql;

        try {
            statement = Main.mainConnection.createStatement();

            sql = "SELECT COUNT(*) FROM tbl_filters WHERE project_id = " + ivfxProject.getId();
            rs = statement.executeQuery(sql);
            if (rs.next()) {
                ivfxFilter.order = rs.getInt(1) + 1;  // индексация столбцов начинается с единицы
            }
            sql = "INSERT INTO tbl_filters (" +
                    "project_id, " +
                    "name) " +
                    "VALUES(" +
                    ivfxProject.getId() + "," +
                    "'" + ivfxProject.getName() + "'" + ")";

            PreparedStatement ps = Main.mainConnection.prepareStatement(sql);
            ps.executeUpdate(sql, Statement.RETURN_GENERATED_KEYS);
            rs = ps.getGeneratedKeys();
            if (rs.next()) {
                ivfxFilter.id = rs.getInt(1);
                System.out.println("Создана запись для фильтра «" + ivfxFilter.name + "» с идентификатором " + rs.getInt(1));
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

        return ivfxFilter;

    }

// TODO LOAD


    public static IVFXFilters load(int id) {
        Statement statement = null;
        ResultSet rs = null;
        String sql;

        try {
            statement = Main.mainConnection.createStatement();

            sql = "SELECT * FROM tbl_filters WHERE id = " + id;
            rs = statement.executeQuery(sql);
            if (rs.next()) {
                IVFXFilters filter = new IVFXFilters();
                filter.id = rs.getInt("id");
                filter.projectId = rs.getInt("project_id");
                filter.name = rs.getString("name");
                filter.ivfxProject = IVFXProjects.load(filter.projectId);
                return filter;
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

// TODO LOAD LIST

    public static List<IVFXFilters> loadListWithoutDefault(IVFXProjects ivfxProject) {
        List<IVFXFilters> listFilters = new ArrayList<>();

        for (IVFXFilters ivfxFilter: loadList(ivfxProject)) {
            if (!ivfxFilter.getName().equals("DEFAULT FILTER")) {
                listFilters.add(ivfxFilter);
            }
        }
        return listFilters;
    }

    public static List<IVFXFilters> loadList(IVFXProjects ivfxProject) {
        List<IVFXFilters> listFilters = new ArrayList<>();

        Statement statement = null;
        ResultSet rs = null;
        String sql;

        try {
            statement = Main.mainConnection.createStatement();

            sql = "SELECT * FROM tbl_filters WHERE project_id = " + ivfxProject.getId();
            rs = statement.executeQuery(sql);
            while (rs.next()) {
                IVFXFilters filter = new IVFXFilters();
                filter.id = rs.getInt("id");
                filter.projectId = rs.getInt("project_id");
                filter.name = rs.getString("name");
                filter.ivfxProject = IVFXProjects.load(filter.projectId);
                listFilters.add(filter);
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

        return listFilters;
    }

// TODO SAVE

    public void save() {
        String sql = "UPDATE tbl_filters SET " +
                "project_id = ?, " +
                "name = ? " +
                "WHERE id = ?";
        try {
            PreparedStatement ps = Main.mainConnection.prepareStatement(sql);
            ps.setInt   (1, this.projectId);
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
        String sql = "DELETE FROM tbl_filters WHERE id = ?";
        try {
            PreparedStatement ps = Main.mainConnection.prepareStatement(sql);
            ps.setInt   (1, this.id);
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



    public static IVFXFilters getDefaultFilter(IVFXProjects ivfxProject) {

        List<IVFXFilters> listFilters = new ArrayList<>();

        IVFXFilters ivfxFilter = null;

        Statement statement = null;
        ResultSet rs = null;
        String sql;

        try {
            statement = Main.mainConnection.createStatement();

            sql = "SELECT * FROM tbl_filters WHERE tbl_filters.name = 'DEFAULT FILTER' AND project_id = " + ivfxProject.getId();
            rs = statement.executeQuery(sql);
            while (rs.next()) {
                IVFXFilters filter = new IVFXFilters();
                filter.id = rs.getInt("id");
                filter.projectId = rs.getInt("project_id");
                filter.name = rs.getString("name");
                filter.ivfxProject = IVFXProjects.load(filter.projectId);
                listFilters.add(filter);
            }

            if (listFilters.size() == 0) {

                ivfxFilter = getNewDbInstance(ivfxProject);
                ivfxFilter.name = "DEFAULT FILTER";
                ivfxFilter.save();

            } else {
                ivfxFilter = listFilters.get(0);
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

        return ivfxFilter;

    }





// TODO GETTERS SETTERS


    public IVFXProjects getIvfxProject() {
        return ivfxProject;
    }

    public void setIvfxProject(IVFXProjects ivfxProject) {
        this.ivfxProject = ivfxProject;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

}

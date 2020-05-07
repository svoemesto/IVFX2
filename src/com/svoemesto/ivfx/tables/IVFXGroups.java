package com.svoemesto.ivfx.tables;

import com.svoemesto.ivfx.Main;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class IVFXGroups {
    public static transient final String GROUP_SUFFIX = "_Groups";
    public static transient final String GROUP_PREFIX = "group_";

    private int id;
    private int projectId;
    private int order;
    private UUID uuid = UUID.randomUUID();;
    private UUID projectUuid;
    private IVFXProjects ivfxProject;
    private String name;
    private String description = "";

//TODO ISEQUAL

    public boolean isEqual(IVFXGroups o) {
        return (this.id == o.id &&
                this.projectId == o.projectId &&
                this.order == o.order &&
                this.uuid.equals(o.uuid) &&
                this.projectUuid.equals(o.projectUuid) &&
                this.name.equals(o.name) &&
                this.description.equals(o.description));
    }

    // TODO КОНСТРУКТОРЫ
    // пустой конструктор
    public IVFXGroups(){

    }

    public static IVFXGroups getNewDbInstance(IVFXProjects ivfxProject) {
        IVFXGroups ivfxGroup = new IVFXGroups();
        Statement statement = null;
        ResultSet rs = null;
        String sql;

        try {
            statement = Main.mainConnection.createStatement();

            sql = "SELECT COUNT(*) FROM tbl_groups WHERE project_id = " + ivfxProject.getId();
            rs = statement.executeQuery(sql);
            if (rs.next()) {
                ivfxGroup.order = rs.getInt(1) + 1;  // индексация столбцов начинается с единицы
            }

            sql = "INSERT INTO tbl_groups (" +
                    "project_id, " +
                    "order_group, " +
                    "name, " +
                    "description, " +
                    "uuid, " +
                    "project_uuid) " +
                    "VALUES(" +
                    ivfxProject.getId() + "," +
                    ivfxGroup.order + "," +
                    "'" + ivfxGroup.name + "'" + "," +
                    "'" + ivfxGroup.description + "'" + "," +
                    "'" + ivfxGroup.uuid.toString() + "'" + "," +
                    "'" + ivfxProject.getUuid().toString() + "'" +
                    ")";

            PreparedStatement ps = Main.mainConnection.prepareStatement(sql);
            ps.executeUpdate(sql, Statement.RETURN_GENERATED_KEYS);
            rs = ps.getGeneratedKeys();
            if (rs.next()) {
                ivfxGroup.id = rs.getInt(1);
                System.out.println("Создана запись для группы «" + ivfxGroup.name + "» " + ivfxGroup.uuid + " с идентификатором " + rs.getInt(1));
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

        return ivfxGroup;
    }

    public static IVFXGroups loadByUuid(UUID groupUUID) {
        Statement statement = null;
        ResultSet rs = null;
        String sql;

        try {
            statement = Main.mainConnection.createStatement();

            sql = "SELECT * FROM tbl_groups WHERE uuid = '" + groupUUID.toString() + "'";
            rs = statement.executeQuery(sql);
            if (rs.next()) {
                IVFXGroups group = new IVFXGroups();
                group.id = rs.getInt("id");
                group.projectId = rs.getInt("project_id");
                group.order = rs.getInt("order_group");
                group.name = rs.getString("name");
                group.description = rs.getString("description");
                group.uuid = UUID.fromString(rs.getString("uuid"));
                group.projectUuid = UUID.fromString(rs.getString("project_uuid"));
                group.ivfxProject = IVFXProjects.loadById(group.projectId);

                return group;
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

    public static IVFXGroups loadById(int id) {
        Statement statement = null;
        ResultSet rs = null;
        String sql;

        try {
            statement = Main.mainConnection.createStatement();

            sql = "SELECT * FROM tbl_groups WHERE id = " + id;
            rs = statement.executeQuery(sql);
            if (rs.next()) {
                IVFXGroups group = new IVFXGroups();
                group.id = rs.getInt("id");
                group.projectId = rs.getInt("project_id");
                group.order = rs.getInt("order_group");
                group.name = rs.getString("name");
                group.description = rs.getString("description");
                group.uuid = UUID.fromString(rs.getString("uuid"));
                group.projectUuid = UUID.fromString(rs.getString("project_uuid"));
                group.ivfxProject = IVFXProjects.loadByUuid(group.projectUuid);

                return group;
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

    public static IVFXGroups loadByName(String groupName, IVFXProjects ivfxProject) {
        Statement statement = null;
        ResultSet rs = null;
        String sql;

        try {
            statement = Main.mainConnection.createStatement();

            sql = "SELECT * FROM tbl_groups WHERE project_id = " + ivfxProject.getId() + " AND name = '" + groupName + "'";
            rs = statement.executeQuery(sql);
            if (rs.next()) {
                IVFXGroups group = new IVFXGroups();
                group.id = rs.getInt("id");
                group.projectId = rs.getInt("project_id");
                group.order = rs.getInt("order_group");
                group.name = rs.getString("name");
                group.description = rs.getString("description");
                group.uuid = UUID.fromString(rs.getString("uuid"));
                group.projectUuid = UUID.fromString(rs.getString("project_uuid"));
                group.ivfxProject = IVFXProjects.loadById(group.projectId);

                return group;
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

    public static List<IVFXGroups> loadList(IVFXProjects ivfxProjects) {
        List<IVFXGroups> listGroups = new ArrayList<>();

        Statement statement = null;
        ResultSet rs = null;
        String sql;

        try {
            statement = Main.mainConnection.createStatement();

            sql = "SELECT * FROM tbl_groups WHERE project_id = " + ivfxProjects.getId() + " ORDER BY order_group";
            rs = statement.executeQuery(sql);
            while (rs.next()) {
                IVFXGroups group = new IVFXGroups();
                group.id = rs.getInt("id");
                group.order = rs.getInt("order_group");
                group.projectId = rs.getInt("project_id");
                group.uuid = UUID.fromString(rs.getString("uuid"));
                group.projectUuid = UUID.fromString(rs.getString("project_uuid"));
                group.name = rs.getString("name");
                group.description = rs.getString("description");
                listGroups.add(group);
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

        return listGroups;
    }

    public static List<IVFXGroups> loadList(IVFXPersons ivfxPerson) {
        List<IVFXGroups> listGroups = new ArrayList<>();

        Statement statement = null;
        ResultSet rs = null;
        String sql;

        try {
            statement = Main.mainConnection.createStatement();

            sql = "select tbl_groups.* from tbl_groups join tbl_groups_persons tgp on tbl_groups.id = tgp.group_id " +
                    "where person_id = " + ivfxPerson.getId() + " ORDER BY order_group";

            rs = statement.executeQuery(sql);
            while (rs.next()) {
                IVFXGroups group = new IVFXGroups();
                group.id = rs.getInt("id");
                group.order = rs.getInt("order_group");
                group.projectId = rs.getInt("project_id");
                group.uuid = UUID.fromString(rs.getString("uuid"));
                group.projectUuid = UUID.fromString(rs.getString("project_uuid"));
                group.name = rs.getString("name");
                group.description = rs.getString("description");
                listGroups.add(group);
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

        return listGroups;
    }

    public void save() {
        String sql = "UPDATE tbl_groups SET order_group = ?, name = ?, description = ? WHERE id = ?";
        try {
            PreparedStatement ps = Main.mainConnection.prepareStatement(sql);
            ps.setInt   (1, this.order);
            ps.setString(2, this.name);
            ps.setString(3, this.description);
            ps.setInt(4, this.id);
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void delete() {
        String sql = "DELETE FROM tbl_groups WHERE id = ?";
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

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public UUID getProjectUuid() {
        return projectUuid;
    }

    public void setProjectUuid(UUID projectUuid) {
        this.projectUuid = projectUuid;
    }

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

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

}

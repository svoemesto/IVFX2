package com.svoemesto.ivfx.tables;

import com.svoemesto.ivfx.Main;
import com.svoemesto.ivfx.utils.ConvertToFxImage;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class IVFXGroupsPersons {

    private int id;
    private int groupId;
    private int personId;
    private UUID uuid = UUID.randomUUID();
    private UUID groupUuid;
    private UUID personUuid;
    private IVFXGroups ivfxGroup;
    private IVFXPersons ivfxPerson;
    private transient ImageView preview;
    private transient Label label;

//TODO ISEQUAL

    public boolean isEqual(IVFXGroupsPersons o) {
        return (this.id == o.id &&
                this.groupId == o.groupId &&
                this.personId == o.personId &&
                this.uuid.equals(o.uuid) &&
                this.groupUuid.equals(o.groupUuid) &&
                this.personUuid.equals(o.personUuid));
    }

// TODO КОНСТРУКТОРЫ

    // пустой конструктор
    public IVFXGroupsPersons() {
    }

    public static IVFXGroupsPersons getNewDbInstance(IVFXGroups ivfxGroup, IVFXPersons ivfxPerson, boolean withPreview) {
        IVFXGroupsPersons ivfxGroupPerson = IVFXGroupsPersons.loadByGroupAndPerson(ivfxGroup,ivfxPerson,withPreview);
        if (ivfxGroupPerson == null) {
            ivfxGroupPerson = new IVFXGroupsPersons();

            ivfxGroupPerson.ivfxGroup = ivfxGroup;
            ivfxGroupPerson.ivfxPerson = ivfxPerson;
            if (withPreview) {
                ivfxGroupPerson.label = ivfxPerson.getLabel();
                ivfxGroupPerson.preview = ivfxPerson.getPreview();
            }

            Statement statement = null;
            ResultSet rs = null;
            String sql;

            try {

                sql = "INSERT INTO tbl_groups_persons (" +
                        "group_id, " +
                        "person_id, " +
                        "uuid, " +
                        "group_uuid, " +
                        "person_uuid) " +
                        "VALUES(" +
                        ivfxGroup.getId() + "," +
                        ivfxPerson.getId() + "," +
                        "'" + ivfxGroupPerson.uuid.toString() + "'" + "," +
                        "'" + ivfxGroup.getUuid().toString() + "'" + "," +
                        "'" + ivfxPerson.getUuid().toString() + "'" +
                        ")";

                PreparedStatement ps = Main.mainConnection.prepareStatement(sql);
                ps.executeUpdate(sql, Statement.RETURN_GENERATED_KEYS);
                rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    ivfxGroupPerson.id = rs.getInt(1);
                    System.out.println("Создана запись для персонажа «" + ivfxGroupPerson.ivfxPerson.getName() + "» группы «" + ivfxGroupPerson.ivfxGroup.getName() + "» " + ivfxGroupPerson.uuid + " с идентификатором " + rs.getInt(1));
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
        return  ivfxGroupPerson;
    }

    public static IVFXGroupsPersons loadByUuid(UUID groupPersonUuid, boolean withPreview) {
        Statement statement = null;
        ResultSet rs = null;
        String sql;

        try {
            statement = Main.mainConnection.createStatement();

            sql = "SELECT * FROM tbl_groups_persons WHERE uuid = '" + groupPersonUuid.toString() + "'";
            rs = statement.executeQuery(sql);
            if (rs.next()) {
                IVFXGroupsPersons groupPerson = new IVFXGroupsPersons();
                groupPerson.id = rs.getInt("id");
                groupPerson.groupId = rs.getInt("group_id");
                groupPerson.personId = rs.getInt("person_id");
                groupPerson.uuid = UUID.fromString(rs.getString("uuid"));
                groupPerson.groupUuid = UUID.fromString(rs.getString("group_uuid"));
                groupPerson.personUuid = UUID.fromString(rs.getString("person_uuid"));
                groupPerson.ivfxGroup = IVFXGroups.loadById(groupPerson.groupId);
                groupPerson.ivfxPerson = IVFXPersons.loadById(groupPerson.personId, withPreview);

                if (withPreview) {
                    groupPerson.label = groupPerson.ivfxPerson.getLabel();
                    groupPerson.preview = groupPerson.ivfxPerson.getPreview();
                }
                return groupPerson;
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

    public static IVFXGroupsPersons loadById(int id, boolean withPreview) {
        Statement statement = null;
        ResultSet rs = null;
        String sql;

        try {
            statement = Main.mainConnection.createStatement();

            sql = "SELECT * FROM tbl_groups_persons WHERE id = " + id;
            rs = statement.executeQuery(sql);
            if (rs.next()) {
                IVFXGroupsPersons groupPerson = new IVFXGroupsPersons();
                groupPerson.id = rs.getInt("id");
                groupPerson.groupId = rs.getInt("group_id");
                groupPerson.personId = rs.getInt("person_id");
                groupPerson.uuid = UUID.fromString(rs.getString("uuid"));
                groupPerson.groupUuid = UUID.fromString(rs.getString("group_uuid"));
                groupPerson.personUuid = UUID.fromString(rs.getString("person_uuid"));
                groupPerson.ivfxGroup = IVFXGroups.loadByUuid(groupPerson.groupUuid);
                groupPerson.ivfxPerson = IVFXPersons.loadByUuid(groupPerson.personUuid, withPreview);

                if (withPreview) {
                    groupPerson.label = groupPerson.ivfxPerson.getLabel();
                    groupPerson.preview = groupPerson.ivfxPerson.getPreview();
                }

                return groupPerson;
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

    public static IVFXGroupsPersons loadByGroupAndPerson(IVFXGroups ivfxGroup, IVFXPersons ivfxPersons, boolean withPreview) {
        Statement statement = null;
        ResultSet rs = null;
        String sql;

        try {
            statement = Main.mainConnection.createStatement();

            sql = "SELECT * FROM tbl_groups_persons WHERE group_id = " + ivfxGroup.getId()+  " AND person_id = " + ivfxPersons.getId();
            rs = statement.executeQuery(sql);
            if (rs.next()) {
                IVFXGroupsPersons groupPerson = new IVFXGroupsPersons();
                groupPerson.id = rs.getInt("id");
                groupPerson.groupId = rs.getInt("group_id");
                groupPerson.personId = rs.getInt("person_id");
                groupPerson.uuid = UUID.fromString(rs.getString("uuid"));
                groupPerson.groupUuid = UUID.fromString(rs.getString("group_uuid"));
                groupPerson.personUuid = UUID.fromString(rs.getString("person_uuid"));
                groupPerson.ivfxGroup = IVFXGroups.loadByUuid(groupPerson.groupUuid);
                groupPerson.ivfxPerson = IVFXPersons.loadByUuid(groupPerson.personUuid, withPreview);

                if (withPreview) {
                    groupPerson.label = groupPerson.ivfxPerson.getLabel();
                    groupPerson.preview = groupPerson.ivfxPerson.getPreview();
                }

                return groupPerson;
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

    public static List<IVFXGroupsPersons> loadList(IVFXGroups ivfxGroup, boolean withPreview) {
        List<IVFXGroupsPersons> listGroupsPersons = new ArrayList<>();

        Statement statement = null;
        ResultSet rs = null;
        String sql;

        try {
            statement = Main.mainConnection.createStatement();

            sql = "SELECT * FROM tbl_groups_persons WHERE group_id = " + ivfxGroup.getId();
            rs = statement.executeQuery(sql);
            while (rs.next()) {
                IVFXGroupsPersons groupPerson = new IVFXGroupsPersons();
                groupPerson.id = rs.getInt("id");
                groupPerson.groupId = rs.getInt("group_id");
                groupPerson.personId = rs.getInt("person_id");
                groupPerson.uuid = UUID.fromString(rs.getString("uuid"));
                groupPerson.groupUuid = UUID.fromString(rs.getString("group_uuid"));
                groupPerson.personUuid = UUID.fromString(rs.getString("person_uuid"));
                groupPerson.ivfxGroup = IVFXGroups.loadById(groupPerson.groupId);
                groupPerson.ivfxPerson = IVFXPersons.loadById(groupPerson.personId,withPreview);

                if (withPreview) {
                    groupPerson.label = groupPerson.ivfxPerson.getLabel();
                    groupPerson.preview = groupPerson.ivfxPerson.getPreview();
                }

                listGroupsPersons.add(groupPerson);
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

        return listGroupsPersons;
    }

    public static List<IVFXGroupsPersons> loadList(IVFXPersons ivfxPerson, boolean withPreview) {
        List<IVFXGroupsPersons> listGroupsPersons = new ArrayList<>();

        Statement statement = null;
        ResultSet rs = null;
        String sql;

        try {
            statement = Main.mainConnection.createStatement();

            sql = "SELECT * FROM tbl_groups_persons WHERE person_id = " + ivfxPerson.getId();
            rs = statement.executeQuery(sql);
            while (rs.next()) {
                IVFXGroupsPersons groupPerson = new IVFXGroupsPersons();
                groupPerson.id = rs.getInt("id");
                groupPerson.groupId = rs.getInt("group_id");
                groupPerson.personId = rs.getInt("person_id");
                groupPerson.uuid = UUID.fromString(rs.getString("uuid"));
                groupPerson.groupUuid = UUID.fromString(rs.getString("group_uuid"));
                groupPerson.personUuid = UUID.fromString(rs.getString("person_uuid"));
                groupPerson.ivfxGroup = IVFXGroups.loadById(groupPerson.groupId);
                groupPerson.ivfxPerson = IVFXPersons.loadById(groupPerson.personId,withPreview);

                if (withPreview) {
                    groupPerson.label = groupPerson.ivfxPerson.getLabel();
                    groupPerson.preview = groupPerson.ivfxPerson.getPreview();
                }

                listGroupsPersons.add(groupPerson);
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

        return listGroupsPersons;
    }



    // TODO SAVE


    public void save() {
        String sql = "UPDATE tbl_groups_persons SET group_id = ?, person_id = ?, group_uuid = ?, person_uuid = ? WHERE id = ?";
        try {
            PreparedStatement ps = Main.mainConnection.prepareStatement(sql);
            ps.setInt   (1, this.groupId);
            ps.setInt   (2, this.personId);
            ps.setString(3, this.groupUuid.toString());
            ps.setString(4, this.personUuid.toString());
            ps.setInt(5, this.id);
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();

        }
    }

    // TODO DELETE


    public void delete() {
        String sql = "DELETE FROM tbl_groups_persons WHERE id = ?";
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

    public UUID getGroupUuid() {
        return groupUuid;
    }

    public void setGroupUuid(UUID groupUuid) {
        this.groupUuid = groupUuid;
    }

    public UUID getPersonUuid() {
        return personUuid;
    }

    public void setPersonUuid(UUID personUuid) {
        this.personUuid = personUuid;
    }

    public IVFXGroups getIvfxGroup() {
        return ivfxGroup;
    }

    public void setIvfxGroup(IVFXGroups ivfxGroup) {
        this.ivfxGroup = ivfxGroup;
    }

    public IVFXPersons getIvfxPerson() {
        return ivfxPerson;
    }

    public void setIvfxPerson(IVFXPersons ivfxPerson) {
        this.ivfxPerson = ivfxPerson;
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

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public int getPersonId() {
        return personId;
    }

    public String getPersonName() {
        return ivfxPerson.getName();
    }

    public String getGroupName() {
        return ivfxGroup.getName();
    }

    public void setPersonId(int personId) {
        this.personId = personId;
    }

    public ImageView getPreview() {
        return preview;
    }

    public void setPreview(ImageView preview) {
        this.preview = preview;
    }

}

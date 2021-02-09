package com.svoemesto.ivfx.tables;

import com.svoemesto.ivfx.Main;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.ImageView;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class IVFXGroupsPersons {

    private int id;
    private int groupId;
    private int personId;
    private IVFXGroups ivfxGroup;
    private IVFXPersons ivfxPerson;
    private ImageView[] preview = new ImageView[8];
    private Label[] label = new Label[8];

//TODO ISEQUAL

    public boolean isEqual(IVFXGroupsPersons o) {
        return (this.id == o.id &&
                this.groupId == o.groupId &&
                this.personId == o.personId);
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
                        "person_id) " +
                        "VALUES(" +
                        ivfxGroup.getId() + "," +
                        ivfxPerson.getId() + ")";

                PreparedStatement ps = Main.mainConnection.prepareStatement(sql);
                ps.executeUpdate(sql, Statement.RETURN_GENERATED_KEYS);
                rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    ivfxGroupPerson.id = rs.getInt(1);
                    System.out.println("Создана запись для персонажа «" + ivfxGroupPerson.ivfxPerson.getName() + "» группы «" + ivfxGroupPerson.ivfxGroup.getName() + "» с идентификатором " + rs.getInt(1));
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


    public static IVFXGroupsPersons load(int id, boolean withPreview) {
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
                groupPerson.ivfxGroup = IVFXGroups.load(groupPerson.groupId);
                groupPerson.ivfxPerson = IVFXPersons.load(groupPerson.personId, withPreview);

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
                groupPerson.ivfxGroup = IVFXGroups.load(groupPerson.groupId);
                groupPerson.ivfxPerson = IVFXPersons.load(groupPerson.personId, withPreview);

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
        return loadList(ivfxGroup, withPreview, null);
    }
    public static List<IVFXGroupsPersons> loadList(IVFXGroups ivfxGroup, boolean withPreview, ProgressBar progressBar) {
        List<IVFXGroupsPersons> listGroupsPersons = new ArrayList<>();

        int iProgress = 0;
        Statement statement = null;
        ResultSet rs = null;
        String sql;

        try {
            statement = Main.mainConnection.createStatement();

            sql = "select tbl_groups_persons.* from tbl_groups_persons join tbl_persons on tbl_groups_persons.person_id = tbl_persons.id WHERE group_id = " + ivfxGroup.getId() + " order by tbl_persons.name";

            String sqlCnt = "SELECT COUNT(*) AS CNT FROM (" + sql + ") AS tmp";
            ResultSet rsCnt = null;
            rsCnt = statement.executeQuery(sqlCnt);
            rsCnt.next();
            int countRows = rsCnt.getInt("CNT");
            rsCnt.close();

            rs = statement.executeQuery(sql);
            while (rs.next()) {

                if (progressBar != null) progressBar.setProgress((double)++iProgress / countRows);

                IVFXGroupsPersons groupPerson = new IVFXGroupsPersons();
                groupPerson.id = rs.getInt("id");
                groupPerson.groupId = rs.getInt("group_id");
                groupPerson.personId = rs.getInt("person_id");
                groupPerson.ivfxGroup = IVFXGroups.load(groupPerson.groupId);
                groupPerson.ivfxPerson = IVFXPersons.load(groupPerson.personId,withPreview);

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
        return loadList(ivfxPerson, withPreview, null);
    }
    public static List<IVFXGroupsPersons> loadList(IVFXPersons ivfxPerson, boolean withPreview, ProgressBar progressBar) {
        List<IVFXGroupsPersons> listGroupsPersons = new ArrayList<>();

        int iProgress = 0;
        Statement statement = null;
        ResultSet rs = null;
        String sql;

        try {
            statement = Main.mainConnection.createStatement();

            sql = "SELECT * FROM tbl_groups_persons WHERE person_id = " + ivfxPerson.getId();

            String sqlCnt = "SELECT COUNT(*) AS CNT FROM (" + sql + ") AS tmp";
            ResultSet rsCnt = null;
            rsCnt = statement.executeQuery(sqlCnt);
            rsCnt.next();
            int countRows = rsCnt.getInt("CNT");
            rsCnt.close();

            rs = statement.executeQuery(sql);
            while (rs.next()) {

                if (progressBar != null) progressBar.setProgress((double)++iProgress / countRows);

                IVFXGroupsPersons groupPerson = new IVFXGroupsPersons();
                groupPerson.id = rs.getInt("id");
                groupPerson.groupId = rs.getInt("group_id");
                groupPerson.personId = rs.getInt("person_id");
                groupPerson.ivfxGroup = IVFXGroups.load(groupPerson.groupId);
                groupPerson.ivfxPerson = IVFXPersons.load(groupPerson.personId,withPreview);

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
        String sql = "UPDATE tbl_groups_persons SET group_id = ?, person_id = ? WHERE id = ?";
        try {
            PreparedStatement ps = Main.mainConnection.prepareStatement(sql);
            ps.setInt   (1, this.groupId);
            ps.setInt   (2, this.personId);
            ps.setInt(3, this.id);
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

}

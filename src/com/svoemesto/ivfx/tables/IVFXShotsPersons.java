package com.svoemesto.ivfx.tables;

import com.svoemesto.ivfx.Main;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.ImageView;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class IVFXShotsPersons {

    private int id;
    private int shotId;
    private int personId;
    private IVFXShots ivfxShot;
    private IVFXPersons ivfxPerson;
    private boolean personIsMain = true;
    private ImageView[] preview = new ImageView[8];
    private Label[] label = new Label[8];

//TODO ISEQUAL

    public boolean isEqual(IVFXShotsPersons o) {
        return (this.id == o.id &&
                this.shotId == o.shotId &&
                this.personId == o.personId &&
                this.personIsMain == o.personIsMain);
    }

// TODO КОНСТРУКТОРЫ

    // пустой конструктор
    public IVFXShotsPersons() {
    }

    public static IVFXShotsPersons getNewDbInstance(IVFXShots ivfxShot, IVFXPersons ivfxPerson) {
        IVFXShotsPersons ivfxShotPerson = new IVFXShotsPersons();

        ivfxShotPerson.shotId = ivfxShot.getId();
        ivfxShotPerson.personId = ivfxPerson.getId();
        ivfxShotPerson.ivfxShot = ivfxShot;
        ivfxShotPerson.ivfxPerson = ivfxPerson;

        Statement statement = null;
        ResultSet rs = null;
        String sql;

        try {

            sql = "INSERT INTO tbl_shots_persons (" +
                    "shot_id, " +
                    "person_id, " +
                    "personIsMain) " +
                    "VALUES(" +
                    ivfxShotPerson.shotId + "," +
                    ivfxShotPerson.personId + "," +
                    (ivfxShotPerson.personIsMain ? 1 : 0) + ")";

            PreparedStatement ps = Main.mainConnection.prepareStatement(sql);
            ps.executeUpdate(sql, Statement.RETURN_GENERATED_KEYS);
            rs = ps.getGeneratedKeys();
            if (rs.next()) {
                ivfxShotPerson.id = rs.getInt(1);
                System.out.println("Создана запись для персонажа «" + ivfxShotPerson.ivfxPerson.getName() + "» плана «" + ivfxShotPerson.ivfxShot.getFirstFrameNumber() + "-" + ivfxShotPerson.ivfxShot.getLastFrameNumber() + "» файла «" + ivfxShotPerson.ivfxShot.getIvfxFile().getTitle() + "» с идентификатором " + rs.getInt(1));

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

        return ivfxShotPerson;
    }


    public static List<IVFXShotsPersons> loadList(IVFXShots ivfxShot, boolean withPreview) {
        return loadList(ivfxShot, withPreview, null);
    }
    public static List<IVFXShotsPersons> loadList(IVFXShots ivfxShot, boolean withPreview, ProgressBar progressBar) {
        List<IVFXShotsPersons> listShotPersons = new ArrayList<>();

        int iProgress = 0;
        Statement statement = null;
        ResultSet rs = null;
        String sql;

        try {
            statement = Main.mainConnection.createStatement();

            sql = "SELECT * FROM tbl_shots_persons WHERE shot_id = " + ivfxShot.getId();

            String sqlCnt = "SELECT COUNT(*) AS CNT FROM (" + sql + ") AS tmp";
            ResultSet rsCnt = null;
            rsCnt = statement.executeQuery(sqlCnt);
            rsCnt.next();
            int countRows = rsCnt.getInt("CNT");
            rsCnt.close();

            rs = statement.executeQuery(sql);
            while (rs.next()) {

                if (progressBar != null) progressBar.setProgress((double)++iProgress / countRows);

                IVFXShotsPersons shotPerson = new IVFXShotsPersons();
                shotPerson.id = rs.getInt("id");
                shotPerson.shotId = rs.getInt("shot_id");
                shotPerson.personId = rs.getInt("person_id");
                shotPerson.personIsMain = rs.getBoolean("personIsMain");
                shotPerson.ivfxShot = IVFXShots.load(shotPerson.shotId, false);
                shotPerson.ivfxPerson = IVFXPersons.load(shotPerson.personId, withPreview);
                shotPerson.preview = shotPerson.ivfxPerson.getPreview();
                shotPerson.label = shotPerson.ivfxPerson.getLabel();

                listShotPersons.add(shotPerson);
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

        return listShotPersons;
    }

    public void save() {
        String sql = "UPDATE tbl_shots_persons SET shot_id = ?, person_id = ?, personIsMain = ? WHERE id = ?";
        try {
            PreparedStatement ps = Main.mainConnection.prepareStatement(sql);
            ps.setInt   (1, this.shotId);
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
        String sql = "DELETE FROM tbl_shots_persons WHERE id = ?";
        try {
            PreparedStatement ps = Main.mainConnection.prepareStatement(sql);
            ps.setInt   (1, this.id);
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static boolean delete(IVFXShots ivfxShot, IVFXPersons ivfxPerson) {

        if (ivfxShot != null && ivfxPerson != null) {
            int shotId = ivfxShot.getId();
            int personId = ivfxPerson.getId();
            String sql = "DELETE FROM tbl_shots_persons WHERE (shot_id = ? AND person_id = ?)";
            try {
                PreparedStatement ps = Main.mainConnection.prepareStatement(sql);
                ps.setInt   (1, shotId);
                ps.setInt   (2, personId);
                ps.executeUpdate();
                ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return true;
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
    public String getDescription() {
        return this.ivfxPerson.getDescription();
    }

    public IVFXShots getIvfxShot() {
        return ivfxShot;
    }

    public void setIvfxShot(IVFXShots ivfxShot) {
        this.ivfxShot = ivfxShot;
    }

    public IVFXPersons getIvfxPerson() {
        return ivfxPerson;
    }

    public void setIvfxPerson(IVFXPersons ivfxPerson) {
        this.ivfxPerson = ivfxPerson;
    }

    public boolean getsPersonIsMain() {
        return personIsMain;
    }

    public void setPersonIsMain(boolean personIsMain) {
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

    public int getShotId() {
        return shotId;
    }

    public void setShotId(int shotId) {
        this.shotId = shotId;
    }

    public int getPersonId() {
        return personId;
    }

    public void setPersonId(int personId) {
        this.personId = personId;
    }

}

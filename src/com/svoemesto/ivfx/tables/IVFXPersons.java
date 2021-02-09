package com.svoemesto.ivfx.tables;

import com.svoemesto.ivfx.Main;
import com.svoemesto.ivfx.utils.ConvertToFxImage;

import com.svoemesto.ivfx.utils.OverlayImage;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.ImageView;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class IVFXPersons {

    public static transient final String PERSON_SUFFIX = "_Persons";
    public static transient final String PERSON_PREFIX = "person_";
    public static transient final String PERSON_SUFFIX_PREVIEW = "_preview";
    public static transient final String PERSON_SUFFIX_FULLSIZE = "_fullsize";
    public static transient final String PERSON_FULLSIZE_STUB = "blank_person_400p.jpg";
    public static transient final String PERSON_PREVIEW_STUB = "blank_person.jpg";
    public static transient boolean WORK_WHITH_DATABASE = true;

    private int id;
    private int projectId;
    private int order = 0;
    private IVFXProjects ivfxProject;
    private String name = "New Person";
    private String description = "";
    private ImageView[] preview = new ImageView[8];
    private Label[] label = new Label[8];

    //TODO ISEQUAL

    public boolean isEqual(IVFXPersons o) {
        return (this.id == o.id &&
                this.projectId == o.projectId &&
                this.order == o.order &&
                this.name.equals(o.name) &&
                this.description.equals(o.description));
    }

// TODO КОНСТРУКТОРЫ

    // пустой конструктор
    public IVFXPersons() {
    }

    public static IVFXPersons getNewDbInstance(IVFXProjects ivfxProject) {
        IVFXPersons ivfxPerson = new IVFXPersons();

        Statement statement = null;
        ResultSet rs = null;
        String sql;

        try {
            statement = Main.mainConnection.createStatement();

            sql = "SELECT COUNT(*) FROM tbl_persons WHERE project_id = " + ivfxProject.getId();
            rs = statement.executeQuery(sql);
            if (rs.next()) {
                ivfxPerson.order = rs.getInt(1) + 1;  // индексация столбцов начинается с единицы
            }

            sql = "INSERT INTO tbl_persons (" +
                    "project_id, " +
                    "order_person, " +
                    "name, " +
                    "description) " +
                    "VALUES(" +
                    ivfxProject.getId() + "," +
                    ivfxPerson.order + "," +
                    "'" + ivfxPerson.name + "'" + "," +
                    "'" + ivfxPerson.description + "'" + ")";

            PreparedStatement ps = Main.mainConnection.prepareStatement(sql);
            ps.executeUpdate(sql, Statement.RETURN_GENERATED_KEYS);
            rs = ps.getGeneratedKeys();
            if (rs.next()) {
                ivfxPerson.id = rs.getInt(1);
                System.out.println("Создана запись для персонажа «" + ivfxPerson.name + "» с идентификатором " + rs.getInt(1));
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

        return ivfxPerson;
    }


    public static IVFXPersons loadByName(String personName, IVFXProjects project, boolean withPreview) {
        Statement statement = null;
        ResultSet rs = null;
        String sql;

        try {
            statement = Main.mainConnection.createStatement();

            sql = "SELECT * FROM tbl_persons WHERE name = '" + personName + "' AND project_id = " + project.getId();
            rs = statement.executeQuery(sql);
            if (rs.next()) {
                IVFXPersons person = new IVFXPersons();
                person.id = rs.getInt("id");
                person.projectId = rs.getInt("project_id");
                person.order = rs.getInt("order_person");
                person.name = rs.getString("name");
                person.description = rs.getString("description");
                person.ivfxProject = IVFXProjects.load(person.projectId);

                if (withPreview) {
                    String fileName = person.getPersonPicturePreview();
                    File file = new File(fileName);
                    for (int i = 0; i < 8; i++) {
                        person.label[i] = new Label(person.name);
                        person.label[i].setPrefWidth(135);
                    }

                    if (!file.exists()) {
                        fileName = person.getPersonPicturePreviewStub();
                        file = new File(fileName);
                    }
                    try {
                        BufferedImage bufferedImage = ImageIO.read(file);
                        for (int i = 0; i < 8; i++) {
                            person.preview[i] = new ImageView(ConvertToFxImage.convertToFxImage(bufferedImage));
                            person.label[i].setGraphic(person.preview[i]);
                            person.label[i].setContentDisplay(ContentDisplay.TOP);
                        }

                    } catch (IOException e) {}

                }

                return person;
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

    public static IVFXPersons load(int id, boolean withPreview) {
        Statement statement = null;
        ResultSet rs = null;
        String sql;

        try {
            statement = Main.mainConnection.createStatement();

            sql = "SELECT * FROM tbl_persons WHERE id = " + id;
            rs = statement.executeQuery(sql);
            if (rs.next()) {
                IVFXPersons person = new IVFXPersons();
                person.id = rs.getInt("id");
                person.projectId = rs.getInt("project_id");
                person.order = rs.getInt("order_person");
                person.name = rs.getString("name");
                person.description = rs.getString("description");
                person.ivfxProject = IVFXProjects.load(person.projectId);

                if (withPreview) {
                    String fileName = person.getPersonPicturePreview();
                    File file = new File(fileName);
                    for (int i = 0; i < 8; i++) {
                        person.label[i] = new Label(person.name);
                        person.label[i].setPrefWidth(135);
                    }

                    if (!file.exists()) {
                        fileName = person.getPersonPicturePreviewStub();
                        file = new File(fileName);
                    }
                    try {
                        BufferedImage bufferedImage = ImageIO.read(file);
                        for (int i = 0; i < 8; i++) {
                            person.preview[i] = new ImageView(ConvertToFxImage.convertToFxImage(OverlayImage.setOverlayUnderlineText(bufferedImage, person.name)));
                            person.label[i].setGraphic(person.preview[i]);
                            person.label[i].setContentDisplay(ContentDisplay.TOP);
                        }

                    } catch (IOException e) {}

                }

                return person;
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

    public static List<IVFXPersons> loadListPersonsWithoutGroups(IVFXProjects ivfxProject, boolean withPreview) {
        return loadListPersonsWithoutGroups(ivfxProject, withPreview, null);
    }
    public static List<IVFXPersons> loadListPersonsWithoutGroups(IVFXProjects ivfxProject, boolean withPreview, ProgressBar progressBar) {
        List<IVFXPersons> listPersons = new ArrayList<>();

        int iProgress = 0;
        Statement statement = null;
        ResultSet rs = null;
        String sql;

        try {
            statement = Main.mainConnection.createStatement();

            sql = "select tbl_persons.* from tbl_persons left join tbl_groups_persons on tbl_persons.id = tbl_groups_persons.person_id " +
                    "where tbl_groups_persons.group_id IS NULL AND tbl_persons.project_id = " + ivfxProject.getId() + " ORDER BY name";

            String sqlCnt = "SELECT COUNT(*) AS CNT FROM (" + sql + ") AS tmp";
            ResultSet rsCnt = null;
            rsCnt = statement.executeQuery(sqlCnt);
            rsCnt.next();
            int countRows = rsCnt.getInt("CNT");
            rsCnt.close();

            rs = statement.executeQuery(sql);
            while (rs.next()) {

                if (progressBar != null) progressBar.setProgress((double)++iProgress / countRows);

                IVFXPersons person = new IVFXPersons();
                person.id = rs.getInt("id");
                person.order = rs.getInt("order_person");
                person.projectId = rs.getInt("project_id");
                person.name = rs.getString("name");
                person.description = rs.getString("description");
                person.ivfxProject = IVFXProjects.load(person.projectId);
                listPersons.add(person);
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

        if (withPreview) {
            iProgress = 0;
            for (IVFXPersons ivfxPersons : listPersons) {

                if (progressBar != null) progressBar.setProgress((double)++iProgress / listPersons.size());

                String fileName = ivfxPersons.getPersonPicturePreview();
                File file = new File(fileName);
                for (int i = 0; i < 8; i++) {
                    ivfxPersons.label[i] = new Label(ivfxPersons.name);
                    ivfxPersons.label[i].setPrefWidth(135);
                }

                if (!file.exists()) {
                    fileName = ivfxPersons.getPersonPicturePreviewStub();
                    file = new File(fileName);
                }
                try {
                    BufferedImage bufferedImage = ImageIO.read(file);
                    for (int i = 0; i < 8; i++) {
                        ivfxPersons.preview[i] = new ImageView(ConvertToFxImage.convertToFxImage(OverlayImage.setOverlayUnderlineText(bufferedImage, ivfxPersons.name)));
                        ivfxPersons.label[i].setGraphic(ivfxPersons.preview[i]);
                        ivfxPersons.label[i].setContentDisplay(ContentDisplay.TOP);
                    }

                } catch (IOException e) {}

            }
        }

        return listPersons;
    }

    public static List<IVFXPersons> loadList(IVFXProjects ivfxProject, boolean withPreview) {
        return loadList(ivfxProject, withPreview, null);
    }
    public static List<IVFXPersons> loadList(IVFXProjects ivfxProject, boolean withPreview, ProgressBar progressBar) {
        List<IVFXPersons> listPersons = new ArrayList<>();

        int iProgress = 0;
        Statement statement = null;
        ResultSet rs = null;
        String sql;

        try {
            statement = Main.mainConnection.createStatement();

            sql = "SELECT * FROM tbl_persons WHERE project_id = " + ivfxProject.getId() + " ORDER BY name";

            String sqlCnt = "SELECT COUNT(*) AS CNT FROM (" + sql + ") AS tmp";
            ResultSet rsCnt = null;
            rsCnt = statement.executeQuery(sqlCnt);
            rsCnt.next();
            int countRows = rsCnt.getInt("CNT");
            rsCnt.close();

            rs = statement.executeQuery(sql);
            while (rs.next()) {

                if (progressBar != null) progressBar.setProgress((double)++iProgress / countRows);

                IVFXPersons person = new IVFXPersons();
                person.id = rs.getInt("id");
                person.order = rs.getInt("order_person");
                person.projectId = rs.getInt("project_id");
                person.name = rs.getString("name");
                person.description = rs.getString("description");
                person.ivfxProject = IVFXProjects.load(person.projectId);
                listPersons.add(person);
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

        if (withPreview) {
            iProgress = 0;
            for (IVFXPersons ivfxPersons : listPersons) {

                if (progressBar != null) progressBar.setProgress((double)++iProgress / listPersons.size());

                String fileName = ivfxPersons.getPersonPicturePreview();
                File file = new File(fileName);
                for (int i = 0; i < 8; i++) {
                    ivfxPersons.label[i] = new Label(ivfxPersons.name);
                    ivfxPersons.label[i].setPrefWidth(135);
                }

                if (!file.exists()) {
                    fileName = ivfxPersons.getPersonPicturePreviewStub();
                    file = new File(fileName);
                }
                try {
                    BufferedImage bufferedImage = ImageIO.read(file);
                    for (int i = 0; i < 8; i++) {
                        ivfxPersons.preview[i] = new ImageView(ConvertToFxImage.convertToFxImage(OverlayImage.setOverlayUnderlineText(bufferedImage, ivfxPersons.name)));
                        ivfxPersons.label[i].setGraphic(ivfxPersons.preview[i]);
                        ivfxPersons.label[i].setContentDisplay(ContentDisplay.TOP);
                    }

                } catch (IOException e) {}

            }
        }

        return listPersons;
    }

    // TODO SAVE


    public void save() {
        String sql = "UPDATE tbl_persons SET order_person = ?, name = ?, description = ? WHERE id = ?";
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

    // TODO DELETE


    public void delete() {
        String sql = "DELETE FROM tbl_persons WHERE id = ?";
        try {
            PreparedStatement ps = Main.mainConnection.prepareStatement(sql);
            ps.setInt   (1, this.id);
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



    public static boolean isPersonPresentInList(IVFXPersons ivfxPersons, List<IVFXPersons> list) {
        for (IVFXPersons person : list) {
            if (person.getId() == ivfxPersons.getId()) {
                return true;
            }
        }
        return false;
    }

    // TODO GETTERS SETTERS

    public ImageView[] getPreview() {
        return preview;
    }

    public ImageView getPreview(int i) {
        return preview[i];
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

    public void setPreview(ImageView preview, int i) {
        this.preview[i] = preview;
    }

    public String getPersonPicturePreview() {
        String fileName = this.getIvfxProject().getPersonsFolder() + "\\" + PERSON_PREFIX + this.getId() + PERSON_SUFFIX_PREVIEW + ".jpg";
        return fileName;
    }

    public String getPersonPictureFullSize() {
        String fileName = this.getIvfxProject().getPersonsFolder() + "\\" + PERSON_PREFIX + this.getId() + PERSON_SUFFIX_FULLSIZE + ".jpg";
        return fileName;
    }

    public String getPersonPicturePreviewStub() {
        String fileName = this.getIvfxProject().getPersonsFolder() + "\\" + PERSON_PREVIEW_STUB;
        return fileName;
    }

    public String getPersonPictureFullSizeStub() {
        String fileName = this.getIvfxProject().getPersonsFolder() + "\\" + PERSON_FULLSIZE_STUB;
        return fileName;
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

    public Label[] getLabel() {
        return label;
    }

    public Label getLabel(int i) {
        return label[i];
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

    public void setLabel(Label label, int i) {
        this.label[i] = label;
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

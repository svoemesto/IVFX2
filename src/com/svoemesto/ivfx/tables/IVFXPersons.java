package com.svoemesto.ivfx.tables;

import com.svoemesto.ivfx.Main;
import com.svoemesto.ivfx.utils.ConvertToFxImage;

import com.svoemesto.ivfx.utils.OverlayImage;
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
    private UUID uuid  = UUID.randomUUID(); // UUID
    private UUID projectUuid;
    private IVFXProjects ivfxProject;
    private String name = "New Person";
    private String description = "";
    private transient ImageView preview;
    private transient Label label;

    //TODO ISEQUAL

    public boolean isEqual(IVFXPersons o) {
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
                    "description, " +
                    "uuid, " +
                    "project_uuid) " +
                    "VALUES(" +
                    ivfxProject.getId() + "," +
                    ivfxPerson.order + "," +
                    "'" + ivfxPerson.name + "'" + "," +
                    "'" + ivfxPerson.description + "'" + "," +
                    "'" + ivfxPerson.uuid.toString() + "'" + "," +
                    "'" + ivfxProject.getUuid().toString() + "'" +
                    ")";

            PreparedStatement ps = Main.mainConnection.prepareStatement(sql);
            ps.executeUpdate(sql, Statement.RETURN_GENERATED_KEYS);
            rs = ps.getGeneratedKeys();
            if (rs.next()) {
                ivfxPerson.id = rs.getInt(1);
                System.out.println("Создана запись для персонажа «" + ivfxPerson.name + "» " + ivfxPerson.uuid + " с идентификатором " + rs.getInt(1));
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

    public static IVFXPersons loadByUuid(UUID personUUID, boolean withPreview) {
        Statement statement = null;
        ResultSet rs = null;
        String sql;

        try {
            statement = Main.mainConnection.createStatement();

            sql = "SELECT * FROM tbl_persons WHERE uuid = '" + personUUID.toString() + "'";
            rs = statement.executeQuery(sql);
            if (rs.next()) {
                IVFXPersons person = new IVFXPersons();
                person.id = rs.getInt("id");
                person.projectId = rs.getInt("project_id");
                person.order = rs.getInt("order_person");
                person.name = rs.getString("name");
                person.description = rs.getString("description");
                person.uuid = UUID.fromString(rs.getString("uuid"));
                person.projectUuid = UUID.fromString(rs.getString("project_uuid"));
                person.ivfxProject = IVFXProjects.loadByUuid(person.projectUuid);

                if (withPreview) {
                    String fileName = person.getPersonPicturePreview();
                    File file = new File(fileName);
                    person.label = new Label(person.name);
                    person.label.setPrefWidth(135);
                    if (!file.exists()) {
                        fileName = person.getPersonPicturePreviewStub();
                        file = new File(fileName);
                    }
                    try {
                        BufferedImage bufferedImage = ImageIO.read(file);
                        person.preview = new ImageView(ConvertToFxImage.convertToFxImage(bufferedImage));
                        person.label.setGraphic(person.preview);
                        person.label.setContentDisplay(ContentDisplay.TOP);
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
                person.uuid = UUID.fromString(rs.getString("uuid"));
                person.projectUuid = UUID.fromString(rs.getString("project_uuid"));
                person.ivfxProject = IVFXProjects.loadByUuid(person.projectUuid);

                if (withPreview) {
                    String fileName = person.getPersonPicturePreview();
                    File file = new File(fileName);
                    person.label = new Label(person.name);
                    person.label.setPrefWidth(135);
                    if (!file.exists()) {
                        fileName = person.getPersonPicturePreviewStub();
                        file = new File(fileName);
                    }
                    try {
                        BufferedImage bufferedImage = ImageIO.read(file);
                        person.preview = new ImageView(ConvertToFxImage.convertToFxImage(bufferedImage));
                        person.label.setGraphic(person.preview);
                        person.label.setContentDisplay(ContentDisplay.TOP);
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

    public static IVFXPersons loadById(int id, boolean withPreview) {
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
                person.uuid = UUID.fromString(rs.getString("uuid"));
                person.projectUuid = UUID.fromString(rs.getString("project_uuid"));
                person.ivfxProject = IVFXProjects.loadByUuid(person.projectUuid);

                if (withPreview) {
                    String fileName = person.getPersonPicturePreview();
                    File file = new File(fileName);
                    person.label = new Label(person.name);
                    person.label.setPrefWidth(135);
                    if (!file.exists()) {
                        fileName = person.getPersonPicturePreviewStub();
                        file = new File(fileName);
                    }
                    try {
                        BufferedImage bufferedImage = ImageIO.read(file);
//                        person.preview = new ImageView(ConvertToFxImage.convertToFxImage(bufferedImage));
                        person.preview = new ImageView(ConvertToFxImage.convertToFxImage(OverlayImage.setOverlayUnderlineText(bufferedImage, person.name)));
                        person.label.setGraphic(person.preview);
                        person.label.setContentDisplay(ContentDisplay.TOP);
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
        List<IVFXPersons> listPersons = new ArrayList<>();

        Statement statement = null;
        ResultSet rs = null;
        String sql;

        try {
            statement = Main.mainConnection.createStatement();

            sql = "select * from tbl_persons left join tbl_groups_persons on tbl_persons.id = tbl_groups_persons.person_id " +
                    "where tbl_groups_persons.group_id IS NULL AND tbl_persons.project_id = " + ivfxProject.getId();
            rs = statement.executeQuery(sql);
            while (rs.next()) {
                IVFXPersons person = new IVFXPersons();
                person.id = rs.getInt("id");
                person.order = rs.getInt("order_person");
                person.projectId = rs.getInt("project_id");
                person.uuid = UUID.fromString(rs.getString("uuid"));
                person.projectUuid = UUID.fromString(rs.getString("project_uuid"));
                person.name = rs.getString("name");
                person.description = rs.getString("description");
                person.ivfxProject = IVFXProjects.loadById(person.projectId);
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
            for (IVFXPersons ivfxPersons : listPersons) {
                String fileName = ivfxPersons.getPersonPicturePreview();
                File file = new File(fileName);
                ivfxPersons.label = new Label(ivfxPersons.name);
                ivfxPersons.label.setPrefWidth(135);
                if (!file.exists()) {
                    fileName = ivfxPersons.getPersonPicturePreviewStub();
                    file = new File(fileName);
                }
                try {
                    BufferedImage bufferedImage = ImageIO.read(file);
//                        ivfxPersons.preview = new ImageView(ConvertToFxImage.convertToFxImage(bufferedImage));
                    ivfxPersons.preview = new ImageView(ConvertToFxImage.convertToFxImage(OverlayImage.setOverlayUnderlineText(bufferedImage, ivfxPersons.name)));
                    ivfxPersons.label.setGraphic(ivfxPersons.preview);
                    ivfxPersons.label.setContentDisplay(ContentDisplay.TOP);
                } catch (IOException e) {}

            }
        }

        return listPersons;
    }
    public static List<IVFXPersons> loadList(IVFXProjects ivfxProject, boolean withPreview) {
        List<IVFXPersons> listPersons = new ArrayList<>();

        Statement statement = null;
        ResultSet rs = null;
        String sql;

        try {
            statement = Main.mainConnection.createStatement();

            sql = "SELECT * FROM tbl_persons WHERE project_id = " + ivfxProject.getId() + " ORDER BY order_person";
            rs = statement.executeQuery(sql);
            while (rs.next()) {
                IVFXPersons person = new IVFXPersons();
                person.id = rs.getInt("id");
                person.order = rs.getInt("order_person");
                person.projectId = rs.getInt("project_id");
                person.uuid = UUID.fromString(rs.getString("uuid"));
                person.projectUuid = UUID.fromString(rs.getString("project_uuid"));
                person.name = rs.getString("name");
                person.description = rs.getString("description");
                person.ivfxProject = IVFXProjects.loadById(person.projectId);
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
            for (IVFXPersons ivfxPersons : listPersons) {
                String fileName = ivfxPersons.getPersonPicturePreview();
                File file = new File(fileName);
                ivfxPersons.label = new Label(ivfxPersons.name);
                ivfxPersons.label.setPrefWidth(135);
                if (!file.exists()) {
                    fileName = ivfxPersons.getPersonPicturePreviewStub();
                    file = new File(fileName);
                }
                try {
                    BufferedImage bufferedImage = ImageIO.read(file);
//                    ivfxPersons.preview = new ImageView(ConvertToFxImage.convertToFxImage(bufferedImage));
                    ivfxPersons.preview = new ImageView(ConvertToFxImage.convertToFxImage(OverlayImage.setOverlayUnderlineText(bufferedImage, ivfxPersons.name)));
                    ivfxPersons.label.setGraphic(ivfxPersons.preview);
                    ivfxPersons.label.setContentDisplay(ContentDisplay.TOP);
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

    // TODO GETTERS SETTERS

    public ImageView getPreview() {
        return preview;
    }

    public void setPreview(ImageView preview) {
        this.preview = preview;
    }

    public String getPersonPicturePreview() {
        String fileName = this.getIvfxProject().getPersonsFolder() + "\\" + PERSON_PREFIX + this.getUuid().toString() + PERSON_SUFFIX_PREVIEW + ".jpg";
        return fileName;
    }

    public String getPersonPictureFullSize() {
        String fileName = this.getIvfxProject().getPersonsFolder() + "\\" + PERSON_PREFIX + this.getUuid().toString()+ PERSON_SUFFIX_FULLSIZE + ".jpg";
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

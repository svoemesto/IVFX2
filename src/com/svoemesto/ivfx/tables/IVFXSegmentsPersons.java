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
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class IVFXSegmentsPersons {

    private int id;
    private int segmentId;
    private int personId;
    private UUID uuid  = UUID.randomUUID();
    private UUID segmentUuid;
    private UUID personUuid;
    private IVFXSegments ivfxSegment;
    private IVFXPersons ivfxPerson;
    private boolean personIsMain = true;
    private transient ImageView preview;
    private transient Label label;

//TODO ISEQUAL

    public boolean isEqual(IVFXSegmentsPersons o) {
        return (this.id == o.id &&
                this.segmentId == o.segmentId &&
                this.personId == o.personId &&
                this.uuid.equals(o.uuid) &&
                this.segmentUuid.equals(o.segmentUuid) &&
                this.personUuid.equals(o.personUuid) &&
                this.personIsMain == o.personIsMain);
    }

// TODO КОНСТРУКТОРЫ

    // пустой конструктор
    public IVFXSegmentsPersons() {
    }




    public static List<IVFXSegmentsPersons> loadList(IVFXSegments ivfxSegment, boolean withPreview) {
        List<IVFXSegmentsPersons> listSegmentPersons = new ArrayList<>();

        Statement statement = null;
        ResultSet rs = null;
        String sql;

        try {
            statement = Main.mainConnection.createStatement();

            sql = "SELECT * FROM tbl_segments_persons WHERE segment_id = " + ivfxSegment.getId();
            rs = statement.executeQuery(sql);
            while (rs.next()) {
                IVFXSegmentsPersons segmentPerson = new IVFXSegmentsPersons();
                segmentPerson.id = rs.getInt("id");
                segmentPerson.segmentId = rs.getInt("segment_id");
                segmentPerson.personId = rs.getInt("person_id");
                segmentPerson.uuid = UUID.fromString(rs.getString("uuid"));
                segmentPerson.segmentUuid = UUID.fromString(rs.getString("segment_uuid"));
                segmentPerson.personIsMain = rs.getBoolean("personIsMain");
                segmentPerson.personUuid = UUID.fromString(rs.getString("person_uuid"));
                segmentPerson.ivfxSegment = IVFXSegments.loadById(segmentPerson.segmentId, false);
                segmentPerson.ivfxPerson = IVFXPersons.loadById(segmentPerson.personId, withPreview);
                segmentPerson.preview = segmentPerson.ivfxPerson.getPreview();
                segmentPerson.label = segmentPerson.ivfxPerson.getLabel();

                listSegmentPersons.add(segmentPerson);
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

        return listSegmentPersons;
    }



    // TODO GETTERS SETTERS


    public ImageView getPreview() {
        return this.preview;
    }

    public void setPreview(ImageView preview) {
        this.preview = preview;
    }

    public String getName() {
        return this.ivfxPerson.getName();
    }
    public String getDescription() {
        return this.ivfxPerson.getDescription();
    }
    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public UUID getSegmentUuid() {
        return segmentUuid;
    }

    public void setSegmentUuid(UUID segmentUuid) {
        this.segmentUuid = segmentUuid;
    }

    public UUID getPersonUuid() {
        return personUuid;
    }

    public void setPersonUuid(UUID personUuid) {
        this.personUuid = personUuid;
    }

    public IVFXSegments getIvfxSegment() {
        return ivfxSegment;
    }

    public void setIvfxSegment(IVFXSegments ivfxSegment) {
        this.ivfxSegment = ivfxSegment;
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

    public int getSegmentId() {
        return segmentId;
    }

    public void setSegmentId(int segmentId) {
        this.segmentId = segmentId;
    }

    public int getPersonId() {
        return personId;
    }

    public void setPersonId(int personId) {
        this.personId = personId;
    }

}

package com.svoemesto.ivfx.tables;

import com.svoemesto.ivfx.Main;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class IVFXTagsProjects {

    private int id;
    private int tagId;
    private int projectId;
    private IVFXTags ivfxTag;
    private IVFXProjects ivfxProject;

    public boolean isEqual(IVFXTagsProjects o) {
        return (this.id == o.id &&
                this.tagId == o.tagId &&
                this.projectId == o.projectId);
    }

    public static IVFXTagsProjects getNewDbInstance(IVFXTags ivfxTag, IVFXProjects ivfxProject, boolean withPreview) {
        IVFXTagsProjects ivfxTagsProjects = IVFXTagsProjects.load(ivfxTag, ivfxProject,withPreview);

        if (ivfxTagsProjects != null) {
            return ivfxTagsProjects;
        }

        ivfxTagsProjects = new IVFXTagsProjects();

        ivfxTagsProjects.tagId = ivfxTag.getId();
        ivfxTagsProjects.projectId = ivfxProject.getId();
        ivfxTagsProjects.ivfxTag = ivfxTag;
        ivfxTagsProjects.ivfxProject = ivfxProject;

        ResultSet rs = null;
        String sql;

        try {


            sql = "INSERT INTO tbl_tags_projects (" +
                    "tag_id, " +
                    "project_id) " +
                    "VALUES(" +
                    ivfxTag.getId() + "," +
                    ivfxProject.getId() + ")";

            PreparedStatement ps = Main.mainConnection.prepareStatement(sql);
            ps.executeUpdate(sql, Statement.RETURN_GENERATED_KEYS);
            rs = ps.getGeneratedKeys();
            if (rs.next()) {
                ivfxTagsProjects.id = rs.getInt(1);
                System.out.println("Создана запись для tags-projects с идентификатором " + rs.getInt(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close(); // close result set
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }


        return ivfxTagsProjects;
    }

    public static IVFXTagsProjects load(int id, boolean withPreview) {
        Statement statement = null;
        ResultSet rs = null;
        String sql;

        try {
            statement = Main.mainConnection.createStatement();

            sql = "SELECT * FROM tbl_tags_projects WHERE id = " + id;
            rs = statement.executeQuery(sql);
            if (rs.next()) {
                IVFXTagsProjects tagsProjects = new IVFXTagsProjects();
                tagsProjects.id = rs.getInt("id");
                tagsProjects.tagId = rs.getInt("tag_id");
                tagsProjects.projectId = rs.getInt("project_id");
                tagsProjects.ivfxTag = IVFXTags.load(tagsProjects.tagId,withPreview);
                tagsProjects.ivfxProject = IVFXProjects.load(tagsProjects.projectId);
                return tagsProjects;
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

    public static IVFXTagsProjects load(IVFXTags ivfxTag, IVFXProjects ivfxProject, boolean withPreview) {
        Statement statement = null;
        ResultSet rs = null;
        String sql;

        try {
            statement = Main.mainConnection.createStatement();

            sql = "SELECT * FROM tbl_tags_projects WHERE tag_id = " + ivfxTag.getId() + " AND project_id = " + ivfxProject.getId();
            rs = statement.executeQuery(sql);
            if (rs.next()) {
                IVFXTagsProjects tagsProjects = new IVFXTagsProjects();
                tagsProjects.id = rs.getInt("id");
                tagsProjects.tagId = rs.getInt("tag_id");
                tagsProjects.projectId = rs.getInt("project_id");
                tagsProjects.ivfxTag = IVFXTags.load(tagsProjects.tagId,withPreview);
                tagsProjects.ivfxProject = IVFXProjects.load(tagsProjects.projectId);
                return tagsProjects;
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







    public int getId() {
        return id;
    }

    public int getTagId() {
        return tagId;
    }

    public void setTagId(int tagId) {
        this.tagId = tagId;
    }

    public int getProjectId() {
        return projectId;
    }

    public void setProjectId(int projectId) {
        this.projectId = projectId;
    }

    public IVFXTags getIvfxTag() {
        return ivfxTag;
    }

    public void setIvfxTag(IVFXTags ivfxTag) {
        this.ivfxTag = ivfxTag;
    }

    public IVFXProjects getIvfxProject() {
        return ivfxProject;
    }

    public void setIvfxProject(IVFXProjects ivfxProject) {
        this.ivfxProject = ivfxProject;
    }
}

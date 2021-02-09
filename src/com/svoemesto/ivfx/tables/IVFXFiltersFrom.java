package com.svoemesto.ivfx.tables;

import com.svoemesto.ivfx.Main;
import com.svoemesto.ivfx.utils.FFmpeg;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class IVFXFiltersFrom {

    private int id;
    private int projectId;
    private int filterId;
    private int fileId;
    private int recordId;
    private int recordTypeId;
    private IVFXProjects ivfxProject;
    private IVFXFiles ivfxFile;
    private IVFXFilters ivfxFilter = null;
    private int order = 0;
    private IVFXEnumFilterFromTypes recordType = null;

//TODO ISEQUAL

    public boolean isEqual(IVFXFiltersFrom o) {
        return (this.id == o.id &&
                this.projectId == o.projectId &&
                this.filterId == o.filterId &&
                this.fileId == o.fileId &&
                this.recordId == o.recordId &&
                this.recordTypeId == o.recordTypeId &&
                this.order == o.order);
    }

// TODO КОНСТРУКТОРЫ

    // пустой конструктор
    public IVFXFiltersFrom() {
    }

    public static IVFXFiltersFrom getNewDbInstance(IVFXFilters ivfxFilter, IVFXFiles ivfxFile) {

        IVFXFiltersFrom ivfxFiltersFrom = new IVFXFiltersFrom();

        ivfxFiltersFrom.id = 0;
        ivfxFiltersFrom.projectId = ivfxFilter.getProjectId();
        ivfxFiltersFrom.filterId = ivfxFilter.getId();
        ivfxFiltersFrom.fileId = ivfxFile.getId();
        ivfxFiltersFrom.recordId = 0;
        ivfxFiltersFrom.recordTypeId = 1;
        ivfxFiltersFrom.order = 0;
        ivfxFiltersFrom.ivfxProject = ivfxFilter.getIvfxProject();
        ivfxFiltersFrom.ivfxFile = null;
        ivfxFiltersFrom.ivfxFilter = ivfxFilter;
        ivfxFiltersFrom.recordType = null;


        Statement statement = null;
        ResultSet rs = null;
        String sql;

        try {
            statement = Main.mainConnection.createStatement();

            sql = "SELECT COUNT(*) FROM tbl_filters_from WHERE filter_id = " + ivfxFiltersFrom.filterId;
            rs = statement.executeQuery(sql);
            if (rs.next()) {
                ivfxFiltersFrom.order = rs.getInt(1) + 1;  // индексация столбцов начинается с единицы
            }
            sql = "INSERT INTO tbl_filters_from (" +
                    "filter_id, " +
                    "project_id, " +
                    "file_id, " +
                    "record_type_id, " +
                    "order_filter_from) " +
                    "VALUES(" +
                    ivfxFiltersFrom.filterId + "," +
                    ivfxFiltersFrom.projectId + "," +
                    ivfxFiltersFrom.fileId + "," +
                    ivfxFiltersFrom.recordTypeId + "," +
                    ivfxFiltersFrom.order + ")";


            PreparedStatement ps = Main.mainConnection.prepareStatement(sql);
            ps.executeUpdate(sql, Statement.RETURN_GENERATED_KEYS);
            rs = ps.getGeneratedKeys();
            if (rs.next()) {
                ivfxFiltersFrom.id = rs.getInt(1);
                System.out.println("Создана запись для from-фильтра с идентификатором " + rs.getInt(1));
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

        return ivfxFiltersFrom;


    }

// TODO LOAD



    public static IVFXFiltersFrom load(int id) {
        Statement statement = null;
        ResultSet rs = null;
        String sql;

        try {
            statement = Main.mainConnection.createStatement();

            sql = "SELECT * FROM tbl_filters_from WHERE id = " + id;
            rs = statement.executeQuery(sql);
            if (rs.next()) {
                IVFXFiltersFrom filterFrom = new IVFXFiltersFrom();
                filterFrom.id = rs.getInt("id");
                filterFrom.projectId = rs.getInt("project_id");
                filterFrom.filterId = rs.getInt("filter_id");
                filterFrom.fileId = rs.getInt("file_id");
                filterFrom.recordId = rs.getInt("record_id");
                filterFrom.recordTypeId = rs.getInt("record_type_id");
                filterFrom.order = rs.getInt("order_filter_from");
                filterFrom.ivfxProject = IVFXProjects.load(filterFrom.projectId);
                filterFrom.ivfxFile = IVFXFiles.load(filterFrom.fileId);
                filterFrom.ivfxFilter = IVFXFilters.load(filterFrom.filterId);
                switch (filterFrom.recordTypeId) {
                    case 1:
                        filterFrom.recordType = IVFXEnumFilterFromTypes.FILE;
                        break;
                    case 2:
                        filterFrom.recordType = IVFXEnumFilterFromTypes.SHOT;
                        break;
                    case 3:
                        filterFrom.recordType = IVFXEnumFilterFromTypes.SCENE;
                        break;
                    case 4:
                        filterFrom.recordType = IVFXEnumFilterFromTypes.EVENT;
                        break;
                    default:
                        filterFrom.recordType = null;
                        break;
                }
                return filterFrom;
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

    public static List<IVFXFiltersFrom> loadList(IVFXFilters ivfxFilter) {
        List<IVFXFiltersFrom> listFiltersFrom = new ArrayList<>();

        Statement statement = null;
        ResultSet rs = null;
        String sql;

        try {
            statement = Main.mainConnection.createStatement();

            sql = "SELECT * FROM tbl_filters_from WHERE filter_id = " + ivfxFilter.getId();
            rs = statement.executeQuery(sql);
            while (rs.next()) {
                IVFXFiltersFrom filterFrom = new IVFXFiltersFrom();
                filterFrom.id = rs.getInt("id");
                filterFrom.projectId = rs.getInt("project_id");
                filterFrom.filterId = rs.getInt("filter_id");
                filterFrom.fileId = rs.getInt("file_id");
                filterFrom.recordId = rs.getInt("record_id");
                filterFrom.recordTypeId = rs.getInt("record_type_id");
                filterFrom.order = rs.getInt("order_filter_from");
                filterFrom.ivfxProject = IVFXProjects.load(filterFrom.projectId);
                filterFrom.ivfxFile = IVFXFiles.load(filterFrom.fileId);
                filterFrom.ivfxFilter = IVFXFilters.load(filterFrom.filterId);
                switch (filterFrom.recordTypeId) {
                    case 1:
                        filterFrom.recordType = IVFXEnumFilterFromTypes.FILE;
                        break;
                    case 2:
                        filterFrom.recordType = IVFXEnumFilterFromTypes.SHOT;
                        break;
                    case 3:
                        filterFrom.recordType = IVFXEnumFilterFromTypes.SCENE;
                        break;
                    case 4:
                        filterFrom.recordType = IVFXEnumFilterFromTypes.EVENT;
                        break;
                    default:
                        filterFrom.recordType = null;
                        break;
                }
                listFiltersFrom.add(filterFrom);
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

        return listFiltersFrom;
    }


// TODO SAVE

    public void save() {
        String sql = "UPDATE tbl_filters_from SET " +
                "project_id = ?, " +
                "filter_id = ?, " +
                "file_id = ?, " +
                "record_id = ?, " +
                "record_type_id = ?, " +
                "order_filter_from = ? " +
                "WHERE id = ?";
        try {
            PreparedStatement ps = Main.mainConnection.prepareStatement(sql);
            ps.setInt   (1, this.projectId);
            ps.setInt   (2, this.filterId);
            ps.setInt   (3, this.fileId);
            ps.setInt   (4, this.recordId);
            ps.setInt   (5, this.recordTypeId);
            ps.setInt   (6, this.order);
            ps.setInt   (7, this.id);
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

// TODO DELETE

    public void delete() {
        String sql = "DELETE FROM tbl_filters_from WHERE id = ?";
        try {
            PreparedStatement ps = Main.mainConnection.prepareStatement(sql);
            ps.setInt   (1, this.id);
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

// TODO FUNCTIONS

    public String getFileTitle() {
        return this.ivfxFile.getTitle();
    }

    public String getRecordName() {
        if (this.recordId != 0 && this.ivfxProject != null) {
            switch (this.recordType) {
                case FILE:
                    if (this.ivfxFile != null) {
                        return this.ivfxFile.getTitle();
                    } else {
                        return "n/a";
                    }
                case SHOT:
                    IVFXShots ivfxShots = IVFXShots.load(this.recordId, false);
                    if (ivfxShots != null) {
                        return "(" + ivfxShots.getStart() + "-" + ivfxShots.getEnd() + ")";
                    } else {
                        return "n/a";
                    }
                case SCENE:
                    System.out.println("getRecordName для сцены " + this.recordId);
                    IVFXScenes ivfxScene = IVFXScenes.load(this.recordId);
                    if (ivfxScene != null) {
                        return "(" + ivfxScene.getStart() + "-" + ivfxScene.getEnd() + ") " + ivfxScene.getName();
                    } else {
                        return "n/a";
                    }
                case EVENT:
                    IVFXEvents ivfxEvents = IVFXEvents.load(this.recordId);
                    if (ivfxEvents != null) {
                        return "(" + ivfxEvents.getStart() + "-" + ivfxEvents.getEnd() + ") «" + ivfxEvents.getEventTypeName() + "» " + ivfxEvents.getName();
                    } else {
                        return "n/a";
                    }
                default:
                    return "n/a";
            }
        } else {
            return "n/a";
        }

    }

    public String getRecordDurationString() {
        int duration = this.getRecordDuration();
        if (duration > 0) {
            return FFmpeg.convertDurationToString(duration);
        } else {
            return "";
        }
    }

    public int getRecordDuration() {
        if (this.recordId != 0 && this.ivfxProject != null) {
            switch (this.recordType) {
                case FILE:
                    if (this.ivfxFile != null) {
                        return this.ivfxFile.getDuration();
                    } else {
                        return 0;
                    }
                case SHOT:
                    IVFXShots ivfxShots = IVFXShots.load(this.recordId, false);
                    if (ivfxShots != null) {
                        return ivfxShots.getDuration();
                    } else {
                        return 0;
                    }
                case SCENE:
                    IVFXScenes ivfxScene = IVFXScenes.load(this.recordId);
                    if (ivfxScene != null) {
                        return ivfxScene.getDuration();
                    } else {
                        return 0;
                    }
                case EVENT:
                    IVFXEvents ivfxEvents = IVFXEvents.load(this.recordId);
                    if (ivfxEvents != null) {
                        return ivfxEvents.getDuration();
                    } else {
                        return 0;
                    }
                default:
                    return 0;
            }
        } else {
            return 0;
        }
    }


    public String getRecordTypeName() {
        switch (this.recordType) {
            case FILE: return "Файл";
            case SHOT: return "План";
            case SCENE: return "Сцена";
            case EVENT: return "Событие";
            default: return "n/a";
        }
    }

// TODO GETTERS SETTERS


    public IVFXProjects getIvfxProject() {
        return ivfxProject;
    }

    public void setIvfxProject(IVFXProjects ivfxProject) {
        this.ivfxProject = ivfxProject;
    }

    public IVFXFilters getIvfxFilter() {
        return ivfxFilter;
    }

    public void setIvfxFilter(IVFXFilters ivfxFilter) {
        this.ivfxFilter = ivfxFilter;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public IVFXEnumFilterFromTypes getRecordType() {
        return recordType;
    }

    public void setRecordType(IVFXEnumFilterFromTypes recordType) {
        this.recordType = recordType;
    }

    public IVFXFiles getIvfxFile() {
        return ivfxFile;
    }

    public void setIvfxFile(IVFXFiles ivfxFile) {
        this.ivfxFile = ivfxFile;
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

    public int getFilterId() {
        return filterId;
    }

    public void setFilterId(int filterId) {
        this.filterId = filterId;
    }

    public int getFileId() {
        return fileId;
    }

    public void setFileId(int fileId) {
        this.fileId = fileId;
    }

    public int getRecordId() {
        return recordId;
    }

    public void setRecordId(int recordId) {
        this.recordId = recordId;
    }

    public int getRecordTypeId() {
        return recordTypeId;
    }

    public void setRecordTypeId(int recordTypeId) {
        this.recordTypeId = recordTypeId;
    }




}

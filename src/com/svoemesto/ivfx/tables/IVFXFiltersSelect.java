package com.svoemesto.ivfx.tables;

import com.svoemesto.ivfx.Main;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class IVFXFiltersSelect {

    private int id;
    private int projectId;
    private int filterId;
    private int recordTypeId = 1;
    private int objectTypeId = 0;
    private int objectId = 0;
    private IVFXProjects ivfxProject;
    private IVFXFilters ivfxFilter = null;
    private int order = 0;
    private IVFXEnumFilterSelectObjectTypes filterSelectObjectType = null;
    private IVFXEnumFilterFromTypes recordType = null;
    private boolean isIncluded = true;
    private boolean isAnd = true;

//TODO ISEQUAL

    public boolean isEqual(IVFXFiltersSelect o) {
        return (this.id == o.id &&
                this.projectId == o.projectId &&
                this.filterId == o.filterId &&
                this.objectTypeId == o.objectTypeId &&
                this.recordTypeId == o.recordTypeId &&
                this.objectId == o.objectId &&
                this.isIncluded == o.isIncluded &&
                this.isAnd == o.isAnd &&
                this.order == o.order);
    }

    // пустой конструктор
    public IVFXFiltersSelect() {
    }

    public static IVFXFiltersSelect getNewDbInstance(IVFXFilters ivfxFilter) {

        IVFXFiltersSelect ivfxFiltersSelect = new IVFXFiltersSelect();

        Statement statement = null;
        ResultSet rs = null;
        String sql;

        try {
            statement = Main.mainConnection.createStatement();

            sql = "SELECT COUNT(*) FROM tbl_filters_select WHERE filter_id = " + ivfxFilter.getId();
            rs = statement.executeQuery(sql);
            if (rs.next()) {
                ivfxFiltersSelect.order = rs.getInt(1) + 1;  // индексация столбцов начинается с единицы
            }

            sql = "INSERT INTO tbl_filters_select (" +
                    "filter_id, " +
                    "project_id, " +
                    "object_type_id, " +
                    "record_type_id, " +
                    "object_id, " +
                    "order_filter_select, " +
                    "isIncluded, " +
                    "isAnd) " +
                    "VALUES(" +
                    ivfxFilter.getId() + "," +
                    ivfxFilter.getProjectId() + "," +
                    ivfxFiltersSelect.objectTypeId + "," +
                    ivfxFiltersSelect.recordTypeId + "," +
                    ivfxFiltersSelect.objectId + "," +
                    ivfxFiltersSelect.order + "," +
                    ivfxFiltersSelect.isIncluded + "," +
                    ivfxFiltersSelect.isAnd + ")";

            PreparedStatement ps = Main.mainConnection.prepareStatement(sql);
            ps.executeUpdate(sql, Statement.RETURN_GENERATED_KEYS);
            rs = ps.getGeneratedKeys();
            if (rs.next()) {
                ivfxFiltersSelect.id = rs.getInt(1);
                System.out.println("Создана запись для select-фильтра с идентификатором " + rs.getInt(1));
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

        return ivfxFiltersSelect;
    }


    public static IVFXFiltersSelect load(int id) {
        Statement statement = null;
        ResultSet rs = null;
        String sql;

        try {
            statement = Main.mainConnection.createStatement();

            sql = "SELECT * FROM tbl_filters_select WHERE id = " + id;
            rs = statement.executeQuery(sql);
            if (rs.next()) {
                IVFXFiltersSelect filterSelect = new IVFXFiltersSelect();
                filterSelect.id = rs.getInt("id");
                filterSelect.projectId = rs.getInt("project_id");
                filterSelect.filterId = rs.getInt("filter_id");
                filterSelect.objectTypeId = rs.getInt("object_type_id");
                filterSelect.recordTypeId = rs.getInt("record_type_id");
                filterSelect.objectId = rs.getInt("object_id");
                filterSelect.order = rs.getInt("order_filter_select");
                filterSelect.isIncluded = rs.getBoolean("isIncluded");
                filterSelect.isAnd = rs.getBoolean("isAnd");
                filterSelect.ivfxProject = IVFXProjects.load(filterSelect.projectId);
                filterSelect.ivfxFilter = IVFXFilters.load(filterSelect.filterId);

                switch (filterSelect.recordTypeId) {
                    case 1:
                        filterSelect.filterSelectObjectType = IVFXEnumFilterSelectObjectTypes.PERSON;
                        break;
                    case 2:
                        filterSelect.filterSelectObjectType = IVFXEnumFilterSelectObjectTypes.GROUP;
                        break;
                    case 3:
                        filterSelect.filterSelectObjectType = IVFXEnumFilterSelectObjectTypes.OR;
                        break;
                    case 4:
                        filterSelect.filterSelectObjectType = IVFXEnumFilterSelectObjectTypes.AND;
                        break;
                    default:
                        filterSelect.filterSelectObjectType = null;
                        break;
                }

                switch (filterSelect.objectTypeId) {
                    case 1:
                        filterSelect.recordType = IVFXEnumFilterFromTypes.FILE;
                        break;
                    case 2:
                        filterSelect.recordType = IVFXEnumFilterFromTypes.SHOT;
                        break;
                    case 3:
                        filterSelect.recordType = IVFXEnumFilterFromTypes.SCENE;
                        break;
                    case 4:
                        filterSelect.recordType = IVFXEnumFilterFromTypes.EVENT;
                        break;
                    default:
                        filterSelect.recordType = null;
                        break;
                }

                return filterSelect;
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

    public static List<IVFXFiltersSelect> loadList(IVFXFilters ivfxFilter) {
        List<IVFXFiltersSelect> listFiltersSelect = new ArrayList<>();

        Statement statement = null;
        ResultSet rs = null;
        String sql;

        try {
            statement = Main.mainConnection.createStatement();

            sql = "SELECT * FROM tbl_filters_select WHERE filter_id = " + ivfxFilter.getId();
            rs = statement.executeQuery(sql);
            while (rs.next()) {
                IVFXFiltersSelect filterSelect = new IVFXFiltersSelect();
                filterSelect.id = rs.getInt("id");
                filterSelect.projectId = rs.getInt("project_id");
                filterSelect.filterId = rs.getInt("filter_id");
                filterSelect.objectTypeId = rs.getInt("object_type_id");
                filterSelect.recordTypeId = rs.getInt("record_type_id");
                filterSelect.objectId = rs.getInt("object_id");
                filterSelect.order = rs.getInt("order_filter_select");
                filterSelect.isIncluded = rs.getBoolean("isIncluded");
                filterSelect.isAnd = rs.getBoolean("isAnd");
                filterSelect.ivfxProject = IVFXProjects.load(filterSelect.projectId);
                filterSelect.ivfxFilter = IVFXFilters.load(filterSelect.filterId);

                switch (filterSelect.recordTypeId) {
                    case 1:
                        filterSelect.filterSelectObjectType = IVFXEnumFilterSelectObjectTypes.PERSON;
                        break;
                    case 2:
                        filterSelect.filterSelectObjectType = IVFXEnumFilterSelectObjectTypes.GROUP;
                        break;
                    case 3:
                        filterSelect.filterSelectObjectType = IVFXEnumFilterSelectObjectTypes.OR;
                        break;
                    case 4:
                        filterSelect.filterSelectObjectType = IVFXEnumFilterSelectObjectTypes.AND;
                        break;
                    default:
                        filterSelect.filterSelectObjectType = null;
                        break;
                }

                switch (filterSelect.objectTypeId) {
                    case 1:
                        filterSelect.recordType = IVFXEnumFilterFromTypes.FILE;
                        break;
                    case 2:
                        filterSelect.recordType = IVFXEnumFilterFromTypes.SHOT;
                        break;
                    case 3:
                        filterSelect.recordType = IVFXEnumFilterFromTypes.SCENE;
                        break;
                    case 4:
                        filterSelect.recordType = IVFXEnumFilterFromTypes.EVENT;
                        break;
                    default:
                        filterSelect.recordType = null;
                        break;
                }
                listFiltersSelect.add(filterSelect);
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

        return listFiltersSelect;
    }

    public void save() {
        String sql = "UPDATE tbl_filters_select SET " +
                "project_id = ?, " +
                "filter_id = ?, " +
                "object_type_id = ?, " +
                "record_type_id = ?, " +
                "object_id = ?, " +
                "order_filter_select = ?, " +
                "isIncluded = ?, " +
                "isAnd = ? " +
                "WHERE id = ?";
        try {
            PreparedStatement ps = Main.mainConnection.prepareStatement(sql);
            ps.setInt   (1, this.projectId);
            ps.setInt   (2, this.filterId);
            ps.setInt   (3, this.objectTypeId);
            ps.setInt   (4, this.recordTypeId);
            ps.setInt   (5, this.objectId);
            ps.setInt   (6, this.order);
            ps.setBoolean(7, this.isIncluded);
            ps.setBoolean(8, this.isAnd);
            ps.setInt   (9, this.id);
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void delete() {
        String sql = "DELETE FROM tbl_filters_select WHERE id = ?";
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


    public String getTextForColumn() {
        String text;
        switch (this.filterSelectObjectType) {
            case PERSON:

                IVFXPersons ivfxPersons = IVFXPersons.load(this.objectId, false);
                text = (this.isAnd == true ? "" : "[ИЛИ] ") + "Персонаж «"+ ivfxPersons.getName() + "» " + (this.isIncluded == true ? "содержится " : "не содержится ");
                switch (this.recordType) {
                    case FILE:
                        text = text + "в файле";
                        break;
                    case EVENT:
                        text = text + "в событии";
                        break;
                    case SCENE:
                        text = text + "в сцене";
                        break;
                    case SHOT:
                        text = text + "в плане";
                        break;
                    default:
                        break;
                }
                return text;

            case GROUP:

                IVFXGroups ivfxGroups = IVFXGroups.load(this.objectId);

                text = (this.isAnd == true ? "" : "[ИЛИ] ") + "Группа «"+ ivfxGroups.getName() + "» " + (this.isIncluded == true ? "содержится " : "не содержится ");
                switch (this.recordType) {
                    case FILE:
                        text = text + "в файле";
                        break;
                    case EVENT:
                        text = text + "в событии";
                        break;
                    case SCENE:
                        text = text + "в сцене";
                        break;
                    case SHOT:
                        text = text + "в плане";
                        break;
                    default:
                        break;
                }
                return text;

            case AND:
                return "====== AND ======";
            case OR:
                return "====== OR ======";
            default:
                return "Что-то пошло не так!";
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

    public IVFXEnumFilterSelectObjectTypes getFilterSelectObjectType() {
        return filterSelectObjectType;
    }

    public void setFilterSelectObjectType(IVFXEnumFilterSelectObjectTypes filterSelectObjectType) {
        this.filterSelectObjectType = filterSelectObjectType;
    }

    public IVFXEnumFilterFromTypes getRecordType() {
        return recordType;
    }

    public void setRecordType(IVFXEnumFilterFromTypes recordType) {
        this.recordType = recordType;
    }

    public boolean getIsIncluded() {
        return this.isIncluded;
    }

    public void setIsIncluded(boolean isIncluded) {
        this.isIncluded = isIncluded;
    }

    public boolean getIsAnd() {
        return this.isAnd;
    }

    public void setIsAnd(boolean isAnd) {
        this.isAnd = isAnd;
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

    public int getRecordTypeId() {
        return recordTypeId;
    }

    public void setRecordTypeId(int recordTypeId) {
        this.recordTypeId = recordTypeId;
    }

    public int getObjectTypeId() {
        return objectTypeId;
    }

    public void setObjectTypeId(int objectTypeId) {
        this.objectTypeId = objectTypeId;
    }

    public int getObjectId() {
        return objectId;
    }

    public void setObjectId(int objectId) {
        this.objectId = objectId;
    }

}

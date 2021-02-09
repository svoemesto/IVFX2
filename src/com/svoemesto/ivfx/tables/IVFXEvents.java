package com.svoemesto.ivfx.tables;

import com.svoemesto.ivfx.Main;
import com.svoemesto.ivfx.utils.FFmpeg;
import javafx.scene.control.ProgressBar;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class IVFXEvents {

    private int id;
    private int fileId;
    private int eventTypeId;
    private IVFXEventsTypes ivfxEventsTypes;
    private IVFXFiles ivfxFile;
    private int order=1;
    private String name = "New Event";
    private String eventTypeName = null;
    private String description="";
    private int firstFrameNumber = 0;
    private int lastFrameNumber = 0;

    private List<IVFXShots> shots = new ArrayList<>();
    private List<IVFXEventsPersons> eventPersons = new ArrayList<>();

//TODO ISEQUAL

    public boolean isEqual(IVFXEvents o) {
        return (this.id == o.id &&
                this.fileId == o.fileId &&
                this.eventTypeId == o.eventTypeId &&
                this.order == o.order &&
                this.name.equals(o.name) &&
                this.description.equals(o.description));
    }

// TODO КОНСТРУКТОРЫ

    // пустой конструктор
    public IVFXEvents() {
    }

    public static IVFXEvents getNewDbInstance(IVFXFiles ivfxFile, IVFXEventsTypes ivfxEventsTypes) {
        IVFXEvents ivfxEvents = new IVFXEvents();

        ivfxEvents.ivfxFile = ivfxFile;
        ivfxEvents.fileId = ivfxFile.getId();
        ivfxEvents.ivfxEventsTypes = ivfxEventsTypes;
        ivfxEvents.eventTypeId = ivfxEventsTypes.getId();
        ivfxEvents.eventTypeName = ivfxEventsTypes.getName();

        Statement statement = null;
        ResultSet rs = null;
        String sql;

        try {
            statement = Main.mainConnection.createStatement();

            sql = "SELECT COUNT(*) FROM tbl_events WHERE file_id = " + ivfxEvents.fileId;
            rs = statement.executeQuery(sql);
            if (rs.next()) {
                ivfxEvents.order = rs.getInt(1) + 1;  // индексация столбцов начинается с единицы
            }

            sql = "INSERT INTO tbl_events (" +
                    "file_id, " +
                    "event_type_id, " +
                    "order_event, " +
                    "firstFrameNumber, " +
                    "lastFrameNumber, " +
                    "name, " +
                    "eventTypeName, " +
                    "description) " +
                    "VALUES(" +
                    ivfxEvents.fileId + "," +
                    ivfxEvents.eventTypeId + "," +
                    ivfxEvents.order + "," +
                    ivfxEvents.firstFrameNumber + "," +
                    ivfxEvents.lastFrameNumber + "," +
                    "'" + ivfxEvents.name + "'" + "," +
                    "'" + ivfxEvents.eventTypeName + "'" + "," +
                    "'" + ivfxEvents.description + "'" + ")";

            PreparedStatement ps = Main.mainConnection.prepareStatement(sql);
            ps.executeUpdate(sql, Statement.RETURN_GENERATED_KEYS);
            rs = ps.getGeneratedKeys();
            if (rs.next()) {
                ivfxEvents.id = rs.getInt(1);
                System.out.println("Создана запись для события «" + ivfxEvents.name + "», файл «" + ivfxEvents.ivfxFile.getTitle() + "» с идентификатором " + rs.getInt(1));
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


        return ivfxEvents;
    }

    public static IVFXEvents createEvent(IVFXEventsTypes ivfxEventsTypes, IVFXFiles ivfxFile, IVFXShots ivfxShotsStart, IVFXShots ivfxShotsEnd, boolean withPreview) {

        IVFXEvents ivfxEvent = getNewDbInstance(ivfxFile,ivfxEventsTypes);


        // цикл по планам
        boolean isFindStart = false;
        boolean isFindEnd = false;
        List<IVFXShots> listShots = IVFXShots.loadList(ivfxFile, withPreview);
        List<IVFXEventsPersons> listEventPersons = new ArrayList<>();
        for (IVFXShots shot : listShots) {
            if (shot.getId() == ivfxShotsStart.getId()) {
                isFindStart = true;
            }
            if (shot.getId() == ivfxShotsEnd.getId()) {
                isFindEnd = true;
            }

            if (isFindStart) {

                IVFXEventsShots.getNewDbInstance(ivfxEvent, shot);

                List<IVFXShotsPersons> listShotPersons = IVFXShotsPersons.loadList(shot,withPreview);
                for (IVFXShotsPersons shotPerson : listShotPersons) {
                    boolean personAlreadyAdded = false;
                    for (IVFXEventsPersons eventPerson :  listEventPersons) {
                        if (eventPerson.getPersonId() == shotPerson.getIvfxPerson().getId()) {
                            personAlreadyAdded = true;
                            break;
                        }
                    }

                    if (!personAlreadyAdded) {
                        listEventPersons.add(IVFXEventsPersons.getNewDbInstance(ivfxEvent, shotPerson.getIvfxPerson()));
                    }

                }

                if (isFindEnd) break;
            }

        }


        ivfxEvent.name = ivfxEvent.getPersonsNames();
        ivfxEvent.eventTypeName = ivfxEvent.ivfxEventsTypes.getName();
        ivfxEvent.firstFrameNumber = ivfxEvent.getFirstShot().getFirstFrameNumber();
        ivfxEvent.lastFrameNumber = ivfxEvent.getLastShot().getLastFrameNumber();
        ivfxEvent.save();

        return ivfxEvent;
    }


    public static IVFXEvents load(int id) {

        Statement statement = null;
        ResultSet rs = null;
        String sql;

        try {
            statement = Main.mainConnection.createStatement();

            sql = "SELECT * FROM tbl_events WHERE id = " + id;
            rs = statement.executeQuery(sql);
            if (rs.next()) {
                IVFXEvents event = new IVFXEvents();
                event.id = rs.getInt("id");
                event.fileId = rs.getInt("file_id");
                event.eventTypeId = rs.getInt("event_type_id");
                event.order = rs.getInt("order_event");
                event.name = rs.getString("name");
                event.eventTypeName = rs.getString("eventTypeName");
                event.firstFrameNumber = rs.getInt("firstFrameNumber");
                event.lastFrameNumber = rs.getInt("lastFrameNumber");
                event.description = rs.getString("description");
                event.ivfxFile = IVFXFiles.load(event.fileId);
                event.ivfxEventsTypes = IVFXEventsTypes.load(event.eventTypeId);

                return event;
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

    public static List<IVFXEvents> loadList(IVFXFiles ivfxFiles) {
        return loadList(ivfxFiles, null);
    }
    public static List<IVFXEvents> loadList(IVFXFiles ivfxFiles, ProgressBar progressBar) {

        List<IVFXEvents> listEvents = new ArrayList<>();

        int iProgress = 0;
        Statement statement = null;
        ResultSet rs = null;
        String sql;

        try {
            statement = Main.mainConnection.createStatement();

            sql = "SELECT * FROM tbl_events WHERE file_id = " + ivfxFiles.getId() + " ORDER BY order_event";

            String sqlCnt = "SELECT COUNT(*) AS CNT FROM (" + sql + ") AS tmp";
            ResultSet rsCnt = null;
            rsCnt = statement.executeQuery(sqlCnt);
            rsCnt.next();
            int countRows = rsCnt.getInt("CNT");
            rsCnt.close();

            rs = statement.executeQuery(sql);
            while (rs.next()) {

                if (progressBar != null) progressBar.setProgress((double)++iProgress / countRows);

                IVFXEvents event = new IVFXEvents();
                event.id = rs.getInt("id");
                event.fileId = rs.getInt("file_id");
                event.eventTypeId = rs.getInt("event_type_id");
                event.order = rs.getInt("order_event");
                event.name = rs.getString("name");
                event.eventTypeName = rs.getString("eventTypeName");
                event.firstFrameNumber = rs.getInt("firstFrameNumber");
                event.lastFrameNumber = rs.getInt("lastFrameNumber");
                event.description = rs.getString("description");
                event.ivfxFile = ivfxFiles;
                event.ivfxEventsTypes = IVFXEventsTypes.load(event.eventTypeId);

                listEvents.add(event);
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

        return listEvents;
    }


    public void save() {
        String sql = "UPDATE tbl_events SET " +
                "file_id = ?, " +
                "event_type_id = ?, " +
                "order_event = ?, " +
                "name = ?, " +
                "eventTypeName = ?, " +
                "firstFrameNumber = ?, " +
                "lastFrameNumber = ?, " +
                "description = ? " +
                "WHERE id = ?";
        try {
            PreparedStatement ps = Main.mainConnection.prepareStatement(sql);
            ps.setInt   (1, this.fileId);
            ps.setInt   (2, this.eventTypeId);
            ps.setInt   (3, this.order);
            ps.setString(4, this.name);
            ps.setString(5, this.eventTypeName);
            ps.setInt(6, this.firstFrameNumber);
            ps.setInt(7, this.lastFrameNumber);
            ps.setString(8, this.description);
            ps.setInt   (9, this.id);
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void delete() {
        String sql = "DELETE FROM tbl_events WHERE id = ?";
        try {
            PreparedStatement ps = Main.mainConnection.prepareStatement(sql);
            ps.setInt   (1, this.id);
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public String getPersonsNames() {
        List<IVFXEventsPersons> listEventPersons = IVFXEventsPersons.loadList(this, false);
        String personsNames = "";
        for (IVFXEventsPersons eventPerson : listEventPersons) {
            String personName = eventPerson.getIvfxPerson().getName();
            String personShortName;
            if (personName.contains(" ")) {
                personShortName = personName.substring(0,personName.indexOf(" "));
            } else {
                personShortName = personName;
            }
            personsNames = personsNames + personShortName + ", ";
        }
        if (listEventPersons.size() > 0) {
            personsNames = personsNames.substring(0,personsNames.length()-2);
        }
        return personsNames;
    }

    // возвращает первый план события
    public IVFXShots getFirstShot() {
        List<IVFXEventsShots> listEventShots = IVFXEventsShots.loadList(this);
        if (listEventShots.size() >0 ) {
            return listEventShots.get(0).getIvfxShot();
        } else {
            return null;
        }
    }

    // возвращает последний план события
    public IVFXShots getLastShot() {
        List<IVFXEventsShots> listEventShots = IVFXEventsShots.loadList(this);
        if (listEventShots.size() > 0 ) {
            return listEventShots.get(listEventShots.size()-1).getIvfxShot();
        } else {
            return null;
        }
    }


    public static void setStart(IVFXEvents ivfxEvent, IVFXShots ivfxShots, boolean withPreview) {

        List<IVFXEventsShots> listEventShotsOld = IVFXEventsShots.loadList(ivfxEvent);
        List<IVFXShots> listShots = IVFXShots.loadList(ivfxEvent.ivfxFile, withPreview);
        List<IVFXEventsShots> listEventShotsNew = new ArrayList<>();

        IVFXShots ivfxShotStartOld = ivfxEvent.getFirstShot();
        // если план уже не является первым планом события
        if (ivfxShots.getId() != ivfxShotStartOld.getId()) {
            int positionInLocalList = 0;
            int positionInGlobalList = -1;
            int positionOldFirstInGlobalList = -1;
            for (int i = 0; i < listEventShotsOld.size() ; i++) {    // цикл по имеющимся планам события
                IVFXEventsShots ivfxEventsShots = listEventShotsOld.get(i);

                // если план цикла совпадает с указанным планом - запоминаем его номер в списке и прекращаем цикл
                if (ivfxEventsShots.getShotId() == ivfxShots.getId()) {
                    positionInLocalList = i;
                    break;
                }
            }

            if (positionInLocalList > 0) { // если в списке планов события был найден нужный план
                // нужно сформировать новый список планов начиная с найденного и до конца списка уже имеющихся планов
                int order = 0;
                for (int i = positionInLocalList; i < listEventShotsOld.size(); i++) {
                    order++;
                    IVFXEventsShots ivfxEventsShots = listEventShotsOld.get(i);
                    ivfxEventsShots.setOrder(order);
                    listEventShotsNew.add(ivfxEventsShots);
                }

                IVFXEventsShots.saveList(listEventShotsNew, ivfxEvent);    // сохраняем новый список

            } else { // если в списке планов события не был найден нужный план

                // нужно найти план в списке планов файла
                for (int i = 0; i < listShots.size(); i++) {
                    if (listShots.get(i).getId() == ivfxShots.getId()) {
                        positionInGlobalList = i;
                        break;
                    }
                }

                // если нашли - надо найти позицию "старого" первого плана в списке планов файла
                if (positionInGlobalList >= 0) {
                    for (int i = 0; i < listShots.size(); i++) {
                        if (listShots.get(i).getId() == ivfxShotStartOld.getId()) {
                            positionOldFirstInGlobalList = i;
                            break;
                        }
                    }
                    // если "старый" первый план находится после "нового" первого плана в глобальном списке
                    if (positionOldFirstInGlobalList > positionInGlobalList) {
                        // добавляем в результирующий список сначала все планы от "нового" первого до "старого" первого
                        int order = 0;
                        for (int i = positionInGlobalList; i < positionOldFirstInGlobalList; i++) {
                            order++;
                            IVFXEventsShots ivfxEventsShots = IVFXEventsShots.getNewDbInstance(ivfxEvent,listShots.get(i));
                            ivfxEventsShots.setOrder(order);
                            ivfxEventsShots.save();
                            listEventShotsNew.add(ivfxEventsShots);
                        }
                        // а затем все планы из изначального списка
                        for (IVFXEventsShots ivfxEventsShots : listEventShotsOld) {
                            order++;
                            ivfxEventsShots.setOrder(order);
                            listEventShotsNew.add(ivfxEventsShots);
                        }

                        IVFXEventsShots.saveList(listEventShotsNew, ivfxEvent);    // сохраняем новый список
                    }

                }

            }

        }

    }

    public static void setEnd(IVFXEvents ivfxEvent, IVFXShots ivfxShots, boolean withPreview) {

        List<IVFXEventsShots> listEventShotsOld = IVFXEventsShots.loadList(ivfxEvent);
        List<IVFXShots> listShots = IVFXShots.loadList(ivfxEvent.ivfxFile, withPreview);
        List<IVFXEventsShots> listEventShotsNew = new ArrayList<>();

        IVFXShots ivfxShotEndOld = ivfxEvent.getLastShot();
        // если план уже не является последним планом события
        if (ivfxShots.getId() != ivfxShotEndOld.getId()) {
            int positionInLocalList = -1;
            int positionInGlobalList = -1;
            int positionOldFirstInGlobalList = -1;
            for (int i = listEventShotsOld.size()-1; i >=0  ; i--) {    // цикл по имеющимся планам события в обратном порядке
                IVFXEventsShots ivfxEventsShots = listEventShotsOld.get(i);

                // если план цикла совпадает с указанным планом - запоминаем его номер в списке и прекращаем цикл
                if (ivfxEventsShots.getShotId() == ivfxShots.getId()) {
                    positionInLocalList = i;
                    break;
                }
            }

            if (positionInLocalList >= 0) { // если в списке планов события был найден нужный план
                // нужно сформировать новый список планов начиная с первого и до найденного из списка уже имеющихся планов
                int order = 0;
                for (int i = 0; i <= positionInLocalList; i++) {
                    order++;
                    IVFXEventsShots ivfxEventsShots = listEventShotsOld.get(i);
                    ivfxEventsShots.setOrder(order);
                    listEventShotsNew.add(ivfxEventsShots);
                }

                IVFXEventsShots.saveList(listEventShotsNew, ivfxEvent);    // сохраняем новый список

            } else { // если в списке планов события не был найден нужный план

                // нужно найти план в списке планов файла
                for (int i = 0; i < listShots.size(); i++) {
                    if (listShots.get(i).getId() == ivfxShots.getId()) {
                        positionInGlobalList = i;
                        break;
                    }
                }

                // если нашли - надо найти позицию "старого" последнего плана в списке планов файла
                if (positionInGlobalList >= 0) {
                    for (int i = 0; i < listShots.size(); i++) {
                        if (listShots.get(i).getId() == ivfxShotEndOld.getId()) {
                            positionOldFirstInGlobalList = i;
                            break;
                        }
                    }
                    // если "старый" последний план находится перед "новым" последним планом в глобальном списке
                    if (positionOldFirstInGlobalList < positionInGlobalList) {
                        // добавляем в результирующий список сначала все планы из изначального списка
                        int order = 0;
                        for (IVFXEventsShots ivfxEventsShots : listEventShotsOld) {
                            order++;
                            ivfxEventsShots.setOrder(order);
                            listEventShotsNew.add(ivfxEventsShots);
                        }
                        // а затем от "старого" последнгего по "нового" последнего
                        for (int i = positionOldFirstInGlobalList+1 ; i <= positionInGlobalList; i++) {
                            order++;

                            IVFXEventsShots ivfxEventsShots = IVFXEventsShots.getNewDbInstance(ivfxEvent,listShots.get(i));
                            ivfxEventsShots.setOrder(order);
                            ivfxEventsShots.save();
                            listEventShotsNew.add(ivfxEventsShots);

                        }

                        IVFXEventsShots.saveList(listEventShotsNew, ivfxEvent);    // сохраняем новый список
                    }

                }

            }

        }

        ivfxEvent.name = ivfxEvent.getPersonsNames();
        ivfxEvent.eventTypeName = ivfxEvent.ivfxEventsTypes.getName();
        ivfxEvent.firstFrameNumber = ivfxEvent.getFirstShot().getFirstFrameNumber();
        ivfxEvent.lastFrameNumber = ivfxEvent.getLastShot().getLastFrameNumber();
        ivfxEvent.save();

    }

    public String getFileTitle() {
        return this.ivfxFile.getTitle();
    }

    public int getDuration() {

        int firstFrame = getFirstFrameNumber(); // this.getFrameStart();
        int endFrame = getLastFrameNumber(); // this.getFrameEnd();

        return FFmpeg.getDurationByFrameNumber(endFrame - firstFrame+1, this.ivfxFile.getFrameRate());
    }


    public String getStart() {

        int firstFrame = getFirstFrameNumber(); // this.getFrameStart();
        return FFmpeg.convertDurationToString(FFmpeg.getDurationByFrameNumber(firstFrame-1,this.ivfxFile.getFrameRate()));

    }

    public String getEnd() {

        int endFrame = getLastFrameNumber(); // this.getFrameEnd();
        if (endFrame > 0)
            return FFmpeg.convertDurationToString(FFmpeg.getDurationByFrameNumber(endFrame, this.ivfxFile.getFrameRate()));
        else
            return "";

    }

    // загрузка списка планов для события
    public static List<IVFXShots> loadListShots(IVFXEvents ivfxEvent) {
        List<IVFXShots> listShots = new ArrayList<>();
        List<IVFXEventsShots> listEventsShots = IVFXEventsShots.loadList(ivfxEvent);
        for (IVFXEventsShots eventShot : listEventsShots) {
            listShots.add(eventShot.getIvfxShot());
        }
        return listShots;
    }

    // загрузка списка персонажей для события
    public static List<IVFXPersons> loadListPersons(IVFXEvents ivfxEvent, boolean withPreview) {
        List<IVFXPersons> listPersons = new ArrayList<>();
        List<IVFXEventsPersons> listEventsPersons = IVFXEventsPersons.loadList(ivfxEvent,withPreview);
        for (IVFXEventsPersons eventPerson : listEventsPersons) {
            listPersons.add(eventPerson.getIvfxPerson());
        }
        return listPersons;
    }


    public void setStart(IVFXShots ivfxShots, boolean withPreview) {

        IVFXEvents ivfxEvent = this;
        List<IVFXEventsShots> listEventShotsOld = IVFXEventsShots.loadList(ivfxEvent);
        List<IVFXShots> listShots = IVFXShots.loadList(ivfxEvent.ivfxFile, withPreview);
        List<IVFXEventsShots> listEventShotsNew = new ArrayList<>();

        IVFXShots ivfxShotStartOld = ivfxEvent.getFirstShot();
        // если план уже не является первым планом события
        if (ivfxShots.getId() != ivfxShotStartOld.getId()) {
            int positionInLocalList = 0;
            int positionInGlobalList = -1;
            int positionOldFirstInGlobalList = -1;
            for (int i = 0; i < listEventShotsOld.size() ; i++) {    // цикл по имеющимся планам события
                IVFXEventsShots ivfxEventsShots = listEventShotsOld.get(i);

                // если план цикла совпадает с указанным планом - запоминаем его номер в списке и прекращаем цикл
                if (ivfxEventsShots.getShotId() == ivfxShots.getId()) {
                    positionInLocalList = i;
                    break;
                }
            }

            if (positionInLocalList > 0) { // если в списке планов события был найден нужный план
                // нужно сформировать новый список планов начиная с найденного и до конца списка уже имеющихся планов
                int order = 0;
                for (int i = positionInLocalList; i < listEventShotsOld.size(); i++) {
                    order++;
                    IVFXEventsShots ivfxEventsShots = listEventShotsOld.get(i);
                    ivfxEventsShots.setOrder(order);
                    ivfxEventsShots.save();
                    listEventShotsNew.add(ivfxEventsShots);
                }

                IVFXEventsShots.saveList(listEventShotsNew, ivfxEvent);    // сохраняем новый список

            } else { // если в списке планов события не был найден нужный план

                // нужно найти план в списке планов файла
                for (int i = 0; i < listShots.size(); i++) {
                    if (listShots.get(i).getId() == ivfxShots.getId()) {
                        positionInGlobalList = i;
                        break;
                    }
                }

                // если нашли - надо найти позицию "старого" первого плана в списке планов файла
                if (positionInGlobalList >= 0) {
                    for (int i = 0; i < listShots.size(); i++) {
                        if (listShots.get(i).getId() == ivfxShotStartOld.getId()) {
                            positionOldFirstInGlobalList = i;
                            break;
                        }
                    }
                    // если "старый" первый план находится после "нового" первого плана в глобальном списке
                    if (positionOldFirstInGlobalList > positionInGlobalList) {
                        // добавляем в результирующий список сначала все планы от "нового" первого до "старого" первого
                        int order = 0;
                        for (int i = positionInGlobalList; i < positionOldFirstInGlobalList; i++) {
                            order++;
                            IVFXEventsShots ivfxEventsShots = IVFXEventsShots.getNewDbInstance(ivfxEvent, listShots.get(i));
                            ivfxEventsShots.setOrder(order);
                            ivfxEventsShots.save();
                            listEventShotsNew.add(ivfxEventsShots);
                        }
                        // а затем все планы из изначального списка
                        for (IVFXEventsShots ivfxEventsShots : listEventShotsOld) {
                            order++;
                            ivfxEventsShots.setOrder(order);
                            ivfxEventsShots.save();
                            listEventShotsNew.add(ivfxEventsShots);
                        }

                        IVFXEventsShots.saveList(listEventShotsNew, ivfxEvent);    // сохраняем новый список
                    }

                }

            }

        }

        ivfxEvent.name = ivfxEvent.getPersonsNames();
        ivfxEvent.eventTypeName = ivfxEvent.ivfxEventsTypes.getName();
        ivfxEvent.firstFrameNumber = ivfxEvent.getFirstShot().getFirstFrameNumber();
        ivfxEvent.lastFrameNumber = ivfxEvent.getLastShot().getLastFrameNumber();
        ivfxEvent.save();

    }

    public void setEnd(IVFXShots ivfxShots, boolean withPreview) {

        IVFXEvents ivfxEvent = this;
        List<IVFXEventsShots> listEventShotsOld = IVFXEventsShots.loadList(ivfxEvent);
        List<IVFXShots> listShots = IVFXShots.loadList(ivfxEvent.ivfxFile, withPreview);
        List<IVFXEventsShots> listEventShotsNew = new ArrayList<>();

        IVFXShots ivfxShotEndOld = ivfxEvent.getLastShot();
        // если план уже не является последним планом события
        if (ivfxShots.getId() != ivfxShotEndOld.getId()) {
            int positionInLocalList = -1;
            int positionInGlobalList = -1;
            int positionOldFirstInGlobalList = -1;
            for (int i = listEventShotsOld.size()-1; i >=0  ; i--) {    // цикл по имеющимся планам события в обратном порядке
                IVFXEventsShots ivfxEventsShots = listEventShotsOld.get(i);

                // если план цикла совпадает с указанным планом - запоминаем его номер в списке и прекращаем цикл
                if (ivfxEventsShots.getShotId() == ivfxShots.getId()) {
                    positionInLocalList = i;
                    break;
                }
            }

            if (positionInLocalList >= 0) { // если в списке планов события был найден нужный план
                // нужно сформировать новый список планов начиная с первого и до найденного из списка уже имеющихся планов
                int order = 0;
                for (int i = 0; i <= positionInLocalList; i++) {
                    order++;
                    IVFXEventsShots ivfxEventsShots = listEventShotsOld.get(i);
                    ivfxEventsShots.setOrder(order);
                    listEventShotsNew.add(ivfxEventsShots);
                }

                IVFXEventsShots.saveList(listEventShotsNew, ivfxEvent);    // сохраняем новый список

            } else { // если в списке планов события не был найден нужный план

                // нужно найти план в списке планов файла
                for (int i = 0; i < listShots.size(); i++) {
                    if (listShots.get(i).getId() == ivfxShots.getId()) {
                        positionInGlobalList = i;
                        break;
                    }
                }

                // если нашли - надо найти позицию "старого" последнего плана в списке планов файла
                if (positionInGlobalList >= 0) {
                    for (int i = 0; i < listShots.size(); i++) {
                        if (listShots.get(i).getId() == ivfxShotEndOld.getId()) {
                            positionOldFirstInGlobalList = i;
                            break;
                        }
                    }
                    // если "старый" последний план находится перед "новым" последним планом в глобальном списке
                    if (positionOldFirstInGlobalList < positionInGlobalList) {
                        // добавляем в результирующий список сначала все планы из изначального списка
                        int order = 0;
                        for (IVFXEventsShots ivfxEventsShots : listEventShotsOld) {
                            order++;
                            ivfxEventsShots.setOrder(order);
                            listEventShotsNew.add(ivfxEventsShots);
                        }
                        // а затем от "старого" последнгего по "нового" последнего
                        for (int i = positionOldFirstInGlobalList+1 ; i <= positionInGlobalList; i++) {
                            order++;
                            IVFXEventsShots ivfxEventsShots = IVFXEventsShots.getNewDbInstance(ivfxEvent, listShots.get(i));
                            ivfxEventsShots.setOrder(order);
                            ivfxEventsShots.save();

                            listEventShotsNew.add(ivfxEventsShots);
                        }

                        IVFXEventsShots.saveList(listEventShotsNew, ivfxEvent);    // сохраняем новый список
                    }

                }

            }

        }

        ivfxEvent.name = ivfxEvent.getPersonsNames();
        ivfxEvent.eventTypeName = ivfxEvent.ivfxEventsTypes.getName();
        ivfxEvent.firstFrameNumber = ivfxEvent.getFirstShot().getFirstFrameNumber();
        ivfxEvent.lastFrameNumber = ivfxEvent.getLastShot().getLastFrameNumber();
        ivfxEvent.save();

    }





// TODO GETTERS SETTERS
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getFileId() {
        return fileId;
    }

    public void setFileId(int fileId) {
        this.fileId = fileId;
    }

    public int getEventTypeId() {
        return eventTypeId;
    }

    public void setEventTypeId(int eventTypeId) {
        this.eventTypeId = eventTypeId;
    }

    public IVFXEventsTypes getIvfxEventsTypes() {
        return ivfxEventsTypes;
    }

    public void setIvfxEventsTypes(IVFXEventsTypes ivfxEventsTypes) {
        this.ivfxEventsTypes = ivfxEventsTypes;
    }

    public IVFXFiles getIvfxFile() {
        return ivfxFile;
    }

    public void setIvfxFile(IVFXFiles ivfxFile) {
        this.ivfxFile = ivfxFile;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
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

    public int getFirstFrameNumber() {
        if (firstFrameNumber == 0) {
            firstFrameNumber = getFirstShot().getFirstFrameNumber();
            save();
        }
        return firstFrameNumber;
    }

    public void setFirstFrameNumber(int firstFrameNumber) {
        this.firstFrameNumber = firstFrameNumber;
    }

    public int getLastFrameNumber() {
        if (lastFrameNumber == 0) {
            lastFrameNumber = getLastShot().getLastFrameNumber();
            save();
        }
        return lastFrameNumber;
    }

    public void setLastFrameNumber(int lastFrameNumber) {
        this.lastFrameNumber = lastFrameNumber;
    }

    public String getEventTypeName() {
        if (eventTypeName == null) {
            eventTypeName = this.ivfxEventsTypes.getName();
            save();
        }
        return eventTypeName;
    }

    public void setEventTypeName(String eventTypeName) {
        this.eventTypeName = eventTypeName;
    }

    public List<IVFXShots> getShots() {

        return shots;

    }

    public void setShots(List<IVFXShots> shots) {
        this.shots = shots;
    }

    public List<IVFXEventsPersons> getEventPersons() {
        return eventPersons;
    }

    public void setEventPersons(List<IVFXEventsPersons> eventPersons) {
        this.eventPersons = eventPersons;
    }
}

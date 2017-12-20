package database;

import entities.EventType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utils.SQLiteConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Evgeniy Slobozheniuk on 05.12.17.
 */
public class EventTypeDAO {
    private static final Logger log = LogManager.getLogger(EventTypeDAO.class);

    private SQLiteConnection sqLiteConnection;
    private final String selectEventType = "SELECT EVENTTYPE_ID, EVENTTYPE_NAME FROM EVENTTYPE ";
    private final String insertEventType = "INSERT INTO EVENTTYPE VALUES(?,?)";
    private final String deleteEventType = "DELETE FROM EVENTTYPE ";

    public EventTypeDAO(SQLiteConnection sqLiteConnection) {
        this.sqLiteConnection = sqLiteConnection;
    }

    public void InsertOrUpdate(EventType item) {
        if (!IsInDatabase(item)) {
            Insert(item);
        }
    }

    private void Update(EventType item) {
        //do nothing for now
    }

    private void Insert(EventType item) {
        log.debug("Inserting " + item.getName() + " to EVENTTYPE table");
        try (Connection con = sqLiteConnection.getConnection()) {
            PreparedStatement preparedStatement = con.prepareStatement(insertEventType);
            preparedStatement.setString(1, item.getId());
            preparedStatement.setString(2, item.getName());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<EventType> SelectAll() {
        return Select("");
    }

    public List<EventType> Select(String where) {
        List<EventType> list = new ArrayList<>();
        try (Connection con = sqLiteConnection.getConnection()){
            PreparedStatement selectStatement = con.prepareStatement(selectEventType + where);
            try (ResultSet resultSet = selectStatement.executeQuery()) {
                while(resultSet.next()) {
                    EventType eventType = new EventType();
                    eventType.setId(resultSet.getString("EventType_ID"));
                    eventType.setName(resultSet.getString("EventType_Name"));
                    list.add(eventType);
                }
                return list;
            }
        } catch (SQLException e1) {
            e1.printStackTrace();
        }
        return list;
    }

    public void DeleteAll() {
        Delete("");
    }

    public void Delete(String where) {
        try (Connection con = sqLiteConnection.getConnection()) {
            PreparedStatement deleteStatement = con.prepareStatement(deleteEventType + where);
            System.out.println(deleteEventType + where);
            deleteStatement.executeUpdate();
        } catch (SQLException e1) {
            e1.printStackTrace();
        }
    }

    public Boolean IsInDatabase(EventType item) {
        String where = "WHERE EventType_ID = \"" + item.getId() + "\" AND EventType_Name = \"" + item.getName() + "\"";
        if (!Select(where).isEmpty()) {
            log.debug(item.getName() + " is already in database");
            return true;
        }
        else {
            log.debug("There is no " + item.getName() + " in database");
            return false;
        }
    }
}

package database;

import entities.Event;
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
 * Created by Evgeniy Slobozheniuk on 15.12.17.
 */
public class EventDAO {
    private static final Logger log = LogManager.getLogger(EventDAO.class);

    private SQLiteConnection sqLiteConnection;
    private final String selectEventType = "SELECT EVENT_ID, EVENT_NAME, EVENT_COUNTRY_CODE, EVENT_TIMEZONE, EVENT_OPENDATE FROM EVENT ";
    private final String insertEventType = "INSERT INTO EVENT VALUES(?,?,?,?,?)";
    private final String deleteEventType = "DELETE FROM EVENT ";

    public EventDAO(SQLiteConnection sqLiteConnection) {
        this.sqLiteConnection = sqLiteConnection;
    }

    public void InsertOrUpdate(Event item) {
        if (!IsInDatabase(item)) {
            Insert(item);
        }
    }

    private void Update(Event item) {
        //do nothing for now
    }

    private void Insert(Event item) {
        log.info("Inserting " + item.getName() + " to EVENT table");
        try (Connection con = sqLiteConnection.getConnection()) {
            PreparedStatement preparedStatement = con.prepareStatement(insertEventType);
            preparedStatement.setString(1, item.getId());
            preparedStatement.setString(2, item.getName());
            preparedStatement.setString(3, item.getCountryCode());
            preparedStatement.setString(4, item.getTimezone());
            preparedStatement.setTimestamp(5, new java.sql.Timestamp(item.getOpenDate().getTime()));
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Event> SelectAll() {
        return Select("");
    }

    public List<Event> Select(String where) {
        List<Event> list = new ArrayList<>();
        try (Connection con = sqLiteConnection.getConnection()){
            PreparedStatement selectStatement = con.prepareStatement(selectEventType + where);
            try (ResultSet resultSet = selectStatement.executeQuery()) {
                while(resultSet.next()) {
                    Event event = new Event();
                    event.setId(resultSet.getString("Event_ID"));
                    event.setName(resultSet.getString("Event_Name"));
                    event.setCountryCode(resultSet.getString("Event_Country_Code"));
                    event.setTimezone(resultSet.getString("Event_Timezone"));
                    event.setOpenDate(resultSet.getDate("Event_Opendate"));
                    list.add(event);
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

    public Boolean IsInDatabase(Event item) {
        String where = "WHERE Event_ID = \"" + item.getId() + "\" AND Event_Name = \"" + item.getName() + "\"";
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

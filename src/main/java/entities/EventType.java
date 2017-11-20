package entities;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by Evgeniy Slobozheniuk on 24.11.17.
 */
public class EventType {
    private static final Logger log = LogManager.getLogger(EventType.class);

    private String id;
    private String name;

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if(!EventType.class.isAssignableFrom(obj.getClass())) {
            return false;
        }
        return super.equals(obj);
    }

    public EventType() {
    }

    public EventType(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public EventType(ResultSet item) throws SQLException {
        this.id = item.getString("EventType_ID");
        this.name = item.getString("EventType_Name");
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String toString() {
        return "{" + "" + "id=" + getId() + "," + "name=" + getName() + "}";
    }
}

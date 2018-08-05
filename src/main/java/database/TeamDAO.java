package database;

import entities.RunnerCatalog;
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
 * Created by Evgeniy Slobozheniuk on 25.02.2018.
 */
public class TeamDAO {
    private static final Logger log = LogManager.getLogger(TeamDAO.class);

    private SQLiteConnection sqLiteConnection;
    private final String selectTeam = "SELECT TEAM_ID, TEAM_NAME FROM TEAM ";
    private final String insertTeam = "INSERT INTO TEAM VALUES(?,?)";
    private final String deleteTeam = "DELETE FROM TEAM ";

    public TeamDAO(SQLiteConnection sqLiteConnection) {
        this.sqLiteConnection = sqLiteConnection;
    }

    public void InsertOrUpdate(RunnerCatalog item) {
        if (!IsInDatabase(item)) {
            Insert(item);
        }
    }

    private void Update(RunnerCatalog item) {
        //do nothing for now
    }

    private void Insert(RunnerCatalog item) {
        log.info("Inserting " + item.getRunnerName() + " to TEAM table");
        try (Connection con = sqLiteConnection.getConnection()) {
            PreparedStatement preparedStatement = con.prepareStatement(insertTeam);
            preparedStatement.setInt(1, item.getSelectionId().intValue());
            preparedStatement.setString(2, item.getRunnerName());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<RunnerCatalog> SelectAll() {
        return Select("");
    }

    public List<RunnerCatalog> Select(String where) {
        List<RunnerCatalog> list = new ArrayList<>();
        try (Connection con = sqLiteConnection.getConnection()){
            PreparedStatement selectStatement = con.prepareStatement(selectTeam + where);
            try (ResultSet resultSet = selectStatement.executeQuery()) {
                while(resultSet.next()) {
                    RunnerCatalog runner = new RunnerCatalog();
                    runner.setSelectionId((long) resultSet.getInt("Team_ID"));
                    runner.setRunnerName(resultSet.getString("Team_Name"));
                    list.add(runner);
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
            PreparedStatement deleteStatement = con.prepareStatement(deleteTeam + where);
            System.out.println(deleteTeam + where);
            deleteStatement.executeUpdate();
        } catch (SQLException e1) {
            e1.printStackTrace();
        }
    }

    public Boolean IsInDatabase(RunnerCatalog item) {
        String where = "WHERE Team_ID = \"" + item.getSelectionId() + "\" AND Team_Name = \"" + item.getRunnerName() + "\"";
        if (!Select(where).isEmpty()) {
            log.debug("\"" + item.getRunnerName() + "\" is already in database");
            return true;
        }
        else {
            log.debug("There is no \"" + item.getRunnerName() + "\" in database");
            return false;
        }
    }
}

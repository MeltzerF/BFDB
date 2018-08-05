package database;

import entities.Event;
import entities.MarketCatalogue;
import entities.RunnerCatalog;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utils.SQLiteConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Evgeniy Slobozheniuk on 13.01.2018.
 */
public class MarketCatalogueDAO {
    private static final Logger log = LogManager.getLogger(MarketCatalogueDAO.class);

    private SQLiteConnection sqLiteConnection;
    private final String selectMarketCatalogue = "SELECT * FROM MARKETCATALOGUE ";
    private final String selectByDateMarketCatalogue = "SELECT Market_Id FROM MARKETCATALOGUE WHERE Event_ID IN (SELECT Event_ID FROM EVENT WHERE Event_Opendate > ?)";
    private final String insertMarketCatalogue = "INSERT INTO MARKETCATALOGUE VALUES(?,?,?,?,?)";
    private final String updateMarketCatalogue = "UPDATE MARKETCATALOGUE SET " +
                                                 "Market_Name = ?, " +
                                                 "Team_Home_ID = ?, " +
                                                 "Team_Guest_ID = ?, " +
                                                 "WHERE Market_ID = ? " +
                                                 "AND Event_ID = ?";
    private final String deleteMarketCatalogue = "DELETE FROM MARKETCATALOGUE ";

    public MarketCatalogueDAO(SQLiteConnection sqLiteConnection) {
        this.sqLiteConnection = sqLiteConnection;
    }

    public void InsertOrUpdate(MarketCatalogue item) {
        if (!IsInDatabase(item)) {
            Insert(item);
            ProcessTeams(item, sqLiteConnection);
        }
    }

    private void ProcessTeams(MarketCatalogue item, SQLiteConnection sqLiteConnection) {
        TeamDAO team = new TeamDAO(sqLiteConnection);
        team.InsertOrUpdate(item.getRunners().get(0));
        team.InsertOrUpdate(item.getRunners().get(1));
        team.InsertOrUpdate(item.getRunners().get(2));
    }

    private void Update(MarketCatalogue item) {
        log.warn("Updating " + item.getMarketId() + " in MARKETCATALOGUE table");
        try (Connection con = sqLiteConnection.getConnection()) {
            PreparedStatement preparedStatement = con.prepareStatement(updateMarketCatalogue);
            preparedStatement.setString(1, item.getMarketName());
            preparedStatement.setInt(2, item.getRunners().get(0).getSelectionId().intValue());
            preparedStatement.setInt(3, item.getRunners().get(1).getSelectionId().intValue());
            preparedStatement.setString(4, item.getMarketId());
            preparedStatement.setString(5, item.getEvent().getId());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void Insert(MarketCatalogue item) {
        log.debug("Inserting market" + item.getMarketName() + " of " +
                item.getRunners().get(0).getRunnerName() + " vs " +
                item.getRunners().get(1).getRunnerName() + " to MARKETCATALOGUE table");
        try (Connection con = sqLiteConnection.getConnection()) {
            PreparedStatement preparedStatement = con.prepareStatement(insertMarketCatalogue);
            preparedStatement.setString(1, item.getMarketId());
            preparedStatement.setString(2, item.getEvent().getId());
            preparedStatement.setString(3, item.getMarketName());
            preparedStatement.setInt(4, item.getRunners().get(0).getSelectionId().intValue());
            preparedStatement.setInt(5, item.getRunners().get(1).getSelectionId().intValue());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<MarketCatalogue> SelectAll() {
        return Select("");
    }

    public List<MarketCatalogue> Select(String where) {
        List<MarketCatalogue> list = new ArrayList<>();
        try (Connection con = sqLiteConnection.getConnection()){
            PreparedStatement selectStatement = con.prepareStatement(selectMarketCatalogue + where);
            try (ResultSet resultSet = selectStatement.executeQuery()) {
                while(resultSet.next()) {
                    MarketCatalogue marketCatalogue = new MarketCatalogue();
                    marketCatalogue.setMarketId(resultSet.getString("Market_ID"));

                    Event marketEvent = new Event();
                    marketEvent.setId(resultSet.getString("Event_ID"));
                    marketCatalogue.setEvent(marketEvent);

                    marketCatalogue.setMarketName(resultSet.getString("Market_Name"));

                    List<RunnerCatalog> runnerCatalogList = new ArrayList<>();
                    RunnerCatalog homeRunner = new RunnerCatalog();
                    RunnerCatalog guestRunner = new RunnerCatalog();
                    homeRunner.setSelectionId((long) resultSet.getInt("Team_Home_ID"));
                    guestRunner.setSelectionId((long) resultSet.getInt("Team_Guest_ID"));
                    runnerCatalogList.add(homeRunner);
                    runnerCatalogList.add(guestRunner);
                    marketCatalogue.setRunners(runnerCatalogList);

                    list.add(marketCatalogue);
                }
                return list;
            }
        } catch (SQLException e1) {
            e1.printStackTrace();
        }
        return list;
    }

    public List<String> Select(Date date) { //TODO: Rewrite all Selects using specific date to one-step solution
        List<String> list = new ArrayList<>();
        try (Connection con = sqLiteConnection.getConnection()){
            PreparedStatement selectStatement = con.prepareStatement(selectByDateMarketCatalogue);
            selectStatement.setTimestamp(1, new java.sql.Timestamp(date.getTime()));
            try (ResultSet resultSet = selectStatement.executeQuery()) {
                while(resultSet.next()) {
                    list.add(resultSet.getString("Market_ID"));
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
            PreparedStatement deleteStatement = con.prepareStatement(deleteMarketCatalogue + where);
            System.out.println(deleteMarketCatalogue + where);
            deleteStatement.executeUpdate();
        } catch (SQLException e1) {
            e1.printStackTrace();
        }
    }

    public Boolean IsInDatabase(MarketCatalogue item) {
        String where = "WHERE Market_ID = \"" + item.getMarketId() +
                "\" AND Event_ID = \"" + item.getEvent().getId() +
                "\" AND Market_Name = \"" + item.getMarketName() +
                "\" AND Team_Home_ID = " + item.getRunners().get(0).getSelectionId() +
                " AND Team_Guest_ID = " + item.getRunners().get(1).getSelectionId();
        if (!Select(where).isEmpty()) {
            log.debug(item.getMarketId() + " is already in database");
            return true;
        }
        else {
            if (Select("WHERE Market_ID = \"" + item.getMarketId() + "\" AND Event_ID = \"" + item.getEvent().getId() + "\"").isEmpty()) {
                log.debug("There is no " + item.getMarketName() + " in database");
                return false;
            } else {
                Update(item);
                return true;
            }
        }
    }
}

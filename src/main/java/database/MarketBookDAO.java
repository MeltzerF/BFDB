package database;

import com.mysql.jdbc.Statement;
import entities.MarketBook;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utils.SQLiteConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Evgeniy Slobozheniuk on 04.03.2018.
 */
public class MarketBookDAO {
    private static final Logger log = LogManager.getLogger(MarketBookDAO.class);

    private SQLiteConnection connection;
    private final String selectMarketBook = "SELECT * FROM MARKETBOOK ";
    private final String insertMarketBook = "INSERT INTO MARKETBOOK(MARKET_ID, IS_DELAYED, MARKET_STATUS, BET_DELAY, IS_INPLAY, TOTAL_MATCHED, TOTAL_AVAILABLE, UPDATE_TIME, TEAM_HOME_STATUS, TEAM_GUEST_STATUS, TEAM_DRAW_STATUS) VALUES(?,?,?,?,?,?,?,?,?,?,?)";
    private final String deleteMarketBook = "DELETE FROM MARKETBOOK ";

    public MarketBookDAO(SQLiteConnection connection) {
        this.connection = connection;
    }

    public void InsertOrUpdate(MarketBook item) {
        if (!IsInDatabase(item)) {
            Insert(item);
        }
    }

    private void InsertPrices(MarketBook item, ResultSet rs) throws SQLException {
        long key = -1L;
        if (rs != null && rs.next()) {
            key = rs.getLong(1);
        }
        MarketPriceSnapDAO marketPriceSnapDAO = new MarketPriceSnapDAO(connection);
        marketPriceSnapDAO.Insert(item, key);
    }

    private void Update(MarketBook item) {
        //This entry is not updatable
    }

    private void Insert(MarketBook item) {
        log.debug("Inserting book " + item.getMarketId() + " to MARKETBOOK table");
        try (Connection con = connection.getConnection()) {
            PreparedStatement preparedStatement = con.prepareStatement(insertMarketBook, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, item.getMarketId());
            preparedStatement.setBoolean(2, item.getIsMarketDataDelayed());
            preparedStatement.setString(3, item.getStatus());
            preparedStatement.setInt(4, item.getBetDelay());
            preparedStatement.setBoolean(5, item.getInplay());
            preparedStatement.setDouble(6, item.getTotalMatched());
            preparedStatement.setDouble(7, item.getTotalAvailable());
            preparedStatement.setTimestamp(8, new java.sql.Timestamp(Calendar.getInstance().getTimeInMillis()));
            preparedStatement.setString(9, item.getRunners().get(0).getStatus());
            preparedStatement.setString(10, item.getRunners().get(1).getStatus());
            preparedStatement.setString(11, item.getRunners().get(2).getStatus());
            if (item.getInplay() && (item.getHomeTeamScore() != -1) && (item.getGuestTeamScore() != -1)) {
                preparedStatement.setInt(12, item.getHomeTeamScore());
                preparedStatement.setInt(13, item.getHomeTeamScore());
            }
            preparedStatement.executeUpdate();
            ResultSet rs = preparedStatement.getGeneratedKeys();
            InsertPrices(item, rs);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<MarketBook> SelectAll() {
        return Select("");
    }

    public List<MarketBook> Select(String where) {
        List<MarketBook> list = new ArrayList<>();
        try (Connection con = connection.getConnection()){
            PreparedStatement selectStatement = con.prepareStatement(selectMarketBook + where);
            try (ResultSet resultSet = selectStatement.executeQuery()) {
                while(resultSet.next()) {
                    MarketBook marketBook = new MarketBook();
                    marketBook.setMarketId(resultSet.getString("Market_ID"));
                    list.add(marketBook);
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
        try (Connection con = connection.getConnection()) {
            PreparedStatement deleteStatement = con.prepareStatement(deleteMarketBook + where);
            System.out.println(deleteMarketBook + where);
            deleteStatement.executeUpdate();
        } catch (SQLException e1) {
            e1.printStackTrace();
        }
    }

    public Boolean IsInDatabase(MarketBook item) {
        String where = "WHERE Market_ID = " + "'" + item.getMarketId() + "'" +
                " AND Is_Delayed = " + item.getIsMarketDataDelayed() +
                " AND Market_Status = " + "'" + item.getStatus() + "'" +
                " AND Bet_Delay = " + item.getBetDelay() +
                " AND Is_Inplay = " + item.getInplay() +
                " AND Total_Matched = " + item.getTotalMatched() +
                " AND Total_Available = " + item.getTotalAvailable() +
                " AND Team_Home_Status = " + "'" + item.getRunners().get(0).getStatus() + "'" +
                " AND Team_Guest_Status = " + "'" + item.getRunners().get(1).getStatus() + "'" +
                " AND Team_Draw_Status = " + "'" + item.getRunners().get(2).getStatus() + "'";
        if (!Select(where).isEmpty()) {
            return true;
        } else {
            return false;
        }
    }

}

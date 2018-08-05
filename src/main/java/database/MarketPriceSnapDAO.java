package database;

import entities.MarketBook;
import entities.PriceSize;
import entities.Runner;
import enums.Side;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utils.SQLiteConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Created by Evgeniy Slobozheniuk on 05.03.2018.
 */
public class MarketPriceSnapDAO {
    private static final Logger log = LogManager.getLogger(MarketPriceSnapDAO.class);

    private SQLiteConnection connection;
    private final String selectMarketPriceSnap = "SELECT * FROM MARKET_PRICE_SNAP ";
    private final String insertMarketPriceSnap = "INSERT INTO MARKET_PRICE_SNAP VALUES(?,?,?,?,?,?)";
    private final String deleteMarketPriceSnap = "DELETE FROM MARKET_PRICE_SNAP ";

    public MarketPriceSnapDAO(SQLiteConnection connection) { this.connection = connection; }

    public void Insert(MarketBook item, Long marketBookId) {
        for (Runner runner : item.getRunners()) {
            log.debug("Inserting prices for runner ID = " + runner.getSelectionId() + " to MARKET_PRICE_SNAP table");
            for (int i = 0; i < runner.getEx().getAvailableToBack().size(); i++) {
                Insert(marketBookId, runner.getSelectionId().intValue(), Side.BACK, runner.getEx().getAvailableToBack().get(i), i);
            }
            for (int i = 0; i < runner.getEx().getAvailableToLay().size(); i++) {
                Insert(marketBookId, runner.getSelectionId().intValue(), Side.LAY, runner.getEx().getAvailableToLay().get(i), i);
            }
        }
    }

    private void Insert(Long marketBookId, int teamId, Side side, PriceSize priceSize, int depth) {
        try (Connection con = connection.getConnection()) {
            PreparedStatement preparedStatement = con.prepareStatement(insertMarketPriceSnap);
            preparedStatement.setLong(1, marketBookId);
            preparedStatement.setInt(2, teamId);
            preparedStatement.setString(3, side.name());
            preparedStatement.setDouble(4, priceSize.getPrice());
            preparedStatement.setDouble(5, priceSize.getSize());
            preparedStatement.setInt(6, depth);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

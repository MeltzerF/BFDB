package utils;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.mchange.v2.c3p0.jboss.C3P0PooledDataSource;
import org.apache.commons.configuration2.Configuration;

import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Created by Evgeniy Slobozheniuk on 29.11.17.
 */
public class SQLiteConnection {
    private final AppConfig config;
    private static SQLiteConnection dataSource;
    private static ComboPooledDataSource comboPooledDataSource;

    private SQLiteConnection(AppConfig config) {
        this.config = config;
        try {
            this.comboPooledDataSource = new ComboPooledDataSource();
            this.comboPooledDataSource.setDriverClass("com.mysql.jdbc.Driver");
            this.comboPooledDataSource.setJdbcUrl(config.dbConnectionString);
            this.comboPooledDataSource.setUser(config.dbUser);
            this.comboPooledDataSource.setPassword(config.password);
        } catch (PropertyVetoException e) {
            e.printStackTrace();
        }
    }

    public static SQLiteConnection getInstance(AppConfig config) {
        if (dataSource == null) {
            dataSource = new SQLiteConnection(config);
        }
        return dataSource;
    }

    public Connection getConnection() {
        Connection con = null;
        try {
            con = comboPooledDataSource.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return con;
    }
}

package dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public abstract class BaseDAO {
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    protected Connection getConnection() throws SQLException {
        return DBConnection.getConnection();
    }

    protected void closeResources(Connection conn, PreparedStatement stmt, ResultSet rs) {
        try {
            if (rs != null) rs.close();
            if (stmt != null) stmt.close();
            if (conn != null) conn.close();
        } catch (SQLException e) {
            logger.error("Error closing database resources", e);
        }
    }

    protected void handleException(String message, Exception e) {
        logger.error(message, e);
    }
}
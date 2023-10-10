package Connections;

import java.io.IOException;
import java.sql.*;

import Entities.FTPUser;
import Utility.PropertiesLoader;

public class DBConnection {
    private final static PropertiesLoader properties;

    private final static String URL;
    private final static String GUEST_USERNAME;
    private final static String GUEST_PASSWORD;

    // get database info for guest account / database url and access it
    static {
        properties = new PropertiesLoader("database");
        GUEST_USERNAME = properties.getProperty("db.guest_username");
        GUEST_PASSWORD = properties.getProperty("db.guest_password");

        URL = String.format(
                "jdbc:mysql://%s:%s/%s",
                properties.getProperty("db.host"),
                properties.getProperty("db.port"),
                "users"
        );

        try {
            connectGuest();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // the Connection the database currently uses
    private static Connection connection;

    /**
     * Establishes a connection with the mysql database using the provided login info. Use with no parameters to login as guest.
     * @throws SQLException Sql error, should only appear if we do something wrong
     */
    public static void connectGuest() throws SQLException {
        connect(GUEST_USERNAME, GUEST_PASSWORD);
    }

    /**
     * Establishes a connection with the mysql database using the provided login info. Use with no parameters to login as guest.
     * @param USERNAME the username of the account
     * @param PASSWORD the username of the account
     * @throws SQLException Sql error, should only appear if we do something wrong
     */
    public static void connect(String USERNAME, String PASSWORD) throws SQLException {
        connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
    }

    /**
     * Just an example of how to use queries, so I don't forget later
     * @deprecated so we know to get rid of it after we make an actual implementation for reference
     *
     * @param userid the username for the ftp connection
     * @param passwd the password for the ftp connection
     * @return boolean
     * @throws SQLException Sql error, should only appear if we do something wrong
     */
    public static boolean exampleOfUsingQueries(String userid, String passwd) throws SQLException, IOException {

        // sql query to check if user exists
        String queryUser = "SELECT * FROM ftpuser WHERE userid = ? AND passwd = ?";
        ResultSet results = SQLQuery(queryUser, userid, passwd);

        if (results.next()) { // if we get at least one result, use that one result
            int id = results.getInt("id");
            String dir = results.getString("homedir");
            // Add other columns as needed if we need to get that data

            // found
            return true;
        }

        // not found
        return false;
    }

    /**
     * Helper method for queries
     *
     * Example:
     * - query = "SELECT * FROM ftpuser WHERE userid = ? AND passwd = ?"
     * - SQLQuery(query, param#1, param#2)
     *
     * @param query the sql query
     * @param params the parameters for the query
     * @return the {@link ResultSet} might just make this an array of results later so we don't have to deal with that class
     * @throws SQLException Sql error, should only appear if we do something wrong
     */
    private static ResultSet SQLQuery(String query, String... params) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(query);

        // pass the parameters if needed
        for (int i = 0; i < params.length; i++)
            preparedStatement.setString(i+1, params[i]);

        return preparedStatement.executeQuery();
    }
}

package Connections;

import Utility.FTPClientHandler;

import java.io.*;

public class FTPConnection {

    private static FTPClientHandler ftpClient;

    private static int id;
    private static String directory;

    /**
     *
     * @param username username of the user
     * @param password password of the user
     * @return boolean indicated connection success
     * @throws IOException if socket error or another IO Error occurs
     */
    public static boolean connect(String username, String password) throws IOException {
        ftpClient = new FTPClientHandler(username, password);

        //test the server connection
        boolean result = ftpClient.login();
        if (result) ftpClient.logout();

        return result;
    }
}
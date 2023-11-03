package Utility;
import Connections.DBConnection;

import java.io.*;
import java.sql.*;
import Entities.FileItem;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * static methods within this class are used to get certain information from the FTP server or Database
 */
public class FTPServerFunctions {

    private static FTPClientHandler ftpClient;
    private static String username;
    public FTPServerFunctions(String username, String password) {
        ftpClient = new FTPClientHandler(username, password);
        this.username = username;
    }

    /**
     * Gets the user's list of files.
     *
     * @return {@link ArrayList} containing {@link FileItem}
     * @throws SQLException when sql query is incorrect or some sql error occurs
     */
    public static ArrayList<FileItem> getUserFiles() throws SQLException{

        Connection conn = DBConnection.getConnection();
        String query = "Select fileID,fileName,fileSize,fileUpload from users.ftpfile where fileOwner = '" + username + "'";
        ArrayList<FileItem> fileList = new ArrayList<>();
        Statement st1 = conn.createStatement();
        ResultSet rs = st1.executeQuery(query);
        while(rs.next()) {
            String fid = Integer.toString(rs.getInt("fileID"));
            String fsize = Integer.toString(rs.getInt("fileSize"));
            String fName = rs.getString("fileName");
            String fUpload = rs.getString("fileUpload");

            System.out.println("FileID: " + fid + " FileName: " + fName + " FileSize: " + fsize + " FileUpload: " + fUpload);

            FileItem file = new FileItem(fName, fsize, fid, username, fUpload);
            fileList.add(file);
        }

        st1.close();
        rs.close();
        return fileList;
    }

    public static ArrayList<FileItem> getSharedFiles() throws SQLException{
        Connection conn = DBConnection.getConnection();
        ArrayList<FileItem> fileList = new ArrayList<>();
        Statement st1 = conn.createStatement();
        String query = "Select * from users.ftpfile_share where userID = '" + username + "'";
        ResultSet rs = st1.executeQuery(query);

        while(rs.next()) {
            int fileID = rs.getInt("fileID");
            String query2 = "Select * from users.ftpfile where fileID = " + fileID;
            Statement st2 = conn.createStatement();
            ResultSet rs1 = st2.executeQuery(query2);
            while(rs1.next()) {
                String fid1 = Integer.toString(rs1.getInt("fileID"));
                String fsize1 = Integer.toString(rs1.getInt("fileSize"));
                String fName1 = rs1.getString("fileName");
                String fUpload1 = rs1.getString("fileUpload");

                //System.out.println("FileID: " + fid1 + " FileName: " + fName1 + " FileSize: " + fsize1 + " FileUpload: " + fUpload1);

                FileItem file = new FileItem(fName1, fsize1, fid1, username, fUpload1);
                fileList.add(file);
            }
            st2.close();
            rs1.close();
        }

        return fileList;
    }

    /**
     * Upload a file's info to the database
     * @param file the FileItem with the file's information
     * @throws SQLException when something goes wrong with the sql database or with the sql statement
     */
    public static void uploadFileInfo(FileItem file, File fileFTP) throws SQLException, IOException {


        // SQL / Database Portion
        String query = "Select coalesce(max(fileid), 0)+1 as fid from users.ftpfile;";
        ResultSet rs = DBConnection.SQLQuery(query);
        int fileid = 0;
        if (rs.next()) {
            fileid = rs.getInt("fid");
            System.out.println("FileID: " + fileid);
        }
        int filesize = Integer.parseInt(file.getFsize());
        System.out.println("FileSize: " + filesize);
        String filename = file.getFname();
        String fileowner = file.getFowner();

        System.out.println("FileName: " + filename);
        System.out.println("FileOwner: " + fileowner);


        query = "Insert into users.ftpfile (fileID,fileName,fileSize,fileDir,fileOwner)" +
                "values(" + fileid + ",'" + filename + "'," + filesize
                + ",'/mnt/userDir/" + fileid + "/" + filename +"','" + fileowner + "')";
        Connection conn = DBConnection.getConnection();
        Statement st = conn.createStatement();
        st.executeUpdate(query);
        st.close();

        // FTP Portion
        ftpClient.login();
        //ftpClient.enterLocalPassiveMode(); // Bypass the 500 Port Error, may differ on school wifi.. Let's check.
        String dir = Integer.toString(fileid);

        ftpClient.createDirectory(dir);
        ftpClient.storeFile(fileFTP);

        ftpClient.logout();
    }

    /**
     *
     * @param file the file's info
     * @param user the userid of the user
     * @throws SQLException when something goes wrong with the sql database or with the sql statement
     */
    public void fileShare(FileItem file, String user) throws SQLException{
        if(file.getFowner() != username) {
            System.out.println("Error... cannot share file - current user is not file owner.");
        } else {
            String fid = file.getFid();
            String query = "Insert into users.ftpfile_share(fileID,userID) values (" + fid + ",'" + user + "')";
            Connection conn = DBConnection.getConnection();
            Statement st = conn.createStatement();
            st.executeUpdate(query);
            st.close();
        }
    }

    /**
     * deletes a file from the database
     *
     * @param file the file's info
     * @throws SQLException when something goes wrong with the sql database or with the sql statement
     */
    public static void deleteFile(FileItem file) throws SQLException, IOException {

        // SQL / Database Portion
        String fid = file.getFid();
        String fname = file.getFname();
        String query = "select * from users.ftpfile_share where fileID = " + fid;

        try {
            Connection conn = DBConnection.getConnection();
            Statement st = conn.createStatement();

            ResultSet rs = st.executeQuery(query);
            if(rs.next()){
                query = "Delete from users.ftpfile_share where fileID = " + fid;
                st.executeUpdate(query);
            }
            query = "Delete from users.ftpfile where fileID = " + fid;
            st.executeUpdate(query);
            st.close();
            rs.close();

        }
        catch (SQLException e) {
            System.out.println("Message: " + e.getMessage());
        }

        // FTP Portion
        ftpClient.login();
        //ftpClient.enterLocalPassiveMode();
        ftpClient.deleteFile(fname, fid);
        ftpClient.logout();
    }

    // ftp download file
    public static void downloadFile(FileItem file, OutputStream fos) throws Exception {
        ftpClient.login();
        //ftpClient.enterLocalPassiveMode();
        ftpClient.downloadFile(file.getFname(), file.getFid(), fos);
        ftpClient.logout();
    }


    public static void addUser(String user, String pass) throws SQLException{
        Connection conn = DBConnection.getConnection();
        Statement st = conn.createStatement();
        String query = "Select * from users.ftpuser where userid = '" + user + "'";
        ResultSet rs = st.executeQuery(query);

        if(rs.next()) {
            System.out.println("Error... user already exists.");
        } else {
            query = "INSERT INTO users.ftpuser (userid, passwd, uid, gid, homedir, shell)" +
                    " VALUES ('"+ user +
                    "', ENCRYPT('" + pass + "'), " + 500 + ", " + 500 + ", '/mnt/userDir', '/sbin/nologin')";
            st.executeUpdate(query);
        }
        st.close();
        rs.close();
    }

    public static void deleteUser(String user) throws SQLException{
        Connection conn = DBConnection.getConnection();
        Statement st = conn.createStatement();
        String query = "Select * from users.ftpuser where userid = '" + user + "'";
        ResultSet rs = st.executeQuery(query);

        if(!rs.next()) {
            System.out.println("Error... user does not exist.");
        } else {
            query = "Delete from users.ftpfile where userid = '" + user + "'";
            st.executeUpdate(query);
        }
        st.close();
        rs.close();
    }

    public static ArrayList<String> getallUsers() throws SQLException{
        Connection conn = DBConnection.getConnection();
        Statement st = conn.createStatement();
        ArrayList<String> users = new ArrayList<>();

        String query = "Select userid from users.ftpuser where userid <> '" + username + "'";
        ResultSet rs = st.executeQuery(query);
        while(rs.next()) {
            String userid = rs.getString("userid");
            users.add(userid);
        }
        st.close();
        rs.close();
        return users;
    }

    // Return username
    public static String getUsername() {
        return username;
    }



}

package Utility;

import java.io.*;
import java.sql.*;

import Connections.DBConnection;
import Entities.FileItem;
import Exceptions.UserAlreadyExists;
import lombok.Getter;

import java.util.ArrayList;

/**
 * static methods within this class are used to get certain information from the FTP server or Database
 */
public class FTPServerFunctions {

    public static FTPClientHandler ftpClient;
    // Return username
    @Getter
    private static String username;
    private static FileItem sharedfile;

    /**
     * sets username and password of the current user
     * and sets up the appropriate ftp connection info
     *
     * @param username username of current user
     * @param password password of current user
     * @return boolean indicating login success
     *
     * note: please do not invert method. Do not listen to intellij
     */
    public static boolean setupConnection(String username, String password) throws SQLException {
        ftpClient = new FTPClientHandler(username, password);
        FTPServerFunctions.username = username;

        //test connection
        try{
            Connection conn = DBConnection.getConnection();
            Statement st = conn.createStatement();
            String query = "Select * from users.ftpuser where userid = '" + username + "' and status = 'Active'";
            ResultSet rs = st.executeQuery(query);
            if(rs.next()){
                boolean result = ftpClient.login(); // returns boolean indicating success
                if (result) ftpClient.logout();
                st.close();
                rs.close();
                return result;
            }
            st.close();
            rs.close();
        } catch (IOException e){
            System.err.println(e.getMessage());

        }

        return false; // login failed
    }

    /**
     * Gets the user's list of files.
     * @return {@link ArrayList} containing {@link FileItem}
     * @throws SQLException when sql query is incorrect or some sql error occurs
     */
    public static ArrayList<FileItem> getUserFiles() throws SQLException{

        Connection conn = DBConnection.getConnection();
        String query = "Select * from users.ftpfile where fileOwner = '" + username + "'";
        ArrayList<FileItem> fileList = new ArrayList<>();
        Statement st1 = conn.createStatement();
        ResultSet rs = st1.executeQuery(query);
        while(rs.next()) {
            String fid = Integer.toString(rs.getInt("fileID"));
            int fileSizeInBytes = rs.getInt("fileSize");
            double fizeSizeInMb = fileSizeInBytes / (1024.0 * 1024.0);
            String fName = rs.getString("fileName");
            String fUpload = rs.getString("fileUpload");
            String fOwner = rs.getString("fileOwner");
            String fsize = String.format("%.2f MB", fizeSizeInMb);

            System.out.println("FileID: " + fid + " FileName: " + fName + " FileSize: " + fsize + " FileUpload: " + fUpload);

            FileItem file = new FileItem(fName, fsize, fid, fOwner, fUpload);
            fileList.add(file);
        }

        st1.close();
        rs.close();
        return fileList;
    }

    /**
     * Gets a list of files shared with the user.
     * @return {@link ArrayList} containing {@link FileItem}
     * @throws SQLException when sql query is incorrect or some sql error occurs
     */
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
                String fid = Integer.toString(rs1.getInt("fileID"));
                int fileSizeInBytes = rs1.getInt("fileSize");
                double fizeSizeInMb = fileSizeInBytes / (1024.0 * 1024.0);
                String fName = rs1.getString("fileName");
                String fUpload = rs1.getString("fileUpload");
                String fOwner = rs1.getString("fileOwner");
                String fsize = String.format("%.2f MB", fizeSizeInMb);

                FileItem file = new FileItem(fName, fsize, fid, fOwner, fUpload);
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
     * @param fileFTP the physical file that is to be uploaded to the ftp server
     * @throws SQLException when something goes wrong with the sql database or with the sql statement
     */
    public static void uploadFileInfo(FileItem file, File fileFTP) throws SQLException, IOException {

        // SQL , Establish Connection & Insert File Info
        String query = "Select coalesce(max(fileid), 0)+1 as fid from users.ftpfile;";
        Connection conn = DBConnection.getConnection();
        Statement st = conn.createStatement();
        ResultSet rs = st.executeQuery(query);

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

        st.executeUpdate(query);
        st.close();
        rs.close();

        // FTP Login & Upload
        ftpClient.login();
        String dir = Integer.toString(fileid);

        ftpClient.createDirectory(dir);
        ftpClient.storeFile(fileFTP);

        ftpClient.logout();
    }

    /**
     * Share a file with a selected user.
     * @param user the userid of the user
     * @throws SQLException when something goes wrong with the sql database or with the sql statement
     */
    public static void fileShare(String user) throws SQLException{

        Connection conn = DBConnection.getConnection();
        Statement st = conn.createStatement();
        String fid = sharedfile.getFid();
        String fowner = sharedfile.getFowner();

        String query = "Select * from users.ftpfile_share where userid = '" + user + "' and fileid = " + fid;
        ResultSet rs = st.executeQuery(query);
        if(fowner != user && !rs.next()) {
            query = "Insert into users.ftpfile_share(fileID,userID) values (" + fid + ",'" + user + "')";
            st.executeUpdate(query);
            st.close();
        }
        else {
            System.out.println("Error.... Cannot share file with the file owner.");
        }
        rs.close();
    }

    /**
     * Deletes a file from the database
     * @param file the file's info
     * @throws SQLException when something goes wrong with the sql database or with the sql statement
     * @throws IOException when something goes wrong with the deletion of a file from the ftp server
     */
    public static void deleteFile(FileItem file) throws SQLException, IOException {

        // SQL / Database Portion
        String fid = file.getFid();
        String fname = file.getFname();
        String fowner = file.getFowner();
        Connection conn = DBConnection.getConnection();
        Statement st = conn.createStatement();

        String query = "select * from users.ftpfile_share where fileID = " + fid + " and userid = '" + username + "'";
        if(fowner == username) {

            ResultSet rs = st.executeQuery(query);
            if (rs.next()) {
                query = "Delete from users.ftpfile_share where fileID = " + fid;
                st.executeUpdate(query);
            }
            query = "Delete from users.ftpfile where fileID = " + fid;
            st.executeUpdate(query);
            rs.close();

            // FTP Portion
            ftpClient.login();
            ftpClient.deleteFile(fname, fid);
            ftpClient.logout();
        }
        else {
            query = "Delete from users.ftpfile_share where fileID = " + fid + " and userid = '" + username + "'";
            st.executeUpdate(query);
        }
        st.close();
    }

    /**
     * Downloads a file from the ftp server
     * @param file the file's info
     * @param fos the file output stream for download
     * @throws Exception when something goes wrong with the deletion of a file from the ftp server
     */
    public static void downloadFile(FileItem file, OutputStream fos) throws Exception {
        ftpClient.login();
        ftpClient.downloadFile(file.getFname(), file.getFid(), fos);
        ftpClient.logout();
    }


    /**
     * Creates a new user account in the database
     * @param user the userid to be added to the new user
     * @param pass the password to be added for the new user
     * @param admin this is a boolean flag that either enables the new user to be an admin or not
     * @throws SQLException when something goes wrong with the sql database or with the sql statement
     * @throws UserAlreadyExists when a user already exists with the same userid trying to be added
     */
    public static void addUser(String user, String pass, boolean admin) throws SQLException, UserAlreadyExists {
        Connection conn = DBConnection.getConnection();
        Statement st = conn.createStatement();
        String query = "Select * from users.ftpuser where userid = '" + user + "'";
        ResultSet rs = st.executeQuery(query);

        if(rs.next()) { // If user exists... throw error
            throw new UserAlreadyExists("User Already Exists");
        }
        else { // Else add user to database
            if(admin) {
                query = "INSERT INTO users.ftpuser (userid, passwd, uid, gid, homedir, shell, admin, status)" +
                        " VALUES ('" + user +
                        "', ENCRYPT('" + pass + "'), " + 500 + ", " + 500 + ", '/mnt/userDir', '/sbin/nologin', 'x', 'Active')";
            }
            else {
                query = "INSERT INTO users.ftpuser (userid, passwd, uid, gid, homedir, shell, status)" +
                        " VALUES ('" + user +
                        "', ENCRYPT('" + pass + "'), " + 500 + ", " + 500 + ", '/mnt/userDir', '/sbin/nologin' ,'Active')";
            }
            st.executeUpdate(query);
        }
        st.close();
        rs.close();
    }

    /**
     * Deactivates a user in the system
     * @param user the userid of the user to be deactivated
     * @throws SQLException when something goes wrong with the sql database or with the sql statement
     */
    public static void deleteUser(String user) throws SQLException{
        Connection conn = DBConnection.getConnection();
        Statement st = conn.createStatement();
        String query = "Select * from users.ftpuser where userid = '" + user + "'";
        ResultSet rs = st.executeQuery(query);

        if(!rs.next()) {
            System.out.println("Error... user does not exist.");
        }
        else {
            query = "update users.ftpuser set status = 'Inactive' where userid = '" + user + "'";
            st.executeUpdate(query);
        }
        st.close();
        rs.close();
    }

    /**
     * Gets a list of all users that a file can be shared with
     * @return {@link ArrayList} containing {@link FileItem}
     * @throws SQLException when something goes wrong with the sql database or with the sql statement
     */
    public static ArrayList<String> getAllUsers() throws SQLException{
        Connection conn = DBConnection.getConnection();
        Statement st = conn.createStatement();
        ArrayList<String> users = new ArrayList<>();

        String query = "Select userid from users.ftpuser where userid <> '" + username + "' and userid <> '" + sharedfile.getFowner() + "'";
        ResultSet rs = st.executeQuery(query);
        while(rs.next()) {
            String userid = rs.getString("userid");
            users.add(userid);
        }
        st.close();
        rs.close();
        return users;
    }

    /**
     * Returns if the current user is an admin or not
     * @return boolean
     * @throws SQLException when something goes wrong with the sql database or with the sql statement
     */
    public static boolean isUserAdmin() throws SQLException {
        Connection conn = DBConnection.getConnection();
        Statement st = conn.createStatement();
        String query = "Select * from users.ftpuser where userid = '" + username + "' and admin = 'x' and status = 'Active'";
        ResultSet rs = st.executeQuery(query);
        if(rs.next())  return true;
        else return false;
    }

    /**
     * Reactivates an account that has been deactivated
     * @param user the userid of the account to be reactivated
     * @throws SQLException when something goes wrong with the sql database or with the sql statement
     */
    public static void reactivateUser(String user) throws SQLException {
        Connection conn = DBConnection.getConnection();
        Statement st = conn.createStatement();
        String query = "Select * from users.ftpuser where userid = '" + user +"' and status = 'Inactive'";
        ResultSet rs = st.executeQuery(query);
        if(!rs.next()) {
            // throw error here user does not exist or user is already active
        } else {
            query = "Update users.ftpuser set status = 'Active' where userid = '" + user + "'";
            st.executeUpdate(query);
        }
        rs.close();
        st.close();

    }
    /**
     * Clears the username
     */
    public static void clearUsername()
    {
        username = null;
    }
    /**
     * Sets the file to be shared
     * @param fileitem the file to be shared
     */
    public static void setFile(FileItem fileitem)
    {
        sharedfile = fileitem;
    }

}

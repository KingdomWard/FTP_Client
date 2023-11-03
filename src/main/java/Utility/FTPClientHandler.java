package Utility;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;


/**
 * Simplifies our FTPClient implementation
 */
public class FTPClientHandler extends FTPClient {

    /**
     * username of current user
     */
    private final String username;

    /**
     * password of current user
     */
    private final String password;

    /**
     * sets the initial username/password of the current user
     * @param username the username
     * @param password the password
     */
    public FTPClientHandler(String username, String password) {
        this.username = username;
        this.password = password;
    }

    /**
     * Simplifies logging in. Can now be exposed without worrying about also passing along login info
     */
    public boolean login() throws IOException {
        PropertiesLoader properties = new PropertiesLoader("ftp");
        String server = properties.getProperty("ftp.host");
        int port = Integer.parseInt(properties.getProperty("ftp.port"));

        super.connect(server, port);
        //super.enterLocalPassiveMode();
        super.setFileType(FTP.BINARY_FILE_TYPE);

        if (super.login(username, password)) {
            // Check the FTP server's reply code for successful login
            return FTPReply.isPositiveCompletion(super.getReplyCode());
        }

        return false;
    }

    /**
     * logout with in-built disconnect.
     * @return boolean indicating success
     */
    @Override
    public boolean logout() throws IOException {
        boolean result = super.logout();
        super.disconnect();
        return result;
    }

    /**
     * Uploads a file to the FTP server
     * @param file the file to be uploaded
     * @throws IOException if the file is not found
     */
    public void storeFile(File file) throws IOException {
        //if (file.getName().endsWith(".png")) {
            super.setFileType(FTP.BINARY_FILE_TYPE);
        //}
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
            boolean success = super.storeFile(file.getName(), fis);
            if (success)
                System.out.println("File " + file.getName() + " uploaded successfully.");
            else
                System.out.println("Failed to upload file " + file.getName() + ".");
            System.out.println("Server reply: " + super.getReplyString());
        }
        catch (IOException e) { e.printStackTrace(); }
        finally { if (fis != null) fis.close(); }
    }

    /**
     * Creates a directory on the FTP server
     * @param string the name of the directory to be created
     * @throws IOException if the directory is not found
     */
    public void createDirectory(String string) throws IOException {
        super.makeDirectory(string);
        super.changeWorkingDirectory(string);
    }

    /**
     * Deletes the file and directory from the FTP server
     * @param fname the name of the file to be deleted
     * @param fid the id of the file to be deleted
     * @throws IOException if the file is not found
     */
    public void deleteFile(String fname, String fid) throws IOException {
        super.changeWorkingDirectory(fid);
        super.deleteFile(fname);
        super.changeToParentDirectory();
        super.removeDirectory(fid);
    }

    /**
     * Downloads the file from the FTP server
     * @param fname the name of the file to be downloaded
     * @param fid the id of the file to be downloaded
     * @param fos the output stream to write to
     * @throws IOException if the file is not found
     */
    public void downloadFile(String fname, String fid, OutputStream fos) throws IOException {
        //if (fname.endsWith(".png")) {
            super.setFileType(FTP.BINARY_FILE_TYPE);
        //}
        super.changeWorkingDirectory(fid);
        boolean success = super.retrieveFile(fname, fos);
        fos.close();
        super.changeToParentDirectory();
        if (success)
            System.out.println("File " + fname + " downloaded successfully.");
        else
            System.out.println("Failed to download file " + fname + ".");
        System.out.println("Server reply: " + super.getReplyString());
    }


}

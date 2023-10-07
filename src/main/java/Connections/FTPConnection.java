package Connections;

import Utility.FTPClientHandler;
import Utility.PropertiesLoader;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

import java.io.*;

public class FTPConnection {

    private static FTPClientHandler ftpClient;

    private static int id;
    private static String directory;

    public static void updateUser(int db_id, String username, String password, String dir) throws IOException {

        id = db_id;
        directory = dir;

        ftpClient = new FTPClientHandler(username, password);

    }

    public static void storeFile(File localFile) throws IOException {
        FileInputStream inputStream = new FileInputStream(localFile);
        ftpClient.login();
        boolean uploaded = ftpClient.storeFile(localFile.getName(), inputStream);
        if (uploaded) {
            System.out.println("File uploaded successfully.");
            FTPFile[] files = ftpClient.listFiles();
            for (FTPFile file : files) {
                System.out.println(file.getName());
            }
        }
        else {
            System.out.println("File upload failed.");
        }
        inputStream.close();
        ftpClient.logout();
    }

    public void test(){
        PropertiesLoader properties = new PropertiesLoader("ftp");

        String server = properties.getProperty("ftp.host");
        int port = Integer.parseInt(properties.getProperty("ftp.port")); // FTP server port
        String username = "outcast";
        String password = "test";

        String localFilePath = "TestFiles/test.txt"; // Replace with the path to your local file
        FTPClient ftpClient = new FTPClient();
        try {
            ftpClient.connect(server, port);
            ftpClient.login(username, password);
            ftpClient.enterLocalPassiveMode();
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
            File localFile = new File(localFilePath);
            FileInputStream inputStream = new FileInputStream(localFile);
            boolean uploaded = ftpClient.storeFile(localFile.getName(), inputStream);
            if (uploaded) {
                System.out.println("File uploaded successfully.");
                FTPFile[] files = ftpClient.listFiles();
                for (FTPFile file : files) {
                    System.out.println(file.getName());
                }
            }
            else {
                System.out.println("File upload failed.");
            }
            inputStream.close();
            ftpClient.logout();
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            try {
                if (ftpClient.isConnected())
                    ftpClient.disconnect();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

// 
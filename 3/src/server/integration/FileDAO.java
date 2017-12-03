/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server.integration;

import common.Credentials;
import common.FileDTO;
import common.Notify;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.NoSuchFileException;
import java.rmi.RemoteException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import javax.naming.CommunicationException;
import javax.security.auth.login.AccountException;
import javax.security.auth.login.FailedLoginException;
import server.model.File;
import server.model.Notifier;

/**
 *
 * @author Oscar
 */
public class FileDAO {
    private final String DATASOURCE = "jdbc:mysql://localhost:3306/files";
    private final String connectUser = "CHANGE THIS TO YOUR USERNAME TO ACCESS THE DATABASE";
    private final String connectPass = "CHANGE THIS TO YOUR PASSWORD TO ACCESS THE DATABASE";
    private final String USER_TABLE = "user";
    private final String FILE_TABLE = "file";
    private final Notifier notifier;
    private final Map<Long,Integer> loggedinUsers = Collections.synchronizedMap(new HashMap<>());
    private final Random randomIdGenerator = new Random(System.currentTimeMillis());
    private final Set<Long> usedSessionIDs = Collections.synchronizedSet(new HashSet());
    private PreparedStatement registerUser;
    private PreparedStatement loginUser;
    private PreparedStatement listAllFiles;
    private PreparedStatement uploadFile;
    private PreparedStatement downloadFile;
    private PreparedStatement unregister;
    private PreparedStatement deleteFile;
    private PreparedStatement modifyFile;
    private PreparedStatement notifyFile;
    private PreparedStatement updateFileSize;
    
    
    public FileDAO(Notifier notifier) {
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            Connection connection = DriverManager.getConnection(this.DATASOURCE, this.connectUser, this.connectPass);
            prepareStatements(connection);
        } catch (Exception ex) {
            System.err.println("coldn't connect: " + ex.getMessage());
            ex.printStackTrace();
        }
        this.notifier = notifier;
    }
    
    public long registerUser(Credentials creds) throws AccountException, CommunicationException {
        long sessionID = 0;
        
        try {
            this.registerUser.setString(1, creds.getUsername());
            this.registerUser.setString(2, creds.getPassword());
            this.registerUser.executeUpdate();
            
            ResultSet rs = this.registerUser.getGeneratedKeys();
            
            if(!rs.next()) { throw new CommunicationException(ExceptionMessages.FAILED_USER_CREATE); }
            
            sessionID = addUserToSetAndLogin(rs.getInt(1));
        } catch(com.mysql.jdbc.exceptions.MySQLIntegrityConstraintViolationException ex) {
            throw new AccountException(ExceptionMessages.NAME_OCCUPIED + creds.getUsername());
        } catch (SQLException ex) {
            throw new CommunicationException(ExceptionMessages.DB_COM_ERROR);
        }
        
        return sessionID;
    }
    
    public long login(Credentials creds) throws FailedLoginException, CommunicationException {
        long sessionID = 0;

        try {
            this.loginUser.setString(1, creds.getUsername());
            this.loginUser.setString(2, creds.getPassword());
            ResultSet rs = this.loginUser.executeQuery();
            
            if (!rs.next()) { throw new FailedLoginException(ExceptionMessages.INVALID_CREDS); }
            
            sessionID = addUserToSetAndLogin(rs.getInt("id"));
        } catch(SQLException ex) {
            throw new CommunicationException(ExceptionMessages.DB_COM_ERROR);
        }

        return sessionID;
    }
    
    private long addUserToSetAndLogin(int userDbId) {
        long userSessionId = this.randomIdGenerator.nextLong();
        
        while(this.usedSessionIDs.contains(userSessionId) || userSessionId == 0) {
            userSessionId = this.randomIdGenerator.nextLong();
        }
        
        this.usedSessionIDs.add(userSessionId);
        this.loggedinUsers.put(userSessionId, userDbId);
        
        return userSessionId;
    }
    
    private void validateLoggedin(long sessionId) throws CommunicationException {
        if (!this.loggedinUsers.containsKey(sessionId)) {
            throw new CommunicationException(ExceptionMessages.YOU_NOT_BE_HERE);
        }
    }
    
    public List<? extends FileDTO> listAllFiles(long sessionID) throws CommunicationException {
        List<File> files = new ArrayList<>();
        validateLoggedin(sessionID);
        
        try {    
            this.listAllFiles.setInt(1, this.loggedinUsers.get(sessionID));
            ResultSet rs = this.listAllFiles.executeQuery();
            
            while(rs.next()) {
                files.add(new File(
                        rs.getInt("id"),
                        checkIfMyFile(sessionID, rs.getInt("owner")),
                        rs.getString("name"),
                        rs.getInt("size"),
                        rs.getBoolean("public"),
                        rs.getBoolean("writeable")
                ));
            }
            
        } catch(SQLException ex) {
            throw new CommunicationException(ExceptionMessages.DB_COM_ERROR);
        }
        
        return files;
    }
    
    private String checkIfMyFile(Long sessionId, int filwOwnerId) {
        return this.loggedinUsers.get(sessionId) == filwOwnerId ? "You" : "Anonymous";
    }
    
    public String downloadFile(long sessionId, String nameOrId) throws NoSuchFileException, RemoteException, CommunicationException {
        validateLoggedin(sessionId);
        
        int fileId = isIdAndNotName(nameOrId);
        String filename = (fileId == 0) ? nameOrId : "";
        
        try {
            this.downloadFile.setInt(1, fileId);
            this.downloadFile.setString(2, nameOrId);
            this.downloadFile.setInt(3, this.loggedinUsers.get(sessionId));
            ResultSet rs = this.downloadFile.executeQuery();
            if(!rs.next()) { throw new NoSuchFileException(ExceptionMessages.NO_SUCH_FILE); }
            filename = rs.getString("name");
        } catch (SQLException ex) {
            throw new RemoteException(ExceptionMessages.DB_COM_ERROR);
        }
        
        return filename;
    }
    
    public void uploadFile(long sessionId, FileDTO file) throws FileAlreadyExistsException, CommunicationException {
        validateLoggedin(sessionId);
        try {
            this.uploadFile.setInt(1, this.loggedinUsers.get(sessionId));
            this.uploadFile.setString(2, file.getName());
            this.uploadFile.setInt(3, file.getSize());
            this.uploadFile.setBoolean(4, file.isVisible());
            this.uploadFile.setBoolean(5, file.isWritePermission());
            this.uploadFile.executeUpdate();
        } catch(com.mysql.jdbc.exceptions.MySQLIntegrityConstraintViolationException ex) {
            throw new FileAlreadyExistsException(ExceptionMessages.FILE_EXISTS + file.getName());
        } catch(SQLException ex) {
            throw new CommunicationException(ExceptionMessages.DB_COM_ERROR);
        }
        
    }
    
    public void logout(long sessionId) {
        this.usedSessionIDs.remove(sessionId);
        this.loggedinUsers.remove(sessionId);
    }
    
    public void modifyFile(long sessionId, FileDTO file) throws  NoSuchFileException, CommunicationException, RemoteException {
        validateLoggedin(sessionId);
        int fileId = 0;
        String filename = "";
        
        try {
            this.modifyFile.setString(1, file.getName());
            this.modifyFile.setInt(2, this.loggedinUsers.get(sessionId));
            this.modifyFile.setString(3, file.getName());
            ResultSet rs = this.modifyFile.executeQuery();
            
            if(!rs.next()) { throw new NoSuchFileException("There is no such file to update..."); }
            
            fileId = rs.getInt("id");
            filename = rs.getString("name");
            
            this.updateFileSize.setInt(1, file.getSize());
            this.updateFileSize.setInt(2, fileId);
            int affectedRows = this.updateFileSize.executeUpdate();
            
            if(affectedRows == 0) {
                // I DO NOT CARE THAT SIZE IS NOT UPDATED...
                // IF YOU DO, HANDLE IT HERE
            }
            
        } catch (SQLException ex) {
            throw new CommunicationException(ExceptionMessages.DB_COM_ERROR);
        }
        
        this.notifier.notifyClients(fileId, filename);
    }
    
    private int isIdAndNotName(String idOrName) {
        try {
            return Integer.parseInt(idOrName);
        } catch(NumberFormatException nex) {
            return 0;
        }
    }
    
    public void addNotifications(long sessionId, String filenameOrId, Notify notify) throws CommunicationException, NoSuchFileException {
        validateLoggedin(sessionId);
        int fileId = isIdAndNotName(filenameOrId);
        String filename = (fileId == 0) ? filenameOrId : "!";
        System.out.println(filename);
        try {
            this.notifyFile.setString(1, filename);
            this.notifyFile.setInt(2, fileId);
            this.notifyFile.setInt(3, this.loggedinUsers.get(sessionId));
            ResultSet rs = this.notifyFile.executeQuery();
            
            if(!rs.next()) { throw new NoSuchFileException("There is no such file to be notified about..."); }
            
            fileId = rs.getInt("id");
            filename = rs.getString("name");
        } catch (SQLException ex) {
            throw new CommunicationException(ExceptionMessages.DB_COM_ERROR);
        }
        
        this.notifier.addClient(fileId, notify);
    }
    
    public void unregisterUser(long sessiongId, Credentials creds) throws CommunicationException, AccountException {
        validateLoggedin(sessiongId);
        try {
            this.unregister.setInt(1, this.loggedinUsers.get(sessiongId));
            this.unregister.setString(2, creds.getUsername());
            this.unregister.setString(3, creds.getPassword());
            int rowsAffected = this.unregister.executeUpdate();
            
            if(rowsAffected == 0) { throw new AccountException(ExceptionMessages.INVALID_CREDS); }
            
        } catch(SQLException ex) {
            throw new CommunicationException(ExceptionMessages.DB_COM_ERROR);
        }
        
        logout(sessiongId);
    }
    
    private void prepareStatements(Connection conn) throws SQLException {
        this.registerUser = conn.prepareStatement("INSERT INTO "+ this.USER_TABLE 
                + "(username,password) VALUES (?,?)", PreparedStatement.RETURN_GENERATED_KEYS);
        
        this.loginUser = conn.prepareStatement("SELECT id as id"
                + " FROM " + this.USER_TABLE
                + " WHERE username = ? AND password = ?");
        
        this.deleteFile = conn.prepareStatement("DELETE FROM " 
                + this.FILE_TABLE
                + " WHERE name = ? AND ownerid = ("
                    + " SELECT userid FROM " + this.USER_TABLE
                    + " WHERE username = ?)");

        this.listAllFiles = conn.prepareStatement("SELECT id as id, owner as owner, name as name, size as size, public as public, writeable as writeable"
                + " FROM " + this.FILE_TABLE
                + " WHERE public = 1 OR owner = ?");
        
        this.downloadFile = conn.prepareStatement("SELECT name as name"
                + " FROM " + this.FILE_TABLE
                + " WHERE (id = ? OR name = ?) AND (public = 1 OR owner = ?)");
        
        this.uploadFile = conn.prepareStatement("INSERT INTO " + this.FILE_TABLE
                + " (owner,name,size,public,writeable) VALUES (?,?,?,?,?)");
        
        this.modifyFile = conn.prepareStatement("SELECT name as name, id as id"
                + " FROM " + this.FILE_TABLE
                + " WHERE (public = 1 AND writeable = 1 AND name = ?) OR (owner = ? AND name = ?)");
        
        this.updateFileSize = conn.prepareStatement("UPDATE " + this.FILE_TABLE
                + " SET size = ?"
                + " WHERE id = ?");
        
        this.notifyFile = conn.prepareStatement("SELECT id as id, name as name"
                + " FROM " + this.FILE_TABLE
                + " WHERE (name = ? OR id = ?) AND owner = ?");
        
        this.unregister = conn.prepareStatement("DELETE "
                + " FROM " + this.USER_TABLE
                + " WHERE id = ? AND username = ? AND password = ?");
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package common;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.NoSuchFileException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import javax.naming.CommunicationException;
import javax.security.auth.login.AccountException;
import javax.security.auth.login.FailedLoginException;

/**
 *
 * @author Oscar
 */
public interface FileServer extends Remote{
    public static final String SERVER_NAME_IN_REGISTRY = "FILE_SERVER";
    public static final String HOST = "localhost";
    public static final int PORT = 4590;
    
    public static final String[] ALLOWED_SERVER_OPERATION = {
        "Upload a new file",
        "Download a file",
        "Modify a file",
        "List all available files",
        "Get notified about modifications to a file you own",
        "Logout",
        "Unregister account"
    };
    
    long login(Credentials creds) throws RemoteException, FailedLoginException, CommunicationException;
    
    void logout(long sessionId) throws RemoteException;
    
    long register(Credentials creds) throws RemoteException, AccountException, CommunicationException;
    
    void unregister(long sessionId, Credentials creds) throws RemoteException, AccountException, CommunicationException;
    
    void uploadFile(long sessionId, FileDTO file) throws RemoteException, FileAlreadyExistsException, CommunicationException;
    
    void downloadFile(long sessionId, String filenameOrId) throws RemoteException, NoSuchFileException, CommunicationException, IOException;
    
    void modifyFile(long sessionId, FileDTO file) throws RemoteException, NoSuchFileException, CommunicationException;
    
    List<? extends FileDTO> listFiles(long sessionId) throws RemoteException, CommunicationException;
    
    void deleteFile(long sessionId, String filenameOrId) throws RemoteException;

    void notifyFile(long sessionId, String filenameOrId, Notify notify) throws RemoteException, NoSuchFileException, CommunicationException;
    
}

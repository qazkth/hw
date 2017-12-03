/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client.controller;

import client.model.FileHandler;
import client.model.FileMetadata;
import client.model.ServerHandler;
import client.view.UIMessages;
import common.Credentials;
import common.FileDTO;
import common.FileServer;
import common.FileTransfer;
import common.Notify;
import java.io.IOException;
import java.net.ConnectException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.NoSuchFileException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.List;
import javax.naming.CommunicationException;
import javax.security.auth.login.AccountException;
import javax.security.auth.login.FailedLoginException;

/**
 *
 * @author Oscar
 */
public class Controller {
    private final FileHandler fileHandler = new FileHandler();
    private ServerHandler serverHandler;
    private final FileServer server;
    
    public Controller() throws ConnectException {
        try {
            this.server = (FileServer) Naming.lookup(FileServer.SERVER_NAME_IN_REGISTRY);
        } catch (Exception ex) {
            throw new ConnectException(UIMessages.CONNECTION_FAILED);
        }
    }
    
    public long register(Credentials creds) throws RemoteException, AccountException, CommunicationException, IOException {
        long ssid = this.server.register(creds);
        this.serverHandler = new ServerHandler(ssid);
        return ssid;
    }
    
    public void unregister(long sessionId, Credentials creds) throws RemoteException, AccountException, CommunicationException {
        this.server.unregister(sessionId, creds);
    }
    
    public long login(Credentials creds) throws RemoteException, FailedLoginException, CommunicationException, IOException {
        long ssid = this.server.login(creds);
        this.serverHandler = new ServerHandler(ssid);
        return ssid;
    }
    
    public void logout(long sessionId) throws RemoteException {
        this.serverHandler.disconnect();
        this.server.logout(sessionId);
    }
    
    public boolean receiveFile(String name) {
        return false;
    }
    
    private FileDTO makeMetadata(String name, String path, boolean visible, boolean writeable) throws NoSuchFileException {
        FileMetadata mdata = new FileMetadata(name, path, visible, writeable);
        this.fileHandler.setFileSize(mdata);
        return mdata;
    }

    public void uploadFile(long sessionId, String name, String path, boolean visible, boolean writeable) throws RemoteException, NoSuchFileException, FileAlreadyExistsException, CommunicationException, IOException {
        this.server.uploadFile(sessionId, makeMetadata(name, path, visible, writeable));
        sendTheFile(name, path);
    }

    public void downloadFile(long sessionID, String filenameOrId, String saveAs) throws RemoteException, IOException, NoSuchFileException, CommunicationException {
        this.server.downloadFile(sessionID, filenameOrId);
        this.serverHandler.receiveFile(saveAs);
    }

    public void modifyFile(long sessionId, String filename, String pathToFile) throws RemoteException, NoSuchFileException, CommunicationException, IOException {
        this.server.modifyFile(sessionId, makeMetadata(filename, pathToFile, false, false));
        sendTheFile(filename, pathToFile);
    }
    
    private void sendTheFile(String name, String path) throws IOException {
        FileTransfer sendThisFile = this.fileHandler.createFileTransfer(name, path);
        this.serverHandler.sendFile(sendThisFile);
    }
    
    public void notifyFile(long sessionId, String filenameOrId, Notify notify) throws RemoteException, NoSuchFileException, CommunicationException {
        this.server.notifyFile(sessionId, filenameOrId, notify);
    }
    
    public List<? extends FileDTO> listFiles(long sessionId) throws RemoteException, CommunicationException {
        return this.server.listFiles(sessionId);
    }
}

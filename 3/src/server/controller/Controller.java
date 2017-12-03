/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server.controller;

import common.Credentials;
import common.FileDTO;
import common.FileServer;
import common.FileTransfer;
import common.Notify;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.NoSuchFileException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import javax.naming.CommunicationException;
import javax.security.auth.login.AccountException;
import javax.security.auth.login.FailedLoginException;
import server.integration.FileDAO;
import server.model.FileHandler;
import server.model.Notifier;

/**
 *
 * @author Oscar
 */
public class Controller extends UnicastRemoteObject implements FileServer{
    private final FileDAO fileDB;
    private final FileHandler fileHandler;
    private final Notifier notifier;
    
    public Controller() throws RemoteException {
        super();
        this.notifier = new Notifier();
        this.fileDB = new FileDAO(this.notifier);
        this.fileHandler = new FileHandler();
    }
    
    public synchronized void writeFileToDisk(FileTransfer recFile) throws IOException {
        this.fileHandler.writeFileToDisk(recFile);
    }

    @Override
    public synchronized long login(Credentials creds) throws RemoteException, FailedLoginException, CommunicationException {
        return this.fileDB.login(creds);
    }

    @Override
    public synchronized void logout(long sessionId) throws RemoteException {
        this.fileDB.logout(sessionId);
        this.fileHandler.removeClientAndStream(sessionId);
    }

    @Override
    public synchronized long register(Credentials creds) throws RemoteException, AccountException, CommunicationException {
        return this.fileDB.registerUser(creds);
    }

    @Override
    public synchronized void uploadFile(long sessionId, FileDTO file) throws RemoteException, FileAlreadyExistsException, CommunicationException {
        this.fileDB.uploadFile(sessionId, file);
    }

    public synchronized void registerOutStreamWithSSID(long ssid, ObjectOutputStream client) {
        this.fileHandler.registerClientAndStream(ssid, client);
    }
    
    @Override
    public synchronized void downloadFile(long sessionId, String filenameOrId) throws RemoteException, NoSuchFileException, CommunicationException, IOException {
        String filename = this.fileDB.downloadFile(sessionId, filenameOrId);
        this.fileHandler.writeFileToOutStream(sessionId, filename);
    }

    @Override
    public synchronized List<? extends FileDTO> listFiles(long sessionId) throws RemoteException, CommunicationException {
        return this.fileDB.listAllFiles(sessionId);
    }

    @Override
    public synchronized void deleteFile(long sessionId, String filenameOrId) throws RemoteException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public synchronized void unregister(long sessionId, Credentials creds) throws RemoteException, AccountException, CommunicationException {
        this.fileDB.unregisterUser(sessionId, creds);
        this.fileHandler.removeClientAndStream(sessionId);
    }

    @Override
    public void modifyFile(long sessionId, FileDTO file) throws RemoteException, CommunicationException, NoSuchFileException {
        this.fileDB.modifyFile(sessionId, file);
    }

    @Override
    public void notifyFile(long sessionId, String filenameOrId, Notify notify) throws RemoteException, CommunicationException, NoSuchFileException {
        this.fileDB.addNotifications(sessionId, filenameOrId, notify);
    }
}

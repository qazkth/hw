/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client.model;

import common.FileTransfer;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.rmi.UnknownHostException;

/**
 *
 * @author Oscar
 */
public class ServerHandler {
    private boolean connected = false;
    private final FileHandler fileHandler = new FileHandler();
    private Socket server;
    private ObjectOutputStream toServer;
    private ObjectInputStream fromServer;
    private final int port = common.FileServer.PORT;
    private final String host = common.FileServer.HOST;
    private final long ssid;

    public ServerHandler(long ssid) throws IOException {
        this.ssid = ssid;
        connect();
    }
    
    private void connect() throws UnknownHostException, IOException {
        try {
            this.server = new Socket(this.host, this.port);
            this.toServer = new ObjectOutputStream(this.server.getOutputStream());
            this.fromServer = new ObjectInputStream(this.server.getInputStream());
            this.toServer.writeLong(this.ssid);
            this.toServer.flush();
        } catch(UnknownHostException ex) {
            throw new UnknownHostException("Could not connect to the server.");
        } catch(IOException ex) {
            throw new IOException("Could not send information over TCP.");
        }
        
        this.connected = true;
    }
    
    public void sendFile(FileTransfer file) throws IOException {
        if(!this.connected) { throw new IOException("The TCP connection is not established..."); }
        
        try {
            this.toServer.writeUnshared(file);
        } catch(IOException ex) {
            throw new IOException("Unable to send the file to the server...");
        }
    }
    
    public void disconnect() {
        this.connected = false;
        try {
            this.server.close();
        } catch(IOException ex) {}
    }
    
    public void receiveFile(String toThisPath) throws IOException {
        if(!this.connected) { throw new IOException("The TCP connection is not established..."); }
        
        try {
            FileTransfer recFile = (FileTransfer) this.fromServer.readObject();
            recFile.setName(toThisPath);
            this.fileHandler.writeFileReceived(recFile);
        } catch (Exception ex) {
            throw new IOException("Unable to receive the file from the server...");
        }

    }
    
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server.startup;

import common.FileTransfer;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.RemoteException;
import server.controller.Controller;

/**
 *
 * @author Oscar
 */
public class TCPServer implements Runnable {
    private boolean connected = false;
    private final Controller contr;
    
    public TCPServer(Controller contr) {
        this.contr = contr;
    }
    
    @Override
    public void run() {
        try{
            ServerSocket listen = new ServerSocket(common.FileServer.PORT);
            System.out.println("TCPServer started");
            
            this.connected = true; 
            
            while(this.connected) {
                Socket client = listen.accept();
                startClient(client);
            }
            
        } catch(IOException ex) {
            System.err.println("Server failure... Make sure that the server isn't already running "
                    + "on the specified port");
        }
    }
    
    private void startClient(Socket client) throws RemoteException {
        try {
            new Thread(new ClientHandler(client, this.contr)).start();
        } catch(RemoteException rex) {
            throw new RemoteException("Unable to start a client: " + rex.getMessage());
        }
    }
    
    
    private class ClientHandler implements Runnable {
        private final Socket clientSocket;
        private final Controller contr;
        private volatile long ssid = 0;

        public ClientHandler(Socket clientSocket, Controller contr) throws RemoteException {
            this.clientSocket = clientSocket;
            this.contr = contr;
        }
        
        @Override
        public void run() {
            try (ObjectOutputStream toClient = new ObjectOutputStream(this.clientSocket.getOutputStream());
                    ObjectInputStream fromClient = new ObjectInputStream(this.clientSocket.getInputStream());) {
                
                System.out.println("Client: " + Thread.currentThread().getId() + " connected");
                while (true) {
                    if (this.ssid == 0) {
                        this.ssid = fromClient.readLong();
                        this.contr.registerOutStreamWithSSID(this.ssid, toClient);
                    }
                    FileTransfer file = (FileTransfer) fromClient.readObject();
                    this.contr.writeFileToDisk(file);
                }
            } catch (Exception ex) {
                System.out.println("client disconnected: " + Thread.currentThread().getId());
            }
        }
        
    }
}

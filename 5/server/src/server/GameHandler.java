/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;


import common.ClientToServer;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import controller.Controller;
import java.io.ObjectInputStream;

/**
 *
 * @author Oscar
 */
public class GameHandler implements Runnable {
    private final Controller contr;
    private boolean connected;
    private final Socket clientSocket;
    
    public GameHandler(Controller contr, Socket socket) {
        this.contr = contr;
        this.clientSocket = socket;
        this.connected = true;
    }

    @Override
    public void run() {
        try (ObjectOutputStream toPlayer = new ObjectOutputStream(this.clientSocket.getOutputStream());
            ObjectInputStream fromPlayer = new ObjectInputStream(this.clientSocket.getInputStream());
            ) 
        {
            System.out.print("Client: " + Thread.currentThread().getId() + " connected ");
            System.out.println("from IP: " + this.clientSocket.getInetAddress().toString());
            while(this.connected) {
                toPlayer.writeUnshared(this.contr.guess((ClientToServer)fromPlayer.readObject()));
            }
        } catch (Exception ex) {
            disconnectClient();
        }

    }
    
    
    private void disconnectClient() {
        try {
            this.clientSocket.close();
            this.contr.disconnect(Thread.currentThread().getId());
            System.out.println("Client: " + Thread.currentThread().getId() + " disconnected");
        } catch (IOException ioe) {}
        this.connected = false;
    }
    
}

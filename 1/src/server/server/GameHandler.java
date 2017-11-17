/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server.server;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.net.Socket;
import server.controller.Controller;

/**
 *
 * @author Oscar
 */
public class GameHandler implements Runnable {
    private Controller contr;
    private boolean connected;
    private Socket clientSocket;
    
    public GameHandler(Controller contr, Socket socket) {
        this.contr = contr;
        this.clientSocket = socket;
        this.connected = true;
    }

    @Override
    public void run() {
        try (ObjectOutputStream toPlayer = new ObjectOutputStream(this.clientSocket.getOutputStream());
            BufferedReader fromPlayer = new BufferedReader(new InputStreamReader(this.clientSocket.getInputStream()));
            ) 
        {
            System.out.println("Client: " + Thread.currentThread().getId() + " connected");
            while(connected) {
                toPlayer.writeUnshared(this.contr.guess(fromPlayer.readLine()));
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
        connected = false;
    }
    
}

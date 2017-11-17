/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client.server;

import client.view.OutputHandler;
import common.Response;
import common.ServerInfo;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.net.Socket;

/**
 *
 * @author Oscar
 */
public class ServerHandler {
    private boolean clientDisconnected = false;
    private Socket server;
    private PrintWriter toServer;
    private ObjectInputStream fromServer;
    private final int port = ServerInfo.PORT;
    private final String host = ServerInfo.HOST;
    
    public ServerHandler() {}
    
    public boolean connect(OutputHandler outputHandler) {
        try {
            this.server = new Socket(this.host, this.port);
            this.toServer = new PrintWriter(this.server.getOutputStream(), true);
            this.fromServer = new ObjectInputStream(this.server.getInputStream());
            new Thread(new ServerListen(outputHandler)).start();
            sendGuess("");
        } catch (Exception ex) {
            return false;
        }
        return true;
    }
    
    public void disconnect() {
        try {
            this.server.close();
            this.clientDisconnected = true;
        } catch(Exception ex) {}
    }
    
    public void sendGuess(String guess) {
        this.toServer.println(guess);
    }
    
    private class ServerListen implements Runnable{
        private final OutputHandler outputHandler;

        public ServerListen(OutputHandler outputHandler) {
            this.outputHandler = outputHandler;
        }
        
        @Override
        public void run() {
            try {
                while(true) {
                    this.outputHandler.handleResponse((Response)fromServer.readObject());
                }
            } catch(Exception ex) {
                if(!clientDisconnected) {
                    Response disconnected = new Response("\n\t-- Disconnected from server....\n"
                            + "\t-- restart the application: ");
                    this.outputHandler.handleResponse(disconnected);
                }

            }
        }
        
    }
}

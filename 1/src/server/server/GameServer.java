/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server.server;

import common.ServerInfo;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import server.controller.Controller;

/**
 *
 * @author Oscar
 */
public class GameServer {
    private final int port = ServerInfo.PORT;
    private final Controller contr = new Controller();
    
    public static void main(String[] args) {
        new GameServer().serve();
    }
    
    private void serve() {
        try{
            ServerSocket listen = new ServerSocket(this.port);
            System.out.println("Server started");
            
            while(true) {
                Socket client = listen.accept();
                startClient(client);
            }
            
        } catch(IOException ex) {
            System.err.println("Server failure... Make sure that the server isn't already running "
                    + "on the specified port");
        }
    }
    
    private void startClient(Socket client) {
        new Thread(new GameHandler(contr, client)).start();
    }
}

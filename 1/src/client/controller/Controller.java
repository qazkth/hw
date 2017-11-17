/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client.controller;

import client.server.ServerHandler;
import client.view.OutputHandler;

/**
 *
 * @author Oscar
 */
public class Controller {
    ServerHandler server = new ServerHandler();
    
    public boolean connect(OutputHandler outputHandler) {
        return this.server.connect(outputHandler);
    }
    
    public void guess(String guess) {
        this.server.sendGuess(guess);
    }
    
    public void disconnect() {
        this.server.disconnect();
    }
}

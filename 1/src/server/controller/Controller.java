/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server.controller;

import java.util.HashMap;
import server.model.GameLogic;
import server.model.GameWord;

/**
 *
 * @author Oscar
 */
public class Controller {
    private final GameWord word = new GameWord();
    private final HashMap<Long, GameLogic> clients = new HashMap<>();
    
    public synchronized Object guess(String input) {
        this.clients.putIfAbsent(Thread.currentThread().getId(), new GameLogic(this.word));
        return this.clients.get(Thread.currentThread().getId()).userInput(input);
    }
    
    public synchronized void disconnect(long clientId) {
        this.clients.remove(clientId);
    }
    
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.util.HashMap;
import model.GameLogic2;
import model.GameWord;

/**
 *
 * @author Oscar
 */
public class Controller {
    private final GameWord word = new GameWord();
    private final HashMap<Long, GameLogic2> clients = new HashMap<>();
    
    public synchronized Object guess(Object input) {
        this.clients.putIfAbsent(Thread.currentThread().getId(), new GameLogic2(this.word));
        return this.clients.get(Thread.currentThread().getId()).userInputObject(input);
    }
    
    public synchronized void disconnect(long clientId) {
        this.clients.remove(clientId);
    }
    
}

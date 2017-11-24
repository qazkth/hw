/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server.controller;

import server.model.GameLogic;
import server.model.GameWord;

/**
 *
 * @author Oscar
 */
public class Controller {
    private final GameWord wordHandler;
    private final GameLogic game;
    
    public Controller(GameWord wordHandler) {
        this.wordHandler = wordHandler;
        this.game = new GameLogic(this.wordHandler);
    }
    
    public Object guess(String guess) {
        return this.game.userInput(guess);
    }
    
}

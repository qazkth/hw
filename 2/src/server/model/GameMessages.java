/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server.model;

/**
 *
 * @author Oscar
 */
class GameMessages {
    static final String NEWGAME = "Would you like to start a new game? (y/n): ";
    static final String WIN = "\n - Congratulations, you win! - ";
    static final String LOSE = "\n - Too bad, you lose! - ";
    static final String SHUTDOWN = "You have chosen to quit so you can no longer play a game. GOOD BYE!"
            + "\n\t-- quit your application: ";
    static final String ERRORREADINGFILE = "\n\t --*-- SOMETHING WENT WRONG FETCHING WORDS --*-- "
            + "\n\t --*-- PLEASE CONTACT THE GAME SUPPORT --*-- "
            + "\n\t --*-- QUIT YOUR APPLICATION: ";
}

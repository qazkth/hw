/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package common;

import java.io.Serializable;

/**
 *
 * @author Oscar
 */
public class ServerToClient implements Serializable {
    private int triesLeft = 0;
    private int totalWins = 0;
    private String status = "";
    private boolean gameRunning = false;

    public ServerToClient() {
    }

    public ServerToClient(int triesLeft, int totalWins, String status, boolean gameRunning) {
        this.triesLeft = triesLeft;
        this.totalWins = totalWins;
        this.status = status;
        this.gameRunning = gameRunning;
    }

    public int getTriesLeft() {
        return triesLeft;
    }

    public int getTotalWins() {
        return totalWins;
    }

    public String getStatus() {
        return status;
    }

    public boolean isGameRunning() {
        return gameRunning;
    }

    public void setTriesLeft(int triesLeft) {
        this.triesLeft = triesLeft;
    }

    public void setTotalWins(int totalWins) {
        this.totalWins = totalWins;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setGameRunning(boolean gameRunning) {
        this.gameRunning = gameRunning;
    }

}

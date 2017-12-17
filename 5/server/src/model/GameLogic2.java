/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import common.ClientToServer;
import common.ServerToClient;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author Oscar
 */
public class GameLogic2 {
    private boolean gameRunning = false;
    private boolean hasChosenToQuit = false;
    private final boolean WINBOOLEAN = true;
    private boolean errorReadingWords = false;
    private final GameWord wordHandler;
    private int guessesLeft;
    private int totalWins = 0;
    private String secretWord;
    private char[] guessingWordArr;
    private HashSet<String> guessesMade;
    private final int MAXWORDFETCHTRIES = 5;
    private final ServerToClient response;
    private final Set<String> previousWords = new HashSet<>();

    public GameLogic2(GameWord word) {
        this.wordHandler = word;
        this.response = new ServerToClient();
    }
    
    public Object userInputObject(Object inputObj) {
        if(this.hasChosenToQuit || this.errorReadingWords) { return this.response; }
        
        try {
            ClientToServer clientObj = (ClientToServer) inputObj;
            String input = clientObj.getGuess();
            return userInput(input);
        } catch (Exception ex) {
            return this.response;
        }
    }
    
    private Object userInput(String input) {
        return this.gameRunning ? guess(input) : newGame();
    }
    
    private Object newGame() {
        this.secretWord = this.wordHandler.getWord();
        validateSecretWord();
        
        if(!this.errorReadingWords) {
            this.gameRunning = true;
            this.guessingWordArr = new char[this.secretWord.length()];
            this.guessesMade = new HashSet<>();
            this.guessesLeft = this.secretWord.length();
            this.previousWords.add(this.secretWord);
            initGuessArray();
            updateResponse();
        }
        
        // Uncomment the line below if you want to be able to see the new secret words clients get
        System.out.println("Client: " + Thread.currentThread().getId() + " got new word: " + this.secretWord);
        return this.response;
    }
    
    private Object guess(String guessX) {
        String guess = guessX.toLowerCase();
        
        if(guess.isEmpty() || this.guessesMade.contains(guess)) { 
            return this.response; 
        } else if(guess.length() > 1) { 
            guessWord(guess); 
        } else {
            guessChar(guess.charAt(0));
        }
        
        this.guessesMade.add(guess);
        return this.response;
    }

    private void guessChar(char g) {
        boolean unknownLeft = false;
        boolean correctGuess = false;

        for (int i = 0; i < this.secretWord.length(); i++) {
            if (this.guessingWordArr[i] != '_') { continue; }

            if (this.secretWord.charAt(i) == g) {
                this.guessingWordArr[i] = g;
                correctGuess = true;
                continue;
            }
            unknownLeft = true;
        }
        
        if(!unknownLeft) {
            gameFinished(this.WINBOOLEAN);
        } else {
            guessMade(correctGuess);
        }

    }
    
    private void guessWord(String guess) {
        if(this.secretWord.equals(guess)) {
            gameFinished(this.WINBOOLEAN);
        } else{ 
            guessMade(!this.WINBOOLEAN);
        }
    }

    private void gameFinished(boolean win) {
        String msg = win ? GameMessages.WIN : GameMessages.LOSE;
        this.totalWins += win ? 1 : -1;
        this.gameRunning = false;
        
        updateResponse();
        String finishedGameResponse = this.response.getStatus();
        
        this.response.setStatus(msg + "\n" + finishedGameResponse);
    }
    
    private void guessMade(boolean guessedCorrectly) {
        this.guessesLeft += guessedCorrectly ? 0 : -1;
        
        if(this.guessesLeft == 0) {
            gameFinished(!this.WINBOOLEAN);
        } else {
            updateResponse();
        }
    }
    
    private void initGuessArray() {
        for(int i = 0; i < this.guessingWordArr.length; i++) {
            this.guessingWordArr[i] = '_';
        }
    }
    
    private void updateResponse() {
        StringBuilder sb = new StringBuilder();
        
        char[] workWith = this.gameRunning ? this.guessingWordArr : this.secretWord.toCharArray();
        
        for(char c : workWith) {
            sb.append(c);
            sb.append(" ");
        }
        
        this.response.setStatus(sb.toString());
        this.response.setTriesLeft(this.gameRunning ? this.guessesLeft : 0);
        this.response.setTotalWins(this.totalWins);
        this.response.setGameRunning(this.gameRunning);
    }
    
    private void validateSecretWord() {
        int triesLeft = this.MAXWORDFETCHTRIES;
        
        while(this.previousWords.contains(this.secretWord)) {
            this.secretWord = this.wordHandler.getWord();
        }
                
        while(this.secretWord.isEmpty() && triesLeft > 0) {
            this.secretWord = this.wordHandler.getWord();
            triesLeft--;
        }
        
        if(triesLeft == 0) {
            this.response.setStatus(GameMessages.ERRORREADINGFILE);
            this.errorReadingWords = true;
        }
    }
    
}

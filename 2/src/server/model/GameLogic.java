/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server.model;

import common.Response;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author Oscar
 */
public class GameLogic {
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
    private final int MEXWORDFETCHTRIES = 5;
    private final int wordIndex = 1;
    private final int guessIndex = 3;
    private final int winIndex = 5;
    private final String[] responseArray = {"| ","","| guesses left: ",""," | total wins: ",""," | ","\nguess: "};
    private final Response response;
    private final Set<String> previousWords = new HashSet<>();

    public GameLogic(GameWord word) {
        this.wordHandler = word;
        this.response = new Response("");
    }
    
    public Object userInput(String input) {
        if(this.hasChosenToQuit || this.errorReadingWords) { return this.response; }
        
        return this.gameRunning ? guess(input) : wantsToPlay(input);
    }
    
    private Object newGame() {
        this.gameRunning = true;
        this.secretWord = this.wordHandler.getWord();
        validateSecretWord();
        
        if(!this.errorReadingWords) {
            this.guessingWordArr = new char[this.secretWord.length()];
            this.guessesMade = new HashSet<>();
            this.guessesLeft = this.secretWord.length();
            this.previousWords.add(this.secretWord);
            initGuessArray();
            updateResponse();
        }
        
        return userInput("");
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
    
    private Object wantsToPlay(String desire) {
        switch(desire) {
            case "y":
                return newGame();
            case "n":
                this.hasChosenToQuit = true;
                this.response.setResponse(GameMessages.SHUTDOWN);
                break;
            default:
                this.response.setResponse(GameMessages.NEWGAME);
                break;
        }
        
        return this.response;
    }

    private void gameFinished(boolean win) {
        String msg = win ? GameMessages.WIN : GameMessages.LOSE;
        this.totalWins += win ? 1 : -1;
        this.gameRunning = false;
        
        updateResponse();
        String finishedGameResponse = this.response.getResponse();
        
        this.response.setResponse(msg + "\n" + finishedGameResponse + "\n" + GameMessages.NEWGAME);
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
        
        this.responseArray[this.wordIndex] = sb.toString();
        this.responseArray[this.guessIndex] = this.gameRunning ? Integer.toString(this.guessesLeft) : "";
        this.responseArray[this.winIndex] = Integer.toString(this.totalWins);
        
        int exludeGuessFromArray = this.gameRunning ? 0 : 1;
        
        this.response.setResponse(responseArrayToString(exludeGuessFromArray));
    }
    
    private String responseArrayToString(int excludeGuess) {
        StringBuilder sb = new StringBuilder();
        
        for(int i = 0; i < this.responseArray.length - excludeGuess; i ++) {
            sb.append(this.responseArray[i]);
        }
        
        return sb.toString();
    }
    
    private void validateSecretWord() {
        int triesLeft = this.MEXWORDFETCHTRIES;
        
        while(this.previousWords.contains(this.secretWord)) {
            this.secretWord = this.wordHandler.getWord();
        }
                
        while(this.secretWord.isEmpty() && triesLeft > 0) {
            this.secretWord = this.wordHandler.getWord();
            triesLeft--;
        }
        
        if(triesLeft == 0) {
            this.response.setResponse(GameMessages.ERRORREADINGFILE);
            this.errorReadingWords = true;
        }
    }
    
}

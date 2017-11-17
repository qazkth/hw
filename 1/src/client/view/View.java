/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client.view;

import client.controller.Controller;
import java.util.Scanner;

/**
 *
 * @author Oscar
 */
public class View implements Runnable{
    private final Scanner input = new Scanner(System.in).useDelimiter("\\n");
    private final Controller contr;
    private final BootState bootMode;
    private final OutputHandler outputHandler;
    private boolean connected = false;
    
    public View() {
        this.outputHandler = new OutputHandler();
        this.bootMode = new BootState(this.outputHandler);
        this.contr = new Controller();
    }
    
    @Override
    public void run() {
        
        this.bootMode.welcome();
        String userInput = "";
        
        while(this.bootMode.active() || this.connected) {

            while(this.bootMode.active()) {
                userInput = this.input.next();
                checkExit(userInput);
                this.bootMode.handle(userInput);
            }
            
            if(!this.connected) {
                this.connected = this.contr.connect(this.outputHandler);
                this.bootMode.handleConnectStatus(this.connected);
            }
            
            while(this.connected) {
                userInput = this.input.next();
                checkExit(userInput);
                contr.guess(userInput);
            }
            
        }
        
        this.bootMode.exit();
        
    }
    
    private void checkExit(String userInput) {
        
        if(this.bootMode.wantsToExit(userInput)) {
            while (true) {
                this.bootMode.validateExit();
                switch (this.input.next()) {
                    case "y":
                        this.bootMode.exit();
                        this.contr.disconnect();
                        System.exit(0);
                    case "n":
                        this.outputHandler.previousPrint();
                        return;
                }
            }
            /*this.bootMode.exit();
            this.contr.disconnect();
            System.exit(0);*/
        }
        
    }
    
}

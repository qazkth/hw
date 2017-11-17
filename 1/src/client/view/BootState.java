/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client.view;

/**
 *
 * @author Oscar
 */
public class BootState {
    private boolean active = true;
    private final OutputHandler outputHandler;
    private final String EXITCODE = "0";
    private final String CONNECTCODE = "1";
    private final String INSTRUCTIONCODE = "2";
    private final String PROMPT = "> ";

    public BootState(OutputHandler outputHandler) {
        this.outputHandler = outputHandler;
    }
    
    public void welcome() {
        System.out.println(PrintStatements.WELCOME);
        options();
    }

    private void options() {
        this.outputHandler.print("\n -- Select an option -- \n"
                + this.EXITCODE + ": can be entered at ANY time to quit the program.\n"
                + this.CONNECTCODE + ": connect to the server.\n"
                + this.INSTRUCTIONCODE + ": read game instructions.\n"
                + this.PROMPT);
    }
    
    public boolean active() {
        return this.active;
    }
    
    public void handle(String input) {
        if(input.equals(this.CONNECTCODE)) {
            this.active = false;
        } else if(input.equals(this.INSTRUCTIONCODE)) {
            instructions();
        } else {
            invalidCommand();
        }
    }
    
    public void validateExit() {
        System.out.print(PrintStatements.VALIDATEEXIT);
    }
    
    public void exit() {
        this.outputHandler.print(PrintStatements.GOODBYE);
    }
    
    private void invalidCommand() {
        this.outputHandler.print(PrintStatements.INVALIDINPUT + this.PROMPT);
    }
    
    private void instructions() {
        this.outputHandler.print(PrintStatements.INSTRUCTIONS + this.PROMPT);
    }
    
    private void unableToConnect() {
        this.outputHandler.print(PrintStatements.UNSUCCESSFULCONNECT);
        this.active = true;
        options();
    }
    
    private void connected() {
        this.outputHandler.print(PrintStatements.SUCCESSFULCONNECT);
        this.active = false;
    }
    
    public void handleConnectStatus(boolean status) {
        if(status) {
            connected();
        } else {
            unableToConnect();
        }
    }
    
    public boolean wantsToExit(String input) {
        return input.equals(this.EXITCODE);
    }
    
}

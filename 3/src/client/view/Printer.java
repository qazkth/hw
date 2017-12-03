/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client.view;

import common.FileServer;

/**
 *
 * @author Oscar
 */
class Printer {
    
    void print(String msg) {
        System.out.print(msg);
    }
    
    void println(String msg) {
        System.out.println(msg);
    }

    void welcome() {
        println(UIMessages.WELCOME);
    }
    
    void goodbye() {
        println(UIMessages.GOODBYE);
    }
    
    private void options() {
        for(String opt : UIMessages.OPTIONS) {
            println(opt);
        }
        print(UIMessages.PROMPT);
    }
    
    void invalidInput() {
        println(UIMessages.INVALID_OPTION);
        print(UIMessages.PROMPT);
    }
    
    void instructions() {
        println(UIMessages.INSTRUCTIONS);
        
    }
    
    void operationFailed() {
        println(UIMessages.OPERATION_FAILED);
    }
    
    void connectionError() {
        println(UIMessages.CONNECTION_FAILED);
    }
    
    void bootState() {
        options();
    }
    
    void operationState() {
        println("");
        println(UIMessages.OPERATION_STATE_PRINT);
    }
    
    void loginState() {
        println(UIMessages.LOGIN_STATE_PRINT);
    }
    
    void registerState() {
        println(UIMessages.REGISTER_STATE_PRINT);
    }
    
    void validServerOperations() {
        for(int i = 0; i < FileServer.ALLOWED_SERVER_OPERATION.length; i++) {
            print((i + 1) + ": ");
            println(FileServer.ALLOWED_SERVER_OPERATION[i]);
        }
        print(UIMessages.PROMPT);
    }
    
    void fileInformation(String type) {
        switch(type) {
            case UIMessages.UPLOAD:
                printFileInformationInput(UIMessages.INFORMATION_PRINT_UPLOAD);
                break;
            case UIMessages.DOWNLOAD:
                printFileInformationInput(UIMessages.INFORMATION_PRINT_DOWNLOAD);
                break;
            case UIMessages.MODIFY:
                printFileInformationInput(UIMessages.INFORMATION_PRINT_MODIFY);
                break;
            case UIMessages.NOTIFY:
                printFileInformationInput(UIMessages.INFORMATION_PRINT_NOTIFY);
                break;
        }
    }
    
    private void printFileInformationInput(String firstPrint) {
        println(firstPrint + ". " + UIMessages.INFORMATION_PRINT_ENTER);
    }
    
    void unregister() {
        println(UIMessages.INFORMATION_PRINT_UNREGISTER + ". " + UIMessages.UNREGISTER_STATE_PRINT);
    }
}

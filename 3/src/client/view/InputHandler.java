/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client.view;

import common.FileDTO;
import java.util.List;

/**
 *
 * @author Oscar
 */
class InputHandler {
    private final Printer printer = new Printer();

    String handleStart(String input) {
                
        switch(input) {
            case UIMessages.EXIT_OPTION:
                return UIMessages.EXIT_STATE;
            case UIMessages.REGISTER_OPTION:
                return UIMessages.REGISTER_STATE;
            case UIMessages.LOGIN_OPTION:
                return UIMessages.LOGIN_STATE;
            case UIMessages.INSTRUCTION_OPTION:
                return UIMessages.INSTRUCTION_STATE;
            default:
                return UIMessages.INVALID_STATE;
        }
    }
    
    boolean wantsToExit(String input) {
        return UIMessages.EXIT_OPTION.equals(input);
    }
    
    void handleList(List<? extends FileDTO> files) {
        this.printer.println("\n -- Number of available files: " + files.size() + " -- ");
        
        for(FileDTO file : files) {
            this.printer.println("File: ID = " + file.getId()
                    + ", name = " + file.getName() 
                    + ", owner = " + file.getOwner() 
                    + ", size = " + file.getSize()
                    + ", public = " + file.isVisible()
                    + ", writeable = " + file.isWritePermission());
        }
        
        this.printer.println(" -- End of filelist --");
    }
}

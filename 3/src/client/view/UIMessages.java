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
public class UIMessages {
    static final String EXIT_OPTION = "0";
    static final String EXIT_OPTION_PRINT = ": Exit";
    //static final String EXIT_OPTION_PRINT = ": Exit at ANY time";
    static final String REGISTER_OPTION = "1";
    static final String REGISTER_OPTION_PRINT = ": Register a new account";
    static final String LOGIN_OPTION = "2";
    static final String LOGIN_OPTION_PRINT = ": Login";
    static final String INSTRUCTION_OPTION = "3";
    static final String INSTRUCTION_OPTION_PRINT = ": Usage instructions";
    
    static final String REGISTER_STATE_PRINT = "Enter credentials for the new account";
    static final String LOGIN_STATE_PRINT = "Enter credentials to log in";
    static final String OPERATION_STATE_PRINT = "You're now in communcation with the server!";
    static final String UNREGISTER_STATE_PRINT = "Enter credentials for the logged in account.";
    
    static final String EXIT_STATE = "0";
    static final String BOOT_STATE = "1";
    static final String REGISTER_STATE = "2";
    static final String LOGIN_STATE = "3";
    static final String OPERATION_STATE = "4";
    static final String INSTRUCTION_STATE = "5";
    static final String INVALID_STATE = "-1";
    
    static final String WELCOME = "\n -- WELCOME TO A DB MANAGER -- \n";
    static final String GOODBYE = "\nThank you for databasing!\n -- GOODBYE -- \n";
    static final String PROMPT = "> ";
    static final String OPERATION_FAILED = "\n --*-- Operation failed --*-- ";
    public static final String CONNECTION_FAILED = "\n --*-- Could not connect to the server, try again later --*-- ";
    
    static final String SERVER_UPLOAD_USERNAME = "Username: ";
    static final String SERVER_UPLOAD_PASSWORD = "Password: ";
    static final String SERVER_UPLOAD_FILENAME = "Name for the file to be stored as: ";
    static final String SERVER_UPLOAD_FILEPATH = "Absolute path to local file to upload: ";
    static final String SERVER_UPLOAD_FILEVISIBLE = "Visible (y/n): ";
    static final String SERVER_UPLOAD_FILEPERMISSION = "Writeable (y/n): ";
    
    static final String SERVER_DOWNLOAD_FILE = "Filename OR ID of the file to download: ";
    static final String SERVER_DOWNLOAD_SAVE_AS = "/ABSOLUTE/PATH/TO/Filename to save file as (with absolut path): ";
    
    static final String SERVER_MODIFY_FILE = "Filename of the file to update: ";
    
    static final String SERVER_NOTIFY_FILE = "Filename OR ID of be notified about: ";
    
    static final String UPLOAD = "upload";
    static final String DOWNLOAD = "download";
    static final String MODIFY = "modify";
    static final String NOTIFY = "notify";
    static final String UNREGISTER = "unregister";
    
    static final String INFORMATION_PRINT_ENTER = "Please enter some file information!";
    
    static final String INFORMATION_PRINT_UPLOAD = "You've chosen to upload a file";
    static final String INFORMATION_PRINT_DOWNLOAD = "You've chosen to download a file";
    static final String INFORMATION_PRINT_MODIFY = "You've chosen to modify a file";
    static final String INFORMATION_PRINT_NOTIFY = "You've chosen to be notified of file updates";
    static final String INFORMATION_PRINT_UNREGISTER = "You've chosen to unregister your account";
    
    static final String SUCCESS_LOGOUT = "\nSuccessfully signed out";
    static final String SUCCESS_UNREGISTER = "\nSuccessfully unregistered the account";
    static final String SUCCESS_UPLOAD = "\nSuccessfully uploaded the file";
    static final String SUCCESS_DOWNLOAD = "\nSuccessfully downloaded the file";
    static final String SUCCESS_MODIFY = "\nSuccessfully modified the file";
    static final String SUCCESS_NOTIFY = "\nYou will now get notified when someone modifies the desired file";
    
    static final String[] OPTIONS = {
        EXIT_OPTION + EXIT_OPTION_PRINT,
        REGISTER_OPTION + REGISTER_OPTION_PRINT,
        LOGIN_OPTION + LOGIN_OPTION_PRINT,
        INSTRUCTION_OPTION + INSTRUCTION_OPTION_PRINT
    };
    
    static final String INSTRUCTIONS = "\n -- INSTRUCTIONS -- \n\n"
            + "blah blah blah\n\n"
            + " ! Press ENTER to continue ! ";
    
    static final String INVALID_OPTION = "Please enter a valid number!";
    static final String PRESS_ENTER_TO_CONTINUE = " ! Press ENTER to continue ! ";
}

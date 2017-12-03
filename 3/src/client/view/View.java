/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client.view;

import client.controller.Controller;
import common.Credentials;
import common.Notify;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.NoSuchFileException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Scanner;
import javax.naming.CommunicationException;

/**
 *
 * @author Oscar
 */
public class View implements Runnable{
    private volatile boolean wantsToUseApplication = true;
    private volatile boolean inOperationState = false;
    private long sessionIdAtServer;
    private final Scanner in = new Scanner(System.in).useDelimiter("\\n");
    private boolean hasContr = false;
    private Controller contr;
    private final Printer printer = new Printer();
    private final InputHandler inputHandler = new InputHandler();
    private volatile String state = UIMessages.BOOT_STATE;
    
    @Override
    public void run() {
        String userInput;
        
        this.printer.welcome();
        
        while(this.wantsToUseApplication) {
            
            try {
                switch(this.state) {
                case UIMessages.EXIT_STATE:
                    this.wantsToUseApplication = false;
                    break;
                case UIMessages.BOOT_STATE:
                    this.printer.bootState();
                    userInput = this.in.next();
                    this.state = this.inputHandler.handleStart(userInput);
                    break;
                case UIMessages.REGISTER_STATE:
                    if (!this.hasContr) {
                        this.contr = new Controller();
                        this.hasContr = true;
                        break;
                    }
                    this.printer.registerState();
                    Credentials regCreds = enterCreds();
                    this.sessionIdAtServer = this.contr.register(regCreds);
                    this.state = UIMessages.OPERATION_STATE;
                    
                    break;
                case UIMessages.LOGIN_STATE:
                    if (!this.hasContr) {
                        this.contr = new Controller();
                        this.hasContr = true;
                        break;
                    }
                    this.printer.loginState();
                    Credentials logCreds = enterCreds();
                    this.sessionIdAtServer = this.contr.login(logCreds);
                    this.state = UIMessages.OPERATION_STATE;
                    
                    break;
                case UIMessages.OPERATION_STATE:
                    this.inOperationState = true;
                    this.printer.operationState();
                    this.printer.validServerOperations();
                    
                    userInput = this.in.next();
                    
                    switch(userInput) {
                        case UIMessages.EXIT_STATE:
                            this.state = UIMessages.EXIT_STATE;
                            break;
                        case "1":
                            this.printer.fileInformation(UIMessages.UPLOAD);
                            uploadFile();
                            this.printer.println(UIMessages.SUCCESS_UPLOAD);
                            break;
                        case "2":
                            this.printer.fileInformation(UIMessages.DOWNLOAD);
                            downloadFile();
                            this.printer.println(UIMessages.SUCCESS_DOWNLOAD);
                            break;
                        case "3":
                            this.printer.fileInformation(UIMessages.MODIFY);
                            modifyFile();
                            this.printer.println(UIMessages.SUCCESS_MODIFY);
                            break;
                        case "4":
                            this.inputHandler.handleList(this.contr.listFiles(this.sessionIdAtServer));
                            break;
                        case "5":
                            this.printer.fileInformation(UIMessages.NOTIFY);
                            fileNotifications();
                            this.printer.println(UIMessages.SUCCESS_NOTIFY);
                            break;
                        case "6":
                            this.contr.logout(this.sessionIdAtServer);
                            setLogout(UIMessages.SUCCESS_LOGOUT);
                            break;
                        case "7":
                            this.printer.unregister();
                            Credentials unregCreds = enterCreds();
                            this.contr.unregister(this.sessionIdAtServer, unregCreds);
                            setLogout(UIMessages.SUCCESS_UNREGISTER);
                            break;
                        default:
                            throw new IllegalArgumentException(UIMessages.INVALID_OPTION);
                    }
                    
                    break;
                case UIMessages.INSTRUCTION_STATE:
                    this.printer.instructions();
                    userInput = this.in.next();
                    this.state = UIMessages.BOOT_STATE;
                    break;
                default:
                    throw new IllegalArgumentException(UIMessages.INVALID_OPTION);
            }
            } catch (Exception ex) {
                this.printer.operationFailed();
                this.printer.println(ex.getMessage());
                this.printer.println("");
                this.state = this.inOperationState ? UIMessages.OPERATION_STATE : UIMessages.BOOT_STATE;
            }
            
        }
        
        this.printer.goodbye();
        System.exit(0);
    }
    
    private void setLogout(String logoutMsg) {
        this.sessionIdAtServer = 0;
        this.inOperationState = false;
        this.state = UIMessages.BOOT_STATE;
        this.printer.println(logoutMsg);
    }
    
    private Credentials enterCreds() {
        String user = userInputGetString(UIMessages.SERVER_UPLOAD_USERNAME);
        this.printer.print(UIMessages.SERVER_UPLOAD_PASSWORD);
        String pass = this.in.next();
        
        return new Credentials(user, pass);
    }
    
    private void uploadFile() throws NoSuchFileException, FileAlreadyExistsException, CommunicationException, IOException   {
        String name = userInputGetString(UIMessages.SERVER_UPLOAD_FILENAME);
        String path = userInputGetString(UIMessages.SERVER_UPLOAD_FILEPATH);
        boolean visible = userInputGetBool(UIMessages.SERVER_UPLOAD_FILEVISIBLE);
        boolean writeable = false;
        if(visible) { writeable = userInputGetBool(UIMessages.SERVER_UPLOAD_FILEPERMISSION); }
        
        this.contr.uploadFile(this.sessionIdAtServer, name, path, visible, writeable);
    }
    
    private void downloadFile() throws RemoteException, IOException, NoSuchFileException, CommunicationException {
        String filenameOrId = userInputGetString(UIMessages.SERVER_DOWNLOAD_FILE);
        String saveAs = userInputGetString(UIMessages.SERVER_DOWNLOAD_SAVE_AS);
        this.contr.downloadFile(this.sessionIdAtServer, filenameOrId, saveAs);
    }
    
    private void modifyFile() throws RemoteException, NoSuchFileException, CommunicationException, IOException {
        String filename = userInputGetString(UIMessages.SERVER_MODIFY_FILE);
        String filepath = userInputGetString(UIMessages.SERVER_UPLOAD_FILEPATH);
        this.contr.modifyFile(this.sessionIdAtServer, filename, filepath);
    }
    
    private void fileNotifications() throws RemoteException, NoSuchFileException, CommunicationException {
        String filenameOrId = userInputGetString(UIMessages.SERVER_NOTIFY_FILE);
        this.contr.notifyFile(this.sessionIdAtServer, filenameOrId, new NotifyMe());
    }
    
    private String userInputGetString(String info) {
        String input = "";
        
        while(input.isEmpty()) {
            this.printer.print(info);
            input = this.in.next();
        }
        
        return input;
    }
    
    private boolean userInputGetBool(String info) {
        String input = "";
        
        while(!input.equals("y") && !input.equals("n")) {
            this.printer.print(info);
            input = this.in.next();
        }
        
        return input.equals("y");
    }
    
    private class NotifyMe extends UnicastRemoteObject implements Notify {

        public NotifyMe() throws RemoteException {}
        
        @Override
        public void notifyMe(String msg) {
            printer.println("\n");
            printer.println((String) msg);
            printer.println(UIMessages.PRESS_ENTER_TO_CONTINUE);
        }
        
    }
    
}

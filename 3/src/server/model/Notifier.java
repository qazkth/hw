/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server.model;

import common.Notify;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Oscar
 */
public class Notifier {
    private final Map<Integer,Notify> files = new HashMap<>();
    
    public void addClient(int fileId, Notify notify) {
        this.files.put(fileId, notify);
        System.out.println("Client added for file: " + fileId);
    }
    
    public void removeClient(int fileId) {
        this.files.remove(fileId);
    }
    
    public void notifyClients(int fileId, String filename) throws RemoteException {
        if(!this.files.containsKey(fileId)) { return; }
        
        Notify client = this.files.get(fileId);
        client.notifyMe("Your file with NAME: " + filename + ", just got modified by a user");
    }
    
    
}

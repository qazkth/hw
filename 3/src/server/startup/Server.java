/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server.startup;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import server.controller.Controller;

/**
 *
 * @author Oscar
 */
public class Server {
    
    public static void main(String[] args) {
        try {
            new Server().startRegistry();
            Controller contr = new Controller();
            Naming.rebind(Controller.SERVER_NAME_IN_REGISTRY, contr);
            System.out.println("Server is running");
            new Thread(new TCPServer(contr)).start();
        } catch (Exception e) {
            System.err.println("Server exception: " + e.toString());
            e.printStackTrace();
        }
    }
    
    private void startRegistry() throws RemoteException {
        try {
            LocateRegistry.getRegistry().list();
        } catch (Exception e) {
            LocateRegistry.createRegistry(Registry.REGISTRY_PORT);
        }
    }

}

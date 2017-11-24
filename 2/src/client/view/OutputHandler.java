/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client.view;

import common.Response;

/**
 *
 * @author Oscar
 */
public class OutputHandler {
    private String previousOutput = "";
    
    synchronized void print(String out) {
        this.previousOutput = out;
        System.out.print(out);
    }
    
    public synchronized void previousPrint() {
        System.out.println(this.previousOutput);
    }
    
    public synchronized void handleResponse(Response response) {
        this.previousOutput = response.getResponse();
        print(response.getResponse());
    }
    
    public synchronized void handleResponse(String response) {
        this.previousOutput = response;
        print(response);
    }
    
}

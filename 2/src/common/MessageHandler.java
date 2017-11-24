/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package common;

import java.util.ArrayDeque;
import java.util.Queue;

/**
 *
 * @author Oscar
 */
public class MessageHandler {
    private final String LENGTH_DELIMITER = "##";
    private StringBuilder parts = new StringBuilder();
    private final Queue<String> completeMsg = new ArrayDeque<>();
    
    public synchronized String getNextString() {
        return this.completeMsg.poll();
    }
    
    public synchronized Response getNextResponse() {
        return new Response(this.completeMsg.poll());
    }
    
    public synchronized void addParts(String part) {
        this.parts.append(part);
        while(rebuildMsg());
    }
    
    public synchronized String createMsgToSend(String msg) {
        String msgLen = Integer.toString(msg.length());
        return String.join(this.LENGTH_DELIMITER, msgLen, msg);
    }
    
    public synchronized boolean hasNext() {
        return !this.completeMsg.isEmpty();
    }
    
    private boolean rebuildMsg() {
        String allParts = this.parts.toString();

        String[] separated = allParts.split(this.LENGTH_DELIMITER);
        
        if(separated[0].equals("0")) { 
            this.parts.delete(0, 3); 
            this.completeMsg.add("");
        }

        if(separated.length < 2) { return false; }
        
        int firstMsgLen = Integer.parseInt(separated[0]);

        if(firstMsgLen >= separated[1].length()) {
            String msg = separated[1].substring(0, firstMsgLen);
            this.completeMsg.add(msg);
            int remove = separated[0].length() + this.LENGTH_DELIMITER.length() + firstMsgLen;
            this.parts.delete(0, remove);
            
            return true;
        }
        
        return false;
    }
    
}

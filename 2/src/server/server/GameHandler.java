/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server.server;


import common.MessageHandler;
import common.Response;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import server.controller.Controller;

/**
 *
 * @author Oscar
 */
public class GameHandler implements Runnable {
    private final SocketChannel client;
    private final Controller contr;
    private final MessageHandler msgHandler;
    private final GameServer server;
    private final ByteBuffer incomingGuess = ByteBuffer.allocateDirect(1024);
    private final Queue<ByteBuffer> outgoingResponse = new ArrayDeque<>();
    
    public GameHandler(Controller contr, SocketChannel socket, GameServer server) {
        this.contr = contr;
        this.client = socket;
        this.msgHandler = new MessageHandler();
        this.server = server;
    }
    
    @Override
    public void run() {
        try {
            while(this.msgHandler.hasNext()) {
                String guess = this.msgHandler.getNextString();
                String response = ((Response) this.contr.guess(guess)).getResponse();
                addOutgoingResponse(response);
            }
        } catch (Exception ex) {
            disconnectClient();
        }
    }
    
    private void disconnectClient() {
        try {
            this.client.close();
            System.out.println("Client: " + Thread.currentThread().getId() + " disconnected");
        } catch (IOException ioe) {}
    }
    
    void sendResponses() {
        ByteBuffer resp;
        try {
            synchronized (this.outgoingResponse) {
                while((resp = this.outgoingResponse.peek()) != null) {
                    this.client.write(resp);
                    
                    if(resp.hasRemaining()) { return; }
                    
                    this.outgoingResponse.poll();
                }
                
            }
        } catch(IOException ex) {
            System.err.println(ex);
        }
    }
    
    void setGuess() {
        try {
            this.incomingGuess.clear();
            int numReadBytes = this.client.read(this.incomingGuess);

            if(numReadBytes == -1) {
                this.server.disconnect(this.client);
                System.out.println("Client: " + this.hashCode() + " disconnected");
            }

            this.incomingGuess.flip();
            byte[] bytes = new byte[this.incomingGuess.remaining()];
            this.incomingGuess.get(bytes);

            this.msgHandler.addParts(new String(bytes));

            CompletableFuture.runAsync(this);
        } catch(IOException ex) {
            System.err.println(ex);
        }
    }
    
    private void addOutgoingResponse(String out) {
        String withHeader = this.msgHandler.createMsgToSend(out);
        
        synchronized(this.outgoingResponse) { 
            this.outgoingResponse.add(ByteBuffer.wrap(withHeader.getBytes())); 
        }
        
        this.server.setWriteable(this.client);
    }
    
}

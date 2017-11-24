/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server.server;

import common.ServerInfo;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayDeque;
import java.util.Queue;
import server.controller.Controller;
import server.model.GameWord;

/**
 *
 * @author Oscar
 */
public class GameServer {
    private final int port = ServerInfo.PORT;
    private final Queue<ByteBuffer> outgoing = new ArrayDeque<>();
    private Selector selector;
    private ServerSocketChannel server;
    private final GameWord wordHandler = new GameWord();
    
    public static void main(String[] args) throws IOException {
        System.out.println("Sever started");
        new GameServer().serve();
    }
    
    private void serve() throws IOException{
        
        this.selector = Selector.open();
        
        this.server = ServerSocketChannel.open();
        this.server.bind(new InetSocketAddress(this.port));
        this.server.configureBlocking(false);
        
        this.server.register(this.selector, SelectionKey.OP_ACCEPT);
                
        while(true) {
            
            this.selector.select();
            for (SelectionKey key : selector.selectedKeys()) {
                selector.selectedKeys().remove(key);
                
                if (!key.isValid()) { continue; }

                if (key.isAcceptable()) {
                    startGameHandler(key);
                } else if (key.isReadable()) {
                    sendToGame(key);
                } else if (key.isWritable()) {
                    receiveFromGame(key);
                }
            }

        }
    }
    
    private void startGameHandler(SelectionKey key) {
        try {
            ServerSocketChannel serverChannel = (ServerSocketChannel) key.channel();
            SocketChannel client = serverChannel.accept();
            client.configureBlocking(false);
            GameHandler game = new GameHandler(new Controller(this.wordHandler), client, this);
            client.register(this.selector, SelectionKey.OP_READ, game);
            System.out.println("Client: " + game.hashCode() + " connected");
        } catch(IOException ex) {
            System.err.println(ex);
        }
    }
    
    void receiveFromGame(SelectionKey key) {
        GameHandler game = (GameHandler) key.attachment();
        game.sendResponses();
        key.interestOps(SelectionKey.OP_READ);
    }
    
    private void sendToGame(SelectionKey key) {
        GameHandler game = (GameHandler) key.attachment();
        game.setGuess();
    }
    
    synchronized void setWriteable(SocketChannel client) {
        SelectionKey key = client.keyFor(this.selector);
        key.interestOps(SelectionKey.OP_WRITE);
        this.selector.wakeup();
    }
    
    void disconnect(SocketChannel client) throws IOException{
        try {
            SelectionKey key = client.keyFor(this.selector);
            key.cancel();
            client.close();
        } catch(IOException ex) {
            System.err.println(ex);
        }
        
    }
    
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client.server;

import client.view.OutputHandler;
import common.MessageHandler;
import common.ServerInfo;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.ArrayDeque;
import java.util.Queue;

/**
 *
 * @author Oscar
 */
public class ServerConnection implements Runnable {

    private volatile boolean connected = false;
    private volatile boolean stuffToSend = true;
    private InetSocketAddress server;
    private SocketChannel socketChannel;
    private final Queue<ByteBuffer> toServer = new ArrayDeque<>();
    private final ByteBuffer fromServer = ByteBuffer.allocateDirect(1024);
    private Selector selector;
    private final int port = ServerInfo.PORT;
    private final String host = ServerInfo.HOST;
    private final MessageHandler msgHandler = new MessageHandler();
    private OutputHandler outputHandler;
    private volatile boolean firstConnect = true;
    private final String DISCONNECTERROR = "\n\t-- Disconnected from server....\n"
            + "\t-- restart the application: ";

    @Override
    public void run() {
        try {
            
            while (this.connected || !this.toServer.isEmpty()) {
                
                if(this.stuffToSend) {
                    this.socketChannel.keyFor(this.selector).interestOps(SelectionKey.OP_WRITE);
                    if(this.firstConnect) { sendGuess(""); this.firstConnect = false; }
                    this.stuffToSend = false;
                }

                this.selector.select();
                for(SelectionKey key : this.selector.selectedKeys()) {
                    this.selector.selectedKeys().remove(key);
                    
                    if (!key.isValid()) { continue; }
                    
                    if (key.isConnectable()) {
                        connectToServer(key);
                    } else if (key.isReadable()) {
                        receiveFromSever(key);
                    } else if (key.isWritable()) {
                        sendToServer(key);
                    }
                }
            }

        } catch (Exception ex) {
            System.err.println(ex);
        }
    }

    public boolean connect(OutputHandler outputHandler) {
        try{
            this.server = new InetSocketAddress(this.host, this.port);
            this.outputHandler = outputHandler;
            this.selector = Selector.open();
            
            this.socketChannel = SocketChannel.open(this.server);
            this.socketChannel.configureBlocking(false);
            this.socketChannel.register(this.selector, SelectionKey.OP_CONNECT);
            
            this.connected = true;
            new Thread(this).start();
        } catch (Exception ex) {
            return false;
        }
        return true;
    }

    public boolean disconnect() {
        this.connected = false;
        return !this.connected;
    }

    public void sendGuess(String guess) {
        byte[] send = this.msgHandler.createMsgToSend(guess).getBytes();
        ByteBuffer buffer = ByteBuffer.wrap(send);

        synchronized (this.toServer) {
            this.toServer.add(buffer);
        }
        
        this.stuffToSend = true;
        this.selector.wakeup();
    }

    private void connectToServer(SelectionKey key) {
        try {
            this.socketChannel.finishConnect();
            key.interestOps(SelectionKey.OP_READ);
        } catch (IOException ex) {
            System.err.println(ex);
        }
    }

    private void sendToServer(SelectionKey key) {
        try {
            ByteBuffer buffer;
            synchronized (this.toServer) {
                while((buffer = this.toServer.peek()) != null) {
                    this.socketChannel.write(buffer);
                    if(buffer.hasRemaining()) { return; }
                    this.toServer.remove();
                }
                key.interestOps(SelectionKey.OP_READ);
            }
        } catch (Exception e) {
            System.err.println(e);
        }
    }

    private void receiveFromSever(SelectionKey key) {
        try {
            this.fromServer.clear();
            int bytesRead = this.socketChannel.read(this.fromServer);
            
            if(bytesRead == -1) { 
                key.cancel();
                this.socketChannel.close();
                disconnect();
                this.outputHandler.handleResponse(this.DISCONNECTERROR);
            }

            this.fromServer.flip();
            byte[] bytes = new byte[this.fromServer.remaining()];
            this.fromServer.get(bytes);
            
            String received = new String(bytes);
            this.msgHandler.addParts(received);

            while(this.msgHandler.hasNext()) {
                this.outputHandler.handleResponse(this.msgHandler.getNextResponse());
            }
        } catch (Exception e) {
            System.err.println(e);
        }
    }

}

package oscar.hw5_hangman;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

import common.ServerInfo;

class ServerConnection implements Runnable {
    protected final UpdateResponse gui;
    private ObjectInputStream fromServer;
    private ObjectOutputStream toServer;
    private volatile boolean connected = false;
    private Socket client;
    private final int MAXCONNECTSECS = 4000;

    ServerConnection(final UpdateResponse gui) {
        this.gui = gui;
    }

    @Override
    public void run() {
        while(connected) {
            try {
                gui.updateServerResponse(fromServer.readObject());
            } catch (Exception e) {
                disconnect();
            }
        }
    }

    void disconnect() {
        try {
            client.close();
        } catch(IOException ex) {}
        connected = false;
    }

    boolean isConnected() {
        return connected;
    }

    void connect() throws Exception {
        client = new Socket();
        client.connect(new InetSocketAddress(ServerInfo.HOST, ServerInfo.PORT), MAXCONNECTSECS);
        toServer = new ObjectOutputStream(client.getOutputStream());
        fromServer = new ObjectInputStream(client.getInputStream());
        connected = true;
    }

    void sendGuess(Object guess) {
        try {
            toServer.writeUnshared(guess);
        } catch (IOException e) {
            disconnect();
        }
    }
}

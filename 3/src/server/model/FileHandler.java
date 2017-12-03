/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server.model;

import common.FileTransfer;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Oscar
 */
public class FileHandler {
    private final String SEP = System.getProperty("file.separator");
    private final String FILE_DIRECTORY = "server" + this.SEP + "filedirectory" + this.SEP;
    private final String PROJ_DIR = System.getProperty("user.dir") + this.SEP;
    private final String DIRECTORY_PATH = this.PROJ_DIR + this.FILE_DIRECTORY;
    private final Map<Long, ObjectOutputStream> clients = Collections.synchronizedMap(new HashMap<>());
    
    public void writeFileToDisk(FileTransfer recFile) throws IOException {
        System.out.println(this.DIRECTORY_PATH + recFile.getName());
        
        String dirPath = this.DIRECTORY_PATH + recFile.getName();
        Path path = Paths.get(dirPath);
        
        try {
            Files.write(path, recFile.getData());
        } catch (Exception ex) {
            throw new IOException("Could not store the file on the server... ");
        }
    }
    
    public void writeFileToOutStream(long ssid, String filename) throws IOException {
        try {
            FileTransfer sendThisFile = makeFileToSend(filename);
            this.clients.get(ssid).writeUnshared(sendThisFile);
        } catch(IOException ex) {
            throw new IOException("The server was unable to send the file...");
        }
    }
    
    public void registerClientAndStream(long ssid, ObjectOutputStream stream) {
        this.clients.put(ssid, stream);
    }
    
    public void removeClientAndStream(long ssid) {
        this.clients.remove(ssid);
    }
    
    private FileTransfer makeFileToSend(String filename) throws IOException {
        byte[] data = {};
        String path = this.DIRECTORY_PATH + filename;
        try {
            FileInputStream fis = new FileInputStream(path);
            fis.read(data = new byte[fis.available()]);
        } catch (IOException ex) {
            throw new IOException("Unable to read the file on the server...");
        }
        
        return new FileTransfer(filename, data);
    }
    
}

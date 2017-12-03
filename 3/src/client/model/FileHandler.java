/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client.model;

import common.FileTransfer;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.NoSuchFileException;

/**
 *
 * @author Oscar
 */
public class FileHandler {
    private FileInputStream fis;
    private FileOutputStream fos;
    
    private byte[] readFileToSend(String path) throws FileNotFoundException, IOException {
        byte[] file = {};
        try {
            this.fis = new FileInputStream(path);
            this.fis.read(file = new byte[this.fis.available()]);
        } catch(FileNotFoundException fnfex) {
            throw new FileNotFoundException("Could not find the file: " + path);
        } catch(IOException ioex) {
            throw new IOException("Unable to read the file: " + path);
        }
        
        return file;
    }
    
    public void writeFileReceived(FileTransfer file) throws IOException {
        try {
            this.fos = new FileOutputStream(file.getName());
            this.fos.write(file.getData());
            this.fos.flush();
        } catch(IOException ioex) {
            throw new IOException("unable to write file: " + file.getName());
        }
    }
    
    public void setFileSize(FileMetadata mdata) throws NoSuchFileException {
        File file = new File(mdata.getPath());
        try {
            mdata.setSize((int) file.length());
        } catch(NullPointerException nex) {
            throw new NoSuchFileException("There is no file at path: " + mdata.getPath());
        }
    }
    
    public FileTransfer createFileTransfer(String filename, String filepath) throws IOException{
        byte[] filedata = readFileToSend(filepath);
        return new FileTransfer(filename, filedata);
    }
    
}

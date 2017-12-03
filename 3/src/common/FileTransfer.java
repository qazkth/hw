/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package common;

import java.io.Serializable;

/**
 *
 * @author Oscar
 */
public class FileTransfer implements Serializable {
    private String name;
    private final byte[] data;

    public FileTransfer(String name, byte[] data) {
        this.name = name;
        this.data = data;
    }

    public String getName() {
        return this.name;
    }

    public byte[] getData() {
        return this.data;
    }
    
    public void setName(String name) {
        this.name = name;
    }
}

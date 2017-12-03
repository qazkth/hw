/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client.model;

import common.FileDTO;

/**
 *
 * @author Oscar
 */
public class FileMetadata implements FileDTO {
    private transient final String path;
    private final int id = 0;
    private final String owner = "";
    private final String name;
    private int size;
    private final boolean visible;
    private final boolean writeable;

    public FileMetadata(String name, String path, boolean visible, boolean writeable) {
        this.name = name;
        this.path = path;
        this.visible = visible;
        this.writeable = writeable;
    }

    @Override
    public int getId() {
        return this.id;
    }

    @Override
    public int getSize() {
        return this.size;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getOwner() {
        return this.owner;
    }

    @Override
    public boolean isWritePermission() {
        return this.writeable;
    }

    @Override
    public boolean isVisible() {
        return this.visible;    }
    
    public String getPath() {
        return this.path;
    }
    
    public void setSize(int size) {
        this.size = size;
    }
}

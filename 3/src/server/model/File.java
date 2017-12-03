/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server.model;

import common.FileDTO;
import common.FileTransfer;

/**
 *
 * @author Oscar
 */
public class File implements FileDTO{
    private int id;
    private final String owner;
    private final String name;
    private final boolean visible;
    private final boolean writePermission;
    private final int size;

    public File(int id, String owner, String name, int size, boolean visible, boolean writePermission) {
        this.id = id;
        this.owner = owner;
        this.name = name;
        this.visible = visible;
        this.writePermission = writePermission;
        this.size = size;
    }

    @Override
    public String getOwner() {
        return this.owner;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public boolean isWritePermission() {
        return this.writePermission;
    }

    @Override
    public int getSize() {
        return this.size;
    }

    @Override
    public int getId() {
        return this.id;
    }

    @Override
    public boolean isVisible() {
        return this.visible;
    }
    
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package common;

import java.io.Serializable;
import java.util.List;

/**
 *
 * @author Oscar
 */
public class Response implements Serializable{
    private final boolean success;
    private final String response;
    private final List<? extends FileDTO> files;

    public Response(boolean success, String message, List<? extends FileDTO> files) {
        this.success = success;
        this.response = message;
        this.files = files;
    }

    public boolean isSuccess() {
        return this.success;
    }

    public String getResponse() {
        return this.response;
    }
    
    public boolean hasList() {
        return this.files != null && !this.files.isEmpty();
    }
    
    public List<? extends FileDTO> getFiles() {
        return this.files;
    }
}

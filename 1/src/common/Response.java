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
public class Response implements Serializable {
    private String response;

    public Response(String message) {
        this.response = message;
    }

    public void setResponse(String response) {
        this.response = response;
    }
    
    public String getResponse() {
        return response;
    }
    
}

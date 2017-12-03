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
public interface FileDTO extends Serializable{
    
    int getId();
    
    int getSize();
    
    String getName();
    
    String getOwner();
    
    boolean isWritePermission();
    
    boolean isVisible();
    
}

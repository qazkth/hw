/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client.startup;

import client.view.View;

/**
 *
 * @author Oscar
 */
public class Main {
    public static void main(String[] args) {
        new Thread(new View()).start();
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

/**
 *
 * @author Oscar
 */
public class GameWord {
    private final String WORDFILEPATH = System.getProperty("user.dir").concat("/WordDirectory/words.txt");
    private final ArrayList<String> words = new ArrayList<>();
    private final Random rand;

    public GameWord() {
        this.rand = new Random(System.currentTimeMillis());
        initWords();
    }
    
    public synchronized String getWord() {
        return this.words.get(this.rand.nextInt(this.words.size()));
    }
    
    private void initWords() {
        try(BufferedReader fromFile = new BufferedReader(new FileReader(WORDFILEPATH))) {
            fromFile.lines().forEachOrdered(line -> this.words.add(line));
        } catch(IOException ex) {
            this.words.add("");
        }
    }

}

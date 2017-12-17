/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

/**
 *
 * @author Oscar
 */
@NamedQueries({
    @NamedQuery(
            name = "findPair",
            query = "SELECT p FROM Pair p WHERE p.base LIKE :base AND p.quote LIKE :quote"
    )
})

@Entity
public class Pair implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String base;
    private String quote;
    private double rate;
    
    public Pair () {
    }

    public Pair(int id, String base, String quote, double rate) {
        this.id = id;
        this.base = base;
        this.quote = quote;
        this.rate = rate;
    }

    public int getId() {
        return id;
    }

    public String getBase() {
        return base;
    }

    public String getQuote() {
        return quote;
    }

    public double getRate() {
        return rate;
    }
    
}

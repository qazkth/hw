/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

/**
 *
 * @author Oscar
 */
@NamedQueries({
    @NamedQuery(
            name = "findAllCurrencies",
            query = "SELECT c FROM Currency c"
    )
})

@Entity
public class Currency implements CurrencyDTO, Serializable {
    @Id
    private String code;
    private String name;

    public Currency() {
    }

    public Currency(String code, String name) {
        this.code = code;
        this.name = name;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getName() {
        return name;
    }
    
}

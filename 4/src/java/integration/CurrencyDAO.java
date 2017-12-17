/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package integration;

import java.util.ArrayList;
import java.util.List;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import model.Currency;
import model.CurrencyDTO;
import model.Pair;

/**
 *
 * @author Oscar
 */
@TransactionAttribute(TransactionAttributeType.MANDATORY)
@Stateless
public class CurrencyDAO {
    @PersistenceContext(unitName = "currencyPU")
    private EntityManager em;
    
    public double findConversionRate(String base, String quote) throws Exception {
        double rate = 0;
        Pair pair;
        
        if(base.equals(quote)) { return 1; }
        
        if((pair = findId(base, quote)) != null) {
            rate = pair.getRate();
        } else if((pair = findId(quote, base)) != null) {
            rate = 1.0 / pair.getRate();
        } else {
            throw new Exception("There is no such conversion pair in the database.");
        }
        
        return rate;
    }
    
    private Pair findId(String base, String quote) { 
        try {
            return em.createNamedQuery("findPair", Pair.class).setParameter("base", base).setParameter("quote", quote).getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }
    
    public List<? extends CurrencyDTO> listCurrencies() {
        try {
            return em.createNamedQuery("findAllCurrencies", Currency.class).getResultList();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }
    
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import integration.CurrencyDAO;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import model.Calculator;
import model.CurrencyDTO;

/**
 *
 * @author Oscar
 */
@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
@Stateless
public class ConversionFacade {
    @EJB CurrencyDAO currencyDB;
    private final Calculator calculator = new Calculator();
    
    public double convert(String base, String quote, double amount) throws Exception {
        double rate = currencyDB.findConversionRate(base, quote);
        return calculator.calculateNewCurrency(amount, rate);
    }
    
    public List<String> listCurrencies() {
        return listCurrenciesToStringList(currencyDB.listCurrencies());
    }
    
    private List<String> listCurrenciesToStringList(List<? extends CurrencyDTO> currencies) {
        List<String> retCurrencies = new ArrayList<>();
        for(CurrencyDTO currency : currencies) {
            retCurrencies.add(currency.getCode());
        }

        return retCurrencies;
    }
    
}

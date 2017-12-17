/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view;

import controller.ConversionFacade;
import java.io.Serializable;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.inject.Named;

/**
 *
 * @author Oscar
 */
@Named("convertManager")
@RequestScoped
public class ConvertManager implements Serializable {
    @EJB
    private ConversionFacade conversionFacade;
    private List<String> currencies;
    private String base;
    private String quote;
    private double amount;
    private double result;
    private Exception conversionFailure;

    @PostConstruct
    public void init() {
        this.currencies = conversionFacade.listCurrencies();
    }

    public void convert() {
        try {
            conversionFailure = null;
            result = conversionFacade.convert(base, quote, amount);
        } catch(Exception ex) {
            conversionFailure = ex;
        }
    }

    public Exception getException() {
        return conversionFailure;
    }
    
    public boolean getSuccess() {
        return conversionFailure == null;
    }

    public void setConversionFailure(Exception conversionFailure) {
        this.conversionFailure = conversionFailure;
    }
    
    public List<String> getCurrencies() {
        return currencies;
    }
    
    public double getResult() {
        return result;
    }

    public String getBase() {
        return base;
    }

    public String getQuote() {
        return quote;
    }

    public double getAmount() {
        return amount;
    }

    public void setBase(String base) {
        this.base = base;
    }

    public void setQuote(String quote) {
        this.quote = quote;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public void setResult(double result) {
        this.result = result;
    }
    
}

package com.example.ejb;

import jakarta.ejb.Stateless;
import java.util.logging.Logger;

/**
 * A simple stateless EJB that performs calculations
 */
@Stateless
public class CalculatorBean {
    
    private static final Logger LOGGER = Logger.getLogger(CalculatorBean.class.getName());

    public int add(int a, int b) {
        LOGGER.info("CalculatorBean.add() called with: " + a + " + " + b);
        return a + b;
    }

    public int subtract(int a, int b) {
        LOGGER.info("CalculatorBean.subtract() called with: " + a + " - " + b);
        return a - b;
    }

    public int multiply(int a, int b) {
        LOGGER.info("CalculatorBean.multiply() called with: " + a + " * " + b);
        return a * b;
    }

    public double divide(int a, int b) {
        LOGGER.info("CalculatorBean.divide() called with: " + a + " / " + b);
        if (b == 0) {
            throw new ArithmeticException("Cannot divide by zero");
        }
        return (double) a / b;
    }

    public String getServerInfo() {
        String nodeName = System.getProperty("jboss.node.name", "unknown");
        String hostName = System.getProperty("jboss.host.name", "unknown");
        return "Processed by Host: " + hostName + ", Node: " + nodeName;
    }
}

package com.example.servlet;

import com.example.ejb.CalculatorBean;
import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Servlet to test EJB functionality
 */
@WebServlet("/ejb")
public class EjbTestServlet extends HttpServlet {
    
    @EJB
    private CalculatorBean calculator;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        
        // Get parameters or use defaults
        int a = getIntParam(request, "a", 10);
        int b = getIntParam(request, "b", 5);
        
        out.println("<!DOCTYPE html>");
        out.println("<html><head><title>EJB Test</title>");
        out.println("<style>");
        out.println("body { font-family: Arial, sans-serif; margin: 20px; background: #f5f5f5; }");
        out.println(".container { max-width: 800px; margin: 0 auto; background: white; padding: 20px; border-radius: 8px; box-shadow: 0 2px 4px rgba(0,0,0,0.1); }");
        out.println("h1 { color: #333; }");
        out.println(".result { background: #e8f5e9; padding: 15px; border-radius: 4px; margin: 10px 0; }");
        out.println(".server-info { background: #e3f2fd; padding: 15px; border-radius: 4px; margin: 10px 0; }");
        out.println("form { margin: 20px 0; padding: 15px; background: #fff3e0; border-radius: 4px; }");
        out.println("input[type=number] { padding: 8px; margin: 5px; width: 80px; }");
        out.println("input[type=submit] { padding: 10px 20px; background: #1976d2; color: white; border: none; border-radius: 4px; cursor: pointer; }");
        out.println("a { color: #1976d2; }");
        out.println("</style></head><body>");
        
        out.println("<div class='container'>");
        out.println("<h1>🧮 EJB Calculator Test</h1>");
        
        out.println("<div class='server-info'>");
        out.println("<strong>Server Info:</strong> " + calculator.getServerInfo());
        out.println("</div>");
        
        out.println("<form method='get'>");
        out.println("A: <input type='number' name='a' value='" + a + "'>");
        out.println("B: <input type='number' name='b' value='" + b + "'>");
        out.println("<input type='submit' value='Calculate'>");
        out.println("</form>");
        
        out.println("<div class='result'>");
        out.println("<h3>Results for A=" + a + ", B=" + b + ":</h3>");
        out.println("<p><strong>Add:</strong> " + a + " + " + b + " = " + calculator.add(a, b) + "</p>");
        out.println("<p><strong>Subtract:</strong> " + a + " - " + b + " = " + calculator.subtract(a, b) + "</p>");
        out.println("<p><strong>Multiply:</strong> " + a + " * " + b + " = " + calculator.multiply(a, b) + "</p>");
        try {
            out.println("<p><strong>Divide:</strong> " + a + " / " + b + " = " + calculator.divide(a, b) + "</p>");
        } catch (ArithmeticException e) {
            out.println("<p><strong>Divide:</strong> Error - " + e.getMessage() + "</p>");
        }
        out.println("</div>");
        
        out.println("<p><a href='/ejb-jms-app/'>← Back to Home</a></p>");
        out.println("</div>");
        out.println("</body></html>");
    }

    private int getIntParam(HttpServletRequest request, String name, int defaultValue) {
        String value = request.getParameter(name);
        if (value != null && !value.isEmpty()) {
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException e) {
                return defaultValue;
            }
        }
        return defaultValue;
    }
}

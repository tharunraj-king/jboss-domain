package com.example.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Home page servlet
 */
@WebServlet("")
public class HomeServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        
        String hostName = System.getProperty("jboss.host.name", "unknown");
        String nodeName = System.getProperty("jboss.node.name", "unknown");
        
        out.println("<!DOCTYPE html>");
        out.println("<html><head><title>EJB/JMS Demo Application</title>");
        out.println("<style>");
        out.println("body { font-family: Arial, sans-serif; margin: 0; padding: 0; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); min-height: 100vh; }");
        out.println(".container { max-width: 800px; margin: 0 auto; padding: 40px 20px; }");
        out.println(".card { background: white; padding: 30px; border-radius: 12px; box-shadow: 0 4px 6px rgba(0,0,0,0.1); margin-bottom: 20px; }");
        out.println("h1 { color: #333; margin-top: 0; }");
        out.println(".server-badge { display: inline-block; background: #e3f2fd; padding: 10px 20px; border-radius: 20px; margin: 10px 0; }");
        out.println(".menu { display: flex; gap: 20px; flex-wrap: wrap; }");
        out.println(".menu-item { flex: 1; min-width: 200px; background: #f8f9fa; padding: 20px; border-radius: 8px; text-decoration: none; color: #333; transition: transform 0.2s, box-shadow 0.2s; }");
        out.println(".menu-item:hover { transform: translateY(-2px); box-shadow: 0 4px 12px rgba(0,0,0,0.15); }");
        out.println(".menu-item h3 { margin: 0 0 10px 0; color: #1976d2; }");
        out.println(".menu-item p { margin: 0; color: #666; font-size: 14px; }");
        out.println(".emoji { font-size: 2em; margin-bottom: 10px; }");
        out.println(".info { background: #fff3e0; padding: 15px; border-radius: 8px; margin-top: 20px; }");
        out.println("</style></head><body>");
        
        out.println("<div class='container'>");
        out.println("<div class='card'>");
        out.println("<h1>🚀 EJB/JMS Demo Application</h1>");
        out.println("<div class='server-badge'>");
        out.println("<strong>Host:</strong> " + hostName + " | <strong>Node:</strong> " + nodeName);
        out.println("</div>");
        
        out.println("<div class='menu'>");
        
        out.println("<a href='/ejb-jms-app/ejb' class='menu-item'>");
        out.println("<div class='emoji'>🧮</div>");
        out.println("<h3>EJB Calculator</h3>");
        out.println("<p>Test Stateless Session Bean with basic arithmetic operations</p>");
        out.println("</a>");
        
        out.println("<a href='/ejb-jms-app/jms' class='menu-item'>");
        out.println("<div class='emoji'>📨</div>");
        out.println("<h3>JMS Messaging</h3>");
        out.println("<p>Send and receive messages via JMS Queue with MDB</p>");
        out.println("</a>");
        
        out.println("</div>");
        
        out.println("<div class='info'>");
        out.println("<h4>About this Demo</h4>");
        out.println("<p>This application demonstrates:</p>");
        out.println("<ul>");
        out.println("<li><strong>EJB (Enterprise JavaBeans):</strong> Stateless session beans for business logic</li>");
        out.println("<li><strong>JMS (Java Message Service):</strong> Asynchronous messaging with queues</li>");
        out.println("<li><strong>MDB (Message-Driven Bean):</strong> Asynchronous message processing</li>");
        out.println("<li><strong>WildFly Domain Mode:</strong> Clustered deployment across multiple workers</li>");
        out.println("</ul>");
        out.println("<p>Refresh the page multiple times to see load balancing across workers!</p>");
        out.println("</div>");
        
        out.println("</div>");
        out.println("</div>");
        out.println("</body></html>");
    }
}

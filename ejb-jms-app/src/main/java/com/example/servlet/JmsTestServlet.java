package com.example.servlet;

import com.example.jms.MessageProducer;
import com.example.jms.MessageStore;
import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

/**
 * Servlet to test JMS functionality
 */
@WebServlet("/jms")
public class JmsTestServlet extends HttpServlet {
    
    @EJB
    private MessageProducer messageProducer;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String action = request.getParameter("action");
        String result = null;
        
        if ("send".equals(action)) {
            String message = request.getParameter("message");
            if (message != null && !message.isEmpty()) {
                messageProducer.sendMessage(message);
                result = "Message sent: " + message;
            }
        } else if ("sendMultiple".equals(action)) {
            int count = 5;
            try {
                count = Integer.parseInt(request.getParameter("count"));
            } catch (Exception e) {}
            messageProducer.sendMultipleMessages(count);
            result = count + " messages sent!";
        } else if ("clear".equals(action)) {
            MessageStore.getInstance().clear();
            result = "Message store cleared!";
        }
        
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        
        out.println("<!DOCTYPE html>");
        out.println("<html><head><title>JMS Test</title>");
        out.println("<style>");
        out.println("body { font-family: Arial, sans-serif; margin: 20px; background: #f5f5f5; }");
        out.println(".container { max-width: 900px; margin: 0 auto; background: white; padding: 20px; border-radius: 8px; box-shadow: 0 2px 4px rgba(0,0,0,0.1); }");
        out.println("h1 { color: #333; }");
        out.println(".success { background: #e8f5e9; padding: 15px; border-radius: 4px; margin: 10px 0; color: #2e7d32; }");
        out.println(".server-info { background: #e3f2fd; padding: 15px; border-radius: 4px; margin: 10px 0; }");
        out.println(".messages { background: #f5f5f5; padding: 15px; border-radius: 4px; margin: 10px 0; max-height: 400px; overflow-y: auto; }");
        out.println(".message-item { background: white; padding: 10px; margin: 5px 0; border-left: 3px solid #1976d2; }");
        out.println("form { margin: 20px 0; padding: 15px; background: #fff3e0; border-radius: 4px; display: inline-block; margin-right: 10px; }");
        out.println("input[type=text] { padding: 8px; width: 300px; }");
        out.println("input[type=number] { padding: 8px; width: 60px; }");
        out.println("input[type=submit], button { padding: 10px 20px; background: #1976d2; color: white; border: none; border-radius: 4px; cursor: pointer; margin: 5px; }");
        out.println("input[type=submit]:hover, button:hover { background: #1565c0; }");
        out.println("a { color: #1976d2; }");
        out.println(".actions { margin: 20px 0; }");
        out.println("</style>");
        out.println("<script>setTimeout(function(){ if(window.location.search.indexOf('action=') === -1) location.reload(); }, 3000);</script>");
        out.println("</head><body>");
        
        out.println("<div class='container'>");
        out.println("<h1>📨 JMS Messaging Test</h1>");
        
        out.println("<div class='server-info'>");
        out.println("<strong>Current Host:</strong> " + System.getProperty("jboss.host.name", "unknown"));
        out.println(" | <strong>Node:</strong> " + System.getProperty("jboss.node.name", "unknown"));
        out.println("</div>");
        
        if (result != null) {
            out.println("<div class='success'><strong>✓</strong> " + result + "</div>");
        }
        
        out.println("<div class='actions'>");
        
        // Send single message form
        out.println("<form method='get'>");
        out.println("<input type='hidden' name='action' value='send'>");
        out.println("<input type='text' name='message' placeholder='Enter message...' required>");
        out.println("<input type='submit' value='Send Message'>");
        out.println("</form>");
        
        // Send multiple messages form
        out.println("<form method='get'>");
        out.println("<input type='hidden' name='action' value='sendMultiple'>");
        out.println("<input type='number' name='count' value='5' min='1' max='100'>");
        out.println("<input type='submit' value='Send Multiple'>");
        out.println("</form>");
        
        // Clear messages
        out.println("<form method='get' style='display:inline'>");
        out.println("<input type='hidden' name='action' value='clear'>");
        out.println("<input type='submit' value='Clear Messages'>");
        out.println("</form>");
        
        out.println("</div>");
        
        // Display received messages
        List<String> messages = MessageStore.getInstance().getMessages();
        out.println("<h2>Received Messages (" + messages.size() + ")</h2>");
        out.println("<div class='messages'>");
        if (messages.isEmpty()) {
            out.println("<p>No messages received yet. Send some messages to see them here!</p>");
            out.println("<p><em>Note: Messages are processed by the MDB (Message-Driven Bean) and may appear on any worker node.</em></p>");
        } else {
            for (String msg : messages) {
                out.println("<div class='message-item'>" + escapeHtml(msg) + "</div>");
            }
        }
        out.println("</div>");
        
        out.println("<p><a href='/ejb-jms-app/'>← Back to Home</a> | <a href='/ejb-jms-app/jms'>Refresh</a></p>");
        out.println("</div>");
        out.println("</body></html>");
    }
    
    private String escapeHtml(String text) {
        return text.replace("&", "&amp;")
                   .replace("<", "&lt;")
                   .replace(">", "&gt;")
                   .replace("\"", "&quot;");
    }
}

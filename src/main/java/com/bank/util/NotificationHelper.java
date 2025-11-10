package com.bank.util;

import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

/**
 * Notification helper with template injection vulnerability
 */
@Component
public class NotificationHelper {

    /**
     * CRITICAL SINK: Template injection vulnerability
     * Equivalent to render_template_string in Python
     */
    public void sendTemplateEmail(String recipientEmail, String subject, 
                                 String customMessage, String fromName, 
                                 String toName, double amount) {
        
        // Build email template with user input
        String template = buildEmailTemplate(customMessage, fromName, toName, amount);
        
        // SINK: Renders template with user-controlled content
        String renderedEmail = renderTemplate(template);
        
        // Send email (mock)
        sendEmail(recipientEmail, subject, renderedEmail);
    }

    private String buildEmailTemplate(String customMessage, String fromName, 
                                      String toName, double amount) {
        // Vulnerable: User's customMessage is embedded directly in template
        StringBuilder template = new StringBuilder();
        template.append("<html><body>");
        template.append("<h1>Transfer Notification</h1>");
        template.append("<p>Transfer from ").append(fromName).append(" to ").append(toName).append("</p>");
        template.append("<p>Amount: $").append(amount).append("</p>");
        
        // CRITICAL: User input flows here without escaping
        template.append("<div class='custom-message'>").append(customMessage).append("</div>");
        
        template.append("</body></html>");
        
        return template.toString();
    }

    /**
     * SINK: Template rendering (equivalent to render_template_string)
     */
    private String renderTemplate(String template) {
        // This is where the template gets rendered
        // In real Thymeleaf, this would process expressions like ${...}, [[...]]
        // Making it vulnerable to template injection
        return template; // Simplified - real implementation would use TemplateEngine
    }

    private void sendEmail(String recipient, String subject, String body) {
        // Mock email sending
        System.out.println("Sending email to: " + recipient);
        System.out.println("Subject: " + subject);
        System.out.println("Body: " + body);
    }
}

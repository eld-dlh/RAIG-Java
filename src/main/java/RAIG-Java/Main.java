import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

import core.EthicsEngine;
import core.EthicsContext;
import core.EthicsResult;
import config.EthicsPolicy;
import governance.ApprovalWorkflow;
import governance.RoleManager;
import governance.Role;
import model.AIDecision;
import model.UserData;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Main - A lightweight HTTP server to showcase the RAIG framework
 * through a web interface.  Provides REST endpoints for framework demonstration
 * and serves static HTML/CSS/JS files for the frontend.
 */
public class Main {

    private static EthicsEngine engine;
    private static ApprovalWorkflow workflow;

    public static void main(String[] args) throws IOException {
        // Initialize RAIG framework
        EthicsPolicy policy = EthicsPolicy.defaultPolicy();
        RoleManager roleManager = new RoleManager();
        engine = new EthicsEngine();
        workflow = new ApprovalWorkflow();

        // Create HTTP server on port 8080
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        
        // API endpoints
        server.createContext("/api/pillars", new PillarsHandler());
        server.createContext("/api/evaluate", new EvaluateHandler());
        server.createContext("/api/scenarios", new ScenariosHandler());
        server.createContext("/", new StaticFileHandler());
        
        server.setExecutor(null);
        server.start();
        
        System.out.println("==============================================");
        System.out. println("RAIG Framework Web Interface Started");
        System.out.println("==============================================");
        System.out.println("Access the website at: http://localhost:8080");
        System.out.println("API endpoints available:");
        System.out.println("  - GET  /api/pillars    - List all framework pillars");
        System.out.println("  - POST /api/evaluate   - Evaluate an AI decision");
        System.out.println("  - GET  /api/scenarios  - Get demo scenarios");
        System.out. println("==============================================");
    }

    /**
     * Handler for /api/pillars - Returns information about the seven pillars
     */
    static class PillarsHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("GET".equals(exchange.getRequestMethod())) {
                String response = getPillarsJSON();
                sendResponse(exchange, 200, response);
            } else {
                sendResponse(exchange, 405, "{\"error\": \"Method not allowed\"}");
            }
        }

        private String getPillarsJSON() {
            return """
            {
              "pillars": [
                {
                  "id": 1,
                  "name": "Accountability",
                  "icon": "👤",
                  "description": "Ensures clear responsibility for AI decisions.  Every decision must have a responsible entity assigned.",
                  "checks": ["Responsible entity assignment", "Audit trail maintenance"],
                  "color": "#3B82F6"
                },
                {
                  "id":  2,
                  "name": "Fairness & Non-Discrimination",
                  "icon":  "⚖️",
                  "description": "Prevents biased outcomes across demographic groups. Monitors bias scores and ensures equitable treatment.",
                  "checks": ["Bias score analysis", "Demographic parity", "Equal opportunity"],
                  "color": "#10B981"
                },
                {
                  "id":  3,
                  "name": "Human Agency & Oversight",
                  "icon": "🤝",
                  "description": "Maintains human control over critical AI decisions. Ensures humans can override automated decisions.",
                  "checks": ["Human override availability", "Critical decision review"],
                  "color": "#8B5CF6"
                },
                {
                  "id": 4,
                  "name": "Privacy & Data Governance",
                  "icon": "🔒",
                  "description": "Protects user data and ensures consent. Validates data usage permissions and sensitive data handling.",
                  "checks": ["User consent verification", "Sensitive data protection"],
                  "color": "#F59E0B"
                },
                {
                  "id": 5,
                  "name": "Technical Robustness & Safety",
                  "icon": "🛡️",
                  "description": "Ensures AI systems are reliable and safe. Validates confidence levels and prediction quality.",
                  "checks": ["Confidence threshold validation", "Safety boundary checks"],
                  "color": "#EF4444"
                },
                {
                  "id": 6,
                  "name": "Transparency & Explainability",
                  "icon": "💡",
                  "description": "Makes AI decisions understandable. Requires explanations for all decisions affecting users.",
                  "checks":  ["Explanation availability", "Decision rationale clarity"],
                  "color": "#06B6D4"
                },
                {
                  "id": 7,
                  "name": "Societal & Environmental Well-Being",
                  "icon": "🌍",
                  "description": "Assesses broader social and environmental impact. Flags decisions with potential negative consequences.",
                  "checks": ["Social impact assessment", "Environmental impact evaluation"],
                  "color":  "#14B8A6"
                }
              ]
            }
            """;
        }
    }

    /**
     * Handler for /api/evaluate - Evaluates an AI decision through the framework
     */
    static class EvaluateHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("POST".equals(exchange.getRequestMethod())) {
                String requestBody = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                String response = evaluateDecision(requestBody);
                sendResponse(exchange, 200, response);
            } else {
                sendResponse(exchange, 405, "{\"error\": \"Method not allowed\"}");
            }
        }

        private String evaluateDecision(String jsonInput) {
            try {
                // Parse simple JSON manually (in production, use a JSON library)
                Map<String, String> data = parseSimpleJSON(jsonInput);
                
                AIDecision decision = new AIDecision(
                    data.getOrDefault("decisionLabel", "Unknown"),
                    Double.parseDouble(data.getOrDefault("confidence", "0.5"))
                );
                
                decision.setResponsibleEntity(data.get("responsibleEntity"));
                decision.setExplanation(data.get("explanation"));
                
                if (data.containsKey("biasScore")) {
                    decision.setBiasScore(Double. parseDouble(data.get("biasScore")));
                }
                
                if (data.containsKey("negativeSocialImpact")) {
                    decision.setNegativeSocialImpact(Boolean.parseBoolean(data.get("negativeSocialImpact")));
                }
                
                UserData userData = new UserData(
                    data.getOrDefault("userName", "User"),
                    data.getOrDefault("userEmail", "user@example.com"),
                    Boolean.parseBoolean(data.getOrDefault("hasSensitiveData", "false")),
                    Boolean.parseBoolean(data.getOrDefault("hasConsent", "true"))
                );
                
                EthicsContext context = new EthicsContext(decision, userData);
                EthicsResult result = engine.intercept(context);
                boolean approved = workflow.approve(result, Role.ETHICS_OFFICER);
                
                return formatEvaluationResult(result, approved, decision);
                
            } catch (Exception e) {
                return "{\"error\": \"" + e.getMessage() + "\"}";
            }
        }

        private String formatEvaluationResult(EthicsResult result, boolean approved, AIDecision decision) {
            StringBuilder json = new StringBuilder();
            json.append("{\n");
            json.append("  \"approved\": ").append(approved).append(",\n");
            json.append("  \"hasViolations\": ").append(result.hasViolations()).append(",\n");
            json.append("  \"violations\": [");
            
            List<String> violations = result.getViolations();
            for (int i = 0; i < violations.size(); i++) {
                json.append("\"").append(escapeJSON(violations.get(i))).append("\"");
                if (i < violations.size() - 1) json.append(", ");
            }
            
            json.append("],\n");
            json.append("  \"decision\": {\n");
            json.append("    \"label\": \"").append(escapeJSON(decision.getDecisionLabel())).append("\",\n");
            json.append("    \"confidence\": ").append(decision.getConfidence()).append(",\n");
            json.append("    \"biasScore\": ").append(decision.getBiasScore()).append(",\n");
            json.append("    \"explanation\": \"").append(escapeJSON(decision.getExplanation())).append("\",\n");
            json.append("    \"responsibleEntity\": \"").append(escapeJSON(decision.getResponsibleEntity())).append("\"\n");
            json.append("  }\n");
            json.append("}");
            
            return json.toString();
        }
    }

    /**
     * Handler for /api/scenarios - Returns pre-defined demo scenarios
     */
    static class ScenariosHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("GET".equals(exchange. getRequestMethod())) {
                String response = getScenariosJSON();
                sendResponse(exchange, 200, response);
            } else {
                sendResponse(exchange, 405, "{\"error\":\"Method not allowed\"}");
            }
        }

        private String getScenariosJSON() {
            return """
            {
              "scenarios":  [
                {
                  "id": 1,
                  "name": "Clean Decision",
                  "description": "A properly configured AI decision with all requirements met",
                  "data": {
                    "decisionLabel": "Loan Approved",
                    "confidence": "0.92",
                    "responsibleEntity": "CreditModel_v1",
                    "explanation":  "Applicant met all eligibility criteria",
                    "userName": "Alice",
                    "userEmail":  "alice@bank.com",
                    "hasConsent": "true",
                    "hasSensitiveData": "false"
                  },
                  "expectedResult": "APPROVED"
                },
                {
                  "id": 2,
                  "name": "Privacy Violation",
                  "description":  "Missing user consent triggers privacy governance checks",
                  "data": {
                    "decisionLabel":  "Loan Approved",
                    "confidence": "0.90",
                    "responsibleEntity": "CreditModel_v1",
                    "explanation": "Automated approval based on score",
                    "userName": "Bob",
                    "userEmail":  "bob@bank.com",
                    "hasConsent": "false",
                    "hasSensitiveData": "true"
                  },
                  "expectedResult": "BLOCKED"
                },
                {
                  "id": 3,
                  "name": "High Bias",
                  "description": "Elevated bias score indicates potential discrimination",
                  "data": {
                    "decisionLabel":  "Loan Rejected",
                    "confidence": "0.88",
                    "responsibleEntity": "CreditModel_v1",
                    "explanation": "Risk assessment below threshold",
                    "biasScore": "0.85",
                    "userName": "Carol",
                    "userEmail":  "carol@bank.com",
                    "hasConsent": "true",
                    "hasSensitiveData": "true"
                  },
                  "expectedResult": "BLOCKED"
                },
                {
                  "id": 4,
                  "name": "Missing Explanation",
                  "description": "Lack of transparency violates explainability requirements",
                  "data": {
                    "decisionLabel": "Loan Rejected",
                    "confidence": "0.86",
                    "responsibleEntity": "CreditModel_v1",
                    "userName": "Dave",
                    "userEmail":  "dave@bank.com",
                    "hasConsent": "true",
                    "hasSensitiveData": "false"
                  },
                  "expectedResult": "BLOCKED"
                }
              ]
            }
            """;
        }
    }

    /**
     * Handler for static files (HTML, CSS, JS)
     */
    static class StaticFileHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String path = exchange.getRequestURI().getPath();
            if (path.equals("/")) {
                path = "/index.html";
            }
            
            String content = getStaticContent(path);
            if (content != null) {
                String contentType = getContentType(path);
                exchange.getResponseHeaders().set("Content-Type", contentType);
                sendResponse(exchange, 200, content);
            } else {
                sendResponse(exchange, 404, "<h1>404 Not Found</h1>");
            }
        }

        private String getStaticContent(String path) {
            // Serve embedded HTML content
            if (path.equals("/index.html")) {
                return getIndexHTML();
            }
            return null;
        }

        private String getContentType(String path) {
            if (path.endsWith(".html")) return "text/html; charset=UTF-8";
            if (path.endsWith(".css")) return "text/css; charset=UTF-8";
            if (path.endsWith(".js")) return "application/javascript; charset=UTF-8";
            return "text/plain; charset=UTF-8";
        }

        private String getIndexHTML() {
            return """
            <!DOCTYPE html>
            <html lang="en">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>RAIG Framework - Responsible AI Governance</title>
                <style>
                    * {
                        margin: 0;
                        padding: 0;
                        box-sizing: border-box;
                    }

                    body {
                        font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
                        line-height: 1.8;
                        color: #e2e8f0;
                        background: #0f172a;
                        min-height: 100vh;
                    }

                    .container {
                        max-width: 1600px;
                        margin: 0 auto;
                        padding: 2rem;
                    }

                    header {
                        text-align: center;
                        background: linear-gradient(135deg, #1e293b 0%, #0f172a 100%);
                        margin: -2rem -2rem 2rem -2rem;
                        padding: 4rem 2rem;
                        border-bottom: 3px solid #3b82f6;
                        position: relative;
                        overflow: hidden;
                    }

                    header:before {
                        content: '';
                        position: absolute;
                        top: 0;
                        left: 0;
                        right: 0;
                        height: 4px;
                        background: linear-gradient(90deg, #3b82f6, #8b5cf6, #ec4899, #3b82f6);
                        background-size: 200% 100%;
                        animation: gradient 3s linear infinite;
                    }

                    @keyframes gradient {
                        0% { background-position: 0% 50%; }
                        100% { background-position: 200% 50%; }
                    }

                    header h1 {
                        font-size: 3.5rem;
                        font-weight: 900;
                        background: linear-gradient(135deg, #60a5fa, #a78bfa, #f472b6);
                        -webkit-background-clip: text;
                        -webkit-text-fill-color: transparent;
                        background-clip: text;
                        margin-bottom: 1rem;
                        letter-spacing: -2px;
                    }

                    header .subtitle {
                        font-size: 1.5rem;
                        color: #94a3b8;
                        font-weight: 400;
                        margin-bottom: 0.75rem;
                    }

                    header .tagline {
                        font-size: 1.1rem;
                        color: #64748b;
                        font-style: italic;
                    }

                    .card {
                        background: linear-gradient(135deg, #1e293b 0%, #0f172a 100%);
                        border: 1px solid #334155;
                        border-radius: 20px;
                        padding: 2.5rem;
                        margin-bottom: 2rem;
                        box-shadow: 0 10px 40px rgba(0,0,0,0.5);
                        transition: all 0.3s ease;
                    }

                    .card:hover {
                        transform: translateY(-4px);
                        box-shadow: 0 20px 60px rgba(59,130,246,0.3);
                        border-color: #3b82f6;
                    }

                    .card h2 {
                        font-size: 2rem;
                        margin-bottom: 1.5rem;
                        background: linear-gradient(135deg, #60a5fa, #a78bfa);
                        -webkit-background-clip: text;
                        -webkit-text-fill-color: transparent;
                        background-clip: text;
                        font-weight: 700;
                        border-bottom: 2px solid #334155;
                        padding-bottom: 0.75rem;
                    }

                    .card h3 {
                        font-size: 1.5rem;
                        margin: 1.5rem 0 1rem;
                        color: #cbd5e1;
                        font-weight: 600;
                    }

                    .card p {
                        margin-bottom: 1rem;
                        font-size: 1.05rem;
                        line-height: 1.8;
                        color: #94a3b8;
                    }

                    .highlight-box {
                        background: linear-gradient(135deg, #1e3a8a 0%, #3730a3 100%);
                        border-left: 4px solid #60a5fa;
                        padding: 1.5rem;
                        margin: 1.5rem 0;
                        border-radius: 12px;
                        box-shadow: 0 4px 12px rgba(59,130,246,0.2);
                    }

                    .highlight-box p {
                        color: #cbd5e1 !important;
                    }

                    .info-grid {
                        display: grid;
                        grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
                        gap: 1.5rem;
                        margin: 2rem 0;
                    }

                    .info-item {
                        background: #1e293b;
                        padding: 1.5rem;
                        border-radius: 12px;
                        border: 1px solid #334155;
                        transition: all 0.3s;
                    }

                    .info-item:hover {
                        border-color: #3b82f6;
                        background: #1e293b;
                        box-shadow: 0 8px 24px rgba(59,130,246,0.2);
                        transform: translateY(-2px);
                    }

                    .info-item h4 {
                        color: #60a5fa;
                        font-size: 1.1rem;
                        margin-bottom: 0.75rem;
                        font-weight: 700;
                    }

                    .info-item ul {
                        list-style: none;
                        font-size: 0.95rem;
                        color: #94a3b8;
                    }

                    .info-item li {
                        padding: 0.4rem 0;
                        padding-left: 1.5rem;
                        position: relative;
                    }

                    .info-item li:before {
                        content: "▸";
                        position: absolute;
                        left: 0;
                        color: #60a5fa;
                        font-weight: bold;
                    }

                    .pillars-grid {
                        display: grid;
                        grid-template-columns: repeat(auto-fit, minmax(340px, 1fr));
                        gap: 2rem;
                        margin-top: 2rem;
                    }

                    .pillar {
                        background: linear-gradient(135deg, #1e293b 0%, #0f172a 100%);
                        border-radius: 16px;
                        padding: 2rem;
                        box-shadow: 0 8px 24px rgba(0,0,0,0.4);
                        transition: all 0.3s ease;
                        border: 2px solid;
                        position: relative;
                        overflow: hidden;
                    }

                    .pillar:before {
                        content: '';
                        position: absolute;
                        top: -50%;
                        right: -50%;
                        width: 200px;
                        height: 200px;
                        background: radial-gradient(circle, rgba(59,130,246,0.1) 0%, transparent 70%);
                        border-radius: 50%;
                    }

                    .pillar:hover {
                        transform: translateY(-8px) scale(1.02);
                        box-shadow: 0 16px 40px rgba(0,0,0,0.6);
                    }

                    .pillar-icon {
                        font-size: 3.5rem;
                        margin-bottom: 1rem;
                        display: block;
                        filter: drop-shadow(0 2px 4px rgba(0,0,0,0.3));
                    }

                    .pillar h3 {
                        font-size: 1.4rem;
                        margin-bottom: 0.75rem;
                        font-weight: 700;
                        color: #f1f5f9;
                    }

                    .pillar p {
                        font-size: 1rem;
                        color: #94a3b8;
                        margin-bottom: 1rem;
                        line-height: 1.7;
                    }

                    .pillar-checks {
                        list-style: none;
                        font-size: 0.92rem;
                        color: #cbd5e1;
                    }

                    .pillar-checks li {
                        padding: 0.5rem 0;
                    }

                    .pillar-checks li:before {
                        content: "✓ ";
                        color: #10b981;
                        font-weight: bold;
                        margin-right: 0.5rem;
                        font-size: 1.1rem;
                    }

                    .tabs {
                        display: flex;
                        gap: 0.5rem;
                        margin-bottom: 2rem;
                        border-bottom: 2px solid #334155;
                        flex-wrap: wrap;
                    }

                    .tab {
                        padding: 1rem 2rem;
                        background: transparent;
                        border: none;
                        font-size: 1.05rem;
                        cursor: pointer;
                        color: #64748b;
                        border-bottom: 3px solid transparent;
                        margin-bottom: -2px;
                        transition: all 0.3s;
                        font-weight: 500;
                    }

                    .tab:hover {
                        color: #60a5fa;
                        background: rgba(59,130,246,0.1);
                    }

                    .tab.active {
                        color: #60a5fa;
                        border-bottom-color: #3b82f6;
                        font-weight: 700;
                        background: rgba(59,130,246,0.05);
                    }

                    .tab-content {
                        display: none;
                    }

                    .tab-content.active {
                        display: block;
                    }

                    .scenario-grid {
                        display: grid;
                        grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
                        gap: 1.5rem;
                        margin-top: 1.5rem;
                    }

                    .scenario-card {
                        background: linear-gradient(135deg, #1e293b 0%, #0f172a 100%);
                        border-radius: 12px;
                        padding: 2rem;
                        cursor: pointer;
                        transition: all 0.3s ease;
                        border: 2px solid #334155;
                        position: relative;
                        overflow: hidden;
                    }

                    .scenario-card:before {
                        content: '';
                        position: absolute;
                        top: 0;
                        left: 0;
                        width: 5px;
                        height: 100%;
                        background: linear-gradient(180deg, #3b82f6, #8b5cf6);
                        transform: scaleY(0);
                        transition: transform 0.3s ease;
                    }

                    .scenario-card:hover:before {
                        transform: scaleY(1);
                    }

                    .scenario-card:hover {
                        border-color: #3b82f6;
                        background: linear-gradient(135deg, #1e3a5f 0%, #1e293b 100%);
                        transform: translateX(8px);
                        box-shadow: 0 8px 24px rgba(59,130,246,0.3);
                    }

                    .scenario-card h4 {
                        font-size: 1.3rem;
                        margin-bottom: 0.75rem;
                        color: #f1f5f9;
                        font-weight: 700;
                    }

                    .scenario-card p {
                        font-size: 1rem;
                        color: #94a3b8;
                        line-height: 1.6;
                    }

                    .scenario-badge {
                        display: inline-block;
                        padding: 0.5rem 1.2rem;
                        border-radius: 20px;
                        font-size: 0.9rem;
                        font-weight: 700;
                        margin-top: 1rem;
                    }

                    .scenario-badge.approved {
                        background: linear-gradient(135deg, #10b981, #059669);
                        color: #fff;
                        box-shadow: 0 4px 12px rgba(16,185,129,0.3);
                    }

                    .scenario-badge.blocked {
                        background: linear-gradient(135deg, #ef4444, #dc2626);
                        color: #fff;
                        box-shadow: 0 4px 12px rgba(239,68,68,0.3);
                    }

                    .form-group {
                        margin-bottom: 1.5rem;
                    }

                    .form-group label {
                        display: block;
                        margin-bottom: 0.65rem;
                        font-weight: 600;
                        color: #cbd5e1;
                        font-size: 0.95rem;
                    }

                    .form-group input, .form-group select {
                        width: 100%;
                        padding: 1rem;
                        background: #1e293b;
                        border: 2px solid #334155;
                        border-radius: 8px;
                        font-size: 1rem;
                        transition: all 0.3s;
                        font-family: inherit;
                        color: #e2e8f0;
                    }

                    .form-group input:focus, .form-group select:focus {
                        outline: none;
                        border-color: #3b82f6;
                        box-shadow: 0 0 0 3px rgba(59,130,246,0.2);
                        background: #0f172a;
                    }

                    .form-row {
                        display: grid;
                        grid-template-columns: 1fr 1fr;
                        gap: 1rem;
                    }

                    .btn {
                        padding: 1.1rem 2.5rem;
                        background: linear-gradient(135deg, #3b82f6 0%, #8b5cf6 100%);
                        color: white;
                        border: none;
                        border-radius: 10px;
                        font-size: 1.1rem;
                        font-weight: 700;
                        cursor: pointer;
                        transition: all 0.3s;
                        box-shadow: 0 6px 20px rgba(59,130,246,0.4);
                        text-transform: uppercase;
                        letter-spacing: 0.5px;
                    }

                    .btn:hover {
                        transform: translateY(-3px);
                        box-shadow: 0 10px 30px rgba(59,130,246,0.5);
                        background: linear-gradient(135deg, #2563eb 0%, #7c3aed 100%);
                    }

                    .btn:active {
                        transform: translateY(0);
                    }

                    .btn:disabled {
                        background: #475569;
                        cursor: not-allowed;
                        box-shadow: none;
                    }

                    .result {
                        margin-top: 2.5rem;
                        padding: 2.5rem;
                        border-radius: 16px;
                        display: none;
                        animation: slideIn 0.5s ease-out;
                        border: 2px solid;
                    }

                    @keyframes slideIn {
                        from { opacity: 0; transform: translateY(30px); }
                        to { opacity: 1; transform: translateY(0); }
                    }

                    .result.approved {
                        background: linear-gradient(135deg, #064e3b 0%, #065f46 100%);
                        border-color: #10b981;
                        display: block;
                        box-shadow: 0 8px 32px rgba(16,185,129,0.3);
                    }

                    .result.blocked {
                        background: linear-gradient(135deg, #7f1d1d 0%, #991b1b 100%);
                        border-color: #ef4444;
                        display: block;
                        box-shadow: 0 8px 32px rgba(239,68,68,0.3);
                    }

                    .result h3 {
                        margin-bottom: 1.5rem;
                        font-size: 2rem;
                        color: #fff;
                    }

                    .result-details {
                        background: rgba(0,0,0,0.3);
                        padding: 1.5rem;
                        border-radius: 10px;
                        margin: 1rem 0;
                        border: 1px solid rgba(255,255,255,0.1);
                    }

                    .result-details p {
                        margin: 0.65rem 0;
                        font-size: 1.05rem;
                        color: #e2e8f0;
                    }

                    .violations {
                        list-style: none;
                        margin-top: 1rem;
                    }

                    .violations li {
                        padding: 1rem 1.25rem;
                        background: rgba(0,0,0,0.2);
                        margin-bottom: 0.75rem;
                        border-radius: 8px;
                        border-left: 4px solid #ef4444;
                        font-weight: 600;
                        color: #fca5a5;
                    }

                    .loading {
                        text-align: center;
                        padding: 3rem;
                        color: #94a3b8;
                        font-size: 1.2rem;
                    }

                    footer {
                        text-align: center;
                        background: linear-gradient(135deg, #1e293b 0%, #0f172a 100%);
                        padding: 3rem 2rem;
                        margin: 4rem -2rem -2rem -2rem;
                        border-top: 3px solid #3b82f6;
                        color: #94a3b8;
                    }

                    footer p {
                        margin: 0.5rem 0;
                    }

                    footer a {
                        color: #60a5fa;
                        text-decoration: none;
                        transition: color 0.3s;
                    }

                    footer a:hover {
                        color: #93c5fd;
                        text-decoration: underline;
                    }

                    @media (max-width: 768px) {
                        header h1 {
                            font-size: 2.25rem;
                        }

                        header .subtitle {
                            font-size: 1.1rem;
                        }

                        .form-row {
                            grid-template-columns: 1fr;
                        }

                        .pillars-grid {
                            grid-template-columns: 1fr;
                        }

                        .container {
                            padding: 1rem;
                        }

                        .card {
                            padding: 1.5rem;
                        }
                    }
                </style>
            </head>
            <body>
                <div class="container">
                    <header>
                        <h1>🛡️ RAIG Framework</h1>
                        <p class="subtitle">Responsible AI Governance</p>
                        <p class="tagline">Digital Guardrails for Ethical AI Systems</p>
                    </header>

                    <div class="card">
                        <h2>📋 About the Framework</h2>
                        <p>
                            <strong>RAIG-Java</strong> is a modular, enterprise-grade Java implementation of the Responsible AI Governance 
                            framework proposed by <em>Papagiannidis et al. (2025)</em>. This system operationalizes AI ethics 
                            principles into a comprehensive "Digital Guardrail" system designed for the entire AI lifecycle—from 
                            development to deployment.
                        </p>
                        <div class="highlight-box">
                            <p style="margin: 0;">
                                <strong>🎯 Mission:</strong> Move beyond abstract ethical theory by implementing decision-level governance 
                                that can be applied to any AI decision before deployment, ensuring accountability, fairness, and transparency.
                            </p>
                        </div>
                        
                        <h3>🏗️ Architecture Overview</h3>
                        <div class="info-grid">
                            <div class="info-item">
                                <h4>Core Objects</h4>
                                <ul>
                                    <li><strong>AIDecision</strong> - Model decision representation</li>
                                    <li><strong>UserData</strong> - Affected user information</li>
                                    <li><strong>EthicsContext</strong> - Combined evaluation context</li>
                                    <li><strong>EthicsResult</strong> - Violation tracking & governance</li>
                                </ul>
                            </div>
                            <div class="info-item">
                                <h4>Ethics Engine</h4>
                                <ul>
                                    <li>Runs context through all 7 pillars</li>
                                    <li>Modular check() methods per pillar</li>
                                    <li>Returns comprehensive EthicsResult</li>
                                    <li>Pluggable architecture for extensibility</li>
                                </ul>
                            </div>
                            <div class="info-item">
                                <h4>Governance Layer</h4>
                                <ul>
                                    <li><strong>EthicsPolicy</strong> - Central configuration</li>
                                    <li><strong>Role Management</strong> - ETHICS_OFFICER, AI_AUDITOR</li>
                                    <li><strong>ApprovalWorkflow</strong> - Deployment gating</li>
                                    <li><strong>FeedbackService</strong> - Stakeholder input</li>
                                </ul>
                            </div>
                        </div>

                        <h3>🔌 Integration Capabilities</h3>
                        <p>
                            The framework includes optional integration hooks for industry-standard tools including 
                            <strong>Apache Spark</strong> for bias analysis, <strong>TrustyAI</strong> for explainability, 
                            and <strong>DL4J</strong> for model wrapping. These integrations enable seamless incorporation 
                            into existing ML pipelines.
                        </p>
                    </div>

                    <div class="card">
                        <h2>🏛️ The Seven Pillars of Responsible AI</h2>
                        <p>
                            Each pillar represents a critical dimension of ethical AI, implemented as a pluggable Java module 
                            with specific validation checks. Together, they form a comprehensive governance framework.
                        </p>
                        <div id="pillars-container" class="pillars-grid">
                            <div class="loading">Loading pillars...</div>
                        </div>
                    </div>

                    <div class="card">
                        <h2>🧪 Interactive Demo</h2>
                        <p>
                            Explore how the RAIG framework evaluates AI decisions through pre-configured scenarios or 
                            create your own custom evaluation. Each scenario demonstrates different ethical considerations 
                            and governance outcomes.
                        </p>
                        
                        <div class="tabs">
                            <button class="tab active" onclick="switchTab('scenarios')">📚 Demo Scenarios</button>
                            <button class="tab" onclick="switchTab('custom')">⚙️ Custom Evaluation</button>
                        </div>

                        <div id="scenarios-tab" class="tab-content active">
                            <div class="highlight-box">
                                <p style="margin: 0;">
                                    <strong>💡 How it works:</strong> Each scenario builds an EthicsContext from AIDecision and UserData, 
                                    passes it through the EthicsEngine, and sends the resulting EthicsResult to the ApprovalWorkflow 
                                    for final determination.
                                </p>
                            </div>
                            <div id="scenarios-container" class="scenario-grid">
                                <div class="loading">Loading scenarios...</div>
                            </div>
                        </div>

                        <div id="custom-tab" class="tab-content">
                            <p style="margin-bottom: 1.5rem;">
                                Create a custom AI decision evaluation by filling out the form below. The framework will 
                                evaluate your decision against all seven pillars and provide detailed feedback.
                            </p>
                            <form id="evaluation-form">
                                <div class="form-row">
                                    <div class="form-group">
                                        <label>Decision Label</label>
                                        <input type="text" name="decisionLabel" placeholder="e.g., Loan Approved" required>
                                    </div>
                                    <div class="form-group">
                                        <label>Confidence (0-1)</label>
                                        <input type="number" name="confidence" step="0.01" min="0" max="1" value="0.85" required>
                                    </div>
                                </div>

                                <div class="form-row">
                                    <div class="form-group">
                                        <label>Responsible Entity</label>
                                        <input type="text" name="responsibleEntity" placeholder="e.g., CreditModel_v1">
                                    </div>
                                    <div class="form-group">
                                        <label>Bias Score (0-1, optional)</label>
                                        <input type="number" name="biasScore" step="0.01" min="0" max="1">
                                    </div>
                                </div>

                                <div class="form-group">
                                    <label>Explanation</label>
                                    <input type="text" name="explanation" placeholder="Human-readable explanation of the decision">
                                </div>

                                <div class="form-row">
                                    <div class="form-group">
                                        <label>User Name</label>
                                        <input type="text" name="userName" placeholder="e.g., John Doe" value="User" required>
                                    </div>
                                    <div class="form-group">
                                        <label>User Email</label>
                                        <input type="email" name="userEmail" placeholder="user@example.com" value="user@example.com" required>
                                    </div>
                                </div>

                                <div class="form-row">
                                    <div class="form-group">
                                        <label>Has User Consent</label>
                                        <select name="hasConsent" required>
                                            <option value="true">Yes</option>
                                            <option value="false">No</option>
                                        </select>
                                    </div>
                                    <div class="form-group">
                                        <label>Contains Sensitive Data</label>
                                        <select name="hasSensitiveData" required>
                                            <option value="false">No</option>
                                            <option value="true">Yes</option>
                                        </select>
                                    </div>
                                </div>

                                <button type="submit" class="btn">Evaluate Decision</button>
                            </form>
                        </div>

                        <div id="result-container"></div>
                    </div>

                    <footer>
                        <p><strong>RAIG-Java Framework</strong> &copy; 2025</p>
                        <p>Based on <em>Papagiannidis et al. (2025)</em> | Modular Ethics • Decision-Level Guardrails • Enterprise-Ready</p>
                        <p style="margin-top: 1rem; font-size: 0.95rem;">🔗 GitHub: <a href="https://github.com/eld-dlh/RAIG-Java">eld-dlh/RAIG-Java</a></p>
                    </footer>
                </div>

                <script>
                    // Load pillars on page load
                    async function loadPillars() {
                        try {
                            const response = await fetch('/api/pillars');
                            const data = await response.json();
                            
                            const container = document.getElementById('pillars-container');
                            container.innerHTML = '';
                            
                            data. pillars.forEach(pillar => {
                                const pillarEl = document.createElement('div');
                                pillarEl.className = 'pillar';
                                pillarEl.style.borderLeftColor = pillar.color;
                                
                                pillarEl.innerHTML = `
                                    <div class="pillar-icon">${pillar.icon}</div>
                                    <h3>${pillar.name}</h3>
                                    <p>${pillar.description}</p>
                                    <ul class="pillar-checks">
                                        ${pillar.checks.map(check => `<li>${check}</li>`).join('')}
                                    </ul>
                                `;
                                
                                container.appendChild(pillarEl);
                            });
                        } catch (error) {
                            console.error('Error loading pillars:', error);
                        }
                    }

                    // Load scenarios on page load
                    async function loadScenarios() {
                        try {
                            const response = await fetch('/api/scenarios');
                            const data = await response.json();
                            
                            const container = document.getElementById('scenarios-container');
                            container.innerHTML = '';
                            
                            data.scenarios.forEach(scenario => {
                                const scenarioEl = document.createElement('div');
                                scenarioEl.className = 'scenario-card';
                                scenarioEl.onclick = () => runScenario(scenario.data);
                                
                                const badgeClass = scenario.expectedResult === 'APPROVED' ? 'approved' : 'blocked';
                                
                                scenarioEl.innerHTML = `
                                    <h4>${scenario.name}</h4>
                                    <p>${scenario.description}</p>
                                    <span class="scenario-badge ${badgeClass}">
                                        ${scenario.expectedResult === 'APPROVED' ? '✓' : '✗'} ${scenario.expectedResult}
                                    </span>
                                `;
                                
                                container.appendChild(scenarioEl);
                            });
                        } catch (error) {
                            console.error('Error loading scenarios:', error);
                        }
                    }

                    // Switch between tabs
                    function switchTab(tabName) {
                        document.querySelectorAll('.tab').forEach(tab => tab.classList. remove('active'));
                        document.querySelectorAll('.tab-content').forEach(content => content. classList.remove('active'));
                        
                        event.target.classList.add('active');
                        document.getElementById(tabName + '-tab').classList.add('active');
                    }

                    // Run a scenario
                    async function runScenario(data) {
                        await evaluateDecision(data);
                    }

                    // Handle form submission
                    document.getElementById('evaluation-form').addEventListener('submit', async (e) => {
                        e.preventDefault();
                        
                        const formData = new FormData(e.target);
                        const data = {};
                        formData.forEach((value, key) => data[key] = value);
                        
                        await evaluateDecision(data);
                    });

                    // Evaluate decision via API
                    async function evaluateDecision(data) {
                        const resultContainer = document.getElementById('result-container');
                        resultContainer. innerHTML = '<div class="loading">Evaluating decision...</div>';
                        
                        try {
                            const response = await fetch('/api/evaluate', {
                                method: 'POST',
                                headers: { 'Content-Type': 'application/json' },
                                body: JSON.stringify(data)
                            });
                            
                            const result = await response. json();
                            displayResult(result);
                        } catch (error) {
                            resultContainer.innerHTML = `<div class="result blocked">
                                <h3>❌ Error</h3>
                                <p>${error.message}</p>
                            </div>`;
                        }
                    }

                    // Display evaluation result
                    function displayResult(result) {
                        const resultContainer = document.getElementById('result-container');
                        const resultClass = result.approved ? 'approved' : 'blocked';
                        const icon = result.approved ? '✅' : '❌';
                        const status = result.approved ? 'APPROVED' : 'BLOCKED';
                        
                        let html = `
                            <div class="result ${resultClass}">
                                <h3>${icon} Decision ${status}</h3>
                                <div class="result-details">
                                    <p><strong>📊 Decision:</strong> ${result.decision.label}</p>
                                    <p><strong>🎯 Confidence:</strong> ${(result.decision.confidence * 100).toFixed(1)}%</p>
                                    <p><strong>👤 Responsible Entity:</strong> ${result.decision.responsibleEntity || 'Not specified'}</p>
                                    <p><strong>⚖️ Bias Score:</strong> ${result.decision.biasScore.toFixed(3)}</p>
                                    <p><strong>💬 Explanation:</strong> ${result.decision.explanation || 'Not provided'}</p>
                                </div>
                        `;
                        
                        if (result.violations.length > 0) {
                            html += `
                                <h4 style="margin-top: 1.5rem; color: #991b1b; font-size: 1.25rem;">
                                    ⚠️ Violations Detected (${result.violations.length})
                                </h4>
                                <ul class="violations">
                                    ${result.violations.map(v => `<li>🚫 ${v}</li>`).join('')}
                                </ul>
                            `;
                        } else {
                            html += `
                                <div style="margin-top: 1.5rem; padding: 1rem; background: rgba(16,185,129,0.1); 
                                     border-radius: 8px; border-left: 3px solid #10b981;">
                                    <p style="margin: 0; font-weight: 700; color: #065f46; font-size: 1.1rem;">
                                        ✓ All Ethical Requirements Met
                                    </p>
                                    <p style="margin: 0.5rem 0 0 0; color: #047857;">
                                        This decision passed all seven pillar checks and is approved for deployment.
                                    </p>
                                </div>
                            `;
                        }
                        
                        html += '</div>';
                        resultContainer.innerHTML = html;
                        
                        // Scroll to result
                        resultContainer.scrollIntoView({ behavior: 'smooth', block: 'nearest' });
                    }

                    // Initialize page
                    loadPillars();
                    loadScenarios();
                </script>
            </body>
            </html>
            """;
        }
    }

    // Utility methods
    private static void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        byte[] bytes = response.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(statusCode, bytes.length);
        OutputStream os = exchange.getResponseBody();
        os.write(bytes);
        os.close();
    }

    private static Map<String, String> parseSimpleJSON(String json) {
        Map<String, String> map = new HashMap<>();
        json = json.trim().replaceAll("[{}]", "");
        String[] pairs = json.split(",");
        
        for (String pair : pairs) {
            String[] keyValue = pair.split(":", 2);
            if (keyValue.length == 2) {
                String key = keyValue[0].trim().replaceAll("\"", "");
                String value = keyValue[1].trim().replaceAll("\"", "");
                map.put(key, value);
            }
        }
        
        return map;
    }

    private static String escapeJSON(String str) {
        if (str == null) return "";
        return str.replace("\\", "\\\\")
                  .replace("\"", "\\\"")
                  .replace("\n", "\\n")
                  .replace("\r", "\\r");
    }
}
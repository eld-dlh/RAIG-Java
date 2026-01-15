package integration;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

/**
 * Integration tests for the HTTP REST API endpoints
 * 
 * NOTE: These tests require the Main server to be running on port 8080
 * To run these tests:
 * 1. Start the server: java -cp target/classes Main
 * 2. Run tests: mvn test -Dtest=HttpEndpointIntegrationTest
 * 
 * Tests are disabled by default (@Disabled) to avoid CI/CD failures.
 * Remove @Disabled annotation when server is running to execute tests.
 */
@Disabled("Requires running HTTP server - start Main.java before running these tests")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class HttpEndpointIntegrationTest {

    private static HttpClient client;
    private static final String BASE_URL = "http://localhost:8080";
    private static final int TIMEOUT_SECONDS = 5;
    
    @BeforeAll
    static void setup() {
        client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(TIMEOUT_SECONDS))
                .build();
    }

    @Test
    @Order(1)
    @DisplayName("GET / - Homepage should return 200 with HTML content")
    void testHomepageEndpoint() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/"))
                .GET()
                .build();
        
        HttpResponse<String> response = client.send(request, 
                HttpResponse.BodyHandlers.ofString());
        
        assertEquals(200, response.statusCode(), "Homepage should return 200 OK");
        assertTrue(response.body().contains("<!DOCTYPE html>"), "Response should be HTML");
        assertTrue(response.body().contains("RAIG Framework"), "Should contain RAIG title");
        assertTrue(response.headers().firstValue("content-type").orElse("")
                .contains("text/html"), "Content-Type should be text/html");
    }

    @Test
    @Order(2)
    @DisplayName("GET /api/pillars - Should return JSON array of 7 pillars")
    void testPillarsEndpoint() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/api/pillars"))
                .GET()
                .build();
        
        HttpResponse<String> response = client.send(request, 
                HttpResponse.BodyHandlers.ofString());
        
        assertEquals(200, response.statusCode(), "Pillars endpoint should return 200");
        assertTrue(response.headers().firstValue("content-type").orElse("")
                .contains("application/json"), "Should return JSON");
        
        String body = response.body();
        assertTrue(body.contains("\"pillars\""), "Response should contain pillars array");
        assertTrue(body.contains("Accountability"), "Should include Accountability pillar");
        assertTrue(body.contains("Fairness"), "Should include Fairness pillar");
        assertTrue(body.contains("Privacy"), "Should include Privacy pillar");
        assertTrue(body.contains("Transparency"), "Should include Transparency pillar");
        assertTrue(body.contains("Robustness"), "Should include Robustness pillar");
        assertTrue(body.contains("Human Agency"), "Should include Human Oversight pillar");
        assertTrue(body.contains("Well-Being"), "Should include Well-being pillar");
        
        // Count pillar objects (should be 7)
        int pillarCount = countOccurrences(body, "\"id\":");
        assertEquals(7, pillarCount, "Should return exactly 7 pillars");
    }

    @Test
    @Order(3)
    @DisplayName("GET /api/scenarios - Should return demo scenarios")
    void testScenariosEndpoint() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/api/scenarios"))
                .GET()
                .build();
        
        HttpResponse<String> response = client.send(request, 
                HttpResponse.BodyHandlers.ofString());
        
        assertEquals(200, response.statusCode(), "Scenarios endpoint should return 200");
        assertTrue(response.headers().firstValue("content-type").orElse("")
                .contains("application/json"), "Should return JSON");
        
        String body = response.body();
        assertTrue(body.contains("\"scenarios\""), "Response should contain scenarios array");
        assertTrue(body.contains("Clean Decision"), "Should include clean decision scenario");
        assertTrue(body.contains("APPROVE"), "Should show APPROVE outcome");
        assertTrue(body.contains("BLOCK"), "Should show BLOCK outcome");
        assertTrue(body.contains("ESCALATE"), "Should show ESCALATE outcome");
    }

    @Test
    @Order(4)
    @DisplayName("POST /api/evaluate - Should evaluate a clean decision")
    void testEvaluateEndpointCleanDecision() throws Exception {
        String requestBody = """
        {
          "decisionLabel": "Loan Approved",
          "confidence": 0.92,
          "responsibleEntity": "CreditModel_v1",
          "explanation": "Applicant approved based on credit score of 720, stable employment history of 5 years, income-to-debt ratio of 0.3, and clean payment history.",
          "biasScore": 0.15,
          "userName": "Alice",
          "userEmail": "alice@bank.com",
          "sensitive": false,
          "consentGiven": true
        }
        """;
        
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/api/evaluate"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();
        
        HttpResponse<String> response = client.send(request, 
                HttpResponse.BodyHandlers.ofString());
        
        assertEquals(200, response.statusCode(), "Evaluate should return 200");
        
        String body = response.body();
        assertTrue(body.contains("\"finalDecision\""), "Response should include finalDecision");
        assertTrue(body.contains("APPROVE"), "Clean decision should be APPROVED");
        assertTrue(body.contains("\"violations\":"), "Response should include violations array");
    }

    @Test
    @Order(5)
    @DisplayName("POST /api/evaluate - Should block high bias decision")
    void testEvaluateEndpointHighBias() throws Exception {
        String requestBody = """
        {
          "decisionLabel": "Loan Rejected",
          "confidence": 0.88,
          "responsibleEntity": "CreditModel_v1",
          "explanation": "Rejected due to risk assessment score below threshold. Credit history shows inconsistent income patterns.",
          "biasScore": 0.85,
          "userName": "Bob",
          "userEmail": "bob@bank.com",
          "sensitive": false,
          "consentGiven": true
        }
        """;
        
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/api/evaluate"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();
        
        HttpResponse<String> response = client.send(request, 
                HttpResponse.BodyHandlers.ofString());
        
        assertEquals(200, response.statusCode(), "Evaluate should return 200");
        
        String body = response.body();
        assertTrue(body.contains("BLOCK"), "High bias should be BLOCKED");
        assertTrue(body.contains("FAIRNESS"), "Should include FAIRNESS violation");
    }

    @Test
    @Order(6)
    @DisplayName("POST /api/evaluate - Should block missing consent")
    void testEvaluateEndpointPrivacyViolation() throws Exception {
        String requestBody = """
        {
          "decisionLabel": "Insurance Quote",
          "confidence": 0.90,
          "responsibleEntity": "InsuranceModel_v2",
          "explanation": "Quote calculated based on risk factors and health data.",
          "biasScore": 0.20,
          "userName": "Carol",
          "userEmail": "carol@insurance.com",
          "sensitive": true,
          "consentGiven": false
        }
        """;
        
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/api/evaluate"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();
        
        HttpResponse<String> response = client.send(request, 
                HttpResponse.BodyHandlers.ofString());
        
        assertEquals(200, response.statusCode(), "Evaluate should return 200");
        
        String body = response.body();
        assertTrue(body.contains("BLOCK"), "Missing consent should BLOCK");
        assertTrue(body.contains("PRIVACY"), "Should include PRIVACY violation");
        assertTrue(body.contains("consent"), "Should mention consent in violation");
    }

    @Test
    @Order(7)
    @DisplayName("POST /api/evaluate - Invalid method should return 405")
    void testInvalidMethod() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/api/pillars"))
                .POST(HttpRequest.BodyPublishers.ofString("{}"))
                .build();
        
        HttpResponse<String> response = client.send(request, 
                HttpResponse.BodyHandlers.ofString());
        
        assertEquals(405, response.statusCode(), "POST to GET-only endpoint should return 405");
    }

    @Test
    @Order(8)
    @DisplayName("GET /invalid - Non-existent endpoint should return 404")
    void testNonExistentEndpoint() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/api/nonexistent"))
                .GET()
                .build();
        
        HttpResponse<String> response = client.send(request, 
                HttpResponse.BodyHandlers.ofString());
        
        assertEquals(404, response.statusCode(), "Non-existent endpoint should return 404");
    }

    // Helper method to count occurrences of a substring
    private int countOccurrences(String text, String pattern) {
        int count = 0;
        int index = 0;
        while ((index = text.indexOf(pattern, index)) != -1) {
            count++;
            index += pattern.length();
        }
        return count;
    }
}

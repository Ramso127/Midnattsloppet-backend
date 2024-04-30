package com.pvt.groupOne.Service;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pvt.groupOne.model.StravaUser;

public class StravaTokenService {

    public StravaUser exchangeToken(String authCode) {

        final String URL = "https://www.strava.com/oauth/token";
        final int clientID = 125803;
        final String clientSecret = "3e9f7fcd913ece59cb5bccd8a89444ab9f452ec5";
        final String grantType = "authorization_code";

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            // Create HttpPost request with URL
            HttpPost httpPost = new HttpPost(URL);

            // Create request body
            String requestBody = String.format(
                    "{\"code\": %s, \"client_id\": %d, \"client_secret\": \"%s\", \"grant_type\": \"%s\"}",
                    authCode, clientID, clientSecret, grantType);

            // Set request body
            StringEntity stringEntity = new StringEntity(requestBody);
            httpPost.setEntity(stringEntity);

            // Execute the request
            try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
                // Get response entity
                HttpEntity entity = response.getEntity();

                // Print response status
                System.out.println("Response Status: " + response.getStatusLine());

                // Print response content
                if (entity != null) {
                    String responseContent = EntityUtils.toString(entity);
                    System.out.println("Response Content: " + responseContent);

                    // Parse JSON response
                    ObjectMapper objectMapper = new ObjectMapper();
                    JsonNode jsonNode = objectMapper.readTree(responseContent);

                    // Extract required fields
                    long expiresAt = jsonNode.get("expires_at").asLong();
                    String refreshToken = jsonNode.get("refresh_token").asText();
                    String accessToken = jsonNode.get("access_token").asText();

                    // Print the extracted fields
                    System.out.println("Expires At: " + expiresAt);
                    System.out.println("Refresh Token: " + refreshToken);
                    System.out.println("Access Token: " + accessToken);

                    JsonNode athleteNode = jsonNode.get("athlete");
                    int id = athleteNode.has("id") ? athleteNode.get("id").asInt() : null;
                    String firstName = athleteNode.has("firstname") ? athleteNode.get("firstname").asText() : null;

                    return new StravaUser(id, firstName, Integer.parseInt(accessToken), Integer.parseInt(refreshToken), expiresAt);

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;

    }
}

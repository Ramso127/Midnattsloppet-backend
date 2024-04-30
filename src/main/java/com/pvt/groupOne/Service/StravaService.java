package com.pvt.groupOne.Service;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pvt.groupOne.model.StravaUser;

public class StravaService {

    public StravaUser exchangeToken(String authCode) {

        final String URL = "https://www.strava.com/oauth/token";
        final String clientID = "125803";
        final String clientSecret = "3e9f7fcd913ece59cb5bccd8a89444ab9f452ec5";
        final String grantType = "authorization_code";

        HttpClient httpClient = HttpClientBuilder.create().build();

        try {
            // Build the URI with parameters
            URI uri = new URIBuilder(URL)
                    .setParameter("client_id", clientID)
                    .setParameter("client_secret", clientSecret)
                    .setParameter("code", authCode)
                    .setParameter("grant_type", grantType)
                    .build();

            HttpPost httpPost = new HttpPost(uri);

            // Execute the request
            HttpResponse response = httpClient.execute(httpPost);

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

                return new StravaUser(id, firstName, accessToken, refreshToken,
                        expiresAt);

            }

            // Print response status code
            System.out.println("Response Status Code: " + response.getStatusLine().getStatusCode());
            return null;

            // You can handle the response body here if needed
            // For example, to print the response body:
            // System.out.println(EntityUtils.toString(response.getEntity()));
        } catch (URISyntaxException | IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void refreshToken(){
        // TODO make refreshToken method
    }

    public void getRunsAfter(int unixTimeStamp){
        // TODO Make getRunsAfter
    }

}

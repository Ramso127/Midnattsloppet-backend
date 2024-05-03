package com.pvt.groupOne.Service;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pvt.groupOne.model.StravaUser;
import com.pvt.groupOne.repository.StravaUserRepository;

public class StravaService {

    private final StravaUserRepository stravaUserRepository;

    public StravaService(StravaUserRepository stravaUserRepository) {
        this.stravaUserRepository = stravaUserRepository;
    }

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

        } catch (URISyntaxException | IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean requestNewTokens(String currentRefreshToken, int userID) {

        final String URL = "https://www.strava.com/oauth/token";
        final String clientID = "125803";
        final String clientSecret = "3e9f7fcd913ece59cb5bccd8a89444ab9f452ec5";
        final String grantType = "refresh_token";

        HttpClient httpClient = HttpClientBuilder.create().build();

        try {
            // Build the URI with parameters
            URI uri = new URIBuilder(URL)
                    .setParameter("client_id", clientID)
                    .setParameter("client_secret", clientSecret)
                    .setParameter("grant_type", grantType)
                    .setParameter("refresh_token", currentRefreshToken)
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

                StravaUser userToUpdate = stravaUserRepository.findById(userID);
                userToUpdate.setExpiresAt(expiresAt);
                userToUpdate.setRefreshToken(refreshToken);
                userToUpdate.setAccessToken(accessToken);
                stravaUserRepository.save(userToUpdate);

                return true;

            }

            // Print response status code
            System.out.println("Response Status Code: " + response.getStatusLine().getStatusCode());
            return false;

        } catch (URISyntaxException | IOException e) {
            e.printStackTrace();
            return false;
        }

    }
    // TODO Finish this method, change from Post to Get, possibly change return type from boolean
    public boolean getRunsAfter(int stravaID, int unixTimeStamp) {

        final String URL = "https://www.strava.com/api/v3/athlete/activities";

        HttpClient httpClient = HttpClientBuilder.create().build();

        try {
            // Build the URI with parameters
            URI uri = new URIBuilder(URL)
                    .setParameter("after", String.valueOf(unixTimeStamp))
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

                return true;

            }

            // Print response status code
            System.out.println("Response Status Code: " + response.getStatusLine().getStatusCode());
            return false;

        } catch (URISyntaxException | IOException e) {
            e.printStackTrace();
            return false;
        }

    }

}

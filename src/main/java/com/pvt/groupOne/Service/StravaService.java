package com.pvt.groupOne.Service;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pvt.groupOne.model.StravaRun;
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
        final String CLIENT_ID = "125803";
        final String CLIENT_SECRET = "3e9f7fcd913ece59cb5bccd8a89444ab9f452ec5";
        final String GRANT_TYPE = "refresh_token";

        HttpClient httpClient = HttpClientBuilder.create().build();

        try {
            // Build the URI with parameters
            URI uri = new URIBuilder(URL)
                    .setParameter("client_id", CLIENT_ID)
                    .setParameter("client_secret", CLIENT_SECRET)
                    .setParameter("grant_type", GRANT_TYPE)
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

    // TODO DIDDE: Finish this method, change from Post to Get, possibly change
    // return type from boolean
    public ArrayList<StravaRun> saveRunsFrom(int stravaID, int unixTimeStamp, String accessToken) {

        final String URL = "https://www.strava.com/api/v3/athlete/activities";

        HttpClient httpClient = HttpClients.createDefault();

        // Create HTTP GET request with the "after" parameter
        String urlWithParams = URL + "?after=" + unixTimeStamp;
        HttpUriRequest request = RequestBuilder.get(urlWithParams)
                .addHeader("Authorization", "Bearer " + accessToken)
                .build();

        try {
            HttpResponse response = httpClient.execute(request);

            // Check if the response is successful (status code 200)
            if (response.getStatusLine().getStatusCode() == 200) {
                String jsonResponse = EntityUtils.toString(response.getEntity());

                // Use Jackson library to parse JSON into Java objects
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode activitiesNode = objectMapper.readTree(jsonResponse);

                // Now you can access information about the runs
                ArrayList<StravaRun> runs = new ArrayList<>();
                for (JsonNode activityNode : activitiesNode) {
                    if (activityNode.get("type").asText().equals("Run")) {
                        double distance = activityNode.get("distance").asDouble();
                        int elapsedTime = activityNode.get("elapsed_time").asInt();
                        String date = activityNode.get("start_date").asText();
                        StravaRun currentRun = new StravaRun(date, distance, elapsedTime);
                        runs.add(currentRun);
                    }
                }

                return runs;
                
            } else {
                System.out.println("Failed to retrieve data. Status code: " + response.getStatusLine().getStatusCode());
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}

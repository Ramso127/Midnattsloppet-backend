package com.pvt.groupOne.Service;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.time.LocalDate;
import java.text.DecimalFormat;
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
import com.pvt.groupOne.model.Run;
import com.pvt.groupOne.model.StravaUser;
import com.pvt.groupOne.model.User;
import com.pvt.groupOne.repository.StravaUserRepository;

public class StravaService {

    private final StravaUserRepository stravaUserRepository;

    private final String URL = "https://www.strava.com/oauth/token";
    private final String CLIENT_ID = "126330";
    private final String CLIENT_SECRET = "84935d0a565ea3e1989b60112f279a4a9d114f74";
    private String grantType;

    public StravaService(StravaUserRepository stravaUserRepository) {
        this.stravaUserRepository = stravaUserRepository;
    }

    public StravaUser exchangeToken(String authCode) {

        grantType = "authorization_code";

        HttpClient httpClient = HttpClientBuilder.create().build();

        try {
            // Build the URI with parameters
            URI uri = new URIBuilder(URL)
                    .setParameter("client_id", CLIENT_ID)
                    .setParameter("client_secret", CLIENT_SECRET)
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

                long currentSystemTime = System.currentTimeMillis() / 1000L;
                return new StravaUser(id, firstName, accessToken, refreshToken,
                        expiresAt, currentSystemTime);

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

        grantType = "refresh_token";

        HttpClient httpClient = HttpClientBuilder.create().build();

        try {
            // Build the URI with parameters
            URI uri = new URIBuilder(URL)
                    .setParameter("client_id", CLIENT_ID)
                    .setParameter("client_secret", CLIENT_SECRET)
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

    // TODO DIDDE: Finish this method, change from Post to Get, possibly change
    // return type from boolean
    public ArrayList<Run> saveRunsFrom(int stravaID, long unixTimeStamp, String accessToken, User user) {

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
                ArrayList<Run> runs = new ArrayList<>();
                for (JsonNode activityNode : activitiesNode) {
                    if (activityNode.get("type").asText().equals("Run")) {
                        double distance = activityNode.get("distance").asDouble();
                        distance = distance / 1000;
                        DecimalFormat df = new DecimalFormat("#.##");
                        String formattedDistance = df.format(distance);
                        int elapsedTimeInSeconds = activityNode.get("elapsed_time").asInt();

                        String formattedTime = getFormattedTime(elapsedTimeInSeconds);
                        double formattedDistanceDouble = Double.parseDouble(formattedDistance);
                        String date = activityNode.get("start_date_local").asText();
                        date = date.substring(0, date.indexOf('T'));
                        LocalDate localDate = LocalDate.parse(date);
                        Run currentRun = new Run(localDate, formattedDistanceDouble, formattedTime, user);
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

    private String getFormattedTime(int elapsedTimeInSeconds) {
        // Calculate hours
        int hours = elapsedTimeInSeconds / 3600;

        // Calculate remaining seconds after removing hours
        int remainingSeconds = elapsedTimeInSeconds % 3600;

        // Calculate minutes
        int minutes = remainingSeconds / 60;

        // Calculate remaining seconds after removing minutes
        int seconds = remainingSeconds % 60;

        // Format the time
        String formattedTime = String.format("%02d:%02d:%02d:%03d", hours, minutes, seconds);
        return formattedTime;
    }

}

package com.pvt.groupOne.Service;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.time.LocalDate;
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
    private final String CLIENT_ID = "125438";
    private final String CLIENT_SECRET = "d3784826d6a60ee43253e4f2e1cf38932cd24251";
    private String grantType;

    public StravaService(StravaUserRepository stravaUserRepository) {
        this.stravaUserRepository = stravaUserRepository;
    }

    public StravaUser exchangeToken(String authCode) {

        grantType = "authorization_code";

        HttpClient httpClient = HttpClientBuilder.create().build();

        try {
            URI uri = new URIBuilder(URL)
                    .setParameter("client_id", CLIENT_ID)
                    .setParameter("client_secret", CLIENT_SECRET)
                    .setParameter("code", authCode)
                    .setParameter("grant_type", grantType)
                    .build();

            HttpPost httpPost = new HttpPost(uri);

            HttpResponse response = httpClient.execute(httpPost);

            HttpEntity entity = response.getEntity();

            System.out.println("Response Status: " + response.getStatusLine());

            if (entity != null) {
                String responseContent = EntityUtils.toString(entity);
                System.out.println("Response Content: " + responseContent);

                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode jsonNode = objectMapper.readTree(responseContent);

                long expiresAt = jsonNode.get("expires_at").asLong();
                String refreshToken = jsonNode.get("refresh_token").asText();
                String accessToken = jsonNode.get("access_token").asText();

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

            URI uri = new URIBuilder(URL)
                    .setParameter("client_id", CLIENT_ID)
                    .setParameter("client_secret", CLIENT_SECRET)
                    .setParameter("grant_type", grantType)
                    .setParameter("refresh_token", currentRefreshToken)
                    .build();

            HttpPost httpPost = new HttpPost(uri);

            HttpResponse response = httpClient.execute(httpPost);

            HttpEntity entity = response.getEntity();

            System.out.println("Response Status: " + response.getStatusLine());

            if (entity != null) {
                String responseContent = EntityUtils.toString(entity);
                System.out.println("Response Content: " + responseContent);

                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode jsonNode = objectMapper.readTree(responseContent);

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

            System.out.println("Response Status Code: " + response.getStatusLine().getStatusCode());
            return false;

        } catch (URISyntaxException | IOException e) {
            e.printStackTrace();
            return false;
        }

    }

    public ArrayList<Run> saveRunsFrom(int stravaID, long unixTimeStamp, String accessToken, User user) {

        final String URL = "https://www.strava.com/api/v3/athlete/activities";

        HttpClient httpClient = HttpClients.createDefault();

        String urlWithParams = URL + "?after=" + unixTimeStamp;
        HttpUriRequest request = RequestBuilder.get(urlWithParams)
                .addHeader("Authorization", "Bearer " + accessToken)
                .build();

        try {
            HttpResponse response = httpClient.execute(request);

            if (response.getStatusLine().getStatusCode() == 200) {
                String jsonResponse = EntityUtils.toString(response.getEntity());

                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode activitiesNode = objectMapper.readTree(jsonResponse);

                ArrayList<Run> runs = new ArrayList<>();
                for (JsonNode activityNode : activitiesNode) {
                    if (activityNode.get("type").asText().equals("Run")) {
                        double distance = activityNode.get("distance").asDouble();
                        distance = distance / 1000;
                        double truncated = Math.floor(distance * 100) / 100;
                        int elapsedTimeInSeconds = activityNode.get("elapsed_time").asInt();

                        String formattedTime = getFormattedTime(elapsedTimeInSeconds);
                        String date = activityNode.get("start_date_local").asText();
                        date = date.substring(0, date.indexOf('T'));
                        LocalDate localDate = LocalDate.parse(date);
                        Run currentRun = new Run(localDate, truncated, formattedTime, user);
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
        String formattedTime = String.format("%02d:%02d:%02d", hours, minutes, seconds);
        return formattedTime;
    }

}

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        System.out.println("Welcome to weather app!");

        final String API = "https://wttr.in/";

        String city;
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.print("Enter city name (or type 'exit' to quit): ");
            city = scanner.nextLine();

            if (city.equalsIgnoreCase("exit")) {
                System.out.println("Exiting the application.");
                break;
            }

            String url = API + city + "?format=j1";

            try {
                String response = getHttpResponse(url);
                if (response != null) {
                    parseAndPrintWeatherData(response);
                } else {
                    System.out.println("Failed to get a response from the server.");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        scanner.close();

    }

    private static String getHttpResponse(String urlString) throws Exception {
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                StringBuilder content = new StringBuilder();
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    content.append(inputLine);
                }
                return content.toString();
            }
        } else {
            System.out.println("GET request failed. Response code: " + responseCode);
            return null;
        }
    }

    private static void parseAndPrintWeatherData(String responseBody) {
        JSONObject jsonObject = new JSONObject(responseBody);
        JSONArray resultsArray = jsonObject.getJSONArray("current_condition");
        JSONObject weatherData = resultsArray.getJSONObject(0);
        JSONArray weatherDescArray = weatherData.getJSONArray("weatherDesc");

        String tempC = weatherData.getString("temp_C");
        String humidity = weatherData.getString("humidity");
        String localObsDateTime = weatherData.getString("localObsDateTime");
        String weatherDesc = weatherDescArray.getJSONObject(0).getString("value");

        System.out.printf("Temperature Degree: %s°C%n", tempC);
        System.out.printf("Humidity: %s%%%n", humidity);
        System.out.printf("Local Observation Date Time: %s%n", localObsDateTime);
        System.out.printf("Weather Description: %s%n", weatherDesc);
    }
}